#pragma once

#include "thread.h"
#include "critical_section.h"
#include "StreamableMessage.h"
#include "connection_base.h"

#include "RfnBroadcastReplyMessage.h"

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

namespace Queues {

struct IM_EX_MSG OutboundQueue
{
    std::string name;

    static const OutboundQueue PorterResponses;
    static const OutboundQueue SmartEnergyProfileControl;
    static const OutboundQueue SmartEnergyProfileRestore;
    static const OutboundQueue EcobeeCyclingControl;
    static const OutboundQueue EcobeeRestore;
    static const OutboundQueue HistoryRowAssociationResponse;
    static const OutboundQueue IvvcAnalysisMessage;
    static const OutboundQueue CapControlOperationMessage;
    static const OutboundQueue RfnBroadcast;
    static const OutboundQueue NetworkManagerE2eDataRequest;
    static const OutboundQueue ScannerOutMessages;
    static const OutboundQueue ScannerInMessages;

private:
    OutboundQueue(std::string name);
};


struct IM_EX_MSG InboundQueue
{
    std::string name;

    static const InboundQueue NetworkManagerE2eDataIndication;
    static const InboundQueue ScannerOutMessages;
    static const InboundQueue ScannerInMessages;

private:
    InboundQueue(std::string name);
};

}
}

class IM_EX_MSG ActiveMQConnectionManager :
    private CtiThread,
    public BaseConnection
{
public:

    typedef std::vector<unsigned char> SerializedMessage;
    typedef boost::function<void (const SerializedMessage &)> SerializedMessageCallback;

    template<class Msg>
    struct CallbackFor
    {
        typedef boost::function<void (const Msg &)> type;
    };

    ActiveMQConnectionManager(const std::string &broker_uri);
    virtual ~ActiveMQConnectionManager();

    static void start();

    static void enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type message);
    static void enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message);
    template<class Msg>
    static void enqueueMessageWithCallbackFor(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type message, typename CallbackFor<Msg>::type callback);
    static void enqueueMessageWithCallback(const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message, SerializedMessageCallback callback);

    static void registerHandler(const ActiveMQ::Queues::InboundQueue &queue, const SerializedMessageCallback callback);

    virtual void close();

protected:

    virtual void enqueueOutgoingMessage(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type message, boost::optional<SerializedMessageCallback> callback);
    virtual void enqueueOutgoingMessage(const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message, boost::optional<SerializedMessageCallback> callback);

    void addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, const SerializedMessageCallback callback);

    void onInboundMessage(const ActiveMQ::Queues::InboundQueue *queue, const cms::Message *message);
    void onTempQueueReply(const cms::Message *message);

private:

    void run();

    CtiCriticalSection _closeConnectionMux;

    const std::string _broker_uri;

    //  Connection/session objects
    boost::scoped_ptr<ActiveMQ::ManagedConnection> _connection;
    boost::scoped_ptr<cms::Session> _producerSession;
    boost::scoped_ptr<cms::Session> _consumerSession;

    //  Message submission objects and methods
    struct Envelope
    {
        const ActiveMQ::Queues::OutboundQueue *queue;

        boost::optional<SerializedMessageCallback> callback;

        virtual cms::Message *extractMessage(cms::Session &session) const = 0;

        virtual ~Envelope() {};
    };

    typedef boost::ptr_deque<Envelope> EnvelopeQueue;
    CtiCriticalSection _outgoingMessagesMux;
    EnvelopeQueue      _outgoingMessages;

    typedef std::map<const ActiveMQ::Queues::InboundQueue *, std::vector<SerializedMessage>> IncomingPerQueue;
    CtiCriticalSection _newIncomingMessagesMux;
    IncomingPerQueue   _newIncomingMessages;

    typedef boost::ptr_map<const std::string, ActiveMQ::QueueProducer> ProducersByQueueName;
    ProducersByQueueName _producers;

    typedef std::multimap<const ActiveMQ::Queues::InboundQueue *, SerializedMessageCallback> CallbacksPerQueue;
    CtiCriticalSection _newCallbackMux;
    CallbacksPerQueue  _newCallbacks;
    CallbacksPerQueue  _callbacks;

    //  Consumer and listener - binds to onInboundMessage
    struct QueueConsumerWithListener
    {
        boost::scoped_ptr<ActiveMQ::QueueConsumer> managedConsumer;
        boost::scoped_ptr<cms::MessageListener> listener;
    };

    typedef boost::ptr_map<const ActiveMQ::Queues::InboundQueue *, QueueConsumerWithListener> ConsumerMap;
    ConsumerMap _consumers;

    //  Temp consumer, listener, and client callback - binds to onTempQueueReply
    struct TempQueueConsumerWithCallback
    {
        boost::scoped_ptr<ActiveMQ::TempQueueConsumer> managedConsumer;
        boost::scoped_ptr<cms::MessageListener> listener;
        SerializedMessageCallback callback;
    };

    typedef boost::ptr_map<std::string, TempQueueConsumerWithCallback> TemporaryConsumersByDestination;
    TemporaryConsumersByDestination _temporaryConsumers;

    typedef std::map<std::string, SerializedMessage> ReplyPerDestination;
    CtiCriticalSection  _tempQueueRepliesMux;
    ReplyPerDestination _tempQueueReplies;

    enum
    {
        DefaultTimeToLiveMillis = 3600 * 1000  //  1 hour
    };

    bool verifyConnectionObjects();
    void releaseConnectionObjects();

    void updateCallbacks();

    void registerConsumersForCallbacks(const CallbacksPerQueue &callbacks);
    void registerConsumer(const ActiveMQ::Queues::InboundQueue *inboundQueue);

    void sendOutgoingMessages();
    ActiveMQ::QueueProducer &getQueueProducer(cms::Session &session, const std::string &queue);

    void dispatchIncomingMessages();
    void dispatchTempQueueReplies();
};

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnBroadcastReplyMessage>(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type message, CallbackFor<Rfn::RfnBroadcastReplyMessage>::type callback);

}
}
