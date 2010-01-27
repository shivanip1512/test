#pragma once

#include "tcp_connection.h"
#include "packet_finder.h"

#include "boost/ptr_container/ptr_map.hpp"

#include <set>

namespace Cti    {
namespace Porter {

class TcpConnectionManager
{
public:

    typedef std::set<long> id_set;
    typedef Connections::SocketStream::bytes bytes;

    struct not_connected : std::runtime_error
    {
        not_connected() : std::runtime_error("") {}
    };

    struct no_record : std::runtime_error
    {
        no_record() : std::runtime_error("") {}
    };

    typedef Connections::EstablishedTcpConnection::write_error write_error;

private:

    typedef Connections::SocketStream             socket_stream;
    typedef Connections::SocketAddress            address;

    typedef Connections::InactiveSocketStream     inactive_stream;
    typedef Connections::PendingTcpConnection     pending_connection;
    typedef Connections::EstablishedTcpConnection established_connection;

    typedef Connections::SocketStream::bytes      bytes;

    typedef boost::ptr_map<const long, inactive_stream>    inactive_map;
    typedef boost::ptr_map<const long, pending_connection>     pending_map;
    typedef boost::ptr_map<const long, established_connection> established_map;

    inactive_map    _inactive;
    pending_map     _pending;
    established_map _established;

    bool _disabled;

    const address *findAddress(const long id) const;
    bytes *findStream(const long id);

    void tryConnectInactive();

    void checkPendingConnectionBlock(std::vector<pending_map::iterator> &pending_block, id_set &connected, id_set &errors);

    void readCandidateSockets(id_set &ready, id_set &errors, std::vector<established_map::iterator> &candidate_sockets);

public:

    TcpConnectionManager() :
        _disabled(false)
    { }

    void connect   (const long id, const Connections::SocketAddress address);
    void disconnect(const long id);

    bool isConnected(const long id) const;

    //  throws: no_record
    Connections::SocketAddress getAddress(const long id) const;

    void updateConnections(id_set &connected, id_set &errors);

    //  throws: not_connected, write_error
    int  send(const long id, const bytes &data);

    bool recv(id_set &ready, id_set &errors);

    bool searchStream(const long id, Protocols::PacketFinder &pf);

    void enable (void);
    void disable(void);
};

}
}

