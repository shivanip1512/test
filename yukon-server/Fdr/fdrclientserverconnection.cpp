#include "precompiled.h"

#include <ctime>

#include "logger.h"
#include "guard.h"

#include "fdrinterface.h"
#include "fdrscadaserver.h"
#include "prot_dnp.h"
#include "win_helper.h"

using namespace std;

int CtiFDRClientServerConnection::_nextConnectionNumber = 1;


/** Cti FDR Client Server Connection constructor
 * This class should throw a StartupException if it cannot create itself (CtiFDRSocketServer
 * will catch this exception).
 */
CtiFDRClientServerConnection::CtiFDRClientServerConnection(const string& connectionName,
                                     SOCKET theSocket,
                                     CtiFDRSocketServer *aParent)
{
    _connectionName = connectionName;
    _connectionNumber = _nextConnectionNumber++;
    _parentInterface = aParent;
    _socket = theSocket;
    _isRegistered = false;
    _connectionFailed = false;

    _sendThread = rwMakeThreadFunction(*this,
                                       &CtiFDRClientServerConnection::threadFunctionSendDataTo);
    _receiveThread = rwMakeThreadFunction(*this,
                                          &CtiFDRClientServerConnection::threadFunctionGetDataFrom);
    _healthThread = rwMakeThreadFunction(*this,
                                          &CtiFDRClientServerConnection::threadFunctionHealth);

    _shutdownEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
    _stillAliveEvent = CreateEvent(NULL, FALSE, FALSE, NULL);

    if (CreateQueue(&_outboundQueue, _shutdownEvent))
    {
        throw StartupException();
    }

    _linkName = _parentInterface->getInterfaceName() + "-" + _connectionName;
    _linkId = _parentInterface->getClientLinkStatusID(_linkName);

    _parentInterface->logEvent(_linkName + ": connection created", "", true);
}

CtiFDRClientServerConnection::~CtiFDRClientServerConnection( )
{
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

string  CtiFDRClientServerConnection::getName() const
{
    return _connectionName;
}

void CtiFDRClientServerConnection::setName(string aName)
{
  _connectionName = aName;
}

int CtiFDRClientServerConnection::getConnectionNumber() const
{
    return _connectionNumber;
}

void CtiFDRClientServerConnection::run ()
{

    _sendThread.start();
    _receiveThread.start();
    _healthThread.start();
    sendLinkState(true);
}

void CtiFDRClientServerConnection::stop ()
{
    sendLinkState(false);
    failConnection();

    // this will cause the health thread to unblock
    SetEvent(_stillAliveEvent);

    // this will cause the reader thread to unblock
    shutdown(_socket, SD_BOTH);
    closesocket(_socket);

    // this will cause the writer thread to unblock
    SetEvent(_shutdownEvent);
    //CloseQueue(_outboundQueue);

    try
    {
        _healthThread.requestCancellation();
        _healthThread.join();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, logNow() <<"exception while shutting down health thread");
    }

    try
    {
        _receiveThread.requestCancellation();
        _receiveThread.join();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, logNow() <<"exception while shutting down receive thread");
    }

    try
    {
        _sendThread.requestCancellation();
        _sendThread.join();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, logNow() <<"exception while shutting down send thread");
    }

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<"Connection stopped");
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

    if (isFailed())
    {
        return false;
    }

    // Ship it to threadFunctionSendDataTo
    INT writeResult = WriteQueue (_outboundQueue, 0, bufferSize, aBuffer, aPriority);
    if (writeResult != NO_ERROR)
    {
        // write to queue failed!
        CTILOG_ERROR(dout, logNow() <<"Error queueing data ("<< writeResult <<"), failing connection");

        success = false;
        failConnection();
    }

    return success;
}

int CtiFDRClientServerConnection::getPortNumber()
{
    Cti::SocketAddress addr( Cti::SocketAddress::STORAGE_SIZE );

    if( getsockname(getRawSocket(), &addr._addr.sa, &addr._addrlen) == SOCKET_ERROR )
    {
        const DWORD error = WSAGetLastError();
        CTILOG_ERROR(dout, logNow() <<"getsockname() failed with error code: "<< error <<" / "<< Cti::getSystemErrorMessage(error));

        return -1; // return invalid port
    }

    return ntohs(addr._addr.sa_in.sin_port);
}

ULONG CtiFDRClientServerConnection::getDebugLevel()
{
    return _parentInterface->getDebugLevel();
}

