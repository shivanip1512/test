#include "yukon.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "unsolicited_handler.h"

#include "prot_gpuff.h"
#include "pt_numeric.h"

#include "portglob.h"
#include "dev_dnp.h"
#include "cparms.h"
#include "msg_dbchg.h"
#include "msg_trace.h"
#include "StatisticsManager.h"

using namespace std;

using Cti::Timing::MillisecondTimer;

extern CtiConnection VanGoghConnection;

extern INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);
extern bool processCommStatus(INT CommResult, LONG DeviceID, LONG TargetID, bool RetryGTZero, const CtiDeviceSPtr &Device);

namespace Cti {
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

    haltLog();

    UnsolicitedPortsQueue.removeClient(this);
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

        MillisecondTimer loop_timer;

        while( !PorterQuit && !_shutdown )
        {
            bool work_remaining = false;

            try
            {
                work_remaining |= handleDbChanges     (loop_timer, loop_timer.elapsed() + 10);

                manageConnections();

                work_remaining |= distributeRequests  (loop_timer, loop_timer.elapsed() + 10);

                work_remaining |= startPendingRequests(loop_timer, loop_timer.elapsed() + 10);

                work_remaining |= generateOutbounds   (loop_timer, loop_timer.elapsed() + 10);

                work_remaining |= collectInbounds     (loop_timer, loop_timer.elapsed() + 10);

                work_remaining |= expireTimeouts      (loop_timer, loop_timer.elapsed() + 10);

                work_remaining |= processInbounds     (loop_timer, loop_timer.elapsed() + 10);

                work_remaining |= sendResults         (loop_timer, loop_timer.elapsed() + 10);

                trace();
            }
            catch( ... )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION in " << describePort() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            if( ! work_remaining )
            {
                const unsigned long elapsed = loop_timer.elapsed();

                if( _active_devices.empty() )
                {
                    if( elapsed < 500 )
                    {
                        Sleep(500 - elapsed);
                    }
                }
                else if( elapsed < 100 )
                {
                    //  we're waiting for comms
                    Sleep(100 - elapsed);
                }
            }

            loop_timer.reset();
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << describePort() << " shutdown." << endl;
    }
}


void UnsolicitedHandler::initializeDeviceRecords( void )
{
    vector<CtiDeviceManager::ptr_type> port_devices;
    vector<const CtiDeviceSingle *> devices;

    _deviceManager.getDevicesByPortID(_port->getPortID(), port_devices);

    for each(CtiDeviceManager::ptr_type device in port_devices)
    {
        if( device && device->isSingle() )
        {
            insertDeviceRecord(device);

            devices.push_back(static_cast<CtiDeviceSingle *>(device.get()));
        }
    }

    loadDeviceProperties(devices);
}


const UnsolicitedHandler::device_record *UnsolicitedHandler::insertDeviceRecord(const CtiDeviceSPtr &device)
{
    if( !device )
    {
        return 0;
    }

    device_record *dr = CTIDBG_new device_record(device);

    _device_records[device->getID()] = dr;

    return dr;
}


bool UnsolicitedHandler::handleDbChanges(const MillisecondTimer &timer, const unsigned long until)
{
    while( _message_queue.size() )
    {
        //  1 ms max wait
        if( CtiMessage *msg = _message_queue.getQueue(1) )
        {
            if( msg->isA() == MSG_DBCHANGE )
            {
                handleDbChange(*static_cast<CtiDBChangeMsg *>(msg));
            }

            delete msg;
        }

        if( timer.elapsed() > until )
        {
            return _message_queue.size();  //  we ran out of time - do we have more work left?
        }
    }

    return false;
}


void UnsolicitedHandler::handleDbChange(const CtiDBChangeMsg &dbchg)
{
    if( dbchg.getId() && dbchg.getDatabase() == ChangePAODb )
    {
        switch( resolvePAOCategory(dbchg.getCategory()) )
        {
            case PAO_CATEGORY_DEVICE:   return handleDeviceChange(dbchg.getId(), dbchg.getTypeOfChange());
            case PAO_CATEGORY_PORT:     return handlePortChange  (dbchg.getId(), dbchg.getTypeOfChange());
            default:                    return;
        }
    }
}


