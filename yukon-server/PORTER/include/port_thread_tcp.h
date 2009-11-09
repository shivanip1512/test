#pragma once
#include "yukon.h"

#include <map>
#include <queue>

#include "unsolicited_handler.h"
#include "port_tcp.h"

namespace Cti    {
namespace Porter {

void PortTcpThread(void *pid);

class TcpPortHandler : public UnsolicitedHandler
{
private:

    typedef std::vector<unsigned char> datastream;

    struct connection
    {
        connection() :
            state(Disconnected),
            socket(INVALID_SOCKET)
        {};

        enum State
        {
            Disconnected,
            Connecting,
            Connected
        };

        State state;

        SOCKET socket;

        CtiTime connect_timeout;

        datastream stream;
    };

    typedef std::map<long, connection> connection_map;

    typedef connection_map::iterator       connection_itr;
    typedef connection_map::const_iterator connection_const_itr;

    typedef std::map<long, u_long>  ip_map;
    typedef std::map<long, u_short> port_map;

    Ports::TcpPortSPtr _tcp_port;

    connection_map _connections;

    ip_map     _ip_addresses;
    port_map   _ports;

    void loadDeviceTcpProperties(const std::set<long> &device_ids);
    static u_long resolveIp(const string &ip);

    void checkPendingConnections(vector<connection_itr> &in_progress);
    void checkPendingConnectionBlock(const int socket_count, fd_set &writeable_sockets, vector<connection_itr> &candidate_connections);

    bool connectToDevice     (const long device_id, connection &c);
    void setConnectionOptions(const long device_id, connection &c);
    void disconnectFromDevice(const long device_id, connection &c);

    void updateDeviceCommStatus(const long device_id, int status);

    void readInput(const long device_id);

    static void reportSocketError(const string method_name, const string winsock_function_name, const long device_id, const char *file, const int line);

protected:

    virtual std::string describePort( void ) const;

    virtual bool setupPort( void );
    virtual bool manageConnections( void );
    virtual void sendOutbound( device_record &dr );
    virtual bool collectInbounds( void );
    virtual void teardownPort( void );

    virtual void loadDeviceProperties(const std::set<long> &device_ids);

    virtual void addDeviceProperties   (const CtiDeviceSingle &device);
    virtual void updateDeviceProperties(const CtiDeviceSingle &device);
    virtual void deleteDeviceProperties(const CtiDeviceSingle &device);

    virtual u_long  getDeviceIp  ( const long device_id ) const;
    virtual u_short getDevicePort( const long device_id ) const;

    virtual std::string ip_to_string(u_long ip) const;

public:

    TcpPortHandler( Ports::TcpPortSPtr &port, CtiDeviceManager &deviceManager );
    virtual ~TcpPortHandler() {};
};

}
}

