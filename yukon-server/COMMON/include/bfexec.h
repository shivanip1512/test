#pragma once

#include <queue>

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

    //RWPCValQueue<RWFunctor0> _functor_queue;
    std::queue<RWFunctor0> _functor_queue;
    long _millis;

    void _timed_thr_func();
};
