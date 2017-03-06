#include <boost/test/unit_test.hpp>

#include "database_bulk_writer.h"

BOOST_AUTO_TEST_SUITE( test_database_bulk_writer )

struct test_BulkWriter : Cti::Database::DatabaseBulkWriter<5>
{
    test_BulkWriter(DbClientType clientType) 
        : DatabaseBulkWriter( clientType, 
            { Cti::Database::ColumnDefinition
                { "ColumnA", "SqlServerTypeA", "OracleTypeA" },
                { "ColumnB", "SqlServerTypeB", "OracleTypeB" },
                { "ColumnC", "SqlServerTypeC", "OracleTypeC" },
                { "ColumnD", "SqlServerTypeD", "OracleTypeD" },
                { "ColumnE", "SqlServerTypeE", "OracleTypeE" }},
            "TemporaryTableName", "DestinationTableName")
    {}

    using DatabaseBulkWriter::getTempTableCreationSql;
    using DatabaseBulkWriter::getTempTableTruncationSql;
    using DatabaseBulkWriter::getInsertSql;

    std::string getFinalizeSql() const override
    {
        return "Unit Test Finalize Sql Placeholder";
    }
};

BOOST_AUTO_TEST_CASE(test_temp_table_creation_sql)
{
    {
        test_BulkWriter bw { test_BulkWriter::DbClientType::SqlServer };

        BOOST_CHECK_EQUAL(
            bw.getTempTableCreationSql(),
            "create table Temp_TemporaryTableName ("
            "ColumnA SqlServerTypeA not null,"
            "ColumnB SqlServerTypeB not null,"
            "ColumnC SqlServerTypeC not null,"
            "ColumnD SqlServerTypeD not null,"
            "ColumnE SqlServerTypeE not null"
            ")");
    }

    {
        test_BulkWriter bw(test_BulkWriter::DbClientType::Oracle);

        BOOST_CHECK_EQUAL(
            bw.getTempTableCreationSql(),
            "create global temporary table Temp_TemporaryTableName ("
                "ColumnA OracleTypeA not null,"
                "ColumnB OracleTypeB not null,"
                "ColumnC OracleTypeC not null,"
                "ColumnD OracleTypeD not null,"
                "ColumnE OracleTypeE not null"
            ") on commit preserve rows");
    }
}

BOOST_AUTO_TEST_CASE(test_temp_table_truncation_sql)
{
    {
        test_BulkWriter bw { test_BulkWriter::DbClientType::SqlServer };

        BOOST_CHECK_EQUAL(
            bw.getTempTableTruncationSql(),
            "truncate table Temp_TemporaryTableName");
    }

    {
        test_BulkWriter bw { test_BulkWriter::DbClientType::Oracle };
        
        BOOST_CHECK_EQUAL(
            bw.getTempTableTruncationSql(),
            "truncate table Temp_TemporaryTableName");
    }
}        

BOOST_AUTO_TEST_CASE(test_temp_table_insert_sql)
{
    {
        test_BulkWriter bw { test_BulkWriter::DbClientType::SqlServer };

        BOOST_CHECK_EQUAL(
            bw.getInsertSql(7),
            "INSERT INTO Temp_TemporaryTableName VALUES "
            "(?,?,?,?,?),(?,?,?,?,?),(?,?,?,?,?),(?,?,?,?,?),"
            "(?,?,?,?,?),(?,?,?,?,?),(?,?,?,?,?)");
    }

    {
        test_BulkWriter bw { test_BulkWriter::DbClientType::Oracle };

        BOOST_CHECK_EQUAL(
            bw.getInsertSql(7),
            "INSERT ALL"
                " INTO Temp_TemporaryTableName VALUES (?,?,?,?,?) INTO Temp_TemporaryTableName VALUES (?,?,?,?,?)"
                " INTO Temp_TemporaryTableName VALUES (?,?,?,?,?) INTO Temp_TemporaryTableName VALUES (?,?,?,?,?)"
                " INTO Temp_TemporaryTableName VALUES (?,?,?,?,?) INTO Temp_TemporaryTableName VALUES (?,?,?,?,?)"
                " INTO Temp_TemporaryTableName VALUES (?,?,?,?,?)"
            " SELECT 1 FROM DUAL");
    }
}        

struct test_BulkInserter : Cti::Database::DatabaseBulkInserter<5>
{
    test_BulkInserter(DbClientType clientType) 
        :    DatabaseBulkInserter( clientType,
                { Cti::Database::ColumnDefinition
                    { "ColumnA", "SqlServerTypeA", "OracleTypeA" },
                    { "ColumnB", "SqlServerTypeB", "OracleTypeB" },
                    { "ColumnC", "SqlServerTypeC", "OracleTypeC" },
                    { "ColumnD", "SqlServerTypeD", "OracleTypeD" },
                    { "ColumnE", "SqlServerTypeE", "OracleTypeE" }},
                "TemporaryTableName", "DestinationTableName", "DestinationIdColumn")
    {}

    using DatabaseBulkInserter::getFinalizeSql;
};

