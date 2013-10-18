#include "precompiled.h"

#include "amq_connection.h"

#include "StreamableMessage.h"

#include "utility.h"

#include "cms/connectionfactory.h"

#include "logger.h"
#include "dllbase.h"  //  for getDebugLevel() and DEBUGLEVEL_ACTIVITY_INFO

#include "amq_util.h"
#include "std_helper.h"

#include <boost/optional.hpp>
#include <boost/assign/list_of.hpp>

using std::make_pair;
using std::auto_ptr;
using std::string;
using boost::scoped_ptr;

namespace Cti {
namespace Messaging {

extern IM_EX_MSG std::auto_ptr<ActiveMQConnectionManager> gActiveMQConnection;

ActiveMQConnectionManager::ActiveMQConnectionManager(const string &broker_uri) :
    _broker_uri(broker_uri)
{
}


ActiveMQConnectionManager::~ActiveMQConnectionManager()
{
    if( isRunning() )
    {
        interrupt(CtiThread::SHUTDOWN);
        join();
    }

    releaseConnectionObjects();
}


inline bool debugActivityInfo()
{
    return getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO;
}


void ActiveMQConnectionManager::releaseConnectionObjects()
{
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
            verifyConnectionObjects();

            updateCallbacks();

            sendOutgoingMessages();

            dispatchTempQueueReplies();

            dispatchIncomingMessages();
        }
        catch( ActiveMQ::ConnectionException &ce )
        {
            releaseConnectionObjects();

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " ActiveMQ CMS connection established" << std::endl;
            }
        }
        catch( cms::CMSException &ce )
        {
            releaseConnectionObjects();

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " ActiveMQ CMS connection established" << std::endl;
            }
        }

        sleep(1000);
    }
}


static const std::set<const ActiveMQ::Queues::InboundQueue *> ThriftInboundQueues = boost::assign::list_of
    (&ActiveMQ::Queues::InboundQueue::NetworkManagerE2eDataIndication);


void ActiveMQConnectionManager::verifyConnectionObjects()
{
    if( ! _connection.get() ||
        ! _connection->verifyConnection() )
    {
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Connection invalid, creating connection " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        boost::scoped_ptr<ActiveMQ::ManagedConnection> tempConnection(
                new ActiveMQ::ManagedConnection(_broker_uri));

        tempConnection->start();

        _connection.swap(tempConnection);

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " ActiveMQ CMS connection established" << std::endl;
        }
    }

    if( ! _producerSession.get() )
    {
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Producer session invalid, creating producer " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        _producerSession.reset(_connection->createSession());
    }

    if( ! _consumerSession.get() )
    {
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Consumer session invalid, creating consumer " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        _consumerSession.reset(_connection->createSession());

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Registering listeners for " << ThriftInboundQueues.size() << " queues " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        for each( const ActiveMQ::Queues::InboundQueue *inboundQueue in ThriftInboundQueues )
        {
            std::auto_ptr<QueueConsumerWithListener> consumer(new QueueConsumerWithListener);

            consumer->managedConsumer.reset(
                    ActiveMQ::createQueueConsumer(
                            *_consumerSession,
                            inboundQueue->name));

            consumer->listener.reset(
                    new ActiveMQ::MessageListener(
                            boost::bind(&ActiveMQConnectionManager::onInboundMessage, this, inboundQueue, _1)));

            consumer->managedConsumer->setMessageListener(
                    consumer->listener.get());

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Listener registered for destination \"" << inboundQueue->name << "\" id " << reinterpret_cast<unsigned long>(inboundQueue) << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            _consumers.push_back(consumer);
        }
    }
}


