#include "precompiled.h"

#include "amq_connection.h"

#include "StreamableMessage.h"
#include "RfnBroadcastReplyMessage.h"

#include "utility.h"

#include "cms/connectionfactory.h"

#include "logger.h"
#include "dllbase.h"  //  for getDebugLevel() and DEBUGLEVEL_ACTIVITY_INFO

#include "amq_util.h"

#include "message_factory.h"

#include "std_helper.h"

#include <boost/optional.hpp>
#include <boost/range/algorithm/for_each.hpp>
#include <boost/range/adaptor/adjacent_filtered.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/adaptor/map.hpp>

using std::make_pair;
using std::auto_ptr;
using std::string;
using boost::scoped_ptr;

using Cti::Logging::Vector::Hex::operator<<;

namespace Cti {
namespace Messaging {

extern IM_EX_MSG std::auto_ptr<ActiveMQConnectionManager> gActiveMQConnection;

ActiveMQConnectionManager::ActiveMQConnectionManager(const string &broker_uri) :
    _broker_uri(broker_uri)
{
}

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
    _consumers.clear();

    _producerSession.reset();
    _consumerSession.reset();

    _connection.reset();
}


void ActiveMQConnectionManager::run()
{
    while( ! isSet(SHUTDOWN) )
    {
        try
        {
            if( verifyConnectionObjects() )
            {
                updateCallbacks();

                sendOutgoingMessages();

                dispatchTempQueueReplies();

                dispatchIncomingMessages();
            }
        }
        catch( ActiveMQ::ConnectionException &e )
        {
            releaseConnectionObjects();

            CTILOG_EXCEPTION_ERROR(dout, e, "Unable to connect to the broker");
        }
        catch( cms::CMSException &e )
        {
            releaseConnectionObjects();

            CTILOG_EXCEPTION_ERROR(dout, e);
        }

        sleep(1000);
    }
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

            _connection.reset( new ActiveMQ::ManagedConnection( _broker_uri ));
        }

        _connection->start(); // start the connection outside the lock

        CTILOG_INFO(dout, "ActiveMQ CMS connection established");
    }

    if( ! _producerSession.get() )
    {
        CTILOG_INFO(dout, "Producer session invalid, creating producer");

        _producerSession.reset(_connection->createSession());
    }

    if( ! _consumerSession.get() )
    {
        CTILOG_INFO(dout,  "Consumer session invalid, creating consumer");

        _consumerSession.reset(_connection->createSession());

        registerConsumersForCallbacks(_callbacks);
    }

    return true;
}


void ActiveMQConnectionManager::registerConsumersForCallbacks(const CallbacksPerQueue &callbacks)
{
    using ActiveMQ::Queues::InboundQueue;

    boost::for_each(
            callbacks
                | boost::adaptors::map_keys
                | boost::adaptors::adjacent_filtered(std::not_equal_to<const InboundQueue *>{})
                | boost::adaptors::filtered([this](const InboundQueue *q) { return ! _consumers.count(q); }),
            [this](const InboundQueue *q)
            {
                registerConsumer(q);
            });
}


void ActiveMQConnectionManager::registerConsumer(const ActiveMQ::Queues::InboundQueue *inboundQueue)
{
    std::auto_ptr<QueueConsumerWithListener> consumer(new QueueConsumerWithListener);

    consumer->managedConsumer.reset(
            ActiveMQ::createQueueConsumer(
                    *_consumerSession,
                    inboundQueue->name));

    consumer->listener.reset(
            new ActiveMQ::MessageListener(
                    [=](const cms::Message *msg)
                    {
                        onInboundMessage(inboundQueue, msg);
                    }));

    consumer->managedConsumer->setMessageListener(
            consumer->listener.get());

    CTILOG_INFO(dout, "Listener registered for destination \"" << inboundQueue->name << "\" id " << reinterpret_cast<unsigned long>(inboundQueue));

    _consumers.insert(inboundQueue, consumer);
}


void ActiveMQConnectionManager::updateCallbacks()
{
    CtiLockGuard<CtiCriticalSection> lock(_newCallbackMux);

    _callbacks.insert(
            _newCallbacks.begin(),
            _newCallbacks.end());

    registerConsumersForCallbacks(_newCallbacks);

    _newCallbacks.clear();
}


