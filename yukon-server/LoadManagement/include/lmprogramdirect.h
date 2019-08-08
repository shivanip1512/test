#pragma once

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "lmprogrambase.h"
#include "lmprogramdirectgear.h"
#include "lmcontrolarea.h"
#include "tbl_lmprogramhistory.h"
#include "database_connection.h"

class CtiLMProgramDirect;

typedef boost::shared_ptr< CtiLMProgramDirect > CtiLMProgramDirectSPtr;

class CtiLMProgramDirect : public CtiLMProgramBase
{

public:

DECLARE_COLLECTABLE( CtiLMProgramDirect );

    CtiLMProgramDirect();
    CtiLMProgramDirect(Cti::RowReader &rdr);
    CtiLMProgramDirect(const CtiLMProgramDirect& directprog);

    virtual ~CtiLMProgramDirect();

    LONG getNotifyActiveOffset() const;
    LONG getNotifyInactiveOffset() const;
    const std::string& getMessageSubject() const;
    const std::string& getMessageHeader() const;
    const std::string& getMessageFooter() const;
    const std::string& getAdditionalInfo() const;
    LONG getTriggerOffset() const;
    LONG getTriggerRestoreOffset() const;

    LONG getCurrentGearNumber() const;
    LONG getLastGroupControlled() const;
//    LONG getDailyOps();
    const CtiTime& getDirectStartTime() const;
    const CtiTime& getDirectStopTime() const;
    const CtiTime& getNotifyActiveTime() const;
    const CtiTime& getNotifyInactiveTime() const;
    const CtiTime& getStartedRampingOutTime() const;
    BOOL getConstraintOverride() const;
    bool isAdjustNotificationPending() const;
    bool shouldNotifyWhenScheduled() const;

    bool getIsRampingIn();
    bool getIsRampingOut();

    const std::vector<CtiLMProgramDirectGear*>& getLMProgramDirectGears() const;

    void addGear(CtiLMProgramDirectGear * gear);

    CtiLMGroupVec& getLMProgramDirectGroups();
    const CtiLMGroupVec& getLMProgramDirectGroups() const;

    std::set<CtiLMProgramDirectSPtr>& getMasterPrograms();
    const std::set<CtiLMProgramDirectSPtr>& getMasterPrograms() const;

    std::set<CtiLMProgramDirectSPtr>& getSubordinatePrograms();
    const std::set<CtiLMProgramDirectSPtr>& getSubordinatePrograms() const;

    std::vector<int>&  getNotificationGroupIDs();

    CtiLMProgramDirect& setMessageSubject(const std::string& subject);
    CtiLMProgramDirect& setMessageHeader(const std::string& header);
    CtiLMProgramDirect& setMessageFooter(const std::string& footer);
    CtiLMProgramDirect& setAdditionalInfo(const std::string& additional);
    CtiLMProgramDirect& setTriggerOffset(LONG trigger_offst);
    CtiLMProgramDirect& setTriggerRestoreOffset(LONG restore_offst);

    CtiLMProgramDirect& setCurrentGearNumber(LONG currentgear);
    CtiLMProgramDirect& setLastGroupControlled(LONG lastcontrolled);


    CtiLMProgramDirect& setDirectStartTime(const CtiTime& start);
    CtiLMProgramDirect& setDirectStopTime(const CtiTime& stop);
    CtiLMProgramDirect& setNotifyActiveTime(const CtiTime& notify);
    CtiLMProgramDirect& setNotifyInactiveTime(const CtiTime& notify);
    CtiLMProgramDirect& setStartedRampingOutTime(const CtiTime& started);
    CtiLMProgramDirect& setConstraintOverride(BOOL override);
    CtiLMProgramDirect& setControlActivatedByStatusTrigger(BOOL flag);

    virtual CtiLMProgramBase& setProgramState(LONG newState);

    void dumpDynamicData();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    CtiLMGroupPtr findGroupToTake(CtiLMProgramDirectGear* currentGearObject);
    CtiLMGroupPtr findGroupToRampOut(CtiLMProgramDirectGear* currentGearObject);

