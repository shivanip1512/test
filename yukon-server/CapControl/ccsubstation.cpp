

/*---------------------------------------------------------------------------
        Filename:  ccsubstation.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSubstation.
                        CtiCCSubstation maintains the state and handles
                        the persistence of substation buses for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/tpsrtvec.h>

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccsubstation.h"
#include "ccid.h"
#include "cccapbank.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "capcontroller.h"
#include "resolvers.h"
#include "utility.h"

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCSubstation, CTICCSUBSTATION_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSubstation::CtiCCSubstation() 
{
}

CtiCCSubstation::CtiCCSubstation(RWDBReader& rdr) 
{
    restore(rdr);
    _operationStats.setPAOId(_paoid);
    _confirmationStats.setPAOId(_paoid);
}

CtiCCSubstation::CtiCCSubstation(const CtiCCSubstation& substation) 
{
    operator=(substation);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstation::~CtiCCSubstation()
{  
    _pointIds.clear();
    try
    {  
        _subBusIds.clear();   
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

CtiCCOperationStats& CtiCCSubstation::getOperationStats()
{
    return _operationStats;
}


CtiCCConfirmationStats& CtiCCSubstation::getConfirmationStats()
{
    return _confirmationStats;
}




/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCSubstation::restoreGuts(RWvistream& istrm)
{
    LONG tempSubBusId;
    LONG numOfSubBusIds;

    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _parentId
    >> _ovUvDisabledFlag;

    istrm >> numOfSubBusIds;
    _subBusIds.clear();
    for(LONG i=0;i<numOfSubBusIds;i++)
    {
        istrm >> tempSubBusId;
        _subBusIds.push_back(tempSubBusId);
    }
    istrm >> _pfactor
        >> _estPfactor
        >> _saEnabledFlag
        >> _saEnabledId
        >> _voltReductionFlag
        >> _recentlyControlledFlag
        >> _childVoltReductionFlag;
    

}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCSubstation::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
    << _parentId
    << _ovUvDisabledFlag;

    ostrm << _subBusIds.size();
    std::list<LONG>::const_iterator iter = _subBusIds.begin();

    for( ; iter != _subBusIds.end(); iter++)
    {
        ostrm << (LONG)*iter;
    }

    ostrm << _pfactor
        << _estPfactor
        << _saEnabledFlag
        << _saEnabledId
        << _voltReductionFlag
        << _recentlyControlledFlag
        << _childVoltReductionFlag;
        

}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::operator=(const CtiCCSubstation& right)
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
        _parentName = right._parentName;
        _parentId = right._parentId;
        _displayOrder = right._displayOrder;

        _additionalFlags = right._additionalFlags;
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _voltReductionFlag = right._voltReductionFlag;
        _voltReductionControlId = right._voltReductionControlId;
        _childVoltReductionFlag = right._childVoltReductionFlag;

        _pfactor = right._pfactor;
        _estPfactor = right._estPfactor;
        _saEnabledFlag = right._saEnabledFlag;
        _saEnabledId = right._saEnabledId;
        _recentlyControlledFlag = right._recentlyControlledFlag;
        _stationUpdatedFlag = right._stationUpdatedFlag;

        _subBusIds.clear();
        _subBusIds.assign(right._subBusIds.begin(), right._subBusIds.end());

        _operationStats = right._operationStats;
        _confirmationStats = right._confirmationStats;

    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCSubstation::operator==(const CtiCCSubstation& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCSubstation::operator!=(const CtiCCSubstation& right) const
{
    return getPAOId() != right.getPAOId();
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSubstation* CtiCCSubstation::replicate() const
{
    return(new CtiCCSubstation(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCSubstation::restore(RWDBReader& rdr)
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

    rdr["voltreductionpointid"] >> _voltReductionControlId;
    
    setOvUvDisabledFlag(FALSE);
    setVoltReductionFlag(FALSE);
    setPFactor(0);
    setEstPFactor(0);
    setSaEnabledFlag(FALSE);
    setSaEnabledId(0);
    setRecentlyControlledFlag(FALSE);
    setStationUpdatedFlag(FALSE);
    setChildVoltReductionFlag(FALSE);

    _insertDynamicDataFlag = TRUE;
    _dirty = TRUE;

}


/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCSubstation::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSubstation::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {
        RWDBTable dynamicCCSubstationTable = getDatabase().table( "dynamicccsubstation" );

        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCSubstationTable.updater();

            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_ovUvDisabledFlag?'Y':'N');
            addFlags[1] = (_saEnabledFlag?'Y':'N');
            addFlags[2] = (_voltReductionFlag?'Y':'N');
            addFlags[3] = (_recentlyControlledFlag?'Y':'N');
            addFlags[4] = (_stationUpdatedFlag?'Y':'N');
            addFlags[5] = (_childVoltReductionFlag?'Y':'N');
            _additionalFlags = string(char2string(*addFlags) + char2string(*(addFlags+1)) + 
                                char2string(*(addFlags+2)) + char2string(*(addFlags+3)) + 
                                char2string(*(addFlags+4)) +  char2string(*(addFlags+5)));
            _additionalFlags.append("NNNNNNNNNNNNNN");

            updater.clear();

            updater.where(dynamicCCSubstationTable["substationid"]==_paoid);

            updater << dynamicCCSubstationTable["additionalflags"].assign( string2RWCString(_additionalFlags) )
                    << dynamicCCSubstationTable["saenabledid"].assign( _saEnabledId );

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
                dout << CtiTime() << " - Inserted area into dynamicCCSubstation: " << getPAOName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            RWDBInserter inserter = dynamicCCSubstationTable.inserter();
            //TS FLAG
            inserter << _paoid
                    <<  string2RWCString(addFlags)
                    << _saEnabledId;

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
void CtiCCSubstation::setDynamicData(RWDBReader& rdr)
{   
    rdr["additionalflags"] >> _additionalFlags;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);
    _ovUvDisabledFlag = (_additionalFlags[0]=='y'?TRUE:FALSE);
    _saEnabledFlag = (_additionalFlags[1]=='y'?TRUE:FALSE);
    _voltReductionFlag = (_additionalFlags[2]=='y'?TRUE:FALSE);
    _recentlyControlledFlag = (_additionalFlags[3]=='y'?TRUE:FALSE);
    _stationUpdatedFlag = (_additionalFlags[4]=='y'?TRUE:FALSE);
    _childVoltReductionFlag = (_additionalFlags[5]=='y'?TRUE:FALSE);

    if (_voltReductionControlId <= 0)
    {
        setVoltReductionFlag(FALSE);
    }

    rdr["saenabledid"] >> _saEnabledId;
    _insertDynamicDataFlag = FALSE;
    _dirty = false;
}


/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the area
---------------------------------------------------------------------------*/
LONG CtiCCSubstation::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the area
---------------------------------------------------------------------------*/
const string& CtiCCSubstation::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the area
---------------------------------------------------------------------------*/
const string& CtiCCSubstation::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the area
---------------------------------------------------------------------------*/
const string& CtiCCSubstation::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the area
---------------------------------------------------------------------------*/
const string& CtiCCSubstation::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the area
---------------------------------------------------------------------------*/
const string& CtiCCSubstation::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCSubstation::getDisableFlag() const
{
    return _disableflag;
}
/*---------------------------------------------------------------------------
    getOvUvDisabledFlag

    Returns the ovuv disable flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstation::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}
/*---------------------------------------------------------------------------
    getVoltReductionFlag

    Returns the VoltReduction flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstation::getVoltReductionFlag() const
{
    return _voltReductionFlag;
}
/*---------------------------------------------------------------------------
    getChildVoltReductionFlag

    Returns the ChildVoltReduction flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstation::getChildVoltReductionFlag() const
{
    return _childVoltReductionFlag;
}
/*---------------------------------------------------------------------------
    getVoltReductionControlId

    Returns the VoltReduction pointId of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstation::getVoltReductionControlId() const
{
    return _voltReductionControlId;
}


/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (AreaId) of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstation::getParentId() const
{
    return _parentId;
}

/*---------------------------------------------------------------------------
    getParentName

    Returns the ParentName of the substation bus
---------------------------------------------------------------------------*/
const string& CtiCCSubstation::getParentName() const
{
    return _parentName;
}
/*---------------------------------------------------------------------------
    getDisplayOrder

    Returns the displayOrder (AreaId) of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstation::getDisplayOrder() const
{
    return _displayOrder;
}
/*---------------------------------------------------------------------------
    getPFactor

    Returns the pfactor  of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstation::getPFactor() const
{
    return _pfactor;
}

/*---------------------------------------------------------------------------
    getEstPFactor

    Returns the estpfactor  of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstation::getEstPFactor() const
{
    return _estPfactor;
}

BOOL CtiCCSubstation::getSaEnabledFlag() const
{
    return _saEnabledFlag;
}

BOOL CtiCCSubstation::getRecentlyControlledFlag() const
{
    return _recentlyControlledFlag;
}
BOOL CtiCCSubstation::getStationUpdatedFlag() const
{
    return _stationUpdatedFlag;
}

LONG CtiCCSubstation::getSaEnabledId() const
{
    return _saEnabledId;
}
/*---------------------------------------------------------------------------
    isDirty()
    
    Returns the dirty flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCSubstation::isDirty() const
{
    return _dirty;
}


/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the area - use with caution
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the area
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setPAOCategory(const string& category)
{
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the area
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setPAOClass(const string& pclass)
{
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the area
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setPAOName(const string& name)
{
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the area
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setPAOType(const string& _type)
{
    _paotype = _type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the area
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setPAODescription(const string& description)
{
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the area
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setDisableFlag(BOOL disable)
{
    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovuv disable flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setOvUvDisabledFlag(BOOL flag)
{
    if (_ovUvDisabledFlag != flag)
    {
        _dirty = TRUE;
    }
    _ovUvDisabledFlag = flag;
    return *this;
}
/*---------------------------------------------------------------------------
    setVoltReductionFlag

    Sets the VoltReduction flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setVoltReductionFlag(BOOL flag)
{                 
    if (_voltReductionFlag != flag)
    {
        _dirty = TRUE;
    }
    _voltReductionFlag = flag;
    return *this;
}
/*---------------------------------------------------------------------------
    setChildVoltReductionFlag

    Sets the ChildVoltReduction flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setChildVoltReductionFlag(BOOL flag)
{                 
    if (_childVoltReductionFlag != flag)
    {
        _dirty = TRUE;
    }
    _childVoltReductionFlag = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    setVoltReductionControlId

    Sets the VoltReductionControlId of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setVoltReductionControlId(LONG pointid)
{
    _voltReductionControlId = pointid;
    return *this;
}


/*---------------------------------------------------------------------------
    setParentId

    Sets the parentID (AreaID) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setParentId(LONG parentId)
{
    _parentId = parentId;
    return *this;
}


/*---------------------------------------------------------------------------
    setParentName

    Sets the ParentName in the substation bus
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setParentName(const string& parentName)
{
    if (_parentName != parentName)
    {
        _dirty = TRUE;
    }
    _parentName = parentName;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisplayOrder

    Sets the DisplayOrder (AreaID) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setDisplayOrder(LONG displayOrder)
{
    _displayOrder = displayOrder;
    return *this;
}
/*---------------------------------------------------------------------------
    setPFactor

    Sets the pfactor (calculated) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setPFactor(DOUBLE pfactor)
{

    _pfactor = pfactor;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstPFactor

    Sets the estpfactor (calculated) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setEstPFactor(DOUBLE estpfactor)
{
    _estPfactor = estpfactor;
    return *this;
}

/*---------------------------------------------------------------------------
    setSaEnabledFlag

    Sets the Special Area Enabled Flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setSaEnabledFlag(BOOL flag)
{
    if (_saEnabledFlag != flag)
    {
        _dirty = TRUE;
    }
    _saEnabledFlag = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the Substation RecentlyControlled Flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setRecentlyControlledFlag(BOOL flag)
{
    if (_recentlyControlledFlag != flag)
    {
        _dirty = TRUE;
        _stationUpdatedFlag = TRUE;
    }
    _recentlyControlledFlag = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the Substation RecentlyControlled Flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setStationUpdatedFlag(BOOL flag)
{
    if (_stationUpdatedFlag != flag)
    {
        _dirty = TRUE;
    }
    _stationUpdatedFlag = flag;
    return *this;
}


/*---------------------------------------------------------------------------
    setSaEnabledId

    Sets the Special Area Enabled Id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setSaEnabledId(LONG saId)
{
    if (_saEnabledId != saId)
    {
        _dirty = TRUE;
    }

    _saEnabledId = saId;
    return *this;
}

/*-------------------------------------------------------------------------
    calculatePowerFactor


--------------------------------------------------------------------------*/
DOUBLE CtiCCSubstation::calculatePowerFactor(DOUBLE kvar, DOUBLE kw)
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

