#include "yukon.h"

#include "amq_connection.h"

#include "StreamableMessage.h"

#include "utility.h"

#include "cms/connectionfactory.h"

using std::make_pair;
using std::auto_ptr;
using std::string;
using boost::scoped_ptr;

namespace Cti {
namespace Messaging {

struct amq_envelope
{
    string queue;

    virtual ~amq_envelope() {}

    virtual auto_ptr<cms::Message> extractMessage(cms::Session &session) const = 0;
};

struct string_envelope : amq_envelope
{
    string message;

    auto_ptr<cms::Message> extractMessage(cms::Session &session) const
    {
        return auto_ptr<cms::Message>(session.createTextMessage(message));
    }
};

struct streamable_envelope : amq_envelope
{
    scoped_ptr<StreamableMessage> message;

    auto_ptr<cms::Message> extractMessage(cms::Session &session) const
    {
        if( ! message.get() )
        {
            return auto_ptr<cms::Message>();
        }

        auto_ptr<cms::StreamMessage> streamMessage(session.createStreamMessage());

        message->streamInto(*streamMessage);

        return streamMessage;
    }
};


ActiveMQConnectionManager::ActiveMQConnectionManager(const string &broker_uri) :
    _broker_uri(broker_uri),
    _initialized(false)
{
    _queue_names.insert(make_pair(Queue_PorterResponses, "yukon.notif.stream.amr.PorterResponseMessage"));
}


ActiveMQConnectionManager::~ActiveMQConnectionManager()
{
    if( isRunning() )
    {
        interrupt(CtiThread::SHUTDOWN);
        join();
    }

    delete_assoc_container(_producers);
}


std::string ActiveMQConnectionManager::getQueueName(Queues queue) const
{
    queue_name_map::const_iterator itr = _queue_names.find(queue);

    if( itr != _queue_names.end() )
    {
        return itr->second;
    }

    return "";
}


void ActiveMQConnectionManager::run()
{
    while( !isSet(SHUTDOWN) )
    {
        sendPendingMessages();

        sleep(2000);
    }
}


void ActiveMQConnectionManager::validateSetup()
{
    if( ! _initialized )
    {
        //  can throw std::runtime_exception
        activemq::library::ActiveMQCPP::initializeLibrary();

        _initialized = true;
    }

    if( ! _connection.get() )
    {
        scoped_ptr<cms::ConnectionFactory> connectionFactory;

        connectionFactory.reset(cms::ConnectionFactory::createCMSConnectionFactory(_broker_uri));

        scoped_ptr<cms::Connection> connection;

        connection.reset(connectionFactory->createConnection());

        connection->start();

        _connection.swap(connection);
    }

    if( ! _session.get() )
    {
        _session.reset(_connection->createSession());
    }
}


void ActiveMQConnectionManager::sendPendingMessages()
try
{
    validateSetup();

    {
        //  lock as little as possible - just empty out the pending queue and let go
        CtiLockGuard<CtiCriticalSection> lock(_incoming_message_mux);

        while( !_incoming_messages.empty() )
        {
            //  guarantee that we cannot have null pointers in _pending_messages
            if( _incoming_messages.front() )
            {
                _pending_messages.push(_incoming_messages.front());
            }

            _incoming_messages.pop();
        }
    }

    while( !_pending_messages.empty() )
    {
        scoped_ptr<amq_envelope> e(_pending_messages.front());

        _pending_messages.pop();

        //  _session must be valid if we passed validateSetup()
        sendMessage(*_session, *e);
    }
}
catch( cms::CMSException &e )
{
    //  possibly add an increasing delay if we keep throwing exceptions?
    return;
}
catch( std::runtime_error &e )
{
    return;
}


void ActiveMQConnectionManager::enqueueEnvelope(auto_ptr<amq_envelope> e)
{
    {
        CtiLockGuard<CtiCriticalSection> lock(_incoming_message_mux);

        _incoming_messages.push(e.release());
    }

    if( !isRunning() )
    {
        //  starts its thread on first outbound message
        start();
    }
    else
    {
        interrupt();
    }
}


void ActiveMQConnectionManager::enqueueMessage(const std::string &queueName, const std::string &message)
{
    string_envelope *e = new string_envelope;

    e->queue   = queueName;
    e->message = message;

    enqueueEnvelope(auto_ptr<amq_envelope>(e));
}


void ActiveMQConnectionManager::enqueueMessage(const Queues queueId, auto_ptr<StreamableMessage> message)
{
    std::string queueName = getQueueName(queueId);

    if( ! queueName.empty() )
    {
        streamable_envelope *se = new streamable_envelope;

        se->queue   = queueName;
        se->message.reset(message.release());

        enqueueEnvelope(auto_ptr<amq_envelope>(se));
    }
}


void ActiveMQConnectionManager::sendMessage(cms::Session &session, const amq_envelope &e)
{
    if( cms::MessageProducer *producer = getProducer(session, e.queue) )
    {
        auto_ptr<cms::Message> m(e.extractMessage(session));

        if( m.get() )
        {
            producer->send(m.get());
        }
    }
}


cms::MessageProducer *ActiveMQConnectionManager::getProducer(cms::Session &session, const std::string &queueName)
{
    producer_map::iterator itr = _producers.find(queueName);

    if( itr == _producers.end() )
    {
        try
        {
            //  if it doesn't exist, try to make one
            if( const cms::Queue *queue = session.createQueue(queueName) )
            {
                if( cms::MessageProducer *producer = session.createProducer(queue) )
                {
                    itr = _producers.insert(make_pair(queueName, producer)).first;
                }
            }
        }
        catch( cms::CMSException &e )
        {
            return 0;
        }
    }

    if( itr == _producers.end() )
    {
        return 0;
    }

    return itr->second;
}


/*
void  ActiveMQConnectionManager::receiveMessage()
{
    scoped_ptr<cms::Connection>      consumer_connection (connectionFactory->createConnection());
    consumer_connection->start();
    scoped_ptr<cms::Session>         consumer_session    (consumer_connection->createSession());
    scoped_ptr<cms::Topic>           consumer_topic      (consumer_session->createTopic("EXAMPLE-TOPIC"));
    scoped_ptr<cms::MessageConsumer> myConsumer          (consumer_session->createConsumer(consumer_topic.get()));


    scoped_ptr<cms::Message>     consumed_message(myConsumer->receive());
    scoped_ptr<cms::TextMessage> consumed_text_message(dynamic_cast<cms::TextMessage *>(consumed_message.get()));

    //  did the dynamic cast work?
    if( consumed_text_message.get() )
    {
        //  release the base pointer version, we've got an upgrade
        consumed_message.release();

        cout << consumed_text_message->getText();
    }
}
*/
}
}
