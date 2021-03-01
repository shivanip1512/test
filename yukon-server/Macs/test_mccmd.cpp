#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "mccmd.h"

BOOST_AUTO_TEST_SUITE( test_mccmd )

BOOST_AUTO_TEST_CASE( test_BuildCommandString )
{
    {
        char *argv[] = {"this", "is", "a", "test"};
        const int argc = 0;

        BOOST_CHECK_EQUAL( BuildCommandString(argc, argv), "");
    }

    {
        char *argv[] = {"this", "is", "a", "test"};
        const int argc = 1;

        BOOST_CHECK_EQUAL( BuildCommandString(argc, argv), "this");
    }

    {
        char *argv[] = {"this", "is", "a", "test"};
        const int argc = 2;

        BOOST_CHECK_EQUAL( BuildCommandString(argc, argv), "this is");
    }

    {
        char *argv[] = {"this", "is", "a", "test"};
        const int argc = 3;

        BOOST_CHECK_EQUAL( BuildCommandString(argc, argv), "this is a");
    }

    {
        char *argv[] = {"this", "is", "a", "test"};
        const int argc = 4;

        BOOST_CHECK_EQUAL( BuildCommandString(argc, argv), "this is a test");
    }
}

BOOST_AUTO_TEST_SUITE_END()
