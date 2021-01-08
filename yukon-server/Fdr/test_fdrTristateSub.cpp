#include <boost/test/unit_test.hpp>

#include "fdrTriStateSub.h"
#include "ctidate.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_fdrTriStateSub )

BOOST_AUTO_TEST_CASE( test_readInFile_goodData )
{   //example cases from an actual data file.
    FDRTriStateSub sm;
    string caseOne   = "\"20070516101500\",\"Nucla 115/69 Xfmr.\",\"MW\",4.326";
    string caseTwo   = "\"20070516101500\",\"Happy Canyon 661Idarado\",\"MW\",2.11";
    string caseThree = "\"20070516101500\",\"Cascade 115/69 (T2)\",\"MW\",5.978";

    istringstream iss;
    string input = "\"20070516101500\",\"Nucla 115/69 Xfmr.\",\"MW\",4.326\n\"20070516101500\",\"Happy Canyon 661Idarado\",\"MW\",2.11\n\"20070516101500\",\"Cascade 115/69 (T2)\",\"MW\",5.978";
    iss.str(input);

    list<string> stringList = sm.readInFile( iss );

    BOOST_CHECK_EQUAL( stringList.size(), 3     );

    list<string>::iterator listItr = stringList.begin();

    BOOST_CHECK_EQUAL( *listItr, caseOne   ); listItr++;
    BOOST_CHECK_EQUAL( *listItr, caseTwo   ); listItr++;
    BOOST_CHECK_EQUAL( *listItr, caseThree ); listItr++;

    BOOST_CHECK_EQUAL( listItr == stringList.end(),true );
}

BOOST_AUTO_TEST_CASE( test_readInFile_emptyFile )
{//a file with nothing in it. should return an empty list.
    FDRTriStateSub sm;

    istringstream iss;
    string input = "";
    iss.str(input);

    list<string> stringList = sm.readInFile( iss );

    BOOST_CHECK_EQUAL( stringList.size(), 0);

    list<string>::iterator listItr = stringList.begin();

    BOOST_CHECK_EQUAL( listItr == stringList.end(),true );

}

BOOST_AUTO_TEST_CASE( test_readInFile_emptyLine )
{
    FDRTriStateSub sm;

    istringstream iss;
    string input = "\n";
    iss.str(input);

    list<string> stringList = sm.readInFile( iss );

    BOOST_CHECK_EQUAL( stringList.size(), 0);

    list<string>::iterator listItr = stringList.begin();

    BOOST_CHECK_EQUAL( listItr == stringList.end(),true );

}

BOOST_AUTO_TEST_CASE( test_readInFile_badData )
{ //Bad data will have to be handled while processing, This is just to see if it will do what is expected.
    FDRTriStateSub sm;
    string caseOne = " ";

    istringstream iss;
    string input = " \n ";
    iss.str(input);

    list<string> stringList = sm.readInFile( iss );

    BOOST_CHECK_EQUAL( stringList.size(), 2);

    list<string>::iterator listItr = stringList.begin();
    BOOST_CHECK_EQUAL( *listItr, caseOne   ); listItr++;
    BOOST_CHECK_EQUAL( *listItr, caseOne   ); listItr++;
    BOOST_CHECK_EQUAL( listItr == stringList.end(),true );

}

BOOST_AUTO_TEST_CASE( test_processData_emptyInput )
{
    FDRTriStateSub sm;

    list<StringMessageContainer> msgList;
    list<string> stringList;

    msgList = sm.processData( stringList );

    BOOST_CHECK_EQUAL(msgList.size(),stringList.size());
}

BOOST_AUTO_TEST_CASE( test_processData_goodInput  )
{
    FDRTriStateSub sm;
    string caseOne   = "\"20070516101500\",\"Nucla 115/69 Xfmr.\",\"MW\",4.326";
    string caseTwo   = "\"20070516101500\",\"Happy Canyon 661Idarado\",\"MW\",2.11";
    string caseThree = "\"20070516101500\",\"Cascade 115/69 (T2)\",\"MW\",5.978";
    CtiTime aTime(CtiDate(16,5,2007),10,15,00);

    vector<double> vec;
    vec.push_back(4.326);vec.push_back(2.11);vec.push_back(5.978);

    StringMessageContainer msg;
    list<StringMessageContainer> msgList;
    list<string> stringList;
    stringList.push_back(caseOne); stringList.push_back(caseTwo);stringList.push_back(caseThree);

    msgList = sm.processData( stringList );

    BOOST_CHECK_EQUAL(msgList.size(),stringList.size());
    int i = 0;
    for( list<StringMessageContainer>::iterator itr = msgList.begin(); itr != msgList.end(); itr++ ){
        msg = *itr;
        CtiPointDataMsg* m = (CtiPointDataMsg*)(msg.getMessage().get());
        BOOST_CHECK_EQUAL( m == NULL, false);
        if( m != NULL ){
            double d  = m->getValue();
            CtiTime t = m->getTime();
            BOOST_CHECK_EQUAL( t == aTime, true);
            BOOST_CHECK_EQUAL( d , vec.at(i));
        }
        i++;
    }
}

