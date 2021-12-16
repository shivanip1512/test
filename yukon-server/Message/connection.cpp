#include "precompiled.h"

#include <winsock2.h>
#include <ws2tcpip.h>

#include "collectable.h"
#include "connection.h"
#include "message.h"
#include "numstr.h"
#include "dllbase.h"
#include "logger.h"
#include "amq_util.h"
#include "message_factory.h"
#include "millisecond_timer.h"
#include "logManager.h"

#include <proton/types.hpp>
#include <proton/transport.hpp>
#include <proton/connection.hpp>
#include <proton/receiver.hpp>
#include <proton/sender.hpp>

#include <boost/algorithm/string/replace.hpp>

using namespace std;
using namespace Cti::Messaging::Serialization;
using namespace Cti::Messaging::Qpid;

using Cti::WorkerThread;
using Cti::Timing::Chrono;

static std::atomic_long connectionId { 0 };

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
    _closed(false),
    _title(title),
    _connectionId(++connectionId),
    _outthread( WorkerThread::Function([this]{ outThreadFunc(); })
            .name( boost::replace_all_copy( title, " ", "" ) + "_outThread" ) // use the title and remove all white spaces to set the thread name
            .priority( THREAD_PRIORITY_HIGHEST )),
    _outQueueLogCountConfig(0),
    _outQueueLogInterval(0),
    _connectionState{ ConnectionState::WaitingForAck }
{
    CTILOG_DEBUG( dout, who() << " - CtiConnection::CtiConnection() @0x" << std::hex << this );
}

CtiConnection::~CtiConnection()
{
    CTILOG_DEBUG( dout, who() << " - CtiConnection::~CtiConnection() @0x" << std::hex << this );
}

long CtiConnection::getConnectionId() const
{
    return _connectionId;
}


std::string CtiConnection::getJmsType(const proton::message& m)
{
    std::string jms_type;
    if (m.message_annotations().exists("x-opt-jms-type"))
    {
        jms_type = proton::get<std::string>(m.message_annotations().get("x-opt-jms-type"));
    }

    return jms_type;
}

void CtiConnection::setJmsType(proton::message& m, const std::string& type)
{
    m.message_annotations().put("x-opt-jms-type", type);
}

void CtiConnection::on_transport_error(proton::transport & t)
{
    CTILOG_ERROR(dout, who() << " - transport error: " << t.error().what() );
}

void CtiConnection::on_connection_error(proton::connection & c)
{
    CTILOG_ERROR(dout, who() << " - connection error: " << c.error().what());
}

void CtiConnection::on_session_error(proton::session & s)
{
    CTILOG_ERROR(dout, who() << " - session error: " << s.error().what());
}

void CtiConnection::on_receiver_error(proton::receiver & r)
{
    CTILOG_ERROR(dout, who() << " - receiver error: " << r.error().what());
}

void CtiConnection::on_sender_error(proton::sender & s)
{
    CTILOG_ERROR(dout, who() << " - sender error: " << s.error().what());
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
                _inQueue         = CTIDBG_new Que_t(_name);
                _localQueueAlloc = true;
            }

            threadInitiate();
        }

        if( ! isConnectionUsable() )
        {
            CTILOG_ERROR(dout, who() <<" - connection has error status");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Error starting connection");

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
        CTILOG_DEBUG(dout, who() << " - Starting _outthread");
        _outthread.start();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout,  who() <<" - Error starting thread");
    }
}

/**
 * out traffic thread. establishes connection and sends outbound messages
 */
void CtiConnection::outThreadFunc()
{
    CTILOG_DEBUG(dout, "outThreadFunc started");

    try
    {
        for(;!_bQuit;)
        {
            if( !_bConnected )
            {
                CTILOG_DEBUG(dout, who() << " - Establishing connection");
                if(!establishConnection())
                {
                    _bQuit = ! canReconnect();

                    if( !_bQuit )
                    {
                        // Prevent a tight loop - only attempt reconnection every second.
                        checkInterruption( Chrono::seconds(1) ); 
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
                        if( isDebugLudicrous() )
                        {
                            CTILOG_TRACE(dout, "Outbound message " << *_outMessage);
                        }

                        sendMessage( *_outMessage );

                        _outMessage.reset();
                    }
                    catch( proton::error& e )
                    {
                        _valid = false; //sending data failed

                        CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - Error while attempting to send message");
                    }
                }
            }

            if( !_valid )
            {
                CTILOG_DEBUG(dout, who() << " - connection no longer valid ");

                // we need to close the producer before receiving all messaging.
                // this will tell the peer connection that we are closing, but still listening
//                _producer->close();

                // we should try to read all pending messages from the temporary inbound queue
                receiveAllMessages();

                _bConnected = false;
                _bQuit      = ! canReconnect();

                checkInterruption();
            }
        }
    }
    catch( WorkerThread::Interrupted& )
    {
        if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
        {
            CTILOG_DEBUG(dout, who() << " - Connection Thread Interrupted.");
        }

        forceTermination();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Unexpected exception caught");
    }

    _valid = false;

    CTILOG_DEBUG(dout, who() << " - outThreadFunc ended");
}

