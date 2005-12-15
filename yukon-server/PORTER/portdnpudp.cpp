/*-----------------------------------------------------------------------------*
*
* File:   portdnpudp
*
* Date:   2004-feb-16
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/12/15 21:20:53 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <map>
#include <queue>

using namespace std;

#include "cparms.h"
#include "dlldefs.h"
#include "dllyukon.h"
#include "dllbase.h"
#include "mgr_device.h"
#include "dev_dnp.h"
#include "guard.h"
#include "logger.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "mgr_route.h"
#include "numstr.h"
#include "portdecl.h"
#include "portglob.h"
#include "port_base.h"
#include "queue.h"
#include "numstr.h"
#include "sema.h"
#include "thread_monitor.h"


#ifdef VSLICK_TAG_WORKAROUND
typedef CtiDeviceSingle *CtiDeviceSingleSPtr;
#else
typedef shared_ptr<CtiDeviceSingle> CtiDeviceSingleSPtr;
#endif


// Some Global Manager types to allow us some RTDB stuff.

extern CtiPortManager PortManager;
extern CtiDeviceManager DeviceManager;

extern CtiConnection VanGoghConnection;

extern INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);


namespace Cti
{
namespace Porter
{
namespace DNPUDP
{

CtiQueue< CtiOutMessage, less<CtiOutMessage> > OutMessageQueue;

struct packet
{
    unsigned char *data;
    int len;
    int used;

    u_long  ip;
    u_short port;
};

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

typedef map< unsigned short, device_record * > dr_address_map;
typedef map< long,           device_record * > dr_id_map;

static CtiMutex packet_mux;
static packet_queue packets;

static const char *tickle_packet = "tickle";

static dr_address_map addresses;
static dr_id_map      devices;

static SOCKET udp_socket;

static bool devices_idle;

bool getOutMessages( unsigned wait );
bool getPackets    ( int wait );

void generateOutbound( dr_id_map::value_type element );
void processInbound  ( dr_id_map::value_type element );
void sendResults     ( dr_id_map::value_type element );

void InboundThread( void *Dummy );

void applyGetUDPInfo(const long unusedid, CtiDeviceSPtr RemoteDevice, void *prtid);

device_record *getDeviceRecordByAddress( unsigned short address );
device_record *getDeviceRecordByID     ( long device_id );


void ExecuteThread( void *Dummy )
{
    unsigned long tickle_time = 0UL;
    const int tickle_interval = 300;

    sockaddr_in local, loopback;

    OUTMESS *om;

    udp_socket = socket(AF_INET, SOCK_DGRAM, 0);    // UDP socket for outbound.

    if( udp_socket == INVALID_SOCKET )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Cti::Porter::DNPUDP::ExecuteThread - **** Checkpoint - socket() failed with error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return;
    }

    local.sin_family      = AF_INET;
    local.sin_addr.s_addr = INADDR_ANY;
    local.sin_port        = htons(gConfigParms.getValueAsInt("PORTER_DNPUDP_PORT", 5500));

    loopback.sin_family           = AF_INET;
    loopback.sin_addr.S_un.S_addr = htonl(INADDR_LOOPBACK);
    loopback.sin_port             = htons(gConfigParms.getValueAsInt("PORTER_DNPUDP_PORT", 5500));

    //  bind() associates a local address and port combination with the socket
    if( bind(udp_socket, (sockaddr *)&local, sizeof(local)) == SOCKET_ERROR )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Cti::Porter::DNPUDP::ExecuteThread - **** Checkpoint - bind() failed with error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Cti::Porter::DNPUDP::ExecuteThread started as TID  " << CurrentTID() << endl;
    }

    //  grab all of the stored UDP info from the DB - this is done before the other thread is started, so we don't need to mux
    DeviceManager.apply(applyGetUDPInfo, (void *)gConfigParms.getValueAsInt("PORTER_DNPUDP_DB_PORTID", 0));

    //  this is where all of the incoming packets come from
    _beginthread(InboundThread, 0, NULL);

    while( !PorterQuit )
    {
        try
        {
            if( !getOutMessages(100) && devices_idle )
            {
                //  if we haven't gotten any new OMs and the devices aren't doing anything at the moment, we can take some time to wait for new packets
                getPackets(2500);
            }
            else
            {
                //  otherwise, we'll just peek quick-like
                getPackets(100);
            }

            //  this will be cleared by any active devices in processInbound
            devices_idle = true;

            for_each(devices.begin(), devices.end(), generateOutbound);
            for_each(devices.begin(), devices.end(), processInbound);
            for_each(devices.begin(), devices.end(), sendResults);
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION in Cti::Porter::DNPUDP::ExecuteThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if( PorterQuit || tickle_time < RWTime::now().seconds() )
        {
            tickle_time = RWTime::now().seconds() + tickle_interval;

            if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Cti::Porter::DNPUDP::ExecuteThread - tickling InboundThread " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            //  tickle the inbound thread
            if( SOCKET_ERROR == sendto(udp_socket, tickle_packet, strlen(tickle_packet), 0, (sockaddr *)&loopback, sizeof(loopback)) )
            {
                if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Cti::Porter::DNPUDP::ExecuteThread - **** SENDTO: Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }

    dr_id_map::iterator drim_itr;
    for( drim_itr = devices.begin(); drim_itr != devices.end(); drim_itr++ )
    {
        delete drim_itr->second;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Cti::Porter::DNPUDP::ExecuteThread shutdown." << endl;
    }
}


bool getOutMessages( unsigned wait )
{
    bool om_read = false;

    OUTMESS *om;
    INMESS   im;

    om_queue local_queue;

    if( om = OutMessageQueue.getQueue(wait) )
    {
        om_read = true;

        local_queue.push(om);

        //  grab everything else waiting on the queue as long as we're here...
        //    this should prevent us from being starved when we have a lot of comms going
        size_t entries = OutMessageQueue.entries();

        if( entries && gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Cti::Porter::DNPUDP::getOutMessages - " << entries << " additional entries on queue " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        while( entries-- && (om = OutMessageQueue.getQueue(25)) )
        {
            local_queue.push(om);
        }

        while( !local_queue.empty() )
        {
            om = local_queue.front();
            local_queue.pop();

            if( om )
            {
                device_record *dr = getDeviceRecordByID(om->TargetID);

                if( dr && dr->device )
                {
                    if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cti::Porter::DNPUDP::getOutMessages - queueing work for \"" << dr->device->getName() << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    dr->work.outbound.push(om);
                }
                else
                {
                    if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cti::Porter::DNPUDP::getOutMessages - no device found for device id (" << om->TargetID << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    //  return a "No Config Data" error - this deletes the OM
                    ReturnResultMessage(NoConfigData, &im, om);
                }
            }
        }
    }
/*
    dr_id_map::iterator drim_itr;
    for( drim_itr = devices.begin(); drim_itr != devices.end(); drim_itr++ )
    {
        device_record *dr = drim_itr->second;

        if( dr && dr->work.last_outbound < RWTime::now.seconds() + gConfigParms.getValueAsULong("PORTER_DNPUDP_KEEPALIVE", 86400) )
        {
            CtiRequestMsg msg(dr->device->getID(), "ping");
            CtiCommandParser parse(msg.CommandString());
            OUTMESS *om = new OUTMESS;

            om->DeviceID = om->TargetID = dr->device->getID();

            CtiDeviceBase::ExecuteRequest(
        }
    }
*/

    return om_read;
}


