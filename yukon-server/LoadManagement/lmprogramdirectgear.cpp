/*---------------------------------------------------------------------------
        Filename:  lmprogramdirectgear.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMProgramDirectGear.
                        CtiLMProgramDirectGear maintains the state and handles
                        the persistence of direct program gears in Load
                        Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmprogramdirectgear.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMProgramDirectGear, CTILMPROGRAMDIRECTGEAR_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear::CtiLMProgramDirectGear()
{   
}

CtiLMProgramDirectGear::CtiLMProgramDirectGear(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMProgramDirectGear::CtiLMProgramDirectGear(const CtiLMProgramDirectGear& proggear)
{
    operator=(proggear);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear::~CtiLMProgramDirectGear()
{
}

/*---------------------------------------------------------------------------
    getPAOId
    
    Returns the unique id of the gear
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirectGear::getPAOId() const
{

    return _paoid;
}

/*---------------------------------------------------------------------------
    getGearName

    Returns the name of the gear
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramDirectGear::getGearName() const
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

/*---------------------------------------------------------------------------
    getControlMethod

    Returns the control method of the gear
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramDirectGear::getControlMethod() const
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
const RWCString& CtiLMProgramDirectGear::getMethodStopType() const
{

    return _methodstoptype;
}

/*---------------------------------------------------------------------------
    getChangeCondition

    Returns the change condition of the gear
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramDirectGear::getChangeCondition() const
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
const RWCString& CtiLMProgramDirectGear::getGroupSelectionMethod() const
{

    return _groupselectionmethod;
}

/*---------------------------------------------------------------------------
    getMethodOptionType

    Returns the method option type of the direct program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramDirectGear::getMethodOptionType() const
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

/*---------------------------------------------------------------------------
    setPAOId
    
    Sets the id of the control area - use with caution
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setPAOId(LONG paoid)
{

    _paoid = paoid;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setGearName
    
    Sets the name of the gear
---------------------------------------------------------------------------*/    
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setGearName(const RWCString& name)
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
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setControlMethod(const RWCString& contmeth)
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
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setMethodStopType(const RWCString& methstoptype)
{

    _methodstoptype = methstoptype;
    return *this;
}

/*---------------------------------------------------------------------------
    setChangeCondition
    
    Sets the change condition of the gear
---------------------------------------------------------------------------*/    
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setChangeCondition(const RWCString& changecond)
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
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setGroupSelectionMethod(const RWCString& group)
{

    _groupselectionmethod = group;

    return *this;
}

/*---------------------------------------------------------------------------
    setMethodOptionType
    
    Sets the method option type of the direct program
---------------------------------------------------------------------------*/    
CtiLMProgramDirectGear& CtiLMProgramDirectGear::setMethodOptionType(const RWCString& optype)
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


/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramDirectGear::restoreGuts(RWvistream& istrm)
{



    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
          >> _gearname
          >> _gearnumber
          >> _controlmethod
          >> _methodrate
          >> _methodperiod
          >> _methodratecount
          >> _cyclerefreshrate
          >> _methodstoptype
          >> _changecondition
          >> _changeduration
          >> _changepriority
          >> _changetriggernumber
          >> _changetriggeroffset
          >> _percentreduction
          >> _groupselectionmethod
          >> _methodoptiontype
          >> _methodoptionmax;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMProgramDirectGear::saveGuts(RWvostream& ostrm ) const  
{


        
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
          << _gearname
          << _gearnumber
          << _controlmethod
          << _methodrate
          << _methodperiod
          << _methodratecount
          << _cyclerefreshrate
          << _methodstoptype
          << _changecondition
          << _changeduration
          << _changepriority
          << _changetriggernumber
          << _changetriggeroffset
          << _percentreduction
          << _groupselectionmethod
          << _methodoptiontype
          << _methodoptionmax;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear& CtiLMProgramDirectGear::operator=(const CtiLMProgramDirectGear& right)
{


    if( this != &right )
    {
        _paoid = right._paoid;
        _gearname = right._gearname;
        _gearnumber = right._gearnumber;
        _controlmethod = right._controlmethod;
        _methodrate = right._methodrate;
        _methodperiod = right._methodperiod;
        _methodratecount = right._methodratecount;
        _cyclerefreshrate = right._cyclerefreshrate;
        _methodstoptype = right._methodstoptype;
        _changecondition = right._changecondition;
        _changeduration = right._changeduration;
        _changepriority = right._changepriority;
        _changetriggernumber = right._changetriggernumber;
        _changetriggeroffset = right._changetriggeroffset;
        _percentreduction = right._percentreduction;
        _groupselectionmethod = right._groupselectionmethod;
        _methodoptiontype = right._methodoptiontype;
        _methodoptionmax = right._methodoptionmax;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMProgramDirectGear::operator==(const CtiLMProgramDirectGear& right) const
{

    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramDirectGear::operator!=(const CtiLMProgramDirectGear& right) const
{

    return getPAOId() != right.getPAOId();
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear* CtiLMProgramDirectGear::replicate() const
{
    return (new CtiLMProgramDirectGear(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramDirectGear::restore(RWDBReader& rdr)
{


    rdr["deviceid"] >> _paoid;//will be paobjectid
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
}

// Static Members

//Possible control methods
const RWCString CtiLMProgramDirectGear::TimeRefreshMethod = "TimeRefresh";
const RWCString CtiLMProgramDirectGear::SmartCycleMethod = "SmartCycle";
const RWCString CtiLMProgramDirectGear::MasterCycleMethod = "MasterCycle";
const RWCString CtiLMProgramDirectGear::RotationMethod = "Rotation";
const RWCString CtiLMProgramDirectGear::LatchingMethod = "Latching";
const RWCString CtiLMProgramDirectGear::TrueCycleMethod = "TrueCycle";
const RWCString CtiLMProgramDirectGear::ThermostatSetbackMethod = "ThermostatSetback";
const RWCString CtiLMProgramDirectGear::NoControlMethod = "NoControl";

//Possible method stop types
const RWCString CtiLMProgramDirectGear::RestoreStopType = "Restore";
const RWCString CtiLMProgramDirectGear::TimeInStopType = "TimeIn";
const RWCString CtiLMProgramDirectGear::StopCycleStopType = "StopCycle";

//Possible gear change condition types
const RWCString CtiLMProgramDirectGear::NoneChangeCondition = "None";
const RWCString CtiLMProgramDirectGear::DurationChangeCondition = "Duration";
const RWCString CtiLMProgramDirectGear::PriorityChangeCondition = "Priority";
const RWCString CtiLMProgramDirectGear::TriggerOffsetChangeCondition = "TriggerOffset";

// Possible group selection methods
const RWCString CtiLMProgramDirectGear::LastControlledSelectionMethod = "LastControlled";
const RWCString CtiLMProgramDirectGear::AlwaysFirstGroupSelectionMethod = "AlwaysFirstGroup";
const RWCString CtiLMProgramDirectGear::LeastControlTimeSelectionMethod = "LeastControlTime";

// Possible method option types
const RWCString CtiLMProgramDirectGear::FixedCountMethodOptionType = "FixedCount";
const RWCString CtiLMProgramDirectGear::CountDownMethodOptionType = "CountDown";
const RWCString CtiLMProgramDirectGear::LimitedCountDownMethodOptionType = "LimitedCountDown";

