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

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmprogrambase.h"
#include "observe.h"
#include "lmprogramdirectgear.h"

class CtiLMProgramDirect : public CtiLMProgramBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramDirect )

    CtiLMProgramDirect();
    CtiLMProgramDirect(RWDBReader& rdr);
    CtiLMProgramDirect(const CtiLMProgramDirect& directprog);

    virtual ~CtiLMProgramDirect();

    ULONG getCurrentGearNumber() const;
    ULONG getLastGroupControlled() const;
    const RWDBDateTime& getDirectStartTime() const;
    const RWDBDateTime& getDirectStopTime() const;
    RWOrdered& getLMProgramDirectGears();
    RWOrdered& getLMProgramDirectGroups();

    CtiLMProgramDirect& setCurrentGearNumber(ULONG currentgear);
    CtiLMProgramDirect& setLastGroupControlled(ULONG lastcontrolled);
    CtiLMProgramDirect& setDirectStartTime(const RWDBDateTime& start);
    CtiLMProgramDirect& setDirectStopTime(const RWDBDateTime& stop);

    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    CtiLMGroupBase* findGroupToTake(CtiLMProgramDirectGear* currentGearObject);
    void restoreDirectSpecificDatabaseEntries(RWDBReader& rdr);
    BOOL maintainProgramControl(ULONG currentPriority, RWOrdered& controlAreaTriggers, ULONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL hasGearChanged(ULONG currentPriority, RWOrdered controlAreaTriggers, ULONG secondsFrom1901);
    CtiLMProgramDirectGear* getCurrentGearObject();
    DOUBLE updateProgramControlForGearChange(ULONG previousGearNumber, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL refreshStandardProgramControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    DOUBLE manualReduceProgramLoad(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    BOOL doesGroupHaveAmpleControlTime(CtiLMGroupBase* currentLMGroup, ULONG estimatedControlTimeInSeconds) const;
    ULONG calculateGroupControlTimeLeft(CtiLMGroupBase* currentLMGroup, ULONG estimatedControlTimeInSeconds) const;

    virtual CtiLMProgramBase* replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, ULONG currentPriority, RWOrdered controlAreaTriggers, ULONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    virtual BOOL hasControlHoursAvailable() const;
    virtual void stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);
    virtual BOOL handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

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

    ULONG _currentgearnumber;
    ULONG _lastgroupcontrolled;
    RWDBDateTime _directstarttime;
    RWDBDateTime _directstoptime;

    RWOrdered _lmprogramdirectgears;
    RWOrdered _lmprogramdirectgroups;

    //don't stream
    BOOL _insertDynamicDataFlag;

    void restore(RWDBReader& rdr);
};
#endif

