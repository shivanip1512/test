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

#include "observe.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
                
class CtiLMGroupBase : public RWCollectable
{

public:

    CtiLMGroupBase();
    CtiLMGroupBase(RWDBReader& rdr);
    CtiLMGroupBase(const CtiLMGroupBase& groupbase);

    virtual ~CtiLMGroupBase();
    
    ULONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    ULONG getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    ULONG getGroupOrder() const;
    DOUBLE getKWCapacity() const;
    ULONG getChildOrder() const;
    BOOL getAlarmInhibit() const;
    BOOL getControlInhibit() const;
    ULONG getGroupControlState() const;
    ULONG getCurrentHoursDaily() const;
    ULONG getCurrentHoursMonthly() const;
    ULONG getCurrentHoursSeasonal() const;
    ULONG getCurrentHoursAnnually() const;
    const RWDBDateTime& getLastControlSent() const;

    CtiLMGroupBase& setPAOId(ULONG id);
    CtiLMGroupBase& setPAOCategory(const RWCString& category);
    CtiLMGroupBase& setPAOClass(const RWCString& pclass);
    CtiLMGroupBase& setPAOName(const RWCString& name);
    CtiLMGroupBase& setPAOType(ULONG type);
    CtiLMGroupBase& setPAODescription(const RWCString& description);
    CtiLMGroupBase& setDisableFlag(BOOL disable);
    CtiLMGroupBase& setGroupOrder(ULONG order);
    CtiLMGroupBase& setKWCapacity(DOUBLE kwcap);
    CtiLMGroupBase& setChildOrder(ULONG order);
    CtiLMGroupBase& setAlarmInhibit(BOOL alarm);
    CtiLMGroupBase& setControlInhibit(BOOL control);
    CtiLMGroupBase& setGroupControlState(ULONG controlstate);
    CtiLMGroupBase& setCurrentHoursDaily(ULONG daily);
    CtiLMGroupBase& setCurrentHoursMonthly(ULONG monthly);
    CtiLMGroupBase& setCurrentHoursSeasonal(ULONG seasonal);
    CtiLMGroupBase& setCurrentHoursAnnually(ULONG annually);
    CtiLMGroupBase& setLastControlSent(const RWDBDateTime& controlsent);

    virtual void dumpDynamicData();
    virtual CtiLMGroupBase* replicate() const = 0;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime) const = 0;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount) const = 0;
    virtual CtiRequestMsg* createRotationRequestMsg(ULONG sendRate, ULONG shedTime) const = 0;
    virtual CtiCommandMsg* createLatchingRequestMsg(ULONG rawState) const;
    //virtual CtiRequestMsg* createRequestMsg() const = 0;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupBase& operator=(const CtiLMGroupBase& right);

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

    ULONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    ULONG _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    ULONG _grouporder;
    DOUBLE _kwcapacity;
    ULONG _childorder;
    BOOL _alarminhibit;
    BOOL _controlinhibit;
    ULONG _groupcontrolstate;
    ULONG _currenthoursdaily;
    ULONG _currenthoursmonthly;
    ULONG _currenthoursseasonal;
    ULONG _currenthoursannually;
    RWDBDateTime _lastcontrolsent;

    //don't stream
    BOOL _insertDynamicDataFlag;

    mutable RWRecursiveLock<RWMutexLock> _mutex;
};
#endif

