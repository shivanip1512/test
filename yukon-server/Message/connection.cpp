#include "precompiled.h"

#include "boost/algorithm/string/replace.hpp"

#include "collectable.h"
#include "connection.h"
#include "message.h"
#include "numstr.h"
#include "dlldefs.h"
#include "logger.h"
#include "amq_util.h"
#include "message_factory.h"
#include "millisecond_timer.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore
using namespace Cti::Messaging::Serialization;
using namespace Cti::Messaging::ActiveMQ;

using Cti::WorkerThread;
using Cti::Timing::Chrono;

#pragma warning(push)
#pragma warning(disable:4355) // disable the warning generated by worker thread using "this" in constructor initializer List

/**
 * class constructor
 * @param title connection title set by connection child classes (type and id)
 * @param inQ inbound queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of seconds to allow the connection to send remaining messages
 */
CtiConnection::CtiConnection( const string& title, Que_t *inQ, int termSeconds ) :
    _inQueue(inQ),
    _termDuration( Chrono::seconds(termSeconds) ),
    _flag(0),
    _title(title),
    _outthread( WorkerThread::Function( &CtiConnection::outThreadFunc, this )
            .name( boost::replace_all_copy( title, " ", "" ) + "_outThread" ) // use the title and remove all white spaces to set the thread name
            .priority( THREAD_PRIORITY_HIGHEST ))
{
    // create message listener and register function and caller
    _messageListener.reset(
            new MessageListener(
                    boost::bind(&CtiConnection::onMessage, this, _1)));
    // create advisory message listener and register function and caller
    _advisoryListener.reset(
            new MessageListener(
                    boost::bind(&CtiConnection::onAdvisoryMessage, this, _1)));
}

#pragma warning(pop)

/**
 * class destructor
 */
CtiConnection::~CtiConnection()
{
}

/**
 * allocate inQueue and start the connection threads
 */
void CtiConnection::start()
{
    try
    {
        {
            WriterGuard guard(_connMux);

            if( _connectCalled )
            {
                return;
            }

            _connectCalled = true;

            if( _inQueue == NULL )
            {
                _inQueue         = CTIDBG_new Que_t;
                _localQueueAlloc = true;
            }

            threadInitiate();
        }

        int status = verifyConnection();

        if( status != NORMAL )
        {
            logStatus( __FUNCTION__, "connection has error status: " + CtiNumStr(status));
        }
    }
    catch(...)
    {
        logException( __FILE__, __LINE__);

        throw;
    }
}

/**
 * starts the connection threads
 */
void CtiConnection::threadInitiate()
{
    try
    {
        _outthread.start();
    }
    catch(const RWxmsg& x)
    {
        logException( __FILE__, __LINE__, typeid(x).name(), x.why() );
    }
}

/**
 * out traffic thread. establishes connection and sends outbound messages
 */
void CtiConnection::outThreadFunc()
{
    try
    {
        for(;!_bQuit;)
        {
            if( !_bConnected )
            {
                if( ! establishConnection() )
                {
                    _bQuit = _dontReconnect;

                    if( !_bQuit )
                    {
                        checkInterruption( Chrono::seconds(1) ); // No runnaway loops ok...
                    }

                    continue;
                }

                _bConnected = true;
            }

            if( _valid )
            {
                if( !_outMessage )
                {
                    _outMessage.reset( _outQueue.getQueue( 1000 ));
                }

                if( !_outMessage )
                {
                    // check for thread interruption every second
                    checkInterruption();
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
                        _valid = false; //sending data failed

                        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
                    }
                }
            }

            if( !_valid )
            {
                // we need to close the producer before receiving all messaging.
                // this will tell the peer connection that we are closing, but still listening
                _producer->close();

                // we should try to read all pending messages from the temporary inbound queue
                receiveAllMessages();

                _bConnected = false;
                _bQuit      = _dontReconnect;

                checkInterruption();
            }
        }
    }
    catch(...)
    {
        _valid = false;

        logException( __FILE__, __LINE__);

        throw; // Let the higher powers handle this crap!
    }
}

/**
 * Send message to the outbound destination using the message producer.
 * @param msg reference to the message to send
 */
void CtiConnection::sendMessage( const CtiMessage& msg )
{
    vector<unsigned char> obytes;

    // serialize message
    const string msgType = g_messageFactory.serialize( msg, obytes );

    if( msgType.empty() )
    {
        logStatus( __FUNCTION__, string("message \"") + typeid(msg).name() + "\" not registered with factory." );
        return;
    }

    // create a new cms bytes message
    const boost::scoped_ptr<cms::BytesMessage> bytes_msg( _sessionOut->createBytesMessage() );

    bytes_msg->writeBytes( obytes );
    bytes_msg->setCMSType( msgType );

    // send the message
    _producer->send( bytes_msg.get() );
}