void ActiveMQConnectionManager::updateCallbacks()
{
    CtiLockGuard<CtiCriticalSection> lock(_newCallbackMux);

    _callbacks.insert(
            _newCallbacks.begin(),
            _newCallbacks.end());

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
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Sending outgoing message for queue \"" << e->queue->name << "\" id " << reinterpret_cast<unsigned long>(e->queue) << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        if( e->callback )
        {
            std::auto_ptr<TempQueueConsumerWithCallback> tempConsumer(new TempQueueConsumerWithCallback);

            tempConsumer->managedConsumer.reset(
                    ActiveMQ::createTempQueueConsumer(
                            *_consumerSession));

            message->setCMSReplyTo(tempConsumer->managedConsumer->getDestination());

            tempConsumer->callback = *(e->callback);

            tempConsumer->listener.reset(
                    new ActiveMQ::MessageListener(
                            boost::bind(&ActiveMQConnectionManager::onTempQueueReply, this, _1)));

            tempConsumer->managedConsumer->setMessageListener(
                    tempConsumer->listener.get());

            //  copy the key string for inserting, since tempConsumer will be invalidated
            //    when passed as a parameter to ptr_map::insert()
            const std::string destinationPhysicalName = tempConsumer->managedConsumer->getDestPhysicalName();

            if( debugActivityInfo() )
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Created temporary queue for callback reply for \"" << destinationPhysicalName << "\" " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            _temporaryConsumers.insert(
                    destinationPhysicalName,
                    tempConsumer);
        }

        ActiveMQ::QueueProducer &queueProducer = getQueueProducer(*_producerSession, e->queue->name);

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

    IncomingPerQueue::const_iterator queueMsgs_itr = incomingMessages.begin();

    const ActiveMQ::Queues::InboundQueue *queue = 0;
    std::pair<CallbacksPerQueue::const_iterator, CallbacksPerQueue::const_iterator> queueCallbacks;

    for( ; queueMsgs_itr != incomingMessages.end(); ++queueMsgs_itr )
    {
        if( debugActivityInfo() )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Received incoming message(s) for queue \"" << queueMsgs_itr->first->name << "\" id " << reinterpret_cast<unsigned long>(queueMsgs_itr->first) << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        //  we should only look up the callbacks once if we receive a bunch of inbound messages for the same queue
        if( ! queue || queue != queueMsgs_itr->first )
        {
            queue = queueMsgs_itr->first;

            queueCallbacks = _callbacks.equal_range(queue);
        }

        CallbacksPerQueue::const_iterator cb_itr = queueCallbacks.first;

        for each( const SerializedMessage &msg in queueMsgs_itr->second )
        {
            if( debugActivityInfo() )
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Dispatching message to callbacks from queue \"" << queueMsgs_itr->first->name << "\" id " << reinterpret_cast<unsigned long>(queueMsgs_itr->first) << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                dout << reinterpret_cast<unsigned long>(queueMsgs_itr->first) << ": ";

                dout << std::hex;

                copy(msg.begin(), msg.end(), padded_output_iterator<int, CtiLogger>(dout, '0', 2));

                dout << std::dec;

                dout << std::endl;
            }

            for( ; cb_itr != queueCallbacks.second; ++cb_itr )
            {
                if( debugActivityInfo() )
                {
                    CtiLockGuard<CtiLogger> dout_guard(dout);
                    dout << CtiTime() << " Calling callback " << reinterpret_cast<unsigned long>(&(cb_itr->second)) << " for queue \"" << queueMsgs_itr->first->name << "\" id " << reinterpret_cast<unsigned long>(queueMsgs_itr->first) << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
                }

                cb_itr->second(msg);
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

    for each( const ReplyPerDestination::value_type &reply in replies )
    {
        TemporaryConsumersByDestination::iterator itr =
                _temporaryConsumers.find(reply.first);

        if( itr == _temporaryConsumers.end() )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Message received with no consumer; destination [" << reply.first << "] in " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            continue;
        }

        if( debugActivityInfo() )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Calling temp queue callback " << reinterpret_cast<unsigned long>(&(itr->second->callback)) << " for temp queue \"" << reply.first << "\" " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            dout << reinterpret_cast<unsigned long>(&(itr->second->callback)) << ": ";

            dout << std::hex;

            copy(reply.second.begin(), reply.second.end(), padded_output_iterator<int, CtiLogger>(dout, '0', 2));

            dout << std::dec;

            dout << std::endl;
        }

        itr->second->callback(reply.second);

        _temporaryConsumers.erase(itr);
    }
}


void ActiveMQConnectionManager::enqueueMessage(const ActiveMQ::Queues::OutboundQueue &queue, auto_ptr<StreamableMessage> message)
{
    gActiveMQConnection->enqueueOutgoingMessage(queue, message);
}


