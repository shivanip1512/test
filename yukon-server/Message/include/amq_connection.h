#pragma once

#include "thread.h"
#include "critical_section.h"
#include "StreamableMessage.h"
#include "connection_base.h"

#include <proton/messaging_handler.hpp>
#include <proton/message.hpp>
#include <proton/session.hpp>

//#include <boost/optional.hpp>

#include <chrono>
#include <queue>
#include <condition_variable>
#include <variant>

namespace cms {
class Connection;
class Session;
class Message;
class MessageProducer;
class Destination;
class TemporaryQueue;
}

namespace Cti {
namespace Messaging {
namespace Qpid {

class ManagedConnection;
class QueueConsumer;
class QueueProducer;
class TempQueueConsumer;
class MessageListener;

namespace Queues {
class OutboundQueue;
class InboundQueue;
}
}

class IM_EX_MSG ActiveMQConnectionManager :
//    private CtiThread,
    public BaseConnection,
    public proton::messaging_handler
{
public:

    using SerializedMessage = std::vector<unsigned char>;

    struct IM_EX_MSG MessageDescriptor
    {
        std::string type;
        SerializedMessage msg;
        std::string replyTo;
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
    using ReplyCallback = std::function<void(SerializedMessage)>;
    using MessageCallbackWithReplies = std::function<void(const MessageDescriptor&, ReplyCallback)>;
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

    // static
    void start();

    static void enqueueMessage(const Qpid::Queues::OutboundQueue &queue, StreamableMessagePtr message);
    static void enqueueMessage(const Qpid::Queues::OutboundQueue &queue, const SerializedMessage &message);

    template<class Msg>
    static void enqueueMessageWithCallbackFor(
            const Qpid::Queues::OutboundQueue &queue, StreamableMessagePtr message,
            typename CallbackFor<Msg>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);
    template<class Msg>
    static void enqueueMessageWithCallbackFor(
            const Qpid::Queues::OutboundQueue &queue, const SerializedMessage &message,
            typename CallbackFor<Msg>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);
    template<class Msg>
    static void enqueueMessageWithCallbackFor(
            const Qpid::Queues::OutboundQueue &queue, const SerializedMessage &message,
            typename CallbackFor<Msg>::Ptr callback, std::chrono::seconds timeout, TimeoutCallback timedOut);
    static void enqueueMessageWithCallback(
            const Qpid::Queues::OutboundQueue &queue, const SerializedMessage &message,
            MessageCallback::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);
    static void enqueueMessageWithSessionCallback(
            const Qpid::Queues::OutboundQueue &queue, const SerializedMessage &message, 
            SessionCallback callback);

    static void registerHandler     (const Qpid::Queues::InboundQueue &queue, MessageCallback::type callback);
    static void registerReplyHandler(const Qpid::Queues::InboundQueue &queue, MessageCallbackWithReply callback);
    static void registerReplyHandler(const Qpid::Queues::InboundQueue& queue, MessageCallbackWithReplies callback);
    static auto registerSessionCallback(const MessageCallback::type callback) -> SessionCallback;

    virtual void close();

    void on_connection_open( proton::connection & c ) override;
    void on_connection_close( proton::connection & c ) override;
    	
protected:

    struct TimedCallback
    {
        MessageCallback::Ptr callback;
        std::chrono::seconds timeout;
        TimeoutCallback timeoutCallback;
    };

    using ReturnAddress = std::variant<TimedCallback, SessionCallback>;

    using ReturnLabel = std::unique_ptr<ReturnAddress>;

    //  Message submission objects and methods
    struct Envelope
    {
        std::string queueName;

        ReturnLabel returnAddress;

        proton::message message;
    };

    struct Reply
    {
        SerializedMessage message;
        std::string dest;
    };

    using EnvelopeQueue        = std::queue<std::unique_ptr<Envelope>>;
    using ReplyQueue           = std::queue<Reply>;
    using IncomingPerQueue     = std::map<const Qpid::Queues::InboundQueue *, std::queue<std::unique_ptr<MessageDescriptor>>>;
    using CallbacksPerQueue    = std::multimap<const Qpid::Queues::InboundQueue *, MessageCallback::Ptr>;
    using RepliesByDestination = std::multimap<std::string, std::unique_ptr<MessageDescriptor>>;

    struct MessagingTasks
    {
        EnvelopeQueue        outgoingMessages;
        ReplyQueue           outgoingReplies;
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

    std::string makeDestinationForReturnAddress(ReturnAddress returnAddress);

    virtual void enqueueOutgoingMessage(
            const std::string &queueName,
            StreamableMessagePtr message,
            ReturnLabel returnAddress);
    virtual void enqueueOutgoingMessage(
            const std::string &queueName,
            const SerializedMessage &message,
            ReturnLabel returnAddress);
    virtual void enqueueOutgoingReply(
            std::string dest,
            const SerializedMessage& message);

    void addNewCallback(const Qpid::Queues::InboundQueue &queue, MessageCallback::Ptr callback);
    void addNewCallback(const Qpid::Queues::InboundQueue &queue, MessageCallbackWithReply callback);
    void addNewCallback(const Qpid::Queues::InboundQueue& queue, MessageCallbackWithReplies callback);
        
//jmoc     virtual void kickstart();
    virtual void createConsumersForCallbacks(const CallbacksPerQueue &callbacks);

private:

    void run();

    CtiCriticalSection _closeConnectionMux;

    std::mutex _taskMux;
    std::condition_variable _newTask;

    //  Connection/session objects
//    std::unique_ptr<Qpid::ManagedConnection> _connection;
//    std::unique_ptr<cms::Session> _producerSession;
//    std::unique_ptr<cms::Session> _consumerSession;
    proton::session _brokerSession;

    MessagingTasks _newTasks;

    template<class Container, typename... Arguments>
    void emplaceTask(Container& c, Arguments&&... args);
        
    using ProducersByQueueName = std::map<std::string, std::unique_ptr<Qpid::QueueProducer>>;
    ProducersByQueueName _producers;

    CallbacksPerQueue  _namedCallbacks;

    using NamedConsumerMap = std::map<const Qpid::Queues::InboundQueue *, std::unique_ptr<Qpid::QueueConsumer>>;
    NamedConsumerMap _namedConsumers;

    //  Temp consumer and client callback
    struct TempQueueConsumerWithCallback
    {
        std::unique_ptr<Qpid::TempQueueConsumer> managedConsumer;
        MessageCallback::Ptr callback;
    };

    using TemporaryCallbacksByDestination = std::map<std::string, TempQueueConsumerWithCallback>;

    //  temp consumers that only last as long as their first reply
    TemporaryCallbacksByDestination _replyConsumers;

    using TemporaryConsumersByDestination = std::map<std::string, std::unique_ptr<Qpid::TempQueueConsumer>>;
    using DestinationsBySessionCallback = std::map<SessionCallback, std::string>;

    //  temp queues that last the lifetime of the session - no timeouts
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

//    bool verifyConnectionObjects();
    void releaseConnectionObjects();

    void updateCallbacks(CallbacksPerQueue newCallbacks);

    void createNamedConsumer(const Qpid::Queues::InboundQueue *inboundQueue);
    std::string createSessionConsumer(const SessionCallback callback);

    void sendOutgoingMessages(EnvelopeQueue messages);
    void sendOutgoingReplies (ReplyQueue replies);
    Qpid::QueueProducer &getQueueProducer( proton::session & session, const std::string & queue );

    std::string getJMSType( proton::message & msg ) const;
};

}
}
