
#pragma warning( disable : 4786)

#pragma once

#include "VoltageRegulator.h"


namespace Cti           {
namespace CapControl    {

class PhaseOperatedVoltageRegulator : public VoltageRegulator
{
public:

    RWDECLARE_COLLECTABLE( PhaseOperatedVoltageRegulator )

    PhaseOperatedVoltageRegulator();
    PhaseOperatedVoltageRegulator(Cti::RowReader & rdr);
    PhaseOperatedVoltageRegulator(const PhaseOperatedVoltageRegulator & toCopy);

    PhaseOperatedVoltageRegulator & operator=(const PhaseOperatedVoltageRegulator & rhs);

    virtual void saveGuts(RWvostream & ostrm) const;

    virtual void loadAttributes(AttributeService * service);

    virtual void updateFlags(const unsigned tapDelay);

    virtual VoltageRegulator * replicate() const;

    virtual const VoltageRegulator::Type getType() const;

    virtual void executeIntegrityScan();
    virtual void executeEnableKeepAlive();
    virtual void executeDisableKeepAlive();
    virtual void executeEnableRemoteControl();
    virtual void executeDisableRemoteControl();

protected:

    bool            _recentTapOperation;

    OperatingMode   _lastOperatingMode;
    OperatingMode   _lastCommandedOperatingMode;

    static const PointAttribute attributes[];
};

}
}

