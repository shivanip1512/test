#include "precompiled.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "cms/ConnectionFactory.h"
#include "activemq/library/activemqcpp.h"
#include "activemq/core/ActiveMQConnection.h"
#include "amq_util.h"

namespace Cti {
namespace Messaging {
namespace ActiveMQ {

namespace {

struct ActiveMQIntializer
{
    ActiveMQIntializer()
    {
        activemq::library::ActiveMQCPP::initializeLibrary(); // can throw std::runtime_exception
    }

    ~ActiveMQIntializer()
    {
        activemq::library::ActiveMQCPP::shutdownLibrary(); // can throw std::runtime_exception
    }
};

boost::scoped_ptr<ActiveMQIntializer> g_activeMQIntializer;

}


ConnectionFactory::ConnectionFactory() :
    _isInitialized(false)
{
    InitializeCriticalSection(&_cs);
}

ConnectionFactory::~ConnectionFactory()
{
}

/*-----------------------------------------------------------------------------
    Intialize activemq library
-----------------------------------------------------------------------------*/
void ConnectionFactory::initializeLib()
{
    EnterCriticalSection(&_cs);

    if( g_activeMQIntializer.get() == NULL )
    {
        g_activeMQIntializer.reset( new ActiveMQIntializer );
    }

    LeaveCriticalSection(&_cs);

    _isInitialized = true;
}

/*-----------------------------------------------------------------------------
    Intialize activemq library and create a new connection

    returns:
    pointer to the new connection that the caller owns
-----------------------------------------------------------------------------*/
cms::Connection* ConnectionFactory::createConnection( const string &brokerUri )
{
    if( !_isInitialized )
    {
        initializeLib();
    }

    boost::scoped_ptr<cms::ConnectionFactory> connectionFactory( cms::ConnectionFactory::createCMSConnectionFactory( brokerUri ));

    return connectionFactory->createConnection();
}

/*-----------------------------------------------------------------------------
    Singleton of connectionFactory
-----------------------------------------------------------------------------*/
IM_EX_MSG ConnectionFactory g_connectionFactory;


/*-----------------------------------------------------------------------------
  Managed connection
-----------------------------------------------------------------------------*/
ManagedConnection::ManagedConnection( const string &brokerUri ) :
    _brokerUri( brokerUri ),
    _closed( false )
{
}

ManagedConnection::~ManagedConnection ()
{
}

void ManagedConnection::start()
{
    const int initialReconnectDelay = 1;    // 1 sec
    const int maxReconnectDelay     = 30;   // 30 sec
    const int maxReconnectAttempts  = 120;  // 120 attempts (about 1 hour, considering 30 sec/attempt)

    int reconnectDelay = initialReconnectDelay;

    for(int connAttempt = 1; connAttempt <= maxReconnectAttempts; connAttempt++)
    {
        try
        {
            boost::lock_guard<boost::mutex> guard(_mutex);

            if( _closed )
            {
                throw ConnectionException("Connection has closed");
            }

            _connection.reset( g_connectionFactory.createConnection( _brokerUri ));
            _connection->start();

            return; // exit if connection succeeded
        }
        catch( cms::CMSException& e )
        {
            // re-throw if the connection is either closed or if we reach the maximum number of attempts
            if( connAttempt >= maxReconnectAttempts )
            {
                throw ConnectionException("Maximum number of connection attempt has been reached");
            }
        }

        for(int sleepIter = 0; sleepIter < reconnectDelay && !_closed; sleepIter++)
        {
            Sleep(1000);
        }

        reconnectDelay = min( 2 * reconnectDelay, maxReconnectDelay );
    }
}

void ManagedConnection::close()
{
     boost::lock_guard<boost::mutex> guard(_mutex);

     _closed = true;

     if( _connection )
     {
         _connection->close();
     }
}

void ManagedConnection::setExceptionListener( cms::ExceptionListener *listener )
{
    boost::lock_guard<boost::mutex> guard(_mutex);

    if( !_connection )
    {
        throw ConnectionException("Connection object is NULL");
    }

    _connection->setExceptionListener( listener );
}

cms::Session* ManagedConnection::createSession()
{
    boost::lock_guard<boost::mutex> guard(_mutex);

    if( !_connection )
    {
        throw ConnectionException("Connection object is NULL");
    }

    return _connection->createSession();
}

bool ManagedConnection::verifyConnection ()
{
    boost::lock_guard<boost::mutex> guard(_mutex);

    const activemq::core::ActiveMQConnection* conn = dynamic_cast<const activemq::core::ActiveMQConnection*>( _connection.get() );

    return ( conn && !conn->isClosed() && conn->isStarted() && !conn->isTransportFailed() );
}


/*-----------------------------------------------------------------------------
  Managed destination
-----------------------------------------------------------------------------*/
ManagedDestination::~ManagedDestination()
{
}

string ManagedDestination::getDestPhysicalName() const
{
    return destPhysicalName( *(this->getDestination()) );
}

/*-----------------------------------------------------------------------------
  Managed message producer
-----------------------------------------------------------------------------*/
ManagedProducer::ManagedProducer( cms::MessageProducer* producer ) :
    _producer( producer )
{
    _producer->setDeliveryMode( cms::DeliveryMode::NON_PERSISTENT ); // set to NON_PERSISTENT
}

ManagedProducer::~ManagedProducer()
{
}

void ManagedProducer::setTimeToLive( long long time )
{
   _producer->setTimeToLive( time );
}

void ManagedProducer::send( cms::Message *message )
{
   _producer->send( message );
}

/*-----------------------------------------------------------------------------
  Managed message consumer
-----------------------------------------------------------------------------*/
ManagedConsumer::ManagedConsumer( cms::MessageConsumer* consumer ) :
    _consumer( consumer )
{
}

ManagedConsumer::~ManagedConsumer()
{
}

void ManagedConsumer::setMessageListener( cms::MessageListener *listener )
{
    _consumer->setMessageListener( listener );
}

cms::Message* ManagedConsumer::receive()
{
    return _consumer->receive();
}

cms::Message* ManagedConsumer::receive( int millisecs )
{
    return _consumer->receive( millisecs );
}

cms::Message* ManagedConsumer::receiveNoWait()
{
    return _consumer->receiveNoWait();
}

/*-----------------------------------------------------------------------------
  Managed destination message producer
-----------------------------------------------------------------------------*/
DestinationProducer::DestinationProducer( cms::Session &session, cms::Destination *dest ) :
    ManagedProducer( session.createProducer( dest )),
    _dest( dest )
{
}

DestinationProducer::~DestinationProducer()
{
}

const cms::Destination* DestinationProducer::getDestination() const
{
    return _dest.get();
}


/*-----------------------------------------------------------------------------
  Managed Queue message producer
-----------------------------------------------------------------------------*/
QueueProducer::QueueProducer( cms::Session &session, cms::Queue* dest ) :
    ManagedProducer( session.createProducer( dest )),
    _dest( dest )
{
}

QueueProducer::~QueueProducer()
{
}

const cms::Destination* QueueProducer::getDestination() const
{
    return _dest.get();
}


/*-----------------------------------------------------------------------------
  Managed Queue message consumer
-----------------------------------------------------------------------------*/
QueueConsumer::QueueConsumer( cms::Session &session, cms::Queue* dest ) :
    ManagedConsumer( session.createConsumer( dest )),
    _dest( dest )
{
}

QueueConsumer::~QueueConsumer()
{
}

const cms::Destination* QueueConsumer::getDestination() const
{
    return _dest.get();
}


/*-----------------------------------------------------------------------------
  Managed topic message consumer
-----------------------------------------------------------------------------*/
TopicConsumer::TopicConsumer( cms::Session &session, cms::Topic* dest ) :
    ManagedConsumer( session.createConsumer( dest )),
    _dest( dest )
{
}

TopicConsumer::TopicConsumer( cms::Session &session, cms::Topic* dest, const string &selector ) :
    ManagedConsumer( session.createConsumer( dest, selector )),
    _dest( dest )
{
}

TopicConsumer::~TopicConsumer()
{
}

const cms::Destination* TopicConsumer::getDestination() const
{
    return _dest.get();
}


/*-----------------------------------------------------------------------------
  Managed temporary queue message consumer
-----------------------------------------------------------------------------*/
TempQueueConsumer::TempQueueConsumer( cms::Session &session, cms::TemporaryQueue* dest ) :
    ManagedConsumer( session.createConsumer( dest )),
    _dest( dest )
{
}

TempQueueConsumer::~TempQueueConsumer()
{
    try
    {
        close();
    }
    catch(...)
    {
        // catch all, do not throw
    }
}

void TempQueueConsumer::close()
{
    _consumer->close(); // close the message consumer (if it exist), before closing the destination
    _dest->destroy();
}

const cms::Destination* TempQueueConsumer::getDestination() const
{
    return _dest.get();
}


}
}
}
