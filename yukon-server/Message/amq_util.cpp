#include "precompiled.h"

#include "amq_util.h"

#include "logger.h"

#include <proton/connection_options.hpp>
#include <proton/sender_options.hpp>
#include <proton/source_options.hpp>
#include <proton/target_options.hpp>
#include <proton/receiver_options.hpp>
#include <proton/work_queue.hpp>


namespace Cti::Messaging::Qpid
{

/*-----------------------------------------------------------------------------
  Managed connection
-----------------------------------------------------------------------------*/
/*ManagedConnection::ManagedConnection(const std::string& brokerUri, proton::connection_options& connOpt) :
    _brokerUri( brokerUri ),
    _conn_options( connOpt ),
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
        catch( proton::error& e )
        {
            // re-check if the connection was closed
            if( conn->isClosed() )
            {
                return;
            }

            // log an error if close() has failed
            CTILOG_EXCEPTION_ERROR(dout, e, "Error closing Proton connection"<< ((attempt != maxAttempt) ? " (will retry)" : ""));
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
    // this reconnect stuff is now in the connection_options that are passed in the constructor...
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

//            g_connectionFactory.createConnection( _brokerUri, _conn_options );

            return; // exit if connection succeeded
        }
        catch( proton::error& e )
        {
            // print exception about every 5 min or if exception message changes
            if( attempt % loggingFreq == 1 || e.what() != prevMessage )
            {
                CTILOG_EXCEPTION_ERROR(dout, e, "Error starting Proton connection");

                prevMessage = e.what();
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
*/
/*-----------------------------------------------------------------------------
  Managed destination
-----------------------------------------------------------------------------*/
ManagedDestination::ManagedDestination( const std::string & dest )
    :   _dest( dest )
{
}

ManagedDestination::~ManagedDestination()
{
}

std::string ManagedDestination::getDestination() const
{
    return _dest;
}

/*-----------------------------------------------------------------------------
  Managed message producer
-----------------------------------------------------------------------------*/
ManagedProducer::ManagedProducer( proton::session & sess, const std::string & dest )
    :   ManagedDestination( dest ),
        _expiryDuration{ 60 * proton::duration::MINUTE },
        _readyToSend{ false }
{
    proton::sender_options options;

    _producer = sess.open_sender( dest, options );
}

ManagedProducer::~ManagedProducer()
{
   _producer.close();
}

void ManagedProducer::setTimeToLiveMillis( std::chrono::milliseconds time )
{
    _expiryDuration = time.count() * proton::duration::MILLISECOND;
}

void ManagedProducer::send( proton::message & msg )
{
    auto now = proton::timestamp::now();

    msg.durable( false );                       // non-persistent
    msg.expiry_time( now + _expiryDuration );

    _producer
        .work_queue()
        .add(
            [ = ]()
            {
                if ( _readyToSend )
                {
                    _producer.send( msg );
                }
                else
                {
                    _deferredMessages.push( msg );
                }
            } );                                       
}

void ManagedProducer::on_sender_open( proton::sender & s )
{
    _readyToSend = true;

    while ( ! _deferredMessages.empty() )
    {
        _producer.send( _deferredMessages.front() );
        _deferredMessages.pop();
    }
}

/*-----------------------------------------------------------------------------
  Managed message consumer
-----------------------------------------------------------------------------*/
ManagedConsumer::ManagedConsumer( proton::session & sess, const std::string & dest, MessageCallback c )
    :   ManagedDestination( dest ),
        _msgCallback( c )
{
    proton::receiver_options    options;

    if ( dest.empty() )     // this is a temp queue consumer
    {
        options.source( proton::source_options().dynamic( true ) );     // which one of these???  source or target?
        options.target( proton::target_options().dynamic( true ) );
    }

    _consumer = sess.open_receiver( dest, options );
}

ManagedConsumer::ManagedConsumer( proton::session& sess, const std::string& dest, MessageCallback c, OpenCallback o )
    :   ManagedConsumer(sess, dest, c)
{
    _openCallback = o;
}

ManagedConsumer::~ManagedConsumer()
{
    _consumer.close();
}

void ManagedConsumer::on_message( proton::delivery & d, proton::message & msg )
{
    _msgCallback( msg );   // magic!!!!!!!!!!!!!!!!!!!!!
}

void ManagedConsumer::on_receiver_open( proton::receiver & rcvr )
{
    if ( _openCallback )
    {
        _openCallback( rcvr );
    }
}

/*-----------------------------------------------------------------------------
  Managed destination message producer
-----------------------------------------------------------------------------*/  // this is the temp queue producer as well as named queue prod

DestinationProducer::DestinationProducer( proton::session & sess, const std::string & dest ) :
    ManagedProducer( sess, dest )
{
}

DestinationProducer::~DestinationProducer()
{
}

/*-----------------------------------------------------------------------------
  Managed destination message consumer
-----------------------------------------------------------------------------*/
DestinationConsumer::DestinationConsumer(proton::session& sess, const std::string& dest, MessageCallback c) :
    ManagedConsumer(sess, dest, c)
{
}

DestinationConsumer::DestinationConsumer( proton::session & sess, const std::string & dest, MessageCallback c, OpenCallback o ) :
    ManagedConsumer( sess, dest, c, o )
{
}

DestinationConsumer::~DestinationConsumer()
{
}

/*-----------------------------------------------------------------------------
  Managed Queue message producer
-----------------------------------------------------------------------------*/
QueueProducer::QueueProducer( proton::session & sess, const std::string & dest ) :
    DestinationProducer( sess, dest )
{
}

QueueProducer::~QueueProducer()
{
}

/*-----------------------------------------------------------------------------
  Managed Queue message consumer
-----------------------------------------------------------------------------*/
QueueConsumer::QueueConsumer(proton::session& sess, const std::string& dest, MessageCallback c) :
    DestinationConsumer(sess, dest, c)
{
}

QueueConsumer::QueueConsumer( proton::session & sess, const std::string & dest, MessageCallback c, OpenCallback o ) :
    DestinationConsumer( sess, dest, c, o )
{
}

QueueConsumer::~QueueConsumer()
{
}

/*-----------------------------------------------------------------------------
  Managed topic message consumer
-----------------------------------------------------------------------------*/
TopicConsumer::TopicConsumer(proton::session& sess, const std::string& dest, MessageCallback c, const std::string& selector) :
    DestinationConsumer(sess, dest, c),
    _selector(selector)
{
}

TopicConsumer::TopicConsumer( proton::session & sess, const std::string & dest, MessageCallback c, OpenCallback o, const std::string & selector ) :
     DestinationConsumer( sess, dest, c, o ),
     _selector( selector )
{
}

TopicConsumer::~TopicConsumer()
{
}

/*-----------------------------------------------------------------------------
  Managed temporary queue message consumer
-----------------------------------------------------------------------------*/
TempQueueConsumer::TempQueueConsumer( proton::session & sess, MessageCallback c) :
    QueueConsumer( sess, "", c )
{
}

TempQueueConsumer::TempQueueConsumer(proton::session& sess, MessageCallback c, OpenCallback o) :
    QueueConsumer(sess, "", c, o)
{
}

TempQueueConsumer::~TempQueueConsumer()
{
}

}

