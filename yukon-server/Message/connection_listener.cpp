#include "precompiled.h"

using namespace std;

#include "activemq/core/ActiveMQConnection.h"
#include "connection_listener.h"
#include "amq_constants.h"
#include "amq_util.h"
#include "logger.h"

using namespace Cti::Messaging::ActiveMQ;


volatile long CtiListenerConnection::_listenerConnectionCount = 0;

/**
 * class constructor
 * @param serverQueueName name of the queue
 */
CtiListenerConnection::CtiListenerConnection( const string &serverQueueName ) :
    _closed( false ),
    _valid( false ),
    _serverQueueName( serverQueueName ),
    _title( "Listener Connection " + CtiNumStr( InterlockedIncrement( &_listenerConnectionCount )))
{
}

/**
 * class destructor
 */
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

/**
 * Establish a new connection, creates a session and consumer
 */
void CtiListenerConnection::start()
{
    if( _valid && !_closed )
    {
        return;
    }

    if( _closed )
    {
        Sleep(1000);
        return;
    }

    while( !_closed )
    {
        try
        {
            logDebug( __FUNCTION__, "connecting to the broker." );

            releaseResources();

            {
                CtiLockGuard<CtiCriticalSection> lock(_closeConnectionMux);

                if( _closed )
                {
                    break; // prevent starting a new connection while closing
                }

                _connection.reset( new ManagedConnection( Broker::flowControlURI ));
            }

            _connection->start();

            // Create a Session
            _session.reset( _connection->createSession() );

            // Create managed queue consumer
            _consumer.reset( createQueueConsumer( *_session, _serverQueueName ));

            _valid = true;

            logStatus( __FUNCTION__, "successfully connected." );

            return;
        }
        catch( cms::CMSException& e )
        {
            logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
        }
        catch( ConnectionException& e )
        {
            if( !_closed )
            {
                logStatus( __FUNCTION__, "unable to connect to the broker. Will try to reconnect." );
            }
        }

        Sleep( 1000 ); // Don't pound the system....
    }
}

/**
 * Closes the connection, or interrupt and close the connection that is currently established
 */
void CtiListenerConnection::close()
{
    {
        CtiLockGuard<CtiCriticalSection> lock(_closeConnectionMux);

        if( _closed )
        {
            return;
        }

        // once the connection has been closed, it cannot be restarted
        _closed = true;

        closeConnection();
    }

    // this delay allow to complete any functions we are currently in, before doing a cleanup
    Sleep(1000);

    releaseResources();

    logStatus( __FUNCTION__, "has closed." );
}

/**
 * check if the underlying cms connection is started and not failed
 * @return true is the connection is valid, false otherwise
 */
bool CtiListenerConnection::verifyConnection()
{
    if( _closed || !_connection || !_connection->verifyConnection() )
    {
        _valid = false;
    }

    return _valid;
}

/**
 * This is blocking function that waits for a client handshake message
 * @return true if a new client as been accepted, false otherwise
 */
bool CtiListenerConnection::acceptClient()
{
    _clientReplyDest.reset();

    if( _closed || !_valid )
    {
        return false;
    }

    try
    {
        const int timeoutMillis = 1000 * 30; // 30 seconds

        // We should block here until the connection is closed or if there is a timeout
        auto_ptr<cms::Message> message( _consumer->receive(timeoutMillis) );

        if( ! message.get() || message->getCMSType() != MessageType::clientInit || ! message->getCMSReplyTo() )
        {
            return false;
        }

        // validate the request : check for a duplicate request in the recent past
        if( ! validateRequest( destPhysicalName( *message->getCMSReplyTo() )))
        {
            return false;
        }

        // update the client reply destination when there's no error
        _clientReplyDest.reset( message->getCMSReplyTo()->clone() );

        return true;
    }
    catch( cms::CMSException& e )
    {
        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );

        return _valid = false;
    }
}

/**
 * check if request received is not a duplicate
 * @param replyTo physical name of reply destination
 * @return true if request is consider valid, false otherwise
 */
bool CtiListenerConnection::validateRequest( const string &replyTo )
{
    const CtiTime now;
    const CtiTime expired = now - 30; // 30 seconds - filter duplicate request younger then this

    // clean up expired
    DestTimeMap::iterator itr = requestTimeMap.begin();
    while( itr != requestTimeMap.end() )
    {
        if( itr->second <= expired )
        {
            requestTimeMap.erase(itr++);
        }
        else
        {
            ++itr;
        }
    }

    // try to insert
    return requestTimeMap.insert( make_pair( replyTo, now )).second;
}

/**
 * Creates a new session that is a child of the CMS Connection
 * @return a pointer to the new connection created
 */
boost::shared_ptr<ManagedConnection> CtiListenerConnection::getConnection() const
{
    return _connection;
}

/**
 * Return the accepted client reply destination
 * @return a pointer to a clone of the client reply destination
 */
auto_ptr<cms::Destination> CtiListenerConnection::getClientReplyDest() const
{
    auto_ptr<cms::Destination> clone;

    if( _clientReplyDest.get() )
    {
        clone.reset( _clientReplyDest->clone() );
    }

    return clone;
}

/**
 * name of the listener connection : title and server queue name
 * @return string with the name
 */
string CtiListenerConnection::who() const
{
    return _title + " \"" + _serverQueueName + "\"";
}

/**
 * get the server queue name use by this connection
 * @return string with the name
 */
string CtiListenerConnection::getServerQueueName() const
{
    return _serverQueueName;
}

/**
 * log status
 * @param funcName function name that will appear in the log
 * @param note additional detail to log
 */
void CtiListenerConnection::logStatus( string funcName, string note ) const
{
    string whoStr = who();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << funcName << " : " << whoStr << " " << note << endl;
    }
}

/**
 * log debug
 * @param funcName function name that will appear in the log
 * @param note additional detail to log
 */
void CtiListenerConnection::logDebug( string funcName, string note ) const
{
    if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
    {
        logStatus( funcName, note );
    }
}

/**
 * log exception
 * @param fileName file name
 * @param line line number
 * @param exceptionName exception name
 * @param note additional detail to log
 */
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

/**
 * cleans up consumer, producer, sessions
 */
void CtiListenerConnection::releaseResources()
{
    CtiLockGuard<CtiCriticalSection> lock(_closeConnectionMux);

    closeConnection();

    _consumer.reset();
    _session.reset();
    _connection.reset(); // release the shared_ptr (child server connection may still be sharing this)
}

/**
 * close the underlying cms connection if it exist
 */
void CtiListenerConnection::closeConnection()
{
    try
    {
        if( _connection )
        {
            // Close the connection as well as any child session, consumer, producer, destinations
            _connection->close();
        }
    }
    catch(...)
    {
        // catch all exception, no throw
    }
}
