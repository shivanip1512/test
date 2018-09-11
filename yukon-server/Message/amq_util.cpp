#include "precompiled.h"

#include "cms/ConnectionFactory.h"
#include "activemq/library/activemqcpp.h"
#include "activemq/core/ActiveMQConnection.h"
#include "amq_util.h"

#include "logger.h"

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

std::unique_ptr<ActiveMQIntializer> g_activeMQIntializer;

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
        g_activeMQIntializer = std::make_unique<ActiveMQIntializer>();
    }

    LeaveCriticalSection(&_cs);

    _isInitialized = true;
}

/*-----------------------------------------------------------------------------
    Intialize activemq library and create a new connection

    returns:
    pointer to the new connection that the caller owns
-----------------------------------------------------------------------------*/
std::unique_ptr<cms::Connection> ConnectionFactory::createConnection( const std::string &brokerUri )
{
    if( !_isInitialized )
    {
        initializeLib();
    }

    std::unique_ptr<cms::ConnectionFactory> connectionFactory { cms::ConnectionFactory::createCMSConnectionFactory( brokerUri ) };

    return std::unique_ptr<cms::Connection> { connectionFactory->createConnection() };
}

/*-----------------------------------------------------------------------------
    Singleton of connectionFactory
-----------------------------------------------------------------------------*/
IM_EX_MSG ConnectionFactory g_connectionFactory;


/*-----------------------------------------------------------------------------
  Managed connection
-----------------------------------------------------------------------------*/
ManagedConnection::ManagedConnection( const std::string &brokerUri ) :
    _brokerUri( brokerUri ),
    _closed( false )
{
}

ManagedConnection::~ManagedConnection()
{
    try
    {
        // make sure connection is closed before destroying it
        close();
    }
    catch(...)
    {
        // we dont care about exception in the destructor
    }
}

// YUK-12801 (workaround)
// activemq-cpp 3.8.1 - a crash may occur while destroying the connection object. it appears that in
// some cases unhandle exceptions are caught during destructions. This causes ActiveMQ's worker threads
// to remain unaware that the ActiveMQConnection object has been destroyed - causing the
// application crash
//
// activemq-cpp 3.8.2 - this issue may be fixed with AMQCPP-520 (this needs to be determined)
//
// has a workaround, closing the ActiveMQConnection is retried a few times (until successful), if unsuccessful
// a small delay is added after each close attempt. Only after trying to close it that the connection object is
// destroy/reset
void ManagedConnection::closeConnection()
{
    unsigned const maxAttempt  = 5;
    unsigned       delayMillis = 250; // 250ms, 500ms, 1sec, 2sec, 4sec

    activemq::core::ActiveMQConnection* conn = dynamic_cast<activemq::core::ActiveMQConnection*>( _connection.get() );
    if( ! conn )
    {
        return;
    }

    for( unsigned attempt=1; ; attempt++ )
    {
        try
        {
            conn->close();

            // if close() does not throw there is no error
            return;
        }
        catch( cms::CMSException& e )
        {
            // re-check if the connection was closed
            if( conn->isClosed() )
            {
                return;
            }

            // log an error if close() has failed
            CTILOG_EXCEPTION_ERROR(dout, e, "Error closing ActiveMQ connection"<< ((attempt != maxAttempt) ? " (will retry)" : ""));
        }

        if( attempt == maxAttempt )
        {
            return;
        }

        Sleep(delayMillis);
        delayMillis *= 2;
    }
}

void ManagedConnection::waitCloseEvent( unsigned millis )
{
    try
    {
        boost::unique_lock<boost::mutex> lock( _closeMux );

        if( _closeCond.timed_wait( lock, boost::posix_time::milliseconds( millis )) && _closed )
        {
            throw ConnectionException("Connection has closed");
        }
    }
    catch( boost::thread_interrupted& )
    {
        close();

        throw; // re-throw the exception
    }
}

void ManagedConnection::start()
{
    const unsigned initialReconnectMillis = 1000;       // 1 sec
    const unsigned maxReconnectMillis     = 1000 * 30;  // 30 sec
    const unsigned maxReconnectAttempts   = 120;        // 120 attempts (about 1 hour, considering 30 sec/attempt)
    const unsigned loggingFreq            = 10;         // every 10 attempt (about 5 min, considering 30 sec/attempt)

    std::string prevMessage;

    unsigned reconnectMillis = initialReconnectMillis;

    for( unsigned attempt=1; ; attempt++ )
    {
        try
        {
            CTIWRITELOCKGUARD(guard, _lock );

            if( _closed )
            {
                throw ConnectionException("Connection has closed");
            }

            // make sure the connection is closed before destroying it
            closeConnection();

            _connection = g_connectionFactory.createConnection( _brokerUri );
            _connection->start();

            return; // exit if connection succeeded
        }
        catch( cms::CMSException& e )
        {
            // print exception about every 5 min or if exception message changes
            if( attempt % loggingFreq == 1 || e.getMessage() != prevMessage )
            {
                CTILOG_EXCEPTION_ERROR(dout, e, "Error starting ActiveMQ connection");

                prevMessage = e.getMessage();
            }
        }

        if( attempt == maxReconnectAttempts )
        {
            throw ConnectionException("Maximum number of connection attempt has been reached");
        }

        waitCloseEvent( reconnectMillis );

        // find the next delay before reconnecting
        reconnectMillis = std::min( 2 * reconnectMillis, maxReconnectMillis );
    }
}

