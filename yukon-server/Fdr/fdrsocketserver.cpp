#include "precompiled.h"

#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/

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
#include "fdrsocketlayer.h"
#include "fdrserverconnection.h"

// this class header
#include "fdrsocketserver.h"
#include "ctitime.h"
#include "ctidate.h"
#include "socket_helper.h"
#include "timing_util.h"

using std::pair;
using std::multimap;

// Constructors, Destructor, and Operators
CtiFDRSocketServer::CtiFDRSocketServer(string name) :
    CtiFDRInterface(name),
    _portNumber(0),
    _pointTimeVariation(0),
    _timestampReasonabilityWindow(0),
    _linkTimeout(0),
    _shutdownEvent(0),
    _singleListeningPort(true),
    _socketShutdown(false)
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;

    CtiFDRManager   *sendList = new CtiFDRManager(getInterfaceName(), string(FDR_INTERFACE_SEND));
    getSendToList().setPointList (sendList);
    sendList = NULL;


}


CtiFDRSocketServer::~CtiFDRSocketServer()
{
}

void CtiFDRSocketServer::clearFailedLayers() {
    CtiLockGuard<CtiMutex> guard(_connectionListMutex);
    ConnectionList::iterator myIter = _connectionList.begin();
    while (myIter != _connectionList.end())
    {
        // we copy the iter to a temp so we can
        // increment it before we delete the temp
        ConnectionList::iterator tempIter = myIter;
        ++myIter;
        if ((*tempIter)->isFailed()) {
            (*tempIter)->stop();
            _connectionList.erase(tempIter);
        }
    }
}

bool CtiFDRSocketServer::isClientConnectionValid()
{
    return _connectionList.size() > 0;
}

BOOL CtiFDRSocketServer::init( void )
{
    // init the base class
    CtiFDRInterface::init();

    _threadHeartbeat = rwMakeThreadFunction(*this, &CtiFDRSocketServer::threadFunctionSendHeartbeat);

    _shutdownEvent = CreateEvent(NULL, TRUE, FALSE, NULL);

    return TRUE;
}

BOOL CtiFDRSocketServer::run( void )
{

    // crank up the base class
    CtiFDRInterface::run();

    // load translation lists (This used to
    // be done in init() but it caused problems
    // because the class wasn't fully constructed.)
    loadTranslationLists();

    // start up the socket layer
    if (_singleListeningPort)
    {
        if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"Configured to run on a single port: "<< getPortNumber());
        }
        _threadSingleConnection = rwMakeThreadFunction(*this, &CtiFDRSocketServer::threadFunctionConnection, getPortNumber(), 0);
        _threadSingleConnection.start();
    }
    else
    {
        if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"Configured to run on multiple ports. Handled in extending code");
        }
    }

    // startup our interfaces
    _threadHeartbeat.start();

    // log this now so we dont' have to everytime one comes in
    if (!shouldUpdatePCTime())
    {
        string desc = getInterfaceName()
            + string (" has been configured to NOT process time sync updates to PC clock");
        logEvent (desc,string());
    }

    return TRUE;
}

BOOL CtiFDRSocketServer::stop( void )
{
    SetEvent(_shutdownEvent);

    {
        CtiLockGuard<CtiMutex> guard(_socketMutex);
        _socketShutdown = true;
        for( PortSocketsMap::iterator itr = _socketConnections.begin(); itr != _socketConnections.end(); itr++)
        {
            itr->second->shutdownAndClose();
        }
    }

    if(_singleListeningPort)
    {
        _threadSingleConnection.requestCancellation();
        _threadSingleConnection.join();
    }

    _threadHeartbeat.requestCancellation();
    _threadHeartbeat.join();

    // iterate through connections, shutting each one down.
    CtiLockGuard<CtiMutex> guard(_connectionListMutex);
    for (ConnectionList::const_iterator myIter = _connectionList.begin();
         myIter != _connectionList.end();
         ++myIter)
    {
        if (getDebugLevel() & CONNECTION_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"Stopping "<< (*myIter)->getName());
        }
        (*myIter)->stop();
    }

    // stop the base class
    CtiFDRInterface::stop();

    return TRUE;
}

