#pragma once

#include "row_reader.h"
#include "collectable.h"

class SmartGearBase;

class CtiLMProgramDirectGear
{

public:

DECLARE_COLLECTABLE( CtiLMProgramDirectGear );

    CtiLMProgramDirectGear();
    CtiLMProgramDirectGear(Cti::RowReader &rdr);

    virtual ~CtiLMProgramDirectGear();

    LONG getProgramPAOId() const;
    const std::string& getGearName() const;
    LONG getGearNumber() const;
    LONG getUniqueID() const;
    const std::string& getControlMethod() const;
    LONG getMethodRate() const;
    LONG getMethodPeriod() const;
    LONG getMethodRateCount() const;
    LONG getCycleRefreshRate() const;
    const std::string& getMethodStopType() const;
    const std::string& getChangeCondition() const;
    LONG getChangeDuration() const;
    LONG getChangePriority() const;
    LONG getChangeTriggerNumber() const;
    DOUBLE getChangeTriggerOffset() const;
    LONG getPercentReduction() const;
    const std::string& getGroupSelectionMethod() const;
    const std::string& getMethodOptionType() const;
    LONG getMethodOptionMax() const;
    LONG getRampInInterval() const;
    LONG getRampInPercent() const;
    LONG getRampOutInterval() const;
    LONG getRampOutPercent() const;
    const std::string& getFrontRampOption() const;
    const std::string& getBackRampOption() const;
    DOUBLE getKWReduction() const;
    LONG getStopRepeatCount() const;

    CtiLMProgramDirectGear& setProgramPAOId(LONG paoid);
    CtiLMProgramDirectGear& setGearName(const std::string& name);
    CtiLMProgramDirectGear& setGearNumber(LONG gearnum);
    CtiLMProgramDirectGear& setControlMethod(const std::string& contmeth);
    CtiLMProgramDirectGear& setMethodRate(LONG methrate);
    CtiLMProgramDirectGear& setMethodPeriod(LONG methper);
    CtiLMProgramDirectGear& setMethodRateCount(LONG methratecount);
    CtiLMProgramDirectGear& setCycleRefreshRate(LONG cyclerefresh);
    CtiLMProgramDirectGear& setMethodStopType(const std::string& methstoptype);
    CtiLMProgramDirectGear& setChangeCondition(const std::string& changecond);
    CtiLMProgramDirectGear& setChangeDuration(LONG changedur);
    CtiLMProgramDirectGear& setChangePriority(LONG changeprior);
    CtiLMProgramDirectGear& setChangeTriggerNumber(LONG triggernumber);
    CtiLMProgramDirectGear& setChangeTriggerOffset(DOUBLE triggeroffset);
    CtiLMProgramDirectGear& setPercentReduction(LONG percentreduce);
    CtiLMProgramDirectGear& setGroupSelectionMethod(const std::string& group);
    CtiLMProgramDirectGear& setMethodOptionType(const std::string& optype);
    CtiLMProgramDirectGear& setMethodOptionMax(LONG opmax);
    CtiLMProgramDirectGear& setRampInInterval(LONG ininterval);
    CtiLMProgramDirectGear& setRampInPercent(LONG inpercent);
    CtiLMProgramDirectGear& setRampOutInterval(LONG outinterval);
    CtiLMProgramDirectGear& setRampOutPercent(LONG outpercent);
    CtiLMProgramDirectGear& setKWReduction(DOUBLE kw);

    virtual CtiLMProgramDirectGear* replicate() const;

   // This is the 'object representation' size
   virtual std::size_t getFixedSize() const     { return sizeof( *this ); }
   // This is any additional data that is allocated from free store memory
   virtual std::size_t getVariableSize() const;

    /* Static Members */

    // static constexpr std::string_view someday(?)
    
    // Possible control methods
    inline static const std::string

