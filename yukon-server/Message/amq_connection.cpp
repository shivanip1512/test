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

typedef std::map<ActiveMQ::OutboundQueues::type, std::string> StreamableOutboundQueueNameMap;
typedef std::map<ActiveMQ::OutboundQueues::type, std::string> ThriftOutboundQueueNameMap;
typedef std::map<ActiveMQ::InboundQueues::type, std::string> ThriftInboundQueueNameMap;

namespace {

typedef ActiveMQ::OutboundQueues SOQ;

const StreamableOutboundQueueNameMap StreamableOutboundQueueNames = boost::assign::map_list_of
    (SOQ::PorterResponses,
        "yukon.notif.stream.amr.PorterResponseMessage")
    (SOQ::SmartEnergyProfileControl,
        "yukon.notif.stream.dr.SmartEnergyProfileControlMessage")
    (SOQ::SmartEnergyProfileRestore,
        "yukon.notif.stream.dr.SmartEnergyProfileRestoreMessage")
    (SOQ::HistoryRowAssociationResponse,
        "yukon.notif.stream.dr.HistoryRowAssociationResponse")
    (SOQ::IvvcAnalysisMessage,
        "yukon.notif.stream.cc.IvvcAnalysisMessage")
    (SOQ::RfnBroadcast,
        "yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest")
    (SOQ::CapControlOperationMessage,
        "yukon.notif.stream.cc.CapControlOperationMessage");

typedef ActiveMQ::OutboundQueues TOQ;
typedef ActiveMQ::InboundQueues  TIQ;

const ThriftOutboundQueueNameMap ThriftOutboundQueueNames = boost::assign::map_list_of
    (TOQ::NetworkManagerE2eDataRequest,
        "yukon.rfn.e2e.data.request");

const ThriftInboundQueueNameMap ThriftInboundQueueNames = boost::assign::map_list_of
    (TIQ::NetworkManagerE2eDataIndication,
        "yukon.rfn.e2e.data.indication");
}

ActiveMQConnectionManager::ActiveMQConnectionManager(const string &broker_uri) :
    _broker_uri(broker_uri),
    _delay(2)
{
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
}


void ActiveMQConnectionManager::run()
{
    while( !isSet(SHUTDOWN) )
    {
        getOutgoingMessages();

        sendPendingMessages();

        sleep(_delay * 1000);
    }
}


void Cti::Messaging::ActiveMQConnectionManager::getOutgoingMessages()
{
    CtiLockGuard<CtiCriticalSection> lock(_outgoing_message_mux);

    while( !_outgoing_messages.empty() )
    {
        //  guarantee that we will not have null pointers in _pending_messages
        if( _outgoing_messages.front() )
        {
            _pending_messages.push(_outgoing_messages.front());
        }

        _outgoing_messages.pop();
    }
}


void ActiveMQConnectionManager::validateSetup()
{
    if( ! _connection.get() )
    {
        scoped_ptr<cms::Connection> connection;
        connection.reset( ActiveMQ::g_connectionFactory.createConnection( _broker_uri ) );
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


void ActiveMQConnectionManager::enqueueMessage(const ActiveMQ::OutboundQueues::type queueId, auto_ptr<StreamableMessage> message)
{
    gActiveMQConnection->enqueueOutgoingMessage(queueId, message);
}


void ActiveMQConnectionManager::enqueueOutgoingMessage(const ActiveMQ::OutboundQueues::type queueId, auto_ptr<StreamableMessage> message)
{
    if( ! message.get() )
    {
        return;
    }

    boost::optional<std::string> queueName = mapFind(StreamableOutboundQueueNames, queueId);

    //  ensure the message is not null
    if( ! queueName || queueName->empty() )
    {
        return;
    }

    envelope *e = new envelope;

    e->queue = *queueName;
    e->message.reset(message.release());

    {
        CtiLockGuard<CtiCriticalSection> lock(_outgoing_message_mux);

        _outgoing_messages.push(e);
    }

    if( !isRunning() )
    {
        //  starts its thread on first outbound message
        start();
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
