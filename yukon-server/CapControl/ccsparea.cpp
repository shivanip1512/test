#include "precompiled.h"

#include "ccsparea.h"
#include "ccid.h"
#include "database_util.h"
#include "ccsubstationbusstore.h"

using std::endl;
using std::string;

using Cti::CapControl::serializeFlag;

extern unsigned long _CC_DEBUG;

DEFINE_COLLECTABLE( CtiCCSpecial, CTICCSPECIALAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSpecial::CtiCCSpecial()
    : CtiCCAreaBase(0)
{
}

CtiCCSpecial::CtiCCSpecial(StrategyManager * strategyManager)
    : CtiCCAreaBase(strategyManager)
{
}

CtiCCSpecial::CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager)
    : CtiCCAreaBase(rdr, strategyManager)
{
    restoreStaticData(rdr);

    if ( hasDynamicData( rdr["additionalflags"] ) )
    {
        restoreDynamicData( rdr );
    }
}

CtiCCSpecial::CtiCCSpecial(const CtiCCSpecial& special)
    : CtiCCAreaBase(special)
{
    operator=(special);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSpecial::~CtiCCSpecial()
{

}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::operator=(const CtiCCSpecial& right)
{
    CtiCCAreaBase::operator=(right);

    if( this != &right )
    {
    }
    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSpecial* CtiCCSpecial::replicate() const
{
    return(new CtiCCSpecial(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCSpecial::restoreStaticData(Cti::RowReader& rdr)
{
    // nothing to restore at this level -- see base class
}

void CtiCCSpecial::restoreDynamicData(Cti::RowReader& rdr)
{
    // nothing to restore at this level -- see base class
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSpecial::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    writeDynamicData( conn,currentDateTime );

    getOperationStats().dumpDynamicData(conn, currentDateTime);
}

std::string CtiCCSpecial::formatFlags() const
{
    std::string flags( 20, 'N' );

    flags[ 0 ] = serializeFlag( getOvUvDisabledFlag() );
    flags[ 3 ] = serializeFlag( getAreaUpdatedFlag() );

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

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << getPaoId()
        << formatFlags()
        << getVoltReductionControlValue();

    return Cti::Database::executeCommand( writer, __FILE__, __LINE__, Cti::Database::LogDebug( _CC_DEBUG & CC_DEBUG_DATABASE ) );
}

