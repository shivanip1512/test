#include "precompiled.h"

#include <ws2tcpip.h>

#include "amq_connection.h"

#include "StreamableMessage.h"
#include "RfnBroadcastReplyMessage.h"
#include "RfnWaterNodeMessaging.h"
#include "DeviceCreation.h"
#include "RfnDataStreamingUpdate.h"

#include "utility.h"

#include "logger.h"
#include "dllbase.h"  //  for getDebugLevel() and DEBUGLEVEL_ACTIVITY_INFO

#include "amq_util.h"
#include "amq_queues.h"

#include "message_factory.h"

#include "std_helper.h"
#include "GlobalSettings.h"
#include "CParms.h"

#include "proton_encoder_proxy.h"

#include <proton/types.hpp>
#include <proton/connection_options.hpp>
#include <proton/reconnect_options.hpp>
#include <proton/session_options.hpp>
#include <proton/work_queue.hpp>

#include <boost/range/algorithm/for_each.hpp>
#include <boost/range/adaptor/adjacent_filtered.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/adaptor/map.hpp>

using Cti::Logging::Vector::Hex::operator<<;

namespace Cti::Messaging {

extern IM_EX_MSG std::unique_ptr<ActiveMQConnectionManager> gActiveMQConnection;

std::atomic_size_t ActiveMQConnectionManager::SessionCallback::globalId;


ActiveMQConnectionManager::ActiveMQConnectionManager()
    : _container()
{
    _container_thread = std::thread([&]() { _container.run(); });
}

ActiveMQConnectionManager::~ActiveMQConnectionManager()
{
    try
    {
        close();

        _container_thread.join();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void ActiveMQConnectionManager::on_connection_open( proton::connection & c )
{
    CTILOG_INFO(dout, "Broker connection established");

    if ( ! c.reconnected() )
    {
        proton::session_options options;
        options.handler( *this );

        _brokerSession = c.open_session( options );
    }
}


void ActiveMQConnectionManager::on_connection_close( proton::connection & c )
{
    CTILOG_INFO(dout, "Broker connection closed");
}


void ActiveMQConnectionManager::on_session_open( proton::session & s )
{
    CTILOG_INFO(dout, "Broker session established");

    // register callbacks

    createConsumersForCallbacks( _namedCallbacks );

    // start the processing of the expirations

    s.work_queue()
        .schedule( 5 * proton::duration::SECOND,
                   [ this ]()
                   {
                       this->expiry_periodic_task();
                   } );
}

proton::session ActiveMQConnectionManager::getSession( proton::messaging_handler & handler )
{
    return gActiveMQConnection->getNewSession( handler );
}

proton::session ActiveMQConnectionManager::getNewSession( proton::messaging_handler & handler )
{
    // get the connection and create a new session from it

    auto connection = _brokerSession.connection();

    proton::session_options options;
    options.handler( handler );

    return connection.open_session( options );
}

void ActiveMQConnectionManager::start()
{
    const auto broker_host = GlobalSettings::getString(GlobalSettings::Strings::JmsBrokerHost, Qpid::Broker::defaultHost);
    const auto broker_port = GlobalSettings::getString(GlobalSettings::Strings::JmsBrokerPort, Qpid::Broker::defaultPort);

    const auto brokerUri = Qpid::Broker::protocol + broker_host + ":" + broker_port;

    // the idle timeout
    const auto idle = GlobalSettings::getInteger( GlobalSettings::Integers::MaxInactivityDuration, 30 );    // seconds

    proton::connection_options  options;

    options
        .handler( *this )
        .idle_timeout( idle * proton::duration::SECOND )
        .reconnect( proton::reconnect_options()
                        .delay( 1 * proton::duration::SECOND )
                        .max_delay( 30 * proton::duration::SECOND )
                    );

    _container.connect( brokerUri, options );
}

void ActiveMQConnectionManager::close()
{
    // this closes all the producers and consumers - link is closed in their destructors

    releaseConnectionObjects();

    // close the session and connection
    {
        CtiLockGuard<CtiCriticalSection> lock(_closeConnectionMux);

        auto connection = _brokerSession.connection();

   //     if ( _brokerSession  )
    //    {
           _brokerSession.close();
    //    }


        connection.close();
    }
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
}


void ActiveMQConnectionManager::createConsumersForCallbacks(const CallbacksPerQueue &callbacks)
{
    using Qpid::Queues::InboundQueue;

    boost::for_each(
            callbacks
                | boost::adaptors::map_keys
                | boost::adaptors::adjacent_filtered(std::not_equal_to<const InboundQueue *>{}),
            [this](const InboundQueue *q)
            {
                createNamedConsumer(q);
            });
}


void ActiveMQConnectionManager::createNamedConsumer(const Qpid::Queues::InboundQueue * queue)
{
    auto consumer =
        Qpid::createQueueConsumer(
            _brokerSession,
            queue->name,
            [ = ]( proton::message & message )
            {                     
                MessageDescriptor   descriptor
                {
                    getJMSType( message ),
                    proton::get<proton::binary>( message.body() ),
                    message.reply_to()
                };

                if ( debugActivityInfo() )
                {
                    CTILOG_DEBUG(dout, "Received inbound message for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue) << std::endl
                                            << reinterpret_cast<unsigned long>(queue) << ": " << descriptor.msg);
                }

                const auto callbackRange = _namedCallbacks.equal_range(queue);
                const auto callbacks     = boost::make_iterator_range(callbackRange.first, callbackRange.second) | boost::adaptors::map_values;

                for ( auto& callback : callbacks )
                {
                    if ( debugActivityInfo() )
                    {
                        CTILOG_DEBUG(dout, "Calling callback " << reinterpret_cast<unsigned long>(callback.get()) << " for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue));
                    }

                    (*callback)(descriptor);
                }
            } );

    CTILOG_INFO(dout, "Listener registered for destination \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue));

    _namedConsumers.emplace(
       queue,
       std::move(consumer) );
}


std::string ActiveMQConnectionManager::createSessionConsumer(const SessionCallback sessionCallback)
{
    auto consumer =
        Qpid::createTempQueueConsumer(
            _brokerSession,
            [ this, sessionCallback ]( proton::message & msg )
            {
                MessageDescriptor   descriptor
                {
                    getJMSType( msg ),
                    proton::get<proton::binary>( msg.body() ),
                    msg.reply_to()
                };

                if ( debugActivityInfo() )
                {
                    auto x = reinterpret_cast<unsigned long>(sessionCallback.callback.target<void (*)(const MessageDescriptor&)>());

                    CTILOG_DEBUG(dout, "Calling session callback " << x << " for temp queue \"" << descriptor.replyTo << "\"" << std::endl
                                            << x << ": " << descriptor.msg);
                }

                (sessionCallback.callback)(descriptor);
            } );

    auto destination = consumer->getDestination();

    CTILOG_INFO(dout, "Session listener " << reinterpret_cast<unsigned long>(&sessionCallback) << " registered for temp queue \"" << destination << "\"");

    _sessionConsumerDestinations.emplace(
       sessionCallback,
       destination );

    _sessionConsumers.emplace(
       destination,
       std::move(consumer) );

    return destination;
}


void ActiveMQConnectionManager::sendOutgoingMessage( Envelope & e )
{
    if( debugActivityInfo() )
    {
        CTILOG_DEBUG(dout, "Sending outgoing message for queue " << e.queueName);
    }

    if( e.returnAddress )
    {
        auto destination = makeDestinationForReturnAddress(std::move(*e.returnAddress));

        if ( destination.empty() )
        {
            CTILOG_ERROR( dout, "No reply-to destination found for message for queue " << e.queueName << " - aborting send." );

            return;
        }

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Setting reply-to destination " << destination << " on message for queue " << e.queueName);
        }

        e.message.reply_to( destination );
    }

    Qpid::QueueProducer & queueProducer = getQueueProducer( _brokerSession, e.queueName );

    queueProducer.send( e.message );
}


std::string ActiveMQConnectionManager::makeDestinationForReturnAddress(ReturnAddress returnAddress)
{
    if( auto timedCallback = std::get_if<TimedCallback>(&returnAddress) )
    {
        auto tempConsumer = 
            Qpid::createTempQueueConsumer(
                _brokerSession,    
                [ this ]( proton::message & msg )
                {
                    MessageDescriptor   descriptor
                    {
                        getJMSType( msg ),
                        proton::get<proton::binary>( msg.body() ),
                        msg.reply_to()
                    };

                    auto itr = _replyConsumers.find( msg.to() );

                    if( itr == _replyConsumers.end() )
                    {
                        CTILOG_ERROR(dout, "Message received with no consumer; destination [" << msg.to() << "]");
                        return;
                    }

                    auto& callback = itr->second.callback;

                    if( debugActivityInfo() )
                    {
                        CTILOG_DEBUG(dout, "Calling temp queue callback " << reinterpret_cast<unsigned long>(callback.get())
                                            << " for temp queue \"" << descriptor.replyTo << "\"" << std::endl
                                            << reinterpret_cast<unsigned long>(callback.get()) << ": " << descriptor.msg);
                    }

                    (*callback)(descriptor);

                    _replyConsumers.erase(itr);
                } );

        const std::string destination = tempConsumer->getDestination();

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Created temporary queue for callback reply for " << destination );
        }

        _replyConsumers.emplace(
            destination,
            TempQueueConsumerWithCallback {
                std::move(tempConsumer),
                std::move(timedCallback->callback) });

        _replyExpirations.emplace(
            CtiTime::now().addSeconds(timedCallback->timeout.count()),  //  extract raw seconds out of the timeout duration
            ExpirationHandler {
                destination,
                timedCallback->timeoutCallback });

        return destination;
    }
    else if( auto callback = std::get_if<SessionCallback>(&returnAddress) )
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

    return "";
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


void ActiveMQConnectionManager::enqueueMessage(const Qpid::Queues::OutboundQueue &queue, StreamableMessagePtr message)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, std::move(message), nullptr);
}

void ActiveMQConnectionManager::enqueueMessage(const Qpid::Queues::OutboundQueue &queue, const SerializedMessage &message)
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
        const Qpid::Queues::OutboundQueue &queue,
        StreamableMessagePtr message,
        typename CallbackFor<Msg>::type callback,
        std::chrono::seconds timeout,
        TimeoutCallback timedOut)
{
    auto callbackWrapper = std::make_unique<DeserializationHelper<Msg>>(std::make_unique<SimpleCallbackFor<Msg>>(callback));

    gActiveMQConnection->enqueueOutgoingMessage(queue.name, std::move(message), gActiveMQConnection->makeReturnLabel(std::move(callbackWrapper), timeout, timedOut));
}


template<typename Msg>
void ActiveMQConnectionManager::enqueueMessageWithCallbackFor(
        const Qpid::Queues::OutboundQueue &queue,
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
        const Qpid::Queues::OutboundQueue &queue, 
        const SerializedMessage &message, 
        typename CallbackFor<Msg>::type callback, 
        std::chrono::seconds timeout, 
        TimeoutCallback timedOut)
{
    auto callbackWrapper = std::make_unique<DeserializationHelper<Msg>>(std::make_unique<SimpleCallbackFor<Msg>>(callback));

    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, gActiveMQConnection->makeReturnLabel(std::move(callbackWrapper), timeout, timedOut));
}