/**
 * receive all message from inbound temporary queue
 * temporary destination. reads all messages for 1 sec
 */
void CtiConnection::receiveAllMessages()
{
    try
    {
        // disconnect asynchronous message listener
        _consumer->setMessageListener( NULL );

        unsigned elapsedMillis;
        const unsigned timeoutMillis = 1000;

        Cti::Timing::MillisecondTimer timer;

        while( (elapsedMillis = timer.elapsed()) < timeoutMillis )
        {
            // receive with a timeout set to the remaining millis
            const boost::scoped_ptr<cms::Message> msg( _consumer->receive( timeoutMillis - elapsedMillis ));

            if( ! msg )
            {
                break;
            }

            onMessage( msg.get() );
        }
    }
    catch( cms::CMSException& e )
    {
        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
    }
}

/**
 * asynchronous function called when a message is received. uses the message factory to deserialize
 * the message and writes it to the inQueue.
 * This function is registerd with the consumer message listener
 * @param message  pointer to the cms::message received
 */
void CtiConnection::onMessage( const cms::Message* message )
{
    MessagePtr<CtiMessage>::type omsg;

    // deserialize received message
    if( const cms::BytesMessage* bytes_msg = dynamic_cast<const cms::BytesMessage*>(message) )
    {
        vector<unsigned char> ibytes( bytes_msg->getBodyLength() );

        bytes_msg->readBytes( ibytes );

        // deserialize the message
        omsg = g_messageFactory.deserialize( message->getCMSType(), ibytes );
    }

    // check for any deserialize failure
    if( !omsg.get() )
    {
        logStatus( __FUNCTION__, "message: \"" + message->getCMSType() + "\" cannot be deserialized." );

        triggerReconnect();

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

/**
 * Function called when an advisory message is received. This function is registered with the advisory message listener
 * @param message pointer to the cms::message received
 */
void CtiConnection::onAdvisoryMessage( const cms::Message* message )
{
    if( message->getCMSType() != "Advisory" )
    {
        logStatus( __FUNCTION__, "received unexpected message: \"" + message->getCMSType() + "\" is not \"Advisory\"." );
    }

    triggerReconnect();

    {
        CtiLockGuard<CtiCriticalSection> guard(_advisoryMux);
        if( _advisoryConsumer )
        {
            // un-register the message listener since we dont need it anymore
            _advisoryConsumer->setMessageListener(NULL);
        }
    }
}

/**
 * setup topic advisory consumers to monitor number of consumer on the outbound destination
 */
void CtiConnection::setupAdvisoryListener()
{
    CtiLockGuard<CtiCriticalSection> guard(_advisoryMux);

    // create advisory topic consumer to monitor if the outbound destination has only 1 message consumer
    _advisoryConsumer.reset( createTopicConsumer(
            *_sessionOut,
            "ActiveMQ.Advisory.Producer.Queue." + _consumer->getDestPhysicalName(),
            "producerCount <> 1" ));

    _advisoryConsumer->setMessageListener(_advisoryListener.get());
}

/**
 * deallocated connection resource and close the connection (see server and client child classes destructor)
 */
void CtiConnection::cleanConnection()
{
    close();

    if( _localQueueAlloc && _inQueue )
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

/**
 * send all remainning message from outQueue and closes the connection
 */
void CtiConnection::close()
{
    try
    {
        WriterGuard guard(_connMux);

        if( _closed )
        {
            return;
        }

        _closed         = true;
        _noLongerViable = true;

        logStatus( __FUNCTION__, "closing connection." );

        if( !_connectCalled )
        {
            return;
        }

        // on client connections, if we are currently trying to establish a connection,
        // abort the connection attempt and prevent re-connections
        abortConnection();

        unsigned entries;
        unsigned elapsedMillis;

        Cti::Timing::MillisecondTimer timer;

        // We want the outQueue/OutThread to flush itself.
        while( _valid && (entries = _outQueue.entries()) && (elapsedMillis = timer.elapsed()) < _termDuration.milliseconds() )
        {
            logDebug( __FUNCTION__, "waiting for outbound Queue to flush " + CtiNumStr(entries) + " entries." );

            // sleep for 100 ms or less
            Sleep( std::min<DWORD>( 100, _termDuration.milliseconds() - elapsedMillis ));
        }

        _outQueue.clearAndDestroy(); // Get rid of the evidence...

        // signal to interrupt the thread
        _outthread.interrupt();

        // interrupt the current or the next getQueue() call
        _outQueue.interruptBlockingRead();

        _outthread.tryJoinOrTerminateFor( Chrono::seconds(2) );

    	forceTermination();

        if( _inQueue && _localQueueAlloc && _inQueue->entries() )
        {
            _inQueue->clearAndDestroy();      // Get rid of the evidence...
        }

        releaseResources();

        logDebug( __FUNCTION__, "has closed." );
    }
    catch(...)
    {
        logException( __FILE__, __LINE__ );
    }
}

/**
 * Write a message to send to the connection outQueue
 * @param QEnt pointer to the CtiMessage to send
 * @param timeout timeout in millisec, if the queue is full
 * @return NORMAL if the message is queued, QUEUE_WRITE if there was a timeout
 */
int CtiConnection::WriteConnQue( CtiMessage *QEnt, unsigned timeoutMillis )
{
    // take ownership of the message
    auto_ptr<CtiMessage> msg( QEnt );

    const int status = verifyConnection();
    if( status != NORMAL )
    {
        logStatus( __FUNCTION__, "connection error (" + CtiNumStr(status) + "), message was NOT able to be queued." );
        return status;
    }

    if( _outQueue.isFull() )
    {
        logStatus( __FUNCTION__, "queue is full. Will BLOCK." );
    }

    if( timeoutMillis > 0 )
    {
        if( !_outQueue.putQueue( msg.get(), timeoutMillis ) )
        {
            logStatus( __FUNCTION__, "message was NOT able to be queued within " + CtiNumStr(timeoutMillis) + " millis" );
            return QUEUE_WRITE;
        }

        msg.release(); // message was queued, release it
    }
    else
    {
        _outQueue.putQueue( msg.release() ); // wait forever
    }

    return NORMAL;
}

/**
 * Read the connection queue for received messages
 * @param Timeout timeout to read a message
 * @return a pointer to CtiMessage received, NULL otherwise
 */
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

/**
 *  wrapper function to write a message to the inQueue
 * @param msg pointer to the messages to write
 */
void CtiConnection::writeIncomingMessageToQueue( CtiMessage *msg )
{
    if( msg != NULL && _inQueue != NULL )
    {
        _inQueue->putQueue( msg );
    }
}

/**
 * verify connection status flags and threads status.
 * @return NORMAL if connection is started and still viable, NOTNORMAL otherwise
 */
int CtiConnection::verifyConnection()
{
    try
    {
        {
            ReaderGuard guard(_connMux);

            if( ! isViable() || ! _connectCalled )
            {
                return NOTNORMAL;
            }

            if( _outthread.isRunning() )
            {
                return NORMAL;
            }
        }

        {
            WriterGuard guard(_connMux);

            if( ! isViable() )
            {
                return NOTNORMAL;
            }

            if( ! _outthread.isRunning() )
            {
                logDebug( __FUNCTION__, "has exited." + string(_dontReconnect?"":" May restart."));

                if( ! _dontReconnect )
                {
                    // if the connection can reconnect, try to restart the thread
                    _outthread.start();

                    if( _outthread.tryJoinFor( Chrono::milliseconds(100)) )
                    {
                        _outQueue.clearAndDestroy();

                        forceTermination();

                        return NOTNORMAL;
                    }
                }
            }

            return NORMAL; // the thread has been restarted
        }
    }
    catch(...)
    {
        logException( __FILE__, __LINE__ );

        Sleep(5000);

        return NOTNORMAL;
    }
}

/**
 * set all flags to force a termination of the connection
 */
void CtiConnection::forceTermination()
{
    _bQuit          = true;
    _dontReconnect  = true;
    _valid          = false;
    _noLongerViable = true;
}

/**
 * set flags to schedule a reconnection.
 */
void CtiConnection::triggerReconnect()
{
    if( ! _valid )
    {
        return;
    }

    if( _dontReconnect )
    {
        // if this connection will not attempt to reconnect, flag this no longer viable so it will be close by the parent thread.
        // connection will keep sending messages to the peer connection, but prevent further messages from being added to the outQueue.
        // A call to verifyConnection() will inform the parent thread that this connection is to be close
        _noLongerViable = true;
    }
    else
    {
        // if this connection will reconnect, make this connection not valid to stop transmission of messages
        _valid = false;
    
        // interrupt the current or the next getQueue() call
        _outQueue.interruptBlockingRead();
    }
}

/**
 * retrieve the last message received time
 * @return reference to a CtiTime containing the message last received time
 */
const CtiTime& CtiConnection::getLastReceiptTime() const
{
    return _lastInQueueWrite;
}

/**
 * called in thread every once in while to very if a cancel thread request has been issued.
 * If so, forceTermination is called.
 */
void CtiConnection::checkInterruption()
{
    try
    {
        WorkerThread::interruptionPoint();
    }
    catch( WorkerThread::Interrupted& )
    {
        logDebug( __FUNCTION__, "Connection Thread Interrupted.");
        forceTermination();
    }
}

/**
 * called in thread every once in while to very if a cancel thread request has been issued.
 * If so, forceTermination is called.
 * @param duration
 */
void CtiConnection::checkInterruption( const Chrono &duration )
{
    try
    {
        WorkerThread::sleepFor( duration );
    }
    catch( WorkerThread::Interrupted& )
    {
        logDebug( __FUNCTION__, "Connection Thread Interrupted.");
        forceTermination();
    }
}

/**
 * get the name of the connection
 * @return reference to the name of the connection
 */
const string& CtiConnection::getName() const
{
    return _name;
}

/**
 * set the name of the connection
 * @param name connection name to set
 */
void CtiConnection::setName( const string &name )
{
    _name = name;

    _outQueue.setName( name );

    if( _inQueue != NULL && _localQueueAlloc )
    {
        _inQueue->setName( name );
    }
}

/**
 *  number of entries in the outbound queue
 * @return number of entries
 */
int CtiConnection::outQueueCount() const
{
    return _outQueue.entries();
}

/**
 * Compare this connection to another
 * @param aRef reference to a connection to compare with
 * @return true if both connection are the same
 */
RWBoolean CtiConnection::operator==(const CtiConnection& aRef) const
{
    return (this == &aRef);
}

/**
 * peer connection physical name and birth time
 * @return peer connection name
 */
string CtiConnection::getPeer() const
{
    if( !_valid )
    {
        return "not connected";
    }

    CtiLockGuard<CtiCriticalSection> guard(_peerMux);

    string peer = _peerName;
    peer += " (";
    peer += _peerConnectTime.asString();
    peer += ")";

    return peer;
}

/**
 * reset peer name and update connection time
 * @param peerName
 */
void CtiConnection::resetPeer( const string &peerName )
{
    CtiLockGuard<CtiCriticalSection> guard(_peerMux);
    
    _peerName        = peerName;
    _peerConnectTime = CtiTime::now();
}

/**
 * title and name of this connection
 * @return title and name
 */
string CtiConnection::who() const
{
    string whoStr = _title;

    if( !_name.empty() )
    {
        whoStr += " \"" + _name + "\"";
    }

    return whoStr;
}

/**
 * @return reference to the outbound queue
 */
CtiConnection::Que_t & CtiConnection::getOutQueueHandle()
{
    return _outQueue;
}

/**
 * @return reference to the inbound queue
 */
CtiConnection::Que_t & CtiConnection::getInQueueHandle()
{
    return *_inQueue;
}

/**
 * @return true if the connection is viable
 */
bool CtiConnection::isViable() const
{
    return ((_valid || !_dontReconnect) && !_noLongerViable);
}

/**
 * @return true of the connection is valid
 */
bool CtiConnection::valid() const
{
    return _valid;
}

/**
 * This method examines the message and records anything we care about
 * @param msg  reference to the message to peek into
 */
void CtiConnection::messagePeek( const CtiMessage& msg )
{
    // Implemented only in CtiClientConnection
}

/**
 * log status
 * @param funcName function name that will appear in the log
 * @param note additional detail to log
 */
void CtiConnection::logStatus( string funcName, string note ) const
{
    string whoStr = who();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << funcName << " : " << whoStr << " " << note << endl;
    }
}

/**
 * log debug
 * @param funcName function name that will appear in the log
 * @param note additional detail to log
 */
void CtiConnection::logDebug( string funcName, string note ) const
{
    if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
    {
        logStatus( funcName, note );
    }
}

/**
 * log exception
 * @param fileName file name
 * @param line line number
 * @param exceptionName exception name
 * @param note additional detail to log
 */
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

/**
 * Abort the connection
 */
void CtiConnection::abortConnection()
{
    // implemented in client connection to terminate a connection attempt
}

/**
 * cleans up consumer, producer, destinations, sessions
 */
void CtiConnection::releaseResources()
{
    _consumer.reset();
    _producer.reset();

    {
        CtiLockGuard<CtiCriticalSection> guard(_advisoryMux);
        _advisoryConsumer.reset();
    }

    _sessionIn.reset();
    _sessionOut.reset();

    _connection.reset();
}
