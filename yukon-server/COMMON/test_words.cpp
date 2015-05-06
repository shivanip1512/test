#include <words.h>

#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "boost_test_helpers.h"

namespace std {

    ostream &operator<<(ostream &o, unsigned char c)
    {
        return o << (unsigned)c;
    }
}


BOOST_AUTO_TEST_SUITE( test_words )

BOOST_AUTO_TEST_CASE(test_B_Word)
{
    BSTRUCT BSt;

    BSt.Address = 0;
    BSt.DlcRoute.Amp = 0;
    BSt.DlcRoute.Bus = 0;
    BSt.DlcRoute.RepFixed = 0;
    BSt.DlcRoute.RepVar = 0;
    BSt.DlcRoute.Stages = 0;
    BSt.Function = 0;
    BSt.IO = 0;
    BSt.Length = 0;
    std::fill(
        BSt.Message,
        BSt.Message + BSTRUCT::MessageLength_Max,
        0);
    BSt.Port = 0;
    BSt.Remote = 0;

    {
        const Cti::Test::byte_str expected =
            "a0 00 00 00 00 03 10";

        std::vector<unsigned char> results;
        results.resize(7);

        BSTRUCT tmpBSt = BSt;

        B_Word(&results.front(), tmpBSt, 0);

        BOOST_CHECK_EQUAL_RANGES(expected, results);
    }
    {
        const Cti::Test::byte_str expected =
            "a0 00 00 00 0f e0 90";

        std::vector<unsigned char> results;
        results.resize(7);

        BSTRUCT tmpBSt = BSt;

        tmpBSt.Function = 0x1fe;

        B_Word(&results.front(), tmpBSt, 0);

        BOOST_CHECK_EQUAL_RANGES(expected, results);
    }
}


BOOST_AUTO_TEST_CASE(test_D_Words)
{
    {
        const Cti::Test::byte_str inboundBWord =
                "af f2 6d c5 3c bc 40 41 "
                "04 8d 0f 43 30 0a b0 39 "
                "2a 55 64 fb 3f ff f0 39";

        DSTRUCT d;
        ESTRUCT e;
        BSTRUCT b;

        BOOST_CHECK_EQUAL(ClientErrors::BWordReceived, D_Words(inboundBWord.data(), inboundBWord.size(), 1, &d, &e, &b));

        BOOST_CHECK_EQUAL(636692, b.Address);
        BOOST_CHECK_EQUAL(203, b.Function);
        BOOST_CHECK_EQUAL(  3, b.IO);
        BOOST_CHECK_EQUAL(  3, b.Length);
        BOOST_CHECK_EQUAL( 31, b.DlcRoute.RepFixed);
        BOOST_CHECK_EQUAL(  7, b.DlcRoute.RepVar);
    }

    {
        const Cti::Test::byte_str badBchBword =
                "A0 0F CB 09 5F D5 70 39 "
                "66 98 3F 9C 0C 83 D0 39 "
                "AE CF 8F 0E 97 F0 20 39";

        DSTRUCT d;
        ESTRUCT e;
        BSTRUCT b;

        BOOST_CHECK_EQUAL(ClientErrors::Word1Nack, D_Words(badBchBword.data(), badBchBword.size(), 1, &d, &e, &b));
    }

    {
        const Cti::Test::byte_str lastWordNack =
                "DE 29 30 0B 6C 5F A0 41 "
                "D3 19 C0 43 30 03 30 41 "
                "55 14 02 64 A0 73 30 39";

        DSTRUCT d;
        ESTRUCT e;
        BSTRUCT b;

        BOOST_CHECK_EQUAL(ClientErrors::BadBch, D_Words(lastWordNack.data(), lastWordNack.size(), 1, &d, &e, &b));
    }

    {
        const Cti::Test::byte_str nackPadded =
                "B1 B1 B1 B1 B1 B1 B1 B1 "
                "B1 B1 B1 B1 B1 B1 B1 B1 "
                "B1 B1 B1 B1 B1 B1 B1 B1";

        DSTRUCT d;
        ESTRUCT e;
        BSTRUCT b;

        BOOST_CHECK_EQUAL(ClientErrors::Word1NackPadded, D_Words(nackPadded.data(), nackPadded.size(), 1, &d, &e, &b));
    }

    {
        const Cti::Test::byte_str dWords =
                "DF DD 55 D7 FF FF F0 39 "
                "FE A4 93 60 4C 80 10 39 "
                "2F 12 86 F0 00 03 00 39";

        DSTRUCT d;
        ESTRUCT e;
        BSTRUCT b;

        BOOST_CHECK_EQUAL(ClientErrors::Word1Nack, D_Words(dWords.data(), dWords.size(), 1, &d, &e, &b));
    }

    {
        const Cti::Test::byte_str dWords =
                "DE BD D3 01 20 9D 20 41 "
                "DB E0 04 83 FF C2 F0 41 "
                "D3 FF C0 00 00 02 10 41";

        DSTRUCT d;
        ESTRUCT e;
        BSTRUCT b;

        BOOST_CHECK_EQUAL(ClientErrors::None, D_Words(dWords.data(), dWords.size(), 1, &d, &e, &b));
    }

    {
         const Cti::Test::byte_str eWords =
                 "ED 6F E0 20 90 01 F0 41 "
                 "ED 6F E0 20 90 01 F0 41 "
                 "ED 6F E0 20 90 01 F0 41";

         DSTRUCT d;
         ESTRUCT e;
         BSTRUCT b;

         BOOST_CHECK_EQUAL(ClientErrors::EWordReceived, D_Words(eWords.data(), eWords.size(), 1, &d, &e, &b));

         BOOST_CHECK_EQUAL(e.echo_address, 5886);
         BOOST_CHECK_EQUAL(e.repeater_variable, 6);
         BOOST_CHECK( ! e.alarm);
         BOOST_CHECK( ! e.power_fail);
         BOOST_CHECK( ! e.diagnostics.incoming_bch_error);
         BOOST_CHECK(e.diagnostics.incoming_no_response);
         BOOST_CHECK( ! e.diagnostics.listen_ahead_bch_error);
         BOOST_CHECK( ! e.diagnostics.listen_ahead_no_response);
         BOOST_CHECK( ! e.diagnostics.repeater_code_mismatch);
         BOOST_CHECK( ! e.diagnostics.weak_signal);
    }
}

BOOST_AUTO_TEST_SUITE_END()

