/*---------------------------------------------------------------------------
        Filename:  lmprogrambase.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMProgramBase
                        CtiLMProgramBase 

        Initial Date:  2/2/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMPROGRAMIMPL_H
#define CTILMPROGRAMIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "msg_multi.h"
#include "lmgroupbase.h"

class CtiLMProgramBase : public RWCollectable
{

public:

    static ULONG numberOfReferences;

    CtiLMProgramBase();
    CtiLMProgramBase(RWDBReader& rdr);
    CtiLMProgramBase(const CtiLMProgramBase& lmprog);

    virtual ~CtiLMProgramBase();

    ULONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    ULONG getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    ULONG getUserOrder() const;
    ULONG getStopOrder() const;
    ULONG getDefaultPriority() const;
    const RWCString& getControlType() const;
    const RWCString& getAvailableSeasons() const;
    const RWCString& getAvailableWeekDays() const;
    ULONG getMaxHoursDaily() const;
    ULONG getMaxHoursMonthly() const;
    ULONG getMaxHoursSeasonal() const;
    ULONG getMaxHoursAnnually() const;
    ULONG getMinActivateTime() const;
    ULONG getMinRestartTime() const;
    ULONG getProgramStatusPointId() const;
    ULONG getProgramState() const;
    ULONG getReductionAnalogPointId() const;
    DOUBLE getReductionTotal() const;
    const RWDBDateTime& getStartedControlling() const;
    const RWDBDateTime& getLastControlSent() const;
    BOOL getManualControlReceivedFlag() const;
    RWOrdered& getLMProgramControlWindows();

    CtiLMProgramBase& setPAOId(ULONG id);
    CtiLMProgramBase& setPAOCategory(const RWCString& category);
    CtiLMProgramBase& setPAOClass(const RWCString& pclass);
    CtiLMProgramBase& setPAOName(const RWCString& name);
    CtiLMProgramBase& setPAOType(ULONG type);
    CtiLMProgramBase& setPAODescription(const RWCString& description);
    CtiLMProgramBase& setDisableFlag(BOOL disable);
    CtiLMProgramBase& setUserOrder(ULONG userorder);
    CtiLMProgramBase& setStopOrder(ULONG stoporder);
    CtiLMProgramBase& setDefaultPriority(ULONG defpriority);
    CtiLMProgramBase& setControlType(const RWCString& conttype);
    CtiLMProgramBase& setAvailableSeasons(const RWCString& availseasons);
    CtiLMProgramBase& setAvailableWeekDays(const RWCString& availweekdays);
    CtiLMProgramBase& setMaxHoursDaily(ULONG daily);
    CtiLMProgramBase& setMaxHoursMonthly(ULONG monthly);
    CtiLMProgramBase& setMaxHoursSeasonal(ULONG seasonal);
    CtiLMProgramBase& setMaxHoursAnnually(ULONG annually);
    CtiLMProgramBase& setMinActivateTime(ULONG activate);
    CtiLMProgramBase& setMinRestartTime(ULONG restart);
    CtiLMProgramBase& setProgramStatusPointId(ULONG statuspointid);
    CtiLMProgramBase& setProgramState(ULONG progstate);
    CtiLMProgramBase& setReductionAnalogPointId(ULONG reductionpointid);
    CtiLMProgramBase& setStartedControlling(const RWDBDateTime& startcont);
    CtiLMProgramBase& setLastControlSent(const RWDBDateTime& lastcontrol);
    CtiLMProgramBase& setReductionTotal(DOUBLE reduction);
    CtiLMProgramBase& setManualControlReceivedFlag(BOOL manualreceived);

    BOOL isAvailableToday();
    BOOL isWithinValidControlWindow(ULONG secondsFromBeginningOfDay);
    void dumpDynamicData();

    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, ULONG currentPriority, RWOrdered controlAreaTriggers, ULONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg) = 0;
    virtual CtiLMProgramBase* replicate() const = 0;
    virtual BOOL hasControlHoursAvailable() const = 0;
    virtual void stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg) = 0;
    virtual BOOL handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg) = 0;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramBase& operator=(const CtiLMProgramBase& right);

    int operator==(const CtiLMProgramBase& right) const;
    int operator!=(const CtiLMProgramBase& right) const;

    // Static Members

    // Possible control types
    static const RWCString AutomaticType;
    static const RWCString ManualOnlyType;

    // Possible program states
    static int InactiveState;
    static int ActiveState;
    static int ManualActiveState;
    static int ScheduledState;
    static int NotifiedState;
    static int FullyActiveState;
    static int StoppingState;

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
    ULONG _userorder;
    ULONG _stoporder;
    ULONG _defaultpriority;
    RWCString _controltype;
    RWCString _availableseasons;
    RWCString _availableweekdays;
    ULONG _maxhoursdaily;
    ULONG _maxhoursmonthly;
    ULONG _maxhoursseasonal;
    ULONG _maxhoursannually;
    ULONG _minactivatetime;
    ULONG _minrestarttime;
    ULONG _programstatuspointid;
    ULONG _programstate;
    ULONG _reductionanalogpointid;
    DOUBLE _reductiontotal;
    RWDBDateTime _startedcontrolling;
    RWDBDateTime _lastcontrolsent;
    BOOL _manualcontrolreceivedflag;

    RWOrdered _lmprogramcontrolwindows;

    //don't stream
    BOOL _insertDynamicDataFlag;

    mutable RWRecursiveLock<RWMutexLock> _mutex;
};
#endif

