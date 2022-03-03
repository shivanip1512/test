#pragma once

#include "ctitime.h"
#include "row_reader.h"
#include "database_connection.h"
#include "collectable.h"

class CtiLMProjectionPointEntry
{//equivalent to an inner class, only used for projections

public:
    CtiLMProjectionPointEntry(double val, const CtiTime& time);
    CtiLMProjectionPointEntry(const CtiLMProjectionPointEntry& pointEntry);

    virtual ~CtiLMProjectionPointEntry();

    double getValue() const;
    const CtiTime& getTimestamp() const;

    CtiLMProjectionPointEntry& setValue(double val);
    CtiLMProjectionPointEntry& setTimestamp(const CtiTime& time);

    CtiLMProjectionPointEntry& operator=(const CtiLMProjectionPointEntry& right);

    //int operator==(const CtiLMProjectionPointEntry& right) const;
    //int operator!=(const CtiLMProjectionPointEntry& right) const;

private:
    DOUBLE _value;
    CtiTime _timestamp;
};


class CtiLMControlAreaTrigger
{

public:

    DECLARE_COLLECTABLE( CtiLMControlAreaTrigger );

    CtiLMControlAreaTrigger();
    CtiLMControlAreaTrigger(Cti::RowReader &rdr);
    CtiLMControlAreaTrigger(const CtiLMControlAreaTrigger& lmcontrolareatrigger);

    virtual ~CtiLMControlAreaTrigger();

    long getThresholdPointId() const;
    LONG getTriggerId() const;
    LONG getPAOId() const;
    LONG getTriggerNumber() const;
    const std::string& getTriggerType() const;
    LONG getPointId() const;
    DOUBLE getPointValue() const;
    const CtiTime& getLastPointValueTimestamp() const;
    LONG getNormalState() const;
    DOUBLE getThreshold() const;
    const std::string& getProjectionType() const;
    LONG getProjectionPoints() const;
    LONG getProjectAheadDuration() const;
    LONG getThresholdKickPercent() const;
    DOUBLE getMinRestoreOffset() const;
    LONG getPeakPointId() const;
    DOUBLE getPeakPointValue() const;
    const CtiTime& getLastPeakPointValueTimestamp() const;
    DOUBLE getProjectedPointValue() const;
    std::vector<CtiLMProjectionPointEntry>& getProjectionPointEntriesQueue();

    CtiLMControlAreaTrigger& setThresholdPointId(const long thresholdId);
    CtiLMControlAreaTrigger& setTriggerId(LONG trigger_id);
    CtiLMControlAreaTrigger& setPAOId(LONG paoid);
    CtiLMControlAreaTrigger& setTriggerNumber(LONG trignum);
    CtiLMControlAreaTrigger& setTriggerType(const std::string& trigtype);
    CtiLMControlAreaTrigger& setPointId(LONG pntid);
    CtiLMControlAreaTrigger& setPointValue(DOUBLE pntval);
    CtiLMControlAreaTrigger& setLastPointValueTimestamp(const CtiTime& lastvaltime);
    CtiLMControlAreaTrigger& setNormalState(LONG normalst);
    CtiLMControlAreaTrigger& setThreshold(DOUBLE threshold);
    CtiLMControlAreaTrigger& setProjectionType(const std::string& projtype);
    CtiLMControlAreaTrigger& setProjectionPoints(LONG projpoints);
    CtiLMControlAreaTrigger& setProjectAheadDuration(LONG projaheaddur);
    CtiLMControlAreaTrigger& setThresholdKickPercent(LONG threskickpercent);
    CtiLMControlAreaTrigger& setMinRestoreOffset(DOUBLE minrestoffset);
    CtiLMControlAreaTrigger& setPeakPointId(LONG peakptid);
    CtiLMControlAreaTrigger& setPeakPointValue(DOUBLE peakptval);
    CtiLMControlAreaTrigger& setLastPeakPointValueTimestamp(const CtiTime& lastpeakvaltime);
    CtiLMControlAreaTrigger& setProjectedPointValue(DOUBLE pntval);

    CtiLMControlAreaTrigger* replicate() const;

    std::size_t getMemoryConsumption() const;

    void dumpDynamicData();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void calculateProjectedValue();

    bool hasReceivedPointData() const;

    CtiLMControlAreaTrigger& operator=(const CtiLMControlAreaTrigger& right);

    int operator==(const CtiLMControlAreaTrigger& right) const;
    int operator!=(const CtiLMControlAreaTrigger& right) const;

    /* Static Members */

    //Possible trigger types
    static const std::string ThresholdTriggerType;
    static const std::string ThresholdPointTriggerType;
    static const std::string StatusTriggerType;

    static const std::string NoneProjectionType;
    static const std::string LSFProjectionType;

private:

    long _thresholdPointId;
    LONG _trigger_id;
    LONG _paoid;
    LONG _triggernumber;
    std::string _triggertype;
    LONG _pointid;
    DOUBLE _pointvalue;
    CtiTime _lastpointvaluetimestamp;
    LONG _normalstate;
    DOUBLE _threshold;
    std::string _projectiontype;
    LONG _projectionpoints;
    LONG _projectaheadduration;
    LONG _thresholdkickpercent;
    DOUBLE _minrestoreoffset;
    LONG _peakpointid;
    DOUBLE _peakpointvalue;
    CtiTime _lastpeakpointvaluetimestamp;
    DOUBLE _projectedpointvalue;

    std::vector<CtiLMProjectionPointEntry> _projectionpointentriesqueue;

    //don't stream
    BOOL _insertDynamicDataFlag;

    void restore(Cti::RowReader &rdr);
};
