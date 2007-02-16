#if !defined(__RWTHRSRVPOOL_H__)
#  define __RWTHRSRVPOOL_H__
/*****************************************************************************
 *
 * srvpool.h 
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

srvpool.h - Class declaration for:
   
   RWServerPool - Handle for RWServerPoolImp instances.

   RWServerPoolImp - Thread runnable object that acts as a multithread-server 
                     for other runnable objects. 

      The RWServerPool object manages a "pool" of RWServerPool instances 
      that are used to execute runnable objects as requested by other threads.
      A server pool object, when started, waits for other threads to enqueue 
      runnable objects that they would like to have executed.  Each runnable 
      object that the server finds in its internal queue is dequeued and is
      passed to the next available runnable server instance within the pool.
      This process continues until the server pool thread is interrupted, 
      canceled, or stopped.
      
      The stop() member may be used to request that the server pool thread 
      stop execution and exit after it dispatches any runnables that were
      already enqueued when stop() was called.  The stop function does not 
      wait for the server to stop; the join() function should be used to wait 
      for the server thread to complete execution and exit.  Any runnables 
      enqueued after stop() is called will not be executed unless the server 
      pool is restarted.  
      
      The RWRunnable::requestCancellation() function should be used if the 
      server thread is to stop execution as soon as possible without dequeuing 
      and dispatching any addition runnables.  
      
      The RWRunnable::requestInterrupt() function can be used to temporarily
      suspend execution of the server thread.  

See Also:

   srvpool.cpp - Out-of-line function definitions.

   server.cpp - Contains the RWServerPool declarations.

   runnable.h - Contains RWRunnable declarations.
   thread.h - Contains RWThread declarations.

******************************************************************************/

#  if !defined(__RWTHRSERVER_H__)
#     include <rw/thr/server.h>
#  endif

class RWTHRExport RWServerPoolImp;
class RWTHRExport RWServerPool;

