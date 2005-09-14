#include "yukon.h"

#include <ctime>

#include "logger.h"
#include "guard.h"

#include "fdrinterface.h"
#include "fdrscadaserver.h"



/** Cti FDR Client Server Connection constructor
 * This class should throw a StartupException if it cannot create itself (CtiFDRSocketServer
 * will catch this exception).
 */
CtiFDRClientServerConnection::CtiFDRClientServerConnection(const RWCString& connectionName, 
                                     SOCKET theSocket,
                                     CtiFDRScadaServer *aParent)
{
    _connectionName = connectionName;
    _parentInterface = aParent;
    _socket = theSocket;
    _isRegistered = false;
    _connectionFailed = false;
    
    _sendThread = rwMakeThreadFunction(*this, 
                                       &CtiFDRClientServerConnection::threadFunctionSendDataTo);
    _receiveThread = rwMakeThreadFunction(*this, 
                                          &CtiFDRClientServerConnection::threadFunctionGetDataFrom);
                                          
    _shutdownEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
                                          
    if (CreateQueue(&_outboundQueue, QUE_PRIORITY, _shutdownEvent))
    {
        throw StartupException();
    }
    
    _linkName = _parentInterface->getInterfaceName() + "-" + _connectionName;
    _linkId = _parentInterface->getClientLinkStatusID(_linkName);
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "connection created" << endl;
    }
    _parentInterface->logEvent(_linkName + ": connection created", "", true);
}


CtiFDRClientServerConnection::~CtiFDRClientServerConnection( )
{   
    // stops all threads in this object
    stop();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "connection destroyed" << endl;
    }
    
    _parentInterface->logEvent(_linkName + ": connection destroyed", "", true);

}

SOCKET CtiFDRClientServerConnection::getRawSocket()
{
    return _socket;
}

bool CtiFDRClientServerConnection::isRegistered ()
{
    return _isRegistered;
}

void CtiFDRClientServerConnection::setRegistered (bool registered)
{
    _isRegistered = registered;
}

RWCString  CtiFDRClientServerConnection::getName(void) const
{
    return _connectionName;
}

void CtiFDRClientServerConnection::setName(RWCString aName)
{
  _connectionName = aName;
}

void CtiFDRClientServerConnection::run () 
{

    _sendThread.start();
    _receiveThread.start();
    sendLinkState(true);
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Connection running" << endl;
    }

}

void CtiFDRClientServerConnection::stop () 
{
    sendLinkState(false);
    failConnection();
    
    // this will cause the reader thread to unblock
    shutdown(_socket, SD_BOTH);
    closesocket(_socket);
    
    // this will cause the writer thread to unblock
    SetEvent(_shutdownEvent);
    //CloseQueue(_outboundQueue);
    
    try 
    {
        _receiveThread.requestCancellation();
        _receiveThread.join();
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "exception while shutting down receive thread" << endl;
    }
    
    try 
    {
        _sendThread.requestCancellation();
        _sendThread.join();
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "exception while shutting down send thread" << endl;
    }
    
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Connection stopped" << endl;
    }
    
}

bool CtiFDRClientServerConnection::isFailed() const
{
    return _connectionFailed;
}

void CtiFDRClientServerConnection::failConnection()
{
    _connectionFailed = true;
}

bool CtiFDRClientServerConnection::queueMessage(CHAR *aBuffer, 
                                              unsigned int bufferSize, 
                                              int aPriority)
{
    bool success = true;
    
    if (_connectionFailed)
    {
        return false;
    }

    // Ship it to threadFunctionSendDataTo
    INT writeResult = WriteQueue (_outboundQueue, 0, bufferSize, aBuffer, aPriority);
    if (writeResult != NO_ERROR)
    {
        // write to queue failed!
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Error queueing data (" << writeResult << "), failing connection" << endl;
        success = false;
        failConnection();
    }

    return success;
}



ULONG CtiFDRClientServerConnection::getDebugLevel() 
{
    return _parentInterface->getDebugLevel();
}

