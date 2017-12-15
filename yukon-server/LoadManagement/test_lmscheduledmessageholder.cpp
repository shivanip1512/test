#include <boost/test/unit_test.hpp>

#include "LMScheduledMessageHolder.h"
#include "msg_pcrequest.h"


BOOST_AUTO_TEST_SUITE(test_lmscheduledmessageholder)

BOOST_AUTO_TEST_CASE( test_messageholder )
{
    LMScheduledMessageHolder testHolder;
    std::unique_ptr<CtiRequestMsg> testMessage (new CtiRequestMsg(2, "test"));
    std::unique_ptr<CtiRequestMsg> futureMessage (new CtiRequestMsg(3, "test"));
    testHolder.addMessage(CtiTime::now() - 20, 2, std::move(testMessage));
    testHolder.addMessage(CtiTime::now() + 500, 3, std::move(futureMessage));
    BOOST_CHECK(!testMessage.get());

    BOOST_CHECK(testHolder.containsMessageForGroup(2));
    BOOST_CHECK(testHolder.containsMessageForGroup(3));
    BOOST_CHECK(!testHolder.containsMessageForGroup(4));
    BOOST_CHECK(!testHolder.containsMessageForGroup(0));

    testMessage = testHolder.getAvailableMessage();
    BOOST_CHECK(testMessage.get());
    testMessage = testHolder.getAvailableMessage();
    BOOST_CHECK(!testMessage.get());
}

BOOST_AUTO_TEST_SUITE_END()