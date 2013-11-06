#include "precompiled.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "boost/algorithm/string/trim.hpp"
#include "collectable.h"
#include "connection.h"
#include "message.h"
#include "numstr.h"
#include "dlldefs.h"
#include "logger.h"
#include "amq_util.h"
#include "message_factory.h"
#include "countdown_timer.h"


using namespace Cti::Messaging::Serialization;
using namespace Cti::Messaging::ActiveMQ;

namespace { // anomymous namespace

/**
 * The ScopedMessage is a workaround for handling a cms::BytesMessage life cycle. ActiveMQBytesMessage destructor
 * can actually throw a cms::Exception that cannot be handle by the sendMessage() nor receiveAllMessages() if the
 * cause the destructor call is a from a preceding exception.
 *
 * (From activemq-cpp release 3.8.0)
 *
 * ActiveMQBytesMessage destructor is as follow :
 * ActiveMQBytesMessage::~ActiveMQBytesMessage() throw() {
 *   this->reset();
 * }
 *
 * and should probably be :
 * ActiveMQBytesMessage::~ActiveMQBytesMessage() throw() {
 *   try {
 *      this->reset();
 *   }
 *   AMQ_CATCHALL_NOTHROW()
 * }
 */
template< typename T >
class ScopedMessage
{
    boost::scoped_ptr<T> _msg;

public:
    ScopedMessage( T * msg ) : _msg( msg )
    {
    }

    ~ScopedMessage()
    {
        try
        {
            _msg.reset();
        }
        catch(...)
        {
            // catch all, no throw
        }
    }

    T * operator->() const
    {
        return _msg.get();
    }

    T * get() const
    {
        return _msg.get();
    }
};

} // anomymous namespace


/**
 * class constructor
 * @param title connection title set by connection child classes (type and id)
 * @param inQ inbound queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of 1 sec iteration to allow the connection to send remaining messages
 */
CtiConnection::CtiConnection( const string& title, Que_t *inQ, INT tt ) :
    _inQueue(inQ),
    _termTime(tt),
    _flag(0),
    _title(title)
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
            WriterGuard guard( _connLock );

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

            ThreadInitiate();
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
void CtiConnection::ThreadInitiate()
{
    try
    {
        outthread = rwMakeThreadFunction( *this, &CtiConnection::OutThread );
        outthread.start();
    }
    catch(const RWxmsg& x)
    {
        logException( __FILE__, __LINE__, typeid(x).name(), x.why() );
    }
}

/**
 * out traffic thread. establishes connection and sends outbound messages
 */
void CtiConnection::OutThread()
{
    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_HIGHEST);

    logDebug( __FUNCTION__, "has begun operations TID: " + CtiNumStr(CurrentTID()));

    // use the title and remove all white spaces to set the thread name
    string threadName = boost::trim_copy( _title ) + "OutThread";
    SetThreadName( -1, threadName.c_str() );

    try
    {
        for(;!_bQuit;)
        {
            if( !_bConnected )
            {
                if( !establishConnection() )
                {
                    _bQuit = _dontReconnect;

                    checkCancellation();

                    if( !_bQuit )
                    {
                        Sleep(1000); // No runnaway loops ok...
                    }

                    continue;
                }

                _bConnected = true;
            }

            if( _valid )
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
                        _valid = false; //sending data failed

                        logException( __FILE__, __LINE__, typeid(e).name(), e.getMessage() );
                    }
                }
            }

            if( !_valid )
            {
                // we should try to read all pending messages from the temporary inbound queue
                receiveAllMessages();

                _bConnected = false;
                _bQuit      = _dontReconnect;

                checkCancellation();
            }
        }
    }
    catch(...)
    {
        _valid = false;

        logException( __FILE__, __LINE__);

        throw; // Let the higher powers handle this crap!
    }

    forceTermination();

    logDebug( __FUNCTION__, "is terminating...." );
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
    ScopedMessage<cms::BytesMessage> bytes_msg( _sessionOut->createBytesMessage() );

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

        // create a second consumer
        //
        // We use this trick because of a potential bug in ActiveMQ regarding the producerCount
        // never returning to zero. This method will simply create another consumer to stop the
        // peer connection and allow us to empty the receiving queue. Consumer count will reach 2
        // thus forcing the disconnection process (witch is ultimately what we want).
        auto_ptr<cms::MessageConsumer> tmpConsumer( _sessionIn->createConsumer( _consumer->getDestination() ));

        Cti::Timing::CountdownTimer timer( 1000 ); // set to 1 sec

        while( ! timer.isExpired() )
        {
            ScopedMessage<cms::Message> msg( tmpConsumer->receive( timer.getRemaining() ));

            if( ! msg.get() )
            {
                break;
            }

            onMessage( msg.get() );

            timer.update();
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

     // unset the message listener since we dont need it anymore
    _advisoryConsumer->setMessageListener( NULL );
}

/**
 * setup topic advisory consumers to monitor number of consumer on the outbound destination
 */
