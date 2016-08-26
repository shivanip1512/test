#include "precompiled.h"

#include "database_bulk_writer.h"

#include <boost/range/algorithm/transform.hpp>
#include <boost/range/adaptor/transformed.hpp>

namespace Cti {
namespace Database {

extern std::string createPlaceholderList(const size_t count);

namespace {
    
static const auto columnName = 
    boost::adaptors::transformed(
        [](ColumnDefinition &cd){ 
            return cd.name; });
    
static const auto sqlServerColumnSpecification = 
    boost::adaptors::transformed(
        [](ColumnDefinition &cd){ 
            return cd.name + " " + cd.sqlServerType + " not null"; });

static const auto oracleColumnSpecification = 
    boost::adaptors::transformed(
        [](ColumnDefinition &cd){ 
            return cd.name + " " + cd.oracleType + " not null"; });
}

template <size_t ColumnCount>
DatabaseBulkWriter<ColumnCount>::DatabaseBulkWriter(TempTableColumns tempTableSchema, const std::string &tempTableName, const std::string &destTableName) 
    :   _schema(tempTableSchema),
        _tempTable(tempTableName),
        _destTable(destTableName)
{}

template <size_t ColumnCount>
std::string DatabaseBulkWriter<ColumnCount>::getTempTableCreationSql(const DbClientType clientType) const
{
    switch( clientType )
    {
        case DbClientType::Oracle:
        {
            return 
                "create global temporary table Temp_" + _tempTable + " ("
                + boost::join(_schema | oracleColumnSpecification, ",")
                + ") on commit preserve rows";
        }
        case DbClientType::SqlServer:
        {
            return 
                "create table ##" + _tempTable + " ("
                + boost::join(_schema | sqlServerColumnSpecification, ",")
                + ")";
        }
    }

    throw DatabaseException{ "Unknown client type " + std::to_string(static_cast<unsigned>(clientType)) };
}

template <size_t ColumnCount>
std::string DatabaseBulkWriter<ColumnCount>::getTempTableTruncationSql(const DbClientType clientType) const
{
    switch( clientType )
    {
        case DbClientType::Oracle:
            return "truncate table Temp_" + _tempTable;
        case DbClientType::SqlServer:
            return "truncate table ##" + _tempTable;
    }

    throw DatabaseException{ "Unknown client type " + std::to_string(static_cast<unsigned>(clientType)) };
}

template <size_t ColumnCount>
std::string DatabaseBulkWriter<ColumnCount>::getInsertSql(const DbClientType clientType, size_t rows) const
{
    const std::string oraclePrefix = "INSERT ALL";
    const std::string oracleInfix  = " INTO Temp_" + _tempTable + " VALUES(" + createPlaceholderList(ColumnCount) + ")";
    const std::string oracleSuffix = " SELECT 1 FROM DUAL";

    const std::string sqlPrefix = "INSERT INTO ##" + _tempTable + " VALUES";
    const std::string sqlInfix = " (" + createPlaceholderList(ColumnCount) + ")";  //  joined by commas

    switch( clientType )
    {
        case DbClientType::SqlServer:
            return sqlPrefix 
                + boost::algorithm::join(std::vector<std::string> { rows, sqlInfix }, ",");

        case DbClientType::Oracle:
        {
            auto sql = oraclePrefix;
            sql.reserve(sql.size() + oracleInfix.size() * rows + oracleSuffix.size());

            while( rows-- )
            {
                sql += oracleInfix;
            }

            return sql += oracleSuffix;
        }
    }

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(clientType)) };
}

/*
template <size_t ColumnCount, class T>
void DatabaseBulkWriter<ColumnCount>::writeRows(std::vector<std::unique_ptr<T>>&& rows) const
{
    static const size_t ChunkSize = 1000 / ColumnCount;

    unsigned rowsWritten = 0;

    boost::optional<DatabaseTransaction> transaction;

    try
    {
        DatabaseWriter truncator{ conn, CtiTableRawPointHistory::getTempTableTruncationSql(conn.getClientType()) };

        try
        {
            truncator.executeWithDatabaseException();
        }
        catch( const DatabaseException &ex )
        {
            CTILOG_INFO(dout, "Temp table not detected, attempting to create");

            DatabaseWriter creator{ conn, CtiTableRawPointHistory::getTempTableCreationSql(conn.getClientType()) };

            executeWriter(creator, __FILE__, __LINE__, Cti::Database::LogDebug::Disable);
        }

        transaction.emplace(conn);

        for( auto chunk : Cti::Coroutines::chunked(rows, ChunkSize) )
        {
            DatabaseWriter inserter{ conn, CtiTableRawPointHistory::getInsertSql(conn.getClientType(), chunk.size()) };

            for( auto& record : chunk ) 
            {
                record->fillInserter(inserter);
            }

            executeWriter(inserter, __FILE__, __LINE__, Cti::Database::LogDebug::Disable);

            rowsWritten += chunk.size();
        }

        DatabaseWriter finalizer{ conn, CtiTableRawPointHistory::getFinalizeSql(conn.getClientType()) };

        finalizer.executeWithDatabaseException();
    }
    catch( DatabaseException & ex )
    {
        if( transaction )
        {
            transaction->rollback();
        }

        CTILOG_EXCEPTION_ERROR(dout, ex, "Unable to insert rows into " << _destTable << ":\n" <<
            boost::join(rowsToWrite |
                boost::adaptors::indirected |
                boost::adaptors::transformed(
                    [](const Cti::Loggable &obj) {
            return obj.toString(); }), "\n"));

        rowsWritten = 0;
    }
}
*/

