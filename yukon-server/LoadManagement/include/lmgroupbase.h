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
#ifdef _DEBUG_MEMORY    
    static LONG numberOfReferences;
#endif    
    
    CtiLMGroupBase();
    CtiLMGroupBase(RWDBReader& rdr);
    CtiLMGroupBase(const CtiLMGroupBase& groupbase);

    virtual ~CtiLMGroupBase();
    
    virtual LONG getPAOId() const;
    virtual const RWCString& getPAOCategory() const;
    virtual const RWCString& getPAOClass() const;
    virtual const RWCString& getPAOName() const;
    virtual LONG getPAOType() const;
    virtual const RWCString& getPAODescription() const;
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
    virtual const RWDBDateTime& getLastControlSent() const;
    virtual const RWDBDateTime& getControlStartTime() const;
    virtual const RWDBDateTime& getControlCompleteTime() const;
    virtual const RWDBDateTime& getNextControlTime() const; //FIXME

    virtual bool getIsRampingIn() const;
    virtual bool getIsRampingOut() const;
    
    virtual LONG getHoursDailyPointId() const;
    virtual LONG getHoursMonthlyPointId() const;
    virtual LONG getHoursSeasonalPointId() const;
    virtual LONG getHoursAnnuallyPointId() const;
    virtual LONG getControlStatusPointId() const;
    virtual const RWCString& getLastControlString() const;

    virtual CtiLMGroupBase& setPAOId(LONG id);
    virtual CtiLMGroupBase& setPAOCategory(const RWCString& category);
    virtual CtiLMGroupBase& setPAOClass(const RWCString& pclass);
    virtual CtiLMGroupBase& setPAOName(const RWCString& name);
    virtual CtiLMGroupBase& setPAOType(LONG type);
    virtual CtiLMGroupBase& setPAODescription(const RWCString& description);
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
    virtual CtiLMGroupBase& setLastControlSent(const RWDBDateTime& controlsent);
    virtual CtiLMGroupBase& setControlStartTime(const RWDBDateTime& start);
    virtual CtiLMGroupBase& setControlCompleteTime(const RWDBDateTime& complete);
    virtual CtiLMGroupBase& setNextControlTime(const RWDBDateTime& controltime);
    virtual void setInternalState(unsigned state);

    virtual CtiLMGroupBase& setIsRampingIn(bool in);
    virtual CtiLMGroupBase& setIsRampingOut(bool out);
    virtual CtiLMGroupBase& resetInternalState();

    virtual CtiLMGroupBase& setHoursDailyPointId(LONG dailyid);
    virtual CtiLMGroupBase& setHoursMonthlyPointId(LONG monthlyid);
    virtual CtiLMGroupBase& setHoursSeasonalPointId(LONG seasonalid);
    virtual CtiLMGroupBase& setHoursAnnuallyPointId(LONG annuallyid);
    virtual CtiLMGroupBase& setControlStatusPointId(LONG cntid);
    virtual CtiLMGroupBase& setLastControlString(const RWCString& controlstr);

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

    BOOL _insertDynamicDataFlag;
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
    LONG _controlstatuspointid;
    RWCString _lastcontrolstring;

};
#endif

