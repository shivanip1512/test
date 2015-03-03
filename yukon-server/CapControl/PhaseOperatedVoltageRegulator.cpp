

#include "precompiled.h"
#include "logger.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "ccutil.h"
#include "ccmessage.h"

extern unsigned long _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY;


namespace Cti           {
namespace CapControl    {

DEFINE_COLLECTABLE( PhaseOperatedVoltageRegulator, CTIVOLTAGEREGULATOR_ID )


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


void PhaseOperatedVoltageRegulator::loadAttributes(AttributeService * service)
{
    const std::vector<PointAttribute> attributes
    {
        PointAttribute::VoltageX,
        PointAttribute::VoltageY,
        PointAttribute::AutoRemoteControl,
        PointAttribute::KeepAlive,
        PointAttribute::Terminate,
        PointAttribute::AutoBlockEnable,
        PointAttribute::HeartbeatTimerConfig
    };

    for each ( const PointAttribute attribute in attributes )
    {
        loadPointAttributes(service, attribute);
    }
}


void PhaseOperatedVoltageRegulator::updateFlags(const unsigned tapDelay)
{
    _keepAliveTimer = getKeepAliveRefreshRate();

    bool recentOperation = ( ( _lastControlOperationTime + 30 ) > CtiTime() );

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


/*
    GangOperatedVoltageRegulator is a CooperCL6 attached to all three phases
    PhaseOperatedVoltageRegulatorType is a CooperCL6 attached to only one phase.
        A distinction without a difference as the phase behavior is controlled by the
        zone type that the regulator is attached to.

    The CooperCL6 functionality is contained here in the PhaseOperatedVoltageRegulator class.

    The PaoType() is poorly named - time permitting it will be changed and the relevent
        classes renamed and reduced to only 2 different types from the original 3.

        GangOperatedVoltageRegulator    --> BeckwithLTC
        PhaseOperatedVoltageRegulator   --> CooperCL6
*/
const VoltageRegulator::Type PhaseOperatedVoltageRegulator::getType() const
{
    if ( getPaoType() == VoltageRegulator::GangOperatedVoltageRegulator )
    {
        return VoltageRegulator::GangOperatedVoltageRegulatorType;
    }

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
    OperatingMode   mode = getOperatingMode();

    double    value = -1.0;

    _keepAliveConfig = 0;   // default to 0 if no point updates
    if ( getPointValue( getPointByAttribute( PointAttribute::KeepAlive ).getPointId(), value ) )
    {
        _keepAliveConfig = ( static_cast<long>(value) + 1 ) % 32768;     // limit range and handle rollover.
    }

    executeKeepAliveHelper( getPointByAttribute( PointAttribute::KeepAlive ), _keepAliveConfig );

    long delay = _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY;

    if ( mode == RemoteMode )
    {
        bool needsAutoBlock = false;
        if ( getPointValue( getPointByAttribute( PointAttribute::AutoBlockEnable ).getPointId(), value ) )
        {
            needsAutoBlock = ( value == 0.0 );  // false
        }

        if ( needsAutoBlock )
        {
            executeDigitalOutputHelper( getPointByAttribute( PointAttribute::AutoBlockEnable ), "Auto Block Enable" );
        }
        else
        {
            delay = _keepAliveTimer;
        }
    }

    _nextKeepAliveSendTime = ( CtiTime::now() + delay );
}


void PhaseOperatedVoltageRegulator::executeDisableKeepAlive()
{
    executeDigitalOutputHelper( getPointByAttribute( PointAttribute::Terminate ), "Keep Alive" );
}


void PhaseOperatedVoltageRegulator::executeEnableRemoteControl()
{
    _lastCommandedOperatingMode = RemoteMode;

    executeRemoteControlHelper( getPointByAttribute( PointAttribute::KeepAlive ), _keepAliveConfig, "Enable Remote Control",
                                capControlIvvcRemoteControlEvent );

    executeEnableKeepAlive();
}


void PhaseOperatedVoltageRegulator::executeDisableRemoteControl()
{
    _lastCommandedOperatingMode = LocalMode;

    executeRemoteControlHelper( getPointByAttribute( PointAttribute::KeepAlive ), 0, "Disable Remote Control",
                                capControlIvvcRemoteControlEvent );

    executeDisableKeepAlive();
}


VoltageRegulator::IDSet PhaseOperatedVoltageRegulator::getVoltagePointIDs()
{
    IDSet IDs;

    LitePoint voltageX = getPointByAttribute( PointAttribute::VoltageX );
    LitePoint voltageY = getPointByAttribute( PointAttribute::VoltageY );

    IDs.insert( voltageX.getPointId() );
    IDs.insert( voltageY.getPointId() );

    return IDs;
}


/*
    We get this value from an attached point.  In case of no point update or value out of range (negative) we return 0 which
        disables the automatic keep alive.  The value represents the amount of time (in minutes) it takes for the regulator
        to time out and return to auto mode after receiving a valid keep alive.
        We convert to seconds but divide by three and subtract two because we want send three keep alive messages
        inside the window.
*/
long PhaseOperatedVoltageRegulator::getKeepAliveRefreshRate()
{
    double    value = -1.0;
    LitePoint point = getPointByAttribute( PointAttribute::HeartbeatTimerConfig );

    if ( getPointValue( point.getPointId(), value ) )
    {
        return ( value <= 0.0 ) ? 0 : static_cast<long>( (value * 60.0 / 3.0) - 2.0 );
    }

    return 0;
}


}
}

