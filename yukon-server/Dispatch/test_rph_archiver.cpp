#include <boost/test/unit_test.hpp>

#include "rph_archiver.h"

#include "tbl_rawpthistory.h"

#include "boost_test_helpers.h"

using namespace std::chrono_literals;

BOOST_AUTO_TEST_SUITE( test_rph_archiver )

struct test_RphArchiver : Cti::Dispatch::RawPointHistoryArchiver
{
    static void noOp(const std::string& whoCares) {};
    bool b;

    test_RphArchiver()
        : RawPointHistoryArchiver(b, &noOp)
    {}

    using RawPointHistoryArchiver::wasPreviouslyArchived;
};

template<typename T, typename U>
CtiTableRawPointHistory operator+(const CtiTableRawPointHistory &lhs, const std::chrono::duration<T, U> d)
{
    return CtiTableRawPointHistory { lhs.pointId, lhs.quality, lhs.value, lhs.time + std::chrono::duration_cast<std::chrono::seconds>(d).count(), lhs.millis };
}
template<typename T, typename U>
CtiTableRawPointHistory operator-(const CtiTableRawPointHistory &lhs, const std::chrono::duration<T, U> d)
{
    return CtiTableRawPointHistory { lhs.pointId, lhs.quality, lhs.value, lhs.time - std::chrono::duration_cast<std::chrono::seconds>(d).count(), lhs.millis };
}

template<>
CtiTableRawPointHistory operator+(const CtiTableRawPointHistory &lhs, std::chrono::milliseconds ms)
{
    return CtiTableRawPointHistory { lhs.pointId, lhs.quality, lhs.value, lhs.time, lhs.millis + static_cast<int>(ms.count()) };
}

BOOST_AUTO_TEST_CASE( test_wasPreviouslyArchived )
{
    test_RphArchiver a;

    CtiTime t { CtiDate { 23, 8, 2016 }, 13, 00, 00 };

    Cti::Test::Override_CtiTime_Now timeOverride { t };
        
    const CtiTableRawPointHistory b { 11235, 5, 12.34, t, 0 };

    //  baseline
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b) );
    
    //  readings with millis do not get cached
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 83ms) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 83ms) );
    
    //  readings with non-integral minutes do not get cached
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 17s) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 17s) );
    
    //  1 hour before baseline
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 1h) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b + 1h) );
    //  1 hour after baseline
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 1h) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b - 1h) );
    
    //  check baseline again
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b) );
    
    //  35 hours before baseline
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 35h) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b - 35h) );

    //  36 hours before baseline, out of range, cannot be cached
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 36h) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 36h) );

    //  check baseline again
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b) );
    
    //  change interval to 15 minutes
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 15min) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b + 15min) );
    
    //  check baseline again
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b) );
    
    //  check hourly again
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b + 1h) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b - 1h) );
    
    //  make sure 36 hour was kicked out...
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 35h) );
    //  ... and stays kicked out, since we have no room for it now
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 35h) );
    
    //  but we do have room for b + 4 - 36 = b - 32 * 15-minute intervals back
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 15min * 32) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b - 15min * 32) );
    
    //  confirm everything else again
    BOOST_CHECK_EQUAL( true, a.wasPreviouslyArchived(b) );
    BOOST_CHECK_EQUAL( true, a.wasPreviouslyArchived(b + 1h) );
    BOOST_CHECK_EQUAL( true, a.wasPreviouslyArchived(b - 1h) );
    BOOST_CHECK_EQUAL( true, a.wasPreviouslyArchived(b + 15min) );

    //  insert something far-flung-future, should be ignored
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 24h) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 24h) );

    //  confirm everything else is still in there
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b + 1h) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b - 1h) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 35h) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 36h) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b + 15min) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b - 15min * 32) );

    //  insert something that is less than 24 hours in the future
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 23h) );
    BOOST_CHECK_EQUAL( true,  a.wasPreviouslyArchived(b + 23h) );

    //  and confirm everything else has been removed
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 1h) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 1h) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 35h) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 36h) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b + 15min) );
    BOOST_CHECK_EQUAL( false, a.wasPreviouslyArchived(b - 15min * 32) );
}

BOOST_AUTO_TEST_SUITE_END()
