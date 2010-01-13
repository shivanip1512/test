#pragma once

#include <set>
#include <vector>

#include <winsock.h>

#include "packet_finder.h"

#include "ctitime.h"

namespace Cti    {
namespace Porter {

class TcpConnectionManager
{
public:

    typedef std::set<long> id_set;
    typedef std::vector<char> bytes;

private:

    struct connection
    {
        connection(unsigned long ip_, unsigned short port_) :
            ip(ip_),
            port(port_),
            sock(INVALID_SOCKET)
        { }

        unsigned long  ip;
        unsigned short port;

        SOCKET sock;

        bytes stream;
    };

    struct pending : connection
    {
        pending(unsigned long ip_, unsigned short port_) :
            connection(ip_, port_)
        { }

        CtiTime timeout;
    };

    typedef std::map<long, pending>    pending_map;
    typedef std::map<long, connection> connection_map;

    pending_map    _pending;
    connection_map _established;
    connection_map _stashed;

    typedef connection_map::iterator       connection_itr;
    typedef connection_map::const_iterator connection_const_itr;
    typedef pending_map::iterator          pending_itr;
    typedef pending_map::const_iterator    pending_const_itr;

    bool _disabled;

    bool connect(pending &p);

    void createConnection(const long id, const unsigned long ip, const short port);

    connection *findExistingConnection(const long id);

    void checkPendingConnectionBlock(fd_set &writeable_sockets, std::vector<pending_itr> &pending_block, id_set &connected, id_set &errors);

    void disconnectEstablished(connection &c);
    void disconnectPending    (pending    &p);

    void setConnectionOptions(const pending &p);

    static void reportSocketError(const std::string winsock_function_name, const char *method_name, const char *file, const int line);

    bool readInput(connection &c);

public:

    TcpConnectionManager() :
        _disabled(false)
    {
    }

    void connect   (const long id, const unsigned long ip, const unsigned short port);
    void disconnect(const long id);
    void disconnectAll( void );

    bool isConnected(const long id) const;

    void checkPendingConnections( id_set &connected, id_set &errors);

    int  send(const long id, const bytes &data);

    bool recv(id_set &ready, id_set &errors);

    bool searchStream(const long id, Protocols::PacketFinder &pf);

    bool enable (void);
    bool disable(void);
};

}
}

