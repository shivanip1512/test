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

#include <limits.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/toolpro/neterr.h>
#include <rw\thr\mutex.h>

#include "boost/algorithm/string/trim.hpp"
#include "collectable.h"
#include "connection.h"
#include "message.h"
#include "numstr.h"
#include "dlldefs.h"
#include "logger.h"

#include <activemq/core/ActiveMQConstants.h>
#include <activemq/commands/ActiveMQMessage.h>
#include <activemq/commands/DestinationInfo.h>
#include <decaf/lang/exceptions/ClassCastException.h>

using namespace decaf::lang::exceptions;

#include "amq_util.h"
#include "message_factory.h"

using Cti::Messaging::Serialization::MessagePtr;

CtiConnection::CtiConnection( const string& title, Que_t *inQ, INT tt ) :
    _inQueue(inQ),
    _termTime(tt),
    _flag(0),
    _title(title)
{
    // create message listener and register function and caller
    _messageListener.reset( new Cti::Messaging::ActiveMQ::MessageListener<CtiConnection>( this, &CtiConnection::onMessage ));
    // create advisory message listener and register function and caller
    _advisoryListener.reset( new Cti::Messaging::ActiveMQ::MessageListener<CtiConnection>( this, &CtiConnection::onAdvisoryMessage ));
}


CtiConnection::~CtiConnection()
{
}


void CtiConnection::start()
{
    if( !_connectCalled )
    {
        _connectCalled = TRUE;

        if( _localQueueAlloc && _inQueue != NULL )
        {
            delete _inQueue;
            _inQueue = NULL;
        }

        if( _inQueue == NULL )
        {
            _inQueue         = CTIDBG_new Que_t;
            _localQueueAlloc = TRUE;
        }

        ThreadInitiate();
    }
}


void CtiConnection::ThreadInitiate()
{
    try
    {
        outthread = rwMakeThreadFunction( *this, &CtiConnection::OutThread );
        outthread.start();

        const INT ConnStatus = verifyConnection();

        if( ConnStatus != NORMAL )
        {
            logStatus( __FUNCTION__, "connection has error status: " + CtiNumStr(ConnStatus));
        }
    }
    catch(const RWxmsg& x)
    {
        logException( __FILE__, __LINE__, typeid(x).name(), x.why() );
    }
}


void CtiConnection::OutThread()
{
    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_HIGHEST);

    if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
    {
        logStatus( __FUNCTION__, "has begun operations TID: " + CtiNumStr(CurrentTID()));
    }

    // use the title and remove all white spaces to set the thread name
    string threadName = boost::trim_copy( _title ) + "OutThread";
    SetThreadName( -1, threadName.c_str() );

    try
    {
        for(;!_bQuit;)
        {
            if( !_bConnected )
            {
                if( establishConnection() != NORMAL )
                {
                    _bQuit = _dontReconnect;
                    Sleep(1000); // No runnaway loops ok...
                    continue;
                }

                _bConnected = TRUE;
            }
            else if( _valid )
            {
                if( !_outMessage )
                {
                    try
                    {
                        _outMessage.reset( _outQueue.getQueue( 1000 ));
                    }
                    catch(...)
                    {
                        logException( __FILE__, __LINE__ );
                    }
                }

                if( !_outMessage )
                {
                    // I need to look for a CANCELATION every second
                    checkCancellation();
                }
                else if( _valid )
                {
                    // peek into the message to save registering information (in case we need to reconnect)
                    messagePeek( *_outMessage );

                    try
                    {
                        sendMessage( *_outMessage );

                        _outMessage.reset();
                    }
                    catch( cms::CMSException& e )
                    {
                        _valid = FALSE; //sending data failed

                        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
                    }
                }
            }
            else
            {
                // if the connection becomes invalid, flush the inbound queue :
                // we should try to read all pending messages from the temporary inbound queue
                flushInDestination();

                _bConnected = FALSE;
                _bQuit      = _dontReconnect;
            }
        }
    }
    catch(...)
    {
        _valid = FALSE;

        logException( __FILE__, __LINE__);

        throw; // Let the higher powers handle this crap!
    }

    forceTermination();

    //if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
    //{
        logStatus( __FUNCTION__, "is terminating...." );
    //}
}