bool CtiFDRSocketServer::loadTranslationLists()
{
    bool retCode = true;

    begineNewPoints();

    retCode = loadList(string(FDR_INTERFACE_SEND),getSendToList());

    if (retCode)
    {
        retCode = loadList(string (FDR_INTERFACE_RECEIVE),getReceiveFromList());
    }
    return retCode;
}


bool CtiFDRSocketServer::loadList(string &aDirection,  CtiFDRPointList &aList)
{
    bool successful = true;
    string translationName;
    bool foundPoint = false, translatedPoint = false;
    bool isSend = (aDirection == FDR_INTERFACE_SEND);

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),aDirection);

        // if status is ok, we were able to read the database at least
        if (!pointList->loadPointList())
        {
            CTILOG_ERROR(dout, logNow() << "Unable to load points from database");
            delete pointList;
            return false;
        }
        /* seeing occasional problems where we get empty data sets back
         * and there should be info in them,  we're checking this to see if
         * is reasonable if the list may now be empty
         * the 2 entry thing is completly arbitrary
         */
        if (((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2)) ||
            (pointList->entries() > 0))
        {
            CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());
            // get iterator on list
            CtiFDRManager::spiterator myIterator = pointList->getMap().begin();

            while (myIterator != pointList->getMap().end())
            {
                foundPoint = translateSinglePoint(myIterator->second,isSend);
                ++myIterator;
            }

            // lock the list I'm inserting into so it doesn't get deleted on me
            if (aList.getPointList() != NULL)
            {
                aList.deletePointList();
            }
            aList.setPointList(pointList);

            // set this to null, the memory is now assigned to the other point
            pointList = NULL;

            if (!foundPoint)
            {
                // means there was nothing in the list, wait until next db change or reload
                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, logNow() <<"No ("<< aDirection <<") "<< "points defined for use by interface");
                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, logNow() <<"Could not load (" << aDirection << ") points, empty data set returned");
            successful = false;
        }
    }   // end try block

    catch (const RWExternalErr& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, logNow() <<"Failed to load point list");
        RWTHROW(e); // is this the right thing to do???
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, logNow() <<"Failed to load point list");
        successful = false;
    }

    return successful;
}

bool CtiFDRSocketServer::buildForeignSystemHeartbeatMsg(char** buffer, unsigned int& bufferSize)
{
    bufferSize = 0;
    *buffer = NULL;
    return false;
}

void CtiFDRSocketServer::threadFunctionSendHeartbeat( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );

    try
    {
        if (getDebugLevel () & CONNECTION_INFORMATION_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"threadFunctionSendHeartbeat initializing");
        }

        for ( ; ; )
        {
            int heartBeatPeriodMSecs = 10000;
            DWORD waitResult = WaitForSingleObject(_shutdownEvent, heartBeatPeriodMSecs);
            if (waitResult == WAIT_OBJECT_0)
            {
                // shutdown event
                break;
            }
            // just in case
            pSelf.serviceCancellation();

            clearFailedLayers();

            CtiLockGuard<CtiMutex> guard(_connectionListMutex);
            ConnectionList::const_iterator myIter;
            for (myIter = _connectionList.begin(); myIter != _connectionList.end(); ++myIter)
            {
                char* heartbeatMsg = NULL;
                unsigned int size = 0;
                bool result = buildForeignSystemHeartbeatMsg(&heartbeatMsg, size);
                if (result && size > 0)
                {
                    if (getDebugLevel () & CONNECTION_HEALTH_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, logNow() <<"Queuing heartbeat message to "<< **myIter);
                    }

                    bool thisResult = (*myIter)->queueMessage(heartbeatMsg, size, (MAXPRIORITY-1));
                    if (!thisResult)
                    {
                        // queueMessage failed, must delete memory ourselves
                        delete[] heartbeatMsg;
                    }
                }
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        // fall through
    }
    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, logNow() <<"threadFunctionSendHeartbeat is dead!");
    }
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<"threadFunctionSendHeartbeat shutdown");
    }
}

