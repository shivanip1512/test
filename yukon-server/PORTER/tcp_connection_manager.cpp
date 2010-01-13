#include "yukon.h"

#include "tcp_connection_manager.h"

#include "cparms.h"
#include "logger.h"

#include <boost/scoped_array.hpp>

using namespace std;

namespace Cti    {
namespace Porter {


void TcpConnectionManager::connect( const long id, const unsigned long ip, const unsigned short port )
{
    if( connection *c = findExistingConnection(id) )
    {
        if( c->ip   == ip &&
            c->port == port )
        {
            return;
        }

        disconnect(id);
    }

    createConnection(id, ip, port);
}


TcpConnectionManager::connection *TcpConnectionManager::findExistingConnection(const long id)
{
    if( _disabled )
    {
        connection_itr d_itr = _stashed.find(id);

        if( d_itr != _stashed.end() )
        {
            return &(d_itr->second);
        }
        else
        {
            return 0;
        }
    }

    connection_itr e_itr = _established.find(id);

    if( e_itr != _established.end() )
    {
        return &(e_itr->second);
    }

    pending_itr p_itr = _pending.find(id);

    if( p_itr != _pending.end() )
    {
        return &(p_itr->second);
    }

    return 0;
}


void TcpConnectionManager::createConnection( const long id, const unsigned long ip, const short port )
{
    pending p(ip, port);

    //  this allows the client to call connect() when the connection is disabled
    if( _disabled )
    {
        _stashed.insert(make_pair(id, p));
    }
    else if( connect(p) )
    {
        _pending.insert(make_pair(id, p));
    }
}


bool TcpConnectionManager::connect(pending &p)
{
    SOCKET s = socket(AF_INET, SOCK_STREAM, 0);

    if( s == INVALID_SOCKET)
    {
        reportSocketError("socket", __FUNCTION__, __FILE__, __LINE__);

        return false;
    }

    unsigned long nonblocking = 1;
    if( ioctlsocket(s, FIONBIO, &nonblocking) )
    {
        reportSocketError("ioctlsocket", __FUNCTION__, __FILE__, __LINE__);

        closesocket(s);

        return false;
    }

    const u_short port = p.port;
    const u_long  ip   = p.ip;

    sockaddr_in device_address = { AF_INET, htons(port), *(in_addr*)&ip, 0 };

    if( ::connect(s, reinterpret_cast<sockaddr *>(&device_address), sizeof(device_address)) == SOCKET_ERROR)
    {
        if( WSAGetLastError() != WSAEWOULDBLOCK )
        {
            reportSocketError("connect", __FUNCTION__, __FILE__, __LINE__);

            closesocket(s);

            return false;
        }
    }

    p.sock    = s;
    p.timeout = CtiTime::now() + gConfigParms.getValueAsULong("PORTER_TCP_CONNECT_TIMEOUT", 15);

    return true;
}


void TcpConnectionManager::disconnect(long id)
{
    if( _disabled )
    {
        _stashed.erase(id);

        return;
    }

    connection_itr c_itr = _established.find(id);

    if( c_itr != _established.end() )
    {
        disconnectEstablished(c_itr->second);

        _established.erase(c_itr);

        return;
    }

    pending_itr p_itr = _pending.find(id);

    if( p_itr != _pending.end() )
    {
        disconnectPending(p_itr->second);

        _pending.erase(p_itr);
    }
}


void TcpConnectionManager::disconnectAll()
{
    connection_itr c_itr = _established.begin();

    for( ; c_itr != _established.end(); ++c_itr )
    {
        disconnectEstablished(c_itr->second);
    }

    _established.clear();

    pending_itr p_itr = _pending.begin();

    for( ; p_itr != _pending.end(); ++p_itr )
    {
        disconnectPending(p_itr->second);
    }

    _pending.clear();
}


void TcpConnectionManager::disconnectEstablished(connection &c)
{
    if( shutdown(c.sock, 2) )  //  SD_BOTH
    {
        reportSocketError("shutdown", __FUNCTION__, __FILE__, __LINE__);
    }

    if( closesocket(c.sock) )
    {
        reportSocketError("close", __FUNCTION__, __FILE__, __LINE__);
    }

    c.sock = INVALID_SOCKET;
}


void TcpConnectionManager::disconnectPending(pending &p)
{
    if( closesocket(p.sock) )
    {
        reportSocketError("close", __FUNCTION__, __FILE__, __LINE__);
    }

    p.sock = INVALID_SOCKET;
}


void TcpConnectionManager::checkPendingConnections(id_set &connected, id_set &errors)
{
    for( pending_itr itr = _pending.begin(); itr != _pending.end(); )
    {
        fd_set writeable_sockets;

        vector<pending_itr> pending_block;

        FD_ZERO(&writeable_sockets);

        int socket_count = 0;

        //  check in blocks of up to FD_SETSIZE
        for( ; itr != _pending.end() && socket_count < FD_SETSIZE; ++itr, ++socket_count )
        {
            const pending &p = itr->second;

            pending_block.push_back(itr);

            FD_SET(p.sock, &writeable_sockets);
        }

        checkPendingConnectionBlock(writeable_sockets, pending_block, connected, errors);
    }
}


void TcpConnectionManager::checkPendingConnectionBlock(fd_set &writeable_sockets, vector<pending_itr> &pending_block, id_set &connected, id_set &errors)
{
    timeval tv = { 0, 1000 };  //  0 seconds + 1000 microseconds = 1 millisecond

    //  check to see if any of the sockets are writable - a writable socket means the connection has completed
    int ready_count = select(0, NULL, &writeable_sockets, NULL, &tv);

    if( ready_count == SOCKET_ERROR )
    {
        reportSocketError("select", __FUNCTION__, __FILE__, __LINE__);

        ready_count = 0;
    }

    const CtiTime Now;

    for each( pending_itr itr in pending_block )
    {
        const long &id = itr->first;
        pending    &p  = itr->second;

        if( ready_count && FD_ISSET(p.sock, &writeable_sockets) )
        {
            setConnectionOptions(p);

            connection c = p;

            _established.insert(make_pair(id, c));

            _pending.erase(itr);

            connected.insert(id);
        }
        else if( p.timeout > Now )
        {
            disconnectPending(p);

            errors.insert(id);

            connect(p);
        }
    }
}


void TcpConnectionManager::setConnectionOptions(const pending &p)
{
    //  none of these are fatal errors, so we'll just log and continue

    //  Make sure we time out on our writes after 5 seconds
    int socket_write_timeout = gConfigParms.getValueAsInt("PORTER_SOCKET_WRITE_TIMEOUT", 5);
    if( setsockopt(p.sock, SOL_SOCKET, SO_SNDTIMEO, reinterpret_cast<char *>(&socket_write_timeout), sizeof(socket_write_timeout)) )
    {
        reportSocketError("setsockopt", __FUNCTION__, __FILE__, __LINE__);
    }

    //  Turn on the keepalive timer
    int keepalive_timer = 1;
    if( setsockopt(p.sock, SOL_SOCKET, SO_KEEPALIVE, reinterpret_cast<char *>(&keepalive_timer), sizeof(keepalive_timer)) )
    {
        reportSocketError("setsockopt", __FUNCTION__, __FILE__, __LINE__);
    }

    //  enable a hard close - erases all pending outbound data, sends a reset to the other side
    linger l = {1, 0};
    if( setsockopt(p.sock, SOL_SOCKET, SO_LINGER, reinterpret_cast<char *>(&l), sizeof(l)) )
    {
        reportSocketError("setsockopt", __FUNCTION__, __FILE__, __LINE__);
    }
}



int TcpConnectionManager::send(const long id, const bytes &data)
{
    connection_itr itr = _established.find(id);

    if( itr == _established.end() )
    {
        return SOCKET_ERROR;
    }

    connection &c = itr->second;

    int bytes_sent = ::send(c.sock, &data.front(), data.size(), 0);

    if( bytes_sent == SOCKET_ERROR )
    {
        reportSocketError("send", __FUNCTION__, __FILE__, __LINE__);

        disconnectEstablished(c);

        createConnection(id, c.ip, c.port);

        _established.erase(itr);
    }

    return bytes_sent;
}


void TcpConnectionManager::reportSocketError(const string winsock_function_name, const char *method_name, const char *file, const int line)
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** Checkpoint - " << winsock_function_name << "() returned (" << WSAGetLastError() << ") in " << method_name << "() **** " << file << " (" << line << ")" << endl;
}


bool TcpConnectionManager::recv(id_set &ready, id_set &errors)
{
    connection_itr itr = _established.begin();

    while( itr != _established.end() )
    {
        int sockets;
        fd_set readable_sockets;
        vector<connection_itr> candidate_sockets;

        FD_ZERO(&readable_sockets);

        //  check in blocks of up to FD_SETSIZE
        for( sockets = 0; itr != _established.end() && sockets < FD_SETSIZE; ++itr )
        {
            const connection &c = itr->second;

            candidate_sockets.push_back(itr);

            FD_SET(c.sock, &readable_sockets);

            sockets++;
        }

        if( sockets > 0 )
        {
            timeval tv = { 0, 1000 };  //  0 seconds + 1000 microseconds = 1 millisecond

            int ready_count = select(sockets, &readable_sockets, NULL, NULL, &tv);

            if( ready_count == SOCKET_ERROR )
            {
                reportSocketError("select", __FUNCTION__, __FILE__, __LINE__);
            }
            else if( ready_count > 0 )
            {
                for each( connection_itr c_itr in candidate_sockets )
                {
                    const long &id = c_itr->first;
                    connection &c  = c_itr->second;

                    if( FD_ISSET(c.sock, &readable_sockets) )
                    {
                        if( readInput(c) )
                        {
                            ready.insert(id);
                        }
                        else
                        {
                            errors.insert(id);

                            disconnectEstablished(c);

                            createConnection(id, c.ip, c.port);

                            _established.erase(c_itr);
                        }
                    }
                }
            }
        }
    }

    return !ready.empty() && !errors.empty();
}


bool TcpConnectionManager::readInput(connection &c)
{
    u_long bytes_available;

    if( ioctlsocket(c.sock, FIONREAD, &bytes_available) )
    {
        reportSocketError("ioctlsocket", __FUNCTION__, __FILE__, __LINE__);
        bytes_available = 0;
    }

    //  we got in here because the socket reported it was readable - if we get 0 bytes, the connection must be closed
    if( !bytes_available )
    {
        return false;
    }

    boost::scoped_array<char> recv_buf(new char[bytes_available]);

    int bytes_read = ::recv(c.sock, recv_buf.get(), bytes_available, 0);

    //  we got in here because the socket reported it was readable - if we get 0 bytes, the connection must be closed
    if( !bytes_read )
    {
        return false;
    }
    else
    {
        copy(recv_buf.get(),
             recv_buf.get() + bytes_read,
             back_inserter(c.stream));
    }

    return true;
}


bool TcpConnectionManager::isConnected( const long id ) const
{
    return _established.find(id) != _established.end();
}


bool TcpConnectionManager::searchStream( const long id, Protocols::PacketFinder &pf )
{
    connection_itr c_itr = _established.find(id);

    if( c_itr != _established.end() )
    {
        connection &c = c_itr->second;

        bytes::iterator itr = c.stream.begin();

        while( itr != c.stream.end() && !pf(*itr) )
        {
            ++itr;
        }

        if( itr != c.stream.end() )
        {
            c.stream.erase(c.stream.begin(), itr + 1);

            return true;
        }
        else
        {
            return false;
        }
    }

    pending_itr p_itr = _pending.find(id);

    if( p_itr != _pending.end() )
    {
        pending &p = p_itr->second;

        bytes::iterator itr = p.stream.begin();

        while( itr != p.stream.end() && !pf(*itr) )
        {
            ++itr;
        }

        if( itr != p.stream.end() )
        {
            p.stream.erase(p.stream.begin(), itr + 1);

            return true;
        }
        else
        {
            return false;
        }
    }

    return false;
}


//  reconnect to all _stashed sockets
bool TcpConnectionManager::enable( void )
{
    _disabled = false;

    for each( const pair<long, connection> c in _stashed )
    {
        connect(c.first, c.second.ip, c.second.port);
    }

    _stashed.clear();

    return true;
}


//  disconnect from each established or pending connection, move them to _disabled
bool TcpConnectionManager::disable( void )
{
    _disabled = true;

    connection_itr e_itr = _established.begin();

    for( ; e_itr != _established.end(); ++e_itr )
    {
        disconnectEstablished(e_itr->second);
    }

    _stashed.insert(_established.begin(), _established.end());

    _established.clear();

    pending_itr p_itr = _pending.begin();

    for( ; p_itr != _pending.end(); ++p_itr )
    {
        disconnectPending(p_itr->second);
    }

    _stashed.insert(_pending.begin(), _pending.end());

    _pending.clear();

    return true;
}


}
}

