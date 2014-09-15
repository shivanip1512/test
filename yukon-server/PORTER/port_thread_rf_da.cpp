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

extern CtiDeviceManager DeviceManager;

namespace Cti    {
namespace Porter {

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

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Shutdown PortThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
        }
    }
}

RfDaPortHandler::RfDaPortHandler( Ports::RfDaPortSPtr &rf_da_port, CtiDeviceManager &deviceManager ) :
    UnsolicitedHandler(boost::static_pointer_cast<CtiPort>(rf_da_port), deviceManager),
    _rf_da_port(rf_da_port)
{
    Messaging::Rfn::E2eMessenger::registerDnpHandler(
            boost::bind(&RfDaPortHandler::receiveIndication, this, _1),
            rf_da_port->getRfnIdentifier());
}


const DWORD RfDaConcurrentRequestsMax = gConfigParms.getValueAsULong("RF_DA_CONCURRENT_REQUESTS", 4);


bool RfDaPortHandler::isPortRateLimited() const
{
    return _rf_da_port->concurrentRequests() >= RfDaConcurrentRequestsMax;
}


void RfDaPortHandler::receiveIndication(Messaging::Rfn::E2eMessenger::Indication msg)
{
    CtiLockGuard<CtiCriticalSection> lock(_indicationMux);

    _indications.push_back(msg);
}


void RfDaPortHandler::receiveConfirm(Messaging::Rfn::E2eMessenger::Confirm msg)
{
    //  ideally this would report any errors back to the unsolicited handler and update the timeout if the packet was sent successfully
}


YukonError_t RfDaPortHandler::sendOutbound( device_record &dr )
{
    if( gConfigParms.isTrue("PORTER_RFDA_DEBUG") )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::RfDaPortHandler::sendOutbound() - sending packet to "
                          << describeDeviceAddress(dr.device->getID()) << " "
                          << __FILE__ << " (" << __LINE__ << ")" << endl;

        for( int offset = 0; offset < dr.xfer.getOutCount(); offset++ )
        {
            dout << " " << CtiNumStr(dr.xfer.getOutBuffer()[offset]).hex().zpad(2).toString();
        }

        dout << endl;
    }

    Messaging::Rfn::E2eMessenger::Request msg;

    msg.rfnIdentifier = _rf_da_port->getRfnIdentifier();
    msg.priority = std::max<int>(1, std::min<int>(dr.outbound.front()->Priority, MAXPRIORITY));

    msg.payload.assign(
            dr.xfer.getOutBuffer(),
            dr.xfer.getOutBuffer() + dr.xfer.getOutCount());

    Messaging::Rfn::E2eMessenger::sendE2eAp_Dnp(msg, boost::bind(&RfDaPortHandler::receiveConfirm, this, _1));

    dr.last_outbound = CtiTime::now();

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

    return rfnid.manufacturer + "_" + rfnid.model + "_" + rfnid.serialNumber;
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

    if( ! _device_id )
    {
        if( ! recentIndications.empty() && gConfigParms.isTrue("PORTER_RFDA_DEBUG") )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::RfDaPortHandler::collectInbounds - _device_id not set, logging packets " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        for each( const Messaging::Rfn::E2eMessenger::Indication & ind in recentIndications )
        {
            //  this packet was unhandled, so we trace it
            traceInbound("(unhandled)", ClientErrors::None, &ind.payload.front(), ind.payload.size());
        }

        return false;
    }

    device_record *dr = getDeviceRecordById(*_device_id);

    if( ! dr || ! dr->device )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::RfDaPortHandler::collectInbounds - can't find device with ID (" << *_device_id << ") " << __FILE__ << " (" << __LINE__ << ")" << endl;

        return false;
    }

    if( dr->device->isInhibited() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Cti::Porter::RfDaPortHandler::collectInbounds - device \"" << dr->device->getName() << "\" is inhibited, discarding packets " << __FILE__ << " (" << __LINE__ << ")" << endl;

        return false;
    }

    //  ignore the timer - this is bound to be quick
    for each( Messaging::Rfn::E2eMessenger::Indication ind in recentIndications )
    {
        if( gConfigParms.isTrue("PORTER_RFDA_DEBUG") )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Cti::Porter::RfDaPortHandler::collectInbounds - new inbound for \"" << ind.rfnIdentifier << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        rf_packet *p = new rf_packet;

        p->rfnid = ind.rfnIdentifier;
        p->len = ind.payload.size();
        p->used = 0;
        p->data = new unsigned char[p->len];

        std::copy(ind.payload.begin(), ind.payload.end(), p->data);

        addInboundWork(*dr, p);
    }

    return false;
}


void RfDaPortHandler::loadDeviceProperties(const std::vector<const CtiDeviceSingle *> &devices)
{
    for each( const CtiDeviceSingle *dev in devices )
    {
        if( dev )
        {
            //  just grab the first one - we're only supposed to have one device assigned
            _device_id = dev->getID();

            return;
        }
    }
}


void RfDaPortHandler::addDeviceProperties(const CtiDeviceSingle &device)
{
    //  just grab the ID - we're only supposed to have one device assigned
    _device_id = device.getID();
}


void RfDaPortHandler::deleteDeviceProperties(const CtiDeviceSingle &device)
{
    //  no more devices
    _device_id.reset();
}


}
}

