#pragma once

#include <rw/pstream.h>

#include <rw/toolpro/portal.h>
#include <rw/toolpro/portstrm.h>

#include <rw/thr/countptr.h>
#include <rw/thr/thrfunc.h>

#include "mc.h"
#include "observe.h"
#include "guard.h"
#include "logger.h"
#include "mutex.h"

#include "message.h"
#include "connection_server.h"

class CtiMCConnection : public CtiObservable
{
    bool _valid;

    CtiServerConnection _connection;

    CtiMCConnection();

public:

    CtiMCConnection( CtiListenerConnection& listenerConn );
    ~CtiMCConnection();

    void start();
    
    BOOL isValid();

    void close();

    void write(CtiMessage* msg);

    //blocking - closing or destroying the connection
    //will cause them to return
    CtiMessage* read();
    CtiMessage* read(unsigned long millis);

    bool operator==(const CtiMCConnection& conn)
    {
        return (this == &conn);
    }
};
