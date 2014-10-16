#include "precompiled.h"

#include "logger.h"
#include "std_helper.h"
#include "win_helper.h"
#include "millisecond_timer.h"
#include "streamSocketConnection.h"

#include <boost/lexical_cast.hpp>

using std::string;
using std::endl;

namespace Cti {
namespace {

std::string getErrorMessage(int error)
{
    return StreamBuffer() <<"Error "<< error <<" -> "<< getSystemErrorMessage(error);
}

std::string getErrorMessage(const SOCKET s, int error)
{
    return StreamBuffer() <<"Socket "<< s <<", Error "<< error <<" -> "<< getSystemErrorMessage(error);
}

std::string millisToStr(const unsigned long millis)
{
    return StreamBuffer() << millis << (millis == 1 ? " millisecond" : " milliseconds");
}

} // namespace anonymous

//-----------------------------------------------------------------------------
//  Constructor / Destructor
//-----------------------------------------------------------------------------
StreamSocketConnection::StreamSocketConnection() :
    _socket(INVALID_SOCKET)
{}

StreamSocketConnection::~StreamSocketConnection()
{
    try
    {
        close();
    }
    catch(...)
    {
    }
}

//-----------------------------------------------------------------------------
//  Check if the nexus state is initialized and if the socket is valid
//-----------------------------------------------------------------------------
bool StreamSocketConnection::isValid() const
{
    CtiLockGuard<CtiCriticalSection> guard(_socketMux);
    return _socket != INVALID_SOCKET;
}

//-----------------------------------------------------------------------------
//  return a copy of the current socket handle
//-----------------------------------------------------------------------------
SOCKET StreamSocketConnection::getSocket()
{
    CtiLockGuard<CtiCriticalSection> guard(_socketMux);
    return _socket;
}

//-----------------------------------------------------------------------------
//  Close the socket
//-----------------------------------------------------------------------------
void StreamSocketConnection::close()
{
    CtiLockGuard<CtiCriticalSection> guard(_socketMux);

    if( _socket != INVALID_SOCKET )
    {
        // try to shutdown the socket
        if( shutdown(_socket, SD_BOTH) == SOCKET_ERROR )
        {
            const int error = WSAGetLastError();
            if( error != WSANOTINITIALISED &&  // Not initialized
                error != WSAENOTCONN )         // Not connected
            {
                CTILOG_ERROR(dout, getErrorMessage(_socket, error));
            }
        }

        // try to close the socket
        if( closesocket(_socket) == SOCKET_ERROR )
        {
            const int error = WSAGetLastError();
            if( error != WSANOTINITIALISED ) // Not initialized
            {
                CTILOG_ERROR(dout, getErrorMessage(_socket, error));
            }
        }

        _socket = INVALID_SOCKET;
    }

    _socketsEventsManager.interruptBlockingWait();
}

//-----------------------------------------------------------------------------
//  Open and connection a client TCP/IP stream socket
//-----------------------------------------------------------------------------
bool StreamSocketConnection::open(const std::string &zServer, unsigned short nPort, ConnectionModes mode)
{
    if( isValid() )
    {
        CTILOG_WARN(dout, "Socket is being re-opened without being closed first. Closing now..");
        close(); // this will interrupt any blocked read
    }

    {
        CtiLockGuard<CtiCriticalSection> guard(_readMux);
        _readBuffer.clear();
    }

    // Find the server
    AddrInfo addrInfo = makeTcpClientSocketAddress(zServer, nPort);
    if( ! addrInfo )
    {
        CTILOG_ERROR(dout, getErrorMessage(addrInfo.getError()));
        return false;
    }

    CtiLockGuard<CtiCriticalSection> guard(_socketMux);

    // Create a TCP/IP stream socket
    _socket = ::socket(addrInfo->ai_family, addrInfo->ai_socktype, addrInfo->ai_protocol);
    if( _socket == INVALID_SOCKET )
    {
        CTILOG_ERROR(dout, getErrorMessage(WSAGetLastError()));
        return false;
    }

    // connect to the server
    if( ::connect(_socket, addrInfo->ai_addr, addrInfo->ai_addrlen) == SOCKET_ERROR )
    {
        // FIXME: should we be reporting any errors?
        // getErrorMessage(WSAGetLastError();
        close();
        return false;
    }

    // Enable keep alive
    bool keepAlive = true;
    if( ::setsockopt(_socket, SOL_SOCKET, SO_KEEPALIVE, (char*)&keepAlive, sizeof(bool)) == SOCKET_ERROR )
    {
        CTILOG_ERROR(dout, getErrorMessage(_socket, WSAGetLastError()));
        close();
        return false;
    }

    // Set to non-blocking mode...
    unsigned long nonBlocking = 1;
    if( ::ioctlsocket(_socket, FIONBIO, &nonBlocking) == SOCKET_ERROR )
    {
        CTILOG_ERROR(dout, getErrorMessage(_socket, WSAGetLastError()));
        close();
        return false;
    }

    _connectionMode = mode;

    return true;
}

//-----------------------------------------------------------------------------
//  Socket write with milliseconds timeout
//-----------------------------------------------------------------------------
size_t StreamSocketConnection::write(const void *buf, int len, const Chrono& timeout)
{
    CtiLockGuard<CtiCriticalSection> guard(_writeMux);

    if( ! isValid() )
    {
        const char* desc = "Write attempted to an invalid socket handle";
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }

    try
    {
        SOCKET sock = getSocket();

        int   bytesWritten = 0;
        int   bytesToSend  = len;
        const char *bptr   = reinterpret_cast<const char*>(buf);

        unsigned long remainingMillis = timeout ? timeout.milliseconds() : ULONG_MAX;

        Timing::MillisecondTimer timer;

        while( bytesToSend )
        {
            const int bytesSent = ::send(sock, bptr, bytesToSend, 0);
            if( bytesSent == SOCKET_ERROR )
            {
                const int error = WSAGetLastError();
                if( error != WSAEWOULDBLOCK )
                {
                    const std::string desc = getErrorMessage(sock, error);
                    CTILOG_ERROR(dout, desc);
                    close();
                    throw StreamConnectionException(desc);
                }

                // if the timeout is zero, there's no reason to wait
                if( remainingMillis )
                {
                    CTILOG_WARN(dout, "Outbound socket stream connection to "<< *Name.get() <<" is full, will wait "<<
                            (timeout ? string("up to ") + millisToStr(remainingMillis) : string("forever"))<< " to retry");

                    // log every 5 seconds or less
                    const unsigned long waitMillis = std::min(remainingMillis, 5000ul);

                    // block until the socket can be written, is closed or there's a timeout
                    _socketsEventsManager.waitForEvent(sock, FD_WRITE|FD_CLOSE, Chrono::milliseconds(waitMillis), NULL);
                }
            }
            else
            {
                bytesWritten += bytesSent;
                bptr         += bytesSent;
                bytesToSend  -= bytesSent;
            }

            if( bytesToSend > 10000 )
            {
                CTILOG_WARN(dout, "Outbound socket stream connection write:"<< bytesToSend << (bytesToSend == 1 ?" byte":" bytes") <<" remaining");
            }

            // update the remaining time
            if( timeout )
            {
                const unsigned long elapsed = timer.elapsed();
                remainingMillis = elapsed < timeout.milliseconds() ? timeout.milliseconds() - elapsed : 0;
                if( ! remainingMillis )
                {
                    break; // timeout !
                }
            }
        }

        if( bytesToSend )
        {
            CTILOG_ERROR(dout, "Outbound socket stream connection to "<<
                    *Name.get() <<" could not be written. "<< bytesToSend << (bytesToSend == 1 ?" byte":" bytes") <<" unwritten.");
        }

        return bytesWritten;
    }
    catch( const SocketException& ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex);
        close();
        throw StreamConnectionException(ex.what());

    }
    catch( const StreamConnectionException & )
    {
        throw;
    }
    catch(...)
    {
        const char* desc = "Unhandled exception caught";
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, desc);
        close();
        throw StreamConnectionException(desc);
    }
}

