#include <boost/test/unit_test.hpp>

#include "LMScheduledMessageHolder.h"
#include "msg_pcrequest.h"


BOOST_AUTO_TEST_SUITE(test_lmscheduledmessageholder)

BOOST_AUTO_TEST_CASE( test_messageholder )
{
    LMScheduledMessageHolder testHolder;
    CtiTime currentTime = CtiTime::now();
    std::unique_ptr<CtiRequestMsg> pastMessage (new CtiRequestMsg(2, "test"));
    std::unique_ptr<CtiRequestMsg> futureMessage (new CtiRequestMsg(3, "test"));
    testHolder.addMessage(currentTime - 20, 2, std::move(pastMessage));
    testHolder.addMessage(currentTime + 500, 3, std::move(futureMessage));
    BOOST_CHECK(!pastMessage.get());

    BOOST_CHECK(testHolder.containsMessageForGroup(2));
    BOOST_CHECK(testHolder.containsMessageForGroup(3));
    BOOST_CHECK(!testHolder.containsMessageForGroup(4));
    BOOST_CHECK(!testHolder.containsMessageForGroup(0));

    std::unique_ptr<CtiRequestMsg> message = testHolder.getAvailableMessage(currentTime);
    BOOST_CHECK(message.get());
    message = testHolder.getAvailableMessage(currentTime);
    BOOST_CHECK(!message.get());
}

BOOST_AUTO_TEST_SUITE_END()