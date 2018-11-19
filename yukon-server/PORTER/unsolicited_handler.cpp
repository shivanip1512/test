#include "precompiled.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "unsolicited_handler.h"

#include "prot_gpuff.h"
#include "pt_numeric.h"

#include "exceptions.h"

#include "portglob.h"
#include "dev_dnp.h"
#include "cparms.h"
#include "msg_dbchg.h"
#include "msg_trace.h"
#include "StatisticsManager.h"

#include "connection_client.h"

using namespace std;

using Cti::Timing::MillisecondTimer;

extern CtiClientConnection VanGoghConnection;

extern YukonError_t ReturnResultMessage(YukonError_t CommResult, INMESS &InMessage, OUTMESS *&OutMessage);
extern bool processCommStatus(INT CommResult, LONG DeviceID, LONG TargetID, bool RetryGTZero, const CtiDeviceSPtr &Device);



namespace Cti::Porter {

namespace
{
std::atomic<long> portHandlerCount = 0;
}

UnsolicitedMessenger UnsolicitedPortsQueue;
using Protocols::GpuffProtocol;

UnsolicitedHandler::UnsolicitedHandler(CtiPortSPtr &port, CtiDeviceManager &deviceManager) :
    _port(port),
    _deviceManager(deviceManager),
    _shutdown(false),
    _portLogManager("porthandler" + std::to_string(++portHandlerCount))
{
    _portLogManager.setToStdOut      ( false );  // Not to std out.
    _portLogManager.setOwnerInfo     ( CompileInfo );
    _portLogManager.setRetentionDays ( gLogRetention );
    _portLogManager.setOutputFormat( Cti::Logging::LogFormat_CommLog );

    _portLog = _portLogManager.getLogger();

    UnsolicitedPortsQueue.addClient(this);
}

UnsolicitedHandler::~UnsolicitedHandler()
{
    //  delete the device records
    delete_assoc_container(_device_records);

    UnsolicitedPortsQueue.removeClient(this);
}


void UnsolicitedHandler::startLog()
{
    if( gLogPorts && !_portLogManager.isStarted() )
    {
        const string comlogdir = gLogDirectory + "\\Comm";

        // Create a subdirectory called Comm beneath Log.
        CreateDirectoryEx(gLogDirectory.data(), comlogdir.data(), NULL);

        _portLogManager.setOutputPath    ( comlogdir );
        _portLogManager.setOutputFile    ( describePort() );
        _portLogManager.start();
    }
}

void UnsolicitedHandler::run( void )
{
    startLog();

    CTILOG_INFO(dout, describePort() <<" started");

    if( !setupPort() )
    {
        CTILOG_ERROR(dout, describePort() <<" setupPort() failed");
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
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, describePort() <<" has failed");
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

    CTILOG_INFO(dout, describePort() <<" shutdown");
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
                auto dbchg = static_cast<CtiDBChangeMsg *>(msg);

                try
                {
                    handleDbChange(*dbchg);
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, describePort() << " failed while processing DBChange " << dbchg);
                }
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
            purgeDeviceWork(*itr, ClientErrors::IdNotFound);

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
                purgeDeviceWork(*itr, ClientErrors::DeviceInhibited);

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
    purgePortWork(ClientErrors::BadPort);
}


void UnsolicitedHandler::updatePort(void)
{
    if( _port->isInhibited() )
    {
        purgePortWork(ClientErrors::PortInhibited);
    }

    updatePortProperties();
}


void UnsolicitedHandler::purgePortWork(const YukonError_t error_code)
{
    for each( const device_activity_map::value_type &active_device in _active_devices )
    {
        purgeDeviceWork(active_device, error_code);
    }

    _active_devices.clear();
}

/*
 * Clean up any work thay the device may have in progress.  
 *
 * Flush inbound and outbound messages.
 * Remove device from it's state queue.
 */
