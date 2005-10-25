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

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmprogrambase.h"
#include "observe.h"
#include "lmprogramdirectgear.h"

using std::set;
using std::vector;
using boost::shared_ptr;

class CtiLMProgramDirect : public CtiLMProgramBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramDirect )
    
    CtiLMProgramDirect();
    CtiLMProgramDirect(RWDBReader& rdr);
    CtiLMProgramDirect(const CtiLMProgramDirect& directprog);

    virtual ~CtiLMProgramDirect();

    LONG getNotifyActiveOffset() const;
    LONG getNotifyInactiveOffset() const;
    const string& getMessageSubject() const;
    const string& getMessageHeader() const;
    const string& getMessageFooter() const;
    LONG getTriggerOffset() const;
    LONG getTriggerRestoreOffset() const;
	
    LONG getCurrentGearNumber() const;
    LONG getLastGroupControlled() const;
//    LONG getDailyOps();
    const RWDBDateTime& getDirectStartTime() const;
    const RWDBDateTime& getDirectStopTime() const;
    const RWDBDateTime& getNotifyActiveTime() const;
    const RWDBDateTime& getNotifyInactiveTime() const;
    const RWDBDateTime& getStartedRampingOutTime() const;
    BOOL getConstraintOverride() const;
    
    bool getIsRampingIn();
    bool getIsRampingOut();
    
    RWOrdered& getLMProgramDirectGears();
    CtiLMGroupVec& getLMProgramDirectGroups();
        
    set<CtiLMProgramDirect*>& getMasterPrograms();
    set<CtiLMProgramDirect*>& getSubordinatePrograms();
    
    vector<int>&  getNotificationGroupIDs();

    CtiLMProgramDirect& setMessageSubject(const string& subject);
    CtiLMProgramDirect& setMessageHeader(const string& header);
    CtiLMProgramDirect& setMessageFooter(const string& footer);
    CtiLMProgramDirect& setTriggerOffset(LONG trigger_offst);
    CtiLMProgramDirect& setTriggerRestoreOffset(LONG restore_offst);
    
    CtiLMProgramDirect& setCurrentGearNumber(LONG currentgear);
    CtiLMProgramDirect& setLastGroupControlled(LONG lastcontrolled);

    
    CtiLMProgramDirect& setDirectStartTime(const RWDBDateTime& start);
    CtiLMProgramDirect& setDirectStopTime(const RWDBDateTime& stop);
    CtiLMProgramDirect& setNotifyActiveTime(const RWDBDateTime& notify);    
    CtiLMProgramDirect& setNotifyInactiveTime(const RWDBDateTime& notify);
    CtiLMProgramDirect& setStartedRampingOutTime(const RWDBDateTime& started);
    CtiLMProgramDirect& setConstraintOverride(BOOL override);
    
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    CtiLMGroupPtr findGroupToTake(CtiLMProgramDirectGear* currentGearObject);
    CtiLMGroupPtr findGroupToRampOut(CtiLMProgramDirectGear* currentGearObject);
    
    BOOL maintainProgramControl(LONG currentPriority, RWOrdered& controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isPastMinResponseTime, BOOL isTriggerCheckNeeded);
    BOOL hasGearChanged(LONG currentPriority, RWOrdered controlAreaTriggers, ULONG secondsFrom1901, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded);
    CtiLMProgramDirectGear* getCurrentGearObject();
    DOUBLE updateProgramControlForGearChange(ULONG secondsFrom1901, LONG previousGearNumber, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL refreshStandardProgramControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    DOUBLE manualReduceProgramLoad(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    BOOL doesGroupHaveAmpleControlTime(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const;
    BOOL hasGroupExceededMaxDailyOps(CtiLMGroupPtr& lm_group) const;
    LONG calculateGroupControlTimeLeft(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const;
    BOOL stopOverControlledGroup(CtiLMProgramDirectGear* currentGearObject, CtiLMGroupPtr& currentLMGroup, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    bool updateGroupsRampingOut(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901);

    BOOL notifyGroupsOfStart(CtiMultiMsg* multiNotifMsg);
    BOOL notifyGroupsOfStop(CtiMultiMsg* multiNotifMsg);    

    virtual CtiLMProgramBase* replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded);
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
    
    void scheduleNotification(const RWDBDateTime& start_time, const RWDBDateTime& stop_time);
    void scheduleStartNotification(const RWDBDateTime& start_time);
    void scheduleStopNotification(const RWDBDateTime& stop_time);
	
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

    bool notifyGroups(int type, CtiMultiMsg* multiNotifMsg);
    
    LONG _notify_active_offset;
    LONG _notify_inactive_offset;

    string _message_subject;
    string _message_header;
    string _message_footer;
    LONG _trigger_offset;
    LONG _trigger_restore_offset;
	
    LONG _currentgearnumber;
    LONG _lastgroupcontrolled;

    RWDBDateTime _directstarttime;
    RWDBDateTime _directstoptime;
    RWDBDateTime _notify_active_time;
    RWDBDateTime _notify_inactive_time;

    RWDBDateTime _startedrampingout;
    BOOL _constraint_override;
    
    //When the dynamic data was last saved
    RWDBDateTime  _dynamictimestamp;
    
    RWOrdered _lmprogramdirectgears;
    CtiLMGroupVec  _lmprogramdirectgroups;

    set<CtiLMProgramDirect*> _master_programs;
    set<CtiLMProgramDirect*> _subordinate_programs;
                                     
    vector<int> _notificationgroupids;

    //don't stream/don't save
    BOOL _insertDynamicDataFlag;
    bool _announced_program_constraint_violation; // used for timed schedules so we don't spam the logs with constraint violations
    
    void ResetGroups();
    void ResetGroupsControlState();
    void ResetGroupsInternalState();    
    void RampInGroups(ULONG secondsFrom1901, CtiLMProgramDirectGear* lm_gear = 0);
    double StartMasterCycle(ULONG secondsFrom1901, CtiLMProgramDirectGear* lm_gear);
    
    void restore(RWDBReader& rdr);
};
#endif

