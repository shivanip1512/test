/*-----------------------------------------------------------------------------
    Filename:  ccclientconn.h
                
    Programmer:  Josh Wolberg
    
    Description: Header file for CtiCCClientConnection
        
    Initial Date:  9/04/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTICCCLIENTCONNECTION_H
#define CTICCCLIENTCONNECTION_H

#include <rw/pstream.h>

#include <rw/toolpro/portal.h>
#include <rw/toolpro/portstrm.h>
 
#include <rw/thr/countptr.h> 
#include <rw/thr/thrfunc.h> 

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

protected:
    RWPCPtrQueue< RWCollectable > _queue;

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

#endif


