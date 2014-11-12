#include "precompiled.h"

#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"
#include "utility.h"

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrsocketinterface.h"
#include "fdrpointlist.h"
#include "fdrserverconnection.h"
#include "fdrclientconnection.h"
#include "fdrsocketlayer.h"
#include "socket_helper.h"

// this class header
#include "fdrinet.h"

using std::string;
using std::endl;

/** global used to start the interface by c functions **/
CtiFDR_Inet * inetInterface;

const CHAR * CtiFDR_Inet::KEY_LISTEN_PORT_NUMBER = "FDR_INET_PORT_NUMBER";
const CHAR * CtiFDR_Inet::KEY_IP_MASK = "FDR_INET_IP_MASK";
const CHAR * CtiFDR_Inet::KEY_TIMESTAMP_WINDOW = "FDR_INET_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_Inet::KEY_DB_RELOAD_RATE = "FDR_INET_DB_RELOAD_RATE";
const CHAR * CtiFDR_Inet::KEY_SOURCE_NAME = "FDR_INET_SOURCE_NAME";
const CHAR * CtiFDR_Inet::KEY_SERVER_LIST = "FDR_INET_SERVER_LIST";
const CHAR * CtiFDR_Inet::KEY_DEBUG_MODE = "FDR_INET_DEBUG_MODE";
const CHAR * CtiFDR_Inet::KEY_QUEUE_FLUSH_RATE = "FDR_INET_QUEUE_FLUSH_RATE";

// Constructors, Destructor, and Operators
CtiFDR_Inet::CtiFDR_Inet(string aName) :
    CtiFDRSocketInterface(aName),
    iClientConnectionSemaphore(NULL)
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;

    CtiFDRManager   *sendList = new CtiFDRManager(getInterfaceName(), string(FDR_INTERFACE_SEND));
    getSendToList().setPointList (sendList);
    sendList = NULL;

}


CtiFDR_Inet::~CtiFDR_Inet()
{
    {
        CtiLockGuard<CtiMutex> guard(iConnectionListMux);

        // delete all the layers
        for (int x=0; x < iConnectionList.size(); x++)
        {
            delete iConnectionList[x];
        }
        // erase the pointers
        iConnectionList.erase (iConnectionList.begin(),iConnectionList.end());
    }
    // kill the connection list and then send the statuses
    setCurrentClientLinkStates();
    {
        CtiLockGuard<CtiMutex> guard(iClientListMux);
        iClientList.erase (iClientList.begin(),iClientList.end());
    }
}

CtiFDR_Inet& CtiFDR_Inet::setSourceName(string &aName)
{
    iSourceName = aName;
    return *this;
}
string & CtiFDR_Inet::getSourceName()
{
        return iSourceName;
}

string  CtiFDR_Inet::getSourceName() const
{
        return iSourceName;
}

vector< CtiFDRSocketLayer *> &CtiFDR_Inet::getConnectionList ()
{
    return iConnectionList;
}
vector< CtiFDRSocketLayer *> CtiFDR_Inet::getConnectionList () const
{
    return iConnectionList;
}

CtiMutex & CtiFDR_Inet::getConnectionMux ()
{
    return iConnectionListMux;
}

vector< string >  &CtiFDR_Inet::getClientList ()
{
    return iClientList;
}
vector< string >  CtiFDR_Inet::getClientList () const
{
    return iClientList;
}

CtiMutex & CtiFDR_Inet::getClientListMux ()
{
    return iClientListMux;
}

/*************************************************
* Function Name: CtiFDR_INET::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDR_Inet::init( void )
{
    // init the base class
    Inherited::init();

    if ( !readConfig( ) )
    {
        return FALSE;
    }

    // start up the socket layer
    loadClientList ();
    loadTranslationLists();

    iThreadMonitor = rwMakeThreadFunction(*this,
                                            &CtiFDR_Inet::threadFunctionMonitor);

    iThreadServer = rwMakeThreadFunction(*this,
                                            &CtiFDR_Inet::threadFunctionServerConnection);

    iThreadClient = rwMakeThreadFunction(*this,
                                            &CtiFDR_Inet::threadFunctionClientConnection);

    if (isInterfaceInDebugMode())
    {
        iThreadSendDebugData = rwMakeThreadFunction(*this,
                                                &CtiFDR_Inet::threadFunctionSendDebugData);
    }

    return TRUE;
}

/*************************************************
* Function Name: CtiFDR_Inet::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_Inet::run( void )
{
    // this is here because I want the readconfig up the tree called in RCCS
    init();

    // crank up the base class
    Inherited::run();

    // startup our interfaces
    iThreadMonitor.start();
    iThreadServer.start();
    iThreadClient.start();
    if (isInterfaceInDebugMode())
    {
        iThreadSendDebugData.start();
    }

    setCurrentClientLinkStates();
    return TRUE;
}


void CtiFDR_Inet::setCurrentClientLinkStates()
{
    // try and load the status ids
    int destEntries = iClientList.size();
    int connEntries = iConnectionList.size();
    long linkID;
    bool foundClient;

    for (int x=0; x < destEntries; x++)
    {
        foundClient = false;
        linkID = getClientLinkStatusID (iClientList[x]);

        // look in our list for our name
        for (int y = 0; y < connEntries; y++)
        {
            // if the names match
            if(ciStringEqual(iConnectionList[y]->getName(),iClientList[x]))
            {
                iConnectionList[y]->setLinkStatusID(linkID);
                iConnectionList[y]->sendLinkState (FDR_CONNECTED);
                foundClient = true;
            }
        }

        // if we didn't find the client, send not connected
        if ((!foundClient) && (linkID))
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
/*************************************************
* Function Name: CtiFDR_Inet::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_Inet::stop( void )
{
    //
    // FIXFIXFIX  - may need to add exception handling here
    //
    shutdownListener();
    iThreadClient.requestCancellation();
    iThreadMonitor.requestCancellation();
    iThreadServer.requestCancellation();

    if (isInterfaceInDebugMode())
    {
        iThreadSendDebugData.requestCancellation();
    }

    // stop the base class
    Inherited::stop();

    return TRUE;
}

/************************************************************************
* Function Name: CtiFDR_Inet::loadTranslationList()
*
* Description: Creates a seperate collection of Status and Analog Point
*              IDs and Inet id for translation.
*
*************************************************************************
*/
bool CtiFDR_Inet::loadTranslationLists()
{
    bool retCode = true;

    retCode = Inherited::loadTranslationLists();

    if (retCode)
    {
        retCode = loadClientList ();
    }
    return retCode;
}

