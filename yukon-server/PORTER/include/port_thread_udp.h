#pragma once
#include "yukon.h"

#include <map>
#include <queue>

#include "unsolicited_handler.h"
#include "port_udp.h"
#include "EncodingFilterFactory.h"

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

    SOCKET _udp_socket;
    unsigned short _connected_port;

    typedef std::pair<unsigned short, unsigned short> dnp_address_pair;
    //typedef std::pair<unsigned short, unsigned short> uecp_address_pair;  //  Not using UECP unsolicited inbounds
    typedef std::pair<unsigned short, unsigned long>  gpuff_type_serial_pair;

    static dnp_address_pair       makeDnpAddressPair     (const CtiDeviceSingle &device);
    static gpuff_type_serial_pair makeGpuffTypeSerialPair(const CtiDeviceSingle &device);

    typedef boost::bimap<gpuff_type_serial_pair, long> type_serial_id_bimap;
    typedef boost::bimap<dnp_address_pair,       long> dnp_address_id_bimap;

    type_serial_id_bimap _typeAndSerial_to_id;
    dnp_address_id_bimap _dnpAddress_to_id;

    typedef std::map<long, u_long>  ip_map;
    typedef std::map<long, u_short> port_map;

    ip_map   _ip_addresses;
    port_map _ports;

    bool bindSocket( void );
    bool tryBindSocket( void );

    void teardownSocket( void );

    packet *recvPacket( unsigned char * const recv_buf, unsigned max_len );

    bool validatePacket(packet *&p);

    void distributePacket(packet *p);

    void handleDnpPacket  (packet *&p);
    void handleGpuffPacket(packet *&p);

    device_record *getDeviceRecordByDnpAddress           ( unsigned short master, unsigned short slave );
    device_record *getDeviceRecordByGpuffDeviceTypeSerial( unsigned short device_type, unsigned long serial );

    void updateDeviceIpAndPort( device_record &dr, const packet &p );
    void loadStaticRdsIPAndPort( const CtiDeviceSingle &device);

    void setDeviceIp  ( const long device_id, const u_long ip );
    void setDevicePort( const long device_id, const u_short port );

    static u_long string_to_ip(string ip_string);

    static void sendDeviceIpAndPort( const CtiDeviceSingleSPtr &device, u_long ip, u_short port );

    void loadEncodingFilter();

protected:

    virtual std::string describePort( void ) const;

    virtual bool setupPort( void );
    virtual bool manageConnections( void );
    virtual int  sendOutbound( device_record &dr );
    virtual bool collectInbounds( const Cti::Timing::MillisecondTimer & timer, const unsigned long until);

    virtual void loadDeviceProperties(const std::vector<const CtiDeviceSingle *> &devices);

    virtual void addDeviceProperties   (const CtiDeviceSingle &device);
    virtual void updateDeviceProperties(const CtiDeviceSingle &device);
    virtual void deleteDeviceProperties(const CtiDeviceSingle &device);

    virtual void updatePortProperties( void );

    virtual bool isDeviceDisconnected( const long device_id ) const;

    virtual u_long  getDeviceIp  ( const long device_id ) const;
    virtual u_short getDevicePort( const long device_id ) const;

    virtual string ip_to_string(u_long ip) const;

public:

    UdpPortHandler( Ports::UdpPortSPtr &port, CtiDeviceManager &deviceManager );
    virtual ~UdpPortHandler();
};

}
}

