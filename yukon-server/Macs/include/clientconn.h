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

class CtiMCConnection : public CtiObservable
{
public:

    CtiMCConnection();
    ~CtiMCConnection();

    void initialize(RWPortal portal);
    
    BOOL isValid();

    void close();

    void write(RWCollectable* msg);

    //blocking - closing or destroying the connection
    //will cause them to return
    RWCollectable* read();
    RWCollectable* read(unsigned long millis);

    bool operator==(const CtiMCConnection& conn)
    {
        return (this == &conn);
    }

protected:
    CtiPCPtrQueue< RWCollectable >  _in;
    CtiPCPtrQueue< RWCollectable > _out;

    void _sendthr();
    void _recvthr();

private:

    CtiMutex _mux;

    volatile bool _valid;
    volatile bool _closed;

    RWPortal* _portal;
    RWPortalStreambuf *sinbuf;
    RWPortalStreambuf *soubuf;
    RWpostream        *oStream;
    RWpistream        *iStream;


    RWThread _recvrunnable;
    RWThread _sendrunnable;

    void _close();
};
