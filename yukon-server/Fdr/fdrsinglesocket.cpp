#include "precompiled.h"

#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "connection.h"
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrsocketinterface.h"
#include "fdrpointlist.h"
#include "fdrsocketlayer.h"
#include "fdrserverconnection.h"

// this class header
#include "fdrsinglesocket.h"

using std::string;
using std::endl;

// Constructors, Destructor, and Operators
CtiFDRSingleSocket::CtiFDRSingleSocket(string &name)
: CtiFDRSocketInterface(name),
    iLayer (NULL)
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;

    CtiFDRManager   *sendList = new CtiFDRManager(getInterfaceName(), string(FDR_INTERFACE_SEND));
    getSendToList().setPointList (sendList);
    sendList = NULL;
}


CtiFDRSingleSocket::~CtiFDRSingleSocket()
{

    // cleanup memory
    if (iLayer != NULL)
    {
        string desc,action;
        desc = iLayer->getName() + "'s link has failed";
        logEvent (desc,action,true);
        delete iLayer;
    }
    setCurrentClientLinkStates();
}

CtiFDRSocketLayer * CtiFDRSingleSocket::getLayer ()
{
    return iLayer;
}

CtiFDRSingleSocket& CtiFDRSingleSocket::setLayer (CtiFDRSocketLayer * aLayer)
{
    iLayer = aLayer;
    return *this;
}

bool CtiFDRSingleSocket::isRegistrationNeeded()
{
    // always false unless overridden
    return false;
}

bool CtiFDRSingleSocket::isClientConnectionValid()
{
    bool retVal;

    if (iLayer->getOutBoundConnectionStatus() == CtiFDRSocketConnection::Ok)
        retVal=true;
    else
        retVal=false;
    return retVal;
}

/*************************************************
* Function Name: CtiFDRSingleSocket::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDRSingleSocket::init( void )
{
    // init the base class
    Inherited::init();

    if ( !readConfig( ) )
    {
        return FALSE;
    }

    loadTranslationLists();

    // start up the socket layer
    iLayer = NULL;

    iThreadConnection = rwMakeThreadFunction(*this,
                                            &CtiFDRSingleSocket::threadFunctionConnection);


    if (isInterfaceInDebugMode())
    {
        iThreadSendDebugData = rwMakeThreadFunction(*this,
                                                &CtiFDRSingleSocket::threadFunctionSendDebugData);
    }
    return TRUE;
}

/*************************************************
* Function Name: CtiFDRSingleSocket::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDRSingleSocket::run( void )
{

    // crank up the base class
    Inherited::run();

    // startup our interfaces
    iThreadConnection.start();

    // log this now so we dont' have to everytime one comes in
    if (!shouldUpdatePCTime())
    {
        string desc = getInterfaceName() + string (" has been configured to NOT process time sync updates to PC clock");
        logEvent (desc,string());
    }

    if (isInterfaceInDebugMode())
        iThreadSendDebugData.start();

    // note:  RDEX will have a problem with this once it is written to handle muliple connections
    long linkID = getClientLinkStatusID (decodeClientName(NULL));

    if (linkID)
    {
        CtiPointDataMsg     *pData;
        pData = new CtiPointDataMsg(linkID,
                                    FDR_NOT_CONNECTED,
                                    NormalQuality,
                                    StatusPointType);
        sendMessageToDispatch (pData);
    }

    return TRUE;
}


/*************************************************
* Function Name: CtiFDRSingleSocket::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDRSingleSocket::stop( void )
{
    //
    // FIXFIXFIX  - may need to add exception handling here
    //
    shutdownListener();
    iThreadConnection.requestCancellation();

    if (isInterfaceInDebugMode())
        iThreadSendDebugData.requestCancellation();

    // stop the base class
    Inherited::stop();

    return TRUE;
}

/************************************************************************
* Function Name: CtiFDRSingleSocket::loadList()
*
* Description: Creates a collection of points and their translations for the
*                               specified direction
*
*************************************************************************
*/
bool CtiFDRSingleSocket::loadList(string &aDirection,  CtiFDRPointList &aList)
{
    bool successful = false;
    bool foundPoint = false;

    static bool firstPassHackFlag = false;// yuck

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(), aDirection);

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
        {
            /**************************************
            * seeing occasional problems where we get empty data sets back
            * and there should be info in them,  we're checking this to see if
            * is reasonable if the list may now be empty
            * the 2 entry thing is completly arbitrary
            ***************************************
            */
            if (((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2)) ||
                (pointList->entries() > 0))
            {
                // Signal the list reload to anyone who cares.
                signalReloadList();

                // get iterator on list
                CtiFDRManager::spiterator myIterator = pointList->getMap().begin();

                for ( ; myIterator != pointList->getMap().end(); ++myIterator )
                {
                    foundPoint = true;
                    successful = translateSinglePoint(myIterator->second);
                }

                // lock the list I'm inserting into so it doesn't get deleted on me
                CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());
                if (aList.getPointList() != NULL)
                {
                    aList.deletePointList();
                }
                aList.setPointList (pointList);

                // set this to null, the memory is now assigned to the other point
                pointList=NULL;

                if (!successful)
                {
                    if (!foundPoint)
                    {
                        // means there was nothing in the list, wait until next db change or reload
                        successful = true;
                        if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " No (" << aDirection << ") points defined for use by interface " << getInterfaceName() << endl;
                        }
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Error loading (" << aDirection << ") points for " << getInterfaceName() << " : Empty data set returned " << endl;
                successful = false;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            successful = false;
        }
    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime () << " " << __FILE__ << " (" << __LINE__ << ") loadTranslationList():  " << e.why() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime () << " " << __FILE__ << " (" << __LINE__ << ") loadTranslationList():  (...)" << endl;
    }

    return successful;
}

