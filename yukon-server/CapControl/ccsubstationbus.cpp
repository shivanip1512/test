/*---------------------------------------------------------------------------
        Filename:  ccsubstationbus.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSubstationBus.
                        CtiCCSubstationBus maintains the state and handles
                        the persistence of substation buses for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/tpsrtvec.h>

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccsubstationbus.h"
#include "ccid.h"
#include "cccapbank.h"
#include "ccfeeder.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "capcontroller.h"
#include "resolvers.h"
#include "mgr_holiday.h"
#include "mgr_paosched.h"

extern ULONG _CC_DEBUG;
extern BOOL _IGNORE_NOT_NORMAL_FLAG;
extern ULONG _SEND_TRIES;
extern BOOL _USE_FLIP_FLAG;

RWDEFINE_COLLECTABLE( CtiCCSubstationBus, CTICCSUBSTATIONBUS_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSubstationBus::CtiCCSubstationBus()
{
}

CtiCCSubstationBus::CtiCCSubstationBus(RWDBReader& rdr)
{
    restore(rdr);
}

CtiCCSubstationBus::CtiCCSubstationBus(const CtiCCSubstationBus& sub)
{
    operator=(sub);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstationBus::~CtiCCSubstationBus()
{  
    try
    {
        _ccfeeders.clearAndDestroy();
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
   // _ccfeeders.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getDisableFlag() const
{
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (AreaId) of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getParentId() const
{
    return _parentId;
}

/*---------------------------------------------------------------------------
    getStrategyId

    Returns the StrategyId of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getStrategyId() const
{
    return _strategyId;
}

/*---------------------------------------------------------------------------
    getStrategyName

    Returns the StrategyName of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getStrategyName() const
{
    return _strategyName;
}
/*---------------------------------------------------------------------------
    getControlMethod

    Returns the control method of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getControlMethod() const
{
    return _controlmethod;
}

/*---------------------------------------------------------------------------
    getMaxDailyOperation

    Returns the max daily operations of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getMaxDailyOperation() const
{
    return _maxdailyoperation;
}

/*---------------------------------------------------------------------------
    getMaxOperationDisableFlag

    Returns the max operations disable flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getMaxOperationDisableFlag() const
{
    return _maxoperationdisableflag;
}

/*---------------------------------------------------------------------------
    getPeakLag

    Returns the peak lag level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPeakLag() const
{
    return _peaklag;
}

/*---------------------------------------------------------------------------
    getOffPeakLag

    Returns the off peak lag level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getOffPeakLag() const
{
    return _offpklag;
}
/*---------------------------------------------------------------------------
    getPeakLead

    Returns the peak lead level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPeakLead() const
{
    return _peaklead;
}

/*---------------------------------------------------------------------------
    getOffPeakLead

    Returns the off peak lead level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getOffPeakLead() const
{
    return _offpklead;
}




/*---------------------------------------------------------------------------
    getPeakStartTime

    Returns the peak start time of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPeakStartTime() const
{
    return _peakstarttime;
}

/*---------------------------------------------------------------------------
    getPeakStopTime

    Returns the peak stop time of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPeakStopTime() const
{
    return _peakstoptime;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointId

    Returns the current var load point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentVarLoadPointId() const
{
    return _currentvarloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointValue

    Returns the current var load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getCurrentVarLoadPointValue() const
{
    return _currentvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointId

    Returns the current watt load point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentWattLoadPointId() const
{
    return _currentwattloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointValue

    Returns the current watt load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getCurrentWattLoadPointValue() const
{
    return _currentwattloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointId

    Returns the current volt load point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentVoltLoadPointId() const
{
    return _currentvoltloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointValue

    Returns the current volt load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getCurrentVoltLoadPointValue() const
{
    return _currentvoltloadpointvalue;
}

/*---------------------------------------------------------------------------
    getControlInterval

    Returns the control interval of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getControlInterval() const
{
    return _controlinterval;
}

/*---------------------------------------------------------------------------
    getMaxConfirmTime

    Returns the maximum confirm time of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getMaxConfirmTime() const
{
    return _maxconfirmtime;
}

/*---------------------------------------------------------------------------
    getMinConfirmPercent

    Returns the minimum confirm percent of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getMinConfirmPercent() const
{
    return _minconfirmpercent;
}

/*---------------------------------------------------------------------------
    getFailurePercent

    Returns the failure percent of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getFailurePercent() const
{
    return _failurepercent;
}

/*---------------------------------------------------------------------------
    getDaysOfWeek

    Returns the days of the week of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getDaysOfWeek() const
{
    return _daysofweek;
}

/*---------------------------------------------------------------------------
    getMapLocationId

    Returns the map location id of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getMapLocationId() const
{
    return _maplocationid;
}

/*---------------------------------------------------------------------------
    getControlUnits

    Returns the control unit of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getControlUnits() const
{
    return _controlunits;
}

/*---------------------------------------------------------------------------
    getControlDelayTime

    Returns the ControlDelayTime of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getControlDelayTime() const
{
    return _controldelaytime;
}

/*---------------------------------------------------------------------------
    getControlSendRetries

    Returns the ControlSendRetries of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getControlSendRetries() const
{
    return _controlsendretries;
}

/*---------------------------------------------------------------------------
    getDecimalPlaces

    Returns the decimal places of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getDecimalPlaces() const
{
    return _decimalplaces;
}

/*---------------------------------------------------------------------------
    getNextCheckTime

    Returns the next check time of the substation
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCSubstationBus::getNextCheckTime() const
{
    return _nextchecktime;
}

/*---------------------------------------------------------------------------
    getNewPointDataReceivedFlag

    Returns the new point data received flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getNewPointDataReceivedFlag() const
{
    return _newpointdatareceivedflag;
}

/*---------------------------------------------------------------------------
    getBusUpdatedFlag

    Returns the substation updated flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getBusUpdatedFlag() const
{
    return _busupdatedflag;
}

/*---------------------------------------------------------------------------
    getLastCurrentVarPointUpdateTime

    Returns the last current var point update time of the substation
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCSubstationBus::getLastCurrentVarPointUpdateTime() const
{
    return _lastcurrentvarpointupdatetime;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointId

    Returns the estimated var load point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getEstimatedVarLoadPointId() const
{
    return _estimatedvarloadpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointValue

    Returns the estimated var load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getEstimatedVarLoadPointValue() const
{
    return _estimatedvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getDailyOperationsAnalogPointId

    Returns the daily operations analog point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getDailyOperationsAnalogPointId() const
{
    return _dailyoperationsanalogpointid;
}

/*---------------------------------------------------------------------------
    getPowerFactorPointId

    Returns the power factor point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPowerFactorPointId() const
{
    return _powerfactorpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorPointId

    Returns the estimated power factor point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getEstimatedPowerFactorPointId() const
{
    return _estimatedpowerfactorpointid;
}

/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentDailyOperations() const
{
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getPeakTimeFlag

    Returns the flag that represents if the substation is in peak time
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getPeakTimeFlag() const
{
    return _peaktimeflag;
}

/*---------------------------------------------------------------------------
    getRecentlyControlledFlag

    Returns the flag if the substation has been recently controlled
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getRecentlyControlledFlag() const
{
    return _recentlycontrolledflag;
}

/*---------------------------------------------------------------------------
    getLastOperationTime

    Returns the last operation time of the substation
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCSubstationBus::getLastOperationTime() const
{
    return _lastoperationtime;
}

/*---------------------------------------------------------------------------
    getVarValueBeforeControl

    Returns the var value before control of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getVarValueBeforeControl() const
{
    return _varvaluebeforecontrol;
}

/*---------------------------------------------------------------------------
    getLastFeederControlledPAOId

    Returns the PAO id of the last feeder controlled of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getLastFeederControlledPAOId() const
{
    return _lastfeedercontrolledpaoid;
}

/*---------------------------------------------------------------------------
    getLastFeederControlledPosition

    Returns the position of the last feeder controlled of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getLastFeederControlledPosition() const
{
    return _lastfeedercontrolledposition;
}

/*---------------------------------------------------------------------------
    getPowerFactorValue

    Returns the PowerFactorValue of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPowerFactorValue() const
{
    return _powerfactorvalue;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorValue

    Returns the EstimatedPowerFactorValue of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getEstimatedPowerFactorValue() const
{
    return _estimatedpowerfactorvalue;
}

/*---------------------------------------------------------------------------
    getKVARSolution

    Returns the KVARSolution of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getKVARSolution() const
{
    return _kvarsolution;
}

/*---------------------------------------------------------------------------
    getCurrentVarPointQuality

    Returns the CurrentVarPointQuality of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentVarPointQuality() const
{
    return _currentvarpointquality;
}

/*---------------------------------------------------------------------------
    getWaiveControlFlag

    Returns the WaiveControlFlag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getWaiveControlFlag() const
{
    return _waivecontrolflag;
}

/*---------------------------------------------------------------------------
    getVerificationFlag

    Returns the WaiveControlFlag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getVerificationFlag() const
{
    return _verificationFlag;
}

BOOL CtiCCSubstationBus::getPerformingVerificationFlag() const
{
    return _performingVerificationFlag;
}

BOOL CtiCCSubstationBus::getVerificationDoneFlag() const
{
    return _verificationDoneFlag;
}

BOOL CtiCCSubstationBus::getOverlappingVerificationFlag() const
{
    return _overlappingSchedulesVerificationFlag;
}

LONG CtiCCSubstationBus::getCurrentVerificationFeederId() const
{
    return _currentVerificationFeederId;
}
LONG CtiCCSubstationBus::getCurrentVerificationCapBankId() const
{
    return _currentVerificationCapBankId;
}
LONG CtiCCSubstationBus::getCurrentVerificationCapBankOrigState() const
{
    return _currentCapBankToVerifyAssumedOrigState;
}



/*---------------------------------------------------------------------------
    getCCFeeders

    Returns the list of feeders in the substation
---------------------------------------------------------------------------*/
RWOrdered& CtiCCSubstationBus::getCCFeeders()
{
    return _ccfeeders;
}