void CtiCCSubstation::checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    std::list <long>::iterator busIter = NULL;


    busIter = getCCSubIds()->begin();
    
    while (busIter != getCCSubIds()->end() )
    {
        currentSubstationBus = store->findSubBusByPAObjectID(*busIter);
        busIter++;

        if (currentSubstationBus != NULL && currentSubstationBus->getVerificationFlag())
        {          
            try
            {
                //reset VerificationFlag
                capMessages.push_back(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, currentSubstationBus->getPAOId(),0, -1));
                
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
                    
        }
    }

}


/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::checkAndUpdateRecentlyControlledFlag()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    std::list <long>::iterator busIter = NULL;

    int numberOfSubBusesPending = 0;

    busIter = getCCSubIds()->begin();

    while (busIter != getCCSubIds()->end() )
    {
        currentSubstationBus = store->findSubBusByPAObjectID(*busIter);
        busIter++;

        if (currentSubstationBus->getRecentlyControlledFlag())
        {
            setRecentlyControlledFlag(TRUE);
            numberOfSubBusesPending += 1;
        }
    }
    if (numberOfSubBusesPending == 0)
    {
        setRecentlyControlledFlag(FALSE);
    }

    return *this;
}


CtiCCSubstation& CtiCCSubstation::checkAndUpdateChildVoltReductionFlags()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    std::list <long>::iterator busIter = NULL;

    int numberOfSubBusesVoltReducting = 0;

    busIter = getCCSubIds()->begin();

    while (busIter != getCCSubIds()->end() )
    {
        currentSubstationBus = store->findSubBusByPAObjectID(*busIter);
        busIter++;

        if (currentSubstationBus->getVoltReductionFlag())
        {
            setChildVoltReductionFlag(TRUE);
            numberOfSubBusesVoltReducting += 1;
        }
    }
    if (numberOfSubBusesVoltReducting == 0)
    {
        setChildVoltReductionFlag(FALSE);
    }

    return *this;
}
