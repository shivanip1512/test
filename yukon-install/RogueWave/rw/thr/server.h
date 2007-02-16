#if !defined(__RWTHRSERVER_H__)
#  define __RWTHRSERVER_H__
/*****************************************************************************
 *
 * server.h 
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

server.h - Class declaration for:
   
   RWRunnableServer - Handle for RWRunnableServerImp instances.

   RWRunnableServerImp - Thread runnable object that acts as a server for 
                         other runnable objects. 

      The runnable server may be used alone, or as the base class for an
      active object class implementation.  The server object, when started,
      waits for other threads to enqueue runnable objects that they would
      like this thread to execute for them.  Each runnable object that the
      server finds in its internal queue is dequeued and started.  This
      process continues until a runnable is enqueued that is not valid
      because it is "empty" and points to no runnable implementation.
      
      An active object implementation using this class would define a 
      client interface with member functions that simply produce "runnable 
      functor" objects (see runfuncx.h files) that, when invoked, execute some
      corresponding private or local member function within the active object 
      instance.  The public members pass the runnable functors to the server 
      thread by calling the enqueue() member, which stores the runnables in a 
      queue.  The client interface member function then returns to its caller.  
      
      Runnables that use IOUs can be used to represent a future return value 
      or result of the asynchronous active object operation.  In this situation,
      the client interface would create the appropriate IOU-capable runnable 
      object, enqueue the runnable for execution, retrieve the runnable's IOU 
      result, and return that IOU to the caller.  The caller could then redeem
      that IOU at some point in the future.

See Also:

   server.cpp - Out-of-line function definitions.

   runnable.h - Contains RWRunnable declarations.
   thread.h - Contains RWThread declarations.

   runfunc.h - Contains RWRunnableFunctor0 declarations.
   runfuncr.h - Contains RWRunnableFunctorR0<ReturnType> declarations.
   runfunci.h - Contains RWRunnableIOUFunctorR0<ReturnType> declarations.

   thrfunc.h - Contains RWThreadFunctor0 declarations.
   thrfuncr.h - Contains RWThreadFunctorR0<ReturnType> declarations.
   thrfunci.h - Contains RWThreadIOUFunctorR0<ReturnType> declarations.

******************************************************************************/

#  if !defined(__RWTHRTHREAD_H__)
#     include <rw/thr/thread.h>
#  endif

// We're no longer using RWGuardedRunnableQueue class for the 
// runnable input queue, but we still need to include this header 
// to get typedef and make functions for the runnable guard objects.
#  if !defined(__RWTHRGRDRNBLQ_H__)
#     include <rw/thr/grdrnblq.h>
#  endif

// Now we use the new RWPCValQueueGuardedPrioritized<Type> class.
#  if !defined(__RWTHRPRODCONS_H__)
#     include <rw/thr/prodcons.h>
#  endif

class RWTHRExport RWRunnableServerImp;
class RWTHRExport RWRunnableServer;

