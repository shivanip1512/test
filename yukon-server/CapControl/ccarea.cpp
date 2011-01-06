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
#include "database_reader.h"
#include "database_writer.h"

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCArea, CTICCAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCArea::CtiCCArea()
    : Controllable(0),
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

CtiCCArea::CtiCCArea(StrategyManager * strategyManager)
    : Controllable(strategyManager),
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

CtiCCArea::CtiCCArea(Cti::RowReader& rdr, StrategyManager * strategyManager)
    : Controllable(rdr, strategyManager)
{
    restore(rdr);
    _operationStats.setPAOId(getPaoId());
    _confirmationStats.setPAOId(getPaoId());
}

CtiCCArea::CtiCCArea(const CtiCCArea& area)
    : Controllable(area)
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

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCArea::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );
    CapControlPao::saveGuts(ostrm);

    ostrm <<  _ovUvDisabledFlag;
    ostrm << _subStationIds.size();

    Cti::CapControl::PaoIdList::const_iterator iter = _subStationIds.begin();
    for( ; iter != _subStationIds.end();iter++)
    {
        ostrm << (long)*iter;
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
        << _voltReductionControlValue
        << _childVoltReductionFlag;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::operator=(const CtiCCArea& right)
{
    Controllable::operator=(right);

    if( this != &right )
    {
        _voltReductionControlPointId = right._voltReductionControlPointId;
        _voltReductionControlValue = right._voltReductionControlValue;
        _childVoltReductionFlag = right._childVoltReductionFlag;

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
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCArea* CtiCCArea::replicate() const
{
    return(new CtiCCArea(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCArea::restore(Cti::RowReader& rdr)
{
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

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCArea::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    {
        if( !_insertDynamicDataFlag )
        {
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_ovUvDisabledFlag?'Y':'N');
            addFlags[1] = (_reEnableAreaFlag?'Y':'N');
            addFlags[2] = (_childVoltReductionFlag?'Y':'N');
            addFlags[3] = (_areaUpdatedFlag?'Y':'N');
            _additionalFlags = string(char2string(*addFlags) + char2string(*(addFlags+1)) + char2string(*(addFlags+2)) +
                                char2string(*(addFlags+3)));
            _additionalFlags.append("NNNNNNNNNNNNNNNN");

            static const string updateSql = "update dynamicccarea set additionalflags = ?, controlvalue = ?"
                                            " where areaid = ?";
            Cti::Database::DatabaseWriter updater(conn, updateSql);

            updater << _additionalFlags << _voltReductionControlValue << getPaoId();

            if(updater.execute())    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updateSql << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted area into dynamicCCArea: " << getPaoName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            static const string insertSql = "insert into dynamicccarea values (?, ?, ?)";
            Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

            dbInserter << getPaoId() << addFlags <<  _voltReductionControlValue;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << dbInserter.asString() << endl;
                }
            }

            if(dbInserter.execute())    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << insertSql << endl;
                    }
                }
            }
        }

        if (getOperationStats().isDirty())
            getOperationStats().dumpDynamicData(conn, currentDateTime);
    }
}
void CtiCCArea::setDynamicData(Cti::RowReader& rdr)
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

    Cti::CapControl::PaoIdList::iterator subIter = getSubStationList()->begin();;
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

    Cti::CapControl::PaoIdList::iterator subIter = getSubStationList()->begin();;
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

