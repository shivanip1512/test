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

    std::string describePort() const override;

    bool setupPort() override         { return true; }  //  nothing to do
    bool manageConnections() override { return false; }  //  nothing to do

    YukonError_t sendOutbound( device_record &dr ) override;
    unsigned getDeviceTimeout( const device_record &dr ) const override;
    bool collectInbounds(const Cti::Timing::MillisecondTimer & timer, const unsigned long until) override;

    void loadDeviceProperties(const std::vector<const CtiDeviceSingle *> &devices) override;

    void addDeviceProperties   (const CtiDeviceSingle &device) override;
    void updateDeviceProperties(const CtiDeviceSingle &device) override {}
    void deleteDeviceProperties(const CtiDeviceSingle &device) override;

    void updatePortProperties() override {}  //  no properties

    bool isDeviceDisconnected( const long device_id ) const override { return false; }

    std::string describeDeviceAddress( const long device_id ) const override;

    RfnIdentifier getRfnIdentifier() const { return RfnIdentifier(); }
};

}
}

