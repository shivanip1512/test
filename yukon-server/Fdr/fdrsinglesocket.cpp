/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrsinglesocket.cpp
*
*    DATE: 03/07/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrsinglesocket.cpp-arc  $
*    REVISION     :  $Revision: 1.7 $
*    DATE         :  $Date: 2005/02/10 23:23:51 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Interface to foreign System
*
*    DESCRIPTION: This class implements a base interface class for those foreign systems
*                   that use a single socket to exchange data
*    ---------------------------------------------------
*    History: 
      $Log: fdrsinglesocket.cpp,v $
      Revision 1.7  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.6  2002/10/14 21:10:56  dsutton
      In the database translation routines, if we failed to hit the database
      we called the load routine again just to get the error code.  Whoops
      The error code is now saved from the original call and printed as needed

      Revision 1.5  2002/09/06 18:55:22  dsutton
      The database load and list swap used to occur before we updated
      the translation names.  I now update the entire temporary list and then
      lock the real list and swap it.  Much faster and doesn't lock the list
      down as long.

      Revision 1.4  2002/08/06 22:03:14  dsutton
      Programming around the error that happens if the dataset is empty when it is
      returned from the database and shouldn't be.  If our point list had more than
      two entries in it before, we fail the attempt and try again in 60 seconds

      Revision 1.3  2002/04/16 15:58:37  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:58  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 2.9   12 Mar 2002 10:30:06   dsutton
   updated to use the listener object in the socketinterface base class.  It allows for more peaceful shutdown of fdr when it is not connected to a foreign system
   
      Rev 2.8   01 Mar 2002 13:28:34   dsutton
   moved valid connection printout to the socketlayer, added timesync processing, added calls to fail the connection on shutdown
   
      Rev 2.7   20 Feb 2002 08:44:02   dsutton
   moved the listener socket creation inside the loop and made sure to shutdown and close it after we have accepted a connection.  Also saved the clients address to be used in the heartbeat log instead of the servers (oops)
   
      Rev 2.6   15 Feb 2002 11:29:00   dsutton
    changed the debug settings for a few of the transactions to make them more uniform throughout fdr
   
      Rev 2.5   11 Feb 2002 15:04:32   dsutton
   added event logs when the connection is established or failed, unknown points, invalid states, etc
   
      Rev 2.4   20 Dec 2001 14:57:24   dsutton
   added a isregistrationneeded function to check if the initial data dump is dependant on a registration message.  Base function in this class returns false and it can be overridden for any child classes.  Aslo a call to see if the client connection is valid to keep from getting stuck in the initial upload loop
   
      Rev 2.3   14 Dec 2001 17:26:22   dsutton
   fdrpointmaplist changed to new class
   
      Rev 2.2   15 Nov 2001 16:16:40   dsutton
   code for multipliers and an queue for the messages to dispatch along with fixes to RCCS/INET interface. Lazy checkin
   
      Rev 2.1   26 Oct 2001 15:20:30   dsutton
   moving revision 1 to 2.x
   
      Rev 1.1.1.0   26 Oct 2001 14:25:18   dsutton
   load the translation table on initializationinstead of waiting until first connection
   
      Rev 1.1   20 Jul 2001 10:01:54   dsutton
   unk
   
      Rev 1.0   19 Jun 2001 10:51:54   dsutton
   Initial revision.
   
*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include "yukon.h"

#include <windows.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

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

// Constructors, Destructor, and Operators
CtiFDRSingleSocket::CtiFDRSingleSocket(RWCString &name)
: CtiFDRSocketInterface(name),
    iLayer (NULL)
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_RECEIVE)); 
    getReceiveFromList().setPointList (recList);
    recList = NULL;

    CtiFDRManager   *sendList = new CtiFDRManager(getInterfaceName(), RWCString(FDR_INTERFACE_SEND));
    getSendToList().setPointList (sendList);
    sendList = NULL;
}


