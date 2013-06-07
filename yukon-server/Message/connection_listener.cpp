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
    _title( "Listener Connection " + CtiNumStr( InterlockedIncrement( &_listenerConnectionCount )))
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

            // Create a Connection
            _connection.reset( Cti::Messaging::ActiveMQ::g_connectionFactory.createConnection( _brokerUri ));
            _connection->start();

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

        if( !_closed )
        {
            logStatus( __FUNCTION__, "unable to connect to the broker. Will try to reconnect." );
        
            Sleep( 1000 ); // Don't pound the system....
        }
    }

    logStatus( __FUNCTION__, "has closed." );

    return NOTNORMAL;
}


void CtiListenerConnection::close()
{
    // once the connection has been closed, it cannot be restarted
    _closed = true;
    _valid  = false;

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

