#include "precompiled.h"

#include "VoltageRegulator.h"
#include "ccutil.h"
#include "logger.h"
#include "msg_signal.h"
#include "msg_pcrequest.h"
#include "Capcontroller.h"
#include "resolvers.h"
#include "mgr_config.h"
#include "config_data_regulator.h"

#include "StandardControlPolicy.h"
#include "NoKeepAlivePolicy.h"
#include "CountdownKeepAlivePolicy.h"
#include "IncrementingKeepAlivePolicy.h"
#include "LoadOnlyScanPolicy.h"
#include "StandardScanPolicy.h"

#include "RegulatorEvents.h"
#include "std_helper.h"

#include <boost/range/adaptor/map.hpp>

using namespace boost::posix_time;
using namespace Cti::Messaging::CapControl;

namespace Cti           {
namespace CapControl    {

namespace   {

Config::DeviceConfigSPtr    getDeviceConfig( const CapControlPao * regulator )
{
    Config::DeviceConfigSPtr    deviceConfig = 
        ConfigManager::getConfigForIdAndType( regulator->getPaoId(),
                                              static_cast<DeviceTypes>( resolvePAOType( regulator->getPaoCategory(),
                                                                                        regulator->getPaoType() ) ) );

    if ( ! deviceConfig )
    {
        // This technically should be impossible - no regulator should exist without an
        //  assigned config - should we bother to throw an exception if lookup fails or fail
        //  silently with acceptable defaults...?
    }

    return deviceConfig;
}

std::unique_ptr<KeepAlivePolicy> resolveKeepAlivePolicy( const std::string & policyType, VoltageRegulator::ControlMode controlMode )
{

    if ( policyType == "COUNTDOWN" )
    {
        return std::make_unique<CountdownKeepAlivePolicy>();
    }
    else if (policyType == "INCREMENT" )
    {
        if ( controlMode == VoltageRegulator::ControlMode::SetPoint )
        {
            return std::make_unique<IncrementingKeepAlivePolicy>( IncrementingKeepAlivePolicy::AutoBlock::Suppress );
        }
        return std::make_unique<IncrementingKeepAlivePolicy>( IncrementingKeepAlivePolicy::AutoBlock::Send );
    }

    return std::make_unique<NoKeepAlivePolicy>();
}

std::unique_ptr<ScanPolicy> resolveScanPolicy( const std::string & paoType )
{

    static const std::map< std::string,
                           std::function< std::unique_ptr<ScanPolicy>() > > Lookup
    {
        { VoltageRegulator::LoadTapChanger,                std::make_unique<LoadOnlyScanPolicy> },
        { VoltageRegulator::GangOperatedVoltageRegulator,  std::make_unique<StandardScanPolicy> },
        { VoltageRegulator::PhaseOperatedVoltageRegulator, std::make_unique<StandardScanPolicy> }
    };

    if ( auto & result = mapFind( Lookup, paoType ) )
    {
        return (*result)();
    }

    return std::make_unique<LoadOnlyScanPolicy>();
}

std::string resolveInstallOrientation( const VoltageRegulator::InstallOrientation & orientation )
{
    return orientation == VoltageRegulator::InstallOrientation::Forward
            ? "Forward"
            : "Reverse";
}

std::string resolveVoltageControlMode( const VoltageRegulator::ControlMode & orientation )
{
    return orientation == VoltageRegulator::ControlMode::SetPoint
            ? "SetPoint"
            : "ManualTap";
}

}

// If these strings change, remember to update them in resolveCapControlType()
const std::string VoltageRegulator::LoadTapChanger                  = "LTC";
const std::string VoltageRegulator::GangOperatedVoltageRegulator    = "GO_REGULATOR";
const std::string VoltageRegulator::PhaseOperatedVoltageRegulator   = "PO_REGULATOR";

DEFINE_COLLECTABLE( VoltageRegulator, CTIVOLTAGEREGULATOR_ID )


VoltageRegulator::VoltageRegulator()
    : CapControlPao(),
    _phase(Phase_Unknown),
    _updated(true),
    _lastControlOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _nextKeepAliveSendTime(CtiTime(neg_infin)),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false),
    _keepAlivePeriod( 0 ),
    _keepAliveValue( 0 ),
    _installOrientation( InstallOrientation::Forward ),
    _controlPolicy( std::make_unique<StandardControlPolicy>() ),
    _keepAlivePolicy( std::make_unique<CountdownKeepAlivePolicy>() ),
    _scanPolicy( std::make_unique<LoadOnlyScanPolicy>() ),
    _regulatorTimeout(0)
{
    // empty...
}


VoltageRegulator::VoltageRegulator(Cti::RowReader & rdr)
    : CapControlPao(rdr),
    _phase(Phase_Unknown),
    _updated(true),
    _lastControlOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _nextKeepAliveSendTime(CtiTime(neg_infin)),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false),
    _keepAlivePeriod( 0 ),
    _keepAliveValue( 0 ),
    _installOrientation( InstallOrientation::Forward ),
    _controlPolicy( std::make_unique<StandardControlPolicy>() ),
    _keepAlivePolicy( std::make_unique<CountdownKeepAlivePolicy>() ),
    _scanPolicy( std::make_unique<LoadOnlyScanPolicy>() ),
    _regulatorTimeout(0)
{
    // empty...
}


