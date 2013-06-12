/*-----------------------------------------------------------------------------*
*
* File:   connection
*
* Date:   5/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/connection.cpp-arc  $
* REVISION     :  $Revision: 1.45.10.1 $
* DATE         :  $Date: 2008/11/13 17:23:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "activemq/core/ActiveMQConnection.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "amq_util.h"


volatile long CtiClientConnection::_clientConnectionCount = 0;


CtiClientConnection::CtiClientConnection( const string &serverQueueName,
                                          Que_t *inQ,
                                          INT tt ) :
    _brokerUri( Cti::Messaging::ActiveMQ::Broker::startupReconnectURI ),
    _serverQueueName( serverQueueName ),
    CtiConnection( string( "Client Connection " ) + CtiNumStr( InterlockedIncrement( &_clientConnectionCount )), inQ, tt ),
    _brokerConnStarted( false )
{
    // create exception listener and register function and caller
    _exceptionListener.reset( new Cti::Messaging::ActiveMQ::ExceptionListener<CtiClientConnection>( this, &CtiClientConnection::onException ));
}


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


void CtiClientConnection::onException( const cms::CMSException& ex )
{
    _valid = FALSE;

    logException( __FILE__, __LINE__, typeid(ex).name(), ex.getMessage() );
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
void CtiClientConnection::brokerConnThread()
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
bool CtiClientConnection::startBrokerConnection()
{
    _brokerConnStarted = false;

    // Create connection
    _connection.reset( Cti::Messaging::ActiveMQ::g_connectionFactory.createConnection( _brokerUri ));

    // create and start thread
    {
        boost::lock_guard<boost::mutex> guard(_brokerConnMutex);

        if( _dontReconnect )
        {
            return false;
        }

        _brokerConnThread.reset( new boost::thread( &CtiClientConnection::brokerConnThread , this ));
    }

    _brokerConnThread->join();

    return _brokerConnStarted;
}


INT CtiClientConnection::establishConnection()
{
    const long receiveMillis    = 30000;
    const long timeToLiveMillis = receiveMillis - 1000;
    const long maxMessageCount  = 120;

    BOOL initialized = FALSE;

    long messageCount = 0;

    try
    {
        while( !_valid && !_dontReconnect )
        {
            try
            {
                if( !initialized )
                {
                    logStatus( __FUNCTION__, "connecting to \"" + _serverQueueName + "\"" );

                    // cleanup inbound destination
                    _destIn.reset();

                    // Create and start connection to broker
                    if( !startBrokerConnection() )
                    {
                        if( !_dontReconnect )
                        {
                            logStatus( __FUNCTION__, "unable to connect to the broker. Will try to reconnect." );
                        }
                        else
                        {
                            logStatus( __FUNCTION__, "has closed." );
                        }
                        continue;
                    }

                    // Create sessions
                    _sessionIn.reset( _connection->createSession() );
                    _sessionOut.reset( _connection->createSession() );

                    messageCount = 0;

                    initialized = TRUE;

                    logStatus( __FUNCTION__, "connected to the broker." );
                }

                // explicitly destroy the inbound destination with the provider
                destroyDestIn();

                // Create inbound destination
                _destIn.reset( _sessionIn->createTemporaryQueue() );

                // Create consumer for inbound traffic
                _consumer.reset( _sessionIn->createConsumer( _destIn.get() ));

                // create a temporary producer to initiate talk with the server
                auto_ptr<cms::MessageProducer> tmpProducer( _sessionOut->createProducer( _sessionOut->createQueue( _serverQueueName )));
                tmpProducer->setDeliveryMode( cms::DeliveryMode::NON_PERSISTENT );
                tmpProducer->setTimeToLive  ( timeToLiveMillis );

                // create an empty message
                auto_ptr<cms::Message> messageToSend( _sessionOut->createMessage() );
                messageToSend->setCMSReplyTo( _destIn.get() );
                messageToSend->setCMSType   ( Cti::Messaging::ActiveMQ::MessageType::clientInit );

                // send first message to request connection
                tmpProducer->send( messageToSend.get() );

                if( messageCount == 0 )
                {
                    logStatus( __FUNCTION__, "waiting for server reply." );
                }

                // We should block here until the delay expires or until the connection is closed
                auto_ptr<cms::Message> msg( _consumer->receive( receiveMillis ));

                if( msg.get() )
                {
                    if( msg->getCMSType() != Cti::Messaging::ActiveMQ::MessageType::serverResp )
                    {
                        initialized = FALSE; // something went wrong?

                        logStatus( __FUNCTION__, "unexpected message: \"" + msg->getCMSType() + "\"" );
                    }
                    else if( msg->getCMSReplyTo() == NULL )
                    {
                        initialized = FALSE; // something went wrong?

                        logStatus( __FUNCTION__, "received NULL ReplyTo destination." );
                    }
                    else
                    {
                        _destOut.reset( msg->getCMSReplyTo()->clone() );

                        _valid = TRUE;

                        logStatus( __FUNCTION__, "is valid.\n"
                                "inbound destination: " + Cti::Messaging::ActiveMQ::destPhysicalName( *_destIn ) + "\n"
                                "outbound destination: " + Cti::Messaging::ActiveMQ::destPhysicalName( *_destOut ));
                    }
                }
                else if( ++messageCount == maxMessageCount )
                {
                    // if we reach the maximum number of attempt, reconnect
                    initialized = FALSE;
                }
            }
            catch( cms::CMSException& e )
            {
                initialized = FALSE;

                logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );

                Sleep( 1000 ); // Don't pound the system....
            }

            checkCancellation();
        }

        if( initialized && _valid && !_dontReconnect )
        {
            // Create advisory consumer destination
            _advisoryDest.reset( _sessionOut->createTopic( "ActiveMQ.Advisory.Consumer.Queue." + Cti::Messaging::ActiveMQ::destPhysicalName( *_destOut )));

            // Create advisory consumer
            _advisoryConsumer.reset( _sessionOut->createConsumer( _advisoryDest.get(), "consumerCount <> 1" ));
            _advisoryConsumer->setMessageListener( _advisoryListener.get() );

            // Create producer for outbound traffic
            _producer.reset( _sessionOut->createProducer( _destOut.get() ) );
            _producer->setDeliveryMode( cms::DeliveryMode::NON_PERSISTENT );

            // set listeners
            _connection->setExceptionListener( _exceptionListener.get() );
            _consumer->setMessageListener( _messageListener.get() );

            // send client registration
            writeRegistration();

            return NORMAL;
        }
    }
    catch( cms::CMSException& e )
    {
        _valid = FALSE;

        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
    }
    catch(...)
    {
        _valid = FALSE;

        logException( __FILE__, __LINE__ );

        throw;
    }

    return NOTNORMAL;
}


void CtiClientConnection::endConnection()
{
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

    // Close the connection as well as any child session, consumer, producer
    if( _connection.get() )
    {
        _connection->close();
    }
}


void CtiClientConnection::cleanUp()
{
    // destroy inbound temporary queue
    try
    {
        destroyDestIn();
    }
    catch(...)
    {
        // since we are shutting down, we dont care about exceptions
    }

    // Closes the connection as well as any child session, consumer, producer
    _connection.reset();
}


void CtiClientConnection::writeRegistration()
{
    try
    {
        if( _regMsg.get() ) // I know who I am....
        {
            logStatus( __FUNCTION__, "re-registering connection." );

            sendMessage( *_regMsg );

            if( _ptRegMsg.get() ) // I know who I am....
            {
                sendMessage( *_ptRegMsg );
            }
        }
    }
    catch( cms::CMSException& e )
    {
        _valid = FALSE; //sending data failed

        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
    }
}


void CtiClientConnection::recordRegistration( const CtiMessage& msg )
{
    _regMsg.reset( dynamic_cast<CtiRegistrationMsg*>( msg.replicateMessage() ));
}


void CtiClientConnection::recordPointRegistration( const CtiMessage& msg )
{
    _ptRegMsg.reset( dynamic_cast<CtiPointRegistrationMsg*>( msg.replicateMessage() ));
}


// This method examines the message and records anything we care about
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
