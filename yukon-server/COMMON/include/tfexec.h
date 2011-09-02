#pragma once
     
#include <rw/thr/thread.h>
#include <rw/thr/thrfunc.h>
#include <rw/thr/prodcons.h>
#include <rw/thr/func0.h>

class CtiTimedFunctorExecutor
{
  public:
  
    CtiTimedFunctorExecutor(long millis);
    virtual ~CtiTimedFunctorExecutor();

    void enqueue(const RWFunctor0& functor);
  
private:
    RWThread _timer_thr;

    CTIPCPtrQueue<RWFunctor0> _functor_queue;
    long _millis;

    void _timed_thr_func();
};
