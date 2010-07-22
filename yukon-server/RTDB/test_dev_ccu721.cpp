/*
 * test Ccu721Device
 *
 */

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test dev_ccu721"
#include <boost/test/unit_test.hpp>


#include <iostream>
#include "dev_ccu721.h"
#include "prot_emetcon.h"

#define BOOST_AUTO_TEST_MAIN "Test CCU-721 Device"
using boost::unit_test_framework::test_suite;

using namespace std;

using Cti::Devices::Ccu721Device;

struct Test_Ccu721Device : Ccu721Device
{
    typedef Ccu721Device Inherited;

    typedef Inherited::byte_buffer_t byte_buffer_t;

    using Ccu721Device::writeBWord;
    using Ccu721Device::decodeEWord;
};

BOOST_AUTO_TEST_CASE(test_ccu721_bword)
{
    BSTRUCT BSt;

    BSt.Address = 12345;
    BSt.DlcRoute.Amp = 1;
    BSt.DlcRoute.Bus = 1;
    BSt.DlcRoute.RepFixed = 1;
    BSt.DlcRoute.RepVar   = 1;
    BSt.DlcRoute.Stages   = 1;
    BSt.Function = 1;
    BSt.IO = Cti::Protocols::EmetconProtocol::IO_Write;
    BSt.Length = 15;
    BSt.Message[ 0] = 0x12;
    BSt.Message[ 1] = 0x23;
    BSt.Message[ 2] = 0x34;
    BSt.Message[ 3] = 0x45;
    BSt.Message[ 4] = 0x56;
    BSt.Message[ 5] = 0x67;
    BSt.Message[ 6] = 0x78;
    BSt.Message[ 7] = 0x89;
    BSt.Message[ 8] = 0x9a;
    BSt.Message[ 9] = 0xab;
    BSt.Message[10] = 0xbc;
    BSt.Message[11] = 0xcd;
    BSt.Message[12] = 0xde;
    BSt.Message[13] = 0xef;
    BSt.Message[14] = 0xf0;

    {
        Test_Ccu721Device::byte_buffer_t buf, expected;

        Test_Ccu721Device::writeBWord(buf, BSt);

        char *result = "\xa2\x10\x0c\x0e\x70\x10\x00"
                       "\xc1\x22\x33\x44\x55\x62\xb0"
                       "\xc6\x77\x88\x99\xaa\xb1\x40"
                       "\xcb\xcc\xdd\xee\xff\x01\xb0";

        expected.assign(reinterpret_cast<unsigned char *>(result),
                        reinterpret_cast<unsigned char *>(result) + 7 * 4);

        BOOST_CHECK_EQUAL(28, buf.size());

        for( int i = 0; i < 7 * 4; ++i )
        {
            BOOST_CHECK_INDEXED_EQUAL(i, buf[i], expected[i]);
        }
    }

    BSt.Length = 0;

    {
        Test_Ccu721Device::byte_buffer_t buf, expected;

        Test_Ccu721Device::writeBWord(buf, BSt);

        char *result = "\xa2\x10\x0c\x0e\x40\x13\x50";

        expected.assign(reinterpret_cast<unsigned char *>(result),
                        reinterpret_cast<unsigned char *>(result) + 7 * 4);

        BOOST_CHECK_EQUAL(7, buf.size());

        for( int i = 0; i < 7 * 1; ++i )
        {
            BOOST_CHECK_INDEXED_EQUAL(i, buf[i], expected[i]);
        }
    }
}


BOOST_AUTO_TEST_CASE(test_ccu721_decode_eword)
{
    {
        const char *e_word = "\xee\x00\x00\x20\x00\x02\xb0";

        BOOST_CHECK_EQUAL(NACKPAD1, Test_Ccu721Device::decodeEWord(reinterpret_cast<const unsigned char *>(e_word), 7));
    }

    {
        const char *e_word = "\xee\x00\x00\x20\x00\x02\xc0";

        BOOST_CHECK_EQUAL(BADBCH, Test_Ccu721Device::decodeEWord(reinterpret_cast<const unsigned char *>(e_word), 7));
    }

    {
        const char *e_word = "\xfe\x00\x00\x20\x00\x02\x60";

        BOOST_CHECK_EQUAL(BADTYPE, Test_Ccu721Device::decodeEWord(reinterpret_cast<const unsigned char *>(e_word), 7));
    }

    {
        const char *e_word = "\xed\x11\x23\x45\x00\x03\xc0";

        BOOST_CHECK_EQUAL(EWORDRCV, Test_Ccu721Device::decodeEWord(reinterpret_cast<const unsigned char *>(e_word), 7));
    }
}

