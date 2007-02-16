#if !defined(__RWTHRTHRPOOL_H__)
#  define __RWTHRTHRPOOL_H__
/*****************************************************************************
 *
 * thrpool.h 
 *
 * $Id$
 *
 * Copyright (c) 1996-1999 Rogue Wave Software, Inc.  All Rights Reserved.
 *
 * This computer software is owned by Rogue Wave Software, Inc. and is
 * protected by U.S. copyright laws and other laws and by international
 * treaties.  This computer software is furnished by Rogue Wave Software,
 * Inc. pursuant to a written license agreement and may be used, copied,
 * transmitted, and stored only in accordance with the terms of such
 * license and with the inclusion of the above copyright notice.  This
 * computer software or any other copies thereof may not be provided or
 * otherwise made available to any other person.
 *
 * U.S. Government Restricted Rights.  This computer software is provided
 * with Restricted Rights.  Use, duplication, or disclosure by the
 * Government is subject to restrictions as set forth in subparagraph (c)
 * (1) (ii) of The Rights in Technical Data and Computer Software clause
 * at DFARS 252.227-7013 or subparagraphs (c) (1) and (2) of the
 * Commercial Computer Software – Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 ****************************************************************************/

/*****************************************************************************

thrpool.h - Class declaration for:
   
   RWThreadPool - Handle for RWThreadPoolImp instances.

   RWThreadPoolImp - Encapsulates a pool of threads that execute enqueued
                     Functor objects. 

   The RWThreadPool object manages a "pool" of RWThread instances 
   that are used to execute work encapsulated into RWFunctor0 functors.
   A thread pool object, when started, waits for other threads to enqueue 
   work that they would like to have executed.  Each work functor
   that the thread pool finds in its internal queue is dequeued and is
   passed to the next available thread within the pool.
   This process continues until the object passes out of scope, 
   the destructor is directly called or the stop() member function is called.


See Also:

   thrpool.cpp - Out-of-line function definitions.

   thread.h - Contains RWThread declarations.

******************************************************************************/

#  if !defined(__RWTHRTHRFUNC_H__)
#     include <rw/thr/thrfunc.h>
#  endif

#  if !defined(__RWTHRPRODCONS_H__)
#     include <rw/thr/prodcons.h>
#  endif

#  if !defined(__RWTHRFUNC0_H__)
#     include <rw/thr/func0.h>
#  endif

const size_t RW_THR_NO_DYNAMIC_THREAD_POOL = 0;
const unsigned long RW_THR_NO_TIMEOUT = 0UL;


class RWTHRExport RWThreadPoolImp;
class RWTHRExport RWThreadPool;

class RWTHRExport RWThreadPool :
   public RWTHRHandle {

   RW_THR_DECLARE_TRACEABLE

   friend class RWThreadPoolImp;

   public:

      // Construct an empty (invalid) handle
      RWThreadPool(void)
         RWTHRTHROWSANY;

      // Construct an external interface handle to the threadpool object 
      // pointed-to by a second handle (if any).
      RWThreadPool(const RWThreadPool& second)
         RWTHRTHROWSANY;

      // Bind this external interface handle to the threadpool object 
      // pointed-to by a second handle (if any)
      RWThreadPool&
      operator=(const RWThreadPool& second)
         RWTHRTHROWSANY;

      // Enqueue a piece of work onto the thread pool
      void 
      enqueue(const RWFunctor0& functor)
         RWTHRTHROWSANY;

      // Returns the number of work entries queued in the thread pool.  
      size_t 
      entries(void) const
         RWTHRTHROWSANY;


      // Get a handle to the thread attribute instance specified during 
      // thread pool construction.  
      // There is no setPoolAttribute() member function because 
      // RWThreadPool enqueues functors, not runnables.
      RWThreadAttribute
      getPoolAttribute(void) 
         RWTHRTHROWSANY;

      // Make a thread pool instance with a pool that contains  from
      // minThreads to maxThreads threads. The actual number
      // of threads will grow and shrink between minThreads and 
      // maxThreads depending on the work load. minThreads must be >= 1.
      // The timeout determines how long
      // a thread will wait for work before exiting. The timeout is in
      // milliseconds. The thread pool threads are created with default 
      // thread attributes.
      static
      RWThreadPool
      make(size_t minThreads,
           size_t maxThreads = RW_THR_NO_DYNAMIC_THREAD_POOL,
           unsigned long timeout = RW_THR_NO_TIMEOUT)
         RWTHRTHROWSANY;


      // Make a thread pool instance with a pool that contains  from
      // minThreads to maxThreads threads each created 
      // with the specified thread attributes. The actual number
      // of threads will grow and shrink between minThreads and maxThreads
      // depending on the work load. The timeout determines how long
      // a thread will wait for work before exiting. The timeout is in
      // milliseconds. 
      // If you set the start policy of poolThreadsAttr to 
      // RW_THR_START_INTERRUPTED, then the thread pool won't
      // do any work because all of the threads will be waiting
      // for releaseInterrupt() calls.
      static
      RWThreadPool
      make(size_t minThreads,
           const RWThreadAttribute& poolThreadsAttr, 
           size_t maxThreads = RW_THR_NO_DYNAMIC_THREAD_POOL,
           unsigned long timeout = RW_THR_NO_TIMEOUT)
         RWTHRTHROWSANY;


      // Returns the size of the thread pool, that is, the 
      // number of threads presently in the pool.   
      size_t
      size(void) const
         RWTHRTHROWSANY;


      // Request that the pool stops execution after it executes 
      // the work that is currently enqueued. No additional work may
      // be enqueued after stop() is called.
      void
      stop(void)
         RWTHRTHROWSANY;


   protected:

      // Construct an external interface handle to a RWThreadPoolImp instance
      RWThreadPool(RWThreadPoolImp* threadImpP)
         RWTHRTHROWSANY;
      
      // Override the parent class body(void) function to provide
      // version that casts to this handle's body class
      RWThreadPoolImp&
      body(void) const
         RWTHRTHROWSANY;
};

