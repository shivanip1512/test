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

    // TODO -- jmoc
    // Send the ActiveMQ Thrift EatonCloudRestore message here with above settings - needs definition...

    // if stopImmediately == true then we are doing a Restore - if false we do a StopCycle,
    // we need to add an enum to the stop message...
    // and what about NoControl gears? based on direct gear code they should do nothing


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

    // TODO -- jmoc
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

    // TODO -- jmoc
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

bool LMGroupEatonCloud::sendNoControl()
{
    // No message to send

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "No Control gear for " << _groupTypeName << " LM Group: " << getPAOName() );
    }

    // TODO -- jmoc
    //  is this true...  legacy gears don't seem to set this (maybe?) - needs investigation
    setGroupControlState( InactiveState );

    //return true;
    return false;   // this will prevent the programs last control time from being updated - don't like as it implies some sort of failure
}

bool LMGroupEatonCloud::doesStopRequireCommandAt( const CtiTime & currentTime ) const
{
    // TODO -- jmoc
    //  is this true?
    // Always send the restore at the end of control.

    return true;
}

