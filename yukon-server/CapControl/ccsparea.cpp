#include "precompiled.h"

#include "ccsparea.h"
#include "ccid.h"
#include "database_util.h"
#include "ccsubstationbusstore.h"

using std::endl;
using std::string;

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
    restore(rdr);

    if ( !rdr["additionalflags"].isNull() )
    {
        setDynamicData( rdr );
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
void CtiCCSpecial::restore(Cti::RowReader& rdr)
{
    setDirty(true);
    _insertDynamicDataFlag = true;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSpecial::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( isDirty() )
    {
        if( !_insertDynamicDataFlag )
        {
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (getOvUvDisabledFlag()?'Y':'N');
            string additionalFlags = string(char2string(*addFlags));
            additionalFlags.append("NNNNNNNNNNNNNNNNNNN");

            static const string updaterSql = "update dynamicccspecialarea set additionalflags = ?, controlvalue = ? "
                                             " where areaid = ?";
            Cti::Database::DatabaseWriter updater(conn, updaterSql);

            updater << additionalFlags << getVoltReductionControlValue() << getPaoId();

            if( Cti::Database::executeCommand( updater, __FILE__, __LINE__ ))
            {
                setDirty(false); // No error occured!
            }
        }
        else
        {
            CTILOG_INFO(dout, "Inserted area into dynamicCCSpecialArea: " << getPaoName());
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            static const string inserterSql = "insert into dynamicccspecialarea values (?, ?, ?)";
            Cti::Database::DatabaseWriter inserter(conn, inserterSql);

            inserter << getPaoId() << addFlags << getVoltReductionControlValue();

            if( Cti::Database::executeCommand( inserter, __FILE__, __LINE__, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
            {
                _insertDynamicDataFlag = false;
                setDirty(false); // No error occured!
            }
        }
    }

    getOperationStats().dumpDynamicData(conn, currentDateTime);
}

void CtiCCSpecial::setDynamicData(Cti::RowReader& rdr)
{
    _insertDynamicDataFlag = false;
    setDirty(false);
}
