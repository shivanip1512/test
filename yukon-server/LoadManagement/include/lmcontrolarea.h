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
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "connection.h"
#include "types.h"
#include "observe.h"
#include "lmprogrambase.h"
#include "msg_pcrequest.h"

class CtiLMControlArea : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMControlArea )

    static LONG numberOfReferences;

    CtiLMControlArea();
    CtiLMControlArea(RWDBReader& rdr);
    CtiLMControlArea(const CtiLMControlArea& controlarea);

    virtual ~CtiLMControlArea();

    LONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    LONG getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    const RWCString& getDefOperationalState() const;
    LONG getControlInterval() const;
    LONG getMinResponseTime() const;
    LONG getDefDailyStartTime() const;
    LONG getDefDailyStopTime() const;
    BOOL getRequireAllTriggersActiveFlag() const;
    const RWDBDateTime& getNextCheckTime() const;
    BOOL getNewPointDataReceivedFlag() const;
    BOOL getUpdatedFlag() const;
    LONG getControlAreaStatusPointId() const;
    LONG getControlAreaState() const;
    LONG getCurrentPriority() const;
    LONG getCurrentDailyStartTime() const;
    LONG getCurrentDailyStopTime() const;
    RWOrdered& getLMControlAreaTriggers();
    RWOrdered& getLMPrograms();

    CtiLMControlArea& setPAOId(LONG id);
    CtiLMControlArea& setPAOCategory(const RWCString& category);
    CtiLMControlArea& setPAOClass(const RWCString& pclass);
    CtiLMControlArea& setPAOName(const RWCString& name);
    CtiLMControlArea& setPAOType(LONG type);
    CtiLMControlArea& setPAODescription(const RWCString& description);
    CtiLMControlArea& setDisableFlag(BOOL disable);
    CtiLMControlArea& setDefOperationalState(const RWCString& opstate);
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
    CtiLMControlArea& setCurrentPriority(LONG currpriority);
    CtiLMControlArea& setCurrentDailyStartTime(LONG tempstart);
    CtiLMControlArea& setCurrentDailyStopTime(LONG tempstop);

    BOOL isTriggerCheckNeeded(ULONG secondsFrom1901);
    BOOL isControlTime(LONG secondsFromBeginningOfDay);
    BOOL isControlStillNeeded();
    BOOL isPastMinResponseTime(ULONG secondsFrom1901);
    BOOL isManualControlReceived();
    BOOL isThresholdTriggerTripped();
    DOUBLE calculateLoadReductionNeeded();
    DOUBLE reduceControlAreaLoad(DOUBLE loadReductionNeeded, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    DOUBLE takeAllAvailableControlAreaLoad(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL maintainCurrentControl(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL examinedControlAreaForControlNeededFlag);
    BOOL stopAllControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901);
    void handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    void handleTimeBasedControl(ULONG secondsFrom1901, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    void createControlStatusPointUpdates(CtiMultiMsg* multiDispatchMsg);

    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMControlArea& operator=(const CtiLMControlArea& right);

    int operator==(const CtiLMControlArea& right) const;
    int operator!=(const CtiLMControlArea& right) const;

    CtiLMControlArea* replicate() const;

    // Possible def operational states
    static const RWCString DefOpStateEnabled;
    static const RWCString DefOpStateDisabled;
    static const RWCString DefOpStateNone;

    // Possible control area states
    static int InactiveState;
    static int ActiveState;
    static int ManualActiveState;
    static int ScheduledState;
    static int FullyActiveState;
    static int AttemptingControlState;

private:
    
    LONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    LONG _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    RWCString _defoperationalstate;
    LONG _controlinterval;
    LONG _minresponsetime;
    LONG _defdailystarttime;
    LONG _defdailystoptime;
    BOOL _requirealltriggersactiveflag;
    RWDBDateTime _nextchecktime;
    BOOL _newpointdatareceivedflag;
    BOOL _updatedflag;
    LONG _controlareastatuspointid;
    LONG _controlareastate;
    LONG _currentpriority;
    LONG _currentdailystarttime;
    LONG _currentdailystoptime;

    RWOrdered _lmcontrolareatriggers;
    RWOrdered _lmprograms;

    //don't stream
    BOOL _insertDynamicDataFlag;

    void restore(RWDBReader& rdr);
    RWCString* getAutomaticallyStartedSignalString();
};
#endif
