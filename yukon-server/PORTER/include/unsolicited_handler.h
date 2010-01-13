#pragma once
#include "yukon.h"

#include <map>
#include <queue>
#include "boost/noncopyable.hpp"

#include "port_base.h"
#include "mgr_device.h"
#include "dev_single.h"
#include "msg_dbchg.h"

namespace Cti    {
namespace Porter {

class UnsolicitedHandler : boost::noncopyable
{
private:

    typedef std::queue< CtiOutMessage * > om_queue;

protected:

    struct packet
    {
        unsigned char *data;
        int len;
        int used;

        u_long  ip;
        u_short port;

        enum ProtocolType
        {
            ProtocolTypeInvalid,
            ProtocolTypeDnp,
            ProtocolTypeGpuff

        } protocol;
    };

    struct device_record
    {
        device_record(const CtiDeviceSPtr &device_, const long id_) :
            device(boost::static_pointer_cast<CtiDeviceSingle>(device_)),
            id(id_)
        {}

        const CtiDeviceSingleSPtr device;

        const long id;

        struct device_work
        {
            device_work() :
                status(NoError),
                pending_decode(false),
                active(false),
                timeout(YUKONEOT)
            {
            }

            typedef std::queue< packet * > packet_queue;

            //  always working on the first entry in the queue
            om_queue     outbound;
            packet_queue inbound;

            CtiXfer xfer;
            int     status;

            bool pending_decode;
            bool active;

            CtiTime timeout;
            CtiTime last_outbound;
            CtiTime last_keepalive;

        } work;
    };

private:

    CtiDeviceManager &_deviceManager;

    CtiFIFOQueue< CtiMessage > _message_queue;

    void startLog( void );
    void haltLog ( void );

    void initializeDeviceRecords( void );
    const device_record *insertDeviceRecord(const CtiDeviceSPtr &device);

    bool addDeviceRecord   (const long device_id);
    bool updateDeviceRecord(const long device_id);
    bool deleteDeviceRecord(const long device_id);

    bool handleDbChanges( void );

    bool handleDbChange(const CtiDBChangeMsg &dbchg);
    bool handleDeviceChange(long device_id, int change_type);
    bool handlePortChange  (long port_id,   int change_type);

    bool deletePort( void );
    bool updatePort( void );

    void purgeDeviceWork(device_record *dr, int error_code);
    void purgePortWork(int error_code);

    bool manageDevices( void );
    bool communicate( void );

    bool handleDeviceRequests( void );

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

    static void sendDevicePointsFromProtocol(vector<CtiPointDataMsg *> &points, const CtiDeviceSingleSPtr &device, CtiConnection &connection);

    CtiPortSPtr _port;
    CtiLogger   _portLog;

    typedef std::map< long, device_record * > device_record_map;

    device_record_map _device_records;

    std::set< device_record * > _active_devices;

    std::list< CtiMessage * > _traceList;

    bool _shutdown;

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

    virtual void updatePortProperties( void ) = 0;

    virtual bool isDeviceDisconnected( const long device_id ) const = 0;

    virtual u_long  getDeviceIp  ( const long device_id ) const = 0;
    virtual u_short getDevicePort( const long device_id ) const = 0;

    device_record *getDeviceRecordById( long device_id );

    void addInboundWork(device_record *dr, packet *&p);

    void traceOutbound( const device_record &dr, int socket_status );
    void traceInbound ( unsigned long ip, unsigned short port, int status, const unsigned char *message, int count, const device_record *dr = 0 );

    static bool isDnpDevice  (const CtiDeviceSingle &ds);
    static bool isGpuffDevice(const CtiDeviceSingle &ds);

    virtual string ip_to_string(u_long ip) const = 0;

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

    void sendMessageToClients(const CtiDBChangeMsg *msg);
};

//  define the instance for disp_thd to write to and all UnsolicitedHandler child classes to register with
extern UnsolicitedMessenger UnsolicitedPortsQueue;

}
}

