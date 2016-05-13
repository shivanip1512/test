#include <boost/test/unit_test.hpp>

#include "encryption.h"



BOOST_AUTO_TEST_SUITE( test_encryption_common_core )

BOOST_AUTO_TEST_CASE(test_encryption)
{
    Cti::Encryption::initialize( "C:\\Yukon" );

    const unsigned char mockFileData[] =
    {
        0xf9, 0xa6, 0xd7, 0x2d, 0x72, 0x84, 0xcc, 0x51,
        0xd4, 0xb0, 0x37, 0x9a, 0xc3, 0x06, 0x59, 0x56,
        0x2d, 0x4d, 0xe9, 0x8f, 0x3b, 0x7c, 0xe4, 0x8f,
        0x13, 0xc6, 0x9f, 0x1e, 0xc5, 0xd9, 0x39, 0xcb,
        0x22, 0x43, 0x5b, 0x85, 0x11, 0x72, 0x4f, 0xe9,
        0x75, 0x3b, 0x0b, 0xa3, 0x5b, 0xf0, 0x0b, 0xea,
        0xed, 0x1f, 0x6c, 0x25, 0x03, 0x25, 0xc6, 0xdb,
        0x4f, 0xfe, 0x1a, 0x28, 0x60, 0x54, 0x74, 0xf9,
        0x6f, 0xc3, 0x03, 0x7d, 0xd5, 0xb2, 0x4b, 0xbd,
        0x72, 0x6b, 0x8b, 0x8a, 0x6a, 0x34, 0xd5, 0x63,
        0xb1, 0xc0, 0x65, 0x5e, 0x0e, 0xb6, 0x7c, 0x8c,
        0x03, 0x88, 0x83, 0x81, 0x0c, 0xa9, 0x2b, 0x37,
        0x25, 0x4d, 0x28, 0x20, 0xce, 0x81, 0xd2, 0x1a,
        0x7b, 0x8b, 0x04, 0xf7, 0x24, 0x9a, 0x18, 0xfd,
        0xb8, 0xe2, 0xda, 0x93, 0x04, 0x59, 0xbf, 0xef,
        0x38, 0xcb, 0x91, 0xd6, 0xb1, 0xb7, 0x2f, 0xa9,
        0xc6, 0xf3, 0x77, 0x3e, 0x87, 0x7b, 0xf7, 0xab,
        0x55, 0xec, 0x15, 0x1b, 0xd6, 0xbe, 0xd1, 0x3c,
        0xd4, 0x4a, 0x0a, 0x51, 0xe6, 0x54, 0x3a, 0x9e,
        0xa9, 0xc8, 0x65, 0x1b, 0x0e, 0x29, 0x4e, 0x13,
        0xc1, 0x4a, 0x02, 0xb3, 0x84, 0xe0, 0x3f, 0xf2,
        0x2d, 0xd2, 0x77, 0x4c, 0xfb, 0x27, 0x8d, 0x40
    };

    // Bypass the real encryption file

    Cti::Encryption::seedFileData( Cti::Encryption::MasterCfg,
                                   Cti::Encryption::Buffer( mockFileData, mockFileData + sizeof( mockFileData ) ) );

    // ciphertext is correct
    {
        const unsigned char cipherText[] =
        {
            0xcb, 0x8f, 0x58, 0x98, 0x00, 0xfb, 0x95, 0xff,
            0x42, 0x49, 0xe6, 0xf5, 0x56, 0x3d, 0x1a, 0x88,
            0xfb, 0x8c, 0x8d, 0x97, 0xa0, 0xd7, 0x97, 0xd5,
            0x67, 0x8e, 0x7d, 0xec, 0xda, 0x12, 0xf6, 0x1f,
            0xa2, 0xb3, 0x82, 0x42, 0x38, 0xc1, 0x56, 0x00,
            0x90, 0xe1, 0x57, 0x62, 0x04, 0x0d, 0xba, 0x19,
            0x04, 0x10, 0xc6, 0xaa, 0x88, 0x2f, 0x95, 0x9f,
            0xe6, 0x78, 0x44, 0x1f, 0xee, 0xa6, 0xd5, 0xc8
        };

        Cti::Encryption::Buffer encrypted( cipherText, cipherText + sizeof( cipherText ) );

        Cti::Encryption::Buffer plainText = Cti::Encryption::decrypt( Cti::Encryption::MasterCfg, encrypted );

        BOOST_REQUIRE_EQUAL( "127.0.0.1", std::string( plainText.begin(), plainText.end() ) );
    }

    // ciphertext has an error
    {
        const unsigned char cipherText[] =
        {
            0xcb, 0x8f, 0x58, 0x98, 0x00, 0xfb, 0x95, 0xff,
            0x42, 0x49, 0xe6, 0xf5, 0x56, 0x3d, 0x1a, 0x88,
            0xfb, 0x8c, 0x8d, 0x97, 0xa0, 0xd7, 0x97, 0xd5,
            0x67, 0x8e, 0x7d, 0xec, 0xda, 0x12, 0xf6, 0x1f,
            0xa2, 0xb3, 0x82, 0x42, 0x38, 0xc1, 0x56, 0x00,
            0x90, 0xe1, 0x57, 0x62, 0x04, 0x0d, 0xba, 0x19,
            0x04, 0x10, 0xc6, 0xaa, 0x88, 0x2f, 0x95, 0x9f,
            0xe6, 0x78, 0x44, 0x1f, 0xee, 0xa6, 0xd5, 0xc9      // <-- last bit is wrong
        };

        Cti::Encryption::Buffer encrypted( cipherText, cipherText + sizeof( cipherText ) );

        Cti::Encryption::Buffer plainText;

        BOOST_REQUIRE_THROW( plainText = Cti::Encryption::decrypt( Cti::Encryption::MasterCfg, encrypted ), Cti::Encryption::Error );
    }

    // Re-enable the real ecryption file

    Cti::Encryption::unseedFileData( Cti::Encryption::MasterCfg );

    Cti::Encryption::cleanup();
}

BOOST_AUTO_TEST_SUITE_END()

