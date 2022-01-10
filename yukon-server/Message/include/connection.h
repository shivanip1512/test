#pragma once

#include <limits.h>

#include <boost/scoped_ptr.hpp>

#include "dlldefs.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_reg.h"
#include "mutex.h"
#include "queue.h"
#include "readers_writer_lock.h"
#include "connection_base.h"
#include "worker_thread.h"

#include <atomic>

#include <proton/session.hpp>
#include <proton/message.hpp>
#include <proton/messaging_handler.hpp>

namespace proton {
    class transport;
    class connection;
//    class session;
    class receiver;
    class sender;
}

namespace Cti::Messaging::Qpid {
class DestinationProducer;
class DestinationConsumer;
class QueueProducer;
class QueueConsumer;
class TopicConsumer;
class TempQueueConsumer;
}

class IM_EX_MSG CtiConnection : public Cti::Messaging::BaseConnection, public proton::messaging_handler
{
public:

    using Que_t = CtiQueue<CtiMessage, std::greater<CtiMessage> >;

protected:

    CtiConnection( const std::string& title, Que_t *inQ = NULL, int termSeconds = 3 );
    virtual ~CtiConnection();  //  definition in cpp file to isolate use of cms types

    // atomic<bool> is not copyable, so make sure we don't try
    CtiConnection( const CtiConnection& other ) = delete; // non construction-copyable
    CtiConnection& operator=( const CtiConnection& ) = delete; // non copyable

    proton::session _session;
    std::mutex _sessionMutex;
    std::condition_variable _sessionCv;

    enum class ConnectionState
    {   // uninit, registered w/session, waiting for ack, connected, bad/error
        WaitingForAck,
        Connected,
        Error
    };

    std::atomic<ConnectionState> _connectionState;

    std::string getJmsType( const proton::message & m );
    void setJmsType( proton::message & m, const std::string& type );


    void joinOutThreadOrDie();

    std::string _name;
    std::string _peerName;
    const std::string _title;
    const long _connectionId;

    const Cti::Timing::Chrono _termDuration;

    CtiTime _lastInQueueWrite;
    CtiTime _peerConnectTime;

    Cti::WorkerThread _outthread;

    Que_t  _outQueue; // contains message to send
    Que_t* _inQueue;  // contains message received

    std::atomic<size_t> _outQueueSizeWarning = 100;
    std::atomic<size_t> _inQueueSizeWarning = 100;

    // we want to print the memory consumption of the _outQueue every N messages

    size_t  _outQueueLogCountConfig;
    std::atomic<size_t> _outQueueLogCount = 0;
    bool shouldLogByCount( size_t queueSize );

    std::chrono::seconds _outQueueLogInterval;
    std::atomic<time_t> _nextOutQueueLogTime = 0;
    bool shouldLogByTime();

    using Lock = Cti::readers_writer_lock_t;
    using ReaderGuard = Lock::reader_lock_guard_t;
    using WriterGuard = Lock::writer_lock_guard_t;

    mutable Lock               _connMux;
    mutable CtiCriticalSection _peerMux;

    // State Descriptions:
    union
    {
        UINT _flag;

        struct // Bit field status definitions
        {
            UINT _bQuit              : 1;
            UINT _noLongerViable     : 1;     // One or both the threads have terminated.
            UINT _connectCalled      : 1;     // Indicates that threads have started and queues exist.
            UINT _valid              : 1;
            UINT _dontReconnect      : 1;
            UINT _localQueueAlloc    : 1;
            UINT _bConnected         : 1;
        };
    };

    std::atomic<bool> _closed;
    bool canReconnect() const;

    std::atomic<time_t> _sendStart { 0 };

    void sendMessage( const CtiMessage& message );
    void receiveAllMessages();

    virtual bool establishConnection         () = 0;
    virtual void abortConnection             ();
    virtual void releaseResources            ();
    virtual void writeIncomingMessageToQueue ( CtiMessage* msg );
    virtual void messagePeek                 ( const CtiMessage& msg );

    void checkInterruption ();
    void checkInterruption ( const Cti::Timing::Chrono &duration );

    void outThreadFunc    (); // OutBound messages to the application go through here -- on_sendable ?? jmoc
    void threadInitiate   (); // This function starts the execution of the next two, which are threads.
    void cleanConnection  ();
    void forceTermination ();
    void triggerReconnect ();
    void resetPeer        ( const std::string &peerName );

    std::unique_ptr<Cti::Messaging::Qpid::DestinationProducer> _producer;
    std::unique_ptr<Cti::Messaging::Qpid::TempQueueConsumer>   _consumer;

    void onMessage( proton::message & msg );

    std::unique_ptr<CtiMessage> _outMessage;

public:

    // message_handler overloads for error handling...
    void on_transport_error(proton::transport & t) override;
    void on_connection_error(proton::connection & c) override;
    void on_session_error(proton::session & s) override;
    void on_receiver_error(proton::receiver & r) override;
    void on_sender_error(proton::sender & s) override;
//    void on_error(const error_condition&)


    void start();
    virtual void close();

    virtual bool operator== ( const CtiConnection& aRef ) const;

    long getConnectionId() const;

    CtiMessage*  ReadConnQue  ( UINT Timeout = UINT_MAX );
    YukonError_t WriteConnQue(CtiMessage* msg, ::Cti::CallSite cs, unsigned timeoutMillis = 0);
    YukonError_t WriteConnQue(std::unique_ptr<CtiMessage> msg, ::Cti::CallSite cs, unsigned timeoutMillis = 0);

    Que_t& getOutQueueHandle();
    Que_t& getInQueueHandle();

    bool isConnectionUsable();
    int  outQueueCount() const;
    bool isViable() const;
    bool valid() const;

    void setOutQueueLogging( std::size_t messageCount, std::chrono::seconds period );

    const CtiTime& getLastReceiptTime() const;

    std::string who() const;
    std::string getPeer() const;

    const std::string& getName () const;
    void               setName ( const std::string &name );
};
