#pragma once

#include <rw/pstream.h>

#include <rw/toolpro/portal.h>
#include <rw/toolpro/portstrm.h>
 
#include <rw/thr/countptr.h> 
#include <rw/thr/thrfunc.h> 
#include <rw/toolpro/sockaddr.h>

#include "ctdpcptrq.h"
#include "observe.h"
    
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


