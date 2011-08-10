#define BOOST_AUTO_TEST_MAIN "Test One-Way Encryption"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;

#include "boostutil.h"
#include "encryption_oneway.h"
#include "CtiDate.h"



BOOST_AUTO_TEST_CASE(test_one_way_encryption)
{
    const unsigned char counter[] =
    {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    const unsigned char parentKey[] =
    {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
    };

    const CtiTime t(CtiDate(31, 12, 1999), 18, 0, 0);
    
    const unsigned int sequenceNo = 1;

    const unsigned char plainText[] =
    {
        0x53, 0x75, 0x70, 0x65, 0x72, 0x20, 0x53, 0x65,
        0x63, 0x72, 0x65, 0x74, 0x20, 0x44, 0x61, 0x74,
        0x61, 0x00
    };

    const unsigned char expectedCipherText[] =
    {
        0xd2, 0x1a, 0x3e, 0x1e, 0x2a, 0x5d, 0x3d, 0x22,
        0x28, 0x4f, 0xfc, 0xc5, 0xa0, 0x2e, 0x1e, 0x3d,
        0x17, 0xbf,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
        0x17, 0xfc, 0xb1, 0xdf
    };

    unsigned char cipherText[ sizeof( expectedCipherText ) ];

    OneWayEncryption  encryptor( counter, parentKey );

    encryptor.encrypt( t, sequenceNo, plainText, sizeof( plainText ), cipherText );

    BOOST_CHECK_EQUAL_COLLECTIONS( cipherText,          cipherText + sizeof( expectedCipherText ),
                                   expectedCipherText,  expectedCipherText + sizeof( expectedCipherText ) );
}

BOOST_AUTO_TEST_CASE(test_one_way_decryption)
{
    const unsigned char counter[] =
    {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    const unsigned char parentKey[] =
    {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
    };

    const unsigned char cipherText[] =
    {
        0xd2, 0x1a, 0x3e, 0x1e, 0x2a, 0x5d, 0x3d, 0x22,
        0x28, 0x4f, 0xfc, 0xc5, 0xa0, 0x2e, 0x1e, 0x3d,
        0x17, 0xbf,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
        0x17, 0xfc, 0xb1, 0xdf
    };

    const unsigned char expectedplainText[] =
    {
        0x53, 0x75, 0x70, 0x65, 0x72, 0x20, 0x53, 0x65,
        0x63, 0x72, 0x65, 0x74, 0x20, 0x44, 0x61, 0x74,
        0x61, 0x00
    };

    unsigned char plainText[ sizeof( expectedplainText ) ];

    std::fill( plainText, plainText + sizeof( expectedplainText ), 0xee );

    OneWayEncryption  decryptor( counter, parentKey );

    bool authenticated = decryptor.decrypt( cipherText, sizeof( cipherText ), plainText );

    BOOST_CHECK_EQUAL( authenticated, true );

    BOOST_CHECK_EQUAL_COLLECTIONS( plainText,           plainText + sizeof( expectedplainText ),
                                   expectedplainText,   expectedplainText + sizeof( expectedplainText ) );
}


BOOST_AUTO_TEST_CASE(test_one_way_encryption_and_decryption_on_random_data)
{
    const unsigned char counter[] =
    {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

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

