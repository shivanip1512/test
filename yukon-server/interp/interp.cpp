/*-----------------------------------------------------------------------------*
*
* File:   mc_interp
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_interp.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/02/10 23:24:04 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <fstream>
using namespace std;

#include <tcl.h>
#include <rw/thr/threadid.h>
#include <rw/rwtime.h>

#include "interp.h"
#include "ctibase.h"

 RWRecursiveLock<RWMutexLock> CtiInterpreter::_mutex;

/*---------------------------------------------------------------------------
    Constructor

    Creates a Tcl interpreter, loads the MACS package into the interpreter
    and sets the temporary file name used to obtain results
---------------------------------------------------------------------------*/
CtiInterpreter::CtiInterpreter()
: _isevaluating(false), _queue(0), _dostop(false), _block(false), _eval_barrier(2)
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

bool CtiInterpreter::evaluate(const string& command, bool block )
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
        _evalstring = (const char*) command.data();
        _block = block;

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
        _evalstring = (const char*) file.data();
        _block = block;

        interrupt( CtiInterpreter::EVALUATE_FILE );

        if( block )
            _eval_barrier.wait();
    }
                                     
    return true;
}

/*---------------------------------------------------------------------------
    isEvaluating

    Returns true if the interpreter eval thread is running, false otherwise
---------------------------------------------------------------------------*/
bool CtiInterpreter::isEvaluating() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    return _isevaluating;
}

/*---------------------------------------------------------------------------
    stopEval

    Causes the current _evalthr to stop running.
---------------------------------------------------------------------------*/
void CtiInterpreter::stopEval()
{
    _dostop = true;

    while ( _isevaluating )
        rwSleep( 50 );
    
    _dostop = false;
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
    Tcl_Interp* interp;

    try
    {
        interp = Tcl_CreateInterp();

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
                    int r = Tcl_Eval( interp, (char*) _evalstring.data() );

                    if( r != 0 )
                    {
                       char* result = Tcl_GetStringResult(interp);

                       // check for interrupted as to not alarm the reader
                       if( strcmp(result,"interrupted") != 0)
                       {                       
                           CtiLockGuard< CtiLogger > guard(dout);
                            
                           char* err = Tcl_GetVar(interp, "errorInfo", 0 );
                           if( err != NULL )
                           {
                                dout << RWTime() << " [" << rwThreadId() << "] INTERPRETER ERROR: " << endl;                
                                dout << RWTime() << " " << err << endl;
                           }                                               
                       }
                    }
                }
                else
                if( isSet( CtiInterpreter::EVALUATE_FILE ) )
                {
                    int r = Tcl_EvalFile( interp, (char*) _evalstring.data() );
		    if(r != 0)
		    {
			char* result = Tcl_GetStringResult(interp);
                        CtiLockGuard< CtiLogger > guard(dout);
                            
                        char* err = Tcl_GetVar(interp, "errorInfo", 0 );
                        if( err != NULL )
                        {
			  dout << RWTime() << " [" << rwThreadId() << "] INTERPRETER ERROR: " << endl;                
                          dout << RWTime() << " " << err << endl;
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
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " An unknown exception occured in CtiInterpreter::run()" << endl;
    }

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " Interpreter shutting down." << endl;
    }

    Tcl_DeleteInterp(interp);


    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " interp exiting" << endl;
    }
}
      


/*-----------------------------------------------------------------------------
    event_check_proc

    Called from Tcl to check if an event should be added to one of the threads
    queues
---------------------------------------------------------------------------*/
void CtiInterpreter::event_check_proc( ClientData clientData, int flags )
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    //find the interpreter who is associated with this thread and
    //post an event the interpreters event queue if _dostop is set
    CtiInterpreter* interp = (CtiInterpreter*) clientData;

    if ( interp->_dostop )
    {  
        int id = interp->getID();

        Tcl_Event* event = (Tcl_Event*) Tcl_Alloc( sizeof( Tcl_Event) );

        event->proc = event_proc;
        event->nextPtr = NULL;

        Tcl_ThreadQueueEvent( (Tcl_ThreadId) id, event, TCL_QUEUE_HEAD );
        Tcl_ThreadAlert( (Tcl_ThreadId) id );

        interp->_dostop = false;
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
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return 1;
}