void UnsolicitedHandler::purgeDeviceWork(const device_activity_map::value_type &active_device, const YukonError_t error_code)
{
    device_record &dr = *active_device.first;

    //  first, purge all of the work
    for( OUTMESS *om : dr.outbound )
    {
        if( om )
        {
            handleDeviceError(om, error_code);
        }
    }

    dr.outbound.clear();

    while( ! dr.inbound.empty() )
    {
        delete dr.inbound.front();

        dr.inbound.pop();
    }

    //  remove from the activity list it was in
    switch (active_device.second)
    {
    case RequestPending:
        CTILOG_TRACE(dout, "Purging device " << dr.device->getID() << " from _request_pending");
        _request_pending.remove(&dr);
        break;

    case ToGenerate:
        CTILOG_TRACE(dout, "Purging device " << dr.device->getID() << " from _to_generate");
        _to_generate.remove(&dr);
        break;

    case WaitingForData:
        {
            CTILOG_TRACE(dout, "Purging device " << dr.device->getID() << " from _waiting_for_data");
            _waiting_for_data.remove(&dr);
            _waiting_devices.erase(&dr);

            deactivateTimeout(dr);

        }
        break;

    case ToDecode:
        CTILOG_TRACE(dout, "Purging device " << dr.device->getID() << " from _to_decode");
        _to_decode.remove(&dr);
        break;

    case RequestComplete:
        CTILOG_TRACE(dout, "Purging device " << dr.device->getID() << " from _request_complete");
        _request_complete.remove(&dr);
        break;
    }
}

// We're going to remove the timeout timer from the queue
void UnsolicitedHandler::deactivateTimeout(device_record &dr)
{
    // We'll narrow the search by looking only at those matching our timeout value
    decltype(_timeouts)::iterator itr, end;
    std::tie(itr, end) = _timeouts.equal_range(dr.timeout);
    while (itr != end)
    {
        if (itr->second->device->getID() == dr.device->getID())
        {
            _timeouts.erase(itr);
            break;
        }
        itr++;
    }
}


bool UnsolicitedHandler::distributeRequests(const MillisecondTimer &timer, const unsigned long until)
{
    generateKeepalives(_port);

    if( ! isPortRateLimited() )
    {
        readPortQueue(_port, _request_queue);
    }

    //  if we got work, attempt to distribute it to the device records
    return processQueue(_request_queue, __FUNCTION__, &UnsolicitedHandler::handleDeviceRequest, timer, until);
}


template<class Element>
bool UnsolicitedHandler::processQueue(std::list<Element> &queue, const char *function, void (UnsolicitedHandler::*processElement)(Element), const Cti::Timing::MillisecondTimer &timer, const unsigned long until)
{
    while( ! queue.empty() )
    {
        try
        {
            if( Element element = queue.front() )
            {
                (this->*processElement)(element);
            }
        }
        catch( ... )
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, describePort() << " failed while processing queue for method " << function);
        }

        queue.pop_front();

        if( timer.elapsed() > until )
        {
            return ! queue.empty();
        }
    }

    return false;
}


void UnsolicitedHandler::handleDeviceError(OUTMESS *om, const YukonError_t error)
{
    //  return an error - this deletes the OM
    INMESS im;
    OutEchoToIN(om, im);
    ReturnResultMessage(error, im, om);
    _port->incQueueProcessed();
}

void UnsolicitedHandler::handleDeviceRequest(OUTMESS *om)
{
    if( _port->isInhibited() )
    {
        return handleDeviceError(om, ClientErrors::PortInhibited);
    }

    device_record *dr = getDeviceRecordById(om->DeviceID);

    if( ! dr )
    {
        if( gConfigParms.isTrue("PORTER_UNSOLICITED_HANDLER_DEBUG") )
        {
            CTILOG_DEBUG(dout, "no device found for device id ("<< om->DeviceID <<")");
        }

        return handleDeviceError(om, ClientErrors::IdNotFound);
    }

    if( dr->device->isInhibited() )
    {
        return handleDeviceError(om, ClientErrors::DeviceInhibited);
    }

    if( om->ExpirationTime && om->ExpirationTime < CtiTime::now() )
    {
        return handleDeviceError(om, ClientErrors::RequestExpired);
    }

    const long device_id = dr->device->getID();

    if( isDeviceDisconnected(device_id) )
    {
        processCommStatus(ClientErrors::DeviceNotConnected, device_id, device_id, false, boost::static_pointer_cast<CtiDeviceBase>(dr->device));

        return handleDeviceError(om, ClientErrors::DeviceNotConnected);
    }

    if( gConfigParms.isTrue("PORTER_UNSOLICITED_HANDLER_DEBUG") )
    {
        CTILOG_DEBUG(dout, "queueing work for \""<< dr->device->getName() <<"\"");
    }

    dr->outbound.push_back(om);

    if( _active_devices.find(dr) == _active_devices.end() )
    {
        queueRequestPending(dr);
    }
}


