#pragma once

#include "unsolicited_handler.h"

#include "packet_finder.h"

#include "port_tcp.h"

#include "tcp_connection_manager.h"

#include "dnpLookup.h"

namespace Cti::Porter {

void PortTcpThread(void *pid);

class TcpPortHandler : public UnsolicitedHandler
{
private:

    Ports::TcpPortSPtr _tcp_port;

    TcpConnectionManager _connectionManager;

    template<class T>
    void loadDeviceTcpProperties(const T& devices);

    using TcpSocketAddress = Connections::SocketAddress;

    TcpSocketAddress getDeviceSocketAddress( const long device_id ) const;

    void updateCommStatuses(std::set<TcpSocketAddress> addresses, YukonError_t status);
    void updateCommStatus(TcpSocketAddress addr, YukonError_t status);
    void updateDeviceCommStatus(long device_id, YukonError_t status);

    using address_ids = boost::bimap<boost::bimaps::multiset_of<TcpSocketAddress>, long>;

    address_ids _address_devices;

    std::map<TcpSocketAddress, DnpLookup> _socketAddresses;

    packet *findPacket( const TcpSocketAddress addr, Protocols::PacketFinder &pf );

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