class RWTHRExport RWThreadPoolImp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE

   friend class RWThreadPool;

   private:

      // The number of threads that are doing work.  
      size_t   committedThreadCount_;
      
      // The current number of threads in the pool.
      size_t   currentThreadCount_;

      // The minimum number of threads in the pool. Set at construction.
      size_t   minThreadCount_;  

      // The maximum number of threads in the pool. Set at construction.
      size_t   maxThreadCount_;  

      // A handle to the thread attribute instance used when creating the 
      // threads for the thread pool.  Thread attributes can not be changed
      // once the thread pool is started.
      RWThreadAttribute initialPoolAttribute_;


      // True if the pool can grow and shrink
      RWBoolean   dynamicallyAdjustable_;
        
      // List of all threads in the pool.
      RWTValSlist<RWThread>    threadList_;

      // PC queue of functors to be executed by threads in the pool.
      RWPCValQueue<RWFunctor0> workQueue_;
      
      // The functor that is the entry point to worker threads
      RWFunctor0 workerFunctor_;

      // Lock for the ...Count_ member variables
      RWMutexLock  countMutex_;

      // Lock for threadList_
      RWMutexLock  threadListMutex_;
  
   protected:

      // Construct a thread pool instance with a pool that contains from
      // minThreads to maxThreads threads. The actual number
      // of threads will grow and shrink between minThreads and 
      // maxThreads depending on the work load. minThreads must be >= 1.
      // The timeout determines how long
      // a thread will wait for work before exiting. The timeout is in
      // milliseconds. The thread pool threads are created with default 
      // thread attributes.
      RWThreadPoolImp(size_t minThreads,
                      size_t maxThreads = RW_THR_NO_DYNAMIC_THREAD_POOL,
                      unsigned long timeout = RW_THR_NO_TIMEOUT)

         RWTHRTHROWSANY;

      // Construct a thread pool instance with a pool that contains from
      // minThreads to maxThreads threads each created 
      // with the specified thread attributes. The actual number
      // of threads will grow and shrink between minThreads and maxThreads
      // depending on the work load. The timeout determines how long
      // a thread will wait for work before exiting. The timeout is in
      // milliseconds. 
      RWThreadPoolImp(size_t minThreads,
           const RWThreadAttribute& poolThreadsAttr, 
           size_t maxThreads = RW_THR_NO_DYNAMIC_THREAD_POOL,
           unsigned long timeout = RW_THR_NO_TIMEOUT)
         RWTHRTHROWSANY;

      // Thread pool implementation destructor. Prevents further work
      // from being enqueued on the pool and waits for all pool threads
      // to complete current work.
      ~RWThreadPoolImp(void)
         RWTHRTHROWSANY;

      // Enqueue a piece of work onto the thread pool    
      void 
      enqueue(const RWFunctor0& functor)
         RWTHRTHROWSANY;


      // Returns the number of work entries queued in the thread pool.  
      size_t 
      entries(void) const
         RWTHRTHROWSANY;


      // Get a handle to the thread attribute instance specified during 
      // thread pool construction.  
      // There is no setPoolAttribute() member function because 
      // RWThreadPool enqueues functors, not runnables.
      RWThreadAttribute
      getPoolAttribute(void) 
         RWTHRTHROWSANY;


      // Returns the size of the thread pool, that is, the 
      // number of threads presently in the pool.   
      size_t
      size(void)       // can't be const because use threadListMutex_.
         RWTHRTHROWSANY;


      // Request that the pool stops execution after it executes 
      // the work that is currently enqueued.
      void
      stop(void)
         RWTHRTHROWSANY;


   private:

      // Don't allow direct copy construction or assignment

      RWThreadPoolImp(const RWThreadPoolImp&);

      RWThreadPoolImp&
      operator=(const RWThreadPoolImp&);

      // Create a new thread and add it to the pool.
      void
      addPoolThread(void)
         RWTHRTHROWSANY;

      // Initialize the thread pool instance
      void
      init(void)
         RWTHRTHROWSANY;

      // removes the current thread from the thread pool list
      void
      removeSelfFromPool(void)
         RWTHRTHROWSANY;

      // The non-adjustable pool thread entry point. Dequeues and executes  
      // functors from the functor queue.
      void
      worker(void)
         RWTHRTHROWSANY;

      // An adjustable pool thread entry point. Dequeues and executes  
      // functors from the functor queue. Adjusts committedThreadCount_.
      void
      workerWithAdjustment(void)
         RWTHRTHROWSANY;

      // The adjustable pool thread entry point. Dequeues and executes  
      // functors from the functor queue. Adjusts committedThreadCount_ 
      // and kills thread if timeout occurs.
      void
      workerWithTimeoutAdjustment(unsigned long timeout)
         RWTHRTHROWSANY;
};