void CtiConnection::setupAdvisoryListener()
{
    // create advisory topic consumer to monitor if the outbound destination has only 1 message consumer
    _advisoryConsumer.reset( createTopicConsumer(
            *_sessionOut,
            "ActiveMQ.Advisory.Consumer.Queue." + _producer->getDestPhysicalName(),
            "consumerCount <> 1" ));

    _advisoryConsumer->setMessageListener( _advisoryListener.get() );
}

/**
 * deallocated connection resource and close the connection (see server and client child classes destructor)
 */
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

/**
 * send all remainning message from outQueue and closes the connection
 */
void CtiConnection::close()
{
    try
    {
        WriterGuard guard( _connLock );

        if( _closed )
        {
            return;
        }

        logStatus( __FUNCTION__, "closing connection." );

        if( !_connectCalled )
        {
            return;
        }

        // 1.  We want the outQueue/OutThread to flush itself.
        //     I allow _termTime seconds for this to occur
        int sleep_nbr = 0,
            entries_nbr;

        while((( entries_nbr = _outQueue.entries()) > 0 || _outMessage ) && ( _valid || !_dontReconnect ))
        {
            if( entries_nbr )
            {
                logDebug( __FUNCTION__, "waiting for outbound Queue to flush " + CtiNumStr( entries_nbr ) + " entries." );
            }
            else
            {
                logDebug( __FUNCTION__, "waiting for the last message to be sent." );
            }

            Sleep(1000);

            if( sleep_nbr++ > _termTime )
            {
                break;
            }
        }

        _outQueue.clearAndDestroy();  // Get rid of the evidence...

        // This flag tells OutThread not to attempt to reconnect....
        _dontReconnect = true;

        outthread.requestCancellation(1);

        // if we are currently trying to establish a connection :
        // 1.  abort the connection attempt
        // 2.  wait for the thread to end by itself
        if( !_bConnected )
        {
            abortConnection();
        }

        if( outthread.join(100) == RW_THR_TIMEOUT )
        {
            if( outthread.requestCancellation(2000) == RW_THR_TIMEOUT )
            {
                logStatus( __FUNCTION__, "OutThread refuses to cancel after 2 seconds." );

                forceTermination();
            }
            if( outthread.join(2000) == RW_THR_TIMEOUT )
            {
                logStatus( __FUNCTION__, "OutThread refuses to join after 2 seconds." );

                try
                {
                    outthread.terminate();
                }
                catch(...)
                {
                    logException( __FILE__, __LINE__ );
                }
            }
        }

        if( _inQueue && _localQueueAlloc && _inQueue->entries() != 0 )
        {
            _inQueue->clearAndDestroy();      // Get rid of the evidence...
        }

        deleteResources();

        _closed = true;

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
 * @param cleaniftimedout set to true to delete the message if there is a timeout
 * @return NORMAL if the message is queued, QUEUE_WRITE if there was a timeout
 */
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
INT CtiConnection::verifyConnection()
{
    try
    {
        {
            ReaderGuard guard( _connLock );

            if( isTerminated() || ! isStarted() )
            {
                return NOTNORMAL;
            }

            if( outthread.getCompletionState() == RW_THR_PENDING )
            {
                return NORMAL;
            }
        }

        {
            WriterGuard guard( _connLock );

            if( isTerminated() )
            {
                return NOTNORMAL;
            }

            int thread_status = outthread.getCompletionState();

            if( thread_status != RW_THR_PENDING )
            {
                logDebug( __FUNCTION__, "has exited with a completion state of " + CtiNumStr(thread_status) + "." + (_dontReconnect?"":" May restart."));

                // if the connection can reconnect, try to restart the thread
                if( _dontReconnect || ( outthread.start() != RW_THR_PENDING ))
                {
                    _outQueue.clearAndDestroy();

                    forceTermination();

                    return OutThreadTerminated; // the outthread has exited!
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
 * check to see if the connection is terminated
 */
bool CtiConnection::isTerminated() const
{
    return (( ! _valid && _dontReconnect ) || _noLongerViable || _bQuit );
}

/**
 * check to see if the connection is started
 */
bool CtiConnection::isStarted() const
{
    return _connectCalled;
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
 * @param mssleep  sleeptime in millisec to wait for a cancel request
 * @return NORMAL if no cancellation request, NOTNORMAL otherwise
 */
void CtiConnection::checkCancellation( INT mssleep )
{
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
        logDebug( __FUNCTION__, "connection Thread canceled.");

        forceTermination();
        // throw;  // 062800 CGP Unknown whether I care to re-throw these.
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
    return (int)_outQueue.entries();
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
    string peer;

    if( _valid )
    {
        peer += "outbound destination: " + _producer->getDestPhysicalName();
    }
    else
    {
        peer += "not connected";
    }

    peer += " (" + _birth.asString() + ")";

    return peer;
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
    return !_noLongerViable;
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
void CtiConnection::deleteResources()
{
    _consumer.reset();
    _producer.reset();

    _advisoryConsumer.reset();

    _sessionIn.reset();
    _sessionOut.reset();
}
