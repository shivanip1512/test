#pragma once

#include "VoltageRegulator.h"

namespace Cti           {
namespace CapControl    {

class PhaseOperatedVoltageRegulator : public VoltageRegulator
{
public:
    DECLARE_COLLECTABLE( PhaseOperatedVoltageRegulator );

public:
    PhaseOperatedVoltageRegulator();
    PhaseOperatedVoltageRegulator(Cti::RowReader & rdr);
    PhaseOperatedVoltageRegulator(const PhaseOperatedVoltageRegulator & toCopy);

    PhaseOperatedVoltageRegulator & operator=(const PhaseOperatedVoltageRegulator & rhs);

    virtual void loadAttributes(AttributeService * service);

    virtual void updateFlags(const unsigned tapDelay);

    virtual VoltageRegulator * replicate() const;

    virtual const VoltageRegulator::Type getType() const;

    virtual void executeIntegrityScan();
    virtual void executeEnableKeepAlive();
    virtual void executeDisableKeepAlive();
    virtual void executeEnableRemoteControl();
    virtual void executeDisableRemoteControl();

    virtual IDSet getVoltagePointIDs();

    virtual bool            getRecentTapOperation() const { return _recentTapOperation; }
    virtual OperatingMode   getLastOperatingMode()  const { return _lastOperatingMode; }
    virtual OperatingMode   getLastCommandedOperatingMode() const { return _lastCommandedOperatingMode; }

protected:
    
    long getKeepAliveRefreshRate();

    bool            _recentTapOperation;

    OperatingMode   _lastOperatingMode;
    OperatingMode   _lastCommandedOperatingMode;
};

}
}

