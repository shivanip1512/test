/*-----------------------------------------------------------------------------*
*
* File:   interp.h
*
* Date:   6/23/2003
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_interp.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/09/05 19:26:34 $
*
* Copyright (c) 2003, Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  interp.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiInterpreter
                    CtiInterpreter provides an interface to an interpreter
                    implementation that executes TCL scripts as well as
                    stand alone commands.

    Initial Date:  4/7/99
                   9/4/03

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999, 2003
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __INTERP__H__
#define __INTERP__H__

#include <rw/cstring.h>
#include <rw/tpslist.h>
#include <rw/thr/thread.h>
#include <rw/thr/thrfunc.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/barrier.h>

#include <tcl.h>

#include "mutex.h"
#include "ctdpcptrq.h"
#include "logger.h"
#include "guard.h"
#include "thread.h"

class IM_EX_INTERP CtiInterpreter : public CtiThread
{

public:
       
    CtiInterpreter();
    virtual ~CtiInterpreter();

    bool evaluate(const string& command, bool block = true );
    bool evaluateFile(const string& file, bool block = true );

    bool isEvaluating() const;
    void stopEval();

    virtual void run();

protected:

    Tcl_Interp* _interp;

private:

    // Two additional thread states
    enum
    {
        EVALUATE = CtiThread::LAST,
        EVALUATE_FILE,
        WAITING
    };

    RWBarrier _eval_barrier;    

    volatile bool _isevaluating;
    volatile bool _dostop;
    volatile bool _block;

    RWCString _evalstring;
    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > _queue;
    
    static RWRecursiveLock<RWMutexLock> _mutex;

    static const CHAR _loadcommand[];

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
