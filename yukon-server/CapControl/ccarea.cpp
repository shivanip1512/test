
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
CtiCCArea::CtiCCArea() :
_paoid(0),
_disableflag(false),
_voltReductionControlPointId(0),
_voltReductionControlValue(0),
_pfactor(0),    
_estPfactor(0),     
_ovUvDisabledFlag(false),    
_reEnableAreaFlag(false),    
_childVoltReductionFlag(false),    
_insertDynamicDataFlag(false),
_dirty(false),
_areaUpdatedFlag(false)
{
}

CtiCCArea::CtiCCArea(RWDBReader& rdr, StrategyPtr strategy)
    :_strategy(strategy)
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


    double pfDisplayValue = _pfactor;
    double estPfDisplayValue = _estPfactor;

    // Modifying the display value of pFactor to represent +100% values as a negative value.
    if (_pfactor > 1)
    {
        pfDisplayValue -= 2;
    }
    if (_estPfactor > 1)
    {
        estPfDisplayValue -= 2;
    }
    istrm >> pfDisplayValue
        >> estPfDisplayValue
        >> _voltReductionControlValue
        >> _childVoltReductionFlag;
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
        << _voltReductionControlValue
        << _childVoltReductionFlag;
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
        _childVoltReductionFlag = right._childVoltReductionFlag;

        _strategy = right._strategy;

        _pfactor = right._pfactor;     
        _estPfactor = right._estPfactor;  

        _additionalFlags = right._additionalFlags;
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _reEnableAreaFlag = right._reEnableAreaFlag;

        _operationStats = right._operationStats;
        _confirmationStats = right._confirmationStats;

        _areaUpdatedFlag = right._areaUpdatedFlag;
        
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

    setPFactor(-1);
    setEstPFactor(-1);

    setVoltReductionControlValue(FALSE);
    setChildVoltReductionFlag(FALSE);
    setAreaUpdatedFlag(FALSE);

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
            addFlags[2] = (_childVoltReductionFlag?'Y':'N');
            addFlags[3] = (_areaUpdatedFlag?'Y':'N');
            _additionalFlags = string(char2string(*addFlags) + char2string(*(addFlags+1)) + char2string(*(addFlags+2)) + 
                                char2string(*(addFlags+3)));
            _additionalFlags.append("NNNNNNNNNNNNNNNN");

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
                    string loggedSQLstring = updater.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
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
                string loggedSQLstring = inserter.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
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
                    string loggedSQLstring = inserter.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
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
    _childVoltReductionFlag = (_additionalFlags[2]=='y'?TRUE:FALSE);
    _areaUpdatedFlag = (_additionalFlags[3]=='y'?TRUE:FALSE);

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
    getAreaUpdatedFlag()

    Returns the getAreaUpdatedFlag() of the area
---------------------------------------------------------------------------*/
BOOL CtiCCArea::getAreaUpdatedFlag() const
{
    return _areaUpdatedFlag;
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
    getControlValue

    Returns the ControlValue of the area
---------------------------------------------------------------------------*/
BOOL CtiCCArea::getChildVoltReductionFlag() const
{
    return _childVoltReductionFlag;
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

DOUBLE CtiCCArea::getPFactor() const
{
    return _pfactor;
}

DOUBLE CtiCCArea::getEstPFactor() const
{
    return _estPfactor;
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
    setAreaUpdatedFlag

    Sets the AreaUpdated flag of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setAreaUpdatedFlag(BOOL flag)
{
    _areaUpdatedFlag = flag;
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
    if(_voltReductionControlValue != flag)
    {    
        _dirty = TRUE;
    }
    _voltReductionControlValue = flag;
    return *this;
}
/*---------------------------------------------------------------------------
    setChildVoltReductionflag

    Sets the ControlValue of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setChildVoltReductionFlag(BOOL flag)
{
    if(_childVoltReductionFlag != flag)
    {    
        setAreaUpdatedFlag(TRUE);
        _dirty = TRUE;
    }
    _childVoltReductionFlag = flag;
    return *this;
}



/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovuv disable flag of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setOvUvDisabledFlag(BOOL flag)
{
    if(_ovUvDisabledFlag != flag)
        _dirty = TRUE;
    _ovUvDisabledFlag = flag;
    return *this;
}


/*---------------------------------------------------------------------------
    setReEnableAreaFlag

    Sets the reEnable Area flag of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setReEnableAreaFlag(BOOL flag)
{
    if(_reEnableAreaFlag != flag)
        _dirty = TRUE;
    _reEnableAreaFlag = flag;
    return *this;
}


CtiCCArea& CtiCCArea::setPFactor(DOUBLE pfactor)
{
    
    if(_pfactor != pfactor)
    {    
        _dirty = TRUE;
        setAreaUpdatedFlag(TRUE);
    }
    _pfactor = pfactor;

    return *this;
}

CtiCCArea& CtiCCArea::setEstPFactor(DOUBLE estPfactor)
{
    if(_estPfactor != estPfactor)
    {    
        _dirty = TRUE;
        setAreaUpdatedFlag(TRUE);
    }
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

CtiCCArea& CtiCCArea::checkAndUpdateChildVoltReductionFlags()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    std::list <long>::iterator subIter = getSubStationList()->begin();;
    CtiCCSubstationPtr currentStation = NULL;

    int numberOfStationsVoltReducting = 0;

    while (subIter != getSubStationList()->end() )
    {
        currentStation = store->findSubstationByPAObjectID(*subIter);
        subIter++;

        if (currentStation->getVoltReductionFlag())
        {
            setChildVoltReductionFlag(TRUE);
            numberOfStationsVoltReducting += 1;
        }
    }
    if (numberOfStationsVoltReducting == 0)
    {
        setChildVoltReductionFlag(FALSE);
    }

    return *this;
}


void CtiCCArea::setStrategy(StrategyPtr strategy)
{
    _strategy = strategy;
}


StrategyPtr CtiCCArea::getStrategy() const
{
    return _strategy;
}

