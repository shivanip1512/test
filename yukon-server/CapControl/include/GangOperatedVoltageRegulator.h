#pragma once

#include "VoltageRegulator.h"

namespace Cti           {
namespace CapControl    {

class GangOperatedVoltageRegulator : public VoltageRegulator
{
public:

    RWDECLARE_COLLECTABLE( GangOperatedVoltageRegulator )

    GangOperatedVoltageRegulator();
    GangOperatedVoltageRegulator(Cti::RowReader & rdr);
    GangOperatedVoltageRegulator(const GangOperatedVoltageRegulator & toCopy);

    GangOperatedVoltageRegulator & operator=(const GangOperatedVoltageRegulator & rhs);

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

    virtual IDSet getVoltagePointIDs();

protected:

    bool            _recentTapOperation;

    OperatingMode   _lastOperatingMode;
    OperatingMode   _lastCommandedOperatingMode;

    static const PointAttribute attributes[];
};

}
}

