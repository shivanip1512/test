#pragma once

#include "dlldefs.h"
#include "critical_section.h"
#include "connection.h"

#include <proton/session.hpp>
#include <proton/messaging_handler.hpp>


class IM_EX_MSG CtiListenerConnection
    :   public Cti::Messaging::BaseConnection,
        public proton::messaging_handler
{
    const std::string _serverQueueName;

    proton::session     _session;

    std::unique_ptr<Cti::Messaging::Qpid::QueueConsumer> _consumer;

    std::queue<std::string> _clients;

    std::mutex  _clientMux;
    std::condition_variable _clientCond;

    bool _ready;

    typedef std::map<std::string, CtiTime> DestTimeMap;
    DestTimeMap requestTimeMap;

    const std::string _title;

    bool validateRequest( const std::string &replyTo );

public:

    void on_session_open(proton::session& session) override;

    CtiListenerConnection( const std::string &serverQueueName );
    virtual ~CtiListenerConnection();

    void close() override { }   // jmoc -- need to revisit this in the base class...

    std::string acceptClient();

    std::string who() const;
};



