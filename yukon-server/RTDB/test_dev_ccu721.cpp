/*
 * test CtiDeviceCCU721
 *
 */

#include <boost/test/unit_test.hpp>

#define _WIN32_WINNT 0x0400

#include <iostream>
#include "dev_ccu721.h"
#include "prot_emetcon.h"

#define BOOST_AUTO_TEST_MAIN "Test CCU-721 Device"
#include <boost/test/auto_unit_test.hpp>
using boost::unit_test_framework::test_suite;

using namespace std;

class Test_CCU721 : public Cti::Device::CCU721
{
private:
    typedef Cti::Device::CCU721 Inherited;
public:
    typedef Inherited::byte_buffer_t byte_buffer_t;

    void writeBWord(byte_buffer_t &buf, const BSTRUCT &BSt)
    {
        Inherited::writeBWord(buf, BSt);
    }
};

BOOST_AUTO_UNIT_TEST(test_ccu721_bword)
{
    Test_CCU721 dev;

    Test_CCU721::byte_buffer_t buf, expected;

    BSTRUCT BSt;

    BSt.Address = 12345;
    BSt.DlcRoute.Amp = 1;
    BSt.DlcRoute.Bus = 1;
    BSt.DlcRoute.RepFixed = 1;
    BSt.DlcRoute.RepVar   = 1;
    BSt.DlcRoute.Stages   = 1;
    BSt.Function = 1;
    BSt.IO = Cti::Protocol::Emetcon::IO_Write;
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
    // BSt.Port = 1;
    // BSt.Remote = 1;

    dev.writeBWord(buf, BSt);

    cout.setf(ios::hex, ios::basefield);
    cout.width(2);

    char *result1 = "\x1c"
                    "\xa2\x10\x0c\x0e\x70\x10\x00"
                    "\xc1\x22\x33\x44\x55\x62\xb0"
                    "\xc6\x77\x88\x99\xaa\xb1\x40"
                    "\xcb\xcc\xdd\xee\xff\x01\xb0";

    expected.assign(reinterpret_cast<unsigned char *>(result1),
                    reinterpret_cast<unsigned char *>(result1) + 7 * 4 + 1);

    // copy(buf.begin(), buf.end(), ostream_iterator<int>(cout, " "));

    BOOST_CHECK(!memcmp(buf.begin(), expected.begin(), 29));
}