/************************************************************************
* Function Name: CtiFDR_Inet::loadList()
*
* Description: Creates a collection of points and their translations for the
*                               specified direction
*
*************************************************************************
*/
bool CtiFDR_Inet::loadList(string &aDirection,  CtiFDRPointList &aList)
{
    bool successful = false;
    bool foundPoint = false;

    int entries;

    try
    {
        // make a list with all received points
        CtiFDRManager *pointList = new CtiFDRManager(getInterfaceName(), aDirection);

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
            if (((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2)) || (pointList->entries() > 0))
            {
                // get iterator on send list
                CtiFDRManager::spiterator  myIterator = pointList->getMap().begin();

                for ( ; myIterator != pointList->getMap().end(); ++myIterator )
                {
                    foundPoint = true;
                    successful = translateSinglePoint(myIterator->second);
                }

                // lock the list I'm swapping
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
                            CTILOG_DEBUG(dout, "No "<< aDirection <<" points defined for use by interface "<< getInterfaceName());
                        }
                    }
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Could not load ("<< aDirection <<") points for "<< getInterfaceName() <<" : Empty data set returned");
                successful = false;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to load points from database for "<< getInterfaceName());
            successful = false;
        }
    }   // end try block

    catch (const RWExternalErr& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to load point list for "<< getInterfaceName());

        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to load point list for "<< getInterfaceName());
    }

    return successful;
}


bool CtiFDR_Inet::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = false;
    string tempString1;
    string tempString2;
    string translationName;

    for (int x=0; x < translationPoint->getDestinationList().size(); x++)
    {
        const string translation = translationPoint->getDestinationList()[x].getTranslation();
        boost::char_separator<char> sep1(";");
        Boost_char_tokenizer nextTranslate(translation, sep1);
        Boost_char_tokenizer::iterator tok_iter = nextTranslate.begin();

        if ( tok_iter != nextTranslate.end() )
        {
            tempString1 = *tok_iter;tok_iter++;
            boost::char_separator<char> sep2(":");
            Boost_char_tokenizer nextTempToken(tempString1, sep2);
            Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin();

            if( tok_iter1 != nextTempToken.end() )
            {
                tok_iter1++;
                if( tok_iter1 != nextTempToken.end() )
                {
                    tempString2 = *tok_iter1;
                }else
                {
                    tempString2 = "";
                }

                // now we have a device name
                if ( !tempString2.empty() )
                {
                    // blank pad device
                    tempString2.resize(20,' ');
                    translationName = tempString2;

                    // next token is the point name
                    if ( tok_iter != nextTranslate.end())
                    {
                        tempString1 = *tok_iter;
                        boost::char_separator<char> sep2(":");
                        Boost_char_tokenizer nextTempToken(tempString1, sep2);
                        Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin();

                        tok_iter1++;
                        if( tok_iter1 != nextTempToken.end() )
                        {
                            tempString2 = *tok_iter1;

                            // now we have a point name:
                            if ( !tempString2.empty() )
                            {
                                tempString2.resize(20,' ');
                                translationName += tempString2;
                                std::transform(translationName.begin(), translationName.end(), translationName.begin(), ::toupper);

                                translationPoint->getDestinationList()[x].setTranslation(translationName);
                                /***/
                                string s = translationPoint->getDestinationList()[x].getDestination();
                                if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CTILOG_DEBUG(dout, "Destination: " << translationPoint->getDestinationList()[x].getDestination());
                                }
                                std::transform(s.begin(), s.end(), s.begin(), ::toupper);
                                translationPoint->getDestinationList()[x].setDestination(s);
                                if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CTILOG_DEBUG(dout, "Destination: " << translationPoint->getDestinationList()[x].getDestination());
                                }
                                /***/
                                successful = true;

                                if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                                {
                                    CTILOG_DEBUG(dout, "Point ID "<< translationPoint->getPointID() <<
                                            " translated: "<< translationName <<" for "<< translationPoint->getDestinationList()[x].getDestination());
                                }

                            }   // point id invalid
                        }
                    }   // second token invalid

                }   // category invalid
            }
        }   // first token invalid

    }   // end of while entries

    return successful;
}

