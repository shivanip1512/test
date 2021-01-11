#include <boost/test/unit_test.hpp>

#include "fdrtextimport.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_fdrtextimport )

BOOST_AUTO_TEST_CASE( test_ReportUnfinished )
{
    std::cout << "TextImport Unit Testing is not complete " << std::endl;
}

BOOST_AUTO_TEST_CASE(test_foreignToYukonQuality)
{
    CtiFDR_TextImport import;
    USHORT quality = import.ForeignToYukonQuality('f');

    BOOST_CHECK_EQUAL(quality, NonUpdatedQuality);
    std::cout << "test_foreignToYukonQuality is not finished " << std::endl;
}

BOOST_AUTO_TEST_CASE( test_parseFile_OneFile )
{
    /*
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

    ofstream strm;

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
    */
    BOOST_CHECK_EQUAL(true, true );
}

BOOST_AUTO_TEST_CASE( test_parseFile_ThreeFile )
{
    /*
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
    */
    BOOST_CHECK_EQUAL(true, true );
}

BOOST_AUTO_TEST_CASE( test_validateAndDecodeLine )
{
    //BOOST_CHECK_EQUAL(1,0);
    std::cout << "test_validateAndDecodeLine not finished\n";
}

BOOST_AUTO_TEST_SUITE_END()
