#pragma once
#include "yukon.h"

#include <map>
#include <queue>
#include "boost/noncopyable.hpp"

#include "port_base.h"
#include "mgr_device.h"
#include "dev_single.h"

namespace Cti    {
namespace Porter {

class UnsolicitedHandler : boost::noncopyable
{
protected:

    typedef std::queue< CtiOutMessage * > om_queue;

    struct packet
    {
        unsigned char *data;
        int len;
        int used;

        u_long  ip;
        u_short port;

        enum ProtocolType
        {
            ProtocolTypeDnp,
            ProtocolTypeGpuff

        } protocol;
    };

    typedef std::queue< packet * > packet_queue;

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

    CtiDeviceManager &_deviceManager;

    CtiFIFOQueue< CtiMessage > _message_queue;

    void startLog( void );
    void haltLog ( void );

    void initializeDeviceRecords( void );
    static device_record *createDeviceRecord(const CtiDeviceSPtr &device);

    bool addDeviceRecord   (long id);
    bool updateDeviceRecord(long id);
    bool deleteDeviceRecord(long id);

    bool checkDbReloads( void );

    bool getDeviceRequests( void );

    bool generateOutbounds( void );

    bool generateKeepalives( om_queue &local_queue );
    static bool isDnpKeepaliveNeeded( const device_record &dr, const CtiTime &TimeNow );
    static void generateDnpKeepalive( om_queue &local_queue, const device_record &dr, const CtiTime &TimeNow );

    void readPortQueue( CtiPortSPtr &port, om_queue &local_queue );

    bool processInbounds( void );
    bool processDnpInbound  (device_record &dr);
    bool processGpuffInbound(device_record &dr);

    void trace( void );
    string describeDevice( const device_record &dr ) const;

    bool sendResults( void );

    static void sendDevicePointsFromProtocol(vector<CtiPointDataMsg *> &points, CtiDeviceSingleSPtr &device, CtiConnection &connection);

    device_record *validateDeviceRecord( device_record *dr );

    CtiPortSPtr _port;
    CtiLogger   _portLog;

    typedef std::map< long, device_record * > device_record_map;

    device_record_map _devices;

    std::list< CtiMessage * > _traceList;

protected:

    virtual string describePort( void ) const = 0;

    virtual bool setupPort( void ) = 0;
    virtual bool manageConnections( void ) = 0;
    virtual void sendOutbound( device_record &dr ) = 0;
    virtual bool collectInbounds( void ) = 0;
    virtual void teardownPort( void ) = 0;

    virtual void loadDeviceProperties( const std::set<long> &device_ids ) = 0;

    virtual void addDeviceProperties   (const CtiDeviceSingle &device) = 0;
    virtual void updateDeviceProperties(const CtiDeviceSingle &device) = 0;
    virtual void deleteDeviceProperties(const CtiDeviceSingle &device) = 0;

    virtual u_long  getDeviceIp  ( const long device_id ) const = 0;
    virtual u_short getDevicePort( const long device_id ) const = 0;

    device_record *getDeviceRecordById( long device_id );

    static bool validatePacket(packet *&p);

    void traceOutbound( const device_record &dr, int socket_status );
    void traceInbound ( unsigned long ip, unsigned short port, int status, const unsigned char *message, int count, const device_record *dr = 0 );

    static bool isDnpDevice  (const CtiDeviceSingle &ds);
    static bool isGpuffDevice(const CtiDeviceSingle &ds);

    static string ip_to_string(u_long ip);
    static u_long string_to_ip(string ip_string);

public:

    UnsolicitedHandler(CtiPortSPtr &port, CtiDeviceManager &deviceManager);
    virtual ~UnsolicitedHandler();

    void run();

    void receiveMessage(CtiMessage *msg);
};


class UnsolicitedMessenger
{
private:

    typedef std::list< UnsolicitedHandler * > client_list;

    CtiCriticalSection _client_mux;
    client_list _clients;

public:

    UnsolicitedMessenger() {};

    void addClient(UnsolicitedHandler *client);

    void sendMessageToClients(CtiMessage *msg);
};

}
}