void UnsolicitedHandler::handleDeviceChange(long device_id, int change_type)
{
    switch( change_type )
    {
        case ChangeTypeDelete:  return deleteDeviceRecord(device_id);
        case ChangeTypeAdd:     return addDeviceRecord   (device_id);
        case ChangeTypeUpdate:  return updateDeviceRecord(device_id);
        default:                return;
    }
}


void UnsolicitedHandler::deleteDeviceRecord(const long device_id)
{
    if( device_record *dr = getDeviceRecordById(device_id) )
    {
        _device_records.erase(device_id);

        device_activity_map::iterator itr = _active_devices.find(dr);

        if( itr != _active_devices.end() )
        {
            purgeDeviceWork(*itr, IDNF);

            _active_devices.erase(itr);
        }

        deleteDeviceProperties(*dr->device);

        delete dr;
    }
}


void UnsolicitedHandler::addDeviceRecord(const long device_id)
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
}


void UnsolicitedHandler::updateDeviceRecord(const long device_id)
{
    if( device_record *dr = getDeviceRecordById(device_id) )
    {
        if( dr->device->getPortID() != _port->getPortID() )
        {
            //  device was moved to another port
            return deleteDeviceRecord(device_id);
        }

        if( dr->device->isInhibited() )
        {
            device_activity_map::iterator itr = _active_devices.find(dr);

            if( itr != _active_devices.end() )
            {
                //  erases it from _active_devices
                purgeDeviceWork(*itr, DEVICEINHIBITED);

                _active_devices.erase(itr);
            }
        }

        updateDeviceProperties(*dr->device);
    }
    else
    {
        //  device isn't in our list - check to see if it just got added to our port
        return addDeviceRecord(device_id);
    }
}


void UnsolicitedHandler::handlePortChange(long port_id, int change_type)
{
    if( port_id == _port->getPortID() )
    {
        switch( change_type )
        {
            case ChangeTypeDelete:  return deletePort();
            case ChangeTypeUpdate:  return updatePort();
            default:                return;
        }
    }
}


void UnsolicitedHandler::deletePort(void)
{
    purgePortWork(BADPORT);
}


void UnsolicitedHandler::updatePort(void)
{
    if( _port->isInhibited() )
    {
        purgePortWork(PORTINHIBITED);
    }

    updatePortProperties();
}


void UnsolicitedHandler::purgePortWork(int error_code)
{
    for each( const device_activity_map::value_type &active_device in _active_devices )
    {
        purgeDeviceWork(active_device, error_code);
    }

    _active_devices.clear();
}


void UnsolicitedHandler::purgeDeviceWork(const device_activity_map::value_type &active_device, int error_code)
{
    device_record &dr = *active_device.first;

    //  first, purge all of the work
    for each( OUTMESS *om in dr.outbound )
    {
        INMESS im;

        OutEchoToIN(om, &im);

        //  deletes the OM
        ReturnResultMessage(error_code, &im, om);
    }

    dr.outbound.clear();

    while( ! dr.inbound.empty() )
    {
        delete dr.inbound.front();

        dr.inbound.pop();
    }

    //  null out its entry in the activity list it was in
    *active_device.second = 0;
}


bool UnsolicitedHandler::distributeRequests(const MillisecondTimer &timer, const unsigned long until)
{
    generateKeepalives(_request_queue);

    readPortQueue(_port, _request_queue);

    //  if we got work, attempt to distribute it to the device records
    return processQueue(_request_queue, &UnsolicitedHandler::handleDeviceRequest, timer, until);
}


template<class Element>
bool UnsolicitedHandler::processQueue(std::list<Element> &queue, void (UnsolicitedHandler::*processElement)(Element), const Cti::Timing::MillisecondTimer &timer, const unsigned long until)
{
    while( ! queue.empty() )
    {
        if( Element element = queue.front() )
        {
            (this->*processElement)(element);
        }

        queue.pop_front();

        if( timer.elapsed() > until )
        {
            return ! queue.empty();
        }
    }

    return false;
}


