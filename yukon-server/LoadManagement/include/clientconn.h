/*-----------------------------------------------------------------------------
    Filename:  clientconn.h
                
    Programmer:  Josh Wolberg
    
    Description: Header file for CtiLMConnection
        
    Initial Date:  2/5/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMCONNECTION_H
#define CTILMCONNECTION_H

#include <rw/pstream.h>

#include <rw/toolpro/portal.h>
#include <rw/toolpro/portstrm.h>
 
#include <rw/thr/countptr.h> 
#include <rw/thr/thrfunc.h> 

#include "ctdpcptrq.h"
    
class CtiLMConnection
{
public:
    CtiLMConnection(RWPortal portal);
    virtual ~CtiLMConnection();

    bool isValid() const;

    void close();

    void write(RWCollectable* msg);

protected:
    RWPCPtrQueue< RWCollectable > _queue;

    void _sendthr();
    void _recvthr();

private:    
    RWMutexLock _sendmutex;
    RWMutexLock _recvmutex;

    bool _valid;

    RWPortal* _portal;
    RWPortalStreambuf *sinbuf;
    RWPortalStreambuf *soubuf;
    RWpostream        *oStream;
    RWpistream        *iStream;

    RWThread _recvrunnable;
    RWThread _sendrunnable;

    unsigned _max_out_queue_size;
};

#endif