/************************************************************************
* Function Name: CtiFDR_Inet::loadDestinationList()
*
* Description: Creates a collection of destinations
*
*************************************************************************
*/
bool CtiFDR_Inet::loadClientList()
{
    bool                successful(FALSE);
    CtiFDRPointSPtr     translationPoint;
    string              receiveConnections;
    int                 entries;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       string (FDR_INTERFACE_SEND));

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
        {
            CtiLockGuard<CtiMutex> destGuard(iClientListMux);
            iClientList.erase (iClientList.begin(),iClientList.end());

            // get iterator on send list
            CtiFDRManager::spiterator  myIterator = pointList->getMap().begin();

            if (pointList->getMap().size())
            {
                for ( ; myIterator != pointList->getMap().end(); ++myIterator )
                {
                    translationPoint = (*myIterator).second;

                    for (int x=0; x < translationPoint->getDestinationList().size(); x++)
                    {
                        bool foundDestination = false;

                        for (int y=0; y < iClientList.size(); y++)
                        {
                            if(ciStringEqual(iClientList[y],translationPoint->getDestinationList()[x].getDestination()))
                                foundDestination = true;
                        }

                        if (!foundDestination)
                        {
                            iClientList.push_back (translationPoint->getDestinationList()[x].getDestination());
                            successful = true;
                        }
                    }
                }
            }
            else
            {
                successful = true;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to load points from database for "<< getInterfaceName());
            successful = false;
        }
        // we're always newing this so delete it everytime through
        delete pointList;

        /******************************
        * note:  for the RCCS interface we are always sending data
        * to the machine we may receive data from
        * because of this, the server list is always empty
        *******************************
        */
        receiveConnections = getCparmValueAsString(KEY_SERVER_LIST);
        if(receiveConnections.length() != 0)
        {
            boost::char_separator<char> sep(",");
            Boost_char_tokenizer next(receiveConnections, sep);
            Boost_char_tokenizer::iterator tok_iter = next.begin();

            string       myInterfaceName;
            string       tempString;

            // parse the interfaces
            while ( tok_iter != next.end() )
            {
                myInterfaceName = *tok_iter;tok_iter++;
                bool foundDestination = false;

                for (int y=0; y < iClientList.size(); y++)
                {
                    if(ciStringEqual(iClientList[y],myInterfaceName))
                        foundDestination = true;
                }

                if (!foundDestination)
                {
                    iClientList.push_back (myInterfaceName);
                    successful = true;
                }
            } // end while (!(myInterfaceName=next
        }
    }   // end try block

    catch (const RWExternalErr& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to load client list for "<< getInterfaceName());

        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to load client list for "<< getInterfaceName());
    }

    return successful;
}

/*************************************************
* Function Name: CtiFDR_Inet::config()
*
* Description: loads cparm config values
*
**************************************************
*/
bool CtiFDR_Inet::readConfig()
{
    string   tempStr;


    tempStr = getCparmValueAsString(KEY_LISTEN_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPortNumber (atoi(tempStr.c_str()));
    }
    else
    {
        setPortNumber (INET_PORTNUMBER);
    }
    // since inet and rccs are the only interfaces that intiate the connection, we must make sure we set the
    // connect port number also
    setConnectPortNumber (getPortNumber());

    tempStr = getCparmValueAsString(KEY_IP_MASK);
    if (tempStr.length() > 0)
    {
        setIpMask(tempStr);
    }
    else
    {
        setIpMask("");
    }

    tempStr = getCparmValueAsString(KEY_TIMESTAMP_WINDOW);
    if (tempStr.length() > 0)
    {
        setTimestampReasonabilityWindow (atoi (tempStr.c_str()));
    }
    else
    {
        setTimestampReasonabilityWindow (120);
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_SOURCE_NAME);
    if (tempStr.length() > 0)
    {
        iSourceName = tempStr;
    }
    else
    {
        iSourceName = string("YUKON");
    }


    tempStr = getCparmValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
        setInterfaceDebugMode (true);
    else
        setInterfaceDebugMode (false);

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
    }
    else
    {
        // default to 5 seconds for inet
        setQueueFlushRate (1);
    }

    tempStr = getCparmValueAsString(KEY_SERVER_LIST);

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add("INET port number")      << getPortNumber();
        loglist.add("INET timestamp window") << getTimestampReasonabilityWindow();
        loglist.add("INET db reload rate")   << getReloadRate();
        loglist.add("INET source name")      << iSourceName;

        if(tempStr.length() != 0)
            loglist <<"INET receive only connections will be initialized";

        if (isInterfaceInDebugMode())
            loglist <<"INET running in debug mode";
        else
            loglist <<"INET running in normal mode";

        CTILOG_DEBUG(dout, loglist);
    }

    return true;
}


/**************************************************************************
* Function Name: CtiFDR_Inet::sendMessageToForeignSys ()
*
* Description: We must find the appropriate destination first and then do our write
*
***************************************************************************
*/