class RWTHRExport RWRunnableServer :
   public RWThread {

   RW_THR_DECLARE_TRACEABLE

   friend class RWRunnableServerImp;
   friend class RWRunnableServerSelf;

   public:

      // Construct an empty (invalid) handle
      RWRunnableServer(void)
         RWTHRTHROWSANY;

      // Construct an external interface handle to the thread object 
      // pointed-to by a second handle (if any).
      RWRunnableServer(const RWRunnableServer& second)
         RWTHRTHROWSANY;

      // Bind this external interface handle to the thread object 
      // pointed-to by a second handle (if any)
      RWRunnableServer&
      operator=(const RWRunnableServer& second)
         RWTHRTHROWSANY;

      void 
      enqueue(const RWRunnable& runnable)
         RWTHRTHROWSANY;

      RWWaitStatus 
      enqueue(const RWRunnable& runnable, unsigned long milliseconds)
         RWTHRTHROWSANY;

      void
      enqueue(const RWRunnable& runnable, const RWRunnableGuard& guard)
         RWTHRTHROWSANY; 
  
      RWWaitStatus
      enqueue(const RWRunnable& runnable, const RWRunnableGuard& guard, unsigned long milliseconds)
         RWTHRTHROWSANY; 
  
      void
      enqueue(long priority, const RWRunnable& runnable)
         RWTHRTHROWSANY; 
  
      RWWaitStatus
      enqueue(long priority, const RWRunnable& runnable, unsigned long milliseconds)
         RWTHRTHROWSANY; 
  
      void
      enqueue(long priority, const RWRunnable& runnable, const RWRunnableGuard& guard)
         RWTHRTHROWSANY; 
 
      RWWaitStatus
      enqueue(long priority, const RWRunnable& runnable, const RWRunnableGuard& guard, unsigned long milliseconds)
         RWTHRTHROWSANY; 
 
      // Returns the maximum capacity of the runnable input queue.
      // A value of zero indicates that the queue has no capacity limit.
      size_t 
      getCapacity(void) const
         RWTHRTHROWSANY;

      // Make a runnable server instance
      static
      RWRunnableServer
      make(void)
         RWTHRTHROWSANY;

      // Make a runnable server instance with the specified thread attributes.
      static
      RWRunnableServer
      make(const RWThreadAttribute& serverThreadAttr)
         RWTHRTHROWSANY;

      // Make a runnable server instance with an input queue whose capacity 
      // for holding unprocessed runnables is limited to the specified capacity.
      static
      RWRunnableServer
      make(size_t maxCapacity)
         RWTHRTHROWSANY;

      // Make a runnable server instance with the specified thread attributes and
      // with an input queue whose capacity for holding unprocessed runnables is 
      // limited to the specified capacity.
      static
      RWRunnableServer
      make(const RWThreadAttribute& serverThreadAttr, 
           size_t maxCapacity)
         RWTHRTHROWSANY;

      // Sets the maximum capacity of the buffer.
      // Accepts a size_t value that specifies the maximum number of 
      // un-processed entries to allow to accumulate within the input queue.  
      // Once the number of entries equals or exceeds this number, any 
      // thread attempting to enqueue an additional entry will be blocked 
      // until an entry is removed by the server thread, or until the 
      // capacity is increased.  A value of zero is used to indicate 
      // that the queue has no capacity limit, and that all enqueue 
      // operations should complete without blocking.
      // Return a size_t value representing the maximum capacity 
      // that existed at the time this method was called.
      size_t 
      setCapacity(size_t maxCapacity)
         RWTHRTHROWSANY;

      // Request that the server stop execution after it executes 
      // the runnables that are currently enqueued...
      void
      stop(void)
         RWTHRTHROWSANY;

   protected:

      // Construct an external interface handle to a RWRunnableServerImp instance
      RWRunnableServer(RWRunnableServerImp* threadImpP)
         RWTHRTHROWSANY;
      
      // Override the parent class body() function to provide
      // version that casts to this handle's body class
      RWRunnableServerImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

class RWTHRExport RWRunnableServerImp :
   public RWThreadImp {

   RW_THR_DECLARE_TRACEABLE

   friend class RWRunnableServer;

   protected:

      // Guarded, Prioritized Queue with Producer-Consumer Synchronization Semantics
      RWPCValQueueGuardedPrioritized<RWRunnable>   queue_;

      // Construct a server instance with default thread attributes.
      RWRunnableServerImp(void)
         RWTHRTHROWSANY;

      // Construct a server instance with an imput queue whos capacity for
      // holding unprocessed runnables is limited to the specified maximum 
      // capacity (unlimited by default).
      RWRunnableServerImp(size_t maxCapacity)
         RWTHRTHROWSANY;

      // Construct a server instance with the specified thread attributes
      // and an input queue whose capacity for holding unprocessed runnables 
      // is limited to the specified capacity (unlimited by default).
      RWRunnableServerImp(const RWThreadAttribute& serverThreadAttr,
                          size_t maxCapacity = 0)
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

      // Get the next runnable off of the input queue, and if the queue
      // is empty, block until a runnable is queued.
      RWRunnable
      dequeue(void)
         RWTHRTHROWSANY;

      // Define class-specific start behavior
      // (Flushes runnable queue and calls RWThreadImp::_dispathExec())
      virtual
      void
      _dispatchExec(void)
         RWTHRTHROWSANY;

      void 
      enqueue(const RWRunnable& runnable)
         RWTHRTHROWSANY;

      RWWaitStatus 
      enqueue(const RWRunnable& runnable, unsigned long milliseconds)
         RWTHRTHROWSANY;

      void
      enqueue(const RWRunnable& runnable, const RWRunnableGuard& guard)
         RWTHRTHROWSANY; 
  
      RWWaitStatus
      enqueue(const RWRunnable& runnable, const RWRunnableGuard& guard, unsigned long milliseconds)
         RWTHRTHROWSANY; 
  
      void
      enqueue(long priority, const RWRunnable& runnable)
         RWTHRTHROWSANY; 
  
      RWWaitStatus
      enqueue(long priority, const RWRunnable& runnable, unsigned long milliseconds)
         RWTHRTHROWSANY; 
  
      void
      enqueue(long priority, const RWRunnable& runnable, const RWRunnableGuard& guard)
         RWTHRTHROWSANY; 

      RWWaitStatus
      enqueue(long priority, const RWRunnable& runnable, const RWRunnableGuard& guard, unsigned long milliseconds)
         RWTHRTHROWSANY; 
  
      // Returns the maximum capacity of the runnable input queue.
      // A value of zero indicates that the queue has no capacity limit.
      size_t 
      getCapacity(void) const
         RWTHRTHROWSANY;

      // Get the next runnable to start.
      //   This implementation simply calls dequeue resulting in FIFO 
      //   execution.  Derived classes may wish to override this member 
      //   to implement other scheduling mechanisms
      virtual
      RWRunnable
      nextRunnable(void)
         RWTHRTHROWSANY;

      // Override the run() member to provide for 
      // execution of queued runnables.
      virtual
      void
      run(void)
         RWTHRTHROWSANY;
   
      // Sets the maximum capacity of the buffer.
      // Accepts a size_t value that specifies the maximum number of 
      // un-processed entries to allow to accumulate within the input queue.  
      // Once the number of entries equals or exceeds this number, any 
      // thread attempting to enqueue an additional entry will be blocked 
      // until an entry is removed by the server thread, or until the 
      // capacity is increased.  A value of zero is used to indicate 
      // that the queue has no capacity limit, and that all enqueue 
      // operations should complete without blocking.
      // Return a size_t value representing the maximum capacity 
      // that existed at the time this method was called.
      size_t 
      setCapacity(size_t maxCapacity)
         RWTHRTHROWSANY;

      // Request that the server stop execution after it executes 
      // the runnables that are currently enqueued...
      void
      stop(void)
         RWTHRTHROWSANY;

   private:

      // Prohibit automatic compiler generation of these members

      RWRunnableServerImp(const RWRunnableServerImp&);

      RWRunnableServerImp&
      operator=(const RWRunnableServerImp&);


      // Method that unblocks the server pool thread so that it might process 
      // other requests. The RWRunnable monitor mutex will be LOCKED upon entry.
      void
      _wakeup(void)
         RWTHRTHROWSANY;

};

#if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file...
template class RWTHRIExport RWPCValQueueGuardedPrioritized<RWRunnable>;
template class RWTHRIExport RWPCValBufferBaseGuarded<RWRunnable,RWGuardAndPriorityDecorator<RWRunnable> >;
template class RWTHRIExport RWPCValBufferBasePrioritized<RWRunnable,RWGuardAndPriorityDecorator<RWRunnable> >;
template class RWTHRIExport RWPCValBufferBase<RWGuardAndPriorityDecorator<RWRunnable> >;
template class RWTHRIExport RWTValSlist<RWGuardAndPriorityDecorator<RWRunnable> >;
#  if defined(RW_NO_STL)
template class RWTHRIExport RWTValSlink<RWGuardAndPriorityDecorator<RWRunnable> >;
template class RWTHRIExport RWTIsvSlist<RWTValSlink<RWGuardAndPriorityDecorator<RWRunnable> > >;
#  endif
#endif

/*****************************************************************************/

// Prohibit automatic compiler generation of these members

inline
RWRunnableServerImp::RWRunnableServerImp(const RWRunnableServerImp&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation!
}

inline
RWRunnableServerImp&
RWRunnableServerImp::operator=(const RWRunnableServerImp&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation!
   return *this;
}

/*****************************************************************************/

// Create an empty (invalid) external interface handle
inline
RWRunnableServer::RWRunnableServer(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,RWRunnableServer(void));
}

