#define BOOST_AUTO_TEST_MAIN "Test delayBehavior"

#include "yukon.h"
#include "boostutil.h"
#include "BehaviorCollection.h"
#include "DelayBehavior.h"
#include "types.h"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using namespace Cti::Simulator;

BOOST_AUTO_TEST_CASE(test_delay_behavior)
{
    BehaviorCollection<CommsBehavior> behaviorCollection;
    std::auto_ptr<CommsBehavior> d(new DelayBehavior());
    d->setChance(100);

    behaviorCollection.push_back_behavior(d);

    bytes message, temp;
    message.push_back(0x04);
    message.push_back(0x08);
    message.push_back(0x0F);
    temp.push_back(0x04);
    temp.push_back(0x08);
    temp.push_back(0x0F);

    BOOST_CHECK_EQUAL(message.size(), 3);

    behaviorCollection.processMessage(message);

    BOOST_CHECK_EQUAL(message.size(), 0);

    message.push_back(0x10);
    message.push_back(0x17);
    message.push_back(0x2A);
    temp.push_back(0x10);
    temp.push_back(0x17);
    temp.push_back(0x2A);

    behaviorCollection.processMessage(message);

    BOOST_CHECK_EQUAL(message.size(), 6);
    BOOST_CHECK_EQUAL(message.size(), temp.size());

    if (message.size() == temp.size())
    {
        for (int i = 0; i < message.size(); i++)
        {
            BOOST_CHECK_EQUAL(message.at(i), temp.at(i));
        }
    }
}
