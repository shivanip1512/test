

#include "precompiled.h"
#include "VoltageRegulator.h"
#include "ccutil.h"

#include "logger.h"

#include "msg_signal.h"
#include "msg_pcrequest.h"
#include "Capcontroller.h"

using namespace boost::posix_time;
using namespace Cti::Messaging::CapControl;

namespace Cti           {
namespace CapControl    {


// If these strings change, remember to update them in resolveCapControlType()
const std::string VoltageRegulator::LoadTapChanger                  = "LTC";
const std::string VoltageRegulator::GangOperatedVoltageRegulator    = "GO_REGULATOR";
const std::string VoltageRegulator::PhaseOperatedVoltageRegulator   = "PO_REGULATOR";


VoltageRegulator::VoltageRegulator()
    : CapControlPao(),
    _phase(Phase_Unknown),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(neg_infin)),
    _voltChangePerTap(0.75)
{
    // empty...
}


VoltageRegulator::VoltageRegulator(Cti::RowReader & rdr)
    : CapControlPao(rdr),
    _phase(Phase_Unknown),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(neg_infin)),
    _voltChangePerTap(0.75)
{
    /*
        We have data from the Regulator table - this will only be the case if we are an LTC.
    */

    if ( ! rdr["KeepAliveTimer"].isNull() )
    {
        rdr["KeepAliveTimer"]   >> _keepAliveTimer;
    }

    if ( ! rdr["KeepAliveConfig"].isNull() )
    {
        rdr["KeepAliveConfig"]  >> _keepAliveConfig;
    }

    rdr["VoltChangePerTap"] >> _voltChangePerTap;
}


VoltageRegulator::VoltageRegulator(const VoltageRegulator & toCopy)
    : CapControlPao(),
    _phase(Phase_Unknown),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(neg_infin)),
    _voltChangePerTap(0.75)
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

        _lastTapOperation       = rhs._lastTapOperation;
        _lastTapOperationTime   = rhs._lastTapOperationTime;

        _attributes = rhs._attributes;

        _pointValues = rhs._pointValues;

        _lastMissingAttributeComplainTime = rhs._lastMissingAttributeComplainTime;

        _keepAliveConfig        = rhs._keepAliveConfig;
        _keepAliveTimer         = rhs._keepAliveTimer;
        _nextKeepAliveSendTime  = rhs._nextKeepAliveSendTime;

        _voltChangePerTap       = rhs._voltChangePerTap;
    }

    return *this;
}


void VoltageRegulator::handlePointData(CtiPointDataMsg * message)
{
    setUpdated(true);
    _pointValues.updatePointValue(message);
}


VoltageRegulator::IDSet VoltageRegulator::getRegistrationPoints()
{
    IDSet IDs;

    if ( getDisabledStatePointId() > 0 )
    {
        IDs.insert( getDisabledStatePointId() );
    }

    for each ( const AttributeMap::value_type & attribute in _attributes  )
    {
        IDs.insert( attribute.second.getPointId() );
    }

    return IDs;
}


LitePoint VoltageRegulator::getPointByAttribute(const PointAttribute & attribute)
{
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


void VoltageRegulator::notifyTapOperation(const TapOperation & operation, const CtiTime & timeStamp)
{
    _lastTapOperation     = operation;
    _lastTapOperationTime = timeStamp;
}


bool VoltageRegulator::getPointValue(int pointId, double & value)
{
    return _pointValues.getPointValue( pointId, value );
}


void VoltageRegulator::loadPointAttributes(AttributeService * service, const PointAttribute & attribute)
{
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


void VoltageRegulator::executeTapUpOperation()
{
    notifyTapOperation( VoltageRegulator::RaiseTap );

    std::string description = "Raise Tap Position" + getPhaseString();

    executeDigitalOutputHelper( getPointByAttribute( PointAttribute::TapUp ), description, capControlIvvcTapOperation );
    sendCapControlOperationMessage( CapControlOperationMessage::createRaiseTapMessage( getPaoId(), CtiTime() ) );
}


void VoltageRegulator::executeTapDownOperation()
{
    notifyTapOperation( VoltageRegulator::LowerTap );

    std::string description = "Lower Tap Position" + getPhaseString();

    executeDigitalOutputHelper( getPointByAttribute( PointAttribute::TapDown ), description, capControlIvvcTapOperation );
    sendCapControlOperationMessage( CapControlOperationMessage::createLowerTapMessage( getPaoId(), CtiTime() ) );
}


void VoltageRegulator::executeIntegrityScanHelper( const LitePoint & point )
{
    CtiCapController::getInstance()->sendMessageToDispatch( createDispatchMessage( point.getPointId(), "Integrity Scan" ) );

    std::string commandString("scan integrity");

    CtiRequestMsg *request = createPorterRequestMsg( point.getPaoId(), commandString );
    request->setSOE(5);

    CtiCapController::getInstance()->manualCapBankControl( request );
    sendCapControlOperationMessage( CapControlOperationMessage::createScanDeviceMessage( point.getPaoId(), CtiTime() ) );
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


double VoltageRegulator::getVoltageChangePerTap() const
{
    return _voltChangePerTap;
}


}
}