VoltageRegulator::VoltageRegulator(const VoltageRegulator & toCopy)
    : CapControlPao(toCopy),
    _phase(toCopy._phase),
    _updated(toCopy._updated),
    _lastControlOperation(toCopy._lastControlOperation),
    _lastMissingAttributeComplainTime(toCopy._lastMissingAttributeComplainTime),
    _nextKeepAliveSendTime(toCopy._nextKeepAliveSendTime),
    _lastOperatingMode(toCopy._lastOperatingMode),
    _lastCommandedOperatingMode(toCopy._lastCommandedOperatingMode),
    _recentTapOperation(toCopy._recentTapOperation),
    _keepAlivePeriod( toCopy._keepAlivePeriod ),
    _keepAliveValue( toCopy._keepAliveValue ),
    _installOrientation( toCopy._installOrientation ),
    _regulatorTimeout(0)

{
    // empty...
}


void VoltageRegulator::handlePointData( const CtiPointDataMsg & message )
{
    setUpdated(true);

    _controlPolicy->updatePointData( message );
    _keepAlivePolicy->updatePointData( message );
    _scanPolicy->updatePointData( message );
}


VoltageRegulator::IDSet VoltageRegulator::getRegistrationPoints()
{
    IDSet IDs;

    if ( getDisabledStatePointId() > 0 )
    {
        IDs.insert( getDisabledStatePointId() );
    }

    for ( const auto ID : _controlPolicy->getRegistrationPointIDs() )
    {
        IDs.insert( ID );
    }

    for ( const auto ID : _keepAlivePolicy->getRegistrationPointIDs() )
    {
        IDs.insert( ID );
    }

    for ( const auto ID : _scanPolicy->getRegistrationPointIDs() )
    {
        IDs.insert( ID );
    }

    return IDs;
}


LitePoint VoltageRegulator::getPointByAttribute(const Attribute & attribute)
{
    try
    {
        return _controlPolicy->getPointByAttribute( attribute );
    } 
    catch ( FailedAttributeLookup & )
    {
        // ... continue on
    }

    try
    {
        return _keepAlivePolicy->getPointByAttribute( attribute );
    } 
    catch ( FailedAttributeLookup & )
    {
        // ... continue on
    }

    try
    {
        return _scanPolicy->getPointByAttribute( attribute );
    } 
    catch ( FailedAttributeLookup & )
    {
        // ... continue on
    }

    // didn't find anywhere...

    throw MissingAttribute( getPaoId(), attribute, getPaoType(), isTimeForMissingAttributeComplain() );
}


bool VoltageRegulator::isUpdated() const
{
    return _updated;
}


void VoltageRegulator::setUpdated(const bool updated)
{
    _updated = updated;
}


void VoltageRegulator::notifyControlOperation(const ControlOperation & operation, const CtiTime & timeStamp)
{
    _lastControlOperation     = operation;
    _lastControlOperationTime = timeStamp;
}


void VoltageRegulator::loadAttributes( AttributeService * service )
{
    // restore stuff from device configuration

    _keepAlivePeriod = getKeepAliveTimer();
    _keepAliveValue  = getKeepAliveConfig();

    _installOrientation = getInstallOrientation();

    _keepAlivePolicy = resolveKeepAlivePolicy( getHeartbeatMode(), getControlMode() );
    _scanPolicy = resolveScanPolicy( getPaoType() );

    _controlPolicy->loadAttributes( *service, getPaoId() );
    _keepAlivePolicy->loadAttributes( *service, getPaoId() );
    _scanPolicy->loadAttributes( *service, getPaoId() );
}


