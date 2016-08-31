//  Exclude database_bulk_writer.cpp from using precompiled.h for now so we can specify /await for just this file.
//
//  DatabaseBulkWriter needs /await to use Coroutines::chunked, but we can't enable /await at the ctibase project scope
//    due to an /await keyword conflict with log4cxx::Condition::await(), which is included by logManager.cpp.
//
//#include "precompiled.h"  

#include "database_bulk_writer.h"
#include "database_exceptions.h"
#include "database_reader.h"
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

std::string getTempTablePrefix(const DatabaseConnection::ClientType clientType)
{
    if( clientType == DatabaseConnection::ClientType::Oracle )    return "Temp_";
    if( clientType == DatabaseConnection::ClientType::SqlServer ) return "##";

    throw DatabaseException{ "Unknown client type " + std::to_string(static_cast<unsigned>(clientType)) };
}

}

template <size_t ColumnCount>
DatabaseBulkWriter<ColumnCount>::DatabaseBulkWriter(const DbClientType clientType, TempTableColumns tempTableSchema, const std::string &tempTableName, const std::string &destTableName) 
    :   _clientType(clientType),
        _schema(tempTableSchema),
        _tempTable(getTempTablePrefix(clientType) + tempTableName),
        _destTable(destTableName)
{}

template <size_t ColumnCount>
std::string DatabaseBulkWriter<ColumnCount>::getTempTableCreationSql() const
{
    switch( _clientType )
    {
        case DbClientType::Oracle:
        {
            return 
                "create global temporary table " + _tempTable + " ("
                + boost::join(_schema | oracleColumnSpecification, ",")
                + ") on commit preserve rows";
        }
        case DbClientType::SqlServer:
        {
            return 
                "create table " + _tempTable + " ("
                + boost::join(_schema | sqlServerColumnSpecification, ",")
                + ")";
        }
    }

    throw DatabaseException{ "Unknown client type " + std::to_string(static_cast<unsigned>(_clientType)) };
}

template <size_t ColumnCount>
std::string DatabaseBulkWriter<ColumnCount>::getTempTableTruncationSql() const
{
    switch( _clientType )
    {
        case DbClientType::Oracle:
            return "truncate table " + _tempTable;
        case DbClientType::SqlServer:
            return "truncate table " + _tempTable;
    }

    throw DatabaseException{ "Unknown client type " + std::to_string(static_cast<unsigned>(_clientType)) };
}

template <size_t ColumnCount>
std::string DatabaseBulkWriter<ColumnCount>::getInsertSql(size_t rows) const
{
    const std::string oraclePrefix = "INSERT ALL";
    const std::string oracleInfix  = " INTO " + _tempTable + " VALUES " + createPlaceholderList(ColumnCount);
    const std::string oracleSuffix = " SELECT 1 FROM DUAL";

    const std::string sqlPrefix = "INSERT INTO " + _tempTable + " VALUES ";
    const std::string sqlInfix = createPlaceholderList(ColumnCount);  //  joined by commas

    switch( _clientType )
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

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(_clientType)) };
}

template <size_t ColumnCount>
std::set<long> DatabaseBulkWriter<ColumnCount>::writeRows(DatabaseConnection& conn, const std::vector<const RowSource*>&& rows) const
{
    static const size_t ChunkSize = 999 / ColumnCount;  //  we only have 999 placeholders

    unsigned rowsWritten = 0;

    boost::optional<DatabaseTransaction> transaction;

    std::set<long> rejectedRows;

    CTILOG_INFO(dout, "Writing " << rows.size() << " records to " << _destTable);

    try
    {
        DatabaseWriter truncator{ conn, getTempTableTruncationSql() };

        try
        {
            truncator.executeWithDatabaseException();
        }
        catch( const DatabaseException & )
        {
            CTILOG_INFO(dout, "Temp table not detected, attempting to create");

            DatabaseWriter creator{ conn, getTempTableCreationSql() };

            executeWriter(creator, __FILE__, __LINE__, Cti::Database::LogDebug::Disable);
        }

        transaction.emplace(conn);

        unsigned logLimit = ChunkSize;

        for( auto chunk : Cti::Coroutines::chunked(rows, ChunkSize) )
        {
            DatabaseWriter inserter{ conn, getInsertSql(chunk.size()) };

            for( auto& record : chunk ) 
            {
                record->fillRowWriter(inserter);
            }

            executeWriter(inserter, __FILE__, __LINE__, Cti::Database::LogDebug::Disable);

            rowsWritten += chunk.size();

            if( rowsWritten > logLimit )
            {
                CTILOG_DEBUG(dout, "Wrote " << rowsWritten << " records to temp table for " << _destTable);

                logLimit *= 1.618;  //  Love me some Golden Ratio
            }
        }

        rejectedRows = validateTemporaryRows(conn);

        DatabaseWriter finalizer{ conn, getFinalizeSql() };

        CTILOG_INFO(dout, "Finalizing records for " << _destTable);

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

    return rejectedRows;
}

template<size_t ColumnCount>
std::set<long> DatabaseBulkWriter<ColumnCount>::validateTemporaryRows(DatabaseConnection & conn) const
{
    return std::set<long>();
}

template <size_t ColumnCount>
std::set<long> DatabaseBulkUpdater<ColumnCount>::validateTemporaryRows(DatabaseConnection& conn) const
{
    long pointID;
    std::set<long> rejectedRows;

    DatabaseReader validateSelecter{ conn, getRejectedRowsSql() };
    validateSelecter.execute();

    while (validateSelecter())
    {
        validateSelecter >> pointID;
        rejectedRows.insert(pointID);
    }

    if ( ! rejectedRows.empty() )
    {
        DatabaseWriter validateDeleter{ conn, getDeleteRejectedRowsSql(rejectedRows) };
        validateDeleter.execute();
    }

    return rejectedRows;
}

template <size_t ColumnCount>
DatabaseBulkInserter<ColumnCount>::DatabaseBulkInserter(const DbClientType clientType, TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& destIdColumn ) 
    :   DatabaseBulkWriter{ clientType, schema, tempTableName, destTableName },
        _idColumn{ destIdColumn }
{}

template <size_t ColumnCount>
std::string DatabaseBulkInserter<ColumnCount>::getFinalizeSql() const
{
    const auto columnNames = boost::join(_schema | columnName, ", ");

    switch( _clientType )
    {
        case DbClientType::SqlServer:
        {
            return
                "declare @maxId numeric;"
                "select @maxId = COALESCE(MAX(" + _idColumn + "), 0) from " + _destTable + ";"
                "insert into " + _destTable + " (" + _idColumn + ", " + columnNames + ")" +
                " select @maxId + ROW_NUMBER() OVER (ORDER BY (SELECT 1)), " + columnNames +
                " FROM " + _tempTable + ";";
        }
        case DbClientType::Oracle:
        {
            return
                "DECLARE maxId number;"
                "BEGIN"
                " SELECT NVL(MAX(" + _idColumn + "), 0) INTO maxId"
                " FROM " + _destTable + ";"
                "INSERT INTO " + _destTable + " (" + _idColumn + ", " + columnNames + ")" +
                " SELECT maxId + ROWNUM, " + columnNames +
                " FROM " + _tempTable + "; "
                "END;";
        }
    }

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(_clientType)) };
}