/*****************************************************************************/

inline
RWThreadPoolImp::RWThreadPoolImp(const RWThreadPoolImp&)
{
   RWTHRASSERT(0); // Dummy Implementation!
}

inline
RWThreadPoolImp&
RWThreadPoolImp::operator=(const RWThreadPoolImp&)
{
   RWTHRASSERT(0); // Dummy Implementation!
   return *this;
}

/*****************************************************************************/

// Create an empty (invalid) external interface handle
inline
RWThreadPool::RWThreadPool(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadPool,RWThreadPool(void));
}

// Construct an external interface handle to a server object instance
inline
RWThreadPool::RWThreadPool(RWThreadPoolImp* imp)
   RWTHRTHROWSANY
   :
      RWTHRHandle(imp)
{
   RWTHRTRACEMF(RWThreadPool,RWThreadPool(RWThreadPoolImp*));
}
     
// Construct an external interface handle to the threadpool 
// object (if any) pointed-to by another external interface handle 
inline
RWThreadPool::RWThreadPool(const RWThreadPool& second)
   RWTHRTHROWSANY
   :
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWThreadPool,RWThreadPool(const RWThreadPool&));
}

// Bind this external interface handle to the threadpool object (if any)
// pointed-to by a second external interface handle.
inline
RWThreadPool&
RWThreadPool::operator=(const RWThreadPool& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadPool,operator=(const RWThreadPool&):RWThreadPool&);
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

inline
RWThreadPoolImp&
RWThreadPool::body(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadPool,body(void):RWThreadPoolImp&);
   return (RWThreadPoolImp&)RWTHRHandle::body();
}

inline
void
RWThreadPool::enqueue(const RWFunctor0& functor)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadPool,enqueue(const RWFunctor0&):void);
   body().enqueue(functor);
}


inline
size_t
RWThreadPool::entries(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWServerPool,size(void):size_t);
   return body().entries();
}

inline
RWThreadAttribute
RWThreadPool::getPoolAttribute(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadPool,getPoolAttribute(void):RWThreadAttribute);
   return body().getPoolAttribute();
}


inline
size_t
RWThreadPool::size(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWServerPool,size(void):size_t);
   return body().size();
}


inline
void
RWThreadPool::stop(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadPool,stop(void):void);
   body().stop();
}


#endif // __RWTHRTHRPOOL_H__
