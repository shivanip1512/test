#include "precompiled.h"

#include "message_serialization.h"
#include "message_serialization_util.h"
#include "amq_constants.h"

#include <vector>

using namespace std;

namespace Cti {
namespace Messaging {
namespace Serialization {

namespace {

struct MessageFactoryRegister
{
    MessageFactoryRegister()
    {
        g_messageFactory.registerSerializer <::CtiMessage,                 Thrift::Message>              ( &populateThrift, &populateMessage, "Message" );
        g_messageFactory.registerSerializer <::CtiCommandMsg,              Thrift::Command>              ( &populateThrift, &populateMessage, "Command" );
        g_messageFactory.registerSerializer <::CtiDBChangeMsg,             Thrift::DBChange>             ( &populateThrift, &populateMessage, "DBChange" );
        g_messageFactory.registerSerializer <::CtiLMControlHistoryMsg,     Thrift::LMControlHistory>     ( &populateThrift, &populateMessage, "LMControlHistory" );
        g_messageFactory.registerSerializer <::CtiMultiMsg,                Thrift::Multi>                ( &populateThrift, &populateMessage, "Multi" );
        g_messageFactory.registerSerializer <::CtiNotifAlarmMsg,           Thrift::NotifAlarm>           ( &populateThrift, &populateMessage, "NotifAlarm" );
        g_messageFactory.registerSerializer <::CtiNotifEmailMsg,           Thrift::NotifEmail>           ( &populateThrift, &populateMessage, "NotifEmail" );
        g_messageFactory.registerSerializer <::CtiCustomerNotifEmailMsg,   Thrift::NotifCustomerEmail>   ( &populateThrift, &populateMessage, "NotifCustomerEmail" );
        g_messageFactory.registerSerializer <::CtiNotifLMControlMsg,       Thrift::NotifLMControl>       ( &populateThrift, &populateMessage, "NotifLMControl" );
        g_messageFactory.registerSerializer <::CtiRequestMsg,              Thrift::Request>              ( &populateThrift, &populateMessage, "Request" );
        g_messageFactory.registerSerializer <::CtiReturnMsg,               Thrift::Return>               ( &populateThrift, &populateMessage, "Return" );
        g_messageFactory.registerSerializer <::CtiPointDataMsg,            Thrift::PointData>            ( &populateThrift, &populateMessage, "PointData" );
        g_messageFactory.registerSerializer <::CtiPointRegistrationMsg,    Thrift::PointRegistration>    ( &populateThrift, &populateMessage, "PointRegistration" );
        g_messageFactory.registerSerializer <::CtiQueueDataMsg,            Thrift::QueueData>            ( &populateThrift, &populateMessage, "QueueData" );
        g_messageFactory.registerSerializer <::CtiRegistrationMsg,         Thrift::Registration>         ( &populateThrift, &populateMessage, "Registration" );
        g_messageFactory.registerSerializer <::CtiRequestCancelMsg,        Thrift::RequestCancel>        ( &populateThrift, &populateMessage, "RequestCancel" );
        g_messageFactory.registerSerializer <::CtiServerRequestMsg,        Thrift::ServerRequest>        ( &populateThrift, &populateMessage, "ServerRequest" );
        g_messageFactory.registerSerializer <::CtiServerResponseMsg,       Thrift::ServerResponse>       ( &populateThrift, &populateMessage, "ServerResponse" );
        g_messageFactory.registerSerializer <::CtiSignalMsg,               Thrift::Signal>               ( &populateThrift, &populateMessage, "Signal" );
        g_messageFactory.registerSerializer <::CtiTagMsg,                  Thrift::Tag>                  ( &populateThrift, &populateMessage, "Tag" );
        g_messageFactory.registerSerializer <::CtiTraceMsg,                Thrift::Trace>                ( &populateThrift, &populateMessage, "Trace" );
    }
};

const IM_EX_MSG MessageFactoryRegister g_messageFactoryRegister;

}

//=============================================================================
//  Message
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Message>::type populateThrift( const ::CtiMessage& imsg )
{
    MessagePtr<Thrift::Message>::type omsg( new Thrift::Message );

    omsg->__set__messageTime            ( CtiTimeToMilliseconds( imsg.getMessageTime() ));
    omsg->__set__messagePriority        ( imsg.getMessagePriority() );
    omsg->__set__soe                    ( imsg.getSOE() );
    omsg->__set__usr                    ( imsg.getUser() );
    omsg->__set__token                  ( imsg.getToken() );
    omsg->__set__src                    ( imsg.getSource() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiMessage>::type populateMessage( const Thrift::Message& imsg )
{
    MessagePtr<::CtiMessage>::type omsg( new ::CtiMessage );

    omsg->setMessageTime                ( MillisecondsToCtiTime( imsg._messageTime ));
    omsg->setMessagePriority            ( imsg._messagePriority );
    omsg->setSOE                        ( imsg._soe );
    omsg->setUser                       ( imsg._usr );
    omsg->setToken                      ( imsg._token );
    omsg->setSource                     ( imsg._src );

    return omsg;
}

//=============================================================================
//  Command
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Command>::type populateThrift( const ::CtiCommandMsg& imsg )
{
    MessagePtr<Thrift::Command>::type omsg( new Thrift::Command );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__operation              ( imsg.getOperation() );
    omsg->__set__opString               ( imsg.getOpString()  );
    omsg->__set__opArgCount             ( imsg.getOpArgList().size() );
    omsg->__set__opArgList              ( imsg.getOpArgList() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiCommandMsg>::type populateMessage( const Thrift::Command& imsg )
{
    MessagePtr<::CtiCommandMsg>::type omsg( new ::CtiCommandMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setOperation                  ( imsg._operation );
    omsg->setOpString                   ( imsg._opString );
    omsg->setOpArgList                  ( imsg._opArgList );

    return omsg;
}

//=============================================================================
//  DBChange
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::DBChange>::type populateThrift( const ::CtiDBChangeMsg& imsg )
{
    MessagePtr<Thrift::DBChange>::type omsg( new Thrift::DBChange );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getId() );
    omsg->__set__database               ( imsg.getDatabase() );
    omsg->__set__category               ( imsg.getCategory() );
    omsg->__set__objecttype             ( imsg.getObjectType() );
    omsg->__set__typeofchange           ( imsg.getTypeOfChange() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiDBChangeMsg>::type populateMessage( const Thrift::DBChange& imsg )
{
    MessagePtr<::CtiDBChangeMsg>::type omsg ( new ::CtiDBChangeMsg(
                                          *populateMessage( imsg._baseMessage ),
                                          imsg._id,
                                          imsg._database,
                                          imsg._category,
                                          imsg._objecttype,
                                          imsg._typeofchange ));

    return omsg;
}

//=============================================================================
//  LMControlHistory
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::LMControlHistory>::type populateThrift( const ::CtiLMControlHistoryMsg& imsg )
{
    MessagePtr<Thrift::LMControlHistory>::type omsg( new Thrift::LMControlHistory );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__paoId                  ( imsg.getPAOId() );
    omsg->__set__pointId                ( imsg.getPointId() );
    omsg->__set__rawState               ( imsg.getRawState() );
    omsg->__set__startDateTime          ( CtiTimeToMilliseconds( imsg.getStartDateTime() ));
    omsg->__set__controlDuration        ( imsg.getControlDuration() );
    omsg->__set__reductionRatio         ( imsg.getReductionRatio() );
    omsg->__set__controlType            ( imsg.getControlType() );
    omsg->__set__activeRestore          ( imsg.getActiveRestore() );
    omsg->__set__reductionValue         ( imsg.getReductionValue() );
    omsg->__set__controlPriority        ( imsg.getControlPriority() );
    omsg->__set__associationKey         ( imsg.getAssociationKey() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiLMControlHistoryMsg>::type populateMessage( const Thrift::LMControlHistory& imsg )
{
    MessagePtr<::CtiLMControlHistoryMsg>::type omsg( new ::CtiLMControlHistoryMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setPAOId                      ( imsg._paoId );
    omsg->setPointId                    ( imsg._pointId );
    omsg->setRawState                   ( imsg._rawState );
    omsg->setStartDateTime              ( MillisecondsToCtiTime( imsg._startDateTime ));
    omsg->setControlDuration            ( imsg._controlDuration );
    omsg->setReductionRatio             ( imsg._reductionRatio );
    omsg->setControlType                ( imsg._controlType );
    omsg->setActiveRestore              ( imsg._activeRestore );
    omsg->setReductionValue             ( imsg._reductionValue );
    omsg->setControlPriority            ( imsg._controlPriority );
    omsg->setAssociationKey             ( imsg._associationKey );

    return omsg;
}

//=============================================================================
//  Multi
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Multi>::type populateThrift( const ::CtiMultiMsg& imsg )
{
    MessagePtr<Thrift::Multi>::type omsg( new Thrift::Multi );

    omsg->__set__baseMessage( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));

    vector<Thrift::GenericMessage> bag;

    omsg->_bag.reserve(imsg.getData().size());

    for( const auto msg : imsg.getData() )
    {
        Thrift::GenericMessage generic_msg = serializeGenericMessage( msg );
        if( !generic_msg._messageType.empty() )
        {
            // if serialization succeed, release generic into the bag
            omsg->_bag.push_back( generic_msg );
        }
    }

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiMultiMsg>::type populateMessage( const Thrift::Multi& imsg )
{
    MessagePtr<::CtiMultiMsg>::type omsg( new ::CtiMultiMsg );

    static_cast<::CtiMessage&>(*omsg) = *populateMessage( imsg._baseMessage );

    for each( const Thrift::GenericMessage generic_msg in imsg._bag )
    {
        MessagePtr<::CtiMessage>::type msg = deserializeGenericMessage( generic_msg );
        if( msg.get() )
        {
            // if deserialization succeed, release ctimessage into the bag
            omsg->insert( msg.release() );
        }
    }

    return omsg;
}

//=============================================================================
//  NotifAlarm
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::NotifAlarm>::type populateThrift( const ::CtiNotifAlarmMsg& imsg )
{
    MessagePtr<Thrift::NotifAlarm>::type omsg( new Thrift::NotifAlarm );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__notifGroupIds          ( imsg.getNotifGroupIDs() );
    omsg->__set__categoryId             ( imsg.getCategoryID() );
    omsg->__set__pointId                ( imsg.getPointID() );
    omsg->__set__condition              ( imsg.getCondition() );
    omsg->__set__value                  ( imsg.getValue() );
    omsg->__set__alarmTimestamp         ( CtiTimeToMilliseconds( imsg.getAlarmTimestamp() ));
    omsg->__set__acknowledged           ( imsg.isAcknowledged() );
    omsg->__set__abnormal               ( imsg.isAbnormal() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiNotifAlarmMsg>::type populateMessage( const Thrift::NotifAlarm& imsg )
{
    MessagePtr<::CtiNotifAlarmMsg>::type omsg( new ::CtiNotifAlarmMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setNotifGroupIDs              ( imsg._notifGroupIds );
    omsg->setCategoryID                 ( imsg._categoryId );
    omsg->setPointID                    ( imsg._pointId );
    omsg->setCondition                  ( imsg._condition );
    omsg->setValue                      ( imsg._value );
    omsg->setAlarmTimestamp             ( MillisecondsToCtiTime( imsg._alarmTimestamp ));
    omsg->setAcknowledged               ( imsg._acknowledged );
    omsg->setAbnormal                   ( imsg._abnormal );

    return omsg;
}

//=============================================================================
//  NotifEmail
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::NotifEmail>::type populateThrift( const ::CtiNotifEmailMsg& imsg )
{
    MessagePtr<Thrift::NotifEmail>::type omsg( new Thrift::NotifEmail );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__to                     ( imsg.getTo() );
    omsg->__set__notifGroupId           ( imsg.getNotifGroupId() );
    omsg->__set__subject                ( imsg.getSubject() );
    omsg->__set__body                   ( imsg.getBody() );
    omsg->__set__toCc                   ( imsg.getToCC() );
    omsg->__set__toBcc                  ( imsg.getToBCC() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiNotifEmailMsg>::type populateMessage( const Thrift::NotifEmail& imsg )
{
    MessagePtr<::CtiNotifEmailMsg>::type omsg( new ::CtiNotifEmailMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setTo                         ( imsg._to );
    omsg->setNotifGroupId               ( imsg._notifGroupId );
    omsg->setSubject                    ( imsg._subject );
    omsg->setBody                       ( imsg._body );
    omsg->setToCC                       ( imsg._toCc );
    omsg->setToBCC                      ( imsg._toBcc );

    return omsg;
}

//=============================================================================
//  CustomerNotifEmail
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::NotifCustomerEmail>::type populateThrift( const ::CtiCustomerNotifEmailMsg& imsg )
{
    MessagePtr<Thrift::NotifCustomerEmail>::type omsg( new Thrift::NotifCustomerEmail );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__to                     ( imsg.getTo() );
    omsg->__set__customerId             ( imsg.getCustomerId() );
    omsg->__set__subject                ( imsg.getSubject() );
    omsg->__set__body                   ( imsg.getBody() );
    omsg->__set__toCc                   ( imsg.getToCC() );
    omsg->__set__toBcc                  ( imsg.getToBCC() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiCustomerNotifEmailMsg>::type populateMessage( const Thrift::NotifCustomerEmail& imsg )
{
    MessagePtr<::CtiCustomerNotifEmailMsg>::type omsg( new ::CtiCustomerNotifEmailMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setTo                         ( imsg._to );
    omsg->setCustomerId                 ( imsg._customerId );
    omsg->setSubject                    ( imsg._subject );
    omsg->setBody                       ( imsg._body );
    omsg->setToCC                       ( imsg._toCc );
    omsg->setToBCC                      ( imsg._toBcc );

    return omsg;
}

//=============================================================================
//  NotifLMControl
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::NotifLMControl>::type populateThrift( const ::CtiNotifLMControlMsg& imsg )
{
    MessagePtr<Thrift::NotifLMControl>::type omsg( new Thrift::NotifLMControl );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__notifGroupIds          ( imsg.getNotifGroupIDs() );
    omsg->__set__notifType              ( imsg.getNotifType() );
    omsg->__set__programId              ( imsg.getProgramID() );
    omsg->__set__startTime              ( CtiTimeToMilliseconds( imsg.getStartTime() ));
    omsg->__set__stopTime               ( CtiTimeToMilliseconds( imsg.getStopTime() ));

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiNotifLMControlMsg>::type populateMessage( const Thrift::NotifLMControl& imsg )
{
    MessagePtr<::CtiNotifLMControlMsg>::type omsg( new ::CtiNotifLMControlMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setNotifGroupIDs              ( imsg._notifGroupIds );
    omsg->setNotifType                  ( imsg._notifType );
    omsg->setProgramID                  ( imsg._programId );
    omsg->setStartTime                  ( MillisecondsToCtiTime( imsg._startTime ));
    omsg->setStopTime                   ( MillisecondsToCtiTime( imsg._stopTime ));

    return omsg;
}

//=============================================================================
//  Request
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Request>::type populateThrift( const ::CtiRequestMsg& imsg )
{
    MessagePtr<Thrift::Request>::type omsg( new Thrift::Request );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__deviceId               ( imsg.DeviceId() );
    omsg->__set__commandString          ( imsg.CommandString() );
    omsg->__set__routeId                ( imsg.RouteId() );
    omsg->__set__macroOffset            ( imsg.MacroOffset() ? *imsg.MacroOffset() + 1 : 0 ); // macro offset in the thrift object is one based (zero when none)
    omsg->__set__attemptNum             ( imsg.AttemptNum() );
    omsg->__set__groupMessageId         ( imsg.GroupMessageId() );
    omsg->__set__userMessageId          ( imsg.UserMessageId() );
    omsg->__set__optionsField           ( imsg.OptionsField() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiRequestMsg>::type populateMessage( const Thrift::Request& imsg )
{
    MessagePtr<::CtiRequestMsg>::type omsg( new ::CtiRequestMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setDeviceId                   ( imsg._deviceId );
    omsg->setCommandString              ( imsg._commandString );
    omsg->setRouteId                    ( imsg._routeId );
    omsg->setMacroOffset                ( imsg._macroOffset ? MacroOffset(imsg._macroOffset-1) : MacroOffset::none ); // macro offset in the thrift object is one based (zero when none)
    omsg->setAttemptNum                 ( imsg._attemptNum );
    omsg->setGroupMessageId             ( imsg._groupMessageId );
    omsg->setUserMessageId              ( imsg._userMessageId );
    omsg->setOptionsField               ( imsg._optionsField );

    return omsg;
}

//=============================================================================
//  Return
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Return>::type populateThrift( const ::CtiReturnMsg& imsg )
{
    MessagePtr<Thrift::Return>::type omsg( new Thrift::Return );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMultiMsg&>(imsg) ));
    omsg->__set__deviceId               ( imsg.DeviceId() );
    omsg->__set__commandString          ( imsg.CommandString() );
    omsg->__set__resultString           ( imsg.ResultString() );
    omsg->__set__status                 ( imsg.Status() );
    omsg->__set__routeId                ( imsg.RouteID() );
    omsg->__set__macroOffset            ( imsg.MacroOffset() ? *imsg.MacroOffset() + 1 : 0 );  // macro offset in the thrift object is one based (zero when none)
    omsg->__set__attemptNum             ( imsg.AttemptNum() );
    omsg->__set__groupMessageId         ( imsg.GroupMessageId() );
    omsg->__set__userMessageId          ( imsg.UserMessageId() );
    omsg->__set__expectMore             ( imsg.ExpectMore() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiReturnMsg>::type populateMessage( const Thrift::Return& imsg )
{
    MessagePtr<::CtiReturnMsg>::type omsg( new ::CtiReturnMsg );

    static_cast<::CtiMultiMsg&>(*omsg)  = *populateMessage( imsg._baseMessage );
    omsg->setDeviceId                   ( imsg._deviceId );
    omsg->setCommandString              ( imsg._commandString );
    omsg->setResultString               ( imsg._resultString );
    omsg->setStatus                     ( imsg._status );
    omsg->setRouteID                    ( imsg._routeId );
    omsg->setMacroOffset                ( imsg._macroOffset ? MacroOffset(imsg._macroOffset-1) : MacroOffset::none ); // macro offset in the thrift object is one based, if zero disabled
    omsg->setAttemptNum                 ( imsg._attemptNum );
    omsg->setGroupMessageId             ( imsg._groupMessageId );
    omsg->setUserMessageId              ( imsg._userMessageId );
    omsg->setExpectMore                 ( imsg._expectMore );

    return omsg;
}

//=============================================================================
//  PointData
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::PointData>::type populateThrift( const ::CtiPointDataMsg& imsg )
{
    MessagePtr<Thrift::PointData>::type omsg( new Thrift::PointData );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getId() );
    omsg->__set__type                   ( imsg.getType() );
    omsg->__set__quality                ( imsg.getQuality() );
    omsg->__set__tags                   ( imsg.getTags() );
    omsg->__set__value                  ( imsg.getValue() );
    omsg->__set__str                    ( imsg.getString() );
    omsg->__set__time                   ( CtiTimeToMilliseconds( imsg.getTime() ));
    omsg->__set__millis                 ( imsg.getMillis() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiPointDataMsg>::type populateMessage( const Thrift::PointData& imsg )
{
    MessagePtr<::CtiPointDataMsg>::type omsg( new ::CtiPointDataMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setId                         ( imsg._id );
    omsg->setType                       ( omsg->resolveType( imsg._type ));
    omsg->setQuality                    ( imsg._quality );
    omsg->resetTags                     (); // reset tags before we set the new one
    omsg->setTags                       ( imsg._tags );
    omsg->setValue                      ( imsg._value );
    omsg->setString                     ( imsg._str );
    omsg->setTimeWithMillis             ( MillisecondsToCtiTime( imsg._time ), imsg._millis );

    return omsg;
}

//=============================================================================
//  PointRegistration
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::PointRegistration>::type populateThrift( const ::CtiPointRegistrationMsg& imsg )
{
    MessagePtr<Thrift::PointRegistration>::type omsg( new Thrift::PointRegistration );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__regFlags               ( imsg.getFlags() );
    omsg->__set__pointList              ( transformContainer<vector<int32_t>>( imsg.getPointList() ));

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiPointRegistrationMsg>::type populateMessage( const Thrift::PointRegistration& imsg )
{
    MessagePtr<::CtiPointRegistrationMsg>::type omsg( new ::CtiPointRegistrationMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setFlags                      ( imsg._regFlags );
    omsg->setPointList                  ( transformContainer<vector<LONG>>( imsg._pointList ));

    return omsg;
}

//=============================================================================
//  QueueData
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::QueueData>::type populateThrift( const ::CtiQueueDataMsg& imsg )
{
    MessagePtr<Thrift::QueueData>::type omsg( new Thrift::QueueData );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getId() );
    omsg->__set__queueCount             ( imsg.getQueueCount() );
    omsg->__set__rate                   ( imsg.getRate() );
    omsg->__set__requestId              ( imsg.getRequestId() );
    omsg->__set__requestIdCount         ( imsg.getRequestIdCount() );
    omsg->__set__aTime                  ( CtiTimeToMilliseconds( imsg.getTime() ));
    omsg->__set__userMessageId          ( imsg.UserMessageId() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiQueueDataMsg>::type populateMessage( const Thrift::QueueData& imsg )
{
    MessagePtr<::CtiQueueDataMsg>::type omsg( new ::CtiQueueDataMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setId                         ( imsg._id );
    omsg->setQueueCount                 ( imsg._queueCount );
    omsg->setRate                       ( imsg._rate );
    omsg->setRequestId                  ( imsg._requestId );
    omsg->setRequestIdCount             ( imsg._requestIdCount );
    omsg->setTime                       ( MillisecondsToCtiTime( imsg._aTime ));
    omsg->setUserMessageId              ( imsg._userMessageId );

    return omsg;
}

//=============================================================================
//  Registration
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Registration>::type populateThrift( const ::CtiRegistrationMsg& imsg )
{
    MessagePtr<Thrift::Registration>::type omsg( new Thrift::Registration );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__appName                ( imsg.getAppName() );
    omsg->__set__appId                  ( imsg.getAppId() );
    omsg->__set__appIsUnique            ( imsg.getAppIsUnique() );
    omsg->__set__appExpirationDelay     ( imsg.getAppExpirationDelay() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiRegistrationMsg>::type populateMessage( const Thrift::Registration& imsg )
{
    MessagePtr<::CtiRegistrationMsg>::type omsg( new ::CtiRegistrationMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setAppName                    ( imsg._appName );
    omsg->setAppID                      ( imsg._appId );
    omsg->setAppIsUnique                ( imsg._appIsUnique );
    omsg->setAppExpirationDelay         ( imsg._appExpirationDelay );

    return omsg;
}

//=============================================================================
//  RequestCancel
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::RequestCancel>::type populateThrift( const ::CtiRequestCancelMsg& imsg )
{
    MessagePtr<Thrift::RequestCancel>::type omsg( new Thrift::RequestCancel );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__RequestId              ( imsg.getRequestId() );
    omsg->__set__RequestIdCount         ( imsg.getRequestIdCount() );
    omsg->__set__Time                   ( CtiTimeToMilliseconds( imsg.getTime() ));
    omsg->__set__UserMessageId          ( imsg.UserMessageId() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiRequestCancelMsg>::type populateMessage( const Thrift::RequestCancel& imsg )
{
    MessagePtr<::CtiRequestCancelMsg>::type omsg( new ::CtiRequestCancelMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setRequestId                  ( imsg._RequestId );
    omsg->setRequestIdCount             ( imsg._RequestIdCount );
    omsg->setTime                       ( MillisecondsToCtiTime( imsg._Time ));
    omsg->setUserMessageId              ( imsg._UserMessageId );

    return omsg;
}

//=============================================================================
//  ServerRequest
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::ServerRequest>::type populateThrift( const ::CtiServerRequestMsg& imsg )
{
    MessagePtr<Thrift::ServerRequest>::type omsg( new Thrift::ServerRequest );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getID() );
    omsg->__set__payload                ( serializeGenericMessage( imsg.getPayload() )); // if the payload is null, an empty generic message is created

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiServerRequestMsg>::type populateMessage( const Thrift::ServerRequest& imsg )
{
    MessagePtr<::CtiServerRequestMsg>::type omsg( new ::CtiServerRequestMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setID                         ( imsg._id );
    omsg->setPayload                    ( deserializeGenericMessage( imsg._payload ).release() );

    return omsg;
}

//=============================================================================
//  ServerResponse
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::ServerResponse>::type populateThrift( const ::CtiServerResponseMsg& imsg )
{
    MessagePtr<Thrift::ServerResponse>::type omsg( new Thrift::ServerResponse );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getID() );
    omsg->__set__status                 ( imsg.getStatus() );
    omsg->__set__message                ( imsg.getMessage() );
    omsg->__set__hasPayload             ( imsg.getPayload() != NULL );
    omsg->__set__payload                ( serializeGenericMessage( imsg.getPayload() )); // if the payload is null, an empty generic message is created

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiServerResponseMsg>::type populateMessage( const Thrift::ServerResponse& imsg )
{
    MessagePtr<::CtiServerResponseMsg>::type omsg( new ::CtiServerResponseMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setID                         ( imsg._id );
    omsg->setStatus                     ( imsg._status );
    omsg->setMessage                    ( imsg._message );
    omsg->setPayload                    ( (imsg._hasPayload) ? deserializeGenericMessage( imsg._payload ).release() : NULL );

    return omsg;
}

//=============================================================================
//  Signal
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Signal>::type populateThrift( const ::CtiSignalMsg& imsg )
{
    MessagePtr<Thrift::Signal>::type omsg( new Thrift::Signal );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getId() );
    omsg->__set__logType                ( imsg.getLogType() );
    omsg->__set__signalCategory         ( imsg.getSignalCategory() );
    omsg->__set__text                   ( imsg.getText() );
    omsg->__set__additionalInfo         ( imsg.getAdditionalInfo() );
    omsg->__set__tags                   ( imsg.getTags() );
    omsg->__set__condition              ( imsg.getCondition() );
    omsg->__set__signalMillis           ( imsg.getSignalMillis() );
    omsg->__set__pointValue             ( imsg.getPointValue() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiSignalMsg>::type populateMessage( const Thrift::Signal& imsg )
{
    MessagePtr<::CtiSignalMsg>::type omsg( new ::CtiSignalMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setId                         ( imsg._id );
    omsg->setLogType                    ( imsg._logType );
    omsg->setSignalCategory             ( imsg._signalCategory );
    omsg->setText                       ( imsg._text );
    omsg->setAdditionalInfo             ( imsg._additionalInfo );
    omsg->setTags                       ( imsg._tags );
    omsg->setCondition                  ( imsg._condition );
    omsg->setSignalMillis               ( imsg._signalMillis );
    omsg->setPointValue                 ( imsg._pointValue );

    return omsg;
}

//=============================================================================
//  Tag
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Tag>::type populateThrift( const ::CtiTagMsg& imsg )
{
    MessagePtr<Thrift::Tag>::type omsg( new Thrift::Tag );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__instanceId             ( imsg.getInstanceID() );
    omsg->__set__pointId                ( imsg.getPointID() );
    omsg->__set__tagId                  ( imsg.getTagID() );
    omsg->__set__descriptionStr         ( imsg.getDescriptionStr() );
    omsg->__set__action                 ( imsg.getAction() );
    omsg->__set__tagTime                ( CtiTimeToMilliseconds( imsg.getTagTime() ));
    omsg->__set__referenceStr           ( imsg.getReferenceStr() );
    omsg->__set__taggedForStr           ( imsg.getTaggedForStr() );
    omsg->__set__clientMsgId            ( imsg.getClientMsgId() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiTagMsg>::type populateMessage( const Thrift::Tag& imsg )
{
    MessagePtr<::CtiTagMsg>::type omsg( new ::CtiTagMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setInstanceID                 ( imsg._instanceId );
    omsg->setPointID                    ( imsg._pointId );
    omsg->setTagID                      ( imsg._tagId );
    omsg->setDescriptionStr             ( imsg._descriptionStr );
    omsg->setAction                     ( imsg._action );
    omsg->setTagTime                    ( MillisecondsToCtiTime( imsg._tagTime ));
    omsg->setReferenceStr               ( imsg._referenceStr );
    omsg->setTaggedForStr               ( imsg._taggedForStr );
    omsg->setClientMsgId                ( imsg._clientMsgId );

    return omsg;
}

//=============================================================================
//  Trace
//=============================================================================

IM_EX_MSG MessagePtr<Thrift::Trace>::type populateThrift( const ::CtiTraceMsg& imsg )
{
    MessagePtr<Thrift::Trace>::type omsg( new Thrift::Trace );

    omsg->__set__baseMessage            ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__end                    ( imsg.isEnd() );
    omsg->__set__attributes             ( imsg.getAttributes() );
    omsg->__set__trace                  ( imsg.getTrace() );

    return omsg;
}

IM_EX_MSG MessagePtr<::CtiTraceMsg>::type populateMessage( const Thrift::Trace& imsg )
{
    MessagePtr<::CtiTraceMsg>::type omsg( new ::CtiTraceMsg );

    static_cast<::CtiMessage&>(*omsg)   = *populateMessage( imsg._baseMessage );
    omsg->setEnd                        ( imsg._end );
    omsg->setAttributes                 ( imsg._attributes );
    omsg->setTrace                      ( imsg._trace );

    return omsg;
}


}
}
}
