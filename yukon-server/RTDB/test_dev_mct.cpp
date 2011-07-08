/*
 * test MctDevice
 *
 */

#include "dev_mct.h"
#include "ctidate.h"

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test dev_mct"
#include <boost/test/unit_test.hpp>

#include <limits>

using boost::unit_test_framework::test_suite;

using Cti::Devices::MctDevice;

struct test_MctDevice : MctDevice
{
    using MctDevice::findLastScheduledFreeze;
};

struct freeze_day_check
{
    unsigned freeze_day;
    CtiTime time_now;
    CtiTime expected;
};

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_0)
{
    freeze_day_check fd[30] =
        {
            {  0, CtiDate(30, 12, 2007), CtiDate(1, 1, 1970)},
            {  0, CtiDate(31, 12, 2007), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 1,  1, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 2,  1, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 3,  1, 2008), CtiDate(1, 1, 1970)},

            {  0, CtiDate(13,  1, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate(14,  1, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate(15,  1, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate(16,  1, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate(17,  1, 2008), CtiDate(1, 1, 1970)},

            {  0, CtiDate(30,  1, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate(31,  1, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 1,  2, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 2,  2, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 3,  2, 2008), CtiDate(1, 1, 1970)},

            //  leap year
            {  0, CtiDate(28,  2, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate(29,  2, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 1,  3, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 2,  3, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 3,  3, 2008), CtiDate(1, 1, 1970)},

            //  non-leap year
            {  0, CtiDate(27,  2, 2009), CtiDate(1, 1, 1970)},
            {  0, CtiDate(28,  2, 2009), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 1,  3, 2009), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 2,  3, 2009), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 3,  3, 2009), CtiDate(1, 1, 1970)},

            {  0, CtiDate(29,  4, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate(30,  4, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 1,  5, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 2,  5, 2008), CtiDate(1, 1, 1970)},
            {  0, CtiDate( 3,  5, 2008), CtiDate(1, 1, 1970)},
        };

    //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_1)
{
    freeze_day_check fd[60] =
        {
            {  1, CtiDate(27, 12, 2007), CtiDate( 2, 12, 2007)},
            {  1, CtiDate(28, 12, 2007), CtiDate( 2, 12, 2007)},
            {  1, CtiDate(29, 12, 2007), CtiDate( 2, 12, 2007)},
            {  1, CtiDate(30, 12, 2007), CtiDate( 2, 12, 2007)},
            {  1, CtiDate(31, 12, 2007), CtiDate( 2, 12, 2007)},
            {  1, CtiDate( 1,  1, 2008), CtiDate( 2, 12, 2007)},
            {  1, CtiDate( 2,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate( 3,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate( 4,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate( 5,  1, 2008), CtiDate( 2,  1, 2008)},

            {  1, CtiDate(10,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(11,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(12,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(13,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(14,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(15,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(16,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(17,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(18,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(19,  1, 2008), CtiDate( 2,  1, 2008)},

            {  1, CtiDate(27,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(28,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(29,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(30,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate(31,  1, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate( 1,  2, 2008), CtiDate( 2,  1, 2008)},
            {  1, CtiDate( 2,  2, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate( 3,  2, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate( 4,  2, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate( 5,  2, 2008), CtiDate( 2,  2, 2008)},

            //  leap year
            {  1, CtiDate(25,  2, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate(26,  2, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate(27,  2, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate(28,  2, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate(29,  2, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate( 1,  3, 2008), CtiDate( 2,  2, 2008)},
            {  1, CtiDate( 2,  3, 2008), CtiDate( 2,  3, 2008)},
            {  1, CtiDate( 3,  3, 2008), CtiDate( 2,  3, 2008)},
            {  1, CtiDate( 4,  3, 2008), CtiDate( 2,  3, 2008)},
            {  1, CtiDate( 5,  3, 2008), CtiDate( 2,  3, 2008)},

            //  non-leap year
            {  1, CtiDate(24,  2, 2009), CtiDate( 2,  2, 2009)},
            {  1, CtiDate(25,  2, 2009), CtiDate( 2,  2, 2009)},
            {  1, CtiDate(26,  2, 2009), CtiDate( 2,  2, 2009)},
            {  1, CtiDate(27,  2, 2009), CtiDate( 2,  2, 2009)},
            {  1, CtiDate(28,  2, 2009), CtiDate( 2,  2, 2009)},
            {  1, CtiDate( 1,  3, 2009), CtiDate( 2,  2, 2009)},
            {  1, CtiDate( 2,  3, 2009), CtiDate( 2,  3, 2009)},
            {  1, CtiDate( 3,  3, 2009), CtiDate( 2,  3, 2009)},
            {  1, CtiDate( 4,  3, 2009), CtiDate( 2,  3, 2009)},
            {  1, CtiDate( 5,  3, 2009), CtiDate( 2,  3, 2009)},

            {  1, CtiDate(26,  4, 2008), CtiDate( 2,  4, 2008)},
            {  1, CtiDate(27,  4, 2008), CtiDate( 2,  4, 2008)},
            {  1, CtiDate(28,  4, 2008), CtiDate( 2,  4, 2008)},
            {  1, CtiDate(29,  4, 2008), CtiDate( 2,  4, 2008)},
            {  1, CtiDate(30,  4, 2008), CtiDate( 2,  4, 2008)},
            {  1, CtiDate( 1,  5, 2008), CtiDate( 2,  4, 2008)},
            {  1, CtiDate( 2,  5, 2008), CtiDate( 2,  5, 2008)},
            {  1, CtiDate( 3,  5, 2008), CtiDate( 2,  5, 2008)},
            {  1, CtiDate( 4,  5, 2008), CtiDate( 2,  5, 2008)},
            {  1, CtiDate( 5,  5, 2008), CtiDate( 2,  5, 2008)},
        };

    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_2)
{
    freeze_day_check fd[60] =
        {
            {  2, CtiDate(27, 12, 2007), CtiDate( 3, 12, 2007)},
            {  2, CtiDate(28, 12, 2007), CtiDate( 3, 12, 2007)},
            {  2, CtiDate(29, 12, 2007), CtiDate( 3, 12, 2007)},
            {  2, CtiDate(30, 12, 2007), CtiDate( 3, 12, 2007)},
            {  2, CtiDate(31, 12, 2007), CtiDate( 3, 12, 2007)},
            {  2, CtiDate( 1,  1, 2008), CtiDate( 3, 12, 2007)},
            {  2, CtiDate( 2,  1, 2008), CtiDate( 3, 12, 2007)},
            {  2, CtiDate( 3,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate( 4,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate( 5,  1, 2008), CtiDate( 3,  1, 2008)},

            {  2, CtiDate(10,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(11,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(12,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(13,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(14,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(15,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(16,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(17,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(18,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(19,  1, 2008), CtiDate( 3,  1, 2008)},

            {  2, CtiDate(27,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(28,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(29,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(30,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate(31,  1, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate( 1,  2, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate( 2,  2, 2008), CtiDate( 3,  1, 2008)},
            {  2, CtiDate( 3,  2, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate( 4,  2, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate( 5,  2, 2008), CtiDate( 3,  2, 2008)},

            //  leap year
            {  2, CtiDate(25,  2, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate(26,  2, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate(27,  2, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate(28,  2, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate(29,  2, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate( 1,  3, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate( 2,  3, 2008), CtiDate( 3,  2, 2008)},
            {  2, CtiDate( 3,  3, 2008), CtiDate( 3,  3, 2008)},
            {  2, CtiDate( 4,  3, 2008), CtiDate( 3,  3, 2008)},
            {  2, CtiDate( 5,  3, 2008), CtiDate( 3,  3, 2008)},

            //  non-leap year
            {  2, CtiDate(24,  2, 2009), CtiDate( 3,  2, 2009)},
            {  2, CtiDate(25,  2, 2009), CtiDate( 3,  2, 2009)},
            {  2, CtiDate(26,  2, 2009), CtiDate( 3,  2, 2009)},
            {  2, CtiDate(27,  2, 2009), CtiDate( 3,  2, 2009)},
            {  2, CtiDate(28,  2, 2009), CtiDate( 3,  2, 2009)},
            {  2, CtiDate( 1,  3, 2009), CtiDate( 3,  2, 2009)},
            {  2, CtiDate( 2,  3, 2009), CtiDate( 3,  2, 2009)},
            {  2, CtiDate( 3,  3, 2009), CtiDate( 3,  3, 2009)},
            {  2, CtiDate( 4,  3, 2009), CtiDate( 3,  3, 2009)},
            {  2, CtiDate( 5,  3, 2009), CtiDate( 3,  3, 2009)},

            {  2, CtiDate(26,  4, 2008), CtiDate( 3,  4, 2008)},
            {  2, CtiDate(27,  4, 2008), CtiDate( 3,  4, 2008)},
            {  2, CtiDate(28,  4, 2008), CtiDate( 3,  4, 2008)},
            {  2, CtiDate(29,  4, 2008), CtiDate( 3,  4, 2008)},
            {  2, CtiDate(30,  4, 2008), CtiDate( 3,  4, 2008)},
            {  2, CtiDate( 1,  5, 2008), CtiDate( 3,  4, 2008)},
            {  2, CtiDate( 2,  5, 2008), CtiDate( 3,  4, 2008)},
            {  2, CtiDate( 3,  5, 2008), CtiDate( 3,  5, 2008)},
            {  2, CtiDate( 4,  5, 2008), CtiDate( 3,  5, 2008)},
            {  2, CtiDate( 5,  5, 2008), CtiDate( 3,  5, 2008)},
        };

    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_27)
{
    freeze_day_check fd[60] =
        {
            { 27, CtiDate(27, 12, 2007), CtiDate(28, 11, 2007)},
            { 27, CtiDate(28, 12, 2007), CtiDate(28, 12, 2007)},
            { 27, CtiDate(29, 12, 2007), CtiDate(28, 12, 2007)},
            { 27, CtiDate(30, 12, 2007), CtiDate(28, 12, 2007)},
            { 27, CtiDate(31, 12, 2007), CtiDate(28, 12, 2007)},
            { 27, CtiDate( 1,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate( 2,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate( 3,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate( 4,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate( 5,  1, 2008), CtiDate(28, 12, 2007)},

            { 27, CtiDate(10,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(11,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(12,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(13,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(14,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(15,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(16,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(17,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(18,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(19,  1, 2008), CtiDate(28, 12, 2007)},

            { 27, CtiDate(27,  1, 2008), CtiDate(28, 12, 2007)},
            { 27, CtiDate(28,  1, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate(29,  1, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate(30,  1, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate(31,  1, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate( 1,  2, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate( 2,  2, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate( 3,  2, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate( 4,  2, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate( 5,  2, 2008), CtiDate(28,  1, 2008)},

            //  leap year
            { 27, CtiDate(25,  2, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate(26,  2, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate(27,  2, 2008), CtiDate(28,  1, 2008)},
            { 27, CtiDate(28,  2, 2008), CtiDate(28,  2, 2008)},
            { 27, CtiDate(29,  2, 2008), CtiDate(28,  2, 2008)},
            { 27, CtiDate( 1,  3, 2008), CtiDate(28,  2, 2008)},
            { 27, CtiDate( 2,  3, 2008), CtiDate(28,  2, 2008)},
            { 27, CtiDate( 3,  3, 2008), CtiDate(28,  2, 2008)},
            { 27, CtiDate( 4,  3, 2008), CtiDate(28,  2, 2008)},
            { 27, CtiDate( 5,  3, 2008), CtiDate(28,  2, 2008)},

            //  non-leap year
            { 27, CtiDate(24,  2, 2009), CtiDate(28,  1, 2009)},
            { 27, CtiDate(25,  2, 2009), CtiDate(28,  1, 2009)},
            { 27, CtiDate(26,  2, 2009), CtiDate(28,  1, 2009)},
            { 27, CtiDate(27,  2, 2009), CtiDate(28,  1, 2009)},
            { 27, CtiDate(28,  2, 2009), CtiDate(28,  2, 2009)},
            { 27, CtiDate( 1,  3, 2009), CtiDate(28,  2, 2009)},
            { 27, CtiDate( 2,  3, 2009), CtiDate(28,  2, 2009)},
            { 27, CtiDate( 3,  3, 2009), CtiDate(28,  2, 2009)},
            { 27, CtiDate( 4,  3, 2009), CtiDate(28,  2, 2009)},
            { 27, CtiDate( 5,  3, 2009), CtiDate(28,  2, 2009)},

            { 27, CtiDate(26,  4, 2008), CtiDate(28,  3, 2008)},
            { 27, CtiDate(27,  4, 2008), CtiDate(28,  3, 2008)},
            { 27, CtiDate(28,  4, 2008), CtiDate(28,  4, 2008)},
            { 27, CtiDate(29,  4, 2008), CtiDate(28,  4, 2008)},
            { 27, CtiDate(30,  4, 2008), CtiDate(28,  4, 2008)},
            { 27, CtiDate( 1,  5, 2008), CtiDate(28,  4, 2008)},
            { 27, CtiDate( 2,  5, 2008), CtiDate(28,  4, 2008)},
            { 27, CtiDate( 3,  5, 2008), CtiDate(28,  4, 2008)},
            { 27, CtiDate( 4,  5, 2008), CtiDate(28,  4, 2008)},
            { 27, CtiDate( 5,  5, 2008), CtiDate(28,  4, 2008)},
        };

    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_28)
{
    freeze_day_check fd[60] =
        {
            { 28, CtiDate(27, 12, 2007), CtiDate(29, 11, 2007)},
            { 28, CtiDate(28, 12, 2007), CtiDate(29, 11, 2007)},
            { 28, CtiDate(29, 12, 2007), CtiDate(29, 12, 2007)},
            { 28, CtiDate(30, 12, 2007), CtiDate(29, 12, 2007)},
            { 28, CtiDate(31, 12, 2007), CtiDate(29, 12, 2007)},
            { 28, CtiDate( 1,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate( 2,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate( 3,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate( 4,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate( 5,  1, 2008), CtiDate(29, 12, 2007)},

            { 28, CtiDate(10,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(11,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(12,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(13,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(14,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(15,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(16,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(17,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(18,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(19,  1, 2008), CtiDate(29, 12, 2007)},

            { 28, CtiDate(27,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(28,  1, 2008), CtiDate(29, 12, 2007)},
            { 28, CtiDate(29,  1, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate(30,  1, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate(31,  1, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate( 1,  2, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate( 2,  2, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate( 3,  2, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate( 4,  2, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate( 5,  2, 2008), CtiDate(29,  1, 2008)},

            //  leap year
            { 28, CtiDate(25,  2, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate(26,  2, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate(27,  2, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate(28,  2, 2008), CtiDate(29,  1, 2008)},
            { 28, CtiDate(29,  2, 2008), CtiDate(29,  2, 2008)},
            { 28, CtiDate( 1,  3, 2008), CtiDate(29,  2, 2008)},
            { 28, CtiDate( 2,  3, 2008), CtiDate(29,  2, 2008)},
            { 28, CtiDate( 3,  3, 2008), CtiDate(29,  2, 2008)},
            { 28, CtiDate( 4,  3, 2008), CtiDate(29,  2, 2008)},
            { 28, CtiDate( 5,  3, 2008), CtiDate(29,  2, 2008)},

            //  non-leap year
            { 28, CtiDate(24,  2, 2009), CtiDate(29,  1, 2009)},
            { 28, CtiDate(25,  2, 2009), CtiDate(29,  1, 2009)},
            { 28, CtiDate(26,  2, 2009), CtiDate(29,  1, 2009)},
            { 28, CtiDate(27,  2, 2009), CtiDate(29,  1, 2009)},
            { 28, CtiDate(28,  2, 2009), CtiDate(29,  1, 2009)},
            { 28, CtiDate( 1,  3, 2009), CtiDate( 1,  3, 2009)},
            { 28, CtiDate( 2,  3, 2009), CtiDate( 1,  3, 2009)},
            { 28, CtiDate( 3,  3, 2009), CtiDate( 1,  3, 2009)},
            { 28, CtiDate( 4,  3, 2009), CtiDate( 1,  3, 2009)},
            { 28, CtiDate( 5,  3, 2009), CtiDate( 1,  3, 2009)},

            { 28, CtiDate(26,  4, 2008), CtiDate(29,  3, 2008)},
            { 28, CtiDate(27,  4, 2008), CtiDate(29,  3, 2008)},
            { 28, CtiDate(28,  4, 2008), CtiDate(29,  3, 2008)},
            { 28, CtiDate(29,  4, 2008), CtiDate(29,  4, 2008)},
            { 28, CtiDate(30,  4, 2008), CtiDate(29,  4, 2008)},
            { 28, CtiDate( 1,  5, 2008), CtiDate(29,  4, 2008)},
            { 28, CtiDate( 2,  5, 2008), CtiDate(29,  4, 2008)},
            { 28, CtiDate( 3,  5, 2008), CtiDate(29,  4, 2008)},
            { 28, CtiDate( 4,  5, 2008), CtiDate(29,  4, 2008)},
            { 28, CtiDate( 5,  5, 2008), CtiDate(29,  4, 2008)},
        };

    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_29)
{
    freeze_day_check fd[60] =
        {
            { 29, CtiDate(27, 12, 2007), CtiDate(30, 11, 2007)},
            { 29, CtiDate(28, 12, 2007), CtiDate(30, 11, 2007)},
            { 29, CtiDate(29, 12, 2007), CtiDate(30, 11, 2007)},
            { 29, CtiDate(30, 12, 2007), CtiDate(30, 12, 2007)},
            { 29, CtiDate(31, 12, 2007), CtiDate(30, 12, 2007)},
            { 29, CtiDate( 1,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate( 2,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate( 3,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate( 4,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate( 5,  1, 2008), CtiDate(30, 12, 2007)},

            { 29, CtiDate(10,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(11,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(12,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(13,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(14,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(15,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(16,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(17,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(18,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(19,  1, 2008), CtiDate(30, 12, 2007)},

            { 29, CtiDate(27,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(28,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(29,  1, 2008), CtiDate(30, 12, 2007)},
            { 29, CtiDate(30,  1, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate(31,  1, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate( 1,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate( 2,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate( 3,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate( 4,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate( 5,  2, 2008), CtiDate(30,  1, 2008)},

            //  leap year
            { 29, CtiDate(25,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate(26,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate(27,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate(28,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate(29,  2, 2008), CtiDate(30,  1, 2008)},
            { 29, CtiDate( 1,  3, 2008), CtiDate( 1,  3, 2008)},
            { 29, CtiDate( 2,  3, 2008), CtiDate( 1,  3, 2008)},
            { 29, CtiDate( 3,  3, 2008), CtiDate( 1,  3, 2008)},
            { 29, CtiDate( 4,  3, 2008), CtiDate( 1,  3, 2008)},
            { 29, CtiDate( 5,  3, 2008), CtiDate( 1,  3, 2008)},

            //  non-leap year
            { 29, CtiDate(24,  2, 2009), CtiDate(30,  1, 2009)},
            { 29, CtiDate(25,  2, 2009), CtiDate(30,  1, 2009)},
            { 29, CtiDate(26,  2, 2009), CtiDate(30,  1, 2009)},
            { 29, CtiDate(27,  2, 2009), CtiDate(30,  1, 2009)},
            { 29, CtiDate(28,  2, 2009), CtiDate(30,  1, 2009)},
            { 29, CtiDate( 1,  3, 2009), CtiDate( 1,  3, 2009)},
            { 29, CtiDate( 2,  3, 2009), CtiDate( 1,  3, 2009)},
            { 29, CtiDate( 3,  3, 2009), CtiDate( 1,  3, 2009)},
            { 29, CtiDate( 4,  3, 2009), CtiDate( 1,  3, 2009)},
            { 29, CtiDate( 5,  3, 2009), CtiDate( 1,  3, 2009)},

            { 29, CtiDate(26,  4, 2008), CtiDate(30,  3, 2008)},
            { 29, CtiDate(27,  4, 2008), CtiDate(30,  3, 2008)},
            { 29, CtiDate(28,  4, 2008), CtiDate(30,  3, 2008)},
            { 29, CtiDate(29,  4, 2008), CtiDate(30,  3, 2008)},
            { 29, CtiDate(30,  4, 2008), CtiDate(30,  4, 2008)},
            { 29, CtiDate( 1,  5, 2008), CtiDate(30,  4, 2008)},
            { 29, CtiDate( 2,  5, 2008), CtiDate(30,  4, 2008)},
            { 29, CtiDate( 3,  5, 2008), CtiDate(30,  4, 2008)},
            { 29, CtiDate( 4,  5, 2008), CtiDate(30,  4, 2008)},
            { 29, CtiDate( 5,  5, 2008), CtiDate(30,  4, 2008)},
        };

    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_30)
{
    freeze_day_check fd[60] =
        {
            { 30, CtiDate(27, 12, 2007), CtiDate( 1, 12, 2007)},
            { 30, CtiDate(28, 12, 2007), CtiDate( 1, 12, 2007)},
            { 30, CtiDate(29, 12, 2007), CtiDate( 1, 12, 2007)},
            { 30, CtiDate(30, 12, 2007), CtiDate( 1, 12, 2007)},
            { 30, CtiDate(31, 12, 2007), CtiDate(31, 12, 2007)},
            { 30, CtiDate( 1,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate( 2,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate( 3,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate( 4,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate( 5,  1, 2008), CtiDate(31, 12, 2007)},

            { 30, CtiDate(10,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(11,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(12,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(13,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(14,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(15,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(16,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(17,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(18,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(19,  1, 2008), CtiDate(31, 12, 2007)},

            { 30, CtiDate(27,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(28,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(29,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(30,  1, 2008), CtiDate(31, 12, 2007)},
            { 30, CtiDate(31,  1, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate( 1,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate( 2,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate( 3,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate( 4,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate( 5,  2, 2008), CtiDate(31,  1, 2008)},

            //  leap year
            { 30, CtiDate(25,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate(26,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate(27,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate(28,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate(29,  2, 2008), CtiDate(31,  1, 2008)},
            { 30, CtiDate( 1,  3, 2008), CtiDate( 1,  3, 2008)},
            { 30, CtiDate( 2,  3, 2008), CtiDate( 1,  3, 2008)},
            { 30, CtiDate( 3,  3, 2008), CtiDate( 1,  3, 2008)},
            { 30, CtiDate( 4,  3, 2008), CtiDate( 1,  3, 2008)},
            { 30, CtiDate( 5,  3, 2008), CtiDate( 1,  3, 2008)},

            //  non-leap year
            { 30, CtiDate(24,  2, 2009), CtiDate(31,  1, 2009)},
            { 30, CtiDate(25,  2, 2009), CtiDate(31,  1, 2009)},
            { 30, CtiDate(26,  2, 2009), CtiDate(31,  1, 2009)},
            { 30, CtiDate(27,  2, 2009), CtiDate(31,  1, 2009)},
            { 30, CtiDate(28,  2, 2009), CtiDate(31,  1, 2009)},
            { 30, CtiDate( 1,  3, 2009), CtiDate( 1,  3, 2009)},
            { 30, CtiDate( 2,  3, 2009), CtiDate( 1,  3, 2009)},
            { 30, CtiDate( 3,  3, 2009), CtiDate( 1,  3, 2009)},
            { 30, CtiDate( 4,  3, 2009), CtiDate( 1,  3, 2009)},
            { 30, CtiDate( 5,  3, 2009), CtiDate( 1,  3, 2009)},

            { 30, CtiDate(26,  4, 2008), CtiDate(31,  3, 2008)},
            { 30, CtiDate(27,  4, 2008), CtiDate(31,  3, 2008)},
            { 30, CtiDate(28,  4, 2008), CtiDate(31,  3, 2008)},
            { 30, CtiDate(29,  4, 2008), CtiDate(31,  3, 2008)},
            { 30, CtiDate(30,  4, 2008), CtiDate(31,  3, 2008)},
            { 30, CtiDate( 1,  5, 2008), CtiDate( 1,  5, 2008)},
            { 30, CtiDate( 2,  5, 2008), CtiDate( 1,  5, 2008)},
            { 30, CtiDate( 3,  5, 2008), CtiDate( 1,  5, 2008)},
            { 30, CtiDate( 4,  5, 2008), CtiDate( 1,  5, 2008)},
            { 30, CtiDate( 5,  5, 2008), CtiDate( 1,  5, 2008)},
        };

    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_31)
{
    freeze_day_check fd[60] =
        {
            { 31, CtiDate(27, 12, 2007), CtiDate( 1, 12, 2007)},
            { 31, CtiDate(28, 12, 2007), CtiDate( 1, 12, 2007)},
            { 31, CtiDate(29, 12, 2007), CtiDate( 1, 12, 2007)},
            { 31, CtiDate(30, 12, 2007), CtiDate( 1, 12, 2007)},
            { 31, CtiDate(31, 12, 2007), CtiDate( 1, 12, 2007)},
            { 31, CtiDate( 1,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate( 2,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate( 3,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate( 4,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate( 5,  1, 2008), CtiDate( 1,  1, 2008)},

            { 31, CtiDate(10,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(11,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(12,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(13,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(14,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(15,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(16,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(17,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(18,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(19,  1, 2008), CtiDate( 1,  1, 2008)},

            { 31, CtiDate(27,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(28,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(29,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(30,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate(31,  1, 2008), CtiDate( 1,  1, 2008)},
            { 31, CtiDate( 1,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate( 2,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate( 3,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate( 4,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate( 5,  2, 2008), CtiDate( 1,  2, 2008)},

            //  leap year
            { 31, CtiDate(25,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate(26,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate(27,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate(28,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate(29,  2, 2008), CtiDate( 1,  2, 2008)},
            { 31, CtiDate( 1,  3, 2008), CtiDate( 1,  3, 2008)},
            { 31, CtiDate( 2,  3, 2008), CtiDate( 1,  3, 2008)},
            { 31, CtiDate( 3,  3, 2008), CtiDate( 1,  3, 2008)},
            { 31, CtiDate( 4,  3, 2008), CtiDate( 1,  3, 2008)},
            { 31, CtiDate( 5,  3, 2008), CtiDate( 1,  3, 2008)},

            //  non-leap year
            { 31, CtiDate(24,  2, 2009), CtiDate( 1,  2, 2009)},
            { 31, CtiDate(25,  2, 2009), CtiDate( 1,  2, 2009)},
            { 31, CtiDate(26,  2, 2009), CtiDate( 1,  2, 2009)},
            { 31, CtiDate(27,  2, 2009), CtiDate( 1,  2, 2009)},
            { 31, CtiDate(28,  2, 2009), CtiDate( 1,  2, 2009)},
            { 31, CtiDate( 1,  3, 2009), CtiDate( 1,  3, 2009)},
            { 31, CtiDate( 2,  3, 2009), CtiDate( 1,  3, 2009)},
            { 31, CtiDate( 3,  3, 2009), CtiDate( 1,  3, 2009)},
            { 31, CtiDate( 4,  3, 2009), CtiDate( 1,  3, 2009)},
            { 31, CtiDate( 5,  3, 2009), CtiDate( 1,  3, 2009)},

            { 31, CtiDate(26,  4, 2008), CtiDate( 1,  4, 2008)},
            { 31, CtiDate(27,  4, 2008), CtiDate( 1,  4, 2008)},
            { 31, CtiDate(28,  4, 2008), CtiDate( 1,  4, 2008)},
            { 31, CtiDate(29,  4, 2008), CtiDate( 1,  4, 2008)},
            { 31, CtiDate(30,  4, 2008), CtiDate( 1,  4, 2008)},
            { 31, CtiDate( 1,  5, 2008), CtiDate( 1,  5, 2008)},
            { 31, CtiDate( 2,  5, 2008), CtiDate( 1,  5, 2008)},
            { 31, CtiDate( 3,  5, 2008), CtiDate( 1,  5, 2008)},
            { 31, CtiDate( 4,  5, 2008), CtiDate( 1,  5, 2008)},
            { 31, CtiDate( 5,  5, 2008), CtiDate( 1,  5, 2008)},
        };

    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_32)
{
    freeze_day_check fd[60] =
        {
            { 32, CtiDate(27, 12, 2007), CtiDate( 1, 12, 2007)},
            { 32, CtiDate(28, 12, 2007), CtiDate( 1, 12, 2007)},
            { 32, CtiDate(29, 12, 2007), CtiDate( 1, 12, 2007)},
            { 32, CtiDate(30, 12, 2007), CtiDate( 1, 12, 2007)},
            { 32, CtiDate(31, 12, 2007), CtiDate( 1, 12, 2007)},
            { 32, CtiDate( 1,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate( 2,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate( 3,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate( 4,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate( 5,  1, 2008), CtiDate( 1,  1, 2008)},

            { 32, CtiDate(10,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(11,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(12,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(13,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(14,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(15,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(16,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(17,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(18,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(19,  1, 2008), CtiDate( 1,  1, 2008)},

            { 32, CtiDate(27,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(28,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(29,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(30,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate(31,  1, 2008), CtiDate( 1,  1, 2008)},
            { 32, CtiDate( 1,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate( 2,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate( 3,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate( 4,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate( 5,  2, 2008), CtiDate( 1,  2, 2008)},

            //  leap year
            { 32, CtiDate(25,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate(26,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate(27,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate(28,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate(29,  2, 2008), CtiDate( 1,  2, 2008)},
            { 32, CtiDate( 1,  3, 2008), CtiDate( 1,  3, 2008)},
            { 32, CtiDate( 2,  3, 2008), CtiDate( 1,  3, 2008)},
            { 32, CtiDate( 3,  3, 2008), CtiDate( 1,  3, 2008)},
            { 32, CtiDate( 4,  3, 2008), CtiDate( 1,  3, 2008)},
            { 32, CtiDate( 5,  3, 2008), CtiDate( 1,  3, 2008)},

            //  non-leap year
            { 32, CtiDate(24,  2, 2009), CtiDate( 1,  2, 2009)},
            { 32, CtiDate(25,  2, 2009), CtiDate( 1,  2, 2009)},
            { 32, CtiDate(26,  2, 2009), CtiDate( 1,  2, 2009)},
            { 32, CtiDate(27,  2, 2009), CtiDate( 1,  2, 2009)},
            { 32, CtiDate(28,  2, 2009), CtiDate( 1,  2, 2009)},
            { 32, CtiDate( 1,  3, 2009), CtiDate( 1,  3, 2009)},
            { 32, CtiDate( 2,  3, 2009), CtiDate( 1,  3, 2009)},
            { 32, CtiDate( 3,  3, 2009), CtiDate( 1,  3, 2009)},
            { 32, CtiDate( 4,  3, 2009), CtiDate( 1,  3, 2009)},
            { 32, CtiDate( 5,  3, 2009), CtiDate( 1,  3, 2009)},

            { 32, CtiDate(26,  4, 2008), CtiDate( 1,  4, 2008)},
            { 32, CtiDate(27,  4, 2008), CtiDate( 1,  4, 2008)},
            { 32, CtiDate(28,  4, 2008), CtiDate( 1,  4, 2008)},
            { 32, CtiDate(29,  4, 2008), CtiDate( 1,  4, 2008)},
            { 32, CtiDate(30,  4, 2008), CtiDate( 1,  4, 2008)},
            { 32, CtiDate( 1,  5, 2008), CtiDate( 1,  5, 2008)},
            { 32, CtiDate( 2,  5, 2008), CtiDate( 1,  5, 2008)},
            { 32, CtiDate( 3,  5, 2008), CtiDate( 1,  5, 2008)},
            { 32, CtiDate( 4,  5, 2008), CtiDate( 1,  5, 2008)},
            { 32, CtiDate( 5,  5, 2008), CtiDate( 1,  5, 2008)},
        };

    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_1000000000)
{
    freeze_day_check fd[60] =
        {
            { 1000000000, CtiDate(27, 12, 2007), CtiDate( 1, 12, 2007)},
            { 1000000000, CtiDate(28, 12, 2007), CtiDate( 1, 12, 2007)},
            { 1000000000, CtiDate(29, 12, 2007), CtiDate( 1, 12, 2007)},
            { 1000000000, CtiDate(30, 12, 2007), CtiDate( 1, 12, 2007)},
            { 1000000000, CtiDate(31, 12, 2007), CtiDate( 1, 12, 2007)},
            { 1000000000, CtiDate( 1,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate( 2,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate( 3,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate( 4,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate( 5,  1, 2008), CtiDate( 1,  1, 2008)},

            { 1000000000, CtiDate(10,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(11,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(12,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(13,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(14,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(15,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(16,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(17,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(18,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(19,  1, 2008), CtiDate( 1,  1, 2008)},

            { 1000000000, CtiDate(27,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(28,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(29,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(30,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate(31,  1, 2008), CtiDate( 1,  1, 2008)},
            { 1000000000, CtiDate( 1,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate( 2,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate( 3,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate( 4,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate( 5,  2, 2008), CtiDate( 1,  2, 2008)},

            //  leap year
            { 1000000000, CtiDate(25,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate(26,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate(27,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate(28,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate(29,  2, 2008), CtiDate( 1,  2, 2008)},
            { 1000000000, CtiDate( 1,  3, 2008), CtiDate( 1,  3, 2008)},
            { 1000000000, CtiDate( 2,  3, 2008), CtiDate( 1,  3, 2008)},
            { 1000000000, CtiDate( 3,  3, 2008), CtiDate( 1,  3, 2008)},
            { 1000000000, CtiDate( 4,  3, 2008), CtiDate( 1,  3, 2008)},
            { 1000000000, CtiDate( 5,  3, 2008), CtiDate( 1,  3, 2008)},

            //  non-leap year
            { 1000000000, CtiDate(24,  2, 2009), CtiDate( 1,  2, 2009)},
            { 1000000000, CtiDate(25,  2, 2009), CtiDate( 1,  2, 2009)},
            { 1000000000, CtiDate(26,  2, 2009), CtiDate( 1,  2, 2009)},
            { 1000000000, CtiDate(27,  2, 2009), CtiDate( 1,  2, 2009)},
            { 1000000000, CtiDate(28,  2, 2009), CtiDate( 1,  2, 2009)},
            { 1000000000, CtiDate( 1,  3, 2009), CtiDate( 1,  3, 2009)},
            { 1000000000, CtiDate( 2,  3, 2009), CtiDate( 1,  3, 2009)},
            { 1000000000, CtiDate( 3,  3, 2009), CtiDate( 1,  3, 2009)},
            { 1000000000, CtiDate( 4,  3, 2009), CtiDate( 1,  3, 2009)},
            { 1000000000, CtiDate( 5,  3, 2009), CtiDate( 1,  3, 2009)},

            { 1000000000, CtiDate(26,  4, 2008), CtiDate( 1,  4, 2008)},
            { 1000000000, CtiDate(27,  4, 2008), CtiDate( 1,  4, 2008)},
            { 1000000000, CtiDate(28,  4, 2008), CtiDate( 1,  4, 2008)},
            { 1000000000, CtiDate(29,  4, 2008), CtiDate( 1,  4, 2008)},
            { 1000000000, CtiDate(30,  4, 2008), CtiDate( 1,  4, 2008)},
            { 1000000000, CtiDate( 1,  5, 2008), CtiDate( 1,  5, 2008)},
            { 1000000000, CtiDate( 2,  5, 2008), CtiDate( 1,  5, 2008)},
            { 1000000000, CtiDate( 3,  5, 2008), CtiDate( 1,  5, 2008)},
            { 1000000000, CtiDate( 4,  5, 2008), CtiDate( 1,  5, 2008)},
            { 1000000000, CtiDate( 5,  5, 2008), CtiDate( 1,  5, 2008)},
        };


    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 0].time_now, fd[ 0].freeze_day), fd[ 0].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 1].time_now, fd[ 1].freeze_day), fd[ 1].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 2].time_now, fd[ 2].freeze_day), fd[ 2].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 3].time_now, fd[ 3].freeze_day), fd[ 3].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 4].time_now, fd[ 4].freeze_day), fd[ 4].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 5].time_now, fd[ 5].freeze_day), fd[ 5].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 6].time_now, fd[ 6].freeze_day), fd[ 6].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 7].time_now, fd[ 7].freeze_day), fd[ 7].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 8].time_now, fd[ 8].freeze_day), fd[ 8].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[ 9].time_now, fd[ 9].freeze_day), fd[ 9].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[10].time_now, fd[10].freeze_day), fd[10].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[11].time_now, fd[11].freeze_day), fd[11].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[12].time_now, fd[12].freeze_day), fd[12].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[13].time_now, fd[13].freeze_day), fd[13].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[14].time_now, fd[14].freeze_day), fd[14].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[15].time_now, fd[15].freeze_day), fd[15].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[16].time_now, fd[16].freeze_day), fd[16].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[17].time_now, fd[17].freeze_day), fd[17].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[18].time_now, fd[18].freeze_day), fd[18].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[19].time_now, fd[19].freeze_day), fd[19].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[20].time_now, fd[20].freeze_day), fd[20].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[21].time_now, fd[21].freeze_day), fd[21].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[22].time_now, fd[22].freeze_day), fd[22].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[23].time_now, fd[23].freeze_day), fd[23].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[24].time_now, fd[24].freeze_day), fd[24].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[25].time_now, fd[25].freeze_day), fd[25].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[26].time_now, fd[26].freeze_day), fd[26].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[27].time_now, fd[27].freeze_day), fd[27].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[28].time_now, fd[28].freeze_day), fd[28].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[29].time_now, fd[29].freeze_day), fd[29].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[30].time_now, fd[30].freeze_day), fd[30].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[31].time_now, fd[31].freeze_day), fd[31].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[32].time_now, fd[32].freeze_day), fd[32].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[33].time_now, fd[33].freeze_day), fd[33].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[34].time_now, fd[34].freeze_day), fd[34].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[35].time_now, fd[35].freeze_day), fd[35].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[36].time_now, fd[36].freeze_day), fd[36].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[37].time_now, fd[37].freeze_day), fd[37].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[38].time_now, fd[38].freeze_day), fd[38].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[39].time_now, fd[39].freeze_day), fd[39].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[40].time_now, fd[40].freeze_day), fd[40].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[41].time_now, fd[41].freeze_day), fd[41].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[42].time_now, fd[42].freeze_day), fd[42].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[43].time_now, fd[43].freeze_day), fd[43].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[44].time_now, fd[44].freeze_day), fd[44].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[45].time_now, fd[45].freeze_day), fd[45].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[46].time_now, fd[46].freeze_day), fd[46].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[47].time_now, fd[47].freeze_day), fd[47].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[48].time_now, fd[48].freeze_day), fd[48].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[49].time_now, fd[49].freeze_day), fd[49].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[50].time_now, fd[50].freeze_day), fd[50].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[51].time_now, fd[51].freeze_day), fd[51].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[52].time_now, fd[52].freeze_day), fd[52].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[53].time_now, fd[53].freeze_day), fd[53].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[54].time_now, fd[54].freeze_day), fd[54].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[55].time_now, fd[55].freeze_day), fd[55].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[56].time_now, fd[56].freeze_day), fd[56].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[57].time_now, fd[57].freeze_day), fd[57].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[58].time_now, fd[58].freeze_day), fd[58].expected);
    BOOST_CHECK_EQUAL(test_MctDevice::findLastScheduledFreeze(fd[59].time_now, fd[59].freeze_day), fd[59].expected);
}


