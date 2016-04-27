#pragma once

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

    TcpConnectionManager _connectionManager;

    void loadDeviceTcpProperties(const std::set<long> &device_ids);

    Connections::SocketAddress getDeviceSocketAddress( const long device_id ) const;

    void updateDeviceCommStatus(const long device_id, YukonError_t status);

    packet *findPacket( const long device_id, Protocols::PacketFinder &pf );

protected:

    virtual std::string describePort( void ) const;

    virtual bool setupPort( void );
    virtual bool manageConnections( void );
    virtual YukonError_t sendOutbound( device_record &dr );
    virtual unsigned getDeviceTimeout( const device_record &dr ) const;
    virtual bool collectInbounds(const Cti::Timing::MillisecondTimer & timer, const unsigned long until);

    virtual void loadDeviceProperties(const std::vector<const CtiDeviceSingle *> &devices);

    virtual void addDeviceProperties   (const CtiDeviceSingle &device);
    virtual void updateDeviceProperties(const CtiDeviceSingle &device);
    virtual void deleteDeviceProperties(const CtiDeviceSingle &device);

    virtual void updatePortProperties( void );

    virtual bool isDeviceDisconnected( const long device_id ) const;

    virtual std::string describeDeviceAddress( const long device_id ) const;

public:

    TcpPortHandler( Ports::TcpPortSPtr &port, CtiDeviceManager &deviceManager );
};

}
}

