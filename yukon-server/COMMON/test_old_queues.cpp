#include <boost/test/unit_test.hpp>

#include "dsm2.h"  //  for CtiOutMessage
#include "queues.h"

BOOST_AUTO_TEST_SUITE( test_old_queues )

using namespace std;

bool findAll(void *, void *)
{
    return true;
}

bool findRequestIdBool(void *request_id, void *om)
{
    return om && (unsigned long)request_id == ((OUTMESS *)om)->Request.GrpMsgID;
}

BOOL findRequestIdBOOL(void *request_id, void *om)
{
    return findRequestIdBool(request_id, om);
}

void setDeviceId(void *device_id, void *om)
{
     om && (((OUTMESS *)om)->DeviceID = (long)device_id);
}

void noOp(void *, void *)
{
}

void initOutMessages(CtiOutMessage outmessages[7])
{
    outmessages[0].DeviceID = 0; outmessages[0].Request.GrpMsgID = 217;  outmessages[0].Priority = 13;
    outmessages[1].DeviceID = 1; outmessages[1].Request.GrpMsgID = 217;  outmessages[1].Priority = 12;
    outmessages[2].DeviceID = 2; outmessages[2].Request.GrpMsgID = 218;  outmessages[2].Priority = 10;
    outmessages[3].DeviceID = 3; outmessages[3].Request.GrpMsgID = 217;  outmessages[3].Priority = 11;
    outmessages[4].DeviceID = 4; outmessages[4].Request.GrpMsgID = 219;  outmessages[4].Priority = 15;
    outmessages[5].DeviceID = 5; outmessages[5].Request.GrpMsgID = 220;  outmessages[5].Priority = 12;
    outmessages[6].DeviceID = 6; outmessages[6].Request.GrpMsgID = 220;  outmessages[6].Priority = 12;
}

BOOST_AUTO_TEST_CASE(test_write_queue)
{
    HCTIQUEUE QueueHandle;

    ULONG elementCount;
    CtiOutMessage outmessages[7];

    CreateQueue(&QueueHandle);

    initOutMessages(outmessages);

    //  inserts not using elementCount
    WriteQueue(QueueHandle, outmessages[0].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[0], outmessages[0].Priority);
    WriteQueue(QueueHandle, outmessages[1].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[1], outmessages[1].Priority);
    WriteQueue(QueueHandle, outmessages[2].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[2], outmessages[2].Priority);
    WriteQueue(QueueHandle, outmessages[3].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[3], outmessages[3].Priority);
    WriteQueue(QueueHandle, outmessages[4].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[4], outmessages[4].Priority);

    QueryQueue(QueueHandle, &elementCount);

    BOOST_CHECK_EQUAL(elementCount, 5);

    //  inserts using elementCount
    WriteQueue(QueueHandle, outmessages[5].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[5], outmessages[5].Priority, &elementCount);
    WriteQueue(QueueHandle, outmessages[6].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[6], outmessages[6].Priority, &elementCount);

    BOOST_CHECK_EQUAL(elementCount, 7);

    CloseQueue(QueueHandle);
}


BOOST_AUTO_TEST_CASE(test_read_queue_front)
{
    HCTIQUEUE QueueHandle;

    CtiOutMessage outmessages[7];

    CreateQueue(&QueueHandle);

    initOutMessages(outmessages);

    WriteQueue(QueueHandle, outmessages[0].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[0], outmessages[0].Priority);
    WriteQueue(QueueHandle, outmessages[1].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[1], outmessages[1].Priority);
    WriteQueue(QueueHandle, outmessages[2].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[2], outmessages[2].Priority);
    WriteQueue(QueueHandle, outmessages[3].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[3], outmessages[3].Priority);
    WriteQueue(QueueHandle, outmessages[4].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[4], outmessages[4].Priority);
    WriteQueue(QueueHandle, outmessages[5].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[5], outmessages[5].Priority);
    WriteQueue(QueueHandle, outmessages[6].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[6], outmessages[6].Priority);

    //  expected sort order:
    //  insert counter / index / requestid / priority
    //        5            4        219         15
    //        1            0        217         13
    //        2            1        217         12
    //        6            5        220         12
    //        7            6        220         12
    //        4            3        217         11
    //        3            2        218         10

    void *item;
    unsigned long item_length;
    unsigned char priority;
    unsigned long requestID;

    int return_code;

    return_code = ReadFrontElement(QueueHandle, &item_length, &item, false, &priority);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[4].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[4]);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadFrontElement(QueueHandle, &item_length, &item, false, &priority);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[0].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[0]);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadFrontElement(QueueHandle, &item_length, &item, false, &priority);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[1].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[1]);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadFrontElement(QueueHandle, &item_length, &item, false, &priority);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[5].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[5]);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadFrontElement(QueueHandle, &item_length, &item, false, &priority);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[6].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[6]);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadFrontElement(QueueHandle, &item_length, &item, false, &priority);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[3].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[3]);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadFrontElement(QueueHandle, &item_length, &item, false, &priority);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[2].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[2]);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    CloseQueue(QueueHandle);
}

