#pragma once

#include <rw/pstream.h>

#include <rw/toolpro/portal.h>
#include <rw/toolpro/portstrm.h>
 
#include <rw/thr/countptr.h> 
#include <rw/thr/thrfunc.h> 
#include <rw/toolpro/sockaddr.h>

#include "ctdpcptrq.h"
#include "observe.h"
    
class CtiCCClientConnection
{
public:
    CtiCCClientConnection(RWPortal portal);
    virtual ~CtiCCClientConnection();

    BOOL isValid() const;

    void close();

    void write(RWCollectable* msg);

    RWSockAddr getConnectionName();
    RWSockAddr getPeerName();

protected:
    CtiPCPtrQueue< RWCollectable > _queue;

    void _sendthr();
    void _recvthr();

private:    
    BOOL _valid;

    RWPortal* _portal;
    RWPortalStreambuf* sinbuf;
    RWPortalStreambuf* soubuf;
    RWpostream* oStream;
    RWpistream* iStream;

  
    RWThread _recvrunnable;
    RWThread _sendrunnable;
};


