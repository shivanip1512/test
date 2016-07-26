#define BOOST_TEST_MAIN

#include <boost/test/unit_test.hpp>

#include "amq_connection.h"

#include "test_main.hpp"

//  hack to get BOOST_CHECK_EQUAL_COLLECTIONS to print unsigned char as integer
namespace std {

ostream &operator<<( ostream &os, const unsigned char &uc ) {
    return os << static_cast<unsigned>(uc);
}

}

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