//-----------------------------------------------------------------------------
//  Socket read with milliseconds
//-----------------------------------------------------------------------------
size_t StreamSocketConnection::read(void *buf, int len, const Chrono& timeout, const HANDLE *hAbort)
{
    CtiLockGuard<CtiCriticalSection> guard(_readMux);

    if( ! isValid() )
    {
        const char* desc = "Read attempted on an invalid socket handle";
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }

    return readFromSocket(buf, len, timeout, hAbort, MessageRead);
}

//-----------------------------------------------------------------------------
//  Socket peek
//-----------------------------------------------------------------------------
size_t StreamSocketConnection::peek(void *buf, int len)
{
    CtiLockGuard<CtiCriticalSection> guard(_readMux);

    if( ! isValid() )
    {
        const char* desc = "Peek attempted on an invalid socket handle";
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }

    return readFromSocket(buf, len, Chrono::milliseconds(0), NULL, MessagePeek);
}

//-----------------------------------------------------------------------------
//  Socket read with milliseconds
//-----------------------------------------------------------------------------
size_t StreamSocketConnection::readFromSocket(void *buf, int len, const Chrono& timeout, const HANDLE *hAbort, ReadOptions option)
{
    try
    {
        SOCKET sock = getSocket();

        unsigned long remainingMillis = timeout ? timeout.milliseconds() : ULONG_MAX;

        Timing::MillisecondTimer timer;

        // check if we have enough bytes already and that the socket is valid
        while( _readBuffer.size() < len )
        {
            const Chrono waitDuration = timeout ? Chrono::milliseconds(remainingMillis) : Chrono::infinite;

            if( _socketsEventsManager.waitForEvent(sock, FD_READ|FD_CLOSE, waitDuration, hAbort) )
            {
                const unsigned long offset = _readBuffer.size();
                const int bytesToRead      = 4096; // read a chunk up to 4-kB

                _readBuffer.insert(_readBuffer.end(), bytesToRead, 0);

                const int bytesRead = ::recv(sock, &_readBuffer[offset], bytesToRead, 0);
                if( bytesRead == SOCKET_ERROR )
                {
                    const int error = WSAGetLastError();
                    if( error != WSAEWOULDBLOCK )
                    {
                        const std::string desc = getErrorMessage(sock, error);
                        CTILOG_ERROR(dout, desc);
                        close();
                        throw StreamConnectionException(desc);
                    }

                    // blocking read detected, resize the read buffer
                    _readBuffer.resize(offset);
                }
                else if( ! bytesRead ) // graceful shutdown
                {
                    if( _readBuffer.empty() || _connectionMode == ReadExactly )
                    {
                        const char* desc = "Connection has been gracefully closed";
                        CTILOG_ERROR(dout, desc);
                        close();
                        throw StreamConnectionException(desc);
                    }

                    _readBuffer.resize(offset);

                    // do not report an error if the read buffer is not empty and we are not in read exactly mode
                    break;
                }
                else if( bytesRead != bytesToRead ) // if we receive less then expected
                {
                    _readBuffer.resize(offset + bytesRead);
                }
            }

            // update the remaining time and check for a timeout
            if( timeout )
            {
                const unsigned long elapsed = timer.elapsed();
                remainingMillis = elapsed < timeout.milliseconds() ? timeout.milliseconds() - elapsed : 0;
                if( ! remainingMillis )
                {
                    break; // timeout!
                }
            }
        } // end of while

        // check if there's enough data
        if( ! _readBuffer.empty() && (_readBuffer.size() >= len || _connectionMode == ReadAny) )
        {
            // copy read buffer to the buffer provided
            const int bytesRead = std::min<int>(_readBuffer.size(), len);

            copy(_readBuffer.begin(), _readBuffer.begin() + bytesRead, reinterpret_cast<char*>(buf));

            if( option != MessagePeek )
            {
                // dont erase any data from the read buffer if we are peeking
                _readBuffer.erase(_readBuffer.begin(), _readBuffer.begin() + bytesRead);
            }

            return bytesRead;
        }

        return 0; // NOTE: a timeout will not throw
    }
    catch( const SocketAbortException & )
    {
        return 0; // abort!
    }
    catch( const SocketException &ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex);
        close();
        throw StreamConnectionException(ex.what());
    }
    catch( const StreamConnectionException & )
    {
        throw;
    }
    catch(...)
    {
        const char* desc = "Unhandled exception caught";
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, desc);
        close();
        throw StreamConnectionException(desc);
    }
}

