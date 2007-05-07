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

#include <boost/test/unit_test.hpp>

#include <string>

#include "test_cmdparse_input.h"
#include "test_cmdparse_output.h"
#include "cmdparse.h"

#define BOOST_AUTO_TEST_MAIN "Test CommandParse"
#include <boost/test/auto_unit_test.hpp>
using boost::unit_test_framework::test_suite;


BOOST_AUTO_UNIT_TEST(testString)
{
    for(int i=0; i<TEST_SIZE; i++){
        CtiCommandParser  parse(inputString[i]);
        std::cout << "input string: " << inputString[i] << std::endl;
        BOOST_CHECK_EQUAL( parse.asString(), outputString[i]); 
    }
}


