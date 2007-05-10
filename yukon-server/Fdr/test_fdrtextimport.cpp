
/*
 * file test_cmdparse.cpp
 *  
 * Author: Jian Liu 
 * Date: 08/05/2005 13:25:55 
 * 
 *
 * test ctitime.cpp
 * 
 * 
 */
#define BOOST_AUTO_TEST_MAIN "Test CtiTime"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <locale>



#include "yukon.h"
#include "ctistring.h" // apparently fdrtextimport doesnt have this file.
#include "fdrtextimport.h"

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_UNIT_TEST(test_foreignToYukonQuality)
{
    CtiFDR_TextImport import;
    USHORT quality = import.ForeignToYukonQuality("failure");

    BOOST_CHECK_EQUAL(quality, NonUpdatedQuality);
    std::cout << "This is not finished " << std::endl;
}

