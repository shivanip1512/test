#pragma once

#include <string>
#include <queue>
#include "dlldefs.h"
#include "readers_writer_lock.h"
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition_variable.hpp>

#include <proton/container.hpp>
#include <proton/messaging_handler.hpp>
#include <proton/message.hpp>
#include <proton/connection_options.hpp>
#include <proton/connection.hpp>
#include <proton/sender.hpp>
#include <proton/receiver.hpp>
#include <proton/session.hpp>

namespace Cti::Messaging::Qpid
{

/*-----------------------------------------------------------------------------
  Managed destination
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedDestination : public proton::messaging_handler
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

    bool _readyToSend;

    std::queue<proton::message> _deferredMessages;

    ManagedProducer( proton::session & sess, const std::string & dest );

public:

    ~ManagedProducer();

    void setTimeToLiveMillis( std::chrono::milliseconds time );

    void send( proton::message & message );

    void on_sender_open( proton::sender & s ) override;
};


/*-----------------------------------------------------------------------------
  Managed message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG ManagedConsumer : public ManagedDestination
{
public:

    typedef std::function<void (proton::message&)> MessageCallback;
    typedef std::function<void(proton::receiver&)> OpenCallback;

protected:

    MessageCallback _msgCallback;
    OpenCallback    _openCallback;

    proton::receiver    _consumer;

    ManagedConsumer(proton::session& sess, const std::string& dest, MessageCallback c);
    ManagedConsumer(proton::session& sess, const std::string& dest, MessageCallback c, OpenCallback o);

public:

    ~ManagedConsumer();

    void on_message( proton::delivery & d, proton::message & msg ) override;
    void on_receiver_open( proton::receiver & rcvr ) override;
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

    DestinationConsumer(proton::session& sess, const std::string& dest, MessageCallback c);
    DestinationConsumer(proton::session& sess, const std::string& dest, MessageCallback c, OpenCallback o);

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

    QueueConsumer(proton::session& sess, const std::string& dest, MessageCallback c);
    QueueConsumer(proton::session& sess, const std::string& dest, MessageCallback c, OpenCallback o);

    ~QueueConsumer();
};

/*-----------------------------------------------------------------------------
  Managed topic message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TopicProducer : public DestinationProducer
{
public:

    TopicProducer(proton::session& sess, const std::string& dest);

    ~TopicProducer();
};

/*-----------------------------------------------------------------------------
  Managed topic message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TopicConsumer : public DestinationConsumer
{
protected:

    std::string _selector;

public:

    TopicConsumer(proton::session& sess, const std::string& dest, MessageCallback c, const std::string& selector = "");
    TopicConsumer(proton::session& sess, const std::string& dest, MessageCallback c, OpenCallback o, const std::string& selector = "");

    ~TopicConsumer();
};

/*-----------------------------------------------------------------------------
  Managed temporary queue message producer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TempQueueProducer : public DestinationProducer
{
public:

    TempQueueProducer(proton::session& sess, const std::string& dest);

    ~TempQueueProducer();
};

/*-----------------------------------------------------------------------------
  Managed temporary queue message consumer
-----------------------------------------------------------------------------*/
class IM_EX_MSG TempQueueConsumer : public QueueConsumer
{
public:

    TempQueueConsumer(proton::session& sess, MessageCallback c);
    TempQueueConsumer(proton::session& sess, MessageCallback c, OpenCallback o);

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

inline std::unique_ptr<QueueConsumer> createQueueConsumer( proton::session & sess, const std::string &queueName, ManagedConsumer::MessageCallback c )
{
    return std::make_unique<QueueConsumer>( sess, queueName, c );
}

inline std::unique_ptr<TopicProducer> createTopicProducer( proton::session& sess, const std::string& topicName )
{
    return std::make_unique<TopicProducer>(sess, topicName);
}

inline std::unique_ptr<TopicConsumer> createTopicConsumer( proton::session & sess, const std::string &topicName, ManagedConsumer::MessageCallback c )
{
    return std::make_unique<TopicConsumer>( sess, topicName, c );
}

inline std::unique_ptr<TopicConsumer> createTopicConsumer( proton::session & sess, const std::string &topicName, ManagedConsumer::MessageCallback c, const std::string &selector )
{
    return std::make_unique<TopicConsumer>( sess, topicName, c, selector );
}

inline std::unique_ptr<TempQueueProducer> createTempQueueProducer(proton::session& sess, const std::string& tempQueueName)
{
    return std::make_unique<TempQueueProducer>(sess, tempQueueName);
}

inline std::unique_ptr<TempQueueConsumer> createTempQueueConsumer( proton::session & sess, ManagedConsumer::MessageCallback c )
{
    return std::make_unique<TempQueueConsumer>( sess, c );
}

inline std::unique_ptr<TempQueueConsumer> createTempQueueConsumer(proton::session& sess, ManagedConsumer::MessageCallback c, ManagedConsumer::OpenCallback o)
{
    return std::make_unique<TempQueueConsumer>(sess, c, o);
}


}