void CtiConnection::sendMessage( const CtiMessage& msg )
{
    vector<unsigned char> obytes;

    // serialize message
    const string msgType = Cti::Messaging::Serialization::g_messageFactory.serialize( msg, obytes );

    if( msgType.empty() )
    {
        logStatus( __FUNCTION__, string("message \"") + typeid(msg).name() + "\" not registered with factory." );
        return;
    }

    // create a new cms bytes message
    auto_ptr<cms::BytesMessage> bytes_msg( _sessionOut->createBytesMessage() );

    bytes_msg->writeBytes( obytes );
    bytes_msg->setCMSType( msgType );

    // send the message
    _producer->send( bytes_msg.get() );
}


void CtiConnection::flushInDestination()
{
    try
    {
        // disconnect asynchronous message listener
        _consumer->setMessageListener( NULL );

        bool bEndLoop = false;

        while( !bEndLoop )
        {
            // receive with a timeout of 1sec and loop until message is null.
            // At that point we assume that the inBound queue is empty.
            auto_ptr<cms::Message> msg( _consumer->receive( 1000 ));

            if( msg.get() != NULL )
            {
                onMessage( msg.get() );
            }
            else
            {
                bEndLoop = true;
            }
        }
    }
    catch( cms::CMSException& e )
    {
        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
    }
}


void CtiConnection::onMessage( const cms::Message* message )
{
    MessagePtr<CtiMessage>::type omsg;

    // deserialize received message
    if( const cms::BytesMessage* bytes_msg = dynamic_cast<const cms::BytesMessage*>(message) )
    {
        vector<unsigned char> ibytes( bytes_msg->getBodyLength() );

        bytes_msg->readBytes( ibytes );

        // deserialize the message
        omsg = Cti::Messaging::Serialization::g_messageFactory.deserialize( message->getCMSType(), ibytes );
    }

    // check for any deserialize failure
    if( !omsg.get() )
    {
        _valid = FALSE;
        logStatus( __FUNCTION__, "message: \"" + message->getCMSType() + "\" cannot be deserialized." );
        return;
    }

    // Pee on this message to mark some teritory...
    omsg->setConnectionHandle( (void*)this );

    // write incoming message to _inQueue
    if( _inQueue )
    {
        try
        {
            if( _inQueue->isFull() )
            {
                logStatus( __FUNCTION__, "queue is full. Will BLOCK. It allows " + CtiNumStr(_inQueue->size()) + " entries." );
            }
        }
        catch(...)
        {
            logException( __FILE__, __LINE__ );
        }

        // Refresh the time...
        _lastInQueueWrite = _lastInQueueWrite.now();

        writeIncomingMessageToQueue( omsg.release() );
    }
    else
    {
        logStatus( __FUNCTION__, "_inQueue is NULL.");
    }
}


void CtiConnection::onAdvisoryMessage( const cms::Message* message )
{
    if( message->getCMSType() == "Advisory" )
    {
        // selector is already checking this condition, do we need this check?
        //if( message->getStringProperty( "consumerCount" ) != "1" )
        //{
            _valid = FALSE;
        //}
    }
}


void CtiConnection::cleanConnection()
{
    try
    {
        close();
    }
    catch(...)
    {
        logException( __FILE__, __LINE__ );
    }

    if( _localQueueAlloc && _inQueue != NULL )
    {
        try
        {
            _inQueue->clearAndDestroy(); // The queue is allocated by me.  I will be responsible for this memory!
            delete _inQueue;
        }
        catch(...)
        {
            logException( __FILE__, __LINE__ );
        }
        _inQueue = 0;
    }

    try
    {
        _outQueue.clearAndDestroy();
    }
    catch(...)
    {
        logException( __FILE__, __LINE__, "", "error cleaning the outbound queue for connection." );
    }
}


