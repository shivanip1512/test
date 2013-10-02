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
                                          INT tt ) :
    CtiConnection( string( "Server Connection " ) + CtiNumStr( InterlockedIncrement( &_serverConnectionCount )), inQ, tt ),
    _connection( listenerConnection.getConnection() ),
    _replyDest( listenerConnection.getClientReplyDest() )
{
    setName( listenerConnection.getServerQueueName() );

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
    try
    {
        _dontReconnect = true;

        _sessionIn.reset( _connection->createSession() );
        _sessionOut.reset( _connection->createSession() );

        // Create consumer for inbound traffic
        _consumer.reset( createTempQueueConsumer( *_sessionIn ));
        _consumer->setMessageListener( _messageListener.get() );

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
