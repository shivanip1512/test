#include "precompiled.h"

#include "amq_connection.h"

#include "StreamableMessage.h"

#include "utility.h"

#include "cms/connectionfactory.h"

#include "logger.h"

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
                dout << CtiTime() << " ActiveMQ CMS connection established\n";
            }
        }
        catch( cms::CMSException &ce )
        {
            releaseConnectionObjects();

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " ActiveMQ CMS connection established\n";
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
        boost::scoped_ptr<ActiveMQ::ManagedConnection> tempConnection(
                new ActiveMQ::ManagedConnection(_broker_uri));

        tempConnection->start();

        _connection.swap(tempConnection);

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " ActiveMQ CMS connection established\n";
        }
    }

    if( ! _producerSession.get() )
    {
        _producerSession.reset(_connection->createSession());
    }

    if( ! _consumerSession.get() )
    {
        _consumerSession.reset(_connection->createSession());

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

        if( e->callback )
        {
            std::auto_ptr<TempQueueConsumerWithCallback> tempConsumer(new TempQueueConsumerWithCallback);

            tempConsumer->managedConsumer.reset(
                    ActiveMQ::createTempQueueConsumer(
                            *_consumerSession));

            tempConsumer->listener.reset(
                    new ActiveMQ::MessageListener(
                            boost::bind(&ActiveMQConnectionManager::onTempQueueReply, this, _1)));

            tempConsumer->callback = *(e->callback);

            _temporaryConsumers.insert(
                    tempConsumer->managedConsumer->getDestination(),
                    tempConsumer);

            message->setCMSReplyTo(tempConsumer->managedConsumer->getDestination());
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
        //  we should only look up the callbacks once if we receive a bunch of inbound messages for the same queue
        if( ! queue || queue != queueMsgs_itr->first )
        {
            queue = queueMsgs_itr->first;

            queueCallbacks = _callbacks.equal_range(queue);
        }

        CallbacksPerQueue::const_iterator cb_itr = queueCallbacks.first;

        for each( const SerializedMessage &msg in queueMsgs_itr->second )
        {
            for( ; cb_itr != queueCallbacks.second; ++cb_itr )
            {
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
            dout << CtiTime() << " Message received for unknown consumer in " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")";

            continue;
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
        dout << CtiTime() << " ActiveMQ CMS producer established (" << queueName << ")\n";
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
    }
}


void ActiveMQConnectionManager::onTempQueueReply(const cms::Message *message)
{
    if( const cms::BytesMessage *bytesMessage = dynamic_cast<const cms::BytesMessage *>(message) )
    {
        SerializedMessage payload(bytesMessage->getBodyLength());

        bytesMessage->readBytes(payload);

        {
            CtiLockGuard<CtiCriticalSection> lock(_tempQueueRepliesMux);

            _tempQueueReplies[message->getCMSDestination()] = payload;
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
                ("com.eaton.eas.yukon.networkmanager.rfn.e2e.data.request");

InboundQueue::InboundQueue(std::string name_) : name(name_) {}

const IM_EX_MSG InboundQueue
        InboundQueue::NetworkManagerE2eDataIndication
                ("com.eaton.eas.yukon.networkmanager.rfn.e2e.data.indication");
}
}

}
}