    BOOL maintainProgramControl(LONG currentPriority, std::vector<CtiLMControlAreaTrigger*>& controlAreaTriggers, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isPastMinResponseTime, BOOL isTriggerCheckNeeded);
    BOOL hasGearChanged(LONG currentPriority,         std::vector<CtiLMControlAreaTrigger*> controlAreaTriggers, CtiTime currentTime, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded);
    CtiLMProgramDirectGear* getCurrentGearObject();
    DOUBLE updateProgramControlForAutomaticGearChange(CtiTime currentTime, LONG previousGearNumber, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL refreshStandardProgramControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    DOUBLE manualReduceProgramLoad(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    bool doesGroupHaveAmpleControlTime(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const;
    BOOL hasGroupExceededMaxDailyOps(CtiLMGroupPtr& lm_group) const;
    LONG calculateGroupControlTimeLeft(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const;
    BOOL stopOverControlledGroup(CtiLMProgramDirectGear* currentGearObject, CtiLMGroupPtr& currentLMGroup, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    bool updateGroupsRampingOut(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiTime currentTime);
    bool isControlling();

    BOOL notifyGroupsOfStart(CtiMultiMsg* multiNotifMsg);
    BOOL notifyGroupsOfStop(CtiMultiMsg* multiNotifMsg);
    bool notifyGroupsOfAdjustment(CtiMultiMsg* multiNotifMsg);
    bool notifyGroupsOfSchedule(const CtiTime &start, const CtiTime &stop);
    BOOL wasControlActivatedByStatusTrigger();

    void requestAdjustNotification(const CtiTime& stop_time);

    virtual CtiLMProgramBaseSPtr replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, std::vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded);
    virtual BOOL hasControlHoursAvailable();
    virtual bool stopSubordinatePrograms(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime);
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime);
    virtual BOOL handleManualControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    virtual BOOL isReadyForTimedControl(LONG secondsFromBeginningOfDay);
    virtual BOOL handleTimedControl(CtiTime currentTime, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    virtual bool startTimedProgram(CtiTime currentTime, long secondsFromBeginningOfDay,CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    virtual BOOL isPastMinRestartTime(CtiTime currentTime);

    double getCurrentLoadReduction();
    ULONG estimateOffTime(ULONG proposed_Gear, CtiTime start_time, CtiTime stop_time);

    void startGroupControl(CtiLMGroupPtr& lm_group, CtiRequestMsg* req, CtiMultiMsg* multiPilMsg);
    void refreshGroupControl(CtiLMGroupPtr& lm_group, CtiRequestMsg* req, CtiMultiMsg* multiPilMsg);
    bool restoreGroup(CtiTime currentTime, CtiLMGroupPtr& lm_group, CtiMultiMsg* multiPilMsg, unsigned repeatCount);
    bool stopCycleGroup(CtiTime currentTime, CtiLMGroupPtr& lm_group, CtiMultiMsg* multiPilMsg, LONG period, unsigned repeatCount);

    void scheduleNotification(const CtiTime& start_time, const CtiTime& stop_time);
    void scheduleNotificationForTimedControl(const CtiTime& start_time, const CtiTime& stop_time);
    void scheduleStartNotification(const CtiTime& start_time);
    void scheduleStopNotification(const CtiTime& stop_time);
    void scheduleStopNotificationForTimedControl(const CtiTime& stop_time);

    virtual void setLastUser(const std::string& user);
    virtual void setChangeReason(const std::string& reason);

    void setOrigin(const std::string& origin) override;
    std::string getOrigin() const;

    virtual bool getHasBeatThePeakGear() const;

    CtiLMProgramDirect& operator=(const CtiLMProgramDirect& right);

    int operator==(const CtiLMProgramDirect& right) const;
    int operator!=(const CtiLMProgramDirect& right) const;

    // Static Members
    static int defaultLMStartPriority;
    static int defaultLMRefreshPriority;

    static int invalidNotifyOffset;

private:

    typedef CtiLMProgramBase Inherited;
    bool notifyGroups(int type, CtiMultiMsg* multiNotifMsg);

    LONG _notify_active_offset;
    LONG _notify_inactive_offset;
    bool _adjustment_notification_enabled;
    bool _notify_when_scheduled;

    std::string _message_subject;
    std::string _message_header;
    std::string _message_footer;
    std::string _additionalinfo;
    std::string _last_user;
    std::string _change_reason;
    std::string _origin;

    LONG _trigger_offset;
    LONG _trigger_restore_offset;

    LONG _currentgearnumber;
    LONG _lastgroupcontrolled;

    CtiTime _directstarttime;
    CtiTime _directstoptime;
    CtiTime _notify_active_time;
    CtiTime _notify_inactive_time;
    bool    _adjustment_notification_pending;

    CtiTime _startedrampingout;
    BOOL _constraint_override;

    BOOL _controlActivatedByStatusTrigger;

    bool _hasBeatThePeakGear;

    //When the dynamic data was last saved
    CtiTime  _dynamictimestamp;

    std::vector<CtiLMProgramDirectGear*> _lmprogramdirectgears;
    CtiLMGroupVec  _lmprogramdirectgroups;

    std::set<CtiLMProgramDirectSPtr> _master_programs;
    std::set<CtiLMProgramDirectSPtr> _subordinate_programs;

    std::vector<int> _notificationgroupids;

    unsigned long _curLogID;

    bool _controlStarted;  // this is to try to log a start vs a restart/update

    //don't stream/don't save
    BOOL _insertDynamicDataFlag;
    bool _announced_program_constraint_violation; // used for timed schedules so we don't spam the logs with constraint violations

    void ResetGroups();
    void ResetGroupsControlState();
    void ResetGroupsInternalState();
    void RampInGroups(CtiTime currentTime, CtiLMProgramDirectGear* lm_gear = 0);
    void updateStandardControlActiveState(LONG numberOfActiveGroups);
    double StartMasterCycle(CtiTime currentTime, CtiLMProgramDirectGear* lm_gear);
    bool sendSimpleThermostatMessage(CtiLMProgramDirectGear* currentGearObject, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, double &expectedLoadReduced, bool isRefresh);
    bool recordHistory(CtiTableLMProgramHistory::LMHistoryActions action, CtiTime &time);
    void scheduleMessageForResend(CtiTime currentTime, const CtiRequestMsg & message, short count, CtiLMGroupPtr & lm_group);
    void cancelScheduledStopsForGroup(CtiLMGroupPtr & lm_group);
    bool isAControlState(int state);
    bool isAStopState(int state);
    unsigned long getCurrentHistLogId();
    void setCurrentHistLogId(unsigned long logID);
    void setAdjustNotificationPending(bool adjustNeedsToBeSent);
    std::string getAndClearChangeReason();
    std::string getLastUser();
    virtual void setHasBeatThePeakGear(bool hasBeatThePeakGear);

    void setPendingGroupsInactive();

    void restore(Cti::RowReader &rdr);
};
