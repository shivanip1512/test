#include "precompiled.h"

#include "lmid.h"
#include "lmgroupeatoncloud.h"
#include "logger.h"

extern ULONG _LM_DEBUG;


DEFINE_COLLECTABLE( LMGroupEatonCloud, LMGROUPEATONCLOUD_ID )

LMGroupEatonCloud::LMGroupEatonCloud( Cti::RowReader &rdr )
    :   SmartGroupBase( "EatonCloud", rdr )
{
    // empty
}

CtiLMGroupBase* LMGroupEatonCloud::replicate() const
{
    return new LMGroupEatonCloud( *this );
}

bool LMGroupEatonCloud::sendStopControl( bool stopImmediately )
{
    CtiTime now;

    // Send the ActiveMQ Thrift EatonCloudRestore message here with above settings - needs definition...

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Stop command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );
    setGroupControlState( InactiveState );

    return true;
}

bool LMGroupEatonCloud::sendShedControl( long controlMinutes )
{
    CtiTime now;

    //    // shed == cycle at 100% duty cycle with no ramp in/out
    // Send the ActiveMQ Thrift EatonCloudCyclingControl message here with above settings

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Shed command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + ( controlMinutes * 60 ) );

    return true;
}

bool LMGroupEatonCloud::sendCycleControl( CycleControlParameters parameters )
{
    CtiTime now;

    long controlDurationSeconds = parameters.controlDurationSeconds;

    // Send the ActiveMQ Thrift EatonCloudCyclingControl message here

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Cycle command, LM Group: " << getPAOName() );
    }

    if ( getGroupControlState() != ActiveState )
    {
        setControlStartTime( now );
        incrementDailyOps();
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + controlDurationSeconds );
    setGroupControlState( ActiveState );

    return true;
}

bool LMGroupEatonCloud::sendTimeRefreshControl( long shedTimeSeconds )
{
    CtiTime now;

    // Adjust the shedTime based on settings ....  see reduceProgramLoad() for the TimeRefresh stuff...

    // Send the ActiveMQ Thrift EatonCloudCyclingControl message here

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Time Refresh command, shed time: " << shedTimeSeconds << "s, LM Group: " << getPAOName() );
    }

    if ( getGroupControlState() != ActiveState )
    {
        setControlStartTime( now );
        incrementDailyOps();
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + shedTimeSeconds );
    setGroupControlState( ActiveState );

    return true;
}

bool LMGroupEatonCloud::sendNoControl()
{
    CtiTime now;

    // No message to send

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "No Control gear for " << _groupTypeName << " LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );
    setGroupControlState( InactiveState );

    return true;
}


bool LMGroupEatonCloud::doesStopRequireCommandAt( const CtiTime & currentTime ) const
{
    // Always send the restore at the end of control.

    return true;
}

