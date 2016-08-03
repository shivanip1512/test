#pragma once

#include "connection.h"
#include "connection_listener.h"

class IM_EX_MSG CtiServerConnection : public CtiConnection
{
    virtual bool establishConnection ();

    const std::unique_ptr<cms::Destination> _replyDest;

public:

    CtiServerConnection ( const CtiListenerConnection &listenerConnection,
                          Que_t *inQ = NULL,
                          int termSeconds = 3 );

    virtual ~CtiServerConnection();
};
