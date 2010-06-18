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
#include "ctistring.h"

using boost::unit_test_framework::test_suite;

BOOST_AUTO_TEST_CASE(testString)
{
    const size_t test_size = sizeof(inputString) / sizeof(inputString[0]);

    for( int i = 0; i < test_size; i++ )
    {
        CtiCommandParser parse(inputString[i]);

        BOOST_CHECK_EQUAL(parse.asString(), parse_asString[i]);
    }
}

BOOST_AUTO_TEST_CASE(testDeviceGroupQuotes)
{
    size_t nstart;
    size_t nstop;

    static const CtiString str_quoted_token("((\".*\")|('.*'))");
    static const boost::regex re_grp    (CtiString("select group ")       + str_quoted_token);

    const string lead_trail_apos = "getvalue kwh update timeout 1800 select group '/Meters/Collection/'Test Group''";
    const string mid_apos        = "getvalue kwh update timeout 1800 select group '/Meters/Collection/Intern's Group'";

    static const CtiString lead_trail_outcome = "'/Meters/Collection/'Test Group''";
    static const CtiString mid_outcome        = "'/Meters/Collection/Intern's Group'";

    CtiString CmdStr, lead_trail_token, mid_token;

    {
        CmdStr = lead_trail_apos;
        lead_trail_token = CmdStr.match(re_grp);

        nstart = lead_trail_token.index("group ", &nstop);
        nstop += nstart;

        lead_trail_token = lead_trail_token.match((const boost::regex)str_quoted_token, nstop);
    }
            
    {
        CmdStr = mid_apos;
        mid_token        = CmdStr.match(re_grp);

        nstart = mid_token.index("group ", &nstop);
        nstop += nstart;

        mid_token = mid_token.match((const boost::regex)str_quoted_token, nstop);
    }

    BOOST_CHECK_EQUAL(lead_trail_token, lead_trail_outcome);
    BOOST_CHECK_EQUAL(mid_token, mid_outcome);
        
}


