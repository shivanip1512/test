#pragma once

#include "ctitime.h"
#include <vector>

namespace Cti    {
namespace Porter {
namespace Connections {

struct SocketAddress
{
    SocketAddress(unsigned long ip_, unsigned short port_) :
        ip  (ip_),
        port(port_)
    { }

    const unsigned long  ip;
    const unsigned short port;

    bool operator==(const SocketAddress &other) const
    {
        return ip == other.ip && port == other.port;
    }
};


struct SocketStream
{
    typedef std::vector<char> bytes;

    bytes stream;

    const SocketAddress address;

    SocketStream(const SocketAddress &address_) :
        address(address_)
    { }
};


struct InactiveSocketStream : SocketStream
{
    CtiTime next_attempt;

    InactiveSocketStream(const SocketAddress &address) :
        SocketStream(address)
    { }

    InactiveSocketStream(const SocketStream &s) :
        SocketStream(s)
    { }
};


struct TcpSocketStream : SocketStream
{
private:

    SOCKET s;

protected:

    //  throws: cannot_open_socket, cannot_set_nonblocking
    TcpSocketStream(const SocketStream &other);
    TcpSocketStream(TcpSocketStream &other);
    ~TcpSocketStream();

    const SOCKET &sock() const { return s; }

public:

    static int reportSocketError(const std::string winsock_function_name, const char *method_name, const char *file, const int line);

    void add_to(fd_set *set) const;
    bool is_in(const fd_set *set) const;

    //  exceptions that may be thrown by TcpSocket and children

    struct tcp_socket_error : std::runtime_error
    {
    protected:
        tcp_socket_error() : std::runtime_error("") { };
    };

    struct wsa_error_code : tcp_socket_error
    {
        const int code;

        wsa_error_code(int code_) : code(code_) { };
    };

    struct cannot_open_socket : wsa_error_code
    {
        cannot_open_socket(int error) : wsa_error_code(error) {};
    };

    struct cannot_set_nonblocking : wsa_error_code
    {
        cannot_set_nonblocking(int error) : wsa_error_code(error) {};
    };
};


struct PendingTcpConnection : TcpSocketStream
{
    //  throws: cannot_connect
    PendingTcpConnection(const InactiveSocketStream &inactive);

    const CtiTime timeout;

    //  exceptions
    struct cannot_connect : wsa_error_code
    {
        cannot_connect(int error) : wsa_error_code(error) {};
    };
};


struct EstablishedTcpConnection : TcpSocketStream
{
    EstablishedTcpConnection(PendingTcpConnection &p);

    ~EstablishedTcpConnection();

    //  throws: write_error
    int  send(const bytes &data) const;
    //  throws: read_error, connection_closed
    void recv();

    //  exceptions
    struct read_error : wsa_error_code
    {
        read_error(int code_) : wsa_error_code(code_) { };
    };

    struct write_error : wsa_error_code
    {
        write_error(int code_) : wsa_error_code(code_) { };
    };

    struct connection_closed : tcp_socket_error
    {
    };
};


}
}
}

