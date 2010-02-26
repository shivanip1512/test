#include <winsock.h>
/*-----------------------------------------------------------------------------*
*
* File:   test_ccusim.cpp
*
* Date:   11/30/2007
* 
* Commented-out functions remaining in the file generate a csv file for
* testing kWh output.
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test ccuSim"

#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <string>
#include <sstream>    // for istringstream
#include <time.h>
#include <locale>
#include <fstream>

#define BOOST_AUTO_TEST_MAIN "Test ccuSim"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <boost/tokenizer.hpp>
#include "ctitime.h"
#include "ctidate.h"
#include "CCU711.h"
#include "yukon.h"
#include "rwutil.h"
#include "numstr.h"
#include "dev_mct410.h"
#include "Mct410.h"

#include "types.h"

using boost::unit_test_framework::test_suite;
using namespace std;
using namespace Cti::Simulator;

BOOST_AUTO_TEST_CASE( test_EmetconWords_extract_bits )
{
    bytes b_word;

    //  verifying on a B word

    b_word.push_back(0xaf);
    b_word.push_back(0xf4);
    b_word.push_back(0xb5);
    b_word.push_back(0xa1);
    b_word.push_back(0xd0);
    b_word.push_back(0x05);
    b_word.push_back(0xa0);

    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word,  0,  4), 0x0a);
    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word,  4,  5), 0x1f);
    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word,  9,  3), 0x07);
    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word, 12, 22), 1234567);
    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word, 34,  1), 0x00);
    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word, 35,  1), 0x01);
    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word, 36,  8), 0x00);
    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word, 44,  2), 0x01);
    BOOST_CHECK_EQUAL(EmetconWord::extract_bits(b_word, 46,  6), 0x1a);

}

/*
unsigned char * buildBufferForSimulatorInput(string hexString, int& retSize)
{
    unsigned char * buffer = new unsigned char [300];

    //tokenize on ' '.
    boost::char_separator<char> sep(" ");
    Boost_char_tokenizer tokenizer(hexString, sep);
    retSize = 0;
    for(Boost_char_tokenizer::iterator tok = tokenizer.begin(); tok != tokenizer.end(); tok++)
    {
        string hexToken = *tok;
        BOOST_REQUIRE(hexToken.length() == 2);

        char *p;
        long ret = strtol(hexToken.c_str(), &p, 16);

        buffer[retSize++] = ret & 0xff;
    }
    cout << endl;
    return buffer;
}

// Function to take two string of hex values and a ccu and make sure the  in and out match
bool TestTransaction(Ccu711 &testCCU, string inBuf, string expectedOutput)
{
    bool ret = true;
    int inBufferSize = 0;
    int outBufferSize = 0;
    unsigned char* inBuffer = NULL;
    unsigned char* outBuffer = NULL;
    unsigned char SendData[300];
    int msgSize = 0;
    int i = 0;

    //Tests:
    inBuffer = buildBufferForSimulatorInput(inBuf,inBufferSize);
    outBuffer = buildBufferForSimulatorInput(expectedOutput,outBufferSize);

    BOOST_REQUIRE(inBuffer != NULL);
    BOOST_REQUIRE(outBuffer != NULL);

    testCCU.processRequest(inBuffer,inBufferSize);

    msgSize = testCCU.createResponse(SendData);

    BOOST_REQUIRE(msgSize == outBufferSize);

    for( i = 0; i < msgSize; i++)
    {
        BOOST_CHECK_EQUAL(SendData[i],outBuffer[i]);
        if (SendData[i] != outBuffer[i])
        {
            ret = false;
        }
    }
    if( ret == false)
    {
        std::cout << endl << "Error in data:\n";
        for (i = 0;i < msgSize;i++)
        {
            std::cout << string(CtiNumStr(outBuffer[i]).hex().zpad(2)) << " ";
        }
        std::cout << endl << endl;

        for (i = 0;i < msgSize;i++)
        {
            std::cout << string(CtiNumStr(SendData[i]).hex().zpad(2)) << " ";
        }
        std::cout << endl;
    }

    delete [] inBuffer;
    delete [] outBuffer;

    return ret;
}

BOOST_AUTO_TEST_CASE( test_LLP_Data_Generation )
{
    CtiTime time;
    bool valueInsideRange = true;

    std::cout << "Testing LoadProfile Generation for all 31556926 possible values. This will take a while." << std::endl;

    for (int i = 0; i < 31556926; i++)
    {
        time.addSeconds(1);
        double value = mctGetLoadProfile(0,time);

        //.5 and 3 are const's from the function.
        if (value < 500 || value > 3500)
        {
            std::cout << value << std::endl;
            //If in here. Its not in range
            BOOST_REQUIRE(valueInsideRange==false);

        }
    }
    std::cout << "Done Testing LoadProfile Generation." << std::endl;
}

BOOST_AUTO_TEST_CASE( test_loadProfile_largeMessage)
{
    //This message caused a bug because it went over 10 messages.
    //This max is set to 20 now.. which should be fine since 255 bytes is the max message...
    Ccu711 testCCU(2000);

    string input =  "7e 17 32 f1 01 2b 11 00 06 87 3b 07 14 be a4 00 ff 00 01 98 00 90 03 11 01 06 ";
           input += "87 3b 07 14 be a5 00 ff 00 01 98 00 90 03 11 02 06 87 3b 07 14 be a6 00 ff 00 ";
           input += "01 98 00 90 03 11 03 06 87 3b 07 14 be a7 00 ff 00 01 98 00 90 03 11 04 06 87 ";
           input += "3b 07 14 be a8 00 ff 00 01 98 00 90 03 11 05 06 87 3b 07 14 be a9 00 ff 00 01 ";
           input += "98 00 90 03 11 06 06 87 3b 07 14 be aa 00 ff 00 01 98 00 90 03 11 07 06 87 3b ";
           input += "07 14 be ab 00 ff 00 01 98 00 90 03 11 08 06 87 3b 07 14 be ac 00 ff 00 01 98 ";
           input += "00 90 03 11 09 06 87 3b 07 14 be ad 00 ff 00 01 98 00 90 03 11 0a 06 87 3b 07 ";
           input += "14 be ae 00 ff 00 01 98 00 90 03 11 0b 06 87 3b 07 14 be af 00 ff 00 01 98 00 ";
           input += "90 03 11 0c 06 87 3b 07 14 be b0 00 ff 00 01 98 00 90 03 11 0d 06 87 3b 07 14 ";
           input += "be b1 00 ff 00 01 98 00 90 03 00 e9 ca";

    string output = "7e 17 12 10 02 ab 00 00 00 00 00 00 12 00 00 2a 00 00 c3 c3 ac 65";

    TestTransaction(testCCU,input,output);
}

BOOST_AUTO_TEST_CASE( test_getValueKwh_noqueue )
{
    //Porter Copy paste. Cloning this transaction.
    //
    //    07/28/2008 14:14:36  P:  35 / CCU Sim 2  D:  32 / CCU Sim Test2 OUT:
    //    7e 05 b2 0d 02 26 0b 06 00 87 af f4 b5 7e 19 0e 30 5d e3
    //    07/28/2008 14:14:37  P:  35 / CCU Sim 2  D:  32 / CCU Sim Test2 IN:
    //    7e 05 1a 19 02 a6 00 00 00 00 00 00 20 01 00 03 00 00 42 42 82 d1 5f 80 65 49
    //    03 80 42 79 97
    //    07/28/2008 14:14:37 **** Accumulator Decode for "TestMCt2" **** dev_mct470.cpp (3486)
    //    07/28/2008 14:14:37  P:  35 / CCU Sim 2  D:  32 / CCU Sim Test2 OUT:
    //    7e 05 d0 0e 00 09 0b 04 b0 ff 01 02 ff 04 05 ff ff 00 c7 c8
    //    07/28/2008 14:14:38  P:  35 / CCU Sim 2  D:  32 / CCU Sim Test2 IN:
    //    7e 05 1c 0e 02 a9 00 00 00 00 00 00 20 01 00 03 00 00 cd 6f
    //    07/28/2008 14:14:39  P:  35 / CCU Sim 2  D:  32 / CCU Sim Test2 OUT:
    //    7e 05 f0 03 01 27 04 a0 2b
    //    07/28/2008 14:14:39  P:  35 / CCU Sim 2  D:  32 / CCU Sim Test2 IN:
    //    7e 05 3e 0f 00 a7 00 00 18 50 00 00 20 00 00 00 00 00 00 48 4c

    Ccu711 testCCU(2000);

    string input = "7e f7 d0 14 01 2b 11 00 06 80 01 0e 12 d5 f7 00 ff 00 01 98 00 90 03 00 ad ad";
    string output = "7e 05 1a 19 02 a6 00 00 00 00 00 00 20 01 00 03 00 00 42 42 82 d1 5f 80 65 49 03 80 42 79 97";

    //Can't test this yet. There is a time being generated. need to adjust for the current system time.
    //bool ok = TestTransaction(testCCU,input,output);

}


namespace Cti {
namespace Simulator {

class test_MCT410 : public Mct410Sim
{
public:

    test_MCT410(unsigned address) :
        Mct410Sim(address)
    {
    }
    //static double makeValue_consumption(const unsigned address, const CtiTime &c_time, const unsigned duration)
    //{
    //return Mct410Sim::makeValue_consumption(address, c_time, duration);
    //}

    unsigned getHectoWattHours(const unsigned _address, const CtiTime c_time)
    {
        unsigned hWh = Mct410Sim::getHectoWattHours(_address, c_time);
        return hWh;
    }

};

}
}

BOOST_AUTO_TEST_CASE( test_makevalue_consumption )
{
    ofstream outFile;
    outFile.open("C:\\Dev\\trunk\\yukon-server-d\\bin\\hWhLog.txt");

    const unsigned address = 1500;
    test_MCT410 test_device(address);
    bool flag = false;
    static const CtiTime end_of_time = CtiTime::CtiTime(CtiDate::CtiDate(31, 12, 2007), 0, 0, 0);
    CtiTime test_time = CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2005), 0, 0, 0);
    // BOOST_CHECK_EQUAL(makeValue_consumption(address, begin_of_time.seconds(), duration), WhoCares);

    outFile << "Consumption Value Test Log\n";
    outFile << "Date/Time of Test: " << CtiTime::now() << endl << "Meter Address: " << address << endl << endl;

    test_time.addDays(1, flag);

    for( ; test_time.seconds() <  end_of_time.seconds() - 600000; test_time.addDays(1, flag))
    {
        if(!(outFile << test_time.asString() << ", " << test_device.getHectoWattHours(address, test_time) / 10.0 << " \tkWh\n"))
        {
            std::cout << "ERROR WRITING TO OUTPUT FILE";
        }
    }

    outFile.close();

}

*/
