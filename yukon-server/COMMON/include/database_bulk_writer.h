#pragma once

#include "database_connection.h"

namespace Cti {
namespace Database {

struct ColumnDefinition
{
    std::string name;
    std::string sqlServerType;
    std::string oracleType;
};
    
template <size_t ColumnCount>
class DatabaseBulkWriter
{
public:

    using TempTableColumns = std::array<ColumnDefinition, ColumnCount>;
    using DbClientType = DatabaseConnection::ClientType;

    /*template <class T>
    void writeRows(std::vector<std::unique_ptr<T>>&& rows) const;*/

protected:

    DatabaseBulkWriter(TempTableColumns tempTableSchema, const std::string &tempTableName, const std::string &destTableName);

    std::string getTempTableCreationSql(const DbClientType clientType) const;
    std::string getTempTableTruncationSql(const DbClientType clientType) const;
    std::string getInsertSql(const DbClientType clientType, size_t rows) const;

    virtual std::string getFinalizeSql(const DbClientType clientType) const = 0;
    
    const TempTableColumns _schema;
    const std::string _tempTable, _destTable;
};

template <size_t ColumnCount>
class IM_EX_CTIBASE DatabaseBulkInserter : DatabaseBulkWriter<ColumnCount>
{
public:
    DatabaseBulkInserter(TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& destIdColumn );

	std::string getFinalizeSql(const DbClientType clientType) const override;

private:

    std::string _idColumn;
};

template <size_t ColumnCount>
class IM_EX_CTIBASE DatabaseBulkUpdater : DatabaseBulkWriter<ColumnCount>
{
public:
    DatabaseBulkUpdater(TempTableColumns schema, const std::string& tempTableName, const std::string& destTableName, const std::string& destIdColumn );

    std::string getFinalizeSql(const DbClientType clientType) const override;

private:

    std::string _idColumn;
};

}
}