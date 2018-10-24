#include "precompiled.h"

#include <ws2tcpip.h>

#include "amq_connection.h"

#include "StreamableMessage.h"
#include "RfnBroadcastReplyMessage.h"
#include "RfnWaterNodeMessaging.h"
#include "DeviceCreation.h"
#include "RfnDataStreamingUpdate.h"

#include "utility.h"

#include "cms/connectionfactory.h"

#include "logger.h"
#include "dllbase.h"  //  for getDebugLevel() and DEBUGLEVEL_ACTIVITY_INFO

#include "amq_util.h"
#include "amq_queues.h"

#include "message_factory.h"

#include "std_helper.h"
#include "GlobalSettings.h"
#include "CParms.h"

#include <boost/optional.hpp>
#include <boost/range/algorithm/for_each.hpp>
#include <boost/range/adaptor/adjacent_filtered.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/adaptor/map.hpp>

using Cti::Logging::Vector::Hex::operator<<;

namespace Cti::Messaging {

extern IM_EX_MSG std::unique_ptr<ActiveMQConnectionManager> gActiveMQConnection;

std::atomic_size_t ActiveMQConnectionManager::SessionCallback::globalId;

ActiveMQConnectionManager::MessageDescriptor::~MessageDescriptor() {
    delete replyTo;
}

ActiveMQConnectionManager::ActiveMQConnectionManager() = default;

ActiveMQConnectionManager::~ActiveMQConnectionManager()
{
    try
    {
        close();
    }
    catch(...)
    {

    }
}

void ActiveMQConnectionManager::start()
{
    static_cast<CtiThread *>(gActiveMQConnection.get())->start();
}

void ActiveMQConnectionManager::close()
{
    if( isRunning() )
    {
        interrupt(CtiThread::SHUTDOWN);

        {
            CtiLockGuard<CtiCriticalSection> lock(_closeConnectionMux);

            if( _connection )
            {
                _connection->close();
            }
        }

        join(); // outside lock to avoid deadlock
    }

    releaseConnectionObjects();
}

inline bool debugActivityInfo()
{
    return getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO;
}


void ActiveMQConnectionManager::releaseConnectionObjects()
{
    CtiLockGuard<CtiCriticalSection> lock(_closeConnectionMux);

    _producers.clear();
    _namedConsumers.clear();
    _replyConsumers.clear();
    _sessionConsumers.clear();
    _sessionConsumerDestinations.clear();

    _producerSession.reset();
    _consumerSession.reset();

    _connection.reset();
}


bool ActiveMQConnectionManager::MessagingTasks::empty() const
{
    return newCallbacks    .empty()
        && outgoingMessages.empty()
        && tempQueueReplies.empty()
        && sessionReplies  .empty()
        && incomingMessages.empty();
}


auto ActiveMQConnectionManager::getTasks() -> MessagingTasks
{
    MessagingTasks tasks;

    std::swap(tasks, _newTasks);

    return tasks;
}


void ActiveMQConnectionManager::run()
{
    while( ! isSet(SHUTDOWN) )
    {
        try
        {
            if( verifyConnectionObjects() )
            {
                MessagingTasks tasks;

                {
                    std::unique_lock taskLock(_taskMux);

                    tasks = getTasks();

                    while( tasks.empty() && ! isSet(SHUTDOWN)  )
                    {
                        _newTask.wait_for(taskLock, std::chrono::seconds(5));  //  only wait for 5 seconds to expedite shutdown

                        tasks = getTasks();
                    }
                }

                processTasks(std::move(tasks));
            }
        }
        catch( ActiveMQ::ConnectionException &e )
        {
            releaseConnectionObjects();
            if (!isSet(SHUTDOWN))
            {
                CTILOG_EXCEPTION_ERROR(dout, e, "Unable to connect to the broker");
            }
        }
        catch( cms::CMSException &e )
        {
            releaseConnectionObjects();

            if (!isSet(SHUTDOWN))
            {
                CTILOG_EXCEPTION_ERROR(dout, e);
            }
        }
    }
}


void ActiveMQConnectionManager::processTasks(MessagingTasks tasks)
{
    updateCallbacks         (std::move(tasks.newCallbacks));
    sendOutgoingMessages    (std::move(tasks.outgoingMessages));
    dispatchTempQueueReplies(std::move(tasks.tempQueueReplies));
    dispatchSessionReplies  (std::move(tasks.sessionReplies));
    dispatchIncomingMessages(std::move(tasks.incomingMessages));
}


bool ActiveMQConnectionManager::verifyConnectionObjects()
{
    if( ! _connection ||
        ! _connection->verifyConnection() )
    {
        CTILOG_INFO(dout, "Connection invalid, creating connection");

        releaseConnectionObjects();

        {
            CtiLockGuard<CtiCriticalSection> lock(_closeConnectionMux);

            if( isSet(SHUTDOWN) )
            {
                return false; // prevent starting a new connection while closing
            }

            const auto broker_host = GlobalSettings::getString(GlobalSettings::Strings::JmsBrokerHost, ActiveMQ::Broker::defaultHost);
            const auto broker_port = GlobalSettings::getString(GlobalSettings::Strings::JmsBrokerPort, ActiveMQ::Broker::defaultPort);

            // MaxInactivityDuration controls how long AMQ keeps a socket open when it's not heard from it.
            const auto maxInactivityDuration = "wireFormat.MaxInactivityDuration=" +
                std::to_string( GlobalSettings::getInteger( GlobalSettings::Integers::MaxInactivityDuration, 30 ) * 1000 );

            _connection = std::make_unique<ActiveMQ::ManagedConnection>( ActiveMQ::Broker::protocol + broker_host + ":" + broker_port + "?" + maxInactivityDuration );
        }

        _connection->start(); // start the connection outside the lock

        CTILOG_INFO(dout, "ActiveMQ CMS connection established");
    }

    if( ! _producerSession.get() )
    {
        CTILOG_INFO(dout, "Producer session invalid, creating producer");

        _producerSession = _connection->createSession();
    }

    if( ! _consumerSession.get() )
    {
        CTILOG_INFO(dout,  "Consumer session invalid, creating consumer");

        _consumerSession = _connection->createSession();

        createConsumersForCallbacks(_namedCallbacks);
    }

    return true;
}


void ActiveMQConnectionManager::createConsumersForCallbacks(const CallbacksPerQueue &callbacks)
{
    using ActiveMQ::Queues::InboundQueue;

    boost::for_each(
            callbacks
                | boost::adaptors::map_keys
                | boost::adaptors::adjacent_filtered(std::not_equal_to<const InboundQueue *>{})
                | boost::adaptors::filtered([this](const InboundQueue *q) { return ! _namedConsumers.count(q); }),
            [this](const InboundQueue *q)
            {
                createNamedConsumer(q);
            });
}


void ActiveMQConnectionManager::createNamedConsumer(const ActiveMQ::Queues::InboundQueue *inboundQueue)
{
    auto consumer = std::make_unique<QueueConsumerWithListener>();

    consumer->managedConsumer = 
            ActiveMQ::createQueueConsumer(
                    *_consumerSession,
                    inboundQueue->name);

    consumer->listener =
            std::make_unique<ActiveMQ::MessageListener>(
                    [=](const cms::Message *msg)
                    {
                        acceptNamedMessage(msg, inboundQueue);
                    });

    consumer->managedConsumer->setMessageListener(
            consumer->listener.get());

    CTILOG_INFO(dout, "Listener registered for destination \"" << inboundQueue->name << "\" id " << reinterpret_cast<unsigned long>(inboundQueue));

    _namedConsumers.emplace(inboundQueue, std::move(consumer));
}


auto ActiveMQConnectionManager::createSessionConsumer(const SessionCallback callback) -> const cms::Destination*
{
    auto consumer = std::make_unique<TempQueueConsumerWithCallback>();

    consumer->managedConsumer = ActiveMQ::createTempQueueConsumer(*_consumerSession);

    static const auto sessionListener = 
        std::make_unique<ActiveMQ::MessageListener>(
            [this](const cms::Message *msg)
            {
                acceptSessionReply(msg);
            });

    consumer->managedConsumer->setMessageListener(sessionListener.get());

    consumer->callback = std::make_unique<SimpleMessageCallback>(callback.callback);

    auto destination = consumer->managedConsumer->getDestination();

    CTILOG_INFO(dout, "Session listener " << reinterpret_cast<unsigned long>(&callback) << " registered for temp queue \"" << consumer->managedConsumer->getDestPhysicalName() << "\"");

    _sessionConsumerDestinations.emplace(callback, consumer->managedConsumer->getDestination());
    _sessionConsumers.emplace(consumer->managedConsumer->getDestPhysicalName(), std::move(consumer));

    return destination;
}


void ActiveMQConnectionManager::updateCallbacks(CallbacksPerQueue newCallbacks)
{
    createConsumersForCallbacks(newCallbacks);

    std::move(newCallbacks.begin(),
              newCallbacks.end(),
              std::inserter(_namedCallbacks, _namedCallbacks.end()));
}


void ActiveMQConnectionManager::sendOutgoingMessages(EnvelopeQueue messages)
{
    while( ! messages.empty() )
    {
        auto e = std::move(messages.front());

        messages.pop();

        auto message = e->extractMessage(*_producerSession);

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Sending outgoing message for queue " << e->queueName);
        }

        if( e->returnAddress )
        {
            auto destination = makeDestinationForReturnAddress(std::move(*e->returnAddress));

            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Setting reply-to destination " << destination << " on message for queue " << e->queueName);
            }

            message->setCMSReplyTo(destination);
        }

