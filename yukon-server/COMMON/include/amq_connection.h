#pragma once

#include "thread.h"
#include "critical_section.h"

#include "activemq/library/activemqcpp.h"
#include "cms/connection.h"

#include <queue>

namespace Cti {

class IM_EX_CTIBASE ActiveMQConnectionManager : private CtiThread
{
private:

    struct envelope
    {
        std::string queue;
        std::string message;
    };

    void run();

    void initialize();

    void sendPendingMessages();

    void sendMessage(const envelope &e);

    cms::MessageProducer *getProducer(const std::string &queue);

    std::auto_ptr<cms::TextMessage> createTextMessage(const std::string &message);

    std::auto_ptr<cms::Connection> _connection;
    std::auto_ptr<cms::Session>    _session;

    const std::string _broker_uri;

    std::queue<envelope> _pending_messages;
    CtiCriticalSection   _pending_message_mux;

    typedef map<std::string, cms::MessageProducer * > producer_map;

    producer_map _producers;

public:

    ActiveMQConnectionManager(const std::string &broker_uri);

    virtual ~ActiveMQConnectionManager();

    void enqueueMessage(const std::string &queue, const std::string &message);
};

}
