#pragma once

#include <boost/noncopyable.hpp>

#include "port_base.h"
#include "mgr_device.h"
#include "dev_single.h"
#include "msg_dbchg.h"

#include "millisecond_timer.h"
#include "logManager.h"
#include "queue.h"
#include "socket_helper.h"

#include <map>
#include <queue>

namespace Cti::Porter {

class UnsolicitedHandler : boost::noncopyable
{
private:

    typedef std::list< CtiOutMessage * > om_list;

protected:

    struct packet
    {
        unsigned char *data;
        int len;
        int used;

        virtual std::string describeAddress() const = 0;

        enum ProtocolType
        {
            ProtocolTypeInvalid,
            ProtocolTypeDnp,
            ProtocolTypeGpuff,
            //ProtocolTypeUecp  //  Not using UECP unsolicited inbounds

        } protocol;
    };

    struct ip_packet : packet
    {
        std::string  ip;
        u_short port;

        virtual std::string describeAddress() const
        {
            return formatHostAndPort(ip, port);
        }
    };

    struct device_record
    {
        device_record(const CtiDeviceSPtr &device_) :
            device(boost::static_pointer_cast<CtiDeviceSingle>(device_)),
            device_status(ClientErrors::None),
            comm_status(ClientErrors::None)
        {}

        const CtiDeviceSingleSPtr device;

        typedef std::queue< packet * > packet_queue;

        //  always working on the first OM in the list
        om_list      outbound;
        packet_queue inbound;

        CtiXfer xfer;
        YukonError_t device_status;
        YukonError_t comm_status;

        CtiTime last_outbound;
        CtiTime last_keepalive;

        CtiTime timeout;
    };

private:

    CtiDeviceManager &_deviceManager;

    CtiFIFOQueue< CtiMessage > _message_queue;

    /* 
     *  State our communication to the device is in.  
     *  Corresponds with the device_list this device is in. 
     */
    enum DeviceState {
        RequestPending,
        ToGenerate,
        WaitingForData,
        ToDecode,
        RequestComplete
    };

    typedef std::list<device_record *> device_list;
    typedef std::map <device_record *, DeviceState> device_activity_map;

    void startLog();

    void initializeDeviceRecords( void );
    const device_record *insertDeviceRecord(const CtiDeviceSPtr &device);

    void addDeviceRecord   (const long device_id);
    void updateDeviceRecord(const long device_id);
    void deleteDeviceRecord(const long device_id);

    bool handleDbChanges(const Cti::Timing::MillisecondTimer &timer, const unsigned long slice);

    void handleDbChange(const CtiDBChangeMsg &dbchg);
    void handleDeviceChange(long device_id, int change_type);
    void handlePortChange  (long port_id,   int change_type);

    void deletePort( void );
    void updatePort( void );

    void purgeDeviceWork(const device_activity_map::value_type &active_device, const YukonError_t error_code);
    void purgePortWork(const YukonError_t error_code);
    void deactivateTimeout(device_record &dr);

    bool distributeRequests(const Cti::Timing::MillisecondTimer &timer, const unsigned long slice);
    void handleDeviceRequest(OUTMESS *om);
    void handleDeviceError(OUTMESS *om, const YukonError_t error);

    bool startPendingRequests(const Cti::Timing::MillisecondTimer &timer, const unsigned long until);
    void startPendingRequest(device_record *dr);

    bool generateOutbounds(const Cti::Timing::MillisecondTimer &timer, const unsigned long until);
    void generateKeepalives( CtiPortSPtr &port );
    static bool isDnpKeepaliveNeeded( const device_record &dr, const CtiTime &TimeNow );
    static void generateDnpKeepalive( CtiPortSPtr &port, const device_record &dr );
    void readPortQueue( CtiPortSPtr &port, om_list &local_queue );
    virtual bool isPortRateLimited() const;
    void tryGenerate(device_record *dr);

    bool expireTimeouts(const Cti::Timing::MillisecondTimer &timer, const unsigned long until);

