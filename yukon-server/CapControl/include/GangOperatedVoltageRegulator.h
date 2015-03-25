#pragma once

#include "VoltageRegulator.h"

namespace Cti           {
namespace CapControl    {

class GangOperatedVoltageRegulator : public VoltageRegulator
{
public:
    DECLARE_COLLECTABLE( GangOperatedVoltageRegulator );

public:
    GangOperatedVoltageRegulator();
    GangOperatedVoltageRegulator(Cti::RowReader & rdr);
    GangOperatedVoltageRegulator(const GangOperatedVoltageRegulator & toCopy);

    GangOperatedVoltageRegulator & operator=(const GangOperatedVoltageRegulator & rhs);

    virtual void loadAttributes(AttributeService * service);

    virtual void updateFlags(const unsigned tapDelay);

    virtual VoltageRegulator * replicate() const;

    virtual const VoltageRegulator::Type getType() const;
};

}
}