template <size_t ColumnCount>
DatabaseBulkInserter<ColumnCount>::DatabaseBulkInserter(TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& destIdColumn ) 
    :   DatabaseBulkWriter{ schema, tempTableName, destTableName },
        _idColumn{ destIdColumn }
{}

template <size_t ColumnCount>
std::string DatabaseBulkInserter<ColumnCount>::getFinalizeSql(const DbClientType clientType) const
{
    switch( clientType )
    {
        case DbClientType::SqlServer:
        {
            return
                "declare @maxId numeric;"
                "select @maxId = COALESCE(MAX(" + _idColumn + "), 0) from " + _destTable + ";"
                "insert into RAWPOINTHISTORY (" + _idColumn + ", " + Cti::join(_schema | columnName) + ")" +
                " select @maxId + ROW_NUMBER() OVER (ORDER BY (SELECT 1)), " + Cti::join(_schema | columnName) +
                " FROM ##" + _tempTable + ";";
        }
        case DbClientType::Oracle:
        {
            return
                "DECLARE maxId number;"
                "BEGIN"
                " SELECT NVL(MAX(" + _idColumn + "), 0) INTO maxId"
                " FROM RAWPOINTHISTORY;"
                "INSERT INTO RAWPOINTHISTORY (" + _idColumn + ", " + Cti::join(_schema | columnName) + ")" +
                " SELECT maxId + ROWNUM, " + Cti::join(_schema | columnName) +
                " FROM Temp_" + _tempTable + "; "
                "END;";
        }
    }

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(clientType)) };
}


template <size_t ColumnCount>
DatabaseBulkUpdater<ColumnCount>::DatabaseBulkUpdater(TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& destIdColumn ) 
    :   DatabaseBulkWriter{ schema, tempTableName, destTableName },
        _idColumn{ destIdColumn }
{}


template <size_t ColumnCount>
std::string DatabaseBulkUpdater<ColumnCount>::getFinalizeSql(const DbClientType clientType) const
{

    const auto columnNames  = Cti::join(_schema | columnName, ",");

    switch( clientType )
    {
        case DbClientType::SqlServer:
        {
            const auto mergeUpdate =
                boost::adaptors::transformed(
                    [this](const ColumnDefinition &cd) {
                return cd.name + " = ##" + _tempTable + "." + cd.name; });

            const auto mergeInsert =
                boost::adaptors::transformed(
                    [this](const ColumnDefinition &cd) {
                return "##" + _tempTable + "." + cd.name; });

            const auto mergeUpdates = Cti::join(_schema | mergeUpdate, ",");
            const auto mergeInserts = Cti::join(_schema | mergeInsert, ",");

            return
                "MERGE " + _destTable +
                "USING ##" + _tempTable +
                "ON " + _destTable + "." + _idColumn + " = ##" + _tempTable + "." + _idColumn +
                "WHEN MATCHED THEN"
                " UPDATE SET " + mergeUpdates +
                "WHEN NOT MATCHED THEN"
                " INSERT (" + _idColumn + "," + columnNames + ")" +
                " VALUES (##" + _tempTable + "." + _idColumn + "," + mergeInserts + ");";
        }
        case DbClientType::Oracle:
        {
            const auto mergeUpdate =
                boost::adaptors::transformed(
                    [this](const ColumnDefinition &cd) {
                return cd.name + " = Temp_" + _tempTable + "." + cd.name; });

            const auto mergeInsert =
                boost::adaptors::transformed(
                    [this](const ColumnDefinition &cd) {
                return "Temp_" + _tempTable + "." + cd.name; });

            const auto mergeUpdates = Cti::join(_schema | mergeUpdate, ",");
            const auto mergeInserts = Cti::join(_schema | mergeInsert, ",");

            return
                "BEGIN"
                "MERGE INTO " + _destTable +
                "USING Temp_" + _tempTable +
                "ON (" + _destTable + "." + _idColumn + " = Temp_" + _tempTable + "." + _idColumn + ")"
                "WHEN MATCHED THEN"
                " UPDATE SET " + mergeUpdates +
                "WHEN NOT MATCHED THEN"
                " INSERT (" + _idColumn + "," + columnNames + ")" +
                " VALUES (Temp_" + _tempTable + "." + _idColumn + "," + mergeInserts + ");";
                "END;";
        }
    }

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(clientType)) };
}


}
}