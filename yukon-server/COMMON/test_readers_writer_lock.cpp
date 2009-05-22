#define BOOST_AUTO_TEST_MAIN "Test readers/writer lock"

#include "yukon.h"
#include <boost/test/unit_test.hpp>
using boost::unit_test_framework::test_suite;

#include "readers_writer_lock.h"

#include <sstream>

BOOST_AUTO_TEST_CASE(test_lock)
{
    Cti::readers_writer_lock_t lock;
    std::ostringstream s;

    s.str("");
    s << "[unlocked]";

    BOOST_CHECK_EQUAL(static_cast<std::string>(lock), s.str());

    lock.acquireRead();

    s.str("");
    s << "[r:" << std::hex << GetCurrentThreadId() << ",1]";

    BOOST_CHECK_EQUAL(static_cast<std::string>(lock), s.str());

    try
    {
        lock.acquireWrite();
        BOOST_FAIL("Acquiring write after read did not throw");
    }
    catch(...)
    {
    }

    BOOST_CHECK_EQUAL(static_cast<std::string>(lock), s.str());

    lock.releaseRead();

    s.str("");
    s << "[unlocked]";

    BOOST_CHECK_EQUAL(static_cast<std::string>(lock), s.str());

    lock.acquireWrite();

    s.str("");
    s << "[w:" << std::hex << GetCurrentThreadId() << ",1]";

    BOOST_CHECK_EQUAL(static_cast<std::string>(lock), s.str());

    lock.acquireRead();

    s.str("");
    s << "[w:" << std::hex << GetCurrentThreadId() << ",1/r:" << GetCurrentThreadId() << ",1]";

    BOOST_CHECK_EQUAL(static_cast<std::string>(lock), s.str());

    lock.releaseRead();

    s.str("");
    s << "[w:" << std::hex << GetCurrentThreadId() << ",1]";

    BOOST_CHECK_EQUAL(static_cast<std::string>(lock), s.str());

    lock.releaseWrite();

    s.str("");
    s << "[unlocked]";

    BOOST_CHECK_EQUAL(static_cast<std::string>(lock), s.str());
}