CtiTime VoltageRegulator::updateMissingAttributeComplainTime()
{
    _lastMissingAttributeComplainTime = CtiTime();
    return _lastMissingAttributeComplainTime;
}


bool VoltageRegulator::isTimeForMissingAttributeComplain(CtiTime time)
{
    if ( _lastMissingAttributeComplainTime + 300 < time )
    {
        updateMissingAttributeComplainTime();
        return true;
    }
    return false;
}


VoltageRegulator::Type VoltageRegulator::getType() const
{
    if ( getPaoType() == VoltageRegulator::LoadTapChanger )
    {
        return VoltageRegulator::LoadTapChangerType;
    }
    if ( getPaoType() == VoltageRegulator::GangOperatedVoltageRegulator )
    {
        return VoltageRegulator::GangOperatedVoltageRegulatorType;
    }

    return VoltageRegulator::PhaseOperatedVoltageRegulatorType;
}


void VoltageRegulator::updateFlags(const unsigned tapDelay)
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


/*
    Return the operating mode based on the auto/remote point
*/
VoltageRegulator::OperatingMode VoltageRegulator::getOperatingMode()
try
{
    switch ( _keepAlivePolicy->getOperatingMode() )
    {
        case KeepAlivePolicy::LocalMode:
        {
            return LocalMode;
        }
        case KeepAlivePolicy::RemoteMode:
        {
            return RemoteMode;
        }
    }

    return UnknownMode;
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}
catch ( UninitializedPointValue & )
{
    return UnknownMode;
}


void VoltageRegulator::setPhase( const Phase phase )
{
    _phase = phase;
}


Phase VoltageRegulator::getPhase() const
{
    return _phase;
}


std::string VoltageRegulator::getPhaseString() const
{
    switch ( _phase )
    {
        case Phase_A:
        case Phase_B:
        case Phase_C:
        {
            return ( " - Phase: " + desolvePhase( _phase ) );
        }
    }

    return "";
}


long VoltageRegulator::getKeepAliveConfig()
{
    Config::DeviceConfigSPtr    deviceConfig = getDeviceConfig( this );

    if ( deviceConfig )
    {
        if ( boost::optional<double>    keepAliveConfig =
             deviceConfig->findValue<double>( Cti::Config::RegulatorStrings::heartbeatValue ) )
        {
            return *keepAliveConfig;
        }
    }

    return 0;
}


long VoltageRegulator::getKeepAliveTimer()
{
    Config::DeviceConfigSPtr    deviceConfig = getDeviceConfig( this );

    if ( deviceConfig )
    {
        if ( boost::optional<double>    keepAliveTimer =
             deviceConfig->findValue<double>( Cti::Config::RegulatorStrings::heartbeatPeriod ) )
        {
            return *keepAliveTimer;
        }
    }

    return 0;
}


double VoltageRegulator::getVoltageChangePerTap() const
{
    Config::DeviceConfigSPtr    deviceConfig = getDeviceConfig( this );

    if ( deviceConfig )
    {
        if ( boost::optional<double>    voltageChange =
             deviceConfig->findValue<double>( Cti::Config::RegulatorStrings::voltageChangePerTap ) )
        {
            return *voltageChange;
        }
    }

    return 0.75;
}


VoltageRegulator::ControlMode VoltageRegulator::getControlMode() const
{
    Config::DeviceConfigSPtr    deviceConfig = getDeviceConfig( this );

    if ( deviceConfig )
    {
        if ( boost::optional<std::string>   mode =
             deviceConfig->findValue<std::string>( Config::RegulatorStrings::voltageControlMode ) )
        {
            if ( *mode == "SET_POINT" )
            {
                return SetPoint;
            }
        }
    }

    return ManualTap;
}


std::string VoltageRegulator::getHeartbeatMode() const
{
    Config::DeviceConfigSPtr    deviceConfig = getDeviceConfig( this );

    if ( deviceConfig )
    {
        if ( boost::optional<std::string>   mode =
             deviceConfig->findValue<std::string>( Config::RegulatorStrings::heartbeatMode ) )
        {
            return *mode;
        }
    }

    return "NONE";
}