ostream CtiFDRClientServerConnection::logNow() 
{
    return _parentInterface->logNow() << "[" << getName() << "] ";
}


void CtiFDRClientServerConnection::threadFunctionSendDataTo( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    int retVal = NORMAL;

    try
    {

        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() <<"threadFunctionSendDataTo initializing" << endl;
        }
        
        clock_t intervalStartTime = clock();
        int outCount = 0;
        // Now sit and wait for stuff to come in
        for (;;)
        {


            UCHAR priority;
            REQUESTDATA queueResult;
            CHAR *buffer = NULL;
            ULONG bytesRead = 0;
            int queueReturn = ReadQueue (_outboundQueue,
                           &queueResult,
                           &bytesRead,
                           (PVOID *) &buffer,
                           0,
                           DCWW_WAIT,
                           &priority);
            // see if we got here because someone requested cancellation
            pSelf.serviceCancellation( );
            
            if (queueReturn == ERROR_QUE_UNABLE_TO_ACCESS)
            {
                // this is the shutdown event
                break;
            }
            if (bytesRead == 0 && queueReturn != ERROR_QUE_EMPTY) 
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                logNow() << "Error reading from out queue (" 
                    << queueReturn << ")" << endl;
                break;
            }
            if (queueReturn == NO_ERROR)
            {
                ULONG bytesSent;
                retVal = writeSocket(buffer, bytesRead, bytesSent);

                outCount++;

                // this where we re-initialize if needed
                if (retVal == SOCKET_ERROR)
                {
                    // exit the send loop
                    // this effectively means this socket will no 
                    // longer be used
                    break;
                }
                
                if (outCount >= _parentInterface->getOutboundSendRate())
                {
                    clock_t currentTime = clock();
                    //don't call sleep routine if nothing is set
                    int intervalClocks = 
                        _parentInterface->getOutboundSendInterval() * CLOCKS_PER_SEC;
                    if (currentTime < intervalStartTime + intervalClocks)
                    {
                        int clocksLeftInInterval = intervalClocks 
                            - (currentTime - intervalStartTime);
                        int millisToSleep = clocksLeftInInterval * (1000.0 / CLOCKS_PER_SEC);

                        unsigned long elementCount = 0;
                        QueryQueue(_outboundQueue, &elementCount);
                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            logNow() << "Maximum throughput of " 
                                << _parentInterface->getOutboundSendRate() 
                                << " entries reached, waiting " 
                                << millisToSleep 
                                << " millisecond(s) with " << elementCount
                                << " items left in queue" << endl;
                        }
                        DWORD waitResult = WaitForSingleObject(_shutdownEvent, millisToSleep);
                        if (waitResult == WAIT_OBJECT_0)
                        {
                            // shutdown event
                            break;
                        }
                    }
                    outCount =0;
                    intervalStartTime = currentTime;
                }
            }

            // free the memory of the stuff on the queue
            delete[] buffer;
            buffer = NULL;
        }
    }
 

    catch ( RWCancellation &cancellationMsg )
    {
        // just let it fall through
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << " Fatal Error: threadFunctionSendDataTo is dead! " << endl;
    }

    failConnection();
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "threadFunctionSendDataTo shutdown" << endl;
    }
}

INT CtiFDRClientServerConnection::writeSocket(CHAR *aBuffer, ULONG length, ULONG &aBytesWritten)
{
    INT retVal = NORMAL;

    aBytesWritten = 0;

    // send the data
    ULONG bytesSent = send(_socket, aBuffer, length, 0);

    if (bytesSent == SOCKET_ERROR)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        logNow() << "Socket Error on write, WSAGetLastError() == " 
            << WSAGetLastError() << endl;

        retVal = SOCKET_ERROR;
    }
    else if (bytesSent != length)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        logNow() << "Socket Error on write, wrote " << bytesSent 
            << " bytes, intended to write " << length 
            << ", WSAGetLastError() == " << WSAGetLastError() << endl;
        
        retVal = SOCKET_ERROR;
    }
    else
    {
        // set return parameter
        aBytesWritten = bytesSent;
    }

    if (getDebugLevel () & MAJOR_DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        logNow() << "Wrote " << aBytesWritten << " bytes" << endl;
    }    

    return retVal;
}



