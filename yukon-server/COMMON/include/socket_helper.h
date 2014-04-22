#pragma once

#include <exception>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>
#include <string>

#include <boost/asio/ip/address.hpp>

#include "numstr.h"
#include "critical_section.h"
#include "guard.h"

namespace Cti {

//-----------------------------------------------------------------------------
//  Socket exception thrown by ServerSockets
//-----------------------------------------------------------------------------
class SocketException : public std::exception
{
    const std::string _desc;

public:
    SocketException( const std::string& desc ) :
        _desc( desc )
    {
    }

    virtual const char* what() const throw()
    {
        return _desc.c_str();
    }
};

//-----------------------------------------------------------------------------
//  Convert SOCKADDR to string using WSAAddressToString()
//-----------------------------------------------------------------------------
inline std::string socketAddressToString(const SOCKADDR *addr, int addrlen)
{
    // [0000:0000:0000:0000:0000:0000:127.127.127.127]:65535
    // maximum length of address (IPV6) with port including null terminator = 54

    char  str[54];              // array of byte that can contain the largest address (IPV6) with port
    DWORD strlen= sizeof(str);  // in case of a WSAEFAULT error, this parameter will contain the required length

    if( WSAAddressToString((LPSOCKADDR)addr, (DWORD)addr, NULL, str, &strlen) == SOCKET_ERROR )
    {
        int err_code = WSAGetLastError();
        return std::string();   // on error, return an empty string
    }

    return std::string(str, strlen);
}

//-----------------------------------------------------------------------------
//  Get string IP Address from SOCKADDR using getNameInfo()
//-----------------------------------------------------------------------------
inline std::string getIpAddressFromSocketAddress(const SOCKADDR *addr, int addrlen)
{
    // 0000:0000:0000:0000:0000:0000:127.127.127.127
    // maximum length of address (IPV6) including null terminator = 46

    char host[46]; // array of byte that can contain the largest IP address (IPV6)

    if( getnameinfo( addr, addrlen, host, sizeof(host), 0, 0, NI_NUMERICHOST ) != 0 )
    {
        int err_code = WSAGetLastError();
        return std::string();   // on error, return an empty string
    }

    return host;
}

//-----------------------------------------------------------------------------
//  Format a host name (or an ip address) and a port into a string
//-----------------------------------------------------------------------------
inline std::string formatHostAndPort(const std::string& host, u_short port)
{
    std::ostringstream ostr;

    const boost::asio::ip::address ipAddress = boost::asio::ip::address::from_string(host);

    if( ipAddress.is_v6() )
    {
        ostr << "[" << host << "]:" << port;
    }
    else
    {
        ostr << host << ":" << port;
    }

    return ostr.str();
}

//-----------------------------------------------------------------------------
//  Manages a socket address of IPv4 and IPv6 family
//-----------------------------------------------------------------------------
struct SocketAddress
{
    typedef union address
    {
        SOCKADDR            sa;
        SOCKADDR_IN         sa_in;
        SOCKADDR_IN6        sa_in6;
        //SOCKADDR_STORAGE    sa_stor;
    } address_t;

    enum
    {
        STORAGE_SIZE = sizeof(address_t) // maximum length for address storage
    };

    address_t _addr;
    int       _addrlen;

    // default construct initializes addr length to zero
    // if the socket address is needed to receive data, i can be initialize
    SocketAddress( int addrlen = 0 ) :
        _addrlen(addrlen)
    {
        assert( _addrlen <= STORAGE_SIZE );
    }

    // copy constructor
    SocketAddress( const SocketAddress& ref ) :
        _addrlen(ref._addrlen)
    {
        assert( _addrlen <= STORAGE_SIZE );
        memcpy( &_addr, &ref._addr, _addrlen );
    }

    // constructor that can use with ADDRINFO results
    SocketAddress( const SOCKADDR* paddr, int addrlen ) :
        _addrlen(addrlen)
    {
        assert( _addrlen <= STORAGE_SIZE );
        memcpy( &_addr, paddr, _addrlen );
    }

    ~SocketAddress()
    {
    }

    void reset( int addrlen = 0 )
    {
        _addrlen = addrlen;
        assert( _addrlen <= STORAGE_SIZE );
    }

    // convert all components of the sockaddr into a human readable string
    // NOTE: use only for display (logging)
    std::string toString() const
    {
        if( !_addrlen )
        {
            return std::string(); // if address length is null, return empty string
        }

        return socketAddressToString( &_addr.sa, _addrlen );
    }

