#pragma once

#include "critical_section.h"
#include "guard.h"
#include "timing_util.h"
#include "win_helper.h"
#include "loggable.h"
#include "dlldefs.h"

#include <boost/ptr_container/ptr_list.hpp>

// Automagically include winsock lib in all modules that use socket_helper.
#pragma comment( lib, "wsock32.lib" )

namespace Cti {

//-----------------------------------------------------------------------------
//  Socket exception thrown by ServerSockets
//-----------------------------------------------------------------------------
class IM_EX_CTIBASE SocketException : public std::exception
{
    const std::string _desc;
    const int         _errorCode;

public:
    SocketException(const std::string& desc);

    SocketException(const std::string& desc, int errorCode);

    virtual const char* what() const throw();

    int getErrorCode() const;
};

class IM_EX_CTIBASE SocketAbortException : public SocketException
{
public:
    SocketAbortException();
};

//-----------------------------------------------------------------------------
//  Convert SOCKADDR to string using WSAAddressToString()
//-----------------------------------------------------------------------------
IM_EX_CTIBASE std::string socketAddressToString(const SOCKADDR *addr, int addrlen);

//-----------------------------------------------------------------------------
//  Get string IP Address from SOCKADDR using getNameInfo()
//-----------------------------------------------------------------------------
IM_EX_CTIBASE std::string getIpAddressFromSocketAddress(const SOCKADDR *addr, int addrlen);

//-----------------------------------------------------------------------------
//  Format a host name (or an ip address) and a port into a string
//-----------------------------------------------------------------------------
IM_EX_CTIBASE std::string formatHostAndPort(const std::string& host, u_short port);

namespace Detail {

/**
 * Helper class that contains sockets events associated with the network events.
 * This class is called by SocketsEventsManager::waitForEvent()
 */
class SocketsEvent : private boost::noncopyable
{
    const std::vector<SOCKET> _sockets;
    const WSAEVENT            _hEvent;
    const long                _lNetworkEvents;

public:

    SocketsEvent(const std::vector<SOCKET> &sockets, const long lNetworkEvents);

    ~SocketsEvent();

    void triggerEvent() const;

    WSAEVENT getHandle() const;

    SOCKET findActiveSocket() const;
};

} // namespace Detail

/**
 *  Manages sockets events and provides waitForEvent() and interruptBlockingWait()
 *  used by StreamSocketConnection
 */
class SocketsEventsManager : private boost::noncopyable
{
    typedef Detail::SocketsEvent          SocketsEvent;
    typedef boost::ptr_list<SocketsEvent> SocketsEventList;

    SocketsEventList           _eventList;
    mutable CtiCriticalSection _mux;

    /**
     * Manages the life cycle of a temporary event created inside waitForEvent()
     */
    class ScopedEvent : private boost::noncopyable
    {
        SocketsEventsManager&       _manager;
        SocketsEventList::iterator  _eventItr;

    public:
        ScopedEvent(SocketsEventsManager &manager, SocketsEvent *newEvent);
        ~ScopedEvent();
        const SocketsEvent* operator->() const;
    };

public:

    SOCKET waitForEvent(const std::vector<SOCKET> &sockets, long lNetworkEvents, const Timing::Chrono &timeout, const HANDLE *hAbort);

    bool waitForEvent(SOCKET socket, long lNetworkEvents, const Timing::Chrono &timeout, const HANDLE *hAbort);

    void interruptBlockingWait();
};

//-----------------------------------------------------------------------------
//  Manages a socket address of IPv4 and IPv6 family
//-----------------------------------------------------------------------------
class IM_EX_CTIBASE SocketAddress : public Cti::Loggable
{
public:
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
    SocketAddress(int addrlen = 0);

    // copy constructor
    SocketAddress(const SocketAddress& ref);

    // constructor that can use with ADDRINFO results
    SocketAddress(const SOCKADDR* paddr, int addrlen);

    ~SocketAddress();

    void reset(int addrlen = 0);

    // convert all components of the sockaddr into a human readable string
    // NOTE: use only for display (logging)
    std::string toString() const override;

    // get the IP address (string) from the sockaddr
    std::string getIpAddress() const;

