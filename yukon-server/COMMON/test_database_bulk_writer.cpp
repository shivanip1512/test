#include <boost/test/unit_test.hpp>

#include "database_bulk_writer.h"

BOOST_AUTO_TEST_SUITE( test_database_bulk_writer )

struct test_BulkWriter : Cti::Database::DatabaseBulkWriter<5>
{
    test_BulkWriter() 
        : DatabaseBulkWriter( 
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

    std::string getFinalizeSql(const DbClientType clientType) const override
    {
        return "Unit Test Finalize Sql Placeholder";
    }
};

BOOST_AUTO_TEST_CASE(test_temp_table_creation_sql)
{
    test_BulkWriter bw;

    BOOST_CHECK_EQUAL(
        bw.getTempTableCreationSql(test_BulkWriter::DbClientType::SqlServer),
        "create table ##TemporaryTableName ("
            "ColumnA SqlServerTypeA not null,"
            "ColumnB SqlServerTypeB not null,"
            "ColumnC SqlServerTypeC not null,"
            "ColumnD SqlServerTypeD not null,"
            "ColumnE SqlServerTypeE not null"
        ")");

    BOOST_CHECK_EQUAL(
        bw.getTempTableCreationSql(test_BulkWriter::DbClientType::Oracle),
        "create global temporary table Temp_TemporaryTableName ("
            "ColumnA OracleTypeA not null,"
            "ColumnB OracleTypeB not null,"
            "ColumnC OracleTypeC not null,"
            "ColumnD OracleTypeD not null,"
            "ColumnE OracleTypeE not null"
        ") on commit preserve rows");
}

BOOST_AUTO_TEST_CASE(test_temp_table_truncation_sql)
{
    test_BulkWriter bw;

    BOOST_CHECK_EQUAL(
        bw.getTempTableTruncationSql(test_BulkWriter::DbClientType::SqlServer),
        "truncate table ##TemporaryTableName");

    BOOST_CHECK_EQUAL(
        bw.getTempTableTruncationSql(test_BulkWriter::DbClientType::Oracle),
        "truncate table Temp_TemporaryTableName");
}        

BOOST_AUTO_TEST_CASE(test_temp_table_insert_sql)
{
    test_BulkWriter bw;

    BOOST_CHECK_EQUAL(
        bw.getInsertSql(test_BulkWriter::DbClientType::SqlServer, 7),
        "INSERT INTO ##TemporaryTableName VALUES "
            "(?,?,?,?,?),(?,?,?,?,?),(?,?,?,?,?),(?,?,?,?,?),"
            "(?,?,?,?,?),(?,?,?,?,?),(?,?,?,?,?)");

    BOOST_CHECK_EQUAL(
        bw.getInsertSql(test_BulkWriter::DbClientType::Oracle, 7),
        "INSERT ALL"
            " INTO Temp_TemporaryTableName VALUES (?,?,?,?,?) INTO Temp_TemporaryTableName VALUES (?,?,?,?,?)"
            " INTO Temp_TemporaryTableName VALUES (?,?,?,?,?) INTO Temp_TemporaryTableName VALUES (?,?,?,?,?)"
            " INTO Temp_TemporaryTableName VALUES (?,?,?,?,?) INTO Temp_TemporaryTableName VALUES (?,?,?,?,?)"
            " INTO Temp_TemporaryTableName VALUES (?,?,?,?,?)"
        " SELECT 1 FROM DUAL");
}        

struct test_BulkInserter : Cti::Database::DatabaseBulkInserter<5>
{
    test_BulkInserter() 
        :    DatabaseBulkInserter( 
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
    test_BulkInserter bi;

    BOOST_CHECK_EQUAL(
        bi.getFinalizeSql(test_BulkInserter::DbClientType::SqlServer),
        "declare @maxId numeric;"
        "select @maxId = COALESCE(MAX(DestinationIdColumn), 0) from DestinationTableName;"
        "insert into"
            " RAWPOINTHISTORY (DestinationIdColumn, ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
            " select @maxId + ROW_NUMBER() OVER (ORDER BY (SELECT 1)), ColumnA, ColumnB, ColumnC, ColumnD, ColumnE FROM ##TemporaryTableName;");

    BOOST_CHECK_EQUAL(
        bi.getFinalizeSql(test_BulkInserter::DbClientType::Oracle),
        "DECLARE maxId number;"
        "BEGIN"
            " SELECT NVL(MAX(DestinationIdColumn), 0) INTO maxId FROM RAWPOINTHISTORY;"
            "INSERT INTO"
                " RAWPOINTHISTORY (DestinationIdColumn, ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
                " SELECT maxId + ROWNUM, ColumnA, ColumnB, ColumnC, ColumnD, ColumnE FROM Temp_TemporaryTableName;"
        " END;");
}        

struct test_BulkUpdater : Cti::Database::DatabaseBulkUpdater<5>
{
    test_BulkUpdater()
        : DatabaseBulkUpdater(
            { Cti::Database::ColumnDefinition
                { "ColumnA", "SqlServerTypeA", "OracleTypeA" },
                { "ColumnB", "SqlServerTypeB", "OracleTypeB" },
                { "ColumnC", "SqlServerTypeC", "OracleTypeC" },
                { "ColumnD", "SqlServerTypeD", "OracleTypeD" },
                { "ColumnE", "SqlServerTypeE", "OracleTypeE" } },
            "TemporaryTableName", "DestinationTableName", "DestinationIdColumn")
    {}