BOOST_AUTO_TEST_CASE(test_read_queue_by_id)
{
    HCTIQUEUE QueueHandle;

    ULONG elementCount;
    CtiOutMessage outmessages[7];

    CreateQueue(&QueueHandle);

    initOutMessages(outmessages);

    WriteQueue(QueueHandle, outmessages[0].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[0], outmessages[0].Priority);
    WriteQueue(QueueHandle, outmessages[1].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[1], outmessages[1].Priority);
    WriteQueue(QueueHandle, outmessages[2].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[2], outmessages[2].Priority);
    WriteQueue(QueueHandle, outmessages[3].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[3], outmessages[3].Priority);
    WriteQueue(QueueHandle, outmessages[4].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[4], outmessages[4].Priority);
    WriteQueue(QueueHandle, outmessages[5].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[5], outmessages[5].Priority);
    WriteQueue(QueueHandle, outmessages[6].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[6], outmessages[6].Priority);

    //  expected sort order:
    //  insert counter / index / requestid / priority
    //        5            4        219         15
    //        1            0        217         13
    //        2            1        217         12
    //        6            5        220         12
    //        7            6        220         12
    //        4            3        217         11
    //        3            2        218         10

    void *item;
    unsigned long item_length;
    unsigned char priority;
    unsigned long requestID;

    int return_code;

    return_code = ReadElementById(QueueHandle, &item_length, &item, 4, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[3].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[3]);
    BOOST_CHECK_EQUAL(elementCount, 6);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadElementById(QueueHandle, &item_length, &item, 1, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[0].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[0]);
    BOOST_CHECK_EQUAL(elementCount, 5);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadElementById(QueueHandle, &item_length, &item, 5, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[4].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[4]);
    BOOST_CHECK_EQUAL(elementCount, 4);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadElementById(QueueHandle, &item_length, &item, 3, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[2].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[2]);
    BOOST_CHECK_EQUAL(elementCount, 3);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadElementById(QueueHandle, &item_length, &item, 2, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[1].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[1]);
    BOOST_CHECK_EQUAL(elementCount, 2);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadElementById(QueueHandle, &item_length, &item, 6, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[5].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[5]);
    BOOST_CHECK_EQUAL(elementCount, 1);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    return_code = ReadElementById(QueueHandle, &item_length, &item, 7, false, &priority, &elementCount);

    BOOST_CHECK_EQUAL(return_code, 0);
    BOOST_CHECK_EQUAL(priority, outmessages[6].Priority);
    BOOST_CHECK_EQUAL(item, &outmessages[6]);
    BOOST_CHECK_EQUAL(elementCount, 0);
    BOOST_CHECK_EQUAL(item_length, sizeof(CtiOutMessage));

    CloseQueue(QueueHandle);
}

BOOST_AUTO_TEST_CASE(test_search_queue)
{
    HCTIQUEUE QueueHandle;

    ULONG elementCount;
    CtiOutMessage outmessages[7];

    CreateQueue(&QueueHandle);

    initOutMessages(outmessages);

    WriteQueue(QueueHandle, outmessages[0].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[0], outmessages[0].Priority);
    WriteQueue(QueueHandle, outmessages[1].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[1], outmessages[1].Priority);
    WriteQueue(QueueHandle, outmessages[2].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[2], outmessages[2].Priority);
    WriteQueue(QueueHandle, outmessages[3].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[3], outmessages[3].Priority);
    WriteQueue(QueueHandle, outmessages[4].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[4], outmessages[4].Priority);
    WriteQueue(QueueHandle, outmessages[5].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[5], outmessages[5].Priority);
    WriteQueue(QueueHandle, outmessages[6].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[6], outmessages[6].Priority);

    //  expected sort order:
    //  insert counter / index / requestid / priority
    //        5            4        219         15
    //        1            0        217         13
    //        2            1        217         12
    //        6            5        220         12
    //        7            6        220         12
    //        4            3        217         11
    //        3            2        218         10

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)999, findRequestIdBOOL), 0);  //  returns 0 (no element ID) if nothing found

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)218, findRequestIdBOOL), 3);  //  this is the insert counter value of the
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)217, findRequestIdBOOL), 1);  //     first/topmost element with this request ID
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)219, findRequestIdBOOL), 5);
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)220, findRequestIdBOOL), 6);

    CloseQueue(QueueHandle);
}