        ActiveMQ::QueueProducer &queueProducer = getQueueProducer(*_producerSession, e->queueName);

        queueProducer.send(message.get());
    }
}


const cms::Destination* ActiveMQConnectionManager::makeDestinationForReturnAddress(ReturnAddress returnAddress)
{
    if( auto callback = boost::get<TimedCallback>(&returnAddress) )
    {
        auto tempConsumer = std::make_unique<TempQueueConsumerWithCallback>();

        tempConsumer->managedConsumer = ActiveMQ::createTempQueueConsumer(*_consumerSession);

        tempConsumer->callback = std::move(callback->callback);

        static const auto singleReplyListener = 
            std::make_unique<ActiveMQ::MessageListener>(
                [this](const cms::Message *msg)
        {
            acceptSingleReply(msg);
        });

        tempConsumer->managedConsumer->setMessageListener(singleReplyListener.get());

        //  copy the key string for inserting, since tempConsumer will be invalidated
        //    when passed as a parameter to ptr_map::insert()
        const std::string destinationPhysicalName = tempConsumer->managedConsumer->getDestPhysicalName();

        const auto destination = tempConsumer->managedConsumer->getDestination();

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Created temporary queue for callback reply for " << destinationPhysicalName << " with destination " << destination);
        }

        _replyConsumers.emplace(
            destinationPhysicalName,
            std::move(tempConsumer));

        _replyExpirations.emplace(
            CtiTime::now().addSeconds(callback->timeout.count()),  //  extract raw seconds out of the timeout duration
            ExpirationHandler {
                destinationPhysicalName,
                callback->timeoutCallback });

        return destination;
    }
    else if( auto callback = boost::get<SessionCallback>(&returnAddress) )
    {
        if( auto existingDestination = mapFind(_sessionConsumerDestinations, *callback) )
        {
            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Found existing session consumer destination " << *existingDestination);
            }

            return *existingDestination;
        }

        auto newDestination = createSessionConsumer(*callback);

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Created new session consumer destination " << newDestination);
        }

        return newDestination;
    }

    CTILOG_ERROR(dout, "No destination found for message");

    return nullptr;
}


