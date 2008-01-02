/*-----------------------------------------------------------------------------*
*
* File:   port_udp
*
* Class:  Cti::Porter::UDPInterface
* Date:   4/27/2006
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/port_base.h-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/01/02 21:10:05 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

//#include <windows.h>
//#include <iostream>
#include <map>
#include <queue>

#include "queue.h"
#include "dev_single.h"
#include "port_base.h"

//#include "portglob.h"
//#include "msg_trace.h"


namespace Cti
{
namespace Porter
{


class UDPInterface
{
private:

    CtiPortSPtr _port;

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

    };

    typedef queue< CtiOutMessage * > om_queue;
    typedef queue< packet *        > packet_queue;

    struct device_work
    {
        //  always working on the first entry in the queue
        om_queue      outbound;
        packet_queue  inbound;
        bool          pending_decode;
        bool          active;
        CtiXfer       xfer;
        int           status;
        unsigned long timeout;
        unsigned long last_outbound;
    };

    struct device_record
    {
        CtiDeviceSingleSPtr device;

        bool dirty;

        device_work work;

        u_short id;

        u_short master;
        u_short slave;

        u_long  ip;
        u_short port;
    };

    typedef map< pair< unsigned short, unsigned short >, device_record * > dr_address_map;
    typedef map< pair< unsigned short, unsigned short >, device_record * > dr_type_serial_map;
    typedef map< long, device_record * > dr_id_map;

    CtiFIFOQueue< packet >     _packet_queue;
    CtiFIFOQueue< CtiMessage > _message_queue;

    void startLog( void );
    void haltLog ( void );
    bool bindSocket( void );

    unsigned int convertBytes( unsigned char *buf, int &position, int bytes_to_combine );
    bool getOutMessages( unsigned wait );
    bool getPackets    ( int wait );

    void add_to_csv_summary( string &keys, string &values, string key, bool     value );
    void add_to_csv_summary( string &keys, string &values, string key, int      value );
    void add_to_csv_summary( string &keys, string &values, string key, unsigned value );
    void add_to_csv_summary( string &keys, string &values, string key, float    value );
    void add_to_csv_summary( string &keys, string &values, string key, string   value );

    static void delete_dr_id_map_value( dr_id_map::value_type map_entry );

    void generateOutbounds( void );
    void sendDeviceInfo   ( device_record *dr ) const;
    void processInbounds  ( void );
    void sendResults      ( void );

    void traceOutbound   ( device_record *dr, int socket_status, list< CtiMessage * > &trace_list );
    void traceInbound    ( unsigned long ip, unsigned short port, int status, const unsigned char *message, int count, list< CtiMessage * > &trace_list, CtiDeviceSPtr device=CtiDeviceSPtr() );

    void trace( void );

    void tickleSelf( void );

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

    device_record *validateDeviceRecord( device_record *dr );

    device_record *getDeviceRecordByDNPAddress           ( unsigned short master, unsigned short slave );
    device_record *getDeviceRecordByGPUFFDeviceTypeSerial( unsigned short device_type, unsigned short serial );
    device_record *getDeviceRecordByID                   ( long device_id );

    CtiLogger _portLog;

    list< CtiMessage * > _traceList;

    dr_address_map     _addresses;
    dr_type_serial_map _types_serials;
    dr_id_map          _devices;

    SOCKET _udp_socket;

    bool _devices_idle;

public:

    UDPInterface( CtiPortSPtr &port );

    void run();

    push_back(CtiMessage *msg);

    static const char *tickle_packet;
};


class UDPMessenger
{
private:

    CtiCriticalSection _critical_section;
    list< UDPInterface * > _clients;

public:

    UDPMessenger();

    void addClient(UDPInterface *client);

    void push_back(CtiMessage *msg);
};

}
}