void CtiFDRSocketServer::threadFunctionConnection( unsigned short listeningPort, int startupDelaySeconds )
{
    using Cti::Timing::Chrono;

    RWRunnableSelf  pSelf = rwRunnable( );

    try {

        if (getDebugLevel() & CONNECTION_INFORMATION_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"threadFunctionConnection initializing in "<< startupDelaySeconds <<" seconds.");
        }

        //Delay for initial load. This gives Dispatch a chance to give us data before being bombarded by VALMET
        Sleep(startupDelaySeconds*1000);

        if (getDebugLevel() & CONNECTION_INFORMATION_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"threadFunctionConnection initializing now.");
        }

        SocketsSharedPtr listeningSockets( new Cti::ServerSockets );

        while (true) {
            // We may have gotten to this point because of a socket being closed on
            // shutdown. Check to see if we're supposed to be exiting, otherwise
            // continue forward and attempt to forge a new connection.
            pSelf.serviceCancellation( );
            DWORD waitResult = WaitForSingleObject(_shutdownEvent, 2000);
            if (waitResult == WAIT_OBJECT_0)
            {
                // shutdown event
                break;
            }
            // just in case
            pSelf.serviceCancellation( );

            if( ! createBoundListener( listeningPort, *listeningSockets ))
            {
                if (getDebugLevel() & CONNECTION_HEALTH_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, logNow() <<"Failed to open listener socket for port: " << listeningPort);
                }
            }
            else
            {
                if (getDebugLevel() & CONNECTION_INFORMATION_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, logNow() <<"Listening for connection on port "<< listeningPort);
                }

                // Keep track of the socket.
                {
                    CtiLockGuard<CtiMutex> guard(_socketMutex);

                    if( _socketShutdown )
                    {
                        // go back to the beginning to service cancellation or check for a shutdown event
                        continue;
                    }

                    _socketConnections.erase(listeningPort); // Remove the last guy if we had a bad connection.
                    _socketConnections.insert(make_pair(listeningPort, listeningSockets));
                }

                while (true) {

                    Cti::SocketAddress addr( Cti::SocketAddress::STORAGE_SIZE );

                    // new socket
                    SOCKET tmpConnection = listeningSockets->accept(addr, Chrono::infinite, &_shutdownEvent);

                    unsigned long disabled = 0;
                    ::ioctlsocket(tmpConnection, FIONBIO, &disabled);  //  reset socket back to blocking mode

                    // when this thread is to be shutdown, requestCancellation()
                    // will be called, then the listener socket will be shutdown
                    // which will cause accept() to return.
                    pSelf.serviceCancellation( );

                    if( tmpConnection == INVALID_SOCKET )
                    {
                        CTILOG_ERROR(dout, logNow() <<"Accept call failed (Error: "<< listeningSockets->getLastError() <<")");
                        // go back to outer loop (will create new listener)
                        break;
                    }
                    else
                    {
                        // Before anything else, clear any of our old
                        // layers that have failed (because the client
                        // reconnecting now may be one of the failed
                        // ones that is still in our list).
                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, logNow() <<"Connection accepted from "<< addr);
                        }

                        try
                        {
                            CtiFDRClientServerConnectionSPtr newConnection = createNewConnection(tmpConnection);
                            CtiLockGuard<CtiMutex> guard(_connectionListMutex);
                            _connectionList.push_back(newConnection);
                            newConnection->run();
                        }
                        catch (CtiFDRClientServerConnection::StartupException& e)
                        {
                            CTILOG_EXCEPTION_ERROR(dout, e, logNow() <<"Unable to create CtiFDRClientServerConnection object");
                        }
                    }
                } // accept loop

                listeningSockets->shutdownAndClose();

            } // else listener != null
        } // thread loop
    } catch ( RWCancellation &cancellationMsg ) {
        // fall through
    } catch ( ... ) {
        // try and catch the thread death
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "CtiFDRSocketServer::threadFunctionConnection is dead!");
    }

    CTILOG_INFO(dout, "threadFunctionConnection shutdown");
}

