#pragma once

#include "critical_section.h"
#include "guard.h"
#include "thread.h"

#include <tcl/tcl.h>

#include <boost/thread/barrier.hpp>

#include <set>

class CtiInterpreter : public CtiThread
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiInterpreter(const CtiInterpreter&);
    CtiInterpreter& operator=(const CtiInterpreter&);

public:

    typedef int (&InitFunction)(Tcl_Interp *);

    CtiInterpreter(InitFunction initFunction, std::set<std::string> macsCommands);
    virtual ~CtiInterpreter();

    bool evaluateRaw(const std::string& command, bool block = true, void (*preEval)(CtiInterpreter* interp) = NULL, void (*postEval)(CtiInterpreter* interp) = NULL);
    bool evaluate(const std::string& command, bool block = true, void (*preEval)(CtiInterpreter* interp) = NULL, void (*postEval)(CtiInterpreter* interp) = NULL);
    void setScheduleId(long schedId);

    long getScheduleId();
    Tcl_Interp *getTclInterpreter();
    bool isEvaluating() const;
    void stopEval();

    virtual void run();

protected:

    Tcl_Interp* _interp;

    std::string escapeQuotationMarks(const std::string &command);
    bool isEscapeCommand(const std::string &command);

private:

    InitFunction _initFunction;

    enum
    {
        EVALUATE = CtiThread::LAST,
        WAITING
    };

    boost::barrier _eval_barrier;

    void (*preEvalFunction)(CtiInterpreter* interp);
    void (*postEvalFunction)(CtiInterpreter* interp);
    volatile bool _isevaluating;
    volatile bool _dostop;
    volatile bool _block;

    long _scheduleId;

    std::string _evalstring;

    std::set<std::string> _macsCommands;

    static CtiCriticalSection _mutex;

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
