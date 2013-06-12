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

#include "connection_server.h"
#include "amq_constants.h"
#include "amq_util.h"


volatile long CtiServerConnection::_serverConnectionCount = 0;


CtiServerConnection::CtiServerConnection( CtiListenerConnection& listenerConnection,
                                          Que_t *inQ,
                                          INT tt ) :
    CtiConnection( string( "Server Connection " ) + CtiNumStr( InterlockedIncrement( &_serverConnectionCount )), inQ, tt )
{
    _sessionIn  = listenerConnection.createSession();
    _sessionOut = listenerConnection.createSession();
    _destOut    = listenerConnection.getClientReplyDest();
    _valid      = TRUE;
}


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


INT CtiServerConnection::establishConnection()
{
    try
    {
        // Create advisory consumer destination
        _advisoryDest.reset( _sessionOut->createTopic( "ActiveMQ.Advisory.Consumer.Queue." + Cti::Messaging::ActiveMQ::destPhysicalName( *_destOut )));

        // Create advisory consumer
        _advisoryConsumer.reset( _sessionOut->createConsumer( _advisoryDest.get(), "consumerCount <> 1" ));
        _advisoryConsumer->setMessageListener( _advisoryListener.get() );

        // Create inbound destination
        _destIn.reset( _sessionIn->createTemporaryQueue() );

        // Create consumer for inbound traffic
        _consumer.reset( _sessionIn->createConsumer( _destIn.get() ));
        _consumer->setMessageListener( _messageListener.get() );

        // if this is a server connection create a message producer for outbound traffic
        _producer.reset( _sessionOut->createProducer( _destOut.get() ));
        _producer->setDeliveryMode( cms::DeliveryMode::NON_PERSISTENT );

        // create a new message and send it to the client
        auto_ptr<cms::Message> msg( _sessionOut->createMessage() );

        // set reply to info to the inbound temporary queue
        msg->setCMSReplyTo( _destIn.get() );
        msg->setCMSType( Cti::Messaging::ActiveMQ::MessageType::serverResp );

        // send the message back to the client (the payload is the reply destination)
        _producer->send( msg.get() );

        _dontReconnect = TRUE;

        logStatus( __FUNCTION__, "is valid.\n"
                "inbound destination: " + Cti::Messaging::ActiveMQ::destPhysicalName( *_destIn ) + "\n"
                "outbound destination: " + Cti::Messaging::ActiveMQ::destPhysicalName( *_destOut ));
    }
    catch( cms::CMSException& e )
    {
        forceTermination();

        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );

        return NOTNORMAL;
    }
    catch( ... )
    {
        logException( __FILE__, __LINE__ );

        throw;
    }

    return NORMAL;
}


void CtiServerConnection::endConnection()
{
    // server connection sessions, consumer and producers are destroy in cleanUp()
}


void CtiServerConnection::cleanUp()
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

    // Closes the session as well as any active child consumers or producers.
    _sessionIn.reset();
    _sessionOut.reset();
}

