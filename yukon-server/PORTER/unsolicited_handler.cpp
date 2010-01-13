#include "yukon.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "unsolicited_handler.h"

#include "prot_gpuff.h"

#include "portglob.h"
#include "dev_dnp.h"
#include "cparms.h"
#include "msg_dbchg.h"
#include "msg_trace.h"
#include "portdecl.h"  //  for statisticsNewAttempt

using namespace std;

extern CtiConnection VanGoghConnection;

extern INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);
extern bool processCommStatus(INT CommResult, LONG DeviceID, LONG TargetID, bool RetryGTZero, CtiDeviceSPtr &Device);

namespace Cti    {
namespace Porter {

UnsolicitedMessenger UnsolicitedPortsQueue;

using Protocols::GpuffProtocol;

UnsolicitedHandler::UnsolicitedHandler(CtiPortSPtr &port, CtiDeviceManager &deviceManager) :
    _port(port),
    _deviceManager(deviceManager),
    _shutdown(false)
{
    UnsolicitedPortsQueue.addClient(this);
}

UnsolicitedHandler::~UnsolicitedHandler()
{
    //  delete the device records
    delete_assoc_container(_device_records);
}


void UnsolicitedHandler::startLog( void )
{
    if( gLogPorts && !_portLog.isRunning() )
    {
        string of(describePort() + "_");

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
    startLog();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << describePort() << " started as TID  " << CurrentTID() << endl;
    }

    if( !setupPort() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << describePort() << " setupPort() failed" << endl;
    }
    else
    {
        initializeDeviceRecords();

        while( !PorterQuit && !_shutdown )
        {
            bool active = false;

            try
            {
                active |= handleDbChanges();

                active |= manageDevices();

                active |= communicate();

                active |= sendResults();

                trace();
            }
            catch( ... )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION in " << describePort() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            if( active )
            {
                Sleep(50);
            }
            else
            {
                Sleep(500);
            }
        }

        teardownPort();
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << describePort() << " shutdown." << endl;
    }

    haltLog();
}


void UnsolicitedHandler::initializeDeviceRecords( void )
{
    vector<CtiDeviceManager::ptr_type> port_devices;
    set<long> device_ids;

    _deviceManager.getDevicesByPortID(_port->getPortID(), port_devices);

    for each(CtiDeviceManager::ptr_type device in port_devices)
    {
        if( device )
        {
            const device_record *dr = insertDeviceRecord(device);

            if( dr )
            {
                device_ids.insert(dr->id);
            }
        }
    }

    loadDeviceProperties(device_ids);
}


const UnsolicitedHandler::device_record *UnsolicitedHandler::insertDeviceRecord(const CtiDeviceSPtr &device)
{
    if( !device )
    {
        return 0;
    }

    device_record *dr = CTIDBG_new device_record(device, device->getID());

    _device_records[dr->id] = dr;

    return dr;
}


bool UnsolicitedHandler::handleDbChanges( void )
{
    bool anythingForUs = false;

    //  limit the number we pull off each time through to make sure
    //    we don't get stuck in a producer/consumer loop
    size_t max_entries = _message_queue.size();

    while( max_entries-- )
    {
        auto_ptr<CtiMessage> msg(_message_queue.getQueue(1));  //  1 ms wait

        if( msg.get() && msg->isA() == MSG_DBCHANGE )
        {
            anythingForUs |= handleDbChange(*static_cast<CtiDBChangeMsg *>(msg.get()));
        }
    }

    return anythingForUs;
}


bool UnsolicitedHandler::handleDbChange(const CtiDBChangeMsg &dbchg)
{
    if( !dbchg.getId() || dbchg.getDatabase() != ChangePAODb )
    {
        return false;
    }

    switch( resolvePAOCategory(dbchg.getCategory()) )
    {
        case PAO_CATEGORY_DEVICE:   return handleDeviceChange(dbchg.getId(), dbchg.getTypeOfChange());
        case PAO_CATEGORY_PORT:     return handlePortChange  (dbchg.getId(), dbchg.getTypeOfChange());
        default:                    return false;
    }
}


bool UnsolicitedHandler::handleDeviceChange(long device_id, int change_type)
{
    switch( change_type )
    {
        case ChangeTypeDelete:  return deleteDeviceRecord(device_id);
        case ChangeTypeAdd:     return addDeviceRecord   (device_id);
        case ChangeTypeUpdate:  return updateDeviceRecord(device_id);
        default:                return false;
    }
}


bool UnsolicitedHandler::deleteDeviceRecord(const long device_id)
{
    device_record *dr = getDeviceRecordById(device_id);

    if( dr )
    {
        _device_records.erase(device_id);

        purgeDeviceWork(dr, IDNF);

        deleteDeviceProperties(*dr->device);

        delete dr;
    }

    return dr;
}


bool UnsolicitedHandler::addDeviceRecord(const long device_id)
{
    CtiDeviceSPtr device = _deviceManager.getDeviceByID(device_id);

    if( device && device->getPortID() == _port->getPortID() )
    {
        const device_record *dr = insertDeviceRecord(device);

        if( dr && dr->device )
        {
            addDeviceProperties(*dr->device);
        }
    }

    return true;
}


bool UnsolicitedHandler::updateDeviceRecord(const long device_id)
{
    device_record *dr = getDeviceRecordById(device_id);

    if( dr && dr->device )
    {
        if( dr->device->getPortID() != _port->getPortID() )
        {
            //  device was moved to another port
            return deleteDeviceRecord(device_id);
        }

        if( dr->device->isInhibited() )
        {
            purgeDeviceWork(dr, DEVICEINHIBITED);
        }

        updateDeviceProperties(*dr->device);
    }
    else
    {
        //  device isn't in our list - check to see if it just got added to our port
        return addDeviceRecord(device_id);
    }

    return dr;
}


bool UnsolicitedHandler::handlePortChange(long port_id, int change_type)
{
    if( port_id != _port->getPortID() )
    {
        return false;
    }

    switch( change_type )
    {
        case ChangeTypeDelete:  return deletePort();
        case ChangeTypeUpdate:  return updatePort();
        default:                return false;
    }
}


bool UnsolicitedHandler::deletePort(void)
{
    purgePortWork(BADPORT);

    return _shutdown = true;
}


bool UnsolicitedHandler::updatePort(void)
{
    if( _port->isInhibited() )
    {
        purgePortWork(PORTINHIBITED);
    }

    updatePortProperties();

    return true;
}


void UnsolicitedHandler::purgePortWork(int error_code)
{
    for each( device_record *dr in _active_devices )
    {
        purgeDeviceWork(dr, error_code);
    }

    _active_devices.clear();
}


void UnsolicitedHandler::purgeDeviceWork(device_record *dr, int error_code)
{
    if( dr )
    {
        while( !dr->work.outbound.empty() )
        {
            OUTMESS *om = dr->work.outbound.front();

            INMESS im;

            OutEchoToIN(dr->work.outbound.front(), &im);

            ReturnResultMessage(error_code, &im, om);

            delete om;

            dr->work.outbound.pop();
        }

        _active_devices.erase(dr);
    }
}


bool UnsolicitedHandler::manageDevices(void)
{
    bool active = false;

    active |= manageConnections();

    active |= handleDeviceRequests();

    return active;
}


bool UnsolicitedHandler::handleDeviceRequests(void)
{
    bool new_requests = false;

    om_queue request_queue;

    generateKeepalives(request_queue);

    readPortQueue(_port, request_queue);

    //  if we got work, attempt to distribute it to the device records
    while( !request_queue.empty() )
    {
        OUTMESS *om = request_queue.front();
        request_queue.pop();

        if( !om )
        {
            continue;
        }

        if( _port->isInhibited() )
        {
            //  return an error - this deletes the OM
            INMESS im;
            ReturnResultMessage(PORTINHIBITED, &im, om);
            continue;
        }

        device_record *dr = getDeviceRecordById(om->TargetID);

        if( !dr || !dr->device )
        {
            if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::DNPUDP::getOutMessages - no device found for device id (" << om->TargetID << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            //  return an error - this deletes the OM
            INMESS im;
            ReturnResultMessage(IDNF, &im, om);
            continue;
        }
        if( dr->device->isInhibited() )
        {
            //  return an error - this deletes the OM
            INMESS im;
            ReturnResultMessage(DEVICEINHIBITED, &im, om);
            continue;
        }
        if( isDeviceDisconnected(dr->id) )
        {
            //  return an error - this deletes the OM
            INMESS im;
            ReturnResultMessage(ErrorDeviceNotConnected, &im, om);
            continue;
        }

        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::getOutMessages - queueing work for \"" << dr->device->getName() << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        dr->work.outbound.push(om);

        _active_devices.insert(dr);

        new_requests = true;
    }

    return new_requests;
}


bool UnsolicitedHandler::generateKeepalives(om_queue &local_queue)
{
    bool keepalive_generated = false;

    const CtiTime TimeNow;

    //  check all of our devices to see if we need to issue a keepalive
    for each( const pair<long, device_record *> &dr_pair in _device_records )
    {
        device_record *dr = dr_pair.second;

        if( !dr || !dr->device || dr->device->isInhibited() )
        {
            continue;
        }

        if( isDnpDevice(*dr->device) &&
            isDnpKeepaliveNeeded(*dr, TimeNow) )
        {
            generateDnpKeepalive(local_queue, *dr, TimeNow);
            dr->work.last_keepalive = TimeNow;
            keepalive_generated = true;
        }
    }

    return keepalive_generated;
}


bool UnsolicitedHandler::isDnpDevice(const CtiDeviceSingle &ds)
{
    switch( ds.getType() )
    {
        case TYPE_DNPRTU:
        case TYPECBC7020:
        case TYPECBCDNP:
        {
            return true;
        }
    }

    return false;
}


bool UnsolicitedHandler::isGpuffDevice(const CtiDeviceSingle &ds)
{
    switch( ds.getType() )
    {
        case TYPE_FCI:
        case TYPE_NEUTRAL_MONITOR:
        {
            return true;
        }
    }

    return false;
}


bool UnsolicitedHandler::isDnpKeepaliveNeeded(const device_record &dr, const CtiTime &TimeNow)
{
    CtiTime last_communication = max(dr.work.last_outbound, dr.work.last_keepalive);
    u_long  keepalive_period   = gConfigParms.getValueAsULong("PORTER_DNPUDP_KEEPALIVE", 86400);

    return (last_communication + keepalive_period) < TimeNow;
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


bool UnsolicitedHandler::generateOutbounds( void )
{
    bool areDevicesActive = false;

    for each( device_record *dr in _active_devices )
    {
        if( !dr || !dr->device )
        {
            _active_devices.erase(dr);

            continue;
        }

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
                if( isDnpDevice(*dr->device) )
                {
                    //  there is no outmessage, so we don't call recvCommRequest -
                    //    we have to call the Cti::Device::DNP-specific initUnsolicited
                    shared_ptr<Device::DNP> dnp_device = boost::static_pointer_cast<Device::DNP>(dr->device);

                    dnp_device->initUnsolicited();

                    dr->work.active = true;

                    dr->work.pending_decode = false;
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
                dr->work.timeout = CtiTime::now() + gConfigParms.getValueAsInt("PORTER_DNPUDP_TIMEOUT", 10);
            }
            else
            {
                dr->work.timeout = YUKONEOT;
            }
        }

        areDevicesActive |= (dr->work.pending_decode || dr->work.active);
    }

    return areDevicesActive;
}


string UnsolicitedHandler::describeDevice( const device_record &dr ) const
{
    ostringstream ostr;

    ostr << dr.device->getName() << " (" << ip_to_string(getDeviceIp(dr.id)) << ":" << getDevicePort(dr.id) << ")";

    return ostr.str();
}


void UnsolicitedHandler::traceOutbound( const device_record &dr, int status )
{
    CtiTraceMsg mTrace;
    string msg;

    //  set bright yellow for the time message
    mTrace.setBrightYellow();
    mTrace.setTrace( CtiTime().asString() );
    mTrace.setEnd(false);
    _traceList.push_back(mTrace.replicateMessage());

    //  set bright cyan for the info message
    mTrace.setBrightCyan();
    msg  = "  P: " + CtiNumStr(_port->getPortID()).spad(3) + " / " + describePort();

    mTrace.setTrace(msg);
    mTrace.setEnd(false);
    _traceList.push_back(mTrace.replicateMessage());

    mTrace.setBrightCyan();
    msg = "  D: " + CtiNumStr(dr.device->getID()).spad(3) + " / " + describeDevice(dr);
    mTrace.setTrace(msg);
    mTrace.setEnd(false);
    _traceList.push_back(mTrace.replicateMessage());

    if( status )
    {
        mTrace.setBrightRed();
        msg = " OUT: " + CtiNumStr(status).spad(3);
    }
    else
    {
        mTrace.setBrightWhite();
        msg = " OUT:";
    }
    mTrace.setTrace(msg);
    mTrace.setEnd(true);
    _traceList.push_back(mTrace.replicateMessage());

    //  then print the formatted hex trace
    mTrace.setBrightGreen();
    CtiPort::traceBytes(dr.work.xfer.getOutBuffer(), dr.work.xfer.getOutCount(), mTrace, _traceList);
}


void UnsolicitedHandler::traceInbound( unsigned long ip, unsigned short port, int status, const unsigned char *message, int mCount, const device_record *dr )
{
    CtiTraceMsg mTrace;
    string msg;

    //  set bright yellow for the time message
    mTrace.setBrightYellow();
    mTrace.setTrace( CtiTime().asString() );
    mTrace.setEnd(false);
    _traceList.push_back(mTrace.replicateMessage());

    //  set bright cyan for the info message
    mTrace.setBrightCyan();
    msg  = "  P: " + CtiNumStr(_port->getPortID()).spad(3) + " / " + describePort();

    mTrace.setTrace(msg);
    mTrace.setEnd(false);
    _traceList.push_back(mTrace.replicateMessage());

    mTrace.setBrightCyan();
    if( dr && dr->device )
    {
        msg = "  D: " + CtiNumStr(dr->device->getID()).spad(3) + " / " + describeDevice(*dr);
    }
    else
    {
        msg = " (" + ip_to_string(ip) + ":" + CtiNumStr(port) + ")";
    }
    mTrace.setTrace(msg);
    mTrace.setEnd(false);
    _traceList.push_back(mTrace.replicateMessage());

    if( status )
    {
        if( status == ErrPortSimulated )
        {
            mTrace.setBrightWhite();
            msg = " IN: (simulated, no bytes returned)";
        }
        else
        {
            mTrace.setBrightRed();
            msg = " IN: " + CtiNumStr(status).spad(3);
        }
    }
    else
    {
        mTrace.setBrightWhite();
        msg = " IN:";
    }
    mTrace.setTrace(msg);
    mTrace.setEnd(true);
    _traceList.push_back(mTrace.replicateMessage());


    //  then print the formatted hex trace
    if( mCount > 0 )
    {
        mTrace.setBrightMagenta();
        CtiPort::traceBytes(message, mCount, mTrace, _traceList);
    }

    if( status && status != ErrPortSimulated )
    {
        mTrace.setBrightRed();
        mTrace.setTrace( FormatError(status) );
        mTrace.setEnd(true);
        _traceList.push_back(mTrace.replicateMessage());
        mTrace.setNormal();
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

    unsigned long max_entries = port->queueCount();

    while( max_entries-- && port->readQueue( &rq, &size, (PPVOID)&om, DCWW_NOWAIT, &priority, &entries) == NORMAL )
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


UnsolicitedHandler::device_record *UnsolicitedHandler::getDeviceRecordById( long device_id )
{
    device_record_map::iterator itr = _device_records.find(device_id);

    if( itr == _device_records.end() )
    {
        return 0;
    }

    return itr->second;
}


void UnsolicitedHandler::addInboundWork(device_record *dr, packet *&p)
{
    if( dr && p )
    {
        dr->work.inbound.push(p);

        p = 0;

        _active_devices.insert(dr);
    }
}


bool UnsolicitedHandler::processInbounds( void )
{
    bool devicesActive = false;

    for each( device_record *dr in _active_devices )
    {
        if( !dr || !dr->device )
        {
            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::processInbounds - dr == 0 || dr->device == 0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            continue;
        }

        if( isDnpDevice(*dr->device) )
        {
            devicesActive |= processDnpInbound(*dr);
        }
        else if( isGpuffDevice(*dr->device) )
        {
            devicesActive |= processGpuffInbound(*dr);
        }
    }

    return devicesActive;
}


bool UnsolicitedHandler::processDnpInbound(device_record &dr)
{
    //  are we doing anything?
    if( dr.device->isTransactionComplete() )
    {
        return false;
    }

    int status = NORMAL;

    if( dr.work.timeout < CtiTime::now() )
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
    else if( dr.work.xfer.getInCountExpected() )
    {
        //  we are expecting input, but we don't have it yet, and we're not timed out yet either
        return true;
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

        traceInbound(getDeviceIp(dr.id), getDevicePort(dr.id), dr.work.status, dr.work.xfer.getInBuffer(), dr.work.xfer.getInCountActual(), &dr);

        if( !dr.work.outbound.empty() && dr.work.outbound.front() )
        {
            om_retry = dr.work.outbound.front()->Retry;
        }

        processCommStatus(status, dr.id, dr.id, om_retry > 0, boost::static_pointer_cast<CtiDeviceBase>(dr.device));

        if( status && !dr.device->isTransactionComplete() )
        {
            //  error, but we're going to keep going
            //    this is the equivalent of a "retry" in the old school code
            statisticsNewAttempt(dr.device->getPortID(), dr.id, dr.id, status, MessageFlag_StatisticsRequested);
        }
    }

    return true;
}


bool UnsolicitedHandler::processGpuffInbound(device_record &dr)
{
    if( dr.work.inbound.empty() )
    {
        return false;
    }

    packet *p = dr.work.inbound.front();

    dr.work.inbound.pop();

    if( !p )
    {
        return false;
    }

    vector<CtiPointDataMsg *> points;

    unsigned last_seq = dr.device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Sequence);
    unsigned seq      = GpuffProtocol::decode(p->data, last_seq, dr.device->getName(), points);

    if( seq != last_seq )
    {
        //  consumes the contents of points
        sendDevicePointsFromProtocol(points, dr.device, VanGoghConnection);

        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Sequence, seq);  // Save the current sequence.
    }
    else
    {
        delete_container(points);

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " GPUFF device " << dr.device->getName() << " sequence number " << seq << " already processed." << endl;
    }

    delete p->data;
    delete p;

    return true;
}


void UnsolicitedHandler::sendDevicePointsFromProtocol(vector<CtiPointDataMsg *> &points, const CtiDeviceSingleSPtr &device, CtiConnection &connection)
{
    auto_ptr<CtiMultiMsg> m(new CtiMultiMsg());

    for each( CtiPointDataMsg *pd in points )
    {
        if( !pd )
        {
            continue;
        }

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

    points.clear();

    if( !m->getData().empty() )
    {
        connection.WriteConnQue(m.release());
    }
}


void UnsolicitedHandler::trace()
{
    if( _traceList.empty() )
    {
        return;
    }

    if( gLogPorts )
    {
        CtiLockGuard<CtiLogger> portlog_guard(_portLog);

        for each( CtiMessage *msg in _traceList )
        {
            _portLog << ((CtiTraceMsg *)msg)->getTrace();

            if( ((CtiTraceMsg *)msg)->isEnd() )
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

        for each( CtiMessage *msg in _traceList )
        {
            SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), (WORD)(((CtiTraceMsg *)msg)->getAttributes()));
            cout << ((CtiTraceMsg *)msg)->getTrace();

            if( ((CtiTraceMsg *)msg)->isEnd() )
            {
                cout << endl;
            }
        }

        SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);
    }

    delete_container(_traceList);
    _traceList.clear();
}


bool UnsolicitedHandler::communicate( void )
{
    bool active = false;

    active |= generateOutbounds();

    active |= collectInbounds();

    active |= processInbounds();

    return active;
}


bool UnsolicitedHandler::sendResults( void )
{
    bool resultsSent = false;

    for each( device_record *dr in _active_devices )
    {
        if( !dr || !dr->device )
        {
            if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::sendResults - dr == 0 || dr->device == 0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            continue;
        }

        //  if we don't have any work or we're not done with this transaction yet
        if( !dr->work.active || !dr->device->isTransactionComplete() )
        {
            continue;
        }

        dr->device->sendDispatchResults(VanGoghConnection);

        resultsSent = true;

        dr->work.active = false;

        //  we may be done with this transaction, but do we have anyone to send it back to?
        if( dr->work.outbound.empty() )
        {
            continue;
        }

        OUTMESS *om = dr->work.outbound.front();

        if( om )
        {
            //  om->Retry will never be nonzero for DNP devices - they handle retries internally
            processCommStatus(dr->work.status, dr->id, dr->id, om->Retry > 0, boost::static_pointer_cast<CtiDeviceBase>(dr->device));

            INMESS im;

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

    return resultsSent;
}


void UnsolicitedHandler::receiveMessage(CtiMessage *msg)
{
    _message_queue.putQueue(msg);
}


void UnsolicitedMessenger::addClient(UnsolicitedHandler *client)
{
    if( !client )
    {
        return;
    }

    CtiLockGuard< CtiCriticalSection > lock(_client_mux);

    _clients.push_back(client);
}


void UnsolicitedMessenger::sendMessageToClients(const CtiDBChangeMsg *msg)
{
    if( !msg )
    {
        return;
    }

    CtiLockGuard< CtiCriticalSection > lock(_client_mux);

    for each( UnsolicitedHandler *client in _clients )
    {
        if( client )
        {
            client->receiveMessage(msg->replicateMessage());
        }
    }
}


}
}

