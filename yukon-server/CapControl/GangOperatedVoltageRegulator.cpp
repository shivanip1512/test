

#include "precompiled.h"
#include "logger.h"
#include "GangOperatedVoltageRegulator.h"
#include "ccutil.h"
#include "capcontroller.h"
#include "ccmessage.h"

namespace Cti           {
namespace CapControl    {

DEFINE_COLLECTABLE( GangOperatedVoltageRegulator, CTIVOLTAGEREGULATOR_ID )


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


void GangOperatedVoltageRegulator::loadAttributes(AttributeService * service)
{
    const std::vector<PointAttribute> attributes
    {
        PointAttribute::VoltageY,
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
    _keepAliveConfig = getKeepAliveConfig();
    _keepAliveTimer  = getKeepAliveTimer();

    executeKeepAliveHelper( getPointByAttribute( PointAttribute::KeepAlive ), _keepAliveConfig );

    if ( isTimeToSendKeepAlive() )      // update the keep alive timer
    {
        _nextKeepAliveSendTime = ( CtiTime::now() + _keepAliveTimer );
    }
}


void GangOperatedVoltageRegulator::executeDisableKeepAlive()
{
    _keepAliveTimer  = getKeepAliveTimer();

    executeKeepAliveHelper( getPointByAttribute( PointAttribute::KeepAlive ), 0);

    if ( isTimeToSendKeepAlive() )      // update the keep alive timer
    {
        _nextKeepAliveSendTime = ( CtiTime::now() + _keepAliveTimer );
    }
}


void GangOperatedVoltageRegulator::executeEnableRemoteControl()
{
    _keepAliveConfig = getKeepAliveConfig();

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

