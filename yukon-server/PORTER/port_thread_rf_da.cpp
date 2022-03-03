#include "precompiled.h"

#include "port_thread_rf_da.h"

#include <boost/shared_ptr.hpp>
#include <boost/scoped_array.hpp>
#include "boostutil.h"

#include "c_port_interface.h"

#include "portglob.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_dnp.h"
#include "dev_rds.h"
#include "cparms.h"
#include "numstr.h"
#include "socket_helper.h"
#include "std_helper.h"

#include "portfield.h"

#include "connection_client.h"

// Some Global Manager types to allow us some RTDB stuff.

using namespace std;

using Cti::Timing::MillisecondTimer;
using Cti::Logging::Vector::Hex::operator<<;
using Cti::Logging::Range::Hex::operator<<;

extern CtiDeviceManager DeviceManager;

namespace Cti::Porter {

/* Threads that handle each port for communications */
void PortRfDaThread(void *pid)
{
    long portid = (long)pid;

    CtiPortSPtr Port(PortManager.getPortById(portid));

    if( Port && Port->getType() == PortTypeRfDa )
    {
        ostringstream thread_name;

        thread_name << "RF DA PortID " << setw(4) << setfill('0') << Port->getPortID();

        SetThreadName(-1, thread_name.str().c_str());

        Ports::RfDaPortSPtr rf_da_port = boost::static_pointer_cast<Ports::RfDaPort>(Port);

        RfDaPortHandler rf_da(rf_da_port, DeviceManager);

        rf_da.run();

        CTILOG_INFO(dout, "Shutdown PortThread for port: "<< setw(4) << Port->getPortID() <<" / "<< Port->getName());
    }
}

RfDaPortHandler::RfDaPortHandler( Ports::RfDaPortSPtr &rf_da_port, CtiDeviceManager &deviceManager ) :
    UnsolicitedHandler(boost::static_pointer_cast<CtiPort>(rf_da_port), deviceManager),
    _rf_da_port(rf_da_port),
    _active_endpoint(nullptr)
{
    Messaging::Rfn::E2eMessenger::registerDnpHandler(
            [&](const Messaging::Rfn::E2eMessenger::Indication &msg)
            {
                CtiLockGuard<CtiCriticalSection> lock(_indicationMux);

                _indications.push_back(msg);
            },
            rf_da_port->getRfnIdentifier());
}


const DWORD RfDaConcurrentRequestsMax = gConfigParms.getValueAsULong("RF_DA_CONCURRENT_REQUESTS", 4);


bool RfDaPortHandler::isPortRateLimited() const
{
    return _rf_da_port->concurrentRequests() >= RfDaConcurrentRequestsMax;
}


YukonError_t RfDaPortHandler::sendOutbound( device_record &dr )
{
    if( gConfigParms.isTrue("PORTER_RFDA_DEBUG") )
    {
        CTILOG_DEBUG(dout, "sending packet to "<< describeDeviceAddress(dr.device->getID()) <<" "<<
                endl << arrayToRange(dr.xfer.getOutBuffer(), dr.xfer.getOutCount()));
    }

    Messaging::Rfn::E2eMessenger::Request msg;

    msg.rfnIdentifier = _rf_da_port->getRfnIdentifier();
    msg.priorityClass = Messaging::Rfn::PriorityClass::RfDa;

    if( dr.outbound.empty() || ! dr.outbound.front() )
    {
        msg.priority   = MAXPRIORITY - 1;  //  unsolicited, highest priority response
        msg.expiration = CtiTime::now() + getDeviceTimeout(dr);
        msg.groupId    = 0;
    }
    else
    {
        const CtiOutMessage &om = *dr.outbound.front();

        msg.priority   = std::clamp<int>(om.Priority, 1, MAXPRIORITY);

        msg.expiration = om.ExpirationTime
                             ? CtiTime(om.ExpirationTime)
                             : CtiTime::now() + 900;  //  15 minutes, as per the RF-DA spec

        msg.groupId    = om.Request.GrpMsgID;
    }

    msg.payload.assign(
            dr.xfer.getOutBuffer(),
            dr.xfer.getOutBuffer() + dr.xfer.getOutCount());

    auto successCallback =
        [](const Messaging::Rfn::E2eMessenger::Confirm &msg) {
            //  TODO - report success/error
        };

    auto failCallback =
        [](const YukonError_t cause){
            //  TODO - report error
        };

    Messaging::Rfn::E2eMessenger::sendE2eAp_Dnp(msg, successCallback, failCallback);

    dr.last_outbound = CtiTime::now();

    _last_endpoint_send_time = std::chrono::high_resolution_clock::now();

    return ClientErrors::None;
}


const unsigned RfDaDeviceTimeout = gConfigParms.getValueAsULong("RF_DA_DEVICE_TIMEOUT", 150);

unsigned RfDaPortHandler::getDeviceTimeout( const device_record &dr ) const
{
    return RfDaDeviceTimeout;
}


std::string RfDaPortHandler::describePort( void ) const
{
    return "RF DA port " + _rf_da_port->getName();
}


std::string RfDaPortHandler::describeDeviceAddress( const long device_id ) const
{
    const RfnIdentifier &rfnid = _rf_da_port->getRfnIdentifier();

    std::string device_address = "(no address)";

    if( auto record = getDeviceRecordById(device_id) )
    {
        if( record->device )
        {
            device_address = DnpLookup::getDnpAddresses(*record->device).toString();
        }
    }

    return device_address + "@" + rfnid.manufacturer + "_" + rfnid.model + "_" + rfnid.serialNumber;
}



bool RfDaPortHandler::collectInbounds( const MillisecondTimer & timer, const unsigned long until)
{
    IndicationQueue recentIndications;

    {
        CtiLockGuard<CtiCriticalSection> lock(_indicationMux);

        recentIndications.swap(_indications);
    }

    if( recentIndications.empty() )
    {
        return false;
    }

    if( _dnpLookup.empty() )
    {
        if( ! recentIndications.empty() && gConfigParms.isTrue("PORTER_RFDA_DEBUG") )
        {
            CTILOG_DEBUG(dout, "No DNP devices registered on port, logging packets");
        }

        for each( const Messaging::Rfn::E2eMessenger::Indication & ind in recentIndications )
        {
            //  this packet was unhandled, so we trace it
            traceInbound("(unhandled)", ClientErrors::None, &ind.payload.front(), ind.payload.size());
        }

        return false;
    }

    //  ignore the timer - this is bound to be quick
    for( const auto &ind : recentIndications )
    {
        if( gConfigParms.isTrue("PORTER_RFDA_DEBUG") )
        {
            CTILOG_DEBUG(dout, "new inbound for "<< ind.rfnIdentifier << endl << ind.payload);
        }

        rf_packet *p = new rf_packet;

        p->rfnid = ind.rfnIdentifier;
        p->len = ind.payload.size();
        p->used = 0;
        p->data = new unsigned char[p->len];

        auto output_itr = stdext::make_checked_array_iterator(p->data, p->len);

        boost::copy(ind.payload, output_itr);

        p->dnpAddress = {
            static_cast<unsigned short>(p->data[4] | (p->data[5] << 8)),
            static_cast<unsigned short>(p->data[6] | (p->data[7] << 8)) };

        auto id = _dnpLookup.getDeviceIdForAddress(p->dnpAddress);

        if( !id )
        {
            CTILOG_ERROR(dout, "can't find device ID for DNP addresses " << p->dnpAddress.toString());
            return false;
        }

        device_record *dr = getDeviceRecordById(*id);

        if( !dr || !dr->device )
        {
            CTILOG_ERROR(dout, "can't find device with id " << *id);
            return false;
        }

        if( dr->device->isInhibited() )
        {
            CTILOG_ERROR(dout, "device \"" << dr->device->getName() << "\" is inhibited, discarding packets");
            return false;
        }

        addInboundWork(*dr, p);
    }

    return false;
}


void RfDaPortHandler::loadDeviceProperties(const std::vector<const CtiDeviceSingle *> &devices)
{
    for( const auto dev : devices )
    {
        addDeviceProperties(*dev);
    }
}


void RfDaPortHandler::addDeviceProperties(const CtiDeviceSingle &device)
{
    if( isDnpDeviceType(device.getType()) )
    {
        if( ! _dnpLookup.addDevice(device) )
        {
            // The insert didn't occur! Complain.
            CTILOG_ERROR(dout, "DNP lookup insert failed for device " << device.getName() <<
                ". Please update the master/slave values for this device to be unique.");
        }
    }
    else
    {
        CTILOG_WARN(dout, "Ignoring non-DNP device on RF-DA port" << FormattedList::of(
            "Device ID",   device.getID(),
            "Device name", device.getName()));
    }
}


void RfDaPortHandler::deleteDeviceProperties(const CtiDeviceSingle &device)
{
    _dnpLookup.deleteDevice(device.getID());
}


void RfDaPortHandler::updateDeviceProperties(const CtiDeviceSingle &device)
{
    if( isDnpDeviceType(device.getType()) )
    {
        _dnpLookup.updateDevice(device);
    }
    else
    {
        _dnpLookup.deleteDevice(device.getID());
    }
}


bool RfDaPortHandler::isPostCommWaitComplete(const device_record& dr, ULONG postCommWait) const
{
    return std::chrono::high_resolution_clock::now() >= ( _last_endpoint_send_time + std::chrono::milliseconds( postCommWait ) );
}

void RfDaPortHandler::setDeviceActive(const device_record& dr)
{
    _active_endpoint = &dr;
}

bool RfDaPortHandler::isDeviceActive(const device_record& dr)
{
    return _active_endpoint == &dr;
}

void RfDaPortHandler::clearActiveDevice(const device_record& dr)
{
    _active_endpoint = nullptr;
}


}