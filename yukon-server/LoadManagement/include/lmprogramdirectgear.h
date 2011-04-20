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
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "row_reader.h"
                
class SmartGearBase;
                
class CtiLMProgramDirectGear : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramDirectGear )

    CtiLMProgramDirectGear();
    CtiLMProgramDirectGear(Cti::RowReader &rdr);
    CtiLMProgramDirectGear(const CtiLMProgramDirectGear& proggear);

    virtual ~CtiLMProgramDirectGear();
    
    LONG getProgramPAOId() const;
    const string& getGearName() const;
    LONG getGearNumber() const;
    LONG getUniqueID() const;
    const string& getControlMethod() const;
    LONG getMethodRate() const;
    LONG getMethodPeriod() const;
    LONG getMethodRateCount() const;
    LONG getCycleRefreshRate() const;
    const string& getMethodStopType() const;
    const string& getChangeCondition() const;
    LONG getChangeDuration() const;
    LONG getChangePriority() const;
    LONG getChangeTriggerNumber() const;
    DOUBLE getChangeTriggerOffset() const;
    LONG getPercentReduction() const;
    const string& getGroupSelectionMethod() const;
    const string& getMethodOptionType() const;
    LONG getMethodOptionMax() const;
    LONG getRampInInterval() const;
    LONG getRampInPercent() const;
    LONG getRampOutInterval() const;
    LONG getRampOutPercent() const;
    const string& getFrontRampOption() const;
    const string& getBackRampOption() const;
    DOUBLE getKWReduction() const;
        
    CtiLMProgramDirectGear& setProgramPAOId(LONG paoid);
    CtiLMProgramDirectGear& setGearName(const string& name);
    CtiLMProgramDirectGear& setGearNumber(LONG gearnum);
    CtiLMProgramDirectGear& setControlMethod(const string& contmeth);
    CtiLMProgramDirectGear& setMethodRate(LONG methrate);
    CtiLMProgramDirectGear& setMethodPeriod(LONG methper);
    CtiLMProgramDirectGear& setMethodRateCount(LONG methratecount);
    CtiLMProgramDirectGear& setCycleRefreshRate(LONG cyclerefresh);
    CtiLMProgramDirectGear& setMethodStopType(const string& methstoptype);
    CtiLMProgramDirectGear& setChangeCondition(const string& changecond);
    CtiLMProgramDirectGear& setChangeDuration(LONG changedur);
    CtiLMProgramDirectGear& setChangePriority(LONG changeprior);
    CtiLMProgramDirectGear& setChangeTriggerNumber(LONG triggernumber);
    CtiLMProgramDirectGear& setChangeTriggerOffset(DOUBLE triggeroffset);
    CtiLMProgramDirectGear& setPercentReduction(LONG percentreduce);
    CtiLMProgramDirectGear& setGroupSelectionMethod(const string& group);
    CtiLMProgramDirectGear& setMethodOptionType(const string& optype);
    CtiLMProgramDirectGear& setMethodOptionMax(LONG opmax);
    CtiLMProgramDirectGear& setRampInInterval(LONG ininterval);
    CtiLMProgramDirectGear& setRampInPercent(LONG inpercent);
    CtiLMProgramDirectGear& setRampOutInterval(LONG outinterval);
    CtiLMProgramDirectGear& setRampOutPercent(LONG outpercent);
    CtiLMProgramDirectGear& setKWReduction(DOUBLE kw);

    virtual CtiLMProgramDirectGear* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    int operator==(const CtiLMProgramDirectGear& right) const;
    int operator!=(const CtiLMProgramDirectGear& right) const;

    /* Static Members */

    //Possible control methods
    static const string TimeRefreshMethod;
    static const string SmartCycleMethod;
    static const string MasterCycleMethod;
    static const string RotationMethod;
    static const string LatchingMethod;
    static const string TrueCycleMethod;
    static const string MagnitudeCycleMethod;
    static const string ThermostatRampingMethod;
    static const string TargetCycleMethod;
    static const string SimpleThermostatRampingMethod;
    static const string SEPCycleMethod;
    static const string SEPTempOffsetMethod;
    static const string NoControlMethod;

    //Possible method stop types
    static const string RestoreStopType;
    static const string TimeInStopType;
    static const string StopCycleStopType;
    static const string RampOutRandomStopType;
    static const string RampOutFIFOStopType;
    static const string RampOutRandomRestoreStopType;
    static const string RampOutFIFORestoreStopType;
    
    //Possible gear change condition types
    static const string NoneChangeCondition;
    static const string DurationChangeCondition;
    static const string PriorityChangeCondition;
    static const string TriggerOffsetChangeCondition;

    // Possible group selection methods
    static const string LastControlledSelectionMethod;
    static const string AlwaysFirstGroupSelectionMethod;
    static const string LeastControlTimeSelectionMethod;

    // Possible method option types
    static const string FixedCountMethodOptionType;
    static const string CountDownMethodOptionType;
    static const string LimitedCountDownMethodOptionType;
    static const string DynamicShedTimeMethodOptionType;

    // Possible randomoption types
    static const string NoneRandomOptionType;
    static const string NoRampRandomOptionType;
    static const string RandomizeRandomOptionType;
    
protected:
    void restore(Cti::RowReader &rdr);

private:

    LONG _program_paoid;
    string _gearname;
    LONG _gearnumber;
    LONG _gearID;
    string _controlmethod;
    LONG _methodrate;
    LONG _methodperiod;
    LONG _methodratecount;
    LONG _cyclerefreshrate;
    string _methodstoptype;
    string _changecondition;
    LONG _changeduration;
    LONG _changepriority;
    LONG _changetriggernumber;
    DOUBLE _changetriggeroffset;
    LONG _percentreduction;
    string _groupselectionmethod;
    string _methodoptiontype;
    LONG _methodoptionmax;
    LONG _rampininterval;
    LONG _rampinpercent;
    LONG _rampoutinterval;
    LONG _rampoutpercent;
    string _front_ramp_option;
    LONG _front_ramp_time;
    string _back_ramp_option;
    LONG _back_ramp_time;
    DOUBLE _kw_reduction;
};
#endif

