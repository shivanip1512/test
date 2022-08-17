#pragma once
#include "yukon.h"

#include <map>
#include <queue>

#include "unsolicited_handler.h"
#include "port_udp.h"
#include "EncodingFilterFactory.h"
#include "socket_helper.h"
#include "dnpLookup.h"

#define BOOST_MULTI_INDEX_DISABLE_SERIALIZATION

#include <boost/bimap.hpp>

namespace Cti    {
namespace Porter {

void PortUdpThread(void *pid);

class UdpPortHandler : public UnsolicitedHandler
{
private:

    Ports::UdpPortSPtr _udp_port;

    EncodingFilterFactory::EncodingFilterSPtr _encodingFilter;

    Cti::ServerSockets _udp_sockets;
    unsigned short _connected_port;

    //typedef std::pair<unsigned short, unsigned short> uecp_address_pair;  //  Not using UECP unsolicited inbounds
    typedef std::pair<unsigned short, unsigned long>  gpuff_type_serial_pair;

    static gpuff_type_serial_pair makeGpuffTypeSerialPair(const CtiDeviceSingle &device);

    typedef boost::bimap<gpuff_type_serial_pair, long> type_serial_id_bimap;

    type_serial_id_bimap _typeAndSerial_to_id;

    DnpLookup _dnpLookup;

    typedef std::map<long, std::string>  ip_map;
    typedef std::map<long, u_short> port_map;

    ip_map   _ip_addresses;
    port_map _ports;

    bool bindSocket( void );
    bool tryBindSocket( void );

    void teardownSocket( void );

    ip_packet *recvPacket( unsigned char * const recv_buf, unsigned max_len );

    bool validatePacket(ip_packet *&p);

    void distributePacket(ip_packet *p);

    void handleDnpPacket  (ip_packet *&p);
    void handleGpuffPacket(ip_packet *&p);

    device_record *getDeviceRecordByGpuffDeviceTypeSerial( unsigned short device_type, unsigned long serial );

    void updateDeviceIpAndPort( device_record &dr, const ip_packet &p );
    void loadStaticRdsIPAndPort( const CtiDeviceSingle &device);

    void setDeviceIp  ( const long device_id, const std::string ip );
    void setDevicePort( const long device_id, const u_short port );

    static void sendDeviceIpAndPort( const CtiDeviceSingleSPtr &device, std::string ip, u_short port );

    void loadEncodingFilter();

    struct OutboundDestination
    {
        SOCKET socket;
        std::vector<char> sockaddr;
    };

    OutboundDestination getDestinationForAddress(const AddrInfo& address);

protected:

    virtual std::string describePort( void ) const;

    virtual bool setupPort( void );
    virtual bool manageConnections( void );
    virtual YukonError_t sendOutbound( device_record &dr );
    virtual unsigned getDeviceTimeout( const device_record &dr ) const;
    virtual bool collectInbounds( const Cti::Timing::MillisecondTimer & timer, const unsigned long until);

    virtual void loadDeviceProperties(const std::vector<const CtiDeviceSingle *> &devices);

    virtual void addDeviceProperties   (const CtiDeviceSingle &device);
    virtual void updateDeviceProperties(const CtiDeviceSingle &device);
    virtual void deleteDeviceProperties(const CtiDeviceSingle &device);

    virtual void updatePortProperties( void );

    virtual bool isDeviceDisconnected( const long device_id ) const;

    virtual std::string getDeviceIp  ( const long device_id ) const;
    virtual u_short     getDevicePort( const long device_id ) const;

    virtual std::string describeDeviceAddress( const long device_id ) const;

public:

    UdpPortHandler( Ports::UdpPortSPtr &port, CtiDeviceManager &deviceManager );
    virtual ~UdpPortHandler();
};

}
}

