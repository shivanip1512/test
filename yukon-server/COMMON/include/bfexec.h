/*-----------------------------------------------------------------------------
    Filename:  tfexec.h
            
    Programmer:  Aaron Lauinger
    
    Description:    Header file for CtiBatchedFunctorExecutor.
                    CtiBatchedFunctorExecutor executes all the functors in it's
                    queue every certain number of milliseconds.  The functors
                    are removed from the queue once they are executed.
                    
                    See Rogue Wave docs on rwMakeFunctor and RWFunctor0
                                       
    Initial Date:  4/25/99
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __TFEXEC_H__
#define __TFEXEC_H__
     
#include <rw/thr/thread.h>
#include <rw/thr/thrfunc.h>
#include <rw/thr/prodcons.h>
#include <rw/thr/func0.h>

class CtiBatchedFunctorExecutor
{
  public:
  
    CtiBatchedFunctorExecutor(long millis);
    virtual ~CtiBatchedFunctorExecutor();

    void enqueue(const RWFunctor0& functor);
  
private:
    RWThread _timer_thr;

    RWPCValQueue<RWFunctor0> _functor_queue;
    long _millis;

    void _timed_thr_func();
};

#endif
