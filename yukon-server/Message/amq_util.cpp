#include "precompiled.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

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
    if( ! (_hCloseEvent = CreateEvent(NULL,FALSE,FALSE,NULL)) )
    {
        throw ConnectionException("Unexpected CreateEvent() has failed with error code: " + ::GetLastError() );
    }
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

    ::CloseHandle(_hCloseEvent);
}

// YUK-12801 (workaround)
// activemq-cpp 3.8.1 - a crash may occur while destroying the connection object. it appears that in
// some cases unhandle exceptions are caught during destructions. This causes ActiveMQ's worker threads
// to remain unaware that the ActiveMQConnection object has been destroyed - causing the
// application crash
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

    for( unsigned attempt=0; attempt != maxAttempt; attempt++ )
    {
        string errorMessage;

        try
        {
            conn->close();
        }
        catch(cms::CMSException& e)
        {
            errorMessage = e.what(); // capture the error message
        }

        // check if the connection was closed
        if( conn->isClosed() )
        {
            return;
        }

        // log error if close has failed
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime::now() << " Error closing ActiveMQ connection: \"" << errorMessage << "\" "
                 << ((attempt != maxAttempt) ? ", will retry " : " ") << __FILE__ << " ("<< __LINE__ << ")" << endl;
        }

        Sleep(delayMillis);
        delayMillis *= 2;
    }
}

void ManagedConnection::waitCloseEvent( unsigned millis )
{
    // expecting WaitForSingleObject() to return WAIT_TIMEOUT
    // or WAIT_OBJECT_0 (if there is a connection close event).
    
    switch( ::WaitForSingleObject( _hCloseEvent, millis ) )
    {
        case WAIT_TIMEOUT:
        {
            return; // if its a timeout, do not throw and return;
        }
        case WAIT_OBJECT_0:
        {
            throw ConnectionException("Connection has closed");
        }
        // unexpected results
        case WAIT_ABANDONED:
        {
            throw ConnectionException("Unexpected WAIT_ABANDONED");
        }
        case WAIT_FAILED:
        {
            throw ConnectionException("Unexpected WAIT_FAILED, error code: " + ::GetLastError() );
        }
    }
}

void ManagedConnection::start()
{
    const unsigned initialReconnectMillis = 1000;       // 1 sec
    const unsigned maxReconnectMillis     = 1000 * 30;  // 30 sec
    const unsigned maxReconnectAttempts   = 120;        // 120 attempts (about 1 hour, considering 30 sec/attempt)
    const unsigned loggingFreq            = 10;         // every 10 attempt (about 5 min, considering 30 sec/attempt)

    string prevMessage;

    unsigned reconnectMillis = initialReconnectMillis;

    for(unsigned connAttempt = 1; connAttempt <= maxReconnectAttempts; connAttempt++)
    {
        try
        {
            WriterGuard guard( _lock );

            if( _closed )
            {
                throw ConnectionException("Connection has closed");
            }

            // make sure the connection is closed before destroying it
            closeConnection();

            _connection.reset( g_connectionFactory.createConnection( _brokerUri ));
            _connection->start();

            return; // exit if connection succeeded
        }
        catch( cms::CMSException& e )
        {
            // print exception about every 5 min or if exception message changes
            if( connAttempt % loggingFreq == 1 || e.getMessage() != prevMessage )
            {
                {
                    CtiLockGuard<CtiLogger> dout_guard(dout);
                    dout << CtiTime::now() << " Error starting ActiveMQ connection: \"" << e.what() << "\" "<< __FILE__ << " ("<< __LINE__ << ")" << endl;
                }

                prevMessage = e.getMessage();
            }
        }

        // re-throw if we reach the maximum number of attempts
        if( connAttempt == maxReconnectAttempts )
        {
            throw ConnectionException("Maximum number of connection attempt has been reached");
        }

        waitCloseEvent( reconnectMillis );

        reconnectMillis = min( 2 * reconnectMillis, maxReconnectMillis );
    }
}

void ManagedConnection::close()
{
    ReaderGuard guard( _lock );

    if( _closed )
    {
        return;
    }

    _closed = true;

    if( ! ::SetEvent( _hCloseEvent ) )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime::now() << "Unexpected SetEvent() has failed with error code: " + ::GetLastError();
    }

    closeConnection();
}

void ManagedConnection::setExceptionListener( cms::ExceptionListener *listener )
{
    ReaderGuard guard( _lock );

    if( !_connection )
    {
        throw ConnectionException("Connection object is NULL");
    }

    _connection->setExceptionListener( listener );
}

cms::Session* ManagedConnection::createSession()
{
    ReaderGuard guard( _lock );

    if( !_connection )
    {
        throw ConnectionException("Connection object is NULL");
    }

    return _connection->createSession();
}

bool ManagedConnection::verifyConnection() const
{
    ReaderGuard guard( _lock );

    const activemq::core::ActiveMQConnection* conn = dynamic_cast<const activemq::core::ActiveMQConnection*>( _connection.get() );

    return ( conn && !conn->isClosed() && conn->isStarted() && !conn->isTransportFailed() );
}

const string& ManagedConnection::getBrokerUri() const
{
    return _brokerUri;
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

TopicConsumer::TopicConsumer( cms::Session &session, cms::Topic* dest, const string &selector ) :
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