void ActiveMQConnectionManager::enqueueMessages(const ActiveMQ::Queues::OutboundQueue &queue, const std::vector<SerializedMessage> &messages, MessageCallback callback)
{
    for each( const SerializedMessage &message in messages )
    {
        //  acquires and releases the mux for each message - we might do better by passing in all or chunks at a time...
        //    but the critical section should be cheap, so let's not optimize prematurely
        gActiveMQConnection->enqueueOutgoingMessage(queue, message, callback);
    }
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(const ActiveMQ::Queues::OutboundQueue &queue, auto_ptr<StreamableMessage> message)
{
    //  ensure the message is not null
    if( ! message.get() )
    {
        return;
    }

    struct StreamableEnvelope : Envelope
    {
        boost::scoped_ptr<StreamableMessage> message;

        cms::Message *extractMessage(cms::Session &session) const
        {
            std::auto_ptr<cms::StreamMessage> streamMessage(session.createStreamMessage());

            message->streamInto(*streamMessage);

            return streamMessage.release();
        }
    };

    std::auto_ptr<StreamableEnvelope> e(new StreamableEnvelope);

    e->queue = &queue;
    e->message.reset(message.release());

    {
        CtiLockGuard<CtiCriticalSection> lock(_outgoingMessagesMux);

        _outgoingMessages.push_back(e);
    }

    if( ! isRunning() )
    {
        start();
    }
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(const ActiveMQ::Queues::OutboundQueue &queue, const SerializedMessage &message, MessageCallback callback)
{
    struct BytesEnvelope : Envelope
    {
        SerializedMessage message;

        cms::Message *extractMessage(cms::Session &session) const
        {
            std::auto_ptr<cms::BytesMessage> bytesMessage(session.createBytesMessage());

            bytesMessage->writeBytes(message);

            return bytesMessage.release();
        }
    };

    std::auto_ptr<BytesEnvelope> e(new BytesEnvelope);

    e->queue  = &queue;
    e->message  = message;
    e->callback = callback;

    if( debugActivityInfo() )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " Enqueuing outbound message for queue \"" << queue.name << "\" id " << reinterpret_cast<unsigned long>(&queue) << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

        dout << reinterpret_cast<unsigned long>(&queue) << ": ";

        dout << std::hex;

        copy(message.begin(), message.end(), padded_output_iterator<int, CtiLogger>(dout, '0', 2));

        dout << std::dec;

        dout << std::endl;
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

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " ActiveMQ CMS producer established (" << queueName << ")" << std::endl;
    }

    queueProducer->setTimeToLive(DefaultTimeToLive);

    return *(_producers.insert(queueName, queueProducer).first->second);
}


void ActiveMQConnectionManager::registerHandler(const ActiveMQ::Queues::InboundQueue &queue, MessageCallback callback)
{
    gActiveMQConnection->addNewCallback(queue, callback);
}


void ActiveMQConnectionManager::addNewCallback(const ActiveMQ::Queues::InboundQueue &queue, MessageCallback callback)
{
    CtiLockGuard<CtiCriticalSection> lock(_newCallbackMux);

    _newCallbacks.insert(std::make_pair(&queue, callback));
}


void ActiveMQConnectionManager::onInboundMessage(const ActiveMQ::Queues::InboundQueue *queue, const cms::Message *message)
{
    if( const cms::BytesMessage *bytesMessage = dynamic_cast<const cms::BytesMessage *>(message) )
    {
        SerializedMessage payload(bytesMessage->getBodyLength());

        bytesMessage->readBytes(payload);

        {
            CtiLockGuard<CtiCriticalSection> lock(_newIncomingMessagesMux);

            _newIncomingMessages[queue].push_back(payload);
        }

        if( debugActivityInfo() )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Received inbound message for queue \"" << queue->name << "\" id " << reinterpret_cast<unsigned long>(queue) << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            dout << reinterpret_cast<unsigned long>(queue) << ": ";

            dout << std::hex;

            copy(payload.begin(), payload.end(), padded_output_iterator<int, CtiLogger>(dout, '0', 2));

            dout << std::dec;

            dout << std::endl;
        }
    }
}


void ActiveMQConnectionManager::onTempQueueReply(const cms::Message *message)
{
    if( const cms::BytesMessage *bytesMessage = dynamic_cast<const cms::BytesMessage *>(message) )
    {
        SerializedMessage payload(bytesMessage->getBodyLength());

        bytesMessage->readBytes(payload);

        if( const cms::Destination *dest = message->getCMSDestination() )
        {
            {
                CtiLockGuard<CtiCriticalSection> lock(_tempQueueRepliesMux);

                _tempQueueReplies[ActiveMQ::destPhysicalName(*dest)] = payload;
            }

            if( debugActivityInfo() )
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Received temp queue reply for \"" << ActiveMQ::destPhysicalName(*dest) << "\" " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                dout << ActiveMQ::destPhysicalName(*dest) << ": ";

                dout << std::hex;

                copy(payload.begin(), payload.end(), padded_output_iterator<int, CtiLogger>(dout, '0', 2));

                dout << std::dec;

                dout << std::endl;
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
        OutboundQueue::NetworkManagerE2eDataRequest
                ("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataRequest");

InboundQueue::InboundQueue(std::string name_) : name(name_) {}

const IM_EX_MSG InboundQueue
        InboundQueue::NetworkManagerE2eDataIndication
                ("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataIndication");
}
}

}
}

