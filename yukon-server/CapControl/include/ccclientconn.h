#pragma once

#include "message.h"
#include "connection_server.h"


class CtiCCClientConnection
{
    bool _valid;

    CtiServerConnection _connection;

public:

    CtiCCClientConnection( const std::string & replyDestination, const std::string & serverQueueName );
    
    virtual ~CtiCCClientConnection();

    bool isValid();

    void close();

    void write(CtiMessage* msg);

    void start();
};


