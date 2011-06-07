/*
 * file test_cmdparse.cpp
 *  
 * Author: Jian Liu 
 * Date: 07/26/2005 10:10:13 
 * 
 *
 * test rwutil.h
 * 
 */
#define BOOST_AUTO_TEST_MAIN "Test RW Utils"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/date_time/local_time_adjustor.hpp>
#include <boost/date_time/c_local_time_adjustor.hpp>

#include <string>

#include "rwutil.h"
#include "utility.h"
#include "ctitime.h"
#include "ctidate.h"
#include "boost_time.h"
#include "dbaccess.h"

using boost::unit_test_framework::test_suite;


BOOST_AUTO_TEST_CASE(test_stringCompareIgnoreCase)
{
    std::string s1 = "My Compare";
    std::string s2 = "my cOmParE";
    BOOST_CHECK_EQUAL( ciStringEqual(s1, s2),true );


}


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
