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

    static LONG numberOfReferences;

    CtiLMProgramBase();
    CtiLMProgramBase(RWDBReader& rdr);
    CtiLMProgramBase(const CtiLMProgramBase& lmprog);

    virtual ~CtiLMProgramBase();

    LONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    LONG getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getUserOrder() const;
    LONG getStopOrder() const;
    LONG getDefaultPriority() const;
    const RWCString& getControlType() const;
    const RWCString& getAvailableSeasons() const;
    const RWCString& getAvailableWeekDays() const;
    LONG getMaxHoursDaily() const;
    LONG getMaxHoursMonthly() const;
    LONG getMaxHoursSeasonal() const;
    LONG getMaxHoursAnnually() const;
    LONG getMinActivateTime() const;
    LONG getMinRestartTime() const;
    LONG getHolidayScheduleId() const;
    LONG getSeasonScheduleId() const;
    LONG getProgramStatusPointId() const;
    LONG getProgramState() const;
    LONG getReductionAnalogPointId() const;
    DOUBLE getReductionTotal() const;
    const RWDBDateTime& getStartedControlling() const;
    const RWDBDateTime& getLastControlSent() const;
    BOOL getManualControlReceivedFlag() const;
    RWOrdered& getLMProgramControlWindows();

    CtiLMProgramBase& setPAOId(LONG id);
    CtiLMProgramBase& setPAOCategory(const RWCString& category);
    CtiLMProgramBase& setPAOClass(const RWCString& pclass);
    CtiLMProgramBase& setPAOName(const RWCString& name);
    CtiLMProgramBase& setPAOType(LONG type);
    CtiLMProgramBase& setPAODescription(const RWCString& description);
    CtiLMProgramBase& setDisableFlag(BOOL disable);
    CtiLMProgramBase& setUserOrder(LONG userorder);
    CtiLMProgramBase& setStopOrder(LONG stoporder);
    CtiLMProgramBase& setDefaultPriority(LONG defpriority);
    CtiLMProgramBase& setControlType(const RWCString& conttype);
    CtiLMProgramBase& setAvailableSeasons(const RWCString& availseasons);
    CtiLMProgramBase& setAvailableWeekDays(const RWCString& availweekdays);
    CtiLMProgramBase& setMaxHoursDaily(LONG daily);
    CtiLMProgramBase& setMaxHoursMonthly(LONG monthly);
    CtiLMProgramBase& setMaxHoursSeasonal(LONG seasonal);
    CtiLMProgramBase& setMaxHoursAnnually(LONG annually);
    CtiLMProgramBase& setMinActivateTime(LONG activate);
    CtiLMProgramBase& setMinRestartTime(LONG restart);
    CtiLMProgramBase& setHolidayScheduleId(LONG schdid);
    CtiLMProgramBase& setSeasonScheduleId(LONG schdid);
    CtiLMProgramBase& setProgramStatusPointId(LONG statuspointid);
    CtiLMProgramBase& setProgramState(LONG progstate);
    CtiLMProgramBase& setReductionAnalogPointId(LONG reductionpointid);
    CtiLMProgramBase& setStartedControlling(const RWDBDateTime& startcont);
    CtiLMProgramBase& setLastControlSent(const RWDBDateTime& lastcontrol);
    CtiLMProgramBase& setReductionTotal(DOUBLE reduction);
    CtiLMProgramBase& setManualControlReceivedFlag(BOOL manualreceived);

    BOOL isAvailableToday();
    BOOL isWithinValidControlWindow(LONG secondsFromBeginningOfDay);
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);
    void createControlStatusPointUpdates(CtiMultiMsg* multiDispatchMsg);

    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded) = 0;
    virtual CtiLMProgramBase* replicate() const = 0;
    virtual BOOL hasControlHoursAvailable() const = 0;
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901) = 0;
    virtual BOOL handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg) = 0;
    virtual BOOL isPastMinRestartTime(ULONG secondsFrom1901);

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
    static int AttemptingControlState;

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
    LONG _userorder;
    LONG _stoporder;
    LONG _defaultpriority;
    RWCString _controltype;
    RWCString _availableseasons;
    RWCString _availableweekdays;
    LONG _maxhoursdaily;
    LONG _maxhoursmonthly;
    LONG _maxhoursseasonal;
    LONG _maxhoursannually;
    LONG _minactivatetime;
    LONG _minrestarttime;
    LONG _holidayscheduleid;
    LONG _seasonscheduleid;
    LONG _programstatuspointid;
    LONG _programstate;
    LONG _reductionanalogpointid;
    DOUBLE _reductiontotal;
    RWDBDateTime _startedcontrolling;
    RWDBDateTime _lastcontrolsent;
    BOOL _manualcontrolreceivedflag;

    RWOrdered _lmprogramcontrolwindows;

    //don't stream
    BOOL _insertDynamicDataFlag;
};
#endif

