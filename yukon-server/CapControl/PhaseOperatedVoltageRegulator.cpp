

#include "yukon.h"
#include "logger.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "ccutil.h"


namespace Cti           {
namespace CapControl    {

RWDEFINE_COLLECTABLE( PhaseOperatedVoltageRegulator, CTIVOLTAGEREGULATOR_ID )


const PointAttribute PhaseOperatedVoltageRegulator::attributes[] =
{
    PointAttribute::VoltageX,
    PointAttribute::VoltageY,
    PointAttribute::TapDown,
    PointAttribute::TapUp,
    PointAttribute::TapPosition,
    PointAttribute::AutoRemoteControl,
    PointAttribute::KeepAlive,
    PointAttribute::Terminate,
    PointAttribute::AutoBlockEnable
};


PhaseOperatedVoltageRegulator::PhaseOperatedVoltageRegulator()
    : VoltageRegulator(),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false)
{
    // empty...
}


PhaseOperatedVoltageRegulator::PhaseOperatedVoltageRegulator(Cti::RowReader & rdr)
    : VoltageRegulator(rdr),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false)
{
    // empty...
}


PhaseOperatedVoltageRegulator::PhaseOperatedVoltageRegulator(const PhaseOperatedVoltageRegulator & toCopy)
    : VoltageRegulator(),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false)
{
    operator=(toCopy);
}


PhaseOperatedVoltageRegulator & PhaseOperatedVoltageRegulator::operator=(const PhaseOperatedVoltageRegulator & rhs)
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


void PhaseOperatedVoltageRegulator::saveGuts(RWvostream & ostrm) const
{
    VoltageRegulator::saveGuts(ostrm);

    ostrm
        << _recentTapOperation
        << _lastOperatingMode
        << _lastCommandedOperatingMode;
}


void PhaseOperatedVoltageRegulator::loadAttributes(AttributeService * service)
{
    for each ( const PointAttribute attribute in attributes )
    {
        loadPointAttributes(service, attribute);
    }
}


void PhaseOperatedVoltageRegulator::updateFlags(const unsigned tapDelay)
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


VoltageRegulator * PhaseOperatedVoltageRegulator::replicate() const
{
    return new PhaseOperatedVoltageRegulator(*this);
}


const VoltageRegulator::Type PhaseOperatedVoltageRegulator::getType() const
{
    return VoltageRegulator::PhaseOperatedVoltageRegulatorType;
}


void PhaseOperatedVoltageRegulator::executeIntegrityScan()
{
    // Scan both Upside and Downside voltage points

    executeIntegrityScanHelper( getPointByAttribute( PointAttribute::VoltageX ) );
    executeIntegrityScanHelper( getPointByAttribute( PointAttribute::VoltageY ) );
}


/*
    If we are already in remote mode here we only need to send a single sequential keep alive message
    If we aren't (we don't know or are in auto mode) we:
        1. send 2 sequential keep alive messages followed by
        2. an auto block enable message
*/
void PhaseOperatedVoltageRegulator::executeEnableKeepAlive()
{
    bool needsAutoBlockEnable = ( getOperatingMode() != RemoteMode );

    // 1. read in 'activate' value
    // 2. increment it [ 0 -- 32767, 0 ... ]

    double    value = -1.0;
    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    if ( getPointValue( point.getPointId(), value ) )
    {
        _keepAliveConfig = ( static_cast<long>(value) + 1 ) % 32768;     // limit range and handle rollover.
    }
    else
    {
        // If we haven't received any point updates yet we assume 0 -- gotta start somewhere...

        _keepAliveConfig = 0;
    }

    executeKeepAliveHelper( getPointByAttribute( PointAttribute::KeepAlive ), _keepAliveConfig );

    if ( needsAutoBlockEnable )
    {
        // get next sequence number

        _keepAliveConfig = ( _keepAliveConfig + 1 ) % 32768;  // limit range and handle rollover.

        // send additional messages

        executeKeepAliveHelper( getPointByAttribute( PointAttribute::KeepAlive ), _keepAliveConfig );

        executeDigitalOutputHelper( getPointByAttribute( PointAttribute::AutoBlockEnable ), "Auto Block Enable", false );
    }
}


void PhaseOperatedVoltageRegulator::executeDisableKeepAlive()
{
    executeDigitalOutputHelper( getPointByAttribute( PointAttribute::Terminate ), "Keep Alive", false );
}


void PhaseOperatedVoltageRegulator::executeEnableRemoteControl()
{
    _lastCommandedOperatingMode = RemoteMode;

    executeRemoteControlHelper( getPointByAttribute( PointAttribute::KeepAlive ), _keepAliveConfig, "Enable Remote Control" );

    executeEnableKeepAlive();
}


void PhaseOperatedVoltageRegulator::executeDisableRemoteControl()
{
    _lastCommandedOperatingMode = LocalMode;

    executeRemoteControlHelper( getPointByAttribute( PointAttribute::KeepAlive ), 0, "Disable Remote Control" );

    executeDisableKeepAlive();
}


}
}