void ActiveMQConnectionManager::sendOutgoingMessages()
{
    EnvelopeQueue messages;

    {
        CtiLockGuard<CtiCriticalSection> lock(_outgoingMessagesMux);

        messages.transfer(messages.end(), _outgoingMessages);
    }

    while( ! messages.empty() )
    {
        EnvelopeQueue::auto_type e = messages.pop_front();

        std::auto_ptr<cms::Message> message(
                e->extractMessage(*_producerSession));

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Sending outgoing message for queue \"" << e->queueName << "\"");
        }

        if( e->replyListener )
        {
            std::auto_ptr<TempQueueConsumerWithCallback> tempConsumer(new TempQueueConsumerWithCallback);

            tempConsumer->managedConsumer.reset(
                    ActiveMQ::createTempQueueConsumer(
                            *_consumerSession));

            message->setCMSReplyTo(tempConsumer->managedConsumer->getDestination());

            tempConsumer->callback = e->replyListener->callback;

            tempConsumer->listener.reset(
                    new ActiveMQ::MessageListener(
                            [=](const cms::Message *msg)
                            {
                                onTempQueueReply(msg);
                            }));

            tempConsumer->managedConsumer->setMessageListener(
                    tempConsumer->listener.get());

            //  copy the key string for inserting, since tempConsumer will be invalidated
            //    when passed as a parameter to ptr_map::insert()
            const std::string destinationPhysicalName = tempConsumer->managedConsumer->getDestPhysicalName();

            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Created temporary queue for callback reply for \"" << destinationPhysicalName << "\"");
            }

            _temporaryConsumers.insert(
                    destinationPhysicalName,
                    tempConsumer);

            _temporaryExpirations.insert(
                    std::make_pair(
                            e->replyListener->expiration,
                            ExpirationHandler{
                                destinationPhysicalName,
                                e->replyListener->timedOut }));
        }

        ActiveMQ::QueueProducer &queueProducer = getQueueProducer(*_producerSession, e->queueName);

        queueProducer.send(message.get());
    }
}


void ActiveMQConnectionManager::dispatchIncomingMessages()
{
    IncomingPerQueue incomingMessages;

    {
        CtiLockGuard<CtiCriticalSection> lock(_newIncomingMessagesMux);

        incomingMessages.insert(
                _newIncomingMessages.begin(),
                _newIncomingMessages.end());

        _newIncomingMessages.clear();
    }

    for( const auto &inbound : incomingMessages )
    {
        const auto &queue       = inbound.first;
        const auto &descriptors = inbound.second;

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Received incoming message(s) for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue));
        }

        const auto callbackRange = _callbacks.equal_range(queue);
        const auto callbacks     = boost::make_iterator_range(callbackRange.first, callbackRange.second) | boost::adaptors::map_values;

        for( const auto &md : descriptors )
        {
            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Dispatching message to callbacks from queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue) << std::endl
                        << reinterpret_cast<unsigned long>(queue) << ": " << md.msg);
            }

            for( auto callback : callbacks )
            {
                if( debugActivityInfo() )
                {
                    CTILOG_DEBUG(dout, "Calling callback " << reinterpret_cast<unsigned long>(&callback) << " for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue));
                }

                callback(md);
            }
        }
    }
}


void ActiveMQConnectionManager::dispatchTempQueueReplies()
{
    ReplyPerDestination replies;

    {
        CtiLockGuard<CtiCriticalSection> lock(_tempQueueRepliesMux);

        replies = _tempQueueReplies;

        _tempQueueReplies.clear();
    }

    for( const auto &kv : replies )
    {
        const auto &destination = kv.first;
        const auto &descriptor  = kv.second;

        //  need the iterator so we can delete later
        auto itr = _temporaryConsumers.find(destination);

        if( itr == _temporaryConsumers.end() )
        {
            CTILOG_ERROR(dout, "Message received with no consumer; destination [" << destination << "]");
            continue;
        }

        const auto &consumer = itr->second;

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Calling temp queue callback " << reinterpret_cast<unsigned long>(&(consumer->callback)) << " for temp queue \"" << destination << "\"" << std::endl
                    << reinterpret_cast<unsigned long>(&(consumer->callback)) << ": " << descriptor.msg);
        }

        consumer->callback(descriptor);

        _temporaryConsumers.erase(itr);
    }

    const auto end = _temporaryExpirations.upper_bound(CtiTime::now());

    if( _temporaryExpirations.begin() != end )
    {
        for( auto itr = _temporaryExpirations.begin(); itr != end; ++itr )
        {
            const auto &expirationHandler = itr->second;

            const auto consumer_itr = _temporaryConsumers.find(expirationHandler.queueName);

            //  if the queue consumer still exists, we never got a response
            if( consumer_itr != _temporaryConsumers.end() )
            {
                _temporaryConsumers.erase(consumer_itr);

                expirationHandler.callback();
            }
        }

        _temporaryExpirations.erase(_temporaryExpirations.begin(), end);
    }
}


void ActiveMQConnectionManager::enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type message)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, boost::none);
}

void ActiveMQConnectionManager::enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, boost::none);
}

