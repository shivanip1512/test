/*-----------------------------------------------------------------------------*
*
* File:   test_queue.cpp
*
* Date:   10/2/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/INCLUDE/test_queue.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/07/08 22:47:06 $
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

    //  The end time must be between 1.0 and 1.2 seconds past the start time
    BOOST_CHECK( (start_seconds + 1.0) <= end_seconds );
    BOOST_CHECK( (start_seconds + 1.2) >  end_seconds );

    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(100);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    start_seconds = xtime_to_double(start_xtime);
    end_seconds   = xtime_to_double(end_xtime);

    //  The end time must be between 0.10 and 0.15 seconds past the start time
    BOOST_CHECK( (start_seconds + 0.10) <= end_seconds );
    BOOST_CHECK( (start_seconds + 0.15) >  end_seconds );
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

    //  The end time must be between 1.0 and 1.2 seconds past the start time
    BOOST_CHECK( (start_seconds + 1.0) <= end_seconds );
    BOOST_CHECK( (start_seconds + 1.2) >  end_seconds );

    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(100);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    start_seconds = xtime_to_double(start_xtime);
    end_seconds   = xtime_to_double(end_xtime);

    //  The end time must be between 0.10 and 0.15 seconds past the start time
    BOOST_CHECK( (start_seconds + 0.10) <= end_seconds );
    BOOST_CHECK( (start_seconds + 0.15) >  end_seconds );
}

struct queueTestStruct
{
    long value, insertOrder;

    bool operator>(const queueTestStruct& rhs) const  {  return value > rhs.value;  }
    bool operator<(const queueTestStruct& rhs) const  {  return value < rhs.value;  }
};

void applySetInsertOrderToInt(queueTestStruct *&dataStruct, void* d)
{
    dataStruct->insertOrder = (int)d;
}

BOOST_AUTO_UNIT_TEST(test_cti_queue_sort)
{
    CtiQueue<queueTestStruct, greater<queueTestStruct> > greaterQueue;
    CtiQueue<queueTestStruct, less<queueTestStruct> > lessQueue;

    queueTestStruct *tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 1;
    greaterQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 2;
    greaterQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 3;
    greaterQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 2;
    tempMsg->insertOrder = 1;
    greaterQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 4;
    greaterQueue.putQueue(tempMsg);

    //  This object is meant to sort by >, so 3's should come off first.
    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 2);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 1);
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 1);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 1);
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 2);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 1);
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 3);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 1);
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 4);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;


    // Do the lessQueue

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 1;
    lessQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 2;
    lessQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 3;
    lessQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 2;
    tempMsg->insertOrder = 1;
    lessQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 4;
    lessQueue.putQueue(tempMsg);

    //  This object is meant to sort by <, so 1's should come off first.
    tempMsg = lessQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 1);
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 1);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
    tempMsg = lessQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 1);
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 2);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
    tempMsg = lessQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 1);
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 3);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
    tempMsg = lessQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 1);
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 4);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
    tempMsg = lessQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->value, 2);
    //std::cout << "value, insertorder: " << tempMsg->value << " " << tempMsg->insertOrder << "\n";
    delete tempMsg;
}

BOOST_AUTO_UNIT_TEST(test_cti_queue_apply)
{
    CtiQueue<queueTestStruct, greater<queueTestStruct> > greaterQueue;

    queueTestStruct *tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 1;
    greaterQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 2;
    greaterQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 3;
    greaterQueue.putQueue(tempMsg);

    tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 4;
    greaterQueue.putQueue(tempMsg);

    greaterQueue.apply(applySetInsertOrderToInt, (void *)5);

    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 5);
    delete tempMsg;
    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 5);
    delete tempMsg;
    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 5);
    delete tempMsg;
    tempMsg = greaterQueue.getQueue();
    BOOST_CHECK_EQUAL(tempMsg->insertOrder, 5);
    delete tempMsg;
}

//This inserts many many objects into a queue and makes sure it does not take
//an unreasonable amount of time
BOOST_AUTO_UNIT_TEST(test_cti_queue_sort_speed)
{
    CtiQueue<queueTestStruct, greater<queueTestStruct> > greaterQueue;

    queueTestStruct *tempMsg = CTIDBG_new queueTestStruct();
    tempMsg->value = 1;
    tempMsg->insertOrder = 1;
    greaterQueue.putQueue(tempMsg);

    CtiTime start;
    for( int a = 1; a<=100;a++ )
    {
        for( int i=0; i<1000; i++ )
        {
            tempMsg = CTIDBG_new queueTestStruct();
            tempMsg->value = 1;
            tempMsg->insertOrder = 1;
            greaterQueue.putQueue(tempMsg);
        }
    }
    CtiTime stop;
    BOOST_CHECK(stop.seconds() < (start.seconds() + 4));//4 is quite a while actually.

}
