#pragma once

#include "thread.h"
#include "critical_section.h"
#include "StreamableMessage.h"
#include "connection_base.h"

#include <boost/optional.hpp>
#include <boost/variant.hpp>

#include <chrono>
#include <queue>
#include <condition_variable>

namespace cms {
class Connection;
class Session;
class Message;
class MessageProducer;
class MessageListener;
class Destination;
class TemporaryQueue;
}

namespace Cti {
namespace Messaging {
namespace ActiveMQ {

class ManagedConnection;
class QueueConsumer;
class QueueProducer;
class TempQueueConsumer;

namespace Queues {
class OutboundQueue;
class InboundQueue;
}
}

class IM_EX_MSG ActiveMQConnectionManager :
    private CtiThread,
    public BaseConnection
{
public:

    using SerializedMessage = std::vector<unsigned char>;

    struct IM_EX_MSG MessageDescriptor
    {
        std::string type;
        SerializedMessage msg;
        const cms::Destination* replyTo = nullptr;

        ~MessageDescriptor();
    };

    struct MessageCallback
    {
        using type = std::function<void(const MessageDescriptor&)>;
        using Ptr  = std::unique_ptr<MessageCallback>;

        virtual void operator()(const MessageDescriptor&) const = 0;
    };

    struct SimpleMessageCallback : MessageCallback
    {
        using Function = type;

        SimpleMessageCallback(Function f) : fn { f } {}

        Function fn;

        void operator()(const MessageDescriptor &md) const override { fn(md); }
    };

    using MessageCallbackWithReply = std::function<std::unique_ptr<SerializedMessage>(const MessageDescriptor &)>;
    using TimeoutCallback = std::function<void()>;

    template<class Msg>
    struct CallbackFor
    {
        using type = std::function<void (const Msg &)>;
        using Ptr  = std::unique_ptr<CallbackFor<Msg>>;

        virtual void operator()(const Msg&) const = 0;
    };

    template<class Msg>
    struct SimpleCallbackFor : CallbackFor<Msg>
    {
        using Function = type;
        
        SimpleCallbackFor(Function f) : fn { f } {}

        Function fn;

        void operator()(const Msg& msg) const override { return fn(msg); }
    };

    class SessionCallback
    {
        friend class ActiveMQConnectionManager;
        static std::atomic_size_t globalId;
        size_t id { std::numeric_limits<size_t>::max() };
        MessageCallback::type callback;
        SessionCallback(MessageCallback::type callback_) : callback { callback_ }, id { globalId++ } {}
    public:
        SessionCallback() = default;
        SessionCallback(const SessionCallback&) = default;
        SessionCallback& operator=(const SessionCallback&) = default;
        bool operator<(const SessionCallback& rhs) const { return id < rhs.id; }
    };

    ActiveMQConnectionManager();
    virtual ~ActiveMQConnectionManager();

    static void start();

    static void enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type&& message);
    static void enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message);

    template<class Msg>
    static void enqueueMessageWithCallbackFor(
            const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type&& message,
            typename CallbackFor<Msg>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);
    template<class Msg>
    static void enqueueMessageWithCallbackFor(
            const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message,
            typename CallbackFor<Msg>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);
    template<class Msg>
    static void enqueueMessageWithCallbackFor(
            const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message,
            typename CallbackFor<Msg>::Ptr callback, std::chrono::seconds timeout, TimeoutCallback timedOut);
    static void enqueueMessageWithCallback(
            const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message,
            MessageCallback::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);
    static void enqueueMessageWithSessionCallback(
            const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message, 
            SessionCallback callback);

    static void registerHandler     (const ActiveMQ::Queues::InboundQueue &queue, MessageCallback::type callback);
    static void registerReplyHandler(const ActiveMQ::Queues::InboundQueue &queue, MessageCallbackWithReply callback);
    static auto registerSessionCallback(const MessageCallback::type callback) -> SessionCallback;

    virtual void close();

protected:

    struct TimedCallback
    {
        MessageCallback::Ptr callback;
        std::chrono::seconds timeout;
        TimeoutCallback timeoutCallback;
    };

    using ReturnAddress = boost::variant<TimedCallback, SessionCallback>;

    using ReturnLabel = std::unique_ptr<ReturnAddress>;

    //  Message submission objects and methods
    struct Envelope
    {
        std::string queueName;

        ReturnLabel returnAddress;

        virtual std::unique_ptr<cms::Message> extractMessage(cms::Session &session) const = 0;

        virtual ~Envelope() = default;
    };

    using EnvelopeQueue        = std::queue<std::unique_ptr<Envelope>>;
    using IncomingPerQueue     = std::map<const ActiveMQ::Queues::InboundQueue *, std::queue<std::unique_ptr<MessageDescriptor>>>;
    using CallbacksPerQueue    = std::multimap<const ActiveMQ::Queues::InboundQueue *, MessageCallback::Ptr>;
    using RepliesByDestination = std::multimap<std::string, std::unique_ptr<MessageDescriptor>>;

