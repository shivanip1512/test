#include "precompiled.h"

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

ActiveMQConnectionManager::ActiveMQConnectionManager(const string &broker_uri) :
    _broker_uri(broker_uri),
    _initialized(false),
    _delay(2)
{
    _queue_names.insert(make_pair(Queue_PorterResponses, "yukon.notif.stream.amr.PorterResponseMessage"));
    _queue_names.insert(make_pair(Queue_SmartEnergyProfileControl, "yukon.notif.stream.dr.SmartEnergyProfileControlMessage"));
    _queue_names.insert(make_pair(Queue_SmartEnergyProfileRestore, "yukon.notif.stream.dr.SmartEnergyProfileRestoreMessage"));
    _queue_names.insert(make_pair(Queue_HistoryRowAssociationResponse, "yukon.notif.stream.dr.HistoryRowAssociationResponse"));
    _queue_names.insert(make_pair(Queue_IvvcAnalysisMessage, "yukon.notif.stream.cc.IvvcAnalysisMessage"));
    _queue_names.insert(make_pair(Queue_RfnBroadcast, "yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest"));
}


ActiveMQConnectionManager::~ActiveMQConnectionManager()
{
    if( isRunning() )
    {
        interrupt(CtiThread::SHUTDOWN);
        join();
    }

    _producers.clear();

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
        getIncomingMessages();

        sendPendingMessages();

        sleep(_delay * 1000);
    }
}


void Cti::Messaging::ActiveMQConnectionManager::getIncomingMessages()
{
    CtiLockGuard<CtiCriticalSection> lock(_incoming_message_mux);

    while( !_incoming_messages.empty() )
    {
        //  guarantee that we will not have null pointers in _pending_messages
        if( _incoming_messages.front() )
        {
            _pending_messages.push(_incoming_messages.front());
        }

        _incoming_messages.pop();
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


void ActiveMQConnectionManager::purgePendingMessages()
{
    while( ! _pending_messages.empty() )
    {
        delete _pending_messages.front();

        _pending_messages.pop();
    }
}


void ActiveMQConnectionManager::sendPendingMessages()
try
{
    try
    {
        validateSetup();
    }
    catch( cms::CMSException &e )
    {
        //  If there's been an error establishing the connection, purge any pending messages
        purgePendingMessages();

        throw;
    }

    while( !_pending_messages.empty() )
    {
        envelope *e = _pending_messages.front();

        //  _session must be valid if we passed validateSetup()
        sendMessage(*_session, *e);

        delete e;

        _pending_messages.pop();
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

    //  Clear all of the connection, session, and producer info
    _connection.reset();

    _session.reset();

    _producers.clear();
}
catch( std::runtime_error &e )
{
}


void ActiveMQConnectionManager::enqueueMessage(const Queues queueId, auto_ptr<StreamableMessage> message)
{
    std::string queueName = getQueueName(queueId);

    //  ensure the message is not null
    if( ! queueName.empty() && message.get() )
    {
        envelope *e = new envelope;

        e->queue = queueName;
        e->message.reset(message.release());

        {
            CtiLockGuard<CtiCriticalSection> lock(_incoming_message_mux);

            _incoming_messages.push(e);
        }

        if( !isRunning() )
        {
            //  starts its thread on first outbound message
            start();
        }
    }
}


void ActiveMQConnectionManager::sendMessage(cms::Session &session, const envelope &e)
{
    if( cms::MessageProducer * const producer = getProducer(session, e.queue) )
    {
        std::auto_ptr<cms::StreamMessage> streamMessage(session.createStreamMessage());

        if( streamMessage.get() )
        {
            e.message->streamInto(*streamMessage);

            producer->send(streamMessage.get());
        }
        else
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);

            dout << CtiTime() << " ActiveMQ CMS session error - could not create a stream message \n";
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

            producer->setTimeToLive(DefaultTimeToLive);

            _producers.insert(queueName, producer);

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
