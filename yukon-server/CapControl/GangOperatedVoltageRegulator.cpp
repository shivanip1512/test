

#include "yukon.h"
#include "logger.h"
#include "GangOperatedVoltageRegulator.h"
#include "ccutil.h"


namespace Cti           {
namespace CapControl    {

RWDEFINE_COLLECTABLE( GangOperatedVoltageRegulator, CTIVOLTAGEREGULATOR_ID )


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator()
    : VoltageRegulator(),
    _autoRemote(false),
    _recentOperation(false)
{
    // empty...
}


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator(Cti::RowReader & rdr)
    : VoltageRegulator(rdr),
    _autoRemote(false),
    _recentOperation(false)
{
    // empty...
}


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator(const GangOperatedVoltageRegulator & toCopy)
    : VoltageRegulator(),
    _autoRemote(false),
    _recentOperation(false)
{
    operator=(toCopy);
}


GangOperatedVoltageRegulator & GangOperatedVoltageRegulator::operator=(const GangOperatedVoltageRegulator & rhs)
{
    if ( this != &rhs )
    {
        VoltageRegulator::operator=(rhs);

        _recentOperation = rhs._recentOperation;
        _autoRemote = rhs._autoRemote;
    }

    return *this;
}


void GangOperatedVoltageRegulator::saveGuts(RWvostream & ostrm) const
{
    VoltageRegulator::saveGuts(ostrm);

    ostrm << _recentOperation
          << _autoRemote
          << getOperatingMode();
}


void GangOperatedVoltageRegulator::loadAttributes(AttributeService * service)
{
    const PointAttribute attributes[] =
    {
        PointAttribute::Voltage,
        PointAttribute::TapDown,
        PointAttribute::TapUp,
        PointAttribute::TapPosition,
        PointAttribute::AutoRemoteControl,
        PointAttribute::KeepAlive
    };

    for each ( const PointAttribute attribute in attributes )
    {
        loadPointAttributes(service, attribute);
    }
}


void GangOperatedVoltageRegulator::updateFlags(const unsigned tapDelay)
{
    CtiTime now;

    bool recentOperation = false;

    if ((_lastTapOperationTime + 30) > now)
    {
        recentOperation = true;
    }

    if (_recentOperation != recentOperation)
    {
        _recentOperation = recentOperation;
        setUpdated(true);
    }

    try
    {
        bool   autoRemote = false;
        double pointValue = 0.0;

        if ( _pointValues.getPointValue( getPointByAttribute(PointAttribute::AutoRemoteControl).getPointId(), pointValue ) )
        {
            autoRemote = (pointValue == 0.0);
        }

        if (_autoRemote != autoRemote)
        {
            _autoRemote = autoRemote;
            setUpdated(true);
        }
    }
    catch ( const MissingPointAttribute & error )
    {
        if (error.complain())
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << error.what() << std::endl;
        }
    }
}


VoltageRegulator * GangOperatedVoltageRegulator::replicate() const
{
    return new GangOperatedVoltageRegulator(*this);
}


const VoltageRegulator::Type GangOperatedVoltageRegulator::getType() const
{
    if ( getPaoType() == VoltageRegulator::LoadTapChanger )
    {
        return VoltageRegulator::LoadTapChangerType;
    }

    return VoltageRegulator::GangOperatedVoltageRegulatorType;
}

}
}

