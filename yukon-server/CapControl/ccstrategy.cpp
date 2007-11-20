
/*---------------------------------------------------------------------------
        Filename:  ccstrategy.cpp

        Programmer:  Julie Richter

        Description:    Source file for CtiCCStrategy.
                        CtiCCStrategy maintains the state and handles
                        the persistence of substation/feeder strategies for Cap Control.

        Initial Date:  8/28/2005

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/tpsrtvec.h>

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccstrategy.h"
#include "ccid.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "capcontroller.h"
#include "resolvers.h"
#include "mgr_holiday.h"

#include "rwutil.h"
#include "utility.h"

RWDEFINE_COLLECTABLE( CtiCCStrategy, CTICCSTRATEGY_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCStrategy::CtiCCStrategy()
{
}

CtiCCStrategy::CtiCCStrategy(RWDBReader& rdr)
{
    restore(rdr);
}

CtiCCStrategy::CtiCCStrategy(const CtiCCStrategy& strategy)
{
    operator=(strategy);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCStrategy::~CtiCCStrategy()
{
}

BOOL CtiCCStrategy::isDirty() const
{
    return _dirty;
}

//Members inherited from RWCollectable
void CtiCCStrategy::restoreGuts( RWvistream& istrm )
{
    RWCollectable::restoreGuts( istrm );

    istrm >> _strategyid
    >> _strategyname
    >> _controlmethod
    >> _maxdailyoperation
    >> _maxoperationdisableflag
    >> _peakstarttime
    >> _peakstoptime
    >> _controlinterval
    >> _maxconfirmtime
    >> _minconfirmpercent
    >> _failurepercent
    >> _daysofweek
    >> _controlunits
    >> _controldelaytime
    >> _controlsendretries
    >> _peaklag
    >> _peaklead
    >> _offpklag
    >> _offpklead;
}
void CtiCCStrategy::saveGuts( RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _strategyid
    << _strategyname
    << _controlmethod
    << _maxdailyoperation
    << _maxoperationdisableflag
    << _peakstarttime
    << _peakstoptime
    << _controlinterval
    << _maxconfirmtime
    << _minconfirmpercent
    << _failurepercent
    << _daysofweek
    << _controlunits
    << _controldelaytime
    << _controlsendretries
    << _peaklag    
    << _peaklead   
    << _offpklag   
    << _offpklead; 
}

CtiCCStrategy& CtiCCStrategy::operator=(const CtiCCStrategy& right)
{
    if( this != &right )
    {
        _strategyid = right._strategyid;
        _strategyname = right._strategyname;
        _controlmethod = right._controlmethod;
        _maxdailyoperation = right._maxdailyoperation;
        _maxoperationdisableflag = right._maxoperationdisableflag;
        _peakstarttime = right._peakstarttime;
        _peakstoptime = right._peakstoptime;
        _controlinterval = right._controlinterval;
        _maxconfirmtime = right._maxconfirmtime;
        _minconfirmpercent = right._minconfirmpercent;
        _failurepercent = right._failurepercent;
        _daysofweek = right._daysofweek;
        _controlunits = right._controlunits;
        _controldelaytime = right._controldelaytime;
        _controlsendretries = right._controlsendretries;
        _peaklag = right._peaklag;
        _peaklead = right._peaklead;
        _offpklag = right._offpklag;
        _offpklead = right._offpklead;
        _peakVARlag = right._peakVARlag;
        _peakVARlead = right._peakVARlead;
        _offpkVARlag = right._offpkVARlag;
        _offpkVARlead = right._offpkVARlead;
        _peakPFSetPoint = right._peakPFSetPoint;
        _offpkPFSetPoint = right._offpkPFSetPoint;
        _integrateFlag = right._integrateFlag;
        _integratePeriod = right._integratePeriod;
        _likeDayFallBack = right._likeDayFallBack;
    }
    return *this;

}

int CtiCCStrategy::operator==(const CtiCCStrategy& right) const
{
    return getStrategyId() == right.getStrategyId();
}
int CtiCCStrategy::operator!=(const CtiCCStrategy& right) const
{
    return getStrategyId() != right.getStrategyId();
}

CtiCCStrategy* CtiCCStrategy::replicate() const
{
    return(new CtiCCStrategy(*this));
}


void CtiCCStrategy::restore(RWDBReader &rdr)
{
    string tempBoolString;

    rdr["strategyid"] >> _strategyid;
    rdr["strategyname"] >> _strategyname;
    rdr["controlmethod"] >> _controlmethod;
    rdr["maxdailyoperation"] >> _maxdailyoperation;
    rdr["maxoperationdisableflag"] >> tempBoolString;
    CtiToLower(tempBoolString);
    _maxoperationdisableflag = (tempBoolString=="y"?TRUE:FALSE);
    //rdr["peaksetpoint"] >> _peaksetpoint;
    //rdr["offpeaksetpoint"] >> _offpeaksetpoint;
    rdr["peakstarttime"] >> _peakstarttime;
    rdr["peakstoptime"] >> _peakstoptime;
    //rdr["upperbandwidth"] >> _upperbandwidth;
    rdr["controlinterval"] >> _controlinterval;
    rdr["minresponsetime"] >> _maxconfirmtime;//will become "minconfirmtime" in the DB in 3.1
    rdr["minconfirmpercent"] >> _minconfirmpercent;
    rdr["failurepercent"] >> _failurepercent;
    rdr["daysofweek"] >> _daysofweek;
    //rdr["lowerbandwidth"] >> _lowerbandwidth;
    rdr["controlunits"] >> _controlunits;
    rdr["controldelaytime"] >> _controldelaytime;
    rdr["controlsendretries"] >> _controlsendretries;
    rdr["peaklag"] >> _peaklag;
    rdr["peaklead"] >> _peaklead;
    if (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::KVARControlUnits)||
        !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::PF_BY_KVARControlUnits)||
        !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::PF_BY_KQControlUnits)) 
    {   
        _peaklag = fabs(_peaklag);
        _peaklead =  -fabs(_peaklead);
    }
    rdr["offpklag"] >> _offpklag;
    rdr["offpklead"] >> _offpklead;
    if (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::KVARControlUnits)||
        !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::PF_BY_KVARControlUnits)||
        !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::PF_BY_KQControlUnits)) 
    {
        _offpklag = fabs(_offpklag);
        _offpklead =  -fabs(_offpklead);  
    }
    rdr["peakvarlag"] >> _peakVARlag;
    rdr["peakvarlead"] >> _peakVARlead;
    rdr["offpkvarlag"] >> _offpkVARlag;
    rdr["offpkvarlead"] >> _offpkVARlead;
    
    _peakVARlag = fabs(_peakVARlag);    
    _peakVARlead =  -fabs(_peakVARlead);
    _offpkVARlag = fabs(_offpkVARlag);      
    _offpkVARlead =  -fabs(_offpkVARlead); 

    rdr["peakpfsetpoint"] >> _peakPFSetPoint;
    if (_peakPFSetPoint > 100) 
    {
        _peakPFSetPoint = -(200 - _peakPFSetPoint);
    }
    rdr["offpkpfsetpoint"] >> _offpkPFSetPoint;
    if (_offpkPFSetPoint > 100) 
    {
        _offpkPFSetPoint = -(200 - _offpkPFSetPoint);
    }
    rdr["integrateflag"] >> tempBoolString;
    CtiToLower(tempBoolString);
    _integrateFlag = (tempBoolString=="y"?TRUE:FALSE);

    rdr["integrateperiod"] >> _integratePeriod;

    rdr["likedayfallback"] >> tempBoolString;
    CtiToLower(tempBoolString);
    _likeDayFallBack = (tempBoolString=="y"?TRUE:FALSE);

}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCStrategy::getStrategyId() const
{
    return _strategyid;
}



const string& CtiCCStrategy::getStrategyName() const
{
    return _strategyname;
}
const string& CtiCCStrategy::getControlMethod() const
{
    return _controlmethod;
}

LONG CtiCCStrategy::getMaxDailyOperation() const
{
    return _maxdailyoperation;
}

BOOL CtiCCStrategy::getMaxOperationDisableFlag() const
{
    return _maxoperationdisableflag;
}

DOUBLE CtiCCStrategy::getPeakLag() const
{
    return _peaklag;
}

DOUBLE CtiCCStrategy::getOffPeakLag() const
{
    return _offpklag;
}

DOUBLE CtiCCStrategy::getPeakLead() const
{
    return _peaklead;
}

DOUBLE CtiCCStrategy::getOffPeakLead() const
{
    return _offpklead;
}

DOUBLE CtiCCStrategy::getPeakVARLag() const
{
    return _peakVARlag;
}

DOUBLE CtiCCStrategy::getOffPeakVARLag() const
{
    return _offpkVARlag;
}

DOUBLE CtiCCStrategy::getPeakVARLead() const
{
    return _peakVARlead;
}

DOUBLE CtiCCStrategy::getOffPeakVARLead() const
{
    return _offpkVARlead;
}

DOUBLE CtiCCStrategy::getPeakPFSetPoint() const
{
    return _peakPFSetPoint;
}

DOUBLE CtiCCStrategy::getOffPeakPFSetPoint() const
{
    return _offpkPFSetPoint;
}

LONG CtiCCStrategy::getPeakStartTime() const
{
    return _peakstarttime;
}

LONG CtiCCStrategy::getPeakStopTime() const
{
    return _peakstoptime;
}

LONG CtiCCStrategy::getControlInterval() const
{
    return _controlinterval;
}

LONG CtiCCStrategy::getMaxConfirmTime() const
{
    return _maxconfirmtime;
}

LONG CtiCCStrategy::getMinConfirmPercent() const
{
    return _minconfirmpercent;
}

LONG CtiCCStrategy::getFailurePercent() const
{
    return _failurepercent;
}

const string& CtiCCStrategy::getDaysOfWeek() const
{
    return _daysofweek;
}

const string& CtiCCStrategy::getControlUnits() const
{
    return _controlunits;
}

LONG CtiCCStrategy::getControlDelayTime() const
{
    return _controldelaytime;
}

LONG CtiCCStrategy::getControlSendRetries() const
{
    return _controlsendretries;
}

BOOL CtiCCStrategy::getIntegrateFlag() const
{
    return _integrateFlag;
}

LONG CtiCCStrategy::getIntegratePeriod() const
{
    return _integratePeriod;
}

BOOL CtiCCStrategy::getLikeDayFallBack() const
{
    return _likeDayFallBack;
}
CtiCCStrategy& CtiCCStrategy::setStrategyId(LONG id)
{
    _strategyid = id;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setStrategyName(const string& strategyname)
{
    _strategyname = strategyname;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setControlMethod(const string& method)
{
    _controlmethod = method;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setMaxDailyOperation(LONG max)
{
    _maxdailyoperation = max;
    return *this;

}

CtiCCStrategy& CtiCCStrategy::setMaxOperationDisableFlag(BOOL maxopdisable)
{
    _maxoperationdisableflag = maxopdisable;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setPeakLag(DOUBLE peak)
{
    _peaklag = peak;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setOffPeakLag(DOUBLE offpeak)
{
    _offpklag = offpeak;
    return *this;
}
CtiCCStrategy& CtiCCStrategy::setPeakLead(DOUBLE peak)
{
    _peaklead = peak;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setOffPeakLead(DOUBLE offpeak)
{
    _offpklead = offpeak;
    return *this;
}
CtiCCStrategy& CtiCCStrategy::setPeakVARLag(DOUBLE peak)
{
    _peakVARlag = peak;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setOffPeakVARLag(DOUBLE offpeak)
{
    _offpkVARlag = offpeak;
    return *this;
}
CtiCCStrategy& CtiCCStrategy::setPeakVARLead(DOUBLE peak)
{
    _peakVARlead = peak;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setOffPeakVARLead(DOUBLE offpeak)
{
    _offpkVARlead = offpeak;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setPeakPFSetPoint(DOUBLE peak)
{
    _peakPFSetPoint = peak;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setOffPeakPFSetPoint(DOUBLE offpeak)
{
    _offpkPFSetPoint = offpeak;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setPeakStartTime(LONG starttime)
{
    _peakstarttime = starttime;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setPeakStopTime(LONG stoptime)
{
    _peakstoptime = stoptime;
    return *this;
}
CtiCCStrategy& CtiCCStrategy::setControlInterval(LONG interval)
{
    _controlinterval = interval;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setMaxConfirmTime(LONG confirm)
{   
    _maxconfirmtime = confirm;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setMinConfirmPercent(LONG confirm)
{
    _minconfirmpercent = confirm;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setFailurePercent(LONG failure)
{
    _failurepercent = failure;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setDaysOfWeek(const string& days)
{
    _daysofweek = days;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setControlUnits(const string& contunit)
{
    _controlunits = contunit;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setControlDelayTime(LONG delay)
{
    _controldelaytime = delay;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setControlSendRetries(LONG retries)
{
    _controlsendretries = retries;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setIntegrateFlag(BOOL flag)
{
    _integrateFlag = flag;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setIntegratePeriod(LONG period)
{
    _integratePeriod = period;
    return *this;
}

CtiCCStrategy& CtiCCStrategy::setLikeDayFallBack(BOOL flag)
{
    _likeDayFallBack = flag;
    return *this;
}


/* Public Static members */
/*const string CtiCCStrategy::IndividualFeederControlMethod   = "IndividualFeeder";
const string CtiCCStrategy::SubstationBusControlMethod      = "SubstationBus";
const string CtiCCStrategy::BusOptimizedFeederControlMethod = "BusOptimizedFeeder";
const string CtiCCStrategy::ManualOnlyControlMethod         = "ManualOnly";

const string CtiCCStrategy::KVARControlUnits         = "KVAR";
const string CtiCCStrategy::PF_BY_KVARControlUnits   = "P-Factor KW/KVar";
const string CtiCCStrategy::PF_BY_KQControlUnits     = "P-Factor KW/KQ";
  */
