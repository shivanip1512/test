

#include "yukon.h"
#include "logger.h"
#include "GangOperatedVoltageRegulator.h"
#include "ccutil.h"
#include "capcontroller.h"
#include "ccmessage.h"



namespace Cti           {
namespace CapControl    {

RWDEFINE_COLLECTABLE( GangOperatedVoltageRegulator, CTIVOLTAGEREGULATOR_ID )


const PointAttribute GangOperatedVoltageRegulator::attributes[] =
{
    PointAttribute::VoltageY,
    PointAttribute::TapDown,
    PointAttribute::TapUp,
    PointAttribute::TapPosition,
    PointAttribute::AutoRemoteControl,
    PointAttribute::KeepAlive
};


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator()
    : VoltageRegulator(),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false)
{
    // empty...
}


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator(Cti::RowReader & rdr)
    : VoltageRegulator(rdr),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false)
{
    // empty...
}


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator(const GangOperatedVoltageRegulator & toCopy)
    : VoltageRegulator(),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false)
{
    operator=(toCopy);
}


GangOperatedVoltageRegulator & GangOperatedVoltageRegulator::operator=(const GangOperatedVoltageRegulator & rhs)
{
    if ( this != &rhs )
    {
        VoltageRegulator::operator=(rhs);

        _recentTapOperation = rhs._recentTapOperation;

        _lastOperatingMode          = rhs._lastOperatingMode;
        _lastCommandedOperatingMode = rhs._lastCommandedOperatingMode;
    }

    return *this;
}


void GangOperatedVoltageRegulator::saveGuts(RWvostream & ostrm) const
{
    VoltageRegulator::saveGuts(ostrm);

    ostrm
        << _recentTapOperation
        << _lastOperatingMode
        << _lastCommandedOperatingMode;
}


void GangOperatedVoltageRegulator::loadAttributes(AttributeService * service)
{
    for each ( const PointAttribute attribute in attributes )
    {
        loadPointAttributes(service, attribute);
    }
}


void GangOperatedVoltageRegulator::updateFlags(const unsigned tapDelay)
{
    bool recentOperation = ( ( _lastTapOperationTime + 30 ) > CtiTime() );

    if (_recentTapOperation != recentOperation)
    {
        _recentTapOperation = recentOperation;
        setUpdated(true);
    }

    OperatingMode currentMode = getOperatingMode();

    if (_lastOperatingMode != currentMode)
    {
        _lastOperatingMode = currentMode;
        setUpdated(true);
    }
}


VoltageRegulator * GangOperatedVoltageRegulator::replicate() const
{
    return new GangOperatedVoltageRegulator(*this);
}


const VoltageRegulator::Type GangOperatedVoltageRegulator::getType() const
{
    return VoltageRegulator::LoadTapChangerType;
}


void GangOperatedVoltageRegulator::executeIntegrityScan()
{
    // Scan Downside voltage only

    executeIntegrityScanHelper( getPointByAttribute( PointAttribute::VoltageY ) );
}


void GangOperatedVoltageRegulator::executeEnableKeepAlive()
{
    executeKeepAliveHelper( getPointByAttribute( PointAttribute::KeepAlive ), _keepAliveConfig );
}


void GangOperatedVoltageRegulator::executeDisableKeepAlive()
{
    executeKeepAliveHelper( getPointByAttribute( PointAttribute::KeepAlive ), 0);
}


void GangOperatedVoltageRegulator::executeEnableRemoteControl()
{
    _lastCommandedOperatingMode = RemoteMode;

    executeRemoteControlHelper( getPointByAttribute( PointAttribute::KeepAlive ), _keepAliveConfig, "Enable Remote Control",
                                capControlIvvcRemoteControlEvent );

    executeEnableKeepAlive();
}


void GangOperatedVoltageRegulator::executeDisableRemoteControl()
{
    _lastCommandedOperatingMode = LocalMode;

    executeRemoteControlHelper( getPointByAttribute( PointAttribute::KeepAlive ), 0, "Disable Remote Control",
                                capControlIvvcRemoteControlEvent );

    executeDisableKeepAlive();
}


VoltageRegulator::IDSet GangOperatedVoltageRegulator::getVoltagePointIDs()
{
    IDSet IDs;

    LitePoint voltage = getPointByAttribute( PointAttribute::VoltageY );

    IDs.insert( voltage.getPointId() );

    return IDs;
}


}
}

