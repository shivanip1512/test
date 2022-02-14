#define BOOST_TEST_MAIN

#include <boost/test/unit_test.hpp>

#include "amq_connection.h"
#include "connectionHandle.h"
#include "rfn_identifier.h"

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

std::ostream& operator<<(std::ostream& o, const ConnectionHandle& ch)
{
    return o << ch.toString();
}

std::ostream& operator<<(std::ostream& os, const RfnIdentifier rfnId)
{
    return os << rfnId.toString();
}

namespace Messaging {

extern IM_EX_MSG std::unique_ptr<ActiveMQConnectionManager> gActiveMQConnection;

}
}

struct test_ActiveMQConnectionManager : Cti::Messaging::ActiveMQConnectionManager
{
    void enqueueOutgoingMessage(
        const Cti::Messaging::ActiveMQ::Queues::OutboundQueue&, 
        Cti::Messaging::StreamableMessage::auto_type&&, 
        ReturnLabel) override
    {
        //  ignore message, do not send
    }
    void enqueueOutgoingMessage(
        const Cti::Messaging::ActiveMQ::Queues::OutboundQueue&, 
        const Cti::Messaging::ActiveMQConnectionManager::SerializedMessage&, 
        ReturnLabel) override
    {
        //  ignore message, do not send
    }
    void enqueueOutgoingMessage(
        const Cti::Messaging::ActiveMQ::Topics::OutboundTopic&,
        const std::string,
        ReturnLabel) override
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