    // get the IP address (string) from the sockaddr
    std::string getIpAddress() const
    {
        if( !_addrlen )
        {
            return std::string(); // if address length is null, return empty string
        }

        return getIpAddressFromSocketAddress( &_addr.sa, _addrlen );
    }

    // get the 16-bit port number from network byte order
    u_short getPort() const
    {
        return ntohs( _addr.sa_in.sin_port );
    }

    SocketAddress& operator=(const SocketAddress& ref)
    {
        _addrlen = ref._addrlen;
        assert( _addrlen <= STORAGE_SIZE );
        memcpy( &_addr, &ref._addr, _addrlen );
        return *this;
    }

    // Return true if value as content
    operator bool() const
    {
        return !_addrlen;
    }

    // Return true if socket addresses have similar family, port and IP address
    bool compare( const SocketAddress& ref )
    {
        // make sure length is not 0 and is similar
        if( !_addrlen || _addrlen != ref._addrlen )
        {
            return false;
        }

        // only compare addresses of similar families
        if( _addr.sa.sa_family != ref._addr.sa.sa_family )
        {
            return false;
        }

        switch( _addr.sa.sa_family )
        {
        case AF_INET :
            {
                return _addr.sa_in.sin_port == ref._addr.sa_in.sin_port &&
                       memcmp(&_addr.sa_in.sin_addr, &ref._addr.sa_in.sin_addr, sizeof(IN_ADDR)) == 0;
            }
        case AF_INET6 :
            {
                return _addr.sa_in6.sin6_port == ref._addr.sa_in6.sin6_port &&
                       memcmp(&_addr.sa_in6.sin6_addr, &ref._addr.sa_in6.sin6_addr, sizeof(IN6_ADDR)) == 0;
            }
        }

        return false;
    }

    bool operator==(const SocketAddress& ref)
    {
        return compare(ref);
    }
};

//-----------------------------------------------------------------------------
//  Struct to manage ADDRINFOA generated by getaddrinfo
//-----------------------------------------------------------------------------
struct AddrInfo
{
private:
    PADDRINFOA _p_ai;
    int        _err_code;

public:
    AddrInfo( PCSTR pNodeName, PCSTR pServiceName, const ADDRINFOA *pHints )
    {
        _p_ai     = NULL;
        _err_code = getaddrinfo(pNodeName, pServiceName, pHints, &_p_ai);
    }

    ~AddrInfo()
    {
        if( _p_ai )
        {
            freeaddrinfo(_p_ai);
        }
    }

    int getError()
    {
        return _err_code;
    }

    // access member of the first address info found
    PADDRINFOA operator->() const
    {
        assert(_p_ai != NULL);
        return _p_ai;
    }

    operator bool() const
    {
        return (_p_ai != NULL && _err_code == 0);
    }

    void reset( PCSTR pNodeName, PCSTR pServiceName, const ADDRINFOA *pHints )
    {
        if( _p_ai )
        {
            freeaddrinfo(_p_ai);
            _p_ai = NULL;
        }

        _err_code = getaddrinfo(pNodeName, pServiceName, pHints, &_p_ai);
    }

    PADDRINFOA release()
    {
        PADDRINFOA tmp = _p_ai;
        _p_ai = NULL;
        return tmp;
    }

    // convert all components of the sockaddr into a human readable string
    // NOTE: use only for display (logging)
    std::string toString() const
    {
        if( !_p_ai )
        {
            return std::string(); // return empty string
        }

        return socketAddressToString(_p_ai->ai_addr, _p_ai->ai_addrlen);
    }

