/*-----------------------------------------------------------------------------*
*
* File:   port_udp
*
* Class:  Cti::Porter::UDP::Port
* Date:   4/27/2006
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/port_base.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2006/12/28 21:00:29 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

//#include <windows.h>
//#include <iostream>
#include <map>
#include <queue>

using namespace std;

//#include "portglob.h"
//#include "msg_trace.h"


namespace Cti
{
namespace Porter
{

//  I'd like CtiThread to become Cti::Thread someday
class UDP_Port : public CtiThread
{
private:

protected:

    struct packet
    {
        unsigned char *data;
        int len;
        int used;

        u_long  ip;
        u_short port;
    };

    class Inbound : public CtiThread
    {
    protected:

        CtiFIFOQueue< packet > &_packet_queue;
        SOCKET &_udp_socket;

        enum ProtocolInfo
        {
            DNPHeaderLength = 10
        };

        void run();

    public:

        Inbound( SOCKET &s, CtiFIFOQueue< packet > &packet_queue );

    } *_inbound;

    typedef queue< CtiOutMessage * > om_queue;
    typedef queue< packet *        > packet_queue;

    struct device_work
    {
        //  always working on the first entry in the queue
        om_queue      outbound;
        packet_queue  inbound;
        bool          pending_decode;
        CtiXfer       xfer;
        int           status;
        unsigned long timeout;
        unsigned long last_outbound;
    };

    struct device_record
    {
        CtiDeviceSingleSPtr device;

        device_work work;

        u_long  ip;
        u_short port;
    };

    typedef map< pair< unsigned short, unsigned short >, device_record * > dr_address_map;
    typedef map< long, device_record * > dr_id_map;

    CtiFIFOQueue< packet > _packet_queue;

    void startLog( void );
    void haltLog ( void );
    bool bindSocket( void );

    bool getOutMessages( unsigned wait );
    bool getPackets    ( int wait );

    static void delete_dr_id_map_value( dr_id_map::value_type map_entry );

    void generateOutbounds( void );
    void processInbounds  ( void );
    void sendResults     ( void );

    void traceOutbound   ( device_record *dr, int socket_status, list< CtiMessage * > &trace_list );
    void traceInbound    ( unsigned long ip, unsigned short port, int status, const unsigned char *message, int count, list< CtiMessage * > &trace_list, CtiDeviceSPtr device=CtiDeviceSPtr() );

    void trace( void );

    void tickleSelf( void );

    struct udp_load_info
    {
        udp_load_info(long p, dr_id_map &d, dr_address_map &a) :
            portid   (p),
            devices  (d),
            addresses(a)
        {
        }

        long portid;

        dr_id_map      &devices;
        dr_address_map &addresses;
    };

    static void applyGetUDPInfo(const long unusedid, CtiDeviceSPtr RemoteDevice, void *prtid);

    device_record *getDeviceRecordByAddress( unsigned short master, unsigned short slave );
    device_record *getDeviceRecordByID     ( long device_id );

    CtiQueue< CtiOutMessage, less<CtiOutMessage> > OutMessageQueue;
    CtiFIFOQueue< CtiMessage > MessageQueue;

    CtiLogger _portLog;

    list< CtiMessage * > _traceList;

    dr_address_map _addresses;
    dr_id_map      _devices;

    SOCKET _udp_socket;

    long _udp_portnum, _port_id;

    bool _devices_idle;

public:

    UDP_Port( long portid, long udp_port );

    string getTag( void );

    void run();

    static const char *tickle_packet;
};


}
}

