#pragma once

#include "thread.h"
#include "critical_section.h"

#include "activemq/library/activemqcpp.h"
#include "cms/connection.h"

#include <queue>

namespace Cti {
namespace Messaging {

class StreamableMessage;
struct amq_envelope;

class IM_EX_MSG ActiveMQConnectionManager : private CtiThread
{
public:

    enum Queues;  //  forward declaration

private:

    void run();

    void validateSetup();

    void sendPendingMessages();

    void sendMessage(cms::Session &session, const amq_envelope &e);

    cms::MessageProducer *getProducer(cms::Session &session, const std::string &queue);

    void enqueueEnvelope(std::auto_ptr<amq_envelope> e);

    std::string getQueueName(Queues queue) const;

    bool _initialized;

    unsigned _delay;

    boost::scoped_ptr<cms::Connection> _connection;
    boost::scoped_ptr<cms::Session>    _session;

    const std::string _broker_uri;

    std::queue<amq_envelope *> _incoming_messages;
    CtiCriticalSection   _incoming_message_mux;

    std::queue<amq_envelope *> _pending_messages;

    typedef std::map<std::string, cms::MessageProducer *> producer_map;

    producer_map _producers;

    typedef std::map<Queues, std::string> queue_name_map;

    queue_name_map _queue_names;

public:

    ActiveMQConnectionManager(const std::string &broker_uri);

    virtual ~ActiveMQConnectionManager();

    /**
     * @deprecated  Prefer {@link #enqueueMessage(const Queues
     *              queue, const StreamableMessage &message);}
     */
    void enqueueMessage(const std::string &queueName, const std::string &message);

    void enqueueMessage(const Queues queueId, std::auto_ptr<StreamableMessage> message);

    enum Queues
    {
        Queue_PorterResponses,
        Queue_SmartEnergyProfileControl
    };
};

extern IM_EX_MSG ActiveMQConnectionManager gActiveMQConnection;

}
}