// Construct an external interface handle to a server object instance
// protected
inline
RWRunnableServer::RWRunnableServer(RWRunnableServerImp* imp)
   RWTHRTHROWSANY
   :
      RWThread((RWThreadImp*)imp)
{
   RWTHRTRACEMF(RWRunnableServer,RWRunnableServer(RWRunnableServerImp*));
}
      
// Construct an external interface handle to the server object (if any)
// pointed-to by another external interface handle 
inline
RWRunnableServer::RWRunnableServer(const RWRunnableServer& second)
   RWTHRTHROWSANY
   :
      RWThread(second)
{
   RWTHRTRACEMF(RWRunnableServer,RWRunnableServer(const RWRunnableServer&));
}

// Bind this external interface handle to the thread object (if any)
// pointed-to by a second external interface handle.
inline
RWRunnableServer&
RWRunnableServer::operator=(const RWRunnableServer& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,operator=(const RWRunnableServer&):RWRunnableServer&);
   if (&second != this)
      RWThread::operator=(second);
   return *this;
}

// protected
inline
RWRunnableServerImp&
RWRunnableServer::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWRunnableServer,body(void):RWRunnableServerImp&);
   return (RWRunnableServerImp&)RWRunnable::body();
}

inline
void
RWRunnableServer::enqueue(const RWRunnable& runnable)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,enqueue(const RWRunnable&):void);
   body().enqueue(runnable);
}

