#include "precompiled.h"

#include <string>
#include <boost/lexical_cast.hpp>

#include "logger.h"
#include "std_helper.h"
#include "win_helper.h"
#include "millisecond_timer.h"
#include "streamSocketListener.h"

using std::string;
using std::endl;

namespace Cti {

namespace {

std::string getErrorMessage(int error)
{
    std::ostringstream desc;
    desc << "Error " << error << " -> " << getSystemErrorMessage(error);
    return desc.str();
}

std::string getErrorMessage(const SOCKET s, int error)
{
    std::ostringstream desc;
    desc << "Socket " << s << ", Error " << error << " -> " << getSystemErrorMessage(error);
    return desc.str();
}

void logError(const char *label, int line, const std::string &desc)
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** ERROR **** " << label << " (" << line << "): " << desc << endl;
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
        logError(__FILE__, __LINE__, getErrorMessage(addrInfo.getError()));
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
        logError(__FILE__, __LINE__, ex.what());
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
            logError(__FILE__, __LINE__, getErrorMessage(newSocket,  WSAGetLastError()));
            ::closesocket(newSocket);
            return std::auto_ptr<StreamSocketConnection>();
        }

        // Set to non-blocking mode...
        unsigned long nonBlocking = 1;
        if( ::ioctlsocket(newSocket, FIONBIO, &nonBlocking) == SOCKET_ERROR )
        {
            logError(__FILE__, __LINE__, getErrorMessage(newSocket,  WSAGetLastError()));
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
        logError(__FILE__, __LINE__, ex.what());
        return std::auto_ptr<StreamSocketConnection>();
    }
}

} // namespace Cti