VoltageRegulator::InstallOrientation VoltageRegulator::getInstallOrientation() const
{
    Config::DeviceConfigSPtr    deviceConfig = getDeviceConfig( this );

    if ( deviceConfig )
    {
        if ( boost::optional<std::string>   orientation =
             deviceConfig->findValue<std::string>( Config::RegulatorStrings::installOrientation ) )
        {
            if ( *orientation == "REVERSE" )
            {
                return InstallOrientation::Reverse;
            }
        }
    }

    return InstallOrientation::Forward;
}


double VoltageRegulator::requestVoltageChange( const double changeAmount,
                                               const VoltageAdjuster adjuster )
{
    const double voltageChangePerTap = getVoltageChangePerTap();

    if ( getControlMode() == ManualTap ) 
    {
        double tapCount = 1;    // adjuster == Single

        if ( adjuster == Inclusive )    // tapCount that would give a result >= changeAmount
        {
            tapCount = std::ceil( std::abs( changeAmount ) / voltageChangePerTap );
        }
        else if ( adjuster == Exclusive )   // tapCount nearest to but < changeAmount
        {
            tapCount = std::floor( std::abs( changeAmount ) / voltageChangePerTap );
        }

        return tapCount * ( ( changeAmount > 0 )
                    ?  voltageChangePerTap
                    : -voltageChangePerTap );
    }
    else    // set point calculations...
    {
        const double currentVoltage       = getVoltage();
        const double currentSetPoint      = _controlPolicy->getSetPointValue();
        const double currentHalfBandwidth = _controlPolicy->getSetPointBandwidth() / 2.0;

        const double voltageWindow = currentVoltage - currentSetPoint;

        if ( adjuster == Single )   // old way for normal IVVC ops
        {
            const bool withinBandwidth =
                ( ( ( currentSetPoint - currentHalfBandwidth ) < currentVoltage ) &&
                ( currentVoltage < ( currentSetPoint + currentHalfBandwidth ) ) );

            if ( withinBandwidth )
            {
                if ( changeAmount > 0 )
                {
                    const int Ntaps = std::abs( 1 +
                        ( ( currentHalfBandwidth + voltageWindow ) / voltageChangePerTap ) );

                    return Ntaps * voltageChangePerTap;
                }
                else
                {
                    const int Ntaps = std::abs( 1 +
                        ( ( currentHalfBandwidth - voltageWindow ) / voltageChangePerTap ) );

                    return -Ntaps * voltageChangePerTap;
                }
            }
        }
        else if ( adjuster == Inclusive )
        {
            const double adjustment =  std::abs( changeAmount ) + currentHalfBandwidth - voltageChangePerTap;

            return ( changeAmount > 0 )
                        ? adjustment
                        : -adjustment;
        }
        else    // adjuster == Exclusive
        {
            const double adjustedChange
                = voltageChangePerTap * std::floor( std::abs( changeAmount ) / voltageChangePerTap );

            const double adjustedHalfBW
                = voltageChangePerTap * std::floor( std::abs( currentHalfBandwidth ) / voltageChangePerTap );

            const double totalAdjustment
                = adjustedChange + adjustedHalfBW
                    + ( ( changeAmount > 0 )
                            ? voltageWindow
                            : -voltageWindow );

            return ( changeAmount > 0 )
                        ? totalAdjustment
                        : -totalAdjustment;
        }
    }

    return 0.0;
}


double VoltageRegulator::getVoltage()
try
{
    return _scanPolicy->getValueByAttribute( Attribute::Voltage );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}
catch ( UninitializedPointValue & )
{
    return 0.0;
}


boost::optional<long> VoltageRegulator::getTapPosition()
try
{
    return _controlPolicy->getTapPosition();
}
catch ( UninitializedPointValue & )
{
    return boost::none;
}


PointValue VoltageRegulator::getCompleteTapPosition()
{
    return _controlPolicy->getCompleteTapPosition();
}


long VoltageRegulator::getMinTapPosition() const
{
    if ( auto deviceConfig = getDeviceConfig( this ) )
    {
        if ( auto position = deviceConfig->findValue<long>( Cti::Config::RegulatorStrings::minTapPosition ) )
        {
            return *position;
        }
    }

    return static_cast<long>( TapPositionLimits::Minimum );
}


long VoltageRegulator::getMaxTapPosition() const
{
    if ( auto deviceConfig = getDeviceConfig( this ) )
    {
        if ( auto position = deviceConfig->findValue<long>( Cti::Config::RegulatorStrings::maxTapPosition ) )
        {
            return *position;
        }
    }

    return static_cast<long>( TapPositionLimits::Maximum );
}


