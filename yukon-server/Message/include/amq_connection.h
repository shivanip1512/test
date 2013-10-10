#pragma once

#include "thread.h"
#include "critical_section.h"
#include "StreamableMessage.h"

#include <boost/function.hpp>
#include <boost/optional.hpp>
#include <boost/scoped_ptr.hpp>
#include <boost/ptr_container/ptr_map.hpp>
#include <boost/ptr_container/ptr_deque.hpp>
#include <boost/ptr_container/ptr_vector.hpp>

namespace cms {
class Connection;
class Session;
class Message;
class MessageProducer;
class MessageListener;
class Destination;
}

namespace Cti {
namespace Messaging {

namespace ActiveMQ {

class ManagedConnection;
class QueueConsumer;
class QueueProducer;
class TempQueueConsumer;

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
    public CtiThread
{
public:

    typedef std::vector<unsigned char> SerializedMessage;
    typedef boost::function<void (const std::vector<unsigned char> &)> MessageCallback;

    ActiveMQConnectionManager(const std::string &broker_uri);
    virtual ~ActiveMQConnectionManager();

    static void enqueueMessage (const ActiveMQ::OutboundQueues::type queueId, std::auto_ptr<StreamableMessage> message);
    static void enqueueMessages(const ActiveMQ::OutboundQueues::type queueId, const std::vector<SerializedMessage> &messages, MessageCallback callback);

    static void registerHandler(const ActiveMQ::InboundQueues::type queueId, const MessageCallback callback);

protected:

    virtual void enqueueOutgoingMessage(const ActiveMQ::OutboundQueues::type queueId, std::auto_ptr<StreamableMessage> message);
    virtual void enqueueOutgoingMessage(const ActiveMQ::OutboundQueues::type queueId, const SerializedMessage &message, MessageCallback callback);

    void addNewCallback(const ActiveMQ::InboundQueues::type queueId, const MessageCallback callback);

    void onInboundMessage(ActiveMQ::InboundQueues::type queue, const cms::Message *message);
    void onTempQueueReply(const cms::Message *message);

private:

    void run();

    //  Main loop methods
    void verifyConnectionObjects();
    void releaseConnectionObjects();

    void updateCallbacks();
    bool addQueueCallback(const ActiveMQ::InboundQueues::type queueId, const MessageCallback callback);

    void sendOutgoingMessages();
    ActiveMQ::QueueProducer &getQueueProducer(cms::Session &session, const std::string &queue);

    void dispatchIncomingMessages();
    void dispatchTempQueueReplies();

    const std::string _broker_uri;

    //  Connection/session objects
    boost::scoped_ptr<ActiveMQ::ManagedConnection> _connection;
    boost::scoped_ptr<cms::Session> _producerSession;
    boost::scoped_ptr<cms::Session> _consumerSession;

    //  Message submission objects and methods
    struct Envelope
    {
        ActiveMQ::OutboundQueues::type queueId;

        boost::optional<MessageCallback> callback;

        virtual cms::Message *extractMessage(cms::Session &session) const = 0;
    };

    typedef boost::ptr_deque<Envelope> EnvelopeQueue;
    CtiCriticalSection _outgoingMessagesMux;
    EnvelopeQueue      _outgoingMessages;

    typedef std::map<ActiveMQ::InboundQueues::type, std::vector<SerializedMessage>> IncomingPerQueue;
    CtiCriticalSection _newIncomingMessagesMux;
    IncomingPerQueue   _newIncomingMessages;

    typedef boost::ptr_map<const std::string, ActiveMQ::QueueProducer> ProducersByQueueName;
    ProducersByQueueName _producers;

    typedef std::multimap<const ActiveMQ::InboundQueues::type, MessageCallback> CallbacksPerQueue;
    CtiCriticalSection _newCallbackMux;
    CallbacksPerQueue  _newCallbacks;
    CallbacksPerQueue  _callbacks;

    //  Consumer and listener - binds to onInboundMessage
    struct QueueConsumerWithListener
    {
        boost::scoped_ptr<ActiveMQ::QueueConsumer> managedConsumer;
        boost::scoped_ptr<cms::MessageListener> listener;
    };

    typedef boost::ptr_vector<QueueConsumerWithListener> ConsumerList;
    ConsumerList _consumers;

    //  Temp consumer, listener, and client callback - binds to onTempQueueReply
    struct TempQueueConsumerWithCallback
    {
        boost::scoped_ptr<ActiveMQ::TempQueueConsumer> managedConsumer;
        boost::scoped_ptr<cms::MessageListener> listener;
        MessageCallback callback;
    };

    typedef boost::ptr_map<const cms::Destination *, TempQueueConsumerWithCallback> TemporaryConsumersByDestination;
    TemporaryConsumersByDestination _temporaryConsumers;

    typedef std::map<const cms::Destination *, SerializedMessage> ReplyPerDestination;
    CtiCriticalSection  _tempQueueRepliesMux;
    ReplyPerDestination _tempQueueReplies;

    enum
    {
        DefaultTimeToLive = 3600
    };
};


}
}