void CtiConnection::close()
{
    logStatus( __FUNCTION__, "closing connection." );

    try
    {
        if( _connectCalled )
        {
            {
                CtiLockGuard< CtiMutex > guard(_mux, 60000);
                // 1.  We want the outQueue/OutThread to flush itself.
                //     I allow _termTime seconds for this to occur

                int sleep_nbr = 0;

                while(( _outQueue.entries() > 0 || _outMessage ) && ( _valid || !_dontReconnect ))
                {
                    if(  _outQueue.entries() )
                    {
                        logStatus( __FUNCTION__, "waiting for outbound Queue to flush " + CtiNumStr(_outQueue.entries()) + " entries." );
                    }
                    else
                    {
                        logStatus( __FUNCTION__, "waiting for the last message to be sent." );
                    }

                    Sleep(1000);

                    if( sleep_nbr++ > _termTime )
                    {
                        break;
                    }
                }

                _outQueue.clearAndDestroy();  // Get rid of the evidence...

                // This flag tells OutThread not to attempt to reconnect....
                _valid         = FALSE;
                _dontReconnect = TRUE;
            }

            if( !_bConnected )
            {
                // if we are currently trying to establish a connection or if the thread has already ended:
                // 1.  abort the connection attempt
                // 2.  wait for the thread to end by itself

                try
                {
                    endConnection(); // close activemq connection or/and end sessions
                }
                catch(...)
                {
                    // since we are shutting down, we dont care about exceptions
                }

                if( outthread.join(100) == RW_THR_TIMEOUT )
                {
                    if( outthread.requestCancellation(2000) == RW_THR_TIMEOUT )
                    {
                        logStatus( __FUNCTION__, "OutThread refuses to cancel after 2 seconds." );
                    }
                    if( outthread.join(2000) == RW_THR_TIMEOUT )
                    {
                        logStatus( __FUNCTION__, "OutThread refuses to join after 2 seconds." );
                    }
                }
            }
            else
            {
                // if we are currently connected, it is possible we are currently sending a message or
                // we are flushing the inbound destination (or both)
                // 1.  wait for the thread to complete...
                // 2.  end the connection afterwards

                if( outthread.join(100) == RW_THR_TIMEOUT )
                {
                    if( outthread.requestCancellation(2000) == RW_THR_TIMEOUT )
                    {
                        logStatus( __FUNCTION__, "OutThread refuses to cancel after 2 seconds." );
                    }
                    if( outthread.join(2000) == RW_THR_TIMEOUT )
                    {
                        logStatus( __FUNCTION__, "OutThread refuses to join after 2 seconds." );
                    }
                }

                try
                {
                    endConnection(); // close activemq connection or/and end sessions
                }
                catch(...)
                {
                    // since we are shutting down, we dont care about exceptions
                }
            }

            if( _inQueue && _localQueueAlloc && _inQueue->entries() != 0 )
            {
                _inQueue->clearAndDestroy();      // Get rid of the evidence...
            }

            if(getDebugLevel() & DEBUGLEVEL_CONNECTION)
            {
                logStatus( __FUNCTION__, "has closed." );
            }
        }
    }
    catch(...)
    {
        logException( __FILE__, __LINE__ );
    }

}


int CtiConnection::WriteConnQue( CtiMessage *QEnt, unsigned timeout, bool cleaniftimedout )
{
    int status = NORMAL,
        verify;

    if( (verify = verifyConnection()) != NORMAL )
    {
        status = verify;

        logStatus( __FUNCTION__, "connection error (" + CtiNumStr(status) + "), message was NOT able to be queued." );

        // autopsy( __FILE__, __LINE__ );

        delete QEnt;
    }
    else
    {
        if( _outQueue.isFull() )
        {
            logStatus( __FUNCTION__, "queue is full. Will BLOCK." );
        }

        if( timeout > 0 )
        {
            if( !_outQueue.putQueue( QEnt, timeout ) )
            {
                // WAS NOT QUEUED!!!

                logStatus( __FUNCTION__, "message was NOT able to be queued within " + CtiNumStr(timeout) + " millis" );

                if( cleaniftimedout ) delete QEnt;

                status = QUEUE_WRITE;
            }
        }
        else
        {
            _outQueue.putQueue( QEnt );
        }
    }

    return status;
}


CtiMessage* CtiConnection::ReadConnQue( UINT Timeout )
{
    CtiMessage *Msg = 0;

    if( _inQueue )
    {
        Msg = _inQueue->getQueue( Timeout );
        if( Msg != NULL )
        {
            Msg->setConnectionHandle( (void*)this );
        }
    }
    else if( Timeout )
    {
        Sleep( 250 );     // This prevents a crazy tight loop!
    }

    return Msg;
}