    PADDRINFOA get() const
    {
        return _p_ai;
    }
};

//-----------------------------------------------------------------------------
//  Returns an addrinfo struct
//-----------------------------------------------------------------------------
inline AddrInfo makeSocketAddress(const char *nodename, const char* servname, const int socktype, const int protocol, const int flags = 0)
{
    ADDRINFOA hints = {};

    // Set requirements
    hints.ai_family   = AF_UNSPEC;                  // IPv4 or IPv6
    hints.ai_socktype = socktype;                   // SOCK_STREAM (tcp) or SOCK_DGRAM (udp)
    hints.ai_protocol = protocol;                   // IPPROTO_TCP or IPPROTO_UDP
    hints.ai_flags    = flags;                      // If node is not NULL, then the AI_PASSIVE flag is ignored.

    return AddrInfo(nodename, servname, &hints);
}

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for TCP client
//-----------------------------------------------------------------------------
inline AddrInfo makeTcpClientSocketAddress(const char *nodename, const char *servname)
{
    return makeSocketAddress(nodename, servname, SOCK_STREAM, IPPROTO_TCP);
};

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for UDP client
//-----------------------------------------------------------------------------
inline AddrInfo makeUdpClientSocketAddress(const char *nodename, const char *servname)
{
    return makeSocketAddress(nodename, servname, SOCK_DGRAM, IPPROTO_UDP);
};

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for TCP server
//-----------------------------------------------------------------------------
inline AddrInfo makeTcpServerSocketAddress(const char *nodename, const char *servname)
{
    return makeSocketAddress(nodename, servname, SOCK_STREAM, IPPROTO_TCP, AI_PASSIVE);
};

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for UDP server
//-----------------------------------------------------------------------------
inline AddrInfo makeUdpServerSocketAddress(const char *nodename, const char *servname)
{
    return makeSocketAddress(nodename, servname, SOCK_DGRAM, IPPROTO_UDP, AI_PASSIVE);
};

//-----------------------------------------------------------------------------
//  Server socket wrapper
//  listen to sockets in parallel for using with different family types
//-----------------------------------------------------------------------------
class ServerSockets
{
    typedef std::vector<SOCKET> SocketsVec;
    SocketsVec _sockets;

    mutable CtiCriticalSection _socketsMux;

    int  _lastError;
    bool _nonBlocking;

    // Shutdown and close a vector sockets
    void shutdownAndClose( SocketsVec &sockets )
    {
        for each( SOCKET s in sockets )
        {
            if( s != INVALID_SOCKET )
            {
                shutdown(s, SD_BOTH);
                closesocket(s);
            }
        }
        sockets.clear();
    }

public:

    ServerSockets() :
        _lastError(0),
        _nonBlocking(false) // blocking by default
    {}

    // Shutdown and close all sockets
    // Note: sockets can still be (re)created afterwards
    void shutdownAndClose()
    {
        CtiLockGuard<CtiCriticalSection> guard(_socketsMux);
        shutdownAndClose(_sockets);
    }

    ~ServerSockets()
    {
        shutdownAndClose();
    }

    int getLastError() const
    {
        return _lastError;
    }

    // Returns a copy of the current sockets
    SocketsVec getSockets() const
    {
        CtiLockGuard<CtiCriticalSection> guard(_socketsMux);
        return _sockets; // return a copy
    }

    // creates sockets from addr info
    void createSockets( PADDRINFOA p_ai )
    {
        // shutdown all sockets handle before continuing
        shutdownAndClose();

        if( p_ai == NULL )
        {
            throw SocketException("PADDRINFOA is NULL");
        }

        SocketsVec sockets;

        while( p_ai != NULL )
        {
            SOCKET s_new = ::socket(p_ai->ai_family, p_ai->ai_socktype, p_ai->ai_protocol );
            if( s_new == INVALID_SOCKET )
            {
                _lastError = WSAGetLastError();
                shutdownAndClose( sockets );
                throw SocketException("socket creation failed with error code: " + CtiNumStr(_lastError));
            }
            sockets.push_back(s_new);
            p_ai = p_ai->ai_next;
        }

        {
            CtiLockGuard<CtiCriticalSection> guard(_socketsMux);
            _sockets = sockets;
        }
    }

    // Set option on all sockets
    void setOption( int level, int optname, const char *optval, int optlen )
    {
        SocketsVec sockets = getSockets(); // get a copy of the sockets
        if( sockets.empty() )
        {
            throw SocketException("No sockets found");
        }

        for each( SOCKET s in sockets )
        {
            if( ::setsockopt(s, level, optname, optval, optlen) == SOCKET_ERROR )
            {
                _lastError = WSAGetLastError();
                shutdownAndClose();
                throw SocketException("setsockopt has fail with error code: " + CtiNumStr(_lastError));
            }
        }
    }

    // Set ioMode on all sockets
    void setIOMode(long cmd, u_long *argp)
    {
        SocketsVec sockets = getSockets(); // get a copy of the sockets
        if( sockets.empty() )
        {
            throw SocketException("No sockets found");
        }

        for each( SOCKET s in sockets )
        {
            if( ::ioctlsocket(s, cmd, argp) == SOCKET_ERROR )
            {
                _lastError = WSAGetLastError();
                shutdownAndClose();
                throw SocketException("ioctlsocket has fail with error code: " + CtiNumStr(_lastError));
            }
        }

        if( cmd == FIONBIO )
        {
            _nonBlocking = (*argp != 0);
        }
    }