BOOST_AUTO_TEST_CASE( test_processData_badInput   )
{
    FDRTriStateSub sm;
    string caseOne   = "\"20070516101500\",\"Nucla 115/69 Xfmr.\",\"MW\",4.326";
    string caseTwo   = "\"20070516101500\",\"Happy Canyon 661Idarado\",\"MW\",2.11";
    string caseThree = "\"20070516101500\",\"Cascade 115/69 (T2)\",\"MW\",5.978";

    BOOST_CHECK_EQUAL(1,1);
}

BOOST_AUTO_TEST_CASE( test_generateMessage_badInput )
{
    FDRTriStateSub sm;
    StringMessageContainer msg;
    string caseOne   = "20070516101500,Nucla 115/69 Xfmr.";
    string caseTwo   = "20070516101500,Nucla 115/69 Xfmr.,MW";
    string caseThr   = "20070516101500";
    string caseFou   = "";
    string caseFiv   = "20070,Nucla 115/69 Xfmr.,MW,4.326";
    string caseSix   = "99999999999999,Nucla 115/69 Xfmr.,MW,4.326";
    {
        //tokenize based off ',' to pass to generateMessage
        boost::char_separator<char> delim(",");
        Boost_char_tokenizer tokens(caseOne, delim);

        msg = sm.generateMessage(tokens);
        BOOST_CHECK_EQUAL(msg.getMessage().get() == NULL, true);
    }
    {
        //tokenize based off ',' to pass to generateMessage
        boost::char_separator<char> delim(",");
        Boost_char_tokenizer tokens(caseTwo, delim);

        msg = sm.generateMessage(tokens);
        BOOST_CHECK_EQUAL(msg.getMessage().get() == NULL, true);
    }
    {
        //tokenize based off ',' to pass to generateMessage
        boost::char_separator<char> delim(",");
        Boost_char_tokenizer tokens(caseThr, delim);

        msg = sm.generateMessage(tokens);
        BOOST_CHECK_EQUAL(msg.getMessage().get() == NULL, true);
    }
    {
        //tokenize based off ',' to pass to generateMessage
        boost::char_separator<char> delim(",");
        Boost_char_tokenizer tokens(caseFou, delim);

        msg = sm.generateMessage(tokens);
        BOOST_CHECK_EQUAL(msg.getMessage().get() == NULL, true);
    }
    //Bad Date
    {
        //tokenize based off ',' to pass to generateMessage
        boost::char_separator<char> delim(",");
        Boost_char_tokenizer tokens(caseFiv, delim);

        msg = sm.generateMessage(tokens);
        BOOST_CHECK_EQUAL(msg.getMessage().get() == NULL, true);
    }
    //Bad Date
    {
        //tokenize based off ',' to pass to generateMessage
        boost::char_separator<char> delim(",");
        Boost_char_tokenizer tokens(caseSix, delim);

        msg = sm.generateMessage(tokens);
        BOOST_CHECK_EQUAL(msg.getMessage().get() == NULL, true);
    }
}

BOOST_AUTO_TEST_CASE( test_generateMessage_goodInput )
{
    FDRTriStateSub sm;
    StringMessageContainer msg;
    string caseOne   = "20070516101500,Nucla 115/69 Xfmr.,MW,4.326";

    //tokenize based off ',' to pass to generateMessage
    boost::char_separator<char> delim(",");
    Boost_char_tokenizer tokens(caseOne, delim);

    msg = sm.generateMessage(tokens);

    CtiTime aTime = ((CtiPointDataMsg*)msg.getMessage().get())->getTime();
    CtiTime cTime(CtiDate(16,5,2007),10,15,0);
    //Check the time in the point
    BOOST_CHECK_EQUAL(aTime,cTime);

    double val = ((CtiPointDataMsg*)msg.getMessage().get())->getValue();
    double v = 4.326;
    //check the val in the point
    BOOST_CHECK_EQUAL(val,v);

}

BOOST_AUTO_TEST_SUITE_END()
