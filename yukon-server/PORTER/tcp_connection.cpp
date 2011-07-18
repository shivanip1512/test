#include "precompiled.h"

#include "cparms.h"
#include "logger.h"

#include "tcp_connection.h"

#include <boost/scoped_array.hpp>

namespace Cti    {
namespace Porter {
namespace Connections {

using std::string;
using std::endl;

TcpSocketStream::TcpSocketStream(const SocketStream &other) :
    SocketStream(other)
{
    s = socket(AF_INET, SOCK_STREAM, 0);

    if( s == INVALID_SOCKET)
    {
        int error = reportSocketError("socket", __FUNCTION__, __FILE__, __LINE__);

        throw cannot_open_socket(error);
    }

    unsigned long nonblocking = 1;
    if( ioctlsocket(s, FIONBIO, &nonblocking) )
    {
        int error = reportSocketError("ioctlsocket", __FUNCTION__, __FILE__, __LINE__);

        if( closesocket(s) )
        {
            reportSocketError("ioctlsocket", __FUNCTION__, __FILE__, __LINE__);
        }

        throw cannot_set_nonblocking(error);
    }
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
        reportSocketError("closesocket", __FUNCTION__, __FILE__, __LINE__);
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


int TcpSocketStream::reportSocketError(const string winsock_function_name, const char *method_name, const char *file, const int line)
{
    int error = WSAGetLastError();

    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** Checkpoint - " << winsock_function_name << "() returned (" << error << ") in " << method_name << "() **** " << file << " (" << line << ")" << endl;

    return error;
}


PendingTcpConnection::PendingTcpConnection(const InactiveSocketStream &inactive) :
    TcpSocketStream(inactive),
    timeout(CtiTime::now() + gConfigParms.getValueAsULong("PORTER_TCP_CONNECT_TIMEOUT", 15))
{
    sockaddr_in inet_socket_address = { AF_INET, htons(address.port), *(in_addr*)&address.ip, 0 };

    if( connect(sock(), reinterpret_cast<sockaddr *>(&inet_socket_address), sizeof(inet_socket_address)) == SOCKET_ERROR )
    {
        int error = WSAGetLastError();

        if( error != WSAEWOULDBLOCK )
        {
            reportSocketError("connect", __FUNCTION__, __FILE__, __LINE__);

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
        reportSocketError("setsockopt", __FUNCTION__, __FILE__, __LINE__);
    }

    //  Turn on the keepalive timer
    int keepalive_timer = 1;
    if( setsockopt(sock(), SOL_SOCKET, SO_KEEPALIVE, reinterpret_cast<char *>(&keepalive_timer), sizeof(keepalive_timer)) )
    {
        reportSocketError("setsockopt", __FUNCTION__, __FILE__, __LINE__);
    }

    //  enable a hard close - erases all pending outbound data, sends a reset to the other side
    linger l = {1, 0};
    if( setsockopt(sock(), SOL_SOCKET, SO_LINGER, reinterpret_cast<char *>(&l), sizeof(l)) )
    {
        reportSocketError("setsockopt", __FUNCTION__, __FILE__, __LINE__);
    }
}


EstablishedTcpConnection::~EstablishedTcpConnection()
{
    if( shutdown(sock(), 2) )  //  SD_BOTH
    {
        reportSocketError("shutdown", __FUNCTION__, __FILE__, __LINE__);
    }
}


int EstablishedTcpConnection::send(const bytes &data) const
{
    int bytes_sent = ::send(sock(), &data.front(), data.size(), 0);

    if( bytes_sent == SOCKET_ERROR )
    {
        int error = reportSocketError("send", __FUNCTION__, __FILE__, __LINE__);

        throw write_error(error);
    }

    return bytes_sent;
}


void EstablishedTcpConnection::recv()
{
    u_long bytes_available;

    if( ioctlsocket(sock(), FIONREAD, &bytes_available) )
    {
        int error = reportSocketError("ioctlsocket", __FUNCTION__, __FILE__, __LINE__);

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
        int error = reportSocketError("recv", __FUNCTION__, __FILE__, __LINE__);

        throw read_error(error);
    }

    stream.insert(stream.end(),
                  recv_buf.get(),
                  recv_buf.get() + bytes_read);
}


}
}
}

