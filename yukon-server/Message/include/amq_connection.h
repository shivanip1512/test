#pragma once

#include "thread.h"
#include "critical_section.h"
#include "StreamableMessage.h"
#include "connection_base.h"

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
    static const OutboundQueue NetworkManagerRequest;
    static const OutboundQueue NetworkManagerE2eDataRequest;
    static const OutboundQueue ScannerOutMessages;
    static const OutboundQueue ScannerInMessages;

private:
    OutboundQueue(std::string name);
};


struct IM_EX_MSG InboundQueue
{
    std::string name;

    static const InboundQueue NetworkManagerResponse;
    static const InboundQueue NetworkManagerE2eDataConfirm;
    static const InboundQueue NetworkManagerE2eDataIndication;
    static const InboundQueue ScannerOutMessages;
    static const InboundQueue ScannerInMessages;
    static const InboundQueue PorterDynamicPaoInfoRequest;

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

    using SerializedMessage = std::vector<unsigned char>;

    struct MessageDescriptor
    {
        std::string type;
        SerializedMessage msg;
        const cms::Destination* replyTo = nullptr;

        ~MessageDescriptor();
    };

    using MessageCallback = std::function<void(const MessageDescriptor &)>;
    using MessageCallbackWithReply = std::function<std::unique_ptr<SerializedMessage>(const MessageDescriptor &)>;
    using TimeoutCallback = std::function<void()>;

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
    static void enqueueMessageWithCallbackFor(
            const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type message,
            typename CallbackFor<Msg>::type callback, CtiTime timeout, TimeoutCallback timedOut);
    static void enqueueMessageWithCallback(
            const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message,
            MessageCallback callback, CtiTime timeout, TimeoutCallback timedOut);

    static void registerHandler     (const ActiveMQ::Queues::InboundQueue &queue, const MessageCallback callback);
    static void registerReplyHandler(const ActiveMQ::Queues::InboundQueue &queue, const MessageCallbackWithReply callback);

    virtual void close();

protected:

    struct TemporaryListener
    {
        MessageCallback callback;
        CtiTime expiration;
        TimeoutCallback timedOut;
    };

    virtual void enqueueOutgoingMessage(
            const std::string &queueName,
            StreamableMessage::auto_type message,
            boost::optional<TemporaryListener> callback);
    virtual void enqueueOutgoingMessage(
            const std::string &queueName,
            const SerializedMessage &message,
            boost::optional<TemporaryListener> callback);

    void addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, const MessageCallback callback);
    void addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, const MessageCallbackWithReply callback);

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
        std::string queueName;

        boost::optional<TemporaryListener> replyListener;

        virtual cms::Message *extractMessage(cms::Session &session) const = 0;

        virtual ~Envelope() = default;
    };

    typedef boost::ptr_deque<Envelope> EnvelopeQueue;
    CtiCriticalSection _outgoingMessagesMux;
    EnvelopeQueue      _outgoingMessages;

    typedef std::map<const ActiveMQ::Queues::InboundQueue *, std::vector<std::unique_ptr<MessageDescriptor>>> IncomingPerQueue;
    CtiCriticalSection _newIncomingMessagesMux;
    IncomingPerQueue   _newIncomingMessages;

    typedef boost::ptr_map<const std::string, ActiveMQ::QueueProducer> ProducersByQueueName;
    ProducersByQueueName _producers;

    typedef std::multimap<const ActiveMQ::Queues::InboundQueue *, MessageCallback> CallbacksPerQueue;
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
        MessageCallback callback;
    };

    typedef boost::ptr_map<std::string, TempQueueConsumerWithCallback> TemporaryConsumersByDestination;
    TemporaryConsumersByDestination _temporaryConsumers;

    struct ExpirationHandler
    {
        std::string queueName;
        TimeoutCallback callback;
    };

    std::multimap<CtiTime, ExpirationHandler> _temporaryExpirations;

    typedef std::map<std::string, std::unique_ptr<MessageDescriptor>> ReplyPerDestination;
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

}
}
