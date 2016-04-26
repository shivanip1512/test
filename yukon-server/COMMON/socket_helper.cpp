#include "precompiled.h"

#include "socket_helper.h"
#include "critical_section.h"
#include "guard.h"
#include "timing_util.h"
#include "win_helper.h"
#include "loggable.h"
#include "dlldefs.h"

#include <boost/asio/ip/address.hpp>
#include "boost/lexical_cast.hpp"

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
    // [0000:0000:0000:0000:0000:0000:127.127.127.127]:65535
    // maximum length of address (IPV6) with port including null terminator = 54

    char  str[54];              // array of byte that can contain the largest address (IPV6) with port
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
    // 0000:0000:0000:0000:0000:0000:127.127.127.127
    // maximum length of address (IPV6) including null terminator = 46

    char host[46]; // array of byte that can contain the largest IP address (IPV6)

    if (getnameinfo(addr, addrlen, host, sizeof(host), 0, 0, NI_NUMERICHOST) != 0)
    {
        return std::string();   // on error, return an empty string
    }

    return host;
}

//-----------------------------------------------------------------------------
//  Format a host name (or an ip address) and a port into a string
//-----------------------------------------------------------------------------
IM_EX_CTIBASE std::string formatHostAndPort(const std::string& host, u_short port)
{
    std::ostringstream ostr;

    const boost::asio::ip::address ipAddress = boost::asio::ip::address::from_string(host);

    if (ipAddress.is_v6())
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

    return getIpAddressFromSocketAddress(&_addr.sa, _addrlen);
}

// get the 16-bit port number from network byte order
u_short SocketAddress::getPort() const
{
    return ntohs(_addr.sa_in.sin_port);
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
bool SocketAddress::compare(const SocketAddress& ref)
{
    // make sure length is not 0 and is similar
    if (!_addrlen || _addrlen != ref._addrlen)
    {
        return false;
    }

    // only compare addresses of similar families
    if (_addr.sa.sa_family != ref._addr.sa.sa_family)
    {
        return false;
    }

    switch (_addr.sa.sa_family)
    {
    case AF_INET:
        {
            return _addr.sa_in.sin_port == ref._addr.sa_in.sin_port &&
                memcmp(&_addr.sa_in.sin_addr, &ref._addr.sa_in.sin_addr, sizeof(IN_ADDR)) == 0;
        }
    case AF_INET6:
        {
            return _addr.sa_in6.sin6_port == ref._addr.sa_in6.sin6_port &&
                memcmp(&_addr.sa_in6.sin6_addr, &ref._addr.sa_in6.sin6_addr, sizeof(IN6_ADDR)) == 0;
        }
    }

    return false;
}

bool SocketAddress::operator==(const SocketAddress& ref)
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

IM_EX_CTIBASE AddrInfo::~AddrInfo()
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
    ADDRINFOA hints = {};

    // Set requirements
    hints.ai_family = AF_INET6;                   // IPv4 or IPv6
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
    return makeTcpClientSocketAddress(nodename, boost::lexical_cast<std::string>(nport));
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
    return makeTcpServerSocketAddress(boost::lexical_cast<std::string>(nport));
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
    return makeUdpClientSocketAddress(nodename, boost::lexical_cast<std::string>(nport));
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
    return makeUdpServerSocketAddress(boost::lexical_cast<std::string>(nport));
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

// Shutdown and close a vector sockets
void ServerSockets::shutdownAndClose(SocketsVec &sockets)
{
    for each(SOCKET s in sockets)
    {
        if (s != INVALID_SOCKET)
        {
            shutdown(s, SD_BOTH);
            closesocket(s);
        }
    }
    sockets.clear();
}

// Shutdown and close all sockets
// Note: sockets can still be (re)created afterwards
void ServerSockets::shutdownAndClose()
{
    CtiLockGuard<CtiCriticalSection> guard(_socketsMux);
    shutdownAndClose(_sockets);
    _socketsEventsManager.interruptBlockingWait();
}

int ServerSockets::getLastError() const
{
    return _lastError;
}

// Returns a copy of the current sockets
ServerSockets::SocketsVec ServerSockets::getSockets() const
{
    CtiLockGuard<CtiCriticalSection> guard(_socketsMux);
    return _sockets; // return a copy
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

    SocketsVec sockets;

    while (p_ai != NULL)
    {
        SOCKET s_new = ::socket(p_ai->ai_family, p_ai->ai_socktype, p_ai->ai_protocol);
        if (s_new == INVALID_SOCKET)
        {
            _lastError = WSAGetLastError();
            shutdownAndClose(sockets);
            throw SocketException("socket creation has failed", _lastError);
        }

        sockets.push_back(s_new);
        p_ai = p_ai->ai_next;
    }

    {
        CtiLockGuard<CtiCriticalSection> guard(_socketsMux);
        _sockets = sockets;
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

// Return first socket corresponding to the family given in argument
SOCKET const ServerSockets::getFamilySocket(const int family)
{
    SocketsVec sockets = getSockets();
    for each(SOCKET s in sockets)
    {
        SocketAddress addr(SocketAddress::STORAGE_SIZE);
        if (::getsockname(s, &addr._addr.sa, &addr._addrlen) == SOCKET_ERROR)
        {
            _lastError = WSAGetLastError();
            return INVALID_SOCKET;
        }

        if (addr._addr.sa.sa_family == family)
        {
            return s;
        }
    }

    return INVALID_SOCKET;
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