void CtiFDRClientServerConnection::threadFunctionGetDataFrom( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    const unsigned int maxBufferSize = 8172;
    const unsigned int magicInitialMessageSize = 4;
    CHAR            data[maxBufferSize];
    INT retVal=0;        
    ULONG   bytesRead=0,totalMsgSize=0;
    int connectionBadCount=0;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() <<"threadFunctionGetDataFrom initializing" << endl;
        }

        for ( ; ; )
        {

            memset (&data, '\0', maxBufferSize);
            // attempt to find out what type of message we're dealing with
            retVal = readSocket((CHAR*)&data, magicInitialMessageSize, bytesRead);
            pSelf.serviceCancellation( );

            // this where we re-initialize if needed
            if (retVal == SOCKET_ERROR)
            {
                // most likely our shutdown event, but it could be a real error
                break;
            }
            // figure out how many more bytes we now need
            unsigned long headerBytes = 
                _parentInterface->getHeaderBytes(data, magicInitialMessageSize);
            totalMsgSize = _parentInterface->getMessageSize(headerBytes);
            if (totalMsgSize == 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << "getMessageSize() returned 0, but " 
                        << magicInitialMessageSize << " bytes were already read" << endl;
                }
                break;
            }
            
            // make sure we have a number 
            // (greater than four, we already read magicInitialMessageSize)
            if (totalMsgSize > magicInitialMessageSize)
            {
                retVal = readSocket((CHAR*)&data + magicInitialMessageSize, 
                                    totalMsgSize - magicInitialMessageSize, 
                                    bytesRead);
                pSelf.serviceCancellation( );

                // this where we re-initialize if needed
                if (retVal == SOCKET_ERROR)
                {
                    // most likely our shutdown event, but it could be a real error
                    {
                        // we'll log it this time because it is less likely to occur
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Read failed while getting remaining message" << endl;
                    }
                    break;
                }
            }
           _parentInterface->processMessageFromForeignSystem (*this, data, totalMsgSize);
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        // just let it fall through
    }
    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Fatal Error: threadFunctionGetDataFrom for is dead! " << endl;
    }
    
    failConnection();
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "threadFunctionGetDataFrom shutdown" << endl;
    }
}


INT CtiFDRClientServerConnection::readSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead)
{
    INT retVal = NORMAL;

    // initialize
    aBytesRead = 0;

    ULONG totalByteCnt = 0;

    // we have data, try and get as much as we need
    do
    {
        LONG bytesReceived = 0;
        // read out of buffer until we have everything
        if ((bytesReceived = recv (_socket,
                                   (aBuffer + totalByteCnt),
                                   length-totalByteCnt,
                                   0)) <= 0)
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            logNow() << "Socket Error on read, WSAGetLastError() == " 
                << WSAGetLastError() << endl;

            // problem with the receive
            retVal = SOCKET_ERROR;
            break;
        }

        // add to our total count
        totalByteCnt += bytesReceived;
        aBytesRead = totalByteCnt;

    } while (totalByteCnt < length);
    
    if (getDebugLevel () & MAJOR_DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        logNow() << "Read " << aBytesRead << " bytes" << endl;
    }    

    return retVal;
}

void CtiFDRClientServerConnection::sendLinkState(bool linkUp)
{
    CtiPointDataMsg* pData;
    pData = new CtiPointDataMsg(_linkId, 
                                linkUp ? FDR_CONNECTED : FDR_NOT_CONNECTED, 
                                NormalQuality, 
                                StatusPointType);
    _parentInterface->sendMessageToDispatch (pData);
}