BOOST_AUTO_TEST_CASE(test_clean_queue)
{
    HCTIQUEUE QueueHandle;

    ULONG elementCount;
    CtiOutMessage outmessages[7];

    CreateQueue(&QueueHandle);

    initOutMessages(outmessages);

    WriteQueue(QueueHandle, outmessages[0].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[0], outmessages[0].Priority);
    WriteQueue(QueueHandle, outmessages[1].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[1], outmessages[1].Priority);
    WriteQueue(QueueHandle, outmessages[2].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[2], outmessages[2].Priority);
    WriteQueue(QueueHandle, outmessages[3].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[3], outmessages[3].Priority);
    WriteQueue(QueueHandle, outmessages[4].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[4], outmessages[4].Priority);
    WriteQueue(QueueHandle, outmessages[5].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[5], outmessages[5].Priority);
    WriteQueue(QueueHandle, outmessages[6].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[6], outmessages[6].Priority);

    //  expected sort order:
    //  insert counter / index / requestid / priority
    //        5            4        219         15
    //        1            0        217         13
    //        2            1        217         12
    //        6            5        220         12
    //        7            6        220         12
    //        4            3        217         11
    //        3            2        218         10

    QueryQueue(QueueHandle, &elementCount);
    BOOST_CHECK_EQUAL(elementCount, 7);

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)217, findRequestIdBOOL), 1);
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)218, findRequestIdBOOL), 3);
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)220, findRequestIdBOOL), 6);

    //  clean request ID 220
    CleanQueue(QueueHandle, (void *)220, findRequestIdBool, noOp, NULL);

    QueryQueue(QueueHandle, &elementCount);
    BOOST_CHECK_EQUAL(elementCount, 5);

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)217, findRequestIdBOOL), 1);
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)218, findRequestIdBOOL), 3);
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)220, findRequestIdBOOL), 0);

    //  clean everything out
    CleanQueue(QueueHandle, (void *)0, findAll, noOp, NULL);

    QueryQueue(QueueHandle, &elementCount);
    BOOST_CHECK_EQUAL(elementCount, 0);

    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)217, findRequestIdBOOL), 0);
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)218, findRequestIdBOOL), 0);
    BOOST_CHECK_EQUAL(SearchQueue(QueueHandle, (void *)220, findRequestIdBOOL), 0);

    CloseQueue(QueueHandle);
}


BOOST_AUTO_TEST_CASE(test_apply_queue)
{
    HCTIQUEUE QueueHandle;

    CtiOutMessage outmessages[7];

    CreateQueue(&QueueHandle);

    initOutMessages(outmessages);

    WriteQueue(QueueHandle, outmessages[0].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[0], outmessages[0].Priority);
    WriteQueue(QueueHandle, outmessages[1].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[1], outmessages[1].Priority);
    WriteQueue(QueueHandle, outmessages[2].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[2], outmessages[2].Priority);
    WriteQueue(QueueHandle, outmessages[3].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[3], outmessages[3].Priority);
    WriteQueue(QueueHandle, outmessages[4].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[4], outmessages[4].Priority);

    ApplyQueue(QueueHandle, (void *)11235L, setDeviceId);

    BOOST_CHECK_EQUAL(11235, outmessages[0].DeviceID);
    BOOST_CHECK_EQUAL(11235, outmessages[1].DeviceID);
    BOOST_CHECK_EQUAL(11235, outmessages[2].DeviceID);
    BOOST_CHECK_EQUAL(11235, outmessages[3].DeviceID);
    BOOST_CHECK_EQUAL(11235, outmessages[4].DeviceID);
    BOOST_CHECK_EQUAL(    5, outmessages[5].DeviceID);
    BOOST_CHECK_EQUAL(    6, outmessages[6].DeviceID);

    WriteQueue(QueueHandle, outmessages[5].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[5], outmessages[5].Priority);
    WriteQueue(QueueHandle, outmessages[6].Request.GrpMsgID, sizeof(CtiOutMessage), &outmessages[6], outmessages[6].Priority);

    ApplyQueue(QueueHandle, (void *)112358L, setDeviceId);

    BOOST_CHECK_EQUAL(112358, outmessages[0].DeviceID);
    BOOST_CHECK_EQUAL(112358, outmessages[1].DeviceID);
    BOOST_CHECK_EQUAL(112358, outmessages[2].DeviceID);
    BOOST_CHECK_EQUAL(112358, outmessages[3].DeviceID);
    BOOST_CHECK_EQUAL(112358, outmessages[4].DeviceID);
    BOOST_CHECK_EQUAL(112358, outmessages[5].DeviceID);
    BOOST_CHECK_EQUAL(112358, outmessages[6].DeviceID);

    CloseQueue(QueueHandle);
}

BOOST_AUTO_TEST_SUITE_END()
