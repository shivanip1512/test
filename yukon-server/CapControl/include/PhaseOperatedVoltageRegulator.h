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

//    virtual void executeEnableKeepAlive();
};

}
}

