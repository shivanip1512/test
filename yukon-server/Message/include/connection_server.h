#pragma once

#include "connection.h"
#include "connection_listener.h"

class IM_EX_MSG CtiServerConnection : public CtiConnection
{
    virtual bool establishConnection ();

    static volatile long _serverConnectionCount;

    const boost::shared_ptr<Cti::Messaging::ActiveMQ::ManagedConnection> _connection;

    const std::auto_ptr<cms::Destination> _replyDest;

public:

    CtiServerConnection ( const CtiListenerConnection &listenerConnection,
                          Que_t *inQ = NULL,
                          INT tt = 3 );

    virtual ~CtiServerConnection();
};