//  If we have indeterminate power flow then we don;t want to move the tap position, likewise if we are
//  at the limits of the tap range, we don;t want to try to move the tap further out of its limits.
VoltageRegulator::TapInhibit VoltageRegulator::isTapInhibited()
{
    if ( determinePowerFlowSituation() == PowerFlowSituations::IndeterminateFlow )
    {
        return TapInhibit::NoTap;
    }

    if ( const auto currentTapPosition = getTapPosition() )
    {
        if ( *currentTapPosition <= getMinTapPosition() )
        {
            return TapInhibit::NoTapDown;
        }

        if ( *currentTapPosition >= getMaxTapPosition() )
        {
            return TapInhibit::NoTapUp;
        }
    }
    else
    {
        return TapInhibit::NoTap;
    }

    return TapInhibit::None;
}


void VoltageRegulator::canExecuteVoltageRequest( const double changeAmount ) //const
try
{
    if ( changeAmount != 0.0 )
    {
        if ( getControlMode() == ManualTap ) 
        {
            _controlPolicy->getPointByAttribute(
                                changeAmount > 0.0
                                    ? Attribute::TapUp
                                    : Attribute::TapDown );
        }
        else
        {
            _controlPolicy->getPointByAttribute( _controlPolicy->getSetPointAttribute() ); 
            _controlPolicy->getPointByAttribute( _controlPolicy->getBandwidthAttribute() ); 
        }
    }
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}


double VoltageRegulator::adjustVoltage( const double changeAmount )
{
    if ( changeAmount == 0.0 )
    {
        return 0.0;
    }

    const double voltageChangePerTap = getVoltageChangePerTap();

    if ( getControlMode() == ManualTap ) 
    {
        if ( changeAmount > 0.0 )
        {
            executeTapUpOperation( SystemUser );

            return voltageChangePerTap;
        }
        else
        {
            executeTapDownOperation( SystemUser );

            return -voltageChangePerTap;
        }
    }
    else    // Set Point
    {
        executeAdjustSetPointOperation( changeAmount, SystemUser );
    }

    return changeAmount;
}


//////////////  Control Policy Commands 


void VoltageRegulator::executeTapUpOperation( const std::string & user )
try
{
    submitControlCommands( _controlPolicy->TapUp(),
                           RaiseTap,
                           "Raise Tap Position",
                           RegulatorEvent::TapUp,
                           getVoltageChangePerTap(),
                           user );

    sendCapControlOperationMessage(
        CapControlOperationMessage::createRaiseTapMessage( getPaoId(), CtiTime() ) );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain() );
}


void VoltageRegulator::executeTapDownOperation( const std::string & user )
try
{
    submitControlCommands( _controlPolicy->TapDown(),
                           LowerTap,
                           "Lower Tap Position",
                           RegulatorEvent::TapDown,
                           -getVoltageChangePerTap(),
                           user );

    sendCapControlOperationMessage(
        CapControlOperationMessage::createLowerTapMessage( getPaoId(), CtiTime() ) );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain() );
}


void VoltageRegulator::executeAdjustSetPointOperation( const double changeAmount, const std::string & user )
try
{
    if ( changeAmount > 0.0 ) 
    {
        submitControlCommands( _controlPolicy->AdjustSetPoint( changeAmount ),
                               RaiseSetPoint,
                               "Raise Set Point",
                               RegulatorEvent::IncreaseSetPoint,
                               changeAmount,
                               user );
    }
    else
    {
        submitControlCommands( _controlPolicy->AdjustSetPoint( changeAmount ),
                               LowerSetPoint,
                               "Lower Set Point",
                               RegulatorEvent::DecreaseSetPoint,
                               changeAmount,
                               user );
    }
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}
catch ( UninitializedPointValue & noValue )
{
    throw NoAttributeValue( getPaoId(),
                            noValue.attribute(),
                            getPaoType() );
}


