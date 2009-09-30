/*
 * test CtiRouteCCU
 *
 */

#include "rte_ccu.h"
#include "devicetypes.h"

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test rte_ccu"
#include <boost/test/unit_test.hpp>

#include <limits>
#include <sstream>

#include "boostutil.h"

using boost::unit_test_framework::test_suite;

class test_CtiRouteCCU : public CtiRouteCCU
{
public:
    static void test_adjustStagesToFollow(unsigned short &stagesToFollow, unsigned &messageFlags, const bool isOneWayCcu711Request)
    {
        return CtiRouteCCU::adjustStagesToFollow(stagesToFollow, messageFlags, isOneWayCcu711Request);
    }
};

const unsigned MsgFlags_None = 0;
const unsigned MsgFlags_Ccu = MessageFlag_AddCcu711CooldownSilence;
const unsigned MsgFlags_Mct = MessageFlag_AddMctDisconnectSilence;
const unsigned MsgFlags_All = ~MsgFlags_None;

const bool isOneWay = true;

BOOST_AUTO_TEST_CASE(test_rte_ccu_adjustStagesToFollow)
{
    struct test_case
    {
        struct test_vars
        {
            unsigned short stagesToFollow;
            unsigned messageFlags;
            const bool oneWay;

        } tv;

        struct expected
        {
            const unsigned short stagesToFollow;
            const unsigned messageFlags;

        } ex;
    }
    test_matrix[] =
    {
        {{ 0, MsgFlags_None, false}, { 0, MsgFlags_None}},
        {{ 1, MsgFlags_None, false}, { 1, MsgFlags_None}},
        {{ 6, MsgFlags_None, false}, { 6, MsgFlags_None}},
        {{ 7, MsgFlags_None, false}, { 7, MsgFlags_None}},
        {{99, MsgFlags_None, false}, { 7, MsgFlags_None}},

        {{ 0, MsgFlags_Mct,  false}, { 0, MsgFlags_Mct}},
        {{ 1, MsgFlags_Mct,  false}, { 1, MsgFlags_Mct}},
        {{ 6, MsgFlags_Mct,  false}, { 6, MsgFlags_Mct}},
        {{ 7, MsgFlags_Mct,  false}, { 7, MsgFlags_Mct}},
        {{99, MsgFlags_Mct,  false}, { 7, MsgFlags_Mct}},

        {{ 0, MsgFlags_All,  false}, { 0, MsgFlags_All}},
        {{ 1, MsgFlags_All,  false}, { 1, MsgFlags_All}},
        {{ 6, MsgFlags_All,  false}, { 6, MsgFlags_All}},
        {{ 7, MsgFlags_All,  false}, { 7, MsgFlags_All}},
        {{99, MsgFlags_All,  false}, { 7, MsgFlags_All}},

        {{ 0, MsgFlags_None,  true}, { 1, MsgFlags_Ccu}},
        {{ 1, MsgFlags_None,  true}, { 1, MsgFlags_None}},
        {{ 6, MsgFlags_None,  true}, { 6, MsgFlags_None}},
        {{ 7, MsgFlags_None,  true}, { 7, MsgFlags_None}},
        {{99, MsgFlags_None,  true}, { 7, MsgFlags_None}},

        {{ 0, MsgFlags_Mct,   true}, { 2, MsgFlags_Mct}},
        {{ 1, MsgFlags_Mct,   true}, { 3, MsgFlags_Mct}},
        {{ 6, MsgFlags_Mct,   true}, { 7, MsgFlags_Mct}},
        {{ 7, MsgFlags_Mct,   true}, { 7, MsgFlags_Mct}},
        {{99, MsgFlags_Mct,   true}, { 7, MsgFlags_Mct}},

        {{ 0, MsgFlags_All,   true}, { 2, MsgFlags_All}},
        {{ 1, MsgFlags_All,   true}, { 3, MsgFlags_All}},
        {{ 6, MsgFlags_All,   true}, { 7, MsgFlags_All}},
        {{ 7, MsgFlags_All,   true}, { 7, MsgFlags_All}},
        {{99, MsgFlags_All,   true}, { 7, MsgFlags_All}}
    };

    const int num_test_cases = sizeof(test_matrix) / sizeof(test_case);

    for( int i = 0; i < num_test_cases; ++i )
    {
        test_CtiRouteCCU::test_adjustStagesToFollow(test_matrix[i].tv.stagesToFollow,
                                                    test_matrix[i].tv.messageFlags,
                                                    test_matrix[i].tv.oneWay);

        BOOST_CHECK_INDEXED_EQUAL(i, test_matrix[i].tv.stagesToFollow,
                                     test_matrix[i].ex.stagesToFollow);

        BOOST_CHECK_INDEXED_EQUAL(i, test_matrix[i].tv.messageFlags,
                                     test_matrix[i].ex.messageFlags);
    }
}