void CtiConnection::writeIncomingMessageToQueue( CtiMessage *msg )
{
    if( msg != NULL && _inQueue != NULL )
    {
        _inQueue->putQueue( msg );
    }
}


INT CtiConnection::verifyConnection()
{
    bool exep  = false;
    INT status = NORMAL;
    INT thread_status;

    try
    {
        if( _bQuit || _noLongerViable || (!_valid && _dontReconnect) )
        {
            status = NOTNORMAL;
        }
        else if( (thread_status = outthread.getCompletionState()) != RW_THR_PENDING )
        {
            if(getDebugLevel() & DEBUGLEVEL_CONNECTION)
            {
                logStatus( __FUNCTION__, "has exited with a completion state of " + CtiNumStr(thread_status) + "." + (_dontReconnect?"":" May restart."));
            }

            if( !_dontReconnect )
            {
                if( outthread.start() != RW_THR_PENDING )
                {
                    status = OutThreadTerminated; // the outthread has exited!
                }
            }
            else
            {
                status = OutThreadTerminated;
            }
        }

        if( status == OutThreadTerminated )
        {
            _outQueue.clearAndDestroy(); // the outthread has exited!
        }
    }
    catch(...)
    {
        exep = true;

        logException( __FILE__, __LINE__ );
    }

    if(exep) Sleep(5000);

    return status;
}


void CtiConnection::forceTermination()
{
    _bQuit          = TRUE;
    _dontReconnect  = TRUE;
    _valid          = FALSE;
    _noLongerViable = TRUE;
}


const CtiTime& CtiConnection::getLastReceiptTime() const
{
    return _lastInQueueWrite;
}


INT CtiConnection::checkCancellation( INT mssleep )
{
    INT status = NORMAL;

    try
    {
        if(mssleep > 0)
        {
            rwSleep(mssleep);
        }
        rwServiceCancellation( );
    }
    catch(const RWCancellation& cMsg)
    {
        if(getDebugLevel() & DEBUGLEVEL_CONNECTION)
        {
            logStatus( __FUNCTION__, "connection Thread canceled.");
        }

        status = NOTNORMAL;
        forceTermination();
        // throw;  // 062800 CGP Unknown whether I care to re-throw these.
    }

    return status;
}


const string& CtiConnection::getName() const
{
    return _name;
}


void CtiConnection::setName( const string &str )
{
    _name = str;

    _outQueue.setName( str );

    if( _inQueue != NULL && _localQueueAlloc )
    {
        _inQueue->setName( str );
    }
}


int CtiConnection::outQueueCount() const
{
    return (int)_outQueue.entries();
}


RWBoolean CtiConnection::operator==(const CtiConnection& aRef) const
{
    return (this == &aRef);
}


unsigned CtiConnection::hash(const CtiConnection& aRef)
{
    return (unsigned)&aRef;            // The address of the Object?
}


string CtiConnection::getPeer() const
{
    string peer;

    if( _valid )
    {
        peer += "outbound destination: " + Cti::Messaging::ActiveMQ::destPhysicalName( *_destOut );
    }
    else
    {
        peer += "not connected";
    }

    peer += " (" + _birth.asString() + ")";

    return peer;
}


string CtiConnection::who() const
{
    string whoStr = _title;

    if( !_name.empty() )
    {
        whoStr += " \"" + _name + "\"";
    }

    return whoStr;
}


CtiConnection::Que_t & CtiConnection::getOutQueueHandle() { return _outQueue; }
CtiConnection::Que_t & CtiConnection::getInQueueHandle()  { return *_inQueue; }


BOOL CtiConnection::isViable() const
{
    return !_noLongerViable;
}


UINT CtiConnection::valid() const
{
    return _valid;
}


void CtiConnection::messagePeek( const CtiMessage& msg )
{
    // Implemented only in CtiClientConnection
}


void CtiConnection::logStatus( string funcName, string note ) const
{
    string whoStr = who();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << funcName << " : " << whoStr << " " << note << endl;
    }
}


void CtiConnection::logException( string fileName, int line, string exceptionName, string note ) const
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

