#pragma once

#include "tcp_connection.h"
#include "packet_finder.h"

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

    template <class T>
    using id_map = std::map<long, std::unique_ptr<T>>;

    template<class T, class U>
    auto emplace_unique_ptr(id_map<T>& idMap, long id, U arg);
        
    using inactive_map    = id_map<inactive_stream>;
    using pending_map     = id_map<pending_connection>;
    using established_map = id_map<established_connection>;

    template <class T>
    using slice = boost::iterator_range<typename T::const_iterator>;

    inactive_map    _inactive;
    pending_map     _pending;
    established_map _established;

    bool _disabled;

    const address *findAddress(const long id) const;
    bytes *findStream(const long id);

    void tryConnectInactive();

    void checkPendingConnectionBlock(slice<pending_map> &pending_block, id_set &connected, id_set &errors);

    void readCandidateSockets(id_set &ready, id_set &errors, slice<established_map> &candidate_sockets);

public:

    TcpConnectionManager() :
        _disabled(false)
    { }

    void connect   (const long id, const Connections::SocketAddress address);
    void disconnect(const long id);

    bool isConnected(const long id) const;

    //  throws: no_record
    Connections::SocketAddress getAddress(const long id) const;

    struct result_ids {
        id_set success;
        id_set errors;
    };
    
    result_ids updateConnections();

    //  throws: not_connected, write_error
    int  send(const long id, const bytes &data);

    result_ids recv();

    bool searchStream(const long id, Protocols::PacketFinder &pf);

    void enable (void);
    void disable(void);
};

}
}

