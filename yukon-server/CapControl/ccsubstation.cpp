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
#include "database_writer.h"

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCSubstation, CTICCSUBSTATION_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSubstation::CtiCCSubstation() :
_parentId(0),
_displayOrder(0),
_ovUvDisabledFlag(false),
_voltReductionFlag(false),
_recentlyControlledFlag(false),
_stationUpdatedFlag(false),
_childVoltReductionFlag(false),
_pfactor(0),
_estPfactor(0),
_saEnabledFlag(false),
_saEnabledId(0),
_voltReductionControlId(0),
_insertDynamicDataFlag(false),
_dirty(false)
{
}

CtiCCSubstation::CtiCCSubstation(Cti::RowReader& rdr) : CapControlPao(rdr)
{
    restore(rdr);
    _operationStats.setPAOId(getPaoId());
    _confirmationStats.setPAOId(getPaoId());
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

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCSubstation::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts(ostrm);
    CapControlPao::saveGuts(ostrm);

    ostrm << _parentId
          << _ovUvDisabledFlag;

    ostrm << _subBusIds.size();
    std::list<LONG>::const_iterator iter = _subBusIds.begin();

    for( ; iter != _subBusIds.end(); iter++)
    {
        ostrm << (LONG)*iter;
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

    ostrm << pfDisplayValue
        << estPfDisplayValue
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
        CapControlPao::operator=(right);

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
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSubstation* CtiCCSubstation::replicate() const
{
    return(new CtiCCSubstation(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCSubstation::restore(Cti::RowReader& rdr)
{
    CapControlPao::restore(rdr);

    string tempBoolString;

    rdr["voltreductionpointid"] >> _voltReductionControlId;

    setOvUvDisabledFlag(FALSE);
    setVoltReductionFlag(FALSE);
    setPFactor(-1);
    setEstPFactor(-1);
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

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSubstation::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    {
        if( !_insertDynamicDataFlag )
        {
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

            static const string updaterSql = "update dynamicccsubstation set additionalflags = ?, saenabledid = ?"
                                             " where substationid = ?";
            Cti::Database::DatabaseWriter updater(conn, updaterSql);

            updater << _additionalFlags <<  _saEnabledId << getPaoId();

            if(updater.execute())    // No error occured!
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << updaterSql << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted substation into dynamicCCSubstation: " << getPaoName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            static const string inserterSql = "insert into dynamicccsubstation values (?, ?, ?)";
            Cti::Database::DatabaseWriter inserter(conn, inserterSql);

            inserter << getPaoId() << addFlags << _saEnabledId;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << inserter.asString() << endl;
                }
            }

            if(inserter.execute())    // No error occured!
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
void CtiCCSubstation::setDynamicData(Cti::RowReader& rdr)
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
        _stationUpdatedFlag = TRUE;
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

    if (_pfactor != pfactor)
    {
        setStationUpdatedFlag(TRUE);
        _dirty = TRUE;
        _pfactor = pfactor;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setEstPFactor

    Sets the estpfactor (calculated) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setEstPFactor(DOUBLE estpfactor)
{

    if (_estPfactor != estpfactor)
    {
        setStationUpdatedFlag(TRUE);
        _dirty = TRUE;
        _estPfactor = estpfactor;
    }
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
    PaoIdList::iterator busIter;


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
                capMessages.push_back(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, currentSubstationBus->getPaoId(),0, -1, currentSubstationBus->getVerificationDisableOvUvFlag()));

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
    PaoIdList::iterator busIter;

    int numberOfSubBusesPending = 0;

    busIter = getCCSubIds()->begin();

    while (busIter != getCCSubIds()->end() )
    {
        currentSubstationBus = store->findSubBusByPAObjectID(*busIter);
        busIter++;

        if (currentSubstationBus->getRecentlyControlledFlag() || currentSubstationBus->getPerformingVerificationFlag())
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
    PaoIdList::iterator busIter;

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