void CtiFDRSingleSocket::signalReloadList()
{
    //Do nothing by default
}

void CtiFDRSingleSocket::signalPointRemoved(string &pointName)
{
    //do Nothing by default
}

bool CtiFDRSingleSocket::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool success = false;

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Point ID " << translationPoint->getPointID();
            dout << " translate: " << translationPoint->getDestinationList()[0].getTranslation() << endl;
        }

        if (translateAndUpdatePoint (translationPoint, x))
        {
            success = true;
        }
    }

    return success;
}

void CtiFDRSingleSocket::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    if (recvList)
    {
        if (translationPoint.get() == NULL)
        {
            return;
        }

        int size = translationPoint->getDestinationList().size();
        for ( int i = 0 ; i < size; i++) {
            string str = translationPoint->getDestinationList()[i].getTranslation();
            if (str != "")
            {
                std::transform(str.begin(), str.end(), str.begin(), toupper);
                signalPointRemoved(str);
            }
        }
    }

    return;
}

void CtiFDRSingleSocket::setCurrentClientLinkStates()
{
    long linkID = getClientLinkStatusID (decodeClientName(NULL));

    // try and load the point here if the link is valid
    if (iLayer != NULL)
    {
        iLayer->setLinkStatusID(linkID);
        iLayer->sendLinkState (FDR_CONNECTED);
    }
    else
    {
        // note:  RDEX will have a problem with this once it is written to handle muliple connections
        if (linkID)
        {
            CtiPointDataMsg     *pData;
            pData = new CtiPointDataMsg(linkID,
                                        FDR_NOT_CONNECTED,
                                        NormalQuality,
                                        StatusPointType);
            sendMessageToDispatch (pData);
        }
    }
}
/**************************************************************************
* Function Name: CtiFDRSingleSocket::sendMessageToForeignSys ()
*
* Description:
*
***************************************************************************
*/

bool CtiFDRSingleSocket::buildAndWriteToForeignSystem (CtiFDRPoint &aPoint )
{
    bool retVal = true;
    CHAR *ptr=NULL;

    /***********************
    *  data is allocated for the buffer inside this call
    *  it will be deleted inside  the socketlayer
    ************************
    */
    ptr = buildForeignSystemMsg (aPoint);

    if (ptr != NULL)
    {
        // if the write is successful
        if (iLayer != NULL)
        {
            // this is messy, may have to try again
            if (iLayer->getOutBoundConnectionStatus() == CtiFDRSocketConnection::Ok)
            {
                if (iLayer->write (ptr))
                    retVal = false;
            }
            else
            {
                delete []ptr;
                retVal = false;
            }
        }
        else
        {
            delete []ptr;
            retVal = false;
        }
    }
    else
    {
        retVal = false;
    }

    return retVal;
}

