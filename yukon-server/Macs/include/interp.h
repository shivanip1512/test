/*-----------------------------------------------------------------------------*
*
* File:   interp
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/interp.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  interp.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiMCInterpreter
                    CtiMCInterpreter provides an interface to an interpreter
                    implementation that executes MC scripts as well as
                    stand alone commands.

    Initial Date:  4/7/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTIMCINTERPRETER_H
#define CTIMCINTERPRETER_H

#include <rw/cstring.h>
#include <rw/tpslist.h>
#include <rw/thr/thread.h>
#include <rw/thr/thrfunc.h>
#include <rw/thr/recursiv.h>

#include <tcl.h>

#include "ctdpcptrq.h"
#include "types.h"

struct Tcl_Interp;

class CtiMCInterpreter
{
friend RWTPtrSlist<CtiMCInterpreter>;

public:
    enum
    {
        WAITING,
        EVALUATING
    };

    CtiMCInterpreter();
   virtual ~CtiMCInterpreter();


    BOOL isEvaluating() const;

    void stopEval();

    static CtiMCInterpreter* evaluate(RWCString& command);
    static CtiMCInterpreter* evaluate(RWCString& command, RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue);
    static CtiMCInterpreter* evaluateFile(RWCString& file);
    static CtiMCInterpreter* evaluateFile(RWCString& file, RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue);

    static void shutdownAllInterpreters();

protected:
    Tcl_Interp* _interp;
    RWThread _evalthr;

private:
    BOOL _dostop;
    BOOL _isevaluating;
    BOOL _evalasfile;

    RWCString _evalstring;
    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > _queue;
    //mutable RWRecursiveLock<RWMutexLock> _mutex;

    static RWTPtrSlist<CtiMCInterpreter> _interplist;
    static RWRecursiveLock<RWMutexLock> _mutex;

    static const CHAR _loadcommand[];

    void _evalloop();

    static CtiMCInterpreter* getAvailableInterpreter();

    //callback functions for tcl interpreter

    //called before an event is deleted
    static int event_delete_proc( Tcl_Event* event, void* flags){ return 1; }

    //called to setup the event source - before event_check_proc don't need to do anything
    static void event_setup_proc( ClientData clientData, int flags )
    { };

    //called to check if an event should be added to one of the threads queues
    static void event_check_proc( ClientData clientData, int flags );

    //the actual event handler
    static int event_proc(Tcl_Event* evtPtr, int flags );

};

#endif
