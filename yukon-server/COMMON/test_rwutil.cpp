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

BOOST_AUTO_TEST_CASE(test_to_rwdbdt)
{
    CtiTime nowCtiTime;
    RWDBDateTime rwdbdt = toRWDBDT(nowCtiTime);

    BOOST_CHECK_EQUAL( rwdbdt.year(), nowCtiTime.date().year() );
    BOOST_CHECK_EQUAL( rwdbdt.dayOfMonth(), nowCtiTime.date().dayOfMonth() );
    BOOST_CHECK_EQUAL( rwdbdt.hour(), nowCtiTime.hour() );
    BOOST_CHECK_EQUAL( rwdbdt.minute(), nowCtiTime.minute() );
    BOOST_CHECK_EQUAL( rwdbdt.second(), nowCtiTime.second() );

    nowCtiTime = nowCtiTime.beginDST(2004);
    rwdbdt = toRWDBDT(nowCtiTime);

    BOOST_CHECK_EQUAL( rwdbdt.year(), nowCtiTime.date().year() );
    BOOST_CHECK_EQUAL( rwdbdt.dayOfMonth(), nowCtiTime.date().dayOfMonth() );
    BOOST_CHECK_EQUAL( rwdbdt.hour(), nowCtiTime.hour() );
    BOOST_CHECK_EQUAL( rwdbdt.minute(), nowCtiTime.minute() );
    BOOST_CHECK_EQUAL( rwdbdt.second(), nowCtiTime.second() );

    nowCtiTime = nowCtiTime.endDST(2004);
    rwdbdt = toRWDBDT(nowCtiTime);

    BOOST_CHECK_EQUAL( rwdbdt.year(), nowCtiTime.date().year() );
    BOOST_CHECK_EQUAL( rwdbdt.dayOfMonth(), nowCtiTime.date().dayOfMonth() );
    BOOST_CHECK_EQUAL( rwdbdt.hour(), nowCtiTime.hour() );
    BOOST_CHECK_EQUAL( rwdbdt.minute(), nowCtiTime.minute() );
    BOOST_CHECK_EQUAL( rwdbdt.second(), nowCtiTime.second() );

    nowCtiTime = nowCtiTime.beginDST(2007);
    rwdbdt = toRWDBDT(nowCtiTime);

    BOOST_CHECK_EQUAL( rwdbdt.year(), nowCtiTime.date().year() );
    BOOST_CHECK_EQUAL( rwdbdt.dayOfMonth(), nowCtiTime.date().dayOfMonth() );
    BOOST_CHECK_EQUAL( rwdbdt.hour(), nowCtiTime.hour() );
    BOOST_CHECK_EQUAL( rwdbdt.minute(), nowCtiTime.minute() );
    BOOST_CHECK_EQUAL( rwdbdt.second(), nowCtiTime.second() );
}

BOOST_AUTO_TEST_CASE(test_to_boost_date)
{
    RWDate rw_date = RWDate();
    date d = to_boost_date(rw_date);
    BOOST_CHECK_EQUAL( rw_date.dayOfMonth(), d.day() );
    BOOST_CHECK_EQUAL( rw_date.month(), d.month() );
    BOOST_CHECK_EQUAL( rw_date.year(), d.year() );
}

BOOST_AUTO_TEST_CASE(test_to_boost_ptime)
{
    typedef boost::date_time::c_local_adjustor<ptime> local_adj;
    RWDBDateTime rw_date_time;
    ptime boost_time = to_boost_ptime(rw_date_time);
    boost_time = local_adj::utc_to_local(boost_time);

    date d = boost_time.date();
    BOOST_CHECK_EQUAL( rw_date_time.dayOfMonth(), d.day() );
    BOOST_CHECK_EQUAL( rw_date_time.month(), d.month() );
    BOOST_CHECK_EQUAL( rw_date_time.year(), d.year() );

    time_duration td = boost_time.time_of_day();
    std::cout << "Boost, rwdate times: " <<  to_simple_string(boost_time) << ", " << rw_date_time.asString() << std::endl;
    BOOST_CHECK_EQUAL( rw_date_time.hour(), td.hours() );
    BOOST_CHECK_EQUAL( rw_date_time.minute(), td.minutes() );
    BOOST_CHECK_EQUAL( rw_date_time.second(), td.seconds() );
}


BOOST_AUTO_TEST_CASE(test_stringCompareIgnoreCase)
{
    std::string s1 = "My Compare";
    std::string s2 = "my cOmParE";
    BOOST_CHECK_EQUAL( stringCompareIgnoreCase(s1, s2), 0 );


}