std::string CtiFDRClientServerConnection::logNow()
{
    return Cti::StreamBuffer() << _parentInterface->logNow() << getName() <<" #"<< getConnectionNumber() <<": ";
}


void CtiFDRClientServerConnection::threadFunctionSendDataTo( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    int retVal = ClientErrors::None;

    try
    {
        if (getDebugLevel () & CONNECTION_INFORMATION_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"threadFunctionSentDataTo initializing");
        }

        clock_t intervalStartTime = clock();
        int outCount = 0;
        // Now sit and wait for stuff to come in
        for (;;)
        {
            if(isFailed())
            {
                // Probably supposed to be shutting down.
                CTILOG_WARN(dout, logNow() <<"threadFunctionSentDataTo shutting down");
                break;
            }

            UCHAR priority;
            CHAR *buffer = NULL;
            ULONG bytesRead = 0;

            const int queueReturn = ReadFrontElement(_outboundQueue, &bytesRead, (PVOID *) &buffer, DCWW_WAIT, &priority);

            // see if we got here because someone requested cancellation
            pSelf.serviceCancellation( );

            if (queueReturn == ERROR_QUE_UNABLE_TO_ACCESS)
            {
                // this is the shutdown event
                break;
            }
            if (bytesRead == 0 && queueReturn != ERROR_QUE_EMPTY)
            {
                CTILOG_ERROR(dout, logNow() <<"Error reading from out queue ("<< queueReturn <<")");
                break;
            }
            if (queueReturn == ClientErrors::None)
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
                    int sendInterval = _parentInterface->getOutboundSendInterval();
                    int intervalClocks = sendInterval * CLOCKS_PER_SEC;
                    if (currentTime < intervalStartTime + intervalClocks)
                    {
                        int clocksLeftInInterval = intervalClocks - (currentTime - intervalStartTime);
                        int millisToSleep = clocksLeftInInterval * (1000.0 / CLOCKS_PER_SEC);

                        unsigned long elementCount = 0;
                        QueryQueue(_outboundQueue, &elementCount);

                        //Do not let wait time be negative.
                        if (millisToSleep < 0)
                        {
                            Cti::FormattedList loglist;
                            loglist.add("Sleep Time")    << millisToSleep;
                            loglist.add("Send Interval") << sendInterval;
                            loglist.add("CPS")           << CLOCKS_PER_SEC;
                            loglist.add("Start Time")    << intervalStartTime;
                            loglist.add("Current Time")  << currentTime;

                            CTILOG_WARN(dout, logNow() <<
                                    loglist <<
                                    endl << "Resetting Sleep Time to 1 second to prevent infinite lock on send thread.");

                            millisToSleep = 1000;//default to 1 second.  prevents an infinite sleep. (negative value)
                        }

                        if (getDebugLevel () & CONNECTION_INFORMATION_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, logNow() <<"Maximum throughput of "<< _parentInterface->getOutboundSendRate() <<" entries reached, "
                                    "waiting "<< millisToSleep <<" millisecond(s) with "<< elementCount <<" items left in queue");
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, logNow() <<"threadFunctionSendDataTo is dead");
    }

    failConnection();

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<"threadFunctionSendDataTo shutdown");
    }
}

void CtiFDRClientServerConnection::threadFunctionHealth( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    int retVal = ClientErrors::None;

    try
    {
        int linkTimeoutSecs = _parentInterface->getLinkTimeout();
        if (linkTimeoutSecs < 1)
        {
            CTILOG_INFO(dout, logNow() <<"No health checks will be performed on this connection");

            return;
        }

        if (getDebugLevel() & CONNECTION_INFORMATION_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<"threadFunctionHealth initializing");
        }

        int linkTimeoutMSecs = linkTimeoutSecs * 1000;
        for ( ; ; )
        {
            DWORD waitResult = WaitForSingleObject(_stillAliveEvent, linkTimeoutMSecs);
            pSelf.serviceCancellation();
            if (waitResult == WAIT_OBJECT_0)
            {
                if (isFailed())
                {
                    // we're probably being asked to shutdown
                    break;
                }
                // otherwise, we've received a message of some type, start loop over
                continue;
            }
            else
            {
                CTILOG_WARN(dout, logNow() <<"No data received for "<< _parentInterface->getLinkTimeout() <<" seconds, failing connection");
                break;
            }
        }

    }
    catch ( RWCancellation &cancellationMsg )
    {
        // just let it fall through
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, logNow() <<"threadFunctionHealth is dead");
    }

    failConnection();
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<"threadFunctionHealth shutdown");
    }
}

