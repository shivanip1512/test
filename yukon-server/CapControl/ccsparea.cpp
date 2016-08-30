#include "precompiled.h"

#include "ccsparea.h"
#include "ccid.h"
#include "database_util.h"
#include "ccsubstationbusstore.h"
#include "ExecutorFactory.h"

using Cti::CapControl::populateFlag;

extern unsigned long _CC_DEBUG;
extern bool _AUTO_VOLT_REDUCTION;

DEFINE_COLLECTABLE( CtiCCSpecial, CTICCSPECIALAREA_ID )

/*
    Constructors
*/
CtiCCSpecial::CtiCCSpecial(StrategyManager * strategyManager)
    : CtiCCAreaBase(strategyManager)
{
}

CtiCCSpecial::CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager)
    : CtiCCAreaBase(rdr, strategyManager)
{
    // no static or dynamic data to restore here
}

/* 
    Clone the object via default defined copy constructor 
*/
CtiCCSpecial* CtiCCSpecial::replicate() const
{
    return new CtiCCSpecial( *this );
}

/*
    Writes out the dynamic information for this area.
*/
void CtiCCSpecial::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    writeDynamicData( conn,currentDateTime );

    getOperationStats().dumpDynamicData(conn, currentDateTime);
}

/*
    Generate the additionalflags string for the dynamic data
*/
std::string CtiCCSpecial::formatFlags() const
{
    std::string flags( Length_DynamicFlags, populateFlag( false ) );

    flags[ Index_OvUvDisabled ] = populateFlag( getOvUvDisabledFlag() );
    flags[ Index_AreaUpdated ]  = populateFlag( getAreaUpdatedFlag() );

    return flags;
}

bool CtiCCSpecial::updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "UPDATE "
            "DYNAMICCCSPECIALAREA "
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

bool CtiCCSpecial::insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "INSERT INTO "
            "DYNAMICCCSPECIALAREA "
        "VALUES "
            "(?, ?, ?)";

    CTILOG_INFO( dout, "Inserting special area into DYNAMICCCSPECIALAREA: " << getPaoName() );

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << getPaoId()
        << formatFlags()
        << getVoltReductionControlValue();

    return Cti::Database::executeCommand( writer, __FILE__, __LINE__, Cti::Database::LogDebug( _CC_DEBUG & CC_DEBUG_DATABASE ) );
}

void CtiCCSpecial::handleSpecializedPointData( const CtiPointDataMsg & message )
{
    const long   pointID = message.getId();
    const double value   = message.getValue();

    if ( pointID == getVoltReductionControlPointId() )
    {
        const bool reduceVoltage = value;

        if ( reduceVoltage != getVoltReductionControlValue()
             && ! getDisableFlag() )
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
        }
    }
}

