#include "precompiled.h"

#include "ccarea.h"
#include "ccid.h"
#include "database_util.h"
#include "ccsubstationbusstore.h"

using std::endl;
using std::string;

extern unsigned long _CC_DEBUG;

using Cti::CapControl::PaoIdVector;

DEFINE_COLLECTABLE( CtiCCArea, CTICCAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCArea::CtiCCArea()
    : CtiCCAreaBase(0),
      _reEnableAreaFlag(false),
      _childVoltReductionFlag(false),
      _insertDynamicDataFlag(false)
{
}

CtiCCArea::CtiCCArea(StrategyManager * strategyManager)
    : CtiCCAreaBase(strategyManager),
      _reEnableAreaFlag(false),
      _childVoltReductionFlag(false),
      _insertDynamicDataFlag(false)
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
    operator=
---------------------------------------------------------------------------*/
CtiCCArea& CtiCCArea::operator=(const CtiCCArea& right)
{
    CtiCCAreaBase::operator=(right);

    if( this != &right )
    {
        _childVoltReductionFlag = right._childVoltReductionFlag;
        _reEnableAreaFlag = right._reEnableAreaFlag;
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

    _insertDynamicDataFlag = true;
    setDirty(false);
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCArea::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( isDirty() )
    {
        if( !_insertDynamicDataFlag )
        {
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (getOvUvDisabledFlag()?'Y':'N');
            addFlags[1] = (_reEnableAreaFlag?'Y':'N');
            addFlags[2] = (_childVoltReductionFlag?'Y':'N');
            addFlags[3] = (getAreaUpdatedFlag()?'Y':'N');
            string additionalFlags = string(char2string(*addFlags) + char2string(*(addFlags+1)) + char2string(*(addFlags+2)) +
                                char2string(*(addFlags+3)));
            additionalFlags.append("NNNNNNNNNNNNNNNN");

            static const string updateSql = "update dynamicccarea set additionalflags = ?, controlvalue = ?"
                                            " where areaid = ?";
            Cti::Database::DatabaseWriter updater(conn, updateSql);

            updater << additionalFlags << getVoltReductionControlValue() << getPaoId();

            if( Cti::Database::executeCommand( updater, __FILE__, __LINE__ ))
            {
                setDirty(false); // No error occured!
            }
        }
        else
        {
            CTILOG_INFO(dout, "Inserted area into dynamicCCArea: " << getPaoName());
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            static const string insertSql = "insert into dynamicccarea values (?, ?, ?)";
            Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

            dbInserter << getPaoId() << addFlags <<  getVoltReductionControlValue();

            if( Cti::Database::executeCommand( dbInserter, __FILE__, __LINE__, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
            {
                _insertDynamicDataFlag = false;
                setDirty(false); // No error occured!
            }
        }
    }

    getOperationStats().dumpDynamicData(conn, currentDateTime);
}

void CtiCCArea::setDynamicData(Cti::RowReader& rdr)
{
    CtiCCAreaBase::setDynamicData(rdr);
    _reEnableAreaFlag = (getAdditionalFlags()[1]=='y');
    _childVoltReductionFlag = (getAdditionalFlags()[2]=='y');
    setAreaUpdatedFlag(getAdditionalFlags()[3]=='y');

    _insertDynamicDataFlag = false;
    setDirty(false);
}

/*---------------------------------------------------------------------------
    getReEnableAreaFlag

    Returns the ovuv disable flag of the area
---------------------------------------------------------------------------*/
bool CtiCCArea::getReEnableAreaFlag() const
{
    return _reEnableAreaFlag;
}

bool CtiCCArea::getChildVoltReductionFlag() const
{
    return _childVoltReductionFlag;
}

/*---------------------------------------------------------------------------
    setChildVoltReductionflag

    Sets the ControlValue of the area
---------------------------------------------------------------------------*/
void CtiCCArea::setChildVoltReductionFlag(bool flag)
{
    if(_childVoltReductionFlag != flag)
    {
        setAreaUpdatedFlag(true);
        setDirty(true);
    }
    _childVoltReductionFlag = flag;
}


/*---------------------------------------------------------------------------
    setReEnableAreaFlag

    Sets the reEnable Area flag of the area
---------------------------------------------------------------------------*/
void CtiCCArea::setReEnableAreaFlag(bool flag)
{
    if(_reEnableAreaFlag != flag)
        setDirty(true);
    _reEnableAreaFlag = flag;
}



void CtiCCArea::checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiLockGuard<CtiCriticalSection>  guard(store->getMux());


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


void CtiCCArea::checkAndUpdateChildVoltReductionFlags()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiLockGuard<CtiCriticalSection>  guard(store->getMux());

    bool isChildSubstationReducing = false;

    for each (const long paoId in getSubstationIds())
    {
        CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(paoId);

        if (currentStation->getVoltReductionFlag() || currentStation->getChildVoltReductionFlag())
        {
            isChildSubstationReducing = true;
        }
    }

    setChildVoltReductionFlag( isChildSubstationReducing );
}

