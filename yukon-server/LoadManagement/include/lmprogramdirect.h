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

    LONG getNotifyOffset() const;
    const string& getMessageSubject() const;
    const string& getMessageHeader() const;
    const string& getMessageFooter() const;
    
    LONG getCurrentGearNumber() const;
    LONG getLastGroupControlled() const;
    LONG getDailyOps();
    const RWDBDateTime& getDirectStartTime() const;
    const RWDBDateTime& getDirectStopTime() const;
    const RWDBDateTime& getNotifyTime() const;
    const RWDBDateTime& getStartedRampingOutTime() const;
    
    bool getIsRampingIn() const;
    bool getIsRampingOut() const;
    
    RWOrdered& getLMProgramDirectGears();
    RWOrdered& getLMProgramDirectGroups();
    
    set<int>&  getNotificationGroupIDs();

    CtiLMProgramDirect& setMessageSubject(const string& subject);
    CtiLMProgramDirect& setMessageHeader(const string& header);
    CtiLMProgramDirect& setMessageFooter(const string& footer);
    
    CtiLMProgramDirect& setCurrentGearNumber(LONG currentgear);
    CtiLMProgramDirect& setLastGroupControlled(LONG lastcontrolled);
    CtiLMProgramDirect& incrementDailyOps();
    CtiLMProgramDirect& resetDailyOps();
    CtiLMProgramDirect& setDirectStartTime(const RWDBDateTime& start);
    CtiLMProgramDirect& setDirectStopTime(const RWDBDateTime& stop);
    CtiLMProgramDirect& setNotifyTime(const RWDBDateTime& notify);
    CtiLMProgramDirect& setStartedRampingOutTime(const RWDBDateTime& started);
    
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    CtiLMGroupBase* findGroupToTake(CtiLMProgramDirectGear* currentGearObject);
    
    BOOL maintainProgramControl(LONG currentPriority, RWOrdered& controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL isPastMinResponseTime, BOOL isTriggerCheckNeeded);
    BOOL hasGearChanged(LONG currentPriority, RWOrdered controlAreaTriggers, ULONG secondsFrom1901, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded);
    CtiLMProgramDirectGear* getCurrentGearObject();
    DOUBLE updateProgramControlForGearChange(LONG previousGearNumber, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL refreshStandardProgramControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    DOUBLE manualReduceProgramLoad(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL doesGroupHaveAmpleControlTime(CtiLMGroupBase* currentLMGroup, LONG estimatedControlTimeInSeconds) const;
    LONG calculateGroupControlTimeLeft(CtiLMGroupBase* currentLMGroup, LONG estimatedControlTimeInSeconds) const;
    BOOL stopOverControlledGroup(CtiLMProgramDirectGear* currentGearObject, CtiLMGroupBase* currentLMGroup, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    bool refreshRampOutProgramControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    bool updateGroupsRampingOut(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901);

    BOOL notifyGroupsOfStart(CtiMultiMsg* multiDispatchMsg);

    virtual CtiLMProgramBase* replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded);
    virtual BOOL hasControlHoursAvailable() const;
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901);
    virtual BOOL handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    virtual BOOL isReadyForTimedControl(LONG secondsFromBeginningOfDay);
    virtual BOOL handleTimedControl(ULONG secondsFrom1901, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    virtual bool startTimedProgram(unsigned long secondsFrom1901, long secondsFromBeginningOfDay,CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    virtual BOOL isPastMinRestartTime(ULONG secondsFrom1901);

    double getCurrentLoadReduction();
    ULONG estimateOffTime(ULONG proposed_Gear, ULONG start, ULONG stop);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramDirect& operator=(const CtiLMProgramDirect& right);

    int operator==(const CtiLMProgramDirect& right) const;
    int operator!=(const CtiLMProgramDirect& right) const;

    // Static Members
    static int defaultLMStartPriority;
    static int defaultLMRefreshPriority;

private: 
    LONG _notifyoffset;

    string _message_subject;
    string _message_header;
    string _message_footer;
    
    LONG _currentgearnumber;
    LONG _lastgroupcontrolled;
    LONG _dailyops;
    RWDBDateTime _directstarttime;
    RWDBDateTime _directstoptime;
    RWDBDateTime _notifytime;
    RWDBDateTime _startedrampingout;

    //When the dynamic data was last saved
    RWDBDateTime  _dynamictimestamp;
    
    RWOrdered _lmprogramdirectgears;
    RWOrdered _lmprogramdirectgroups;
    
    set<int> _notificationgroupids;

    //don't stream/don't save
    BOOL _insertDynamicDataFlag;
    bool _announced_constraint_violation; // used for timed schedules so we don't spam the logs with constraint violations
    
    void ResetGroups();
    void RampInGroups(ULONG secondsFrom1901, CtiLMProgramDirectGear* lm_gear = 0);
    double StartMasterCycle(ULONG secondsFrom1901, CtiLMProgramDirectGear* lm_gear);
    
    void restore(RWDBReader& rdr);
};
#endif

