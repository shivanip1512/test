#define BOOST_AUTO_TEST_MAIN "Test behaviorCollection"

#include "yukon.h"
#include "boostutil.h"
#include "BehaviorCollection.h"
#include "logger.h"

#include <boost/test/unit_test.hpp>

class IntBehavior
{
public:
    typedef int target_type;

    virtual void applyBehavior(target_type &value)=0;
};

class MultByTwentyBehavior : public IntBehavior
{
public:
    MultByTwentyBehavior() {};
    virtual void applyBehavior(int &value)
    {
            value *= 20;
    }
};

class AddOneBehavior : public IntBehavior
{
public:
    AddOneBehavior() {};
    virtual void applyBehavior(int &value)
    {
        value += 1;
    }
};

using boost::unit_test_framework::test_suite;
using namespace Cti::Simulator;

BOOST_AUTO_TEST_CASE(test_behavior_collection)
{
    BehaviorCollection<IntBehavior> behaviorCollection;
    std::auto_ptr<IntBehavior> a(new MultByTwentyBehavior());
    std::auto_ptr<IntBehavior> m(new AddOneBehavior());
    behaviorCollection.addBehavior(a);
    behaviorCollection.addBehavior(m);

    int value = 3;

    behaviorCollection.processMessage(value);
    
    BOOST_CHECK_EQUAL(value, 61);

    behaviorCollection.emptyBehaviors();

    value = 10;

    std::auto_ptr<IntBehavior> a2(new MultByTwentyBehavior());
    std::auto_ptr<IntBehavior> m2(new AddOneBehavior());
    behaviorCollection.addBehavior(m2);
    behaviorCollection.addBehavior(a2);

    behaviorCollection.processMessage(value);

    BOOST_CHECK_EQUAL(value, 220);
}
