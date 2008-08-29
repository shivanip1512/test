
/*---------------------------------------------------------------------------
        Filename:  ccsubstationbus.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCArea.
                        CtiCCArea maintains the state and handles
                        the persistence of substation buses for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/tpsrtvec.h>

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccarea.h"
#include "ccid.h"
#include "cccapbank.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "capcontroller.h"
#include "resolvers.h"
#include "utility.h"

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCArea, CTICCAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCArea::CtiCCArea() 
{
}

CtiCCArea::CtiCCArea(RWDBReader& rdr)
{
    restore(rdr);
    _operationStats.setPAOId(_paoid);
    _confirmationStats.setPAOId(_paoid);
}

CtiCCArea::CtiCCArea(const CtiCCArea& area) 
{
    operator=(area);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCArea::~CtiCCArea()
{  
    _pointIds.clear();
    
    if (!_subStationIds.empty())
    {
        _subStationIds.clear();
    }

}

CtiCCOperationStats& CtiCCArea::getOperationStats()
{
    return _operationStats;
}

CtiCCConfirmationStats& CtiCCArea::getConfirmationStats()
{
    return _confirmationStats;
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCArea::restoreGuts(RWvistream& istrm)
{
    long tempSubBusId;
    long numOfSubBusIds;

    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _ovUvDisabledFlag;

    istrm >> numOfSubBusIds;
    _subStationIds.clear();
    for(LONG i=0;i<numOfSubBusIds;i++)
    {
        istrm >> tempSubBusId;
        _subStationIds.push_back(tempSubBusId);
    }

    istrm >> _pfactor
        >> _estPfactor
        >> _voltReductionControlValue;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCArea::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
    << _ovUvDisabledFlag;
    ostrm << _subStationIds.size();

    std::list<long>::const_iterator iter = _subStationIds.begin();
    for( ; iter != _subStationIds.end();iter++)
    {
        ostrm << (long)*iter;
    }
    ostrm << _pfactor
       << _estPfactor
        << _voltReductionControlValue;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::operator=(const CtiCCArea& right)
{
    if( this != &right )
    {
        _paoid = right.getPAOId();
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _voltReductionControlPointId = right._voltReductionControlPointId;
        _voltReductionControlValue = right._voltReductionControlValue;

        _strategyId = right._strategyId;
        _strategyName = right._strategyName;
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
        _offpklag = right._offpklag;
        _peaklead = right._peaklead;
        _offpklead = right._offpklead;
        _peakVARlag = right._peakVARlag;
        _offpkVARlag = right._offpkVARlag;
        _peakVARlead = right._peakVARlead;
        _offpkVARlead = right._offpkVARlead;
        _peakPFSetPoint =  right._peakPFSetPoint;
        _offpkPFSetPoint = right._offpkPFSetPoint;

        _integrateflag = right._integrateflag;
        _integrateperiod = right._integrateperiod;

        _pfactor = right._pfactor;     
        _estPfactor = right._estPfactor;  

        _additionalFlags = right._additionalFlags;
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _reEnableAreaFlag = right._reEnableAreaFlag;

        _operationStats = right._operationStats;
        _confirmationStats = right._confirmationStats;
        
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCArea::operator==(const CtiCCArea& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCArea::operator!=(const CtiCCArea& right) const
{
    return getPAOId() != right.getPAOId();
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCArea* CtiCCArea::replicate() const
{
    return(new CtiCCArea(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCArea::restore(RWDBReader& rdr)
{
    string tempBoolString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paotype;
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _disableflag = (tempBoolString=="y"?TRUE:FALSE);

    rdr["voltreductionpointid"] >> _voltReductionControlPointId;
    if (_voltReductionControlPointId <= 0)
    {
        setVoltReductionControlValue(FALSE);
    }
    setOvUvDisabledFlag(FALSE);
    setReEnableAreaFlag(FALSE);


   //initialize strategy members
    setStrategyId(0);
    setStrategyName("(none)");
    setControlMethod("SubstationBus");
    setMaxDailyOperation(0);
    setMaxOperationDisableFlag(FALSE);
    setPeakLag(0);
    setOffPeakLag(0);
    setPeakLead(0);
    setOffPeakLead(0);
    setPeakVARLag(0);
    setOffPeakVARLag(0);
    setPeakVARLead(0);
    setOffPeakVARLead(0);
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

    setPeakPFSetPoint(100);
    setOffPeakPFSetPoint(100);
    setIntegrateFlag(FALSE);
    setIntegratePeriod(0);

    setPFactor(0);
    setEstPFactor(0);

    setVoltReductionControlValue(FALSE);

    _insertDynamicDataFlag = TRUE;
    //_dirty = FALSE;



}


/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCArea::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCArea::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {
        RWDBTable dynamicCCAreaTable = getDatabase().table( "dynamicccarea" );

        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCAreaTable.updater();

            
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_ovUvDisabledFlag?'Y':'N');
            addFlags[1] = (_reEnableAreaFlag?'Y':'N');
            _additionalFlags = string(char2string(*addFlags));
            _additionalFlags.append("NNNNNNNNNNNNNNNNNNN");

            updater.clear();

            updater.where(dynamicCCAreaTable["areaid"]==_paoid);
            updater << dynamicCCAreaTable["additionalflags"].assign( string2RWCString(_additionalFlags) )
                    <<  dynamicCCAreaTable["controlvalue"].assign( _voltReductionControlValue );
                    

            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted area into dynamicCCArea: " << getPAOName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            RWDBInserter inserter = dynamicCCAreaTable.inserter();
            //TS FLAG
            inserter << _paoid
                <<  string2RWCString(addFlags)
                <<  _voltReductionControlValue;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << inserter.asString().data() << endl;
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
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << inserter.asString() << endl;
                }
            }
        }

        if (getOperationStats().isDirty())
            getOperationStats().dumpDynamicData(conn, currentDateTime);
    }
}
void CtiCCArea::setDynamicData(RWDBReader& rdr)
{   
    string tempBoolString;

    rdr["additionalflags"] >> _additionalFlags;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);

    _ovUvDisabledFlag = (_additionalFlags[0]=='y'?TRUE:FALSE);
    _reEnableAreaFlag = (_additionalFlags[1]=='y'?TRUE:FALSE);

    rdr["controlvalue"] >> _voltReductionControlValue;
    if (_voltReductionControlPointId <= 0)
    {
        setVoltReductionControlValue(FALSE);
    }
    
    _insertDynamicDataFlag = FALSE;
    _dirty = false;


}


/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the area
---------------------------------------------------------------------------*/
LONG CtiCCArea::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the area
---------------------------------------------------------------------------*/
const string& CtiCCArea::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the area
---------------------------------------------------------------------------*/
const string& CtiCCArea::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the area
---------------------------------------------------------------------------*/
const string& CtiCCArea::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the area
---------------------------------------------------------------------------*/
const string& CtiCCArea::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the area
---------------------------------------------------------------------------*/
const string& CtiCCArea::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCArea::getDisableFlag() const
{
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getControlValue

    Returns the ControlValue of the area
---------------------------------------------------------------------------*/
BOOL CtiCCArea::getVoltReductionControlValue() const
{
    return _voltReductionControlValue;
}

/*---------------------------------------------------------------------------
    getControlPointId

    Returns the ControlPointId of the area
---------------------------------------------------------------------------*/
LONG CtiCCArea::getVoltReductionControlPointId() const
{
    return _voltReductionControlPointId;
}

/*---------------------------------------------------------------------------
    getOvUvDisabledFlag

    Returns the ovuv disable flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCArea::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}
/*---------------------------------------------------------------------------
    getReEnableAreaFlag

    Returns the ovuv disable flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCArea::getReEnableAreaFlag() const
{
    return _reEnableAreaFlag;
}

/*---------------------------------------------------------------------------
    isDirty()
    
    Returns the dirty flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCArea::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    getStrategyId

    Returns the strategy id of the area
---------------------------------------------------------------------------*/
LONG CtiCCArea::getStrategyId() const
{
    return _strategyId;
}

const string& CtiCCArea::getStrategyName() const
{
    return _strategyName;
}
const string& CtiCCArea::getControlMethod() const
{
    return _controlmethod;
}

LONG CtiCCArea::getMaxDailyOperation() const
{
    return _maxdailyoperation;
}

BOOL CtiCCArea::getMaxOperationDisableFlag() const
{
    return _maxoperationdisableflag;
}

DOUBLE CtiCCArea::getPeakLag() const
{
    return _peaklag;
}

DOUBLE CtiCCArea::getOffPeakLag() const
{
    return _offpklag;
}

DOUBLE CtiCCArea::getPeakLead() const
{
    return _peaklead;
}

DOUBLE CtiCCArea::getOffPeakLead() const
{
    return _offpklead;
}

DOUBLE CtiCCArea::getPeakVARLag() const
{
    return _peakVARlag;
}

DOUBLE CtiCCArea::getOffPeakVARLag() const
{
    return _offpkVARlag;
}

DOUBLE CtiCCArea::getPeakVARLead() const
{
    return _peakVARlead;
}

DOUBLE CtiCCArea::getOffPeakVARLead() const
{
    return _offpkVARlead;
}

DOUBLE CtiCCArea::getPeakPFSetPoint() const
{
    return _peakPFSetPoint;
}

DOUBLE CtiCCArea::getOffPeakPFSetPoint() const
{
    return _offpkPFSetPoint;
}

LONG CtiCCArea::getPeakStartTime() const
{
    return _peakstarttime;
}

LONG CtiCCArea::getPeakStopTime() const
{
    return _peakstoptime;
}

LONG CtiCCArea::getControlInterval() const
{
    return _controlinterval;
}

LONG CtiCCArea::getMaxConfirmTime() const
{
    return _maxconfirmtime;
}

LONG CtiCCArea::getMinConfirmPercent() const
{
    return _minconfirmpercent;
}

LONG CtiCCArea::getFailurePercent() const
{
    return _failurepercent;
}

const string& CtiCCArea::getDaysOfWeek() const
{
    return _daysofweek;
}

const string& CtiCCArea::getControlUnits() const
{
    return _controlunits;
}

LONG CtiCCArea::getControlDelayTime() const
{
    return _controldelaytime;
}

LONG CtiCCArea::getControlSendRetries() const
{
    return _controlsendretries;
}

BOOL CtiCCArea::getIntegrateFlag() const
{
    return _integrateflag;
}

LONG CtiCCArea::getIntegratePeriod() const
{
    return _integrateperiod;
}

DOUBLE CtiCCArea::getPFactor() const
{
    return _pfactor;
}

DOUBLE CtiCCArea::getEstPFactor() const
{
    return _estPfactor;
}

void CtiCCArea::setStrategyValues(CtiCCStrategyPtr strategy)
{
    string tempBoolString;

    _strategyName = strategy->getStrategyName();                      
    _controlmethod = strategy->getControlMethod();                    
    _maxdailyoperation = strategy->getMaxDailyOperation();            
    _maxoperationdisableflag = strategy->getMaxOperationDisableFlag();
    _peaklag = strategy->getPeakLag();                      
    _offpklag = strategy->getOffPeakLag();                
    _peaklead = strategy->getPeakLead();                      
    _offpklead = strategy->getOffPeakLead();                
    _peakVARlag = strategy->getPeakVARLag();                      
    _offpkVARlag = strategy->getOffPeakVARLag();                
    _peakVARlead = strategy->getPeakVARLead();                      
    _offpkVARlead = strategy->getOffPeakVARLead();                
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
    _integrateflag = strategy->getIntegrateFlag();
    _integrateperiod = strategy->getIntegratePeriod();

}


/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the area - use with caution
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setPAOCategory(const string& category)
{
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setPAOClass(const string& pclass)
{
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setPAOName(const string& name)
{
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setPAOType(const string& _type)
{
    _paotype = _type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setPAODescription(const string& description)
{
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setDisableFlag(BOOL disable)
{
    _disableflag = disable;
    return *this;
}


/*---------------------------------------------------------------------------
    setControlPointId

    Sets the ControlPointId of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setVoltReductionControlPointId(LONG pointId)
{
    _voltReductionControlPointId = pointId;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlValue

    Sets the ControlValue of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setVoltReductionControlValue(BOOL flag)
{
    _voltReductionControlValue = flag;
    return *this;
}



/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovuv disable flag of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setOvUvDisabledFlag(BOOL flag)
{
    _ovUvDisabledFlag = flag;
    return *this;
}


/*---------------------------------------------------------------------------
    setReEnableAreaFlag

    Sets the reEnable Area flag of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setReEnableAreaFlag(BOOL flag)
{
    _reEnableAreaFlag = flag;
    return *this;
}



CtiCCArea& CtiCCArea::setStrategyId(LONG strategyId)
{
    _strategyId = strategyId;
    return *this;
}

CtiCCArea& CtiCCArea::setStrategyName(const string& strategyName)
{
    _strategyName = strategyName;
    return *this;
}

CtiCCArea& CtiCCArea::setControlMethod(const string& method)
{
    _controlmethod = method;
    return *this;
}

CtiCCArea& CtiCCArea::setMaxDailyOperation(LONG max)
{
    _maxdailyoperation = max;
    return *this;

}

CtiCCArea& CtiCCArea::setMaxOperationDisableFlag(BOOL maxopdisable)
{
    _maxoperationdisableflag = maxopdisable;
    return *this;
}

CtiCCArea& CtiCCArea::setPeakLag(DOUBLE peak)
{
    _peaklag = peak;
    return *this;
}

CtiCCArea& CtiCCArea::setOffPeakLag(DOUBLE offpeak)
{
    _offpklag = offpeak;
    return *this;
}
CtiCCArea& CtiCCArea::setPeakLead(DOUBLE peak)
{
    _peaklead = peak;
    return *this;
}

CtiCCArea& CtiCCArea::setOffPeakLead(DOUBLE offpeak)
{
    _offpklead = offpeak;
    return *this;
}
CtiCCArea& CtiCCArea::setPeakVARLag(DOUBLE peak)
{
    _peakVARlag = peak;
    return *this;
}

CtiCCArea& CtiCCArea::setOffPeakVARLag(DOUBLE offpeak)
{
    _offpkVARlag = offpeak;
    return *this;
}
CtiCCArea& CtiCCArea::setPeakVARLead(DOUBLE peak)
{
    _peakVARlead = peak;
    return *this;
}

CtiCCArea& CtiCCArea::setOffPeakVARLead(DOUBLE offpeak)
{
    _offpkVARlead = offpeak;
    return *this;
}

CtiCCArea& CtiCCArea::setPeakPFSetPoint(DOUBLE peak)
{
    _peakPFSetPoint = peak;
    return *this;
}

CtiCCArea& CtiCCArea::setOffPeakPFSetPoint(DOUBLE offpeak)
{
    _offpkPFSetPoint = offpeak;
    return *this;
}

CtiCCArea& CtiCCArea::setPeakStartTime(LONG starttime)
{
    _peakstarttime = starttime;
    return *this;
}

CtiCCArea& CtiCCArea::setPeakStopTime(LONG stoptime)
{
    _peakstoptime = stoptime;
    return *this;
}
CtiCCArea& CtiCCArea::setControlInterval(LONG interval)
{
    _controlinterval = interval;
    return *this;
}

CtiCCArea& CtiCCArea::setMaxConfirmTime(LONG confirm)
{   
    _maxconfirmtime = confirm;
    return *this;
}

CtiCCArea& CtiCCArea::setMinConfirmPercent(LONG confirm)
{
    _minconfirmpercent = confirm;
    return *this;
}

CtiCCArea& CtiCCArea::setFailurePercent(LONG failure)
{
    _failurepercent = failure;
    return *this;
}

CtiCCArea& CtiCCArea::setDaysOfWeek(const string& days)
{
    _daysofweek = days;
    return *this;
}

CtiCCArea& CtiCCArea::setControlUnits(const string& contunit)
{
    _controlunits = contunit;
    return *this;
}

CtiCCArea& CtiCCArea::setControlDelayTime(LONG delay)
{
    _controldelaytime = delay;
    return *this;
}

CtiCCArea& CtiCCArea::setControlSendRetries(LONG retries)
{
    _controlsendretries = retries;
    return *this;
}

CtiCCArea& CtiCCArea::setIntegrateFlag(BOOL flag)
{
    _integrateflag = flag;
    return *this;
}

CtiCCArea& CtiCCArea::setIntegratePeriod(LONG period)
{
    _integrateperiod = period;
    return *this;
}

CtiCCArea& CtiCCArea::setPFactor(DOUBLE pfactor)
{
    _pfactor = pfactor;
    return *this;
}

CtiCCArea& CtiCCArea::setEstPFactor(DOUBLE estPfactor)
{
    _estPfactor = estPfactor;
    return *this;
}

void CtiCCArea::checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    std::list <long>::iterator subIter = getSubStationList()->begin();;
    CtiCCSubstationPtr currentSubstation = NULL;

    while (subIter != getSubStationList()->end())
    {
        currentSubstation = store->findSubstationByPAObjectID(*subIter);
        subIter++;            
        if (currentSubstation != NULL  &&
            (getDisableFlag() || currentSubstation->getDisableFlag()))
        {
            currentSubstation->checkForAndStopVerificationOnChildSubBuses(capMessages);
        }
    }
}



