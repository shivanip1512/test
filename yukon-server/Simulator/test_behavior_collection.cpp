#include <boost/test/unit_test.hpp>

#include "BehaviorCollection.h"
#include "SimulatorLogger.h"

using namespace Cti::Simulator;

BOOST_AUTO_TEST_SUITE( test_behavior_collection )

class IntBehavior
{
public:
    typedef int target_type;

    virtual void apply(target_type &value, Logger &logger)=0;
};

class MultByTwentyBehavior : public IntBehavior
{
public:
    MultByTwentyBehavior() {};
    virtual void apply(int &value, Logger &logger)
    {
        value *= 20;
    }
};

class AddOneBehavior : public IntBehavior
{
public:
    AddOneBehavior() {};
    virtual void apply(int &value, Logger &logger)
    {
        value += 1;
    }
};

BOOST_AUTO_TEST_CASE(test_behavior_collection)
{
    SimulatorLogger logger(dout);

    BehaviorCollection<IntBehavior> behaviorCollection;
    std::auto_ptr<IntBehavior> multiply(new MultByTwentyBehavior());
    std::auto_ptr<IntBehavior> add(new AddOneBehavior());
    behaviorCollection.push_back(multiply);
    behaviorCollection.push_back(add);

    int value = 3;

    behaviorCollection.processMessage(value, logger);

    BOOST_CHECK_EQUAL(value, 61);

    behaviorCollection.clear();

    value = 10;

    std::auto_ptr<IntBehavior> multiply2(new MultByTwentyBehavior());
    std::auto_ptr<IntBehavior> add2(new AddOneBehavior());
    behaviorCollection.push_back(add2);
    behaviorCollection.push_back(multiply2);

    behaviorCollection.processMessage(value, logger);

    BOOST_CHECK_EQUAL(value, 220);
}

BOOST_AUTO_TEST_SUITE_END()
