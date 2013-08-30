#include <words.h>

#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

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
        std::vector<unsigned char> expected = boost::assign::list_of
            (0xa0)(0x00)(0x00)(0x00)(0x00)(0x03)(0x10);

        std::vector<unsigned char> results;
        results.resize(7);

        BSTRUCT tmpBSt = BSt;

        B_Word(&results.front(), tmpBSt, 0);

        BOOST_CHECK_EQUAL_COLLECTIONS(
            expected.begin(), expected.end(),
            results.begin(), results.end());
    }
    {
        std::vector<unsigned char> expected = boost::assign::list_of
            (0xa0)(0x00)(0x00)(0x00)(0x0f)(0xe0)(0x90);

        std::vector<unsigned char> results;
        results.resize(7);

        BSTRUCT tmpBSt = BSt;

        tmpBSt.Function = 0x1fe;

        B_Word(&results.front(), tmpBSt, 0);

        BOOST_CHECK_EQUAL_COLLECTIONS(
            expected.begin(), expected.end(),
            results.begin(), results.end());
    }
}

BOOST_AUTO_TEST_SUITE_END()

