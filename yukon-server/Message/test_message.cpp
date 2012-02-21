#include <boost/test/unit_test.hpp>

#include "message.h"
#include "queue.h"

BOOST_AUTO_TEST_SUITE( test_message )

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

BOOST_AUTO_TEST_SUITE_END()