auto ActiveMQConnectionManager::makeReturnLabel(MessageCallback::Ptr callback, std::chrono::seconds timeout, TimeoutCallback timeoutCallback) -> ReturnLabel
{
    TimedCallback tc;

    tc.callback = std::move(callback);
    tc.timeout = timeout;
    tc.timeoutCallback = timeoutCallback;

    return std::make_unique<ReturnAddress>(std::move(tc));
}

auto ActiveMQConnectionManager::makeReturnLabel(SessionCallback& sessionCallback) -> ReturnLabel
{
    return std::make_unique<ReturnAddress>(sessionCallback);
}


void ActiveMQConnectionManager::dispatchIncomingMessages(IncomingPerQueue incomingMessages)
{
    for( auto& [queue, descriptors] : incomingMessages )
    {
        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Received incoming message(s) for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue));
        }

        const auto callbackRange = _namedCallbacks.equal_range(queue);
        const auto callbacks     = boost::make_iterator_range(callbackRange.first, callbackRange.second) | boost::adaptors::map_values;

        while( ! descriptors.empty() )
        {
            auto md = std::move(descriptors.front());
            descriptors.pop();

            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Dispatching message to callbacks from queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue) << std::endl
                        << reinterpret_cast<unsigned long>(queue) << ": " << md->msg);
            }

            for( auto& callback : callbacks )
            {
                if( debugActivityInfo() )
                {
                    CTILOG_DEBUG(dout, "Calling callback " << reinterpret_cast<unsigned long>(callback.get()) << " for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue));
                }

                (*callback)(*md);
            }
        }
    }
}


