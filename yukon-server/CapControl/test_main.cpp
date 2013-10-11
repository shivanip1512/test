#define BOOST_TEST_MAIN

#include "amq_connection.h"

#include <boost/test/unit_test.hpp>

#include <memory>

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

    void enqueueOutgoingMessage(const Cti::Messaging::ActiveMQ::Queues::OutboundQueue &queueId, std::auto_ptr<Cti::Messaging::StreamableMessage> message)
    {
        //  delete message
    }
    void enqueueOutgoingMessage(const Cti::Messaging::ActiveMQ::Queues::OutboundQueue &queueId, Cti::Messaging::ActiveMQConnectionManager::SerializedMessage &message)
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