void ActiveMQConnectionManager::enqueueMessageWithCallback(
        const Qpid::Queues::OutboundQueue &queue,
        const SerializedMessage &message,
        MessageCallback::type callback,
        std::chrono::seconds timeout,
        TimeoutCallback timedOut)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, gActiveMQConnection->makeReturnLabel(std::make_unique<SimpleMessageCallback>(callback), timeout, timedOut));
}


void ActiveMQConnectionManager::enqueueMessageWithSessionCallback(
    const Qpid::Queues::OutboundQueue &queue,
    const SerializedMessage &message,
    SessionCallback callback)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, gActiveMQConnection->makeReturnLabel(callback));
}


template<class T, class U, class V>
void emplaceHelper(std::multimap<T, U>& mm, T key, V && value)
{
    mm.emplace(key, std::move(value));
}


template<typename... Arguments>
void ActiveMQConnectionManager::emplaceCallback(Arguments&&... args)
{
    std::unique_lock lock(_callbackMux);

    emplaceHelper(_namedCallbacks, std::move(args)...);
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(
        const std::string &queueName,
        StreamableMessagePtr message,
        ReturnLabel returnAddress)
{
    //  ensure the message is not null
    if( ! message.get() )
    {
        return;
    }

    Envelope e;

    {
        // The proxy destructor inserts the finish() token into the stream. the e->message isn't complete until the
        // proxy object is destroyed, hence the convoluted code... looking for a better way - jmoc

        Proton::EncoderProxy proxy( e.message );
        message->streamInto( proxy );
    }

    e.queueName = queueName;
    e.returnAddress = std::move(returnAddress);

    sendOutgoingMessage( e );
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(
        const std::string &queueName,
        const SerializedMessage &message,
        ReturnLabel returnAddress)
{
    Envelope e;

    e.queueName = queueName;
    e.returnAddress = std::move(returnAddress);
    e.message.body( proton::binary{ std::cbegin(message), std::cend(message) } );

    if( debugActivityInfo() )
    {
        CTILOG_DEBUG(dout,  "Enqueuing outbound message for queue \"" << queueName << "\"" << std::endl << message);
    }

    sendOutgoingMessage( e );
}


void ActiveMQConnectionManager::enqueueOutgoingReply(
    std::string dest,
    const SerializedMessage& message)
{
    if( debugActivityInfo() )
    {
        CTILOG_DEBUG(dout, "Enqueuing outbound reply" << std::endl << message);
    }

    auto replyProducer = Qpid::createDestinationProducer( _brokerSession, dest );

    if( debugActivityInfo() )
    {
        CTILOG_DEBUG(dout, "Sending outgoing reply to destination " << replyProducer->getDestination());
    }

    proton::message m;

    m.body( proton::binary{ std::cbegin(message), std::cend(message) } );

    replyProducer->send( m );
}


Qpid::QueueProducer& ActiveMQConnectionManager::getQueueProducer( proton::session & session, const std::string & queueName )
{
    if (const auto existingProducer = mapFindRef(_producers, queueName))
    {
        return **existingProducer;
    }
    
    //  if it doesn't exist, try to make one
    auto queueProducer = Qpid::createQueueProducer(session, queueName);

    CTILOG_INFO(dout, "ActiveMQ CMS producer established (" << queueName << ")");

    auto result =
        _producers.emplace(
           queueName,
           std::move(queueProducer) );

    return *(result.first->second);
}


void ActiveMQConnectionManager::registerHandler(const Qpid::Queues::InboundQueue &queue, MessageCallback::type callback)
{
    gActiveMQConnection->addNewCallback(queue, std::make_unique<SimpleMessageCallback>(callback));
}


void ActiveMQConnectionManager::registerReplyHandler(const Qpid::Queues::InboundQueue &queue, MessageCallbackWithReply callback)
{
    gActiveMQConnection->addNewCallback(queue, callback);
}


void ActiveMQConnectionManager::registerReplyHandler(const Qpid::Queues::InboundQueue& queue, MessageCallbackWithReplies callback)
{
    gActiveMQConnection->addNewCallback(queue, callback);
}


auto ActiveMQConnectionManager::registerSessionCallback(MessageCallback::type callback) -> SessionCallback
{
    //  implicitly construct a SessionCallback from the MessageCallback::type
    return callback;
}


void ActiveMQConnectionManager::addNewCallback(const Qpid::Queues::InboundQueue &queue, MessageCallback::Ptr callback)
{
    emplaceCallback( &queue, std::move(callback) );
}


void ActiveMQConnectionManager::addNewCallback(const Qpid::Queues::InboundQueue &queue, MessageCallbackWithReply callback)
{
    auto wrappedCallback = 
        std::make_unique<SimpleMessageCallback>(
            [=, &queue](const MessageDescriptor &md)
            {
                auto tempQueueProducer = Qpid::createDestinationProducer( _brokerSession, md.replyTo );

                auto result = callback(md);

                if( ! result )
                {
                    CTILOG_WARN(dout, "Callback-with-reply returned no reply for queue " << queue.name);

                    return;
                }

                proton::message m;

                m.body( proton::binary{ std::cbegin(*result), std::cend(*result) } );

                tempQueueProducer->send( m );
        });

    emplaceCallback( &queue, std::move(wrappedCallback) );
}


void ActiveMQConnectionManager::addNewCallback(const Qpid::Queues::InboundQueue& queue, MessageCallbackWithReplies callback)
{
    auto wrappedCallback =
        std::make_unique<SimpleMessageCallback>(
            [this, callback, &queue](const MessageDescriptor& md) {
                std::string replyTo { md.replyTo };
                callback(
                    md,
                    [this, replyTo](SerializedMessage message) {
                        enqueueOutgoingReply(replyTo, message);
                    });
            });

    emplaceCallback( &queue, std::move(wrappedCallback) );
}


std::string ActiveMQConnectionManager::getJMSType( proton::message & msg ) const
{
    std::string jms_type;

    if ( msg.message_annotations().exists( "x-opt-jms-type") )
    {
        jms_type = proton::get<std::string>( msg.message_annotations().get( "x-opt-jms-type") );
    }

    return jms_type;
}


void ActiveMQConnectionManager::expiry_periodic_task()
{
    // process expirations

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

    // re-schedule 

    _brokerSession
        .work_queue()
        .schedule( 5 * proton::duration::SECOND,
                   [ this ]()
                   {
                       this->expiry_periodic_task();
                   } );
}


template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnBroadcastReplyMessage>(const Qpid::Queues::OutboundQueue &queue, StreamableMessagePtr message, CallbackFor<Rfn::RfnBroadcastReplyMessage>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnSetChannelConfigReplyMessage>(const Qpid::Queues::OutboundQueue &queue, const ActiveMQConnectionManager::SerializedMessage & message, CallbackFor<Rfn::RfnSetChannelConfigReplyMessage>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnGetChannelConfigReplyMessage>(const Qpid::Queues::OutboundQueue &queue, const ActiveMQConnectionManager::SerializedMessage & message, CallbackFor<Rfn::RfnGetChannelConfigReplyMessage>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::DataStreamingUpdateReplyMessage>(const Qpid::Queues::OutboundQueue &queue, const ActiveMQConnectionManager::SerializedMessage & message, CallbackFor<Rfn::DataStreamingUpdateReplyMessage>::type callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<RfnDeviceCreationReplyMessage>(const Qpid::Queues::OutboundQueue &queue, const ActiveMQConnectionManager::SerializedMessage & message, CallbackFor<RfnDeviceCreationReplyMessage>::Ptr callback, std::chrono::seconds timeout, TimeoutCallback timedOut);

}

