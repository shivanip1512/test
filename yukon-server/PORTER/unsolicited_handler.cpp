#include "yukon.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "unsolicited_handler.h"

#include "prot_gpuff.h"

#include "portglob.h"
#include "mgr_device.h"
#include "dev_dnp.h"
#include "cparms.h"
#include "msg_dbchg.h"
#include "msg_trace.h"
#include "portdecl.h"  //  for statisticsNewAttempt

using namespace std;

extern CtiConnection VanGoghConnection;
extern CtiDeviceManager DeviceManager;

extern INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);
extern bool processCommResult(INT CommResult, LONG DeviceID, LONG TargetID, bool RetryGTZero, CtiDeviceSPtr &Device);

namespace Cti    {
namespace Porter {

UnsolicitedMessenger UnsolicitedPortsQueue;

using Protocols::GpuffProtocol;

UnsolicitedHandler::UnsolicitedHandler(CtiPortSPtr &port) :
    _port(port)
{
    UnsolicitedPortsQueue.addClient(this);

    startLog();
}

UnsolicitedHandler::~UnsolicitedHandler()
{
    //  delete the device records
    delete_assoc_container(_devices);

    //  these were all invalidated by the deletion
    _devices.clear();
    _addresses.clear();
    _types_serials.clear();

    haltLog();
}


void UnsolicitedHandler::startLog( void )
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

void UnsolicitedHandler::haltLog( void )
{
    if( gLogPorts )
    {
        _portLog.interrupt(CtiThread::SHUTDOWN);
        _portLog.join();
    }
}


void UnsolicitedHandler::run( void )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << describePort() << " started as TID  " << CurrentTID() << endl;
    }

    if( !setup() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << describePort() << " setup failed" << endl;
        }

        return;
    }

    while( !PorterQuit )
    {
        try
        {
            checkDbReloads();

            getOutMessages();

            generateOutbounds();

            collectInbounds();
            processInbounds();

            trace();

            sendResults();
        }
        catch( ... )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION in " << describePort() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    teardown();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << describePort() << " shutdown." << endl;
    }
}


void UnsolicitedHandler::checkDbReloads( void )
{
    while( _message_queue.entries() )
    {
        auto_ptr<CtiMessage> msg(_message_queue.getQueue(1));  //  1 ms wait

        if( msg.get() && msg->isA() == MSG_DBCHANGE )
        {
            auto_ptr<CtiDBChangeMsg> db_msg((CtiDBChangeMsg *)msg.release());
            device_record *dr;

            //  find the device record, if it exists
            if( db_msg->getId() && (dr = getDeviceRecordByID(db_msg->getId())) )
            {
                if( db_msg->getTypeOfChange() == ChangeTypeDelete )
                {
                    _devices.erase(dr->id);
                    _addresses.erase(make_pair(dr->master, dr->slave));

                    delete dr;
                }
                else
                {
                    //  mark it for closer inspection later
                    dr->dirty = true;
                }
            }
        }
    }
}


bool UnsolicitedHandler::getOutMessages()
{
    bool om_ready = false;

    OUTMESS *om;

    om_queue local_queue;

    generateKeepalives(local_queue);

    readPortQueue(_port, local_queue);

    //  if we got work, attempt to distribute it to the device records
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
                INMESS im;
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
                                                                       om->Request.GrpMsgID,
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
                    dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::getOutMessages - queueing work for \"" << dr->device->getName() << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                dr->work.outbound.push(om);

                om_ready = true;
            }
        }
    }

    return om_ready;
}


bool UnsolicitedHandler::generateKeepalives(om_queue &local_queue)
{
    bool keepalive_generated = false;

    //  check all of our devices to see if we need to issue a keepalive
    dr_id_map::iterator drim_itr = _devices.begin();

    const CtiTime TimeNow;

    for( ; drim_itr != _devices.end(); drim_itr++ )
    {
        device_record *dr = drim_itr->second;

        if( dr && dr->device )
        {
            switch( dr->device->getType() )
            {
                case TYPE_DNPRTU:
                case TYPECBC7020:
                case TYPECBCDNP:
                {
                    if( isDnpKeepaliveNeeded(*dr, TimeNow) )
                    {
                        generateDnpKeepalive(local_queue, *dr, TimeNow);
                        dr->work.last_keepalive = TimeNow.seconds();
                        keepalive_generated = true;
                    }

                    break;
                }
            }
        }
    }

    return keepalive_generated;
}


