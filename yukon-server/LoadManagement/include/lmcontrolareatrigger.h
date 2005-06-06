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

    LONG getTriggerId() const;
    LONG getPAOId() const;
    LONG getTriggerNumber() const;
    const RWCString& getTriggerType() const;
    LONG getPointId() const;
    DOUBLE getPointValue() const;
    const RWDBDateTime& getLastPointValueTimestamp() const;
    LONG getNormalState() const;
    DOUBLE getThreshold() const;
    const RWCString& getProjectionType() const;
    LONG getProjectionPoints() const;
    LONG getProjectAheadDuration() const;
    LONG getThresholdKickPercent() const;
    DOUBLE getMinRestoreOffset() const;
    LONG getPeakPointId() const;
    DOUBLE getPeakPointValue() const;
    const RWDBDateTime& getLastPeakPointValueTimestamp() const;
    DOUBLE getProjectedPointValue() const;
    RWTValDlist<CtiLMProjectionPointEntry>& getProjectionPointEntriesQueue();

    CtiLMControlAreaTrigger& setTriggerId(LONG trigger_id);
    CtiLMControlAreaTrigger& setPAOId(LONG paoid);
    CtiLMControlAreaTrigger& setTriggerNumber(LONG trignum);
    CtiLMControlAreaTrigger& setTriggerType(const RWCString& trigtype);
    CtiLMControlAreaTrigger& setPointId(LONG pntid);
    CtiLMControlAreaTrigger& setPointValue(DOUBLE pntval);
    CtiLMControlAreaTrigger& setLastPointValueTimestamp(const RWDBDateTime& lastvaltime);
    CtiLMControlAreaTrigger& setNormalState(LONG normalst);
    CtiLMControlAreaTrigger& setThreshold(DOUBLE threshold);
    CtiLMControlAreaTrigger& setProjectionType(const RWCString& projtype);
    CtiLMControlAreaTrigger& setProjectionPoints(LONG projpoints);
    CtiLMControlAreaTrigger& setProjectAheadDuration(LONG projaheaddur);
    CtiLMControlAreaTrigger& setThresholdKickPercent(LONG threskickpercent);
    CtiLMControlAreaTrigger& setMinRestoreOffset(DOUBLE minrestoffset);
    CtiLMControlAreaTrigger& setPeakPointId(LONG peakptid);
    CtiLMControlAreaTrigger& setPeakPointValue(DOUBLE peakptval);
    CtiLMControlAreaTrigger& setLastPeakPointValueTimestamp(const RWDBDateTime& lastpeakvaltime);
    CtiLMControlAreaTrigger& setProjectedPointValue(DOUBLE pntval);

    CtiLMControlAreaTrigger* replicate() const;

    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);
    void calculateProjectedValue();
    
    bool hasReceivedPointData() const;
    
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

    LONG _trigger_id;
    LONG _paoid;
    LONG _triggernumber;
    RWCString _triggertype;
    LONG _pointid;
    DOUBLE _pointvalue;
    RWDBDateTime _lastpointvaluetimestamp;
    LONG _normalstate;
    DOUBLE _threshold;
    RWCString _projectiontype;
    LONG _projectionpoints;
    LONG _projectaheadduration;
    LONG _thresholdkickpercent;
    DOUBLE _minrestoreoffset;
    LONG _peakpointid;
    DOUBLE _peakpointvalue;
    RWDBDateTime _lastpeakpointvaluetimestamp;
    DOUBLE _projectedpointvalue;

    RWTValDlist<CtiLMProjectionPointEntry> _projectionpointentriesqueue;

    //don't stream
    BOOL _insertDynamicDataFlag;

    void restore(RWDBReader& rdr);
};
#endif

