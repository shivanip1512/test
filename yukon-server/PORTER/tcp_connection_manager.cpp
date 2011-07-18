#include "precompiled.h"

#include "tcp_connection_manager.h"

#include "cparms.h"
#include "logger.h"

#include <boost/assign/ptr_map_inserter.hpp>

using namespace std;

namespace Cti    {
namespace Porter {


void TcpConnectionManager::connect( const long id, const Connections::SocketAddress new_address )
{
    if( const address *old_address = findAddress(id) )
    {
        if( *old_address == new_address )
        {
            return;
        }

        disconnect(id);
    }

    boost::assign::ptr_map_insert(_inactive)(id, new_address);
}


template<class Map>
typename Map::const_pointer::second_type const_find_in_map(const long id, Map &m)
{
    Map::const_iterator itr = m.find(id);

    if( itr == m.end() )
    {
        return 0;
    }

    return itr->second;
}


const TcpConnectionManager::address *TcpConnectionManager::findAddress(const long id) const
{
    const socket_stream *s;

    if( s = const_find_in_map(id, _established) )  return &(s->address);
    if( s = const_find_in_map(id, _pending) )      return &(s->address);
    if( s = const_find_in_map(id, _inactive) )     return &(s->address);

    return 0;
}


template<class Map>
typename Map::value_type::second_type find_in_map(const long id, Map &m)
{
    Map::iterator itr = m.find(id);

    if( itr == m.end() )
    {
        return 0;
    }

    return itr->second;
}


TcpConnectionManager::bytes *TcpConnectionManager::findStream(const long id)
{
    socket_stream *s;

    if( s = find_in_map(id, _established) )  return &(s->stream);
    if( s = find_in_map(id, _pending) )      return &(s->stream);
    if( s = find_in_map(id, _inactive) )     return &(s->stream);

    return 0;
}


void TcpConnectionManager::disconnect(long id)
{
    _established.erase(id);
    _pending    .erase(id);
    _inactive   .erase(id);
}


void TcpConnectionManager::updateConnections(id_set &connected, id_set &errors)
{
    tryConnectInactive();

    for( pending_map::iterator itr = _pending.begin(); itr != _pending.end(); )
    {
        vector<pending_map::iterator> pending_block;

        //  check in blocks of up to FD_SETSIZE
        for( int socket_count = 0; itr != _pending.end() && socket_count < FD_SETSIZE; ++socket_count )
        {
            pending_block.push_back(itr++);
        }

        checkPendingConnectionBlock(pending_block, connected, errors);
    }
}


void Cti::Porter::TcpConnectionManager::tryConnectInactive()
{
    const CtiTime now;
    const CtiTime next_attempt = now + gConfigParms.getValueAsULong("PORTER_TCP_CONNECT_TIMEOUT", 15);

    inactive_map::iterator itr = _inactive.begin();

    while( itr != _inactive.end() )
    {
        inactive_map::iterator current_pos = itr++;

        const long id = current_pos->first;
        inactive_stream &inactive_stream = *(current_pos->second);

        if( inactive_stream.next_attempt < now )
        {
            try
            {
                boost::assign::ptr_map_insert(_pending)(id, inactive_stream);

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


void TcpConnectionManager::checkPendingConnectionBlock(vector<pending_map::iterator> &pending_block, id_set &connected, id_set &errors)
{
    timeval tv = { 0, 1000 };  //  0 seconds + 1000 microseconds = 1 millisecond
    fd_set new_connections;

    FD_ZERO(&new_connections);

    for each( pending_map::const_iterator itr in pending_block )
    {
        itr->second->add_to(&new_connections);
    }

    //  check to see if any of the sockets are writable - a writable socket means the connection has completed
    int ready_count = select(0, NULL, &new_connections, NULL, &tv);

    if( ready_count == SOCKET_ERROR )
    {
        Connections::TcpSocketStream::reportSocketError("select", __FUNCTION__, __FILE__, __LINE__);

        ready_count = 0;
    }

    const CtiTime Now;

    for each( pending_map::iterator itr in pending_block )
    {
        const long &id = itr->first;
        pending_connection    &p  = *(itr->second);

        if( ready_count && p.is_in(&new_connections) )
        {
            _established.insert(id, new established_connection(p));

            _pending.erase(itr);

            connected.insert(id);
        }
        else if( p.timeout > Now )
        {
            boost::assign::ptr_map_insert(_inactive)(id, p);

            _pending.erase(itr);

            errors.insert(id);
        }
    }
}


int TcpConnectionManager::send(const long id, const bytes &data)
{
    established_map::iterator itr = _established.find(id);

    if( itr == _established.end() )
    {
        throw not_connected();
    }

    established_connection &e = *(itr->second);

    try
    {
        return e.send(data);
    }
    catch( write_error &ex )
    {
        boost::assign::ptr_map_insert(_inactive)(id, e);

        _established.erase(itr);

        throw;
    }
}



bool TcpConnectionManager::recv(id_set &ready, id_set &errors)
{
    established_map::iterator itr = _established.begin();

    while( itr != _established.end() )
    {
        vector<established_map::iterator> candidate_sockets;

        //  check in blocks of up to FD_SETSIZE
        for( int sockets = 0; itr != _established.end() && sockets < FD_SETSIZE; ++itr, ++sockets )
        {
            candidate_sockets.push_back(itr);
        }

        readCandidateSockets(ready, errors, candidate_sockets);
    }

    return !ready.empty() && !errors.empty();
}


void TcpConnectionManager::readCandidateSockets(id_set &ready, id_set &errors, vector<established_map::iterator> &candidate_sockets)
{
    fd_set readable_sockets;

    FD_ZERO(&readable_sockets);

    for each( established_map::iterator e_itr in candidate_sockets )
    {
        e_itr->second->add_to(&readable_sockets);
    }

    timeval tv = { 0, 1000 };  //  0 seconds + 1000 microseconds = 1 millisecond

    int ready_count = select(0, &readable_sockets, NULL, NULL, &tv);

    if( ready_count <= 0 )
    {
        if( ready_count == SOCKET_ERROR )
        {
            Connections::TcpSocketStream::reportSocketError("select", __FUNCTION__, __FILE__, __LINE__);
        }

        return;
    }

    for each( established_map::iterator e_itr in candidate_sockets )
    {
        const long &id = e_itr->first;
        established_connection &e = *(e_itr->second);

        if( e.is_in(&readable_sockets) )
        {
            try
            {
                e.recv();

                ready.insert(id);
            }
            catch( Connections::TcpSocketStream::tcp_socket_error &ex )
            {
                errors.insert(id);

                boost::assign::ptr_map_insert(_inactive)(id, e);

                _established.erase(e_itr);
            }
        }
    }
}


bool TcpConnectionManager::isConnected( const long id ) const
{
    return _established.find(id) != _established.end();
}


Connections::SocketAddress TcpConnectionManager::getAddress(const long id) const
{
    if( const address *a = findAddress(id) )
    {
        return *a;
    }

    throw no_record();
}


bool TcpConnectionManager::searchStream( const long id, Protocols::PacketFinder &pf )
{
    if( bytes *stream = findStream(id) )
    {
        bytes::iterator itr = stream->begin();

        while( itr != stream->end() )
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

    for each( established_map::reference e in _established )
    {
        boost::assign::ptr_map_insert(_inactive)(e.first, *e.second);
    }

    _established.clear();

    for each( pending_map::reference p in _pending )
    {
        boost::assign::ptr_map_insert(_inactive)(p.first, *p.second);
    }

    _pending.clear();
}


}
}

