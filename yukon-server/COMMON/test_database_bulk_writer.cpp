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
                "SELECT DISTINCT Temp_TemporaryTableName.*"
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
                "SELECT DISTINCT Temp_TemporaryTableName.*"
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

struct test_TwoPrimaryKeyBulkUpdater : Cti::Database::DatabaseBulkUpdater<5>
{
    test_TwoPrimaryKeyBulkUpdater(DbClientType clientType)
        : DatabaseBulkUpdater( clientType,
            { Cti::Database::ColumnDefinition
                { "ColumnA", "SqlServerTypeA", "OracleTypeA" },
                { "ColumnB", "SqlServerTypeB", "OracleTypeB" },
                { "ColumnC", "SqlServerTypeC", "OracleTypeC" },
                { "ColumnD", "SqlServerTypeD", "OracleTypeD" },
                { "ColumnE", "SqlServerTypeE", "OracleTypeE" } },
            2,  // ColumnA and ColumnB are primary keys
            "TemporaryTableName", "DestinationTableName", "ForeignKeyTableName")
    {}

    using DatabaseBulkUpdater::getFinalizeSql;
    using DatabaseBulkUpdater::getRejectedRowsSql;
};

BOOST_AUTO_TEST_CASE(test_2_primary_key_bulk_updater_finalize_sql)
{
    {
        test_TwoPrimaryKeyBulkUpdater bu { test_BulkUpdater::DbClientType::SqlServer };

        BOOST_CHECK_EQUAL(
            bu.getFinalizeSql(),
            "MERGE DestinationTableName"
            " USING ("
                "SELECT DISTINCT Temp_TemporaryTableName.*"
                " FROM Temp_TemporaryTableName"
                " JOIN ForeignKeyTableName"
                " ON Temp_TemporaryTableName.ColumnA=ForeignKeyTableName.ColumnA) t"
            " ON DestinationTableName.ColumnA = t.ColumnA AND DestinationTableName.ColumnB = t.ColumnB"
            " WHEN MATCHED THEN"
            " UPDATE SET ColumnC = t.ColumnC, ColumnD = t.ColumnD, ColumnE = t.ColumnE"
            " WHEN NOT MATCHED THEN"
            " INSERT (ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
            " VALUES (t.ColumnA, t.ColumnB, t.ColumnC, t.ColumnD, t.ColumnE);");
    }

    {
        test_TwoPrimaryKeyBulkUpdater bu { test_BulkUpdater::DbClientType::Oracle };

        BOOST_CHECK_EQUAL(
            bu.getFinalizeSql(),
            "MERGE INTO DestinationTableName"
            " USING ("
                "SELECT DISTINCT Temp_TemporaryTableName.*"
                " FROM Temp_TemporaryTableName"
                " JOIN ForeignKeyTableName"
                " ON Temp_TemporaryTableName.ColumnA=ForeignKeyTableName.ColumnA) t"
            " ON (DestinationTableName.ColumnA = t.ColumnA AND DestinationTableName.ColumnB = t.ColumnB)"
            " WHEN MATCHED THEN"
            " UPDATE SET ColumnC = t.ColumnC, ColumnD = t.ColumnD, ColumnE = t.ColumnE"
            " WHEN NOT MATCHED THEN"
            " INSERT (ColumnA, ColumnB, ColumnC, ColumnD, ColumnE)"
            " VALUES (t.ColumnA, t.ColumnB, t.ColumnC, t.ColumnD, t.ColumnE)");
    }
}

struct test_DynamicPaoStatsAccumulator : Cti::Database::DatabaseBulkAccumulator<9>
{
    test_DynamicPaoStatsAccumulator(DbClientType clientType)
        : DatabaseBulkAccumulator( clientType,
        { 
            Cti::Database::ColumnDefinition
                { "PAObjectId",             "numeric",      "NUMBER"        },
                { "StatisticType",          "varchar(16)",  "VARCHAR2(16)"  },
                { "StartDateTime",          "datetime",     "DATE"          },
                { "Requests",               "numeric",      "NUMBER"        },
                { "Attempts",               "numeric",      "NUMBER"        },
                { "Completions",            "numeric",      "NUMBER"        },
                { "CommErrors",             "numeric",      "NUMBER"        },
                { "ProtocolErrors",         "numeric",      "NUMBER"        },
                { "SystemErrors",           "numeric",      "NUMBER"        }
        },
        3, "DynamicPAOStatistics", "DynamicPaoStatistics", "DynamicPAOStatisticsId", "YukonPAObject")
    {}