void UnsolicitedHandler::generateKeepalives(CtiPortSPtr &port)
{
    const CtiTime TimeNow;

    //  check all of our devices to see if we need to issue a keepalive
    for each( const pair<long, device_record *> &dr_pair in _device_records )
    {
        device_record *dr = dr_pair.second;

        if( dr->device->isInhibited() )
        {
            continue;
        }

        if( isDnpDeviceType(dr->device->getType()) &&
            isDnpKeepaliveNeeded(*dr, TimeNow) )
        {
            generateDnpKeepalive(port, *dr);
            dr->last_keepalive = TimeNow;
        }
    }
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

void UnsolicitedHandler::generateDnpKeepalive(CtiPortSPtr &port, const device_record &dr)
{
    CtiRequestMsg msg(dr.device->getID(), "ping");
    CtiCommandParser parse(msg.CommandString());
    CtiDeviceSingle::CtiMessageList vg_list, ret_list;
    CtiDeviceSingle::OutMessageList om_list;

    dr.device->beginExecuteRequest(&msg, parse, vg_list, ret_list, om_list);

    for each(OUTMESS *om in om_list )
    {
        port->writeQueue(om);

        PorterStatisticsManager.newRequest(om->Port, om->DeviceID, om->TargetID, om->MessageFlags);
    }

    delete_container(vg_list);
    delete_container(ret_list);

    om_list.clear();
}


bool UnsolicitedHandler::startPendingRequests(const MillisecondTimer &timer, const unsigned long until)
{
    return processQueue(_request_pending, __FUNCTION__, &UnsolicitedHandler::startPendingRequest, timer, until);
}


void UnsolicitedHandler::startPendingRequest(device_record *dr)
{
    if( ! dr->outbound.empty() )
    {
        if( OUTMESS *om = dr->outbound.front() )
        {
            if( const auto status = dr->device->recvCommRequest(om) )
            {
                dr->device_status = status;

                queueRequestComplete(dr);
            }
            else
            {
                queueToGenerate(dr);
            }
        }
        else
        {
            //  null outmessage, needs to be removed
            dr->outbound.pop_front();

            //  but re-examine this device
            queueRequestPending(dr);
        }
    }
    else if( ! dr->inbound.empty() )
    {
        //  no new outbound work, so process the unexpected inbound
        if( isDnpDeviceType(dr->device->getType()) )
        {
            //  there is no outmessage, so we don't call recvCommRequest -
            //    we have to call the Device::DNP-specific initUnsolicited
            Devices::DnpDevice &dnp_device = static_cast<Devices::DnpDevice &>(*dr->device);

            try
            {
                dnp_device.initUnsolicited();

                dr->xfer.setInCountExpected(Protocols::DNP::DatalinkPacket::HeaderLength);  //  we expect at least a header

                //  push a null OM on there so can distinguish this request from anything else that comes in
                dr->outbound.push_back(0);

                queueToGenerate(dr);
            }
            catch( const std::exception& e )
            {
                CTILOG_EXCEPTION_ERROR(dout, e, "DNP device " << dnp_device.getName() << " had a configuration failure, unable to process inbound message.");
            }
        }
        else if( isGpuffDevice(*dr->device) )
        {
            //  GPUFF is unsolicited-only - decode inbounds right away
            queueToDecode(dr);
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
    return processQueue(_to_generate, __FUNCTION__, &UnsolicitedHandler::tryGenerate, timer, until);
}


void UnsolicitedHandler::tryGenerate(device_record *dr)
{
    dr->device_status = dr->device->generate(dr->xfer);

    if( dr->device_status )
    {
        if( dr->device->isTransactionComplete() )
        {
            queueRequestComplete(dr);
        }
        else
        {
            queueToGenerate(dr);
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
        dr->comm_status = ClientErrors::None;
    }

    //  if we have data, are expecting no data, or we have an error, decode right away
    if( ! dr->inbound.empty() || ! dr->xfer.getInCountExpected() || dr->comm_status )
    {
        queueToDecode(dr);
    }
    else
    {
        const CtiTime timeout = CtiTime::now() + getDeviceTimeout(*dr);

        queueWaitingForData(dr);

        //  insert because it's a multimap - we might have multiple entries for this timeout value
        _timeouts.emplace(timeout, dr);
        dr->timeout = timeout;
    }
}


string UnsolicitedHandler::describeDevice( const device_record &dr ) const
{
    ostringstream ostr;

    ostr << dr.device->getName() << " (" << describeDeviceAddress(dr.device->getID()) << ")";

    return ostr.str();
}


void UnsolicitedHandler::traceOutbound( const device_record &dr, YukonError_t status )
{
    CtiTraceMsg mTrace;
    string msg;

    SYSTEMTIME stm;
    GetLocalTime(&stm);

    //  set bright yellow for the time message
    mTrace.setBrightYellow();
    mTrace.setTrace(Cti::formatSystemTime(stm));
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


void UnsolicitedHandler::traceInbound( string address, YukonError_t status, const unsigned char *message, int mCount, const device_record *dr )
{
    CtiTraceMsg mTrace;
    string msg;
    
    SYSTEMTIME stm;
    GetLocalTime(&stm);

    //  set bright yellow for the time message
    mTrace.setBrightYellow();
    mTrace.setTrace(Cti::formatSystemTime(stm));
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
        msg = " (" + address + ")";
    }
    mTrace.setTrace(msg);
    mTrace.setEnd(false);
    _traceList.push_back(mTrace.replicateMessage());

    if( status )
    {
        if( status == ClientErrors::PortSimulated )
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

    if( status && status != ClientErrors::PortSimulated )
    {
        mTrace.setBrightRed();
        mTrace.setTrace( CtiError::GetErrorString(status) );
        mTrace.setEnd(true);
        _traceList.push_back(mTrace.replicateMessage());
        mTrace.setNormal();
    }
}


bool UnsolicitedHandler::isPortRateLimited() const
{
    return false;
}


void UnsolicitedHandler::readPortQueue(CtiPortSPtr &port, om_list &local_queue)
{
    unsigned long entries = 0;
    bool printed = false;
    unsigned long size;
    unsigned char priority;
    OUTMESS *om;

    unsigned long max_entries = port->queueCount();

    while( max_entries-- && port->readQueue( &size, (PPVOID)&om, DCWW_NOWAIT, &priority, &entries) == ClientErrors::None )
    {
        port->incQueueSubmittal();

        if( !printed && entries && gConfigParms.isTrue("PORTER_UNSOLICITED_HANDLER_DEBUG") )
        {
            CTILOG_DEBUG(dout, entries <<" additional entries on queue");
            printed = true;
        }

        local_queue.push_back(om);
    }
}


UnsolicitedHandler::device_record *UnsolicitedHandler::getDeviceRecordById( long device_id ) const
{
    return mapFindOrDefault(_device_records, device_id, nullptr);
}


void UnsolicitedHandler::addInboundWork(device_record &dr, packet *p)
{
    if( p )
    {
        dr.inbound.push(p);

        //  check to see if the device was waiting for anything
        device_activity_map::iterator itr = _waiting_devices.find(&dr);

        if( itr != _waiting_devices.end() )
        {
            _waiting_devices.erase(&dr);
            deactivateTimeout(dr);

            //  we just got work - we're ready to try a decode
            queueToDecode(&dr);
        }
        else if( _active_devices.find(&dr) == _active_devices.end() )
        {
            //  it's new work, so get him started next time the pending list is examined
            queueRequestPending(&dr);
        }
    }
}


bool UnsolicitedHandler::expireTimeouts(const MillisecondTimer &timer, const unsigned long until)
{
    CtiTime now;

    // we may have been waiting for a device that has already been removed from the timeout queue.
    while( ! _timeouts.empty() && _timeouts.begin()->first < now )
    {
        const CtiTime timeout = _timeouts.begin()->first;
        device_record *dr = _timeouts.begin()->second;

        _timeouts.erase(_timeouts.begin());

        if (timeout == dr->timeout)
        {
            queueToDecode(dr);
            _waiting_for_data.remove(dr);
        }

        if( timer.elapsed() > until )
        {
            return true;
        }
    }

    return false;
}


bool UnsolicitedHandler::processInbounds(const MillisecondTimer &timer, const unsigned long until)
{
    return processQueue(_to_decode, __FUNCTION__, &UnsolicitedHandler::processInbound, timer, until);
}


void UnsolicitedHandler::processInbound(device_record *dr)
{
    if( isGpuffDevice(*dr->device) )
    {
        processGpuffInbound(*dr);

        queueRequestComplete(dr);
    }
    else
    {
        processDeviceSingleInbound(*dr);

        if( dr->device->isTransactionComplete() )
        {
            queueRequestComplete(dr);
        }
        else
        {
            queueToGenerate(dr);
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
                dr.comm_status = ClientErrors::ReadTimeout;
            }
        }
    }

    dr.device_status = dr.device->decode(dr.xfer, dr.comm_status);

    if( dr.device_status || dr.xfer.getInCountExpected() )
    {
        int om_retry = 0;

        traceInbound(describeDeviceAddress(dr.device->getID()), dr.device_status, dr.xfer.getInBuffer(), dr.xfer.getInCountActual(), &dr);

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

            YukonError_t status = sendOutbound(dr);

            traceOutbound(dr, status);
        }
    }
    else
    {
        delete_container(packet.points);

        CTILOG_INFO(dout, "GPUFF device "<< dr.device->getName() <<" sequence number "<< packet.seq <<" already processed.");
    }

    delete p->data;
    delete p;
}


void UnsolicitedHandler::sendDevicePointsFromProtocol(vector<CtiPointDataMsg *> &points, const CtiDeviceSingleSPtr &device, CtiConnection &connection)
{
    unique_ptr<CtiMultiMsg> m(new CtiMultiMsg());

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
        connection.WriteConnQue(m.release(), CALLSITE);
    }
}


void UnsolicitedHandler::trace()
{
    if( _traceList.empty() )
    {
        _portLog->poke();  //  called 2x-10x/second, depending if _activeDevices is empty

        return;
    }

    if( gLogPorts )
    {
        Cti::StreamBuffer output;

        for each( const CtiMessage *msg in _traceList )
        {
            const CtiTraceMsg* trace = static_cast<const CtiTraceMsg*>(msg);
            output << trace->getTrace();
            if(trace->isEnd())
            {
                output << endl;
            }
        }

        CTILOG_INFO(_portLog, output);
    }

    for( int attempt = 5; attempt >= 0; attempt-- )
    {
        Cti::TryLockGuard<CtiCriticalSection> coutTryGuard(coutMux);

        if( ! coutTryGuard.isAcquired() && attempt )
        {
            Sleep(100);

            continue;
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

        break;
    }

    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);

    delete_container(_traceList);
    _traceList.clear();
}


bool UnsolicitedHandler::sendResults(const MillisecondTimer &timer, const unsigned long until)
{
    return processQueue(_request_complete, __FUNCTION__, &UnsolicitedHandler::sendResult, timer, until);
}


void UnsolicitedHandler::sendResult(device_record *dr)
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

            OutEchoToIN(*om, im);

            //  ignoring the result of this for now - DeviceDnp always returns NoError
            dr->device->sendCommResult(im);

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
                ReturnResultMessage(dr->device_status, im, om);

                _port->incQueueProcessed();

                //  If ReturnResultMessage doesn't delete the OM (i.e. wasn't an error condition), then we'll delete it
                delete om;
            }
        }

        dr->outbound.pop_front();
    }

    //  all done with this request - check to see if there's anything else waiting for us
    queueRequestPending(dr);
}


