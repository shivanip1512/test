/*---------------------------------------------------------------------------
        Filename:  lmcontrolareatrigger.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMControlAreaTrigger
                        CtiLMControlAreaTrigger

        Initial Date:  2/6/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMCONTROLAREATRIGGERIMPL_H
#define CTILMCONTROLAREATRIGGERIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <rw/tvdlist.h> 

#include "observe.h"
                
class CtiLMProjectionPointEntry
{//equivalent to an inner class, only used for projections

public:
    CtiLMProjectionPointEntry(double val, const RWTime& time);
    CtiLMProjectionPointEntry(const CtiLMProjectionPointEntry& pointEntry);

    virtual ~CtiLMProjectionPointEntry();

    double getValue() const;
    const RWTime& getTimestamp() const;

    CtiLMProjectionPointEntry& setValue(double val);
    CtiLMProjectionPointEntry& setTimestamp(const RWTime& time);

    CtiLMProjectionPointEntry& operator=(const CtiLMProjectionPointEntry& right);

    //int operator==(const CtiLMProjectionPointEntry& right) const;
    //int operator!=(const CtiLMProjectionPointEntry& right) const;

private:
    DOUBLE _value;
    RWTime _timestamp;
};


class CtiLMControlAreaTrigger : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMControlAreaTrigger )

    CtiLMControlAreaTrigger();
    CtiLMControlAreaTrigger(RWDBReader& rdr);
    CtiLMControlAreaTrigger(const CtiLMControlAreaTrigger& lmcontrolareatrigger);

    virtual ~CtiLMControlAreaTrigger();

    ULONG getPAOId() const;
    ULONG getTriggerNumber() const;
    const RWCString& getTriggerType() const;
    ULONG getPointId() const;
    DOUBLE getPointValue() const;
    const RWDBDateTime& getLastPointValueTimestamp() const;
    ULONG getNormalState() const;
    DOUBLE getThreshold() const;
    const RWCString& getProjectionType() const;
    ULONG getProjectionPoints() const;
    ULONG getProjectAheadDuration() const;
    ULONG getThresholdKickPercent() const;
    DOUBLE getMinRestoreOffset() const;
    ULONG getPeakPointId() const;
    DOUBLE getPeakPointValue() const;
    const RWDBDateTime& getLastPeakPointValueTimestamp() const;
    DOUBLE getProjectedPointValue() const;
    RWTValDlist<CtiLMProjectionPointEntry>& getProjectionPointEntriesQueue();

    CtiLMControlAreaTrigger& setPAOId(ULONG paoid);
    CtiLMControlAreaTrigger& setTriggerNumber(ULONG trignum);
    CtiLMControlAreaTrigger& setTriggerType(const RWCString& trigtype);
    CtiLMControlAreaTrigger& setPointId(ULONG pntid);
    CtiLMControlAreaTrigger& setPointValue(DOUBLE pntval);
    CtiLMControlAreaTrigger& setLastPointValueTimestamp(const RWDBDateTime& lastvaltime);
    CtiLMControlAreaTrigger& setNormalState(ULONG normalst);
    CtiLMControlAreaTrigger& setThreshold(DOUBLE threshold);
    CtiLMControlAreaTrigger& setProjectionType(const RWCString& projtype);
    CtiLMControlAreaTrigger& setProjectionPoints(ULONG projpoints);
    CtiLMControlAreaTrigger& setProjectAheadDuration(ULONG projaheaddur);
    CtiLMControlAreaTrigger& setThresholdKickPercent(ULONG threskickpercent);
    CtiLMControlAreaTrigger& setMinRestoreOffset(DOUBLE minrestoffset);
    CtiLMControlAreaTrigger& setPeakPointId(ULONG peakptid);
    CtiLMControlAreaTrigger& setPeakPointValue(DOUBLE peakptval);
    CtiLMControlAreaTrigger& setLastPeakPointValueTimestamp(const RWDBDateTime& lastpeakvaltime);
    CtiLMControlAreaTrigger& setProjectedPointValue(DOUBLE pntval);

    CtiLMControlAreaTrigger* replicate() const;

    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);
    void calculateProjectedValue();

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMControlAreaTrigger& operator=(const CtiLMControlAreaTrigger& right);

    int operator==(const CtiLMControlAreaTrigger& right) const;
    int operator!=(const CtiLMControlAreaTrigger& right) const;

    /* Static Members */

    //Possible trigger types
    static const RWCString ThresholdTriggerType;
    static const RWCString StatusTriggerType;

    static const RWCString NoneProjectionType;
    static const RWCString LSFProjectionType;

private:
    
    ULONG _paoid;
    ULONG _triggernumber;
    RWCString _triggertype;
    ULONG _pointid;
    DOUBLE _pointvalue;
    RWDBDateTime _lastpointvaluetimestamp;
    ULONG _normalstate;
    DOUBLE _threshold;
    RWCString _projectiontype;
    ULONG _projectionpoints;
    ULONG _projectaheadduration;
    ULONG _thresholdkickpercent;
    DOUBLE _minrestoreoffset;
    ULONG _peakpointid;
    DOUBLE _peakpointvalue;
    RWDBDateTime _lastpeakpointvaluetimestamp;
    DOUBLE _projectedpointvalue;

    RWTValDlist<CtiLMProjectionPointEntry> _projectionpointentriesqueue;

    //don't stream
    BOOL _insertDynamicDataFlag;

    void restore(RWDBReader& rdr);
};
#endif

