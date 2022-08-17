#pragma once

#include "dbmemobject.h"
#include "msg_multi.h"
#include "lmgroupbase.h"
#include "lmcontrolareatrigger.h"
#include "ctidate.h"
#include "row_reader.h"
#include "database_connection.h"

class CtiLMProgramControlWindow;
class CtiLMControlArea;

class CtiLMProgramBase;

typedef boost::shared_ptr< CtiLMProgramBase > CtiLMProgramBaseSPtr;

class CtiLMProgramBase : public CtiMemDBObject
{

public:

#ifdef _DEBUG_MEMORY
    static LONG numberOfReferences;
#endif
    CtiLMProgramBase();
    CtiLMProgramBase(Cti::RowReader &rdr);
    CtiLMProgramBase(const CtiLMProgramBase& lmprog);

    virtual ~CtiLMProgramBase();

    LONG getPAOId() const;
    const std::string& getPAOCategory() const;
    const std::string& getPAOClass() const;
    const std::string& getPAOName() const;
    LONG getPAOType() const;
    const std::string& getPAOTypeString() const;
    const std::string& getPAODescription() const;
    BOOL getDisableFlag() const;
    int getStartPriority() const;
    int getStopPriority() const;
    const std::string& getControlType() const;
    LONG getConstraintID() const;
    const std::string& getConstraintName() const;
    const std::string& getAvailableWeekDays() const;
    LONG getMaxHoursDaily() const;
    LONG getMaxHoursMonthly() const;
    LONG getMaxHoursSeasonal() const;
    LONG getMaxHoursAnnually() const;
    LONG getMinActivateTime() const;
    LONG getMinRestartTime() const;
    LONG getMaxDailyOps() const;
    LONG getMaxActivateTime() const;
    LONG getHolidayScheduleId() const;
    LONG getSeasonScheduleId() const;
    LONG getProgramStatusPointId() const;
    LONG getProgramState() const;
    LONG getReductionAnalogPointId() const;
    DOUBLE getReductionTotal() const;
    const CtiTime& getStartedControlling() const;
    const CtiTime& getLastControlSent() const;
    BOOL getManualControlReceivedFlag() const;
    std::vector<CtiLMProgramControlWindow*>& getLMProgramControlWindows();
    const std::vector<CtiLMProgramControlWindow*>& getLMProgramControlWindows() const;

    CtiLMProgramBase& setPAOId(LONG id);
    CtiLMProgramBase& setPAOCategory(const std::string& category);
    CtiLMProgramBase& setPAOClass(const std::string& pclass);
    CtiLMProgramBase& setPAOName(const std::string& name);
    CtiLMProgramBase& setPAODescription(const std::string& description);
    CtiLMProgramBase& setDisableFlag(BOOL disable);
    CtiLMProgramBase& setStartPriority(int start_priority);
    CtiLMProgramBase& setStopPriority(int stop_priority);
    CtiLMProgramBase& setControlType(const std::string& conttype);
    CtiLMProgramBase& setConstraintID(LONG constraintid);
    CtiLMProgramBase& setConstraintName(const std::string& constraintname);
    CtiLMProgramBase& setAvailableWeekDays(const std::string& availweekdays);
    CtiLMProgramBase& setMaxHoursDaily(LONG daily);
    CtiLMProgramBase& setMaxHoursMonthly(LONG monthly);
    CtiLMProgramBase& setMaxHoursSeasonal(LONG seasonal);
    CtiLMProgramBase& setMaxHoursAnnually(LONG annually);
    CtiLMProgramBase& setMinActivateTime(LONG activate);
    CtiLMProgramBase& setMinRestartTime(LONG restart);
    CtiLMProgramBase& setMaxDailyOps(LONG max_ops);
    CtiLMProgramBase& setMaxActivateTime(LONG max_active_time);
    CtiLMProgramBase& setHolidayScheduleId(LONG schdid);
    CtiLMProgramBase& setSeasonScheduleId(LONG schdid);
    CtiLMProgramBase& setProgramStatusPointId(LONG statuspointid);
    CtiLMProgramBase& setReductionAnalogPointId(LONG reductionpointid);
    CtiLMProgramBase& setStartedControlling(const CtiTime& startcont);
    CtiLMProgramBase& setLastControlSent(const CtiTime& lastcontrol);
    CtiLMProgramBase& setReductionTotal(DOUBLE reduction);
    CtiLMProgramBase& setManualControlReceivedFlag(BOOL manualreceived);
    CtiLMControlArea* getControlArea();
    virtual CtiLMProgramBase& setProgramState(LONG progstate);

