#pragma once

#include "connection_server.h"


class CtiMCConnection
{
    bool _valid;

    CtiServerConnection _connection;

public:

    CtiMCConnection( const std::string & replyToName, const std::string & serverQueueName, CtiConnection::Que_t *inQ );

    ~CtiMCConnection();

    bool isValid();

    void start();

    void write(CtiMessage* msg);

    bool hasConnection(const Cti::ConnectionHandle connectionHandle) const;
};