/**
 * Creates, bind and listening sockets on sockets for each family (IPv4 and IPv6)
 * @param listeningPort port number common to all sockets
 * @param listeningSockets manages sockets return by getAddrInfo
 * @return true if listenning sockets where creates sucessfully, false otherwise
 */
bool CtiFDRSocketServer::createBoundListener(unsigned short listeningPort, Cti::ServerSockets &listeningSockets)
{
    Cti::AddrInfo ai = Cti::makeTcpServerSocketAddress(listeningPort);
    if( !ai )
    {
        CTILOG_ERROR(dout, "Failed to retrieve address info (Error: "<< ai.getError() <<")");
        return false;
    }

    try
    {
        listeningSockets.createSockets ( ai.get() );
        listeningSockets.bind          ( ai.get() );
        listeningSockets.listen        ( SOMAXCONN );
    }
    catch( Cti::SocketException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, logNow() <<"Failed to create listener socket");
        return false;
    }

    return true;
}

bool CtiFDRSocketServer::sendAllPoints(int portNumber)
{
    bool retVal = true;
    PortToPointsMap::iterator itr =  _portToPointsMap.find(portNumber);
    if (itr == _portToPointsMap.end())
    {
        //No points defined
        return retVal;
    }
    else
    {
        for (set<int>::iterator itr2 =  itr->second.begin(); itr2 != itr->second.end(); ++itr2)
        {
            //Each itr is a pointId
            //Find by pointId
            CtiFDRPointSPtr point = getSendToList().getPointList()->findFDRPointID((*itr2));
            retVal = retVal && sendPoint(point);
        }

    }

    return retVal;
}

void CtiFDRSocketServer::insertPortToPointsMap(int portId, int pointId)
{
    PortToPointsMap::iterator itr =  _portToPointsMap.find(portId);
    if (itr == _portToPointsMap.end())
    {
        std::set<int> newSet;
        newSet.insert(pointId);
        _portToPointsMap.insert(std::make_pair<int,std::set<int> >(portId,newSet));
    }
    else
    {
        itr->second.insert(pointId);
    }
}

void CtiFDRSocketServer::removePortToPointsMap(int portId, int pointId)
{
    PortToPointsMap::iterator itr =  _portToPointsMap.find(portId);
    if (itr == _portToPointsMap.end())
    {
        //Port isn't even in here.
        return;
    }
    else
    {
        itr->second.erase(pointId);
    }
}

bool CtiFDRSocketServer::sendPoint(CtiFDRPointSPtr point)
{
    bool retVal = true;

    if (point->isControllable())
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"Control point "<< *point <<" was not sent because a database reload triggered the send");
        }
        return false;
    }
    if (point->getLastTimeStamp() < CtiTime(CtiDate(1,1,2001)))
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() << *point <<" was not sent to any interfaces because it hasn't been initialized");
        }
        return false;
    }

    CtiFDRPoint::DestinationList& destList = point->getDestinationList();
    for each( CtiFDRDestination dest in destList )
    {
        CtiFDRClientServerConnectionSPtr connection = findConnectionForDestination(dest);
        if (connection)
        {
            if (!connection->isRegistered())
            {
                continue;
            }
            char* buffer = NULL;
            unsigned int bufferSize = 0;
            bool result = buildForeignSystemMessage(dest, &buffer, bufferSize);
            if (result && bufferSize > 0)
            {
                if( ! connection->queueMessage(buffer, bufferSize, MAXPRIORITY-1))
                {
                    return false;
                }
            }
        }
    }
    return retVal;
}

bool CtiFDRSocketServer::sendAllPoints(CtiFDRClientServerConnectionSPtr connection)
{
    int portNumber = connection->getPortNumber();
    return sendAllPoints(portNumber);
}

void CtiFDRSocketServer::processCommandFromDispatch(CtiCommandMsg* commandMsg)
{
    const CtiCommandMsg::OpArgList portList = commandMsg->getOpArgList();

    //There is only one.
    const int portNumber = portList[0];

    sendAllPoints(portNumber);
}

