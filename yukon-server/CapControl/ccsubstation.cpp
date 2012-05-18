#include "precompiled.h"

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
#include "MsgVerifyBanks.h"

using std::endl;

extern unsigned long _CC_DEBUG;

using Cti::CapControl::PaoIdVector;
using Cti::CapControl::setVariableIfDifferent;

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

    for each(long busId in _subBusIds)
    {
        ostrm << busId;
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

    setParentId(0);
    setDisplayOrder(0);
    setOvUvDisabledFlag(false);
    setVoltReductionFlag(false);
    setPFactor(-1);
    setEstPFactor(-1);
    setSaEnabledFlag(false);
    setSaEnabledId(0);
    setRecentlyControlledFlag(false);
    setStationUpdatedFlag(false);
    setChildVoltReductionFlag(false);

    _insertDynamicDataFlag = true;
    _dirty = true;

}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSubstation::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if ( _dirty )
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
                _dirty = false;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updaterSql << endl;
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
                _insertDynamicDataFlag = false;
                _dirty = false;
            }
            else
            {

                string loggedSQLstring = inserter.asString();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << loggedSQLstring << endl;
                }
            }
        }

        getOperationStats().dumpDynamicData(conn, currentDateTime);
    }
}
void CtiCCSubstation::setDynamicData(Cti::RowReader& rdr)
{
    rdr["additionalflags"] >> _additionalFlags;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);
    _ovUvDisabledFlag = (_additionalFlags[0]=='y');
    _saEnabledFlag = (_additionalFlags[1]=='y');
    _voltReductionFlag = (_additionalFlags[2]=='y');
    _recentlyControlledFlag = (_additionalFlags[3]=='y');
    _stationUpdatedFlag = (_additionalFlags[4]=='y');
    _childVoltReductionFlag = (_additionalFlags[5]=='y');

    if (_voltReductionControlId <= 0)
    {
        setVoltReductionFlag(false);
    }

    rdr["saenabledid"] >> _saEnabledId;
    _insertDynamicDataFlag = false;
    _dirty = false;
}

