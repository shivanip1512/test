#pragma once

#include <string>
#include "dlldefs.h"
#include "readers_writer_lock.h"
#include "cms/Connection.h"
#include "activemq/commands/DestinationInfo.h"
#include "boost/thread/mutex.hpp"

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

    cms::Connection* createConnection( const std::string &brokerUri );
};


/*-----------------------------------------------------------------------------
  Connection factory singleton
-----------------------------------------------------------------------------*/
IM_EX_MSG extern ConnectionFactory g_connectionFactory;


/*-----------------------------------------------------------------------------
  Message listener template
-----------------------------------------------------------------------------*/
template <class T>
class MessageListener : public cms::MessageListener
{
    typedef void (T::*onMessageFunc) ( const cms::Message* message );
    onMessageFunc _func;
    T* _caller;

public:
    MessageListener ( T* c, onMessageFunc f ) : _caller(c), _func( f ) {}
    virtual ~MessageListener () {}
    virtual void onMessage ( const cms::Message* message )
    {
        (_caller->*_func)( message );
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

    bool _closed;

    boost::scoped_ptr<cms::Connection> _connection;

    typedef Cti::readers_writer_lock_t Lock;
    typedef Lock::reader_lock_guard_t  ReaderGuard;
    typedef Lock::writer_lock_guard_t  WriterGuard;

    mutable Lock _lock;

public:
    ManagedConnection( const std::string& brokerUri );

    virtual ~ManagedConnection();

    void start();

    void close();

    void setExceptionListener( cms::ExceptionListener *listener );

    cms::Session* createSession();

    bool verifyConnection() const;
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
    const boost::scoped_ptr<cms::MessageProducer> _producer;

public:
    ManagedProducer ( cms::MessageProducer* producer );

    virtual ~ManagedProducer ();

    void setTimeToLive ( long long time );

    void send (cms::Message *message);
};


/*-----------------------------------------------------------------------------
  Managed message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedConsumer : public ManagedDestination
{
protected:
    const boost::scoped_ptr<cms::MessageConsumer> _consumer;

public:
    ManagedConsumer ( cms::MessageConsumer* consumer );

    virtual ~ManagedConsumer ();

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
    const boost::scoped_ptr<cms::Destination> _dest;

public:
    DestinationProducer ( cms::Session &session, cms::Destination *dest );

    virtual ~DestinationProducer ();

    virtual const cms::Destination* getDestination () const;
};


/*-----------------------------------------------------------------------------
  Managed Queue message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG QueueProducer : public ManagedProducer
{
protected:
    const boost::scoped_ptr<cms::Queue> _dest;

public:
    QueueProducer ( cms::Session &session, cms::Queue* dest );

    virtual ~QueueProducer ();

    virtual const cms::Destination* getDestination () const;
};


/*-----------------------------------------------------------------------------
  Managed Queue message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG QueueConsumer : public ManagedConsumer
{
protected:
    const boost::scoped_ptr<cms::Queue> _dest;

public:
    QueueConsumer ( cms::Session &session, cms::Queue* dest );

    virtual ~QueueConsumer ();

    virtual const cms::Destination* getDestination () const;
};


/*-----------------------------------------------------------------------------
  Managed topic message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TopicConsumer : public ManagedConsumer
{
protected:
    const boost::scoped_ptr<cms::Topic> _dest;

public:
    TopicConsumer ( cms::Session &session, cms::Topic* dest );

    TopicConsumer ( cms::Session &session, cms::Topic* dest, const std::string &selector );

    virtual ~TopicConsumer ();

    virtual const cms::Destination* getDestination () const;
};


/*-----------------------------------------------------------------------------
  Managed temporary queue message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TempQueueConsumer : public ManagedConsumer
{
protected:
    const boost::scoped_ptr<cms::TemporaryQueue> _dest;

public:
    TempQueueConsumer ( cms::Session &session, cms::TemporaryQueue* dest );

    virtual ~TempQueueConsumer ();

    void close ();

    virtual const cms::Destination* getDestination () const;
};


/*-----------------------------------------------------------------------------
  creator functions for Managed destinations message consumer / producer
-----------------------------------------------------------------------------*/

inline DestinationProducer* createDestinationProducer( cms::Session &session, const cms::Destination *dest )
{
    return new DestinationProducer( session, dest->clone() );
}

inline QueueProducer* createQueueProducer( cms::Session &session, const std::string &queueName )
{
    return new QueueProducer( session, session.createQueue( queueName ));
}

inline QueueConsumer* createQueueConsumer( cms::Session &session, const std::string &queueName )
{
    return new QueueConsumer( session, session.createQueue( queueName ));
}

inline TopicConsumer* createTopicConsumer( cms::Session &session, const std::string &topicName )
{
    return new TopicConsumer( session, session.createTopic( topicName ));
}

inline TopicConsumer* createTopicConsumer( cms::Session &session, const std::string &topicName, const std::string &selector )
{
    return new TopicConsumer( session, session.createTopic( topicName ) , selector );
}

inline TempQueueConsumer* createTempQueueConsumer( cms::Session &session )
{
    return new TempQueueConsumer( session, session.createTemporaryQueue());
}


}
}
}