bool getPackets( int wait )
{
    const int granularity = 50;
    bool first_attempt = true,
         packet_read = false;

    packet_queue local_queue;

    //  do this at least once
    while( first_attempt || local_queue.empty() && wait > 0 )
    {
        first_attempt = false;

        //  try to read from the inbound packet queue
        {
            CtiLockGuard< CtiMutex > guard(packet_mux, granularity);

            if( guard.isAcquired() )
            {
                while( !packets.empty() )
                {
                    if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cti::Porter::DNPUDP::getPackets - grabbing packet " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    local_queue.push(packets.front());
                    packets.pop();
                }
            }
            else if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Cti::Porter::DNPUDP::getPackets - packet_mux not aquired " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if( local_queue.empty() && wait > 0 )
        {
            CTISleep(granularity);

            wait -= granularity;
        }
    }

    packet_read = !local_queue.empty();

    while( !local_queue.empty() )
    {
        packet *p;

        p = local_queue.front();
        local_queue.pop();

        if( p )
        {
            //  this is where we'd add any other protocols in the future - right now, this is very DNP-centric
            const int DNPHeaderLength = 10;

            if( p->len >= DNPHeaderLength )
            {
                unsigned short header_crc = p->data[8] | (p->data[9] << 8);
                unsigned short crc = Protocol::DNP::Datalink::crc(p->data, DNPHeaderLength - 2);

                //  check the framing bytes
                if( p->data[0] == 0x05 && p->data[1] == 0x64 && crc == header_crc )
                {
                    unsigned short dnp_slave_address = p->data[6] | (p->data[7] << 8);

                    device_record *dr = getDeviceRecordByAddress(dnp_slave_address);

                    //  we have no record of this device, we'll try to look it up
                    if( !dr )
                    {
                        boost::shared_ptr<CtiDeviceBase> dev_base;

                        //  we didn't have this device in the mapping table, so look it up
                        if( dev_base = DeviceManager.RemoteGetPortRemoteTypeEqual(gConfigParms.getValueAsInt("PORTER_DNPUDP_DB_PORTID", 0), dnp_slave_address, TYPE_DNPRTU) )
                        {
                            CtiDeviceSingleSPtr dev_single = boost::static_pointer_cast<CtiDeviceSingle>(dev_base);

                            if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Cti::Porter::DNPUDP::getPackets - inserting device \"" << dev_base->getName() << "\" in list " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            //  we found it, insert the new record and packet
                            dr = CTIDBG_new device_record;

                            dr->device = dev_single;

                            dr->work.timeout = RWTime(YUKONEOT).seconds();

                            devices.insert  (make_pair(dev_single->getID(),      dr));
                            addresses.insert(make_pair(dev_single->getAddress(), dr));
                        }
                        else if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Cti::Porter::DNPUDP::getPackets - can't find DNP slave (" << dnp_slave_address << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    //  do we have a device yet?
                    if( dr )
                    {
                        if( dr->ip   != p->ip ||
                            dr->port != p->port )
                        {
                            if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Cti::Porter::DNPUDP::getPackets - IP or port mismatch for device \"" << dr->device->getName() << "\", updating (" << dr->ip << " != " << p->ip << " || " << dr->port << " != " << p->port << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            dr->ip   = p->ip;
                            dr->port = p->port;

                            dr->device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP,   dr->ip);
                            dr->device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port, dr->port);
                        }

                        dr->work.inbound.push(p);

                        p = 0;
                    }
                }
                else if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Cti::Porter::DNPUDP::getPackets - bad CRC or header on inbound DNP message, cannot assign " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Cti::Porter::DNPUDP::getPackets - read " << p->len << " bytes, not enough for a full DNP header - discarding " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if( p )
        {
            delete p->data;
            delete p;
        }
    }

    return packet_read;
}


void generateOutbound( dr_id_map::value_type element )
{
    device_record *dr = element.second;

    sockaddr_in to;

    if( dr && dr->device )
    {
        //  should we look for new work?
        if( dr->device->isTransactionComplete() )
        {
            if( !dr->work.outbound.empty() )
            {
                //  clear all inbound in case of a new outbound - this isn't ideal...
                //  what do we do with an unexpected inbound?  does that take priority over new outbounds?  ideally, it would,
                //    but we aren't really set up for that kind of a system...

                while( !dr->work.inbound.empty() )
                {
                    delete dr->work.inbound.front();
                    dr->work.inbound.pop();
                }

                //  if we aren't doing anything else and we have an available outmessage, try to use it
                if( dr->work.outbound.front() )
                {
                    dr->device->recvCommRequest(dr->work.outbound.front());

                    dr->work.pending_decode = false;
                }
                else
                {
                    //  broken outmessage, needs to be removed
                    dr->work.outbound.pop();
                }
            }
            else if( !dr->work.inbound.empty() )
            {
                //  no new outbound work, so the unexpected inbound can be processed

                //  there is no outmessage, so we don't call recvCommRequest -
                //    we have to call the Cti::Device::DNP-specific initUnsolicited
                shared_ptr<Cti::Device::DNP> dev = boost::static_pointer_cast<Cti::Device::DNP>(dr->device);

                dev->initUnsolicited();

                dr->work.pending_decode = false;
            }
        }

        //  are we ready to generate anything?
        if( !dr->work.pending_decode && !dr->device->isTransactionComplete() )
        {
            dr->device->generate(dr->work.xfer);

            dr->work.pending_decode = true;

            if( dr->work.xfer.getOutCount() > 0 )
            {
                if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Cti::Porter::DNPUDP::generateOutbound - sending packet to "
                                     << ((dr->ip >> 24) & 0xff) << "."
                                     << ((dr->ip >> 16) & 0xff) << "."
                                     << ((dr->ip >>  8) & 0xff) << "."
                                     << ((dr->ip >>  0) & 0xff) << ":"
                                     << (dr->port) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;

                    for(int xx = 0; xx < dr->work.xfer.getOutCount(); xx++)
                    {
                        dout << " " << RWCString(CtiNumStr(dr->work.xfer.getOutBuffer()[xx]).hex().zpad(2));
                    }

                    dout << endl;
                }

                to.sin_family           = AF_INET;
                to.sin_addr.S_un.S_addr = htonl(dr->ip);
                to.sin_port             = htons(dr->port);

                if( SOCKET_ERROR == sendto(udp_socket, (char *)dr->work.xfer.getOutBuffer(), dr->work.xfer.getOutCount(), 0, (sockaddr *)&to, sizeof(to)) )
                {
                    if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cti::Porter::DNPUDP::generateOutbound - **** SENDTO: Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    dr->work.last_outbound = RWTime::now().seconds();
                }
            }

            if( dr->work.xfer.getInCountExpected() > 0 )
            {
                dr->work.timeout = RWTime::now().seconds() + gConfigParms.getValueAsInt("PORTER_DNPUDP_TIMEOUT", 20);
            }
            else
            {
                dr->work.timeout = RWTime(YUKONEOT).seconds();
            }
        }
    }
}


void processInbound( dr_id_map::value_type element )
{
    device_record *dr = element.second;

    int status = NORMAL;
    packet *p = 0;

    if( dr && dr->device )
    {
        //  are we doing anything?
        if( !dr->device->isTransactionComplete() )
        {
            if( !dr->work.inbound.empty() || (dr->work.xfer.getInCountExpected() == 0) || dr->work.timeout < RWTime::now().seconds() )
            {
                if( dr->work.timeout < RWTime::now().seconds() )
                {
                    if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Cti::Porter::DNPUDP::processInbound - status = READTIMEOUT (" << dr->work.timeout << " < " << RWTime::now().seconds() << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    status = READTIMEOUT;

                    dr->work.xfer.setInCountActual(0UL);
                }
                else if( !dr->work.inbound.empty() )
                {
                    if( p = dr->work.inbound.front() )
                    {
                        if( p->len - p->used < dr->work.xfer.getInCountExpected() )
                        {
                            if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Cti::Porter::DNPUDP::processInbound - status = READTIMEOUT (" << (p->len - p->used) << " < " << dr->work.xfer.getInCountExpected() << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            status = READTIMEOUT;

                            dr->work.xfer.setInCountActual(p->len - p->used);
                        }
                        else
                        {
                            dr->work.xfer.setInCountActual(dr->work.xfer.getInCountExpected());
                        }

                        memcpy(dr->work.xfer.getInBuffer(), p->data + p->used, dr->work.xfer.getInCountActual());

                        p->used += dr->work.xfer.getInCountActual();
                    }
                    else
                    {
                        dr->work.inbound.pop();
                    }
                }

                dr->work.status = dr->device->decode(dr->work.xfer, status);

                dr->work.pending_decode = false;

                if( p && (p->used >= p->len) )
                {
                    //  we've used the packet up
                    delete p->data;
                    delete p;

                    dr->work.inbound.pop();
                }

                //  if we're done, we're not waiting for anything
                devices_idle &= dr->device->isTransactionComplete() & dr->work.outbound.empty() & dr->work.inbound.empty();
            }
        }
    }
    else if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Cti::Porter::DNPUDP::processInbound - dr == 0 || dr->device == 0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void sendResults( dr_id_map::value_type element )
{
    device_record *dr = element.second;

    INMESS im;

    if( dr && dr->device )
    {
        //  we may be done with this transaction, but do we have anyone to send it back to?
        if( dr->device->isTransactionComplete() && !dr->work.outbound.empty() )
        {
            if( dr->work.outbound.front() )
            {
                dr->device->sendDispatchResults(VanGoghConnection);

                im.EventCode = dr->work.status;

                OutEchoToIN(dr->work.outbound.front(), &im);

                dr->device->sendCommResult(&im);

                //  This method may delete the om!
                ReturnResultMessage(dr->work.status, &im, dr->work.outbound.front());

                delete dr->work.outbound.front();
            }

            dr->work.outbound.pop();
        }
    }
    else if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Cti::Porter::DNPUDP::sendResults - dr == 0 || dr->device == 0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void InboundThread( void *Dummy )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Cti::Porter::DNPUDP::InboundThread started as TID  " << CurrentTID() << endl;
    }

    sockaddr_in local,
                from;

    const int DNPHeaderLength = 10;

    unsigned char *recv_buf;
    int recv_len;

    CtiDeviceSingleSPtr dev_single;

    recv_buf = CTIDBG_new unsigned char[16000];  //  should be big enough for any incoming packet

    while( !PorterQuit )
    {
        int fromlen = sizeof(from);
        recv_len = recvfrom(udp_socket, (char *)recv_buf, 16000, 0, (sockaddr *)&from, &fromlen);

        if( recv_len == SOCKET_ERROR )
        {
            if( WSAGetLastError() == WSAEWOULDBLOCK)
            {
                Sleep(100);
            }
            else
            {
                if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Cti::Porter::DNPUDP::InboundThread - **** Checkpoint - error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                Sleep(500);
            }

            continue;
        }
        else if( recv_len == strlen(tickle_packet) )
        {
            if( !memcmp(recv_buf, tickle_packet, recv_len) )
            {
                if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Cti::Porter::DNPUDP::InboundThread - received tickle packet " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                continue;
            }
        }

        packet *p;

        if( p = CTIDBG_new packet )
        {
            p->ip   = ntohl(from.sin_addr.S_un.S_addr);
            p->port = ntohs(from.sin_port);

            p->len  = recv_len;
            p->used = 0;

            p->data = CTIDBG_new unsigned char[recv_len];
            memcpy(p->data, recv_buf, recv_len);

            if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);

                dout << RWTime() << " Cti::Porter::DNPUDP::InboundThread - packet received from "
                                 << ((p->ip >> 24) & 0xff) << "."
                                 << ((p->ip >> 16) & 0xff) << "."
                                 << ((p->ip >>  8) & 0xff) << "."
                                 << ((p->ip >>  0) & 0xff) << ":"
                                 << (p->port) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;

                for(int xx = 0; xx < recv_len; xx++)
                {
                    dout << " " << RWCString(CtiNumStr(recv_buf[xx]).hex().zpad(2));
                }

                dout << endl;
            }

            {
                CtiLockGuard< CtiMutex > guard(packet_mux, 15000);

                if( guard.isAcquired() )
                {
                    packets.push(p);
                }
                else if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Cti::Porter::DNPUDP::InboundThread - **** Checkpoint - access_mux not aquired **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        else
        {
            if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Cti::Porter::DNPUDP::InboundThread - **** Checkpoint - unable to allocate packet **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    delete [] recv_buf;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Cti::Porter::DNPUDP::InboundThread shutdown." << endl;
    }
}


device_record *getDeviceRecordByID( long device_id )
{
    device_record *retval = 0;

    dr_id_map::iterator drim_itr = devices.find(device_id);

    if( drim_itr != devices.end() )
    {
        retval = drim_itr->second;
    }

    return retval;
}


device_record *getDeviceRecordByAddress( unsigned short address )
{
    device_record *retval = 0;

    dr_address_map::iterator dram_itr = addresses.find(address);

    if( dram_itr != addresses.end() )
    {
        retval = dram_itr->second;
    }

    return retval;
}


static void applyGetUDPInfo(const long unusedid, CtiDeviceSPtr RemoteDevice, void *prtid)
{
    LONG portid = (LONG)prtid;

    if( portid == RemoteDevice->getPortID() )
    {
        if( RemoteDevice->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP) &&
            RemoteDevice->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port) )
        {
            device_record *dr = CTIDBG_new device_record;

            dr->device = boost::static_pointer_cast<CtiDeviceSingle>(RemoteDevice);

            dr->ip   = dr->device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP);
            dr->port = dr->device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port);

            if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Cti::Porter::DNPUDP::applyGetUDPInfo - loading device \""
                     << dr->device->getName() << "\" (" << ((dr->ip >> 24) & 0xff) << "."
                                                        << ((dr->ip >> 16) & 0xff) << "."
                                                        << ((dr->ip >>  8) & 0xff) << "."
                                                        << ((dr->ip >>  0) & 0xff) << ":"
                                                        << (dr->port) << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            dr->work.timeout = RWTime(YUKONEOT).seconds();

            devices.insert  (make_pair(dr->device->getID(),      dr));
            addresses.insert(make_pair(dr->device->getAddress(), dr));
        }
    }
}


}
}
}

