/*-----------------------------------------------------------------------------
    Filename:  clientconn.h
                
    Programmer:  Josh Wolberg
    
    Description: Header file for CtiCCConnection
        
    Initial Date:  8/16/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTICCCONNECTION_H
    #define CTICCCONNECTION_H

    #include <rw/pstream.h>

    #include <rw/toolpro/portal.h>
    #include <rw/toolpro/portstrm.h>
     
    #include <rw/thr/countptr.h> 
    #include <rw/thr/thrfunc.h> 

    #include "ctdpcptrq.h"
    #include "observe.h"
    
class CtiCCConnection : public CtiObserver
{
public:
    CtiCCConnection(RWPortal portal);
    virtual ~CtiCCConnection();

    bool isValid() const;

    void close();

    //Inherited from CtiObserver
    void update(CtiObservable& observable);

protected:
    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > _queue;

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
};

#endif