    using DatabaseBulkAccumulator::getFinalizeSql;
};

BOOST_AUTO_TEST_CASE(test_dymamicPaoStatistics_bulk_accumulator_finalize_sql)
{
    {
        test_DynamicPaoStatsAccumulator dpsba { test_BulkUpdater::DbClientType::SqlServer };

        BOOST_CHECK_EQUAL(
            dpsba.getFinalizeSql(),
            "DECLARE @maxId NUMERIC;"
            "SELECT @maxId = COALESCE(MAX(DynamicPAOStatisticsId), 0) FROM DynamicPaoStatistics;"
            "MERGE DynamicPaoStatistics d "
            "USING ("
                "SELECT DISTINCT @maxId + ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS Temp_maxID, Temp_DynamicPAOStatistics.* "
                "FROM Temp_DynamicPAOStatistics "
                "JOIN YukonPAObject "
                    "ON Temp_DynamicPAOStatistics.PAObjectId=YukonPAObject.PAObjectId) t "
            "ON d.PAObjectId = t.PAObjectId "
                "AND d.StatisticType = t.StatisticType "
                "AND d.StartDateTime = t.StartDateTime "
            "WHEN MATCHED THEN "
                "UPDATE SET "
                    "Requests = d.Requests + t.Requests, "
                    "Attempts = d.Attempts + t.Attempts, "
                    "Completions = d.Completions + t.Completions, "
                    "CommErrors = d.CommErrors + t.CommErrors, "
                    "ProtocolErrors = d.ProtocolErrors + t.ProtocolErrors, "
                    "SystemErrors = d.SystemErrors + t.SystemErrors "
            "WHEN NOT MATCHED THEN "
                "INSERT "
                    "(DynamicPAOStatisticsId, PAObjectId, StatisticType, StartDateTime, Requests, Attempts, Completions, CommErrors, ProtocolErrors, SystemErrors) "
                "VALUES "
                    "(t.Temp_maxID, t.PAObjectId, t.StatisticType, t.StartDateTime, t.Requests, t.Attempts, t.Completions, t.CommErrors, t.ProtocolErrors, t.SystemErrors);"
        );
    }

    {
        test_DynamicPaoStatsAccumulator dpsba { test_BulkUpdater::DbClientType::Oracle };

        BOOST_CHECK_EQUAL(
            dpsba.getFinalizeSql(),
            "DECLARE maxId NUMBER;"
            "BEGIN "
            "SELECT COALESCE(MAX(DynamicPAOStatisticsId), 0) INTO maxId FROM DynamicPaoStatistics;"
            "MERGE INTO DynamicPaoStatistics d "
            "USING ("
                "SELECT DISTINCT maxId + (ROW_NUMBER() OVER (ORDER BY (SELECT 1 FROM DUAL))) AS Temp_maxID, Temp_DynamicPAOStatistics.* "
                "FROM Temp_DynamicPAOStatistics "
                "JOIN YukonPAObject "
                    "ON Temp_DynamicPAOStatistics.PAObjectId=YukonPAObject.PAObjectId) t "
            "ON (d.PAObjectId = t.PAObjectId "
                "AND d.StatisticType = t.StatisticType "
                "AND d.StartDateTime = t.StartDateTime) "
            "WHEN MATCHED THEN "
                "UPDATE SET "
                    "Requests = d.Requests + t.Requests, "
                    "Attempts = d.Attempts + t.Attempts, "
                    "Completions = d.Completions + t.Completions, "
                    "CommErrors = d.CommErrors + t.CommErrors, "
                    "ProtocolErrors = d.ProtocolErrors + t.ProtocolErrors, "
                    "SystemErrors = d.SystemErrors + t.SystemErrors "
            "WHEN NOT MATCHED THEN "
                "INSERT "
                    "(DynamicPAOStatisticsId, PAObjectId, StatisticType, StartDateTime, Requests, Attempts, Completions, CommErrors, ProtocolErrors, SystemErrors) "
                "VALUES "
                    "(t.Temp_maxID, t.PAObjectId, t.StatisticType, t.StartDateTime, t.Requests, t.Attempts, t.Completions, t.CommErrors, t.ProtocolErrors, t.SystemErrors); "
            "END;"
        );
    }
}

BOOST_AUTO_TEST_SUITE_END()

