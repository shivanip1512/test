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
    
    LONG getPAOId() const;
    const RWCString& getGearName() const;
    LONG getGearNumber() const;
    const RWCString& getControlMethod() const;
    LONG getMethodRate() const;
    LONG getMethodPeriod() const;
    LONG getMethodRateCount() const;
    LONG getCycleRefreshRate() const;
    const RWCString& getMethodStopType() const;
    const RWCString& getChangeCondition() const;
    LONG getChangeDuration() const;
    LONG getChangePriority() const;
    LONG getChangeTriggerNumber() const;
    DOUBLE getChangeTriggerOffset() const;
    LONG getPercentReduction() const;
    const RWCString& getGroupSelectionMethod() const;
    const RWCString& getMethodOptionType() const;
    LONG getMethodOptionMax() const;
    LONG getRampInInterval() const;
    LONG getRampInPercent() const;
    LONG getRampOutInterval() const;
    LONG getRampOutPercent() const;

    CtiLMProgramDirectGear& setPAOId(LONG paoid);
    CtiLMProgramDirectGear& setGearName(const RWCString& name);
    CtiLMProgramDirectGear& setGearNumber(LONG gearnum);
    CtiLMProgramDirectGear& setControlMethod(const RWCString& contmeth);
    CtiLMProgramDirectGear& setMethodRate(LONG methrate);
    CtiLMProgramDirectGear& setMethodPeriod(LONG methper);
    CtiLMProgramDirectGear& setMethodRateCount(LONG methratecount);
    CtiLMProgramDirectGear& setCycleRefreshRate(LONG cyclerefresh);
    CtiLMProgramDirectGear& setMethodStopType(const RWCString& methstoptype);
    CtiLMProgramDirectGear& setChangeCondition(const RWCString& changecond);
    CtiLMProgramDirectGear& setChangeDuration(LONG changedur);
    CtiLMProgramDirectGear& setChangePriority(LONG changeprior);
    CtiLMProgramDirectGear& setChangeTriggerNumber(LONG triggernumber);
    CtiLMProgramDirectGear& setChangeTriggerOffset(DOUBLE triggeroffset);
    CtiLMProgramDirectGear& setPercentReduction(LONG percentreduce);
    CtiLMProgramDirectGear& setGroupSelectionMethod(const RWCString& group);
    CtiLMProgramDirectGear& setMethodOptionType(const RWCString& optype);
    CtiLMProgramDirectGear& setMethodOptionMax(LONG opmax);
    CtiLMProgramDirectGear& setRampInInterval(LONG ininterval);
    CtiLMProgramDirectGear& setRampInPercent(LONG inpercent);
    CtiLMProgramDirectGear& setRampOutInterval(LONG outinterval);
    CtiLMProgramDirectGear& setRampOutPercent(LONG outpercent);

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
    static const RWCString ThermostatSetbackMethod;
    static const RWCString NoControlMethod;

    //Possible method stop types
    static const RWCString RestoreStopType;
    static const RWCString TimeInStopType;
    static const RWCString StopCycleStopType;
    static const RWCString RampOutStopType;
    
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

protected:
    void restore(RWDBReader& rdr);

private:

    LONG _paoid;
    RWCString _gearname;
    LONG _gearnumber;
    RWCString _controlmethod;
    LONG _methodrate;
    LONG _methodperiod;
    LONG _methodratecount;
    LONG _cyclerefreshrate;
    RWCString _methodstoptype;
    RWCString _changecondition;
    LONG _changeduration;
    LONG _changepriority;
    LONG _changetriggernumber;
    DOUBLE _changetriggeroffset;
    LONG _percentreduction;
    RWCString _groupselectionmethod;
    RWCString _methodoptiontype;
    LONG _methodoptionmax;
    LONG _rampininterval;
    LONG _rampinpercent;
    LONG _rampoutinterval;
    LONG _rampoutpercent;
};
#endif