    using DatabaseBulkUpdater::getFinalizeSql;
};

BOOST_AUTO_TEST_CASE(test_bulk_updater_finalize_sql)
{
    test_BulkUpdater bu;

    BOOST_CHECK_EQUAL(
        bu.getFinalizeSql(test_BulkUpdater::DbClientType::SqlServer),
        "MERGE DestinationTableName"
        " USING ##TemporaryTableName"
        " ON DestinationTableName.DestinationIdColumn = ##TemporaryTableName.DestinationIdColumn"
        " WHEN MATCHED THEN"
        " UPDATE SET ColumnA = ##TemporaryTableName.ColumnA, ColumnB = ##TemporaryTableName.ColumnB, ColumnC = ##TemporaryTableName.ColumnC, ColumnD = ##TemporaryTableName.ColumnD, ColumnE = ##TemporaryTableName.ColumnE"
        " WHEN NOT MATCHED THEN"
        " INSERT (ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
        " VALUES (##TemporaryTableName.ColumnA, ##TemporaryTableName.ColumnB, ##TemporaryTableName.ColumnC, ##TemporaryTableName.ColumnD, ##TemporaryTableName.ColumnE);");

    BOOST_CHECK_EQUAL(
        bu.getFinalizeSql(test_BulkUpdater::DbClientType::Oracle),
        "BEGIN"
        " MERGE INTO DestinationTableName"
        " USING Temp_TemporaryTableName"
        " ON (DestinationTableName.DestinationIdColumn = Temp_TemporaryTableName.DestinationIdColumn)"
        " WHEN MATCHED THEN"
        " UPDATE SET ColumnA = Temp_TemporaryTableName.ColumnA, ColumnB = Temp_TemporaryTableName.ColumnB, ColumnC = Temp_TemporaryTableName.ColumnC, ColumnD = Temp_TemporaryTableName.ColumnD, ColumnE = Temp_TemporaryTableName.ColumnE"
        " WHEN NOT MATCHED THEN"
        " INSERT (ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
        " VALUES (Temp_TemporaryTableName.ColumnA, Temp_TemporaryTableName.ColumnB, Temp_TemporaryTableName.ColumnC, Temp_TemporaryTableName.ColumnD, Temp_TemporaryTableName.ColumnE)"
        " END;");
}

BOOST_AUTO_TEST_SUITE_END()