    bool processInbounds(const Cti::Timing::MillisecondTimer &timer, const unsigned long until);
    void processInbound(device_record *dr);
    void processGpuffInbound(device_record &dr);
    void processDeviceSingleInbound(device_record &dr);

    void trace( void );
    std::string describeDevice( const device_record &dr ) const;

    bool sendResults(const Cti::Timing::MillisecondTimer &timer, const unsigned long slice);
    void sendResult(device_record *dr);

    static void sendDevicePointsFromProtocol(std::vector<CtiPointDataMsg *> &points, const CtiDeviceSingleSPtr &device, CtiConnection &connection);

    CtiPortSPtr _port;

    Cti::Logging::LogManager _portLogManager;
    Cti::Logging::LoggerPtr  _portLog;

    typedef std::map< long, device_record * > device_record_map;

    device_record_map _device_records;

    om_list _request_queue;

    /* While processing commands, devices are passed around these queues based on their state. */
    device_list _request_pending;
    device_list _to_generate;
    device_list _waiting_for_data;
    device_list _to_decode;
    device_list _request_complete;

    template<class Element>
    bool processQueue(std::list<Element> &queue, const char *function, void (UnsolicitedHandler::*processElement)(Element), const Cti::Timing::MillisecondTimer &timer, const unsigned long until);

    std::multimap<CtiTime, device_record *> _timeouts;

    device_activity_map _active_devices;

    device_activity_map _waiting_devices;

    std::list< CtiMessage * > _traceList;

    bool _shutdown;

protected:

    virtual std::string describePort( void ) const = 0;

    virtual bool setupPort( void ) = 0;
    virtual bool manageConnections( void ) = 0;
    virtual YukonError_t sendOutbound( device_record &dr ) = 0;
    virtual unsigned getDeviceTimeout( const device_record &dr ) const = 0;
    virtual bool collectInbounds( const Cti::Timing::MillisecondTimer & timer, const unsigned long until) = 0;

    virtual void loadDeviceProperties( const std::vector<const CtiDeviceSingle *> &devices ) = 0;

    virtual void addDeviceProperties   (const CtiDeviceSingle &device) = 0;
    virtual void updateDeviceProperties(const CtiDeviceSingle &device) = 0;
    virtual void deleteDeviceProperties(const CtiDeviceSingle &device) = 0;

    virtual void updatePortProperties( void ) = 0;

    virtual bool isDeviceDisconnected( const long device_id ) const = 0;

    virtual std::string describeDeviceAddress( const long device_id ) const = 0;

    device_record* getDeviceRecordById( long device_id ) const;

    void addInboundWork(device_record &dr, packet *p);

    void traceOutbound( const device_record &dr, YukonError_t socket_status );
    void traceInbound ( std::string location, YukonError_t status, const unsigned char *message, int count, const device_record *dr = 0 );

    static bool isGpuffDevice(const CtiDeviceSingle &ds);
    static bool isRdsDevice (const CtiDeviceSingle &ds);

public:

    UnsolicitedHandler(CtiPortSPtr &port, CtiDeviceManager &deviceManager);
    virtual ~UnsolicitedHandler();

    void run();

    void receiveMessage(CtiMessage *msg);

    // Put device in the work queue and set state
    void queueRequestPending(device_record *dr);
    void queueToGenerate(device_record *dr);
    void queueWaitingForData(device_record *dr);
    void queueToDecode(device_record *dr);
    void queueRequestComplete(device_record *dr);
    void setDeviceState(device_list &map, device_record *dr, DeviceState state);
};


class UnsolicitedMessenger
{
private:

    typedef std::set< UnsolicitedHandler * > client_collection;

    CtiCriticalSection _client_mux;
    client_collection _clients;

public:

    UnsolicitedMessenger() {};

    void addClient(UnsolicitedHandler *client);
    void removeClient(UnsolicitedHandler *client);

    void sendMessageToClients(const CtiDBChangeMsg &msg);
};

//  define the instance for disp_thd to write to and all UnsolicitedHandler child classes to register with
extern UnsolicitedMessenger UnsolicitedPortsQueue;

}