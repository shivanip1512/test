#include "precompiled.h"

#include "dbaccess.h"
#include "lmprogramdirectgear.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

using std::string;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMProgramDirectGear, CTILMPROGRAMDIRECTGEAR_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear::CtiLMProgramDirectGear()
: _front_ramp_option(NoneRandomOptionType),
_front_ramp_time(0),
_back_ramp_option(NoneRandomOptionType),
_back_ramp_time(0),
_program_paoid(0),
_gearnumber(0),
_gearID(0),
_methodrate(0),
_methodperiod(0),
_methodratecount(0),
_cyclerefreshrate(0),
_changeduration(0),
_changepriority(0),
_changetriggernumber(0),
_changetriggeroffset(0),
_percentreduction(0),
_methodoptionmax(0),
_rampininterval(0),
_rampinpercent(0),
_rampoutinterval(0),
_rampoutpercent(0),
_kw_reduction(0),
_stop_repeat_count(0)
{
}

CtiLMProgramDirectGear::CtiLMProgramDirectGear(Cti::RowReader &rdr)
{
    restore(rdr);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear::~CtiLMProgramDirectGear()
{
}

/*---------------------------------------------------------------------------
    getProgramPAOId

    Returns the unique id of the gear's program
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getProgramPAOId() const
{

    return _program_paoid;
}

/*---------------------------------------------------------------------------
    getGearName

    Returns the name of the gear
---------------------------------------------------------------------------*/
const string& CtiLMProgramDirectGear::getGearName() const
{

    return _gearname;
}

/*---------------------------------------------------------------------------
    getGearNumber

    Returns the gear number of the gear
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getGearNumber() const
{

    return _gearnumber;
}

/**
 * Returns the unique ID of this gear. This is NOT a PAO id but
 * is unique on the gear table.
 *
 *
 * @return LONG
 */
LONG CtiLMProgramDirectGear::getUniqueID() const
{
    return _gearID;
}

/*---------------------------------------------------------------------------
    getControlMethod

    Returns the control method of the gear
---------------------------------------------------------------------------*/
const string& CtiLMProgramDirectGear::getControlMethod() const
{

    return _controlmethod;
}

/*---------------------------------------------------------------------------
    getMethodRate

    Returns the send/refresh rate in seconds for rotation/refresh; also
    percentage for cycling in the gear
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getMethodRate() const
{

    return _methodrate;
}

/*---------------------------------------------------------------------------
    getMethodPeriod

    Returns the length of 1 period in seconds for cycling or shed-time length
    for refresh/rotate in seconds for the gear
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getMethodPeriod() const
{

    return _methodperiod;
}

/*---------------------------------------------------------------------------
    getMethodRateCount

    Returns the number of cycle counts for cycling (a zero value means
    calculate the best number based on the total duration of shed); also
    the number of groups to take in refresh/rotation methods for the gear
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getMethodRateCount() const
{

    return _methodratecount;
}

/*---------------------------------------------------------------------------
    getCycleRefreshRate

    Returns the number of periods to wait in a cycle gear before we should
    refresh the cycle command. 0 for non-cycle gears, 1 to the default count
    for cycle gears.
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getCycleRefreshRate() const
{

    return _cyclerefreshrate;
}

/*---------------------------------------------------------------------------
    getMethodStopType

    Returns the method stop type of the gear
---------------------------------------------------------------------------*/
const string& CtiLMProgramDirectGear::getMethodStopType() const
{

    return _methodstoptype;
}

/*---------------------------------------------------------------------------
    getChangeCondition

    Returns the change condition of the gear
---------------------------------------------------------------------------*/
const string& CtiLMProgramDirectGear::getChangeCondition() const
{

    return _changecondition;
}

/*---------------------------------------------------------------------------
    getChangeDuration

    Returns the change duration of the gear in seconds
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getChangeDuration() const
{

    return _changeduration;
}

/*---------------------------------------------------------------------------
    getChangePriority

    Returns the change priority of the gear
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getChangePriority() const
{

    return _changepriority;
}

/*---------------------------------------------------------------------------
    getChangeTriggerNumber

    Returns the trigger number that the gear will use to compare the trigger
    offset value with.
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getChangeTriggerNumber() const
{

    return _changetriggernumber;
}

/*---------------------------------------------------------------------------
    getChangeTriggerOffset

    Returns the change trigger offset of the gear
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirectGear::getChangeTriggerOffset() const
{

    return _changetriggeroffset;
}

/*---------------------------------------------------------------------------
    getPercentReduction

    Returns the percent reduction of the gear
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getPercentReduction() const
{

    return _percentreduction;
}

/*---------------------------------------------------------------------------
    getGroupSelectionMethod

    Returns the group selection method of the direct program
---------------------------------------------------------------------------*/
const string& CtiLMProgramDirectGear::getGroupSelectionMethod() const
{

    return _groupselectionmethod;
}

/*---------------------------------------------------------------------------
    getMethodOptionType

    Returns the method option type of the direct program
---------------------------------------------------------------------------*/
const string& CtiLMProgramDirectGear::getMethodOptionType() const
{

    return _methodoptiontype;
}

/*---------------------------------------------------------------------------
    getMethodOptionMax

    Returns the method option max of the gear
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getMethodOptionMax() const
{

    return _methodoptionmax;
}

/*----------------------------------------------------------------------------
  getRampInInterval

  Returns the ramp in interval of the gear
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getRampInInterval() const
{
    return _rampininterval;
}

/*----------------------------------------------------------------------------
  getRampInPercent

  Returns the ramp in percent of the gear
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getRampInPercent() const
{
    return _rampinpercent;
}

/*----------------------------------------------------------------------------
  getRampOutInterval

  Returns the ramp out interval of the gear
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getRampOutInterval() const
{
    return _rampoutinterval;
}

/*----------------------------------------------------------------------------
  getRampOutPercent

  Returns the ramp out percent of the gear
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getRampOutPercent() const
{
    return _rampoutpercent;
}

/*----------------------------------------------------------------------------
  getFrontRampOption

 ----------------------------------------------------------------------------*/
const string& CtiLMProgramDirectGear::getFrontRampOption() const
{
    return _front_ramp_option;
}

const string& CtiLMProgramDirectGear::getBackRampOption() const
{
    return _back_ramp_option;
}

/*----------------------------------------------------------------------------
  getKWReduction

  Returns the KW reduction of the gear
----------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirectGear::getKWReduction() const
{
    return _kw_reduction;
}

/*----------------------------------------------------------------------------
  getStopCommandRepeat

  Returns the number of times the stop command should be repeated, once per minute
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getStopRepeatCount() const
{
    return _stop_repeat_count;
}

/*---------------------------------------------------------------------------
    setProgramPAOId

    Sets the id of the program - use with caution
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setProgramPAOId(LONG paoid)
{

    _program_paoid = paoid;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setGearName

    Sets the name of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setGearName(const string& name)
{

    _gearname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setGearNumber

    Sets the number of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setGearNumber(LONG gearnum)
{

    _gearnumber = gearnum;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlMethod

    Sets the control method of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setControlMethod(const string& contmeth)
{

    _controlmethod = contmeth;
    return *this;
}

/*---------------------------------------------------------------------------
    setMethodRate

    Sets the send/refresh rate in seconds for rotation/refresh; also
    percentage for cycling in the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setMethodRate(LONG methrate)
{

    _methodrate = methrate;
    return *this;
}

/*---------------------------------------------------------------------------
    setMethodPeriod

    Sets the length of 1 period in seconds for cycling or shed-time length
    for refresh/rotate in seconds for the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setMethodPeriod(LONG methper)
{

    _methodperiod = methper;
    return *this;
}

/*---------------------------------------------------------------------------
    setMethodRateCount

    Sets the number of cycle counts for cycling (a zero value means
    calculate the best number based on the total duration of shed); also
    the number of groups to take in refresh/rotation methods for the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setMethodRateCount(LONG methratecount)
{

    _methodratecount = methratecount;
    return *this;
}

/*---------------------------------------------------------------------------
    setCycleRefeshRate

    Sets the number of periods to wait in a cycle gear before we should
    refresh the cycle command. 0 for non-cycle gears, 1 to the default count
    for cycle gears.
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setCycleRefreshRate(LONG cyclerefresh)
{

    _cyclerefreshrate = cyclerefresh;
    return *this;
}

/*---------------------------------------------------------------------------
    setMethodStopType

    Sets the method stop type of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setMethodStopType(const string& methstoptype)
{

    _methodstoptype = methstoptype;
    return *this;
}

/*---------------------------------------------------------------------------
    setChangeCondition

    Sets the change condition of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setChangeCondition(const string& changecond)
{

    _changecondition = changecond;
    return *this;
}

/*---------------------------------------------------------------------------
    setChangeDuration

    Sets the change duration of the gear in seconds
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setChangeDuration(LONG changedur)
{

    _changeduration = changedur;
    return *this;
}

/*---------------------------------------------------------------------------
    setChangePriority

    Sets the change priority of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setChangePriority(LONG changeprior)
{

    _changepriority = changeprior;
    return *this;
}

/*---------------------------------------------------------------------------
    setChangeTriggerNumber

    Sets the trigger number that the gear will use to compare the trigger
    offset value with.
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setChangeTriggerNumber(LONG triggernumber)
{

    _changetriggernumber = triggernumber;
    return *this;
}

/*---------------------------------------------------------------------------
    setChangeTriggerOffset

    Sets the change offset of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setChangeTriggerOffset(DOUBLE triggeroffset)
{

    _changetriggeroffset = triggeroffset;
    return *this;
}

/*---------------------------------------------------------------------------
    setPercentReduction

    Sets the reduction percentage of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setPercentReduction(LONG percentreduce)
{

    _percentreduction = percentreduce;
    return *this;
}

/*---------------------------------------------------------------------------
    setGroupSelectionMethod

    Sets the group selection method of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setGroupSelectionMethod(const string& group)
{

    _groupselectionmethod = group;

    return *this;
}

/*---------------------------------------------------------------------------
    setMethodOptionType

    Sets the method option type of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setMethodOptionType(const string& optype)
{

    _methodoptiontype = optype;

    return *this;
}


/*---------------------------------------------------------------------------
    setMethodOptionMax

    Sets the method option max of the gear
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setMethodOptionMax(LONG opmax)
{

    _methodoptionmax = opmax;
    return *this;
}

/*----------------------------------------------------------------------------
  setRampInInterval

  Sets the ramp in interval of the gear
----------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setRampInInterval(LONG interval)
{
    _rampininterval = interval;
    return *this;
}

/*----------------------------------------------------------------------------
  setRampInPercent

  Sets the ramp in percent of the gear
----------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setRampInPercent(LONG percent)
{
    _rampinpercent = percent;
    return *this;
}

/*----------------------------------------------------------------------------
  setRampOutInterval

  Sets the ramp out interval of the gear
----------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setRampOutInterval(LONG interval)
{
    _rampoutinterval = interval;
    return *this;
}

/*----------------------------------------------------------------------------
  setRampOutPercent

  Sets the ramp out percent of the gear
----------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setRampOutPercent(LONG percent)
{
    _rampoutpercent = percent;
    return *this;
}

/*----------------------------------------------------------------------------
  setKWReduction

  Sets the kw reduction of the gear
----------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setKWReduction(DOUBLE kw)
{
    _kw_reduction = kw;
    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear* CtiLMProgramDirectGear::replicate() const
{
    return(CTIDBG_new CtiLMProgramDirectGear(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMProgramDirectGear::restore(Cti::RowReader &rdr)
{
    rdr["deviceid"] >> _program_paoid;//will be paobjectid
    rdr["gearname"] >> _gearname;
    rdr["gearnumber"] >> _gearnumber;
    rdr["controlmethod"] >> _controlmethod;
    rdr["methodrate"] >> _methodrate;
    rdr["methodperiod"] >> _methodperiod;
    rdr["methodratecount"] >> _methodratecount;
    rdr["cyclerefreshrate"] >> _cyclerefreshrate;
    rdr["methodstoptype"] >> _methodstoptype;
    rdr["changecondition"] >> _changecondition;
    rdr["changeduration"] >> _changeduration;
    rdr["changepriority"] >> _changepriority;
    rdr["changetriggernumber"] >> _changetriggernumber;
    rdr["changetriggeroffset"] >> _changetriggeroffset;
    rdr["percentreduction"] >> _percentreduction;
    rdr["groupselectionmethod"] >> _groupselectionmethod;
    rdr["methodoptiontype"] >> _methodoptiontype;
    rdr["methodoptionmax"] >> _methodoptionmax;
    rdr["gearid"] >> _gearID;
    rdr["rampininterval"] >> _rampininterval;
    rdr["rampinpercent"] >> _rampinpercent;
    rdr["rampoutinterval"] >> _rampoutinterval;
    rdr["rampoutpercent"] >>_rampoutpercent;
    rdr["frontrampoption"] >> _front_ramp_option;
    rdr["frontramptime"] >> _front_ramp_time;
    rdr["backrampoption"] >> _back_ramp_option;
    rdr["backramptime"] >> _back_ramp_time;
    rdr["kwreduction"] >> _kw_reduction;
    rdr["stopcommandrepeat"] >> _stop_repeat_count;
}

// Static Members

//Possible control methods
const string CtiLMProgramDirectGear::TimeRefreshMethod = "TimeRefresh";
const string CtiLMProgramDirectGear::SmartCycleMethod = "SmartCycle";
const string CtiLMProgramDirectGear::MasterCycleMethod = "MasterCycle";
const string CtiLMProgramDirectGear::RotationMethod = "Rotation";
const string CtiLMProgramDirectGear::LatchingMethod = "Latching";
const string CtiLMProgramDirectGear::TrueCycleMethod = "TrueCycle";
const string CtiLMProgramDirectGear::ThermostatRampingMethod = "ThermostatRamping";
const string CtiLMProgramDirectGear::SimpleThermostatRampingMethod = "SimpleThermostatRamping";
const string CtiLMProgramDirectGear::TargetCycleMethod = "TargetCycle";
const string CtiLMProgramDirectGear::MagnitudeCycleMethod = "MagnitudeCycle";
const string CtiLMProgramDirectGear::SEPCycleMethod = "SEPCycle";
const string CtiLMProgramDirectGear::SEPTempOffsetMethod = "SEPTemperatureOffset";
const string CtiLMProgramDirectGear::EcobeeCycleMethod = "EcobeeCycle";
const string CtiLMProgramDirectGear::HoneywellCycleMethod = "HoneywellCycle";
const string CtiLMProgramDirectGear::NestCriticalCycleMethod = "NestCriticalCycle";
const string CtiLMProgramDirectGear::NestStandardCycleMethod = "NestStandardCycle";
const string CtiLMProgramDirectGear::ItronCycleMethod = "ItronCycle";
const string CtiLMProgramDirectGear::MeterDisconnectMethod = "MeterDisconnect";
const string CtiLMProgramDirectGear::BeatThePeakMethod = "BeatThePeak";
const string CtiLMProgramDirectGear::NoControlMethod = "NoControl";

//Possible method stop types
const string CtiLMProgramDirectGear::RestoreStopType = "Restore";
const string CtiLMProgramDirectGear::TimeInStopType = "TimeIn";
const string CtiLMProgramDirectGear::StopCycleStopType = "StopCycle";
const string CtiLMProgramDirectGear::RampOutRandomStopType = "RampOutRandom";
const string CtiLMProgramDirectGear::RampOutFIFOStopType = "RampOutFIFO";
const string CtiLMProgramDirectGear::RampOutRandomRestoreStopType = "RampOutRandomRest";
const string CtiLMProgramDirectGear::RampOutFIFORestoreStopType = "RampOutFIFORestore";


//Possible gear change condition types
const string CtiLMProgramDirectGear::NoneChangeCondition = "None";
const string CtiLMProgramDirectGear::DurationChangeCondition = "Duration";
const string CtiLMProgramDirectGear::PriorityChangeCondition = "Priority";
const string CtiLMProgramDirectGear::TriggerOffsetChangeCondition = "TriggerOffset";

// Possible group selection methods
const string CtiLMProgramDirectGear::LastControlledSelectionMethod = "LastControlled";
const string CtiLMProgramDirectGear::AlwaysFirstGroupSelectionMethod = "AlwaysFirstGroup";
const string CtiLMProgramDirectGear::LeastControlTimeSelectionMethod = "LeastControlTime";

// Possible method option types
const string CtiLMProgramDirectGear::FixedCountMethodOptionType = "FixedCount";
const string CtiLMProgramDirectGear::CountDownMethodOptionType = "CountDown";
const string CtiLMProgramDirectGear::LimitedCountDownMethodOptionType = "LimitedCountDown";
const string CtiLMProgramDirectGear::DynamicShedTimeMethodOptionType = "DynamicShedTime";

// Possible random option types
const string CtiLMProgramDirectGear::NoneRandomOptionType = "(none)";
const string CtiLMProgramDirectGear::NoRampRandomOptionType = "NoRamp";
const string CtiLMProgramDirectGear::RandomizeRandomOptionType = "Random";
