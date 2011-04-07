#define BOOST_AUTO_TEST_MAIN "Test BchBehavior"

#include "yukon.h"
#include "boostutil.h"
#include "BehaviorCollection.h"
#include "BchBehavior.h"
#include "types.h"

#include <time.h>
#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using namespace Cti::Simulator;

BOOST_AUTO_TEST_CASE(test_bch_behavior)
{
    srand(time(NULL));
    // Apparently the first random needs to be dumped or else
    // the random number will be some linear function as 
    // opposed to being truly (or rather pseudo-) random.
    rand();

    BehaviorCollection<PlcBehavior> behaviorCollection;
    std::auto_ptr<PlcBehavior> d(new BchBehavior());
    d->setChance(1000);

    behaviorCollection.push_back(d);

    bytes message, reference;

    // Pushing data onto each vector. The BchBehavior should modify the 
    // second-to-last nibble of the message.
    message.push_back(0x04);
    message.push_back(0x08);
    message.push_back(0x0F);
    reference.push_back(0x04);
    reference.push_back(0x08);
    reference.push_back(0x1F); // This is where the change should occur!

    behaviorCollection.processMessage(message);

    for (int i = 0; i < message.size(); i++)
    {
        // Check to see that the BchBehavior correctly 
        // processed the message and put the information back
        // in the intended order to match the reference vector.
        BOOST_CHECK_EQUAL(message.at(i), reference.at(i));
    }

    reference.pop_back();
    reference.push_back(0x0F);

    behaviorCollection.processMessage(message);

    // After a second processing, the bit that was initially flipped
    // should now be flipped back to its original state. 
    for (int i = 0; i < message.size(); i++)
    {
        // Check to see that the delayBehavior correctly 
        // processed the message and put the information back
        // in the intended order to match the reference vector.
        BOOST_CHECK_EQUAL(message.at(i), reference.at(i));
    }
}
