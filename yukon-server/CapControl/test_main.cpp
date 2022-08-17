#define BOOST_TEST_MAIN

#include "amq_connection.h"

#include "boost_test_helpers.h"

#include <boost/test/unit_test.hpp>

#include <memory>

//  Prevents database connections.  Currently not enabled for Cap Control, since some tests still invoke connections to the DB.
//#include "test_main.hpp"

Cti::Test::use_in_unit_tests_only test_tag;

// Close all yukon messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

namespace Cti {
namespace Messaging {

extern IM_EX_MSG std::unique_ptr<ActiveMQConnectionManager> gActiveMQConnection;

}
}

struct test_ActiveMQConnectionManager : Cti::Messaging::ActiveMQConnectionManager
{
    void enqueueOutgoingMessage(const std::string &queueName, Cti::Messaging::StreamableMessage::auto_type&& message, ReturnLabel returnLabel) override
    {
        //  ignore message, do not send
    }
    void enqueueOutgoingMessage(const std::string &queueName, const Cti::Messaging::ActiveMQConnectionManager::SerializedMessage &message, ReturnLabel returnLabel) override
    {
        //  ignore message, do not send
    }
};

struct OverrideActiveMQConnectionManager
{
    OverrideActiveMQConnectionManager()
    {
        Cti::Messaging::gActiveMQConnection = std::make_unique<test_ActiveMQConnectionManager>();
    }
};

BOOST_GLOBAL_FIXTURE( OverrideActiveMQConnectionManager );
