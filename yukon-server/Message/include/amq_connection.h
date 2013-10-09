#pragma once

#include "thread.h"
#include "critical_section.h"
#include "StreamableMessage.h"

#include <boost/scoped_ptr.hpp>
#include <boost/ptr_container/ptr_map.hpp>

#include <queue>

namespace cms {
class Connection;
class Session;
class MessageProducer;
}

namespace Cti {
namespace Messaging {

namespace ActiveMQ {
class QueueProducer;
class ManagedConnection;

struct OutboundQueues
{
    enum type  //  standin for strongly-typed enums
    {
        PorterResponses,
        SmartEnergyProfileControl,
        SmartEnergyProfileRestore,
        HistoryRowAssociationResponse,
        IvvcAnalysisMessage,
        CapControlOperationMessage,
        RfnBroadcast,
        NetworkManagerE2eDataRequest,
    };
};

struct InboundQueues {

    enum type  //  standin for strongly-typed enums
    {
        NetworkManagerE2eDataIndication,
    };
};

}

class IM_EX_MSG ActiveMQConnectionManager :
    private CtiThread
{
public:

    ActiveMQConnectionManager(const std::string &broker_uri);

    virtual ~ActiveMQConnectionManager();

    static void enqueueMessage(const ActiveMQ::OutboundQueues::type queueId, std::auto_ptr<StreamableMessage> message);

    static void enqueueMessage(const ActiveMQ::OutboundQueues::type queueId, std::vector<unsigned char> payload);

protected:

    virtual void enqueueOutgoingMessage(const ActiveMQ::OutboundQueues::type queueId, std::auto_ptr<StreamableMessage> message);

private:

    struct envelope
    {
        std::string queue;

        boost::scoped_ptr<StreamableMessage> message;
    };

    void run();

    void getOutgoingMessages();

    void sendPendingMessages();

    void validateSetup();

    void sendMessage(cms::Session &session, const envelope &e);

    void purgePendingMessages();

    cms::MessageProducer *getProducer(cms::Session &session, const std::string &queue);

    unsigned _delay;

    boost::scoped_ptr<cms::Connection> _connection;
    boost::scoped_ptr<cms::Session>    _session;

    const std::string _broker_uri;

    std::queue<envelope *> _outgoing_messages;
    CtiCriticalSection     _outgoing_message_mux;

    std::queue<envelope *> _pending_messages;

    typedef boost::ptr_map<const std::string, cms::MessageProducer> producer_map;

    producer_map _producers;

    enum
    {
        DefaultTimeToLive = 3600
    };
};


}
}
