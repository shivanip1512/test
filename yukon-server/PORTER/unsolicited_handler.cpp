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
    _deviceManager(deviceManager)
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

        bool active;

        while( !PorterQuit )
        {
            try
            {
                active  = handleDbReloads();

                active |= manageConnections();

                active |= getDeviceRequests();

                active |= generateOutbounds();

                active |= collectInbounds();

                active |= processInbounds();

                active |= sendResults();

                trace();

                if( active )
                {
                    Sleep(50);
                }
                else
                {
                    Sleep(500);
                }

            }
            catch( ... )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION in " << describePort() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


bool UnsolicitedHandler::handleDbReloads( void )
{
    bool anythingForUs = false;

    //  limit the number we pull off each time through to make sure
    //    we don't get stuck in a producer/consumer loop
    unsigned max_entries = _message_queue.entries();

    while( max_entries-- && _message_queue.entries() )
    {
        auto_ptr<CtiMessage> msg(_message_queue.getQueue(1));  //  1 ms wait

        if( !msg.get() || msg->isA() != MSG_DBCHANGE )
        {
            continue;
        }

        auto_ptr<CtiDBChangeMsg> db_msg((CtiDBChangeMsg *)msg.release());

        //  find the device record, if it exists
        if( db_msg->getId() &&
            db_msg->getDatabase() == ChangePAODb &&
            resolvePAOCategory(db_msg->getCategory()) == PAO_CATEGORY_DEVICE )
        {
            switch( db_msg->getTypeOfChange() )
            {
                case ChangeTypeDelete:  anythingForUs |= deleteDeviceRecord(db_msg->getId());  break;
                case ChangeTypeAdd:     anythingForUs |= addDeviceRecord   (db_msg->getId());  break;
                case ChangeTypeUpdate:  anythingForUs |= updateDeviceRecord(db_msg->getId());  break;
            }
        }
    }

    return anythingForUs;
}


bool UnsolicitedHandler::deleteDeviceRecord(const long device_id)
{
    device_record *dr = getDeviceRecordById(device_id);

    _device_records.erase(device_id);

    if( dr )
    {
        deleteDeviceProperties(*dr->device);

        delete dr;
    }

    return true;
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

    if( !dr )
    {
        return false;
    }

    updateDeviceProperties(*dr->device);

    return true;
}


bool UnsolicitedHandler::getDeviceRequests(void)
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

        if( om )
        {
            new_requests = true;

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
            }
            else
            {
                if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::getOutMessages - queueing work for \"" << dr->device->getName() << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                dr->work.outbound.push(om);
            }
        }
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

        if( !dr || !dr->device )
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

    for each( const pair<long, device_record *> &dr_pair in _device_records )
    {
        device_record *dr = dr_pair.second;

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
                    if( isDnpDevice(*dr->device) )
                    {
                        //  there is no outmessage, so we don't call recvCommRequest -
                        //    we have to call the Cti::Device::DNP-specific initUnsolicited
                        shared_ptr<Device::DNP> dev = boost::static_pointer_cast<Device::DNP>(dr->device);

                        dev->initUnsolicited();

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
    CtiTraceMsg trace;
    string msg;

    //  set bright yellow for the time message
    trace.setBrightYellow();
    trace.setTrace( CtiTime().asString() );
    trace.setEnd(false);
    _traceList.push_back(trace.replicateMessage());

    //  set bright cyan for the info message
    trace.setBrightCyan();
    msg  = "  P: " + CtiNumStr(_port->getPortID()).spad(3) + " / " + describePort();

    trace.setTrace(msg);
    trace.setEnd(false);
    _traceList.push_back(trace.replicateMessage());

    trace.setBrightCyan();
    msg = "  D: " + CtiNumStr(dr.device->getID()).spad(3) + " / " + describeDevice(dr);
    trace.setTrace(msg);
    trace.setEnd(false);
    _traceList.push_back(trace.replicateMessage());

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
    _traceList.push_back(trace.replicateMessage());

    //  then print the formatted hex trace
    trace.setBrightGreen();
    CtiPort::traceBytes(dr.work.xfer.getOutBuffer(), dr.work.xfer.getOutCount(), trace, _traceList);
}


void UnsolicitedHandler::traceInbound( unsigned long ip, unsigned short port, int status, const unsigned char *message, int count, const device_record *dr )
{
    CtiTraceMsg trace;
    string msg;

    //  set bright yellow for the time message
    trace.setBrightYellow();
    trace.setTrace( CtiTime().asString() );
    trace.setEnd(false);
    _traceList.push_back(trace.replicateMessage());

    //  set bright cyan for the info message
    trace.setBrightCyan();
    msg  = "  P: " + CtiNumStr(_port->getPortID()).spad(3) + " / " + describePort();

    trace.setTrace(msg);
    trace.setEnd(false);
    _traceList.push_back(trace.replicateMessage());

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
    _traceList.push_back(trace.replicateMessage());

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
    _traceList.push_back(trace.replicateMessage());


    //  then print the formatted hex trace
    if( count > 0 )
    {
        trace.setBrightMagenta();
        CtiPort::traceBytes(message, count, trace, _traceList);
    }

    if( status && status != ErrPortSimulated )
    {
        trace.setBrightRed();
        trace.setTrace( FormatError(status) );
        trace.setEnd(true);
        _traceList.push_back(trace.replicateMessage());
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


UnsolicitedHandler::device_record *UnsolicitedHandler::getDeviceRecordById( long device_id )
{
    device_record_map::iterator itr = _device_records.find(device_id);

    if( itr == _device_records.end() )
    {
        return 0;
    }

    return itr->second;
}


bool UnsolicitedHandler::validatePacket(packet *&p) const
{
    if( Protocol::DNP::Datalink::isPacketValid(p->data, p->len) )
    {
        p->protocol = packet::ProtocolTypeDnp;
    }
    else if( GpuffProtocol::isPacketValid(p->data, p->len) )
    {
        p->protocol = packet::ProtocolTypeGpuff;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::UnsolicitedHandler::validatePacket() - incoming packet from " << ip_to_string(p->ip) <<  ":" << p->port << " is invalid";
        }

        delete p->data;
        delete p;

        p = 0;
    }

    return p;
}


bool UnsolicitedHandler::processInbounds( void )
{
    bool devicesActive = false;

    for each( const pair<long, device_record *> &dr_pair in _device_records )
    {
        device_record *dr = dr_pair.second;

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


bool UnsolicitedHandler::sendResults( void )
{
    bool resultsSent = false;

    for each( const pair<long, device_record *> &dr_pair in _device_records )
    {
        device_record *dr = dr_pair.second;

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
            INMESS im;

            im.EventCode = dr->work.status;

            //  om->Retry will never be nonzero for DNP devices - they handle retries internally
            processCommStatus(dr->work.status, dr->id, dr->id, om->Retry > 0, boost::static_pointer_cast<CtiDeviceBase>(dr->device));

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

