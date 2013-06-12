#include "precompiled.h"

using namespace std;

#include "activemq/core/ActiveMQConnection.h"
#include "connection_listener.h"
#include "amq_constants.h"
#include "amq_util.h"
#include "logger.h"


volatile long CtiListenerConnection::_listenerConnectionCount = 0;


CtiListenerConnection::CtiListenerConnection( const string &serverQueueName ) :
    _closed( false ),
    _valid( false ),
    _brokerUri( Cti::Messaging::ActiveMQ::Broker::startupReconnectURI ),
    _serverQueueName( serverQueueName ),
    _title( "Listener Connection " + CtiNumStr( InterlockedIncrement( &_listenerConnectionCount ))),
    _brokerConnStarted( false )
{
}


CtiListenerConnection::~CtiListenerConnection()
{
    try
    {
        close();
    }
    catch(...)
    {
        logException( __FILE__, __LINE__, "", "error closing." );
    }
}


//
//  The broker connection thread use as a wrapper around the cms::Connection::start() function.
//
//  NOTE:
//  activemq-cpp v3.7.0 appears to have an issue :
//  closing a connection with cms::Connection::close() results in a runtime error if the connection is blocked inside
//  a cms::Connection::start(). As a temporary solution, cms::Connection::start() will be executed inside a thread, and
//  this thread will be terminated when the connection has to be closed()
//
void CtiListenerConnection::brokerConnThread()
{
    try
    {
        _connection->start(); // start the connection
    }
    catch( cms::CMSException& e )
    {
        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );

        Sleep( 1000 ); // Don't pound the system....

        return;
    }
    catch(...)
    {
        logException( __FILE__, __LINE__ );

        // we should never get here since cms::Connection::start() can only return CMSExceptions

        return;
    }

    _brokerConnStarted = true;
}

//
//  Function that start and wait for the BrokerConnThread to complete.
//  Returns NORMAL if the connection has started
//
bool CtiListenerConnection::startBrokerConnection()
{
    _brokerConnStarted = false;

    // Create connection
    _connection.reset( Cti::Messaging::ActiveMQ::g_connectionFactory.createConnection( _brokerUri ));

    // create and start thread
    {
        boost::lock_guard<boost::mutex> guard(_brokerConnMutex);

        if( _closed )
        {
            return false;
        }

        _brokerConnThread.reset( new boost::thread( &CtiListenerConnection::brokerConnThread , this ));
    }

    _brokerConnThread->join();

    return _brokerConnStarted;
}


int CtiListenerConnection::establishConnection()
{
    if( _valid && !_closed )
    {
        return NORMAL;
    }

    for(;!_closed;)
    {
        try
        {
            logStatus( __FUNCTION__, "is connecting." );

            // Create and start connection to broker
            if( !startBrokerConnection() )
            {
                if( !_closed )
                {
                    logStatus( __FUNCTION__, "unable to connect to the broker. Will try to reconnect." );
                }
                continue;
            }

            // Create a Session
            _session.reset( _connection->createSession() );

            // Create the destination
            _destination.reset( _session->createQueue( _serverQueueName ));

            // Create the message consumer
            _consumer.reset( _session->createConsumer( _destination.get() ));

            _valid = true;

            logStatus( __FUNCTION__, "is valid." );

            return NORMAL;
        }
        catch( cms::CMSException& e )
        {
            logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
        }

        Sleep( 1000 ); // Don't pound the system....
    }

    logStatus( __FUNCTION__, "has closed." );

    return NOTNORMAL;
}


void CtiListenerConnection::close()
{
    // once the connection has been closed, it cannot be restarted
    _closed = true;
    _valid  = false;

    try
    {
        {
            boost::lock_guard<boost::mutex> guard(_brokerConnMutex);

            if( _brokerConnThread.get() )
            {
                TerminateThread( _brokerConnThread->native_handle(), EXIT_SUCCESS );
            }
        }
    }
    catch(...)
    {
        // since we are shutting down, we dont care about exceptions
    }

    // Close the connection as well as any child session, consumer, producer, destinations
    if( _connection.get() )
    {
    	_connection->close();
    }
}


int CtiListenerConnection::verifyConnection()
{
    activemq::core::ActiveMQConnection* conn = dynamic_cast<activemq::core::ActiveMQConnection*>( _connection.get() );

    if( conn == NULL || conn->isClosed() || !conn->isStarted() || conn->isTransportFailed() )
    {
        _valid = false;
    }

    return (_valid) ? NORMAL : NOTNORMAL;
}


int CtiListenerConnection::acceptClient()
{
    _clientReplyDest.reset();

    if( !_valid )
    {
        return NOTNORMAL;
    }

    try
    {
        // We should block here until the connection is closed
        auto_ptr<cms::Message> message( _consumer->receive() );

        if( message.get() != NULL && message->getCMSType() == Cti::Messaging::ActiveMQ::MessageType::clientInit && message->getCMSReplyTo() != NULL )
        {
            // update the client reply destination when there's no error
            _clientReplyDest.reset( message->getCMSReplyTo()->clone() );

            return NORMAL;
        }
    }
    catch( cms::CMSException& e )
    {
        _valid = false;
    }

    return NOTNORMAL;
}


std::auto_ptr<cms::Session> CtiListenerConnection::createSession()
{
    return std::auto_ptr<cms::Session>( _connection->createSession() );
}


std::auto_ptr<cms::Destination> CtiListenerConnection::getClientReplyDest()
{
    std::auto_ptr<cms::Destination> clone;

    if( _clientReplyDest.get() )
    {
        clone.reset( _clientReplyDest->clone() );
    }

    return clone;
}


string CtiListenerConnection::who() const
{
    return _title + " \"" + _serverQueueName + "\"";
}


void CtiListenerConnection::logStatus( string funcName, string note ) const
{
    string whoStr = who();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << funcName << " : " << whoStr << " " << note << endl;
    }
}


void CtiListenerConnection::logException( string fileName, int line, string exceptionName, string note ) const
{
    string whoStr = who();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << whoStr << " " << fileName << " (" << line << ") ";

        if(!exceptionName.empty())
            dout << " " << exceptionName;

        if(!note.empty())
            dout << " : " << note;

        dout << endl;
    }
}

