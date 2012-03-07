#include "precompiled.h"

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

using std::endl;

extern ULONG _CC_DEBUG;

using Cti::CapControl::PaoIdVector;

RWDEFINE_COLLECTABLE( CtiCCArea, CTICCAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCArea::CtiCCArea()
    : CtiCCAreaBase(0),
      _reEnableAreaFlag(false),
      _childVoltReductionFlag(false),
      _insertDynamicDataFlag(false),
      _areaUpdatedFlag(false)
{
}

CtiCCArea::CtiCCArea(StrategyManager * strategyManager)
    : CtiCCAreaBase(strategyManager),
      _reEnableAreaFlag(false),
      _childVoltReductionFlag(false),
      _insertDynamicDataFlag(false),
      _areaUpdatedFlag(false)
{
}

CtiCCArea::CtiCCArea(Cti::RowReader& rdr, StrategyManager * strategyManager)
    : CtiCCAreaBase(rdr, strategyManager)
{
    restore(rdr);

}

CtiCCArea::CtiCCArea(const CtiCCArea& area)
    : CtiCCAreaBase(area)
{
    operator=(area);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCArea::~CtiCCArea()
{

}


/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCArea::saveGuts(RWvostream& ostrm ) const
{
    CtiCCAreaBase::saveGuts(ostrm);

    ostrm << getOvUvDisabledFlag();
    Cti::CapControl::PaoIdVector substationIds = getSubstationIds();
    ostrm << substationIds.size();
    ostrm << substationIds;
    
    double pfDisplayValue = getPFactor();
    double estPfDisplayValue = getEstPFactor();

    // Modifying the display value of pFactor to represent +100% values as a negative value.
    if (pfDisplayValue > 1)
    {
        pfDisplayValue -= 2;
    }
    if (estPfDisplayValue > 1)
    {
        estPfDisplayValue -= 2;
    }
    ostrm << pfDisplayValue
          << estPfDisplayValue
          << getVoltReductionControlValue()
          << _childVoltReductionFlag;

}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::operator=(const CtiCCArea& right)
{
    CtiCCAreaBase::operator=(right);

    if( this != &right )
    {
        _childVoltReductionFlag = right._childVoltReductionFlag;
        _reEnableAreaFlag = right._reEnableAreaFlag;
        _areaUpdatedFlag = right._areaUpdatedFlag;
        _insertDynamicDataFlag = right._insertDynamicDataFlag;

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
    CtiCCAreaBase::restore(rdr);
    _reEnableAreaFlag = false;
    _childVoltReductionFlag = false;
    _areaUpdatedFlag = false;

    _insertDynamicDataFlag = true;
    setDirty(false);
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
            addFlags[0] = (getOvUvDisabledFlag()?'Y':'N');
            addFlags[1] = (_reEnableAreaFlag?'Y':'N');
            addFlags[2] = (_childVoltReductionFlag?'Y':'N');
            addFlags[3] = (_areaUpdatedFlag?'Y':'N');
            string additionalFlags = string(char2string(*addFlags) + char2string(*(addFlags+1)) + char2string(*(addFlags+2)) +
                                char2string(*(addFlags+3)));
            additionalFlags.append("NNNNNNNNNNNNNNNN");

            static const string updateSql = "update dynamicccarea set additionalflags = ?, controlvalue = ?"
                                            " where areaid = ?";
            Cti::Database::DatabaseWriter updater(conn, updateSql);

            updater << additionalFlags << getVoltReductionControlValue() << getPaoId();

            if(updater.execute())    // No error occured!
            {
                setDirty(false);
            }
            else
            {
                setDirty(true);
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

            dbInserter << getPaoId() << addFlags <<  getVoltReductionControlValue();

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
                setDirty(false);
            }
            else
            {
                setDirty(true);
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
    CtiCCAreaBase::setDynamicData(rdr);
    _reEnableAreaFlag = (getAdditionalFlags()[1]=='y'?TRUE:FALSE);
    _childVoltReductionFlag = (getAdditionalFlags()[2]=='y'?TRUE:FALSE);
    _areaUpdatedFlag = (getAdditionalFlags()[3]=='y'?TRUE:FALSE);

    _insertDynamicDataFlag = FALSE;
    setDirty(false);


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
    getReEnableAreaFlag

    Returns the ovuv disable flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCArea::getReEnableAreaFlag() const
{
    return _reEnableAreaFlag;
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
    setChildVoltReductionflag

    Sets the ControlValue of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setChildVoltReductionFlag(BOOL flag)
{
    if(_childVoltReductionFlag != flag)
    {
        setAreaUpdatedFlag(TRUE);
        setDirty(true);
    }
    _childVoltReductionFlag = flag;
    return *this;
}


/*---------------------------------------------------------------------------
    setReEnableAreaFlag

    Sets the reEnable Area flag of the area
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::setReEnableAreaFlag(BOOL flag)
{
    if(_reEnableAreaFlag != flag)
        setDirty(true);
    _reEnableAreaFlag = flag;
    return *this;
}



void CtiCCArea::checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());


    CtiCCSubstationPtr currentSubstation = NULL;

    for each (long paoId in getSubstationIds())
    {
        currentSubstation = store->findSubstationByPAObjectID(paoId);
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

    CtiCCSubstationPtr currentStation = NULL;

    int numberOfStationsVoltReducting = 0;

    for each (long paoId in getSubstationIds())
    {
        currentStation = store->findSubstationByPAObjectID(paoId);

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