    // Binds all socket to the corresponding address info
    void bind( PADDRINFOA p_ai )
    {
        SocketsVec sockets = getSockets(); // get a copy of the sockets
        if( sockets.empty() )
        {
            throw SocketException("No sockets found");
        }

        for each( SOCKET s in sockets )
        {
            SOCKADDR* addr = NULL;
            int addrlen    = 0;

            if(p_ai)
            {
                addr    = p_ai->ai_addr;
                addrlen = p_ai->ai_addrlen;
                p_ai    = p_ai->ai_next;
            }

            if( ::bind(s, addr, addrlen) == SOCKET_ERROR )
            {
                _lastError = WSAGetLastError();
                shutdownAndClose();
                throw SocketException("bind has fail with error code: " + CtiNumStr(_lastError));
            }
        }
    }

    // Call listen on all sockets
    void listen( int backlog )
    {
        SocketsVec sockets = getSockets(); // get a copy of the sockets
        if( sockets.empty() )
        {
            throw SocketException("No sockets found");
        }

        for each( SOCKET s in sockets )
        {
            if( ::listen(s, backlog) == SOCKET_ERROR )
            {
                _lastError = WSAGetLastError();
                shutdownAndClose();
                throw SocketException("listen has fail with error code: " + CtiNumStr(_lastError));
            }
        }
    }

    // Accept first socket connection in blocking or non-blocking mode
    SOCKET accept(SOCKADDR *addr, int *addrlen)
    {
        SocketsVec sockets = getSockets(); // get a copy of the sockets
        if( sockets.empty() )
        {
            _lastError = WSAENOTSOCK;
            return INVALID_SOCKET;
        }

        SOCKET s_out = INVALID_SOCKET;

        if( _nonBlocking )
        {
            const int addrlen_init = (addrlen) ? *addrlen : 0;

            for each( SOCKET s in sockets )
            {
                if(( s_out = ::accept( s, addr, addrlen )) != INVALID_SOCKET )
                {
                    return s_out;
                }

                if(( _lastError = WSAGetLastError()) != WSAEWOULDBLOCK )
                {
                    shutdownAndClose();
                    return INVALID_SOCKET;
                }

                if( addrlen )
                {
                    // if addrlen is not null restore its initial value
                    *addrlen = addrlen_init;
                }
            }
        }
        else // blocking mode
        {
            fd_set SockSet;
            FD_ZERO(&SockSet);

            for each( SOCKET s in sockets )
            {
                FD_SET(s, &SockSet);
            }

            // we should block here until one of the socket is ready to accept a connection
            if( ::select(0, &SockSet, NULL, NULL, NULL) == SOCKET_ERROR )
            {
                _lastError = WSAGetLastError();
                shutdownAndClose();
                return INVALID_SOCKET;
            }

            for each( SOCKET s in sockets )
            {
                if( FD_ISSET(s, &SockSet) )
                {
                    if(( s_out = ::accept( s, addr, addrlen )) == INVALID_SOCKET )
                    {
                        _lastError = WSAGetLastError();
                        shutdownAndClose();
                    }

                    return s_out;
                }
            }
        }

        return INVALID_SOCKET;
    }

    // wrapper for when return address is unused
    SOCKET accept()
    {
        return accept( NULL, NULL );
    }

    // wrapper using a socket address storage
    // note : available storage length shall be set prior by the user
    SOCKET accept( SocketAddress& addr )
    {
        return accept( &addr._addr.sa, &addr._addrlen );
    }

    // Return first socket corresponding to the family given in argument
    SOCKET const getFamilySocket(const int family)
    {
        SocketsVec sockets = getSockets();
        for each( SOCKET s in sockets )
        {
            SocketAddress addr( SocketAddress::STORAGE_SIZE );
            if( ::getsockname(s, &addr._addr.sa, &addr._addrlen) == SOCKET_ERROR )
            {
                _lastError = WSAGetLastError();
                return INVALID_SOCKET;
            }

            if( addr._addr.sa.sa_family == family )
            {
                return s;
            }
        }

        return INVALID_SOCKET;
    }

    // Return true if sockets are all valid, false otherwise
    bool areSocketsValid() const
    {
        SocketsVec sockets = getSockets();
        if( sockets.empty() )
        {
            return false;
        }

        for each( SOCKET s in sockets )
        {
            if( s == INVALID_SOCKET )
            {
                return false;
            }
        }

        return true;
    }
};

}