template <size_t ColumnCount>
DatabaseBulkUpdater<ColumnCount>::DatabaseBulkUpdater(const DbClientType clientType, TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& foreignKeyTableName) 
    :   DatabaseBulkWriter{ clientType, schema, tempTableName, destTableName },
        _idColumn{ _schema[0].name },
        _valueColumns{ _schema.begin() + 1, _schema.end() },
        _fkTable{ foreignKeyTableName }
{}


template <size_t ColumnCount>
std::string DatabaseBulkUpdater<ColumnCount>::getFinalizeSql() const
{
    const auto columnNames = boost::join(_schema | columnName, ", ");

    switch( _clientType )
    {
        case DbClientType::SqlServer:
        {
            const auto mergeUpdate =
                boost::adaptors::transformed(
                    [this](const ColumnDefinition &cd) {
                        return cd.name + " = " + _tempTable + "." + cd.name; });

            const auto mergeInsert =
                boost::adaptors::transformed(
                    [this](const ColumnDefinition &cd) {
                        return "" + _tempTable + "." + cd.name; });

            const auto mergeUpdates = boost::join(_valueColumns | mergeUpdate, ", ");
            const auto mergeInserts = boost::join(_schema | mergeInsert, ", ");

            return
                "MERGE " + _destTable +
                " USING " + _tempTable +
                " ON " + _destTable + "." + _idColumn + " = " + _tempTable + "." + _idColumn +
                " WHEN MATCHED THEN"
                " UPDATE SET " + mergeUpdates +
                " WHEN NOT MATCHED THEN"
                " INSERT (" + columnNames + ")"
                " VALUES (" + mergeInserts + ");";
        }
        case DbClientType::Oracle:
        {
            const auto mergeUpdate =
                boost::adaptors::transformed(
                    [this](const ColumnDefinition &cd) {
                        return cd.name + " = " + _tempTable + "." + cd.name; });

            const auto mergeInsert =
                boost::adaptors::transformed(
                    [this](const ColumnDefinition &cd) {
                        return "" + _tempTable + "." + cd.name; });

            const auto mergeUpdates = boost::join(_valueColumns | mergeUpdate, ", ");
            const auto mergeInserts = boost::join(_schema | mergeInsert, ", ");

            return
                "BEGIN"
                " MERGE INTO " + _destTable +
                " USING " + _tempTable +
                " ON (" + _destTable + "." + _idColumn + " = " + _tempTable + "." + _idColumn + ")"
                " WHEN MATCHED THEN"
                " UPDATE SET " + mergeUpdates +
                " WHEN NOT MATCHED THEN"
                " INSERT (" + columnNames + ")"
                " VALUES (" + mergeInserts + ")"
                " END;";
        }
    }

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(_clientType)) };
}

template <size_t ColumnCount>
std::string DatabaseBulkUpdater<ColumnCount>::getRejectedRowsSql() const
{
    return
        "SELECT " + _tempTable + "." + _idColumn +
        " FROM " + _tempTable +
        " LEFT JOIN " + _fkTable +
        " ON " + _tempTable + "." + _idColumn + "=" + _fkTable + "." + _idColumn +
        " WHERE " + _fkTable + "." + _idColumn + " IS NULL";
}

template <size_t ColumnCount>
std::string DatabaseBulkUpdater<ColumnCount>::getDeleteRejectedRowsSql(std::set<long> rejectedRowSet) const
{
    std::string rejectedRowString = Cti::join(rejectedRowSet, ", ");

    return
        "DELETE FROM " + _tempTable + 
        " WHERE " + _idColumn + " IN (" + rejectedRowString + ");";
}

template DatabaseBulkInserter<5>;
template DatabaseBulkUpdater<7>;
template DatabaseBulkUpdater<5>;


}
}