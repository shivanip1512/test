/*---------------------------------------------------------------------------
        Filename:  test_LantronixEncryption.cpp

        Programmer:  Thain Spar

        Initial Date:  10/10/2008

        COPYRIGHT:  Copyright (C) Cannon Technologies 2007
---------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test Lantronix Encryption"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>

#include "yukon.h"
#include "encryption_lantronix.h"
#include "numstr.h"

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_TEST_CASE(decode_from_debug_mode_encoding)
{
    const int encoded_size   = 66;
    const int plaintext_size = 42;

    //  The first 16 bytes are the IV, followed by 2 for the length, and 48 for the encoded blocks.
    const unsigned char encoded[]   = {0xed, 0x3d, 0x13, 0xc9, 0x39, 0x54, 0x5f, 0xdf, 0x30, 0x08, 0x56, 0x93, 0x7c, 0x1e, 0xa2, 0xa9,
                                       0x00, 0x2a,
                                       0xee, 0xdc, 0x56, 0x3e, 0x9a, 0xa7, 0x34, 0xeb, 0xab, 0x4f, 0x9b, 0x0d, 0x69, 0xb3, 0xa4, 0xe5,
                                       0x17, 0x1a, 0x57, 0xd7, 0xf6, 0xbe, 0xbf, 0xdb, 0x72, 0xb0, 0x85, 0xd5, 0x40, 0xd0, 0x5d, 0x23,
                                       0x4e, 0x61, 0x2d, 0x3e, 0x71, 0x63, 0xa0, 0xa1, 0xf1, 0x68, 0x78, 0xbf, 0xd1, 0xe8, 0x3b, 0x6f};

    const unsigned char plaintext[] = {0x05, 0x64, 0x21, 0x44, 0x01, 0x00, 0x62, 0x00,
                                       0xf6, 0xea, 0xc0, 0xf3, 0x82, 0x90, 0x00, 0x02,
                                       0x01, 0x17, 0x01, 0x04, 0x81, 0x20, 0x02, 0x18,
                                       0x03, 0x00, 0x16, 0x2a, 0x04, 0x01, 0x69, 0x04,
                                       0x05, 0x01, 0xb5, 0x04, 0x06, 0x01, 0x63, 0x04, 0xef, 0x8c};

    string key("0e0e0e0e0e0e0e0e0e0e0e0e0e0e0e0e");

    LantronixEncryptionImpl *filter = new LantronixEncryptionImpl();

    filter->setKey(key);

    vector<unsigned char> decoded;

    bool ret = filter->decode(encoded, encoded_size, decoded);

    BOOST_REQUIRE(ret == true);

    BOOST_REQUIRE_EQUAL(plaintext_size, decoded.size());

    BOOST_REQUIRE(equal(plaintext, plaintext + plaintext_size, decoded.begin()));

    delete filter;
}

BOOST_AUTO_TEST_CASE(decode_from_release_mode_encoding)
{
    const int encoded_size   = 66;
    const int plaintext_size = 42;

    //  The first 16 bytes are the IV, followed by 2 for the length, and 48 for the encoded blocks.
    const unsigned char encoded[]   = {0xed, 0x3d, 0x13, 0xc9, 0x39, 0x54, 0x5f, 0xdf, 0x30, 0x08, 0x56, 0x93, 0x7c, 0x1e, 0xa2, 0xa9,
                                       0x00, 0x2a,
                                       0xee, 0xdc, 0x56, 0x3e, 0x9a, 0xa7, 0x34, 0xeb, 0xab, 0x4f, 0x9b, 0x0d, 0x69, 0xb3, 0xa4, 0xe5,
                                       0x17, 0x1a, 0x57, 0xd7, 0xf6, 0xbe, 0xbf, 0xdb, 0x72, 0xb0, 0x85, 0xd5, 0x40, 0xd0, 0x5d, 0x23,
                                       0xad, 0x4d, 0xf7, 0x13, 0xc2, 0xfc, 0x4f, 0x79, 0x16, 0xc5, 0x87, 0x88, 0x09, 0xd7, 0x69, 0xe8};

    const unsigned char plaintext[] = {0x05, 0x64, 0x21, 0x44, 0x01, 0x00, 0x62, 0x00,
                                       0xf6, 0xea, 0xc0, 0xf3, 0x82, 0x90, 0x00, 0x02,
                                       0x01, 0x17, 0x01, 0x04, 0x81, 0x20, 0x02, 0x18,
                                       0x03, 0x00, 0x16, 0x2a, 0x04, 0x01, 0x69, 0x04,
                                       0x05, 0x01, 0xb5, 0x04, 0x06, 0x01, 0x63, 0x04, 0xef, 0x8c};

    const string key("0e0e0e0e0e0e0e0e0e0e0e0e0e0e0e0e");

    LantronixEncryptionImpl *filter = new LantronixEncryptionImpl();

    filter->setKey(key);

    vector<unsigned char> decoded;

    bool ret = filter->decode(encoded, encoded_size, decoded);

    BOOST_REQUIRE(ret == true);

    BOOST_REQUIRE_EQUAL(plaintext_size, decoded.size());

    BOOST_REQUIRE(equal(plaintext, plaintext + plaintext_size, decoded.begin()));

    delete filter;
}

BOOST_AUTO_TEST_CASE(round_trip_encrypt_decrypt)
{
    const int encoded_size   = 66;
    const int plaintext_size = 42;

    const unsigned char plaintext[] = {0x05, 0x64, 0x21, 0x44, 0x01, 0x00, 0x62, 0x00,
                                       0xf6, 0xea, 0xc0, 0xf3, 0x82, 0x90, 0x00, 0x02,
                                       0x01, 0x17, 0x01, 0x04, 0x81, 0x20, 0x02, 0x18,
                                       0x03, 0x00, 0x16, 0x2a, 0x04, 0x01, 0x69, 0x04,
                                       0x05, 0x01, 0xb5, 0x04, 0x06, 0x01, 0x63, 0x04, 0xef, 0x8c};

    const string key("0e0e0e0e0e0e0e0e0e0e0e0e0e0e0e0e");

    vector<unsigned char> encoded;

    {
        LantronixEncryptionImpl *filter = new LantronixEncryptionImpl();

        filter->setKey(key);

        bool ret = filter->encode(plaintext, plaintext_size, encoded);

        BOOST_REQUIRE(ret == true);

        BOOST_REQUIRE_EQUAL(encoded.size(), encoded_size);

        delete filter;
    }

    //  create a brand new filter for the decode
    {
        LantronixEncryptionImpl *filter = new LantronixEncryptionImpl();

        filter->setKey(key);

        vector<unsigned char> decoded;

        bool ret = filter->decode(&encoded.front(), encoded.size(), decoded);

        BOOST_REQUIRE(ret == true);

        BOOST_REQUIRE_EQUAL(plaintext_size, decoded.size());

        BOOST_REQUIRE(equal(plaintext, plaintext + plaintext_size, decoded.begin()));

        delete filter;
    }
}

BOOST_AUTO_TEST_CASE(test_setKey)
{
    const unsigned char *result;
    const string sKey("0e0e0e0e0e0e0e0e0e0e0e0e0e0e0e0e");

    LantronixEncryptionImpl* filter = new LantronixEncryptionImpl();
    filter->setKey(sKey);

    result = filter->getKey();
    for (int i = 0; i < 16; ++i)
    {
        BOOST_CHECK_EQUAL((int)result[i],14);
    }

    delete filter;
}

BOOST_AUTO_TEST_CASE(test_setIV)
{
    const unsigned char origIV[] = {0xed, 0x3d, 0x13, 0xc9, 0x39, 0x54, 0x5f, 0xdf, 0x30, 0x08, 0x56, 0x93, 0x7c, 0x1e, 0xa2, 0xa9};
    const unsigned char *result;

    LantronixEncryptionImpl* filter = new LantronixEncryptionImpl();
    filter->setIV(origIV);

    result = filter->getIV();
    for (int i = 0; i < 16; ++i)
    {
        BOOST_CHECK_EQUAL(result[i],origIV[i]);
    }

    delete filter;
}
