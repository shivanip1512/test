#include "precompiled.h"

#include "connection_client.h"
#include "dllbase.h"
#include "amq_constants.h"
#include "amq_util.h"
#include "GlobalSettings.h"

#include <activemq/core/ActiveMQConnection.h>
#include <decaf/lang/Thread.h>

#include <atomic>

using namespace std;
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


static std::atomic<long> clientConnectionCount = 0;

/**
 * class constructor
 * @param serverQueueName name of the queue to establish a connection with
 * @param inQ inbound queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of 1 sec iteration to allow the connection to send remaining messages
 */
CtiClientConnection::CtiClientConnection( const string &serverQueueName,
                                          Que_t *inQ,
                                          int termSeconds ) :
    CtiConnection( "Client Connection " + std::to_string(++clientConnectionCount), inQ, termSeconds ),
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
    CTILOG_DEBUG( dout, who() << "CtiClientConnection::~CtiClientConnection()" );

    try
    {
        cleanConnection();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() << " - error cleaning the connection");
    }
}

/**
 * connection exception listener function.
 * @param ex connection exception received
 */
void CtiClientConnection::onException( const cms::CMSException& e )
{
    if( _valid )
    {
        _valid = false;

        // interrupt the current or the next getQueue() call
        _outQueue.interruptBlockingRead();
    }

    CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - caught exception");
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
        while( !_valid && canReconnect() )
        {
            try
            {
                {
                    CTILOCKGUARD(CtiMutex, lock, _abortConnMux);

                    if( ! canReconnect() )
                    {
                        return false;
                    }

                    // clean up activemq objects before resetting the connection
                    CtiConnection::releaseResources();

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

                CTILOG_INFO(dout, who() << " - connecting to \"" << _serverQueueName << "\"\n"
                        << "broker URI: \"" << _connection->getBrokerUri() << "\"" );

                // start connection to the broker, throws ConnectionException
                _connection->start();

                if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
                {
                    CTILOG_DEBUG(dout, who() << " - connected to the broker");
                }

                _sessionIn  = _connection->createSession();
                _sessionOut = _connection->createSession();

                // create a temporary producer to initiate talk with the server`s listener connection
                QueueProducer handshakeProducer(*_sessionOut, _sessionOut->createQueue( _serverQueueName ));
                handshakeProducer.setTimeToLiveMillis( timeToLiveMillis );

                // Create consumer for inbound traffic
                _consumer = createTempQueueConsumer( *_sessionIn );

                while( !_valid && canReconnect() && _connection->verifyConnection() )
                {
                    // create an empty message for handshake
                    unique_ptr<cms::Message> outMessage( _sessionOut->createMessage() );

                    outMessage->setCMSReplyTo( _consumer->getDestination() );
                    outMessage->setCMSType( MessageType::clientInit );

                    handshakeProducer.send( outMessage.get() );

                    if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
                    {
                        CTILOG_DEBUG(dout, who() << " - waiting for server reply.");
                    }

                    // We should block here until the delay expires or until the connection is closed
                    unique_ptr<cms::Message> inMessage( _consumer->receive( receiveMillis ));

                    if( inMessage.get() )
                    {
                        if( inMessage->getCMSType() != MessageType::serverResp )
                        {
                            CTILOG_ERROR(dout, who() << " - unexpected message: \"" << inMessage->getCMSType() << "\"");
                            break; // something went wrong? - retry connecting from scratch
                        }

                        if( ! inMessage->getCMSReplyTo() )
                        {
                            CTILOG_ERROR(dout, who() << " - received NULL ReplyTo destination.");
                            break; // something went wrong? - retry connecting from scratch
                        }

                        _producer = createDestinationProducer( *_sessionOut, inMessage->getCMSReplyTo() );

                        resetPeer( _producer->getDestPhysicalName() );

                        _valid = true;

                        CTILOG_INFO(dout, who() << " - successfully connected.\n"
                                << "inbound: "    << _consumer->getDestPhysicalName()
                                << ", outbound: " << _producer->getDestPhysicalName());
                    }
                    else
                    {
                        CTILOG_WARN(dout, who() << " - timeout while trying to connect to \"" << _serverQueueName << "\". reconnecting..");

                        // check for thread interruption before re-sending a handshake message
                        checkInterruption();
                    }
                }
            }
            catch( cms::CMSException& e )
            {
                if( canReconnect() )
                {
                    CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - caught CMS exception while trying to establish connection");

                    Sleep(1000); // Don't pound the system....
                }
            }
            catch( ConnectionException& e )
            {
                if( canReconnect() )
                {
                    CTILOG_EXCEPTION_WARN(dout, e, who() <<" - unable to connect to the broker at \"" << _connection->getBrokerUri() << "\", reconnecting..");
                }
            }

            // check for thread interruption after each connection attempt
            checkInterruption();
        }

        if( ! canReconnect() )
        {
            CTILOG_INFO(dout, who() << " - has closed.");
            return false;
        }

        // set exception listener
        _connection->setExceptionListener( _exceptionListener.get() );

        // Create advisory topic consumer
        setupAdvisoryListener();

        // set message listener
        _consumer->setMessageListener( _messageListener.get() );

        // create and send client acknowledge message
        unique_ptr<cms::Message> ackMessage( _sessionOut->createMessage() );
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

        CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - caught CMS exception while trying to establish connection");

        return false;
    }
    catch (boost::thread_interrupted &)
    {
        CTILOG_INFO(dout, who() << " - connection interrupted.");
        return false;
    }
    catch(...)
    {
        _valid = false;

        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - caught unexpected exception while trying to establish connection");

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
        CTILOCKGUARD(CtiMutex, lock, _abortConnMux);

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
            if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
            {
                CTILOG_DEBUG(dout, who() << " - re-registering connection.");
            }

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

        CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - send registration message has failed");
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
    catch( const bad_cast& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - multi-message cast has failed");
    }
}