void ActiveMQConnectionManager::dispatchTempQueueReplies(RepliesByDestination replies)
{
    for( const auto& [destination, descriptor] : replies )
    {
        //  need the iterator so we can delete later
        auto itr = _replyConsumers.find(destination);

        if( itr == _replyConsumers.end() )
        {
            CTILOG_ERROR(dout, "Message received with no consumer; destination [" << destination << "]");
            continue;
        }

        const auto &consumer = itr->second;

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Calling temp queue callback " << reinterpret_cast<unsigned long>(consumer->callback.get()) << " for temp queue \"" << destination << "\"" << std::endl
                    << reinterpret_cast<unsigned long>(consumer->callback.get()) << ": " << descriptor->msg);
        }

        (*consumer->callback)(*descriptor);

        _replyConsumers.erase(itr);
    }

    const auto end = _replyExpirations.upper_bound(CtiTime::now());

    if( _replyExpirations.begin() != end )
    {
        for( auto itr = _replyExpirations.begin(); itr != end; ++itr )
        {
            const auto &expirationHandler = itr->second;

            const auto consumer_itr = _replyConsumers.find(expirationHandler.queueName);

            //  if the queue consumer still exists, we never got a response
            if( consumer_itr != _replyConsumers.end() )
            {
                _replyConsumers.erase(consumer_itr);

                expirationHandler.callback();
            }
        }

        _replyExpirations.erase(_replyExpirations.begin(), end);
    }
}


void ActiveMQConnectionManager::dispatchSessionReplies(RepliesByDestination replies)
{
    for( auto& [destination, descriptor] : replies )
    {
        if( auto consumer = mapFindRef(_sessionConsumers, destination) )
        {
            const auto& callback = (*consumer)->callback;

            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Calling session callback " << reinterpret_cast<unsigned long>(callback.get()) << " for temp queue \"" << destination << "\"" << std::endl
                    << reinterpret_cast<unsigned long>(callback.get()) << ": " << descriptor->msg);
            }

            (*callback)(*descriptor);
        }
        else
        {
            CTILOG_ERROR(dout, "Message received with no consumer; destination [" << destination << "]");
        }
    }
}


void ActiveMQConnectionManager::enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type&& message)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, std::move(message), nullptr);
}

void ActiveMQConnectionManager::enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, nullptr);
}

template<typename Msg>
struct DeserializationHelper : ActiveMQConnectionManager::MessageCallback
{
    using CallbackForMsg = typename ActiveMQConnectionManager::CallbackFor<Msg>::Ptr;
    const CallbackForMsg callback;