BOOST_AUTO_TEST_CASE(test_makeLeftOuterJoinSQL92Compliant)
{
    // Tests both Microsoft SQL and Oracle syntax

    // No transformation

    string ms_input = "SELECT t54.PointID, t54.TRANSLATION, t54.DESTINATION FROM FDRTranslation t54";
    string or_input = ms_input;
    string expected = ms_input;

    string result = makeLeftOuterJoinSQL92Compliant(ms_input);
    BOOST_CHECK_EQUAL(result, expected);
    result = makeLeftOuterJoinSQL92Compliant(or_input);
    BOOST_CHECK_EQUAL(result, expected);

    // single left outer join with no where condition

    ms_input = "SELECT t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM Point t55, PointAnalog t56"
               " WHERE t55.PointID *= t56.PointID";
    or_input = "SELECT t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM Point t55, PointAnalog t56"
               " WHERE t55.PointID = t56.PointID (+)";
    expected = "SELECT t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM Point t55 LEFT OUTER JOIN"
               " PointAnalog t56 ON t55.PointID = t56.PointID";

    result = makeLeftOuterJoinSQL92Compliant(ms_input);
    BOOST_CHECK_EQUAL(result, expected);
    result = makeLeftOuterJoinSQL92Compliant(or_input);
    BOOST_CHECK_EQUAL(result, expected);

    // single left outer join with extra where condition

    ms_input = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55, PointAnalog t56 WHERE t54.PointID = t55.PointID"
               " AND t55.PointID *= t56.PointID";
    or_input = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55, PointAnalog t56 WHERE t54.PointID = t55.PointID"
               " AND t55.PointID = t56.PointID (+)";
    expected = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55 LEFT OUTER JOIN PointAnalog t56 ON t55.PointID = t56.PointID"
               " WHERE t54.PointID = t55.PointID";

    result = makeLeftOuterJoinSQL92Compliant(ms_input);
    BOOST_CHECK_EQUAL(result, expected);
    result = makeLeftOuterJoinSQL92Compliant(or_input);
    BOOST_CHECK_EQUAL(result, expected);

    // multiple left outer joins with no where condition

    ms_input = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55, PointAnalog t56 WHERE t54.PointID *= t55.PointID"
               " AND t54.PointID *= t56.PointID";
    or_input = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55, PointAnalog t56 WHERE t54.PointID = t55.PointID (+)"
               " AND t54.PointID = t56.PointID (+)";
    expected = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54 LEFT OUTER JOIN Point t55 ON t54.PointID = t55.PointID LEFT OUTER JOIN"
               " PointAnalog t56 ON t54.PointID = t56.PointID";

    result = makeLeftOuterJoinSQL92Compliant(ms_input);
    BOOST_CHECK_EQUAL(result, expected);
    result = makeLeftOuterJoinSQL92Compliant(or_input);
    BOOST_CHECK_EQUAL(result, expected);

    // multiple left outer joins with extra where condition

    ms_input = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55, PointAnalog t56 WHERE t54.PointID *= t55.PointID"
               " AND t54.PointID *= t56.PointID AND t54.INTERFACETYPE = 'RCCS' AND"
               " (t54.DIRECTIONTYPE = 'Receive' OR t54.DIRECTIONTYPE = 'Receive for control')";
    or_input = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55, PointAnalog t56 WHERE t54.PointID = t55.PointID (+)"
               " AND t54.PointID = t56.PointID (+) AND t54.INTERFACETYPE = 'RCCS' AND"
               " (t54.DIRECTIONTYPE = 'Receive' OR t54.DIRECTIONTYPE = 'Receive for control')";
    expected = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54 LEFT OUTER JOIN Point t55 ON t54.PointID = t55.PointID LEFT OUTER JOIN"
               " PointAnalog t56 ON t54.PointID = t56.PointID WHERE t54.INTERFACETYPE = 'RCCS' AND"
               " (t54.DIRECTIONTYPE = 'Receive' OR t54.DIRECTIONTYPE = 'Receive for control')";

    result = makeLeftOuterJoinSQL92Compliant(ms_input);
    BOOST_CHECK_EQUAL(result, expected);
    result = makeLeftOuterJoinSQL92Compliant(or_input);
    BOOST_CHECK_EQUAL(result, expected);

    // single left outer join with multiple on conditions and extra where condition

    ms_input = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55, PointAnalog t56 WHERE t54.PointID = t55.PointID"
               " AND t55.PointID *= t56.PointID AND t55.MULTIPLIER *= t56.MULTIPLIER";
    or_input = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55, PointAnalog t56 WHERE t54.PointID = t55.PointID"
               " AND t55.PointID = t56.PointID (+) AND t55.MULTIPLIER = t56.MULTIPLIER (+)";
    expected = "SELECT t54.PointID, t55.PointType, t56.MULTIPLIER, t56.DATAOFFSET FROM"
               " FDRTranslation t54, Point t55 LEFT OUTER JOIN PointAnalog t56 ON t55.PointID = t56.PointID"
               " AND t55.MULTIPLIER = t56.MULTIPLIER WHERE t54.PointID = t55.PointID";

    result = makeLeftOuterJoinSQL92Compliant(ms_input);
    BOOST_CHECK_EQUAL(result, expected);
    result = makeLeftOuterJoinSQL92Compliant(or_input);
    BOOST_CHECK_EQUAL(result, expected);
}

