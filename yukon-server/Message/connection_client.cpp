#include "precompiled.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "activemq/core/ActiveMQConnection.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "amq_util.h"

using namespace Cti::Messaging::ActiveMQ;

namespace { // anonymous

template <typename MuxType>
struct InsideScope
{
    MuxType &_mux;
    bool    &_flag;

    InsideScope( MuxType& mux, bool &flag )
        :   _mux(mux),
            _flag(flag)
    {
        CtiLockGuard<MuxType> lock(_mux);
        _flag = true;
    }

    ~InsideScope()
    {
        CtiLockGuard<MuxType> lock(_mux);
        _flag = false;
    }
};

} // anonymous


volatile long CtiClientConnection::_clientConnectionCount = 0;

/**
 * class constructor
 * @param serverQueueName name of the queue to establish a connection with
 * @param inQ inbound queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of 1 sec iteration to allow the connection to send remaining messages
 */
CtiClientConnection::CtiClientConnection( const string &serverQueueName,
                                          Que_t *inQ,
                                          int termSeconds ) :
    CtiConnection( string( "Client Connection " ) + CtiNumStr( InterlockedIncrement( &_clientConnectionCount )), inQ, termSeconds ),
    _serverQueueName( serverQueueName ),
    _canAbortConn( false )
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
    if( _valid )
    {
        _valid = false;

        // interrupt the current or the next getQueue() call
        _outQueue.interruptRead();
    }

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
    InsideScope<CtiMutex> insideEstablishConn(_abortConnMux, _canAbortConn);

    const long receiveMillis    = 1000 * 60 * 60;  // 1 hour
    const long timeToLiveMillis = 1000 * 60 * 120; // 2 hours

    try
    {
        while( !_valid && !_dontReconnect )
        {
            try
            {
                {
                    CtiLockGuard<CtiMutex> lock(_abortConnMux);

                    if( _dontReconnect )
                    {
                        return false;
                    }

                    // clean up activemq objects before resetting the connection
                    CtiConnection::releaseResources();

                    _connection.reset( new ManagedConnection( Broker::flowControlURI ));
                }

                logStatus( __FUNCTION__, "connecting to \"" + _serverQueueName + "\"."
                                         "\nbroker URI: \"" + _connection->getBrokerUri() + "\"" );

                // start connection to the broker, throws ConnectionException
                _connection->start();

                logDebug( __FUNCTION__, "connected to the broker" );

                _sessionIn.reset( _connection->createSession() );
                _sessionOut.reset( _connection->createSession() );

                // create a temporary producer to initiate talk with the server`s listener connection
                QueueProducer handshakeProducer(*_sessionOut, _sessionOut->createQueue( _serverQueueName ));
                handshakeProducer.setTimeToLiveMillis( timeToLiveMillis );

                // Create consumer for inbound traffic
                _consumer.reset( createTempQueueConsumer( *_sessionIn ));

                while( !_valid && !_dontReconnect && _connection->verifyConnection() )
                {
                    // create an empty message for handshake
                    auto_ptr<cms::Message> outMessage( _sessionOut->createMessage() );

                    outMessage->setCMSReplyTo( _consumer->getDestination() );
                    outMessage->setCMSType( MessageType::clientInit );

                    handshakeProducer.send( outMessage.get() );

                    logDebug( __FUNCTION__, "waiting for server reply." );

                    // We should block here until the delay expires or until the connection is closed
                    auto_ptr<cms::Message> inMessage( _consumer->receive( receiveMillis ));

                    if( inMessage.get() )
                    {
                        if( inMessage->getCMSType() != MessageType::serverResp )
                        {
                            logStatus( __FUNCTION__, "unexpected message: \"" + inMessage->getCMSType() + "\"" );

                            break; // something went wrong? - retry connecting from scratch
                        }

                        if( ! inMessage->getCMSReplyTo() )
                        {
                            logStatus( __FUNCTION__, "received NULL ReplyTo destination." );

                            break; // something went wrong? - retry connecting from scratch
                        }

                        _producer.reset( createDestinationProducer( *_sessionOut, inMessage->getCMSReplyTo() ));

                        resetPeer( _producer->getDestPhysicalName() );

                        _valid = true;

                        logStatus( __FUNCTION__, "successfully connected.\n"
                                                 "inbound destination  : " + _consumer->getDestPhysicalName() + "\n"
                                                 "outbound destination : " + _producer->getDestPhysicalName());
                    }
                    else
                    {
                        logStatus( __FUNCTION__, "timeout while trying to connect to \"" + _serverQueueName + "\". reconnecting." );

                        // check for thread interruption before re-sending a handshake message
                        checkInterruption();
                    }
                }
            }
            catch( cms::CMSException& e )
            {
                if( !_dontReconnect )
                {
                    logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );

                    Sleep(1000); // Don't pound the system....
                }
            }
            catch( ConnectionException& e )
            {
                if( !_dontReconnect )
                {
                    logStatus( __FUNCTION__, "unable to connect to the broker at \"" + _connection->getBrokerUri() + "\". reconnecting." );
                }
            }

            // check for thread interruption after each connection attempt
            checkInterruption();
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

        // create and send client acknowledge message
        auto_ptr<cms::Message> ackMessage( _sessionOut->createMessage() );
        ackMessage->setCMSReplyTo( _consumer->getDestination() );
        ackMessage->setCMSType( MessageType::clientAck );

        _producer->send( ackMessage.get() );

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
 * Abort the connection attempt and disable reconnection
 */
void CtiClientConnection::abortConnection()
{
    try
    {
        CtiLockGuard<CtiMutex> lock(_abortConnMux);

        _dontReconnect = true;

        if( _connection && _canAbortConn )
        {
            // Close the connection as well as any child session, consumer, producer
            _connection->close();
        }
    }
    catch(...)
    {
        // since we are shutting down, we dont care about exceptions
    }
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
        switch( msg.isA() )
        {
            case MSG_REGISTER:
            {
                recordRegistration( msg );
                break;
            }
            case MSG_POINTREGISTRATION:
            {
                recordPointRegistration( msg );
                break;
            }
            case MSG_MULTI:
            {
                const CtiMultiMsg& pMulti = dynamic_cast<const CtiMultiMsg&>( msg );

                for(int i = 0; i < pMulti.getCount() && i < 3; i++)    // Only look at the first three entries
                {
                    messagePeek( *pMulti.getData()[i] );               // recurse.
                }
                break;
            }
        }
    }
    catch( bad_cast )
    {
        logException( __FILE__, __LINE__ );
    }
}
