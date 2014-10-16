#include "precompiled.h"

#include "dllbase.h"
#include "interp.h"
#include "logger.h"

#include <tcl.h>

#include <rw/thr/thrutil.h>

#include <boost/algorithm/string.hpp>

using namespace std;

CtiCriticalSection CtiInterpreter::_mutex;

/*---------------------------------------------------------------------------
    Constructor

    Creates a Tcl interpreter, loads the MACS package into the interpreter
    and sets the temporary file name used to obtain results
---------------------------------------------------------------------------*/
CtiInterpreter::CtiInterpreter(InitFunction initFunction, std::set<string> macsCommands)
: _initFunction(initFunction), _isevaluating(false), _dostop(false), _block(false), _eval_barrier(2), _interp(NULL), preEvalFunction(NULL), postEvalFunction(NULL), _scheduleId(0), _macsCommands(macsCommands)
{
    set( CtiInterpreter::EVALUATE, false);
}

/*---------------------------------------------------------------------------
    Destructor

---------------------------------------------------------------------------*/
CtiInterpreter::~CtiInterpreter()
{
}

string CtiInterpreter::escapeQuotationMarks(const string &command)
{
    string tok_str, escaped;

    std::vector<std::string> input_lines, output_lines;

    // The command string may have multiple commands, separated by newline characters.
    boost::split(input_lines, command, boost::is_any_of("\n"));

    for each( const std::string &input in input_lines )
    {
        if( isEscapeCommand(input) )
        {
            output_lines.push_back(
               boost::algorithm::replace_all_copy(input, "\"", "\\\""));
        }
        else
        {
            output_lines.push_back(input);
        }
    }

    return boost::join(output_lines, "\n");
}

bool CtiInterpreter::isEscapeCommand(const string &command)
{
    const size_t start_pos = command.find_first_not_of(" \t");

    //  No non-whitespace characters found.
    if( start_pos == string::npos )
    {
        return false;
    }

    const size_t end_pos = command.find_first_of(" \t", start_pos);

    size_t count = string::npos;

    if( end_pos != string::npos )
    {
        count = end_pos - start_pos;
    }

    const string strCmd = command.substr(start_pos, count);

    return _macsCommands.find(strCmd) != _macsCommands.end();
}

bool CtiInterpreter::evaluate(const string& command, bool block, void (*preEval)(CtiInterpreter* interp), void (*postEval)(CtiInterpreter* interp))
{
    string escapedString = escapeQuotationMarks(command);

    return evaluateRaw(escapedString, block, preEval, postEval);
}

bool CtiInterpreter::evaluateRaw(const string& command, bool block, void (*preEval)(CtiInterpreter* interp), void (*postEval)(CtiInterpreter* interp))
{
    //Hack to handle the condition that the thread hasn't started up yet
    //but evaluate has been called

    while( isSet( CtiThread::STARTING ) ||
          !isSet( CtiInterpreter::WAITING ) )

    {
        Sleep(200);
    }

    {
        _isevaluating = true;
        _evalstring = command;
        _block = block;

        if( preEval != NULL )
        {
            preEvalFunction = preEval;
        }
        if( postEval != NULL )
        {
            postEvalFunction = postEval;
        }

        interrupt( CtiInterpreter::EVALUATE );

        if( block )
            _eval_barrier.wait();
    }

    return true;
}

void CtiInterpreter::setScheduleId(long schedId)
{
    _scheduleId = schedId;
}

long CtiInterpreter::getScheduleId()
{
    return _scheduleId;
}

/*---------------------------------------------------------------------------
    isEvaluating

    Returns true if the interpreter eval thread is running, false otherwise
---------------------------------------------------------------------------*/
bool CtiInterpreter::isEvaluating() const
{
    CtiLockGuard<CtiCriticalSection> guard(_mutex);

    return _isevaluating;
}

/*---------------------------------------------------------------------------
    stopEval

    Causes the current _evalthr to stop running.
---------------------------------------------------------------------------*/
void CtiInterpreter::stopEval()
{
    _dostop = true;

    while ( _isevaluating && isRunning() )
        rwSleep( 50 );

    _dostop = false;
}

Tcl_Interp *CtiInterpreter::getTclInterpreter()
{
    return _interp;
}

/*---------------------------------------------------------------------------
    run

    This function has a Tcl interpreter that it uses to evaluate files.
    The Tcl interpreter must be created in this thread!
    Most of the tcl functions use thread specific storage, and calling
    them from different threads really screws it up.
---------------------------------------------------------------------------*/
void CtiInterpreter::run()
{
    try
    {
        _interp = Tcl_CreateInterp();

        _initFunction(_interp);

        Tcl_CreateEventSource( &CtiInterpreter::event_setup_proc, &CtiInterpreter::event_check_proc, this );

        for ( ; ; )
        {
            {
                // Indicates we are ready, really only needed on the
                // first pass, but _eval_b MUST be acquired before we
                // set it or the first call to evaluate might not
                // wait till we are ready
                set( CtiInterpreter::WAITING, true );

                sleep( numeric_limits<int>::max()  );

                if( isSet( CtiThread::SHUTDOWN ) )
                {
                    break;
                }

                if( isSet( CtiInterpreter::EVALUATE ) )
                {
                    if( preEvalFunction != NULL)
                    {
                        (*preEvalFunction)(this);
                        preEvalFunction = NULL;
                    }

                    int r = Tcl_Eval( _interp, (char*) _evalstring.c_str() );

                    if( postEvalFunction != NULL)
                    {
                        (*postEvalFunction)(this);
                        postEvalFunction = NULL;
                    }

                    if( r != 0 )
                    {
                       char* result = Tcl_GetStringResult(_interp);

                       // check for interrupted as to not alarm the reader
                       if( strcmp(result, "interrupted") != 0)
                       {
                           if( const char* err = Tcl_GetVar(_interp, "errorInfo", 0) )
                           {
                                CTILOG_ERROR(dout, "Interpreter Error: "<< err);
                           }
                       }
                    }
                }

                set( CtiInterpreter::EVALUATE, false );
                _isevaluating = false;
            }

            if( _block )
            {
                _eval_barrier.wait();
            }
        }

        Tcl_DeleteInterp(_interp);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "Interpreter shutting down.");
}



/*-----------------------------------------------------------------------------
    event_check_proc

    Called from Tcl to check if an event should be added to one of the threads
    queues
---------------------------------------------------------------------------*/
void CtiInterpreter::event_check_proc( ClientData clientData, int flags )
{
    CtiLockGuard<CtiCriticalSection> guard(_mutex);

    //find the interpreter who is associated with this thread and
    //post an event the interpreters event queue if _dostop is set
    CtiInterpreter* interpreter = (CtiInterpreter*) clientData;

    if ( interpreter->_dostop )
    {
        int id = interpreter->getID();

        Tcl_Event* event = (Tcl_Event*) Tcl_Alloc( sizeof( Tcl_Event) );

        event->proc = event_proc;
        event->nextPtr = NULL;

        Tcl_ThreadQueueEvent( (Tcl_ThreadId) id, event, TCL_QUEUE_HEAD );
        Tcl_ThreadAlert( (Tcl_ThreadId) id );

        interpreter->_dostop = false;
    }

}

/*---------------------------------------------------------------------------
    event_proc

    The actual event handler - sets the interpreter to !_isevaluating so
    that the eval loop will go back to sleep after it returns.
    Returning 1 signifies that we handled the event.
---------------------------------------------------------------------------*/
int CtiInterpreter::event_proc(Tcl_Event* evtPtr, int flags )
{
    return 1;
}

