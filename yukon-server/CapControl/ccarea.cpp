#include "precompiled.h"

#include "ccarea.h"
#include "ccid.h"
#include "database_util.h"
#include "ccsubstationbusstore.h"

using std::endl;
using std::string;

extern unsigned long _CC_DEBUG;

using Cti::CapControl::deserializeFlag;
using Cti::CapControl::serializeFlag;
using Cti::CapControl::PaoIdVector;

DEFINE_COLLECTABLE( CtiCCArea, CTICCAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCArea::CtiCCArea()
    : CtiCCAreaBase(0),
      _reEnableAreaFlag(false),
      _childVoltReductionFlag(false)
{
}

CtiCCArea::CtiCCArea(StrategyManager * strategyManager)
    : CtiCCAreaBase(strategyManager),
      _reEnableAreaFlag(false),
      _childVoltReductionFlag(false)
{
}

CtiCCArea::CtiCCArea(Cti::RowReader& rdr, StrategyManager * strategyManager)
    :   CtiCCAreaBase( rdr, strategyManager ),
        _reEnableAreaFlag( false ),
        _childVoltReductionFlag( false )
{
    restoreStaticData(rdr);

    if ( hasDynamicData( rdr["additionalflags"] ) )
    {
        restoreDynamicData( rdr );
    }
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
void CtiCCArea::restoreStaticData(Cti::RowReader& rdr)
{
    // nothing to restore at this level -- see base class
}

void CtiCCArea::restoreDynamicData(Cti::RowReader& rdr)
{
    std::string flags;

    rdr["additionalflags"] >> flags;

    _reEnableAreaFlag       = deserializeFlag( flags, 1 );
    _childVoltReductionFlag = deserializeFlag( flags, 2 );
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCArea::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    writeDynamicData( conn, currentDateTime );

    getOperationStats().dumpDynamicData(conn, currentDateTime);
}

std::string CtiCCArea::formatFlags() const
{
    std::string flags( 20, 'N' );

    flags[ 0 ] = serializeFlag( getOvUvDisabledFlag() );
    flags[ 1 ] = serializeFlag( _reEnableAreaFlag );
    flags[ 2 ] = serializeFlag( _childVoltReductionFlag );
    flags[ 3 ] = serializeFlag( getAreaUpdatedFlag() );

    return flags;
}

bool CtiCCArea::updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "UPDATE "
            "DYNAMICCCAREA "
        "SET "
            "additionalflags = ?, "
            "ControlValue = ? "
        "WHERE "
            "AreaID = ?";

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << formatFlags()
        << getVoltReductionControlValue()
        << getPaoId();

    return Cti::Database::executeCommand( writer, __FILE__, __LINE__ );
}

bool CtiCCArea::insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "INSERT INTO "
            "DYNAMICCCAREA "
        "VALUES "
            "(?, ?, ?)";

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << getPaoId()
        << formatFlags()
        << getVoltReductionControlValue();

    return Cti::Database::executeCommand( writer, __FILE__, __LINE__, Cti::Database::LogDebug( _CC_DEBUG & CC_DEBUG_DATABASE ) );
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
    if ( updateDynamicValue( _childVoltReductionFlag, flag ) )
    {
        setAreaUpdatedFlag( true );
    }
}


/*---------------------------------------------------------------------------
    setReEnableAreaFlag

    Sets the reEnable Area flag of the area
---------------------------------------------------------------------------*/
void CtiCCArea::setReEnableAreaFlag(bool flag)
{
    updateDynamicValue( _reEnableAreaFlag, flag );
}

void CtiCCArea::checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiLockGuard<CtiCriticalSection>  guard(store->getMux());

    for ( long paoId : getSubstationIds() )
    {
        CtiCCSubstationPtr currentSubstation = store->findSubstationByPAObjectID(paoId);

        if (currentSubstation &&
            ( getDisableFlag() || currentSubstation->getDisableFlag() ) )
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

    for ( const long paoId : getSubstationIds() )
    {
        CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(paoId);

        if (currentStation->getVoltReductionFlag() || currentStation->getChildVoltReductionFlag())
        {
            isChildSubstationReducing = true;
        }
    }

    setChildVoltReductionFlag( isChildSubstationReducing );
}

