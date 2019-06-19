#pragma once

#include "database_connection.h"
#include "database_util.h"

#include <boost/range/sub_range.hpp>

namespace Cti {
namespace Database {

template <size_t ColumnCount>
class DatabaseBulkWriter
{
public:
    using TempTableColumns = std::array<ColumnDefinition, ColumnCount>;
    using DbClientType = DatabaseConnection::ClientType;

    std::set<long> writeRows(DatabaseConnection& conn, const std::vector<const RowSource*>&& rows) const;

protected:
    DatabaseBulkWriter(const DbClientType clientType, TempTableColumns tempTableSchema, const std::string &tempTableName, const std::string &destTableName);

    std::string getTempTableCreationSql() const;
    std::string getTempTableTruncationSql() const;
    std::string getInsertSql(size_t rows) const;

    virtual std::string getFinalizeSql() const = 0;
    virtual std::set<long> getRejectedRows(DatabaseConnection& conn) const;
    
    const DbClientType _clientType;
    const TempTableColumns _schema;
    const std::string _tempTable, _destTable;
};

template <size_t ColumnCount>
class IM_EX_CTIBASE DatabaseBulkInserter : public DatabaseBulkWriter<ColumnCount>
{
public:
    DatabaseBulkInserter(const DbClientType clientType, TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& destIdColumn);

protected:
    std::string getFinalizeSql() const override;

private:
    std::string _idColumn;
};

template <size_t ColumnCount>
class IM_EX_CTIBASE DatabaseBulkUpdater : public DatabaseBulkWriter<ColumnCount>
{
public:
    DatabaseBulkUpdater(const DbClientType clientType, TempTableColumns schema, const unsigned primaryKeyCount, const std::string& tempTableName, const std::string& destTableName, const std::string& foreignKeyTableName);
    DatabaseBulkUpdater(const DbClientType clientType, TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& foreignKeyTableName);

protected:
    std::string getFinalizeSql() const override;
    std::string getRejectedRowsSql() const;

    std::set<long> getRejectedRows(DatabaseConnection& conn) const override;

private:
    const std::string _idColumn;
    const boost::sub_range<const std::array<ColumnDefinition, ColumnCount>> _primaryKeyColumns;
    const boost::sub_range<const std::array<ColumnDefinition, ColumnCount>> _valueColumns;
    const std::string _fkTable;
};

template <size_t ColumnCount>
class IM_EX_CTIBASE DatabaseBulkAccumulator : public DatabaseBulkWriter<ColumnCount>
{
public:
    DatabaseBulkAccumulator(const DbClientType clientType, TempTableColumns schema, const unsigned primaryKeyCount, const std::string& tempTableName, const std::string& destTableName, const std::string& destIdColumn, const std::string& foreignKeyTableName);

protected:
    std::string getFinalizeSql() const override;
    std::string getRejectedRowsSql() const;

    std::set<long> getRejectedRows(DatabaseConnection& conn) const override;

private:
    const std::string _idColumn;
    const boost::sub_range<const std::array<ColumnDefinition, ColumnCount>> _primaryKeyColumns;
    const boost::sub_range<const std::array<ColumnDefinition, ColumnCount>> _valueColumns;
    const std::string _fkTable;
    const std::string _destIdColumn;
};

}
}