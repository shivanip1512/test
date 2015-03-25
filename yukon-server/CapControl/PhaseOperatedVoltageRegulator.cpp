

#include "precompiled.h"
#include "logger.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "ccutil.h"
#include "ccmessage.h"

#include "IncrementingKeepAlivePolicy.h"
#include "StandardScanPolicy.h"

#include "capcontroller.h"

extern unsigned long _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY;


namespace Cti           {
namespace CapControl    {

DEFINE_COLLECTABLE( PhaseOperatedVoltageRegulator, CTIVOLTAGEREGULATOR_ID )


PhaseOperatedVoltageRegulator::PhaseOperatedVoltageRegulator()
    : VoltageRegulator()
{

    _keepAlivePolicy = std::make_unique<IncrementingKeepAlivePolicy>();
    _scanPolicy = std::make_unique<StandardScanPolicy>();

    // empty...
}


PhaseOperatedVoltageRegulator::PhaseOperatedVoltageRegulator(Cti::RowReader & rdr)
    : VoltageRegulator(rdr)
{

    _keepAlivePolicy = std::make_unique<IncrementingKeepAlivePolicy>();
    _scanPolicy = std::make_unique<StandardScanPolicy>();

    // empty...
}


PhaseOperatedVoltageRegulator::PhaseOperatedVoltageRegulator(const PhaseOperatedVoltageRegulator & toCopy)
    : VoltageRegulator()
{
    operator=(toCopy);
}


PhaseOperatedVoltageRegulator & PhaseOperatedVoltageRegulator::operator=(const PhaseOperatedVoltageRegulator & rhs)
{
    if ( this != &rhs )
    {
        VoltageRegulator::operator=(rhs);
    }

    return *this;
}


void PhaseOperatedVoltageRegulator::loadAttributes(AttributeService * service)
{
    const std::vector<PointAttribute> attributes
    {
        PointAttribute::HeartbeatTimerConfig
    };

    for each ( const PointAttribute attribute in attributes )
    {
        loadPointAttributes(service, attribute);
    }
}


void PhaseOperatedVoltageRegulator::updateFlags(const unsigned tapDelay)
{
    _keepAliveTimer = getKeepAliveTimer();

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


/*
    If we are already in remote mode here we only need to send a single sequential keep alive message
    If we aren't (we don't know or are in auto mode) we:
        1. send 2 sequential keep alive messages followed by
        2. an auto block enable message
*/

#if 0

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






#endif


}
}

