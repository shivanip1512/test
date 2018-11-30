#include "precompiled.h"

#include "tcp_connection_manager.h"

#include "cparms.h"
#include "logger.h"
#include "std_helper.h"
#include "coroutine_util.h"

#include <boost/range/join.hpp>

using namespace std;

namespace Cti::Porter {


template<class T, class U>
auto TcpConnectionManager::emplace_unique_ptr(address_map<T>& addrMap, address addr, U arg)
{
    return addrMap.emplace(addr, std::make_unique<TcpConnectionManager::address_map<T>::mapped_type::element_type>(arg));
}


void TcpConnectionManager::connect(const address new_address )
{
    if( ! findStream(new_address) )
    {
        CTILOG_INFO(dout, "Connecting to " << new_address);

        emplace_unique_ptr(_inactive, new_address, new_address);
    }
}


TcpConnectionManager::bytes *TcpConnectionManager::findStream(const address addr)
{
    socket_stream *s;

    if( s = mapFindPtr(_established, addr) )  return &(s->stream);
    if( s = mapFindPtr(_pending,     addr) )  return &(s->stream);
    if( s = mapFindPtr(_inactive,    addr) )  return &(s->stream);

    return nullptr;
}


void TcpConnectionManager::disconnect(const address addr)
{
    CTILOG_INFO(dout, "Disconnecting from " << addr);

    _established.erase(addr);
    _pending    .erase(addr);
    _inactive   .erase(addr);
}


auto TcpConnectionManager::updateConnections() -> address_results
{
    tryConnectInactive();

    address_results results;

    for( auto pending_block : Coroutines::chunked(_pending, FD_SETSIZE) )
    {
        checkPendingConnectionBlock(pending_block, results);
    }

    for( auto addr : boost::range::join(results.success, results.errors) )
    {
        _pending.erase(addr);
    }

    return results;
}


void Cti::Porter::TcpConnectionManager::tryConnectInactive()
{
    const CtiTime now;
    const CtiTime next_attempt = now + gConfigParms.getValueAsULong("PORTER_TCP_CONNECT_TIMEOUT", 15);

    for( auto itr = _inactive.begin(); itr != _inactive.end(); )
    {
        const auto current_pos = itr++;

        const address addr = current_pos->first;
        inactive_stream &inactive_stream = *(current_pos->second);

        if( inactive_stream.next_attempt < now )
        {
            try
            {
                emplace_unique_ptr(_pending, addr, inactive_stream);

                _inactive.erase(current_pos);
            }
            catch( Connections::TcpSocketStream::wsa_error_code &ex )
            {
                //  construction of a pending object failed - move past it and try again later
                inactive_stream.next_attempt = next_attempt;
            }
        }
    }
}


void TcpConnectionManager::checkPendingConnectionBlock(slice<pending_map> &pending_block, address_results& results)
{
    timeval tv = { 0, 1000 };  //  0 seconds + 1000 microseconds = 1 millisecond
    fd_set new_connections;

    FD_ZERO(&new_connections);

    for( auto& [id, pendingConnection] : pending_block )
    {
        pendingConnection->add_to(&new_connections);
    }

    //  check to see if any of the sockets are writable - a writable socket means the connection has completed
    int ready_count = select(0, NULL, &new_connections, NULL, &tv);

    if( ready_count == SOCKET_ERROR )
    {
        CTILOG_ERROR(dout, Connections::TcpSocketStream::formatSocketError("select", WSAGetLastError()));

        ready_count = 0;
    }

    const CtiTime Now;

    for( auto& [id, p] : pending_block )
    {
        try
        {
            if( ready_count && p->is_in(&new_connections) )
            {
                emplace_unique_ptr(_established, id, *p);

                results.success.insert(id);
            }
            else if( p->timeout < Now )
            {
                emplace_unique_ptr(_inactive, id, *p);

                results.errors.insert(id);
            }
        }
        catch( ... )
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "failure during pending connection test");
        }
    }
}


int TcpConnectionManager::send(const address addr, const bytes &data)
{
    auto itr = _established.find(addr);

    if( itr == _established.end() )
    {
        throw not_connected();
    }

    established_connection& e = *(itr->second);

    try
    {
        return e.send(data);
    }
    catch( write_error &ex )
    {
        emplace_unique_ptr(_inactive, addr, e);

        _established.erase(itr);

        throw;
    }
}



auto TcpConnectionManager::recv() -> address_results
{
    address_results results;

    //  check in blocks of up to FD_SETSIZE
    for( auto candidate_sockets : Coroutines::chunked(_established, FD_SETSIZE) )
    {
        readCandidateSockets(results, candidate_sockets);
    }

    for( const address addr : results.errors )
    {
        _established.erase(addr);
    }

    return results;
}


void TcpConnectionManager::readCandidateSockets(address_results& results, slice<established_map> &candidate_sockets)
{
    fd_set readable_sockets;

    FD_ZERO(&readable_sockets);

    for( auto& [id, connection] : candidate_sockets )
    {
        connection->add_to(&readable_sockets);
    }

    timeval tv { 0, 1000 };  //  0 seconds + 1000 microseconds = 1 millisecond

    if( int ready_count = select(0, &readable_sockets, NULL, NULL, &tv); ready_count <= 0 )
    {
        if( ready_count == SOCKET_ERROR )
        {
            CTILOG_ERROR(dout, Connections::TcpSocketStream::formatSocketError("select", WSAGetLastError()));
        }
        else if( ready_count < 0 )
        {
            CTILOG_ERROR(dout, "Unknown socket error, ready_count = " << ready_count);
        }

        return;
    }

    for( auto& [id, e] : candidate_sockets )
    {
        if( e->is_in(&readable_sockets) )
        {
            try
            {
                e->recv();

                results.success.insert(id);
            }
            catch( Connections::TcpSocketStream::tcp_socket_error &ex )
            {
                results.errors.insert(id);

                emplace_unique_ptr(_inactive, id, *e);
            }
        }
    }
}


bool TcpConnectionManager::isConnected( const address addr ) const
{
    return _established.count(addr);
}


bool TcpConnectionManager::searchStream( const address addr, Protocols::PacketFinder &pf )
{
    if( bytes *stream = findStream(addr) )
    {
        for( auto itr = stream->begin();  itr != stream->end(); )
        {
            if( pf(*itr++) )
            {
                stream->erase(stream->begin(), itr);

                return true;
            }
        }
    }

    return false;
}


//  reconnect to all _stashed sockets
void TcpConnectionManager::enable( void )
{
    _disabled = false;

    tryConnectInactive();
}


//  disconnect from each established or pending connection, move them to _disabled
void TcpConnectionManager::disable( void )
{
    _disabled = true;

    for( auto& [id, connection] : _established )
    {
        emplace_unique_ptr(_inactive, id, *connection);
    }

    _established.clear();

    for( auto& [id, connection] : _pending )
    {
        emplace_unique_ptr(_inactive, id, *connection);
    }

    _pending.clear();
}


}
