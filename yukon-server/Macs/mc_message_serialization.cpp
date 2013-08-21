#include "precompiled.h"

#include "message_serialization.h"
#include "message_serialization_util.h"
#include "mc_message_serialization.h"

using namespace std;

namespace Cti {
namespace Messaging {
namespace Serialization {

namespace {

struct MCMessageFactoryRegister
{
    MCMessageFactoryRegister()
    {
        g_messageFactory.registerSerializer <::CtiMCUpdateSchedule,   Thrift::MCUpdateSchedule>   ( &serialize, &deserialize, "MCUpdateSchedule" );
        g_messageFactory.registerSerializer <::CtiMCAddSchedule,      Thrift::MCAddSchedule>      ( &serialize, &deserialize, "MCAddSchedule" );
        g_messageFactory.registerSerializer <::CtiMCDeleteSchedule,   Thrift::MCDeleteSchedule>   ( &serialize, &deserialize, "MCDeleteSchedule" );
        g_messageFactory.registerSerializer <::CtiMCRetrieveSchedule, Thrift::MCRetrieveSchedule> ( &serialize, &deserialize, "MCRetrieveSchedule" );
        g_messageFactory.registerSerializer <::CtiMCRetrieveScript,   Thrift::MCRetrieveScript>   ( &serialize, &deserialize, "MCRetrieveScript" );
        g_messageFactory.registerSerializer <::CtiMCOverrideRequest,  Thrift::MCOverrideRequest>  ( &serialize, &deserialize, "MCOverrideRequest" );
        g_messageFactory.registerSerializer <::CtiMCInfo,             Thrift::MCInfo>             ( &serialize, &deserialize, "MCInfo" );
        g_messageFactory.registerSerializer <::CtiMCSchedule,         Thrift::MCSchedule>         ( &serialize, &deserialize, "MCSchedule" );
        g_messageFactory.registerSerializer <::CtiMCScript,           Thrift::MCScript>           ( &serialize, &deserialize, "MCScript" );
    }
};

const MCMessageFactoryRegister g_mcMessageFactoryRegister;

}

//=============================================================================
//  MCUpdateSchedule
//=============================================================================

MessagePtr<Thrift::MCUpdateSchedule>::type serialize( const ::CtiMCUpdateSchedule& imsg )
{
    MessagePtr<Thrift::MCUpdateSchedule>::type omsg( new Thrift::MCUpdateSchedule );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__schedule               ( *serialize( imsg.getSchedule() ));
    omsg->__set__script                 ( imsg.getScript() );

    return omsg;
}

MessagePtr<::CtiMCUpdateSchedule>::type deserialize( const Thrift::MCUpdateSchedule& imsg )
{
    MessagePtr<::CtiMCUpdateSchedule>::type omsg( new ::CtiMCUpdateSchedule );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setSchedule                   ( *deserialize( imsg._schedule ));
    omsg->setScript                     ( imsg._script );

    return omsg;
}

//=============================================================================
//  MCAddSchedule
//=============================================================================

MessagePtr<Thrift::MCAddSchedule>::type serialize( const ::CtiMCAddSchedule& imsg )
{
    MessagePtr<Thrift::MCAddSchedule>::type omsg( new Thrift::MCAddSchedule );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__schedule               ( *serialize( imsg.getSchedule() ));
    omsg->__set__script                 ( imsg.getScript() );

    return omsg;
}

MessagePtr<::CtiMCAddSchedule>::type deserialize( const Thrift::MCAddSchedule& imsg )
{
    MessagePtr<::CtiMCAddSchedule>::type omsg( new ::CtiMCAddSchedule );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setSchedule                   ( *deserialize( imsg._schedule ));
    omsg->setScript                     ( imsg._script );

    return omsg;
}

//=============================================================================
//  MCDeleteSchedule
//=============================================================================

MessagePtr<Thrift::MCDeleteSchedule>::type serialize( const ::CtiMCDeleteSchedule& imsg )
{
    MessagePtr<Thrift::MCDeleteSchedule>::type omsg( new Thrift::MCDeleteSchedule );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getScheduleID() );