void UnsolicitedHandler::handleDeviceRequest(OUTMESS *om)
{
    if( _port->isInhibited() )
    {
        //  return an error - this deletes the OM
        INMESS im;
        ReturnResultMessage(PORTINHIBITED, &im, om);
        return;
    }

    if( device_record *dr = getDeviceRecordById(om->DeviceID) )
    {
        if( dr->device->isInhibited() )
        {
            //  return an error - this deletes the OM
            INMESS im;
            ReturnResultMessage(DEVICEINHIBITED, &im, om);
            return;
        }

        if( isDeviceDisconnected(dr->device->getID()) )
        {
            //  return an error - this deletes the OM
            INMESS im;
            ReturnResultMessage(ErrorDeviceNotConnected, &im, om);
            return;
        }

        if( gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Porter::UnsolicitedHandler::getOutMessages - queueing work for \"" << dr->device->getName() << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        dr->outbound.push_back(om);

        if( _active_devices.find(dr) == _active_devices.end() )
        {
            _active_devices[dr] = _request_pending.insert(_request_pending.end(), dr);
        }
    }
    else
    {
        if( gConfigParms.getValueAsULong("PORTER_DNPUDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Porter::DNPUDP::getOutMessages - no device found for device id (" << om->DeviceID << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        //  return an error - this deletes the OM
        INMESS im;
        ReturnResultMessage(IDNF, &im, om);
        return;
    }
}


void UnsolicitedHandler::generateKeepalives(om_list &local_queue)
{
    const CtiTime TimeNow;

    if( _last_keepalive < TimeNow )
    {
        _last_keepalive = TimeNow;

        //  check all of our devices to see if we need to issue a keepalive
        for each( const pair<long, device_record *> &dr_pair in _device_records )
        {
            device_record *dr = dr_pair.second;

            if( dr->device->isInhibited() )
            {
                continue;
            }

            if( isDnpDevice(*dr->device) &&
                isDnpKeepaliveNeeded(*dr, TimeNow) )
            {
                generateDnpKeepalive(local_queue, *dr, TimeNow);
                dr->last_keepalive = TimeNow;
            }
        }
    }
}


bool UnsolicitedHandler::isDnpDevice(const CtiDeviceSingle &ds)
{
    switch( ds.getType() )
    {
        case TYPE_DNPRTU:
        case TYPECBC7020:
        case TYPECBC8020:
        case TYPECBCDNP:
        {
            return true;
        }
    }

    return false;
}

bool UnsolicitedHandler::isRdsDevice(const CtiDeviceSingle &ds)
{
    switch( ds.getType() )
    {
        case TYPE_RDS:
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
    CtiTime last_communication = max(dr.last_outbound, dr.last_keepalive);
    u_long  keepalive_period   = gConfigParms.getValueAsULong("PORTER_DNPUDP_KEEPALIVE", 86400);

    return (last_communication + keepalive_period) < TimeNow;
}

void UnsolicitedHandler::generateDnpKeepalive(om_list &local_queue, const device_record &dr, const CtiTime &TimeNow)
{
    CtiRequestMsg msg(dr.device->getID(), "ping");
    CtiCommandParser parse(msg.CommandString());
    list<CtiMessage *> vg_list, ret_list;
    list<OUTMESS *> om_list;

    dr.device->beginExecuteRequest(&msg, parse, vg_list, ret_list, om_list);

    for each(OUTMESS *om in om_list )
    {
        PorterStatisticsManager.newRequest(om->Port, om->DeviceID, om->TargetID, om->MessageFlags);
    }

    delete_container(vg_list);
    delete_container(ret_list);

    local_queue.insert(local_queue.end(), om_list.begin(), om_list.end());

    om_list.clear();
}


bool UnsolicitedHandler::startPendingRequests(const MillisecondTimer &timer, const unsigned long until)
{
    return processQueue(_request_pending, &UnsolicitedHandler::startPendingRequest, timer, until);
}


void UnsolicitedHandler::startPendingRequest(device_record *dr)
{
    if( ! dr->outbound.empty() )
    {
        if( OUTMESS *om = dr->outbound.front() )
        {
            //  this should pay attention to the status
            dr->device->recvCommRequest(om);

            _active_devices[dr] = _to_generate.insert(_to_generate.end(), dr);
        }
        else
        {
            //  null outmessage, needs to be removed
            dr->outbound.pop_front();

            //  but re-examine this device
            _active_devices[dr] = _request_pending.insert(_request_pending.end(), dr);
        }
    }
    else if( ! dr->inbound.empty() )
    {
        //  no new outbound work, so process the unexpected inbound
        if( isDnpDevice(*dr->device) )
        {
            //  push a null OM on there so can distinguish this request from anything else that comes in
            dr->outbound.push_back(0);

            //  there is no outmessage, so we don't call recvCommRequest -
            //    we have to call the Device::DNP-specific initUnsolicited
            shared_ptr<Devices::DnpDevice> dnp_device = boost::static_pointer_cast<Devices::DnpDevice>(dr->device);

            dnp_device->initUnsolicited();

            _active_devices[dr] = _to_generate.insert(_to_generate.end(), dr);
        }
        else if( isGpuffDevice(*dr->device) )
        {
            //  GPUFF is unsolicited-only - decode inbounds right away
            _active_devices[dr] = _to_decode.insert(_to_decode.end(), dr);
        }
        else
        {
            //  only DNP and GPUFF devices support unsolicited - erase any other inbounds
            while( !dr->inbound.empty() )
            {
                delete dr->inbound.front();

                dr->inbound.pop();
            }

            _active_devices.erase(dr);
        }
    }
    else
    {
        //  no outbounds, no inbounds - this guy is done for now
        _active_devices.erase(dr);
    }
}


bool UnsolicitedHandler::generateOutbounds(const MillisecondTimer &timer, const unsigned long until)
{
    return processQueue(_to_generate, &UnsolicitedHandler::tryGenerate, timer, until);
}


void Cti::Porter::UnsolicitedHandler::tryGenerate(device_record *dr)
{
    dr->device_status = dr->device->generate(dr->xfer);

    if( dr->device_status )
    {
        if( dr->device->isTransactionComplete() )
        {
            _active_devices[dr] = _request_complete.insert(_request_complete.end(), dr);
        }
        else
        {
            _active_devices[dr] = _to_generate.insert(_to_generate.end(), dr);
        }

        return;
    }

    dr->xfer.setInCountActual(0UL);

    if( dr->xfer.getOutCount() )
    {
        dr->comm_status = sendOutbound(*dr);

        traceOutbound(*dr, dr->comm_status);
    }
    else
    {
        dr->comm_status = NORMAL;
    }

    //  if we have data, are expecting no data, or we have an error, decode right away
    if( ! dr->inbound.empty() || ! dr->xfer.getInCountExpected() || dr->comm_status )
    {
        _active_devices[dr] = _to_decode.insert(_to_decode.end(), dr);
    }
    else
    {
        unsigned portDelay    = _port->getDelay(EXTRA_DELAY); // Additional timeout for the port.
        unsigned cparmTimeout = gConfigParms.getValueAsInt("PORTER_DNPUDP_TIMEOUT", 10);

        const CtiTime timeout = CtiTime::now() + std::max(portDelay, cparmTimeout);

        const device_list::iterator waiting_itr = _waiting_for_data.insert(_waiting_for_data.end(), dr);

        _active_devices [dr] = waiting_itr;
        _waiting_devices[dr] = waiting_itr;

        //  insert because it's a multimap - we might have multiple entries for this timeout value
        _timeouts.insert(make_pair(timeout, waiting_itr));
    }
}


string UnsolicitedHandler::describeDevice( const device_record &dr ) const
{
    ostringstream ostr;

    ostr << dr.device->getName() << " (" << ip_to_string(getDeviceIp(dr.device->getID())) << ":" << getDevicePort(dr.device->getID()) << ")";

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
    CtiPort::traceBytes(dr.xfer.getOutBuffer(), dr.xfer.getOutCount(), mTrace, _traceList);
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
    if( dr )
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


void UnsolicitedHandler::readPortQueue(CtiPortSPtr &port, om_list &local_queue)
{
    unsigned long entries = 0;
    bool printed = false;
    unsigned long size;
    unsigned char priority;
    OUTMESS *om;

    unsigned long max_entries = port->queueCount();

    while( max_entries-- && port->readQueue( &size, (PPVOID)&om, DCWW_NOWAIT, &priority, &entries) == NORMAL )
    {
        port->incQueueSubmittal(1, CtiTime());

        if( !printed && entries && gConfigParms.getValueAsULong("PORTER_UDP_DEBUGLEVEL", 0, 16) & 0x00000001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Porter::UnsolicitedHandler::getOutMessages - " << entries << " additional entries on queue " << __FILE__ << " (" << __LINE__ << ")" << endl;

            printed = true;
        }

        local_queue.push_back(om);
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
        dr->inbound.push(p);

        p = 0;

        //  check to see if the device was waiting for anything
        device_activity_map::iterator itr = _waiting_devices.find(dr);

        if( itr != _waiting_devices.end() )
        {
            device_list::iterator waiting_itr = itr->second;

            //  we just got data - null out this device's entry so the timeout thread ignores it
            *waiting_itr = 0;

            _waiting_devices.erase(itr);

            //  we just got work - we're ready to try a decode
            _active_devices[dr] = _to_decode.insert(_to_decode.end(), dr);
        }
        else if( _active_devices.find(dr) == _active_devices.end() )
        {
            //  it's new work, so get him started next time the pending list is examined
            _active_devices[dr] = _request_pending.insert(_request_pending.end(), dr);
        }
    }
}


bool UnsolicitedHandler::expireTimeouts(const MillisecondTimer &timer, const unsigned long until)
{
    CtiTime now;

    while( ! _timeouts.empty() && _timeouts.begin()->first < now )
    {
        const CtiTime timeout = _timeouts.begin()->first;
        device_list::iterator waiting_itr = _timeouts.begin()->second;

        _timeouts.erase(_timeouts.begin());

        if( device_record *dr = *waiting_itr )
        {
            _waiting_devices.erase(dr);

            _active_devices[dr] = _to_decode.insert(_to_decode.end(), dr);
        }

        _waiting_for_data.erase(waiting_itr);

        if( timer.elapsed() > until )
        {
            return true;
        }
    }

    return false;
}


bool UnsolicitedHandler::processInbounds(const MillisecondTimer &timer, const unsigned long until)
{
    return processQueue(_to_decode, &UnsolicitedHandler::processInbound, timer, until);
}


void Cti::Porter::UnsolicitedHandler::processInbound(device_record *dr)
{
    if( isGpuffDevice(*dr->device) )
    {
        processGpuffInbound(*dr);

        _active_devices[dr] = _request_complete.insert(_request_complete.end(), dr);
    }
    else
    {
        processDeviceSingleInbound(*dr);

        if( dr->device->isTransactionComplete() )
        {
            _active_devices[dr] = _request_complete.insert(_request_complete.end(), dr);
        }
        else
        {
            _active_devices[dr] = _to_generate.insert(_to_generate.end(), dr);
        }
    }
}


void UnsolicitedHandler::processDeviceSingleInbound(device_record &dr)
{
    if( ! dr.comm_status )
    {
        if( ! dr.inbound.empty() )
        {
            //  all unsolicited protocols are packet-based - they cannot cross packet boundaries.
            //    UDP packets must arrive as 1 or more whole packets.
            //    TCP packets are broken up into packets by TcpPortHandler::collectInbounds() use of findPacket().
            //  This all means we cannot use more than one packet per xfer request.
            packet *p = dr.inbound.front();

            const int bytes_used = std::min<int>(p->len - p->used, dr.xfer.getInCountExpected());

            dr.xfer.setInCountActual(bytes_used);

            memcpy(dr.xfer.getInBuffer(), p->data + p->used, bytes_used);

            p->used += bytes_used;

            if( p->used >= p->len )
            {
                //  we've used the packet up
                delete p->data;
                delete p;

                dr.inbound.pop();
            }
        }

        if( ! dr.xfer.getNonBlockingReads() )
        {
            if( dr.xfer.getInCountActual() < dr.xfer.getInCountExpected() )
            {
                dr.comm_status = READTIMEOUT;
            }
        }
    }

    dr.device_status = dr.device->decode(dr.xfer, dr.comm_status);

    if( dr.device_status || dr.xfer.getInCountExpected() )
    {
        int om_retry = 0;

        traceInbound(getDeviceIp(dr.device->getID()), getDevicePort(dr.device->getID()), dr.device_status, dr.xfer.getInBuffer(), dr.xfer.getInCountActual(), &dr);

        if( !dr.outbound.empty() && dr.outbound.front() )
        {
            om_retry = dr.outbound.front()->Retry;
        }

        processCommStatus(dr.device_status, dr.device->getID(), dr.device->getID(), om_retry > 0, boost::static_pointer_cast<CtiDeviceBase>(dr.device));
    }
}


void UnsolicitedHandler::processGpuffInbound(device_record &dr)
{
    if( dr.inbound.empty() )
    {
        return;
    }

    packet *p = dr.inbound.front();

    dr.inbound.pop();

    vector<CtiPointDataMsg *> points;

    unsigned last_seq = dr.device->getDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Sequence);

    GpuffProtocol::decoded_packet packet = GpuffProtocol::decode(p->data, last_seq, dr.device->getName());

    if( packet.seq != last_seq )
    {
        //  consumes the contents of points
        sendDevicePointsFromProtocol(packet.points, dr.device, VanGoghConnection);

        dr.device->setDynamicInfo(CtiTableDynamicPaoInfo::Key_UDP_Sequence, packet.seq);  // Save the current sequence.

        if( packet.ack_required )
        {
            vector<unsigned char> response = GpuffProtocol::generateAck(packet);

            dr.xfer.setOutBuffer(&response.front());
            dr.xfer.setOutCount(response.size());

            int status = sendOutbound(dr);

            traceOutbound(dr, status);
        }
    }
    else
    {
        delete_container(packet.points);

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " GPUFF device " << dr.device->getName() << " sequence number " << packet.seq << " already processed." << endl;
    }

    delete p->data;
    delete p;
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
            if( p->isNumeric() )
            {
                CtiPointNumeric *n = (CtiPointNumeric *)p.get();

                pd->setValue(n->computeValueForUOM(pd->getValue()));
            }

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


bool UnsolicitedHandler::sendResults(const MillisecondTimer &timer, const unsigned long until)
{
    return processQueue(_request_complete, &UnsolicitedHandler::sendResult, timer, until);
}


void Cti::Porter::UnsolicitedHandler::sendResult(device_record *dr)
{
    dr->device->sendDispatchResults(VanGoghConnection);

    //  we may be done with this transaction, but do we have anyone to send it back to?
    if( ! dr->outbound.empty() )
    {
        if( OUTMESS *om = dr->outbound.front() )
        {
            //  om->Retry will never be nonzero for DNP devices - they handle retries internally
            processCommStatus(dr->device_status, dr->device->getID(), dr->device->getID(), om->Retry > 0, boost::static_pointer_cast<CtiDeviceBase>(dr->device));

            INMESS im;

            OutEchoToIN(om, &im);

            //  ignoring the result of this for now - DeviceDnp always returns NoError
            dr->device->sendCommResult(&im);

            if( dr->device_status && om->Retry > 0 )
            {
                om->Retry--;

                //  error, but we're going to keep going
                //    this is the equivalent of a "retry" in the old school code
                PorterStatisticsManager.newAttempt(dr->device->getPortID(), dr->device->getID(), dr->device->getID(), dr->device_status, dr->outbound.front()->MessageFlags);

                _request_queue.push_back(om);
            }
            else
            {
                //  if dr->status is nonzero, ReturnResultMessage() calls SendError(),
                //    which calls PorterStatisticsManager.newCompletion()...  so we don't need to call it if we're not in error
                if( ! dr->device_status )
                {
                    PorterStatisticsManager.newCompletion( om->Port, om->DeviceID, om->TargetID, dr->device_status, om->MessageFlags );
                }

                //  This method may delete the OM!
                ReturnResultMessage(dr->device_status, &im, om);

                //  If ReturnResultMessage doesn't delete the OM (i.e. wasn't an error condition), then we'll delete it
                delete om;
            }
        }

        dr->outbound.pop_front();
    }

    //  all done with this request - check to see if there's anything else waiting for us
    _active_devices[dr] = _request_pending.insert(_request_pending.end(), dr);
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

    _clients.insert(client);
}


void UnsolicitedMessenger::removeClient(UnsolicitedHandler *client)
{
    if( !client )
    {
        return;
    }

    CtiLockGuard< CtiCriticalSection > lock(_client_mux);

    _clients.erase(client);
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
        client->receiveMessage(msg->replicateMessage());
    }
}


}
}

