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

namespace cms {
class Connection;
class Session;
class Message;
class ExceptionListener;
class MessageListener;
class Destination;
class MessageConsumer;
class MessageProducer;
class CMSException;
}

namespace decaf::internal::util::concurrent {
struct Threading;
struct ThreadHandle;
}

namespace Cti::Messaging::ActiveMQ {
class ManagedConnection;
class DestinationProducer;
class DestinationConsumer;
class QueueProducer;
class QueueConsumer;
class TopicConsumer;
class TempQueueConsumer;
}

class IM_EX_MSG CtiConnection : public Cti::Messaging::BaseConnection
{
public:

    using Que_t = CtiQueue<CtiMessage, std::greater<CtiMessage> >;

protected:

    CtiConnection( const std::string& title, Que_t *inQ = NULL, int termSeconds = 3 );
    virtual ~CtiConnection();  //  definition in cpp file to isolate use of cms types

    // atomic<bool> is not copyable, so make sure we don't try
    CtiConnection( const CtiConnection& other ) = delete; // non construction-copyable
    CtiConnection& operator=( const CtiConnection& ) = delete; // non copyable

    void joinOutThreadOrDie();

    std::string _name;
    std::string _peerName;
    const std::string _title;
    const long _connectionId;

    const Cti::Timing::Chrono _termDuration;

    CtiTime _lastInQueueWrite;
    CtiTime _peerConnectTime;

    Cti::WorkerThread _outthread;
    std::atomic<decaf::internal::util::concurrent::ThreadHandle*> _amqOutThreadHandle;

    Que_t  _outQueue; // contains message to send
    Que_t* _inQueue;  // contains message received

    std::atomic<size_t> _outQueueSizeWarning = 100;
    std::atomic<size_t> _inQueueSizeWarning = 100;

    using Lock = Cti::readers_writer_lock_t;
    using ReaderGuard = Lock::reader_lock_guard_t;
    using WriterGuard = Lock::writer_lock_guard_t;

    mutable Lock               _connMux;
    mutable CtiCriticalSection _peerMux;
    mutable CtiCriticalSection _advisoryMux;

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

    void outThreadFunc    (); // OutBound messages to the application go through here
    void threadInitiate   (); // This function starts the execution of the next two, which are threads.
    void cleanConnection  ();
    void forceTermination ();
    void triggerReconnect ();
    void resetPeer        ( const std::string &peerName );

    boost::shared_ptr<Cti::Messaging::ActiveMQ::ManagedConnection> _connection;

    std::unique_ptr<cms::Session> _sessionIn;
    std::unique_ptr<cms::Session> _sessionOut;

    std::unique_ptr<Cti::Messaging::ActiveMQ::DestinationProducer> _producer;
    std::unique_ptr<Cti::Messaging::ActiveMQ::TempQueueConsumer>   _consumer;
    std::unique_ptr<cms::MessageListener>                          _messageListener;

    std::unique_ptr<Cti::Messaging::ActiveMQ::TopicConsumer> _advisoryConsumer;
    std::unique_ptr<cms::MessageListener>                    _advisoryListener;

    void onMessage         ( const cms::Message* message );
    void onAdvisoryMessage ( const cms::Message* message );

    void setupAdvisoryListener();

    std::unique_ptr<CtiMessage> _outMessage;

public:

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

    const CtiTime& getLastReceiptTime() const;

    std::string who() const;
    std::string getPeer() const;

    const std::string& getName () const;
    void               setName ( const std::string &name );
};