/*---------------------------------------------------------------------------
    getOvUvDisabledFlag

    Returns the ovuv disable flag of the substation
---------------------------------------------------------------------------*/
bool CtiCCSubstation::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}
/*---------------------------------------------------------------------------
    getVoltReductionFlag

    Returns the VoltReduction flag of the substation
---------------------------------------------------------------------------*/
bool CtiCCSubstation::getVoltReductionFlag() const
{
    return _voltReductionFlag;
}
/*---------------------------------------------------------------------------
    getChildVoltReductionFlag

    Returns the ChildVoltReduction flag of the substation
---------------------------------------------------------------------------*/
bool CtiCCSubstation::getChildVoltReductionFlag() const
{
    return _childVoltReductionFlag;
}
/*---------------------------------------------------------------------------
    getVoltReductionControlId

    Returns the VoltReduction pointId of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstation::getVoltReductionControlId() const
{
    return _voltReductionControlId;
}


/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (AreaId) of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstation::getParentId() const
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
long CtiCCSubstation::getDisplayOrder() const
{
    return _displayOrder;
}
/*---------------------------------------------------------------------------
    getPFactor

    Returns the pfactor  of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstation::getPFactor() const
{
    return _pfactor;
}

/*---------------------------------------------------------------------------
    getEstPFactor

    Returns the estpfactor  of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstation::getEstPFactor() const
{
    return _estPfactor;
}

bool CtiCCSubstation::getSaEnabledFlag() const
{
    return _saEnabledFlag;
}

bool CtiCCSubstation::getRecentlyControlledFlag() const
{
    return _recentlyControlledFlag;
}
bool CtiCCSubstation::getStationUpdatedFlag() const
{
    return _stationUpdatedFlag;
}

long CtiCCSubstation::getSaEnabledId() const
{
    return _saEnabledId;
}
/*---------------------------------------------------------------------------
    isDirty()

    Returns the dirty flag of the area
---------------------------------------------------------------------------*/
bool CtiCCSubstation::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovuv disable flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setOvUvDisabledFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_ovUvDisabledFlag, flag);
    return *this;
}
/*---------------------------------------------------------------------------
    setVoltReductionFlag

    Sets the VoltReduction flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setVoltReductionFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_voltReductionFlag, flag);
    return *this;
}
/*---------------------------------------------------------------------------
    setChildVoltReductionFlag

    Sets the ChildVoltReduction flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setChildVoltReductionFlag(bool flag)
{
    bool flagChanged = setVariableIfDifferent(_childVoltReductionFlag, flag);
    _dirty |= flagChanged;
    _stationUpdatedFlag |= flagChanged;
    return *this;
}

/*---------------------------------------------------------------------------
    setVoltReductionControlId

    Sets the VoltReductionControlId of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setVoltReductionControlId(long pointid)
{
    _voltReductionControlId = pointid;
    return *this;
}


/*---------------------------------------------------------------------------
    setParentId

    Sets the parentID (AreaID) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setParentId(long parentId)
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
    _dirty |= setVariableIfDifferent(_parentName, parentName);
    return *this;
}

/*---------------------------------------------------------------------------
    setDisplayOrder

    Sets the DisplayOrder (AreaID) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setDisplayOrder(long displayOrder)
{
    _displayOrder = displayOrder;
    return *this;
}
/*---------------------------------------------------------------------------
    setPFactor

    Sets the pfactor (calculated) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setPFactor(double pfactor)
{
    _dirty |= setVariableIfDifferent(_pfactor, pfactor);
    return *this;
}

/*---------------------------------------------------------------------------
    setEstPFactor

    Sets the estpfactor (calculated) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setEstPFactor(double estpfactor)
{

    if (_estPfactor != estpfactor)
    {
        setStationUpdatedFlag(true);
        _dirty = true;
        _estPfactor = estpfactor;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setSaEnabledFlag

    Sets the Special Area Enabled Flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setSaEnabledFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_saEnabledFlag, flag);
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the Substation RecentlyControlled Flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setRecentlyControlledFlag(bool flag)
{
    bool flagChanged = setVariableIfDifferent(_recentlyControlledFlag, flag);
    _dirty |= flagChanged;
    _stationUpdatedFlag |= flagChanged;
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the Substation RecentlyControlled Flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setStationUpdatedFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_stationUpdatedFlag, flag);
    return *this;
}


/*---------------------------------------------------------------------------
    setSaEnabledId

    Sets the Special Area Enabled Id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::setSaEnabledId(long saId)
{
    _dirty |= setVariableIfDifferent(_saEnabledId, saId);
    return *this;
}

/*-------------------------------------------------------------------------
    calculatePowerFactor


--------------------------------------------------------------------------*/
double CtiCCSubstation::calculatePowerFactor(double kvar, double kw)
{
    double newPowerFactorValue = 1.0;
    double kva = 0.0;

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
    
    for each (long busId in getCCSubIds() )
    {
        currentSubstationBus = store->findSubBusByPAObjectID(busId);
        
        if (currentSubstationBus != NULL && currentSubstationBus->getVerificationFlag())
        {
            try
            {
                //reset VerificationFlag
                capMessages.push_back(new VerifyBanks(currentSubstationBus->getPaoId(),currentSubstationBus->getVerificationDisableOvUvFlag(), CapControlCommand::STOP_VERIFICATION));
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
    int numberOfSubBusesPending = 0;

    for each (long busId in getCCSubIds())
    {
        currentSubstationBus = store->findSubBusByPAObjectID(busId);
        
        if (currentSubstationBus->getRecentlyControlledFlag() || currentSubstationBus->getPerformingVerificationFlag())
        {
            setRecentlyControlledFlag(true);
            numberOfSubBusesPending += 1;
        }
    }
    if (numberOfSubBusesPending == 0)
    {
        setRecentlyControlledFlag(false);
    }

    return *this;
}


CtiCCSubstation& CtiCCSubstation::checkAndUpdateChildVoltReductionFlags()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    int numberOfSubBusesVoltReducting = 0;

    for each (long busId in getCCSubIds())
    {
        currentSubstationBus = store->findSubBusByPAObjectID(busId);
        
        if (currentSubstationBus->getVoltReductionFlag())
        {
            setChildVoltReductionFlag(true);
            numberOfSubBusesVoltReducting += 1;
        }
    }
    if (numberOfSubBusesVoltReducting == 0)
    {
        setChildVoltReductionFlag(false);
    }

    return *this;
}
