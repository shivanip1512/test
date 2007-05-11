/*
    File test_fdrTextImport.cpp

    Author: Thain Spar
    Date:   5/10/2007 15:26 CST

    Test TextImport. Well only my UTC change to foreigntoyukon time function.
    But someday maybe more.

*/

#define BOOST_AUTO_TEST_MAIN "Test fdrTextImport"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <locale>



#include "yukon.h"
#include "ctidate.h"
#include "ctistring.h" // apparently fdrtextimport doesnt have this file.
#include "fdrtextimport.h"

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_UNIT_TEST(test_foreignToYukonQuality)
{
    CtiFDR_TextImport import;
    USHORT quality = import.ForeignToYukonQuality("failure");

    BOOST_CHECK_EQUAL(quality, NonUpdatedQuality);
    std::cout << "This is not finished " << std::endl;
}

BOOST_AUTO_UNIT_TEST( test_ForeignToYukonTime_flag )
{
    // date form:  string 05/02/2007 11:32:25
    // flag form:  char  'D' 'S' 'U'
    char dst = 'D';
    char standard = 'S';
    char utc = 'U';
    bool currentlyDst = false;

    struct tm *loc= new struct tm();
    CtiTime().extract(loc);

    //We need to know if it is DST now, because the CtiTime constructor will adjust
    //  our specified time to match what it thinks about DST.
    if(loc->tm_isdst == 1)
        currentlyDst = true;

    CtiString okDateString("05/10/2007 14:58:34");
    CtiTime okDate(CtiDate(10,5,2007),14,58,34);
    CtiString badString("03/11/2007 02:30:05:12");
    CtiTime badresult = PASTDATE;
    CtiString utcDateString("05/10/2007 19:58:34");

    CtiTime res;
    CtiFDR_TextImport txtimp;

    // Test with a daylight savings flag
    res = txtimp.ForeignToYukonTime(okDateString, dst);
    if(currentlyDst)
        BOOST_CHECK_EQUAL(res, okDate);
    else
        BOOST_CHECK_EQUAL( res, okDate-3600 );


    // Test with a standard flag
    res = txtimp.ForeignToYukonTime(okDateString, standard);
    if(currentlyDst)
        BOOST_CHECK_EQUAL(res, okDate+3600);
    else
        BOOST_CHECK_EQUAL(res, okDate);

    // Test with a utc flag.
    res = txtimp.ForeignToYukonTime(utcDateString, utc);
    BOOST_CHECK_EQUAL(res, okDate);

    //test the bad data
    res = txtimp.ForeignToYukonTime(badString, standard);
    BOOST_CHECK_EQUAL(res, badresult);

}

