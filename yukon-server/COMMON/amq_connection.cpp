#include "yukon.h"

#include "utility.h"

#include "cms/connectionfactory.h"

#include "amq_connection.h"

namespace Cti {

ActiveMQConnectionManager::ActiveMQConnectionManager(const std::string &broker_uri) :
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

    delete_assoc_container(_producers);
}


void ActiveMQConnectionManager::run()
{
    initialize();

    while( !isSet(SHUTDOWN) )
    {
        sendPendingMessages();

        sleep(2000);
    }
}


void ActiveMQConnectionManager::initialize()
{
    activemq::library::ActiveMQCPP::initializeLibrary();

    std::auto_ptr<cms::ConnectionFactory> connectionFactory(cms::ConnectionFactory::createCMSConnectionFactory(_broker_uri));

    _connection.reset(connectionFactory->createConnection());

    _connection->start();

    _session.reset(_connection->createSession());
}


void ActiveMQConnectionManager::sendPendingMessages()
{
    std::queue<envelope> tmpQueue;

    {
        //  lock as little as possible - just empty out the pending queue and let go
        CtiLockGuard<CtiCriticalSection> lock(_pending_message_mux);

        tmpQueue = _pending_messages;

        while( !_pending_messages.empty() )
        {
            _pending_messages.pop();
        }
    }

    while( !tmpQueue.empty() )
    {
        sendMessage(tmpQueue.front());

        tmpQueue.pop();
    }
}


void ActiveMQConnectionManager::enqueueMessage(const std::string &queue, const std::string &message)
{
    CtiLockGuard<CtiCriticalSection> lock(_pending_message_mux);

    envelope e = { queue, message };

    _pending_messages.push(e);

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


void ActiveMQConnectionManager::sendMessage(const envelope &e)
{
    cms::MessageProducer *producer = getProducer(e.queue);

    if( producer )
    {
        std::auto_ptr<cms::TextMessage> message = createTextMessage(e.message);

        producer->send(message.get());
    }
}


cms::MessageProducer *ActiveMQConnectionManager::getProducer(const std::string &queue)
{
    producer_map::iterator itr = _producers.find(queue);

    if( itr == _producers.end() )
    {
        cms::MessageProducer *producer = _session->createProducer(_session->createQueue(queue));

        itr = _producers.insert(make_pair(queue, producer)).first;
    }

    return itr->second;
}


std::auto_ptr<cms::TextMessage> ActiveMQConnectionManager::createTextMessage(const std::string &message)
{
    std::auto_ptr<cms::TextMessage> textMessage(_session->createTextMessage());

    textMessage->setText(message.c_str());

    return textMessage;
}


/*
void  ActiveMQConnectionManager::receiveMessage()
{
    std::auto_ptr<cms::Connection>      consumer_connection (connectionFactory->createConnection());
    consumer_connection->start();
    std::auto_ptr<cms::Session>         consumer_session    (consumer_connection->createSession());
    std::auto_ptr<cms::Topic>           consumer_topic      (consumer_session->createTopic("EXAMPLE-TOPIC"));
    std::auto_ptr<cms::MessageConsumer> myConsumer          (consumer_session->createConsumer(consumer_topic.get()));


    std::auto_ptr<cms::Message>     consumed_message(myConsumer->receive());
    std::auto_ptr<cms::TextMessage> consumed_text_message(dynamic_cast<cms::TextMessage *>(consumed_message.get()));

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