/**
 * Send message to the outbound destination using the message producer.
 * @param msg reference to the message to send
 */
void CtiConnection::sendMessage( const CtiMessage& msg )
{
    vector<unsigned char> obytes;

    if( isDebugLudicrous() )
    {
        CTILOG_TRACE(dout, getName() << " sending " << msg);
    }

    // serialize message
    const string msgType = g_messageFactory.serialize( msg, obytes );

    if( msgType.empty() )
    {
        CTILOG_ERROR(dout, who() << " - message \"" << typeid(msg).name() << "\" not registered with factory." );
        return;
    }

    // create a new message
    proton::message m;

    m.body( proton::binary{ std::cbegin(obytes), std::cend(obytes) });
    setJmsType( m, msgType );
 
    _sendStart.exchange(time(nullptr));  // Mark when the send started
    // send the message
    _producer->send( m );
    _sendStart.exchange(0);   // Mark send as complete
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
   //     _consumer->setMessageListener( NULL );

        unsigned elapsedMillis;
        const unsigned timeoutMillis = 1000;

        Cti::Timing::MillisecondTimer timer;

        while( (elapsedMillis = timer.elapsed()) < timeoutMillis )
        {
            // receive with a timeout set to the remaining millis
//  jmoc          const boost::scoped_ptr<cms::Message> msg(_consumer->receive(timeoutMillis - elapsedMillis));
         //   const boost::scoped_ptr<cms::Message> msg;// (_consumer->receive(timeoutMillis - elapsedMillis));

          //  if( ! msg )
            {
          //      break;
            }

// jmoc

       //     onMessage( msg.get() );
        }
    }
    catch( proton::error& e )
    {
        CTILOG_EXCEPTION_WARN(dout, e, who() <<" - Exception while attempting to receive all messages");
    }
}

/**
 * asynchronous function called when a message is received. uses the message factory to deserialize
 * the message and writes it to the inQueue.
 * This function is registered with the consumer message listener
 * @param message  pointer to the cms::message received
 */
void CtiConnection::onMessage( proton::message& msg )
{
    vector<unsigned char>  payload{ proton::get<proton::binary>(msg.body()) };

    MessagePtr<CtiMessage>::type omsg = g_messageFactory.deserialize( getJmsType(msg), payload);

    // check for any deserialize failure
    if( !omsg.get() )
    {
        CTILOG_ERROR(dout, who() << " - message: \"" << getJmsType(msg) << "\" cannot be deserialized.");
        return;
    }

    omsg->setConnectionHandle(Cti::ConnectionHandle{ _connectionId });

    // write incoming message to _inQueue
    if( !_inQueue )
    {
        CTILOG_ERROR(dout, who() << " - _inQueue is NULL.");
        return;
    }

    if( isDebugLudicrous() )
    {
        CTILOG_TRACE(dout, getName() << " just received " << *omsg);
    }

    // Refresh the time...
    _lastInQueueWrite = _lastInQueueWrite.now();

    writeIncomingMessageToQueue( omsg.release() );

    const auto inQueueSize = _inQueue->size();
    auto inQueueSizeWarning = _inQueueSizeWarning.load();

    if(inQueueSize > inQueueSizeWarning)
    {
        CTILOG_WARN(dout, who() << " - inQueue has more than " << inQueueSizeWarning << " elements (" << inQueueSize << ")");

        if(!_inQueueSizeWarning.compare_exchange_strong(inQueueSizeWarning, inQueueSizeWarning * 2))
        {
            CTILOG_WARN(dout, who() << " - could not update _inQueueSizeWarning, value was set to " << inQueueSizeWarning << " by another thread");
        }
    }
}

bool CtiConnection::canReconnect() const
{
    return ! (_dontReconnect || _stopping);
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
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Unexpected exception while cleaning connection");
        }
        _inQueue = 0;
    }

    try
    {
        _outQueue.clearAndDestroy();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Unexpected exception while clearing outbound message queue");
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

        CTILOG_INFO(dout, who() << " - closing connection");

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
            if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
            {
                CTILOG_DEBUG(dout, who() << " - waiting for outbound Queue to flush " << entries << " entries");
            }

            // sleep for 100 ms or less
            Sleep( std::min<DWORD>( 100, _termDuration.milliseconds() - elapsedMillis ));
        }

        _outQueue.clearAndDestroy(); // Get rid of the evidence...

        // signal to interrupt the thread
        _outthread.interrupt();

        // interrupt the current or the next getQueue() call
        _outQueue.interruptBlockingRead();

        joinOutThreadOrDie();

        forceTermination();

        if( _inQueue && _localQueueAlloc )
        {
            _inQueue->clearAndDestroy(); // Get rid of the evidence...

            // interrupt the current or the next getQueue() call
            _inQueue->interruptBlockingRead();
        }

        releaseResources();

        if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
        {
            CTILOG_DEBUG(dout, who() << " - has closed.");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Unexpected exception during connection close");
    }
}