    struct MessagingTasks
    {
        EnvelopeQueue        outgoingMessages;
        IncomingPerQueue     incomingMessages;
        CallbacksPerQueue    newCallbacks;
        RepliesByDestination tempQueueReplies;
        RepliesByDestination sessionReplies;

        MessagingTasks() = default;
        MessagingTasks(MessagingTasks&&) = default;
        MessagingTasks& operator=(MessagingTasks&&) = default;

        bool empty() const;
    };

    MessagingTasks getTasks();

    void processTasks(MessagingTasks tasks);

    const cms::Destination* makeDestinationForReturnAddress(ReturnAddress returnAddress);

    virtual void enqueueOutgoingMessage(
            const std::string &queueName,
            StreamableMessage::auto_type&& message,
            ReturnLabel returnAddress);
    virtual void enqueueOutgoingMessage(
            const std::string &queueName,
            const SerializedMessage &message,
            ReturnLabel returnAddress);

    void addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, MessageCallback::Ptr callback);
    void addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, MessageCallbackWithReply callback);
        
    virtual void emplaceNamedMessage(const ActiveMQ::Queues::InboundQueue* queue, const std::string type, std::vector<unsigned char> payload, cms::Destination* replyTo);

    virtual void kickstart();
    virtual void createConsumersForCallbacks(const CallbacksPerQueue &callbacks);

private:

    void run();

    CtiCriticalSection _closeConnectionMux;

    std::mutex _taskMux;
    std::condition_variable _newTask;

    //  Connection/session objects
    std::unique_ptr<ActiveMQ::ManagedConnection> _connection;
    std::unique_ptr<cms::Session> _producerSession;
    std::unique_ptr<cms::Session> _consumerSession;

    MessagingTasks _newTasks;

    template<class Container, typename... Arguments>
    void emplaceTask(Container& c, Arguments&&... args);
        
    using ProducersByQueueName = std::map<std::string, std::unique_ptr<ActiveMQ::QueueProducer>>;
    ProducersByQueueName _producers;

    CallbacksPerQueue  _namedCallbacks;

    //  Consumer and listener - binds to acceptNamedMessage
    struct QueueConsumerWithListener
    {
        std::unique_ptr<ActiveMQ::QueueConsumer> managedConsumer;
        std::unique_ptr<cms::MessageListener> listener;
    };

    using NamedConsumerMap = std::map<const ActiveMQ::Queues::InboundQueue *, std::unique_ptr<QueueConsumerWithListener>>;
    NamedConsumerMap _namedConsumers;

    //  Temp consumer and client callback
    struct TempQueueConsumerWithCallback
    {
        std::unique_ptr<ActiveMQ::TempQueueConsumer> managedConsumer;
        MessageCallback::Ptr callback;
    };

    using TemporaryConsumersByDestination = std::map<std::string, std::unique_ptr<TempQueueConsumerWithCallback>>;

    //  temp consumer that only lasts as long as the first reply - binds to acceptSingleReply
    TemporaryConsumersByDestination _replyConsumers;

    using DestinationsBySessionCallback = std::map<SessionCallback, const cms::Destination*>;

    //  temp queues that last the lifetime of the session - binds to acceptSessionReply, no timeouts
    TemporaryConsumersByDestination _sessionConsumers;
    DestinationsBySessionCallback _sessionConsumerDestinations;

    ReturnLabel makeReturnLabel(MessageCallback::Ptr callback, std::chrono::seconds timeout, TimeoutCallback timeoutCallback);
    ReturnLabel makeReturnLabel(SessionCallback& callback);

    struct ExpirationHandler
    {
        std::string queueName;
        TimeoutCallback callback;
    };

    std::multimap<CtiTime, ExpirationHandler> _replyExpirations;

    enum
    {
        DefaultTimeToLiveMillis = 3600 * 1000  //  1 hour
    };

    bool verifyConnectionObjects();
    void releaseConnectionObjects();

    void updateCallbacks(CallbacksPerQueue newCallbacks);

    void createNamedConsumer(const ActiveMQ::Queues::InboundQueue *inboundQueue);
    auto createSessionConsumer(const SessionCallback callback) -> const cms::Destination*;

    void sendOutgoingMessages(EnvelopeQueue messages);
    ActiveMQ::QueueProducer &getQueueProducer(cms::Session &session, const std::string &queue);

    void dispatchIncomingMessages(IncomingPerQueue incomingMessages);
    void dispatchTempQueueReplies(RepliesByDestination tempQueueReplies);
    void dispatchSessionReplies  (RepliesByDestination sessionReplies);

    void acceptNamedMessage(const cms::Message *message, const ActiveMQ::Queues::InboundQueue *queue);
    void acceptSingleReply (const cms::Message *message);
    void acceptSessionReply(const cms::Message *message);
};

}
}
