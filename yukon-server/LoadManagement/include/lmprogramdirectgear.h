/*---------------------------------------------------------------------------
        Filename:  lmprogramdirectgear.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMProgramDirectGear
                        CtiLMProgramDirectGear

        Initial Date:  2/5/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMPROGRAMDIRECTGEARIMPL_H
#define CTILMPROGRAMDIRECTGEARIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
                
class CtiLMProgramDirectGear : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramDirectGear )

    CtiLMProgramDirectGear();
    CtiLMProgramDirectGear(RWDBReader& rdr);
    CtiLMProgramDirectGear(const CtiLMProgramDirectGear& proggear);

    virtual ~CtiLMProgramDirectGear();
    
    ULONG getPAOId() const;
    const RWCString& getGearName() const;
    ULONG getGearNumber() const;
    const RWCString& getControlMethod() const;
    ULONG getMethodRate() const;
    ULONG getMethodPeriod() const;
    ULONG getMethodRateCount() const;
    ULONG getCycleRefreshRate() const;
    const RWCString& getMethodStopType() const;
    const RWCString& getChangeCondition() const;
    ULONG getChangeDuration() const;
    ULONG getChangePriority() const;
    ULONG getChangeTriggerNumber() const;
    DOUBLE getChangeTriggerOffset() const;
    ULONG getPercentReduction() const;
    const RWCString& getGroupSelectionMethod() const;
    const RWCString& getMethodOptionType() const;
    ULONG getMethodOptionMax() const;

    CtiLMProgramDirectGear& setPAOId(ULONG paoid);
    CtiLMProgramDirectGear& setGearName(const RWCString& name);
    CtiLMProgramDirectGear& setGearNumber(ULONG gearnum);
    CtiLMProgramDirectGear& setControlMethod(const RWCString& contmeth);
    CtiLMProgramDirectGear& setMethodRate(ULONG methrate);
    CtiLMProgramDirectGear& setMethodPeriod(ULONG methper);
    CtiLMProgramDirectGear& setMethodRateCount(ULONG methratecount);
    CtiLMProgramDirectGear& setCycleRefreshRate(ULONG cyclerefresh);
    CtiLMProgramDirectGear& setMethodStopType(const RWCString& methstoptype);
    CtiLMProgramDirectGear& setChangeCondition(const RWCString& changecond);
    CtiLMProgramDirectGear& setChangeDuration(ULONG changedur);
    CtiLMProgramDirectGear& setChangePriority(ULONG changeprior);
    CtiLMProgramDirectGear& setChangeTriggerNumber(ULONG triggernumber);
    CtiLMProgramDirectGear& setChangeTriggerOffset(DOUBLE triggeroffset);
    CtiLMProgramDirectGear& setPercentReduction(ULONG percentreduce);
    CtiLMProgramDirectGear& setGroupSelectionMethod(const RWCString& group);
    CtiLMProgramDirectGear& setMethodOptionType(const RWCString& optype);
    CtiLMProgramDirectGear& setMethodOptionMax(ULONG opmax);

    CtiLMProgramDirectGear* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramDirectGear& operator=(const CtiLMProgramDirectGear& right);

    int operator==(const CtiLMProgramDirectGear& right) const;
    int operator!=(const CtiLMProgramDirectGear& right) const;

    /* Static Members */

    //Possible control methods
    static const RWCString TimeRefreshMethod;
    static const RWCString SmartCycleMethod;
    static const RWCString MasterCycleMethod;
    static const RWCString RotationMethod;
    static const RWCString LatchingMethod;
    static const RWCString TrueCycleMethod;

    //Possible method stop types
    static const RWCString RestoreStopType;
    static const RWCString TimeInStopType;
    static const RWCString StopCycleStopType;

    //Possible gear change condition types
    static const RWCString NoneChangeCondition;
    static const RWCString DurationChangeCondition;
    static const RWCString PriorityChangeCondition;
    static const RWCString TriggerOffsetChangeCondition;

    // Possible group selection methods
    static const RWCString LastControlledSelectionMethod;
    static const RWCString AlwaysFirstGroupSelectionMethod;
    static const RWCString LeastControlTimeSelectionMethod;

    // Possible method option types
    static const RWCString FixedCountMethodOptionType;
    static const RWCString CountDownMethodOptionType;
    static const RWCString LimitedCountDownMethodOptionType;

private:

    ULONG _paoid;
    RWCString _gearname;
    ULONG _gearnumber;
    RWCString _controlmethod;
    ULONG _methodrate;
    ULONG _methodperiod;
    ULONG _methodratecount;
    ULONG _cyclerefreshrate;
    RWCString _methodstoptype;
    RWCString _changecondition;
    ULONG _changeduration;
    ULONG _changepriority;
    ULONG _changetriggernumber;
    DOUBLE _changetriggeroffset;
    ULONG _percentreduction;
    RWCString _groupselectionmethod;
    RWCString _methodoptiontype;
    ULONG _methodoptionmax;

    void restore(RWDBReader& rdr);
};
#endif

