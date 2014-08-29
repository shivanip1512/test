#pragma once
#include "yukon.h"

#include "unsolicited_handler.h"
#include "port_rf_da.h"
#include "rfn_e2e_messenger.h"

namespace Cti    {
namespace Porter {

void PortRfDaThread(void *pid);

class RfDaPortHandler : public UnsolicitedHandler
{
    Ports::RfDaPortSPtr _rf_da_port;

    boost::optional<long> _device_id;

    typedef std::vector<Messaging::Rfn::E2eMessenger::Indication> IndicationQueue;

    CtiCriticalSection _indicationMux;
    IndicationQueue _indications;

    struct rf_packet : packet
    {
        RfnIdentifier rfnid;

        virtual std::string describeAddress() const
        {
            return rfnid.manufacturer + "_" + rfnid.model +"_" + rfnid.serialNumber;
        }
    };

    virtual bool isPortRateLimited() const;

public:

    RfDaPortHandler( Ports::RfDaPortSPtr &port, CtiDeviceManager &deviceManager );

protected:

    virtual std::string describePort() const;

    virtual bool setupPort()         { return true; }  //  nothing to do
    virtual bool manageConnections() { return false; }  //  nothing to do

    void receiveConfirm(Messaging::Rfn::E2eMessenger::Confirm msg);

    virtual YukonError_t sendOutbound( device_record &dr );
    virtual unsigned getDeviceTimeout( const device_record &dr ) const;
    virtual bool collectInbounds(const Cti::Timing::MillisecondTimer & timer, const unsigned long until);

    virtual void loadDeviceProperties(const std::vector<const CtiDeviceSingle *> &devices);

    virtual void addDeviceProperties   (const CtiDeviceSingle &device);
    virtual void updateDeviceProperties(const CtiDeviceSingle &device) {}
    virtual void deleteDeviceProperties(const CtiDeviceSingle &device);

    virtual void updatePortProperties() {}  //  no properties

    virtual bool isDeviceDisconnected( const long device_id ) const { return false; }

    virtual std::string describeDeviceAddress( const long device_id ) const;

    RfnIdentifier getRfnIdentifier() const { return RfnIdentifier(); }

    void receiveIndication(Messaging::Rfn::E2eMessenger::Indication msg);
};

}
}

