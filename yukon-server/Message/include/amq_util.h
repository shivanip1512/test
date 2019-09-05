#pragma once

#include <string>
#include "dlldefs.h"
#include "readers_writer_lock.h"
#include "cms/Connection.h"
#include "cms/MessageListener.h"
#include "cms/Queue.h"
#include "cms/Session.h"
#include "cms/TemporaryQueue.h"
#include "activemq/commands/DestinationInfo.h"
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition_variable.hpp>

namespace Cti {
namespace Messaging {
namespace ActiveMQ {

/*-----------------------------------------------------------------------------
  Initiliaze ActiveMQ Lib and create connection
-----------------------------------------------------------------------------*/
class IM_EX_MSG ConnectionFactory
{
    bool _isInitialized;

    CRITICAL_SECTION _cs;

    void initializeLib();

public:
    ConnectionFactory();
    virtual ~ConnectionFactory();

    std::unique_ptr<cms::Connection> createConnection( const std::string &brokerUri );
};


/*-----------------------------------------------------------------------------
  Connection factory singleton
-----------------------------------------------------------------------------*/
IM_EX_MSG extern ConnectionFactory g_connectionFactory;


/*-----------------------------------------------------------------------------
  Message listener template
-----------------------------------------------------------------------------*/
class MessageListener : public cms::MessageListener
{
    typedef std::function<void (const cms::Message*)> Callback;
    Callback _callback;

public:
    MessageListener ( Callback c ) : _callback( c ) {}
    virtual ~MessageListener () {}
    virtual void onMessage ( const cms::Message* message )
    {
        _callback( message );
    }
};


/*-----------------------------------------------------------------------------
  Exception listener template
-----------------------------------------------------------------------------*/
template <class T>
class ExceptionListener : public cms::ExceptionListener
{
    typedef void (T::*onExceptionFunc) ( const cms::CMSException& ex );
    onExceptionFunc _func;
    T* _caller;

public:
    ExceptionListener ( T* c, onExceptionFunc f ) : _caller(c), _func( f ) {}
    virtual ~ExceptionListener () {}
    virtual void onException ( const cms::CMSException& ex )
    {
        (_caller->*_func)( ex );
    }
};


/*-----------------------------------------------------------------------------
   Connection exception thrown by ManagedConnection
-----------------------------------------------------------------------------*/
class ConnectionException : public std::exception
{
    const std::string _desc;

public:
    ConnectionException( const std::string& desc ) :
        _desc( desc )
    {
    }

