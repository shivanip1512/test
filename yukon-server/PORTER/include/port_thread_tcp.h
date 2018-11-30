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

    boost::optional<const TcpSocketAddress> getDeviceSocketAddress( const long device_id ) const;

    void updateCommStatuses(std::set<TcpSocketAddress> addresses, YukonError_t status);
    void updateCommStatus(TcpSocketAddress addr, YukonError_t status);
    void updateDeviceCommStatus(long device_id, YukonError_t status);

    using address_ids = boost::bimap<boost::bimaps::multiset_of<TcpSocketAddress>, long>;

    address_ids _address_devices;

    std::map<TcpSocketAddress, DnpLookup> _socketAddresses;

    auto findPacket( const TcpSocketAddress addr, Protocols::PacketFinder &pf ) -> std::unique_ptr<packet>;

protected:

    std::string describePort( void ) const override;

    bool setupPort( void ) override;
    bool manageConnections( void ) override;
    YukonError_t sendOutbound( device_record &dr ) override;
    unsigned getDeviceTimeout( const device_record &dr ) const override;
    bool collectInbounds(const Cti::Timing::MillisecondTimer & timer, const unsigned long until) override;

    void loadDeviceProperties(const std::vector<const CtiDeviceSingle *> &devices) override;

    void addDeviceProperties   (const CtiDeviceSingle &device) override;
    void updateDeviceProperties(const CtiDeviceSingle &device) override;
    void deleteDeviceProperties(const CtiDeviceSingle &device) override;

    void updatePortProperties( void ) override;

    bool isDeviceDisconnected( const long device_id ) const override;

    std::string describeDeviceAddress( const long device_id ) const override;

public:

    TcpPortHandler( Ports::TcpPortSPtr &port, CtiDeviceManager &deviceManager );
};

}