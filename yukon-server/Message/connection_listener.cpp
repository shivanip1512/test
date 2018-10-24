#include "precompiled.h"

using namespace std;

#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>

#include <wtypes.h>

#include <stdlib.h>
#include <stdio.h>

#include <algorithm>
#include <iomanip>
#include <functional>

#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <set>

#include "connection_listener.h"
#include "dllbase.h"
#include "amq_constants.h"
#include "amq_util.h"
#include "logger.h"
#include "GlobalSettings.h"

#include <activemq/core/ActiveMQConnection.h>

#include <atomic>

using namespace Cti::Messaging::ActiveMQ;


static std::atomic<long> listenerConnectionCount = 0;

/**
 * class constructor
 * @param serverQueueName name of the queue
 */
CtiListenerConnection::CtiListenerConnection( const string &serverQueueName ) :
    _closed( false ),
    _valid( false ),
    _serverQueueName( serverQueueName ),
    _title( "Listener Connection " + std::to_string(++listenerConnectionCount) )
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Error closing the connection.");
    }
}

/**
 * Establish a new connection, creates a session and consumer
 */
void CtiListenerConnection::start()
{
    if( _valid || _closed )
    {
        return;
    }

    for( ; ; )
    {
        try
        {
            //
            // release resources and reset the connection
            //
            {
                WriterGuard guard(_connMux);

                if( _closed )
                {
                    // prevent starting a new connection while closing
                    return;
                }

                releaseResources();

                const auto broker_host = GlobalSettings::getString(GlobalSettings::Strings::JmsBrokerHost, Broker::defaultHost);
                const auto broker_port = GlobalSettings::getString(GlobalSettings::Strings::JmsBrokerPort, Broker::defaultPort);

                // producerWindowSize sets the size in Bytes of messages that a producer can send before it is blocked
                // to await a ProducerAck from the broker that frees enough memory to allow another message to be sent.
                const string producerWindowSize = "connection.producerWindowSize=" +
                    to_string( GlobalSettings::getInteger( GlobalSettings::Integers::ProducerWindowSize, 1024 ) * 1024 );

                // MaxInactivityDuration controls how long AMQ keeps a socket open when it's not heard from it.
                const string maxInactivityDuration = "wireFormat.MaxInactivityDuration=" +
                    to_string( GlobalSettings::getInteger( GlobalSettings::Integers::MaxInactivityDuration, 30 ) * 1000 );

                _connection.reset( new ManagedConnection( Broker::protocol + broker_host + ":" + broker_port + "?" + producerWindowSize + "&" + maxInactivityDuration ) );
            }

            if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
            {
                CTILOG_DEBUG(dout, who() << " - connecting to the broker.")
            }

            //
            // connect to the broker
            //
            {
                ReaderGuard guard(_connMux);

                if( _connection )
                {
                    _connection->start();
                }
            }

            //
            // create session and consumer
            //
            {
                WriterGuard guard(_connMux);

                if( _closed )
                {
                    // connection has closed during broker connection attempt
                    return;
                }

                // Create a Session
                _session = _connection->createSession();

                // Create managed queue consumer
                _consumer = createQueueConsumer( *_session, _serverQueueName );

                _valid = true;
            }

            CTILOG_INFO(dout, who() << " - successfully connected.");

            return;
        }
        catch( cms::CMSException& e )
        {
            CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - Error while starting listener connection");
        }
        catch( ConnectionException& e )
        {
            if( !_closed )
            {
                CTILOG_EXCEPTION_WARN(dout, e, who() <<" - unable to connect to the broker. Will try to reconnect..");
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
        ReaderGuard guard(_connMux);

        if( _closed )
        {
            return;
        }

        // once the connection has been closed, it cannot be restarted
        _closed = true;

        if( _consumer )
        {
            // if the consumer exist, we are currently connected, close the consumer and release resources
            _consumer->close();
        }
        else if( _connection )
        {
            // if the consumer does not exist, but the connection does, we are currently trying to establish a connection,
            // abort the connection attempt by closing it.
            _connection->close();
        }
    }

    {
        WriterGuard guard(_connMux);
        releaseResources();
    }

    CTILOG_INFO(dout, who() << " - has closed.");
}

/**
 * check if the underlying cms connection is started and not failed
 * @return true is the connection is valid, false otherwise
 */
bool CtiListenerConnection::verifyConnection()
{
    ReaderGuard guard(_connMux);

    if( !_connection || !_connection->verifyConnection() || _closed )
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
    ReaderGuard guard(_connMux);

    if( !_valid || _closed )
    {
        return false;
    }

    try
    {
        const int timeoutMillis = 1000 * 30; // 30 seconds

        // We should block here until the connection is closed or if there is a timeout
        boost::scoped_ptr<cms::Message> message( _consumer->receive(timeoutMillis) );

        if( ! message || message->getCMSType() != MessageType::clientInit || ! message->getCMSReplyTo() )
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
        CTILOG_EXCEPTION_ERROR(dout, e, who() << " - Error while accepting new client connection");

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
    ReaderGuard guard(_connMux);

    return _connection;
}

/**
 * Return the accepted client reply destination
 * @return a pointer to a clone of the client reply destination
 */
std::unique_ptr<cms::Destination> CtiListenerConnection::getClientReplyDest() const
{
    std::unique_ptr<cms::Destination> clone;

    if( _clientReplyDest )
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
 * cleans up consumer, producer, sessions
 */
void CtiListenerConnection::releaseResources()
{
    _consumer.reset();
    _session.reset();
    _connection.reset(); // release the shared_ptr (child server connection may still be sharing this)
}
