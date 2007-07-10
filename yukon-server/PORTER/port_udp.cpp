/*-----------------------------------------------------------------------------*
*
* File:   port_udp
*
* Date:   2004-feb-16
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2007/07/10 21:09:36 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <map>
#include <queue>

using namespace std;

#include "boost/shared_ptr.hpp"

#include "portglob.h"
#include "mgr_device.h"
#include "dev_dnp.h"
#include "cparms.h"
#include "numstr.h"
#include "msg_trace.h"
#include "msg_dbchg.h"
#include "port_udp.h"
#include "port_base.h"
#include "portdecl.h"  //  for statisticsNewCompletion

// Some Global Manager types to allow us some RTDB stuff.

extern CtiDeviceManager DeviceManager;

extern CtiConnection VanGoghConnection;

extern INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);
extern void commFail(CtiDeviceSPtr &Device);


namespace Cti
{
namespace Porter
{

UDPMessenger UDPInterfaceQueue;

const char *UDPInterface::tickle_packet = "tickle";



void UDPInterface::delete_dr_id_map_value( dr_id_map::value_type map_entry )
{
    delete map_entry.second;
}


UDPInterface::UDPInterface( CtiPortSPtr &port ) :
    _port(port)
{
    UDPInterfaceQueue.addClient(this);
}


void UDPInterface::run( void )
{
    unsigned long tickle_time = 0UL;
    const int tickle_interval = 300;

    OUTMESS *om;

    if( !bindSocket() )
    {
        return;
    }

    if( !_port )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - _port == 0 in UDPInterface::run() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UDP::ExecuteThread started as TID  " << CurrentTID() << endl;
    }

    startLog();

    udp_load_info uli(_port->getPortID(), _devices, _addresses);

    //  grab all of the stored UDP info from the DB
    DeviceManager.apply(applyGetUDPInfo, (void *)&uli);

    //  this is where all of the incoming packets come from
    Inbound inbound(_udp_socket, _packet_queue);
    inbound.start();


    while( !PorterQuit )
    {
        try
        {
            if( MessageQueue.size() > 0 )
            {
                CtiMessage *msg;

                if( (msg = MessageQueue.getQueue()) && (msg->isA() == MSG_DBCHANGE) )
                {
                    CtiDBChangeMsg *db_msg = (CtiDBChangeMsg *)msg;
                    device_record *dr;

                    //  find the device record, if it exists
                    if( db_msg->getId() && (dr = getDeviceRecordByID(db_msg->getId())) )
                    {
                        //  mark it for closer inspection later
                        dr->dirty = true;
                    }
                }
            }

            if( !getOutMessages(100) && _devices_idle )
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
            _devices_idle = true;

            generateOutbounds();
            processInbounds();
            sendResults();

            trace();

            if( PorterQuit || tickle_time < CtiTime::now().seconds() )
            {
                tickleSelf();

                tickle_time  = CtiTime::now().seconds() + tickle_interval;
                tickle_time -= tickle_time % tickle_interval;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION in Cti::Porter::UDPInterface::ExecuteThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if( inbound.isRunning() )
    {
        inbound.interrupt(CtiThread::SHUTDOWN);

        inbound.join();
    }

    //  delete the device records
    for_each(_devices.begin(), _devices.end(), delete_dr_id_map_value);

    //  both of these were invalidated by the deletion
    _devices.clear();
    _addresses.clear();

    haltLog();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UDPInterface::ExecuteThread shutdown." << endl;
    }
}


void UDPInterface::startLog( void )
{
    if( gLogPorts && !_portLog.isRunning() )
    {
        string of("UDP_");

        string comlogdir;
        comlogdir  = gLogDirectory.data();
        comlogdir += "\\Comm";
        // Create a subdirectory called Comm beneath Log.
        CreateDirectoryEx(gLogDirectory.data(), comlogdir.data(), NULL);

        _portLog.setToStdOut(false);  // Not to std out.
        _portLog.setOwnerInfo(CompileInfo);
        _portLog.setOutputPath(comlogdir);
        _portLog.setOutputFile(of);
        _portLog.setWriteInterval(10000);                   // 7/23/01 CGP.

        _portLog.start();
    }
}


void UDPInterface::haltLog( void )
{
    if( gLogPorts )
    {
        _portLog.interrupt(CtiThread::SHUTDOWN);
        _portLog.join();
    }
}


bool UDPInterface::bindSocket( void )
{
    sockaddr_in local;

    _udp_socket = socket(AF_INET, SOCK_DGRAM, 0);    // UDP socket for outbound.

    if( _udp_socket == INVALID_SOCKET )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UDP::UDPInterface::bindSocket() - **** Checkpoint - socket() failed with error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return false;
    }

    local.sin_family      = AF_INET;
    local.sin_addr.s_addr = INADDR_ANY;
    local.sin_port        = htons(_port->getIPPort());

    //  bind() associates a local address and port combination with the socket
    if( bind(_udp_socket, (sockaddr *)&local, sizeof(local)) == SOCKET_ERROR )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UDP::UDPInterface::bindSocket() - **** Checkpoint - bind() failed with error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return false;
    }

    return true;
}


bool UDPInterface::getOutMessages( unsigned wait )
{
    bool om_ready = false;
    unsigned long entries = 0;

    OUTMESS *om;
    INMESS   im;

    om_queue local_queue;

    //  first, check all of our devices to see if we need to issue a keepalive
    dr_id_map::iterator drim_itr;
    for( drim_itr = _devices.begin(); drim_itr != _devices.end(); drim_itr++ )
    {
        device_record *dr = drim_itr->second;

        if( dr && (dr->work.last_outbound + gConfigParms.getValueAsULong("PORTER_DNPUDP_KEEPALIVE", 86400)) < CtiTime::now().seconds() )
        {
            CtiRequestMsg msg(dr->device->getID(), "ping");
            CtiCommandParser parse(msg.CommandString());
            list<CtiMessage *> vg_list, ret_list;
            list<OUTMESS *> om_list;

            dr->device->ExecuteRequest(&msg, parse, vg_list, ret_list, om_list);

            if( !vg_list.empty() || !ret_list.empty() )
            {
                if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Cti::Porter::UDPInterface::getOutMessages - !vg_list.empty() || !ret_list.empty() while creating keepalive request for device \"" << dr->device->getName() << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                delete_list(vg_list);
                vg_list.clear();

                delete_list(ret_list);
                ret_list.clear();
            }

            if( !om_list.empty() )
            {
                //  we just generated an entry - don't wait around for Porter to send us anything
                wait = 25;

                while( !om_list.empty() )
                {
                    local_queue.push(om_list.front());

                    om_list.pop_front();
                }
            }
        }
    }

    bool printed = false;
    REQUESTDATA rq;
    unsigned long size;
    unsigned char priority;

    //  then look to see if Porter's sent us anything
    while( _port->readQueue( &rq, &size, (PPVOID)&om, DCWW_NOWAIT, &priority, &entries) == NORMAL )
    {
        _port->incQueueSubmittal(1, CtiTime());

        if( entries && gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UDPInterface::getOutMessages - " << entries << " additional entries on queue " << __FILE__ << " (" << __LINE__ << ")" << endl;

            printed = true;
        }

        local_queue.push(om);
    }

    //  if we got work, attempt to distribute it to the device records
    if( !local_queue.empty() )
    {
        while( !local_queue.empty() )
        {
            om = local_queue.front();
            local_queue.pop();

            if( om )
            {
                device_record *dr = getDeviceRecordByID(om->TargetID);

                if( !dr || !dr->device )
                {
                    if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cti::Porter::DNPUDP::getOutMessages - no device found for device id (" << om->TargetID << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    //  return an error - this deletes the OM
                    ReturnResultMessage(ErrorDeviceIPUnknown, &im, om);
                }
                else if( dr->dirty )
                {
                    if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cti::Porter::DNPUDP::getOutMessages - waiting for device reload for device id (" << om->TargetID << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    VanGoghConnection.WriteConnQue(CTIDBG_new CtiReturnMsg(dr->device->getID(),
                                                                           om->Request.CommandStr,
                                                                           "Waiting for device reload for device \"" + dr->device->getName() + "\"",
                                                                           NOTNORMAL,
                                                                           om->Request.RouteID,
                                                                           om->Request.MacroOffset,
                                                                           om->Request.Attempt,
                                                                           om->Request.TrxID,
                                                                           om->Request.UserID,
                                                                           om->Request.SOE,
                                                                           CtiMultiMsg_vec()));

                    delete om;
                    om = 0;
                }
                else
                {
                    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cti::Porter::UDPInterface::getOutMessages - queueing work for \"" << dr->device->getName() << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    dr->work.outbound.push(om);

                    om_ready = true;
                }
            }
        }
    }

    return om_ready;
}


bool UDPInterface::getPackets( int wait )
{
    const int granularity = 50;
    bool first_attempt = true,
         packet_read = false;

    packet_queue local_queue;

    //  do this at least once
    while( first_attempt || local_queue.empty() && wait > 0 )
    {
        first_attempt = false;

        //  read from the inbound packet queue
        while( _packet_queue.size() )
        {
            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UDPInterface::getPackets - grabbing packet " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            local_queue.push(_packet_queue.getQueue());
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
            const int DNPHeaderLength   = 10,
                      GPUFFHeaderLength = 13;

            if( p->len >= DNPHeaderLength && p->data[0] == 0x05
                                          && p->data[1] == 0x64 )
            {
                unsigned short header_crc = p->data[8] | (p->data[9] << 8);
                unsigned short crc = Protocol::DNP::Datalink::crc(p->data, DNPHeaderLength - 2);

                //  check the framing bytes
                if( crc == header_crc )
                {
                    unsigned short slave_address  = p->data[6] | (p->data[7] << 8);
                    unsigned short master_address = p->data[4] | (p->data[5] << 8);

                    device_record *dr = getDeviceRecordByAddress(master_address, slave_address);

                    if( !dr )
                    {
                        //  we have no record of this device, we'll try to look it up
                        boost::shared_ptr<CtiDeviceBase> dev_base;

                        //  we didn't have this device in the mapping table, so look it up
                        if( (dev_base = DeviceManager.RemoteGetPortMasterSlaveTypeEqual(_port->getPortID(), master_address, slave_address, TYPE_DNPRTU)) ||
                            (dev_base = DeviceManager.RemoteGetPortMasterSlaveTypeEqual(_port->getPortID(), master_address, slave_address, TYPECBC7020)) )
                        {
                            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Cti::Porter::UDPInterface::getPackets - inserting device \"" << dev_base->getName() << "\" in list " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            //  we found it, insert the new record and packet
                            dr = CTIDBG_new device_record;

                            //  assignment - we're just grabbing a reference, so when this reference goes away, so will
                            //    our hold on this pointer...  no need to delete it
                            dr->device = boost::static_pointer_cast<CtiDeviceSingle>(dev_base);

                            dr->id     = dr->device->getID();

                            dr->master = dr->device->getMasterAddress();
                            dr->slave  = dr->device->getAddress();

                            _devices.insert(make_pair(dr->id, dr));
                            _addresses.insert(make_pair(make_pair(dr->master, dr->slave), dr));

                            dr->work.timeout = CtiTime(YUKONEOT).seconds();
                        }
                        else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Cti::Porter::UDPInterface::getPackets - can't find DNP master/slave (" << master_address << "/" << slave_address << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    //  do we have a device yet?
                    if( dr && dr->device )
                    {
                        if( dr->ip   != p->ip ||
                            dr->port != p->port )
                        {
                            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Cti::Porter::UDPInterface::getPackets - IP or port mismatch for device \"" << dr->device->getName() << "\", updating (" << dr->ip << " != " << p->ip << " || " << dr->port << " != " << p->port << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            dr->ip   = p->ip;
                            dr->port = p->port;

                            dr->device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP,   dr->ip);
                            dr->device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port, dr->port);
                        }

                        //  sends IP and port as pointdata messages
                        sendDeviceInfo(dr);

                        dr->work.inbound.push(p);

                        p = 0;
                    }
                }
                else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Cti::Porter::UDPInterface::getPackets - bad CRC or header on inbound DNP message, cannot assign " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( p->len >= GPUFFHeaderLength && p->data[0] == 0xa5
                                                 && p->data[1] == 0x96 )
            {
                unsigned len, seq, devt, devr, ser, crc;
                bool crc_included, ack_required;

                len  = p->data[2] | (p->data[3] & 0x03 << 8);
                crc_included = p->data[3] & 0x80;
                ack_required = p->data[3] & 0x40;

                seq  = p->data[4] | (p->data[5] << 8);
                devt = p->data[6] | (p->data[7] << 8);
                devr = p->data[8];
                ser  = p->data[9] | (p->data[10] <<  8)
                                  | (p->data[11] << 16)
                                  | (p->data[12] << 24);

                if( p->len < len + 4 )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - incoming packet from "
                             << ((p->ip >> 24) & 0xff) << "."
                             << ((p->ip >> 16) & 0xff) << "."
                             << ((p->ip >>  8) & 0xff) << "."
                             << ((p->ip >>  0) & 0xff) << ":" << p->port
                             << " is too small (" << p->len << " < " << len << " + 4) **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else if( CheckCCITT16CRC(-1, p->data, len + 4) )
                {
                    //  CRC failed.
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - incoming packet from "
                             << ((p->ip >> 24) & 0xff) << "."
                             << ((p->ip >> 16) & 0xff) << "."
                             << ((p->ip >>  8) & 0xff) << "."
                             << ((p->ip >>  0) & 0xff) << ":" << p->port
                             << " failed its CRC check **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - incoming packet from "
                             << ((p->ip >> 24) & 0xff) << "."
                             << ((p->ip >> 16) & 0xff) << "."
                             << ((p->ip >>  8) & 0xff) << "."
                             << ((p->ip >>  0) & 0xff) << ":" << p->port << ": " << endl;

                        dout << "LEN  : " << len << endl;
                        dout << "CRC? : " << crc_included << endl;
                        dout << "ACK? : " << ack_required << endl;
                        dout << "SEQ  : " << seq << endl;
                        dout << "DEVT : " << devt << endl;
                        dout << "DEVR : " << devr << endl;
                        dout << "SER  : " << ser << endl;

                        dout << endl;
                    }

                    int pos = 13, usedbytes = 0;

                    switch( devt )
                    {
                        case 1:  //  Faulted Circuit Indicator
                        {
                            while( usedbytes < len + 2 )
                            {
                                switch( p->data[pos] )
                                {
                                    case 0x00:
                                    {


                                        break;
                                    }
                                    case 0x01:
                                    {

                                    }
                                    case 0x02:
                                    {

                                    }
                                    case 0x03:
                                    {

                                    }
                                    case 0x04:
                                    {

                                    }
                                }
                            }

                            break;
                        }

                        case 2:  //  Neutral Current Sensor
                        {


                            break;
                        }

                        default:
                        {

                        }
                    }
                }
            }
            else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UDPInterface::getPackets() - packet doesn't match any known protocol - discarding " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if( p )
        {
            //  this packet was unhandled, so we trace it
            traceInbound(p->ip, p->port, 0, p->data, p->len, _traceList);

            delete p->data;
            delete p;
        }
    }

    return packet_read;
}


void UDPInterface::sendDeviceInfo( device_record *dr ) const
{
    CtiReturnMsg    *vgMsg = CTIDBG_new CtiReturnMsg(0);
    CtiPointDataMsg *pdm;
    CtiPointSPtr     point;

    if( dr && dr->device )
    {
        if( point = dr->device->getDevicePointOffsetTypeEqual(CtiDeviceSingle::PointOffset_Analog_IPAddress, AnalogPointType) )
        {
            pdm = CTIDBG_new CtiPointDataMsg(point->getID(), dr->ip);
            vgMsg->PointData().push_back(pdm);
        }

        if( point = dr->device->getDevicePointOffsetTypeEqual(CtiDeviceSingle::PointOffset_Analog_Port, AnalogPointType) )
        {
            pdm = CTIDBG_new CtiPointDataMsg(point->getID(), dr->port);
            vgMsg->PointData().push_back(pdm);
        }

        if( !vgMsg->PointData().empty() )
        {
            VanGoghConnection.WriteConnQue(vgMsg);
        }
        else
        {
            delete vgMsg;
        }
    }
}


void UDPInterface::generateOutbounds( void )
{
    dr_id_map::iterator itr = _devices.begin();

    for( ; itr != _devices.end(); itr++ )
    {
        device_record *dr = itr->second;

        sockaddr_in to;

        if( dr && dr->device )
        {
            //  should we look for new work?
            if( dr->device->isTransactionComplete() )
            {
                if( !dr->work.outbound.empty() )
                {
                    //  clear all inbound in case of a new outbound - this isn't ideal...
                    //  what do we do with an unexpected inbound?  does that take priority over new outbounds?

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
                        //  null outmessage, needs to be removed
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
                    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Cti::Porter::UDPInterface::generateOutbound - sending packet to "
                                         << ((dr->ip >> 24) & 0xff) << "."
                                         << ((dr->ip >> 16) & 0xff) << "."
                                         << ((dr->ip >>  8) & 0xff) << "."
                                         << ((dr->ip >>  0) & 0xff) << ":"
                                         << (dr->port) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;

                        for(int xx = 0; xx < dr->work.xfer.getOutCount(); xx++)
                        {
                            dout << " " << CtiNumStr(dr->work.xfer.getOutBuffer()[xx]).hex().zpad(2).toString();
                        }

                        dout << endl;
                    }

                    to.sin_family           = AF_INET;
                    to.sin_addr.S_un.S_addr = htonl(dr->ip);
                    to.sin_port             = htons(dr->port);

                    if( SOCKET_ERROR == sendto(_udp_socket, (char *)dr->work.xfer.getOutBuffer(), dr->work.xfer.getOutCount(), 0, (sockaddr *)&to, sizeof(to)) )
                    {
                        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Cti::Porter::UDPInterface::generateOutbound - **** SENDTO: Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        traceOutbound(dr, WSAGetLastError(), _traceList);
                    }
                    else
                    {
                        traceOutbound(dr, 0, _traceList);

                        dr->work.last_outbound = CtiTime::now().seconds();
                    }
                }

                if( dr->work.xfer.getInCountExpected() > 0 )
                {
                    dr->work.timeout = CtiTime::now().seconds() + gConfigParms.getValueAsInt("PORTER_DNPUDP_TIMEOUT", 20);
                }
                else
                {
                    dr->work.timeout = CtiTime(YUKONEOT).seconds();
                }
            }
        }
    }
}


void UDPInterface::traceOutbound( device_record *dr, int socket_status, list< CtiMessage * > &trace_list )
{
    CtiTraceMsg trace;
    string msg;

    //  set bright yellow for the time message
    trace.setBrightYellow();
    trace.setTrace( CtiTime().asString() );
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    //  set bright cyan for the info message
    trace.setBrightCyan();
    msg  = "  P: " + CtiNumStr(_port->getPortID()).spad(3) + " / UDP port " + CtiNumStr(_port->getIPPort()).spad(5);
    msg += " (" + CtiNumStr((dr->ip >> 24) & 0xff) + "."
                + CtiNumStr((dr->ip >> 16) & 0xff) + "."
                + CtiNumStr((dr->ip >>  8) & 0xff) + "."
                + CtiNumStr((dr->ip >>  0) & 0xff) + ":" + CtiNumStr(dr->port) + ")";

    trace.setTrace(msg);
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    trace.setBrightCyan();
    msg = "  D: " + CtiNumStr(dr->device->getID()).spad(3) + " / " + dr->device->getName();
    trace.setTrace(msg);
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    if(socket_status)
    {
        trace.setBrightRed();
        msg = " OUT: " + CtiNumStr(socket_status).spad(3);
    }
    else
    {
        trace.setBrightWhite();
        msg = " OUT:";
    }
    trace.setTrace(msg);
    trace.setEnd(true);
    trace_list.push_back(trace.replicateMessage());

    //  then print the formatted hex trace
    trace.setBrightGreen();
    CtiPort::traceBytes(dr->work.xfer.getOutBuffer(), dr->work.xfer.getOutCount(), trace, trace_list);
}


void UDPInterface::processInbounds( void )
{
    dr_id_map::iterator itr = _devices.begin();

    for( ; itr !=  _devices.end(); itr++ )
    {
        device_record *dr = itr->second;

        int status = NORMAL;
        packet *p = 0;

        if( dr && dr->device )
        {
            //  are we doing anything?
            if( !dr->device->isTransactionComplete() )
            {
                if( !dr->work.inbound.empty() || (dr->work.xfer.getInCountExpected() == 0) || dr->work.timeout < CtiTime::now().seconds() )
                {
                    if( dr->work.timeout < CtiTime::now().seconds() )
                    {
                        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Cti::Porter::UDPInterface::processInbound - status = READTIMEOUT (" << dr->work.timeout << " < " << CtiTime::now().seconds() << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Cti::Porter::UDPInterface::processInbound - status = READTIMEOUT (" << (p->len - p->used) << " < " << dr->work.xfer.getInCountExpected() << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    else
                    {
                        dr->work.xfer.setInCountActual(0UL);
                    }

                    dr->work.status = dr->device->decode(dr->work.xfer, status);

                    dr->work.pending_decode = false;

                    if( status || dr->work.xfer.getInCountActual() )
                    {
                        traceInbound(dr->ip, dr->port, dr->work.status, dr->work.xfer.getInBuffer(), dr->work.xfer.getInCountActual(), _traceList, dr->device);
                    }

                    if( p && (p->used >= p->len) )
                    {
                        //  we've used the packet up
                        delete p->data;
                        delete p;

                        dr->work.inbound.pop();
                    }

                    //  if we're done, we're not waiting for anything
                    _devices_idle &= dr->device->isTransactionComplete() & dr->work.outbound.empty() & dr->work.inbound.empty();
                }
            }
        }
        else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UDPInterface::processInbound - dr == 0 || dr->device == 0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void UDPInterface::traceInbound( unsigned long ip, unsigned short port, int status, const unsigned char *message, int count, list< CtiMessage * > &trace_list, CtiDeviceSPtr device )
{
    CtiTraceMsg trace;
    string msg;

    //  set bright yellow for the time message
    trace.setBrightYellow();
    trace.setTrace( CtiTime().asString() );
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    //  set bright cyan for the info message
    trace.setBrightCyan();
    msg  = "  P: " + CtiNumStr(_port->getPortID()).spad(3) + " / UDP port " + CtiNumStr(_port->getIPPort()).spad(5);
    msg += " (" + CtiNumStr((ip >> 24) & 0xff) + "."
                + CtiNumStr((ip >> 16) & 0xff) + "."
                + CtiNumStr((ip >>  8) & 0xff) + "."
                + CtiNumStr((ip >>  0) & 0xff) + ":" + CtiNumStr(port).spad(5) + ")";

    trace.setTrace(msg);
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    if(device)
    {
        trace.setBrightCyan();
        msg = "  D: " + CtiNumStr(device->getID()).spad(3) + " / " + device->getName();
        trace.setTrace(msg);
        trace.setEnd(false);
        trace_list.push_back(trace.replicateMessage());
    }

    if(status)
    {
        if( status == ErrPortSimulated )
        {
            trace.setBrightWhite();
            msg = " IN: (simulated, no bytes returned)";
        }
        else
        {
            trace.setBrightRed();
            msg = " IN: " + CtiNumStr(status).spad(3);
        }
    }
    else
    {
        trace.setBrightWhite();
        msg = " IN:";
    }
    trace.setTrace(msg);
    trace.setEnd(true);
    trace_list.push_back(trace.replicateMessage());


    //  then print the formatted hex trace
    if(count > 0)
    {
        trace.setBrightMagenta();
        CtiPort::traceBytes(message, count, trace, trace_list);
    }

    if(status && status != ErrPortSimulated)
    {
        trace.setBrightRed();
        trace.setTrace( FormatError(status) );
        trace.setEnd(true);
        trace_list.push_back(trace.replicateMessage());
        trace.setNormal();
    }
}


void UDPInterface::sendResults( void )
{
    dr_id_map::iterator itr = _devices.begin();

    for( ; itr !=  _devices.end(); itr++ )
    {
        device_record *dr = itr->second;

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

                    OUTMESS *om = dr->work.outbound.front();

                    bool commFailed = (dr->work.status == NORMAL);

                    if( dr->device->adjustCommCounts(commFailed, om->Retry > 0) )
                    {
                        commFail(boost::static_pointer_cast<CtiDeviceBase>(dr->device));
                    }

                    statisticsNewCompletion( om->Port, om->DeviceID, om->TargetID, dr->work.status, om->MessageFlags );

                    OutEchoToIN(dr->work.outbound.front(), &im);

                    dr->device->sendCommResult(&im);

                    //  This method may delete the om!
                    ReturnResultMessage(dr->work.status, &im, dr->work.outbound.front());

                    delete dr->work.outbound.front();
                }

                dr->work.outbound.pop();
            }
        }
        else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UDPInterface::sendResults - dr == 0 || dr->device == 0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void UDPInterface::trace( void )
{
    if(gLogPorts)
    {
        CtiLockGuard<CtiLogger> portlog_guard(_portLog);

        list<CtiMessage *>::const_iterator itr;
        for( itr = _traceList.begin(); itr != _traceList.end(); itr++ )
        {
            _portLog << ((CtiTraceMsg *)(*itr))->getTrace();

            if( ((CtiTraceMsg *)(*itr))->isEnd() )
            {
                _portLog << endl;
            }
        }
    }

    {
        int attempts = 5;
        RWMutexLock::TryLockGuard coutTryGuard(coutMux);

        while(!coutTryGuard.isAcquired() && attempts-- > 0 )
        {
            Sleep(100);
            coutTryGuard.tryAcquire();
        }

        list<CtiMessage *>::const_iterator itr;
        for( itr = _traceList.begin(); itr != _traceList.end(); itr++ )
        {
            SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), (WORD)(((CtiTraceMsg *)(*itr))->getAttributes()));
            cout << ((CtiTraceMsg *)(*itr))->getTrace();

            if( ((CtiTraceMsg *)(*itr))->isEnd() )
            {
                cout << endl;
            }
        }

        SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);
    }

    delete_list(_traceList);
    _traceList.clear();
}


void UDPInterface::tickleSelf( void )
{
    sockaddr_in loopback;

    loopback.sin_family           = AF_INET;
    loopback.sin_addr.S_un.S_addr = htonl(INADDR_LOOPBACK);
    loopback.sin_port             = htons(_port->getIPPort());

    if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UDP::UDPInterface::tickleSelf - tickling UDPInterface::inbound " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    //  tickle the inbound thread
    if( SOCKET_ERROR == sendto(_udp_socket, tickle_packet, strlen(tickle_packet), 0, (sockaddr *)&loopback, sizeof(loopback)) )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UDP::UDPInterface::tickleSelf - **** SENDTO: Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


UDPInterface::Inbound::Inbound( SOCKET &s, CtiFIFOQueue< UDPInterface::packet > &packet_queue ) :
    _udp_socket(s),
    _packet_queue(packet_queue)
{
}


void UDPInterface::Inbound::run()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UDP::UDPInterface::_inbound started as TID  " << CurrentTID() << endl;
    }

    sockaddr_in from;

    unsigned char *recv_buf;
    int recv_len;

    CtiDeviceSingleSPtr dev_single;

    recv_buf = CTIDBG_new unsigned char[16000];  //  should be big enough for any incoming packet

    while( !PorterQuit )
    {
        int fromlen = sizeof(from);
        recv_len = recvfrom(_udp_socket, (char *)recv_buf, 16000, 0, (sockaddr *)&from, &fromlen);

        if( recv_len == SOCKET_ERROR )
        {
            if( WSAGetLastError() == WSAEWOULDBLOCK)
            {
                Sleep(100);
            }
            else
            {
                if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Cti::Porter::UDP::UDPInterface::inboundThread - **** Checkpoint - error " << WSAGetLastError() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                Sleep(500);
            }

            continue;
        }
        else if( recv_len == strlen(UDPInterface::tickle_packet) )
        {
            if( !memcmp(recv_buf, UDPInterface::tickle_packet, recv_len) )
            {
                if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Cti::Porter::UDPInterface::InboundThread - received tickle packet " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);

                dout << CtiTime() << " Cti::Porter::UDPInterface::InboundThread - packet received from "
                                  << ((p->ip >> 24) & 0xff) << "."
                                  << ((p->ip >> 16) & 0xff) << "."
                                  << ((p->ip >>  8) & 0xff) << "."
                                  << ((p->ip >>  0) & 0xff) << ":"
                                  << (p->port) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;

                for(int xx = 0; xx < recv_len; xx++)
                {
                    dout << " " << CtiNumStr(recv_buf[xx]).hex().zpad(2).toString();
                }

                dout << endl;
            }

            _packet_queue.putQueue(p);
        }
        else
        {
            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UDPInterface::InboundThread - **** Checkpoint - unable to allocate packet **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    delete [] recv_buf;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UDPInterface::InboundThread shutdown." << endl;
    }
}


UDPInterface::device_record *UDPInterface::validateDeviceRecord( device_record *dr )
{
    //  should we reload the device?
    if( dr && dr->device && dr->dirty && !dr->device->isDirty() )
    {
        //  have the addresses changed?
        if( dr->master != dr->device->getMasterAddress() ||
            dr->slave  != dr->device->getAddress() )
        {
            //  if so, remove it from the collections
            _devices.erase(dr->id);
            _addresses.erase(make_pair(dr->master, dr->slave));

            delete dr;
            dr = 0;
        }
        else
        {
            //  not dirty any more - we can reset our own dirty flag
            dr->dirty = false;
        }
    }

    return dr;
}


UDPInterface::device_record *UDPInterface::getDeviceRecordByID( long device_id )
{
    device_record *retval = 0;

    dr_id_map::iterator drim_itr = _devices.find(device_id);

    if( drim_itr != _devices.end() )
    {
        retval = validateDeviceRecord(drim_itr->second);
    }

    return retval;
}


UDPInterface::device_record *UDPInterface::getDeviceRecordByAddress( unsigned short master, unsigned short slave )
{
    device_record *retval = 0;

    dr_address_map::iterator dram_itr = _addresses.find(make_pair(master, slave));

    if( dram_itr != _addresses.end() )
    {
        retval = validateDeviceRecord(dram_itr->second);
    }

    return retval;
}


void UDPInterface::applyGetUDPInfo(const long unusedid, CtiDeviceSPtr RemoteDevice, void *uli)
{
    udp_load_info *info = (udp_load_info *)uli;

    if( info && info->portid == RemoteDevice->getPortID() )
    {
        if( RemoteDevice->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP) &&
            RemoteDevice->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port) )
        {
            device_record *dr = CTIDBG_new device_record;

            dr->device = boost::static_pointer_cast<CtiDeviceSingle>(RemoteDevice);

            dr->id     = dr->device->getID();

            dr->master = dr->device->getMasterAddress();
            dr->slave  = dr->device->getAddress();

            dr->ip   = dr->device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_IP);
            dr->port = dr->device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Port);

            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UDPInterface::applyGetUDPInfo - loading device \""
                     << dr->device->getName() << "\" (" << ((dr->ip >> 24) & 0xff) << "."
                                                        << ((dr->ip >> 16) & 0xff) << "."
                                                        << ((dr->ip >>  8) & 0xff) << "."
                                                        << ((dr->ip >>  0) & 0xff) << ":"
                                                        << (dr->port) << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            dr->work.timeout = CtiTime(YUKONEOT).seconds();

            info->devices.insert(make_pair(dr->device->getID(), dr));

            info->addresses.insert(make_pair(make_pair(dr->device->getMasterAddress(), dr->device->getAddress()), dr));
        }
    }
}


UDPInterface::push_back(CtiMessage *msg)
{
    _message_queue.putQueue(msg);
}


UDPMessenger::UDPMessenger()
{
}


void UDPMessenger::addClient(UDPInterface *client)
{
    CtiLockGuard< CtiCriticalSection > lock(_critical_section);

    _clients.push_back(client);
}


void UDPMessenger::push_back(CtiMessage *msg)
{
    CtiLockGuard< CtiCriticalSection > lock(_critical_section);

    list<UDPInterface *>::iterator itr;

    for( itr = _clients.begin(); itr != _clients.end(); )
    {
        if( *itr )
        {
            //  push the message, then increment the iterator
            (*itr++)->push_back(msg);
        }
        else
        {
            //  erase() returns the next element
            itr = _clients.erase(itr);
        }
    }
}


}
}


