#include <boost/test/unit_test.hpp>

#include "CallSite.h"

BOOST_AUTO_TEST_SUITE( test_CallSite )

BOOST_AUTO_TEST_CASE(test_trimPath)
{
    struct test_CallSite : Cti::CallSite
    {
        using CallSite::trimPath;
    };

    auto p1 = "";
    auto p2 = "\\";
    auto p3 = "no_slashes_here";
    auto p4 = R"(c:\jenkins\workspace\yukon_server_head\yukon\yukon-server\common\database_bulk_writer.cpp)";

    BOOST_CHECK_EQUAL(test_CallSite::trimPath(p1), "");
    BOOST_CHECK_EQUAL(test_CallSite::trimPath(p2), "");
    BOOST_CHECK_EQUAL(test_CallSite::trimPath(p3), "no_slashes_here");
    BOOST_CHECK_EQUAL(test_CallSite::trimPath(p4), "database_bulk_writer.cpp");
}

BOOST_AUTO_TEST_SUITE_END()