void UnsolicitedHandler::receiveMessage(CtiMessage *msg)
{
    _message_queue.putQueue(msg);
}

void UnsolicitedHandler::queueRequestPending(device_record *dr)
{
    CTILOG_TRACE(dout, "Queueing device " << dr->device->getID() << " to _request_pending");

    setDeviceState(_request_pending, dr, RequestPending);
}

void UnsolicitedHandler::queueToGenerate(device_record *dr)
{
    CTILOG_TRACE(dout, "Queueing device " << dr->device->getID() << " to _to_generate");

    setDeviceState(_to_generate, dr, ToGenerate);
}

void UnsolicitedHandler::queueWaitingForData(device_record *dr)
{
    CTILOG_TRACE(dout, "Queueing device " << dr->device->getID() << " to _waiting_for_data");

    _waiting_for_data.insert(_waiting_for_data.end(), dr);
    _active_devices[dr] = WaitingForData;

    _waiting_devices[dr] = WaitingForData;
}

void UnsolicitedHandler::queueToDecode(device_record *dr)
{
    CTILOG_TRACE(dout, "Queueing device " << dr->device->getID() << " to _to_decode");

    setDeviceState(_to_decode, dr, ToDecode);
}

void UnsolicitedHandler::queueRequestComplete(device_record *dr)
{
    CTILOG_TRACE(dout, "Queueing device " << dr->device->getID() << " to _request_complete");

    setDeviceState(_request_complete, dr, RequestComplete);
}

void UnsolicitedHandler::setDeviceState(device_list &queue, device_record *dr, DeviceState state)
{
    queue.insert(queue.end(), dr);
    _active_devices[dr] = state;
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


void UnsolicitedMessenger::sendMessageToClients(const CtiDBChangeMsg &msg)
{
    CtiLockGuard< CtiCriticalSection > lock(_client_mux);

    for each( UnsolicitedHandler *client in _clients )
    {
        client->receiveMessage(msg.replicateMessage());
    }
}


}
