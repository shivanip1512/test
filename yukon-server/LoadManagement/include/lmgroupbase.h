#pragma once

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "dbmemobject.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "row_reader.h"
#include "database_connection.h"

class CtiLMProgramThermostatGear;

class CtiLMGroupBase : public CtiMemDBObject
{

public:
    CtiLMGroupBase();
    CtiLMGroupBase(Cti::RowReader &rdr);
    CtiLMGroupBase(const CtiLMGroupBase& groupbase);

    virtual ~CtiLMGroupBase();

    virtual LONG getPAOId() const;
    virtual const std::string& getPAOCategory() const;
    virtual const std::string& getPAOClass() const;
    virtual const std::string& getPAOName() const;
    LONG getPAOType() const;
    const std::string& getPAOTypeString() const;
    virtual const std::string& getPAODescription() const;
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
    virtual const CtiTime& getLastStopTimeSent() const; //Currently only maintained by LMGroupDigiSep

    virtual bool getIsRampingIn() const;
    virtual bool getIsRampingOut() const;

    virtual ULONG getCurrentControlDuration() const;

    virtual LONG getHoursDailyPointId() const;
    virtual LONG getHoursMonthlyPointId() const;
    virtual LONG getHoursSeasonalPointId() const;
    virtual LONG getHoursAnnuallyPointId() const;
    virtual LONG getControlStatusPointId() const;
    virtual const std::string& getLastControlString() const;
    virtual bool readyToControlAt(CtiTime &currentTime) const;

    unsigned getInternalState() const;

    virtual CtiLMGroupBase& setPAOId(LONG id);
    virtual CtiLMGroupBase& setPAOCategory(const std::string& category);
    virtual CtiLMGroupBase& setPAOClass(const std::string& pclass);
    virtual CtiLMGroupBase& setPAOName(const std::string& name);
    virtual CtiLMGroupBase& setPAODescription(const std::string& description);
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
    virtual CtiLMGroupBase& setControlCompleteTime(const CtiTime& complete);
    virtual void            setNextRefreshTime( const CtiTime currentTime, long refreshRate );
    virtual CtiLMGroupBase& setNextControlTime(const CtiTime& controltime);
    virtual CtiLMGroupBase& setLastStopTimeSent(const CtiTime& lastStopTimeSent);  //Currently only maintained by LMGroupDigiSep
    virtual CtiLMGroupBase& setDynamicTimestamp(const CtiTime& timestamp);
    virtual CtiLMGroupBase& incrementDailyOps();
    virtual CtiLMGroupBase& resetDailyOps(int ops = 0);
    virtual void setInternalState(unsigned state);
    virtual CtiLMGroupBase& setControlStartTime(const CtiTime& start);

    virtual CtiLMGroupBase& setIsRampingIn(bool in);
    virtual CtiLMGroupBase& setIsRampingOut(bool out);
    virtual CtiLMGroupBase& resetInternalState();
    virtual CtiLMGroupBase& resetGroupControlState();

    virtual CtiLMGroupBase& setHoursDailyPointId(LONG dailyid);
    virtual CtiLMGroupBase& setHoursMonthlyPointId(LONG monthlyid);
    virtual CtiLMGroupBase& setHoursSeasonalPointId(LONG seasonalid);
    virtual CtiLMGroupBase& setHoursAnnuallyPointId(LONG annuallyid);
    virtual CtiLMGroupBase& setControlStatusPointId(LONG cntid);
    virtual CtiLMGroupBase& setLastControlString(const std::string& controlstr);

    virtual void dumpDynamicData();
    virtual void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    //virtuals but not pure because only one type of group can handle each of the messages
    virtual CtiRequestMsg* createLatchingRequestMsg(bool do_shed, int priority) const;
    virtual CtiCommandMsg* createLatchingCommandMsg(LONG rawState, int priority) const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;// in CtiLMGroupExpresscom
    virtual CtiRequestMsg* createTargetCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority, DOUBLE kwh, CtiTime originalTime, const std::string& additionalInfo) const;// in CtiLMGroupExpresscom

    virtual CtiRequestMsg* createSetPointRequestMsg(const CtiLMProgramThermostatGear & gear, int priority, std::string & logMessage) const;// in CtiLMGroupExpresscom
    virtual CtiRequestMsg* createSetPointSimpleMsg(const CtiLMProgramThermostatGear & gear, LONG totalTime, LONG minutesFromBegin, int priority, std::string & logMessage) const;// CtiLMGroupExpresscom

    virtual CtiRequestMsg* createStopCycleMsg(LONG period, CtiTime &currentTime);
    virtual bool doesStopRequireCommandAt(const CtiTime &currentTime) const; //Currently only used by lmGroupDigiSep
    //pure virtuals
    virtual CtiLMGroupBase* replicate() const = 0;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const = 0;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const = 0;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const = 0;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const = 0;

    CtiRequestMsg * buildRequestMessage( const std::string & request, const int priority ) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(CtiTime currentTime, CtiTime controlEndTime, ULONG offTime);

    virtual void setDirty(BOOL b=TRUE);

    CtiLMGroupBase& operator=(const CtiLMGroupBase& right);
    bool operator<(const CtiLMGroupBase& right) const;

    std::string buildShedString(LONG shedTime) const;
    std::string buildPeriodString(LONG periodTime) const;

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
    std::string _paocategory;
    std::string _paoclass;
    std::string _paoname;
    LONG _paoType;
    std::string _paoTypeString;
    std::string _paodescription;
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

    CtiTime _lastStopTimeSent;

    CtiTime _dynamic_timestamp;

    unsigned  _internalState;

    LONG _hoursdailypointid;
    LONG _hoursmonthlypointid;
    LONG _hoursseasonalpointid;
    LONG _hoursannuallypointid;

    //don't stream
    LONG _controlstatuspointid;
    std::string _lastcontrolstring;
};

typedef boost::shared_ptr<CtiLMGroupBase> CtiLMGroupPtr;
typedef std::vector<CtiLMGroupPtr> CtiLMGroupVec;
typedef CtiLMGroupVec::iterator CtiLMGroupIter;
typedef CtiLMGroupVec::const_iterator CtiLMGroupConstIter;

inline bool operator==( const CtiLMGroupBase & lhs, const CtiLMGroupBase & rhs )
{
    return lhs.getPAOId() == rhs.getPAOId();
}

inline bool operator!=( const CtiLMGroupBase & lhs, const CtiLMGroupBase & rhs )
{
    return ! ( lhs == rhs );
}

