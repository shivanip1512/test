#include "precompiled.h"

#include "cparms.h"
#include "logger.h"
#include "socket_helper.h"

#include "tcp_connection.h"

#include <boost/scoped_array.hpp>

namespace Cti::Porter::Connections {

using std::string;
using std::endl;

bool operator<(const SocketAddress &lhs, const SocketAddress &rhs)
{
    if( const auto ip_compare = lhs.ip.compare(rhs.ip);
        ip_compare < 0 )
    {
        return true;
    }
    else if( ip_compare == 0 )
    {
        return lhs.port < rhs.port;
    }

    return false;
}

TcpSocketStream::TcpSocketStream(const SocketStream &other) :
    SocketStream(other),
    s(INVALID_SOCKET)
{
}

TcpSocketStream::TcpSocketStream(TcpSocketStream &other) :
    SocketStream(other),
    s(other.s)
{
    other.s = INVALID_SOCKET;  //  transfer of ownership
}

TcpSocketStream::~TcpSocketStream()
{
    if( s != INVALID_SOCKET && closesocket(s) )
    {
        CTILOG_ERROR(dout, formatSocketError("closesocket", WSAGetLastError()));
    }
}

void TcpSocketStream::createSocket(int af, int type, int protocol)
{
    s = socket(af, type, protocol);

    if( s == INVALID_SOCKET)
    {
        const int error = WSAGetLastError();
        CTILOG_ERROR(dout, formatSocketError("socket", error));

        throw cannot_open_socket(error);
    }

    unsigned long nonblocking = 1;
    if( ioctlsocket(s, FIONBIO, &nonblocking) )
    {
        const int error = WSAGetLastError();
        CTILOG_ERROR(dout, formatSocketError("ioctlsocket", error));

        if( closesocket(s) )
        {
            CTILOG_ERROR(dout, formatSocketError("closesocket", WSAGetLastError()));
        }

        throw cannot_set_nonblocking(error);
    }
}

void TcpSocketStream::add_to(fd_set *fds) const
{
    FD_SET(s, fds);
}


bool TcpSocketStream::is_in(const fd_set *fds) const
{
    return FD_ISSET(s, fds);
}


std::string TcpSocketStream::formatSocketError(const string& winsock_function_name, int error)
{
    return Cti::StreamBuffer() << winsock_function_name <<"() returned error "<< error <<" -> "<< Cti::getSystemErrorMessage(error);
}


PendingTcpConnection::PendingTcpConnection(const InactiveSocketStream &inactive) :
    TcpSocketStream(inactive),
    timeout(CtiTime::now() + gConfigParms.getValueAsULong("PORTER_TCP_CONNECT_TIMEOUT", 15))
{
    Cti::AddrInfo pAddrInfo = Cti::makeTcpClientSocketAddress(address.ip, address.port);
    if( ! pAddrInfo )
    {
        CTILOG_ERROR(dout, "Cannot resolve IP \""<< address.ip <<":"<< address.port <<" (getaddrinfo() failed with error: "<< pAddrInfo.getError() <<")");

        throw cannot_connect(pAddrInfo.getError());
    }

    createSocket(pAddrInfo->ai_family, pAddrInfo->ai_socktype, pAddrInfo->ai_protocol);

    if( connect(sock(), pAddrInfo->ai_addr, pAddrInfo->ai_addrlen) == SOCKET_ERROR )
    {
        const int error = WSAGetLastError();
        if( error != WSAEWOULDBLOCK )
        {
            CTILOG_ERROR(dout, formatSocketError("connect", error));
            throw cannot_connect(error);
        }
    }
}


EstablishedTcpConnection::EstablishedTcpConnection(PendingTcpConnection &p) :
    TcpSocketStream(p)
{
    //  none of these are fatal errors, so we'll just log and continue

    //  Make sure we time out on our writes after 5 seconds
    int socket_write_timeout = gConfigParms.getValueAsInt("PORTER_SOCKET_WRITE_TIMEOUT", 5);
    if( setsockopt(sock(), SOL_SOCKET, SO_SNDTIMEO, reinterpret_cast<char *>(&socket_write_timeout), sizeof(socket_write_timeout)) )
    {
        CTILOG_ERROR(dout, formatSocketError("setsockopt", WSAGetLastError()));
    }

    //  Turn on the keepalive timer
    int keepalive_timer = 1;
    if( setsockopt(sock(), SOL_SOCKET, SO_KEEPALIVE, reinterpret_cast<char *>(&keepalive_timer), sizeof(keepalive_timer)) )
    {
        CTILOG_ERROR(dout, formatSocketError("setsockopt", WSAGetLastError()));
    }

    //  enable a hard close - erases all pending outbound data, sends a reset to the other side
    linger l = {1, 0};
    if( setsockopt(sock(), SOL_SOCKET, SO_LINGER, reinterpret_cast<char *>(&l), sizeof(l)) )
    {
        CTILOG_ERROR(dout, formatSocketError("setsockopt", WSAGetLastError()));
    }
}


EstablishedTcpConnection::~EstablishedTcpConnection()
{
    if( shutdown(sock(), 2) )  //  SD_BOTH
    {
        const auto wsaError = WSAGetLastError();

        if( wsaError != WSAENOTSOCK )
        {
            CTILOG_ERROR(dout, formatSocketError("shutdown", wsaError));
        }
    }
}


int EstablishedTcpConnection::send(const bytes &data) const
{
    int bytes_sent = ::send(sock(), reinterpret_cast<const char*>(&data.front()), data.size(), 0);

    if( bytes_sent == SOCKET_ERROR )
    {
        const int error = WSAGetLastError();
        CTILOG_ERROR(dout, formatSocketError("send", error));

        throw write_error(error);
    }

    return bytes_sent;
}


void EstablishedTcpConnection::recv()
{
    u_long bytes_available = 0;

    if( ioctlsocket(sock(), FIONREAD, &bytes_available) )
    {
        const int error = WSAGetLastError();
        CTILOG_ERROR(dout, formatSocketError("ioctlsocket", error));

        throw read_error(error);
    }

    //  we got in here because the socket reported it was readable - if we get 0 bytes, the connection must be closed
    if( !bytes_available )
    {
        throw connection_closed();
    }

    boost::scoped_array<char> recv_buf(new char[bytes_available]);

    int bytes_read = ::recv(sock(), recv_buf.get(), bytes_available, 0);

    //  we got in here because the socket reported it was readable - if we get 0 bytes, the connection must be closed
    if( !bytes_read )
    {
        throw connection_closed();
    }

    if( bytes_read == SOCKET_ERROR )
    {
        const int error = WSAGetLastError();
        CTILOG_ERROR(dout, formatSocketError("recv", error));

        throw read_error(error);
    }

    stream.insert(stream.end(),
                  recv_buf.get(),
                  recv_buf.get() + bytes_read);
}


}