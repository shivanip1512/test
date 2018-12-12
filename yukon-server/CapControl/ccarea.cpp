#include "precompiled.h"

#include "ccarea.h"
#include "ccid.h"
#include "database_util.h"
#include "row_reader.h"
#include "ccsubstationbusstore.h"
#include "ExecutorFactory.h"

extern unsigned long _CC_DEBUG;
extern bool _AUTO_VOLT_REDUCTION;

using Cti::CapControl::deserializeFlag;
using Cti::CapControl::populateFlag;
using Cti::CapControl::PaoIdVector;

DEFINE_COLLECTABLE( CtiCCArea, CTICCAREA_ID )

/*
    Constructors
*/
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
    // no static data to restore here
    if ( hasDynamicData( rdr["additionalflags"] ) )
    {
        restoreDynamicData( rdr );
    }
}

/* 
    Clone the object via default defined copy constructor 
*/
CtiCCArea* CtiCCArea::replicate() const
{
    return new CtiCCArea( *this );
}

/* 
    Restores the dynamic data portion of the area from the given Reader
*/
void CtiCCArea::restoreDynamicData(Cti::RowReader& rdr)
{
    std::string flags;

    rdr["additionalflags"] >> flags;

    _reEnableAreaFlag       = deserializeFlag( flags, Index_ReEnableArea );
    _childVoltReductionFlag = deserializeFlag( flags, Index_ChildVReduction );
}

/*
    Writes out the dynamic information for this area.
*/
void CtiCCArea::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    writeDynamicData( conn, currentDateTime );

    getOperationStats().dumpDynamicData(conn, currentDateTime);
}

/*
    Generate the additionalflags string for the dynamic data
*/
std::string CtiCCArea::formatFlags() const
{
    std::string flags( Length_DynamicFlags, populateFlag( false ) );

    flags[ Index_OvUvDisabled ]    = populateFlag( getOvUvDisabledFlag() );
    flags[ Index_ReEnableArea ]    = populateFlag( _reEnableAreaFlag );
    flags[ Index_ChildVReduction ] = populateFlag( _childVoltReductionFlag );
    flags[ Index_AreaUpdated ]     = populateFlag( getAreaUpdatedFlag() );

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

    return Cti::Database::executeCommand( writer, CALLSITE );
}

bool CtiCCArea::insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "INSERT INTO "
            "DYNAMICCCAREA "
        "VALUES "
            "(?, ?, ?)";

    CTILOG_INFO( dout, "Inserting area into DYNAMICCCAREA: " << getPaoName() );

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << getPaoId()
        << formatFlags()
        << getVoltReductionControlValue();

    return Cti::Database::executeCommand( writer, CALLSITE, Cti::Database::LogDebug( _CC_DEBUG & CC_DEBUG_DATABASE ) );
}

/*
    Accessors
*/
bool CtiCCArea::getReEnableAreaFlag() const
{
    return _reEnableAreaFlag;
}

bool CtiCCArea::getChildVoltReductionFlag() const
{
    return _childVoltReductionFlag;
}

/*
    Mutators
        * static data should be updated through updateStaticValue().
        * dynamic data should be updated through updateDynamicValue() to maintain the state of the _dirty
            flag ensuring proper DB serialization.
        * anything that changes a value on the web display should set _areaUpdatedFlag to make sure the
            area message is sent to the UI.
*/
void CtiCCArea::setChildVoltReductionFlag(const bool flag)
{
    if ( updateDynamicValue( _childVoltReductionFlag, flag ) )
    {
        setAreaUpdatedFlag( true );
    }
}

void CtiCCArea::setReEnableAreaFlag(const bool flag)
{
    updateDynamicValue( _reEnableAreaFlag, flag );
}


void CtiCCArea::checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    for ( const long paoId : getSubstationIds() )
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

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

void CtiCCArea::handleSpecializedPointData( const CtiPointDataMsg & message )
{
    const long   pointID = message.getId();
    const double value   = message.getValue();

    if ( pointID == getVoltReductionControlPointId() )
    {
        const bool reduceVoltage = value;

        if ( reduceVoltage != getVoltReductionControlValue() )
        {
            setVoltReductionControlValue( reduceVoltage );

            if ( _AUTO_VOLT_REDUCTION )
            {
                CtiCCExecutorFactory::createExecutor(
                    new ItemCommand( getVoltReductionControlValue()
                                        ? CapControlCommand::AUTO_DISABLE_OVUV
                                        : CapControlCommand::AUTO_ENABLE_OVUV,
                                     getPaoId() ) )->execute();
            }

            checkAndUpdateChildVoltReductionFlags();
        }
    }
}