void VoltageRegulator::submitControlCommands( Policy::Action                  & action,
                                              const ControlOperation            operation,
                                              const std::string               & opDescription,
                                              const RegulatorEvent::EventTypes  eventType,
                                              const double                      changeAmount,
                                              const std::string               & user )
{
    auto & signal  = action.first;
    auto & request = action.second;

    notifyControlOperation( operation );

    signal->setText( signal->getText() + getPhaseString() );
    signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

    CtiCapController::getInstance()->sendMessageToDispatch( signal.release(), CALLSITE );

    boost::optional<double> newSetPoint;

    if ( eventType == RegulatorEvent::IncreaseSetPoint ||
         eventType == RegulatorEvent::DecreaseSetPoint )
    {

        try
        {
            newSetPoint = _controlPolicy->getSetPointValue() + changeAmount;
        }
        catch ( UninitializedPointValue & )
        {
            // nothing...  inserts a null
        }
    }

    enqueueRegulatorEvent( RegulatorEvent::makeControlEvent( eventType,
                                                             getPaoId(),
                                                             _phase,
                                                             newSetPoint,
                                                             getTapPosition(),
                                                             user ) );

    CtiCapController::getInstance()->manualCapBankControl( std::move( request ) );
}


//////////////  Scan Policy Commands 


void VoltageRegulator::executeIntegrityScan( const std::string & user )
try
{
    bool scanSent = false;

    std::map<long, PorterRequest> requests;

    for ( auto & action : _scanPolicy->IntegrityScan() )
    {
        scanSent = true;

        auto & signal  = action.first;
        auto & request = action.second;

        signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

        CtiCapController::getInstance()->sendMessageToDispatch( signal.release(), CALLSITE );

        //  Only send one scan request per pao ID
        requests[request->DeviceId()] = std::move(request);
    }

    for ( auto & request : requests | boost::adaptors::map_values )
    {
        const long pointPaoID = request->DeviceId();

        CtiCapController::getInstance()->manualCapBankControl( std::move( request ) );

        sendCapControlOperationMessage( 
            Messaging::CapControl::CapControlOperationMessage::createScanDeviceMessage(
                pointPaoID, CtiTime() ) );
    }

    // only log user commanded scans, not system issued commands
    //  and
    // only record a single integrity scan event no matter how many voltage points we are scanning
    //  on the regulator

    if ( scanSent && user != SystemUser )
    {
        enqueueRegulatorEvent(
            RegulatorEvent::makeScanEvent( RegulatorEvent::IntegrityScan,
                                           getPaoId(),
                                           _phase,
                                           user ) );
    }
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}


//////////////  KeepAlive Policy Commands 


void VoltageRegulator::executeEnableRemoteControl( const std::string & user )
try
{
    if ( _lastCommandedOperatingMode != RemoteMode )
    {
        CTILOG_DEBUG( dout, "Enabling Remote mode for regulator: " << getPaoName() );

        _lastCommandedOperatingMode = RemoteMode;

        submitRemoteControlCommands( _keepAlivePolicy->EnableRemoteControl( _keepAliveValue ),
                                     RegulatorEvent::EnableRemoteControl,
                                     user );

        executeEnableKeepAlive( user );
    }
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}


void VoltageRegulator::executeDisableRemoteControl( const std::string & user )
try
{
    if ( _lastCommandedOperatingMode != LocalMode )
    {
        CTILOG_DEBUG( dout, "Disabling Remote mode for regulator: " << getPaoName() );

        _lastCommandedOperatingMode = LocalMode;

        submitRemoteControlCommands(_keepAlivePolicy->DisableRemoteControl(),
                                     RegulatorEvent::DisableRemoteControl,
                                     user );

        executeDisableKeepAlive( user );
    }
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}


void VoltageRegulator::submitRemoteControlCommands( Policy::Actions                   & actions,
                                                    const RegulatorEvent::EventTypes    eventType,
                                                    const std::string                 & user )
{
    for ( auto & action : actions )
    {
        auto & signal = action.first;

        signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

        CtiCapController::getInstance()->sendMessageToDispatch( signal.release(), CALLSITE );

        enqueueRegulatorEvent( RegulatorEvent::makeRemoteControlEvent( eventType,
                                                                       getPaoId(),
                                                                       _phase,
                                                                       user ) );
    }
}


long VoltageRegulator::executeEnableKeepAlive( const std::string & user )
try
{
    _lastCommandedOperatingMode = RemoteMode;

    long delay = submitKeepAliveCommands( _keepAlivePolicy->SendKeepAlive( _keepAliveValue , _regulatorTimeout) );

    CTILOG_DEBUG( dout, "Sending KeepAlive for regulator: " << getPaoName() );

    return delay;
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}