BOOST_AUTO_TEST_CASE(test_bulk_inserter_finalize_sql)
{
    {
        test_BulkInserter bi { test_BulkInserter::DbClientType::SqlServer };

        BOOST_CHECK_EQUAL(
            bi.getFinalizeSql(),
            "declare @maxId numeric;"
            "select @maxId = COALESCE(MAX(DestinationIdColumn), 0) from DestinationTableName;"
            "insert into"
            " DestinationTableName (DestinationIdColumn, ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
            " select @maxId + ROW_NUMBER() OVER (ORDER BY (SELECT 1)), ColumnA, ColumnB, ColumnC, ColumnD, ColumnE FROM Temp_TemporaryTableName");
    }

    {
        test_BulkInserter bi { test_BulkInserter::DbClientType::Oracle };

        BOOST_CHECK_EQUAL(
            bi.getFinalizeSql(),
            "DECLARE maxId number;"
            "BEGIN"
                " SELECT NVL(MAX(DestinationIdColumn), 0) INTO maxId FROM DestinationTableName;"
                "INSERT INTO"
                    " DestinationTableName (DestinationIdColumn, ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
                    " SELECT maxId + ROWNUM, ColumnA, ColumnB, ColumnC, ColumnD, ColumnE FROM Temp_TemporaryTableName;"
            " END;");
    }
}        

struct test_BulkUpdater : Cti::Database::DatabaseBulkUpdater<5>
{
    test_BulkUpdater(DbClientType clientType)
        : DatabaseBulkUpdater( clientType,
            { Cti::Database::ColumnDefinition
                { "ColumnA", "SqlServerTypeA", "OracleTypeA" },
                { "ColumnB", "SqlServerTypeB", "OracleTypeB" },
                { "ColumnC", "SqlServerTypeC", "OracleTypeC" },
                { "ColumnD", "SqlServerTypeD", "OracleTypeD" },
                { "ColumnE", "SqlServerTypeE", "OracleTypeE" } },
            "TemporaryTableName", "DestinationTableName", "ForeignKeyTableName")
    {}

    using DatabaseBulkUpdater::getFinalizeSql;
    using DatabaseBulkUpdater::getRejectedRowsSql;
};

BOOST_AUTO_TEST_CASE(test_bulk_updater_finalize_sql)
{
    {
        test_BulkUpdater bu { test_BulkUpdater::DbClientType::SqlServer };

        BOOST_CHECK_EQUAL(
            bu.getFinalizeSql(),
            "MERGE DestinationTableName"
            " USING ("
                "SELECT Temp_TemporaryTableName.*"
                " FROM Temp_TemporaryTableName"
                " JOIN ForeignKeyTableName"
                " ON Temp_TemporaryTableName.ColumnA=ForeignKeyTableName.ColumnA) t"
            " ON DestinationTableName.ColumnA = t.ColumnA"
            " WHEN MATCHED THEN"
            " UPDATE SET ColumnB = t.ColumnB, ColumnC = t.ColumnC, ColumnD = t.ColumnD, ColumnE = t.ColumnE"
            " WHEN NOT MATCHED THEN"
            " INSERT (ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
            " VALUES (t.ColumnA, t.ColumnB, t.ColumnC, t.ColumnD, t.ColumnE);");
    }

    {
        test_BulkUpdater bu { test_BulkUpdater::DbClientType::Oracle };

        BOOST_CHECK_EQUAL(
            bu.getFinalizeSql(),
            "MERGE INTO DestinationTableName"
            " USING ("
                "SELECT Temp_TemporaryTableName.*"
                " FROM Temp_TemporaryTableName"
                " JOIN ForeignKeyTableName"
                " ON Temp_TemporaryTableName.ColumnA=ForeignKeyTableName.ColumnA) t"
            " ON (DestinationTableName.ColumnA = t.ColumnA)"
            " WHEN MATCHED THEN"
            " UPDATE SET ColumnB = t.ColumnB, ColumnC = t.ColumnC, ColumnD = t.ColumnD, ColumnE = t.ColumnE"
            " WHEN NOT MATCHED THEN"
            " INSERT (ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
            " VALUES (t.ColumnA, t.ColumnB, t.ColumnC, t.ColumnD, t.ColumnE)");
    }
}

BOOST_AUTO_TEST_CASE(test_bulk_updater_get_rejected_rows_sql)
{
    {
        test_BulkUpdater bu { test_BulkUpdater::DbClientType::SqlServer };

        BOOST_CHECK_EQUAL(
            bu.getRejectedRowsSql(),
            "SELECT Temp_TemporaryTableName.ColumnA"
            " FROM Temp_TemporaryTableName"
            " WHERE NOT EXISTS ("
                "SELECT ColumnA"
                " FROM ForeignKeyTableName"
                " WHERE ForeignKeyTableName.ColumnA = Temp_TemporaryTableName.ColumnA)");
    }

    {
        test_BulkUpdater bu { test_BulkUpdater::DbClientType::Oracle };

        BOOST_CHECK_EQUAL(
            bu.getRejectedRowsSql(),
            "SELECT Temp_TemporaryTableName.ColumnA"
            " FROM Temp_TemporaryTableName"
            " WHERE NOT EXISTS ("
                "SELECT ColumnA"
                " FROM ForeignKeyTableName"
                " WHERE ForeignKeyTableName.ColumnA = Temp_TemporaryTableName.ColumnA)");
    }
}

BOOST_AUTO_TEST_SUITE_END()

