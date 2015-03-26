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

#include "RegulatorEvents.h"


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

}

// If these strings change, remember to update them in resolveCapControlType()
const std::string VoltageRegulator::LoadTapChanger                  = "LTC";
const std::string VoltageRegulator::GangOperatedVoltageRegulator    = "GO_REGULATOR";
const std::string VoltageRegulator::PhaseOperatedVoltageRegulator   = "PO_REGULATOR";


VoltageRegulator::VoltageRegulator()
    : CapControlPao(),
    _phase(Phase_Unknown),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastControlOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(neg_infin)),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false),
    _controlPolicy( std::make_unique<StandardControlPolicy>() )
{
    // empty...
}


VoltageRegulator::VoltageRegulator(Cti::RowReader & rdr)
    : CapControlPao(rdr),
    _phase(Phase_Unknown),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastControlOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(neg_infin)),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false),
    _controlPolicy( std::make_unique<StandardControlPolicy>() )
{
    // empty...
}


VoltageRegulator::VoltageRegulator(const VoltageRegulator & toCopy)
    : CapControlPao(),
    _phase(Phase_Unknown),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastControlOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(neg_infin)),
    _lastOperatingMode(UnknownMode),
    _lastCommandedOperatingMode(UnknownMode),
    _recentTapOperation(false),
    _controlPolicy( std::make_unique<StandardControlPolicy>() )
{
    operator=(toCopy);
}


VoltageRegulator & VoltageRegulator::operator=(const VoltageRegulator & rhs)
{
    if ( this != &rhs )
    {
        CapControlPao::operator=(rhs);

        _phase = rhs._phase;

        _updated    = rhs._updated;
        _mode       = rhs._mode;

        _lastControlOperation       = rhs._lastControlOperation;
        _lastControlOperationTime   = rhs._lastControlOperationTime;

        _attributes = rhs._attributes;

        _pointValues = rhs._pointValues;

        _lastMissingAttributeComplainTime = rhs._lastMissingAttributeComplainTime;

        _keepAliveConfig        = rhs._keepAliveConfig;
        _keepAliveTimer         = rhs._keepAliveTimer;
        _nextKeepAliveSendTime  = rhs._nextKeepAliveSendTime;

        _lastOperatingMode  = rhs._lastOperatingMode;
        _lastCommandedOperatingMode = rhs._lastCommandedOperatingMode;
        _recentTapOperation = rhs._recentTapOperation;

    }

    return *this;
}


