//  Exclude database_bulk_writer.cpp from using precompiled.h for now so we can specify /await for just this file.
//
//  DatabaseBulkWriter needs /await to use Coroutines::chunked, but we can't enable /await at the ctibase project scope
//    due to an /await keyword conflict with log4cxx::Condition::await(), which is included by logManager.cpp.
//
//#include "precompiled.h"  

#include "database_bulk_writer.h"
#include "database_exceptions.h"
#include "database_transaction.h"

#include "coroutine_util.h"

#include <boost/range/algorithm/transform.hpp>
#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/adaptor/indirected.hpp>

namespace Cti {
namespace Database {

extern std::string createPlaceholderList(const size_t count);

namespace {
    
static const auto columnName = 
    boost::adaptors::transformed(
        [](const ColumnDefinition &cd){ 
            return cd.name; });
    
static const auto sqlServerColumnSpecification = 
    boost::adaptors::transformed(
        [](const ColumnDefinition &cd){ 
            return cd.name + " " + cd.sqlServerType + " not null"; });

static const auto oracleColumnSpecification = 
    boost::adaptors::transformed(
        [](const ColumnDefinition &cd){ 
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
    const std::string oracleInfix  = " INTO Temp_" + _tempTable + " VALUES " + createPlaceholderList(ColumnCount);
    const std::string oracleSuffix = " SELECT 1 FROM DUAL";

    const std::string sqlPrefix = "INSERT INTO ##" + _tempTable + " VALUES ";
    const std::string sqlInfix = createPlaceholderList(ColumnCount);  //  joined by commas

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


template <size_t ColumnCount>
void DatabaseBulkWriter<ColumnCount>::writeRows(DatabaseConnection& conn, std::vector<std::unique_ptr<RowSource>>&& rows) const
{
    static const size_t ChunkSize = 1000 / ColumnCount;

    unsigned rowsWritten = 0;

    boost::optional<DatabaseTransaction> transaction;

    try
    {
        DatabaseWriter truncator{ conn, getTempTableTruncationSql(conn.getClientType()) };

        try
        {
            truncator.executeWithDatabaseException();
        }
        catch( const DatabaseException & )
        {
            CTILOG_INFO(dout, "Temp table not detected, attempting to create");

            DatabaseWriter creator{ conn, getTempTableCreationSql(conn.getClientType()) };

            executeWriter(creator, __FILE__, __LINE__, Cti::Database::LogDebug::Disable);
        }

        transaction.emplace(conn);

        for( auto chunk : Cti::Coroutines::chunked(rows, ChunkSize) )
        {
            DatabaseWriter inserter{ conn, getInsertSql(conn.getClientType(), chunk.size()) };

            for( auto& record : chunk ) 
            {
                record->fillRowWriter(inserter);
            }

            executeWriter(inserter, __FILE__, __LINE__, Cti::Database::LogDebug::Disable);

            rowsWritten += chunk.size();
        }

        DatabaseWriter finalizer{ conn, getFinalizeSql(conn.getClientType()) };

        finalizer.executeWithDatabaseException();
    }
    catch( DatabaseException & ex )
    {
        if( transaction )
        {
            transaction->rollback();
        }

        CTILOG_EXCEPTION_ERROR(dout, ex, "Unable to insert rows into " << _destTable << ":\n" <<
            boost::join(rows |
                boost::adaptors::indirected |
                boost::adaptors::transformed(
                    [](const Cti::Loggable &obj) {
            return obj.toString(); }), "\n"));

        rowsWritten = 0;
    }
}


template <size_t ColumnCount>
DatabaseBulkInserter<ColumnCount>::DatabaseBulkInserter(TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& destIdColumn ) 
    :   DatabaseBulkWriter{ schema, tempTableName, destTableName },
        _idColumn{ destIdColumn }
{}

template <size_t ColumnCount>
std::string DatabaseBulkInserter<ColumnCount>::getFinalizeSql(const DbClientType clientType) const
{
    const auto columnNames = boost::join(_schema | columnName, ", ");

    switch( clientType )
    {
        case DbClientType::SqlServer:
        {
            return
                "declare @maxId numeric;"
                "select @maxId = COALESCE(MAX(" + _idColumn + "), 0) from " + _destTable + ";"
                "insert into RAWPOINTHISTORY (" + _idColumn + ", " + columnNames + ")" +
                " select @maxId + ROW_NUMBER() OVER (ORDER BY (SELECT 1)), " + columnNames +
                " FROM ##" + _tempTable + ";";
        }
        case DbClientType::Oracle:
        {
            return
                "DECLARE maxId number;"
                "BEGIN"
                " SELECT NVL(MAX(" + _idColumn + "), 0) INTO maxId"
                " FROM RAWPOINTHISTORY;"
                "INSERT INTO RAWPOINTHISTORY (" + _idColumn + ", " + columnNames + ")" +
                " SELECT maxId + ROWNUM, " + columnNames +
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
    const auto columnNames = boost::join(_schema | columnName, ", ");

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

            const auto mergeUpdates = boost::join(_schema | mergeUpdate, ", ");
            const auto mergeInserts = boost::join(_schema | mergeInsert, ", ");

            return
                "MERGE " + _destTable +
                "USING ##" + _tempTable +
                "ON " + _destTable + "." + _idColumn + " = ##" + _tempTable + "." + _idColumn +
                "WHEN MATCHED THEN"
                " UPDATE SET " + mergeUpdates +
                "WHEN NOT MATCHED THEN"
                " INSERT (" + _idColumn + ", " + columnNames + ")"
                " VALUES (##" + _tempTable + "." + _idColumn + ", " + mergeInserts + ");";
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

            const auto mergeUpdates = boost::join(_schema | mergeUpdate, ", ");
            const auto mergeInserts = boost::join(_schema | mergeInsert, ", ");

            return
                "BEGIN"
                "MERGE INTO " + _destTable +
                "USING Temp_" + _tempTable +
                "ON (" + _destTable + "." + _idColumn + " = Temp_" + _tempTable + "." + _idColumn + ")"
                "WHEN MATCHED THEN"
                " UPDATE SET " + mergeUpdates +
                "WHEN NOT MATCHED THEN"
                " INSERT (" + _idColumn + ", " + columnNames + ")"
                " VALUES (Temp_" + _tempTable + "." + _idColumn + ", " + mergeInserts + ")"
                "END;";
        }
    }

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(clientType)) };
}


template DatabaseBulkInserter<5>;
template DatabaseBulkUpdater<9>;
template DatabaseBulkUpdater<5>;


}
}