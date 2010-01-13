#pragma once
#include "yukon.h"

#include <map>
#include <queue>

#include "unsolicited_handler.h"
#include "packet_finder.h"

#include "port_tcp.h"

#include "tcp_connection_manager.h"

namespace Cti    {
namespace Porter {

void PortTcpThread(void *pid);

class TcpPortHandler : public UnsolicitedHandler
{
private:

    Ports::TcpPortSPtr _tcp_port;

    typedef std::map<long, u_long>  ip_map;
    typedef std::map<long, u_short> port_map;

    ip_map     _ip_addresses;
    port_map   _ports;

    TcpConnectionManager _connectionManager;

    void loadDeviceTcpProperties(const std::set<long> &device_ids);

    static u_long resolveIp(const string &ip);

    void updateDeviceCommStatus(const long device_id, int status);

    packet *findPacket( const long device_id, Protocols::PacketFinder &pf );

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

    virtual void updatePortProperties( void );

    virtual bool isDeviceDisconnected( const long device_id ) const;

    virtual u_long  getDeviceIp  ( const long device_id ) const;
    virtual u_short getDevicePort( const long device_id ) const;

    virtual std::string ip_to_string(u_long ip) const;

public:

    TcpPortHandler( Ports::TcpPortSPtr &port, CtiDeviceManager &deviceManager );
    virtual ~TcpPortHandler() {};
};

}
}

