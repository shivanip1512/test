#include "precompiled.h"


#include <fstream>
using namespace std;

#include <tcl.h>
#include <rw/thr/threadid.h>

#include "interp.h"
#include "ctibase.h"

CtiCriticalSection CtiInterpreter::_mutex;

/*---------------------------------------------------------------------------
    Constructor

    Creates a Tcl interpreter, loads the MACS package into the interpreter
    and sets the temporary file name used to obtain results
---------------------------------------------------------------------------*/
CtiInterpreter::CtiInterpreter(std::set<string> macsCommands)
: _isevaluating(false), _dostop(false), _block(false), _eval_barrier(2), _interp(NULL), preEvalFunction(NULL), postEvalFunction(NULL), _scheduleId(0), _macsCommands(macsCommands)
{
    set( CtiInterpreter::EVALUATE, false);
    set( CtiInterpreter::EVALUATE_FILE, false );
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

    boost::char_separator<char> sep("\n");
    Boost_char_tokenizer tok(command, sep);
    Boost_char_tokenizer::iterator tok_iter = tok.begin();

    while( (tok_iter != tok.end()) && !(tok_str = *tok_iter++).empty() )
    {
        // trim whitespace in the front
        trim_left(tok_str);

        if (isEscapeCommand(tok_str))
        {
            // command is a macs command, we need to escape any non-escaped quotes
            size_t n = tok_str.length();

            for (int i = 0; i < n; i++)
            {
                switch (tok_str[i])
                {
                    case '\"':
                        escaped.append(1, '\\');
                    default:
                        escaped.append(1, tok_str[i]);
                }
            }
        }
        else
        {
            escaped.append(tok_str);
        }
    }

    return escaped;
}

bool CtiInterpreter::isEscapeCommand(const string &command)
{
    size_t pos = command.find(' ');

    string strCmd = command.substr(0, pos);

    std::set<string>::iterator itr = _macsCommands.find(strCmd);

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

bool CtiInterpreter::evaluateFile(const string& file, bool block )
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
        _evalstring = escapeQuotationMarks(file);;
        _block = block;

        interrupt( CtiInterpreter::EVALUATE_FILE );

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
                else
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
                       if( strcmp(result,"interrupted") != 0)
                       {
                           CtiLockGuard< CtiLogger > guard(dout);

                           char* err = Tcl_GetVar(_interp, "errorInfo", 0 );
                           if( err != NULL )
                           {
                                dout << CtiTime() << " [" << rwThreadId() << "] INTERPRETER ERROR: " << endl;
                                dout << CtiTime() << " " << err << endl;
                           }
                       }
                    }
                }
                else
                if( isSet( CtiInterpreter::EVALUATE_FILE ) )
                {
                    int r = Tcl_EvalFile( _interp, (char*) _evalstring.c_str() );
                    if(r != 0)
                    {
                        char* result = Tcl_GetStringResult(_interp);
                        CtiLockGuard< CtiLogger > guard(dout);

                        char* err = Tcl_GetVar(_interp, "errorInfo", 0 );
                        if( err != NULL )
                        {
                            dout << CtiTime() << " [" << rwThreadId() << "] INTERPRETER ERROR: " << endl;
                            dout << CtiTime() << " " << err << endl;
                        }
                    }
                }

                set( CtiInterpreter::EVALUATE, false );
                set( CtiInterpreter::EVALUATE_FILE, false );
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
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " An unknown exception occured in CtiInterpreter::run()" << endl;
    }

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " Interpreter shutting down." << endl;
    }


    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " _interp exiting" << endl;
    }
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
    //CtiLockGuard<CtiCriticalSection> guard(_mutex);
    return 1;
}

