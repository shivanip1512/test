#pragma once

#include <string>
#include "dlldefs.h"
#include "critical_section.h"
#include "connection.h"

class IM_EX_MSG CtiListenerConnection : public Cti::Messaging::BaseConnection
{
    const std::string _serverQueueName;

    boost::shared_ptr<Cti::Messaging::ActiveMQ::ManagedConnection> _connection;

    boost::scoped_ptr<cms::Session>                            _session;
    boost::scoped_ptr<cms::Destination>                        _clientReplyDest;
    boost::scoped_ptr<Cti::Messaging::ActiveMQ::QueueConsumer> _consumer;

    typedef std::map<std::string, CtiTime> DestTimeMap;
    DestTimeMap requestTimeMap;

    typedef Cti::readers_writer_lock_t Lock;
    typedef Lock::reader_lock_guard_t  ReaderGuard;
    typedef Lock::writer_lock_guard_t  WriterGuard;

    mutable Lock _connMux;

    bool _closed;
    bool _valid;

    const std::string _title;

    void releaseResources();
    bool validateRequest( const std::string &replyTo );

public:

    CtiListenerConnection( const std::string &serverQueueName );
    virtual ~CtiListenerConnection();

    void start();
    virtual void close();

    bool verifyConnection();
    bool acceptClient();

    boost::shared_ptr<Cti::Messaging::ActiveMQ::ManagedConnection> getConnection() const;

    std::auto_ptr<cms::Destination> getClientReplyDest() const;

    std::string who() const;
    std::string getServerQueueName() const;
};