template<typename Msg>
struct DeserializationHelper
{
    typedef typename ActiveMQConnectionManager::CallbackFor<Msg>::type CallbackForMsg;
    const CallbackForMsg callback;

    DeserializationHelper(const CallbackForMsg &callback_) : callback(callback_) {}

    void operator()(const ActiveMQConnectionManager::MessageDescriptor &md) const
    {
        if( const boost::optional<Msg> msg = Serialization::MessageSerializer<Msg>::deserialize(md.msg) )
        {
            callback(*msg);
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
        StreamableMessage::auto_type message,
        typename CallbackFor<Msg>::type callback,
        CtiTime timeout,
        TimeoutCallback timedOut)
{
    MessageCallback callbackWrapper = DeserializationHelper<Msg>(callback);

    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, TemporaryListener{ callbackWrapper, timeout, timedOut });
}


void ActiveMQConnectionManager::enqueueMessageWithCallback(
        const ActiveMQ::Queues::OutboundQueue &queue,
        const SerializedMessage &message,
        MessageCallback callback,
        CtiTime timeout,
        TimeoutCallback timedOut)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue.name, message, TemporaryListener{ callback, timeout, timedOut });
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(
        const std::string &queueName,
        StreamableMessage::auto_type message,
        boost::optional<TemporaryListener> replyListener)
{
    //  ensure the message is not null
    if( ! message.get() )
    {
        return;
    }

    struct StreamableEnvelope : Envelope
    {
        boost::scoped_ptr<const StreamableMessage> message;

        cms::Message *extractMessage(cms::Session &session) const
        {
            std::auto_ptr<cms::StreamMessage> streamMessage(session.createStreamMessage());

            message->streamInto(*streamMessage);

            return streamMessage.release();
        }
    };

    std::auto_ptr<StreamableEnvelope> e(new StreamableEnvelope);

    e->queueName = queueName;
    e->message.reset(message.release());
    e->replyListener = replyListener;

    {
        CtiLockGuard<CtiCriticalSection> lock(_outgoingMessagesMux);

        _outgoingMessages.push_back(e);
    }

    if( ! isRunning() )
    {
        start();
    }
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(
        const std::string &queueName,
        const SerializedMessage &message,
        boost::optional<TemporaryListener> replyListener)
{
    struct BytesEnvelope : Envelope
    {
        SerializedMessage message;

        cms::Message *extractMessage(cms::Session &session) const
        {
            std::unique_ptr<cms::BytesMessage> bytesMessage{session.createBytesMessage()};

            bytesMessage->writeBytes(message);

            return bytesMessage.release();
        }
    };

    std::auto_ptr<BytesEnvelope> e(new BytesEnvelope);

    e->queueName = queueName;
    e->message   = message;
    e->replyListener = replyListener;

    if( debugActivityInfo() )
    {
        CTILOG_DEBUG(dout,  "Enqueuing outbound message for queue \"" << queueName << "\"" << std::endl << message);
    }

    {
        CtiLockGuard<CtiCriticalSection> lock(_outgoingMessagesMux);

        _outgoingMessages.push_back(e);
    }

    if( ! isRunning() )
    {
        start();
    }
}


ActiveMQ::QueueProducer &ActiveMQConnectionManager::getQueueProducer(cms::Session &session, const std::string &queueName)
{
    ProducersByQueueName::iterator existingProducer = _producers.find(queueName);

    if( existingProducer != _producers.end() )
    {
        return *(existingProducer->second);
    }

    //  if it doesn't exist, try to make one
    std::auto_ptr<ActiveMQ::QueueProducer> queueProducer(
            ActiveMQ::createQueueProducer(session, queueName));

    CTILOG_INFO(dout, "ActiveMQ CMS producer established (" << queueName << ")");

    queueProducer->setTimeToLiveMillis(DefaultTimeToLiveMillis);

    return *(_producers.insert(queueName, queueProducer).first->second);
}


void ActiveMQConnectionManager::registerHandler(const ActiveMQ::Queues::InboundQueue &queue, MessageCallback callback)
{
    gActiveMQConnection->addNewCallback(queue, callback);
}


void ActiveMQConnectionManager::addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, MessageCallback callback)
{
    {
        CtiLockGuard<CtiCriticalSection> lock(_newCallbackMux);

        _newCallbacks.emplace(&queue, callback);
    }

    if( ! isRunning() )
    {
        start();
    }
}


void ActiveMQConnectionManager::onInboundMessage(const ActiveMQ::Queues::InboundQueue *queue, const cms::Message *message)
{
    if( const cms::BytesMessage *bytesMessage = dynamic_cast<const cms::BytesMessage *>(message) )
    {
        MessageDescriptor md;

        md.type = bytesMessage->getCMSType();

        md.msg.resize(bytesMessage->getBodyLength());

        bytesMessage->readBytes(md.msg);

        {
            CtiLockGuard<CtiCriticalSection> lock(_newIncomingMessagesMux);

            _newIncomingMessages[queue].push_back(md);
        }

        if( debugActivityInfo() )
        {
            CTILOG_DEBUG(dout, "Received inbound message for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue) << std::endl
                    << reinterpret_cast<unsigned long>(queue) << ": " << md.msg);
        }
    }
}


