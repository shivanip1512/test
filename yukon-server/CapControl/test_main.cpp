#define BOOST_TEST_MAIN

#include "amq_connection.h"

#include <boost/test/unit_test.hpp>

#include <memory>

// Close all yukon messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

namespace Cti {
namespace Messaging {

extern IM_EX_MSG std::auto_ptr<ActiveMQConnectionManager> gActiveMQConnection;

}
}

struct test_ActiveMQConnectionManager : Cti::Messaging::ActiveMQConnectionManager
{
    test_ActiveMQConnectionManager(std::string uri) :
        ActiveMQConnectionManager(uri)
    {
    }

    void enqueueOutgoingMessage(const Cti::Messaging::ActiveMQ::Queues::OutboundQueue &queueId, Cti::Messaging::StreamableMessage::auto_type message, boost::optional<SerializedMessageCallback> callback) override
    {
        //  delete message
    }
    void enqueueOutgoingMessage(const Cti::Messaging::ActiveMQ::Queues::OutboundQueue &queueId, const Cti::Messaging::ActiveMQConnectionManager::SerializedMessage &message, boost::optional<SerializedMessageCallback> callback) override
    {
        //  delete message
    }
};

struct OverrideActiveMQConnectionManager
{
    OverrideActiveMQConnectionManager()
    {
        Cti::Messaging::gActiveMQConnection.reset(
           new test_ActiveMQConnectionManager("0.0.0.0:0"));
    }
};

BOOST_GLOBAL_FIXTURE( OverrideActiveMQConnectionManager );