bool CtiFDRSocketServer::sendMessageToForeignSys(CtiMessage *aMessage)
{
    CtiPointDataMsg *localMsg = (CtiPointDataMsg *)aMessage;

    if (!forwardPointData(*localMsg))
    {
        return false;
    }
    // need to update this in my list always
    updatePointByIdInList (getSendToList(), localMsg);

    // if this is a response to a registration, do nothing
    if (localMsg->getTags() & TAG_POINT_MOA_REPORT)
    {
        if (getDebugLevel () & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"Point registration response tag set, point "<< localMsg->getId() <<" will not be sent");
        }
        return false;
    }

    CtiFDRPointSPtr point;
    {
        // lock on the send list
        CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
        point = getSendToList().getPointList()->findFDRPointID(localMsg->getId());
    }

    if (!point)
    {
        if (getDebugLevel () & ERROR_FDR_DEBUGLEVEL)
        {
            CTILOG_ERROR(dout, logNow() <<"Translation for point "<< localMsg->getId() <<" cannot be found");
        }
        return false;
    }

   /* If the timestamp is less than 01-01-2000 (completely arbitrary number),
    * then don't route the point because it is uninitialized (uninitialized points
    * come across as 11-10-1990).
    */
    if (point->getLastTimeStamp() < CtiTime(CtiDate(1,1,2000)))
    {
        if (getDebugLevel () & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"Data for "<< point <<" was not sent because it hasn't been initialized");
        }
        return false;
    }

    bool retVal = true;
    try
    {
        retVal = sendPoint(point);
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        retVal = false;
    }

    return retVal;
}

/** Determine if point data should be forwarded.
 *  If true is returned, the new point data will be forwarded
 *  to the foreign system.
 */
bool CtiFDRSocketServer::forwardPointData(const CtiPointDataMsg& localMsg)
{
    bool result = true;

    // if requested, check the timestamp and value to see if we should forward this message
    if (getPointTimeVariation() > 0)
    {
        CtiFDRPoint point;
        findPointIdInList (localMsg.getId(), getSendToList(), point);

        // if the values are equal
        if (point.getValue() == localMsg.getValue())
        {
            // check timestamp
            if (point.getLastTimeStamp() + getPointTimeVariation() >= localMsg.getTime())
            {
                result = false;
            }
        }
    }
    return result;
}

CtiFDRClientServerConnectionSPtr CtiFDRSocketServer::findConnectionForDestination(const CtiFDRDestination destination) const
{
    // Because new connections are put on the end of the list,
    // we want to search the list backwards so that we find
    // the newest connection that matches the destination
    // (eventually the older connections will timeout, but
    // that could take a while).
    ConnectionList::const_reverse_iterator myIter;
    CtiLockGuard<CtiMutex> guard(_connectionListMutex);
    for (myIter = _connectionList.rbegin(); myIter != _connectionList.rend(); ++myIter)
    {
        if ((*myIter)->getName() == destination.getDestination())
        {
            return (*myIter);
        }
    }

    return CtiFDRClientServerConnectionSPtr();
}

unsigned short CtiFDRSocketServer::getPortNumber() const
{
    return _portNumber;
}

void CtiFDRSocketServer::setPortNumber(const unsigned short port)
{
    _portNumber = port;
}

int CtiFDRSocketServer::getPointTimeVariation() const
{
    return _pointTimeVariation;
}

void CtiFDRSocketServer::setPointTimeVariation(int timeVariation)
{
    _pointTimeVariation = timeVariation;
}

int CtiFDRSocketServer::getTimestampReasonabilityWindow() const
{
    return _timestampReasonabilityWindow;
}

void CtiFDRSocketServer::setTimestampReasonabilityWindow(const int window)
{
    _timestampReasonabilityWindow= window;
}

int  CtiFDRSocketServer::getLinkTimeout() const
{
    return _linkTimeout;
}
void CtiFDRSocketServer::setLinkTimeout(const int linkTimeout)
{
    _linkTimeout = linkTimeout;
}

void CtiFDRSocketServer::setSingleListeningPort(bool singleListeningPort)
{
    _singleListeningPort = singleListeningPort;
}