bool UnsolicitedHandler::isDnpKeepaliveNeeded(const device_record &dr, const CtiTime &TimeNow)
{
    unsigned long last_communication = max(dr.work.last_outbound, dr.work.last_keepalive);
    unsigned long keepalive_period   = gConfigParms.getValueAsULong("PORTER_DNPUDP_KEEPALIVE", 86400);

    return (last_communication + keepalive_period) < TimeNow.seconds();
}

void UnsolicitedHandler::generateDnpKeepalive(om_queue &local_queue, const device_record &dr, const CtiTime &TimeNow)
{
    CtiRequestMsg msg(dr.device->getID(), "ping");
    CtiCommandParser parse(msg.CommandString());
    list<CtiMessage *> vg_list, ret_list;
    list<OUTMESS *> om_list;

    dr.device->ExecuteRequest(&msg, parse, vg_list, ret_list, om_list);

    delete_container(vg_list);
    delete_container(ret_list);

    while( !om_list.empty() )
    {
        local_queue.push(om_list.front());

        om_list.pop_front();
    }
}


void UnsolicitedHandler::generateOutbounds( void )
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

                        dr->work.active = true;

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
                    switch( dr->device->getType() )
                    {
                        case TYPE_DNPRTU:
                        case TYPECBC7020:
                        case TYPECBCDNP:
                        {
                            //  there is no outmessage, so we don't call recvCommRequest -
                            //    we have to call the Cti::Device::DNP-specific initUnsolicited
                            shared_ptr<Device::DNP> dev = boost::static_pointer_cast<Device::DNP>(dr->device);

                            dev->initUnsolicited();

                            dr->work.active = true;

                            dr->work.pending_decode = false;

                            break;
                        }
                    }
                }
            }

            //  are we ready to generate anything?
            if( !dr->work.pending_decode && !dr->device->isTransactionComplete() )
            {
                dr->device->generate(dr->work.xfer);

                dr->work.pending_decode = true;

                sendOutbound(*dr);

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


string UnsolicitedHandler::describeDevice( const device_record &dr ) const
{
    ostringstream ostr;

    ostr << dr.device->getName() << " (" << ip_to_string(dr.ip) << ":" << dr.port << ")";

    return ostr.str();
}


void UnsolicitedHandler::traceOutbound( const device_record &dr, int status, list< CtiMessage * > &trace_list )
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
    msg  = "  P: " + CtiNumStr(_port->getPortID()).spad(3) + " / " + describePort();

    trace.setTrace(msg);
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    trace.setBrightCyan();
    msg = "  D: " + CtiNumStr(dr.device->getID()).spad(3) + " / " + describeDevice(dr);
    trace.setTrace(msg);
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    if( status )
    {
        trace.setBrightRed();
        msg = " OUT: " + CtiNumStr(status).spad(3);
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
    CtiPort::traceBytes(dr.work.xfer.getOutBuffer(), dr.work.xfer.getOutCount(), trace, trace_list);
}


void UnsolicitedHandler::traceInbound( unsigned long ip, unsigned short port, int status, const unsigned char *message, int count, list< CtiMessage * > &trace_list, const device_record *dr )
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
    msg  = "  P: " + CtiNumStr(_port->getPortID()).spad(3) + " / " + describePort();

    trace.setTrace(msg);
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    trace.setBrightCyan();
    if( dr && dr->device )
    {
        msg = "  D: " + CtiNumStr(dr->device->getID()).spad(3) + " / " + describeDevice(*dr);
    }
    else
    {
        msg = " (" + ip_to_string(ip) + ":" + CtiNumStr(port) + ")";
    }
    trace.setTrace(msg);
    trace.setEnd(false);
    trace_list.push_back(trace.replicateMessage());

    if( status )
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
    if( count > 0 )
    {
        trace.setBrightMagenta();
        CtiPort::traceBytes(message, count, trace, trace_list);
    }

    if( status && status != ErrPortSimulated )
    {
        trace.setBrightRed();
        trace.setTrace( FormatError(status) );
        trace.setEnd(true);
        trace_list.push_back(trace.replicateMessage());
        trace.setNormal();
    }
}


