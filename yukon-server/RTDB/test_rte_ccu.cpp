#include <boost/test/unit_test.hpp>

#include "rte_ccu.h"
#include "devicetypes.h"

BOOST_AUTO_TEST_SUITE( test_rte_ccu )

const unsigned MsgFlags_None = 0;
const unsigned MsgFlags_Ccu = MessageFlag_AddCcu711CooldownSilence;
const unsigned MsgFlags_Mct = MessageFlag_AddMctDisconnectSilence;
const unsigned MsgFlags_All = ~MsgFlags_None;

BOOST_AUTO_TEST_CASE(test_rte_ccu_adjustStagesToFollow)
{
    struct test_case
    {
        struct test_vars
        {
            unsigned short stagesToFollow;
            unsigned messageFlags;
            const int type;

        } tv;

        struct expected
        {
            const unsigned short stagesToFollow;
            const unsigned messageFlags;

        } ex;
    }
    test_matrix[] =
    {
        //  Test CCU-710
        {{ 0, MsgFlags_None, TYPE_CCU710}, { 0, MsgFlags_None}},  //  0
        {{ 1, MsgFlags_None, TYPE_CCU710}, { 1, MsgFlags_None}},
        {{ 6, MsgFlags_None, TYPE_CCU710}, { 6, MsgFlags_None}},
        {{ 7, MsgFlags_None, TYPE_CCU710}, { 7, MsgFlags_None}},
        {{99, MsgFlags_None, TYPE_CCU710}, { 7, MsgFlags_None}},

        {{ 0, MsgFlags_Mct,  TYPE_CCU710}, { 0, MsgFlags_Mct}},
        {{ 1, MsgFlags_Mct,  TYPE_CCU710}, { 1, MsgFlags_Mct}},
        {{ 6, MsgFlags_Mct,  TYPE_CCU710}, { 6, MsgFlags_Mct}},
        {{ 7, MsgFlags_Mct,  TYPE_CCU710}, { 7, MsgFlags_Mct}},
        {{99, MsgFlags_Mct,  TYPE_CCU710}, { 7, MsgFlags_Mct}},

        {{ 0, MsgFlags_All,  TYPE_CCU710}, { 0, MsgFlags_All}},  //  10
        {{ 1, MsgFlags_All,  TYPE_CCU710}, { 1, MsgFlags_All}},
        {{ 6, MsgFlags_All,  TYPE_CCU710}, { 6, MsgFlags_All}},
        {{ 7, MsgFlags_All,  TYPE_CCU710}, { 7, MsgFlags_All}},
        {{99, MsgFlags_All,  TYPE_CCU710}, { 7, MsgFlags_All}},

        //  Test CCU-711
        {{ 0, MsgFlags_None, TYPE_CCU711}, { 1, MsgFlags_Ccu}},
        {{ 1, MsgFlags_None, TYPE_CCU711}, { 1, MsgFlags_None}},
        {{ 6, MsgFlags_None, TYPE_CCU711}, { 6, MsgFlags_None}},
        {{ 7, MsgFlags_None, TYPE_CCU711}, { 7, MsgFlags_None}},
        {{99, MsgFlags_None, TYPE_CCU711}, { 7, MsgFlags_None}},

        {{ 0, MsgFlags_Mct,  TYPE_CCU711}, { 2, MsgFlags_Mct}},  //  20
        {{ 1, MsgFlags_Mct,  TYPE_CCU711}, { 3, MsgFlags_Mct}},
        {{ 6, MsgFlags_Mct,  TYPE_CCU711}, { 7, MsgFlags_Mct}},
        {{ 7, MsgFlags_Mct,  TYPE_CCU711}, { 7, MsgFlags_Mct}},
        {{99, MsgFlags_Mct,  TYPE_CCU711}, { 7, MsgFlags_Mct}},

        {{ 0, MsgFlags_All,  TYPE_CCU711}, { 2, MsgFlags_All}},
        {{ 1, MsgFlags_All,  TYPE_CCU711}, { 3, MsgFlags_All}},
        {{ 6, MsgFlags_All,  TYPE_CCU711}, { 7, MsgFlags_All}},
        {{ 7, MsgFlags_All,  TYPE_CCU711}, { 7, MsgFlags_All}},
        {{99, MsgFlags_All,  TYPE_CCU711}, { 7, MsgFlags_All}},

        //  Test CCU-721
        {{ 0, MsgFlags_None, TYPE_CCU721}, { 0, MsgFlags_None}},  //  30
        {{ 1, MsgFlags_None, TYPE_CCU721}, { 1, MsgFlags_None}},
        {{ 6, MsgFlags_None, TYPE_CCU721}, { 6, MsgFlags_None}},
        {{ 7, MsgFlags_None, TYPE_CCU721}, { 7, MsgFlags_None}},
        {{99, MsgFlags_None, TYPE_CCU721}, { 7, MsgFlags_None}},

        {{ 0, MsgFlags_Mct,  TYPE_CCU721}, { 2, MsgFlags_Mct}},
        {{ 1, MsgFlags_Mct,  TYPE_CCU721}, { 3, MsgFlags_Mct}},
        {{ 6, MsgFlags_Mct,  TYPE_CCU721}, { 7, MsgFlags_Mct}},
        {{ 7, MsgFlags_Mct,  TYPE_CCU721}, { 7, MsgFlags_Mct}},
        {{99, MsgFlags_Mct,  TYPE_CCU721}, { 7, MsgFlags_Mct}},

        {{ 0, MsgFlags_All,  TYPE_CCU721}, { 2, MsgFlags_All}},  //  40
        {{ 1, MsgFlags_All,  TYPE_CCU721}, { 3, MsgFlags_All}},
        {{ 6, MsgFlags_All,  TYPE_CCU721}, { 7, MsgFlags_All}},
        {{ 7, MsgFlags_All,  TYPE_CCU721}, { 7, MsgFlags_All}},
        {{99, MsgFlags_All,  TYPE_CCU721}, { 7, MsgFlags_All}}
    };

    struct test_CtiRouteCCU : CtiRouteCCU
    {
        using CtiRouteCCU::adjustOutboundStagesToFollow;
    };

    std::vector<unsigned short> expected_stagesToFollow, results_stagesToFollow;
    std::vector<unsigned> expected_messageFlags, results_messageFlags;

    for each( test_case tc in test_matrix )
    {
        expected_stagesToFollow.push_back(tc.ex.stagesToFollow);
        expected_messageFlags.push_back(tc.ex.messageFlags);

        test_CtiRouteCCU::adjustOutboundStagesToFollow(
            tc.tv.stagesToFollow,
            tc.tv.messageFlags,
            tc.tv.type);

        results_stagesToFollow.push_back(tc.tv.stagesToFollow);
        results_messageFlags.push_back(tc.tv.messageFlags);
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected_stagesToFollow.begin(), expected_stagesToFollow.end(),
        results_stagesToFollow.begin(), results_stagesToFollow.end());

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected_messageFlags.begin(), expected_messageFlags.end(),
        results_messageFlags.begin(), results_messageFlags.end());
}

BOOST_AUTO_TEST_SUITE_END()