//-----------------------------------------------------------------------------
//  Flush input network buffer
//-----------------------------------------------------------------------------
void StreamSocketConnection::flushInput()
{
    CtiLockGuard<CtiCriticalSection> guard(_readMux);

    SOCKET sock = getSocket();

    _readBuffer.resize(4096);

    for(;;)
    {
        const int bytesRead = ::recv(sock, &_readBuffer[0], _readBuffer.size(), 0);
        if( bytesRead == SOCKET_ERROR )
        {
            const int error = WSAGetLastError();
            if( error != WSAEWOULDBLOCK )
            {
                const std::string desc = getErrorMessage(sock, error);
                CTILOG_ERROR(dout, desc);
                close();
                throw StreamConnectionException(desc);
            }

            // wsock network buffer appears to be empty
            break;
        }

        if( ! bytesRead )
        {
            const char* desc = "Connection has been gracefully closed";
            CTILOG_ERROR(dout, desc);
            close();
            throw StreamConnectionException(desc);
        }
    }

    _readBuffer.clear();
}

//-----------------------------------------------------------------------------
//  swap connections
//-----------------------------------------------------------------------------
void StreamSocketConnection::swap(StreamSocketConnection& other)
{
    CtiLockGuard<CtiCriticalSection> guard_1(_readMux);
    CtiLockGuard<CtiCriticalSection> guard_2(_socketMux);

    CtiLockGuard<CtiCriticalSection> guard_3(other._readMux);
    CtiLockGuard<CtiCriticalSection> guard_4(other._socketMux);

    // Note:
    // - no need to acquire the writeMux, its only there to prevent concurrent write
    // - we dont swap the SocketsEventsManager
    std::swap(_socket,         other._socket);
    std::swap(_connectionMode, other._connectionMode);
    std::swap(_readBuffer,     other._readBuffer);

    Name.swap(other.Name);
}

} // namespace Cti
