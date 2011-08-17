#define BOOST_AUTO_TEST_MAIN "Test One-Way Message Encryption"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;

#include "boostutil.h"
#include "encryption_oneway_message.h"
#include "CtiDate.h"
#include "CtiTime.h"



BOOST_AUTO_TEST_CASE(test_one_way_message_sequence_generator)
{
    CtiTime timeNow(CtiDate(31, 12, 1999), 18, 0, 0);

    CtiTime  lastXmitTime;
    unsigned utcCounter;

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 0         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 1         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 2         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 3         );

    timeNow += 5;

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 0         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 1         );

    timeNow += 1;

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 0         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 1         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 2         );
}


BOOST_AUTO_TEST_CASE(test_one_way_message_encryption)
{
    const CtiTime timeNow(CtiDate(31, 12, 1999), 18, 0, 0);

    char plainText[300] = 
    {
        0x53, 0x75, 0x70, 0x65, 0x72, 0x20, 0x53, 0x65,
        0x63, 0x72, 0x65, 0x74, 0x20, 0x44, 0x61, 0x74,
        0x61, 0x00,
        0x4d, 0x79, 0x50, 0x61, 0x73, 0x73, 0x77, 0x6f,     // Password = "MyPasswordIsPassword"
        0x72, 0x64, 0x49, 0x73, 0x50, 0x61, 0x73, 0x73,
        0x77, 0x6f, 0x72, 0x64,
        0x14,
        0xfc, 0x47, 0xcf, 0xe9, 0xe6, 0x16, 0x0b, 0x87,     // Key { 0x00, 0x01, 0x02, ..., 0x0f } encrypted with above
        0x97, 0x7e, 0x59, 0x5d, 0xb2, 0x6e, 0x39, 0x97,     //  password and yukon's encryption key
        0x7b, 0xe9, 0xff, 0x68                              // CMAC of encrypted key
    };

    unsigned plainTextLength = 59;

    char cipherText[300] = {0};

    unsigned cipherTextLength;

    const char expectedCipherText[] =
    {
        0xd2, 0x1a, 0x3e, 0x1e, 0x2a, 0x5d, 0x3d, 0x22,
        0x28, 0x4f, 0xfc, 0xc5, 0xa0, 0x2e, 0x1e, 0x3d,
        0x17, 0xbf,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
        0x17, 0xfc, 0xb1, 0xdf
    };
    
    const unsigned expectedCipherTextLength = sizeof( expectedCipherText );

    OneWayMsgEncryption     oneWay;

    // encrypt message twice to get the sequence number to the value we expect from our one and only test vector

    cipherTextLength = oneWay.encryptMessage( timeNow, plainText, plainTextLength, cipherText );

    cipherTextLength = oneWay.encryptMessage( timeNow, plainText, plainTextLength, cipherText );

    BOOST_CHECK_EQUAL( sizeof( expectedCipherText ), cipherTextLength );

    BOOST_CHECK_EQUAL_COLLECTIONS( cipherText,          cipherText + cipherTextLength,
                                   expectedCipherText,  expectedCipherText + sizeof( expectedCipherText ) );
}

