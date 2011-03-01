/*---------------------------------------------------------------------------
        Filename:  controlarea.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMControlArea
                        CtiLMControlArea maintains the state and handles
                        the persistence of control areas for Load Management.

        Initial Date:  2/1/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMCONTROLAREAIMPL_H
#define CTILMCONTROLAREAIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "dbmemobject.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "lmprogrambase.h"
#include "lmcontrolareatrigger.h"
#include "msg_pcrequest.h"
#include "row_reader.h"
#include "database_connection.h"

class CtiLMControlArea : public CtiMemDBObject, public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMControlArea )

    static LONG numberOfReferences;

    CtiLMControlArea();
    CtiLMControlArea(Cti::RowReader &rdr);
    CtiLMControlArea(const CtiLMControlArea& controlarea);

    virtual ~CtiLMControlArea();
    virtual void setDirty(BOOL b = TRUE);

    LONG getPAOId() const;
    const string& getPAOCategory() const;
    const string& getPAOClass() const;
    const string& getPAOName() const;
    LONG getPAOType() const;
    const string& getPAODescription() const;
    BOOL getDisableFlag() const;
    const string& getDefOperationalState() const;
    LONG getControlInterval() const;
    LONG getMinResponseTime() const;
    CtiTime getDefDailyStartTime(const CtiDate &defaultDate = CtiDate::now()) const;
    CtiTime getDefDailyStopTime(const CtiDate &defaultDate = CtiDate::now()) const;
    BOOL getRequireAllTriggersActiveFlag() const;
    const CtiTime& getNextCheckTime() const;
    BOOL getNewPointDataReceivedFlag() const;
    BOOL getUpdatedFlag() const;
    LONG getControlAreaStatusPointId() const;
    LONG getControlAreaState() const;
    LONG getCurrentStartPriority() const;
    int getCurrentStopPriority();
    LONG getCurrentStartSecondsFromDayBegin() const;
    LONG getCurrentStopSecondsFromDayBegin() const;
    CtiTime getCurrentDailyStartTime(const CtiDate &defaultDate = CtiDate::now()) const;
    CtiTime getCurrentDailyStopTime(const CtiDate &defaultDate = CtiDate::now()) const;
    vector<CtiLMControlAreaTrigger*>& getLMControlAreaTriggers();
    CtiLMControlAreaTrigger* getThresholdTrigger() const;
    CtiLMControlAreaTrigger* getThresholdPointTrigger() const;
    vector<CtiLMProgramBaseSPtr>& getLMPrograms();

    CtiLMControlArea& setPAOId(LONG id);
    CtiLMControlArea& setPAOCategory(const string& category);
    CtiLMControlArea& setPAOClass(const string& pclass);
    CtiLMControlArea& setPAOName(const string& name);
    CtiLMControlArea& setPAOType(LONG type);
    CtiLMControlArea& setPAODescription(const string& description);
    CtiLMControlArea& setDisableFlag(BOOL disable);
    CtiLMControlArea& setDefOperationalState(const string& opstate);
    CtiLMControlArea& setControlInterval(LONG interval);
    CtiLMControlArea& setMinResponseTime(LONG response);
    CtiLMControlArea& setDefDailyStartTime(LONG start);
    CtiLMControlArea& setDefDailyStopTime(LONG stop);
    CtiLMControlArea& setRequireAllTriggersActiveFlag(BOOL requireall);
    CtiLMControlArea& figureNextCheckTime(ULONG secondsFrom1901);
    CtiLMControlArea& setNewPointDataReceivedFlag(BOOL newdatareceived);
    CtiLMControlArea& setUpdatedFlag(BOOL updated);
    CtiLMControlArea& setControlAreaStatusPointId(LONG statuspointid);
    CtiLMControlArea& setControlAreaState(LONG state);
    CtiLMControlArea& setCurrentStartPriority(LONG currpriority);
    CtiLMControlArea& setCurrentDailyStartTime(LONG tempstart);
    CtiLMControlArea& setCurrentDailyStopTime(LONG tempstop);
    CtiLMControlArea& resetCurrentDailyStartTime();
    CtiLMControlArea& resetCurrentDailyStopTime();

    BOOL isTriggerCheckNeeded(ULONG secondsFrom1901);
    BOOL isControlTime(LONG secondsFromBeginningOfDay);
    BOOL isControlStillNeeded();
    BOOL isPastMinResponseTime(ULONG secondsFrom1901);
    BOOL isManualControlReceived();
    void clearManualControlReceivedFlags();
    BOOL isThresholdTriggerTripped(CtiLMProgramBaseSPtr program = CtiLMProgramBaseSPtr());
    BOOL hasThresholdTrigger();
    BOOL hasStatusTrigger();
    BOOL isStatusTriggerTripped(CtiLMProgramBaseSPtr program = CtiLMProgramBaseSPtr());
    
    DOUBLE calculateLoadReductionNeeded();
    double calculateExpectedLoadIncrease(int stop_priority);
    bool shouldReduceControl();
        
    DOUBLE reduceControlAreaLoad(DOUBLE loadReductionNeeded, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    void reduceControlAreaControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    DOUBLE takeAllAvailableControlAreaLoad(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    void manuallyStartAllProgramsNow(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    void manuallyStopAllProgramsNow(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, bool forceAll);    
    BOOL stopProgramsBelowThreshold(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    BOOL maintainCurrentControl(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL examinedControlAreaForControlNeededFlag);
    BOOL stopAllControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, ULONG secondsFrom1901);
    void handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    void handleTimeBasedControl(ULONG secondsFrom1901, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);
    void handleNotification(ULONG secondsFrom1901, CtiMultiMsg* multiNotifMsg);
    void createControlStatusPointUpdates(CtiMultiMsg* multiDispatchMsg);
    void updateTimedPrograms(LONG secondsFromBeginningOfDay);
    
    void dumpDynamicData();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMControlArea& operator=(const CtiLMControlArea& right);

    int operator==(const CtiLMControlArea& right) const;
    int operator!=(const CtiLMControlArea& right) const;

    CtiLMControlArea* replicate() const;

    // Possible def operational states
    static const string DefOpStateEnabled;
    static const string DefOpStateDisabled;
    static const string DefOpStateNone;

    // Possible control area states
    static int InactiveState;
    static int ActiveState;
    static int ManualActiveState;
    static int ScheduledState;
    static int FullyActiveState;
    static int AttemptingControlState;

private:
    
    LONG _paoid;
    string _paocategory;
    string _paoclass;
    string _paoname;
    LONG _paotype;
    string _paodescription;
    BOOL _disableflag;
    string _defoperationalstate;
    LONG _controlinterval;
    LONG _minresponsetime;
    LONG _defdailystarttime;
    LONG _defdailystoptime;
    BOOL _requirealltriggersactiveflag;
    CtiTime _nextchecktime;
    BOOL _newpointdatareceivedflag;
    BOOL _updatedflag;
    LONG _controlareastatuspointid;
    LONG _controlareastate;
    LONG _currentpriority;
    LONG _currentdailystarttime;
    LONG _currentdailystoptime;

    vector<CtiLMControlAreaTrigger*> _lmcontrolareatriggers;
    vector<CtiLMProgramBaseSPtr> _lmprograms;

    //don't stream
    BOOL _insertDynamicDataFlag;

    void restore(Cti::RowReader &rdr);
    string* getAutomaticallyStartedSignalString();
};
#endif
