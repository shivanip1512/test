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

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "dbmemobject.h"
#include "observe.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"

class CtiLMGroupBase : public CtiMemDBObject, public RWCollectable
{

public:

    CtiLMGroupBase();
    CtiLMGroupBase(RWDBReader& rdr);
    CtiLMGroupBase(const CtiLMGroupBase& groupbase);

    virtual ~CtiLMGroupBase();
    
    LONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    LONG getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getGroupOrder() const;
    DOUBLE getKWCapacity() const;
    LONG getChildOrder() const;
    BOOL getAlarmInhibit() const;
    BOOL getControlInhibit() const;
    LONG getGroupControlState() const;
    LONG getCurrentHoursDaily() const;
    LONG getCurrentHoursMonthly() const;
    LONG getCurrentHoursSeasonal() const;
    LONG getCurrentHoursAnnually() const;
    const RWDBDateTime& getLastControlSent() const;
    const RWDBDateTime& getControlStartTime() const;
    const RWDBDateTime& getControlCompleteTime() const;
    const RWDBDateTime& getNextControlTime() const; //FIXME

    bool getIsRampingIn() const;
    bool getIsRampingOut() const;
    
    LONG getHoursDailyPointId() const;
    LONG getHoursMonthlyPointId() const;
    LONG getHoursSeasonalPointId() const;
    LONG getHoursAnnuallyPointId() const;
    LONG getLMProgramId() const;
    LONG getControlStatusPointId() const;
    const RWCString& getLastControlString() const;

    CtiLMGroupBase& setPAOId(LONG id);
    CtiLMGroupBase& setPAOCategory(const RWCString& category);
    CtiLMGroupBase& setPAOClass(const RWCString& pclass);
    CtiLMGroupBase& setPAOName(const RWCString& name);
    CtiLMGroupBase& setPAOType(LONG type);
    CtiLMGroupBase& setPAODescription(const RWCString& description);
    CtiLMGroupBase& setDisableFlag(BOOL disable);
    CtiLMGroupBase& setGroupOrder(LONG order);
    CtiLMGroupBase& setKWCapacity(DOUBLE kwcap);
    CtiLMGroupBase& setChildOrder(LONG order);
    CtiLMGroupBase& setAlarmInhibit(BOOL alarm);
    CtiLMGroupBase& setControlInhibit(BOOL control);
    CtiLMGroupBase& setGroupControlState(LONG controlstate);
    CtiLMGroupBase& setCurrentHoursDaily(LONG daily);
    CtiLMGroupBase& setCurrentHoursMonthly(LONG monthly);
    CtiLMGroupBase& setCurrentHoursSeasonal(LONG seasonal);
    CtiLMGroupBase& setCurrentHoursAnnually(LONG annually);
    CtiLMGroupBase& setLastControlSent(const RWDBDateTime& controlsent);
    CtiLMGroupBase& setControlStartTime(const RWDBDateTime& start);
    CtiLMGroupBase& setControlCompleteTime(const RWDBDateTime& complete);
    CtiLMGroupBase& setNextControlTime(const RWDBDateTime& controltime);
    
    CtiLMGroupBase& setIsRampingIn(bool in);
    CtiLMGroupBase& setIsRampingOut(bool out);
    CtiLMGroupBase& resetInternalState();
    
    CtiLMGroupBase& setHoursDailyPointId(LONG dailyid);
    CtiLMGroupBase& setHoursMonthlyPointId(LONG monthlyid);
    CtiLMGroupBase& setHoursSeasonalPointId(LONG seasonalid);
    CtiLMGroupBase& setHoursAnnuallyPointId(LONG annuallyid);
    CtiLMGroupBase& setLMProgramId(LONG progid);
    CtiLMGroupBase& setControlStatusPointId(LONG cntid);
    CtiLMGroupBase& setLastControlString(const RWCString& controlstr);

    virtual void dumpDynamicData();
    virtual void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    //virtuals but not pure because only one type of group can handle each of the messages
    virtual CtiCommandMsg* createLatchingRequestMsg(LONG rawState, int priority) const;// in CtiLMGroupPoint
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;// in CtiLMGroupExpresscom
    virtual CtiRequestMsg* createSetPointRequestMsg(RWCString settings, LONG minValue, LONG maxValue,
                                                    LONG valueB, LONG valueD, LONG valueF, LONG random,
                                                    LONG valueTA, LONG valueTB, LONG valueTC, LONG valueTD,
                                                    LONG valueTE, LONG valueTF, int priority) const;// in CtiLMGroupExpresscom

    //pure virtuals
    virtual CtiLMGroupBase* replicate() const = 0;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const = 0;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const = 0;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const = 0;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const = 0;
    //virtual CtiRequestMsg* createRequestMsg() const = 0;
    //pure virtuals

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupBase& operator=(const CtiLMGroupBase& right);
    bool operator<(const CtiLMGroupBase& right) const;
    
    RWCString buildShedString(LONG shedTime) const;
    RWCString buildPeriodString(LONG periodTime) const;
    
    int operator==(const CtiLMGroupBase& right) const;
    int operator!=(const CtiLMGroupBase& right) const;

    // Static Members

    // Possible group control states
    static int InactiveState;
    static int ActiveState;
    static int InactivePendingState;
    static int ActivePendingState;

protected:

    void restore(RWDBReader& rdr);

private:

    LONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    LONG _paotype;
    RWCString _paodescription;
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
    RWDBDateTime _lastcontrolsent;
    RWDBDateTime _controlstarttime;
    RWDBDateTime _controlcompletetime;
//FIXME    
    RWDBDateTime _next_control_time;
    unsigned  _internalState;

    LONG _hoursdailypointid;
    LONG _hoursmonthlypointid;
    LONG _hoursseasonalpointid;
    LONG _hoursannuallypointid;

    //don't stream
    LONG _lmprogramid;
    LONG _controlstatuspointid;
    RWCString _lastcontrolstring;
    BOOL _insertDynamicDataFlag;
};
#endif

