/*-----------------------------------------------------------------------------
    Filename:  tfexec.cpp
            
    Programmer:  Aaron Lauinger
    
    Description:    Source file for CtiTimedFunctorExecutor
            
    Initial Date:  4/25/99
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tfexec.h"

/*-----------------------------------------------------------------------------
    Constructor
    
    millis is the number of milliseconds between executing all of the functors
    in the queue.  A thread is started which will be stopped when the destructor
    is called.
-----------------------------------------------------------------------------*/    
CtiTimedFunctorExecutor::CtiTimedFunctorExecutor(long millis) : _millis(millis) 
{      
    RWThreadFunction timer_thr_func = 
    rwMakeThreadFunction( *this, &CtiTimedFunctorExecutor::_timed_thr_func );

    timer_thr_func.start();       
    _timer_thr = timer_thr_func;
};    

/*---------------------------------------------------------------------------
    Destructor
    
    Stops the timer/executor thread.
-----------------------------------------------------------------------------*/
CtiTimedFunctorExecutor::~CtiTimedFunctorExecutor()
{
    _timer_thr.requestCancellation();
}

/*-----------------------------------------------------------------------------
    enqueue
    
    Adds functor to the queue of functors to execute.
-----------------------------------------------------------------------------*/    
void CtiTimedFunctorExecutor::enqueue(const RWFunctor0& functor)
{
    _functor_queue.write(&functor);
}

/*-----------------------------------------------------------------------------
    _timed_thr_func
    
    The main loop which waits a specified amount of time and then executes
    all of the functors in it's queue.
    It periodically checks for cancellation.
-----------------------------------------------------------------------------*/    
void CtiTimedFunctorExecutor::_timed_thr_func()
{
    while (1)
    {
        //The functors won't be called exactely _millis often
        //but it should be close enough
        long count = _millis / 100L;

        for ( long i = 0; i < count ; i++ )
        {
            rwRunnable().serviceCancellation();
            rwRunnable().sleep(100);          
        }

        RWFunctor0* func;
        RWWaitStatus status;
        while ( _functor_queue.read( func, 0 ) != false )
        {
            *func();
        }
    }
}


