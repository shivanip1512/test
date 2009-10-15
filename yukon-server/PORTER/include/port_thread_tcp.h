#pragma once
#include "yukon.h"

#include <map>
#include <queue>

#include "queue.h"
#include "dev_single.h"
#include "EncodingFilterFactory.h"
#include "port_tcp.h"
#include "unsolicited_handler.h"

namespace Cti    {
namespace Porter {

void PortTcpThread(void *pid);
/*
class UdpPortHandler : public UnsolicitedHandler
{
private:

    Ports::UdpPortSPtr _udp_port;

    EncodingFilterFactory::EncodingFilterSPtr _encodingFilter;

    bool bindSocket( void );

    packet *recvPacket( unsigned char * const recv_buf, unsigned max_len );

    static void sendDeviceInfo( const device_record &dr );

    struct udp_load_info
    {
        udp_load_info(long p, dr_id_map &d, dr_address_map &a, dr_type_serial_map &ts) :
            portid   (p),
            devices  (d),
            addresses(a),
            types_serials(ts)
        {
        }

        long portid;

        dr_id_map          &devices;
        dr_address_map     &addresses;
        dr_type_serial_map &types_serials;
    };

    static void applyGetUDPInfo(const long unusedid, CtiDeviceSPtr RemoteDevice, void *prtid);

    SOCKET _udp_socket;

protected:

    virtual bool setup( void );

    virtual string describePort( void ) const;

    virtual void sendOutbound( device_record &dr );

    virtual void collectInbounds( void );

    virtual void updateDeviceRecord( device_record &dr, const packet &p );

    virtual void teardown( void );

public:

    UdpPortHandler( Ports::UdpPortSPtr &port );
};
*/
}
}

