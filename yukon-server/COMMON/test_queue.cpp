/*-----------------------------------------------------------------------------*
*
* File:   test_queue.cpp
*
* Date:   10/2/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/INCLUDE/test_queue.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2007/10/09 15:45:26 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#define BOOST_AUTO_TEST_MAIN "Test CtiQueue"

#include <boost/thread/thread.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/test/floating_point_comparison.hpp>
#include "queue.h"

using namespace std;

using boost::unit_test_framework::test_suite;

inline double xtime_to_double(boost::xtime xt)
{
    return (double)xt.sec + ((double)xt.nsec * 1e-9);
}

BOOST_AUTO_UNIT_TEST(test_cti_queue_timing)
{
    CtiQueue<int, std::less<int> > testQueue;
    
    boost::xtime start_xtime,   end_xtime;
    double       start_seconds, end_seconds;

    //Test reading off an empty queue
    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(1000);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    //For the purposes of this test, the stop time is not so important.
    //The stop time is set a way out because we wont want busy system false positives.

    start_seconds = xtime_to_double(start_xtime);
    end_seconds   = xtime_to_double(end_xtime);

    //  The end time must be between 1.00 and 1.01 seconds past the start time
    BOOST_CHECK( (start_seconds + 1.00) <= end_seconds );
    BOOST_CHECK( (start_seconds + 1.01) >  end_seconds );

    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(100);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    start_seconds = xtime_to_double(start_xtime);
    end_seconds   = xtime_to_double(end_xtime);

    //  The end time must be between 0.10 and 0.11 seconds past the start time
    BOOST_CHECK( (start_seconds + 0.10) <= end_seconds );
    BOOST_CHECK( (start_seconds + 0.11) >  end_seconds );
}

BOOST_AUTO_UNIT_TEST(test_fifo_queue_timing)
{
    CtiFIFOQueue<int> testQueue;
    
    boost::xtime start_xtime,   end_xtime;
    double       start_seconds, end_seconds;

    //Test reading off an empty queue
    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(1000);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    //For the purposes of this test, the stop time is not so important.
    //The stop time is set a way out because we wont want busy system false positives.

    start_seconds = xtime_to_double(start_xtime);
    end_seconds   = xtime_to_double(end_xtime);

    //  The end time must be between 1.00 and 1.01 seconds past the start time
    BOOST_CHECK( (start_seconds + 1.00) <= end_seconds );
    BOOST_CHECK( (start_seconds + 1.01) >  end_seconds );

    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(100);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    start_seconds = xtime_to_double(start_xtime);
    end_seconds   = xtime_to_double(end_xtime);

    //  The end time must be between 0.10 and 0.11 seconds past the start time
    BOOST_CHECK( (start_seconds + 0.10) <= end_seconds );
    BOOST_CHECK( (start_seconds + 0.11) >  end_seconds );
}
