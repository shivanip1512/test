#define BOOST_AUTO_TEST_MAIN "Test old queues"

#include <boost/thread/thread.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/test/floating_point_comparison.hpp>
#include <boost/bind.hpp>
#include <iostream>

#include "dsm2.h"  //  for CtiOutMessage
#include "queues.h"

using namespace std;
using boost::unit_test_framework::test_suite;


inline double xtime_duration(boost::xtime &begin, boost::xtime &end)
{
    return static_cast<double>(end.sec  - begin.sec) +
           static_cast<double>(end.nsec - begin.nsec) * 1e-9;
}

struct instance_counter
{
    int instance;
    static int counter;  //  keeps a counter instance to track construction and destruction

    instance_counter()  { instance = ++counter; };
    ~instance_counter() { counter--; };
};

int instance_counter::counter;


struct test_element
{
    long value, insertOrder;

    test_element(long value_, long insertOrder_) : value(value_), insertOrder(insertOrder_) { }

    bool operator>(const test_element& rhs) const  {  return value > rhs.value;  }
    bool operator<(const test_element& rhs) const  {  return value < rhs.value;  }
};

BOOL findRequestId(void *request_id, void *om)
{
    return om && (unsigned long)request_id == ((OUTMESS *)om)->Request.GrpMsgID;
}

BOOST_AUTO_TEST_CASE(test_queue)
{
    HCTIQUEUE QueueHandle;

    ULONG elementCount;
    CtiOutMessage outmessages[5];

    CreateQueue(&QueueHandle, QUE_PRIORITY);

    outmessages[0].Request.GrpMsgID = 217;  outmessages[0].Priority = 13;
    outmessages[1].Request.GrpMsgID = 217;  outmessages[1].Priority = 12;
    outmessages[2].Request.GrpMsgID = 218;  outmessages[2].Priority = 10;
    outmessages[3].Request.GrpMsgID = 217;  outmessages[3].Priority = 11;
    outmessages[4].Request.GrpMsgID = 219;  outmessages[4].Priority = 15;

    WriteQueue(QueueHandle, outmessages[0].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[0], outmessages[0].Priority, &elementCount);

    BOOST_CHECK_EQUAL(elementCount, 1);

    WriteQueue(QueueHandle, outmessages[1].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[1], outmessages[1].Priority, &elementCount);
    WriteQueue(QueueHandle, outmessages[2].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[2], outmessages[2].Priority, &elementCount);
    WriteQueue(QueueHandle, outmessages[3].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[3], outmessages[3].Priority, &elementCount);
    WriteQueue(QueueHandle, outmessages[4].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[4], outmessages[4].Priority, &elementCount);

    BOOST_CHECK_EQUAL(elementCount, 5);

    //  expected sort order:
    //  insert order / index / requestid / priority
    //     5             4        219         15
    //     1             0        217         13
    //     2             1        217         12
    //     4             3        217         11
    //     3             2        218         10

    unsigned long requestID;

    requestID = 999;

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)requestID, findRequestId), 0);  //   returns 0 (no element ID) if nothing found

    requestID = 218;

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)requestID, findRequestId), 3);  //  this is the insert order

    requestID = 217;

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)requestID, findRequestId), 1);

    requestID = 219;

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)requestID, findRequestId), 5);

    void *item;
    unsigned long item_length;
    unsigned char priority;

    int return_code;
    REQUESTDATA Request;

    return_code = ReadQueue(QueueHandle, &Request, &item_length, &item, 0, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, 15);
    BOOST_CHECK_EQUAL(item, &outmessages[4]);
    BOOST_CHECK_EQUAL(elementCount, 4);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadQueue(QueueHandle, &Request, &item_length, &item, 0, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, 13);
    BOOST_CHECK_EQUAL(item, &outmessages[0]);
    BOOST_CHECK_EQUAL(elementCount, 3);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadQueue(QueueHandle, &Request, &item_length, &item, 0, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, 12);
    BOOST_CHECK_EQUAL(item, &outmessages[1]);
    BOOST_CHECK_EQUAL(elementCount, 2);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadQueue(QueueHandle, &Request, &item_length, &item, 0, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, 11);
    BOOST_CHECK_EQUAL(item, &outmessages[3]);
    BOOST_CHECK_EQUAL(elementCount, 1);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadQueue(QueueHandle, &Request, &item_length, &item, 0, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, 10);
    BOOST_CHECK_EQUAL(item, &outmessages[2]);
    BOOST_CHECK_EQUAL(elementCount, 0);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));
}


/*
{
    CleanQueue(QueueHandle);

    ApplyQueue(QueueHandle);

    test_element *element;
}
*/
