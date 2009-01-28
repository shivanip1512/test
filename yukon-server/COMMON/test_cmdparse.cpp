/*
 * file test_cmdparse.cpp
 *
 * Author: Jian Liu
 * Date: 07/18/2005 11:23:53
 *
 *
 * test cmdparse.cpp
 *
 * use test_cmdparse_input.h as the input
 * should expect output in test_cmdparse_output.h
 *
 */

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test CommandParse"
#include <boost/test/unit_test.hpp>

#include "boostutil.h"

#include <string>

#include "test_cmdparse_input.h"
#include "test_cmdparse_output.h"
#include "cmdparse.h"

using boost::unit_test_framework::test_suite;


BOOST_AUTO_TEST_CASE(testString)
{
    for( int i = 0; i < TEST_SIZE; i++ )
    {
        CtiCommandParser parse(inputString[i]);

        BOOST_CHECK_EQUAL(parse.asString(), parse_asString[i]);
    }
}