    virtual const char* what() const throw()
    {
        return _desc.c_str();
    }
};


/*-----------------------------------------------------------------------------
  Managed connection
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedConnection
{
    const std::string _brokerUri;

    bool                      _closed;
    boost::condition_variable _closeCond;
    boost::mutex              _closeMux;

    std::unique_ptr<cms::Connection> _connection;

    typedef Cti::readers_writer_lock_t Lock;
    typedef Lock::reader_lock_guard_t  ReaderGuard;
    typedef Lock::writer_lock_guard_t  WriterGuard;

    mutable Lock _lock;

    void closeConnection();
    void waitCloseEvent( unsigned millis );

public:
    ManagedConnection( const std::string& brokerUri );

    virtual ~ManagedConnection();

    void start();

    void close();

    void setExceptionListener( cms::ExceptionListener *listener );

    std::unique_ptr<cms::Session> createSession();

    bool verifyConnection() const;

    const std::string& getBrokerUri() const;
};


/*-----------------------------------------------------------------------------
  Retrieve physical name from cms destination
-----------------------------------------------------------------------------*/
inline std::string destPhysicalName( const cms::Destination& dest )
{
    if( const activemq::commands::ActiveMQDestination* amqDest = dynamic_cast<const activemq::commands::ActiveMQDestination*>( &dest ))
    {
        return amqDest->getPhysicalName();
    }

    return std::string();
}


/*-----------------------------------------------------------------------------
  Managed destination
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedDestination
{
public:
    virtual ~ManagedDestination ();

    std::string getDestPhysicalName () const;

    virtual const cms::Destination* getDestination () const = 0;
};


/*-----------------------------------------------------------------------------
  Managed message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedProducer : public ManagedDestination
{
protected:
    const std::unique_ptr<cms::MessageProducer> _producer;

    ManagedProducer ( cms::MessageProducer* producer );

public:
    virtual ~ManagedProducer ();

    virtual void close();

    void setTimeToLiveMillis ( long long time );

    void send (cms::Message *message);
};


/*-----------------------------------------------------------------------------
  Managed message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedConsumer : public ManagedDestination
{
protected:
    const std::unique_ptr<cms::MessageConsumer> _consumer;

    ManagedConsumer ( cms::MessageConsumer* consumer );

public:
    virtual ~ManagedConsumer ();

    virtual void close();

    void setMessageListener (cms::MessageListener * listener);

    cms::Message* receive ();

    cms::Message* receive ( int millisecs );

    cms::Message* receiveNoWait ();
};


/*-----------------------------------------------------------------------------
  Managed destination message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG DestinationProducer : public ManagedProducer
{
protected:
    const std::unique_ptr<cms::Destination> _dest;

    DestinationProducer ( cms::MessageProducer* producer, cms::Destination *dest );

public:
    // allow the creation of destination producer for responding to replyTo destinations
    DestinationProducer ( cms::Session &session, cms::Destination *dest );

    virtual ~DestinationProducer ();

    const cms::Destination* getDestination () const;
};

/*-----------------------------------------------------------------------------
  Managed destination message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG DestinationConsumer : public ManagedConsumer
{
protected:
    const std::unique_ptr<cms::Destination> _dest;

    DestinationConsumer ( cms::MessageConsumer* consumer, cms::Destination *dest );
public:

    virtual ~DestinationConsumer ();

    const cms::Destination* getDestination () const;
};

/*-----------------------------------------------------------------------------
  Managed Queue message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG QueueProducer : public DestinationProducer
{
public:
    QueueProducer ( cms::Session &session, cms::Queue* dest );

    virtual ~QueueProducer ();
};

/*-----------------------------------------------------------------------------
  Managed Queue message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG QueueConsumer : public DestinationConsumer
{
public:
    QueueConsumer ( cms::Session &session, cms::Queue* dest );

    virtual ~QueueConsumer ();
};

/*-----------------------------------------------------------------------------
  Managed topic message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TopicConsumer : public DestinationConsumer
{
public:
    TopicConsumer ( cms::Session &session, cms::Topic* dest );

    TopicConsumer ( cms::Session &session, cms::Topic* dest, const std::string &selector );

    virtual ~TopicConsumer ();
};

/*-----------------------------------------------------------------------------
  Managed temporary queue message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TempQueueConsumer : public QueueConsumer
{
public:
    TempQueueConsumer ( cms::Session &session, cms::TemporaryQueue* dest );

    virtual ~TempQueueConsumer ();

    virtual void close (); // override close to delete the destination
};

/*-----------------------------------------------------------------------------
  creator functions for Managed destinations message consumer / producer
-----------------------------------------------------------------------------*/

inline std::unique_ptr<DestinationProducer> createDestinationProducer( cms::Session &session, const cms::Destination *dest )
{
    return std::make_unique<DestinationProducer>( session, dest->clone() );
}

inline std::unique_ptr<QueueProducer> createQueueProducer( cms::Session &session, const std::string &queueName )
{
    return std::make_unique<QueueProducer>( session, session.createQueue( queueName ));
}

inline std::unique_ptr<QueueConsumer> createQueueConsumer( cms::Session &session, const std::string &queueName )
{
    return std::make_unique<QueueConsumer>( session, session.createQueue( queueName ));
}

inline std::unique_ptr<TopicConsumer> createTopicConsumer( cms::Session &session, const std::string &topicName )
{
    return std::make_unique<TopicConsumer>( session, session.createTopic( topicName ));
}

inline std::unique_ptr<TopicConsumer> createTopicConsumer( cms::Session &session, const std::string &topicName, const std::string &selector )
{
    return std::make_unique<TopicConsumer>( session, session.createTopic( topicName ) , selector );
}

inline std::unique_ptr<TempQueueConsumer> createTempQueueConsumer( cms::Session &session )
{
    return std::make_unique<TempQueueConsumer>( session, session.createTemporaryQueue());
}


}
}
}
