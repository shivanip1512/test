#pragma once

#include "connection_server.h"


class CtiMCConnection
{
    bool _valid;

    CtiServerConnection _connection;

    CtiMCConnection();

public:

    CtiMCConnection( CtiListenerConnection& listenerConn, CtiConnection::Que_t *inQ );

    ~CtiMCConnection();

    bool isValid();

    void start();
    
    void write(CtiMessage* msg);

    bool hasConnection(const Cti::ConnectionHandle connectionHandle) const;
};