CtiFDRSingleSocket::~CtiFDRSingleSocket()
{

    // cleanup memory
    if (iLayer != NULL)
    {
        RWCString desc,action;
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
        RWCString desc = getInterfaceName() + RWCString (" has been configured to NOT process time sync updates to PC clock");
        logEvent (desc,RWCString());
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
*				specified direction
* 
*************************************************************************
*/
bool CtiFDRSingleSocket::loadList(RWCString &aDirection,  CtiFDRPointList &aList)
{
    bool                successful(false);
    CtiFDRPoint *       translationPoint = NULL;
    CtiFDRPoint *       point = NULL;
    RWCString           tempString1;
    RWCString           tempString2;
    RWCString           translationName;
    bool                foundPoint = false, translatedPoint(false);
    RWCString           controlDirection;
    static bool firstPassHackFlag=false;  // yuck
    RWDBStatus          listStatus;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(), 
                                                       aDirection);
        listStatus = pointList->loadPointList();

        // if status is ok, we were able to read the database at least
        if ( listStatus.errorCode() == (RWDBStatus::ok))
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
                // get iterator on list
                CtiFDRManager::CTIFdrPointIterator  myIterator(pointList->getMap());
//                CtiFDRManager::CTIFdrPointIterator  myIterator(aList.getPointList()->getMap());
                int x;

                for ( ; myIterator(); )
                {
                    foundPoint = true;
                    translationPoint = myIterator.value();

                    for (x=0; x < translationPoint->getDestinationList().size(); x++)
                    {
                        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Point ID " << translationPoint->getPointID();
                            dout << " translate: " << translationPoint->getDestinationList()[0].getTranslation() << endl;
                        }
                        // translate and put the point id the list
                        if (translateAndUpdatePoint (translationPoint, x))
                        {
                            translatedPoint = true;
                            successful = true;
                        }
                    }
                }   // end for interator

                // lock the list I'm inserting into so it doesn't get deleted on me
                CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());  
                if (aList.getPointList() != NULL)
                {
                    aList.deletePointList();
                }
                aList.setPointList (pointList);

                // set this to null, the memory is now assigned to the other point
                pointList=NULL;

                if (!translatedPoint)
                {
                    if (!foundPoint)
                    {
                        // means there was nothing in the list, wait until next db change or reload
                        successful = true;
                        if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " No (" << aDirection << ") points defined for use by interface " << getInterfaceName() << endl;
                        }
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error loading (" << aDirection << ") points for " << getInterfaceName() << " : Empty data set returned " << endl;
                successful = false;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
            successful = false;
        }
    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") loadTranslationList():  " << e.why() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") loadTranslationList():  (...)" << endl;
    }

    return successful;
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
        case SINGLE_SOCKET_TIMESYNC:
            {
                if (shouldUpdatePCTime())
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Time sync message received from " << getInterfaceName() << endl;
                    }

                    retVal = processTimeSyncMessage (aBuffer);
                }
                else
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Time sync message received from " << getInterfaceName() << endl;
                        dout << RWTime() << " PC time will not updated due to current configuration " << endl;
                    }
                }
                break;
            }
        case SINGLE_SOCKET_NULL:
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Heartbeat message received from " << getInterfaceName() << " at " << RWCString (inet_ntoa(iLayer->getInBoundConnection()->getAddr().sin_addr)) <<  endl;
                }
                break;
            }
        default:
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unknown message type " << ntohs (*function) <<  " received from " << getInterfaceName() << endl;
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

RWCString CtiFDRSingleSocket::decodeClientName(CHAR * aBuffer)
{
    return RWCString ();
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
    RWCString            desc;
    RWCString           action;
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
            dout << RWTime() << " Initializing CtiFDRSingleSocket::threadFunctionConnection for " << getInterfaceName() << endl;
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
                        dout << RWTime() << " Failed to create listener socket for " << getInterfaceName() << endl;
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
                            dout << RWTime() << " Failed to set reuse option for listener socket in "<< getInterfaceName() << endl;
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
                                dout << RWTime() << " Failed to bind listener socket in " << getInterfaceName() << endl;
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
                                    dout << RWTime() << " " << iLayer->getName() << "'s link has failed" << endl;
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
                                    dout <<  RWTime() << " Listening for connection on port " << getPortNumber() << endl;
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
                                        dout << RWTime() << " Accept call failed in " << getInterfaceName() <<endl;
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
                                       dout << RWTime() << " Connection established to " << decodeClientName(NULL) << " at " << RWCString (inet_ntoa(returnAddr.sin_addr)) << endl;
                                    }

                                    desc = decodeClientName(NULL) + RWCString ("'s client link has been established at ") + RWCString (inet_ntoa(returnAddr.sin_addr));
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
        dout << RWTime() << " Fatal Error:  CtiFDRSingleSocket::threadFunctionConnection in " << getInterfaceName() << " is dead! " << endl;
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
        dout << RWTime() << " Starting Debug Thread for " << getInterfaceName() << endl;
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
                    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());  
                    CtiFDRManager::CTIFdrPointIterator  myIterator(getSendToList().getPointList()->getMap());

                    for ( ; myIterator(); )
                    {
                        // find the point id
                        point = *myIterator.value();

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
        dout << RWTime() << " Fatal Error:  threadFunctionDebugData in " << getInterfaceName() << " is dead! " << endl;
    }
}


/* Convert the order of an IEEE floating point from network to host */
FLOAT CtiFDRSingleSocket::ntohieeef (LONG NetLong)
{
    union
    {
        FLOAT HostFloat;
        LONG HostLong;
    } HostUnion;

    HostUnion.HostLong = ntohl (NetLong);

    return(HostUnion.HostFloat);
}



/* Convert the order of an IEEE floating point from host to network */
LONG CtiFDRSingleSocket::htonieeef (FLOAT  HostFloat) 
{
    union
    {
        FLOAT HostFloat;
        LONG HostLong;
    } HostUnion;

    HostUnion.HostFloat = HostFloat;

    return(htonl (HostUnion.HostLong));
}


