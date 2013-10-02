#include "precompiled.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "activemq/core/ActiveMQConnection.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "amq_util.h"

using namespace Cti::Messaging::ActiveMQ;


volatile long CtiClientConnection::_clientConnectionCount = 0;

/**
 * class constructor
 * @param serverQueueName name of the queue to establish a connection with
 * @param inQ inbound queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of 1 sec iteration to allow the connection to send remaining messages
 */
CtiClientConnection::CtiClientConnection( const string &serverQueueName,
                                          Que_t *inQ,
                                          INT tt ) :
    CtiConnection( string( "Client Connection " ) + CtiNumStr( InterlockedIncrement( &_clientConnectionCount )), inQ, tt ),
    _serverQueueName( serverQueueName ),
    _connection( new ManagedConnection( Broker::flowControlURI ))
{
    // create exception listener and register function and caller
    _exceptionListener.reset( new ExceptionListener<CtiClientConnection>( this, &CtiClientConnection::onException ));
}

/**
 * class destructor
 */
CtiClientConnection::~CtiClientConnection()
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
 * connection exception listener function.
 * @param ex connection exception received
 */
void CtiClientConnection::onException( const cms::CMSException& ex )
{
    _valid = false;

    logException( __FILE__, __LINE__, typeid(ex).name(), ex.getMessage() );
}

/**
 * Establish a new connection while _valid and _dontReconnect remains false.
 * uses failover with exponential backoff to connect with the broker
 * afterwards, sends a handshake message every 30 sec to the server listener connection
 * @return true if the connection has been establish, false otherwise
 */
bool CtiClientConnection::establishConnection()
{
    const long receiveMillis    = 30000;
    const long timeToLiveMillis = receiveMillis - 1000;
    const long maxMessageCount  = 120;

    bool initialized = false;

    long messageCount = 0;

    auto_ptr<QueueProducer> handshakeProducer;

    try
    {
        while( !_valid && !_dontReconnect )
        {
            try
            {
                if( ! initialized )
                {
                    logStatus( __FUNCTION__, "connecting to \"" + _serverQueueName + "\"" );

                    // clean up activemq objects before resetting the connection
                    handshakeProducer.reset();
                    CtiConnection::deleteResources();

                    // reset and start connection to the broker, throws ConnectionException
                    _connection->start();

                    logDebug( __FUNCTION__, "connected to the broker" );

                    _sessionIn.reset( _connection->createSession() );
                    _sessionOut.reset( _connection->createSession() );

                    // create a temporary producer to initiate talk with the server`s listener connection
                    handshakeProducer.reset( new QueueProducer(*_sessionOut, _sessionOut->createQueue( _serverQueueName )));
                    handshakeProducer->setTimeToLive( timeToLiveMillis );

                    messageCount = 0;

                    initialized = true;
                }

                // Create consumer for inbound traffic
                _consumer.reset( createTempQueueConsumer( *_sessionIn ));

                // create an empty message for handshake
                auto_ptr<cms::Message> outMessage( _sessionOut->createMessage() );

                outMessage->setCMSReplyTo( _consumer->getDestination() );
                outMessage->setCMSType( MessageType::clientInit );

                handshakeProducer->send( outMessage.get() );

                if( messageCount == 0 )
                {
                    logDebug( __FUNCTION__, "waiting for server reply." );
                }

                // We should block here until the delay expires or until the connection is closed
                auto_ptr<cms::Message> inMessage( _consumer->receive( receiveMillis ));

                if( inMessage.get() )
                {
                    if( inMessage->getCMSType() != MessageType::serverResp )
                    {
                        initialized = false; // something went wrong?

                        logStatus( __FUNCTION__, "unexpected message: \"" + inMessage->getCMSType() + "\"" );
                    }
                    else if( ! inMessage->getCMSReplyTo() )
                    {
                        initialized = false; // something went wrong?

                        logStatus( __FUNCTION__, "received NULL ReplyTo destination." );
                    }
                    else
                    {
                        _producer.reset( createDestinationProducer( *_sessionOut, inMessage->getCMSReplyTo() ));

                        _valid = true;

                        logStatus( __FUNCTION__, "successfully connected.\n"
                                "inbound destination  : " + _consumer->getDestPhysicalName() + "\n"
                                "outbound destination : " + _producer->getDestPhysicalName());
                    }
                }
                else if( ++messageCount == maxMessageCount )
                {
                    // if we reach the maximum number of attempt, reconnect
                    initialized = false;
                }
            }
            catch( cms::CMSException& e )
            {
                initialized = false;

                logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );

                Sleep( 1000 ); // Don't pound the system....
            }
            catch( ConnectionException& e )
            {
                if( !_dontReconnect )
                {
                    logStatus( __FUNCTION__, "unable to connect to the broker. Will try to reconnect." );
                }
            }

            checkCancellation();
        }

        if( _dontReconnect )
        {
            logStatus( __FUNCTION__, "has closed." );
            return false;
        }

        // set exception listener
        _connection->setExceptionListener( _exceptionListener.get() );

        // Create advisory topic consumer
        setupAdvisoryListener();

        // set message listener
        _consumer->setMessageListener( _messageListener.get() );

        // send client registration
        writeRegistration();

        return true;
    }
    catch( cms::CMSException& e )
    {
        _valid = false;

        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );

        return false;
    }
    catch(...)
    {
        _valid = false;

        logException( __FILE__, __LINE__ );

        throw;
    }
}

