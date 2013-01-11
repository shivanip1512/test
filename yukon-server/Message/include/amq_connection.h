#pragma once

#include "thread.h"
#include "critical_section.h"

#include "activemq/library/activemqcpp.h"
#include "cms/connection.h"

#include <boost/scoped_ptr.hpp>

#include <queue>

namespace Cti {
namespace Messaging {

class StreamableMessage;

class IM_EX_MSG ActiveMQConnectionManager : private CtiThread
{
public:

    enum Queues;  //  forward declaration

private:

    struct envelope
    {
        std::string queue;

        boost::scoped_ptr<StreamableMessage> message;
    };

    void run();

    void getIncomingMessages();

    void sendPendingMessages();

    void validateSetup();

    void sendMessage(cms::Session &session, const envelope &e);

    void purgePendingMessages();

    cms::MessageProducer *getProducer(cms::Session &session, const std::string &queue);

    std::string getQueueName(Queues queue) const;

    bool _initialized;

    unsigned _delay;

    boost::scoped_ptr<cms::Connection> _connection;
    boost::scoped_ptr<cms::Session>    _session;

    const std::string _broker_uri;

    std::queue<envelope *> _incoming_messages;
    CtiCriticalSection     _incoming_message_mux;

    std::queue<envelope *> _pending_messages;

    typedef boost::ptr_map<const std::string, cms::MessageProducer> producer_map;

    producer_map _producers;

    typedef std::map<Queues, std::string> queue_name_map;

    queue_name_map _queue_names;

    enum
    {
        DefaultTimeToLive = 3600
    };

public:

    ActiveMQConnectionManager(const std::string &broker_uri);

    virtual ~ActiveMQConnectionManager();

    void enqueueMessage(const Queues queueId, std::auto_ptr<StreamableMessage> message);

    enum Queues
    {
        Queue_PorterResponses,
        Queue_SmartEnergyProfileControl,
        Queue_SmartEnergyProfileRestore,
        Queue_HistoryRowAssociationResponse,
        Queue_IvvcAnalysisMessage,
        Queue_CapControlOperationMessage,
        Queue_RfnBroadcast
    };
};

extern IM_EX_MSG ActiveMQConnectionManager gActiveMQConnection;

}
}
