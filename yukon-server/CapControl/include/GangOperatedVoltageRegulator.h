
#pragma warning( disable : 4786)

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

protected:

    bool _recentOperation;
    bool _autoRemote;
};

}
}

