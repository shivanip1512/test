#pragma once

#include <rw/thr/thread.h>
#include <rw/thr/thrfunc.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/barrier.h>

#include <tcl.h>

#include "mutex.h"
#include "ctdpcptrq.h"
#include "logger.h"
#include "critical_section.h"
#include "guard.h"
#include "thread.h"

class IM_EX_INTERP CtiInterpreter : public CtiThread
{

public:

    CtiInterpreter();
    virtual ~CtiInterpreter();

    bool evaluate(const std::string& command, bool block = true, void (*preEval)(CtiInterpreter* interp) = NULL, void (*postEval)(CtiInterpreter* interp) = NULL);
    bool evaluateFile(const std::string& file, bool block = true );
    void setScheduleId(long schedId);

    long getScheduleId();
    Tcl_Interp *getTclInterpreter();
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

    void (*preEvalFunction)(CtiInterpreter* interp);
    void (*postEvalFunction)(CtiInterpreter* interp);
    volatile bool _isevaluating;
    volatile bool _dostop;
    volatile bool _block;

    long _scheduleId;

    std::string _evalstring;

    static CtiCriticalSection _mutex;

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
