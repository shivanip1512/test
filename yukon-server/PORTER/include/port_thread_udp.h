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

    typedef std::pair<unsigned short, unsigned short> dnp_address_pair;
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

    packet *recvPacket( unsigned char * const recv_buf, unsigned max_len );
    void distributePacket(packet *p);

    void handleDnpPacket  (packet *&p);
    void handleGpuffPacket(packet *&p);

    device_record *getDeviceRecordByDnpAddress           ( unsigned short master, unsigned short slave );
    device_record *getDeviceRecordByGpuffDeviceTypeSerial( unsigned short device_type, unsigned long serial );

    void updateDeviceIpAndPort( device_record &dr, const packet &p );

    void setDeviceIp  ( const long device_id, const u_long ip );
    void setDevicePort( const long device_id, const u_short port );

    static void sendDeviceIpAndPort( const CtiDeviceSingleSPtr &device, u_long ip, u_short port );

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

    UdpPortHandler( Ports::UdpPortSPtr &port, CtiDeviceManager &deviceManager );
    virtual ~UdpPortHandler() {};
};

}
}

