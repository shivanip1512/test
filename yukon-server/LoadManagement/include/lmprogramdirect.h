/*---------------------------------------------------------------------------
        Filename:  lmprogramdirect.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMProgramDirect
                        CtiLMProgramDirect 

        Initial Date:  2/2/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMPROGRAMDIRECTIMPL_H
#define CTILMPROGRAMDIRECTIMPL_H

#include <vector>
#include <set>

#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmprogrambase.h"
#include "observe.h"
#include "lmprogramdirectgear.h"
#include "lmcontrolarea.h"
#include "tbl_lmprogramhistory.h"
#include "database_connection.h"

using std::set;
using std::vector;
using boost::shared_ptr;
class CtiLMProgramDirect;

#if VSLICK_TAG_WORKAROUND
typedef CtiLMProgramDirect * CtiLMProgramDirectSPtr;
#else
typedef shared_ptr< CtiLMProgramDirect > CtiLMProgramDirectSPtr;
#endif

class CtiLMProgramDirect : public CtiLMProgramBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramDirect )
    
    CtiLMProgramDirect();
    CtiLMProgramDirect(Cti::RowReader &rdr);
    CtiLMProgramDirect(const CtiLMProgramDirect& directprog);

    virtual ~CtiLMProgramDirect();

    LONG getNotifyActiveOffset() const;
    LONG getNotifyInactiveOffset() const;
    const string& getMessageSubject() const;
    const string& getMessageHeader() const;
    const string& getMessageFooter() const;
    const string& getAdditionalInfo() const;
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
    
    bool getIsRampingIn();
    bool getIsRampingOut();
    
    vector<CtiLMProgramDirectGear*>& getLMProgramDirectGears();
    CtiLMGroupVec& getLMProgramDirectGroups();
        
    set<CtiLMProgramDirectSPtr>& getMasterPrograms();
    set<CtiLMProgramDirectSPtr>& getSubordinatePrograms();
    
    vector<int>&  getNotificationGroupIDs();

    CtiLMProgramDirect& setMessageSubject(const string& subject);
    CtiLMProgramDirect& setMessageHeader(const string& header);
    CtiLMProgramDirect& setMessageFooter(const string& footer);
    CtiLMProgramDirect& setAdditionalInfo(const string& additional);
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
    
    BOOL maintainProgramControl(LONG currentPriority, vector<CtiLMControlAreaTrigger*>& controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isPastMinResponseTime, BOOL isTriggerCheckNeeded);
    BOOL hasGearChanged(LONG currentPriority,         vector<CtiLMControlAreaTrigger*> controlAreaTriggers, ULONG secondsFrom1901, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded);
    CtiLMProgramDirectGear* getCurrentGearObject();
    DOUBLE updateProgramControlForGearChange(ULONG secondsFrom1901, LONG previousGearNumber, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL refreshStandardProgramControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    DOUBLE manualReduceProgramLoad(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    BOOL doesGroupHaveAmpleControlTime(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const;
    BOOL hasGroupExceededMaxDailyOps(CtiLMGroupPtr& lm_group) const;
    LONG calculateGroupControlTimeLeft(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const;
    BOOL stopOverControlledGroup(CtiLMProgramDirectGear* currentGearObject, CtiLMGroupPtr& currentLMGroup, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    bool updateGroupsRampingOut(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901);
    bool isControlling();

    BOOL notifyGroupsOfStart(CtiMultiMsg* multiNotifMsg);
    BOOL notifyGroupsOfStop(CtiMultiMsg* multiNotifMsg);    
    BOOL wasControlActivatedByStatusTrigger();   

    virtual CtiLMProgramBaseSPtr replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded);
    virtual BOOL hasControlHoursAvailable();
    virtual bool stopSubordinatePrograms(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, ULONG secondsFrom1901);
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, ULONG secondsFrom1901);    
    virtual BOOL handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    virtual BOOL isReadyForTimedControl(LONG secondsFromBeginningOfDay);
    virtual BOOL handleTimedControl(ULONG secondsFrom1901, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    virtual bool startTimedProgram(unsigned long secondsFrom1901, long secondsFromBeginningOfDay,CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    virtual BOOL isPastMinRestartTime(ULONG secondsFrom1901);

    double getCurrentLoadReduction();
    ULONG estimateOffTime(ULONG proposed_Gear, ULONG start, ULONG stop);

    void startGroupControl(CtiLMGroupPtr& lm_group, CtiRequestMsg* req, CtiMultiMsg* multiPilMsg);
    void refreshGroupControl(CtiLMGroupPtr& lm_group, CtiRequestMsg* req, CtiMultiMsg* multiPilMsg);    
    bool restoreGroup(ULONG seconds_from_1901, CtiLMGroupPtr& lm_group, CtiMultiMsg* multiPilMsg);
    bool terminateGroup(ULONG seconds_from_1901, CtiLMGroupPtr& lm_group, CtiMultiMsg* multiPilMsg);
    
    void scheduleNotification(const CtiTime& start_time, const CtiTime& stop_time);
    void scheduleStartNotification(const CtiTime& start_time);
    void scheduleStopNotification(const CtiTime& stop_time);

    virtual void setLastUser(const string& user);
    virtual void setChangeReason(const string& reason);
	
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

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

    string _message_subject;
    string _message_header;
    string _message_footer;
    string _additionalinfo;
    string _last_user;
    string _change_reason;
    LONG _trigger_offset;
    LONG _trigger_restore_offset;
	
    LONG _currentgearnumber;
    LONG _lastgroupcontrolled;

    CtiTime _directstarttime;
    CtiTime _directstoptime;
    CtiTime _notify_active_time;
    CtiTime _notify_inactive_time;

    CtiTime _startedrampingout;
    BOOL _constraint_override;
    
    BOOL _controlActivatedByStatusTrigger;
    
    //When the dynamic data was last saved
    CtiTime  _dynamictimestamp;
    
    vector<CtiLMProgramDirectGear*> _lmprogramdirectgears;
    CtiLMGroupVec  _lmprogramdirectgroups;

    set<CtiLMProgramDirectSPtr> _master_programs;
    set<CtiLMProgramDirectSPtr> _subordinate_programs;
                                     
    vector<int> _notificationgroupids;

    unsigned long _curLogID;

    //don't stream/don't save
    BOOL _insertDynamicDataFlag;
    bool _announced_program_constraint_violation; // used for timed schedules so we don't spam the logs with constraint violations
    
    void ResetGroups();
    void ResetGroupsControlState();
    void ResetGroupsInternalState();    
    void RampInGroups(ULONG secondsFrom1901, CtiLMProgramDirectGear* lm_gear = 0);
    void updateStandardControlActiveState(LONG numberOfActiveGroups);
    double StartMasterCycle(ULONG secondsFrom1901, CtiLMProgramDirectGear* lm_gear);
    bool sendSimpleThermostatMessage(CtiLMProgramDirectGear* currentGearObject, LONG secondsFrom1901, CtiMultiMsg* multiPilMsg, double &expectedLoadReduced, bool isRefresh);
    bool recordHistory(CtiTableLMProgramHistory::LMHistoryActions action, CtiTime &time);
    bool isAControlState(int state);
    bool isAStopState(int state);
    unsigned long getCurrentHistLogId();
    void setCurrentHistLogId(unsigned long logID);
    string getAndClearChangeReason();
    string getLastUser();
    
    void restore(Cti::RowReader &rdr);
};
#endif

