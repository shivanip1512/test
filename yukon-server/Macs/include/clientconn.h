
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   clientconn
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/clientconn.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/05/26 20:57:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  clientconn.h

    Programmer:  Aaron Lauinger

    Description: Header file for CtiMCConnection
                 CtiMCConnection is thread hot.
                 When given a RWPortal it spawns two threads, an send thread
                 and and a receive thread.  It contains two producer
                 consumer queues to store outgoing and incoming messages.

                 It inherits from CtiObserverable in order to notify
                 any CtiObservers that a message has been received or the
                 status of the connection has changed.  It is up to the

                 A reference to the CtiMCConnection as a CtiObservable
                 will be passed to the update function of the CtiObserver.
                 It is up to the observer to query the connection to see
                 if it cares about the state change.

                 The observer can cast the Observable& to a CtiMCConnection&
                 and use the read() member functions to obtain any messages
                 that have been received.

                 See CtiObservable::addObserver(..) and
                 CtiObservable::deleteObserver(...).

    Initial Date:  5/12/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTIMCCONNECTION_H
#define CTIMCCONNECTION_H

#include <rw/pstream.h>
#include <rw/rwtime.h>

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
    RWPCPtrQueue< RWCollectable >  _in;
    RWPCPtrQueue< RWCollectable > _out;

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

#endif


