#pragma once

#include <string>
#include "dlldefs.h"
#include "connection.h"

class IM_EX_MSG CtiListenerConnection
{
    const std::string _brokerUri;
    const std::string _serverQueueName;

    std::auto_ptr<cms::Connection>      _connection;
    std::auto_ptr<cms::Session>         _session;
    std::auto_ptr<cms::Destination>     _destination;
    std::auto_ptr<cms::MessageConsumer> _consumer;
    std::auto_ptr<cms::Destination>     _clientReplyDest;

    bool _closed;
    bool _valid;

    static volatile long _listenerConnectionCount;

    const std::string _title;

    void logStatus    ( std::string funcName, std::string note ) const;
    void logException ( std::string fileName, int line, std::string exceptionName = "", std::string note = "" ) const;

public:

    CtiListenerConnection( const std::string &serverQueueName );
    virtual ~CtiListenerConnection();

    void close();

    int verifyConnection();
    int establishConnection();
    int acceptClient();

    std::auto_ptr<cms::Destination> getClientReplyDest();
    std::auto_ptr<cms::Session>     createSession();

    std::string who() const;
};