void VoltageRegulator::handlePointData(CtiPointDataMsg * message)
{
    setUpdated(true);
    _pointValues.updatePointValue(message);

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

    for ( const auto & attribute : _attributes )
    {
        IDs.insert( attribute.second.getPointId() );
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
        // ... continue on to the local map lookup
    }

    try
    {
        return _keepAlivePolicy->getPointByAttribute( attribute );
    } 
    catch ( FailedAttributeLookup & )
    {
        // ... continue on to the local map lookup
    }

    try
    {
        return _scanPolicy->getPointByAttribute( attribute );
    } 
    catch ( FailedAttributeLookup & )
    {
        // ... continue on to the local map lookup
    }

    AttributeMap::const_iterator iter = _attributes.find(attribute);

    if ( iter == _attributes.end() )
    {
        throw MissingPointAttribute( getPaoId(), attribute, getPaoType(), isTimeForMissingAttributeComplain()  );
    }

    return iter->second;
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


bool VoltageRegulator::getPointValue(int pointId, double & value)
{
    return _pointValues.getPointValue( pointId, value );
}


void VoltageRegulator::loadPointAttributes(AttributeService * service, const PointAttribute & attribute)
{
    _controlPolicy->loadAttributes( *service, getPaoId() );
    _keepAlivePolicy->loadAttributes( *service, getPaoId() );
    _scanPolicy->loadAttributes( *service, getPaoId() );

    LitePoint point = service->getPointByPaoAndAttribute( getPaoId(), attribute );

    if (point.getPointType() != InvalidPointType)
    {
        _attributes.insert( std::make_pair( attribute, point ) );
    }
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


void VoltageRegulator::setKeepAliveConfig(const long value)
{
    _keepAliveConfig = value;
}




void VoltageRegulator::executeDigitalOutputHelper( const LitePoint & point,
                                                   const std::string & textDescription,
                                                   const int recordEventType )
{
    CtiCapController::getInstance()->sendMessageToDispatch( createDispatchMessage( point.getPointId(), textDescription ) );

    if ( recordEventType != capControlNoEvent )
    {
        CtiCapController::submitEventLogEntry( EventLogEntry( textDescription, getPaoId(), recordEventType ) );
    }

    std::string commandString = point.getStateOneControl() + " select pointid " + CtiNumStr( point.getPointId() );

    CtiRequestMsg * request = createPorterRequestMsg( point.getPaoId(), commandString );
    request->setSOE(5);

    CtiCapController::getInstance()->manualCapBankControl( request );
}


void VoltageRegulator::executeRemoteControlHelper( const LitePoint & point,
                                                   const int keepAliveValue,
                                                   const std::string & textDescription,
                                                   const int recordEventType )
{
    CtiSignalMsg * signalMsg = createDispatchMessage( point.getPointId(), textDescription );

    signalMsg->setPointValue(keepAliveValue);

    CtiCapController::getInstance()->sendMessageToDispatch( signalMsg );

    if ( recordEventType != capControlNoEvent )
    {
        CtiCapController::submitEventLogEntry( EventLogEntry( textDescription, getPaoId(), recordEventType ) );
    }
}


void VoltageRegulator::executeKeepAliveHelper( const LitePoint & point, const int keepAliveValue )
{
    CtiCapController::getInstance()->sendMessageToDispatch( createDispatchMessage( point.getPointOffset(), "Keep Alive" ) );

    const long pointOffset=
        point.getControlOffset() ?
            point.getControlOffset() :
            point.getPointOffset() % 10000;

    std::string commandString = "putvalue analog " + CtiNumStr( pointOffset ) + " " + CtiNumStr( keepAliveValue );

    CtiRequestMsg *request = createPorterRequestMsg( point.getPaoId(), commandString );
    request->setSOE(5);

    CtiCapController::getInstance()->manualCapBankControl( request );
}


CtiSignalMsg * VoltageRegulator::createDispatchMessage( const long ID, const std::string & text )
{
    return new CtiSignalMsg( ID,
                             0,
                             text,
                             std::string("Voltage Regulator Name: " + getPaoName()),
                             CapControlLogType,
                             SignalEvent,
                             std::string("cap control") );
}


/*
    Return the operating mode based on the auto/remote point
*/
VoltageRegulator::OperatingMode VoltageRegulator::getOperatingMode()
{
    double    value = -1.0;
    LitePoint point = getPointByAttribute( PointAttribute::AutoRemoteControl );

    if ( getPointValue( point.getPointId(), value ) )
    {
        return ( value == 1.0 ) ? RemoteMode : LocalMode;
    }

    return UnknownMode;
}


bool VoltageRegulator::isTimeToSendKeepAlive()
{
    _keepAliveTimer = getKeepAliveTimer();

    return ( ( _keepAliveTimer != 0 ) && ( CtiTime::now() >= _nextKeepAliveSendTime ) );
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
{
    double    value = 0.0;
    LitePoint point = getPointByAttribute( PointAttribute::VoltageY );

    if ( getPointValue( point.getPointId(), value ) )
    {
        return value;
    }

    return 0.0;
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
            executeTapUpOperation();

            return voltageChangePerTap;
        }
        else
        {
            executeTapDownOperation();

            return -voltageChangePerTap;
        }
    }
    else    // Set Point
    {
        executeAdjustSetPointOperation( changeAmount );
    }

    return changeAmount;
}


//////////////  Control Policy Commands 


void VoltageRegulator::executeTapUpOperation()
try
{
    submitControlCommands( _controlPolicy->TapUp(),
                           RaiseTap,
                           "Raise Tap Position",
                           RegulatorEvent::TapUp,
                           getVoltageChangePerTap() );

    sendCapControlOperationMessage(
        CapControlOperationMessage::createRaiseTapMessage( getPaoId(), CtiTime() ) );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingPointAttribute( getPaoId(),
                                 missingAttribute.attribute(),
                                 getPaoType(),
                                 isTimeForMissingAttributeComplain()  );
}


void VoltageRegulator::executeTapDownOperation()
try
{
    submitControlCommands( _controlPolicy->TapDown(),
                           LowerTap,
                           "Lower Tap Position",
                           RegulatorEvent::TapDown,
                           -getVoltageChangePerTap() );

    sendCapControlOperationMessage(
        CapControlOperationMessage::createLowerTapMessage( getPaoId(), CtiTime() ) );
}
catch ( FailedAttributeLookup & missingAttribute )
{
    throw MissingPointAttribute( getPaoId(),
                                 missingAttribute.attribute(),
                                 getPaoType(),
                                 isTimeForMissingAttributeComplain()  );
}


void VoltageRegulator::executeAdjustSetPointOperation( const double changeAmount )
try
{
    if ( changeAmount > 0.0 ) 
    {
        submitControlCommands( _controlPolicy->AdjustSetPoint( changeAmount ),
                               RaiseSetPoint,
                               "Raise Set Point",
                               RegulatorEvent::IncreaseSetPoint,
                               changeAmount );
    }
    else
    {
        submitControlCommands( _controlPolicy->AdjustSetPoint( changeAmount ),
                               LowerSetPoint,
                               "Lower Set Point",
                               RegulatorEvent::DecreaseSetPoint,
                               changeAmount );
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
                                              const double                      changeAmount )
{
    auto & signal  = action.first;
    auto & request = action.second;

    notifyControlOperation( operation );

    std::string fullDescription = opDescription + getPhaseString();

    signal->setText( fullDescription );
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

    boost::optional<long> tapPosition;

    try
    {
        tapPosition = _controlPolicy->getTapPosition();
    }
    catch ( UninitializedPointValue & )
    {
        // nothing...  inserts a null
    }

    enqueueRegulatorEvent( RegulatorEvent::makeControlEvent( eventType, getPaoId(), _phase, newSetPoint, tapPosition ) );

    CtiCapController::getInstance()->manualCapBankControl( request.release() );
}


//////////////  Scan Policy Commands 


void VoltageRegulator::executeIntegrityScan()
try
{
    for ( auto & action : _scanPolicy->IntegrityScan() )
    {
        auto & signal  = action.first;
        auto & request = action.second;

        signal->setText( "Integrity Scan" );
        signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

        CtiCapController::getInstance()->sendMessageToDispatch( signal.release() );

        const long pointPaoID = request->DeviceId();

        CtiCapController::getInstance()->manualCapBankControl( request.release() );

        sendCapControlOperationMessage( 
            Messaging::CapControl::CapControlOperationMessage::createScanDeviceMessage(
                pointPaoID, CtiTime() ) );
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


void VoltageRegulator::executeEnableRemoteControl()
{
    try
    {
        submitRemoteControlCommands( _keepAlivePolicy->EnableRemoteControl( getKeepAliveConfig() ),
                                     "Enable Remote Control",
                                     RegulatorEvent::EnableRemoteControl );

        _lastCommandedOperatingMode = RemoteMode;
    }
    catch ( FailedAttributeLookup & missingAttribute )
    {
        throw MissingPointAttribute( getPaoId(),
                                     missingAttribute.attribute(),
                                     getPaoType(),
                                     isTimeForMissingAttributeComplain()  );
    }

    executeEnableKeepAlive();
}


void VoltageRegulator::executeDisableRemoteControl()
{
    try
    {
        submitRemoteControlCommands( _keepAlivePolicy->DisableRemoteControl(),
                                     "Disable Remote Control",
                                     RegulatorEvent::DisableRemoteControl );

        _lastCommandedOperatingMode = LocalMode;
    }
    catch ( FailedAttributeLookup & missingAttribute )
    {
        throw MissingPointAttribute( getPaoId(),
                                     missingAttribute.attribute(),
                                     getPaoType(),
                                     isTimeForMissingAttributeComplain()  );
    }

    executeDisableKeepAlive();
}


void VoltageRegulator::submitRemoteControlCommands( Policy::Action                    & action,
                                                    const std::string                 & description,
                                                    const RegulatorEvent::EventTypes    eventType )
{
    auto & signal = action.first;

    signal->setText( description );
    signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

    CtiCapController::getInstance()->sendMessageToDispatch( signal.release() );

    enqueueRegulatorEvent( RegulatorEvent::makeRemoteControlEvent( eventType, getPaoId(), _phase ) );
}


void VoltageRegulator::executeEnableKeepAlive()
{
    long keepAlivePeriod = getKeepAliveTimer();

    try
    {
        const long delay = submitKeepAliveCommands( _keepAlivePolicy->SendKeepAlive( getKeepAliveConfig() ) );
        if ( delay > 0 )
        {
            keepAlivePeriod = delay;
        }
    }
    catch ( FailedAttributeLookup & missingAttribute )
    {
        throw MissingPointAttribute( getPaoId(),
                                     missingAttribute.attribute(),
                                     getPaoType(),
                                     isTimeForMissingAttributeComplain()  );
    }

    if ( isTimeToSendKeepAlive() )      // update the keep alive timer
    {
        _nextKeepAliveSendTime = ( CtiTime::now() + keepAlivePeriod );
    }
}


void VoltageRegulator::executeDisableKeepAlive()
{
    long keepAlivePeriod = getKeepAliveTimer();

    try
    {
        const long delay = submitKeepAliveCommands( _keepAlivePolicy->StopKeepAlive() );
        if ( delay > 0 )
        {
            keepAlivePeriod = delay;
        }
    }
    catch ( FailedAttributeLookup & missingAttribute )
    {
        throw MissingPointAttribute( getPaoId(),
                                     missingAttribute.attribute(),
                                     getPaoType(),
                                     isTimeForMissingAttributeComplain()  );
    }

    if ( isTimeToSendKeepAlive() )      // update the keep alive timer
    {
        _nextKeepAliveSendTime = ( CtiTime::now() + keepAlivePeriod );
    }
}


long VoltageRegulator::submitKeepAliveCommands( Policy::Actions & actions )
{
    auto & signal  = actions[0].first;
    auto & request = actions[0].second;

    const long delay = signal->getPointValue();

    signal->setText( "Keep Alive" );
    signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

    CtiCapController::getInstance()->sendMessageToDispatch( signal.release() );

    CtiCapController::getInstance()->manualCapBankControl( request.release() );

    if ( actions.size() > 1 )
    {
        auto & signal  = actions[1].first;
        auto & request = actions[1].second;

        signal->setText( "Auto Block Enable" );
        signal->setAdditionalInfo( "Voltage Regulator Name: " + getPaoName() );

        CtiCapController::getInstance()->sendMessageToDispatch( signal.release() );

        CtiCapController::getInstance()->manualCapBankControl( request.release() );
    }

    return delay;
}


}
}

