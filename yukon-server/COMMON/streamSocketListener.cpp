#include "precompiled.h"

#include "logger.h"
#include "std_helper.h"
#include "win_helper.h"
#include "millisecond_timer.h"
#include "streamSocketListener.h"

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

} // namespace anonymous

//-----------------------------------------------------------------------------
//  Constructor / Destructor
//---------------------------------------------------------------------------
StreamSocketListener::StreamSocketListener()
{}

StreamSocketListener::~StreamSocketListener()
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
bool StreamSocketListener::isValid() const
{
    return listeningSockets.areSocketsValid();
}

//-----------------------------------------------------------------------------
//  Close the socket
//-----------------------------------------------------------------------------
void StreamSocketListener::close()
{
    listeningSockets.shutdownAndClose();
}

//-----------------------------------------------------------------------------
//  Create and start listener sockets for a given port
//-----------------------------------------------------------------------------
bool StreamSocketListener::create(unsigned short nPort)
{
    // Create AddrInfo for TCP server
    AddrInfo addrInfo = makeTcpServerSocketAddress(nPort);
    if( ! addrInfo )
    {
        CTILOG_ERROR(dout, getErrorMessage(addrInfo.getError()));
        return false;
    }

    try
    {
        // Create a TCP/IP stream socket to "listen" with
        listeningSockets.createSockets(addrInfo.get());

        // bind the name to the socket
        listeningSockets.bind(addrInfo.get());

        // set the sockets to listen. This initializes the wsock queues
        listeningSockets.listen(SOMAXCONN); // Number of connection request queue

        return true;
    }
    catch( const SocketException &ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex);
        return false;
    }
}

//-----------------------------------------------------------------------------
//  Waits on a socket for an incoming request, using optional abort handle
//-----------------------------------------------------------------------------
std::auto_ptr<StreamSocketConnection> StreamSocketListener::accept(ConnectionModes mode, const Chrono &timeout, const HANDLE *hAbort)
{
    try
    {
        SOCKET newSocket = listeningSockets.accept(timeout, hAbort);
        if( newSocket == INVALID_SOCKET )
        {
            return std::auto_ptr<StreamSocketConnection>(); // timeout or aborted
        }

        // Enable keep alive
        bool keepAlive = true;
        if( ::setsockopt(newSocket, SOL_SOCKET, SO_KEEPALIVE, (char*)&keepAlive, sizeof(bool)) == SOCKET_ERROR )
        {
            CTILOG_ERROR(dout, getErrorMessage(newSocket, WSAGetLastError()));
            ::closesocket(newSocket);
            return std::auto_ptr<StreamSocketConnection>();
        }

        // Set to non-blocking mode...
        unsigned long nonBlocking = 1;
        if( ::ioctlsocket(newSocket, FIONBIO, &nonBlocking) == SOCKET_ERROR )
        {
            CTILOG_ERROR(dout, getErrorMessage(newSocket, WSAGetLastError()));
            ::closesocket(newSocket);
            return std::auto_ptr<StreamSocketConnection>();
        }

        std::auto_ptr<StreamSocketConnection> newConnection( new StreamSocketConnection );

        newConnection->_socket          = newSocket;
        newConnection->_connectionMode  = mode;

        return newConnection; // transfer ownership to the caller
    }
    catch( const SocketAbortException & )
    {
        return std::auto_ptr<StreamSocketConnection>();
    }
    catch( const SocketException &ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex);
        return std::auto_ptr<StreamSocketConnection>();
    }
}

} // namespace Cti