void ManagedConnection::close()
{
    CTIREADLOCKGUARD(guard, _lock);

    {
        boost::unique_lock<boost::mutex> lock( _closeMux );

        if( _closed )
        {
            return;
        }

        _closed = true;

        _closeCond.notify_one();
    }

    closeConnection();
}

void ManagedConnection::setExceptionListener( cms::ExceptionListener *listener )
{
    CTIREADLOCKGUARD(guard, _lock);

    if( !_connection )
    {
        throw ConnectionException("Connection object is NULL");
    }

    _connection->setExceptionListener( listener );
}

std::unique_ptr<cms::Session> ManagedConnection::createSession()
{
    CTIREADLOCKGUARD(guard, _lock);

    if( !_connection )
    {
        throw ConnectionException("Connection object is NULL");
    }

    return std::unique_ptr<cms::Session>(_connection->createSession());
}

bool ManagedConnection::verifyConnection() const
{
    CTIREADLOCKGUARD(guard, _lock);

    const activemq::core::ActiveMQConnection* conn = dynamic_cast<const activemq::core::ActiveMQConnection*>( _connection.get() );

    return ( conn && !conn->isClosed() && conn->isStarted() && !conn->isTransportFailed() );
}

const std::string& ManagedConnection::getBrokerUri() const
{
    return _brokerUri;
}

/*-----------------------------------------------------------------------------
  Managed destination
-----------------------------------------------------------------------------*/
ManagedDestination::~ManagedDestination()
{
}

std::string ManagedDestination::getDestPhysicalName() const
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

void ManagedProducer::setTimeToLiveMillis( long long time )
{
   _producer->setTimeToLive( time );
}

void ManagedProducer::send( cms::Message *message )
{
   _producer->send( message );
}

void ManagedProducer::close()
{
    return _producer->close();
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

void ManagedConsumer::close()
{
    return _consumer->close();
}

/*-----------------------------------------------------------------------------
  Managed destination message producer
-----------------------------------------------------------------------------*/

DestinationProducer::DestinationProducer( cms::MessageProducer *producer, cms::Destination *dest ) :
    ManagedProducer( producer ),
    _dest( dest )
{
}

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
  Managed destination message consumer
-----------------------------------------------------------------------------*/
DestinationConsumer::DestinationConsumer( cms::MessageConsumer *consumer, cms::Destination *dest ) :
    ManagedConsumer( consumer ),
    _dest( dest )
{
}

DestinationConsumer::~DestinationConsumer()
{
}

const cms::Destination* DestinationConsumer::getDestination() const
{
    return _dest.get();
}

/*-----------------------------------------------------------------------------
  Managed Queue message producer
-----------------------------------------------------------------------------*/
QueueProducer::QueueProducer( cms::Session &session, cms::Queue* dest ) :
    DestinationProducer( session.createProducer( dest ), dest )
{
}

QueueProducer::~QueueProducer()
{
}

/*-----------------------------------------------------------------------------
  Managed Queue message consumer
-----------------------------------------------------------------------------*/
QueueConsumer::QueueConsumer( cms::Session &session, cms::Queue* dest ) :
    DestinationConsumer( session.createConsumer( dest ), dest )
{
}

QueueConsumer::~QueueConsumer()
{
}

/*-----------------------------------------------------------------------------
  Managed topic message consumer
-----------------------------------------------------------------------------*/
TopicConsumer::TopicConsumer( cms::Session &session, cms::Topic* dest ) :
     DestinationConsumer( session.createConsumer( dest ), dest )
{
}

TopicConsumer::TopicConsumer( cms::Session &session, cms::Topic* dest, const std::string &selector ) :
     DestinationConsumer( session.createConsumer( dest, selector ), dest )
{
}

TopicConsumer::~TopicConsumer()
{
}

/*-----------------------------------------------------------------------------
  Managed temporary queue message consumer
-----------------------------------------------------------------------------*/
TempQueueConsumer::TempQueueConsumer( cms::Session &session, cms::TemporaryQueue* dest ) :
    QueueConsumer( session, dest )
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
    ManagedConsumer::close(); // close the message consumer, before closing the destination

    static_cast<cms::TemporaryQueue&>(*_dest).destroy();
}

}
}
}

