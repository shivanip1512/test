#define BOOST_TEST_MAIN

#include "amq_connection.h"

#include <boost/test/unit_test.hpp>

#include <memory>

//  Prevents database connections.  Currently not enabled for Cap Control, since some tests still invoke connections to the DB.
//#include "test_main.hpp"

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

    void enqueueOutgoingMessage(const std::string &queueName, Cti::Messaging::StreamableMessage::auto_type message, boost::optional<TemporaryListener> callback) override
    {
        //  ignore message, do not send
    }
    void enqueueOutgoingMessage(const std::string &queueName, const Cti::Messaging::ActiveMQConnectionManager::SerializedMessage &message, boost::optional<TemporaryListener> callback) override
    {
        //  ignore message, do not send
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
