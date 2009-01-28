
#pragma warning( disable : 4786)
#ifndef __PENDING_INFO_H__
#define __PENDING_INFO_H__

/*-----------------------------------------------------------------------------*
*
* File:   pending_info
*
* Class:  CtiPendingPointOperations
* Date:   11/17/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/pending_info.h-arc  $
* REVISION     :  $Revision: 1.15.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:48 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <limits.h>
#include "ctitime.h"

#include "msg_pdata.h"
#include "msg_signal.h"
#include "tbl_lm_controlhist.h"

class CtiPendingPointOperations
{
public:

    typedef map< int, long > CtiOffsetPIDMap_t;

    typedef enum
    {
        invalid,
        intervalcrossing,
        intervalpointpostcrossing,  // Control history points are posted at this interval if there is no other post to be performed!
        stopintervalcrossing,   // This is an interval used to post time remaining in control.
        datachange,             // CONTROL Point went to the alter state either by force, or restore.
        newcontrol,             // Any time a control occurs which starts a control.  Requires time.
        control,                // Already controlling. Deal with new input information. Requires time.
        repeatcontrol,          // Already controlling, message has been repeated. Requires time.
        seasonReset,            // Controlling or not, a season control reset message arrived for processing.  Requires time.
        dispatchShutdown,       // Dispatch is being shutdown.
    }
    CtiPendingControlUpdateCause_t;

    typedef enum
    {
        noControl,
        controlSentToPorter,
        controlPending,
        controlInProgress,
        controlCompleteCommanded,
        controlCompleteTimedIn,
        controlCompleteManual,
        controlComplete,
        controlSeasonalReset
    }
    CtiPendingControlState_t;

    typedef enum
    {
        pendingInvalid,
        pendingControl,
        pendingPointData,
        pendingLimit
        // Leave space here for each of the limits.
        // ...

    }
    CtiPendingOpType_t;

    void dump() const;

protected:

    LONG        _pid;                // Point ID.

    INT         _pendingType;       // What type of pending operation is this?

    CtiTime      _time;               // time on this pending operation.

    INT         _limitBeingTimed;
    UINT        _limitDuration;      // Number of seconds from _limitTime that we need to be handled.

    INT         _controlState;       // What is this control doing?
    UINT        _controlTimeout;    // Number of seconds from _controlTime that we need to be handled.
    DOUBLE      _controlCompleteValue; // What output value indicates that this control completed successfully?
    DOUBLE      _controlCompleteDeadband; // How close do I need to get to be considered successful?

    CtiTime      _lastHistoryPost;       // Time this was last posted.
    bool        _excludeFromHistory;  //Do not create history logs for this point!

    CtiTableLMControlHistory _control;

    CtiSignalMsg    *_signal;         // Signal to send to the peoples.

    CtiPointDataMsg *_pdata;          // PointData to send to the peoples.

    CtiOffsetPIDMap_t _opidMap;     // Once found this is where we keep point offsets.
    CtiTime _opidNextTime;           // This is the last time the opid search failed.  Use this to keep us from constantly doing deep scans.

private:

    public:
    CtiPendingPointOperations(LONG id = 0, INT type = pendingInvalid);

    CtiPendingPointOperations(const CtiPendingPointOperations& aRef);
    virtual ~CtiPendingPointOperations();

    CtiPendingPointOperations& operator=(const CtiPendingPointOperations& aRef);

    bool operator==(const CtiPendingPointOperations& y) const;
    bool operator()(const CtiPendingPointOperations& y) const;
    bool operator<(const CtiPendingPointOperations& y) const;

    LONG getPointID() const;
    INT getType() const;
    INT getLimitBeingTimed() const;
    CtiTime& getTime();
    CtiTime getTime() const;
    UINT getLimitDuration() const;


    INT getControlState() const;
    UINT getControlTimeout() const;
    DOUBLE getControlCompleteValue() const;
    DOUBLE getControlCompleteDeadband() const;
    bool getExcludeFromHistory() const;

    bool isInControlCompleteState( DOUBLE &value );

    CtiSignalMsg* getSignal();
    const CtiSignalMsg* getSignal() const;
    CtiPointDataMsg* getPointData();
    const CtiPointDataMsg* getPointData() const;


    CtiPendingPointOperations& setPointID( LONG pid );
    CtiPendingPointOperations& setType( INT type );
    CtiPendingPointOperations& setLimitBeingTimed( INT lim );
    CtiPendingPointOperations& setLimitDuration( UINT ld );
    CtiPendingPointOperations& setControlState(INT st);
    CtiPendingPointOperations& setTime(const CtiTime& rt );
    CtiPendingPointOperations& setControlTimeout( UINT ld );
    CtiPendingPointOperations& setSignal( CtiSignalMsg *pSig );
    CtiPendingPointOperations& setControlCompleteValue( const DOUBLE &aDbl );
    CtiPendingPointOperations& setControlCompleteDeadband( const DOUBLE &aDbl );
    CtiPendingPointOperations& setExcludeFromHistory(bool exclude);
    CtiPendingPointOperations& setPointData( CtiPointDataMsg *pDat );

    const CtiTableLMControlHistory& getControl() const;
    CtiTableLMControlHistory& getControl();
    CtiPendingPointOperations& setControl(const CtiTableLMControlHistory& ref);

    CtiOffsetPIDMap_t getOffsetMap() const;
    CtiPendingPointOperations& setOffsetMap(CtiOffsetPIDMap_t &aMap);
    LONG getOffsetsPointID(int offset);
    void addOffset(int offset, long pid);

    CtiTime getLastHistoryPost() const;       // Time this was last posted.
    void setLastHistoryPost(CtiTime rwt);
};
#endif // #ifndef __PENDING_INFO_H__