        TimeRefreshMethod               { "TimeRefresh" },
        SmartCycleMethod                { "SmartCycle" },
        MasterCycleMethod               { "MasterCycle" },
        RotationMethod                  { "Rotation" },
        LatchingMethod                  { "Latching" },
        TrueCycleMethod                 { "TrueCycle" },
        ThermostatRampingMethod         { "ThermostatRamping" },
        SimpleThermostatRampingMethod   { "SimpleThermostatRamping" },
        TargetCycleMethod               { "TargetCycle" },
        MagnitudeCycleMethod            { "MagnitudeCycle" },
        SEPCycleMethod                  { "SEPCycle" },
        SEPTempOffsetMethod             { "SEPTemperatureOffset" },
        EcobeeCycleMethod               { "EcobeeCycle" },
        EcobeeSetpointMethod            { "EcobeeSetpoint" },
        EcobeePlusMethod                { "EcobeePlus" },
        HoneywellCycleMethod            { "HoneywellCycle" },
        HoneywellSetpointMethod         { "HoneywellSetpoint" },
        NestCriticalCycleMethod         { "NestCriticalCycle" },
        NestStandardCycleMethod         { "NestStandardCycle" },
        ItronCycleMethod                { "ItronCycle" },
        MeterDisconnectMethod           { "MeterDisconnect" },
        BeatThePeakMethod               { "BeatThePeak" },
        NoControlMethod                 { "NoControl" },
        EatonCloudCycleMethod           { "EatonCloudCycle" },
        EatonCloudNoControlMethod       { "EatonCloudNoControl" };      

    // Possible method stop types
    static const std::string RestoreStopType;
    static const std::string TimeInStopType;
    static const std::string StopCycleStopType;
    static const std::string RampOutRandomStopType;
    static const std::string RampOutFIFOStopType;
    static const std::string RampOutRandomRestoreStopType;
    static const std::string RampOutFIFORestoreStopType;

    // Possible gear change condition types
    static const std::string NoneChangeCondition;
    static const std::string DurationChangeCondition;
    static const std::string PriorityChangeCondition;
    static const std::string TriggerOffsetChangeCondition;

    // Possible group selection methods
    static const std::string LastControlledSelectionMethod;
    static const std::string AlwaysFirstGroupSelectionMethod;
    static const std::string LeastControlTimeSelectionMethod;

    // Possible method option types
    static const std::string FixedCountMethodOptionType;
    static const std::string CountDownMethodOptionType;
    static const std::string LimitedCountDownMethodOptionType;
    static const std::string DynamicShedTimeMethodOptionType;

    // Possible randomoption types
    static const std::string NoneRandomOptionType;
    static const std::string NoRampRandomOptionType;
    static const std::string RandomizeRandomOptionType;

protected:
    void restore(Cti::RowReader &rdr);

private:

    LONG _program_paoid;
    std::string _gearname;
    LONG _gearnumber;
    LONG _gearID;
    std::string _controlmethod;
    LONG _methodrate;
    LONG _methodperiod;
    LONG _methodratecount;
    LONG _cyclerefreshrate;
    std::string _methodstoptype;
    std::string _changecondition;
    LONG _changeduration;
    LONG _changepriority;
    LONG _changetriggernumber;
    DOUBLE _changetriggeroffset;
    LONG _percentreduction;
    std::string _groupselectionmethod;
    std::string _methodoptiontype;
    LONG _methodoptionmax;
    LONG _rampininterval;
    LONG _rampinpercent;
    LONG _rampoutinterval;
    LONG _rampoutpercent;
    std::string _front_ramp_option;
    LONG _front_ramp_time;
    std::string _back_ramp_option;
    LONG _back_ramp_time;
    DOUBLE _kw_reduction;
    LONG _stop_repeat_count;
};

inline bool operator==( const CtiLMProgramDirectGear & lhs, const CtiLMProgramDirectGear & rhs )
{
    return lhs.getProgramPAOId() == rhs.getProgramPAOId();
}

inline bool operator!=( const CtiLMProgramDirectGear & lhs, const CtiLMProgramDirectGear & rhs )
{
    return ! ( lhs == rhs );
}

std::size_t calculateMemoryConsumption( const CtiLMProgramDirectGear * g );

