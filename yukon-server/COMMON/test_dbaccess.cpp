#include <boost/test/unit_test.hpp>

#include "dbaccess.h"

BOOST_AUTO_TEST_SUITE( test_dbaccess )

BOOST_AUTO_TEST_CASE(test_assignSQLPlaceholders)
{
    const std::string sql_input[] = {
        "",
        "select * from tableX",
        "delete from ccfeederbanklist where feederid = ?",
        "delete from ccfeederbanklist where feederid = ? and paoid = ?",
        "insert into tableY values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        "insert into tableY (ip, port) values (?, ?)",
        "update tableZ set requests = ?, startdatetime = ?, stopdatetime = ? where paobjectid = ? OR statistictype = ?"
    };

    const std::string sql_expected_output[] = {
        "",
        "select * from tableX",
        "delete from ccfeederbanklist where feederid = :1",
        "delete from ccfeederbanklist where feederid = :1 and paoid = :2",
        "insert into tableY values (:1, :2, :3, :4, :5, :6, :7, :8, :9, :10, :11)",
        "insert into tableY (ip, port) values (:1, :2)",
        "update tableZ set requests = :1, startdatetime = :2, stopdatetime = :3 where paobjectid = :4 OR statistictype = :5"
    };

    for (int i = 0; i < sizeof(sql_input)/sizeof(*sql_input); i++)
    {
        BOOST_CHECK_EQUAL( sql_expected_output[i], assignSQLPlaceholders( sql_input[i] ) );
    }
}

BOOST_AUTO_TEST_SUITE_END()

