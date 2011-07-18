/*-----------------------------------------------------------------------------*
*
* File:   test_message
*
* Date:   3/26/2008
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.1.2.1 $
* DATE         :  $Date: 2008/11/10 20:47:13 $
*
* Copyright (c) 2008 Cannon Technologies. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "message.h"
#include "queue.h"

#define BOOST_AUTO_TEST_MAIN "Test Message"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_TEST_CASE(test_messagequeue)
{
    CtiQueue<CtiMessage, std::greater<CtiMessage> > test_queue;

    CtiMessage msg1, msg2;

    msg1.setMessagePriority(0);
    msg1.setSource("first");

    test_queue.putQueue(&msg1);

    msg2.setMessagePriority(15);
    msg2.setSource("second");

    test_queue.putQueue(&msg2);

    BOOST_CHECK_EQUAL(test_queue.getQueue(), &msg2);
    BOOST_CHECK_EQUAL(test_queue.getQueue(), &msg1);
}
