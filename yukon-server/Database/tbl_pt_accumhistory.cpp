#include "precompiled.h"
#include "tbl_pt_accumhistory.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

using std::string;

CtiTablePointAccumulatorHistory::CtiTablePointAccumulatorHistory(LONG pid,
                                                                 ULONG prevpulsecount,
                                                                 ULONG pulsecount) :
_pointID(pid),
_previousPulseCount(prevpulsecount),
_presentPulseCount(pulsecount)
{}

CtiTablePointAccumulatorHistory::CtiTablePointAccumulatorHistory(const CtiTablePointAccumulatorHistory& ref)
{
    *this = ref;
}

CtiTablePointAccumulatorHistory::~CtiTablePointAccumulatorHistory()
{
    if(isDirty())
    {
        Update();
    }
}

CtiTablePointAccumulatorHistory& CtiTablePointAccumulatorHistory::operator=(const CtiTablePointAccumulatorHistory& aRef)
{
    if(this != &aRef)
    {
        _pointID    = aRef.getPointID();
        _previousPulseCount = aRef.getPreviousPulseCount();
        _presentPulseCount = aRef.getPresentPulseCount();
    }
    return *this;
}

bool CtiTablePointAccumulatorHistory::operator==(const CtiTablePointAccumulatorHistory& right) const
{
    return( getPointID() == right.getPointID() );
}

string CtiTablePointAccumulatorHistory::getTableName() const
{
    return "DynamicAccumulator";
}

bool CtiTablePointAccumulatorHistory::Restore()
{
    static const string sql =  "SELECT DAC.pointid, DAC.previouspulses, DAC.presentpulses "
                               "FROM DynamicAccumulator DAC "
                               "WHERE DAC.pointid = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection, sql);

    reader << getPointID();

    reader.execute();

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty(FALSE);
        return true;
    }
    else
    {
        setDirty( TRUE );
        return false;
    }
}

bool CtiTablePointAccumulatorHistory::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "previouspulses = ?, "
                                        "presentpulses = ?"
                                   " where "
                                        "pointid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getPresentPulseCount()       // is this a bug? should it be: getPreviousPulseCount()?
        << getPresentPulseCount()
        << getPointID();

    bool success = executeUpdater(updater);

    if ( success )
    {
        setDirty(FALSE);
    }

    return success;
}

bool CtiTablePointAccumulatorHistory::Insert()
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getPointID()
        << getPreviousPulseCount()
        << getPresentPulseCount();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(FALSE);
    }

    return success;
}

void CtiTablePointAccumulatorHistory::DecodeDatabaseReader(Cti::RowReader& rdr )
{
    rdr >> _pointID >> _previousPulseCount >> _presentPulseCount;
}

ULONG CtiTablePointAccumulatorHistory::getPreviousPulseCount() const
{
    return _previousPulseCount;
}

ULONG CtiTablePointAccumulatorHistory::getPresentPulseCount() const
{
    return _presentPulseCount;
}

LONG CtiTablePointAccumulatorHistory::getPointID() const
{
    return _pointID;
}
CtiTablePointAccumulatorHistory& CtiTablePointAccumulatorHistory::setPointID(LONG pointID)
{
    _pointID = pointID;
    return *this;
}

CtiTablePointAccumulatorHistory& CtiTablePointAccumulatorHistory::setPreviousPulseCount(ULONG pc)
{
    _previousPulseCount = pc;
    setDirty(TRUE);
    return *this;
}

CtiTablePointAccumulatorHistory& CtiTablePointAccumulatorHistory::setPresentPulseCount(ULONG pc)
{
    _presentPulseCount = pc;
    setDirty(TRUE);
    return *this;
}