    DeserializationHelper(CallbackForMsg callback_) : callback(std::move(callback_)) {}

    void operator()(const ActiveMQConnectionManager::MessageDescriptor &md) const override
    {
        if( const auto msg = Serialization::MessageSerializer<Msg>::deserialize(md.msg) )
        {
            (*callback)(*msg);
        }
        else
        {
            CTILOG_ERROR(dout, "Failed to deserialize " << typeid(Msg).name() << " from payload [" << md.msg << "]");
        }
    }
};


template<typename Msg>
void ActiveMQConnectionManager::enqueueMessageWithCallbackFor(
        const ActiveMQ::Queues::OutboundQueue &queue,
        StreamableMessage::auto_type&& message,
        typename CallbackFor<Msg>::type callback,
        std::chrono::seconds timeout,
        TimeoutCallback timedOut)
{
    auto callbackWrapper = std::make_unique<DeserializationHelper<Msg>>(std::make_unique<SimpleCallbackFor<Msg>>(callback));

    gActiveMQConnection->enqueueOutgoingMessage(queue.name, std::move(message), gActiveMQConnection->makeReturnLabel(std::move(callbackWrapper), timeout, timedOut));
}


template<typename Msg>
void ActiveMQConnectionManager::enqueueMessageWithCallbackFor(
        const ActiveMQ::Queues::OutboundQueue &queue,
        const SerializedMessage &message,
        typename CallbackFor<Msg>::Ptr callback,
        std::chrono::seconds timeout,
        TimeoutCallback timedOut)
{
    auto callbackWrapper = std::make_unique<DeserializationHelper<Msg>>(std::move(callback));

    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, gActiveMQConnection->makeReturnLabel(std::move(callbackWrapper), timeout, timedOut));
}


template<typename Msg>
void ActiveMQConnectionManager::enqueueMessageWithCallbackFor(
        const ActiveMQ::Queues::OutboundQueue &queue, 
        const SerializedMessage &message, 
        typename CallbackFor<Msg>::type callback, 
        std::chrono::seconds timeout, 
        TimeoutCallback timedOut)
{
    auto callbackWrapper = std::make_unique<DeserializationHelper<Msg>>(std::make_unique<SimpleCallbackFor<Msg>>(callback));

    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, gActiveMQConnection->makeReturnLabel(std::move(callbackWrapper), timeout, timedOut));
}


void ActiveMQConnectionManager::enqueueMessageWithCallback(
        const ActiveMQ::Queues::OutboundQueue &queue,
        const SerializedMessage &message,
        MessageCallback::type callback,
        std::chrono::seconds timeout,
        TimeoutCallback timedOut)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, gActiveMQConnection->makeReturnLabel(std::make_unique<SimpleMessageCallback>(callback), timeout, timedOut));
}


void ActiveMQConnectionManager::enqueueMessageWithSessionCallback(
    const ActiveMQ::Queues::OutboundQueue &queue,
    const SerializedMessage &message,
    SessionCallback callback)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, gActiveMQConnection->makeReturnLabel(callback));
}


template<class T, class U>
void emplaceHelper(std::queue<T>& q, U && value)
{
    q.emplace(std::move(value));
}


template<class T, class U, class V>
void emplaceHelper(std::multimap<T, U>& mm, T key, V && value)
{
    mm.emplace(key, std::move(value));
}


template<class T, class U>
void emplaceHelper(std::map<T, std::queue<U>>& m, T key, U && value)
{
    m[key].emplace(std::move(value));
}


template<class Container, typename... Arguments>
void ActiveMQConnectionManager::emplaceTask(Container& c, Arguments&&... args)
{
    std::unique_lock taskLock(_taskMux);

    emplaceHelper(c, std::move(args)...);

    _newTask.notify_one();
}


