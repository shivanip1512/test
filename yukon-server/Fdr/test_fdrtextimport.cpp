#include <winsock2.h>
/*
    File test_fdrTextImport.cpp

    Author: Thain Spar
    Date:   5/10/2007 15:26 CST

    Test TextImport. Well only my UTC change to foreigntoyukon time function.
    But someday maybe more.

*/

#define BOOST_AUTO_TEST_MAIN "Test fdrTextImport"

#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <sstream>    // for istringstream
#include <time.h>
#include <locale>

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

#include "fdrtextimport.h"
#include "yukon.h"
#include "ctidate.h"
#include "ctistring.h" // apparently fdrtextimport doesnt have this file.


using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_UNIT_TEST( test_ReportUnfinished )
{
    std::cout << "TextImport Unit Testing is not complete " << std::endl;
}

BOOST_AUTO_UNIT_TEST(test_foreignToYukonQuality)
{
    CtiFDR_TextImport import;
    USHORT quality = import.ForeignToYukonQuality("failure");

    BOOST_CHECK_EQUAL(quality, NonUpdatedQuality);
    std::cout << "test_foreignToYukonQuality is not finished " << std::endl;
}
/*
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
*/
BOOST_AUTO_UNIT_TEST( test_parseFile_OneFile )
{
    CtiFDR_TextImport import;
    std::list<string> expectedReturn;
    std::list<string> returnedStrings;

    string c5012w = "1,ALDM_C5012_kw,4410.736083984380,G,01/10/2007 02:03:00,S";
    string c5012v = "1,ALDM_C5012_kvar,1440.347984433174,G,01/10/2007 02:03:00,S";
    string c5013w = "1,ALDM_C5013_kw,3023.148059844970,G,01/10/2007 02:03:00,S";
    string c5013v = "1,ALDM_C5013_kvar,469.56400014460121,G,01/10/2007 02:03:00,S";
    string w0289w = "1,ALFY_W289_kw,2791.0041809082,G,01/10/2007 02:03:00,S";
    string w0289v = "1,ALFY_W289_kvar,553.980000317097,G,01/10/2007 02:03:00,S";
    string w0290w = "1,ALFY_W290_kw,3329.155921936040,G,01/10/2007 02:03:00,S";
    string w0290v = "1,ALFY_W290_kvar,701.708003878593,G,01/10/2007 02:03:00,S";

    
    expectedReturn.push_back(c5012w);
    expectedReturn.push_back(c5012v);
    expectedReturn.push_back(c5013w);
    expectedReturn.push_back(c5013v);
    expectedReturn.push_back(w0289w);
    expectedReturn.push_back(w0289v);
    expectedReturn.push_back(w0290w);
    expectedReturn.push_back(w0290v);

    stringstream strm;

    for(list<string>::iterator itr = expectedReturn.begin(); itr != expectedReturn.end();  )
    {
        strm << *itr;
        itr++;
        if( itr != expectedReturn.end() )
            strm << "\n";
    }

    std::list< std::iostream* > strmList;
    strmList.push_back( &strm );

    returnedStrings = import.parseFiles( strmList );

    bool sizeCheck = false;
    if( returnedStrings.size() == expectedReturn.size() )
        sizeCheck = true;

    BOOST_CHECK_EQUAL(sizeCheck, true );

    if( sizeCheck )
    {
        list<string>::iterator r_itr = returnedStrings.begin();
        for(list<string>::iterator itr = expectedReturn.begin(); itr != expectedReturn.end();  )
        {
            BOOST_CHECK_EQUAL( (*itr).compare(*r_itr), 0);
            itr++;
            r_itr++;
        }
    }
}

BOOST_AUTO_UNIT_TEST( test_parseFile_ThreeFile )
{
    CtiFDR_TextImport import;
    std::list<string> expectedReturn;
    std::list<string> returnedStrings;

    string c5012w = "1,ALDM_C5012_kw,4410.736083984380,G,01/10/2007 02:03:00,S";
    string c5012v = "1,ALDM_C5012_kvar,1440.347984433174,G,01/10/2007 02:03:00,S";
    string c5013w = "1,ALDM_C5013_kw,3023.148059844970,G,01/10/2007 02:03:00,S";
    string c5013v = "1,ALDM_C5013_kvar,469.56400014460121,G,01/10/2007 02:03:00,S";
    string w0289w = "1,ALFY_W289_kw,2791.0041809082,G,01/10/2007 02:03:00,S";
    string w0289v = "1,ALFY_W289_kvar,553.980000317097,G,01/10/2007 02:03:00,S";
    string w0290w = "1,ALFY_W290_kw,3329.155921936040,G,01/10/2007 02:03:00,S";
    string w0290v = "1,ALFY_W290_kvar,701.708003878593,G,01/10/2007 02:03:00,S";

    //file 1
    expectedReturn.push_back(w0289v);
    expectedReturn.push_back(w0290w);
    expectedReturn.push_back(w0290v);

    //file 2
    expectedReturn.push_back(c5012w);
    expectedReturn.push_back(c5013v);
    expectedReturn.push_back(w0289w);
    expectedReturn.push_back(c5012v);

    //file 3
    expectedReturn.push_back(c5013w);

    stringstream strm1, strm2, strm3;

    strm1 << w0289v << "\n" << w0290w << "\n" << w0290v;
    strm2 << c5012w << "\n" << c5013v << "\n" << w0289w << "\n" << c5012v;
    strm3 << c5013w;


    std::list< std::iostream* > strmList;
    strmList.push_back( &strm1 );
    strmList.push_back( &strm2 );
    strmList.push_back( &strm3 );

    returnedStrings = import.parseFiles( strmList );

    bool sizeCheck = false;
    if( returnedStrings.size() == expectedReturn.size() )
        sizeCheck = true;

    BOOST_CHECK_EQUAL(sizeCheck, true );

    if( sizeCheck )
    {
        list<string>::iterator r_itr = returnedStrings.begin();
        for(list<string>::iterator itr = expectedReturn.begin(); itr != expectedReturn.end();  )
        {
            BOOST_CHECK_EQUAL( (*itr).compare(*r_itr), 0);
            itr++;
            r_itr++;
        }
    }
}

BOOST_AUTO_UNIT_TEST( test_validateAndDecodeLine )
{
    //BOOST_CHECK_EQUAL(1,0);
    std::cout << "test_validateAndDecodeLine not finished\n";
}