bool CtiFDR_Inet::buildAndWriteToForeignSystem (CtiFDRPoint &aPoint )
{
    bool retVal = true;
    int  connectionIndex;;
    CHAR *foreignSys=NULL;

    if (getDebugLevel () & MAJOR_DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, "Building message to send to INET");
    }

    // now loop thru the many possible destinations for the point
    for (int x=0; x < aPoint.getDestinationList().size(); x++)
    {
        CtiLockGuard<CtiMutex> guard(iConnectionListMux);
        connectionIndex = findConnectionByNameInList (aPoint.getDestinationList()[x].getDestination());

        if (connectionIndex != -1)
        {
            /**************************
            * we allocate an inet message here and it will be deleted
            * inside of the write function on the connection
            ***************************
            */
            foreignSys = new CHAR[sizeof (InetInterface_t)];
            InetInterface_t *ptr = (InetInterface_t *)foreignSys;

            // make sure we have all the pieces
            if (foreignSys != NULL)
            {
                // put everything in the message
                ptr->Type = INETTYPEVALUE;
                iSourceName.resize(INETDESTSIZE,' ');
                strncpy (ptr->SourceName, iSourceName.c_str(), INETDESTSIZE);

                CtiTime timestamp(aPoint.getLastTimeStamp());
                if (timestamp < CtiTime(CtiDate(1,1,2001)))
                {
                    timestamp = CtiTime();
                }

                ptr->msgUnion.value.TimeStamp = (timestamp.seconds());
                strncpy (ptr->msgUnion.value.DeviceName, aPoint.getDestinationList()[x].getTranslation().c_str(),20);
                strncpy (ptr->msgUnion.value.PointName, &aPoint.getDestinationList()[x].getTranslation()[20],20);

                /***********************
                * for exchanging with DSM2 systems
                * the daylight savings flag is the most significant bit
                * in the quality, if we are in daylight savings, I need to set the quality
                * so the receiving side knows the time
                ************************
                */
                ptr->msgUnion.value.Quality = YukonToForeignQuality (aPoint.getQuality());
                if (timestamp.isDST())
                {
                    ptr->msgUnion.value.Quality |= DSTACTIVE;
                }
                 // need to intercept sending a status point to make it DSM2 like
                switch (aPoint.getPointType())
                {
                    case CalculatedStatusPointType:
                    case StatusPointType:
                        {
                            ptr->msgUnion.value.Value = aPoint.getValue()+1;
                            break;
                        }
                    default:
                        ptr->msgUnion.value.Value = aPoint.getValue();
                        break;
                }
                ptr->msgUnion.value.AlarmState = ClientErrors::None;

                /**************************
                * if we get this far, the connection list must exist so no null check
                * required (memory is consumed no matter what if we get this far)
                ***************************
                */
                string clientName(ptr->SourceName);
                string deviceName(ptr->msgUnion.value.DeviceName);
                string pointName(ptr->msgUnion.value.PointName);

                // memory is consumed no matter what
                if (!iConnectionList[connectionIndex]->write(foreignSys))
                {
                    clientName.resize(INETDESTSIZE,' ');
                    deviceName.resize(20,' ');
                    pointName.resize(20,' ');

                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, trim(deviceName) <<" "<< trim(pointName) <<" sent to "<< iConnectionList[connectionIndex]->getName());
                    }

                    // successfully sent message
                    retVal = true;
                }
                else
                {
                    CTILOG_ERROR(dout, "Could not send "<< trim(deviceName) <<" "<< trim(pointName) <<" to "<< iConnectionList[connectionIndex]->getName());
                }
            }
        }
        else
        {
            if (getDebugLevel () & MAJOR_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Could not find the destination connection. <"<< aPoint.getDestinationList()[x].getDestination() <<">");
            }
        }
    }
    return retVal;
}


int CtiFDR_Inet::findConnectionByNameInList(string aName)
{
    bool                    foundFlag= false;
    int                     index = 0, foundIndex=-1;
    int                     entries;

        // list must be protected outside of this call
    entries = iConnectionList.size();

    while ((index < entries) && !foundFlag)
    {
        // find the point id
        if(ciStringEqual(iConnectionList[index]->getName(),aName))
        {
            foundFlag = true;
            foundIndex = index;
        }
        // increment our counter
        index++;
    }
    return foundIndex;
}

int CtiFDR_Inet::findClientInList(const Cti::SocketAddress& aAddr)
{
    if( !aAddr )
    {
        return -1;
    }

    int index = 0;

    for each( CtiFDRSocketLayer* conn in iConnectionList )
    {
        if( conn->getOutBoundConnection() && conn->getOutBoundConnection()->getAddr() == aAddr )
        {
            return index;
        }

        index++;
    }

    return -1; // client not found
}

/**************************************************************************
* Function Name: CtiFDR_Inet::threadFunctionConnection
*
* Description: thread that watches connection status and re-establishes it as needed
*
***************************************************************************
*/
void CtiFDR_Inet::threadFunctionMonitor( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );

    boost::ptr_vector<CtiFDRSocketLayer> connectionsToDelete;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Initializing threadFunctionMonitor");
        }
        // don't try to do anything while the list is null
        while (iConnectionList.size() == 0)
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (500);
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (250);

            //
            // Copy all connections to delete after iConnectionListMux is released
            //

            {
                CtiLockGuard<CtiMutex> guard(iConnectionListMux);

                vector<CtiFDRSocketLayer*>::iterator connectionIt = iConnectionList.begin() ;

                // iterate list or until we find our entry
                /************************
                * we have a vector of pointers so we have to de-reference
                * the iterator before we can access the pointer
                *************************
                */
                while( connectionIt != iConnectionList.end() )
                {
                    // see if we failed
                    if( (*connectionIt)->getInBoundConnectionStatus() == CtiFDRSocketConnection::Failed ||
                        (*connectionIt)->getOutBoundConnectionStatus() == CtiFDRSocketConnection::Failed )
                    {
                        CTILOG_ERROR(dout, (*connectionIt)->getName() <<"'s link has failed");

                        // log it before we kill it
                        string action, desc;
                        desc = (*connectionIt)->getName()+ "'s link has failed";
                        logEvent (desc,action, true);

                        connectionsToDelete.push_back( *connectionIt );

                        connectionIt = iConnectionList.erase(connectionIt);

                        break;
                    }
                    else
                    {
                        connectionIt++;
                    }
                }
            }

            //
            // Delete all connections
            //

            boost::ptr_vector<CtiFDRSocketLayer>::iterator toDeleteIt = connectionsToDelete.begin();

            while( toDeleteIt != connectionsToDelete.end() )
            {
                CtiFDRSocketLayer::FDRConnectionType connectionType = toDeleteIt->getConnectionType();

                toDeleteIt = connectionsToDelete.erase( toDeleteIt );

                // if its a server connection, the client will re-connect
                if( connectionType == CtiFDRSocketLayer::Client_Multiple )
                {
                    // this signals the client thread to spit out another
                    SetEvent( iClientConnectionSemaphore );
                }
            }
        }
    }

    catch( RWCancellation &cancellationMsg )
    {
        {
            CtiLockGuard<CtiMutex> guard(iConnectionListMux);

            // copy all connection pointers to delete after iConnectionListMux is released
            for each( CtiFDRSocketLayer* conn in iConnectionList )
            {
                connectionsToDelete.push_back( conn );
            }

            iConnectionList.clear();
        }

        // destroy the connection list here
        connectionsToDelete.clear();

        setCurrentClientLinkStates();

        CTILOG_INFO(dout, "CANCELLATION of threadFunctionMonitor");
    }

    // try and catch the thread death
    catch(...)
    {
        {
            CtiLockGuard<CtiMutex> guard(iConnectionListMux);

            // copy all connection pointers to delete after iConnectionListMux is released
            for each( CtiFDRSocketLayer* conn in iConnectionList )
            {
                connectionsToDelete.push_back( conn );
            }

            iConnectionList.clear();
        }

        // destroy the connection list here
        connectionsToDelete.clear();

        setCurrentClientLinkStates();

        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionMonitor is dead!");
    }
}