int CtiFDRSingleSocket::processMessageFromForeignSystem(CHAR *aBuffer)
{
    int retVal = NORMAL;
    USHORT *function = (USHORT *)aBuffer;

    switch (ntohs (*function))
    {
        case SINGLE_SOCKET_VALUE:
            {
                retVal = processValueMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_REGISTRATION:
            {
                retVal = processRegistrationMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_STATUS:
            {
                retVal = processStatusMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_CONTROL:
        case SINGLE_SOCKET_VALMET_CONTROL:
            {
                retVal = processControlMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_FORCESCAN:
            {
                retVal = processScanMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_TIMESYNC:
            {
                if (shouldUpdatePCTime())
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Time sync message received from " << getInterfaceName() << endl;
                    }

                    retVal = processTimeSyncMessage (aBuffer);
                }
                else
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Time sync message received from " << getInterfaceName() << endl;
                        dout << CtiTime() << " PC time will not updated due to current configuration " << endl;
                    }
                }
                break;
            }
        case SINGLE_SOCKET_NULL:
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Heartbeat message received from " << getInterfaceName() << " at " << string (inet_ntoa(iLayer->getInBoundConnection()->getAddr().sin_addr)) <<  endl;
                }
                break;
            }
        default:
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Unknown message type " << ntohs (*function) <<  " received from " << getInterfaceName() << endl;
            }
    }

    return retVal;

}

int CtiFDRSingleSocket::processValueMessage(CHAR *data)
{
    int retVal = NORMAL;
    return retVal;
}

int CtiFDRSingleSocket::processStatusMessage(CHAR *data)
{
    int retVal = NORMAL;
    return retVal;
}

int CtiFDRSingleSocket::processRegistrationMessage(CHAR *data)
{
    int retVal = NORMAL;
    return retVal;
}

int CtiFDRSingleSocket::processControlMessage(CHAR *data)
{
    int retVal = NORMAL;
    return retVal;
}
int CtiFDRSingleSocket::processScanMessage(CHAR *data)
{
    int retVal = NORMAL;
    return retVal;
}
int CtiFDRSingleSocket::processTimeSyncMessage(CHAR *data)
{
    int retVal = NORMAL;
    return retVal;
}

CHAR *CtiFDRSingleSocket::buildForeignSystemHeartbeatMsg ()
{
    return NULL;
}

int CtiFDRSingleSocket::getMessageSize(CHAR *aBuffer)
{
    return 0;
}

string CtiFDRSingleSocket::decodeClientName(CHAR * aBuffer)
{
    return string ();
}

