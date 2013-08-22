#pragma once

#include "message.h"
#include "connection_server.h"

class CtiCCClientConnection
{
    bool _valid;

    CtiServerConnection _connection;

public:

    CtiCCClientConnection( CtiListenerConnection& listenerConn );
    
    virtual ~CtiCCClientConnection();

    bool isValid();

    void close();

    void write(CtiMessage* msg);

    void start();
};


