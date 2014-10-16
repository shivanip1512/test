#include <boost/test/unit_test.hpp>

#include "encryption_oneway.h"
#include "CtiDate.h"
#include "CtiTime.h"

BOOST_AUTO_TEST_SUITE( test_encryption_oneway )

BOOST_AUTO_TEST_CASE(test_one_way_encryption_and_decryption_on_random_data)
{
    const unsigned long counter = 0xdeadbeef;

    const unsigned char parentKey[] =
    {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
    };

    const CtiTime t(CtiDate(9, 9, 2010), 16, 6, 0);

    const unsigned int sequenceNo = 23456;

    const std::size_t   plainTextLength = 512;

    unsigned char plainText[plainTextLength];
    unsigned char cipherText[plainTextLength + 10];
    unsigned char expectedPlainText[plainTextLength];

    // fill plainText with "random" data ....

    std::srand( plainTextLength );
    for ( int i = 0; i < plainTextLength; ++i )
    {
        plainText[i] = std::rand() & 0x0ff;
    }

    OneWayEncryption  e( counter, parentKey );

    for ( int length = 0; length < plainTextLength; ++length )
    {
        e.encrypt( t, sequenceNo, plainText, length, cipherText );

        bool authenticated = e.decrypt( cipherText, length + 10, expectedPlainText );

        BOOST_CHECK_EQUAL( authenticated, true );

        BOOST_CHECK_EQUAL_COLLECTIONS( plainText,           plainText + length,
                                       expectedPlainText,   expectedPlainText + length );
    }
}

BOOST_AUTO_TEST_SUITE_END()