void ActiveMQConnectionManager::kickstart()
{
    if( ! isRunning() )
    {
        start();
    }
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(
        const std::string &queueName,
        StreamableMessage::auto_type&& message,
        ReturnLabel returnAddress)
{
    //  ensure the message is not null
    if( ! message.get() )
    {
        return;
    }

    struct StreamableEnvelope : Envelope
    {
        std::unique_ptr<const StreamableMessage> message;

        std::unique_ptr<cms::Message> extractMessage(cms::Session &session) const
        {
            std::unique_ptr<cms::StreamMessage> streamMessage { session.createStreamMessage() };

            message->streamInto(*streamMessage);

            return std::move(streamMessage);  //  move to downcast to cms::Message
        }
    };

    auto e = std::make_unique<StreamableEnvelope>();

    e->queueName = queueName;
    e->message = std::move(message);
    e->returnAddress = std::move(returnAddress);

    emplaceTask(_newTasks.outgoingMessages, std::move(e));

    kickstart();
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(
        const std::string &queueName,
        const SerializedMessage &message,
        ReturnLabel returnAddress)
{
    struct BytesEnvelope : Envelope
    {
        SerializedMessage message;

        std::unique_ptr<cms::Message> extractMessage(cms::Session &session) const
        {
            std::unique_ptr<cms::BytesMessage> bytesMessage { session.createBytesMessage() };

            bytesMessage->writeBytes(message);

            return std::move(bytesMessage);
        }
    };

    auto e = std::make_unique<BytesEnvelope>();

    e->queueName = queueName;
    e->message   = message;
    e->returnAddress = std::move(returnAddress);

    if( debugActivityInfo() )
    {
        CTILOG_DEBUG(dout,  "Enqueuing outbound message for queue \"" << queueName << "\"" << std::endl << message);
    }

    emplaceTask(_newTasks.outgoingMessages, std::move(e));

    kickstart();
}


ActiveMQ::QueueProducer &ActiveMQConnectionManager::getQueueProducer(cms::Session &session, const std::string &queueName)
{
    if( const auto existingProducer = mapFindRef(_producers, queueName) )
    {
        return **existingProducer;
    }

    //  if it doesn't exist, try to make one
    auto queueProducer = ActiveMQ::createQueueProducer(session, queueName);

    CTILOG_INFO(dout, "ActiveMQ CMS producer established (" << queueName << ")");

    queueProducer->setTimeToLiveMillis(DefaultTimeToLiveMillis);

    auto result = _producers.emplace(queueName, std::move(queueProducer));

    return *(result.first->second);
}


void ActiveMQConnectionManager::registerHandler(const ActiveMQ::Queues::InboundQueue &queue, MessageCallback::type callback)
{
    gActiveMQConnection->addNewCallback(queue, std::make_unique<SimpleMessageCallback>(callback));
}


void ActiveMQConnectionManager::registerReplyHandler(const ActiveMQ::Queues::InboundQueue &queue, MessageCallbackWithReply callback)
{
    gActiveMQConnection->addNewCallback(queue, callback);
}


auto ActiveMQConnectionManager::registerSessionCallback(MessageCallback::type callback) -> SessionCallback
{
    //  implicitly construct a SessionCallback from the MessageCallback::type
    return callback;
}


void ActiveMQConnectionManager::addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, MessageCallback::Ptr callback)
{
    emplaceTask(_newTasks.newCallbacks, &queue, std::move(callback));

    kickstart();
}


void ActiveMQConnectionManager::addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, MessageCallbackWithReply callback)
{
    auto wrappedCallback = 
        std::make_unique<SimpleMessageCallback>(
            [=, &queue](const MessageDescriptor &md)
            {
                auto tempQueueProducer = ActiveMQ::createDestinationProducer(*_producerSession, md.replyTo);

                auto result = callback(md);

                if( ! result )
                {
                    CTILOG_WARN(dout, "Callback-with-reply returned no reply for queue " << queue.name);

                    return;
                }

                std::unique_ptr<cms::BytesMessage> bytesMessage{ _producerSession->createBytesMessage() };

                bytesMessage->writeBytes(*result);

                tempQueueProducer->send(bytesMessage.release());
        });

    emplaceTask(_newTasks.newCallbacks, &queue, std::move(wrappedCallback));

    kickstart();
}


