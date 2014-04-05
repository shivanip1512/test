#include "precompiled.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "connection_server.h"
#include "amq_constants.h"
#include "amq_util.h"

using namespace Cti::Messaging::ActiveMQ;

volatile long CtiServerConnection::_serverConnectionCount = 0;

/**
 * class constructor
 * @param listenerConnection reference to the listener connection
 * @param inQ queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of 1 sec iteration to allow the connection to send remaining messages
 */
CtiServerConnection::CtiServerConnection( const CtiListenerConnection &listenerConnection,
                                          Que_t *inQ,
                                          int termSeconds ) :
    CtiConnection( string( "Server Connection " ) + CtiNumStr( InterlockedIncrement( &_serverConnectionCount )), inQ, termSeconds ),
    _replyDest( listenerConnection.getClientReplyDest() )
{
    setName( listenerConnection.getServerQueueName() );

    // use the same managed connection as the listener
    _connection = listenerConnection.getConnection();

    // set the outbound destination physical name
    _peerName = destPhysicalName(*_replyDest);

    // consider server connections valid by default
    _valid = true;
}

/**
 * class destructor
 */
CtiServerConnection::~CtiServerConnection()
{
    try
    {
        cleanConnection();
    }
    catch(...)
    {
        logException( __FILE__, __LINE__, "", "error cleaning the connection." );
    }
}

/**
 * establish a new connection : creates sessions, consumer and destination and
 * sends a reply message to the client connection
 * @return true if the connection has been establish, false otherwise
 */
bool CtiServerConnection::establishConnection()
{
    const long receiveMillis = 30000;

    try
    {
        _dontReconnect = true;

        _sessionIn.reset( _connection->createSession() );
        _sessionOut.reset( _connection->createSession() );

        // Create consumer for inbound traffic
        _consumer.reset( createTempQueueConsumer( *_sessionIn ));

        // Create producer for outbound traffic
        _producer.reset( createDestinationProducer( *_sessionOut, _replyDest.get() ));

        // Create advisory topic consumer
        setupAdvisoryListener();

        // create a new handshake reply message
        auto_ptr<cms::Message> outMessage( _sessionOut->createMessage() );

        outMessage->setCMSReplyTo( _consumer->getDestination() );
        outMessage->setCMSType( MessageType::serverResp );

        // send the message back to the client (the payload is the reply destination)
        _producer->send( outMessage.get() );

        // We should block here until the delay expires or until the connection is closed
        auto_ptr<cms::Message> ackMessage( _consumer->receive( receiveMillis ));

        if( ! ackMessage.get() )
        {
            logStatus( __FUNCTION__, "timeout while waiting for client acknowledge message" );
            return false;
        }

        if( ackMessage->getCMSType() != MessageType::clientAck )
        {
            logStatus( __FUNCTION__, "unexpected message type: \"" + ackMessage->getCMSType() + "\"" );
            return false;
        }

        if( ! ackMessage->getCMSReplyTo() )
        {
            logStatus( __FUNCTION__, "received null ReplyTo destination"
                                     ", expected: " + _producer->getDestPhysicalName() );
            return false;
        }

        if( destPhysicalName(*ackMessage->getCMSReplyTo()) != _producer->getDestPhysicalName() )
        {
            logStatus( __FUNCTION__, "received invalid ReplyTo destination: " + destPhysicalName(*ackMessage->getCMSReplyTo()) +
                                     ", expected: " + _producer->getDestPhysicalName() );
            return false;
        }

        _consumer->setMessageListener( _messageListener.get() );

        logStatus( __FUNCTION__, "successfully connected.\n"
                "inbound destination  : " + _consumer->getDestPhysicalName() + "\n"
                "outbound destination : " + _producer->getDestPhysicalName());

        return true;
    }
    catch( cms::CMSException& e )
    {
        forceTermination();

        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );

        return false;
    }
    catch( ... )
    {
        logException( __FILE__, __LINE__ );

        throw;
    }
}