void CtiFDR_Inet::threadFunctionClientConnection( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    ULONG postCount;
    int destEntries=0, connEntries=0, index=0;
    bool foundFlag=false, allFound=false;
    CtiFDRSocketLayer   *layer;
    DWORD semRet;
    int retVal;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Initializing threadFunctionClientConnection");
        }

        iClientConnectionSemaphore = CreateEvent ((LPSECURITY_ATTRIBUTES)NULL,TRUE,false,NULL);
        if (iClientConnectionSemaphore != NULL)
        {
            // load inital clients
            while (!allFound)
            {
                allFound = findAndInitializeClients();

                pSelf.sleep (500);
                pSelf.serviceCancellation( );
            }

            for ( ; ; )
            {
                pSelf.serviceCancellation( );
                pSelf.sleep (500);

                // returns an error 1 for a timeout, error or otherwise
                semRet = WaitForSingleObject(iClientConnectionSemaphore, 5000L);

                // returns an error 1 for a timeout, error or otherwise
                if(semRet == WAIT_TIMEOUT)
                {
                    pSelf.serviceCancellation( );

                    // reset here because of processing elsewhere
                    ResetEvent (iClientConnectionSemaphore);
                }
                else
                {
                    pSelf.serviceCancellation( );

                    // reset here because of processing elsewhere
                    ResetEvent (iClientConnectionSemaphore);

                    /***********************
                    * if we were tripped, we need to
                    * walk our destination list and re-initialize
                    * the client that has gone away
                    ************************
                    */
                    allFound = false;
                    while (!allFound)
                    {
                        allFound = findAndInitializeClients();

                        pSelf.sleep (1000);
                        pSelf.serviceCancellation( );
                    }
                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to create connection semaphore for "<< getInterfaceName() <<", loading interface failed");
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CTILOG_INFO(dout, "CANCELLATION of threadFunctionClientConnection");
    }
    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionClientConnection is dead!");
    }
}

bool  CtiFDR_Inet::findAndInitializeClients( void )
{
    int destEntries=0, connEntries=0, index=0;
    CtiFDRClientConnection *clientConnection = NULL;
    DWORD semRet;
    BYTE *ptr;
    bool retVal=true,foundFlag=false;
    string            desc;
    string           action;

    CtiLockGuard<CtiMutex> destGuard(iClientListMux);
    CtiLockGuard<CtiMutex> guard(iConnectionListMux);

    destEntries = iClientList.size();
    connEntries = iConnectionList.size();

    for (int x=0; x < destEntries; x++)
    {
        foundFlag = false;

        // look in our list for our name
        for (int y = 0; y < connEntries; y++)
        {
            // if the names match
            if(ciStringEqual(iConnectionList[y]->getName(),iClientList[x]))
            {
                foundFlag = true;
            }
        }

        // if we found the connection do nothing, otherwise re-init
        if (foundFlag == false)
        {
            CtiFDRSocketLayer *layer = new CtiFDRSocketLayer (iClientList[x], CtiFDRSocketLayer::Client_Multiple, this);
            retVal = layer->init();

            if (!retVal )
            {
                layer->setLinkStatusID(getClientLinkStatusID(iClientList[x]));
                retVal = layer->run();

                if (retVal)
                {
                    retVal = false;
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, "Initialization failed for " << iClientList[x]);
                    }
                    delete layer;
                }
                else
                {
                    const string AddrStr = layer->getOutBoundConnection()->getAddr().toString();

                    CTILOG_INFO(dout, "Client connection initialized for "<< iClientList[x] <<" at "<< AddrStr);

                    desc = iClientList[x] + string ("'s client link has been established at ") + AddrStr;
                    logEvent (desc,action, true);
                    iConnectionList.push_back (layer);
                }
            }
            else
            {
                retVal = false;

                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Initialization failed for "<< iClientList[x] <<" client layer would not initialize");
                }
                delete layer;
            }
        }
    }
    return retVal;
}