class RWTHRExport RWServerPool :
   public RWRunnableServer {

   RW_THR_DECLARE_TRACEABLE

   friend class RWServerPoolImp;

   public:

      // Construct an empty (invalid) handle
      RWServerPool(void)
         RWTHRTHROWSANY;

      // Construct an external interface handle to the thread object 
      // pointed-to by a second handle (if any).
      RWServerPool(const RWServerPool& second)
         RWTHRTHROWSANY;

      // Bind this external interface handle to the thread object 
      // pointed-to by a second handle (if any)
      RWServerPool&
      operator=(const RWServerPool& second)
         RWTHRTHROWSANY;

      // Get a handle to the thread attribute instance specified during 
      // server pool construction or the instance specified in the last call 
      // to setPoolAttribute().
      RWThreadAttribute
      getPoolAttribute(void) 
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Make a server pool instance with a pool that contains the specified
      // number of threads.  The server pool's main thread and its pool threads
      // will be created with default thread attributes.
      static
      RWServerPool
      make(size_t numThreads)
         RWTHRTHROWSANY;

      // Make a server pool instance whose pool will contain the specified 
      // number of threads, and whose input queue will have its capacity for 
      // holding unprocessed runnables limited to the specified maximum.
      // The server pool's main thread and its pool threads will be created 
      // with default thread attributes.
      static
      RWServerPool
      make(size_t numThreads,
           size_t maxCapacity)
         RWTHRTHROWSANY;

      // Make a server pool instance whose main thread will be created with 
      // the specified thread attributes and whose pool will contain the 
      // specified number of threads, each created with a default set of 
      // thread attributes
      static
      RWServerPool
      make(const RWThreadAttribute& serverThreadAttr,
           size_t numThreads)
         RWTHRTHROWSANY;

      // Make a server pool instance whose main thread will be created with 
      // the specified thread attributes, and whose pool will contain the 
      // specified number of threads, and whose input queue will have its 
      // capacity for holding unprocessed runnables limited to the specified 
      // maximum.
      static
      RWServerPool
      make(const RWThreadAttribute& serverThreadAttr, 
           size_t numThreads,
           size_t maxCapacity)
         RWTHRTHROWSANY;

      // Make a server pool instance whose main thread will be created with 
      // default thread attributes, and whose pool will contain the specified
      // number of threads, each created with the specified set of 
      // thread attributes.
      static
      RWServerPool
      make(size_t numThreads,
           const RWThreadAttribute& poolThreadsAttr)
         RWTHRTHROWSANY;

      // Make a server pool instance whose main thread will be created with
      // default thread attributes, whose pool will contain the specified
      // number of threads, each created with the specified thread attributes,
      // and whose input queue will have its capacity for holding unprocessed 
      // runnables limited to the specified maximum.
      static
      RWServerPool
      make(size_t numThreads,
           const RWThreadAttribute& poolThreadAttr, 
           size_t maxCapacity)
         RWTHRTHROWSANY;

      // Make a server pool instance whose main thread will be created with 
      // the specified thread attributes, whose pool will contain the specified
      // number of threads, each created with the specified thread attributes.
      static
      RWServerPool
      make(const RWThreadAttribute& serverThreadAttr,
           size_t numThreads,
           const RWThreadAttribute& poolThreadsAttr)
         RWTHRTHROWSANY;

      // Make a server pool instance whose main thread will be created with 
      // the specified thread attributes, and whose pool will contain the specified
      // number of threads, each created with the specified thread attributes, and
      // whose input queue will have its capacity for holding unprocessed 
      // runnables limited to the specified maximum.
      static
      RWServerPool
      make(const RWThreadAttribute& serverThreadAttr,
           size_t numThreads,
           const RWThreadAttribute& poolThreadsAttr,
           size_t maxCapacity)
         RWTHRTHROWSANY;

      // Changes the number of threads within the thread pool.  If the new 
      // thread pool size is less than the current size, then a sufficient 
      // number of threads will destroyed but only if after they have 
      // completed execution of any assigned runnable object.  If the size is 
      // greater than the current size, then the pool server thread will 
      // create a  sufficient number of new threads and add them to the pool.
      void
      resize(size_t size)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Change the thread attribute instance used to initialize new runnable 
      // server objects as they are created for the thread pool.  Changing the 
      // pool thread attributes after the server has started will only affect 
      // new threads started by the pool server in response to a request for a 
      // larger pool size; existing threads will continue with their original 
      // attributes.
      void
      setPoolAttribute(const RWThreadAttribute& poolThreadsAttr) 
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Returns the target size for the thread pool.  May be less than or greater
      // than the actual number of runnable server instances in the pool, since the
      // pool server thread may not yet have completed a previously requested 
      // expansion or contraction of the thread pool.
      size_t
      size(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

   protected:

      // Construct an external interface handle to a RWServerPoolImp instance
      RWServerPool(RWServerPoolImp* threadImpP)
         RWTHRTHROWSANY;
      
      // Override the parent class body() function to provide
      // version that casts to this handle's body class
      RWServerPoolImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

class RWTHRExport RWServerPoolImp :
   public RWRunnableServerImp {

   RW_THR_DECLARE_TRACEABLE

   friend class RWServerPool;

   private:
      
      // The current number of runnable server instances in the pool.
      size_t   currentThreadCount_;

      // The desired number of runnable server instances in the pool.
      size_t   targetThreadCount_;

      // A handle to the thread attribute instance used when creating the 
      // runnable server instances for the thread pool.  This value is
      // copied when the pool server is started, so changes to the thread
      // attibute instance, or specification of a new thread attribute 
      // instance will have no affect on new pool threads unless the 
      // server pool is stopped and restarted.
      RWThreadAttribute initialPoolAttribute_;

      // Local copy of the initial pool attribute instance
      RWThreadAttribute currentPoolAttribute_;

      // Handle for the callback functor that is registered with all
      // runnable server instances in the pool.
      RWFunctor2<const RWRunnable&,RWExecutionState>  callbackFunctor_;
      
      // List of all runnable server instances in the pool.
      RWTValSlist<RWRunnableServer>    pool_;

      // Queue of runnable servers that are ready and waiting to 
      // execute another runnable.
      RWPCValQueue<RWRunnableServer>   ready_;
      
   protected:

      // Construct a server pool instance with a pool that contains the specified
      // number of threads.  The server pool's main thread and its pool threads
      // will be created with default thread attributes.
      RWServerPoolImp(size_t numThreads)
         RWTHRTHROWSANY;

      // Construct a server pool instance whose pool will contain the specified 
      // number of threads, and whose input queue will have its capacity for 
      // holding unprocessed runnables limited to the specified maximum.
      // The server pool's main thread and its pool threads will be created 
      // with default thread attributes.
      RWServerPoolImp(size_t numThreads,
                      size_t maxCapacity)
         RWTHRTHROWSANY;

      // Construct a server pool instance whose main thread will be created with 
      // the specified thread attributes and whose pool will contain the 
      // specified number of threads, each created with a default set of 
      // thread attributes
      RWServerPoolImp(const RWThreadAttribute& serverThreadAttr,
                      size_t numThreads)
         RWTHRTHROWSANY;

      // Construct a server pool instance whose main thread will be created with 
      // the specified thread attributes, and whose pool will contain the 
      // specified number of threads, and whose input queue will have its 
      // capacity for holding unprocessed runnables limited to the specified 
      // maximum.
      RWServerPoolImp(const RWThreadAttribute& serverThreadAttr,
                      size_t numThreads,
                      size_t maxCapacity)
         RWTHRTHROWSANY;

      // Construct a server pool instance whose main thread will be created with 
      // default thread attributes, and whose pool will contain the specified
      // number of threads, each created with the specified set of 
      // thread attributes.
      RWServerPoolImp(size_t numThreads,
                      const RWThreadAttribute& poolThreadsAttr)
         RWTHRTHROWSANY;

      // Construct a server pool instance whose main thread will be created with
      // default thread attributes, whose pool will contain the specified
      // number of threads, each created with the specified thread attributes,
      // and whose input queue will have its capacity for holding unprocessed 
      // runnables limited to the specified maximum.
      RWServerPoolImp(size_t numThreads,
                      const RWThreadAttribute& poolThreadsAttr,
                      size_t maxCapacity)
         RWTHRTHROWSANY;

      // Construct a server pool instance whose main thread will be created with 
      // the specified thread attributes, whose pool will contain the specified
      // number of threads, each created with the specified thread attributes.
      RWServerPoolImp(const RWThreadAttribute& serverThreadAttr,
                      size_t numThreads,
                      const RWThreadAttribute& poolThreadsAttr)
         RWTHRTHROWSANY;

      // Construct a server pool instance whose main thread will be created with 
      // the specified thread attributes, and whose pool will contain the specified
      // number of threads, each created with the specified thread attributes, and
      // whose input queue will have its capacity for holding unprocessed 
      // runnables limited to the specified maximum.
      RWServerPoolImp(const RWThreadAttribute& serverThreadAttr,
                      size_t numThreads,
                      const RWThreadAttribute& poolThreadsAttr,
                      size_t maxCapacity)
         RWTHRTHROWSANY;

      // Virtual method called by requestCancellation() members. Allows
      // this class to define behavior to support cancellation requests.
      // The RWRunnable monitor mutex will be LOCKED upon entry.
      void
      _checkCancellation(void)
         RWTHRTHROWSANY;

      // Virtual method called by requestInterrupt() members. Allows
      // this class to define behavior to support interrupt requests.
      // The RWRunnable monitor mutex will be LOCKED upon entry.
      void
      _checkInterrupt(void)
         RWTHRTHROWSANY;

      // Get a handle to the thread attribute instance specified during 
      // server pool construction or the instance specified in the last call 
      // to setPoolAttribute().
      RWThreadAttribute
      getPoolAttribute(void) 
         RWTHRTHROWS1(RWTHRInternalError);

      // Changes the number of threads within the thread pool.  If the new 
      // thread pool size is less than the current size, then a sufficient 
      // number of threads will destroyed but only if after they have 
      // completed execution of any assigned runnable object.  If the size is 
      // greater than the current size, then the pool server thread will 
      // create a  sufficient number of new threads and add them to the pool.
      void
      resize(size_t size)
         RWTHRTHROWS1(RWTHRInternalError);

      // Overrides the run() member to provide for the dispatching of enqueued 
      // runnables to the runnable servers in the pool.
      virtual
      void
      run(void)
         RWTHRTHROWSANY;
   
      // Change the thread attribute instance to be used when creating the 
      // runnable server instances for the thread pool.  The pool attribute
      // instance is copied when the pool server is started, so changes to 
      // the original thread attibute instance, or specification of a new 
      // thread attribute instance will have no affect on new pool threads 
      // unless the server pool is stopped and restarted.
      void
      setPoolAttribute(const RWThreadAttribute& runnablesServersAttr) 
         RWTHRTHROWS1(RWTHRInternalError);

      // Returns the target size for the thread pool.  May be less than or greater
      // than the actual number of runnable server instances in the pool, since the
      // pool server thread may not yet have completed a previously requested 
      // expansion or contraction of the thread pool.
      size_t
      size(void) const
         RWTHRTHROWS1(RWTHRInternalError);

   private:

      // Don't allow direct copy construction or assignment

      RWServerPoolImp(const RWServerPoolImp&);

      RWServerPoolImp&
      operator=(const RWServerPoolImp&);

      // Shrink or expand the server pool as required to 
      // satisfy the current thread count target.
      void
      adjustPool(void)
         RWTHRTHROWSANY;

      // Cancel the runnable server threads in the pool
      void
      cancelPool(void)
         RWTHRTHROWSANY;

      // Reduce the number of runnable servers in the thread pool
      void
      contract(void)
         RWTHRTHROWSANY;

      // Increase the number of runnable servers in the thread pool
      void
      expand(void)
         RWTHRTHROWSANY;

      // Initialize the server pool instance
      void
      init(void)
         RWTHRTHROWSANY;

      // Get the next runnable server that is ready to execute another runnable,
      // and if no servers are available, blocking in a wait until a server does 
      // become available.
      RWRunnableServer
      nextServer(void)
         RWTHRTHROWSANY;

      // Remove a runnable server instance from the pool;
      // Removes instance from internal list, and removes callback from instance.
      void
      remove(RWRunnableServer& server)
         RWTHRTHROWSANY;

      // Add a runnable server to the available-server queue.  This function
      // is called by a functor that is registered as a callback on each 
      // runnable server instance in the pool.
      void
      serverReadyCallback(const RWRunnable& server, RWExecutionState state)
         RWTHRTHROWS1(RWTHRInternalError);

      // Method that unblocks the server pool thread so that it might process 
      // other requests. The RWRunnable monitor mutex will be LOCKED upon entry.
      void
      _wakeup(void)
         RWTHRTHROWSANY;

};

/*****************************************************************************/

inline
RWServerPoolImp::RWServerPoolImp(const RWServerPoolImp&)
{
   RWTHRASSERT(0); // Dummy Implementation!
}

inline
RWServerPoolImp&
RWServerPoolImp::operator=(const RWServerPoolImp&)
{
   RWTHRASSERT(0); // Dummy Implementation!
   return *this;
}

/*****************************************************************************/

// Create an empty (invalid) external interface handle
inline
RWServerPool::RWServerPool(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWServerPool,RWServerPool(void));
}

// Construct an external interface handle to a server object instance
// protected
inline
RWServerPool::RWServerPool(RWServerPoolImp* imp)
   RWTHRTHROWSANY
   :
      RWRunnableServer(imp)
{
   RWTHRTRACEMF(RWServerPool,RWServerPool(RWServerPoolImp*));
}
      
// Construct an external interface handle to the server object (if any)
// pointed-to by another external interface handle 
inline
RWServerPool::RWServerPool(const RWServerPool& second)
   RWTHRTHROWSANY
   :
      RWRunnableServer(second)
{
   RWTHRTRACEMF(RWServerPool,RWServerPool(const RWServerPool&));
}

// Bind this external interface handle to the thread object (if any)
// pointed-to by a second external interface handle.
inline
RWServerPool&
RWServerPool::operator=(const RWServerPool& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWServerPool,operator=(const RWServerPool&):RWServerPool&);
   if (&second != this)
      RWRunnableServer::operator=(second);
   return *this;
}

// protected
inline
RWServerPoolImp&
RWServerPool::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWServerPool,body(void):RWServerPoolImp&);
   return (RWServerPoolImp&)RWRunnableServer::body();
}

inline
RWThreadAttribute
RWServerPool::getPoolAttribute(void)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWServerPool,getPoolAttribute(void):RWThreadAttribute);
   return body().getPoolAttribute();
}

inline
void
RWServerPool::resize(size_t count)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWServerPool,resize(size_t):void);
   body().resize(count);
}

inline
void
RWServerPool::setPoolAttribute(const RWThreadAttribute& attr)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWServerPool,setPoolAttribute(const RWThreadAttribute&):void);
   body().setPoolAttribute(attr);
}

inline
size_t
RWServerPool::size(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWServerPool,size(void):size_t);
   return body().size();
}

#endif // __RWTHRSERVER_H__
