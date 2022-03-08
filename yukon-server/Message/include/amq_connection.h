#pragma once

#include "thread.h"
#include "critical_section.h"
#include "StreamableMessage.h"
#include "connection_base.h"

#include <proton/messaging_handler.hpp>
#include <proton/message.hpp>
#include <proton/session.hpp>
#include <proton/session_options.hpp>
#include <proton/container.hpp>
#include <proton/connection.hpp>


#include <chrono>
#include <queue>
#include <variant>

namespace Cti::Messaging
{
namespace Qpid
{
class QueueConsumer;
class DestinationProducer;
class TempQueueConsumer;

namespace Queues
{
class OutboundQueue;
class InboundQueue;
}   // Queues
namespace Topics 
{
class OutboundTopic;
}   // Topics
}   // Qpid

class IM_EX_MSG ActiveMQConnectionManager :
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
        using Function = CallbackFor<Msg>::type;
        
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
    void start_impl();

    static void enqueueMessage(const Qpid::Queues::OutboundQueue& queue, StreamableMessagePtr message);
    static void enqueueMessage(const Qpid::Queues::OutboundQueue& queue, const SerializedMessage &message);
    static void enqueueMessage(const Qpid::Topics::OutboundTopic& topic, std::string message);

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
    void on_session_open( proton::session & s ) override;

    static bool getSession( proton::messaging_handler & handler );    	
    bool getNewSession( proton::messaging_handler & handler );

    bool _sessionIsAlive;

    proton::container  _container;

    std::thread  _container_thread;

    proton::connection  _connection;

protected:

    struct TimedCallback
    {
        MessageCallback::Ptr callback;
        std::chrono::seconds timeout;
        TimeoutCallback timeoutCallback;
    };

    using ReturnAddress = std::variant<TimedCallback, SessionCallback>;

    using ReturnLabel = std::unique_ptr<ReturnAddress>;

    using OutboundDestination = 
        std::variant<
            const Qpid::Queues::OutboundQueue*,
            const Qpid::Topics::OutboundTopic*>;

    //  Message submission objects and methods
    struct Envelope
    {
        OutboundDestination destination;
        ReturnLabel returnAddress;
        proton::message message;
    };

    using CallbacksPerQueue = std::multimap<const Qpid::Queues::InboundQueue *, MessageCallback::Ptr>;

    std::string makeDestinationForReturnAddress(ReturnAddress returnAddress);

    virtual void enqueueOutgoingMessage(
            const Qpid::Queues::OutboundQueue &queue,
            StreamableMessagePtr message,
            ReturnLabel returnAddress);
    virtual void enqueueOutgoingMessage(
            const Qpid::Queues::OutboundQueue &queue,
            const SerializedMessage &message,
            ReturnLabel returnAddress);
    virtual void enqueueOutgoingMessage(
            const Qpid::Topics::OutboundTopic& topic,
            const std::string message,
            ReturnLabel returnAddress);
    virtual void enqueueOutgoingReply(
            std::string dest,
            const SerializedMessage& message);

    void addNewCallback(const Qpid::Queues::InboundQueue &queue, MessageCallback::Ptr callback);
    void addNewCallback(const Qpid::Queues::InboundQueue &queue, MessageCallbackWithReply callback);
    void addNewCallback(const Qpid::Queues::InboundQueue& queue, MessageCallbackWithReplies callback);
        
    virtual void createConsumersForCallbacks(const CallbacksPerQueue &callbacks);

private:

    CtiCriticalSection _closeConnectionMux;

    std::mutex _callbackMux;

    proton::session _brokerSession;

    template<typename... Arguments>
    void emplaceCallback(Arguments&&... args);
        
    using ProducersByOutboundDestination = std::map<OutboundDestination, std::unique_ptr<Qpid::DestinationProducer>>;
    ProducersByOutboundDestination _producers;

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

    void releaseConnectionObjects();

    void createNamedConsumer(const Qpid::Queues::InboundQueue *inboundQueue);
    std::string createSessionConsumer(const SessionCallback callback);

    void sendOutgoingMessage( Envelope & e );
    Qpid::DestinationProducer& getDestinationProducer( proton::session & session, OutboundDestination destination );

    std::string getJMSType( proton::message & msg ) const;

    void expiry_periodic_task();
};

}

