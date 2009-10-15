#pragma once
#include "yukon.h"

#include <map>
#include <queue>
#include "boost/noncopyable.hpp"

#include "port_base.h"
#include "dev_single.h"

namespace Cti    {
namespace Porter {

class UnsolicitedHandler : boost::noncopyable
{
protected:

    typedef queue< CtiOutMessage * > om_queue;

    struct packet
    {
        unsigned char *data;
        int len;
        int used;

        u_long  ip;
        u_short port;
    };

    typedef queue< packet * > packet_queue;

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
        unsigned long last_keepalive;
    };

    struct device_record
    {
        CtiDeviceSingleSPtr device;

        bool dirty;

        device_work work;

        long id;

        u_short master;
        u_short slave;

        u_long  ip;
        u_short port;
    };

private:

    CtiFIFOQueue< CtiMessage > _message_queue;

    void startLog( void );
    void haltLog ( void );

    void checkDbReloads( void );

    bool getOutMessages( void );

    void generateOutbounds( void );

    bool generateKeepalives( om_queue &local_queue );
    static bool isDnpKeepaliveNeeded( const device_record &dr, const CtiTime &TimeNow );
    static void generateDnpKeepalive( om_queue &local_queue, const device_record &dr, const CtiTime &TimeNow );

    void readPortQueue( CtiPortSPtr &port, om_queue &local_queue );

    void processInbounds  (void);
    void processDnpInbound  (device_record &dr);
    void processGpuffInbound(device_record &dr);

    void trace( void );
    string describeDevice( const device_record &dr ) const;

    void sendResults( void );

    static void sendDevicePointsFromProtocol(vector<CtiPointDataMsg *> &points, CtiDeviceSingleSPtr &device, CtiConnection &connection);

    device_record *validateDeviceRecord( device_record *dr );

    device_record *getDeviceRecordByID                   ( long device_id );
    device_record *getDeviceRecordByDNPAddress           ( unsigned short master, unsigned short slave );
    device_record *getDeviceRecordByGPUFFDeviceTypeSerial( unsigned short device_type, unsigned short serial );

    CtiPortSPtr _port;
    CtiLogger   _portLog;

protected:

    typedef map< long, device_record * > dr_id_map;
    typedef map< pair< unsigned short, unsigned short >, device_record * > dr_address_map;
    typedef map< pair< unsigned short, unsigned short >, device_record * > dr_type_serial_map;

    dr_id_map          _devices;
    dr_address_map     _addresses;
    dr_type_serial_map _types_serials;

    list< CtiMessage * > _traceList;

    virtual string describePort( void ) const = 0;

    virtual bool setup( void ) = 0;
    virtual void sendOutbound( device_record &dr ) = 0;
    virtual void collectInbounds( void ) = 0;
    virtual void updateDeviceRecord(device_record &dr, const packet &p) = 0;
    virtual void teardown( void ) = 0;

    void traceOutbound( const device_record &dr, int socket_status, list< CtiMessage * > &trace_list );
    void traceInbound ( unsigned long ip, unsigned short port, int status, const unsigned char *message, int count, list< CtiMessage * > &trace_list, const device_record *dr = 0 );

    void handleDnpPacket  (packet *&p);
    void handleGpuffPacket(packet *&p);

    static string ip_to_string(u_long ip);
    static u_long string_to_ip(string ip_string);

public:

    UnsolicitedHandler(CtiPortSPtr &port);
    virtual ~UnsolicitedHandler();

    void run();

    void receiveMessage(CtiMessage *msg);
};


class UnsolicitedMessenger
{
private:

    typedef list< UnsolicitedHandler * > client_list;

    CtiCriticalSection _critical_section;
    client_list _clients;

public:

    UnsolicitedMessenger() {};

    void addClient(UnsolicitedHandler *client);

    void sendMessageToClients(CtiMessage *msg);
};

}
}