void UnsolicitedHandler::readPortQueue(CtiPortSPtr &port, om_queue &local_queue)
{
    unsigned long entries = 0;
    bool printed = false;
    REQUESTDATA rq;
    unsigned long size;
    unsigned char priority;
    OUTMESS *om;

    //  then look to see if Porter's sent us anything
    while( port->readQueue( &rq, &size, (PPVOID)&om, DCWW_NOWAIT, &priority, &entries) == NORMAL )
    {
        port->incQueueSubmittal(1, CtiTime());

        if( !printed && entries && gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::getOutMessages - " << entries << " additional entries on queue " << __FILE__ << " (" << __LINE__ << ")" << endl;

            printed = true;
        }

        local_queue.push(om);
    }
}


void UnsolicitedHandler::handleDnpPacket(packet *&p)
{
    unsigned short header_crc = p->data[8] | (p->data[9] << 8);
    unsigned short crc = Protocol::DNP::Datalink::crc(p->data, Protocol::DNP::DatalinkPacket::HeaderLength - 2);

    //  check the framing bytes
    if( crc == header_crc )
    {
        unsigned short slave_address  = p->data[6] | (p->data[7] << 8);
        unsigned short master_address = p->data[4] | (p->data[5] << 8);

        device_record *dr = getDeviceRecordByDNPAddress(master_address, slave_address);

        if( !dr )
        {
            //  we have no record of this device, we'll try to look it up
            boost::shared_ptr<CtiDeviceBase> dev_base;

            //  we didn't have this device in the mapping table, so look it up
            if( (dev_base = DeviceManager.RemoteGetPortMasterSlaveTypeEqual(_port->getPortID(), master_address, slave_address, TYPE_DNPRTU)) ||
                (dev_base = DeviceManager.RemoteGetPortMasterSlaveTypeEqual(_port->getPortID(), master_address, slave_address, TYPECBC7020)) ||
                (dev_base = DeviceManager.RemoteGetPortMasterSlaveTypeEqual(_port->getPortID(), master_address, slave_address, TYPECBCDNP)) )
            {
                if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::getPackets - inserting device \"" << dev_base->getName() << "\" in list " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                //  we found it, insert the new record and packet
                dr = CTIDBG_new device_record;

                //  assignment - we're just grabbing a reference, so when this reference goes away, so will
                //    our hold on this pointer...  no need to delete it
                dr->device = boost::static_pointer_cast<CtiDeviceSingle>(dev_base);

                dr->id     = dr->device->getID();
                dr->dirty  = dr->device->isDirty();

                dr->master = dr->device->getMasterAddress();
                dr->slave  = dr->device->getAddress();

                _devices.insert(make_pair(dr->id, dr));
                _addresses.insert(make_pair(make_pair(dr->master, dr->slave), dr));

                dr->work.timeout = CtiTime(YUKONEOT).seconds();
            }
            else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::getPackets - can't find DNP master/slave (" << master_address << "/" << slave_address << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        //  do we have a device yet?
        if( dr && dr->device )
        {
            updateDeviceRecord(*dr, *p);

            dr->work.inbound.push(p);

            p = 0;
        }
    }
    else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::getPackets - bad CRC or header on inbound DNP message, cannot assign " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void UnsolicitedHandler::handleGpuffPacket(packet *&p)
{
    unsigned len, devt, ser;
    bool crc_included, ack_required;

    crc_included = p->data[2] & 0x80;
    ack_required = p->data[2] & 0x40;

    len  = ((p->data[2] & 0x03) << 8) | p->data[3];

    if( p->len < len + 4 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - incoming packet from " << ip_to_string(p->ip) <<  ":" << p->port
            << " is too small (" << p->len << " < " << len << " + 4) **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else if( crc_included && CheckCCITT16CRC(-1, p->data, len + 4) )
    {
        //  CRC failed.
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - incoming packet from " << ip_to_string(p->ip) << ":" << p->port
            << " failed its CRC check **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        devt = (p->data[8] << 8) | p->data[9];

        ser  = (p->data[11] << 24) |
               (p->data[12] << 16) |
               (p->data[13] <<  8) |  p->data[14];

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - incoming packet from " << ip_to_string(p->ip) << ":" << p->port << ": " << endl;
        }

        GpuffProtocol::describeFrame(p->data, p->len, len, crc_included, ack_required, devt, ser);

        device_record *dr = getDeviceRecordByGPUFFDeviceTypeSerial(devt, ser);

        try
        {
            if( !dr )
            {
                //  we have no record of this device, we'll try to look it up
                boost::shared_ptr<CtiDeviceBase> dev_base;

                int type = -1;

                switch( devt )
                {
                case 3:
                case 1:     type = TYPE_FCI;                break;
                case 2:     type = TYPE_NEUTRAL_MONITOR;    break;
                }

                //  we didn't have this device in the mapping table, so look it up
                if( dev_base = DeviceManager.RemoteGetPortRemoteTypeEqual(_port->getPortID(), ser, type) )
                {
                    //  we found it, insert the new record and packet
                    dr = CTIDBG_new device_record;

                    //  assignment - we're just grabbing a reference, so when this reference goes away, so will
                    //    our hold on this pointer...  no need to delete it
                    dr->device = boost::static_pointer_cast<CtiDeviceSingle>(dev_base);

                    dr->id     = dr->device->getID();
                    dr->dirty  = dr->device->isDirty();

                    dr->master = 0;
                    dr->slave  = dr->device->getAddress();

                    _devices.insert(make_pair(dr->id, dr));
                    _types_serials.insert(make_pair(make_pair(devt, dr->slave), dr));

                    dr->work.timeout = CtiTime(YUKONEOT).seconds();
                }
            }
        }
        catch( ... )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        //  do we have a device yet?
        try
        {
            if( dr && dr->device )
            {
                updateDeviceRecord(*dr, *p);

                dr->work.inbound.push(p);

                traceInbound(p->ip, p->port, 0, p->data, p->len, _traceList);

                p = 0;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - no device found for GPUFF serial (" << ser << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        catch( ... )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
}


UnsolicitedHandler::device_record *UnsolicitedHandler::validateDeviceRecord( device_record *dr )
{
    try
    {
        //  should we reload the device?
        if( dr && dr->device && dr->dirty && !dr->device->isDirty() )
        {
            //  have the addresses changed?
            if( dr->master != dr->device->getMasterAddress() ||
                dr->slave  != dr->device->getAddress() )
            {
                //  if so, remove it from the collections

                try
                {
                    _devices.erase(dr->id);
                }
                catch( ... )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                try
                {
                    _addresses.erase(make_pair(dr->master, dr->slave));
                }
                catch( ... )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                try
                {
                    delete dr;
                    dr = 0;
                }
                catch( ... )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
            else
            {
                //  not dirty any more - we can reset our own dirty flag
                dr->dirty = false;
            }
        }
    }
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return dr;
}


UnsolicitedHandler::device_record *UnsolicitedHandler::getDeviceRecordByID( long device_id )
{
    device_record *retval = 0;

    dr_id_map::iterator drim_itr = _devices.find(device_id);

    if( drim_itr != _devices.end() )
    {
        retval = validateDeviceRecord(drim_itr->second);
    }

    return retval;
}


UnsolicitedHandler::device_record *UnsolicitedHandler::getDeviceRecordByDNPAddress( unsigned short master, unsigned short slave )
{
    device_record *retval = 0;

    dr_address_map::iterator dram_itr = _addresses.find(make_pair(master, slave));

    if( dram_itr != _addresses.end() )
    {
        retval = validateDeviceRecord(dram_itr->second);
    }

    return retval;
}


UnsolicitedHandler::device_record *UnsolicitedHandler::getDeviceRecordByGPUFFDeviceTypeSerial( unsigned short device_type, unsigned short serial )
{
    device_record *retval = 0;

    try
    {
        dr_type_serial_map::iterator drtsm_itr = _types_serials.find(make_pair(device_type, serial));

        if( drtsm_itr != _types_serials.end() )
        {
            retval = validateDeviceRecord(drtsm_itr->second);
        }
    }
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return retval;
}


void UnsolicitedHandler::processInbounds( void )
{
    dr_id_map::iterator itr = _devices.begin();

    for( ; itr != _devices.end(); itr++ )
    {
        device_record *dr = itr->second;

        if( dr && dr->device )
        {
            switch( dr->device->getType() )
            {
                case TYPE_DNPRTU:
                case TYPECBC7020:
                case TYPECBCDNP:
                {
                    processDnpInbound(*dr);

                    break;
                }

                case TYPE_FCI:
                case TYPE_NEUTRAL_MONITOR:
                {
                    processGpuffInbound(*dr);

                    break;
                }
            }
        }
        else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::processInbounds - dr == 0 || dr->device == 0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void UnsolicitedHandler::processDnpInbound(device_record &dr)
{
    //  are we doing anything?
    if( dr.device->isTransactionComplete() )
    {
        return;
    }

    int status = NORMAL;

    if( dr.work.timeout < CtiTime::now().seconds() )
    {
        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::processDnpInbound() - status = READTIMEOUT (" << dr.work.timeout << " < " << CtiTime::now().seconds() << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        status = READTIMEOUT;

        dr.work.xfer.setInCountActual(0UL);
    }
    else if( !dr.work.inbound.empty() )
    {
        packet *p = dr.work.inbound.front();

        if( p )
        {
            if( p->len - p->used < dr.work.xfer.getInCountExpected() )
            {
                if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::processDnpInbound() - status = READTIMEOUT (" << (p->len - p->used) << " < " << dr.work.xfer.getInCountExpected() << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                status = READTIMEOUT;

                dr.work.xfer.setInCountActual(p->len - p->used);
            }
            else
            {
                dr.work.xfer.setInCountActual(dr.work.xfer.getInCountExpected());
            }

            memcpy(dr.work.xfer.getInBuffer(), p->data + p->used, dr.work.xfer.getInCountActual());

            p->used += dr.work.xfer.getInCountActual();

            if( p->used >= p->len )
            {
                //  we've used the packet up
                delete p->data;
                delete p;

                dr.work.inbound.pop();
            }
        }
        else
        {
            dr.work.inbound.pop();
        }
    }
    else
    {
        dr.work.xfer.setInCountActual(0UL);
    }

    dr.work.status = dr.device->decode(dr.work.xfer, status);

    dr.work.pending_decode = false;

    if( status || dr.work.xfer.getInCountActual() )
    {
        int om_retry = 0;

        traceInbound(dr.ip, dr.port, dr.work.status, dr.work.xfer.getInBuffer(), dr.work.xfer.getInCountActual(), _traceList, &dr);

        if( !dr.work.outbound.empty() && dr.work.outbound.front() )
        {
            om_retry = dr.work.outbound.front()->Retry;
        }

        processCommResult(status, dr.id, dr.id, om_retry > 0, boost::static_pointer_cast<CtiDeviceBase>(dr.device));

        if( status && !dr.device->isTransactionComplete() )
        {
            //  error, but we're going to keep going
            //    this is the equivalent of a "retry" in the old school code
            statisticsNewAttempt(dr.device->getPortID(), dr.id, dr.id, status, MessageFlag_StatisticsRequested);
        }
    }
}


void UnsolicitedHandler::processGpuffInbound(device_record &dr)
{
    if( dr.work.inbound.empty() )
    {
        return;
    }

    packet *p = dr.work.inbound.front();

    dr.work.inbound.pop();

    if( !p )
    {
        return;
    }

    vector<CtiPointDataMsg *> points;

    unsigned last_seq = dr.device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Sequence);
    unsigned seq      = GpuffProtocol::decode(p->data, last_seq, dr.device->getName(), points);

    if( seq != last_seq )
    {
        sendDevicePointsFromProtocol(points, dr.device, VanGoghConnection);

        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Sequence, seq);  // Save the current sequence.
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " GPUFF device " << dr.device->getName() << " sequence number " << seq << " already processed." << endl;
    }

    delete p->data;
    delete p;
}


void UnsolicitedHandler::sendDevicePointsFromProtocol(vector<CtiPointDataMsg *> &points, CtiDeviceSingleSPtr &device, CtiConnection &connection)
{
    auto_ptr<CtiMultiMsg> m(new CtiMultiMsg());

    vector<CtiPointDataMsg *>::iterator itr = points.begin();

    for( ; itr != points.end(); ++itr )
    {
        CtiPointDataMsg *pd = *itr;

        if( pd )
        {
            CtiPointSPtr p = device->getDevicePointOffsetTypeEqual(pd->getId(), pd->getType());

            if( p )
            {
                pd->setId(p->getID());

                m->insert(pd);
            }
            else
            {
                delete pd;
            }
        }
    }

    if( !m->getData().empty() )
    {
        connection.WriteConnQue(m.release());
    }
}


void UnsolicitedHandler::trace()
{
    if( gLogPorts )
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

        while( !coutTryGuard.isAcquired() && attempts-- > 0 )
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

    delete_container(_traceList);
    _traceList.clear();
}


void UnsolicitedHandler::sendResults( void )
{
    dr_id_map::iterator itr = _devices.begin();

    for( ; itr !=  _devices.end(); itr++ )
    {
        device_record *dr = itr->second;

        if( dr && dr->device )
        {
            if( dr->work.active && dr->device->isTransactionComplete() )
            {
                dr->device->sendDispatchResults(VanGoghConnection);

                dr->work.active = false;

                //  we may be done with this transaction, but do we have anyone to send it back to?
                if( !dr->work.outbound.empty() )
                {
                    if( dr->work.outbound.front() )
                    {
                        INMESS im;

                        im.EventCode = dr->work.status;

                        OUTMESS *om = dr->work.outbound.front();

                        //  dr->work.outbound.front()->Retry will never be nonzero for DNP devices - they handle retries internally
                        processCommResult(dr->work.status, dr->id, dr->id, dr->work.outbound.front()->Retry > 0, boost::static_pointer_cast<CtiDeviceBase>(dr->device));

                        /*
                        bool commFailed = (dr->work.status == NORMAL);

                        //  om->Retry will never be nonzero for DNP devices - they handle retries internally
                        if( dr->device->adjustCommCounts(commFailed, om->Retry > 0) )
                        {
                            commFail(boost::static_pointer_cast<CtiDeviceBase>(dr->device));
                        }
                        */

                        OutEchoToIN(om, &im);

                        dr->device->sendCommResult(&im);

                        //  if dr->work.status is nonzero, ReturnResultMessage() calls SendError(),
                        //    which calls statisticsNewCompletion()...  so we don't need to call it if we're not in error
                        if( !dr->work.status )
                        {
                            statisticsNewCompletion( om->Port, om->DeviceID, om->TargetID, dr->work.status, om->MessageFlags );
                        }

                        //  This method may delete the OM!
                        ReturnResultMessage(dr->work.status, &im, om);

                        //  If ReturnResultMessage doesn't delete the OM (i.e. wasn't an error condition), then we'll delete it
                        delete om;
                    }

                    dr->work.outbound.pop();
                }
            }
        }
        else if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::sendResults - dr == 0 || dr->device == 0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void UnsolicitedHandler::receiveMessage(CtiMessage *msg)
{
    _message_queue.putQueue(msg);
}


void UnsolicitedMessenger::addClient(UnsolicitedHandler *client)
{
    CtiLockGuard< CtiCriticalSection > lock(_critical_section);

    _clients.push_back(client);
}


void UnsolicitedMessenger::sendMessageToClients(CtiMessage *msg)
{
    CtiLockGuard< CtiCriticalSection > lock(_critical_section);

    client_list::iterator itr;

    for( itr = _clients.begin(); itr != _clients.end(); )
    {
        if( *itr )
        {
            (*itr)->receiveMessage(msg);
            ++itr;
        }
        else
        {
            //  erase() returns the next element
            itr = _clients.erase(itr);
        }
    }
}


string UnsolicitedHandler::ip_to_string(u_long ip)
{
    ostringstream ostr;

    ostr << ((ip >> 24) & 0xff) << ".";
    ostr << ((ip >> 16) & 0xff) << ".";
    ostr << ((ip >>  8) & 0xff) << ".";
    ostr << ((ip >>  0) & 0xff);

    return ostr.str();
}

u_long UnsolicitedHandler::string_to_ip(string ip_string)
{
    int pos = 0;
    u_long ip = 0;

    while( pos < ip_string.length() )
    {
        ip <<= 8;

        ip |= atoi(ip_string.c_str() + pos);

        pos = ip_string.find_first_of(".", pos + 1);

        if( pos != string::npos )
        {
            pos++;  //  move past the dot if we found one
        }
    }

    return ip;
}


}
}

