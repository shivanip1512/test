#pragma once

#include "tcp_connection.h"
#include "packet_finder.h"

#include <boost/bimap.hpp>
#include <boost/bimap/set_of.hpp>
#include <boost/bimap/multiset_of.hpp>

#include <set>

namespace Cti::Porter {

class TcpConnectionManager
{
public:

    using address     = Connections::SocketAddress;
    using address_set = std::set<address>;
    struct address_results 
    {
        address_set success;
        address_set errors;
    };

    using bytes       = Connections::TcpSocketStream::bytes;

    struct not_connected : std::runtime_error
    {
        not_connected() : std::runtime_error("") {}
    };

    struct no_record : std::runtime_error
    {
        no_record() : std::runtime_error("") {}
    };

    using write_error = Connections::EstablishedTcpConnection::write_error;

private:

    using socket_stream          = Connections::SocketStream;

    using inactive_stream        = Connections::InactiveSocketStream;
    using pending_connection     = Connections::PendingTcpConnection;
    using established_connection = Connections::EstablishedTcpConnection;

    template <class T>
    using address_map = std::map<address, std::unique_ptr<T>>;

    template<class T, class U>
    auto emplace_unique_ptr(address_map<T>& addrMap, address addr, U arg);
        
    using inactive_map    = address_map<inactive_stream>;
    using pending_map     = address_map<pending_connection>;
    using established_map = address_map<established_connection>;

    template <class T>
    using slice = boost::iterator_range<typename T::const_iterator>;

    inactive_map    _inactive;
    pending_map     _pending;
    established_map _established;

    bool _disabled;

    bytes *findStream(const address addr);

    void tryConnectInactive();

    void checkPendingConnectionBlock(slice<pending_map> &pending_block, address_results& results);

    void readCandidateSockets(address_results& results, slice<established_map> &candidate_sockets);

public:

    TcpConnectionManager() :
        _disabled(false)
    { }

    void connect   (const address addr);
    void disconnect(const address addr);

    bool isConnected(const address addr) const;

    address_results updateConnections();

    //  throws: not_connected, write_error
    int  send(const address addr, const bytes &data);

    address_results recv();

    bool searchStream(const address addr, Protocols::PacketFinder &pf);

    void enable (void);
    void disable(void);
};

}