/**************************************************************************
* Function Name: CtiFDRSingleSocket::threadFunctionConnection
*
* Description: thread that watches connection status and re-establishes it as needed
*
***************************************************************************
*/
void CtiFDRSingleSocket::threadFunctionConnection( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0;
    CtiFDRServerConnection   *serverConnection;
    CtiFDRClientConnection   *clinetConnection;
    CtiFDRSocketLayer   *layer;
    int connectionIndex;
    string            desc;
    string           action;
    SOCKADDR_IN tmp;

    SOCKET listener, tmpConnection;
    SOCKADDR_IN             socketAddr, returnAddr;
    int                   returnLength;
    LPHOSTENT               hostEntry;
    bool continueFlag;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDRSingleSocket::threadFunctionConnection for " << getInterfaceName() << endl;
        }
        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (500);

            continueFlag = false;

            if (iLayer == NULL)
            {
                // allows us in the first time and any time a catastrophic error occurred
                continueFlag = true;
            }
            else
            {
                // see if we've died
                if (iLayer->getInBoundConnectionStatus() == CtiFDRSocketConnection::Failed ||
                    iLayer->getOutBoundConnectionStatus() == CtiFDRSocketConnection::Failed)
                    continueFlag = true;
            }
            // see if we've died
            if (continueFlag)
            {
                listener = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
                if (listener == INVALID_SOCKET)
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Failed to create listener socket for " << getInterfaceName() << endl;
                    }
                }
                else
                {
                    BOOL ka = TRUE;
                    if (setsockopt(listener, SOL_SOCKET, SO_REUSEADDR, (char*)&ka, sizeof(BOOL)))
                    {
                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Failed to set reuse option for listener socket in "<< getInterfaceName() << endl;
                        }

                        shutdown(listener, 2);
                        closesocket(listener);
                    }
                    else
                    {
                        // Fill in the address structure
                        socketAddr.sin_family = AF_INET;
                        socketAddr.sin_addr.s_addr = INADDR_ANY; // Let WinNexus supply address
                        socketAddr.sin_port = htons(getPortNumber());      // Use port from command line

                        if (bind(listener, (LPSOCKADDR)&(socketAddr), sizeof(struct sockaddr)))
                        {
                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Failed to bind listener socket in " << getInterfaceName() << endl;
                            }

                            shutdown(listener, 2);
                            closesocket(listener);
                        }
                        else
                        {
                            // if the interface needs a registration
                            if (isRegistrationNeeded())
                            {
                                setRegistered(false);
                            }
                            // delete the old connection if its there
                            if (iLayer != NULL)
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " " << iLayer->getName() << "'s link has failed" << endl;
                                }
                                desc = iLayer->getName() + "'s link has failed";
                                logEvent (desc,action,true);
                                delete iLayer;
                                iLayer = NULL;
                            }


                            // listening
                            getListener()->setConnection(listener);
                            if (listen(getListener()->getConnection(), SOMAXCONN))
                            {
                                shutdown(getListener()->getConnection(), 2);
                                closesocket(getListener()->getConnection());
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout <<  CtiTime() << " Listening for connection on port " << getPortNumber() << endl;
                                }

                                // new socket
                                returnLength = sizeof (returnAddr);
                                tmpConnection = accept(getListener()->getConnection(), (struct sockaddr *) &returnAddr, &returnLength);

                                shutdown(getListener()->getConnection(), 2);
                                closesocket(getListener()->getConnection());
                                listener = NULL;

                                if (tmpConnection == INVALID_SOCKET)
                                {
                                    shutdown(tmpConnection, 2);
                                    closesocket(tmpConnection);
                                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " Accept call failed in " << getInterfaceName() <<endl;
                                    }
                                }
                                else
                                {
                                    // set to non blocking mode
                                    ULONG param=1;
                                    ioctlsocket(tmpConnection, FIONBIO, &param);

                                    /***************************
                                    * note:  acs, valmet both have decodeclientname funcs that return
                                    * their interface names
                                    * rdex does not because its client name doesn't exist until
                                    * the registration msg comes thru
                                    ****************************
                                    */
                                    iLayer = new CtiFDRSocketLayer (decodeClientName(NULL), tmpConnection, tmpConnection, CtiFDRSocketLayer::Server_Single, this);
                                    iLayer->setLinkStatusID(getClientLinkStatusID(decodeClientName(NULL)));
                                    iLayer->init();
                                    iLayer->run();

//                                    if(getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                                    {
                                       CtiLockGuard<CtiLogger> doubt_guard(dout);
                                       dout << CtiTime() << " Connection established to " << decodeClientName(NULL) << " at " << string (inet_ntoa(returnAddr.sin_addr)) << endl;
                                    }

                                    desc = decodeClientName(NULL) + string ("'s client link has been established at ") + string (inet_ntoa(returnAddr.sin_addr));
                                    logEvent (desc,action, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    catch ( RWCancellation &cancellationMsg )
    {
        // delete the connection object
        if (iLayer != NULL)
        {
            delete iLayer;
            iLayer = NULL;
        }
        setCurrentClientLinkStates();

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION of CtiFDRSingleSocket::threadFunctionConnection for interface " << getInterfaceName() << endl;
        return;
    }

    // try and catch the thread death
    catch ( ... )
    {
        // delete the connection object
        if (iLayer != NULL)
        {
            delete iLayer;
            iLayer = NULL;
        }
        setCurrentClientLinkStates();

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDRSingleSocket::threadFunctionConnection in " << getInterfaceName() << " is dead! " << endl;
    }
}


void CtiFDRSingleSocket::threadFunctionSendDebugData( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0;
    CtiFDRPoint        point;
    CtiPointDataMsg     *localMsg;
    int quality = NormalQuality, index, entries;
    FLOAT value = 1.0;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Starting Debug Thread for " << getInterfaceName() << endl;
    }

    // don't try to do anything until the layer is available
    while (iLayer == NULL)
    {
        pSelf.serviceCancellation( );
        pSelf.sleep (1000);
    }

    try
    {
        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            if (iLayer->getOutBoundConnectionStatus() == CtiFDRSocketConnection::Ok)
            {
                index=0;
                {
                    // for debug lock this the whole time we're sending the list
                    CtiFDRManager* mgrPtr = getSendToList().getPointList();

                    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
                    CtiFDRManager::readerLock guard(mgrPtr->getLock());

                    CtiFDRManager::spiterator  myIterator = mgrPtr->getMap().begin();

                    for ( ; myIterator != mgrPtr->getMap().end(); ++myIterator )
                    {
                        // find the point id
                        point = *((*myIterator).second);

                        localMsg = new CtiPointDataMsg (point.getPointID(), value, quality);

                        sendMessageToForeignSys (localMsg);

                        if (value > 10000.0)
                        {
                            value = 1.0;
                        }
                        value++;
                        index++;
                        delete localMsg;
                    }
                }



                if (quality == NormalQuality )
                    quality = ManualQuality;
                else
                    quality = NormalQuality;
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION" << endl;
        return;
    }
    // try and catch the thread death
    catch ( ... )
    {
        iLayer->setInBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        iLayer->setOutBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  threadFunctionDebugData in " << getInterfaceName() << " is dead! " << endl;
    }
}


bool CtiFDRSingleSocket::alwaysSendRegistrationPoints ( )
{
    return false;
}