    return omsg;
}

MessagePtr<::CtiMCDeleteSchedule>::type deserialize( const Thrift::MCDeleteSchedule& imsg )
{
    MessagePtr<::CtiMCDeleteSchedule>::type omsg( new ::CtiMCDeleteSchedule );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setScheduleID                 ( imsg._id );

    return omsg;
}

//=============================================================================
//  MCRetrieveSchedule
//=============================================================================

MessagePtr<Thrift::MCRetrieveSchedule>::type serialize( const ::CtiMCRetrieveSchedule& imsg )
{
    MessagePtr<Thrift::MCRetrieveSchedule>::type omsg( new Thrift::MCRetrieveSchedule );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getScheduleID() );

    return omsg;
}

MessagePtr<::CtiMCRetrieveSchedule>::type deserialize( const Thrift::MCRetrieveSchedule& imsg )
{
    MessagePtr<::CtiMCRetrieveSchedule>::type omsg( new ::CtiMCRetrieveSchedule );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setScheduleID                 ( imsg._id );

    return omsg;
}

//=============================================================================
//  MCRetrieveScript
//=============================================================================

MessagePtr<Thrift::MCRetrieveScript>::type serialize( const ::CtiMCRetrieveScript& imsg )
{
    MessagePtr<Thrift::MCRetrieveScript>::type omsg( new Thrift::MCRetrieveScript );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__name                   ( imsg.getScriptName() );

    return omsg;
}

MessagePtr<::CtiMCRetrieveScript>::type deserialize( const Thrift::MCRetrieveScript& imsg )
{
    MessagePtr<::CtiMCRetrieveScript>::type omsg( new ::CtiMCRetrieveScript );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setScriptName                 ( imsg._name );

    return omsg;
}

//=============================================================================
//  MCOverrideRequest
//=============================================================================

MessagePtr<Thrift::MCOverrideRequest>::type serialize( const ::CtiMCOverrideRequest& imsg )
{
    MessagePtr<Thrift::MCOverrideRequest>::type omsg( new Thrift::MCOverrideRequest );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__action                 ( imsg.getAction() );
    omsg->__set__id                     ( imsg.getID() );
    omsg->__set__startTime              ( CtiTimeToMilliseconds( imsg.getStartTime() ));
    omsg->__set__stopTime               ( CtiTimeToMilliseconds( imsg.getStopTime() ));

    return omsg;
}

MessagePtr<::CtiMCOverrideRequest>::type deserialize( const Thrift::MCOverrideRequest& imsg )
{
    MessagePtr<::CtiMCOverrideRequest>::type omsg( new ::CtiMCOverrideRequest );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setAction                     ( (::CtiMCOverrideRequest::Action)imsg._action );
    omsg->setID                         ( imsg._id );
    omsg->setStartTime                  ( MillisecondsToCtiTime( imsg._startTime ));
    omsg->setStopTime                   ( MillisecondsToCtiTime( imsg._stopTime ));

    return omsg;
}

//=============================================================================
//  MCInfo
//=============================================================================

MessagePtr<Thrift::MCInfo>::type serialize( const ::CtiMCInfo& imsg )
{
    MessagePtr<Thrift::MCInfo>::type omsg( new Thrift::MCInfo );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__id                     ( imsg.getID() );
    omsg->__set__info                   ( imsg.getInfo() );

    return omsg;
}

MessagePtr<::CtiMCInfo>::type deserialize( const Thrift::MCInfo& imsg )
{
    MessagePtr<::CtiMCInfo>::type omsg( new ::CtiMCInfo );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setID                         ( imsg._id );
    omsg->setInfo                       ( imsg._info );

    return omsg;
}

//=============================================================================
//  MCSchedule
//=============================================================================

MessagePtr<Thrift::MCSchedule>::type serialize( const ::CtiMCSchedule& imsg )
{
    MessagePtr<Thrift::MCSchedule>::type omsg( new Thrift::MCSchedule );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__scheduleId             ( imsg.getScheduleID() );
    omsg->__set__scheduleName           ( imsg.getScheduleName() );
    omsg->__set__categoryName           ( imsg.getCategoryName() );
    omsg->__set__scheduleType           ( imsg.getScheduleType() );
    omsg->__set__holidayScheduleId      ( imsg.getHolidayScheduleID() );
    omsg->__set__commandFile            ( imsg.getCommandFile() );
    omsg->__set__currentState           ( imsg.getCurrentState() );
    omsg->__set__startPolicy            ( imsg.getStartPolicy() );
    omsg->__set__stopPolicy             ( imsg.getStopPolicy() );
    omsg->__set__lastRunTime            ( CtiTimeToMilliseconds( imsg.getLastRunTime() ));
    omsg->__set__lastRunStatus          ( imsg.getLastRunStatus() );
    omsg->__set__startDay               ( imsg.getStartDay() );
    omsg->__set__startMonth             ( imsg.getStartMonth() );
    omsg->__set__startYear              ( imsg.getStartYear() );
    omsg->__set__startTime              ( imsg.getStartTime() );
    omsg->__set__stopTime               ( imsg.getStopTime() );
    omsg->__set__validWeekDays          ( imsg.getValidWeekDays() );
    omsg->__set__duration               ( imsg.getDuration() );
    omsg->__set__manualStartTime        ( 0 ); //getManualStartTime().seconds();
    omsg->__set__manualStopTime         ( 0 ); //getManualStopTime().seconds();
    omsg->__set__targetPaoId            ( imsg.getTargetPaoId() );
    omsg->__set__startCommand           ( imsg.getStartCommand() );
    omsg->__set__stopCommand            ( imsg.getStopCommand() );
    omsg->__set__repeatInterval         ( imsg.getRepeatInterval() );
    omsg->__set__currentStartTime       ( CtiTimeToMilliseconds( imsg.getCurrentStartTime() ));
    omsg->__set__currentStopTime        ( CtiTimeToMilliseconds( imsg.getCurrentStopTime() ));
    omsg->__set__templateType           ( imsg.getTemplateType() );

    if( !imsg.checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return omsg;
}

MessagePtr<::CtiMCSchedule>::type deserialize( const Thrift::MCSchedule& imsg )
{
    MessagePtr<::CtiMCSchedule>::type omsg( new ::CtiMCSchedule );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setScheduleID                 ( imsg._scheduleId );
    omsg->setScheduleName               ( imsg._scheduleName );
    omsg->setCategoryName               ( imsg._categoryName );
    omsg->setScheduleType               ( imsg._scheduleType );
    omsg->setHolidayScheduleID          ( imsg._holidayScheduleId );
    omsg->setCommandFile                ( imsg._commandFile );
    omsg->setCurrentState               ( imsg._currentState );
    omsg->setStartPolicy                ( imsg._startPolicy );
    omsg->setStopPolicy                 ( imsg._stopPolicy );
    omsg->setLastRunTime                ( MillisecondsToCtiTime( imsg._lastRunTime ));
    omsg->setLastRunStatus              ( imsg._lastRunStatus );
    omsg->setStartDay                   ( imsg._startDay );
    omsg->setStartMonth                 ( imsg._startMonth );
    omsg->setStartYear                  ( imsg._startYear );
    omsg->setStartTime                  ( imsg._startTime );
    omsg->setStopTime                   ( imsg._stopTime );
    omsg->setValidWeekDays              ( imsg._validWeekDays );
    omsg->setDuration                   ( imsg._duration );

    // Dont set the man start/stop time remove this in the future
    // omsg.setManualStartTime( MillisecondsToCtiTime( imsg._manualStartTime ));
    // omsg.setManualStopTime ( MillisecondsToCtiTime( imsg._manualStopTime ));

    omsg->setTargetPaoID                ( imsg._targetPaoId );
    omsg->setStartCommand               ( imsg._startCommand );
    omsg->setStopCommand                ( imsg._stopCommand );
    omsg->setRepeatInterval             ( imsg._repeatInterval );

    // TODO: find why those are not included in original code
    // omsg.setCurrentStartTime( CtiTime( imsg._currentStartTime ));
    // omsg.setCurrentStopTime ( CtiTime( imsg._currentStopTime ));

    omsg->setTemplateType               ( imsg._templateType );

    if( !omsg->checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return omsg;
}

//=============================================================================
//  MCScript
//=============================================================================

MessagePtr<Thrift::MCScript>::type serialize( const ::CtiMCScript& imsg )
{
    MessagePtr<Thrift::MCScript>::type omsg( new Thrift::MCScript );

    omsg->__set__baseMessage            ( *serialize( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__name                   ( imsg.getScriptName() );
    omsg->__set__contents               ( imsg.getContents() );

    return omsg;
}

MessagePtr<::CtiMCScript>::type deserialize( const Thrift::MCScript& imsg )
{
    MessagePtr<::CtiMCScript>::type omsg( new ::CtiMCScript );

    static_cast<::CtiMessage&>(*omsg)   = *deserialize( imsg._baseMessage );
    omsg->setScriptName                 ( imsg._name );
    omsg->setContents                   ( imsg._contents );

    return omsg;
}

}
}
}