inline
RWWaitStatus
RWRunnableServer::enqueue(const RWRunnable& runnable, unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,enqueue(const RWRunnable&, unsigned long):void);
   return body().enqueue(runnable,milliseconds);
}

inline
void
RWRunnableServer::enqueue(const RWRunnable& runnable, const RWRunnableGuard& guard)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,enqueue(const RWRunnable&,const RWRunnableGuard&):void);
   body().enqueue(runnable, guard);
}

inline
RWWaitStatus
RWRunnableServer::enqueue(const RWRunnable& runnable, const RWRunnableGuard& guard, unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,enqueue(const RWRunnable&,const RWRunnableGuard&,unsigned long):void);
   return body().enqueue(runnable, guard, milliseconds);
}

inline
void
RWRunnableServer::enqueue(long priority, const RWRunnable& runnable)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,enqueue(long,const RWRunnable&):void);
   body().enqueue(priority, runnable);
}

inline
RWWaitStatus
RWRunnableServer::enqueue(long priority, const RWRunnable& runnable, unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,enqueue(long,const RWRunnable&,unsigned long):void);
   return body().enqueue(priority, runnable, milliseconds);
}

inline
void
RWRunnableServer::enqueue(long priority, const RWRunnable& runnable, const RWRunnableGuard& guard)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,enqueue(long,const RWRunnable&,const RWRunnableGuard&):void);
   body().enqueue(priority, runnable, guard);
}

inline
RWWaitStatus
RWRunnableServer::enqueue(long priority, const RWRunnable& runnable, const RWRunnableGuard& guard, unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,enqueue(long,const RWRunnable&,const RWRunnableGuard&,unsigned long):void);
   return body().enqueue(priority, runnable, guard, milliseconds);
}

inline
size_t
RWRunnableServer::getCapacity(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,getCapacity(void) const:size_t);
   return body().getCapacity();
}

inline
size_t
RWRunnableServer::setCapacity(size_t maxCapacity)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,setCapacity(size_t):size_t);
   return body().setCapacity(maxCapacity);
}

inline
void
RWRunnableServer::stop(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableServer,stop(void):void);
   body().stop();
}

#endif // __RWTHRSERVER_H__