void CtiFDR_Inet::threadFunctionServerConnection( void )
{
    using Cti::Timing::Chrono;

    RWRunnableSelf pSelf = rwRunnable( );
    string desc, action;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Initializing threadFunctionServerConnection");
        }

        // retrieve socket addrinfo for listener sockets
        Cti::AddrInfo pAddrInfo = Cti::makeTcpServerSocketAddress(getPortNumber());
        if( ! pAddrInfo )
        {
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Failed in host name to address translation");
            }
            return;
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep(500);

            try
            {
                CtiLockGuard<CtiMutex> lock(_listenerMux);

                if( isListenerShutdown() )
                {
                    // listener sockets have already shutdown
                    return;
                }

                if( !_listenerSockets.areSocketsValid() )
                {
                    // create sockets from the addrinfo
                    _listenerSockets.createSockets(pAddrInfo.get());

                    // set sockets options to allows socket to bind to an address and port already in use
                    BOOL ka = TRUE;
                    _listenerSockets.setOption(SOL_SOCKET, SO_REUSEADDR, (char*)&ka, sizeof(BOOL));

                    // bind the socket
                    _listenerSockets.bind(pAddrInfo.get());

                    // set sockets in listening state
                    _listenerSockets.listen(SOMAXCONN);
                }
            }
            catch( Cti::SocketException& e )
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                   CTILOG_EXCEPTION_ERROR(dout, e, "Failed to setup listener sockets");
                }
                continue;
            }

            CTILOG_INFO(dout, "Listening for connection on port "<< getPortNumber());

            Cti::SocketAddress returnAddr( Cti::SocketAddress::STORAGE_SIZE );

            // accept a new socket connection
            SOCKET tmpConnection = _listenerSockets.accept(returnAddr, Chrono::infinite, &_listenerShutdownEvent);

            if( tmpConnection == INVALID_SOCKET )
            {
                if( getDebugLevel () & DETAIL_FDR_DEBUGLEVEL )
                {
                    CTILOG_DEBUG(dout, "Accept call failed with error code: "<< _listenerSockets.getLastError());
                }
                continue;
            }

            // set to non blocking mode
            ULONG param=1;
            ioctlsocket(tmpConnection, FIONBIO, &param);

            auto_ptr<CtiFDRServerConnection> connection( new CtiFDRServerConnection( tmpConnection, returnAddr ));

            string connAddrStr = connection->getAddr().toString();
            CTILOG_INFO(dout, "Server connection processed for client at "<< connAddrStr);

            {
                CtiLockGuard<CtiMutex> guard(iConnectionListMux);

                //-----------------------------------------------------
                // check our list for a possible client
                //-----------------------------------------------------
                const int connectionIndex = findClientInList (connection->getAddr());

                // if it returns -1, the client wasn't found
                if (connectionIndex == -1)
                {
                    auto_ptr<CtiFDRSocketLayer> layer(new CtiFDRSocketLayer(string(), connection.release(), CtiFDRSocketLayer::Server_Multiple, this));

                    // this will start everything appropriately
                    if (!layer->init())
                    {
                        if (!layer->run())
                        {
                            iConnectionList.push_back (layer.release());
                        }
                        else
                        {
                            desc = string (" Client connection to ") + connAddrStr + string (" has failed");
                            CTILOG_ERROR(dout, desc);
                            logEvent (desc,action, true);
                        }
                    }
                    else
                    {
                        desc = string (" Client connection to ") + connAddrStr + string (" has failed");
                        CTILOG_ERROR(dout, desc);
                        logEvent (desc,action, true);
                    }
                }
                else
                {
                    // found the client so put the server connection there
                    if (iConnectionList[connectionIndex]->getInBoundConnection() == NULL)
                    {
                        // set the parent to the found connection
                        connection->setParent (iConnectionList[connectionIndex]);
                        connection->init();

                        // attach it to the client and start it up
                        iConnectionList[connectionIndex]->setInBoundConnection (connection.release());
                        iConnectionList[connectionIndex]->getInBoundConnection()->run();
                    }
                    else
                    {
                        desc = string (" Server connection for ") + iConnectionList[connectionIndex]->getName() + string (" already established ");
                        CTILOG_ERROR(dout, desc);
                        logEvent (desc,action, true);
                    }
                }
            }
        }
    }
    catch ( RWCancellation &cancellationMsg )
    {
        CTILOG_INFO(dout, "CANCELLATION of threadFunctionServerConnection");
    }
    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionServerConnection is dead!");
    }
}

void CtiFDR_Inet::threadFunctionSendDebugData( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0;
    CtiFDRPointSPtr point;
    CtiPointDataMsg     *localMsg;
    int quality = NormalQuality, index, entries;
    FLOAT value = 1.0;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Initializing threadFunctionSendDebugData");
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);


            index=0;
            {
                CtiFDRManager* mgrPtr = getSendToList().getPointList();
                CtiFDRManager::readerLock guard(mgrPtr->getLock());

                CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
                CtiFDRManager::spiterator myIterator = mgrPtr->getMap().begin();

                for ( ; myIterator != mgrPtr->getMap().end(); ++myIterator )
                {
                    // find the point id
                    point = (*myIterator).second;

                    localMsg = new CtiPointDataMsg (point->getPointID(), value, quality, point->getPointType());
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

    catch ( RWCancellation &cancellationMsg )
    {
        CTILOG_INFO(dout, "CANCELLATION of threadFunctionSendDebugData");
    }
    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionDebugData is dead!");
    }
}

