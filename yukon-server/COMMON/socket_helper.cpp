#include "precompiled.h"

#include "socket_helper.h"
#include "critical_section.h"
#include "guard.h"
#include "timing_util.h"
#include "win_helper.h"
#include "loggable.h"
#include "dlldefs.h"

#include <boost/range/adaptor/map.hpp>

//-----------------------------------------------------------------------------
//  Server socket wrapper
//  listen to sockets in parallel for using with different family types
//-----------------------------------------------------------------------------

namespace Cti {

//-----------------------------------------------------------------------------
//  Socket exception thrown by ServerSockets
//-----------------------------------------------------------------------------
SocketException::SocketException(const std::string& desc) :
    _desc(desc),
    _errorCode(0)
{
}

SocketException::SocketException(const std::string& desc, int errorCode) :
    _desc(desc + ": " + std::to_string(errorCode) + " -> " + getSystemErrorMessage(errorCode)),
    _errorCode(errorCode)
{
}

const char* SocketException::what() const throw()
{
    return _desc.c_str();
}

int SocketException::getErrorCode() const
{
    return _errorCode;
}

SocketAbortException::SocketAbortException() : SocketException("WaitForSocketEvent has aborted")
{
}

//-----------------------------------------------------------------------------
//  Convert SOCKADDR to string using WSAAddressToString()
//-----------------------------------------------------------------------------
IM_EX_CTIBASE std::string socketAddressToString(const SOCKADDR *addr, int addrlen)
{
    char  str[INET6_ADDRSTRLEN]; // array of byte that can contain the largest address (IPV6) with port
    DWORD strlen = sizeof(str);  // in case of a WSAEFAULT error, this parameter will contain the required length

    if (WSAAddressToString((LPSOCKADDR)addr, (DWORD)addr, NULL, str, &strlen) == SOCKET_ERROR
        || strlen == 0)
    {
        return std::string();   // on error, return an empty string
    }

    return std::string(str, strlen - 1);
}

//-----------------------------------------------------------------------------
//  Get string IP Address from SOCKADDR using getNameInfo()
//-----------------------------------------------------------------------------
IM_EX_CTIBASE std::string getIpAddressFromSocketAddress(const SOCKADDR *addr, int addrlen)
{
    SOCKADDR_IN v4mapped{};

    char host[INET6_ADDRSTRLEN]; // array of byte that can contain the largest IP address (IPV6)
    if (addr->sa_family == AF_INET)
    {
        SOCKADDR_IN *addr4 = (SOCKADDR_IN *)addr;

        CTILOG_DEBUG(dout, "getIpAddressFromSocketAddress("
            << addr4->sin_addr.S_un.S_un_b.s_b1 << "."
            << addr4->sin_addr.S_un.S_un_b.s_b2 << "."
            << addr4->sin_addr.S_un.S_un_b.s_b3 << "."
            << addr4->sin_addr.S_un.S_un_b.s_b4 << ":"
            << addr4->sin_port
        );
    }
    else
    {
        SOCKADDR_IN6 *addr6 = (SOCKADDR_IN6 *)addr;

        CTILOG_DEBUG(dout, "getIpAddressFromSocketAddress(["
            << addr6->sin6_addr.u.Word[0] << ":"
            << addr6->sin6_addr.u.Word[1] << ":"
            << addr6->sin6_addr.u.Word[2] << ":"
            << addr6->sin6_addr.u.Word[3] << ":"
            << addr6->sin6_addr.u.Word[4] << ":"
            << addr6->sin6_addr.u.Word[5] << ":"
            << addr6->sin6_addr.u.Word[6] << ":"
            << addr6->sin6_addr.u.Word[7] << "]:"
            << addr6->sin6_port
        );

        //  If the address is an IPv4-mapped address...
        if( IN6_IS_ADDR_V4MAPPED(&addr6->sin6_addr) )
        {
            v4mapped.sin_family = AF_INET;
            v4mapped.sin_addr.S_un.S_un_b.s_b1 = addr6->sin6_addr.u.Byte[12];
            v4mapped.sin_addr.S_un.S_un_b.s_b2 = addr6->sin6_addr.u.Byte[13];
            v4mapped.sin_addr.S_un.S_un_b.s_b3 = addr6->sin6_addr.u.Byte[14];
            v4mapped.sin_addr.S_un.S_un_b.s_b4 = addr6->sin6_addr.u.Byte[15];
            
            //  override addr and addrlen with the v4 version
            addr = reinterpret_cast<const SOCKADDR *>(&v4mapped);
            addrlen = sizeof(SOCKADDR_IN);
        }
    }

    if (getnameinfo(addr, addrlen, host, sizeof(host), 0, 0, NI_NUMERICHOST) != 0)
    {
            CTILOG_DEBUG(dout, "getIpAddressFromSocketAddress returned ''")
            return std::string();   // on error, return an empty string
    }

    CTILOG_DEBUG(dout, "getIpAddressFromSocketAddress returned " << host);
    return host;
}

//-----------------------------------------------------------------------------
//  Format a host name (or an ip address) and a port into a string
//-----------------------------------------------------------------------------
IM_EX_CTIBASE std::string formatHostAndPort(const std::string& host, u_short port)
{
    std::ostringstream ostr;

    //  Contains a colon, so it's probably IPv6 - bracket the host as per RFC 3986: https://serverfault.com/a/205794
    if( host.find(':', 0) != std::string::npos )
    {
        ostr << "[" << host << "]:" << port;
    }
    else
    {
        ostr << host << ":" << port;
    }

    return ostr.str();
}

namespace Detail {

/**
* Helper class that contains sockets events associated with the network events.
* This class is called by SocketsEventsManager::waitForEvent()
*/
SocketsEvent::SocketsEvent(const std::vector<SOCKET> &sockets, const long lNetworkEvents)
    : _sockets(sockets), _lNetworkEvents(lNetworkEvents), _hEvent(WSACreateEvent())
{
    if (_hEvent == WSA_INVALID_EVENT)
    {
        throw SocketException("WSACreateEvent has failed", WSAGetLastError());
    }

    for each(SOCKET s in _sockets)
    {
        // The WSAEventSelect function automatically sets socket s to nonblocking mode, regardless of the value of
        // lNetworkEvents.
        if (WSAEventSelect(s, _hEvent, _lNetworkEvents))
        {
            throw SocketException("WSAEventSelect has failed", WSAGetLastError());
        }
    }
}

SocketsEvent::~SocketsEvent()
{
    // clear the event record associated with each socket via a call to WSAEventSelect with lNetworkEvents set to
    // zero and the hEventObject parameter set to NULL.
    for each(SOCKET s in _sockets)
    {
        WSAEventSelect(s, NULL, 0);
    }

    // close the handles
    WSACloseEvent(_hEvent);
}

void SocketsEvent::triggerEvent() const
{
    if (!WSASetEvent(_hEvent))
    {
        throw SocketException("WSASetEvent has failed", WSAGetLastError());
    }
}

WSAEVENT SocketsEvent::getHandle() const
{
    return _hEvent;
}

SOCKET SocketsEvent::findActiveSocket() const
{
    WSANETWORKEVENTS NetworkEvents;

    for each(SOCKET s in _sockets)
    {
        if (WSAEnumNetworkEvents(s, _hEvent, &NetworkEvents))
        {
            throw SocketException("WSAEnumNetworkEvents has failed", WSAGetLastError());
        }

        if (NetworkEvents.lNetworkEvents & _lNetworkEvents)
        {
            return s;
        }
    }

    return INVALID_SOCKET;
}

} // namespace Detail

/**
*  Manages sockets events and provides waitForEvent() and interruptBlockingWait()
*  used by StreamSocketConnection
*/
SocketsEventsManager::ScopedEvent::ScopedEvent(SocketsEventsManager &manager, SocketsEvent *newEvent) : _manager(manager)
{
    CtiLockGuard<CtiCriticalSection> guard(_manager._mux);
    _eventItr = _manager._eventList.insert(_manager._eventList.end(), newEvent);
}

SocketsEventsManager::ScopedEvent::~ScopedEvent()
{
    try
    {
        CtiLockGuard<CtiCriticalSection> guard(_manager._mux);
        _manager._eventList.erase(_eventItr);
    }
    catch (...)
    {
    }
}

const SocketsEventsManager::SocketsEvent* SocketsEventsManager::ScopedEvent::operator->() const
{
    return &*_eventItr;
}


SOCKET SocketsEventsManager::waitForEvent(const std::vector<SOCKET> &sockets, long lNetworkEvents, const Timing::Chrono &timeout, const HANDLE *hAbort)
{
    std::vector<HANDLE> waitEvents;

    if (hAbort)
    {
        waitEvents.push_back(*hAbort);
    }

    // The ScopedEvent will remove itself from the event list and self destruct when it goes out-of-scope
    const ScopedEvent event(*this, new SocketsEvent(sockets, lNetworkEvents));
    waitEvents.push_back(event->getHandle());

    const DWORD nCount = waitEvents.size();
    const DWORD waitMillis = timeout ? timeout.milliseconds() : INFINITE;
    const DWORD waitResult = WaitForMultipleObjects(nCount, &waitEvents[0], false, waitMillis);

    switch (waitResult)
    {
    case WAIT_OBJECT_0:
        {
            if (hAbort)
            {
                throw SocketAbortException(); // aborted !
            }
        }
    case WAIT_OBJECT_0 + 1:
        {
            SOCKET sock = event->findActiveSocket();
            if (sock == INVALID_SOCKET)
            {
                throw SocketException("WaitForMultipleObjects was interrupted");
            }

            return sock;
        }
    case WAIT_TIMEOUT:
        {
            return INVALID_SOCKET; // timeout !
        }
    case WAIT_FAILED:
        {
            throw SocketException("WaitForMultipleObjects has failed", GetLastError());
        }
    default:
        {
            throw SocketException("WaitForMultipleObjects returned an unexpected value");
        }
    }
}

bool SocketsEventsManager::waitForEvent(SOCKET socket, long lNetworkEvents, const Timing::Chrono &timeout, const HANDLE *hAbort)
{
    return waitForEvent(std::vector<SOCKET>(1, socket), lNetworkEvents, timeout, hAbort) != INVALID_SOCKET;
}

void SocketsEventsManager::interruptBlockingWait()
{
    CtiLockGuard<CtiCriticalSection> guard(_mux);
    for each(const SocketsEvent& event in _eventList)
    {
        event.triggerEvent();
    }
}

//-----------------------------------------------------------------------------
//  Manages a socket address of IPv4 and IPv6 family
//-----------------------------------------------------------------------------

// default construct initializes addr length to zero
// if the socket address is needed to receive data, i can be initialize
SocketAddress::SocketAddress(int addrlen) :
    _addrlen(addrlen)
{
    assert(_addrlen <= STORAGE_SIZE);
}

// copy constructor
SocketAddress::SocketAddress(const SocketAddress& ref) :
    _addrlen(ref._addrlen)
{
    assert(_addrlen <= STORAGE_SIZE);
    memcpy(&_addr, &ref._addr, _addrlen);
}

// constructor that can use with ADDRINFO results
SocketAddress::SocketAddress(const SOCKADDR* paddr, int addrlen) :
    _addrlen(addrlen)
{
    assert(_addrlen <= STORAGE_SIZE);
    memcpy(&_addr, paddr, _addrlen);
}

SocketAddress::~SocketAddress()
{
}

void SocketAddress::reset(int addrlen)
{
    _addrlen = addrlen;
    assert(_addrlen <= STORAGE_SIZE);
}

// convert all components of the sockaddr into a human readable string
// NOTE: use only for display (logging)
std::string SocketAddress::toString() const
{
    if (!_addrlen)
    {
        return std::string(); // if address length is null, return empty string
    }

    return socketAddressToString(&_addr.sa, _addrlen);
}

// get the IP address (string) from the sockaddr
std::string SocketAddress::getIpAddress() const
{
    if (!_addrlen)
    {
        return std::string(); // if address length is null, return empty string
    }

    std::string addr = getIpAddressFromSocketAddress(&_addr.sa, _addrlen);
    CTILOG_DEBUG(dout, "getIPAddress returned " << addr);
    return addr;
}

// get the 16-bit port number from network byte order
u_short SocketAddress::getPort() const
{
    return ntohs(_addr.sa_in.sin_port);
}

// set the 16-bit port number from network byte order
void SocketAddress::setPort(short port)
{
    _addr.sa_in.sin_port=htons(port);
}

SocketAddress& SocketAddress::operator=(const SocketAddress& ref)
{
    _addrlen = ref._addrlen;
    assert(_addrlen <= STORAGE_SIZE);
    memcpy(&_addr, &ref._addr, _addrlen);
    return *this;
}

// Return true if value has content
bool SocketAddress::isSet() const
{
    return _addrlen;
}

// Return true if socket addresses have similar family, port and IP address
bool SocketAddress::compare(const SocketAddress& ref) const
{
    switch (_addr.sa.sa_family)
    {
    case AF_INET:
        {
            if (ref._addr.sa.sa_family == AF_INET6)
            {
                if (IN6_IS_ADDR_V4MAPPED(&ref._addr.sa_in6.sin6_addr))
                {
                    CTILOG_DEBUG(dout, "ref is IN6_IS_ADDR_V4MAPPED");
                    if (_addr.sa_in.sin_addr.S_un.S_un_b.s_b1 == ref._addr.sa_in6.sin6_addr.u.Byte[12] &&
                        _addr.sa_in.sin_addr.S_un.S_un_b.s_b2 == ref._addr.sa_in6.sin6_addr.u.Byte[13] &&
                        _addr.sa_in.sin_addr.S_un.S_un_b.s_b3 == ref._addr.sa_in6.sin6_addr.u.Byte[14] &&
                        _addr.sa_in.sin_addr.S_un.S_un_b.s_b4 == ref._addr.sa_in6.sin6_addr.u.Byte[15] &&
                        _addr.sa_in.sin_port == ref._addr.sa_in6.sin6_port )
                    {
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
            return _addr.sa_in.sin_port == ref._addr.sa_in.sin_port &&
                memcmp(&_addr.sa_in.sin_addr, &ref._addr.sa_in.sin_addr, sizeof(IN_ADDR)) == 0;
        }
    case AF_INET6:
        {
            if (ref._addr.sa.sa_family == AF_INET)
            {
                if (IN6_IS_ADDR_V4MAPPED(&_addr.sa_in6.sin6_addr))
                {
                    CTILOG_DEBUG(dout, "this is IN6_IS_ADDR_V4MAPPED");
                    if (ref._addr.sa_in.sin_addr.S_un.S_un_b.s_b1 == _addr.sa_in6.sin6_addr.u.Byte[12] &&
                        ref._addr.sa_in.sin_addr.S_un.S_un_b.s_b2 == _addr.sa_in6.sin6_addr.u.Byte[13] &&
                        ref._addr.sa_in.sin_addr.S_un.S_un_b.s_b3 == _addr.sa_in6.sin6_addr.u.Byte[14] &&
                        ref._addr.sa_in.sin_addr.S_un.S_un_b.s_b4 == _addr.sa_in6.sin6_addr.u.Byte[15] &&
                        ref._addr.sa_in.sin_port == _addr.sa_in6.sin6_port )
                    {
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }

            return _addr.sa_in6.sin6_port == ref._addr.sa_in6.sin6_port &&
                memcmp(&_addr.sa_in6.sin6_addr, &ref._addr.sa_in6.sin6_addr, sizeof(IN6_ADDR)) == 0;
        }
    }

    return false;
}

bool SocketAddress::operator==(const SocketAddress& ref) const
{
    return compare(ref);
}

//-----------------------------------------------------------------------------
//  Struct to manage ADDRINFOA generated by getaddrinfo
//-----------------------------------------------------------------------------
AddrInfo::AddrInfo(PCSTR pNodeName, PCSTR pServiceName, const ADDRINFOA *pHints)
{
    _p_ai = NULL;
    _err_code = getaddrinfo(pNodeName, pServiceName, pHints, &_p_ai);
}

AddrInfo::~AddrInfo()
{
    if (_p_ai)
    {
        freeaddrinfo(_p_ai);
    }
}

int AddrInfo::getError()
{
    return _err_code;
}

// access member of the first address info found
PADDRINFOA AddrInfo::operator->() const
{
    assert(_p_ai != NULL);
    return _p_ai;
}

AddrInfo::operator bool() const
{
    return (_p_ai != NULL && _err_code == 0);
}

void AddrInfo::reset(PCSTR pNodeName, PCSTR pServiceName, const ADDRINFOA *pHints)
{
    if (_p_ai)
    {
        freeaddrinfo(_p_ai);
        _p_ai = NULL;
    }

    _err_code = getaddrinfo(pNodeName, pServiceName, pHints, &_p_ai);
}

PADDRINFOA AddrInfo::release()
{
    PADDRINFOA tmp = _p_ai;
    _p_ai = NULL;
    return tmp;
}

// convert all components of the sockaddr into a human readable string
// NOTE: use only for display (logging)
std::string AddrInfo::toString() const
{
    if (!_p_ai)
    {
        return std::string(); // return empty string
    }

    return socketAddressToString(_p_ai->ai_addr, _p_ai->ai_addrlen);
}

PADDRINFOA AddrInfo::get() const
{
    return _p_ai;
}

//-----------------------------------------------------------------------------
//  Returns an addrinfo struct
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeSocketAddress(const char *nodename, const char* servname, const int socktype, const int protocol, const int flags)
{
    CTILOG_DEBUG(dout, "Finding addr for " << (nodename ? nodename : "listener") << " for " << servname << " socktype " << socktype << " protocol " << protocol);
    ADDRINFOA hints = {};

    // Set requirements
    if (nodename == 0)
    {                                                   // Listening socket
        hints.ai_family = AF_INET6;                     // accept either IPv4 or IPv6
    }
    else
    {                                                   // connect socket
        hints.ai_family = AF_UNSPEC;                    // IPv4 or IPv6 depending on connect string
    }
    hints.ai_socktype = socktype;                   // SOCK_STREAM (tcp) or SOCK_DGRAM (udp)
    hints.ai_protocol = protocol;                   // IPPROTO_TCP or IPPROTO_UDP
    hints.ai_flags = flags;                      // If node is not NULL, then the AI_PASSIVE flag is ignored.

    return AddrInfo(nodename, servname, &hints);
}

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for TCP client
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeTcpClientSocketAddress(const std::string &nodename, const std::string &servname)
{
    return makeSocketAddress(nodename.c_str(), servname.c_str(), SOCK_STREAM, IPPROTO_TCP);
};

IM_EX_CTIBASE AddrInfo makeTcpClientSocketAddress(const std::string &nodename, const unsigned short nport)
{
    return makeTcpClientSocketAddress(nodename, std::to_string(nport));
};

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for TCP server
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeTcpServerSocketAddress(const std::string &servname)
{
    return makeSocketAddress(NULL, servname.c_str(), SOCK_STREAM, IPPROTO_TCP, AI_PASSIVE);
};

IM_EX_CTIBASE AddrInfo makeTcpServerSocketAddress(const unsigned short nport)
{
    return makeTcpServerSocketAddress(std::to_string(nport));
};

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for UDP client
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeUdpClientSocketAddress(const std::string &nodename, const std::string &servname)
{
    return makeSocketAddress(nodename.c_str(), servname.c_str(), SOCK_DGRAM, IPPROTO_UDP);
};

IM_EX_CTIBASE AddrInfo makeUdpClientSocketAddress(const std::string &nodename, const unsigned short nport)
{
    return makeUdpClientSocketAddress(nodename, std::to_string(nport));
};

//-----------------------------------------------------------------------------
//  Returns AddrInfo struct for UDP server
//-----------------------------------------------------------------------------
IM_EX_CTIBASE AddrInfo makeUdpServerSocketAddress(const std::string &servname)
{
    return makeSocketAddress(NULL, servname.c_str(), SOCK_DGRAM, IPPROTO_UDP, AI_PASSIVE);
};

IM_EX_CTIBASE AddrInfo makeUdpServerSocketAddress(const unsigned short nport)
{
    return makeUdpServerSocketAddress(std::to_string(nport));
};

ServerSockets::ServerSockets() : _lastError(0) {}

ServerSockets::~ServerSockets()
{
    try
    {
        shutdownAndClose();
    }
    catch (...)
    {
    }
}

// Shutdown and close a collection of sockets
void ServerSockets::shutdownAndClose(SocketDescriptors& descriptors)
{
    for( const auto desc : descriptors )
    {
        auto s = desc.first;
        if (s != INVALID_SOCKET)
        {
            shutdown(s, SD_BOTH);
            closesocket(s);
        }
    }
    descriptors.clear();
}

// Shutdown and close all sockets
// Note: sockets can still be (re)created afterwards
void ServerSockets::shutdownAndClose()
{
    CtiLockGuard<CtiCriticalSection> guard(_descriptorsMux);
    shutdownAndClose(_descriptors);
    _socketsEventsManager.interruptBlockingWait();
}

int ServerSockets::getLastError() const
{
    return _lastError;
}

// Returns a copy of the current sockets
auto ServerSockets::getSockets() const -> SocketsVec 
{
    CtiLockGuard<CtiCriticalSection> guard(_descriptorsMux);
    return boost::copy_range<SocketsVec>(_descriptors | boost::adaptors::map_keys);
}

// Returns a copy of the current socket descriptors
auto ServerSockets::getSocketDescriptors() const -> SocketDescriptors
{
    CtiLockGuard<CtiCriticalSection> guard(_descriptorsMux);
    return _descriptors;
}

// creates sockets from addr info
void ServerSockets::createSockets(PADDRINFOA p_ai)
{
    // shutdown all sockets handle before continuing
    shutdownAndClose();

    if (p_ai == NULL)
    {
        throw SocketException("PADDRINFOA is NULL");
    }

    SocketDescriptors descriptors;

    while (p_ai != NULL)
    {
        SOCKET s_new = ::socket(p_ai->ai_family, p_ai->ai_socktype, p_ai->ai_protocol);
        if (s_new == INVALID_SOCKET)
        {
            _lastError = WSAGetLastError();
            shutdownAndClose(descriptors);
            throw SocketException("socket creation has failed", _lastError);
        }
        CTILOG_DEBUG(dout, "Created socket " << s_new << " for family " << p_ai->ai_family << " type " << p_ai->ai_socktype << " protocol " << p_ai->ai_protocol);
        descriptors.emplace(s_new, SocketDescriptor{ p_ai->ai_family, p_ai->ai_socktype, p_ai->ai_protocol });
        p_ai = p_ai->ai_next;
    }

    {
        CtiLockGuard<CtiCriticalSection> guard(_descriptorsMux);
        _descriptors.swap(descriptors);
    }

    DWORD v6only = 0;
    setOption(IPPROTO_IPV6, IPV6_V6ONLY, (char *)&v6only, sizeof v6only);

}

// Set option on all sockets
void ServerSockets::setOption(int level, int optname, const char *optval, int optlen)
{
    SocketsVec sockets = getSockets(); // get a copy of the sockets
    if (sockets.empty())
    {
        throw SocketException("No sockets found");
    }

    for each(SOCKET s in sockets)
    {
        if (::setsockopt(s, level, optname, optval, optlen) == SOCKET_ERROR)
        {
            _lastError = WSAGetLastError();
            shutdownAndClose();
            throw SocketException("setsockopt has failed: ", _lastError);
        }
    }
}

// Set ioMode on all sockets
void ServerSockets::setIOMode(long cmd, u_long *argp)
{
    SocketsVec sockets = getSockets(); // get a copy of the sockets
    if (sockets.empty())
    {
        throw SocketException("No sockets found");
    }

    for each(SOCKET s in sockets)
    {
        if (::ioctlsocket(s, cmd, argp) == SOCKET_ERROR)
        {
            _lastError = WSAGetLastError();
            shutdownAndClose();
            throw SocketException("ioctlsocket has failed", _lastError);
        }
    }
}

// Binds all socket to the corresponding address info
void ServerSockets::bind(PADDRINFOA p_ai)
{
    SocketsVec sockets = getSockets(); // get a copy of the sockets
    if (sockets.empty())
    {
        throw SocketException("No sockets found");
    }

    for each(SOCKET s in sockets)
    {
        SOCKADDR* addr = NULL;
        int addrlen = 0;

        if (p_ai)
        {
            addr = p_ai->ai_addr;
            addrlen = p_ai->ai_addrlen;
            p_ai = p_ai->ai_next;
        }

        if (::bind(s, addr, addrlen) == SOCKET_ERROR)
        {
            _lastError = WSAGetLastError();
            shutdownAndClose();
            throw SocketException("bind has failed", _lastError);
        }
    }
}

// Call listen on all sockets
void ServerSockets::listen(int backlog)
{
    SocketsVec sockets = getSockets(); // get a copy of the sockets
    if (sockets.empty())
    {
        throw SocketException("No sockets found");
    }

    for each(SOCKET s in sockets)
    {
        if (::listen(s, backlog) == SOCKET_ERROR)
        {
            _lastError = WSAGetLastError();
            shutdownAndClose();
            throw SocketException("listen has failed", _lastError);
        }
    }
}

// Accept first socket connection in blocking or non-blocking mode
SOCKET ServerSockets::accept(SOCKADDR *addr, int *addrlen, const Timing::Chrono &timeout, const HANDLE *hAbort)
{
    SocketsVec sockets = getSockets(); // get a copy of the sockets
    if (sockets.empty())
    {
        _lastError = WSAENOTSOCK;
        return INVALID_SOCKET;
    }

    SOCKET listenSocket = INVALID_SOCKET;

    try
    {
        listenSocket = _socketsEventsManager.waitForEvent(sockets, FD_ACCEPT | FD_CLOSE, timeout, hAbort);
    }
    catch (const SocketAbortException&)
    {
        return INVALID_SOCKET;
    }
    catch (const SocketException& ex)
    {
        _lastError = ex.getErrorCode();
        shutdownAndClose();
        return INVALID_SOCKET;
    }

    if (listenSocket == INVALID_SOCKET)
    {
        return INVALID_SOCKET; // timeout
    }

    SOCKET s_out = ::accept(listenSocket, addr, addrlen);
    if (s_out == INVALID_SOCKET)
    {
        _lastError = WSAGetLastError();
        if (_lastError != WSAEWOULDBLOCK)
        {
            shutdownAndClose();
        }
    }

    return s_out;
}

// wrapper for when return address is unused
SOCKET ServerSockets::accept(const Timing::Chrono &timeout, const HANDLE *hAbort)
{
    return accept(NULL, NULL, timeout, hAbort);
}

// wrapper using a socket address storage
// note : available storage length shall be set prior by the user
SOCKET ServerSockets::accept(SocketAddress& addr, const Timing::Chrono &timeout, const HANDLE *hAbort)
{
    return accept(&addr._addr.sa, &addr._addrlen, timeout, hAbort);
}

// Return true if sockets are all valid, false otherwise
bool ServerSockets::areSocketsValid() const
{
    SocketsVec sockets = getSockets();
    if (sockets.empty())
    {
        return false;
    }

    for each(SOCKET s in sockets)
    {
        if (s == INVALID_SOCKET)
        {
            return false;
        }
    }

    return true;
}

}

IM_EX_CTIBASE std::ostream& operator<< (std::ostream& o, const Cti::SocketAddress& sa)
{
    return o << sa.getIpAddress();
}

