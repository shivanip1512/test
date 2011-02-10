#pragma once

#include "thread.h"
#include "critical_section.h"

#include "activemq/library/activemqcpp.h"
#include "cms/connection.h"

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
        cms::Message *message;
    };

    void run();

    void initialize();

    void sendPendingMessages();

    void sendMessage(const envelope &e);

    cms::MessageProducer *getProducer(const std::string &queue);

    cms::TextMessage   *createTextMessage  (const std::string &message);
    cms::StreamMessage *createStreamMessage(const StreamableMessage &message);

    void enqueueEnvelope(envelope &e);

    std::string getQueueName(Queues queue) const;

    std::auto_ptr<cms::Connection> _connection;
    std::auto_ptr<cms::Session>    _session;

    const std::string _broker_uri;

    std::queue<envelope> _pending_messages;
    CtiCriticalSection   _pending_message_mux;

    typedef std::map<std::string, cms::MessageProducer * > producer_map;

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
    void enqueueMessage(const std::string &queue, const std::string &message);

    void enqueueMessage(const Queues queue, const StreamableMessage &message);

    enum Queues
    {
        Queue_PorterResponses
    };
};

extern IM_EX_MSG ActiveMQConnectionManager gActiveMQConnection;

}
}
