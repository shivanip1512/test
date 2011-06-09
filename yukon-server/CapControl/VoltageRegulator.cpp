

#include "yukon.h"
#include "VoltageRegulator.h"
#include "ccutil.h"

#include "logger.h"

#include "msg_signal.h"
#include "msg_pcrequest.h"
#include "Capcontroller.h"

using namespace boost::posix_time;

namespace Cti           {
namespace CapControl    {


// If these strings change, remember to update them in resolveCapControlType()
const std::string VoltageRegulator::LoadTapChanger                  = "LTC";
const std::string VoltageRegulator::GangOperatedVoltageRegulator    = "GO_REGULATOR";
const std::string VoltageRegulator::PhaseOperatedVoltageRegulator   = "PO_REGULATOR";


VoltageRegulator::VoltageRegulator()
    : CapControlPao(),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(pos_infin))
{
    // empty...
}


VoltageRegulator::VoltageRegulator(Cti::RowReader & rdr)
    : CapControlPao(rdr),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(pos_infin))
{
    rdr["KeepAliveTimer"]   >> _keepAliveTimer;
    rdr["KeepAliveConfig"]  >> _keepAliveConfig;
}


VoltageRegulator::VoltageRegulator(const VoltageRegulator & toCopy)
    : CapControlPao(),
    _updated(true),
    _mode(VoltageRegulator::RemoteMode),
    _lastTapOperation(VoltageRegulator::None),
    _lastMissingAttributeComplainTime(CtiTime(neg_infin)),
    _keepAliveConfig(0),
    _keepAliveTimer(0),
    _nextKeepAliveSendTime(CtiTime(pos_infin))
{
    operator=(toCopy);
}


VoltageRegulator & VoltageRegulator::operator=(const VoltageRegulator & rhs)
{
    if ( this != &rhs )
    {
        CapControlPao::operator=(rhs);

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
    }

    return *this;
}


void VoltageRegulator::saveGuts(RWvostream& ostrm) const
{
    RWCollectable::saveGuts(ostrm);
    CapControlPao::saveGuts(ostrm);

    ostrm << 0                      //parentId Must be here for clients...
          << _lastTapOperation
          << _lastTapOperationTime;
}


void VoltageRegulator::handlePointData(CtiPointDataMsg * message)
{
    setUpdated(true);
    _pointValues.updatePointValue(message);
}


VoltageRegulator::IDSet VoltageRegulator::getRegistrationPoints()
{
    IDSet IDs;

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

    executeDigitalOutputHelper( getPointByAttribute( PointAttribute::TapUp ), "Raise Tap Position", capControlIvvcTapOperation );
}


void VoltageRegulator::executeTapDownOperation()
{
    notifyTapOperation( VoltageRegulator::LowerTap );

    executeDigitalOutputHelper( getPointByAttribute( PointAttribute::TapDown ), "Lower Tap Position", capControlIvvcTapOperation );
}


void VoltageRegulator::executeIntegrityScanHelper( const LitePoint & point )
{
    CtiCapController::getInstance()->sendMessageToDispatch( createDispatchMessage( point.getPointId(), "Integrity Scan" ) );

    std::string commandString("scan integrity");

    CtiRequestMsg *request = createPorterRequestMsg( point.getPaoId(), commandString );
    request->setSOE(5);

    CtiCapController::getInstance()->manualCapBankControl( request );
}


void VoltageRegulator::executeDigitalOutputHelper( const LitePoint & point,
                                                   const std::string & textDescription,
                                                   const int recordEventType )
{
    CtiCapController::getInstance()->sendMessageToDispatch( createDispatchMessage( point.getPointId(), textDescription ) );

    if ( recordEventType != capControlNoEvent )
    {
        CtiCapController::getInstance()->sendEventLogMessage( new CtiCCEventLogMsg( textDescription, getPaoId(), recordEventType ) );
    }

    std::string commandString = "control close select pointid " + CtiNumStr( point.getPointId() );

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
        CtiCapController::getInstance()->sendEventLogMessage( new CtiCCEventLogMsg( textDescription, getPaoId(), recordEventType ) );
    }
}


void VoltageRegulator::executeKeepAliveHelper( const LitePoint & point, const int keepAliveValue )
{
    long pointOffset = point.getPointOffset() % 10000;

    CtiCapController::getInstance()->sendMessageToDispatch( createDispatchMessage( point.getPointOffset(), "Keep Alive" ) );

    std::string commandString = "putvalue analog " + CtiNumStr( pointOffset ) + " " + CtiNumStr( keepAliveValue );

    CtiRequestMsg *request = createPorterRequestMsg( point.getPaoId(), commandString );
    request->setSOE(5);

    CtiCapController::getInstance()->manualCapBankControl( request );

    if ( isTimeToSendKeepAlive() )      // update the keep alive timer
    {
        _nextKeepAliveSendTime += _keepAliveTimer;
    }
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

}
}