void ActiveMQConnectionManager::onTempQueueReply(const cms::Message *message)
{
    if( const cms::BytesMessage *bytesMessage = dynamic_cast<const cms::BytesMessage *>(message) )
    {
        if( const cms::Destination *dest = message->getCMSDestination() )
        {
            MessageDescriptor md;

            md.type = bytesMessage->getCMSType();

            md.msg.resize(bytesMessage->getBodyLength());

            bytesMessage->readBytes(md.msg);

            {
                CtiLockGuard<CtiCriticalSection> lock(_tempQueueRepliesMux);

                _tempQueueReplies[ActiveMQ::destPhysicalName(*dest)] = md;
            }

            if( debugActivityInfo() )
            {
                CTILOG_DEBUG(dout, "Received temp queue reply for \"" << ActiveMQ::destPhysicalName(*dest) << "\"" << std::endl
                        << ActiveMQ::destPhysicalName(*dest) << ": " << md.msg);
            }
        }
    }
}


namespace ActiveMQ {
namespace Queues {

OutboundQueue::OutboundQueue(std::string name_) : name(name_) {}

const IM_EX_MSG OutboundQueue
        OutboundQueue::PorterResponses
                ("yukon.notif.stream.amr.PorterResponseMessage");
const IM_EX_MSG OutboundQueue
        OutboundQueue::SmartEnergyProfileControl
                ("yukon.notif.stream.dr.SmartEnergyProfileControlMessage");
const IM_EX_MSG OutboundQueue
        OutboundQueue::SmartEnergyProfileRestore
                ("yukon.notif.stream.dr.SmartEnergyProfileRestoreMessage");
const IM_EX_MSG OutboundQueue
        OutboundQueue::EcobeeCyclingControl
                ("yukon.notif.stream.dr.EcobeeCyclingControlMessage");
const IM_EX_MSG OutboundQueue
        OutboundQueue::EcobeeRestore
                ("yukon.notif.stream.dr.EcobeeRestoreMessage");
const IM_EX_MSG OutboundQueue
        OutboundQueue::HistoryRowAssociationResponse
                ("yukon.notif.stream.dr.HistoryRowAssociationResponse");
const IM_EX_MSG OutboundQueue
        OutboundQueue::IvvcAnalysisMessage
                ("yukon.notif.stream.cc.IvvcAnalysisMessage");
const IM_EX_MSG OutboundQueue
        OutboundQueue::CapControlOperationMessage
                ("yukon.notif.stream.cc.CapControlOperationMessage");
const IM_EX_MSG OutboundQueue
        OutboundQueue::RfnBroadcast
                ("yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest");
const IM_EX_MSG OutboundQueue
        OutboundQueue::NetworkManagerRequest
                ("com.eaton.eas.yukon.networkmanager.request");
const IM_EX_MSG OutboundQueue
        OutboundQueue::NetworkManagerE2eDataRequest
                ("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataRequest");
const IM_EX_MSG OutboundQueue
        OutboundQueue::ScannerOutMessages
                ("com.eaton.eas.yukon.scanner.outmessages");
const IM_EX_MSG OutboundQueue
        OutboundQueue::ScannerInMessages
                ("com.eaton.eas.yukon.scanner.inmessages");

InboundQueue::InboundQueue(std::string name_) : name(name_) {}

const IM_EX_MSG InboundQueue
        InboundQueue::NetworkManagerResponse
                ("com.eaton.eas.yukon.networkmanager.response");
const IM_EX_MSG InboundQueue
        InboundQueue::NetworkManagerE2eDataConfirm
                ("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataConfirm");
const IM_EX_MSG InboundQueue
        InboundQueue::NetworkManagerE2eDataIndication
                ("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataIndication");
const IM_EX_MSG InboundQueue
        InboundQueue::ScannerOutMessages
                ("com.eaton.eas.yukon.scanner.outmessages");
const IM_EX_MSG InboundQueue
        InboundQueue::ScannerInMessages
                ("com.eaton.eas.yukon.scanner.inmessages");
}
}


template void IM_EX_MSG ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnBroadcastReplyMessage>(const ActiveMQ::Queues::OutboundQueue &queue, StreamableMessage::auto_type message, CallbackFor<Rfn::RfnBroadcastReplyMessage>::type callback, CtiTime expiration, TimeoutCallback timedOut);


}
}