void CtiConnection::joinOutThreadOrDie()
{
    try
    {
        if( _outthread.tryJoinFor(Chrono::seconds(35)) )
        {
            return;
        }
    }
    catch( boost::thread_interrupted& )
    {
        CTILOG_INFO(dout, "Interrupted while joining outThread");
    }

    try
    {
        if( _outthread.tryJoinFor(Chrono::seconds(5)) )
        {
            return;
        }
    }
    catch( boost::thread_interrupted& )
    {
        CTILOG_INFO(dout, "Interrupted while joining outThread");
    }

    Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

    CTILOG_FATAL(dout, who() << "_outthread failed to join in a timely manner. Creating a mini-dump and aborting.");

    std::ostringstream os;

    os << "connection.close-" << _title << "-" << getName() << "-" << GetCurrentThreadId();
    CreateMiniDump(os.str());

    std::this_thread::sleep_for(std::chrono::seconds(10));

    abort();
}

YukonError_t CtiConnection::WriteConnQue(CtiMessage *QEnt, ::Cti::CallSite cs, unsigned timeoutMillis)
{
    // take ownership of the message
    return WriteConnQue(
                std::unique_ptr<CtiMessage>{ QEnt }, 
                cs, 
                timeoutMillis);
}

/**
 * Write a message to send to the connection outQueue
 * @param QEnt pointer to the CtiMessage to send
 * @param timeout timeout in millisec, if the queue is full
 * @return NORMAL if the message is queued, QUEUE_WRITE if there was a timeout
 */
YukonError_t CtiConnection::WriteConnQue(std::unique_ptr<CtiMessage> msg, ::Cti::CallSite callSite, unsigned timeoutMillis)
{
    if( ! msg )
    {
        CTILOG_ERROR(dout, who() << "WriteConnQue: Caller passed in null pointer from " << callSite);
    }

    if( ! isConnectionUsable() )
    {
        CTILOG_ERROR(dout, who() <<" - connection error, message was NOT able to be queued.");
        return ClientErrors::Abnormal;
    }

    const auto outQueueSize = _outQueue.size();
    auto outQueueSizeWarning = _outQueueSizeWarning.load();

    if( outQueueSize > outQueueSizeWarning )
    {
        CTILOG_WARN(dout, who() <<" - outQueue has more than " << outQueueSizeWarning << " elements (" << outQueueSize << ")");

        if( ! _outQueueSizeWarning.compare_exchange_strong(outQueueSizeWarning, outQueueSizeWarning * 2) )
        {
            CTILOG_WARN(dout, who() <<" - could not update _outQueueSizeWarning, value was set to " << outQueueSizeWarning << " by another thread");
        }
    }

    if ( shouldLogByTime() || shouldLogByCount( outQueueSize ) )
    {
        constexpr size_t MaximumConsumption = 3 * 1024 * 1024 * 1024;   // 3GB - our arbitrary limit du jour
        size_t consumption = _outQueue.getQueueMemoryConsumption();

        CTILOG_DEBUG( dout, who() << " - queue has " << outQueueSize << " entries consuming " << consumption << " bytes." );
        if ( _outQueue.getQueueMemoryConsumption() > MaximumConsumption )
        {
            CTILOG_DEBUG( dout, who() << " - queue has consumed the maximum allowable memory amount of " << MaximumConsumption << " bytes.  Closing and re-connecting." );
            close();
            return ClientErrors::Abnormal;
        }
    }

    if( timeoutMillis > 0 )
    {
        if( !_outQueue.putQueue( msg.get(), timeoutMillis ) )
        {
            CTILOG_ERROR(dout, who() << " - message was NOT able to be queued within " << timeoutMillis << " millis" );

            return ClientErrors::QueueWrite;
        }

        msg.release(); // message was queued, release it
    }
    else
    {
        _outQueue.putQueue( msg.release() ); // wait forever
    }

    return ClientErrors::None;
}

/**
 * Read the connection queue for received messages
 * @param Timeout timeout to read a message
 * @return a pointer to CtiMessage received, NULL otherwise
 */
