

#include "yukon.h"
#include "logger.h"
#include "GangOperatedVoltageRegulator.h"


namespace Cti           {
namespace CapControl    {

RWDEFINE_COLLECTABLE( GangOperatedVoltageRegulator, CTIVOLTAGEREGULATOR_ID )


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator()
    : VoltageRegulator(),
    _lowerTap(false),
    _raiseTap(false),
    _autoRemote(false)
{
    // empty...
}


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator(Cti::RowReader & rdr)
    : VoltageRegulator(rdr),
    _lowerTap(false),
    _raiseTap(false),
    _autoRemote(false)
{
    // empty...
}


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator(const GangOperatedVoltageRegulator & toCopy)
    : VoltageRegulator(),
    _lowerTap(false),
    _raiseTap(false),
    _autoRemote(false)
{
    operator=(toCopy);
}


GangOperatedVoltageRegulator & GangOperatedVoltageRegulator::operator=(const GangOperatedVoltageRegulator & rhs)
{
    if ( this != &rhs )
    {
        VoltageRegulator::operator=(rhs);
    
        _lowerTap   = rhs._lowerTap;
        _raiseTap   = rhs._raiseTap;
        _autoRemote = rhs._autoRemote;
    }

    return *this;
}


void GangOperatedVoltageRegulator::saveGuts(RWvostream & ostrm) const
{
    VoltageRegulator::saveGuts(ostrm);

    ostrm
        << 0                //Parent Id.
        << _lowerTap
        << _raiseTap
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

    bool raiseTap = false;
    bool lowerTap = false;

    if ((_lastTapOperationTime + tapDelay) > now)
    {
        if (_lastTapOperation == VoltageRegulator::RaiseTap)
        {
            raiseTap = true;
        }
        else if (_lastTapOperation == VoltageRegulator::LowerTap)
        {
            lowerTap = true;
        }
    }

    if (_raiseTap != raiseTap)
    {
        _raiseTap = raiseTap;
        setUpdated(true);
    }

    if (_lowerTap != lowerTap)
    {
        _lowerTap = lowerTap;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);

        dout << CtiTime() << " - " << error.what() << std::endl;
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