/**
 * Terminate an ongoing connection thread and closes the connection
 */
void CtiClientConnection::endConnection()
{
    if( _connection.get() )
    {
        // Close the connection as well as any child session, consumer, producer
        _connection->close();
    }
}

/**
 * clean up consumer, producer, destinations, sessions and the cms connection
 */
void CtiClientConnection::deleteResources()
{
    CtiConnection::deleteResources();

    // Close and delete the connection
    _connection.reset();
}

/**
 * send registration messages, if the connection was previously registered
 */
void CtiClientConnection::writeRegistration()
{
    try
    {
        if( _regMsg.get() ) // I know who I am....
        {
            logDebug( __FUNCTION__, "re-registering connection." );

            sendMessage( *_regMsg );

            if( _ptRegMsg.get() ) // I know who I am....
            {
                sendMessage( *_ptRegMsg );
            }
        }
    }
    catch( cms::CMSException& e )
    {
        _valid = false; //sending data failed

        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
    }
}

/**
 * copy and save a registration message
 * @param msg reference to the message to save
 */
void CtiClientConnection::recordRegistration( const CtiMessage& msg )
{
    _regMsg.reset( dynamic_cast<CtiRegistrationMsg*>( msg.replicateMessage() ));
}

/**
 * copy and save a point registration message
 * @param msg reference to the message to save
 */
void CtiClientConnection::recordPointRegistration( const CtiMessage& msg )
{
    _ptRegMsg.reset( dynamic_cast<CtiPointRegistrationMsg*>( msg.replicateMessage() ));
}

/**
 *  This method examines the message and records anything we care about
 * @param msg reference to the message to peek into
 */
void CtiClientConnection::messagePeek( const CtiMessage& msg )
{
    try
    {
        if( msg.isA() == MSG_REGISTER )
        {
            recordRegistration( msg );
        }
        else if( msg.isA() == MSG_POINTREGISTRATION )
        {
            recordPointRegistration( msg );
        }
        else if( msg.isA() == MSG_MULTI )
        {
            const CtiMultiMsg& pMulti = dynamic_cast<const CtiMultiMsg&>( msg );

            for(int i = 0; i < pMulti.getCount() && i < 3; i++)    // Only look at the first three entries
            {
                messagePeek( *pMulti.getData()[i] );               // recurse.
            }
        }
    }
    catch( bad_cast )
    {
        logException( __FILE__, __LINE__ );
    }
}
