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

std::unique_ptr<KeepAlivePolicy> resolveKeepAlivePolicy( const std::string & policyType )
{
    static const std::map< std::string,
                           std::function< std::unique_ptr<KeepAlivePolicy>() > > Lookup
    {
        { "NONE",      std::make_unique<NoKeepAlivePolicy> },
        { "INCREMENT", std::make_unique<IncrementingKeepAlivePolicy> },
        { "COUNTDOWN", std::make_unique<CountdownKeepAlivePolicy> }
    };

    if ( auto & result = mapFind( Lookup, policyType ) )
    {
        return (*result)();
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
    _controlPolicy( std::make_unique<StandardControlPolicy>() ),
    _keepAlivePolicy( std::make_unique<CountdownKeepAlivePolicy>() ),
    _scanPolicy( std::make_unique<LoadOnlyScanPolicy>() )
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
    _controlPolicy( std::make_unique<StandardControlPolicy>() ),
    _keepAlivePolicy( std::make_unique<CountdownKeepAlivePolicy>() ),
    _scanPolicy( std::make_unique<LoadOnlyScanPolicy>() )
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
    _keepAliveValue( toCopy._keepAliveValue )
{
    // empty...
}


void VoltageRegulator::handlePointData(CtiPointDataMsg * message)
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


LitePoint VoltageRegulator::getPointByAttribute(const PointAttribute & attribute)
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

    throw MissingPointAttribute( getPaoId(), attribute, getPaoType(), isTimeForMissingAttributeComplain()  );
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

    _keepAlivePolicy = resolveKeepAlivePolicy( getHeartbeatMode() );
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
    throw MissingPointAttribute( getPaoId(),
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


double VoltageRegulator::requestVoltageChange( const double changeAmount,
                                               const bool   isEmergency )
{
    const double voltageChangePerTap = getVoltageChangePerTap();

    if ( getControlMode() == ManualTap ) 
    {
        double tapCount = isEmergency
                            ? std::ceil( std::abs( changeAmount ) / voltageChangePerTap )
                            : 1 ;

        return tapCount * ( changeAmount > 0 )
                    ?  voltageChangePerTap
                    : -voltageChangePerTap;
    }
    else    // set point calculations...
    {
        const double currentVoltage       = getVoltage();
        const double currentSetPoint      = _controlPolicy->getSetPointValue();
        const double currentHalfBandwidth = _controlPolicy->getSetPointBandwidth() / 2.0;

        const bool withinBandwidth =
            ( ( ( currentSetPoint - currentHalfBandwidth ) < currentVoltage ) &&
            ( currentVoltage < ( currentSetPoint + currentHalfBandwidth ) ) );

        const double voltageWindow = currentVoltage - currentSetPoint;

        if ( withinBandwidth )
        {
            if ( changeAmount > 0 )
            {
                const int Ntaps = std::abs( 1 +
                    ( ( currentHalfBandwidth + voltageWindow ) / voltageChangePerTap ) );

                const int caTaps = changeAmount / voltageChangePerTap;  // taps for 'all' requested change

                return ( Ntaps <= caTaps ? Ntaps : 1 ) * voltageChangePerTap;
            }
            else
            {
                const int Ntaps = std::abs( 1 +
                    ( ( currentHalfBandwidth + voltageWindow ) / voltageChangePerTap ) );

                return -Ntaps * voltageChangePerTap;
            }
        }
    }

    return 0.0;
}


double VoltageRegulator::getVoltage()
try
{
    return _scanPolicy->getValueByAttribute( PointAttribute::VoltageY );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingPointAttribute( getPaoId(),
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


void VoltageRegulator::canExecuteVoltageRequest( const double changeAmount ) //const
try
{
    if ( changeAmount != 0.0 )
    {
        if ( getControlMode() == ManualTap ) 
        {
            _controlPolicy->getPointByAttribute(
                                changeAmount > 0.0
                                    ? PointAttribute::TapUp
                                    : PointAttribute::TapDown );
        }
        else
        {
            _controlPolicy->getPointByAttribute( PointAttribute::ForwardSetPoint ); 
            _controlPolicy->getPointByAttribute( PointAttribute::ForwardBandwidth ); 
        }
    }
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingPointAttribute( getPaoId(),
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
    throw MissingPointAttribute( getPaoId(),
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
    throw MissingPointAttribute( getPaoId(),
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
    throw MissingPointAttribute( getPaoId(),
                                 missingAttribute.attribute(),
                                 getPaoType(),
                                 isTimeForMissingAttributeComplain()  );
}
catch ( UninitializedPointValue & noValue )
{
    throw NoPointAttributeValue( getPaoId(),
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

    CtiCapController::getInstance()->sendMessageToDispatch( signal.release() );

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

    CtiCapController::getInstance()->manualCapBankControl( request.release() );
}


//////////////  Scan Policy Commands 


void VoltageRegulator::executeIntegrityScan( const std::string & user )
try
{
    for ( auto & action : _scanPolicy->IntegrityScan() )
    {
        auto & signal  = action.first;
        auto & request = action.second;

        signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

        CtiCapController::getInstance()->sendMessageToDispatch( signal.release() );

        const long pointPaoID = request->DeviceId();

        CtiCapController::getInstance()->manualCapBankControl( request.release() );

        sendCapControlOperationMessage( 
            Messaging::CapControl::CapControlOperationMessage::createScanDeviceMessage(
                pointPaoID, CtiTime() ) );

        if ( user != SystemUser )    // only log user issued commands
        {
            enqueueRegulatorEvent(
                RegulatorEvent::makeScanEvent( RegulatorEvent::IntegrityScan,
                                               getPaoId(),
                                               _phase,
                                               user ) );
        }
    }
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingPointAttribute( getPaoId(),
                                 missingAttribute.attribute(),
                                 getPaoType(),
                                 isTimeForMissingAttributeComplain()  );
}


//////////////  KeepAlive Policy Commands 


void VoltageRegulator::executeEnableRemoteControl( const std::string & user )
{
    try
    {
        submitRemoteControlCommands( _keepAlivePolicy->EnableRemoteControl( _keepAliveValue ),
                                     RegulatorEvent::EnableRemoteControl,
                                     user );

        _lastCommandedOperatingMode = RemoteMode;
    }
    catch ( FailedAttributeLookup & missingAttribute )
    {
        throw MissingPointAttribute( getPaoId(),
                                     missingAttribute.attribute(),
                                     getPaoType(),
                                     isTimeForMissingAttributeComplain()  );
    }

    executeEnableKeepAlive( user );
}


void VoltageRegulator::executeDisableRemoteControl( const std::string & user )
{
    try
    {
        submitRemoteControlCommands( _keepAlivePolicy->DisableRemoteControl(),
                                     RegulatorEvent::DisableRemoteControl,
                                     user );

        _lastCommandedOperatingMode = LocalMode;
    }
    catch ( FailedAttributeLookup & missingAttribute )
    {
        throw MissingPointAttribute( getPaoId(),
                                     missingAttribute.attribute(),
                                     getPaoType(),
                                     isTimeForMissingAttributeComplain()  );
    }

    executeDisableKeepAlive( user );
}


void VoltageRegulator::submitRemoteControlCommands( Policy::Actions                   & actions,
                                                    const RegulatorEvent::EventTypes    eventType,
                                                    const std::string                 & user )
{
    for ( auto & action : actions )
    {

        auto & signal = action.first;

        signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

        CtiCapController::getInstance()->sendMessageToDispatch( signal.release() );

        enqueueRegulatorEvent( RegulatorEvent::makeRemoteControlEvent( eventType,
                                                                       getPaoId(),
                                                                       _phase,
                                                                       user ) );
    }
}


long VoltageRegulator::executeEnableKeepAlive( const std::string & user )
try
{
    return submitKeepAliveCommands( _keepAlivePolicy->SendKeepAlive( _keepAliveValue ) );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingPointAttribute( getPaoId(),
                                 missingAttribute.attribute(),
                                 getPaoType(),
                                 isTimeForMissingAttributeComplain()  );
}


void VoltageRegulator::executeDisableKeepAlive( const std::string & user )
try
{
    submitKeepAliveCommands( _keepAlivePolicy->StopKeepAlive() );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingPointAttribute( getPaoId(),
                                 missingAttribute.attribute(),
                                 getPaoType(),
                                 isTimeForMissingAttributeComplain()  );
}


long VoltageRegulator::submitKeepAliveCommands( Policy::Actions & actions )
{
    long delay = 0;

    for ( auto & action : actions )
    {
        auto & signal  = action.first;
        auto & request = action.second;

        delay = std::max( delay, static_cast<long>( signal->getPointValue() ) );     // gross - smuggle the message delay out in the point value

        signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

        CtiCapController::getInstance()->sendMessageToDispatch( signal.release() );

        CtiCapController::getInstance()->manualCapBankControl( request.release() );
    }

    return delay;
}


bool VoltageRegulator::executePeriodicKeepAlive( const std::string & user )
{
    if ( _keepAlivePeriod && ( CtiTime::now() >= _nextKeepAliveSendTime ) )
    {
        const long delay = executeEnableKeepAlive( user );

        _nextKeepAliveSendTime += ( delay ) ? delay : _keepAlivePeriod;

        return true;
    }

    return false;
}


}
}

