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
#include "fdrsocketserver.h"
#include "ctitime.h"
#include "ctidate.h"

// Constructors, Destructor, and Operators
CtiFDRSocketServer::CtiFDRSocketServer(string &name) :
    CtiFDRInterface(name),
    _portNumber(0),
    _pointTimeVariation(0),
    _timestampReasonabilityWindow(0),
    _linkTimeout(0),
    _shutdownEvent(0),
    _singleListeningPort(true)
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
    for (ConnectionList::const_iterator myIter = _connectionList.begin();
         myIter != _connectionList.end();
         ++myIter) {
        delete *myIter;

    }
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
            delete *tempIter;
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

    if ( !readConfig( ) )
    {
        return FALSE;
    }

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
        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Configured to run on a single port: " << getPortNumber() << endl;
        }
        _threadSingleConnection = rwMakeThreadFunction(*this, &CtiFDRSocketServer::threadFunctionConnection, getPortNumber(), 0);
        _threadSingleConnection.start();
    } 
    else 
    {
        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Configured to run on multiple ports. Handled in extending code" << endl;
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
    {
        CtiLockGuard<CtiMutex> guard(_socketMutex);
        for (PortSocketMap_itr iter = _socketConnections.begin();
             iter != _socketConnections.end();
             iter++)
        {
            closesocket(*iter->second);
        }
    }

    SetEvent(_shutdownEvent);

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
         ++myIter) {
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Error in loadList()" << endl;
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << " No (" << aDirection << ") " << "points defined for use by interface" << endl;
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << " Error loading (" << aDirection << ") points, empty data set returned"
                << endl;
            successful = false;
        }
    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "RWExternalErr caught in CtiFDRSocketServer::loadList(): " << e.why() << endl;
        RWTHROW(e); // is this the right thing to do???
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Unknown exception caught in CtiFDRSocketServer::loadList()" << endl;
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
        if (getDebugLevel () & CONNECTION_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() <<"threadFunctionSendHeartbeat initializing" << endl;
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
                    if (getDebugLevel () & CONNECTION_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Queueing heartbeat message to " << **myIter << endl;
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Fatal Error: threadFunctionSendHeartbeat is dead!" << endl;
        }
    }
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "threadFunctionSendHeartbeat shutdown" << endl;
    }
}

void CtiFDRSocketServer::threadFunctionConnection( unsigned short listeningPort, int startupDelaySeconds )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    SOCKET listenerSocket;

    try {

        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL) {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "threadFunctionConnection initializing in " << startupDelaySeconds << " seconds." << endl;
        }

        //Delay for initial load. This gives Dispatch a chance to give us data before being bombarded by VALMET
        Sleep(startupDelaySeconds*1000);

        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL) {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "threadFunctionConnection initializing now." << endl;
        }

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

            listenerSocket = createBoundListener(listeningPort);
            if (listenerSocket == NULL) {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                logNow() << "Failed to open listener socket for port: " << listeningPort << endl;
            } else {
                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << "Listening for connection on port " << listeningPort << endl;
                }

                // Keep track of the socket.
                {
                    CtiLockGuard<CtiMutex> guard(_socketMutex);
                    _socketConnections.erase(listeningPort); // Remove the last guy if we had a bad connection.
                    _socketConnections.insert(std::make_pair(listeningPort, &listenerSocket));
                }

                while (true) {
                    // new socket
                    SOCKADDR_IN returnAddr;
                    int returnLength = sizeof (returnAddr);
                    SOCKET tmpConnection = NULL;
                    tmpConnection = accept(listenerSocket,
                                           (struct sockaddr *) &returnAddr,
                                           &returnLength);
                    // when this thread is to be shutdown, requestCancellation()
                    // will be called, then the listener socket will be shutdown
                    // which will cause accept() to return.
                    pSelf.serviceCancellation( );

                    if (tmpConnection == INVALID_SOCKET) {
                        shutdown(tmpConnection, SD_BOTH);
                        closesocket(tmpConnection);
                        {
                            int errorCode = WSAGetLastError();
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            logNow() << "Accept call failed "
                            << " (Error: " << errorCode << ")" << endl;
                        }
                        // go back to outer loop (will create new listener)
                        break;
                    } else {
                        // Before anything else, clear any of our old
                        // layers that have failed (because the client
                        // reconnecting now may be one of the failed
                        // ones that is still in our list).
                        clearFailedLayers();
                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            logNow() << "Connection accepted from "
                               << inet_ntoa(returnAddr.sin_addr)
                               << endl;
                        }

                        CtiFDRClientServerConnection* newConnection;
                        try
                        {
                            // the following may throw an exception
                            newConnection = createNewConnection(tmpConnection);
                            CtiLockGuard<CtiMutex> guard(_connectionListMutex);
                            _connectionList.push_back(newConnection);
                            newConnection->run();
                        }
                        catch (CtiFDRClientServerConnection::StartupException& e)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            logNow() << "Unable to create CtiFDRClientServerConnection object: "
                               << e.what() << endl;
                        }
                    }
                } // accept loop

                closesocket(listenerSocket);

            } // else listener != null
        } // thread loop
    } catch ( RWCancellation &cancellationMsg ) {
        // fall through
    } catch ( ... ) {
        // try and catch the thread death
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Fatal Error: CtiFDRSocketServer::threadFunctionConnection is dead!" << endl;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "threadFunctionConnection shutdown" << endl;
    }
}

