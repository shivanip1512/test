#include "amq_connection.h"
#include "amq_queues.h"

#include "boost_test_helpers.h"

#include "activemq/commands/ActiveMQBytesMessage.h"

#include <boost/test/unit_test.hpp>

namespace Cti::Messaging {
    extern IM_EX_MSG std::unique_ptr<ActiveMQConnectionManager> gActiveMQConnection;
}

BOOST_AUTO_TEST_SUITE(test_amq_connection)

using ActiveMQConnectionManager = Cti::Messaging::ActiveMQConnectionManager;

struct test_AmqConnectionManmager : ActiveMQConnectionManager
{
    using ActiveMQConnectionManager::getTasks;
    using ActiveMQConnectionManager::processTasks;
    using ActiveMQConnectionManager::emplaceNamedMessage;
    
    void kickstart() override {}  //  no-op, don't start the thread
    void createConsumersForCallbacks(const CallbacksPerQueue &callbacks) override {};  //  don't create AMQ consumers
};

class AmqTestContext
{
    std::unique_ptr<test_AmqConnectionManmager> newManager;
    std::unique_ptr<ActiveMQConnectionManager> oldManager;
public:
    test_AmqConnectionManmager& testManager;

    AmqTestContext() 
        :   newManager(std::make_unique<test_AmqConnectionManmager>()),
            testManager(*newManager)
    {
        oldManager.reset(newManager.release());

        oldManager.swap(Cti::Messaging::gActiveMQConnection);
    }

    ~AmqTestContext()
    {
        oldManager.swap(Cti::Messaging::gActiveMQConnection);
    }
};

BOOST_FIXTURE_TEST_SUITE(test_with_override, AmqTestContext)

BOOST_AUTO_TEST_CASE(test_named_queue)
{
    const std::vector<unsigned char> expected_message { 0xde, 0xad, 0xbe, 0xef };
    const std::string expected_type = "Jimmy John's Gargantuan";

    std::vector<unsigned char> actual_message;
    std::string actual_type;

    ActiveMQConnectionManager::registerHandler(
        Cti::Messaging::ActiveMQ::Queues::InboundQueue::PorterDynamicPaoInfoRequest, 
        [&](const ActiveMQConnectionManager::MessageDescriptor& md) {
            actual_message = md.msg;
            actual_type    = md.type;
        });

    testManager.emplaceNamedMessage(&Cti::Messaging::ActiveMQ::Queues::InboundQueue::PorterDynamicPaoInfoRequest, expected_type, expected_message, nullptr);

    testManager.processTasks(testManager.getTasks());

    BOOST_CHECK_EQUAL_RANGES(expected_message, actual_message);
    BOOST_CHECK_EQUAL(expected_type, actual_type);
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()
