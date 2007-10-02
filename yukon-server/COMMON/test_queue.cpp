/*-----------------------------------------------------------------------------*
*
* File:   test_queue.cpp
*
* Date:   10/2/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/INCLUDE/test_queue.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/10/02 19:07:19 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#define BOOST_AUTO_TEST_MAIN "Test CtiQueue"

#include <boost/thread/thread.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include "queue.h"

using namespace std;

using boost::unit_test_framework::test_suite;

BOOST_AUTO_UNIT_TEST(test_cti_queue_timing)
{
    CtiQueue<int, std::less<int> > testQueue;
    
    //Test reading off an empty queue
    struct boost::xtime startTime, endTime;
    boost::xtime_get(&startTime, boost::TIME_UTC);
    testQueue.getQueue(1000);
    boost::xtime_get(&endTime, boost::TIME_UTC);

    //For the purposes of this test, the stop time is not so important.
    //The stop time is set a way out because we wont want busy system false positives.
    BOOST_CHECK( (unsigned int)startTime.sec < (unsigned int)endTime.sec );
    BOOST_CHECK( (unsigned int)startTime.sec + 2 > (unsigned int)endTime.sec );

    boost::xtime_get(&startTime, boost::TIME_UTC);
    testQueue.getQueue(100);
    boost::xtime_get(&endTime, boost::TIME_UTC);

    if( (unsigned int)startTime.sec == (unsigned int)endTime.sec )
    {
        unsigned int startMS = startTime.nsec/1000000;
        unsigned int stopMS = endTime.nsec/1000000;
        BOOST_CHECK( startMS + 75 < stopMS );
        BOOST_CHECK( startMS + 300 > stopMS );
    }
    else
    {
        unsigned int startMS = startTime.nsec/1000000;
        unsigned int stopMS = endTime.nsec/1000000 + 1000;
        BOOST_CHECK( startMS + 75 < stopMS );
        BOOST_CHECK( startMS + 300 > stopMS ); 
    }
}

BOOST_AUTO_UNIT_TEST(test_fifo_queue_timing)
{
    CtiFIFOQueue<int> testQueue;
    
    //Test reading off an empty queue
    struct boost::xtime startTime, endTime;
    boost::xtime_get(&startTime, boost::TIME_UTC);
    testQueue.getQueue(1000);
    boost::xtime_get(&endTime, boost::TIME_UTC);

    //For the purposes of this test, the stop time is not so important.
    //The stop time is set a way out because we wont want busy system false positives.
    BOOST_CHECK( (unsigned int)startTime.sec < (unsigned int)endTime.sec );
    BOOST_CHECK( (unsigned int)startTime.sec + 2 > (unsigned int)endTime.sec );

    boost::xtime_get(&startTime, boost::TIME_UTC);
    testQueue.getQueue(100);
    boost::xtime_get(&endTime, boost::TIME_UTC);

    if( (unsigned int)startTime.sec == (unsigned int)endTime.sec )
    {
        unsigned int startMS = startTime.nsec/1000000;
        unsigned int stopMS = endTime.nsec/1000000;
        BOOST_CHECK( startMS + 75 < stopMS );
        BOOST_CHECK( startMS + 300 > stopMS );
    }
    else
    {
        unsigned int startMS = startTime.nsec/1000000;
        unsigned int stopMS = endTime.nsec/1000000 + 1000;
        BOOST_CHECK( startMS + 75 < stopMS );
        BOOST_CHECK( startMS + 300 > stopMS ); 
    }
}
