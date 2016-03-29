#pragma once

#include "message.h"
#include "msg_cmd.h"
#include "msg_dbchg.h"
#include "msg_lmcontrolhistory.h"
#include "msg_multi.h"
#include "msg_notif_alarm.h"
#include "msg_notif_email.h"
#include "msg_notif_lmcontrol.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_queuedata.h"
#include "Msg_reg.h"
#include "msg_requestcancel.h"
#include "msg_server_req.h"
#include "msg_server_resp.h"
#include "msg_signal.h"
#include "msg_tag.h"
#include "msg_trace.h"

#include "test_serialization_helper.h"
#include "connection_server.h"

/*-----------------------------------------------------------------------------
    Message
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiMessage> : public TestCaseBase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiMessage );
        _omsg.reset( new CtiMessage );
    }

    void Populate()
    {
        CtiMessage &imsg = *_imsg;

        GenerateRandom( imsg.MessageTime );
        GenerateRandom( imsg.MessagePriority, 0, 15 );
        GenerateRandom( imsg._soe );
        GenerateRandom( imsg._usr );
        GenerateRandom( imsg._token );
        GenerateRandom( imsg._src );
    }

    void Compare()
    {
        CtiMessage &imsg = *_imsg,
                   &omsg = *_omsg;

        CompareMember( "MessageTime",           imsg.MessageTime,           omsg.MessageTime );
        CompareMember( "MessagePriority",       imsg.MessagePriority,       omsg.MessagePriority );
        CompareMember( "_soe",                  imsg._soe,                  omsg._soe );
        CompareMember( "_usr",                  imsg._usr,                  omsg._usr );
        CompareMember( "_token",                imsg._token,                omsg._token );
        CompareMember( "_src",                  imsg._src,                  omsg._src );
    }
};

/*-----------------------------------------------------------------------------
    Command
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCommandMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiCommandMsg );
        _omsg.reset( new CtiCommandMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiCommandMsg &imsg = dynamic_cast<CtiCommandMsg&>(*_imsg);

        GenerateRandom( imsg.iOperation );
        GenerateRandom( imsg.iOpString );
        GenerateRandom( imsg.iOpArgList );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiCommandMsg &imsg = dynamic_cast<CtiCommandMsg&>(*_imsg),
                      &omsg = dynamic_cast<CtiCommandMsg&>(*_omsg);

        CompareMember( "iOperation",            imsg.iOperation,            omsg.iOperation );
        CompareMember( "iOpString",             imsg.iOpString,             omsg.iOpString );
        CompareMember( "iOpArgList",            imsg.iOpArgList,            omsg.iOpArgList );
    }
};

/*-----------------------------------------------------------------------------
    DBChange
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiDBChangeMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiDBChangeMsg(0,0,"","", 0) );
        _omsg.reset( new CtiDBChangeMsg(0,0,"","", 0) );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiDBChangeMsg &imsg = dynamic_cast<CtiDBChangeMsg&>(*_imsg);

        GenerateRandom( imsg._id );
        GenerateRandom( imsg._database );
        GenerateRandom( imsg._category );
        GenerateRandom( imsg._objecttype );
        GenerateRandom( imsg._typeofchange, -1 , 2 ); // typeofchange <=> [-1, 2]
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiDBChangeMsg &imsg = dynamic_cast<CtiDBChangeMsg&>(*_imsg),
                       &omsg = dynamic_cast<CtiDBChangeMsg&>(*_omsg);

        CompareMember( "_id",                   imsg._id,                  omsg._id );
        CompareMember( "_database",             imsg._database,            omsg._database );
        CompareMember( "_category",             imsg._category,            omsg._category );
        CompareMember( "_objecttype",           imsg._objecttype,          omsg._objecttype );
        CompareMember( "_typeofchange",         imsg._typeofchange,        omsg._typeofchange );
    }
};

/*-----------------------------------------------------------------------------
    LMControlHistory
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMControlHistoryMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMControlHistoryMsg );
        _omsg.reset( new CtiLMControlHistoryMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiLMControlHistoryMsg &imsg = dynamic_cast<CtiLMControlHistoryMsg&>(*_imsg);

        GenerateRandom( imsg._paoId );
        GenerateRandom( imsg._pointId );
        GenerateRandom( imsg._rawState );
        GenerateRandom( imsg._startDateTime );
        GenerateRandom( imsg._controlDuration );
        GenerateRandom( imsg._reductionRatio );
        GenerateRandom( imsg._controlType );
        GenerateRandom( imsg._activeRestore );
        GenerateRandom( imsg._reductionValue );
        GenerateRandom( imsg._controlPriority );
        GenerateRandom( imsg._associationKey );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiLMControlHistoryMsg &imsg = dynamic_cast<CtiLMControlHistoryMsg&>(*_imsg),
                               &omsg = dynamic_cast<CtiLMControlHistoryMsg&>(*_omsg);

        CompareMember( "_paoId",                imsg._paoId,               omsg._paoId );
        CompareMember( "_pointId",              imsg._pointId,             omsg._pointId );
        CompareMember( "_rawState",             imsg._rawState,            omsg._rawState );
        CompareMember( "_startDateTime",        imsg._startDateTime,       omsg._startDateTime );
        CompareMember( "_controlDuration",      imsg._controlDuration,     omsg._controlDuration );
        CompareMember( "_reductionRatio",       imsg._reductionRatio,      omsg._reductionRatio );
        CompareMember( "_controlType",          imsg._controlType,         omsg._controlType );
        CompareMember( "_activeRestore",        imsg._activeRestore,       omsg._activeRestore );
        CompareMember( "_reductionValue",       imsg._reductionValue,      omsg._reductionValue );
        CompareMember( "_controlPriority",      imsg._controlPriority,     omsg._controlPriority );
        CompareMember( "_associationKey",       imsg._associationKey,      omsg._associationKey );
    }
};

/*-----------------------------------------------------------------------------
    NotifAlarm
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiNotifAlarmMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiNotifAlarmMsg );
        _omsg.reset( new CtiNotifAlarmMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiNotifAlarmMsg &imsg = dynamic_cast<CtiNotifAlarmMsg&>(*_imsg);

        GenerateRandom( imsg._notif_group_ids );
        GenerateRandom( imsg._category_id );
        GenerateRandom( imsg._point_id );
        GenerateRandom( imsg._condition );
        GenerateRandom( imsg._value );
        GenerateRandom( imsg._alarm_timestamp );
        GenerateRandom( imsg._acknowledged );
        GenerateRandom( imsg._abnormal );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiNotifAlarmMsg &imsg = dynamic_cast<CtiNotifAlarmMsg&>(*_imsg),
                         &omsg = dynamic_cast<CtiNotifAlarmMsg&>(*_omsg);

        CompareMember( "_notif_group_ids",      imsg._notif_group_ids,     omsg._notif_group_ids );
        CompareMember( "_category_id",          imsg._category_id,         omsg._category_id );
        CompareMember( "_point_id",             imsg._point_id,            omsg._point_id );
        CompareMember( "_condition",            imsg._condition,           omsg._condition );
        CompareMember( "_value",                imsg._value,               omsg._value );
        CompareMember( "_alarm_timestamp",      imsg._alarm_timestamp,     omsg._alarm_timestamp );
        CompareMember( "_acknowledged",         imsg._acknowledged,        omsg._acknowledged );
        CompareMember( "_abnormal",             imsg._abnormal,            omsg._abnormal );
    }
};

/*-----------------------------------------------------------------------------
    NotifEmail
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiNotifEmailMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiNotifEmailMsg );
        _omsg.reset( new CtiNotifEmailMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiNotifEmailMsg &imsg = dynamic_cast<CtiNotifEmailMsg&>(*_imsg);

        GenerateRandom( imsg._notifGroupID );
        GenerateRandom( imsg._to );
        GenerateRandom( imsg._subject );
        GenerateRandom( imsg._body );
        GenerateRandom( imsg._toCC );
        GenerateRandom( imsg._toBCC );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiNotifEmailMsg &imsg = dynamic_cast<CtiNotifEmailMsg&>(*_imsg),
                         &omsg = dynamic_cast<CtiNotifEmailMsg&>(*_omsg);

        CompareMember( "_notifGroupID",         imsg._notifGroupID,        omsg._notifGroupID );
        CompareMember( "_to",                   imsg._to,                  omsg._to );
        CompareMember( "_subject",              imsg._subject,             omsg._subject );
        CompareMember( "_body",                 imsg._body,                omsg._body );
        CompareMember( "_toCC",                 imsg._toCC,                omsg._toCC );
        CompareMember( "_toBCC",                imsg._toBCC,               omsg._toBCC );
    }
};

/*-----------------------------------------------------------------------------
    CustomerNotifEmail
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCustomerNotifEmailMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiCustomerNotifEmailMsg );
        _omsg.reset( new CtiCustomerNotifEmailMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiCustomerNotifEmailMsg &imsg = dynamic_cast<CtiCustomerNotifEmailMsg&>(*_imsg);

        GenerateRandom( imsg._customerID );
        GenerateRandom( imsg._to );
        GenerateRandom( imsg._subject );
        GenerateRandom( imsg._body );
        GenerateRandom( imsg._toCC );
        GenerateRandom( imsg._toBCC );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiCustomerNotifEmailMsg &imsg = dynamic_cast<CtiCustomerNotifEmailMsg&>(*_imsg),
                                 &omsg = dynamic_cast<CtiCustomerNotifEmailMsg&>(*_omsg);

        CompareMember( "_customerID",           imsg._customerID,          omsg._customerID );
        CompareMember( "_to",                   imsg._to,                  omsg._to );
        CompareMember( "_subject",              imsg._subject,             omsg._subject );
        CompareMember( "_body",                 imsg._body,                omsg._body );
        CompareMember( "_toCC",                 imsg._toCC,                omsg._toCC );
        CompareMember( "_toBCC",                imsg._toBCC,               omsg._toBCC );
    }
};

/*-----------------------------------------------------------------------------
    NotifLMControl
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiNotifLMControlMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiNotifLMControlMsg );
        _omsg.reset( new CtiNotifLMControlMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiNotifLMControlMsg &imsg = dynamic_cast<CtiNotifLMControlMsg&>(*_imsg);

        GenerateRandom( imsg._notif_group_ids );
        GenerateRandom( imsg._notif_type );
        GenerateRandom( imsg._program_id );
        GenerateRandom( imsg._start_time );
        GenerateRandom( imsg._stop_time );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiNotifLMControlMsg &imsg = dynamic_cast<CtiNotifLMControlMsg&>(*_imsg),
                             &omsg = dynamic_cast<CtiNotifLMControlMsg&>(*_omsg);

        CompareMember( "_notif_group_ids",         imsg._notif_group_ids,          omsg._notif_group_ids );
        CompareMember( "_notif_type",              imsg._notif_type,               omsg._notif_type );
        CompareMember( "_program_id",              imsg._program_id,               omsg._program_id );
        CompareMember( "_start_time",              imsg._start_time,               omsg._start_time );
        CompareMember( "_stop_time",               imsg._stop_time,                omsg._stop_time );
    }
};

/*-----------------------------------------------------------------------------
    Request
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiRequestMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiRequestMsg );
        _omsg.reset( new CtiRequestMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiRequestMsg &imsg = dynamic_cast<CtiRequestMsg&>(*_imsg);

        GenerateRandom( imsg._device_id );
        GenerateRandom( imsg._command_string );
        GenerateRandom( imsg._route_id );

        if( GenerateRandom<bool>() )
        {
            imsg._macro_offset = GenerateRandom<unsigned>();
        }

        GenerateRandom( imsg._attempt_num );
        GenerateRandom( imsg._group_message_id );
        GenerateRandom( imsg._user_message_id );
        GenerateRandom( imsg._options_field );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiRequestMsg &imsg = dynamic_cast<CtiRequestMsg&>(*_imsg),
                      &omsg = dynamic_cast<CtiRequestMsg&>(*_omsg);

        CompareMember( "_device_id",                    imsg._device_id,                        omsg._device_id );
        CompareMember( "_command_string",               imsg._command_string,                   omsg._command_string );
        CompareMember( "_route_id",                     imsg._route_id,                         omsg._route_id );
        CompareMember( "_macro_offset",                 imsg._macro_offset,                     omsg._macro_offset );
        CompareMember( "_attempt_num",                  imsg._attempt_num,                      omsg._attempt_num );
        CompareMember( "_group_message_id",             imsg._group_message_id,                 omsg._group_message_id );
        CompareMember( "_user_message_id",              imsg._user_message_id,                  omsg._user_message_id );
        CompareMember( "_options_field",                imsg._options_field,                    omsg._options_field );
    }
};

/*-----------------------------------------------------------------------------
    PointData
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiPointDataMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiPointDataMsg );
        _omsg.reset( new CtiPointDataMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiPointDataMsg &imsg = dynamic_cast<CtiPointDataMsg&>(*_imsg);

        GenerateRandom( imsg._id );
        GenerateRandom( (int&)(imsg._type), 0, (int)InvalidPointType - 1 ); // invalid point is excluded
        GenerateRandom( imsg._quality,      0, (int)EstimatedQuality );
        GenerateRandom( imsg._tags );
        GenerateRandom( imsg._limit );
        GenerateRandom( imsg._value );
        GenerateRandom( imsg._time );
        GenerateRandom( imsg._millis, 0, 999 );
        GenerateRandom( imsg._str );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiPointDataMsg &imsg = dynamic_cast<CtiPointDataMsg&>(*_imsg),
                        &omsg = dynamic_cast<CtiPointDataMsg&>(*_omsg);

        CompareMember( "_id",                   imsg._id,                 omsg._id );
        CompareMember( "_type",                 (int&)(imsg._type),       (int&)(omsg._type) );
        CompareMember( "_quality",              imsg._quality,            omsg._quality );
        CompareMember( "_tags",                 imsg._tags,               omsg._tags ); // this also checks Exemption status
        CompareMember( "_limit",                imsg._limit,              omsg._limit );
        CompareMember( "_value",                imsg._value,              omsg._value );
        CompareMember( "_time",                 imsg._time,               omsg._time );
        CompareMember( "_millis",               imsg._millis,             omsg._millis );
        CompareMember( "_str",                  imsg._str,                omsg._str );
    }
};

/*-----------------------------------------------------------------------------
    PointRegistration
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiPointRegistrationMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiPointRegistrationMsg );
        _omsg.reset( new CtiPointRegistrationMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiPointRegistrationMsg &imsg = dynamic_cast<CtiPointRegistrationMsg&>(*_imsg);

        GenerateRandom( imsg.RegFlags );
        GenerateRandom( imsg.PointList );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiPointRegistrationMsg &imsg = dynamic_cast<CtiPointRegistrationMsg&>(*_imsg),
                                &omsg = dynamic_cast<CtiPointRegistrationMsg&>(*_omsg);

        CompareMember( "RegFlags",              imsg.RegFlags,          omsg.RegFlags );
        CompareMember( "PointList",             imsg.PointList,         omsg.PointList );
    }
};

/*-----------------------------------------------------------------------------
    QueueDataMsg
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiQueueDataMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiQueueDataMsg );
        _omsg.reset( new CtiQueueDataMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiQueueDataMsg &imsg = dynamic_cast<CtiQueueDataMsg&>(*_imsg);

        GenerateRandom( imsg._queueID );
        GenerateRandom( imsg._requestId );
        GenerateRandom( imsg._rate );
        GenerateRandom( imsg._queueCount );
        GenerateRandom( imsg._requestIdCount );
        GenerateRandom( imsg._userMessageID );
        GenerateRandom( imsg._time );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiQueueDataMsg &imsg = dynamic_cast<CtiQueueDataMsg&>(*_imsg),
                        &omsg = dynamic_cast<CtiQueueDataMsg&>(*_omsg);

        CompareMember( "_queueID",               imsg._queueID,                  omsg._queueID );
        CompareMember( "_requestId",             imsg._requestId,                omsg._requestId );
        CompareMember( "_rate",                  imsg._rate,                     omsg._rate );
        CompareMember( "_queueCount",            imsg._queueCount,               omsg._queueCount );
        CompareMember( "_requestIdCount",        imsg._requestIdCount,           omsg._requestIdCount );
        CompareMember( "_userMessageID",         imsg._userMessageID,            omsg._userMessageID );
        CompareMember( "_time",                  imsg._time,                     omsg._time );
    }
};

/*-----------------------------------------------------------------------------
    Registration
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiRegistrationMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiRegistrationMsg );
        _omsg.reset( new CtiRegistrationMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiRegistrationMsg &imsg = dynamic_cast<CtiRegistrationMsg&>(*_imsg);

        bool appIsUnique;

        GenerateRandom( imsg._appName );
        GenerateRandom( imsg._appId );
        GenerateRandom( appIsUnique );
        GenerateRandom( imsg._appExpirationDelay );

        imsg._appIsUnique = appIsUnique;
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiRegistrationMsg &imsg = dynamic_cast<CtiRegistrationMsg&>(*_imsg),
                           &omsg = dynamic_cast<CtiRegistrationMsg&>(*_omsg);

        CompareMember( "_appName",              imsg._appName,                  omsg._appName );
        CompareMember( "_appId",                imsg._appId,                    omsg._appId );
        CompareMember( "_appIsUnique",          imsg._appIsUnique,              omsg._appIsUnique );
        CompareMember( "_appExpirationDelay",   imsg._appExpirationDelay,       omsg._appExpirationDelay );
    }
};

/*-----------------------------------------------------------------------------
    RequestCancel
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiRequestCancelMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiRequestCancelMsg );
        _omsg.reset( new CtiRequestCancelMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiRequestCancelMsg &imsg = dynamic_cast<CtiRequestCancelMsg&>(*_imsg);

        GenerateRandom( imsg._requestId );
        GenerateRandom( imsg._requestIdCount );
        GenerateRandom( imsg._userMessageID );
        GenerateRandom( imsg._time );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiRequestCancelMsg &imsg = dynamic_cast<CtiRequestCancelMsg&>(*_imsg),
                            &omsg = dynamic_cast<CtiRequestCancelMsg&>(*_omsg);

        CompareMember( "_requestId",              imsg._requestId,                         omsg._requestId );
        CompareMember( "_requestIdCount",         imsg._requestIdCount,                    omsg._requestIdCount );
        CompareMember( "_userMessageID",          imsg._userMessageID,                     omsg._userMessageID );
        CompareMember( "_time",                   imsg._time,                              omsg._time );
    }
};

/*-----------------------------------------------------------------------------
    ServerRequest
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiServerRequestMsg> : public TestCase<CtiMessage>
{
    typedef CtiCommandMsg payload_t;

    TestCaseItem<payload_t> _tc_payload;

    std::auto_ptr<payload_t> imsg_payload, omsg_payload;

    void Create()
    {
        _imsg.reset( new CtiServerRequestMsg );
        _omsg.reset( new CtiServerRequestMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiServerRequestMsg &imsg = dynamic_cast<CtiServerRequestMsg&>(*_imsg);

        GenerateRandom( imsg._id );

        //  retain ownership of the payload, since CtiServerRequestMsg doesn't delete it
        imsg_payload.reset(new payload_t);

        imsg.setPayload( imsg_payload.get() );
        _tc_payload.Populate( (payload_t*)imsg._payload );
    }

    ~TestCase()
    {
        if( ! _imsg.get() )
        {
            imsg_payload.release();
        }
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiServerRequestMsg &imsg = dynamic_cast<CtiServerRequestMsg&>(*_imsg),
                            &omsg = dynamic_cast<CtiServerRequestMsg&>(*_omsg);

        CompareMember( "_id",  imsg._id,  omsg._id );

        if( !_tc_payload.Compare( (payload_t*)omsg._payload ) )
        {
            reportMismatch( "_payload", _tc_payload._failures );
        }

        //  retain ownership of the payload, since CtiServerRequestMsg doesn't delete it
        omsg_payload.reset(dynamic_cast<payload_t*>(omsg.getPayload()));
    }
};

/*-----------------------------------------------------------------------------
    ServerResponse
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiServerResponseMsg> : public TestCase<CtiMessage>
{
    typedef CtiCommandMsg payload_t;

    TestCaseItem<payload_t> _tc_payload;

    void Create()
    {
        _imsg.reset( new CtiServerResponseMsg );
        _omsg.reset( new CtiServerResponseMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiServerResponseMsg &imsg = dynamic_cast<CtiServerResponseMsg&>(*_imsg);

        GenerateRandom( imsg._id );
        GenerateRandom( imsg._status );
        GenerateRandom( imsg._message );

        imsg.setPayload( new payload_t );
        _tc_payload.Populate( (payload_t*)imsg._payload );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiServerResponseMsg &imsg = dynamic_cast<CtiServerResponseMsg&>(*_imsg),
                             &omsg = dynamic_cast<CtiServerResponseMsg&>(*_omsg);

        CompareMember( "_id",      imsg._id,      omsg._id );
        CompareMember( "_status",  imsg._status,  omsg._status );
        CompareMember( "_message", imsg._message, omsg._message );

        if( !_tc_payload.Compare( (payload_t*)omsg._payload ) )
        {
            reportMismatch( "_payload", _tc_payload._failures );
        }
    }
};

/*-----------------------------------------------------------------------------
    Signal
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiSignalMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiSignalMsg );
        _omsg.reset( new CtiSignalMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiSignalMsg &imsg = dynamic_cast<CtiSignalMsg&>(*_imsg);

        GenerateRandom( imsg._id );
        GenerateRandom( imsg._logType );
        GenerateRandom( imsg._signalCategory );
        GenerateRandom( imsg._text );
        GenerateRandom( imsg._additional );
        GenerateRandom( imsg._tags );
        GenerateRandom( imsg._condition );
        GenerateRandom( imsg._signalMillis, 0 , 999 );
        GenerateRandom( imsg._point_value );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiSignalMsg &imsg = dynamic_cast<CtiSignalMsg&>(*_imsg),
                     &omsg = dynamic_cast<CtiSignalMsg&>(*_omsg);

        CompareMember( "_id",                    imsg._id,                        omsg._id );
        CompareMember( "_logType",               imsg._logType,                   omsg._logType );
        CompareMember( "_signalCategory",        imsg._signalCategory,            omsg._signalCategory );
        CompareMember( "_text",                  imsg._text,                      omsg._text );
        CompareMember( "_additional",            imsg._additional,                omsg._additional );
        CompareMember( "_tags",                  imsg._tags,                      omsg._tags );
        CompareMember( "_condition",             imsg._condition,                 omsg._condition );
        CompareMember( "_signalMillis",          imsg._signalMillis,              omsg._signalMillis );
        CompareMember( "_point_value",           imsg._point_value,               omsg._point_value );
    }
};

/*-----------------------------------------------------------------------------
    Tag
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiTagMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiTagMsg );
        _omsg.reset( new CtiTagMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiTagMsg &imsg = dynamic_cast<CtiTagMsg&>(*_imsg);

        GenerateRandom( imsg._instanceid );
        GenerateRandom( imsg._pointid );
        GenerateRandom( imsg._tagid );
        GenerateRandom( imsg._descriptionStr );
        GenerateRandom( imsg._action, 0, 3 ); // 0 - 3
        GenerateRandom( imsg._tagtime );
        GenerateRandom( imsg._referenceStr );
        GenerateRandom( imsg._taggedForStr );
        GenerateRandom( imsg._clientMsgId );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiTagMsg &imsg = dynamic_cast<CtiTagMsg&>(*_imsg),
                  &omsg = dynamic_cast<CtiTagMsg&>(*_omsg);

        CompareMember( "_instanceid",            imsg._instanceid,                omsg._instanceid );
        CompareMember( "_pointid",               imsg._pointid,                   omsg._pointid );
        CompareMember( "_tagid",                 imsg._tagid,                     omsg._tagid );
        CompareMember( "_descriptionStr",        imsg._descriptionStr,            omsg._descriptionStr );
        CompareMember( "_action",                imsg._action,                    omsg._action );
        CompareMember( "_tagtime",               imsg._tagtime,                   omsg._tagtime );
        CompareMember( "_referenceStr",          imsg._referenceStr,              omsg._referenceStr );
        CompareMember( "_taggedForStr",          imsg._taggedForStr,              omsg._taggedForStr );
        CompareMember( "_clientMsgId",           imsg._clientMsgId,               omsg._clientMsgId );
    }
};

/*-----------------------------------------------------------------------------
    Trace
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiTraceMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiTraceMsg );
        _omsg.reset( new CtiTraceMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiTraceMsg &imsg = dynamic_cast<CtiTraceMsg&>(*_imsg);

        GenerateRandom( imsg._end );
        GenerateRandom( imsg._attributes );
        GenerateRandom( imsg._trace );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiTraceMsg &imsg = dynamic_cast<CtiTraceMsg&>(*_imsg),
                    &omsg = dynamic_cast<CtiTraceMsg&>(*_omsg);

        CompareMember( "_end",               imsg._end,                   omsg._end );
        CompareMember( "_attributes",        imsg._attributes,            omsg._attributes );
        CompareMember( "_trace",             imsg._trace,                 omsg._trace );
    }
};

/*-----------------------------------------------------------------------------
    Multi
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiMultiMsg> : public TestCase<CtiMessage>
{
    typedef CtiCommandMsg       message0_t;
    typedef CtiServerRequestMsg message1_t;

    TestCaseItem<message0_t> _tc_message0;
    TestCaseItem<message1_t> _tc_message1;

    void Create()
    {
        _imsg.reset( new CtiMultiMsg );
        _omsg.reset( new CtiMultiMsg );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMultiMsg &imsg = dynamic_cast<CtiMultiMsg&>(*_imsg);

        imsg.insert( new message0_t );
        imsg.insert( new message1_t );

        _tc_message0.Populate( (message0_t*)imsg._bag[0] );
        _tc_message1.Populate( (message1_t*)imsg._bag[1] );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMultiMsg &imsg = dynamic_cast<CtiMultiMsg&>(*_imsg),
                    &omsg = dynamic_cast<CtiMultiMsg&>(*_omsg);

        if( omsg._bag.size() == 2 )
        {
            if( !_tc_message0.Compare( (message0_t*)omsg._bag[0] )) reportMismatch( "_bag[0]", _tc_message0._failures );
            if( !_tc_message1.Compare( (message1_t*)omsg._bag[1] )) reportMismatch( "_bag[1]", _tc_message1._failures );
        }
        else
        {
            reportMismatch( "_bag", "size expected != size received" );
        }

    }
};

/*-----------------------------------------------------------------------------
    Return
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiReturnMsg> : public TestCase<CtiMultiMsg>
{
    void Create()
    {
        _imsg.reset( new CtiReturnMsg );
        _omsg.reset( new CtiReturnMsg );
    }

    void Populate()
    {
        TestCase<CtiMultiMsg>::Populate();

        CtiReturnMsg &imsg = dynamic_cast<CtiReturnMsg&>(*_imsg);

        GenerateRandom( imsg._device_id );
        GenerateRandom( imsg._command_string );
        GenerateRandom( imsg._result_string );
        GenerateRandom( imsg._status );
        GenerateRandom( imsg._routeid );

        if( GenerateRandom<bool>() )
        {
            imsg._macro_offset = GenerateRandom<unsigned>();
        }

        GenerateRandom( imsg._attempt_num );
        GenerateRandom( imsg._expectMore );
        GenerateRandom( imsg._group_message_id );
        GenerateRandom( imsg._user_message_id );

    }

    void Compare()
    {
        TestCase<CtiMultiMsg>::Compare();

        CtiReturnMsg &imsg = dynamic_cast<CtiReturnMsg&>(*_imsg),
                     &omsg = dynamic_cast<CtiReturnMsg&>(*_omsg);

        CompareMember( "_device_id",           imsg._device_id,                 omsg._device_id );
        CompareMember( "_command_string",      imsg._command_string,            omsg._command_string );
        CompareMember( "_result_string",       imsg._result_string,             omsg._result_string );
        CompareMember( "_status",              imsg._status,                    omsg._status );
        CompareMember( "_routeid",             imsg._routeid,                   omsg._routeid );
        CompareMember( "_macro_offset",        imsg._macro_offset,              omsg._macro_offset );
        CompareMember( "_attempt_num",         imsg._attempt_num,               omsg._attempt_num );
        CompareMember( "_expectMore",          imsg._expectMore,                omsg._expectMore );
        CompareMember( "_group_message_id",    imsg._group_message_id,          omsg._group_message_id );
        CompareMember( "_user_message_id",     imsg._user_message_id,           omsg._user_message_id );
    }
};
