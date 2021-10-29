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

#include <proton/container.hpp>
#include <proton/messaging_handler.hpp>
#include <proton/message.hpp>
#include <proton/connection_options.hpp>
#include <proton/sender.hpp>
#include <proton/receiver.hpp>
#include <proton/session.hpp>

namespace Cti::Messaging::Qpid
{

/*-----------------------------------------------------------------------------
  Initiliaze ActiveMQ Lib and create connection
-----------------------------------------------------------------------------*/
class IM_EX_MSG ConnectionFactory
{
    proton::container  _container;

    std::thread  _container_thread;

public:

    ConnectionFactory();
    ~ConnectionFactory();

    proton::container & getContainer();

    void createConnection( const std::string &brokerUri, proton::connection_options & connOpt );
};


/*-----------------------------------------------------------------------------
  Connection factory singleton
-----------------------------------------------------------------------------*/
//IM_EX_MSG extern ConnectionFactory g_connectionFactory;


/*-----------------------------------------------------------------------------
  Message listener template
-----------------------------------------------------------------------------*/
class MessageListener 
{
    typedef std::function<void (proton::message&)> Callback;
    Callback _callback;

public:
    MessageListener( Callback c ) : _callback( c ) {}
    virtual ~MessageListener() {}

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
    proton::connection_options  _conn_options;

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
    ManagedConnection( const std::string& brokerUri, proton::connection_options & connOpt );

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
//inline std::string destPhysicalName( const cms::Destination& dest )
//{
//    if( const activemq::commands::ActiveMQDestination* amqDest = dynamic_cast<const activemq::commands::ActiveMQDestination*>( &dest ))
//    {
//        return amqDest->getPhysicalName();
//    }
//
//    return std::string();
//}


/*-----------------------------------------------------------------------------
  Managed destination
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedDestination
{
protected:

    std::string _dest;

public:

    ManagedDestination( const std::string & dest );

    ~ManagedDestination();

    std::string getDestination() const;
};


/*-----------------------------------------------------------------------------
  Managed message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedProducer : public ManagedDestination
{
protected:

    proton::duration    _expiryDuration;

    proton::sender  _producer;

    ManagedProducer( proton::session & sess, const std::string & dest );

public:

    ~ManagedProducer();

    void setTimeToLiveMillis( std::chrono::milliseconds time );

    void send( proton::message & message );
};


/*-----------------------------------------------------------------------------
  Managed message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedConsumer : public ManagedDestination, public proton::messaging_handler
{
public:

    typedef std::function<void (proton::message&)> Callback;

protected:

    Callback _callback;

    proton::receiver    _consumer;

    ManagedConsumer( proton::session & sess, const std::string & dest );

public:

    ~ManagedConsumer();

    void setMessageListener(Qpid::MessageListener * listener);  // ctor - 

    cms::Message* receive();

    cms::Message* receive( int millisecs );

    cms::Message* receiveNoWait();

    void on_message( proton::delivery & d, proton::message & msg ) override;
};


/*-----------------------------------------------------------------------------
  Managed destination message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG DestinationProducer : public ManagedProducer
{
public:
    // allow the creation of destination producer for responding to replyTo destinations
    DestinationProducer( proton::session & sess, const std::string & dest );

    ~DestinationProducer();
};

/*-----------------------------------------------------------------------------
  Managed destination message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG DestinationConsumer : public ManagedConsumer
{
public:

    DestinationConsumer( proton::session & sess, const std::string & dest );

    ~DestinationConsumer();
};

/*-----------------------------------------------------------------------------
  Managed Queue message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG QueueProducer : public DestinationProducer
{
public:

    QueueProducer( proton::session & sess, const std::string & dest );

    ~QueueProducer();
};

/*-----------------------------------------------------------------------------
  Managed Queue message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG QueueConsumer : public DestinationConsumer
{
public:

    QueueConsumer( proton::session & sess, const std::string & dest );

    ~QueueConsumer();
};

/*-----------------------------------------------------------------------------
  Managed topic message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TopicConsumer : public DestinationConsumer
{
protected:

    std::string _selector;

public:

    TopicConsumer( proton::session & sess, const std::string & dest, const std::string & selector = "" );

    ~TopicConsumer();
};

/*-----------------------------------------------------------------------------
  Managed temporary queue message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TempQueueConsumer : public QueueConsumer
{
public:

    TempQueueConsumer( proton::session & sess );

    ~TempQueueConsumer();
};

/*-----------------------------------------------------------------------------
  creator functions for Managed destinations message consumer / producer
-----------------------------------------------------------------------------*/

inline std::unique_ptr<DestinationProducer> createDestinationProducer( proton::session & sess, const std::string & dest )
{
    return std::make_unique<DestinationProducer>( sess, dest );
}

inline std::unique_ptr<QueueProducer> createQueueProducer( proton::session & sess, const std::string &queueName )
{
    return std::make_unique<QueueProducer>( sess, queueName );
}

inline std::unique_ptr<QueueConsumer> createQueueConsumer( proton::session & sess, const std::string &queueName )
{
    return std::make_unique<QueueConsumer>( sess, queueName );
}

inline std::unique_ptr<TopicConsumer> createTopicConsumer( proton::session & sess, const std::string &topicName )
{
    return std::make_unique<TopicConsumer>( sess, topicName );
}

inline std::unique_ptr<TopicConsumer> createTopicConsumer( proton::session & sess, const std::string &topicName, const std::string &selector )
{
    return std::make_unique<TopicConsumer>( sess, topicName, selector );
}

inline std::unique_ptr<TempQueueConsumer> createTempQueueConsumer( proton::session & sess )
{
    return std::make_unique<TempQueueConsumer>( sess );
}


}

