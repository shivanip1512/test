#pragma once

#include <limits.h>

#include <rw/thr/thrfunc.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>
#include <rw/toolpro/neterr.h>

#include <boost/scoped_ptr.hpp>

#include "dlldefs.h"
#include "exchange.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_reg.h"
#include "mutex.h"
#include "queue.h"
#include "readers_writer_lock.h"
#include "connection_base.h"

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

namespace Cti {
namespace Messaging {
namespace ActiveMQ {
class ManagedConnection;
class DestinationProducer;
class DestinationConsumer;
class QueueProducer;
class QueueConsumer;
class TopicConsumer;
class TempQueueConsumer;
}
}
}

class IM_EX_MSG CtiConnection : public Cti::Messaging::BaseConnection
{
public:

    typedef  CtiQueue<CtiMessage, std::greater<CtiMessage> > Que_t;
    // typedef  CtiFIFOQueue<CtiMessage> Que_t;

protected:

    CtiConnection( const std::string& title, Que_t *inQ = NULL, INT tt = 3 );

    virtual ~CtiConnection();

    std::string _name;
    const std::string _title;

    INT _termTime;

    CtiTime _birth;
    CtiTime _lastInQueueWrite;

    RWThreadFunction outthread;

    Que_t  _outQueue; // contains message to send
    Que_t* _inQueue;  // contains message received

    typedef Cti::readers_writer_lock_t Lock;
    typedef Lock::reader_lock_guard_t  ReaderGuard;
    typedef Lock::writer_lock_guard_t  WriterGuard;

    mutable Lock _connLock;

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
            UINT _closed             : 1;
        };
    };

    void sendMessage( const CtiMessage& message );
    void receiveAllMessages();

    virtual bool establishConnection         () = 0;
    virtual void abortConnection             ();
    virtual void deleteResources             ();
    virtual void writeIncomingMessageToQueue ( CtiMessage* msg );
    virtual void messagePeek                 ( const CtiMessage& msg );

    void checkCancellation ( INT mssleep = 0 );

    void OutThread        (); // OutBound messages to the applicaiton go through here
    void ThreadInitiate   (); // This function starts the execution of the next two, which are threads.
    void cleanConnection  ();
    void forceTermination ();
    void destroyDestIn    ();
    void triggerReconnect ();

    std::auto_ptr<cms::Session> _sessionIn;
    std::auto_ptr<cms::Session> _sessionOut;

    std::auto_ptr<Cti::Messaging::ActiveMQ::DestinationProducer> _producer;
    std::auto_ptr<Cti::Messaging::ActiveMQ::TempQueueConsumer>   _consumer;
    std::auto_ptr<cms::MessageListener>                          _messageListener;

    std::auto_ptr<Cti::Messaging::ActiveMQ::TopicConsumer> _advisoryConsumer;
    std::auto_ptr<cms::MessageListener>                    _advisoryListener;

    void onMessage         ( const cms::Message* message );
    void onAdvisoryMessage ( const cms::Message* message );

    void setupAdvisoryListener();

    void logStatus    ( std::string funcName, std::string note ) const;
    void logDebug     ( std::string funcName, std::string note ) const;
    void logException ( std::string fileName, int line, std::string exceptionName = "", std::string note = "" ) const;

    boost::scoped_ptr<CtiMessage> _outMessage;

public:

    void start();
    virtual void close();

    virtual bool operator== ( const CtiConnection& aRef ) const;

    CtiMessage* ReadConnQue  ( UINT Timeout = UINT_MAX );
    int         WriteConnQue ( CtiMessage* msg, unsigned millitimeout = 0, bool cleaniftimedout = true );

    Que_t& getOutQueueHandle();
    Que_t& getInQueueHandle();

    INT  verifyConnection();
    int  outQueueCount() const;
    bool isViable() const;
    bool valid() const;
    bool isStarted() const;
    bool isTerminated() const;

    const CtiTime& getLastReceiptTime() const;

    std::string who() const;
    std::string getPeer() const;

    const std::string& getName () const;
    void               setName ( const std::string &name );
};

typedef CtiConnection* CtiConnectionPtr;