SOCKET CtiFDRSocketServer::createBoundListener(unsigned short listeningPort) {

    SOCKET listener = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (listener == INVALID_SOCKET) {
        {
            int errorCode = WSAGetLastError();
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Failed to create listener socket"
                <<" (Error: " << errorCode << ")" << endl;
        }
        return NULL;
    }

    BOOL ka = TRUE;
//    //This seams like a really bad idea!!!
//    int sockoptresult = setsockopt(listener,
//                                   SOL_SOCKET, SO_REUSEADDR,
//                                   (char*)&ka, sizeof(BOOL));
//    if (sockoptresult == SOCKET_ERROR) {
//        // setsockopt failed
//        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL) {
//            int errorCode = WSAGetLastError();
//            CtiLockGuard<CtiLogger> doubt_guard(dout);
//            logNow() << "Failed to set reuse option for listener socket "
//                <<" (Error: " << errorCode << ")" << endl;
//
//        }
//
//        shutdown(listener, SD_BOTH);
//        closesocket(listener);
//        return NULL;
//    }
    // Fill in the address structure
    sockaddr_in socketAddr;
    socketAddr.sin_family = AF_INET;
    socketAddr.sin_addr.s_addr = INADDR_ANY; // allow connections from any interface
    socketAddr.sin_port = htons(listeningPort);

    int bindresult = bind(listener, (SOCKADDR*)&socketAddr, sizeof(socketAddr));
    if (bindresult == SOCKET_ERROR) {
        {
            int errorCode = WSAGetLastError();
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Failed to bind listener socket "
                <<" (Error: " << errorCode << ")" << endl;
        }

        shutdown(listener, SD_BOTH);
        closesocket(listener);
        return NULL;
    }

    int listenresult = listen(listener, SOMAXCONN);
    if (listenresult == SOCKET_ERROR) {
        {
            int errorCode = WSAGetLastError();
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Failed to listen on listener socket "
                << " (Error: " << errorCode << ")" << endl;

        }
        shutdown(listener, SD_BOTH);
        closesocket(listener);
    }

    return listener;

}