CHAR *CtiFDR_Inet::buildForeignSystemHeartbeatMsg ()
{
    /***********************
    *  data is allocated for the buffer inside this call
    *  it will be deleted inside  the socketlayer
    ************************
    */
    CHAR *foreignSys=NULL;

    /**************************
    * we allocate an inet message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    foreignSys = new CHAR[sizeof (InetInterface_t)];
    InetInterface_t *ptr = (InetInterface_t *)foreignSys;

    // make sure we have all the pieces
    if (foreignSys != NULL)
    {
        ptr->Type = INETTYPENULL;
        iSourceName.resize(10,' ');
        strncpy (ptr->SourceName, iSourceName.c_str(),10);
    }
    return foreignSys;
}

int CtiFDR_Inet::getMessageSize(CHAR *aBuffer)
{
    int retVal = 0;
    USHORT *type = (USHORT *)aBuffer;

    switch (*type)
    {
        case INETTYPEVALUE:
            retVal = sizeof (InetValue_t);
            break;
        case INETTYPESHUTDOWN:
            retVal = sizeof (InetShutdown_t);
            break;
        case INETTYPENULL:
            retVal = sizeof (InetHeartbeat_t);
            break;
        default:
            retVal = 0;
    }
    return retVal;
}

string CtiFDR_Inet::decodeClientName(CHAR * aBuffer)
{
    InetInterface_t *data = (InetInterface_t*)aBuffer;
    string tmpName(data->SourceName);

    tmpName.resize(INETDESTSIZE,' ');
    string clientName (trim(tmpName));

    return clientName;
}


int CtiFDR_Inet::processMessageFromForeignSystem(CHAR *aBuffer)
{
    int retVal = ClientErrors::None;
    InetInterface_t *data = (InetInterface_t*)aBuffer;
    string clientName(data->SourceName);
    string deviceName(data->msgUnion.value.DeviceName);
    string pointName(data->msgUnion.value.PointName);

    clientName.resize(INETDESTSIZE,' ');
    deviceName.resize(20,' ');
    pointName.resize(20,' ');

    switch (data->Type)
    {
        case INETTYPESHUTDOWN:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Shutdown message received from "<< clientName);
                }
                break;
            }
        case INETTYPENULL:
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Heartbeat message received from "<< clientName);
                }
                break;
            }
        case INETTYPEVALUE:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, trim(deviceName) <<" "<< trim(pointName) <<" received from "<< trim(clientName));
                }
                retVal = processValueMessage (data);
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Default or invalid type from "<< clientName <<" type "<< data->Type);
                // process them all as value messages
                retVal = processValueMessage (data);
                break;
            }

    }
    return retVal;

}

int CtiFDR_Inet::processValueMessage(InetInterface_t *data)
{
    int retVal = ClientErrors::None;
    CtiPointDataMsg     *pData;
    string           translationName (data->msgUnion.value.DeviceName);
    int                 quality;
    DOUBLE              value;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool                flag = true;
    string            traceState;
    CHAR            action[60];
    string           desc;

    string tmp = string (data->msgUnion.value.PointName);
    translationName.resize(20,' ');
    tmp.resize(20,' ');

    // convert to our name
    translationName += tmp;

    // chops off the rest of the message
//    translationName.resize(40,' ');
    flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if (flag == true)
    {
        // need to process accordingly in here
        switch (point.getPointType())
        {
            case AnalogPointType:
            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
            case CalculatedPointType:
                {
                    // assign last stuff
                    quality = ForeignToYukonQuality (data->msgUnion.value.Quality);
                    value = data->msgUnion.value.Value;
                    value *= point.getMultiplier();
                    value += point.getOffset();

                    timestamp = CtiTime (data->msgUnion.value.TimeStamp);
                    if (timestamp == PASTDATE)
                    {
                        desc = decodeClientName((CHAR*)data) + string (" analog point received with an invalid timestamp ");
                        _snprintf(action,60,"%s for pointID %d",
                                  translationName.c_str(),
                                  point.getPointID());
                        logEvent (desc,string (action));
                        retVal = ClientErrors::Abnormal;
                    }
                    else
                    {
                        pData = new CtiPointDataMsg(point.getPointID(),
                                                    value,
                                                    quality,
                                                    point.getPointType());

                        pData->setTime(timestamp);
                        // consumes a delete memory
                        queueMessageToDispatch(pData);

                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "Analog point "<< translationName <<
                                    " value "<< value <<" from "<< getInterfaceName() <<" assigned to point "<< point.getPointID());
                        }
                    }
                    break;
                }
            case StatusPointType:
                {
                    // check for control functions
                    if (point.isControllable())
                    {
                        int controlState=-1;

                        // make sure the value is valid
                        if (data->msgUnion.value.Value == Inet_Open)
                        {
                            controlState = STATE_OPENED;
                        }
                        else if (data->msgUnion.value.Value == Inet_Closed)
                        {
                            controlState = STATE_CLOSED;
                        }
                        else
                        {
                            CTILOG_ERROR(dout, "Invalid control state "<< data->msgUnion.value.Value <<
                                    " for "<< translationName <<" received from "<< getInterfaceName());

                            CHAR state[20];
                            _snprintf (state,20,"%.0f",data->msgUnion.value.Value);
                            desc = decodeClientName((CHAR*)data) + string (" control point received with an invalid state ") + string (state);
                            _snprintf(action,60,"%s for pointID %d",
                                      translationName.c_str(),
                                      point.getPointID());
                            logEvent (desc,string (action));
                            retVal = ClientErrors::Abnormal;
                        }

                        if (controlState != -1)
                        {
                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                Cti::StreamBuffer logmsg;
                                logmsg <<" Control point "<< translationName;
                                if (controlState == STATE_OPENED)
                                {
                                    logmsg <<" control: Open ";
                                }
                                else
                                {
                                    logmsg <<" control: Closed ";
                                }

                                logmsg <<" from "<< getInterfaceName() <<" and processed for point "<< point.getPointID();

                                CTILOG_DEBUG(dout, logmsg);
                            }

                            // build the command message and send the control
                            CtiCommandMsg *cmdMsg;
                            cmdMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                            cmdMsg->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                            cmdMsg->insert(0);                   // device id, unknown at this point, dispatch will find it
                            cmdMsg->insert(point.getPointID());  // point for control
                            cmdMsg->insert(controlState);
                            sendMessageToDispatch(cmdMsg);
                        }
                    }
                    else
                    {
                        // assign last stuff
                        quality = ForeignToYukonQuality (data->msgUnion.value.Quality);
                        switch ((int)data->msgUnion.value.Value)
                        {
                            case Inet_Open:
                                value = STATE_OPENED;
                                traceState = string("Opened");
                                break;
                            case Inet_Closed:
                                value = STATE_CLOSED;
                                traceState = string("Closed");
                                break;
                            case Inet_Indeterminate:
                                value = STATE_INDETERMINATE;
                                traceState = string("Indeterminate");
                                break;
                            case Inet_State_Four:
                                value = STATEFOUR;
                                traceState = string("State Four");
                                break;
                            case Inet_State_Five:
                                value = STATEFIVE;
                                traceState = string("State Five");
                                break;
                            case Inet_State_Six:
                                value = STATESIX;
                                traceState = string("State Six");
                                break;
                            case Inet_Invalid:
                            default:
                                {
                                    value = STATE_INVALID;
                                }
                        }

                        if (value == STATE_INVALID)
                        {
                            CTILOG_ERROR(dout, "Status point "<< translationName <<" received an invalid state "<< (int)data->msgUnion.value.Value <<
                                    " from "<< getInterfaceName() <<" for point "<< point.getPointID());

                            CHAR state[20];
                            _snprintf (state,20,"%.0f",data->msgUnion.value.Value);
                            desc = decodeClientName((CHAR*)data) + string (" status point received with an invalid state ") + string (state);
                            _snprintf(action,60,"%s for pointID %d",
                                      translationName.c_str(),
                                      point.getPointID());
                            logEvent (desc,string (action));
                            retVal = ClientErrors::Abnormal;

                        }
                        else
                        {
                            timestamp = CtiTime (data->msgUnion.value.TimeStamp);
                            if (timestamp == PASTDATE)
                            {
                                desc = decodeClientName((CHAR*)data) + string (" status point received with an invalid timestamp ");
                                _snprintf(action,60,"%s for pointID %d",
                                          translationName.c_str(),
                                          point.getPointID());
                                logEvent (desc,string (action));
                                retVal = ClientErrors::Abnormal;
                            }
                            else
                            {
                                pData = new CtiPointDataMsg(point.getPointID(),
                                                            value,
                                                            quality,
                                                            StatusPointType);

                                pData->setTime(timestamp);
                                // consumes a delete memory
                                queueMessageToDispatch(pData);
                                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CTILOG_DEBUG(dout, "Status point "<< translationName <<
                                            " new state: "<< traceState <<
                                            " from "<< getInterfaceName() <<" assigned to point "<< point.getPointID());
                                }
                            }
                        }
                    }
                    break;
                }
        }

    }
    else
    {
        CTILOG_ERROR(dout, "Translation for point "<< translationName <<
                " from "<< getInterfaceName() <<" was not found");

        desc = decodeClientName((CHAR*)data) + string ("'s point is not listed in the translation table");
        _snprintf(action,60,"%s", translationName.c_str());
        logEvent (desc,string (action));
        retVal = ClientErrors::Abnormal;
    }

    return retVal;
}


USHORT CtiFDR_Inet::ForeignToYukonQuality (USHORT aQuality)
{
    USHORT Quality = NormalQuality;

    // Test for the various dsm2 qualities
    if (aQuality & INETDATAINVALID)
        Quality = InvalidQuality;

    if (aQuality & INETUNREASONABLE)
        Quality = AbnormalQuality;

    if (aQuality & INETPLUGGED)
        Quality = NonUpdatedQuality;

    if (aQuality & INETMANUAL)
        Quality = ManualQuality;

    if (aQuality & INETOUTOFSCAN)
        Quality = UnknownQuality;

    return(Quality);
}

USHORT CtiFDR_Inet::YukonToForeignQuality (USHORT aQuality)
{
        USHORT Quality = INETDATAINVALID;

        // Test for the various CTI Qualities and translate to Inet
        if (aQuality == NonUpdatedQuality)
                Quality = INETPLUGGED;

        if (aQuality == ManualQuality)
                Quality = INETMANUAL;

        if (aQuality == InvalidQuality)
                Quality = INETDATAINVALID;

        if (aQuality == AbnormalQuality)
                Quality = INETUNREASONABLE;

        if (aQuality == NormalQuality)
                Quality = ClientErrors::None;

        return(htons (Quality));
}


/****************************************************************************************
*
*      Here Starts some C functions that are used to Start the
*      Interface and Stop it from the Main() of FDR.EXE.
*
*/

#ifdef __cplusplus
extern "C" {
#endif

/************************************************************************
* Function Name: Extern C int RunInterface(void)
*
* Description: This is used to Start the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function creates a global FDRCygnet Object and then
*              calls its run method to cank it up.
*
*************************************************************************
*/

    DLLEXPORT int RunInterface(void)
    {

        // make a point to the interface
        inetInterface = new CtiFDR_Inet();

        // now start it up
        return inetInterface->run();
    }

/************************************************************************
* Function Name: Extern C int StopInterface(void)
*
* Description: This is used to Stop the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function stops a global FDRCygnet Object and then
*              deletes it.
*
*************************************************************************
*/
    DLLEXPORT int StopInterface( void )
    {

        inetInterface->stop();
        delete inetInterface;
        inetInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif




