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
        "update tableZ set requests = ?, startdatetime = ?, stopdatetime = ? where paobjectid = ? OR statistictype = ?",
        "insert into tableOmega values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
    };

    const std::string sql_expected_output[] = {
        "",
        "select * from tableX",
        "delete from ccfeederbanklist where feederid = :1",
        "delete from ccfeederbanklist where feederid = :1 and paoid = :2",
        "insert into tableY values (:1, :2, :3, :4, :5, :6, :7, :8, :9, :10, :11)",
        "insert into tableY (ip, port) values (:1, :2)",
        "update tableZ set requests = :1, startdatetime = :2, stopdatetime = :3 where paobjectid = :4 OR statistictype = :5",
        "insert into tableOmega values (:1, :2, :3, :4, :5, :6, :7, :8, :9, :10, :11)"
                                    ", (:12, :13, :14, :15, :16, :17, :18, :19, :20, :21, :22), (:23, :24, :25, :26, :27, :28, :29, :30, :31, :32, :33)"
                                    ", (:34, :35, :36, :37, :38, :39, :40, :41, :42, :43, :44), (:45, :46, :47, :48, :49, :50, :51, :52, :53, :54, :55)"
                                    ", (:56, :57, :58, :59, :60, :61, :62, :63, :64, :65, :66), (:67, :68, :69, :70, :71, :72, :73, :74, :75, :76, :77)"
                                    ", (:78, :79, :80, :81, :82, :83, :84, :85, :86, :87, :88), (:89, :90, :91, :92, :93, :94, :95, :96, :97, :98, :99)"
                                    ", (:100, :101, :102, :103, :104, :105, :106, :107, :108, :109, :110)"
    };

    for (int i = 0; i < sizeof(sql_input)/sizeof(*sql_input); i++)
    {
        BOOST_CHECK_EQUAL( sql_expected_output[i], assignSQLPlaceholders( sql_input[i] ) );
    }
}

BOOST_AUTO_TEST_SUITE_END()

