#include <boost/test/unit_test.hpp>

#include "dev_mct.h"
#include "dev_ccu.h"
#include "rte_ccu.h"
#include "ctidate.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using namespace Cti::Protocols;

BOOST_AUTO_TEST_SUITE( test_dev_mct )


struct test_CtiDeviceCCU : CtiDeviceCCU
{
    test_CtiDeviceCCU()
    {
        _paObjectID = 12345;
    }
};

struct test_CtiRouteCCU : CtiRouteCCU
{
    CtiDeviceSPtr ccu;

    test_CtiRouteCCU() : ccu(new test_CtiDeviceCCU)
    {
        _tblPAO.setID(1234);
        setDevicePointer(ccu);
    }
};

struct test_MctDevice : Cti::Devices::MctDevice
{
    using MctDevice::findLastScheduledFreeze;
    using MctDevice::getOperation;
    using MctDevice::ResultDecode;
    using MctDevice::decodeReadDataForKey;

    CtiRouteSPtr rte;

    test_MctDevice() : rte(new test_CtiRouteCCU)
    {
        _name = "Test MCT device";
        _paObjectID = 123456;
    }

    virtual CtiRouteSPtr getRoute() const
    {
        return rte;
    }
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
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
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
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_dev_mct_decodeReadDataForKey)
{
    using Dpi = CtiTableDynamicPaoInfo;

    Cti::Test::Override_DynamicPaoInfoManager scopedPaoInfo;

    test_MctDevice mct;

    unsigned char freeze_days[] = { 0, 15 };

    BOOST_CHECK( ! mct.hasDynamicInfo(Dpi::Key_MCT_ScheduledFreezeDay) );
    BOOST_CHECK( ! mct.hasDynamicInfo(Dpi::Key_MCT_ScheduledFreezeConfigTimestamp) );

    //  Decode a new value
    {
        const CtiTime Now(CtiDate(1, 2, 2013), 12, 34, 56);
        Cti::Test::Override_CtiTime_Now scopedTimeOverride(Now);

        mct.decodeReadDataForKey(Dpi::Key_MCT_ScheduledFreezeDay, freeze_days + 0, freeze_days + 1);

        unsigned freezeDay;
        CtiTime  freezeConfigTimestamp;

        BOOST_CHECK( mct.getDynamicInfo(Dpi::Key_MCT_ScheduledFreezeDay, freezeDay) );
        BOOST_CHECK( mct.getDynamicInfo(Dpi::Key_MCT_ScheduledFreezeConfigTimestamp, freezeConfigTimestamp) );

        BOOST_CHECK_EQUAL(freezeDay, 0);
        BOOST_CHECK_EQUAL(freezeConfigTimestamp, Now);

        BOOST_CHECK(scopedPaoInfo.dpi->dirtyEntries.count(mct.getID()));
        BOOST_CHECK(scopedPaoInfo.dpi->dirtyEntries[mct.getID()].count(Dpi::Key_MCT_ScheduledFreezeDay));
        BOOST_CHECK(scopedPaoInfo.dpi->dirtyEntries[mct.getID()].count(Dpi::Key_MCT_ScheduledFreezeConfigTimestamp));
        scopedPaoInfo.dpi->dirtyEntries.clear();
    }

    //  Decode the same value again at a new time, confirm neither value changes
    {
        const CtiTime Then(CtiDate(1, 2, 2013), 12, 34, 56);
        const CtiTime Now (CtiDate(1, 2, 2013), 12, 35, 56);
        Cti::Test::Override_CtiTime_Now scopedTimeOverride(Now);

        mct.decodeReadDataForKey(Dpi::Key_MCT_ScheduledFreezeDay, freeze_days + 0, freeze_days + 1);

        unsigned freezeDay;
        CtiTime  freezeConfigTimestamp;

        BOOST_CHECK( mct.getDynamicInfo(Dpi::Key_MCT_ScheduledFreezeDay, freezeDay) );
        BOOST_CHECK( mct.getDynamicInfo(Dpi::Key_MCT_ScheduledFreezeConfigTimestamp, freezeConfigTimestamp) );

        BOOST_CHECK_EQUAL(freezeDay, 0);
        BOOST_CHECK_EQUAL(freezeConfigTimestamp, Then);

        BOOST_CHECK(scopedPaoInfo.dpi->dirtyEntries.empty());
    }

    //  Decode a new value at a new time, confirm both values change
    {
        const CtiTime Then(CtiDate(1, 2, 2013), 12, 34, 56);
        const CtiTime Now (CtiDate(1, 2, 2013), 12, 36, 56);
        Cti::Test::Override_CtiTime_Now scopedTimeOverride(Now);

        mct.decodeReadDataForKey(Dpi::Key_MCT_ScheduledFreezeDay, freeze_days + 1, freeze_days + 2);

        unsigned freezeDay;
        CtiTime  freezeConfigTimestamp;

        BOOST_CHECK( mct.getDynamicInfo(Dpi::Key_MCT_ScheduledFreezeDay, freezeDay) );
        BOOST_CHECK( mct.getDynamicInfo(Dpi::Key_MCT_ScheduledFreezeConfigTimestamp, freezeConfigTimestamp) );

        BOOST_CHECK_EQUAL(freezeDay, 15);
        BOOST_CHECK_EQUAL(freezeConfigTimestamp, Now);

        BOOST_CHECK(scopedPaoInfo.dpi->dirtyEntries.count(mct.getID()));
        BOOST_CHECK(scopedPaoInfo.dpi->dirtyEntries[mct.getID()].count(Dpi::Key_MCT_ScheduledFreezeDay));
        BOOST_CHECK(scopedPaoInfo.dpi->dirtyEntries[mct.getID()].count(Dpi::Key_MCT_ScheduledFreezeConfigTimestamp));
        scopedPaoInfo.dpi->dirtyEntries.clear();
    }
}


struct executeRequest_helper
{
    test_MctDevice mct;

    test_MctDevice::CtiMessageList vgList, retList;
    test_MctDevice::OutMessageList outList;

    ~executeRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(control_connect, executeRequest_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_connect_execute)
    {
        CtiRequestMsg    req( -1, "control connect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( 1, outList.size() );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 66 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       32 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   0 );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_connect_decode)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Connect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control connect");

        BOOST_CHECK_EQUAL( ClientErrors::None , mct.ResultDecode(im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        const std::vector<const CtiMessage *> retMsgs(retList.begin(), retList.end());

        const CtiRequestMsg *req = dynamic_cast<const CtiRequestMsg *>(retMsgs[0]);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect" );

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retMsgs[1]);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control connect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "Test MCT device / control sent" );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_connect_decode_noqueue)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Connect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control connect noqueue");

        BOOST_CHECK_EQUAL( ClientErrors::None , mct.ResultDecode(im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        const std::vector<const CtiMessage *> retMsgs(retList.begin(), retList.end());

        const CtiRequestMsg *req = dynamic_cast<const CtiRequestMsg *>(retMsgs[0]);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect noqueue" );

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retMsgs[1]);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control connect noqueue" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "Test MCT device / control sent" );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_disconnect)
    {
        CtiRequestMsg    req( -1, "control disconnect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( ClientErrors::None , mct.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( 1, outList.size() );

        OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 65 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       32 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   0 );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct_control_disconnect_decode)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Disconnect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None , mct.ResultDecode(im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        const std::vector<const CtiMessage *> retMsgs(retList.begin(), retList.end());

        const CtiRequestMsg *req = dynamic_cast<const CtiRequestMsg *>(retMsgs[0]);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect" );

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retMsgs[1]);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control disconnect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "Test MCT device / control sent" );
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()
