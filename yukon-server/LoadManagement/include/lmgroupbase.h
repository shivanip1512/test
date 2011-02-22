/*---------------------------------------------------------------------------
        Filename:  lmgroupbase.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMGroupBase
                        CtiLMGroupBase

        Initial Date:  2/5/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPBASEIMPL_H
#define CTILMGROUPBASEIMPL_H

#include <vector>
#include <string>

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "dbmemobject.h"
#include "observe.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "row_reader.h"
#include "database_connection.h"

using std::vector;
using std::string;
using boost::shared_ptr;


class CtiLMGroupBase : public RWCollectable, public CtiMemDBObject 
{

public:
    CtiLMGroupBase();
    CtiLMGroupBase(Cti::RowReader &rdr);
    CtiLMGroupBase(const CtiLMGroupBase& groupbase);

    virtual ~CtiLMGroupBase();
    
    virtual LONG getPAOId() const;
    virtual const string& getPAOCategory() const;
    virtual const string& getPAOClass() const;
    virtual const string& getPAOName() const;
    virtual LONG getPAOType() const;
    virtual const string& getPAODescription() const;
    virtual BOOL getDisableFlag() const;
    virtual LONG getGroupOrder() const;
    virtual DOUBLE getKWCapacity() const;
    virtual LONG getChildOrder() const;
    virtual BOOL getAlarmInhibit() const;
    virtual BOOL getControlInhibit() const;
    virtual LONG getGroupControlState() const;
    virtual LONG getCurrentHoursDaily() const;
    virtual LONG getCurrentHoursMonthly() const;
    virtual LONG getCurrentHoursSeasonal() const;
    virtual LONG getCurrentHoursAnnually() const;
    virtual const CtiTime& getLastControlSent() const;
    virtual const CtiTime& getControlStartTime() const;
    virtual const CtiTime& getControlCompleteTime() const;
    virtual const CtiTime& getNextControlTime() const; //FIXME
    virtual const CtiTime& getDynamicTimestamp() const;
    virtual LONG getDailyOps();
    
    virtual bool getIsRampingIn() const;
    virtual bool getIsRampingOut() const;

    virtual ULONG getCurrentControlDuration() const;
    
    virtual LONG getHoursDailyPointId() const;
    virtual LONG getHoursMonthlyPointId() const;
    virtual LONG getHoursSeasonalPointId() const;
    virtual LONG getHoursAnnuallyPointId() const;
    virtual LONG getControlStatusPointId() const;
    virtual const string& getLastControlString() const;

    virtual CtiLMGroupBase& setPAOId(LONG id);
    virtual CtiLMGroupBase& setPAOCategory(const string& category);
    virtual CtiLMGroupBase& setPAOClass(const string& pclass);
    virtual CtiLMGroupBase& setPAOName(const string& name);
    virtual CtiLMGroupBase& setPAOType(LONG type);
    virtual CtiLMGroupBase& setPAODescription(const string& description);
    virtual CtiLMGroupBase& setDisableFlag(BOOL disable);
    virtual CtiLMGroupBase& setGroupOrder(LONG order);
    virtual CtiLMGroupBase& setKWCapacity(DOUBLE kwcap);
    virtual CtiLMGroupBase& setChildOrder(LONG order);
    virtual CtiLMGroupBase& setAlarmInhibit(BOOL alarm);
    virtual CtiLMGroupBase& setControlInhibit(BOOL control);
    virtual CtiLMGroupBase& setGroupControlState(LONG controlstate);
    virtual CtiLMGroupBase& setCurrentHoursDaily(LONG daily);
    virtual CtiLMGroupBase& setCurrentHoursMonthly(LONG monthly);
    virtual CtiLMGroupBase& setCurrentHoursSeasonal(LONG seasonal);
    virtual CtiLMGroupBase& setCurrentHoursAnnually(LONG annually);
    virtual CtiLMGroupBase& setLastControlSent(const CtiTime& controlsent);
    virtual CtiLMGroupBase& setControlStartTime(const CtiTime& start);
    virtual CtiLMGroupBase& setControlCompleteTime(const CtiTime& complete);
    virtual CtiLMGroupBase& setNextControlTime(const CtiTime& controltime);
    virtual CtiLMGroupBase& setDynamicTimestamp(const CtiTime& timestamp);
    virtual CtiLMGroupBase& incrementDailyOps();
    virtual CtiLMGroupBase& resetDailyOps(int ops = 0);
    virtual void setInternalState(unsigned state);

    virtual CtiLMGroupBase& setIsRampingIn(bool in);
    virtual CtiLMGroupBase& setIsRampingOut(bool out);
    virtual CtiLMGroupBase& resetInternalState();
    virtual CtiLMGroupBase& resetGroupControlState();

    virtual CtiLMGroupBase& setHoursDailyPointId(LONG dailyid);
    virtual CtiLMGroupBase& setHoursMonthlyPointId(LONG monthlyid);
    virtual CtiLMGroupBase& setHoursSeasonalPointId(LONG seasonalid);
    virtual CtiLMGroupBase& setHoursAnnuallyPointId(LONG annuallyid);
    virtual CtiLMGroupBase& setControlStatusPointId(LONG cntid);
    virtual CtiLMGroupBase& setLastControlString(const string& controlstr);

    virtual void dumpDynamicData();
    virtual void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    //virtuals but not pure because only one type of group can handle each of the messages
    virtual CtiRequestMsg* createLatchingRequestMsg(bool do_shed, int priority) const;
    virtual CtiCommandMsg* createLatchingCommandMsg(LONG rawState, int priority) const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;// in CtiLMGroupExpresscom
    virtual CtiRequestMsg* createTargetCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority, DOUBLE kwh, CtiTime originalTime, const string& additionalInfo) const;// in CtiLMGroupExpresscom
    virtual CtiRequestMsg* createSetPointRequestMsg(string settings, LONG minValue, LONG maxValue,
                                                    LONG valueB, LONG valueD, LONG valueF, LONG random,
                                                    LONG valueTA, LONG valueTB, LONG valueTC, LONG valueTD,
                                                    LONG valueTE, LONG valueTF, int priority) const;// in CtiLMGroupExpresscom
    virtual CtiRequestMsg* createSetPointSimpleMsg(string settings, LONG minValue, LONG maxValue,
                                                    LONG precoolTemp, LONG random, float rampRate,
                                                    LONG precoolTime, LONG precoolHoldTime, LONG maxTempChange,
                                                    LONG totalTime, LONG rampOutTime, LONG minutesFromBegin,
                                                    int priority) const;// CtiLMGroupExpresscom
    virtual bool sendSEPCycleControl(long controlMinutes, long cyclePercent, bool isTrueCycle, bool randomizeStart, bool randomizeStop);
    virtual bool sendStopControl(bool stopImmediatelly);

    //pure virtuals
    virtual CtiLMGroupBase* replicate() const = 0;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const = 0;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const = 0;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const = 0;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const = 0;

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);

    virtual void setDirty(BOOL b=TRUE);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupBase& operator=(const CtiLMGroupBase& right);
    bool operator<(const CtiLMGroupBase& right) const;
    
    string buildShedString(LONG shedTime) const;
    string buildPeriodString(LONG periodTime) const;
    
    int operator==(const CtiLMGroupBase& right) const;
    int operator!=(const CtiLMGroupBase& right) const;

    // Static Members

    // Possible group control states
    static int InactiveState;
    static int ActiveState;
    static int InactivePendingState;
    static int ActivePendingState;

    BOOL _insertDynamicDataFlag;
protected:

    void restore(Cti::RowReader &rdr);

private:

    void updateDailyOps();
    
    LONG _paoid;
    string _paocategory;
    string _paoclass;
    string _paoname;
    LONG _paotype;
    string _paodescription;
    BOOL _disableflag;
    LONG _grouporder;
    DOUBLE _kwcapacity;
    LONG _childorder;
    BOOL _alarminhibit;
    BOOL _controlinhibit;
    
    LONG _groupcontrolstate;
    LONG _currenthoursdaily;
    LONG _currenthoursmonthly;
    LONG _currenthoursseasonal;
    LONG _currenthoursannually;
    CtiTime _lastcontrolsent;
    CtiTime _controlstarttime;
    CtiTime _controlcompletetime;
   
    CtiTime _next_control_time;
    LONG _daily_ops;
    
    CtiTime _dynamic_timestamp;
    
    unsigned  _internalState;

    LONG _hoursdailypointid;
    LONG _hoursmonthlypointid;
    LONG _hoursseasonalpointid;
    LONG _hoursannuallypointid;

    //don't stream
    LONG _controlstatuspointid;
    string _lastcontrolstring;

};

typedef shared_ptr<CtiLMGroupBase> CtiLMGroupPtr;
typedef vector<CtiLMGroupPtr> CtiLMGroupVec;
typedef CtiLMGroupVec::iterator CtiLMGroupIter;
typedef CtiLMGroupVec::const_iterator CtiLMGroupConstIter;

#endif