    CtiLMProgramBase& setPaoTypeString           ( const std::string& typeString );
    CtiLMProgramBase& setLMProgramControlWindows ( const std::vector<CtiLMProgramControlWindow*>& lmProgramControlWindows );
    CtiLMProgramBase& setPaoType                 ( const LONG paoType );

    BOOL isAvailableToday();
    BOOL isWithinValidControlWindow(LONG secondsFromBeginningOfDay);

    virtual void dumpDynamicData();
    virtual void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void createControlStatusPointUpdates(CtiMultiMsg* multiDispatchMsg);
    void setControlArea(CtiLMControlArea *controlArea);

    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, std::vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded) = 0;
    virtual CtiLMProgramBaseSPtr replicate() const = 0;

    virtual BOOL hasControlHoursAvailable() = 0;
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime) = 0;
    virtual BOOL handleManualControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg) = 0;
    virtual BOOL isReadyForTimedControl(LONG secondsFromBeginningOfDay);
    virtual BOOL handleTimedControl(CtiTime currentTime, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    virtual BOOL isPastMinRestartTime(CtiTime currentTime);
    virtual CtiLMProgramControlWindow* getControlWindow(LONG secondsFromBeginningOfDay, CtiDate &defaultDate = CtiDate::now());
    virtual CtiLMProgramControlWindow* getNextControlWindow(LONG secondsFromBeginningOfDay);

    virtual void setDirty(BOOL b=TRUE);
    virtual void setChangeReason(const std::string& reason);
    virtual void setLastUser(const std::string& user);
    virtual void setOrigin(const std::string& origin);

    CtiLMProgramBase& operator=(const CtiLMProgramBase& right);

    int operator==(const CtiLMProgramBase& right) const;
    int operator!=(const CtiLMProgramBase& right) const;

    // Static Members

    // Possible control types
    static const std::string AutomaticType;
    static const std::string ManualOnlyType;
    static const std::string TimedType;

    // Possible program states
    static const int InactiveState;
    static const int ActiveState;
    static const int ManualActiveState;
    static const int ScheduledState;
    static const int NotifiedState;
    static const int FullyActiveState;
    static const int StoppingState;
    static const int GearChangeState;
    static const int NonControllingState;
    static const int TimedActiveState;

protected:


    void restore(Cti::RowReader &rdr);

private:

    LONG _paoid;
    std::string _paocategory;
    std::string _paoclass;
    std::string _paoname;
    LONG _paoType;
    std::string _paoTypeString;
    std::string _paodescription;
    BOOL _disableflag;
    int _start_priority;
    int _stop_priority;
    std::string _controltype;
    LONG _constraintid;
    std::string _constraintname;
    std::string _availableweekdays;
    LONG _maxhoursdaily;
    LONG _maxhoursmonthly;
    LONG _maxhoursseasonal;
    LONG _maxhoursannually;
    LONG _minactivatetime;
    LONG _minrestarttime;
    LONG _maxdailyops;
    LONG _maxactivatetime;
    LONG _holidayscheduleid;
    LONG _seasonscheduleid;
    LONG _programstatuspointid;
    LONG _programstate;
    LONG _reductionanalogpointid;
    DOUBLE _reductiontotal;
    CtiTime _startedcontrolling;
    CtiTime _lastcontrolsent;
    BOOL _manualcontrolreceivedflag;
    LONG _lastsentstate;
    CtiLMControlArea *_controlArea;

    std::vector<CtiLMProgramControlWindow*> _lmprogramcontrolwindows;

    //don't stream
    BOOL _insertDynamicDataFlag;
};

