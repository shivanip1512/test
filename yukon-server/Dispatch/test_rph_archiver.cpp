#include <boost/test/unit_test.hpp>

#include "rph_archiver.h"

#include "tbl_rawpthistory.h"

#include "boost_test_helpers.h"

using namespace std::chrono_literals;

struct test_RphArchiver : Cti::Dispatch::RawPointHistoryArchiver
{
    static void noOp(const std::string& whoCares) {};
    bool b;

    test_RphArchiver()
        : RawPointHistoryArchiver(b, &noOp)
    {}

    using RawPointHistoryArchiver::getArchiveStatus;
    using RawPointHistoryArchiver::ArchiveStatus;
};

using AS = test_RphArchiver::ArchiveStatus;

namespace Cti {
namespace Dispatch {
//  Put the output operator in the same namespace as the type so ADL works
std::ostream& operator<<(std::ostream& os, const AS archiveStatus)
{
    switch( archiveStatus )
    {
    case AS::Ancient:       return os << "Ancient";
    case AS::BeyondCache:   return os << "Beyond cache";
    case AS::Duplicate:     return os << "Duplicate";
    case AS::Future:        return os << "Future";
    case AS::NotInCache:    return os << "Not in cache";
    default:                return os << "bad state";
    }
}
}
}

BOOST_AUTO_TEST_SUITE( test_rph_archiver )

template<typename T, typename U>
CtiTableRawPointHistory operator+(const CtiTableRawPointHistory &lhs, const std::chrono::duration<T, U> d)
{
    return CtiTableRawPointHistory { lhs.pointId, lhs.quality, lhs.value, lhs.time + std::chrono::duration_cast<std::chrono::seconds>(d).count(), lhs.millis, "" };
}
template<typename T, typename U>
CtiTableRawPointHistory operator-(const CtiTableRawPointHistory &lhs, const std::chrono::duration<T, U> d)
{
    return CtiTableRawPointHistory { lhs.pointId, lhs.quality, lhs.value, lhs.time - std::chrono::duration_cast<std::chrono::seconds>(d).count(), lhs.millis, "" };
}

template<>
CtiTableRawPointHistory operator+(const CtiTableRawPointHistory &lhs, std::chrono::milliseconds ms)
{
    return CtiTableRawPointHistory { lhs.pointId, lhs.quality, lhs.value, lhs.time, lhs.millis + static_cast<int>(ms.count()), "" };
}

BOOST_AUTO_TEST_CASE( test_wasPreviouslyArchived )
{
    test_RphArchiver a;

    const CtiTime t { CtiDate { 23, 8, 2016 }, 13, 00, 00 };

    const auto now = t.seconds();
        
    const CtiTableRawPointHistory b { 11235, 5, 12.34, t, 0, "" };

    //  baseline
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b, now) );
    
    //  readings with millis do not get cached
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b + 83ms, now) );
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b + 83ms, now) );
    
    //  readings with non-integral minutes do not get cached
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b + 17s, now) );
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b + 17s, now) );
    
    //  1 hour before baseline
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b + 1h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b + 1h, now) );
    //  1 hour after baseline
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b - 1h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b - 1h, now) );
    
    //  check baseline again
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b, now) );
    
    //  35 hours before baseline
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b - 35h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b - 35h, now) );

    //  36 hours before baseline, out of range, cannot be cached
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 36h, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 36h, now) );

    //  check baseline again
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b, now) );
    
    //  change interval to 15 minutes
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b + 15min, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b + 15min, now) );
    
    //  check baseline again
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b, now) );
    
    //  check hourly again
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b + 1h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b - 1h, now) );
    
    //  make sure 36 hour was kicked out...
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 35h, now) );
    //  ... and stays kicked out, since we have no room for it now
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 35h, now) );
    
    //  but we do have room for b + 4 - 36 = b - 32 * 15-minute intervals back
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b - 15min * 32, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b - 15min * 32, now) );
    
    //  confirm everything else again
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b + 1h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b - 1h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b + 15min, now) );

    //  insert something far-flung-future, should be ignored
    BOOST_CHECK_EQUAL( AS::Future,      a.getArchiveStatus(b + 24h, now) );
    BOOST_CHECK_EQUAL( AS::Future,      a.getArchiveStatus(b + 24h, now) );

    //  confirm everything else is still in there
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b + 1h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b - 1h, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 35h, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 36h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b + 15min, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b - 15min * 32, now) );

    //  insert something that is less than 24 hours in the future
    BOOST_CHECK_EQUAL( AS::NotInCache,  a.getArchiveStatus(b + 23h, now) );
    BOOST_CHECK_EQUAL( AS::Duplicate,   a.getArchiveStatus(b + 23h, now) );

    //  and confirm everything else has been removed
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b + 1h, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 1h, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 35h, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 36h, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b + 15min, now) );
    BOOST_CHECK_EQUAL( AS::BeyondCache, a.getArchiveStatus(b - 15min * 32, now) );
}

BOOST_AUTO_TEST_SUITE_END()
