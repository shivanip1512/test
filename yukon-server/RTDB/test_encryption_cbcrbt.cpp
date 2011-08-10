#define BOOST_AUTO_TEST_MAIN "Test CBC-RBT Mode Encryption"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;

#include "boostutil.h"
#include "encryption_cbcrbt.h"

#include <cstddef>
#include <cstdlib>



BOOST_AUTO_TEST_CASE(test_cbcrbt_encryption)
{
    const unsigned char iv[] = 
    {
        0x69, 0x50, 0x37, 0x98, 0xdd, 0xf2, 0x8e, 0xe3,
        0xfa, 0x23, 0x58, 0xa5, 0xab, 0x9d, 0xef, 0x30
    };

    const unsigned char parentKey[] =
    {
        0xc3, 0x52, 0x80, 0x57, 0x54, 0x23, 0x7f, 0x31,
        0x1a, 0xc0, 0xff, 0xf4, 0xe3, 0xe0, 0x3e, 0x78
    };

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
        0x17, 0xbf
    };

    unsigned char cipherText[ sizeof( plainText ) ];


    CbcRbtEncryption    encryptor( parentKey, iv );
    
    encryptor.encrypt( plainText, sizeof( plainText ), cipherText );

    BOOST_CHECK_EQUAL_COLLECTIONS( cipherText,          cipherText + sizeof( plainText ),
                                   expectedCipherText,  expectedCipherText + sizeof( plainText ) );
}


BOOST_AUTO_TEST_CASE(test_cbcrbt_decryption)
{
    const unsigned char iv[] = 
    {
        0x69, 0x50, 0x37, 0x98, 0xdd, 0xf2, 0x8e, 0xe3,
        0xfa, 0x23, 0x58, 0xa5, 0xab, 0x9d, 0xef, 0x30
    };

    const unsigned char parentKey[] =
    {
        0xc3, 0x52, 0x80, 0x57, 0x54, 0x23, 0x7f, 0x31,
        0x1a, 0xc0, 0xff, 0xf4, 0xe3, 0xe0, 0x3e, 0x78
    };

    const unsigned char cipherText[] =
    {
        0xd2, 0x1a, 0x3e, 0x1e, 0x2a, 0x5d, 0x3d, 0x22,
        0x28, 0x4f, 0xfc, 0xc5, 0xa0, 0x2e, 0x1e, 0x3d,
        0x17, 0xbf
    };

    const unsigned char expectedPlainText[] =
    {
        0x53, 0x75, 0x70, 0x65, 0x72, 0x20, 0x53, 0x65,
        0x63, 0x72, 0x65, 0x74, 0x20, 0x44, 0x61, 0x74,
        0x61, 0x00
    };

    unsigned char plainText[ sizeof( cipherText ) ];


    CbcRbtEncryption    decryptor( parentKey, iv );
    
    decryptor.decrypt( cipherText, sizeof( cipherText ), plainText );

    BOOST_CHECK_EQUAL_COLLECTIONS( plainText,           plainText + sizeof( cipherText ),
                                   expectedPlainText,   expectedPlainText + sizeof( cipherText ) );
}


BOOST_AUTO_TEST_CASE(test_cbcrbt_encryption_and_decryption_on_random_data)
{
    const unsigned char iv[] = 
    {
        0x69, 0x50, 0x37, 0x98, 0xdd, 0xf2, 0x8e, 0xe3,
        0xfa, 0x23, 0x58, 0xa5, 0xab, 0x9d, 0xef, 0x30
    };

    const unsigned char parentKey[] =
    {
        0xc3, 0x52, 0x80, 0x57, 0x54, 0x23, 0x7f, 0x31,
        0x1a, 0xc0, 0xff, 0xf4, 0xe3, 0xe0, 0x3e, 0x78
    };

    const std::size_t   plainTextLength = 512;

    unsigned char plainText[plainTextLength];
    unsigned char cipherText[plainTextLength];
    unsigned char expectedPlainText[plainTextLength];

    // fill plainText with "random" data ....

    std::srand( plainTextLength );
    for ( int i = 0; i < plainTextLength; ++i )
    {
        plainText[i] = std::rand() & 0x0ff;
    }

    CbcRbtEncryption    encryptor( parentKey, iv );

    for ( int length = 0; length < plainTextLength; ++length )
    {
        encryptor.encrypt( plainText, length, cipherText );
        
        encryptor.decrypt( cipherText, length, expectedPlainText );        

        BOOST_CHECK_EQUAL_COLLECTIONS( plainText,           plainText + length,
                                       expectedPlainText,   expectedPlainText + length );
    }
}