void ActiveMQConnectionManager::acceptNamedMessage(const cms::Message *message, const ActiveMQ::Queues::InboundQueue *queue)
{
    if( const cms::BytesMessage *bytesMessage = dynamic_cast<const cms::BytesMessage *>(message) )
    {
        const auto replyTo = bytesMessage->getCMSReplyTo()
                ? bytesMessage->getCMSReplyTo()->clone()
                : nullptr;

        std::vector<unsigned char> payload(bytesMessage->getBodyLength());

        bytesMessage->readBytes(payload);

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Received inbound message for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue) << std::endl
                << reinterpret_cast<unsigned long>(queue) << ": " << payload);
        }

        emplaceNamedMessage(queue, bytesMessage->getCMSType(), payload, replyTo);
    }
}

void ActiveMQConnectionManager::emplaceNamedMessage(const ActiveMQ::Queues::InboundQueue* queue, const std::string type, std::vector<unsigned char> payload, cms::Destination* replyTo)
{
    auto md = std::make_unique<MessageDescriptor>();

    md->type = type;
    md->msg = payload;
    md->replyTo = replyTo;

    emplaceTask(_newTasks.incomingMessages, queue, std::move(md));
}


void ActiveMQConnectionManager::acceptSingleReply(const cms::Message *message)
{
    if( const cms::BytesMessage *bytesMessage = dynamic_cast<const cms::BytesMessage *>(message) )
    {
        if( const cms::Destination *dest = message->getCMSDestination() )
        {
            auto md = std::make_unique<MessageDescriptor>();

            md->type = bytesMessage->getCMSType();

            md->msg.resize(bytesMessage->getBodyLength());

            bytesMessage->readBytes(md->msg);

            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Received temp queue reply for \"" << ActiveMQ::destPhysicalName(*dest) << "\"" << std::endl
                    << ActiveMQ::destPhysicalName(*dest) << ": " << md->msg);
            }

            emplaceTask(_newTasks.tempQueueReplies, ActiveMQ::destPhysicalName(*dest), std::move(md));
        }
    }
}


void ActiveMQConnectionManager::acceptSessionReply(const cms::Message *message)
{
    if( const cms::BytesMessage *bytesMessage = dynamic_cast<const cms::BytesMessage *>(message) )
    {
        if( const cms::Destination *dest = message->getCMSDestination() )
        {
            auto md = std::make_unique<MessageDescriptor>();

            md->type = bytesMessage->getCMSType();

            md->msg.resize(bytesMessage->getBodyLength());

            bytesMessage->readBytes(md->msg);

            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Received temp queue reply for \"" << ActiveMQ::destPhysicalName(*dest) << "\"" << std::endl
                    << ActiveMQ::destPhysicalName(*dest) << ": " << md->msg);
            }

            emplaceTask(_newTasks.sessionReplies, ActiveMQ::destPhysicalName(*dest), std::move(md));
        }
    }
}


template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnBroadcastReplyMessage>(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type&& message, CallbackFor<Rfn::RfnBroadcastReplyMessage>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnSetChannelConfigReplyMessage>(const ActiveMQ::Queues::OutboundQueue &queue, const ActiveMQConnectionManager::SerializedMessage & message, CallbackFor<Rfn::RfnSetChannelConfigReplyMessage>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnGetChannelConfigReplyMessage>(const ActiveMQ::Queues::OutboundQueue &queue, const ActiveMQConnectionManager::SerializedMessage & message, CallbackFor<Rfn::RfnGetChannelConfigReplyMessage>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::DataStreamingUpdateReplyMessage>(const ActiveMQ::Queues::OutboundQueue &queue, const ActiveMQConnectionManager::SerializedMessage & message, CallbackFor<Rfn::DataStreamingUpdateReplyMessage>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<RfnDeviceCreationReplyMessage>(const ActiveMQ::Queues::OutboundQueue &queue, const ActiveMQConnectionManager::SerializedMessage & message, CallbackFor<RfnDeviceCreationReplyMessage>::Ptr callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

}