CtiMessage* CtiConnection::ReadConnQue( UINT Timeout )
{
    CtiMessage *Msg = 0;

    // Verify that the last send has completed, or we issue a warning.
    if( const auto sendStart = _sendStart.load() )
    {
        const auto sendElapsed = time(nullptr) - sendStart;
        if( sendElapsed > 30 )    // > 30 seconds
        {
            CTILOG_LOG(
                (sendElapsed > 5 * 60              //  has it been more than 5 minutes?
                    ? Cti::Logging::Logger::Error       //  if so, error
                    : Cti::Logging::Logger::Warn),      //  otherwise, just warn
                dout, who() << " - send queue thread has send outstanding since " << CtiTime(sendStart) );
        }
    }

    if( _inQueue )
    {
        Msg = _inQueue->getQueue( Timeout );
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
 * @return true if connection is started and still viable,
 *         false otherwise
 */
bool CtiConnection::isConnectionUsable()
{
    try
    {
        {
            ReaderGuard guard(_connMux);

            if( ! isViable() || ! _connectCalled )
            {
                return false;
            }

            if( _outthread.isRunning() )
            {
                return true;
            }
        }

        {
            WriterGuard guard(_connMux);

            if( ! isViable() )
            {
                return false;
            }

            if( ! _outthread.isRunning() )
            {
                if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
                {
                    CTILOG_DEBUG(dout, who() << " - has exited." << (canReconnect()?" May restart.":""));
                }

                if( canReconnect() )
                {
                    // if the connection can reconnect, try to restart the thread
                    _outthread.start();

                    //  If the thread exited immediately, we can't recover
                    if( _outthread.tryJoinFor( Chrono::milliseconds(100)) )
                    {
                        _outQueue.clearAndDestroy();

                        forceTermination();

                        return false;
                    }
                }
            }

            return true; // the thread has been restarted
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Unexpected exception while verifying the connection");

        Sleep(5000);

        return false;
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
    CTILOG_DEBUG(dout, who() << " - triggerReconnect");

    if( ! _valid )
    {
        return;
    }

    if( ! canReconnect() )
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
        if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
        {
            CTILOG_DEBUG(dout, who() << " - Connection Thread Interrupted.");
        }

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
        if( getDebugLevel() & DEBUGLEVEL_CONNECTION )
        {
            CTILOG_DEBUG(dout, who() << " - Connection Thread Interrupted.");
        }

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
bool CtiConnection::operator==(const CtiConnection& aRef) const
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
    return ((_valid || canReconnect()) && !_noLongerViable);
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
  //  _consumer.reset();
  //  _producer.reset();


}

void CtiConnection::setOutQueueLogging( std::size_t messageCount, std::chrono::seconds period )
{
    if ( messageCount > 0 )
    {
        // Turn on and configure message count based logging
        _outQueueLogCountConfig = messageCount;

        auto originalCount = _outQueueLogCount.load();
        if ( ! _outQueueLogCount.compare_exchange_strong( originalCount, originalCount + _outQueueLogCountConfig ) )
        {
            CTILOG_WARN( dout, who() << " - could not update _outQueueLogCount, value was set to " << originalCount << " by another thread" );
        }
    }

    if ( period.count() > 0 )
    {
        // Turn on and configure time interval based logging
        _outQueueLogInterval = period;

        CtiTime now;

        auto nextLogTime = _nextOutQueueLogTime.load();
        if ( ! _nextOutQueueLogTime.compare_exchange_strong( nextLogTime, nextLogTime + now.seconds() + _outQueueLogInterval.count() ) )
        {
            CTILOG_WARN( dout, who() << " - could not update _nextOutQueueLogTime, value was set to " << nextLogTime << " by another thread" );
        }
    }
}

bool CtiConnection::shouldLogByCount( size_t queueSize )
{
    auto logCount = _outQueueLogCount.load();

    if ( logCount > 0 && queueSize > logCount )
    {
        if ( ! _outQueueLogCount.compare_exchange_strong( logCount, logCount + _outQueueLogCountConfig ) )
        {
            CTILOG_WARN( dout, who() << " - could not update _outQueueLogCount, value was set to " << logCount << " by another thread" );
        }

        return true;
    }

    return false;
}

bool CtiConnection::shouldLogByTime()
{
    CtiTime now;
    auto logPeriod = _nextOutQueueLogTime.load();

    if ( logPeriod > 0 && now.seconds() > logPeriod )
    {
        if ( ! _nextOutQueueLogTime.compare_exchange_strong( logPeriod, logPeriod + _outQueueLogInterval.count() ) )
        {
            CTILOG_WARN( dout, who() << " - could not update _nextOutQueueLogTime, value was set to " << logPeriod << " by another thread" );
        }

        return true;
    }

    return false;
}

