#include "yukon.h"

#include "amq_connection.h"

#include "StreamableMessage.h"

#include "utility.h"

#include "cms/connectionfactory.h"

#include "logger.h"

using std::make_pair;
using std::auto_ptr;
using std::string;
using boost::scoped_ptr;

namespace Cti {
namespace Messaging {

struct amq_envelope
{
    string queue;

    CtiTime expiration;

    virtual ~amq_envelope() {}

    auto_ptr<cms::Message> extractMessage(cms::Session &session) const
    {
        auto_ptr<cms::Message> message = createMessage(session);

        if( message.get() )
        {
            message->setCMSExpiration(expiration.seconds());
            message->setCMSDeliveryMode(cms::DeliveryMode::NON_PERSISTENT);
        }

        return message;
    }

    virtual auto_ptr<cms::Message> createMessage(cms::Session &session) const = 0;
};

struct string_envelope : amq_envelope
{
    string message;

    auto_ptr<cms::Message> createMessage(cms::Session &session) const
    {
        return auto_ptr<cms::Message>(session.createTextMessage(message));
    }
};

struct streamable_envelope : amq_envelope
{
    scoped_ptr<StreamableMessage> message;

    auto_ptr<cms::Message> createMessage(cms::Session &session) const
    {
        auto_ptr<cms::StreamMessage> streamMessage;

        if( message.get() )
        {
            streamMessage.reset(session.createStreamMessage());

            message->streamInto(*streamMessage);
        }

        return streamMessage;
    }
};


ActiveMQConnectionManager::ActiveMQConnectionManager(const string &broker_uri) :
    _broker_uri(broker_uri),
    _initialized(false),
    _delay(2)
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

    _connection.reset();
    _session.reset();

    if( _initialized )
    {
        activemq::library::ActiveMQCPP::shutdownLibrary();
    }
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

        sleep(_delay * 1000);
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

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);

            dout << CtiTime() << " ActiveMQ CMS connection established\n";
        }
    }

    if( ! _session.get() )
    {
        _session.reset(_connection->createSession());

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);

            dout << CtiTime() << " ActiveMQ CMS session established\n";
        }
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

    //  If successful, reset delay
    _delay = 2;
}
catch( cms::CMSException &e )
{
    if( _delay < 5 )
    {
        _delay = 5;
    }
    else if( _delay < 30 )
    {
        _delay += 5;
    }

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);

        dout << CtiTime() << " ActiveMQ CMS error - \"" << trim(string(e.what())) << "\" - delaying " << _delay << " seconds\n";
    }

    //  If there's been an error, purge any pending messages
    while( ! _pending_messages.empty() )
    {
        delete _pending_messages.front();

        _pending_messages.pop();
    }

    //  Clear all of the connection, session, and producer info
    _connection.reset();

    _session.reset();

    delete_assoc_container(_producers);
    _producers.clear();

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
}


void ActiveMQConnectionManager::enqueueMessage(const std::string &queueName, const std::string &message)
{
    string_envelope *e = new string_envelope;

    e->queue   = queueName;
    e->message = message;
    e->expiration = CtiTime() + DefaultExpiration;

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
        se->expiration = CtiTime() + DefaultExpiration;

        enqueueEnvelope(auto_ptr<amq_envelope>(se));
    }
}


void ActiveMQConnectionManager::sendMessage(cms::Session &session, const amq_envelope &e)
{
    if( cms::MessageProducer * const producer = getProducer(session, e.queue) )
    {
        auto_ptr<cms::Message> m(e.extractMessage(session));

        if( m.get() )
        {
            producer->send(m.get());
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);

        dout << CtiTime() << " ActiveMQ CMS producer error - could not get producer for queue (" << e.queue << ")\n";
    }
}


cms::MessageProducer *ActiveMQConnectionManager::getProducer(cms::Session &session, const std::string &queueName)
{
    producer_map::iterator itr = _producers.find(queueName);

    if( itr != _producers.end() )
    {
        return itr->second;

    }

    //  if it doesn't exist, try to make one
    if( const cms::Queue *queue = session.createQueue(queueName) )
    {
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);

            dout << CtiTime() << " ActiveMQ CMS queue established (" << queueName << ")\n";
        }

        if( cms::MessageProducer *producer = session.createProducer(queue) )
        {
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);

                dout << CtiTime() << " ActiveMQ CMS producer established (" << queueName << ")\n";
            }

            _producers.insert(make_pair(queueName, producer));

            return producer;
        }
    }

    return 0;
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