void VoltageRegulator::executeDisableKeepAlive( const std::string & user )
try
{
    _lastCommandedOperatingMode = LocalMode;

    submitKeepAliveCommands( _keepAlivePolicy->StopKeepAlive() );

    CTILOG_DEBUG( dout, "Stopping KeepAlive for regulator: " << getPaoName() );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingAttribute( getPaoId(),
                            missingAttribute.attribute(),
                            getPaoType(),
                            isTimeForMissingAttributeComplain()  );
}


long VoltageRegulator::submitKeepAliveCommands( Policy::Actions & actions )
{
    long delay = -1;

    for ( auto & action : actions )
    {
        auto & signal  = action.first;
        auto & request = action.second;

        delay = std::max( delay, static_cast<long>( signal->getPointValue() ) );     // gross - smuggle the message delay out in the point value

        signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

        CtiCapController::getInstance()->sendMessageToDispatch( signal.release(), CALLSITE );

        CtiCapController::getInstance()->manualCapBankControl( std::move( request ) );
    }

    return delay;
}


bool VoltageRegulator::executePeriodicKeepAlive( const std::string & user )
{
    if ( _keepAlivePeriod && ( CtiTime::now() >= _nextKeepAliveSendTime ) )
    {
        const long delay = executeEnableKeepAlive( user );

        if ( delay >= 0 )
        {
            _nextKeepAliveSendTime = CtiTime::now() + ( ( delay ) ? delay : _keepAlivePeriod );

            return true;
        }
        else
        {
            _keepAlivePeriod = 0;
        }
    }

    return false;
}


bool VoltageRegulator::isReverseFlowDetected()
{
    return _controlPolicy->inReverseFlow();
}


ControlPolicy::ControlModes VoltageRegulator::getConfigurationMode()
{
    return _controlPolicy->getControlMode();
}


double VoltageRegulator::getSetPointValue() const
{
    return _controlPolicy->getSetPointValue();
}


Policy::Action VoltageRegulator::setSetPointValue( const double newSetPoint )
{
    return _controlPolicy->setSetPointValue( newSetPoint );
}


std::string VoltageRegulator::detailedDescription()
{
    return
        resolveVoltageControlMode( getControlMode() )
        + " Regulator: "
        + getPaoName()
        + " in "
        + resolveControlMode( getConfigurationMode() )
        + " mode";
}

