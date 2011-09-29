#include "dev_mct.h"
#include "ctidate.h"

#define BOOST_TEST_MAIN "Test dev_mct"
#include <boost/test/unit_test.hpp>

using Cti::Devices::MctDevice;
using Cti::Protocols::EmetconProtocol;

struct test_MctDevice : MctDevice
{
    using MctDevice::findLastScheduledFreeze;
    using MctDevice::getOperation;
};

struct freeze_day_check
{
    unsigned freeze_day;
    CtiTime time_now;
    CtiTime expected;
};

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_0)
{
    freeze_day_check fd[30] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_1)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_2)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_27)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_28)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_29)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_30)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_31)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_32)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct_findLastScheduledFreeze_day_1000000000)
{
    freeze_day_check fd[60] = {
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

    std::vector<CtiTime> expected, results;

    for each(const freeze_day_check &fdc in fd)
    {
        //  this is relying on an implicit cast from CtiDate to CtiTime at midnight for brevity
        expected.push_back(fdc.expected);
        results .push_back(test_MctDevice::findLastScheduledFreeze(fdc.time_now, fdc.freeze_day));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results .begin(), results .end());
}


struct getOperation_helper
{
    test_MctDevice mct;
    BSTRUCT BSt;
};


BOOST_FIXTURE_TEST_SUITE(test_getOperation, getOperation_helper)
//{  For Jeremy.  <3
    BOOST_AUTO_TEST_CASE(test_getOperation_01)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Command_Loop, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_02)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Model, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_03)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Install, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_04)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddressEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x54);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_05)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddressInhibit, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x53);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_06)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_07)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Shed, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_08)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Restore, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_09)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x42);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_10)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x41);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_11)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ARMC, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x62);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_12)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ARML, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x60);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_13)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x49);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
//}  For Jeremy.  <3
BOOST_AUTO_TEST_SUITE_END()