INT CtiFDRClientServerConnection::writeSocket(CHAR *aBuffer, ULONG length, ULONG &aBytesWritten)
{
    INT retVal = ClientErrors::None;

    aBytesWritten = 0;

    // send the data
    ULONG bytesSent = send(_socket, aBuffer, length, 0);

    if (bytesSent == SOCKET_ERROR)
    {
        const DWORD error = WSAGetLastError();
        CTILOG_ERROR(dout, logNow() <<"Socket write failed with error code: "<< error <<" / "<< Cti::getSystemErrorMessage(error));

        retVal = SOCKET_ERROR;
    }
    else if (bytesSent != length)
    {
        CTILOG_ERROR(dout, logNow() <<"Socket write failed - wrote "<< bytesSent <<" bytes, intended to write "<< length);

        retVal = SOCKET_ERROR;
    }
    else
    {
        // set return parameter
        aBytesWritten = bytesSent;
    }

    return retVal;
}



void CtiFDRClientServerConnection::threadFunctionGetDataFrom( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );

    const unsigned int MaxBufferSize = 8172;
    char data[MaxBufferSize];

    const unsigned int hdrLen = _parentInterface->getHeaderLength();

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<"threadFunctionGetDataFrom initializing");
    }

    try
    {
        for ( ; ; )
        {
            if(isFailed())
            {
                // Probably supposed to be shutting down.
                CTILOG_WARN(dout, logNow() <<"threadFunctionGetDataFrom shutting down");
                break;
            }

            std::fill(data, data + MaxBufferSize, 0);

            // attempt to find out what type of message we're dealing with
            unsigned long hdrBytesRead;
            const int hdrReadStatus = readSocket(data, hdrLen, hdrBytesRead);

            pSelf.serviceCancellation();

            // this where we re-initialize if needed
            if (hdrReadStatus == SOCKET_ERROR)
            {
                // most likely our shutdown event, but it could be a real error
                break;
            }

            // restart connection timeout
            SetEvent(_stillAliveEvent);

            // figure out how many more bytes we need
            const unsigned long len = _parentInterface->getMessageSize(data);
            if (len == 0)
            {
                CTILOG_WARN(dout, logNow() <<"getMessageSize() returned 0, but "<< hdrLen <<" bytes were already read");
                break;
            }

            // make sure we have a number greater than headerLength before we try to read
            if (len > hdrLen)
            {
                unsigned long msgBytesRead;
                const int msgReadStatus = readSocket(data + hdrLen,
                                                     len  - hdrLen,
                                                     msgBytesRead);
                pSelf.serviceCancellation( );

                // this where we re-initialize if needed
                if (msgReadStatus == SOCKET_ERROR)
                {
                    // most likely our shutdown event, but it could be a real error
                    // we'll log it this time because it is less likely to occur
                    CTILOG_ERROR(dout, logNow() <<"Read failed while getting remaining message");
                    break;
                }
            }

            _parentInterface->processMessageFromForeignSystem (*this, data, len);
        }
    }
    catch ( RWCancellation &cancellationMsg )
    {
        // just let it fall through
    }
    // catch any thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, logNow() <<"threadFunctionGetDataFrom is dead");
    }

    failConnection();
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<"threadFunctionGetDataFrom shutdown");
    }
}


INT CtiFDRClientServerConnection::readSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead)
{
    INT retVal = ClientErrors::None;

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
            const DWORD error = WSAGetLastError();
            CTILOG_ERROR(dout, logNow() <<"Socket receive failed with error code: "<< error <<" / "<< Cti::getSystemErrorMessage(error));

            // problem with the receive
            retVal = SOCKET_ERROR;
            break;
        }

        // add to our total count
        totalByteCnt += bytesReceived;
        aBytesRead = totalByteCnt;

    } while (totalByteCnt < length);

    return retVal;
}

void CtiFDRClientServerConnection::sendLinkState(bool linkUp)
{
    CtiPointDataMsg* pData;
    pData = new CtiPointDataMsg(_linkId,
                                linkUp ? FDR_CONNECTED : FDR_NOT_CONNECTED,
                                NormalQuality,
                                SystemPointType);
    _parentInterface->sendMessageToDispatch (pData);
}