VoltageRegulator::PowerFlowSituations VoltageRegulator::determinePowerFlowSituation()
{
    struct
    {
        PowerFlowSituations code;
        std::string         reason;
    }
    status { PowerFlowSituations::OK, "OK" };

    const ControlPolicy::ControlModes configMode = getConfigurationMode();
    const InstallOrientation orientation = getInstallOrientation();

    const bool  inReverseFlow = isReverseFlowDetected();
    const bool  controlPowerFlowReverse = _controlPolicy->isControlPowerFlowReverse();

    // check for reverse installation first and unsupported modes

    switch ( configMode )
    {
        case ControlPolicy::LockedForward:
        case ControlPolicy::ReverseIdle:
        case ControlPolicy::NeutralIdle:
        case ControlPolicy::Cogeneration:
        case ControlPolicy::BiasCogeneration:
        {
            if ( orientation == InstallOrientation::Reverse )
            {
                status = { PowerFlowSituations::ReverseInstallation, " is installed in reverse orientation." };
            }
            break;
        }
        case ControlPolicy::LockedReverse:
        case ControlPolicy::ReverseCogeneration:
        {
            if ( orientation == InstallOrientation::Forward )
            {
                status = { PowerFlowSituations::ReverseInstallation, " is installed in forward orientation." };
            }
            break;
        }
        case ControlPolicy::Bidirectional:
        case ControlPolicy::BiasBidirectional:
        {
            // all good...
            break;
        }
        default:
        {
            status = { PowerFlowSituations::UnsupportedMode, ", is unsupported." };
            break;
        }
    }

    if ( status.code == PowerFlowSituations::OK )
    {
        if (_controlPolicy->isPowerFlowIndeterminate())
        {
            status = { PowerFlowSituations::IndeterminateFlow, " has indeterminate power flow." };
        }
        else if ( getControlMode() == VoltageRegulator::ManualTap )
        {
            switch ( configMode )
            {
                case ControlPolicy::LockedForward:
                {
                    if ( inReverseFlow )
                    {
                        status = { PowerFlowSituations::ReverseFlow, " is detecting reverse power flow." };
                    }
                    else if ( controlPowerFlowReverse )
                    {
                        status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting reverse control power flow." };
                    }
                    break;
                }
                case ControlPolicy::ReverseIdle:
                case ControlPolicy::NeutralIdle:
                {
                    if ( inReverseFlow )
                    {
                        status = { PowerFlowSituations::ReverseFlow, " is detecting reverse power flow." };
                    }
                    else if ( ! controlPowerFlowReverse )
                    {
                        status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting forward control power flow." };
                    }
                    break;
                }
                case ControlPolicy::Bidirectional:
                {
                    if ( orientation == InstallOrientation::Reverse )
                    {
                        status = { PowerFlowSituations::ReverseInstallation, " is installed in reverse orientation." };
                    }
                    else if ( inReverseFlow )
                    {
                        status = { PowerFlowSituations::ReverseFlow, " is detecting reverse power flow." };
                    }
                    else if ( controlPowerFlowReverse )
                    {
                        status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting reverse control power flow." };
                    }
                    break;
                }
                default:
                {
                    status = { PowerFlowSituations::UnsupportedMode, ", is unsupported." };
                    break;
                }
            }
        }
        else    // we are configured as SetPoint
        {
            switch ( configMode )
            {
                case ControlPolicy::LockedForward:
                {
                    if ( inReverseFlow )
                    {
                        status = { PowerFlowSituations::ReverseFlow, " is detecting reverse power flow." };
                    }
                    else if ( controlPowerFlowReverse )
                    {
                        status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting reverse control power flow." };
                    }
                    break;
                }
                case ControlPolicy::LockedReverse:
                {
                    if ( ! inReverseFlow )
                    {
                        status = { PowerFlowSituations::ReverseFlow, " is detecting forward power flow." };
                    }
                    else if ( ! controlPowerFlowReverse )
                    {
                        status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting forward control power flow." };
                    }
                    break;
                }
                case ControlPolicy::ReverseIdle:
                case ControlPolicy::NeutralIdle:
                {
                    if ( inReverseFlow )
                    {
                        status = { PowerFlowSituations::ReverseFlow, " is detecting reverse power flow." };
                    }
                    else if ( ! controlPowerFlowReverse )
                    {
                        status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting forward control power flow." };
                    }
                    break;
                }
                case ControlPolicy::Bidirectional:
                case ControlPolicy::BiasBidirectional:
                {
                    if ( orientation == InstallOrientation::Forward )
                    {
                        if ( inReverseFlow )
                        {
                            status = { PowerFlowSituations::ReverseFlow, " is detecting reverse power flow." };
                        }
                        else if ( controlPowerFlowReverse )
                        {
                            status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting reverse control power flow." };
                        }
                    }
                    else
                    {
                        if ( ! inReverseFlow )
                        {
                            status = { PowerFlowSituations::ReverseFlow, " is detecting forward power flow." };
                        }
                        else if ( ! controlPowerFlowReverse )
                        {
                            status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting forward control power flow." };
                        }
                    }
                    break;
                }
                case ControlPolicy::Cogeneration:
                case ControlPolicy::BiasCogeneration:
                {
                    if ( controlPowerFlowReverse )
                    {
                        status = { PowerFlowSituations::ReverseControlPowerFlow, " is detecting reverse control power flow." };
                    }
                    break;
                }
                case ControlPolicy::ReverseCogeneration:
                {
                    if ( inReverseFlow )
                    {
                        if ( ! controlPowerFlowReverse )
                        {
                            status = { PowerFlowSituations::ReverseControlPowerFlow, " has reverse flow, but is detecting forward control power flow." };
                        }
                    }
                    else
                    {
                        if ( controlPowerFlowReverse )
                        {
                            status = { PowerFlowSituations::ReverseControlPowerFlow, " has forward flow, but is detecting reverse control power flow." };
                        }
                    }
                    break;
                }
                default:
                {
                    status = { PowerFlowSituations::UnsupportedMode, ", is unsupported." };
                    break;
                }
            }
        }
    }

    if ( status.code != PowerFlowSituations::OK )
    {
        CTILOG_INFO( dout, detailedDescription() << status.reason );
    }

    return status.code;
}

void VoltageRegulator::setRegulatorTimeout(std::chrono::seconds regulatorTimeout)
{
    _regulatorTimeout = regulatorTimeout;
}
}
}