/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOCategory(const RWCString& category)
{
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOClass(const RWCString& pclass)
{
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOName(const RWCString& name)
{
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOType(const RWCString& type)
{
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAODescription(const RWCString& description)
{
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDisableFlag(BOOL disable)
{
    _disableflag = disable;
    return *this;
}


/*---------------------------------------------------------------------------
    setParentId

    Sets the parentID (AreaID) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setParentId(LONG parentId)
{
    _parentId = parentId;
    return *this;
}

/*---------------------------------------------------------------------------
    setStrategyId

    Sets the strategy id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setStrategyId(LONG strategyId)
{
    _strategyId = strategyId;
    return *this;
}

/*---------------------------------------------------------------------------
    setStrategyName

    Sets the strategy name of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setStrategyName(const RWCString& strategyName)
{
    _strategyName = strategyName;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlMethod

    Sets the control method of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlMethod(const RWCString& method)
{
    _controlmethod = method;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxDailyOperation

    Sets the max daily operations of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMaxDailyOperation(LONG max)
{
    _maxdailyoperation = max;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxOperationDisableFlag

    Sets the max operations disable flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMaxOperationDisableFlag(BOOL maxopdisable)
{
    _maxoperationdisableflag = maxopdisable;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakLag

    Sets the peak lag level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakLag(DOUBLE peak)
{
    _peaklag = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakLag

    Sets the off peak lag level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setOffPeakLag(DOUBLE offpeak)
{
    _offpklag = offpeak;
    return *this;
}
/*---------------------------------------------------------------------------
    setPeakLead

    Sets the peak lead level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakLead(DOUBLE peak)
{
    _peaklead = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakLead

    Sets the off peak lead level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setOffPeakLead(DOUBLE offpeak)
{
    _offpklead = offpeak;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakStartTime

    Sets the peak start time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakStartTime(LONG starttime)
{
    _peakstarttime = starttime;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakStopTime

    Sets the peak stop time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakStopTime(LONG stoptime)
{
    _peakstoptime = stoptime;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointId

    Sets the current var load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVarLoadPointId(LONG currentvarid)
{
    _currentvarloadpointid = currentvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointValue

    Sets the current var load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVarLoadPointValue(DOUBLE currentvarval)
{
    if( _currentvarloadpointvalue != currentvarval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentvarloadpointvalue = currentvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointId

    Sets the current watt load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentWattLoadPointId(LONG currentwattid)
{
    _currentwattloadpointid = currentwattid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentWattLoadPointValue(DOUBLE currentwattval)
{
    if( _currentwattloadpointvalue != currentwattval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentwattloadpointvalue = currentwattval;
    return *this;
}
/*---------------------------------------------------------------------------
    setCurrentVoltLoadPointId

    Sets the current volt load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVoltLoadPointId(LONG currentvoltid)
{
    _currentvoltloadpointid = currentvoltid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVoltLoadPointValue

    Sets the current volt load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVoltLoadPointValue(DOUBLE currentvoltval)
{
    if( _currentvoltloadpointvalue != currentvoltval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentvoltloadpointvalue = currentvoltval;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlInterval

    Sets the control interval of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlInterval(LONG interval)
{
    _controlinterval = interval;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxConfirmTime

    Sets the MaxConfirmTime of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMaxConfirmTime(LONG confirm)
{
    _maxconfirmtime = confirm;
    return *this;
}

/*---------------------------------------------------------------------------
    setMinConfirmPercent

    Sets the min confirm percent of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMinConfirmPercent(LONG confirm)
{
    _minconfirmpercent = confirm;
    return *this;
}

/*---------------------------------------------------------------------------
    setFailurePercent

    Sets the failure percent of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setFailurePercent(LONG failure)
{
    _failurepercent = failure;
    return *this;
}

/*---------------------------------------------------------------------------
    setDaysOfWeek

    Sets the days of the week of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDaysOfWeek(const RWCString& days)
{
    _daysofweek = days;
    return *this;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMapLocationId(const RWCString& maplocation)
{
    _maplocationid = maplocation;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlUnits

    Sets the ControlUnits of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlUnits(const RWCString& units)
{
    _controlunits = units;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlDelayTime

    Sets the ControlDelayTime of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlDelayTime(LONG delay)
{
    _controldelaytime = delay;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlSendRetries

    Sets the ControlSendRetries of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlSendRetries(LONG retries)
{
    _controlsendretries = retries;
    return *this;
}

/*---------------------------------------------------------------------------
    setDecimalPlaces

    Sets the decimal places of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDecimalPlaces(LONG places)
{
    _decimalplaces = places;
    return *this;
}

/*---------------------------------------------------------------------------
    figureNextCheckTime

    Figures out when the substation should be checked again according to the
    control interval.
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::figureNextCheckTime()
{
    RWDBDateTime currenttime = RWDBDateTime();
    if( _controlinterval != 0 )
    {
        LONG tempsum = (currenttime.seconds()-(currenttime.seconds()%_controlinterval))+_controlinterval;
        _nextchecktime = RWDBDateTime(RWTime(tempsum));
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    else
    {
        _nextchecktime = currenttime;
        if (!_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase))
        {
            for (LONG i = 0; i < _ccfeeders.entries(); i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                if (currentFeeder->getStrategyId() > 0)
                {
                    LONG tempsum1 = (currenttime.seconds() - ( currenttime.seconds()%currentFeeder->getControlInterval() ) + currentFeeder->getControlInterval() );
                    _nextchecktime = RWDBDateTime(RWTime(tempsum1));
                    break;
                }
            }

        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setNewPointDataReceivedFlag(BOOL newpointdatareceived)
{
    if( _newpointdatareceivedflag != newpointdatareceived )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _newpointdatareceivedflag = newpointdatareceived;
    return *this;
}

/*---------------------------------------------------------------------------
    setBusUpdatedFlag

    Sets the substation updated flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setBusUpdatedFlag(BOOL busupdated)
{
    /*if( _busupdatedflag != busupdated )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        _dirty = TRUE;
    }*/
    _busupdatedflag = busupdated;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastCurrentVarPointUpdateTime

    Sets the last current var point update time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastCurrentVarPointUpdateTime(const RWDBDateTime& lastpointupdate)
{
    if( _lastcurrentvarpointupdatetime != lastpointupdate )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastcurrentvarpointupdatetime = lastpointupdate;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointId

    Sets the estimated var load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedVarLoadPointId(LONG estimatedvarid)
{
    _estimatedvarloadpointid = estimatedvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointValue

    Sets the estimated var load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedVarLoadPointValue(DOUBLE estimatedvarval)
{
    if( _estimatedvarloadpointvalue != estimatedvarval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _estimatedvarloadpointvalue = estimatedvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setDailyOperationsAnalogPointId

    Sets the daily operations analog point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDailyOperationsAnalogPointId(LONG opanalogpointid)
{
    _dailyoperationsanalogpointid = opanalogpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorPointId

    Sets the power factor point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPowerFactorPointId(LONG pfpointid)
{
    _powerfactorpointid = pfpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorPointId

    Sets the estimated power factor point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedPowerFactorPointId(LONG epfpointid)
{
    _estimatedpowerfactorpointid = epfpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentDailyOperations(LONG operations)
{
    if( _currentdailyoperations != operations )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentdailyoperations = operations;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakTimeFlag

    Sets the flag if the substation is in peak time
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakTimeFlag(LONG peaktime)
{
    if( _peaktimeflag != peaktime )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _peaktimeflag = peaktime;
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setRecentlyControlledFlag(BOOL recentlycontrolled)
{
    if( _recentlycontrolledflag != recentlycontrolled )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _recentlycontrolledflag = recentlycontrolled;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastOperationTime

    Sets the last operation time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastOperationTime(const RWDBDateTime& lastoperation)
{
    if( _lastoperationtime != lastoperation )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastoperationtime = lastoperation;
    return *this;
}

/*---------------------------------------------------------------------------
    setVarValueBeforeControl

    Sets the var value before control of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setVarValueBeforeControl(DOUBLE oldvarval)
{
    if( _varvaluebeforecontrol != oldvarval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _varvaluebeforecontrol = oldvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastFeederControlledPAOId

    Sets the pao id of the last feeder controlled in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastFeederControlledPAOId(LONG lastfeederpao)
{
    if( _lastfeedercontrolledpaoid != lastfeederpao )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastfeedercontrolledpaoid = lastfeederpao;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastFeederControlledPosition

    Sets the position of the last feeder controlled in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastFeederControlledPosition(LONG lastfeederposition)
{
    if( _lastfeedercontrolledposition != lastfeederposition )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastfeedercontrolledposition = lastfeederposition;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorValue

    Sets the PowerFactorValue in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPowerFactorValue(DOUBLE pfval)
{
    if( _powerfactorvalue != pfval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _powerfactorvalue = pfval;
    return *this;
}

/*---------------------------------------------------------------------------
    setKVARSolution

    Sets the KVARSolution in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setKVARSolution(DOUBLE solution)
{
    if( _kvarsolution != solution )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _kvarsolution = solution;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorValue

    Sets the EstimatedPowerFactorValue in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedPowerFactorValue(DOUBLE epfval)
{
    if( _estimatedpowerfactorvalue != epfval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _estimatedpowerfactorvalue = epfval;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarPointQuality

    Sets the CurrentVarPointQuality in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVarPointQuality(LONG cvpq)
{
    if( _currentvarpointquality != cvpq )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentvarpointquality = cvpq;
    return *this;
}

/*---------------------------------------------------------------------------
    setWaiveControlFlag

    Sets the WaiveControlFlag in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setWaiveControlFlag(BOOL waive)
{
    if( _waivecontrolflag != waive )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _waivecontrolflag = waive;
    return *this;
}


/*---------------------------------------------------------------------------
    figureEstimatedVarLoadPointValue

    Figures out the estimated var point value according to the states
    of the individual cap banks in each of the feeders
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::figureEstimatedVarLoadPointValue()
{
    if( getCurrentVarLoadPointId() > 0 )
    {
        DOUBLE tempValue;
        if( getRecentlyControlledFlag() )
            tempValue = getVarValueBeforeControl();
        else
            tempValue = getCurrentVarLoadPointValue();
    
        for(LONG i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
    
            for(LONG j=0;j<ccCapBanks.entries();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
                {
                    tempValue = tempValue + currentCapBank->getBankSize();
                }
            }
            //if( currentFeeder->getRecentlyControlledFlag() )
            //{
            currentFeeder->figureEstimatedVarLoadPointValue();
            //}
        }
    
        setEstimatedVarLoadPointValue(tempValue);
    }
    else
    {
        setEstimatedVarLoadPointValue(0.0);
    }

    return *this;
}

/*---------------------------------------------------------------------------
    checkForAndProvideNeededControl


---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::checkForAndProvideNeededControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    BOOL keepGoing = TRUE;

    //have we went past the max daily ops
    if( getMaxDailyOperation() > 0 &&
        _currentdailyoperations == getMaxDailyOperation() )//only send once
    {
        RWCString text = RWCString("Substation Bus Exceeded Max Daily Operations");
        RWCString additional = RWCString("Substation Bus: ");
        additional += getPAOName();
        //CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,GeneralLogType,SignalEvent));
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalAlarm0));


        //we should disable bus if the flag says so
        if( getMaxOperationDisableFlag() )
        {
            setDisableFlag(TRUE);
            setBusUpdatedFlag(TRUE);
            //store->UpdateSubstation(currentSubstationBus);
            RWCString text = RWCString("Substation Bus Disabled");
            RWCString additional = RWCString("Bus: ");
            additional += getPAOName();
            //CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalAlarm0));
            //CtiSignalMsg *pFailSig = CTIDBG_new CtiSignalMsg(pPoint->getID(), Cmd->getSOE(), devicename + " / " + pPoint->getName() + ": Commanded Control " + ResolveStateName(pPoint->getStateGroupID(), rawstate) + " Failed", getAlarmStateName( pPoint->getAlarming().getAlarmCategory(CtiTablePointAlarming::commandFailure) ), GeneralLogType, pPoint->getAlarming().getAlarmCategory(CtiTablePointAlarming::commandFailure), Cmd->getUser());
            
            keepGoing = FALSE;
        }
    }

    if( keepGoing )
    {
        if( currentDateTime.seconds() >= getLastOperationTime().seconds() + getControlDelayTime() )
        {
            if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
            {
                for(LONG i=0;i<_ccfeeders.entries();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if( !currentFeeder->getRecentlyControlledFlag() &&
                        (getControlInterval()!=0 ||
                         currentFeeder->getNewPointDataReceivedFlag() ||
                         currentFeeder->getControlInterval() != 0) )
                    {
                        figureCurrentSetPoint(currentDateTime);//this is just to set the Peak Time Flag
                        if( currentFeeder->checkForAndProvideNeededIndividualControl(currentDateTime, pointChanges, pilMessages, getPeakTimeFlag(), getDecimalPlaces(), getControlUnits()) )
                        {
                            setLastOperationTime(currentDateTime);
                            setRecentlyControlledFlag(TRUE);
                            setCurrentDailyOperations(getCurrentDailyOperations() + 1);
                        }
                        setBusUpdatedFlag(TRUE);
                        //currentFeeder->setNewPointDataReceivedFlag(FALSE);
                    }
                }
                //setNewPointDataReceivedFlag(FALSE);
            }
            else if( _controlmethod.compareTo(CtiCCSubstationBus::ManualOnlyControlMethod,RWCString::ignoreCase) )//intentionally left the ! off
            {
                DOUBLE lagLevel = (isPeakTime(currentDateTime)?_peaklag:_offpklag);
                DOUBLE leadLevel = (getPeakTimeFlag()?_peaklead:_offpklead);
                DOUBLE setPoint = (lagLevel + leadLevel)/2;
                setKVARSolution(calculateKVARSolution(_controlunits,setPoint,getCurrentVarLoadPointValue(),getCurrentWattLoadPointValue()));
                if( !_IGNORE_NOT_NORMAL_FLAG ||
                    getCurrentVarPointQuality() == NormalQuality )
                {
                    if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) )
                    {
                        if( getCurrentVarLoadPointValue() > lagLevel ||
                            getCurrentVarLoadPointValue() < leadLevel )
                        {
                            if( !_controlmethod.compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) )
                            {
                                regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, pilMessages);
                            }
                            else if( !_controlmethod.compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
                            {
                                optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, pilMessages);
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Invalid control method: " << _controlmethod << ", in sub bus: " << getPAOName() << endl;
                            }
                        }
                    }
                    else if( !_controlunits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) )
                    {
                        if( getCurrentVoltLoadPointValue() < lagLevel ||
                            getCurrentVoltLoadPointValue() > leadLevel )
                        {
                            if( !_controlmethod.compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) )
                            {
                                if (getCurrentVarLoadPointId() > 0)
                                    regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, pilMessages);
                                else
                                { 
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Substation Volt Control requires Var PointID.  Var PointID not found in sub bus: " << getPAOName() << endl;
                                }
                            }
                            else if( !_controlmethod.compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
                            {
                                optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, pilMessages);
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Invalid control method: " << _controlmethod << ", in sub bus: " << getPAOName() << endl;
                            }
                        }
                    }
                    else if( !_controlunits.compareTo(CtiCCSubstationBus::PF_BY_KVARControlUnits,RWCString::ignoreCase) ||
                             !_controlunits.compareTo(CtiCCSubstationBus::PF_BY_KQControlUnits,RWCString::ignoreCase) )
                    {
                        if( !_controlmethod.compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) )
                        {
                            regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, pilMessages);
                        }
                        else if( !_controlmethod.compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
                        {
                            optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, pilMessages);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Invalid control method: " << _controlmethod << ", in sub bus: " << getPAOName() << endl;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Invalid control units: " << _controlunits << ", in sub bus: " << getPAOName() << endl;
                    }
                }
                clearOutNewPointReceivedFlags();
            }
        }
        else if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Cannot control an additional bank because not past control delay time, in sub bus: " << getPAOName() << endl;
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    calculateKVARSolution


---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::calculateKVARSolution(const RWCString& controlUnits, DOUBLE setPoint, DOUBLE varValue, DOUBLE wattValue)
{
    DOUBLE returnKVARSolution = 0.0;
    if( !controlUnits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) ||
        !controlUnits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) )
    {
        returnKVARSolution = setPoint - varValue;
    }
    else if( !controlUnits.compareTo(CtiCCSubstationBus::PF_BY_KVARControlUnits,RWCString::ignoreCase) ||
             !controlUnits.compareTo(CtiCCSubstationBus::PF_BY_KQControlUnits,RWCString::ignoreCase))
    {
        setPoint = 100;
        DOUBLE targetKVA = wattValue / (setPoint/100.0);
        DOUBLE NaNDefenseDouble = (targetKVA*targetKVA)-(wattValue*wattValue);
        DOUBLE targetKVAR = 0.0;
        if( NaNDefenseDouble > 0.0 )
        {
            targetKVAR = sqrt(NaNDefenseDouble);
        }

        returnKVARSolution = targetKVAR - varValue;
    }
    else if( !controlUnits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) )
    {
        returnKVARSolution = 0;
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid control units: " << controlUnits << ", in: " __FILE__ << " at: " << __LINE__ << endl;
    }

    return returnKVARSolution;
}

/*---------------------------------------------------------------------------
    regularSubstationBusControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::regularSubstationBusControl(DOUBLE lagLevel, DOUBLE leadLevel, const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    CtiRequestMsg* request = NULL;
    try
    {
        CtiCCFeeder* currentFeeder = NULL;
        LONG currentPosition = getLastFeederControlledPosition();
        LONG iterations = 0;
        if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) ||
            !_controlunits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) )
        {
            if( ( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) &&
                lagLevel < getCurrentVarLoadPointValue() ) ||
                ( !_controlunits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) &&
                lagLevel > getCurrentVoltLoadPointValue() ) )
            {
                //if( _CC_DEBUG )
                if(  !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().data() << endl;
                }
                else
                {
                    setKVARSolution(-1);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Attempting to Increase Volt level in substation bus: " << getPAOName().data() << endl;
                    }
                }

                CtiCCCapBank* capBank = NULL;
                while( capBank == NULL &&
                       iterations < _ccfeeders.entries() )
                {
                    if( currentPosition >= _ccfeeders.entries()-1 )
                    {
                        currentPosition = 0;
                    }
                    else
                    {
                        currentPosition++;
                    }
                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution());
                    }
                    iterations++;
                }
                if( capBank != NULL )
                {
                    if( capBank != NULL &&
                        capBank->getRecloseDelay() > 0 &&
                        currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime().rwtime() << endl;
                            dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                            dout << " Current Date Time:       " << currentDateTime.rwtime() << endl;
                        }
                    }
                    else
                    {
                        request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                    }
                }
    
                if( request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Decrease Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Close state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_ccfeeders.entries();i++)
                        {
                            RWSortedVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                            for(int j=0;j<ccCapBanks.entries();j++)
                            {
                                currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            else  // lead Level is greater than currentVarPointValue
            {
                if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().data() << endl;
                }
                else
                {
                    setKVARSolution(1);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Attempting to Decrease Volt level in substation bus: " << getPAOName().data() << endl;
                    }
                }

                CtiCCCapBank* capBank = NULL;
                while( capBank == NULL &&
                       iterations < _ccfeeders.entries() )
                {
                    if( currentPosition <= 0 )
                    {
                        currentPosition = _ccfeeders.entries()-1;
                    }
                    else
                    {
                        currentPosition--;
                    }
                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution());
                    }
                    iterations++;
                }
                if( capBank != NULL )
                {
                    request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                }

                if( request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Open state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_ccfeeders.entries();i++)
                        {
                            RWSortedVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                            for(int j=0;j<ccCapBanks.entries();j++)
                            {
                                currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
        }
        else if( !_controlunits.compareTo(CtiCCSubstationBus::PF_BY_KVARControlUnits,RWCString::ignoreCase) ||
                 !_controlunits.compareTo(CtiCCSubstationBus::PF_BY_KQControlUnits,RWCString::ignoreCase) )
        {   
            if( getKVARSolution() < 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().data() << endl;
                }
    
                CtiCCCapBank* capBank = NULL;
                while( capBank == NULL &&
                       iterations < _ccfeeders.entries() )
                {
                    if( currentPosition >= _ccfeeders.entries()-1 )
                    {
                        currentPosition = 0;
                    }
                    else
                    {
                        currentPosition++;
                    }
                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution());
                    }
                    iterations++;
                }

                if( capBank != NULL )
                {
                    DOUBLE adjustedBankKVARReduction = (lagLevel/100.0)*((DOUBLE)capBank->getBankSize());
                    if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                    {
                        if( capBank != NULL &&
                            capBank->getRecloseDelay() > 0 &&
                            currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime().rwtime() << endl;
                                dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                dout << " Current Date Time:       " << currentDateTime.rwtime() << endl;
                            }
                        }
                        else
                        {
                            request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                        }
                    }
                    else
                    {//cap bank too big
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                    }
                }
    
                if( capBank == NULL && request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Decrease Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Close state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_ccfeeders.entries();i++)
                        {
                            RWSortedVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                            for(int j=0;j<ccCapBanks.entries();j++)
                            {
                                currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().data() << endl;
                }
    
                CtiCCCapBank* capBank = NULL;
                while( capBank == NULL &&
                       iterations < _ccfeeders.entries() )
                {
                    if( currentPosition <= 0 )
                    {
                        currentPosition = _ccfeeders.entries()-1;
                    }
                    else
                    {
                        currentPosition--;
                    }
                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution());
                    }
                    iterations++;
                }

                if( capBank != NULL )
                {
                    DOUBLE adjustedBankKVARIncrease = (leadLevel/100.0)*((DOUBLE)capBank->getBankSize());
                    if( adjustedBankKVARIncrease <= getKVARSolution() )
                    {
                        request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                    }
                    else
                    {//cap bank too big
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                    }
                }
    
                if( capBank == NULL && request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Open state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_ccfeeders.entries();i++)
                        {
                            RWSortedVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                            for(int j=0;j<ccCapBanks.entries();j++)
                            {
                                currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
        }

        if( request != NULL )
        {
            pilMessages.insert(request);
            setLastOperationTime(currentDateTime);
            setLastFeederControlledPAOId(currentFeeder->getPAOId());
            setLastFeederControlledPosition(currentPosition);
            ((CtiCCFeeder*)_ccfeeders[currentPosition])->setLastOperationTime(currentDateTime);
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            setCurrentDailyOperations(getCurrentDailyOperations() + 1);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.insert(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setRecentlyControlledFlag(TRUE);
        }
        //setNewPointDataReceivedFlag(FALSE);
        //regardless what happened the substation bus should be should be sent to the client
        setBusUpdatedFlag(TRUE);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    optimizedSubstationBusControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::optimizedSubstationBusControl(DOUBLE lagLevel, DOUBLE leadLevel, const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    CtiRequestMsg* request = NULL;
    CtiCCFeeder* lastFeederControlled = NULL;
    int positionLastFeederControlled = -1;
    try
    {
        RWTPtrSortedVector<CtiCCFeeder, FeederVARComparison<CtiCCFeeder> > varSortedFeeders;
        //DOUBLE setpoint = figureCurrentSetPoint(currentDateTime);

        for(int i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
            currentFeeder->fillOutBusOptimizedInfo(getPeakTimeFlag());
            varSortedFeeders.insert(currentFeeder);
        }

        if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) ||
            !_controlunits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) )
        {
            if( ( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) &&
                lagLevel < getCurrentVarLoadPointValue() ) ||
                ( !_controlunits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) &&
                lagLevel > getCurrentVoltLoadPointValue() ) )
            {
                //if( _CC_DEBUG )
                if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().data() << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Increase Volt level in substation bus: " << getPAOName().data() << endl;
                }
    
                CtiCCCapBank* capBank = NULL;
                for(int j=0;j<varSortedFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)varSortedFeeders[j];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag())
                    {
                        if (currentFeeder->getCurrentVarLoadPointId() > 0)
                        {                                                
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution());
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found in feeder: " << currentFeeder->getPAOName() << endl;
                        }    
                    }
                    if( capBank != NULL )
                    {
                        if( capBank != NULL &&
                            capBank->getRecloseDelay() > 0 &&
                            currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime().rwtime() << endl;
                                dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                dout << " Current Date Time:       " << currentDateTime.rwtime() << endl;
                            }
                        }
                        else
                        {
                            request = ((CtiCCFeeder*)varSortedFeeders[j])->createDecreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                            lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                            positionLastFeederControlled = j;
                        }
                        break;
                    }
                }
                if( request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Decrease Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Close state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_ccfeeders.entries();i++)
                        {
                            RWSortedVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                            for(int j=0;j<ccCapBanks.entries();j++)
                            {
                                currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            else //leadLevel greater than currentVarPointValue
            {
                //if( _CC_DEBUG )
                if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().data() << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Decrease Volt level in substation bus: " << getPAOName().data() << endl;
                }

                CtiCCCapBank* capBank = NULL;
                for(int j=varSortedFeeders.entries()-1;j>=0;j--)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)varSortedFeeders[j];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() )
                    {
                        if (currentFeeder->getCurrentVarLoadPointId() > 0)
                        {                                                
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution());
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found in feeder: " << currentFeeder->getPAOName() << endl;
                        }
                    }
                    if( capBank != NULL )
                    {
                        request = ((CtiCCFeeder*)varSortedFeeders[j])->createIncreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                        lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                        positionLastFeederControlled = j;
                        break;
                    }
                }
    
                if( request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Open state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_ccfeeders.entries();i++)
                        {
                            RWSortedVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                            for(int j=0;j<ccCapBanks.entries();j++)
                            {
                                currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
        }
        else if( !_controlunits.compareTo(CtiCCSubstationBus::PF_BY_KVARControlUnits,RWCString::ignoreCase) ||
                 !_controlunits.compareTo(CtiCCSubstationBus::PF_BY_KQControlUnits,RWCString::ignoreCase) )
        {
            if( getKVARSolution() < 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().data() << endl;
                }
    
                CtiCCCapBank* capBank = NULL;
                for(int j=0;j<varSortedFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)varSortedFeeders[j];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution());
                    }
                    if( capBank != NULL )
                    {
                        lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                        positionLastFeederControlled = j;
                        DOUBLE adjustedBankKVARReduction = (lagLevel/100.0)*((DOUBLE)capBank->getBankSize());
                        if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                        {
                            if( capBank != NULL &&
                                capBank->getRecloseDelay() > 0 &&
                                currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime().rwtime() << endl;
                                    dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                    dout << " Current Date Time:       " << currentDateTime.rwtime() << endl;
                                }
                            }
                            else
                            {
                                request = ((CtiCCFeeder*)varSortedFeeders[j])->createDecreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                            }
                        }
                        else
                        {//cap bank too big
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                        }
                        break;
                    }
                }

                if( capBank == NULL && request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Decrease Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Close stateor Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_ccfeeders.entries();i++)
                        {
                            RWSortedVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                            for(int j=0;j<ccCapBanks.entries();j++)
                            {
                                currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().data() << endl;
                }
    
                CtiCCCapBank* capBank = NULL;
                for(int j=varSortedFeeders.entries()-1;j>=0;j--)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)varSortedFeeders[j];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution());
                    }
                    if( capBank != NULL )
                    {
                        lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                        positionLastFeederControlled = j;
                        DOUBLE adjustedBankKVARIncrease = (leadLevel/100.0)*((DOUBLE)capBank->getBankSize());
                        if( adjustedBankKVARIncrease <= getKVARSolution() )
                        {
                            request = ((CtiCCFeeder*)varSortedFeeders[j])->createIncreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                        }
                        else
                        {//cap bank too big
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                        }
                        break;
                    }
                }
    
                if( capBank == NULL && request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Open state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_ccfeeders.entries();i++)
                        {
                            RWSortedVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                            for(int j=0;j<ccCapBanks.entries();j++)
                            {
                                currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid control units: " << _controlunits << " in sub bus: " << getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

        if( request != NULL )
        {
            pilMessages.insert(request);
            setLastOperationTime(currentDateTime);
            setLastFeederControlledPAOId(lastFeederControlled->getPAOId());
            setLastFeederControlledPosition(positionLastFeederControlled);
            lastFeederControlled->setLastOperationTime(currentDateTime);
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            setCurrentDailyOperations(getCurrentDailyOperations() + 1);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.insert(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setRecentlyControlledFlag(TRUE);
        }
        //setNewPointDataReceivedFlag(FALSE);
        //for(int j=0;j<_ccfeeders.entries();j++)
        //{
            //((CtiCCFeeder*)_ccfeeders[j])->setNewPointDataReceivedFlag(FALSE);
        //}
        //regardless what happened the substation bus should be should be sent to the client
        setBusUpdatedFlag(TRUE);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}



/*---------------------------------------------------------------------------
    figureCurrentSetPoint

    Returns the current set point depending on if it is peak or off peak
    time and sets the set point status
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::figureCurrentSetPoint(const RWDBDateTime& currentDateTime)
{
    DOUBLE lagLevel = (isPeakTime(currentDateTime)?_peaklag:_offpklag);
    DOUBLE leadLevel = (getPeakTimeFlag()?_peaklead:_offpklead);

    return (lagLevel + leadLevel)/2;
} 

/*---------------------------------------------------------------------------
    isPeakTime

    Returns a boolean if it is peak time it also sets the peak time flag.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isPeakTime(const RWDBDateTime& currentDateTime)
{
    unsigned secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
    if( isPeakDay() && getPeakStartTime() <= secondsFromBeginningOfDay && secondsFromBeginningOfDay <= getPeakStopTime() )
    {
        setPeakTimeFlag(TRUE);
    }
    else
    {
        setPeakTimeFlag(FALSE);
    }
    return _peaktimeflag;
}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::capBankControlStatusUpdate(RWOrdered& pointChanges)
{
    BOOL returnBoolean = TRUE;
    char tempchar[64] = "";
    RWCString text = "";
    RWCString additional = "";

    if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) ||
        !_controlmethod.compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
    {
        LONG recentlyControlledFeeders = 0;
        for(LONG i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

            if( currentFeeder->getRecentlyControlledFlag() )
            {
                LONG minConfirmPercent = getMinConfirmPercent();
                LONG maxConfirmTime = getMaxConfirmTime();
                LONG sendRetries = getControlSendRetries();
                LONG failPercent = getFailurePercent();

                if (currentFeeder->getStrategyId() > 0)
                {
                    minConfirmPercent = currentFeeder->getMinConfirmPercent();
                    maxConfirmTime = currentFeeder->getMaxConfirmTime();
                    sendRetries = currentFeeder->getControlSendRetries();
                    failPercent = currentFeeder->getFailurePercent();
                }
                if( currentFeeder->isAlreadyControlled(minConfirmPercent) ||
                    currentFeeder->isPastMaxConfirmTime(RWDBDateTime(),maxConfirmTime,sendRetries) )
                {
                    currentFeeder->capBankControlStatusUpdate(pointChanges,minConfirmPercent,failPercent,currentFeeder->getVarValueBeforeControl(),currentFeeder->getCurrentVarLoadPointValue(), currentFeeder->getCurrentVarPointQuality());
                    //currentFeeder->setNewPointDataReceivedFlag(FALSE);
                }
                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    recentlyControlledFeeders++;
                }
            }
        }
        if( recentlyControlledFeeders == 0 )
        {
            setRecentlyControlledFlag(FALSE);
        }
        figureEstimatedVarLoadPointValue();
    }
    else if( !_controlmethod.compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) )
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()];

        if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
        {
            currentFeeder->capBankControlStatusUpdate(pointChanges,getMinConfirmPercent(),getFailurePercent(),getVarValueBeforeControl(),getCurrentVarLoadPointValue(), getCurrentVarPointQuality());
            setRecentlyControlledFlag(FALSE);
            figureEstimatedVarLoadPointValue();
        }
        else
        {
            for(LONG i=0;i<_ccfeeders.entries();i++)
            {
                currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

                if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                {
                    currentFeeder->capBankControlStatusUpdate(pointChanges,getMinConfirmPercent(),getFailurePercent(),getVarValueBeforeControl(),getCurrentVarLoadPointValue(), getCurrentVarPointQuality());
                    setRecentlyControlledFlag(FALSE);
                    figureEstimatedVarLoadPointValue();
                    break;
                }
            }
        }
    }
    else if( !_controlmethod.compareTo(CtiCCSubstationBus::ManualOnlyControlMethod,RWCString::ignoreCase) )
    {
        LONG recentlyControlledFeeders = 0;
        for(LONG i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

            if( currentFeeder->getRecentlyControlledFlag() )
            {
                if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                {
                    if( currentFeeder->isAlreadyControlled(getMinConfirmPercent()) ||
                        currentFeeder->isPastMaxConfirmTime(RWDBDateTime(),getMaxConfirmTime(),getControlSendRetries()) )
                    {
                        currentFeeder->capBankControlStatusUpdate(pointChanges,getMinConfirmPercent(),getFailurePercent(),currentFeeder->getVarValueBeforeControl(),currentFeeder->getCurrentVarLoadPointValue(), currentFeeder->getCurrentVarPointQuality());
                    }
                    if( currentFeeder->getRecentlyControlledFlag() )
                    {
                        recentlyControlledFeeders++;
                    }
                }
                else if( getCurrentVarLoadPointId() > 0 )
                {
                    currentFeeder->capBankControlStatusUpdate(pointChanges,getMinConfirmPercent(),getFailurePercent(),getVarValueBeforeControl(),getCurrentVarLoadPointValue(), getCurrentVarPointQuality());
                    //setRecentlyControlledFlag(FALSE);
                    figureEstimatedVarLoadPointValue();
                }
                else
                {
                    currentFeeder->capBankControlStatusUpdate(pointChanges,0,0,getVarValueBeforeControl(),getCurrentVarLoadPointValue(), getCurrentVarPointQuality());
                    //setRecentlyControlledFlag(FALSE);
                    figureEstimatedVarLoadPointValue();
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Manual Only Sub Bus: " << getPAOName() << " does not have Var points to perform confirmation logic." << endl;
                    }
                }
            }
        }
        if( recentlyControlledFeeders == 0 )
        {
            setRecentlyControlledFlag(FALSE);
        }
        figureEstimatedVarLoadPointValue();
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid control method in: " << __FILE__ << " at: " << __LINE__ << endl;
    }

    //setNewPointDataReceivedFlag(FALSE);

    return returnBoolean;
}


BOOL CtiCCSubstationBus::capBankVerificationStatusUpdate(RWOrdered& pointChanges)
{
    BOOL returnBoolean = FALSE;
    char tempchar[64] = "";
    RWCString text = "";
    RWCString additional = "";

    BOOL vResult = FALSE; //fail

    for(LONG i=0;i<_ccfeeders.entries();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
        if (currentFeeder->getPAOId() == getCurrentVerificationFeederId())
        {
            RWSortedVector& ccCapBanks = currentFeeder->getCCCapBanks();
            CtiCCCapBank* currentCapBank = NULL;

            for(int j=0;j<ccCapBanks.entries();j++)
            {
               currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
               if (currentCapBank->getPAOId() == getCurrentVerificationCapBankId())
               {   

                   if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                   {
                       if( !_IGNORE_NOT_NORMAL_FLAG ||
                           getCurrentVarPointQuality() == NormalQuality )
                       {    
                           DOUBLE change;
                           if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
                           {
                               change = currentFeeder->getCurrentVarLoadPointValue() - currentFeeder->getVarValueBeforeControl();
                           }
                           else
                           {
                               change = getCurrentVarLoadPointValue() - getVarValueBeforeControl();
                           }
                           if( change < 0 )
                           {
                               {
                                   CtiLockGuard<CtiLogger> logger_guard(dout);
                                   dout << RWTime() << " - Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                               }
                               if (currentCapBank->getControlDeviceType().contains("CBC 70", RWCString::ignoreCase) && _USE_FLIP_FLAG == TRUE &&
                                   currentCapBank->getVCtrlIndex() == 1)            
                               {
                                   currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                                   setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                                   currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                                   //return returnBoolean;
                                   change = 0 - change;
                               }
                           }
                           DOUBLE ratio = change/currentCapBank->getBankSize();
                           if( ratio < getMinConfirmPercent()*.01 )
                           {
                               if( ratio < getFailurePercent()*.01 && getFailurePercent() != 0 && getMinConfirmPercent() != 0 )
                               {
                                   currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                                   text = RWCString("Var Change = ");
                                   text += doubleToString(ratio*100.0);
                                   text += "%, OpenFail";
                                   additional = RWCString("Feeder: ");
                                   additional = getPAOName();
                               }
                               else if( getMinConfirmPercent() != 0 )
                               {
                                   currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                                   text = RWCString("Var Change = ");
                                   text += doubleToString(ratio*100.0);
                                   text += "%, OpenQuestionable";
                                   additional = RWCString("Feeder: ");
                                   additional = getPAOName();
                               }
                               else
                               {
                                   currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                   vResult = TRUE;
                               }
                           }        
                           else
                           {
                               currentCapBank->setControlStatus(CtiCCCapBank::Open);
                               vResult = TRUE;
                           }
                       }
                       else
                       {
                           char tempchar[80];
                           currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                           text = RWCString("Non Normal Var Quality = ");
                           _ltoa(getCurrentVarPointQuality(),tempchar,10);
                           text += tempchar;
                           text += "%, OpenQuestionable";
                           additional = RWCString("Feeder: ");
                           additional = getPAOName();
                       }
                   }
                   else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                   {
                       if( !_IGNORE_NOT_NORMAL_FLAG ||
                           getCurrentVarPointQuality() == NormalQuality )
                       {
                           DOUBLE change;
                           if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
                           {
                               change = currentFeeder->getVarValueBeforeControl() - currentFeeder->getCurrentVarLoadPointValue();
                           }
                           else
                           {
                               change = getVarValueBeforeControl() - getCurrentVarLoadPointValue();
                           }
                           if( change < 0 )
                           {
                               {
                                   CtiLockGuard<CtiLogger> logger_guard(dout);
                                   dout << RWTime() << " - Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                               }
                               if (currentCapBank->getControlDeviceType().contains("CBC 70", RWCString::ignoreCase) && _USE_FLIP_FLAG == TRUE &&
                                   currentCapBank->getVCtrlIndex() == 1)            
                               {
                                   currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                                   setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                                   currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                                   //return returnBoolean;
                                   change = 0 - change;
                               }
                           }
                           DOUBLE ratio = change/currentCapBank->getBankSize();
                           if( ratio < getMinConfirmPercent()*.01 )
                           {
                               if( ratio < getFailurePercent()*.01 && getFailurePercent() != 0 && getMinConfirmPercent() != 0 )
                               {
                                   currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                                   text = RWCString("Var Change = ");
                                   text += doubleToString(ratio*100.0);
                                   text += "%, CloseFail";
                                   additional = RWCString("Feeder: ");
                                   additional = getPAOName();
                               }
                               else if( getMinConfirmPercent() != 0 )
                               {
                                   currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                                   text = RWCString("Var Change = ");
                                   text += doubleToString(ratio*100.0);
                                   text += "%, CloseQuestionable";
                                   additional = RWCString("Feeder: ");
                                   additional = getPAOName();
                               }
                               else
                               {
                                   currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                   vResult = TRUE;
                               }
                           }
                           else
                           {
                               currentCapBank->setControlStatus(CtiCCCapBank::Close);
                               vResult = TRUE;
                           }
                       }
                       else
                       {
                           char tempchar[80];
                           currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                           text = RWCString("Non Normal Var Quality = ");
                           _ltoa(getCurrentVarPointQuality(),tempchar,10);
                           text += tempchar;
                           text += "%, CloseQuestionable";
                           additional = RWCString("Feeder: ");
                           additional = getPAOName();
                       }
                   }
                   else
                   {
                       CtiLockGuard<CtiLogger> logger_guard(dout);
                       dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                       returnBoolean = FALSE;
                      // break;
                   }

                   if( currentCapBank->getStatusPointId() > 0 )
                   {
                       if( text.length() > 0 )
                       {//if control failed or questionable, create event to be sent to dispatch
                           long tempLong = currentCapBank->getStatusPointId();
                           pointChanges.insert(new CtiSignalMsg(tempLong,0,text,additional,CapControlLogType,SignalEvent));
                           ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                       }
                       pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
                       ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                       currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                   }
                   else
                   {
                       CtiLockGuard<CtiLogger> logger_guard(dout);
                       dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                       << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                   }

                   if (currentCapBank->updateVerificationState())
                   {
                       returnBoolean = TRUE;
                       currentCapBank->setPerformingVerificationFlag(FALSE);
                       currentFeeder->setPerformingVerificationFlag(FALSE);
                       setBusUpdatedFlag(TRUE);
                       return returnBoolean;
                   }

                   break;
               }
            }
            break;
        }
    }
    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isVarCheckNeeded

    Returns a boolean if the control interval is up or if new point data has
    been received for all the points associated with the bus.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isVarCheckNeeded(const RWDBDateTime& currentDateTime)
{
    BOOL returnBoolean = FALSE;

    if( getControlInterval() > 0 )
    {
        returnBoolean = (getNextCheckTime().seconds() <= currentDateTime.seconds());
    }
    else
    {
        if( !_controlmethod.compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
        {
            if( _ccfeeders.entries() > 0 )
            {
                //confirm cap bank changes on just the feeder var value
                if( getRecentlyControlledFlag() )
                {
                    try
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()];

                        if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                        {
                            if( currentFeeder->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = TRUE;
                            }
                        }
                        else
                        {
                            for(LONG i=0;i<_ccfeeders.entries();i++)
                            {
                                currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

                                if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                                {
                                    if( currentFeeder->getNewPointDataReceivedFlag() )
                                    {
                                        returnBoolean = TRUE;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }

                    if( !returnBoolean )
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()];

                        if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                        {
                            if( currentFeeder->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = TRUE;
                            }
                        }
                        else
                        {
                            for(LONG i=0;i<_ccfeeders.entries();i++)
                            {
                                currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

                                if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                                {
                                    if( currentFeeder->getNewPointDataReceivedFlag() )
                                    {
                                        returnBoolean = TRUE;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                //only try to control bus optimized if we have new var values
                //for the bus and all feeders off the bus
                else if( _newpointdatareceivedflag )
                {
                    returnBoolean = TRUE;
                    for(LONG i=0;i<_ccfeeders.entries();i++)
                    {
                        if( !((CtiCCFeeder*)_ccfeeders[i])->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = FALSE;
                            break;
                        }
                    }
                }
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
        {
            if( _ccfeeders.entries() > 0 )
            {
                for(LONG i=0;i<_ccfeeders.entries();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if( currentFeeder->getStrategyId() > 0  && 
                        currentFeeder->getControlInterval() > 0 )
                    {
                        returnBoolean = (getNextCheckTime().seconds() <= currentDateTime.seconds());
                    }

                    if( ((CtiCCFeeder*)_ccfeeders[i])->getNewPointDataReceivedFlag() )
                    {
                        returnBoolean = TRUE;
                        break;
                    }
                }
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) )
        {
            if( _ccfeeders.entries() > 0 )
            {
                returnBoolean = _newpointdatareceivedflag;
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::ManualOnlyControlMethod,RWCString::ignoreCase) )
        {
            if( _newpointdatareceivedflag )
            {
                returnBoolean = TRUE;
            }
            else
            {
                for(LONG i=0;i<_ccfeeders.entries();i++)
                {
                    if( ((CtiCCFeeder*)_ccfeeders[i])->getNewPointDataReceivedFlag() )
                    {
                        returnBoolean = TRUE;
                        break;
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid Control Method in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isConfirmCheckNeeded

    Returns a boolean if the if new point data has
    been received for all the points associated with the bus.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isConfirmCheckNeeded()
{
    BOOL returnBoolean = FALSE;

    if( getRecentlyControlledFlag() )
    {
        if( !_controlmethod.compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
        {
            if( _ccfeeders.entries() > 0 )
            {
                //confirm cap bank changes on just the feeder var value
                try
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()];
    
                    if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                    {
                        if( currentFeeder->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = TRUE;
                        }
                    }
                    else
                    {
                        for(LONG i=0;i<_ccfeeders.entries();i++)
                        {
                            currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
    
                            if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                            {
                                if( currentFeeder->getNewPointDataReceivedFlag() )
                                {
                                    returnBoolean = TRUE;
                                }
                                break;
                            }
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
    
                if( !returnBoolean )
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()];
    
                    if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                    {
                        if( currentFeeder->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = TRUE;
                        }
                    }
                    else
                    {
                        for(LONG i=0;i<_ccfeeders.entries();i++)
                        {
                            currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
    
                            if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                            {
                                if( currentFeeder->getNewPointDataReceivedFlag() )
                                {
                                    returnBoolean = TRUE;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
        {
            if( _ccfeeders.entries() > 0 )
            {
                for(LONG i=0;i<_ccfeeders.entries();i++)
                {
                    if( ((CtiCCFeeder*)_ccfeeders[i])->getNewPointDataReceivedFlag() )
                    {
                        returnBoolean = TRUE;
                        break;
                    }
                }
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) )
        {
            if( _ccfeeders.entries() > 0 )
            {
                returnBoolean = _newpointdatareceivedflag;
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::ManualOnlyControlMethod,RWCString::ignoreCase) )
        {
            if( _ccfeeders.entries() > 0 )
            {
                returnBoolean = _newpointdatareceivedflag;
                if( !returnBoolean )
                {
                    for(LONG i=0;i<_ccfeeders.entries();i++)
                    {
                        if( ((CtiCCFeeder*)_ccfeeders[i])->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = TRUE;
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid Control Method in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPeakDay

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isPeakDay()
{
    //-------------------------------------
    //Need to check if it is a holiday today
    //also, but we must wait until there is
    //a dll with a function to do this
    //-------------------------------------
    RWTime now;
    struct tm start_tm;

    now.extract(&start_tm);

    if( _daysofweek(start_tm.tm_wday) == 'Y' &&
        ( _daysofweek(7) == 'Y' ||
          !CtiHolidayManager::getInstance().isHoliday() ) )
        return TRUE;
    else
        return FALSE;
}

/*---------------------------------------------------------------------------
    isPeakDay

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::clearOutNewPointReceivedFlags()
{
    setNewPointDataReceivedFlag(FALSE);
    for(int i=0;i<_ccfeeders.entries();i++)
    {
        ((CtiCCFeeder*)_ccfeeders[i])->setNewPointDataReceivedFlag(FALSE);
    }
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isAlreadyControlled()
{
    BOOL returnBoolean = FALSE;

    if( !_IGNORE_NOT_NORMAL_FLAG ||
        getCurrentVarPointQuality() == NormalQuality )
    {
        if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) ||
            !_controlmethod.compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
        {
            if( getMinConfirmPercent() > 0 )
            {
                for(LONG i=0;i<_ccfeeders.entries();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if( currentFeeder->getRecentlyControlledFlag() )
                    {
                        if( currentFeeder->isAlreadyControlled(getMinConfirmPercent()) )
                        {
                            returnBoolean = TRUE;
                            break;
                        }
                    }
                }
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) )
        {
            if( getMinConfirmPercent() > 0 )
            {
                DOUBLE oldCalcValue = getVarValueBeforeControl();
                DOUBLE newCalcValue = getCurrentVarLoadPointValue();
                for(LONG i=0;i<_ccfeeders.entries();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()];
    
                    if( getLastFeederControlledPAOId() == currentFeeder->getPAOId() )
                    {
                        RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                        for(LONG j=0;j<ccCapBanks.entries();j++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                            if( currentCapBank->getPAOId() == currentFeeder->getLastCapBankControlledDeviceId() )
                            {
                                if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                                {
                                    DOUBLE change = newCalcValue - oldCalcValue;
                                    DOUBLE ratio = change/currentCapBank->getBankSize();
                                    if( ratio >= getMinConfirmPercent()*.01 )
                                    {
                                        returnBoolean = TRUE;
                                    }
                                    else
                                    {
                                        returnBoolean = FALSE;
                                    }
                                }
                                else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                                {
                                    DOUBLE change = oldCalcValue - newCalcValue;
                                    DOUBLE ratio = change/currentCapBank->getBankSize();
                                    if( ratio >= getMinConfirmPercent()*.01 )
                                    {
                                        returnBoolean = TRUE;
                                    }
                                    else
                                    {
                                        returnBoolean = FALSE;
                                    }
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                                    returnBoolean = FALSE;
                                }
                                break;
                            }
                        }
                    }
                    else
                    {
                        for(LONG i=0;i<_ccfeeders.entries();i++)
                        {
                            currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
    
                            if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                            {
                                if( getLastFeederControlledPAOId() == currentFeeder->getPAOId() )
                                {
                                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                                    for(LONG j=0;j<ccCapBanks.entries();j++)
                                    {
                                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                        if( currentCapBank->getPAOId() == currentFeeder->getLastCapBankControlledDeviceId() )
                                        {
                                            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                                            {
                                                DOUBLE change = newCalcValue - oldCalcValue;
                                                DOUBLE ratio = change/currentCapBank->getBankSize();
                                                if( ratio >= getMinConfirmPercent()*.01 )
                                                {
                                                    returnBoolean = TRUE;
                                                }
                                                else
                                                {
                                                    returnBoolean = FALSE;
                                                }
                                            }
                                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                                            {
                                                DOUBLE change = oldCalcValue - newCalcValue;
                                                DOUBLE ratio = change/currentCapBank->getBankSize();
                                                if( ratio >= getMinConfirmPercent()*.01 )
                                                {
                                                    returnBoolean = TRUE;
                                                }
                                                else
                                                {
                                                    returnBoolean = FALSE;
                                                }
                                            }
                                            else
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                                                returnBoolean = FALSE;
                                            }
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::ManualOnlyControlMethod,RWCString::ignoreCase) )
        {
            if( getMinConfirmPercent() > 0 )
            {
                for(LONG i=0;i<_ccfeeders.entries();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if( currentFeeder->getRecentlyControlledFlag() )
                    {
                        if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                        {
                            if( currentFeeder->isAlreadyControlled(getMinConfirmPercent()) )
                            {
                                returnBoolean = TRUE;
                                break;
                            }
                        }
                        else
                        {
                            DOUBLE oldCalcValue = getVarValueBeforeControl();
                            DOUBLE newCalcValue = getCurrentVarLoadPointValue();
                            for(LONG i=0;i<_ccfeeders.entries();i++)
                            {
                                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()];

                                if( getLastFeederControlledPAOId() == currentFeeder->getPAOId() )
                                {
                                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                                    for(LONG j=0;j<ccCapBanks.entries();j++)
                                    {
                                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                        if( currentCapBank->getPAOId() == currentFeeder->getLastCapBankControlledDeviceId() )
                                        {
                                            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                                            {
                                                DOUBLE change = newCalcValue - oldCalcValue;
                                                DOUBLE ratio = change/currentCapBank->getBankSize();
                                                if( ratio >= getMinConfirmPercent()*.01 )
                                                {
                                                    returnBoolean = TRUE;
                                                }
                                                else
                                                {
                                                    returnBoolean = FALSE;
                                                }
                                            }
                                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                                            {
                                                DOUBLE change = oldCalcValue - newCalcValue;
                                                DOUBLE ratio = change/currentCapBank->getBankSize();
                                                if( ratio >= getMinConfirmPercent()*.01 )
                                                {
                                                    returnBoolean = TRUE;
                                                }
                                                else
                                                {
                                                    returnBoolean = FALSE;
                                                }
                                            }
                                            else
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                                                returnBoolean = FALSE;
                                            }
                                            break;
                                        }
                                    }
                                }
                                else
                                {
                                    for(LONG i=0;i<_ccfeeders.entries();i++)
                                    {
                                        currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

                                        if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                                        {
                                            if( getLastFeederControlledPAOId() == currentFeeder->getPAOId() )
                                            {
                                                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                                                for(LONG j=0;j<ccCapBanks.entries();j++)
                                                {
                                                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                                    if( currentCapBank->getPAOId() == currentFeeder->getLastCapBankControlledDeviceId() )
                                                    {
                                                        if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                                                        {
                                                            DOUBLE change = newCalcValue - oldCalcValue;
                                                            DOUBLE ratio = change/currentCapBank->getBankSize();
                                                            if( ratio >= getMinConfirmPercent()*.01 )
                                                            {
                                                                returnBoolean = TRUE;
                                                            }
                                                            else
                                                            {
                                                                returnBoolean = FALSE;
                                                            }
                                                        }
                                                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                                                        {
                                                            DOUBLE change = oldCalcValue - newCalcValue;
                                                            DOUBLE ratio = change/currentCapBank->getBankSize();
                                                            if( ratio >= getMinConfirmPercent()*.01 )
                                                            {
                                                                returnBoolean = TRUE;
                                                            }
                                                            else
                                                            {
                                                                returnBoolean = FALSE;
                                                            }
                                                        }
                                                        else
                                                        {
                                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                                            dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                                                            returnBoolean = FALSE;
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid control method in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }

    return returnBoolean;
}


CtiCCSubstationBus& CtiCCSubstationBus::getNextCapBankToVerify()
{
    _currentVerificationFeederId = -1;
    _currentVerificationCapBankId = -1;

    //_currentCapBankToVerifyId = (LONG) _verificationCapBankIds.back();
    //_verificationCapBankIds.pop_back();

    if (getOverlappingVerificationFlag())
    {
        setCapBanksToVerifyFlags(getVerificationStrategy());
    }
    
    for(LONG i=0;i<_ccfeeders.entries();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

        if( currentFeeder->getVerificationFlag() && !currentFeeder->getVerificationDoneFlag() )
        {
            _currentVerificationFeederId = currentFeeder->getPAOId();

            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
            for(LONG j=0;j<ccCapBanks.entries();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getVerificationFlag() && !currentCapBank->getVerificationDoneFlag() )
                {  
                    _currentVerificationCapBankId = currentCapBank->getPAOId();
                    return *this;
                }
            }
            currentFeeder->setVerificationDoneFlag(TRUE);
            _currentVerificationFeederId = -1;
        }
    }
    setBusUpdatedFlag(TRUE);

    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setVerificationFlag(BOOL verificationFlag)
{

    if( _verificationFlag != verificationFlag )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _verificationFlag = verificationFlag;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setPerformingVerificationFlag(BOOL performingVerificationFlag)
{
    if( _performingVerificationFlag != performingVerificationFlag )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _performingVerificationFlag = performingVerificationFlag;

    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setVerificationDoneFlag(BOOL verificationDoneFlag)
{
    if( _verificationDoneFlag != verificationDoneFlag )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _verificationDoneFlag = verificationDoneFlag;

    return *this;
}


CtiCCSubstationBus& CtiCCSubstationBus::setOverlappingVerificationFlag(BOOL overlapFlag)
{
    if( _overlappingSchedulesVerificationFlag != overlapFlag )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _overlappingSchedulesVerificationFlag = overlapFlag;

    return *this;
}
 

CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVerificationFeederId(LONG feederId)
{
    if( _currentVerificationFeederId != feederId )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _currentVerificationFeederId = feederId;

    return *this;
}


CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVerificationCapBankId(LONG capBankId)
{
    if( _currentVerificationCapBankId != capBankId )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _currentVerificationCapBankId = capBankId;

    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVerificationCapBankState(LONG status)
{
    if( _currentCapBankToVerifyAssumedOrigState != status )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _currentCapBankToVerifyAssumedOrigState = status;

    return *this;
}




CtiCCSubstationBus& CtiCCSubstationBus::sendNextCapBankVerificationControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    CtiRequestMsg* request = NULL;
    for (LONG i = 0; i < _ccfeeders.entries(); i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*) _ccfeeders[i];
        if( currentFeeder->getPAOId() == _currentVerificationFeederId )
        {
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
            for(LONG j=0;j<ccCapBanks.entries();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getPAOId() == _currentVerificationCapBankId )
                {
                    if (currentCapBank->getVCtrlIndex() == 1)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ***WARNING*** Should not get here! vCtrlIdx = 1, sendNextVControl? NO. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else if (currentCapBank->getVCtrlIndex() == 2)
                    {
                        if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                        {
                            request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                        }
                        else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                        {
                            request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges,getCurrentVarLoadPointValue(), getDecimalPlaces());
                        }

                    }
                    else if (currentCapBank->getVCtrlIndex() == 3)
                    {
                        if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                        {
                            request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges,getCurrentVarLoadPointValue(), getDecimalPlaces());
                        }
                        else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                        {
                            request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                        }

                    }
                    else if (currentCapBank->getVCtrlIndex() == 5)
                    {
                        request = NULL;
                    }
                    if( request != NULL )
                    {
                        pilMessages.insert(request);
                        setLastOperationTime(currentDateTime);
                        setLastFeederControlledPAOId(currentFeeder->getPAOId());
                        setLastFeederControlledPosition(i);
                        currentFeeder->setLastCapBankControlledDeviceId( currentCapBank->getPAOId());
                        currentFeeder->setLastOperationTime(currentDateTime);
                       ((CtiCCFeeder*)_ccfeeders[i])->setLastOperationTime(currentDateTime);
                        setVarValueBeforeControl(getCurrentVarLoadPointValue());
                        setCurrentDailyOperations(getCurrentDailyOperations() + 1);
                        figureEstimatedVarLoadPointValue();
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.insert(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }
                        //setRecentlyControlledFlag(TRUE);
                        setBusUpdatedFlag(TRUE);
                        return *this;
                    }

                }
            }
            
        }
    }

    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::startVerificationOnCapBank(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    //get CapBank to perform verification on...subbus stores, currentCapBankToVerifyId

    CtiRequestMsg* request = NULL;

    for (LONG i = 0; i < _ccfeeders.entries(); i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*) _ccfeeders[i];
        if( currentFeeder->getPAOId() == _currentVerificationFeederId )
        {
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
            for(LONG j=0;j<ccCapBanks.entries();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getPAOId() == _currentVerificationCapBankId )
                {
                
                    currentCapBank->initVerificationControlStatus();
                    setCurrentVerificationCapBankState(currentCapBank->getAssumedOrigVerificationState());
                    currentCapBank->setAssumedOrigVerificationState(getCurrentVerificationCapBankOrigState());
                    currentCapBank->setPreviousVerificationControlStatus(-1);
                    currentCapBank->setVCtrlIndex(1); //1st control sent
                    currentCapBank->setPerformingVerificationFlag(TRUE);
                    currentFeeder->setPerformingVerificationFlag(TRUE);


                    if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                    {
                        request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges,getCurrentVarLoadPointValue(), getDecimalPlaces());
                    }
                    else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                    {
                        request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                    }


                    if( request != NULL )
                    {
                        pilMessages.insert(request);
                        setLastOperationTime(currentDateTime);
                        setLastFeederControlledPAOId(currentFeeder->getPAOId());
                        setLastFeederControlledPosition(i);
                        currentFeeder->setLastCapBankControlledDeviceId( currentCapBank->getPAOId());
                        currentFeeder->setLastOperationTime(currentDateTime);
                       //((CtiCCFeeder*)_ccfeeders[currentPosition])->setLastOperationTime(currentDateTime);
                        setVarValueBeforeControl(getCurrentVarLoadPointValue());
                        setCurrentDailyOperations(getCurrentDailyOperations() + 1);
                        figureEstimatedVarLoadPointValue();
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.insert(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }
                        //setRecentlyControlledFlag(TRUE);
                    }
                    //setNewPointDataReceivedFlag(FALSE);
                    //regardless what happened the substation bus should be should be sent to the client
                    setBusUpdatedFlag(TRUE);
                    return *this;
                }
            }
        }
    }

    
    return *this;
}
/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isVerificationAlreadyControlled()
{
    BOOL returnBoolean = FALSE;

    if( !_IGNORE_NOT_NORMAL_FLAG ||
        getCurrentVarPointQuality() == NormalQuality )
    {
        if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) ||
            !_controlmethod.compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
        {
            if( getMinConfirmPercent() > 0 )
            {                   
                for(LONG i = 0; i < _ccfeeders.entries(); i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if( currentFeeder->getPAOId() == getCurrentVerificationFeederId() )
                    {
                        if( currentFeeder->isVerificationAlreadyControlled(getMinConfirmPercent()) )
                        {
                            returnBoolean = TRUE;
                            break;
                        }
                    }
                } 
            }
        }
        else if( !_controlmethod.compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) )
        {
            if( getMinConfirmPercent() > 0 )
            {
                DOUBLE oldCalcValue = getVarValueBeforeControl();
                DOUBLE newCalcValue = getCurrentVarLoadPointValue();
                for(LONG i=0;i<_ccfeeders.entries();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

                    if( currentFeeder->getPAOId() == getCurrentVerificationFeederId() )
                    {
                        RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                        for(LONG j=0;j<ccCapBanks.entries();j++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                            if( currentCapBank->getPAOId() == getCurrentVerificationCapBankId() )
                            {
                                if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                    currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                                {
                                    DOUBLE change = newCalcValue - oldCalcValue;
                                    DOUBLE ratio = abs(change/currentCapBank->getBankSize());
                                    if( ratio >= getMinConfirmPercent()*.01 )
                                    {
                                        returnBoolean = TRUE;
                                    }
                                    else
                                    {
                                        returnBoolean = FALSE;
                                    }
                                }
                                /*else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                                {
                                    DOUBLE change = oldCalcValue - newCalcValue;
                                    DOUBLE ratio = change/currentCapBank->getBankSize();
                                    if( ratio >= getMinConfirmPercent()*.01 )
                                    {
                                        returnBoolean = TRUE;
                                    }
                                    else
                                    {
                                        returnBoolean = FALSE;
                                    }
                                }  */
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                                    returnBoolean = FALSE;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        else
        {
            returnBoolean = isAlreadyControlled();
        }
    }
    return returnBoolean;
}
/*---------------------------------------------------------------------------
    isPastMaxConfirmTime

    Returns a boolean if the last control is past the maximum confirm time.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isPastMaxConfirmTime(const RWDBDateTime& currentDateTime)
{
    BOOL returnBoolean = FALSE;

    if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
    {
        for(LONG i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders[i];
            if (currentCCFeeder->getStrategyId() > 0)
            {

            }
            if( currentCCFeeder->getRecentlyControlledFlag() &&
                currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }
    else
    {
        if( ((getLastOperationTime().seconds() + (getMaxConfirmTime()/_SEND_TRIES)) <= currentDateTime.seconds()) ||
            ((getLastOperationTime().seconds() + (getMaxConfirmTime()/(getControlSendRetries()+1))) <= currentDateTime.seconds()) )
        {
            returnBoolean = TRUE;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    checkForAndPerformSendRetry

    Returns boolean if .
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::checkForAndPerformSendRetry(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    BOOL returnBoolean = FALSE;

    if( !_controlmethod.compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
    {
        for(LONG i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders[i];
            if( currentCCFeeder->getRecentlyControlledFlag() &&
                currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) &&
                currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, pilMessages, getMaxConfirmTime()) )
            {
                setLastOperationTime(currentDateTime);
                returnBoolean = TRUE;
                break;
            }
            else if (getVerificationFlag() && getPerformingVerificationFlag() &&
                     currentCCFeeder->getPAOId() == getCurrentVerificationFeederId() && 
                     currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) &&
                     currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, pilMessages, getMaxConfirmTime()) )
            {
                setLastOperationTime(currentDateTime);
                returnBoolean = TRUE;
                break;
            }

        }
    }
    else
    {
        for(LONG i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders[i];
            if( currentCCFeeder->getPAOId() == getLastFeederControlledPAOId() &&
                currentCCFeeder->getRecentlyControlledFlag() &&
                currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, pilMessages, getMaxConfirmTime()) )
            {
                setLastOperationTime(currentDateTime);
                returnBoolean = TRUE;
                break;
            }
            else if (getVerificationFlag() && getPerformingVerificationFlag() &&
                     currentCCFeeder->getPAOId() == getCurrentVerificationFeederId() &&
                     currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, pilMessages, getMaxConfirmTime()) )
                     
            {
                setLastOperationTime(currentDateTime);
                returnBoolean = TRUE;
                break;
            }

        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    {
        RWDBTable dynamicCCSubstationBusTable = getDatabase().table( "dynamicccsubstationbus" );

        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCSubstationBusTable.updater();

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==_paoid);

            updater << dynamicCCSubstationBusTable["currentvarpointvalue"].assign( _currentvarloadpointvalue )
            << dynamicCCSubstationBusTable["currentwattpointvalue"].assign( _currentwattloadpointvalue )
            << dynamicCCSubstationBusTable["nextchecktime"].assign( (RWDBDateTime)_nextchecktime )
            << dynamicCCSubstationBusTable["newpointdatareceivedflag"].assign( RWCString((_newpointdatareceivedflag?'Y':'N')) )
            << dynamicCCSubstationBusTable["busupdatedflag"].assign( RWCString((_busupdatedflag?'Y':'N')) );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }

            updater.clear();

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==_paoid);

            updater << dynamicCCSubstationBusTable["lastcurrentvarupdatetime"].assign( (RWDBDateTime)_lastcurrentvarpointupdatetime )
            << dynamicCCSubstationBusTable["estimatedvarpointvalue"].assign( _estimatedvarloadpointvalue )
            << dynamicCCSubstationBusTable["currentdailyoperations"].assign( _currentdailyoperations )
            << dynamicCCSubstationBusTable["peaktimeflag"].assign( RWCString((_peaktimeflag?'Y':'N')) );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }

            updater.clear();

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==_paoid);

            updater << dynamicCCSubstationBusTable["recentlycontrolledflag"].assign( RWCString((_recentlycontrolledflag?'Y':'N')) )
            << dynamicCCSubstationBusTable["lastoperationtime"].assign( (RWDBDateTime)_lastoperationtime )
            << dynamicCCSubstationBusTable["varvaluebeforecontrol"].assign( _varvaluebeforecontrol )
            << dynamicCCSubstationBusTable["lastfeederpaoid"].assign( _lastfeedercontrolledpaoid );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_verificationFlag?'Y':'N');
            addFlags[1] = (_performingVerificationFlag?'Y':'N');
            addFlags[2] = (_verificationDoneFlag?'Y':'N');
            addFlags[3] = (_overlappingSchedulesVerificationFlag?'Y':'N');
            _additionalFlags = RWCString(RWCString(*addFlags) + RWCString(*(addFlags+1)) + RWCString(*(addFlags+2)) +  RWCString(*(addFlags+3)) + RWCString(*(addFlags + 4), 16));

            updater.clear();

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==_paoid);

            updater << dynamicCCSubstationBusTable["lastfeederposition"].assign( _lastfeedercontrolledposition )
            << dynamicCCSubstationBusTable["ctitimestamp"].assign((RWDBDateTime)currentDateTime)
            << dynamicCCSubstationBusTable["powerfactorvalue"].assign( _powerfactorvalue )
            << dynamicCCSubstationBusTable["kvarsolution"].assign( _kvarsolution )
            << dynamicCCSubstationBusTable["estimatedpfvalue"].assign( _estimatedpowerfactorvalue )
            << dynamicCCSubstationBusTable["currentvarpointquality"].assign( _currentvarpointquality )
            << dynamicCCSubstationBusTable["waivecontrolflag"].assign( RWCString((_waivecontrolflag?'Y':'N')) )
            << dynamicCCSubstationBusTable["additionalflags"].assign( _additionalFlags )
            << dynamicCCSubstationBusTable["currverifycbid"].assign( _currentVerificationCapBankId )
            << dynamicCCSubstationBusTable["currverifyfeederid"].assign( _currentVerificationFeederId )
            << dynamicCCSubstationBusTable["currverifycborigstate"].assign( _currentCapBankToVerifyAssumedOrigState )
            << dynamicCCSubstationBusTable["verificationstrategy"].assign( _verificationStrategy )
            << dynamicCCSubstationBusTable["cbinactivitytime"].assign( _capBankToVerifyInactivityTime )
            << dynamicCCSubstationBusTable["currentvoltpointvalue"].assign( _currentvoltloadpointvalue );

             
            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted substation bus into DynamicCCSubstationBus: " << getPAOName() << endl;
            }
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};

            RWDBInserter inserter = dynamicCCSubstationBusTable.inserter();

            inserter << _paoid
            << _currentvarloadpointvalue
            << _currentwattloadpointvalue
            << _nextchecktime
            << RWCString((_newpointdatareceivedflag?'Y':'N'))
            << RWCString((_busupdatedflag?'Y':'N'))
            << _lastcurrentvarpointupdatetime
            << _estimatedvarloadpointvalue
            << _currentdailyoperations
            << RWCString((_peaktimeflag?'Y':'N'))
            << RWCString((_recentlycontrolledflag?'Y':'N'))
            << _lastoperationtime
            << _varvaluebeforecontrol
            << _lastfeedercontrolledpaoid
            << _lastfeedercontrolledposition
            << currentDateTime
            << _powerfactorvalue
            << _kvarsolution
            << _estimatedpowerfactorvalue
            << _currentvarpointquality
            << RWCString((_waivecontrolflag?'Y':'N'))
            << RWCString(*addFlags, 20)
            << _currentVerificationCapBankId
            << _currentVerificationFeederId 
            << _currentCapBankToVerifyAssumedOrigState
            << _verificationStrategy
            << _capBankToVerifyInactivityTime
            << _currentvoltloadpointvalue;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
            }

            inserter.execute( conn );

            if(inserter.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << inserter.asString() << endl;
                }
            }
        }
    }
}

/*-------------------------------------------------------------------------
    calculatePowerFactor


--------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::calculatePowerFactor(DOUBLE kvar, DOUBLE kw)
{
    DOUBLE newPowerFactorValue = 1.0;
    DOUBLE kva = 0.0;

    kva = sqrt((kw*kw)+(kvar*kvar));

    if( kva != 0.0 )
    {
        if( kw < 0 )
        {
            kw = -kw;
        }
        newPowerFactorValue = kw / kva;
        //check if this is leading
        if( kvar < 0.0 && newPowerFactorValue != 1.0 )
        {
            newPowerFactorValue = 2.0-newPowerFactorValue;
        }
    }

    return newPowerFactorValue;
}

/*-------------------------------------------------------------------------
    convertKQToKVAR

    Converts KQ to KVAR, needs kw also
--------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::convertKQToKVAR(DOUBLE kq, DOUBLE kw)
{
    DOUBLE returnKVAR = 0.0;
    returnKVAR = ((2.0*kq)-kw)/SQRT3;
    return returnKVAR;
}

/*-------------------------------------------------------------------------
    convertKVARToKQ

    Converts KVAR to KQ, needs kw also
--------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::convertKVARToKQ(DOUBLE kvar, DOUBLE kw)
{
    DOUBLE returnKQ = 0.0;
    returnKQ = ((SQRT3*kvar)+kw)/2.0;
    return returnKQ;
}




/////////////////////////////////////////////////////////////////////////////
// LOTS OF WORK TO DO HERE!!!!
/////////////////////////////////////////////////////////////////////////////

BOOL CtiCCSubstationBus::isBusPerformingVerification()
{
    return _performingVerificationFlag;

}
BOOL CtiCCSubstationBus::isVerificationPastMaxConfirmTime(const RWDBDateTime& currentDateTime)
{
    if (getLastOperationTime().seconds() + getMaxConfirmTime() <= currentDateTime.seconds() )
        return TRUE;
    else
        return FALSE;
}
BOOL CtiCCSubstationBus::capBankVerificationDone(RWOrdered& pointChanges)
{
    BOOL returnBool = FALSE;

   /*if (_currentVerificationCapBank->getVerificationState() == VerificationSuccess ||
        _currentVerificationCapBank->getVerificationState() == VerificationFail)
    {
        return TRUE;
    }  */


    return returnBool;

}
BOOL CtiCCSubstationBus::areThereMoreCapBanksToVerify()
{
    getNextCapBankToVerify();
    if (getCurrentVerificationCapBankId() != -1)
    {
        setPerformingVerificationFlag(TRUE);

        return TRUE;
    }
    else
    {
        for(LONG i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
            //currentFeeder->setVerificationFlag(FALSE);
            currentFeeder->setPerformingVerificationFlag(FALSE);
            currentFeeder->setVerificationDoneFlag(TRUE);

            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
            for(LONG j=0;j<ccCapBanks.entries();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                //currentCapBank->setVerificationFlag(FALSE);
                currentCapBank->setPerformingVerificationFlag(FALSE);
                currentCapBank->setVerificationDoneFlag(TRUE);
            }
        }
        setPerformingVerificationFlag(FALSE);
        setBusUpdatedFlag(TRUE);
        return FALSE;
    }
}
   
    
/*list <LONG> CtiCCSubstationBus::getVerificationCapBankList()
{
    return _verificationCapBankIds;
} */

void CtiCCSubstationBus::setVerificationStrategy(int verificationStrategy)
{
    if( _verificationStrategy != verificationStrategy )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _verificationStrategy = verificationStrategy;
}

int CtiCCSubstationBus::getVerificationStrategy(void) const
{
    return _verificationStrategy;
}


void CtiCCSubstationBus::setCapBankInactivityTime(LONG capBankToVerifyInactivityTime)
{
    if( _capBankToVerifyInactivityTime != capBankToVerifyInactivityTime )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _capBankToVerifyInactivityTime = capBankToVerifyInactivityTime;
}

LONG CtiCCSubstationBus::getCapBankInactivityTime(void) const
{
    return _capBankToVerifyInactivityTime;
}


CtiCCSubstationBus& CtiCCSubstationBus::setCapBanksToVerifyFlags(int verificationStrategy)
{
    LONG x, j;
    //_verificationCapBankIds.clear();

    switch (verificationStrategy)
    {
        //case ALLBANKS:
        case CtiPAOScheduleManager::AllBanks:
        {
            for (x = 0; x < _ccfeeders.entries(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[x];
                if (!getOverlappingVerificationFlag())
                {    
                    currentFeeder->setVerificationFlag(TRUE);
                    currentFeeder->setVerificationDoneFlag(FALSE);
                }
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(j=0;j<ccCapBanks.entries();j++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                    if (!currentCapBank->getOperationalState().compareTo(CtiCCCapBank::SwitchedOperationalState,RWCString::ignoreCase))
                    {

                        if (!getOverlappingVerificationFlag())
                        {
                            currentCapBank->setVerificationFlag(TRUE);
                            currentCapBank->setVerificationDoneFlag(FALSE);
                            currentCapBank->setVCtrlIndex(0);
                        }
                        else
                        {
                            if (!currentCapBank->getVerificationDoneFlag())
                            {
                                currentCapBank->setVerificationFlag(TRUE);
                                currentCapBank->setVerificationDoneFlag(FALSE);
                                currentCapBank->setVCtrlIndex(0);
                                currentFeeder->setVerificationFlag(TRUE);
                                currentFeeder->setVerificationDoneFlag(FALSE);
                            }
                            else
                            {
                                currentFeeder->setVerificationDoneFlag(TRUE);
                            }
                        }
                    }
                }
            }
            break;
        }
        case CtiPAOScheduleManager::FailedAndQuestionableBanks:
        {
            for (x = 0; x < _ccfeeders.entries(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[x];
                if (!getOverlappingVerificationFlag())
                {    
                    currentFeeder->setVerificationFlag(TRUE);
                    currentFeeder->setVerificationDoneFlag(FALSE);
                }
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(j=0;j<ccCapBanks.entries();j++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                    if (!currentCapBank->getOperationalState().compareTo(CtiCCCapBank::SwitchedOperationalState,RWCString::ignoreCase) &&
                        (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail || 
                         currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ||
                         currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable || 
                         currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ) )
                    {
                        if (!getOverlappingVerificationFlag())
                        {
                            currentCapBank->setVerificationFlag(TRUE);
                            currentCapBank->setVerificationDoneFlag(FALSE);
                            currentCapBank->setVCtrlIndex(0);
                        }
                        else
                        {
                            if (!currentCapBank->getVerificationDoneFlag())
                            {
                                currentCapBank->setVerificationFlag(TRUE);
                                currentCapBank->setVerificationDoneFlag(FALSE);
                                currentCapBank->setVCtrlIndex(0);
                                currentFeeder->setVerificationFlag(TRUE);
                                currentFeeder->setVerificationDoneFlag(FALSE);
                            }
                            else
                            {
                                currentFeeder->setVerificationDoneFlag(TRUE);
                            }
                        }
                    }
                }
            }
            break;
        }

        case CtiPAOScheduleManager::FailedBanks:
        {
            for (x = 0; x < _ccfeeders.entries(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[x];
                if (!getOverlappingVerificationFlag())
                {    
                    currentFeeder->setVerificationFlag(TRUE);
                    currentFeeder->setVerificationDoneFlag(FALSE);
                }
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(j=0;j<ccCapBanks.entries();j++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                    if (!currentCapBank->getOperationalState().compareTo(CtiCCCapBank::SwitchedOperationalState,RWCString::ignoreCase) &&
                        ( currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail || 
                          currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ) )
                    {
                        if (!getOverlappingVerificationFlag())
                        {
                            currentCapBank->setVerificationFlag(TRUE);
                            currentCapBank->setVerificationDoneFlag(FALSE);
                            currentCapBank->setVCtrlIndex(0);
                        }
                        else
                        {
                            if (!currentCapBank->getVerificationDoneFlag())
                            {
                                currentCapBank->setVerificationFlag(TRUE);
                                currentCapBank->setVerificationDoneFlag(FALSE);
                                currentCapBank->setVCtrlIndex(0);
                                currentFeeder->setVerificationFlag(TRUE);
                                currentFeeder->setVerificationDoneFlag(FALSE);
                            }
                            else
                            {
                                currentFeeder->setVerificationDoneFlag(TRUE);
                            }
                        }
                    }
                }
            }
            break;
        }
        case CtiPAOScheduleManager::QuestionableBanks:
        {
            for (x = 0; x < _ccfeeders.entries(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[x];
                if (!getOverlappingVerificationFlag())
                {    
                    currentFeeder->setVerificationFlag(TRUE);
                    currentFeeder->setVerificationDoneFlag(FALSE);
                }
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(j=0;j<ccCapBanks.entries();j++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                    if (!currentCapBank->getOperationalState().compareTo(CtiCCCapBank::SwitchedOperationalState,RWCString::ignoreCase) &&
                        (currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable || 
                         currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ) )
                    {
                        if (!getOverlappingVerificationFlag())
                        {
                            currentCapBank->setVerificationFlag(TRUE);
                            currentCapBank->setVerificationDoneFlag(FALSE);
                            currentCapBank->setVCtrlIndex(0);
                        }
                        else
                        {
                            if (!currentCapBank->getVerificationDoneFlag())
                            {
                                currentCapBank->setVerificationFlag(TRUE);
                                currentCapBank->setVerificationDoneFlag(FALSE);
                                currentCapBank->setVCtrlIndex(0);
                                currentFeeder->setVerificationFlag(TRUE);
                                currentFeeder->setVerificationDoneFlag(FALSE);
                            }
                            else
                            {
                                currentFeeder->setVerificationDoneFlag(TRUE);
                            }
                        }
                    }
                }
            }
            break;
        }
        case CtiPAOScheduleManager::SelectedForVerificationBanks:
        {
            break;
        }
        case CtiPAOScheduleManager::BanksInactiveForXTime:
        {
            RWDBDateTime currentTime = RWTime();
            currentTime.now();
            for (x = 0; x < _ccfeeders.entries(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[x];
                if (!getOverlappingVerificationFlag())
                {    
                    currentFeeder->setVerificationFlag(TRUE);
                    currentFeeder->setVerificationDoneFlag(FALSE);
                }
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(j=0;j<ccCapBanks.entries();j++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                    if (!currentCapBank->getOperationalState().compareTo(CtiCCCapBank::SwitchedOperationalState,RWCString::ignoreCase) &&
                        ( currentCapBank->getLastStatusChangeTime().seconds() <= ( currentTime.seconds() - getCapBankInactivityTime())) )
                    {
                        if (!getOverlappingVerificationFlag())
                        {
                            currentCapBank->setVerificationFlag(TRUE);
                            currentCapBank->setVerificationDoneFlag(FALSE);
                            currentCapBank->setVCtrlIndex(0);
                        }
                        else
                        {
                            if (!currentCapBank->getVerificationDoneFlag())
                            {
                                currentCapBank->setVerificationFlag(TRUE);
                                currentCapBank->setVerificationDoneFlag(FALSE);
                                currentCapBank->setVCtrlIndex(0);
                                currentFeeder->setVerificationFlag(TRUE);
                                currentFeeder->setVerificationDoneFlag(FALSE);
                            }
                            else
                            {
                                currentFeeder->setVerificationDoneFlag(TRUE);
                            }
                        }
                    }
                }
            }

            break;
        }
        case CtiPAOScheduleManager::StandAloneBanks:
        {
            for (x = 0; x < _ccfeeders.entries(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[x];
                if (!getOverlappingVerificationFlag())
                {    
                    currentFeeder->setVerificationFlag(TRUE);
                    currentFeeder->setVerificationDoneFlag(FALSE);
                }
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(j=0;j<ccCapBanks.entries();j++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                    if (!currentCapBank->getOperationalState().compareTo(CtiCCCapBank::StandAloneState,RWCString::ignoreCase))
                    {

                        if (!getOverlappingVerificationFlag())
                        {
                            currentCapBank->setVerificationFlag(TRUE);
                            currentCapBank->setVerificationDoneFlag(FALSE);
                            currentCapBank->setVCtrlIndex(0);
                        }
                        else
                        {
                            if (!currentCapBank->getVerificationDoneFlag())
                            {
                                currentCapBank->setVerificationFlag(TRUE);
                                currentCapBank->setVerificationDoneFlag(FALSE);
                                currentCapBank->setVCtrlIndex(0);
                                currentFeeder->setVerificationFlag(TRUE);
                                currentFeeder->setVerificationDoneFlag(FALSE);
                            }
                            else
                            {
                                currentFeeder->setVerificationDoneFlag(TRUE);
                            }
                        }
                    }
                }
            }
            break;
        }
        default:
            break;
    }
    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout <<" CapBank LIST:  ";
        }
        for (x = 0; x < _ccfeeders.entries(); x++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[x];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
            for(j=0;j<ccCapBanks.entries();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                if (currentCapBank->getVerificationFlag())
                { 
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout <<"  " << currentCapBank->getPAOId();
                    }
                }

            }
        }
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout <<endl;
        }
    }


    setBusUpdatedFlag(TRUE);

    return *this;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCSubstationBus::restoreGuts(RWvistream& istrm)
{
    RWTime tempTime2;
    RWTime tempTime3;
    RWCString tempString;

    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _parentId
    >> _maxdailyoperation
    >> _maxoperationdisableflag
    >> _currentvarloadpointid
    >> _currentvarloadpointvalue
    >> _currentwattloadpointid
    >> _currentwattloadpointvalue
    >> _maplocationid
    >> _controlunits
    >> _decimalplaces
    >> _newpointdatareceivedflag
    >> _busupdatedflag
    >> tempTime2
    >> _estimatedvarloadpointid
    >> _estimatedvarloadpointvalue
    >> _dailyoperationsanalogpointid
    >> _powerfactorpointid
    >> _estimatedpowerfactorpointid
    >> _currentdailyoperations
    >> _peaktimeflag
    >> _recentlycontrolledflag
    >> tempTime3
    >> _varvaluebeforecontrol
    >> _powerfactorvalue
    >> _estimatedpowerfactorvalue
    >> _currentvarpointquality
    >> _waivecontrolflag
    >> _peaklag
    >> _offpklag
    >> _peaklead
    >> _offpklead
    >> _currentvoltloadpointid
    >> _currentvoltloadpointvalue
    >> _verificationFlag
    >> _ccfeeders;

    _lastcurrentvarpointupdatetime = RWDBDateTime(tempTime2);
    _lastoperationtime = RWDBDateTime(tempTime3);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    DOUBLE temppowerfactorvalue = _powerfactorvalue;
    DOUBLE tempestimatedpowerfactorvalue = _estimatedpowerfactorvalue;
    if( _powerfactorvalue > 1 )
    {
        temppowerfactorvalue = _powerfactorvalue - 2;
    }
    if( _estimatedpowerfactorvalue > 1 )
    {
        tempestimatedpowerfactorvalue = _estimatedpowerfactorvalue - 2;
    }

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
    << _parentId
    << _maxdailyoperation
    << _maxoperationdisableflag
    << _currentvarloadpointid
    << _currentvarloadpointvalue
    << _currentwattloadpointid
    << _currentwattloadpointvalue
    << _maplocationid
    << _controlunits
    << _decimalplaces
    << _newpointdatareceivedflag
    << _busupdatedflag
    << _lastcurrentvarpointupdatetime.rwtime()
    << _estimatedvarloadpointid
    << _estimatedvarloadpointvalue
    << _dailyoperationsanalogpointid
    << _powerfactorpointid
    << _estimatedpowerfactorpointid
    << _currentdailyoperations
    << _peaktimeflag
    << _recentlycontrolledflag
    << _lastoperationtime.rwtime()
    << _varvaluebeforecontrol
    << temppowerfactorvalue
    << tempestimatedpowerfactorvalue
    << _currentvarpointquality
    << _waivecontrolflag
    << _peaklag
    << _offpklag
    << _peaklead
    << _offpklead
    << _currentvoltloadpointid
    << _currentvoltloadpointvalue
    << _verificationFlag
    << _ccfeeders;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::operator=(const CtiCCSubstationBus& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _parentId = right._parentId;
        _strategyId = right._strategyId;
        _strategyName = right._strategyName;
        _controlmethod = right._controlmethod;
        _maxdailyoperation = right._maxdailyoperation;
        _maxoperationdisableflag = right._maxoperationdisableflag;
        _peakstarttime = right._peakstarttime;
        _peakstoptime = right._peakstoptime;
        _currentvarloadpointid = right._currentvarloadpointid;
        _currentvarloadpointvalue = right._currentvarloadpointvalue;
        _currentwattloadpointid = right._currentwattloadpointid;
        _currentvoltloadpointvalue = right._currentwattloadpointvalue;
        _currentvoltloadpointid = right._currentvoltloadpointid;
        _currentwattloadpointvalue = right._currentvoltloadpointvalue;
        _controlinterval = right._controlinterval;
        _maxconfirmtime = right._maxconfirmtime;
        _minconfirmpercent = right._minconfirmpercent;
        _failurepercent = right._failurepercent;
        _daysofweek = right._daysofweek;
        _maplocationid = right._maplocationid;
        _controlunits = right._controlunits;
        _controldelaytime = right._controldelaytime;
        _controlsendretries = right._controlsendretries;
        _peaklag = right._peaklag;
        _offpklag = right._offpklag;
        _peaklead = right._peaklead;
        _offpklead = right._offpklead;
        _decimalplaces = right._decimalplaces;
        _nextchecktime = right._nextchecktime;
        _newpointdatareceivedflag = right._newpointdatareceivedflag;
        _busupdatedflag = right._busupdatedflag;
        _lastcurrentvarpointupdatetime = right._lastcurrentvarpointupdatetime;
        _estimatedvarloadpointid = right._estimatedvarloadpointid;
        _estimatedvarloadpointvalue = right._estimatedvarloadpointvalue;
        _dailyoperationsanalogpointid = right._dailyoperationsanalogpointid;
        _powerfactorpointid = right._powerfactorpointid;
        _estimatedpowerfactorpointid = right._estimatedpowerfactorpointid;
        _currentdailyoperations = right._currentdailyoperations;
        _peaktimeflag = right._peaktimeflag;
        _recentlycontrolledflag = right._recentlycontrolledflag;
        _lastoperationtime = right._lastoperationtime;
        _varvaluebeforecontrol = right._varvaluebeforecontrol;
        _lastfeedercontrolledpaoid = right._lastfeedercontrolledpaoid;
        _lastfeedercontrolledposition = right._lastfeedercontrolledposition;
        _powerfactorvalue = right._powerfactorvalue;
        _kvarsolution = right._kvarsolution;
        _estimatedpowerfactorvalue = right._estimatedpowerfactorvalue;
        _currentvarpointquality = right._currentvarpointquality;
        _waivecontrolflag = right._waivecontrolflag;
        _additionalFlags = right._additionalFlags;
        _currentVerificationCapBankId = right._currentVerificationCapBankId; 
        _currentVerificationFeederId = right._currentVerificationFeederId; 
        _verificationStrategy = right._verificationStrategy;
        _capBankToVerifyInactivityTime = right._capBankToVerifyInactivityTime; 
        _verificationFlag = right._verificationFlag;
        
        _ccfeeders.clearAndDestroy();
        for(LONG i=0;i<right._ccfeeders.entries();i++)
        {
            _ccfeeders.insert(((CtiCCFeeder*)right._ccfeeders[i])->replicate());
        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCSubstationBus::operator==(const CtiCCSubstationBus& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCSubstationBus::operator!=(const CtiCCSubstationBus& right) const
{
    return getPAOId() != right.getPAOId();
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSubstationBus* CtiCCSubstationBus::replicate() const
{
    return(new CtiCCSubstationBus(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::restore(RWDBReader& rdr)
{
    RWCString tempBoolString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paotype;
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    _disableflag = (tempBoolString=="y"?TRUE:FALSE);
    rdr["currentvarloadpointid"] >> _currentvarloadpointid;
    rdr["currentwattloadpointid"] >> _currentwattloadpointid;
    rdr["maplocationid"] >> _maplocationid;
    rdr["strategyid"] >> _strategyId;
    rdr["currentvoltloadpointid"] >> _currentvoltloadpointid;

    _parentId =  0;
    _decimalplaces = 2;
    _estimatedvarloadpointid = 0;
    _dailyoperationsanalogpointid = 0;
    _powerfactorpointid = 0;
    _estimatedpowerfactorpointid = 0;


    //initialize strategy members
	setStrategyName("(none)");
    setControlMethod("SubstationBus");
    setMaxDailyOperation(0);
    setMaxOperationDisableFlag(FALSE);
    setPeakLag(0);
    setOffPeakLag(0);
    setPeakLead(0);
    setOffPeakLead(0);
    setPeakStartTime(0);
    setPeakStopTime(0);
    setControlInterval(0);
    setMaxConfirmTime(0);
    setMinConfirmPercent(0);
    setFailurePercent(0);
    setDaysOfWeek("NYYYYYNN");
    setControlUnits("KVAR");
    setControlDelayTime(0);
    setControlSendRetries(0);
    
   
    figureNextCheckTime();
    setNewPointDataReceivedFlag(FALSE);
    setBusUpdatedFlag(FALSE);
    setLastCurrentVarPointUpdateTime(gInvalidRWDBDateTime);
    setEstimatedVarLoadPointValue(0.0);
    setCurrentDailyOperations(0);
    setPeakTimeFlag(TRUE);
    setRecentlyControlledFlag(FALSE);
    setLastOperationTime(gInvalidRWDBDateTime);
    setVarValueBeforeControl(0.0);
    setLastFeederControlledPAOId(0);
    setLastFeederControlledPosition(-1);
    setPowerFactorValue(-1000000.0);
    setKVARSolution(0.0);
    setEstimatedPowerFactorValue(-1000000.0);
    setCurrentVarPointQuality(NormalQuality);
    setWaiveControlFlag(FALSE);

    _additionalFlags = RWCString("NNNNNNNNNNNNNNNNNNNN");
    setVerificationFlag(FALSE);
    setPerformingVerificationFlag(FALSE);
    setVerificationDoneFlag(FALSE);
    setOverlappingVerificationFlag(FALSE);
    setCurrentVerificationCapBankId(-1);
    setCurrentVerificationFeederId(-1);
    setCurrentVerificationCapBankState(0);
    setVerificationStrategy(-1);
    setCapBankInactivityTime(-1);

    _insertDynamicDataFlag = TRUE;

    if( _currentvarloadpointid <= 0 )
    {
        _currentvarloadpointvalue = 0;
    }
    if( _currentwattloadpointid <= 0 )
    {
        _currentwattloadpointvalue = 0;
    }
    if( _currentvoltloadpointid <= 0 )
    {
        _currentvoltloadpointvalue = 0;
    }

}

void CtiCCSubstationBus::setStrategyValues(CtiCCStrategyPtr strategy)
{
    RWCString tempBoolString;

    _strategyName = strategy->getStrategyName();                      
    _controlmethod = strategy->getControlMethod();                    
    _maxdailyoperation = strategy->getMaxDailyOperation();            
    _maxoperationdisableflag = strategy->getMaxOperationDisableFlag();
    _peaklag = strategy->getPeakLag();                      
    _offpklag = strategy->getOffPeakLag();                
    _peaklead = strategy->getPeakLead();                      
    _offpklead = strategy->getOffPeakLead();                
    _peakstarttime = strategy->getPeakStartTime();                    
    _peakstoptime = strategy->getPeakStopTime();                      
    _controlinterval = strategy->getControlInterval();                
    _maxconfirmtime = strategy->getMaxConfirmTime();                  
    _minconfirmpercent = strategy->getMinConfirmPercent();            
    _failurepercent = strategy->getFailurePercent();                  
    _daysofweek = strategy->getDaysOfWeek();                          
    _controlunits = strategy->getControlUnits();                      
    _controldelaytime = strategy->getControlDelayTime();              
    _controlsendretries = strategy->getControlSendRetries();          

}

void CtiCCSubstationBus::setDynamicData(RWDBReader& rdr)
{   
    RWDBNullIndicator isNull;
    RWDBDateTime dynamicTimeStamp;
    RWCString tempBoolString;


    /*rdr["currentvarpointvalue"] >> isNull;
    if( !isNull )
    { */
        rdr["currentvarpointvalue"] >> _currentvarloadpointvalue;
        rdr["currentwattpointvalue"] >> _currentwattloadpointvalue;
        rdr["nextchecktime"] >> _nextchecktime;
        rdr["newpointdatareceivedflag"] >> tempBoolString;
        tempBoolString.toLower();
        _newpointdatareceivedflag = (tempBoolString=="y"?TRUE:FALSE);
        rdr["busupdatedflag"] >> tempBoolString;
        tempBoolString.toLower();
        _busupdatedflag = (tempBoolString=="y"?TRUE:FALSE);
        rdr["lastcurrentvarupdatetime"] >> _lastcurrentvarpointupdatetime;
        rdr["estimatedvarpointvalue"] >> _estimatedvarloadpointvalue;
        rdr["currentdailyoperations"] >> _currentdailyoperations;
        rdr["peaktimeflag"] >> tempBoolString;
        tempBoolString.toLower();
        _peaktimeflag = (tempBoolString=="y"?TRUE:FALSE);
        rdr["recentlycontrolledflag"] >> tempBoolString;
        tempBoolString.toLower();
        _recentlycontrolledflag = (tempBoolString=="y"?TRUE:FALSE);
        rdr["lastoperationtime"] >> _lastoperationtime;
        rdr["varvaluebeforecontrol"] >> _varvaluebeforecontrol;
        rdr["lastfeederpaoid"] >> _lastfeedercontrolledpaoid;
        rdr["lastfeederposition"] >> _lastfeedercontrolledposition;
        rdr["ctitimestamp"] >> dynamicTimeStamp;
        rdr["powerfactorvalue"] >> _powerfactorvalue;
        rdr["kvarsolution"] >> _kvarsolution;
        rdr["estimatedpfvalue"] >> _estimatedpowerfactorvalue;
        rdr["currentvarpointquality"] >> _currentvarpointquality;
        rdr["waivecontrolflag"] >> tempBoolString;
        tempBoolString.toLower();
        _waivecontrolflag = (tempBoolString=="y"?TRUE:FALSE);

        rdr["additionalflags"] >> _additionalFlags;
        _additionalFlags.toLower();
        _verificationFlag = (_additionalFlags.data()[0]=='y'?TRUE:FALSE);
        _performingVerificationFlag = (_additionalFlags.data()[1]=='y'?TRUE:FALSE);
        _verificationDoneFlag = (_additionalFlags.data()[2]=='y'?TRUE:FALSE);
        _overlappingSchedulesVerificationFlag = (_additionalFlags.data()[2]=='y'?TRUE:FALSE);

        rdr["currverifycbid"] >> _currentVerificationCapBankId;
        rdr["currverifyfeederid"] >> _currentVerificationFeederId;
        rdr["currverifycborigstate"] >> _currentCapBankToVerifyAssumedOrigState;
        rdr["verificationstrategy"] >> _verificationStrategy;
        rdr["cbinactivitytime"] >> _capBankToVerifyInactivityTime;
        rdr["currentvoltpointvalue"] >> _currentvoltloadpointvalue;

        _insertDynamicDataFlag = FALSE;

        _dirty = false;
    //}
}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the RWCString representation of a double
---------------------------------------------------------------------------*/
RWCString CtiCCSubstationBus::doubleToString(DOUBLE doubleVal)
{
    char tempchar[80] = "";
    RWCString retString = RWCString("");
    _snprintf(tempchar,80,"%d",(int)(doubleVal+0.5));
    retString += tempchar;

    return retString;
}

void CtiCCSubstationBus::deleteCCFeeder(long feederId)
{
    RWOrdered& ccFeeders = getCCFeeders();
    for (LONG j = 0; j < ccFeeders.entries(); j++)
    {
        CtiCCFeeder *feeder = (CtiCCFeeder*)ccFeeders[j];
        if (feeder->getPAOId() == feederId)
        {
            getCCFeeders().removeAt(j);
            break;
        }

    }
    return;
}

/* Public Static members */
const RWCString CtiCCSubstationBus::IndividualFeederControlMethod   = "IndividualFeeder";
const RWCString CtiCCSubstationBus::SubstationBusControlMethod      = "SubstationBus";
const RWCString CtiCCSubstationBus::BusOptimizedFeederControlMethod = "BusOptimizedFeeder";
const RWCString CtiCCSubstationBus::ManualOnlyControlMethod         = "ManualOnly";

const RWCString CtiCCSubstationBus::KVARControlUnits         = "KVAR";
const RWCString CtiCCSubstationBus::VoltControlUnits         = "Volts";
const RWCString CtiCCSubstationBus::PF_BY_KVARControlUnits   = "P-Factor KW/KVar";
const RWCString CtiCCSubstationBus::PF_BY_KQControlUnits     = "P-Factor KW/KQ";
                                                
