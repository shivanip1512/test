#include "precompiled.h"

#include "connection_server.h"
#include "amq_constants.h"
#include "amq_util.h"

#include <atomic>

using namespace std;
using namespace Cti::Messaging::ActiveMQ;

static std::atomic<long> serverConnectionCount = 0;

/**
 * class constructor
 * @param listenerConnection reference to the listener connection
 * @param inQ queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of 1 sec iteration to allow the connection to send remaining messages
 */
CtiServerConnection::CtiServerConnection( const CtiListenerConnection &listenerConnection,
                                          Que_t *inQ,
                                          int termSeconds ) :
    CtiConnection( "Server Connection " + std::to_string(++serverConnectionCount), inQ, termSeconds ),
    _replyDest( listenerConnection.getClientReplyDest() )
{
    CTILOG_DEBUG( dout, who() << " - CtiServerConnection::CtiServerConnection() @0x" << std::hex << this );

    if( inQ )
    {
        inQ->setName(listenerConnection.getServerQueueName());
    }

    // use the same managed connection as the listener
    _connection = listenerConnection.getConnection();

    // set the outbound destination physical name
    if( _replyDest.get())
    {
        _peerName = destPhysicalName( *_replyDest );
    }

    setName(listenerConnection.getServerQueueName()+_peerName);

    // consider server connections valid by default
    _valid = true;
}

/**
 * class destructor
 */
CtiServerConnection::~CtiServerConnection()
{
    CTILOG_DEBUG( dout, who() << "- CtiServerConnection::~CtiServerConnection() @0x" << std::hex << this );

    try
    {
        cleanConnection();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - error cleaning the connection.");
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
            CTILOG_WARN(dout, who() << " - timeout while waiting for client acknowledge message");
            return false;
        }

        if( ackMessage->getCMSType() != MessageType::clientAck )
        {
            CTILOG_ERROR(dout, who() << " - unexpected message type: \"" << ackMessage->getCMSType() << "\"");
            return false;
        }

        if( ! ackMessage->getCMSReplyTo() )
        {
            CTILOG_ERROR(dout, who() << " - received null ReplyTo destination, expected: " << _producer->getDestPhysicalName() );
            return false;
        }

        if( destPhysicalName(*ackMessage->getCMSReplyTo()) != _producer->getDestPhysicalName() )
        {
            CTILOG_ERROR(dout, who() << " - received invalid ReplyTo destination: " << destPhysicalName(*ackMessage->getCMSReplyTo())
                    << ", expected: " << _producer->getDestPhysicalName() );

            return false;
        }

        _consumer->setMessageListener( _messageListener.get() );

        CTILOG_INFO(dout, who() << " - successfully connected"
                << "\ninbound  : " << _consumer->getDestPhysicalName()
                << "\noutbound : " << _producer->getDestPhysicalName()
                );

        return true;
    }
    catch( cms::CMSException& e )
    {
        forceTermination();

        CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - Failed to establish connection");

        return false;
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Failed to establish connection");

        throw;
    }
}
