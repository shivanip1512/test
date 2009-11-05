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
            socket(INVALID_SOCKET),
            connect_timeout(0)
        {};

        enum State
        {
            Disconnected,
            Connecting,
            Connected
        };

        State state;

        SOCKET socket;

        unsigned long connect_timeout;

        datastream stream;
    };

    typedef std::map<long, connection> connection_map;

    typedef std::map<long, u_long>  ip_map;
    typedef std::map<long, u_short> port_map;

    Ports::TcpPortSPtr _tcp_port;

    connection_map _connections;

    ip_map     _ip_addresses;
    port_map   _ports;

    void loadDeviceTcpProperties(const std::set<long> &device_ids);
    static u_long resolveIp(const string &ip);

    bool connectToDevice     (const long device_id, connection &c);
    void setConnectionOptions(const long device_id, connection &c);
    void disconnectFromDevice(const long device_id, connection &c);

    void readInput(const long device_id);

    static void reportSocketError(const string function_name, const long device_id, const char *file, const int line);


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

public:

    TcpPortHandler( Ports::TcpPortSPtr &port, CtiDeviceManager &deviceManager );
    virtual ~TcpPortHandler() {};
};

}
}