bool CtiFDRSocketServer::sendAllPoints(CtiFDRClientServerConnection* connection)
{
    bool retVal = true;
    CtiFDRPointSPtr point;

    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
    CtiFDRManager::spiterator  myIterator = getSendToList().getPointList()->getMap().begin();
    for ( ; myIterator != getSendToList().getPointList()->getMap().end(); ++myIterator)
    {
        point = (*myIterator).second;
        if (point->isControllable())
        {
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                logNow() << "Control point " << *point << " was not sent because a database reload triggered the send" << endl;
            }
            continue;
        }
        if (point->getLastTimeStamp() < CtiTime(CtiDate(1,1,2001)))
        {
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                logNow() << *point
                    << " was not sent to any interfaces because it hasn't been initialized"
                    << endl;
            }
            continue;
        }

        CtiLockGuard<CtiMutex> guard(_connectionListMutex);
        CtiFDRPoint::DestinationList& destList = point->getDestinationList();
        for each( CtiFDRDestination dest in destList )
        {
            CtiFDRClientServerConnection::Destination destination = dest.getDestination();

            // For ValmetMulti interface, we may have multiple connections to the same
            // IP differentiated only by port number, so we need to check if this port
            // is correct before sending out all the points.
            int portNum = atoi(dest.getTranslationValue("Port").c_str());
            //bool portValid = !portNum || portNum == connection->getPortNumber();
            bool portValid = (portNum == connection->getPortNumber());

            if (portValid)
            {
                if (!connection->isRegistered()) {
                    continue;
                }
                char* buffer = NULL;
                unsigned int bufferSize = 0;
                bool result = buildForeignSystemMessage(dest, &buffer, bufferSize);
                if (result && bufferSize > 0)
                {
                    bool tmpRet = connection->queueMessage(buffer, bufferSize, MAXPRIORITY-1);
                    retVal = retVal && tmpRet;
                }
            }
        }
    }
    return retVal;
}

bool CtiFDRSocketServer::sendMessageToForeignSys(CtiMessage *aMessage)
{
    CtiPointDataMsg     *localMsg = (CtiPointDataMsg *)aMessage;
    CtiFDRPoint point;

    // lock on the send list
    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());

    if (!forwardPointData(*localMsg))
    {
        return false;
    }
    // need to update this in my list always
    updatePointByIdInList (getSendToList(), localMsg);

    // if this is a response to a registration, do nothing
    if (localMsg->getTags() & TAG_POINT_MOA_REPORT)
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Point registration response tag set, point "
                << localMsg->getId() << " will not be sent" << endl;
        }
        return false;
    }

    // see if the point exists;
    if (!findPointIdInList(localMsg->getId(), getSendToList(), point))
    {
        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Translation for point " << localMsg->getId()
                << " cannot be found" << endl;;
        }
        return false;
    }

   /* If the timestamp is less than 01-01-2000 (completely arbitrary number),
    * then don't route the point because it is uninitialized (uninitialized points
    * come across as 11-10-1990).
    */
    if (point.getLastTimeStamp() < CtiTime(CtiDate(1,1,2000)))
    {
        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Data for " << point
                << " was not sent because it hasn't been initialized " << endl;
        }
        return false;
    }

    bool retVal = true;
    try
    {
        CtiLockGuard<CtiMutex> guard(_connectionListMutex);
        CtiFDRPoint::DestinationList& destList = point.getDestinationList();
        for each (const CtiFDRDestination dest in destList)
        {
            CtiFDRClientServerConnection* connection = findConnectionForDestination(dest);
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
                    bool tmpRet = connection->queueMessage(buffer, bufferSize, MAXPRIORITY-1);
                    retVal = retVal && tmpRet;
                }
            }
        }

    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Unknown exception caught in CtiFDRSocketServer::sendMessageToForeignSys()"
            << endl;
        retVal = false;
    }

    if (!retVal)
    {
        clearFailedLayers();
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

CtiFDRClientServerConnection* CtiFDRSocketServer::findConnectionForDestination(const CtiFDRDestination destination) const
{
    // Because new connections are put on the end of the list,
    // we want to search the list backwards so that we find
    // the newest connection that matches the destination
    // (eventually the older connections will timeout, but
    // that could take a while).
    ConnectionList::const_reverse_iterator myIter;
    for (myIter = _connectionList.rbegin(); myIter != _connectionList.rend(); ++myIter)
    {
        if ((*myIter)->getName() == destination.getDestination())
        {
            return (*myIter);
        }
    }

    return NULL;
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