    // get the 16-bit port number from network byte order
    u_short getPort() const;

    SocketAddress& operator=(const SocketAddress& ref);

    // Return true if value has content
    bool isSet() const;

    // Return true if socket addresses have similar family, port and IP address
    bool compare(const SocketAddress& ref);

    bool operator==(const SocketAddress& ref);
};

//-----------------------------------------------------------------------------
//  Struct to manage ADDRINFOA generated by getaddrinfo
//-----------------------------------------------------------------------------
class IM_EX_CTIBASE AddrInfo : public Cti::Loggable
{
private:
    PADDRINFOA _p_ai;
    int        _err_code;

public:
    AddrInfo(PCSTR pNodeName, PCSTR pServiceName, const ADDRINFOA *pHints);

    ~AddrInfo();

    int getError();

    // access member of the first address info found
    PADDRINFOA operator->() const;

    operator bool() const;

    void reset(PCSTR pNodeName, PCSTR pServiceName, const ADDRINFOA *pHints);

    PADDRINFOA release();

    // convert all components of the sockaddr into a human readable string
    // NOTE: use only for display (logging)
    std::string toString() const override;

    PADDRINFOA get() const;
};

//-----------------------------------------------------------------------------
//  Returns an addrinfo struct
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeSocketAddress(const char *nodename, const char* servname, const int socktype, const int protocol, const int flags = 0);

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for TCP client
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeTcpClientSocketAddress(const std::string &nodename, const std::string &servname);

IM_EX_CTIBASE AddrInfo makeTcpClientSocketAddress(const std::string &nodename, const unsigned short nport);

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for TCP server
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeTcpServerSocketAddress(const std::string &servname);

IM_EX_CTIBASE AddrInfo makeTcpServerSocketAddress(const unsigned short nport);

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for UDP client
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeUdpClientSocketAddress(const std::string &nodename, const std::string &servname);

IM_EX_CTIBASE AddrInfo makeUdpClientSocketAddress(const std::string &nodename, const unsigned short nport);

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for UDP server
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeUdpServerSocketAddress(const std::string &servname);

IM_EX_CTIBASE AddrInfo makeUdpServerSocketAddress(const unsigned short nport);

//-----------------------------------------------------------------------------
//  Server socket wrapper
//  listen to sockets in parallel for using with different family types
//-----------------------------------------------------------------------------
class IM_EX_CTIBASE ServerSockets
{
    typedef std::vector<SOCKET> SocketsVec;
    SocketsVec _sockets;

    mutable CtiCriticalSection _socketsMux;

    int  _lastError;

    SocketsEventsManager _socketsEventsManager;

public:

    ServerSockets();
    ~ServerSockets();

    // Shutdown and close a vector sockets
    void shutdownAndClose(SocketsVec &sockets);

    // Shutdown and close all sockets
    // Note: sockets can still be (re)created afterwards
    void shutdownAndClose();

    int getLastError() const;

    // Returns a copy of the current sockets
    SocketsVec getSockets() const;

    // creates sockets from addr info
    void createSockets(PADDRINFOA p_ai);

    // Set option on all sockets
    void setOption(int level, int optname, const char *optval, int optlen);

    // Set ioMode on all sockets
    void setIOMode(long cmd, u_long *argp);

    // Binds all socket to the corresponding address info
    void bind(PADDRINFOA p_ai);

    // Call listen on all sockets
    void listen(int backlog);

    // Accept first socket connection in blocking or non-blocking mode
    SOCKET accept(SOCKADDR *addr, int *addrlen, const Timing::Chrono &timeout, const HANDLE *hAbort);

    // wrapper for when return address is unused
    SOCKET accept(const Timing::Chrono &timeout, const HANDLE *hAbort);

    // wrapper using a socket address storage
    // note : available storage length shall be set prior by the user
    SOCKET accept(SocketAddress& addr, const Timing::Chrono &timeout, const HANDLE *hAbort);

    // Return first socket corresponding to the family given in argument
    SOCKET const getFamilySocket(const int family);

    // Return true if sockets are all valid, false otherwise
    bool areSocketsValid() const;
};

}
