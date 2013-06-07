#pragma once

#include "connection.h"
#include "connection_listener.h"

class IM_EX_MSG CtiServerConnection : public CtiConnection
{
    virtual INT establishConnection ();
    virtual INT endConnection       ();

    static volatile long _serverConnectionCount;

public:

    CtiServerConnection ( CtiListenerConnection& listenerConnection,
                          Que_t *inQ = NULL,
                          INT tt = 3 );

    virtual ~CtiServerConnection();
};
