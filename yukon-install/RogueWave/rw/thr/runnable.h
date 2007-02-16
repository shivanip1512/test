#if !defined(__RWTHRRUNNABLE_H__)
#  define __RWTHRRUNNABLE_H__
/*****************************************************************************
 *
 * runnable.h
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

runnable.h - Declarations for:

   RWCompletionState - enumerated completion states.
   RWExecutionState - enumerated execution states.

   rwRunnable() - Retrieves the current runnable object, if any, that
                  the calling thread is executing within.

   RWRunnableImp - Internal runnable implementation.
   
      RWRunnableImp is the abstract base class for all runnable objects. 
      A "runnable" is an object that is used to control and manage threads, 
      and also acts as the abstract representation of the "work" to be done 
      by a thread.  A runnable object becomes "active" when its start() member 
      is called.  If the runnable object is an instance of a derived class 
      with synchronous behavior (such as provided by RWRunnableFunctor0), then 
      the thread that calls start() effectively enters the runnable instance 
      to become its active thread.  Only one thread may be active within a 
      runnable instance at one time - the start() member will throw an 
      exception if any thread attempts to call start while a thread is already 
      executing "inside" of the runnable.  If the runnable object is an 
      instance of a class derived from RWThreadImp (also a derivative of this 
      class), then a call to start will result in the creation of a new thread 
      of execution within the runnable, freeing the starting thread for other 
      activities.  Runnable objects may be directly executed (as described 
      above) or queued on a RWRunnableServer or RWRunnableServerPool instance 
      for start-up and execution in a separate thread.

   RWRunnableHandle - Base class for runnable handle classes
      
      A base "handle" class whose instances may be bound to RWRunnableImp 
      instances.  The RWRunnableHandle class provides access to those runnable
      functions that may be called any thread.

   RWRunnable - Runnable handle with "external" runnable interface
      
      A "handle" class whose instances may be bound to RWRunnableImp instances.
      A RWRunnable instance is used by threads executing "outside" of a 
      RWRunnableImp instance to access and manipulate that instance.  Only 
      those RWRunnableImp functions that are safe for use by external threads 
      are made available in the interface of this class.
   
   RWRunnableSelf - Runnable handle with "internal" runnable interface
    
      A "handle" class whose instances may be bound to RWRunnableImp instances.  
      A RWRunnableSelf instance is used by the thread executing "inside" a 
      RWRunnableImp instance to access and manipulate that instance.  Only 
      those RWRunnableImp functions that are safe for use by the internal 
      thread are made available in the interface of this class.

   Make functions for creating runnable callback functors:         

   RWFunctor2 rwMakeRunnableCallback(void (*function)(const RWRunnable&,RWExecutionState))
   RWFunctor2 rwMakeRunnableCallback(void (*function)(const RWRunnable&,RWExecutionState,A1),A1)
   RWFunctor2 rwMakeRunnableCallback(void (*function)(const RWRunnable&,RWExecutionState,A1,A2),A1,A2)
   RWFunctor2 rwMakeRunnableCallback(Callee&,void (Callee::*function)(const RWRunnable&,RWExecutionState))
   RWFunctor2 rwMakeRunnableCallback(Callee&,void (Callee::*function)(const RWRunnable&,RWExecutionState,A1),A1)
   RWFunctor2 rwMakeRunnableCallback(Callee&,void (Callee::*function)(const RWRunnable&,RWExecutionState,A1,A2),A1,A2)

See Also:

   runnable.cpp  - Out-of-line function definitions.

   runfunc.h - Declares a functor-based synchronous runnable class.
   runfuncr.h - Declares a functor-based synchronous runnable class for 
                functions that return a value.
   runfunci.h - Declares a functor-based synchronous runnable class for
                functions that return a value.  The return value is stored
                and accessed using IOUs.

   thread.h - Declares the thread runnable implementation RWThreadImp and its
              associated handles.

   thrfunc.h - Declares a functor-based thread runnable class.
   thrfuncr.h - Declares a functor-based thread runnable class for 
                functions that return a value.
   thrfunci.h - Declares a functor-based thread runnable class for
                functions that return a value.  The return value is stored
                and accessed using IOUs.

*****************************************************************************/

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

#  if !defined(__RWTHRCOUNTPTR_H__)
#     include <rw/thr/countptr.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

#  if !defined(__RWTHRTHREADID_H__)
#     include <rw/thr/threadid.h>
#  endif

#  if !defined(__RWTHRSEMAPHOR_H__)
#     include <rw/thr/semaphor.h>
#  endif

#  if !defined(__RWTHRONLYPTR_H__)
#     include <rw/thr/onlyptr.h>
#  endif

#  if !defined(__RWTHRCB2MLIST_H__)
#     include <rw/thr/cb2mlist.h>
#  endif

enum RWExecutionState {
   RW_THR_INITIAL        = 0x0001,  // Constructed; waiting for start or restart
   RW_THR_STARTING       = 0x0002,  // Runnable started, but has not started executing yet
   RW_THR_RUNNING        = 0x0004,  // Active, executing
   RW_THR_INTERRUPTED    = 0x0008,  // Active; Waiting for releaseInterrupt()
   RW_THR_SUSPENDED      = 0x0010,  // Active; Waiting for resume()
   RW_THR_SLEEPING       = 0x0020,  // Active; Waiting for expiration of sleep period
   RW_THR_YIELDING       = 0x0040,  // Active; Yielded execution to other Runnables, Waiting for execution
   RW_THR_CANCELING      = 0x0080,  // Active; Runnable cancellation in progress (CANCELING replaces RUNNING)
   RW_THR_ABORTING       = 0x0100,  // Active; Aborting cancellation (ABORTING replaces CANCELING)
   RW_THR_TERMINATING    = 0x0200,  // Active; Terminating a RWThreadImp runnable
   RW_THR_EXCEPTION      = 0x0400,  // Active; Exiting with an exception
   RW_THR_SERVER_WAIT    = 0x0800,  // Active; Server Waiting for another Runnable to execute 
   RW_THR_RESERVED1      = 0x1000,  // Reserved for future use by Rogue Wave Software
   RW_THR_RESERVED2      = 0x2000,  // Reserved for future use by Rogue Wave Software
   RW_THR_USER1          = 0x4000,  // Active; User code called enterUser1() (USER1 replaces RUNNING)
   RW_THR_USER2          = 0x8000,  // Active; User code called enterUser2() (USER2 replaces RUNNING)
   RW_THR_ACTIVE         = 0xFFFC   // Mask value for all states but initial and starting, used to test for succussful startup
};

enum RWCompletionState {
   RW_THR_PENDING,      // Runnable has not yet exited because it has not been started or is still active
   RW_THR_NORMAL,       // exited normally with optional exit code
   RW_THR_FAILED,       // exited with exception
   RW_THR_CANCELED,     // exited in response to request for cancellation
   RW_THR_TERMINATED,   // exited in response to external termination with optional exit code
   RW_THR_UNEXPECTED    // exiting due to unhandled exception or signal (currently shutting down process)
};

// Forward declarations...
class RWTHRExport RWRunnableImp;
class RWTHRExport RWRunnable;
class RWTHRExport RWRunnableSelf;
class RWTHRExport RWThreadManagerImp;

#if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated in this header file...
template class RWTHRIExport RWFunctor2<const RWRunnable&,RWExecutionState>;
template class RWTHRIExport RWFunctor2Imp<const RWRunnable&,RWExecutionState>;
#endif

// RWRunnableHandle is the base class for all runnable handles and
// is used to reference and access RWRunnableImp instances.
// It also provides access to those runnable functions that may be
// accessed by any thread, regardless of whether or not the
// calling thread is currently executing "inside" or "outside" of the 
// runnable instance.  An RWTHRInvalidPointer exception is produced 
// when an attempt is made to call any accessor function when the 
// handle does not point to a valid runnable instance.

class RWTHRExport RWRunnableHandle :
   public RWTHRHandle {
   
   RW_THR_DECLARE_TRACEABLE
   
   public:

      /////////////////////////////////////////////////////////////////////////
      //  The following functions may be called by any thread
      /////////////////////////////////////////////////////////////////////////

      // Add a callback to end of state-change callback list.  The mask
      // value is used to choose the runnable state or states for which 
      // the callback is to be executed (See RWExecutionState).  The scope 
      // argument determines whether the callback is called just once or 
      // repeatedly. Callbacks are called in FIFO order!
      void
      addCallback(const RWFunctor2<const RWRunnable&,RWExecutionState>& functor,
                  unsigned long stateMask,
                  RWCallbackScope scope=RW_THR_CALL_REPEATEDLY)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Query the completion status of the runnable.
      RWCompletionState
      getCompletionState(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Query the current execution state of the runnable.
      RWExecutionState
      getExecutionState(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Query runnable to determine whether or not the runnable has an 
      // outstanding request to interrupt.
      RWBoolean
      isInterruptRequested(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);
      
      // Indicates whether calling thread is the same thread that is 
      // currently executing within this runnable...
      RWBoolean
      isSelf(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Indicates whether specified thread id refers to the thread 
      // that is currently executing within this runnable...
      RWBoolean
      isSelf(const RWThreadId& id) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Remove all previously added callback entries that call the specified 
      // functor.  This function locks the runnable object while removing the 
      // callback and unlocks it when done.
      void
      removeCallback(const RWFunctor2<const RWRunnable&,RWExecutionState>& functor)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Query for the id of the thread currently active within the runnable.
      // The exception RWTHRThreadNotActive is thrown if there is not a thread
      // executing within the runnable.
      RWThreadId
      threadId(void) const
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

   protected:
      
      // Construct an empty, invalid, handle instance
      RWRunnableHandle(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWRunnableHandle(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to a runnable instance 
      RWRunnableHandle(RWRunnableImp* runnableImpP)
         RWTHRTHROWSANY;

      // Bind a new handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableHandle(const RWRunnableHandle& second)
         RWTHRTHROWSANY;

#  if RW_THR_COMPILER_VERSION_HP_CPP == 0x00101100

   // HP cfront-base compiler incorrectly complains about access
   // to the operator= member!  So make it public as workaround...
   public:

      // Bind this handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableHandle&
      operator=(const RWRunnableHandle& second)
         RWTHRTHROWSANY;

   protected:

#  else

      // Bind this handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableHandle&
      operator=(const RWRunnableHandle& second)
         RWTHRTHROWSANY;

#  endif

      // Get a reference to the runnable instance, if any, 
      // pointed-to by this handle instance
      RWRunnableImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};

// RWRunnable is used to reference and access RWRunnableImp instances.
// It provides access to those runnable functions that may be
// accessed by any threads executing "outside" of the runnable
// that is pointed-to by the handle.  An RWTHRIllegalAccess exception
// will be produced if the thread executing "inside" a runnable 
// object uses this interface to access its own runnable object.  

class RWTHRExport RWRunnable :
   public RWRunnableHandle {
   
   RW_THR_DECLARE_TRACEABLE

   friend class RWRunnableImp; // Give access to protected constructor.
   friend class RWRunnableSelf; // Give access to protected constructor.
      
   public:

      // Construct an empty, invalid, handle instance
      RWRunnable(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWRunnable(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnable(const RWRunnable& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnable&
      operator=(const RWRunnable& second)
         RWTHRTHROWSANY;

      // Return an internal interface handle instance bound to the same 
      // runnable instance, if any, pointed-to by this handle instance.
      RWRunnableSelf
      getRWRunnableSelf(void) const
         RWTHRTHROWSANY;

      /////////////////////////////////////////////////////////////////////////
      //  The following functions may be called by external threads only
      /////////////////////////////////////////////////////////////////////////

      // Query for an active nested runnable (if any).
      // This function returns a reference to the runnable
      // object this runnable's thread is currently executing within.
      // If thread is only active within the current runnable, 
      // then the handle returned will not point to any runnable 
      // and will return FALSE if tested by calling the isValid() member.
      RWRunnable
      getNestedRunnable(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Wait for this runnable to complete execution.
      
      // Returns immediately if the runnable has already 
      // been started and has completed and is currently inactive.

      // If the runnable object is going to be restarted, then this function 
      // should be used with care by threads other than the thread starting 
      // the runnable; user code will need to synchronize the thread(s) 
      // starting the runnable with the thread(s) joining the runnable so 
      // that joining threads will know which iteration they are joining!
      void
      join(void)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Wait for this runnable to complete execution or until expiration
      // of the specified time-out period.
      
      // Returns immediately if the runnable has already been started and 
      // completed and is currently inactive.  Waits for the runnable to 
      // complete if it has never been started, or if it has been started 
      // and is currently active.

      // If the runnable object is going to be restarted, then this function 
      // should be used with care by threads other than the thread starting 
      // the runnable; user code will need to synchronize the thread(s) 
      // starting the runnable with the thread(s) joining the runnable so 
      // that joining threads can know which start operation they are joining!
      RWWaitStatus
      join(unsigned long milliseconds)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Force the exception, if any, produced during the execution of this 
      // runnable to be rethrown.  This function simply returns if no 
      // exception was produced during the most recently completed execution 
      // of this runnable.
      void
      raise(void) const
         RWTHRTHROWSANY;

      // Release the thread executing with the runnable from an interrupt.
      // Changes the execution state to the state assumed by the runnable 
      // prior to its entering the interrupted state (typically this state
      // will be RW_THR_RUNNING or one of the user defined states).
      void
      releaseInterrupt(void)
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRThreadActive,
                      RWTHRInternalError);

      // Request and wait for the runnable to cancel itself.  Cancellation
      // starts when the thread running within the runnable instance calls
      // the serviceCancellation() member.  This member checks for a
      // cancellation request, and if found, throws a special exception 
      // object called RWCancellation.  This object notifies the runnable
      // instance when it is created and when it is destroyed so that the
      // thread requesting cancellation can be notified of the success or
      // failure of the cancellation attempt.
      // This member returns:
      // RW_THR_COMPLETED 
      //    To indicate that the runnable was canceled, exited, or was not 
      //    active anyway.
      // RW_THR_ABORTED 
      //    To indicate that cancellation started but did not complete.
      //    (The cancellation exception was caught and destroyed in a
      //    catch-block (exception-handler)).
      // If the cancellation is successful, then this function will have 
      // produced a sequence of events that result in the runnable execution 
      // changing to RW_THR_CANCELING and then to RW_THR_INITIAL.
      // If the cancellation was aborted, the state will have changed from
      // its current state to RW_THR_CANCELING, then to RW_THR_ABORTING, and 
      // will eventually change back to RW_THR_RUNNING.
      RWWaitStatus
      requestCancellation(void)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Request and wait for the runnable to cancel itself or until the
      // specified amount of time has passed.  
      // This member returns:
      // RW_THR_COMPLETED 
      //    To indicate that the runnable was canceled, exited, or was not 
      //    active anyway.
      // RW_THR_ABORTED 
      //    To indicate that cancellation started but did not complete.
      //    (The cancellation exception was caught and destroyed in a
      //    catch-block (exception-handler)).
      // RW_THR_TIMEOUT
      //    To indicate that the time-out period elapsed before the runnable 
      //    completed cancellation.
      // If the cancellation is successful, then this function will have 
      // produced a sequence of events that result in the runnable execution 
      // changing to RW_THR_CANCELING and then to RW_THR_INITIAL.
      // If the cancellation was aborted, the state will have changed from
      // its current state to RW_THR_CANCELING, then to RW_THR_ABORTING, and 
      // will eventually change back to RW_THR_RUNNING.
      // If the cancellation request times-out, then the execution state may
      // not have changed at all, or may have changed to RW_THR_CANCELING,
      // in which case, cancellation has already started, and may yet abort 
      // or succeed.
      RWWaitStatus
      requestCancellation(unsigned long milliseconds)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Request the thread executing within the runnable to interrupt and
      // wait until the thread is interrupted. To respond to the request 
      // for an interrupt, the thread executing within the runnable must call 
      // the runnable's serviceInterrupt() member.  If this member is called 
      // while there is an outstanding request for an interrupt, the calling
      // thread is blocked inside the serviceInterrupt() call until some other 
      // thread releases the interrupt by calling releaseInterrupt();
      // This function returns:
      //    RW_THR_ACQUIRED - If the runnable *was* interrupted - a return 
      //                      value of RW_THR_ACQUIRED does not guarantee 
      //                      that the runnable is currently interrupted, 
      //                      as any thread may release a runnable from an 
      //                      interrupt, and the user may design an 
      //                      application in such a manner that another 
      //                      thread was able to release the interrupt before 
      //                      this function returns.
      //    RW_THR_ABORTED - If the runnable is not active
      // If the interrupt is acquired, then the execution state will have
      // been changed to RW_THR_INTERRUPTED.
      RWWaitStatus
      requestInterrupt(void)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Same as previous function except the user may limit the time the
      // calling thread will wait for the runnable thread to interrupt.
      // Returns:
      //    RW_THR_ACQUIRED - If the runnable was interrupted (see prior 
      //                      discussion above).
      //    RW_THR_TIMEOUT - If the runnable did not interrupt prior to the 
      //                     expiration of the time-out interval
      //    RW_THR_ABORTED - If the runnable is not active
      // If the interrupt is acquired, then the execution state will have
      // been changed to RW_THR_INTERRUPTED.
      RWWaitStatus
      requestInterrupt(unsigned long milliseconds)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Start execution of the runnable object. 
      // For RWRunnableImp instances, this causes the calling thread to
      // execute synchronously "within" the runnable object.  For RWThreadImp 
      // instances, start() results in the creation of a new thread of 
      // execution that will execute "within" the runnable object.
      // Changes the execution state to RW_THR_STARTING, and for synchronous
      // runnables, changes to RW_THR_RUNNING.  RWThreadImp runnables enter
      // the RW_THR_INTERRUPTED state shortly starting, and are then are 
      // automatically or manually released to RW_THR_RUNNING.
      RWCompletionState
      start(void)
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWTHRThreadActive,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Waits until the runnable object enters an execution state referenced
      // by the mask.  It returns the execution state value that satisfied 
      // the specified mask.
      RWExecutionState
      wait(unsigned long stateMask)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Waits until the runnable object enters a state contained in the
      // mask, or until the time-out period of milliseconds has elapsed.
      // If a masked state is entered prior to the expiration of the 
      // time-out period, this member will write the state value that
      // satisfied the wait in the state argument location (if not rwnil),
      // and returns a value of RW_THR_SIGNALED.  If the time-out period
      // elapses without a state change that satisfies the mask, then
      // the function returns RW_THR_TIMEOUT without changing the 
      // storage specified by the state argument.
      RWWaitStatus 
      wait(unsigned long stateMask,
           RWExecutionState* state,
           unsigned long milliseconds) 
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

   protected:
      
      // Construct an external interface handle to a RWRunnableImp instance
      // Used by derived classes during make operations.
      RWRunnable(RWRunnableImp* runnableImpP)
         RWTHRTHROWSANY;

      // Construct an external interface handle to the runnable 
      // object (if any) pointed-to by the internal handle.
      // This is used by RWRunnableSelf to implement getRWRunnable().
      RWRunnable(const RWRunnableSelf& second)
         RWTHRTHROWSANY;

};

// RWRunnableSelf is used to reference and access RWRunnableImp instances.
// It provides access to those runnable functions that may be accessed by 
// the thread executing "inside" of the runnable instance. 
// An RWTHRIllegalAccess exception will be produced if the thread executing 
// "outside" a runnable object uses this interface to access its own runnable 
// instance.  

class RWTHRExport RWRunnableSelf :
   public RWRunnableHandle {
   
   RW_THR_DECLARE_TRACEABLE

   friend class RWRunnableImp; // Give access to protected constructor.
   friend class RWRunnable; // Give access to protected constructor.
   
   public:

      // Construct an empty, invalid, handle instance
      RWRunnableSelf(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWRunnableSelf(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableSelf(const RWRunnableSelf& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableSelf&
      operator=(const RWRunnableSelf& second)
         RWTHRTHROWSANY;

      // Return an external interface handle bound to the same runnable 
      // instance (if any) pointed-to by this handle.
      RWRunnable
      getRWRunnable(void) const
         RWTHRTHROWSANY;

      /////////////////////////////////////////////////////////////////////////
      //  The following functions may be called by an internal thread only
      /////////////////////////////////////////////////////////////////////////

      // Query for an active nested runnable (if any).
      // This function returns a reference to the runnable
      // object this runnable's thread is currently executing within.
      // If thread is only active within the current runnable, 
      // then the handle returned will not point to any runnable 
      // and will return FALSE if tested by calling the isValid() member.
      RWRunnableSelf
      getNestedRunnable(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Interrupts the calling thread executing within the runnable until 
      // the runnable is released by another thread.
      // Changes execution state to RW_THR_INTERRUPTED.
      void
      interrupt(void)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Checks for cancellation requests, throwing an RWCancellation object
      // if cancellation is requested, and returning if otherwise.
      // May result in a change of execution state to RW_THR_CANCELING.
      void
      serviceCancellation(void)
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWCancellation,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Checks for interrupt requests, blocking the calling thread if an 
      // interrupt has been requested and returning immediately if no 
      // interrupt requests are pending.
      // May result in a change of execution state to RW_THR_INTERRUPTED.
      RWBoolean
      serviceInterrupt(void)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Causes the calling thread, executing within the runnable, to sleep 
      // for the specified time period, yielding execution to other threads.
      // Temporarily changes the execution state to RW_THR_SLEEPING 
      // while sleeping, and then restores the previous execution state.
      // The global function rwSleep() may be used instead of this function,
      // but rwSleep() does *not* produce any execution state changes in
      // a runnable instance while this member does.
      void
      sleep(unsigned long milliseconds)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Explicitly yield control to other threads. Depending on the
      // scheduler, control will be given to another thread with the
      // same priority. If there is none, then this thread will continue
      // to run. Temporarily changes the execution state to RW_THR_YIELDING. 
      // while yielding, and then restores the previous execution state.
      // The global function rwYield() may be used instead of this function,
      // but rwYield() does *not* produce any execution state changes in
      // a runnable instance while this member does.
      void
      yield(void)
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);
    
   protected:

      // Bind a new internal interface handle instance to the runnable 
      // object, if any, pointed-to by an external handle instance.
      // This is used by RWRunnable to implement getRWRunnableSelf().
      RWRunnableSelf(const RWRunnable& second)
         RWTHRTHROWSANY;

      // Register a "nested" runnable object so that it might be notified 
      // and serviced during cancellation.  This is done automatically when 
      // a runnable is started by a thread created by a RWThreadImp or 
      // derived class instance. A runnable is "nested" if its active thread
      // originated or is already executing within another runnable instance.
      void
      setNestedRunnable(const RWRunnable& runnable) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

};

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file...
template class RWTHRIExport RWMaskedCallback2Elem<const RWRunnable&,RWExecutionState>;
template class RWTHRIExport RWMaskedCallback2List<RWMutexLock,const RWRunnable&,RWExecutionState>;
template class RWTHRIExport RWPointer<RWMaskedCallback2List<RWMutexLock,const RWRunnable&,RWExecutionState> >;
template class RWTHRIExport RWOnlyPointer<RWMaskedCallback2List<RWMutexLock,const RWRunnable&,RWExecutionState> >;
template class RWTHRIExport RWTValSlist<RWMaskedCallback2Elem<const RWRunnable&,RWExecutionState> >;
#    if defined(RW_NO_STL)
template class RWTHRIExport RWTValSlink<RWMaskedCallback2Elem<const RWRunnable&,RWExecutionState> >;
template class RWTHRIExport RWTIsvSlist<RWTValSlink<RWMaskedCallback2Elem<const RWRunnable&,RWExecutionState> > >;
#    endif
template class RWTHRIExport RWPointer<RWTHRxmsg>;
template class RWTHRIExport RWOnlyPointer<RWTHRxmsg>;
#  endif

// RWRunnableImp is the abstract base class for all runnable objects. 
// A "runnable" is an object that is used to control and manage threads, and 
// also acts as the abstract representation of the "work" to be done by a 
// thread.  A runnable object becomes "active" when its start() member is 
// called.  If the runnable object is an instance of a derived class 
// with synchronous behavior (such as provided by RWRunnableFunctor0), then the 
// thread that calls start() effectively enters the runnable instance to 
// become its active thread.  Only one thread may be active within a runnable 
// instance at one time - the start() member will throw an exception if 
// any thread attempts to call start while a thread is already executing 
// "inside" of the runnable.  If the runnable object is an instance of a
// class derived from RWThreadImp (also a derivative of this class), then a 
// call to start will result in the creation of a new thread of execution 
// within the runnable, freeing the starting thread for other activities.
// Runnable objects may be directly executed (as described above) or queued 
// on a RWRunnableServer or RWRunnableServerPool instance for start-up and
// execution in a separate thread.


class RWTHRExport RWRunnableImp :
   public RWTHRBody { // All runnables are reference counted

   RW_THR_DECLARE_TRACEABLE

   friend class RWCancellationImp;

   friend class RWRunnableHandle;
   friend class RWRunnable;
   friend class RWRunnableSelf;
   friend class RWThreadManagerImp;

   protected:

      // Pointer to an RWMaskedCallback2List object used to maintain callbacks 
      // that are invoked in response to changes in execution state.  To reduce 
      // the construction cost of RWRunnableImp objects, the callback list is 
      // not constructed until a request is made to add or remove a callback 
      // from the runnable instance.
      RWOnlyPointer<RWMaskedCallback2List<RWMutexLock,const RWRunnable&,RWExecutionState> >   callbacks_;

      // The completion status for this runnable (RW_THR_PENDING if still running)
      RWCompletionState                   completionState_;

      // The exception, if any, that cause this runnable to exit
      RWOnlyPointer<RWTHRxmsg>            exception_;

      // The current execution state of this runnable
      RWExecutionState                    executionState_;

      // Flag that indicates that this runnable is supposed to cancel
      RWBoolean                           isCanceled_;

      // Number of outstanding interrupt requests
      unsigned                            interruptCount_;

      // Handle to a nested runnable, if any, that is currently being
      // executed by the same thread that is executing within this runnable.
      // This field is used to link runnable object instances together for
      // purposes of thread control and to support queries for the current 
      // runnable object associated with a thread (if any).  
      RWRunnable                          nestedRunnable_;

      // Saved execution state (for temporary state changes)
      RWExecutionState                    savedExecutionState_;

      // The id of this runnable's thread, if any.
      RWThreadId                          threadId_;


#if defined(RW_THR_CANT_NEST_PROTECTED_TEMPLATE)
      public:
#endif

      // Define a helper class for use in signaling state 
      // changes to interested threads.
      class MaskedSemaphore {
         RW_THR_DECLARE_TRACEABLE
         private:
            // Semphore used for signaling
            RWSemaphore       semaphore_;
            // Mask that identifies the desired state(s)
            unsigned long     mask_;
            // The state that matched the mask and resulted in the signal
            RWExecutionState  state_;
         public:
            // Construct a masked semphore instance with mask
            MaskedSemaphore(unsigned long mask)
               RWTHRTHROWSANY;
            // Do nothing destructor to keep stdlib happy...
            ~MaskedSemaphore(void) 
               RWTHRTHROWSNONE;
            // Get the current execution state mask value
            unsigned long mask(void) const
               RWTHRTHROWSNONE;
            // Get the execution state that matched the mask
            RWExecutionState state(void) const
               RWTHRTHROWSNONE;
            // Set the execution state
            void state(RWExecutionState state)
               RWTHRTHROWSNONE;
            // Decrement (acquire) the semaphore, blocking while zero.
            void acquire(void) 
               RWTHRTHROWS2(RWTHRResourceLimit, // Ignore cancellation, it can't happen                            
                            RWTHRInternalError);
            // Decrement (acquire) the semaphore, blocking while zero,
            // until unblocked when another thread increments (releases)
            // the semaphore or until the specified amount of time passes.
            RWWaitStatus acquire(unsigned long milliseconds) 
               RWTHRTHROWS2(RWTHRResourceLimit, // Ignore cancellation, it can't happen
                            RWTHRInternalError);
            // Increment (release) the semaphore and unblock one waiting thread (if any)
            void release(void) 
               RWTHRTHROWS1(RWTHRInternalError);
            // Try to decrement (acquire) semaphore without blocking
            // return TRUE for success and FALSE if the semaphore is zero.
            RWBoolean tryAcquire(void) 
               RWTHRTHROWS1(RWTHRInternalError);
      };


#if defined(RW_THR_CANT_NEST_PROTECTED_TEMPLATE)
      protected:
#endif

      // Define a list for threads waiting on execution state changes.
      RWTValSlist<MaskedSemaphore*>       waiters_;

   public:

      // Destroy runnable object (called by destructor of last handle instance)
      virtual
      ~RWRunnableImp(void)
         RWTHRTHROWSNONE;

   protected: 

   /////////////////////////////////////////////////////////////////////////
   //  The following functions may be accessed by any thread
   /////////////////////////////////////////////////////////////////////////

      // Add a callback to end of state-change callback list.  The mask
      // value is used to choose the runnable state or states for which 
      // the callback is to be executed.  The scope argument determines
      // whether the callback is called just once or repeatedly.
      // Callbacks are called in FIFO order!
      void
      addCallback(const RWFunctor2<const RWRunnable&,RWExecutionState>& functor,
                  unsigned long stateMask,
                  RWCallbackScope scope=RW_THR_CALL_REPEATEDLY)
         RWTHRTHROWS1(RWTHRInternalError);

      // Query for an active nested runnable (if any).
      // This function returns a reference to the runnable
      // object this runnable's thread is currently executing within.
      // If thread is only active within the current runnable, 
      // then the handle returned will not point to any runnable 
      // and will return FALSE if tested by calling the isValid() member.
      RWRunnable
      getNestedRunnable(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      // Query the completion status of the runnable.
      RWCompletionState
      getCompletionState(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      // Query the current execution state of the runnable.
      RWExecutionState
      getExecutionState(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      // Query runnable to determine whether or not the runnable has an 
      // outstanding request to interrupt.
      RWBoolean
      isInterruptRequested(void) const
         RWTHRTHROWS1(RWTHRInternalError);
      
      // Indicates whether calling thread is the same thread that is 
      // currently executing within this runnable...
      RWBoolean
      isSelf(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      // Indicates whether specified thread id refers to the thread 
      // that is currently executing within this runnable...
      RWBoolean
      isSelf(const RWThreadId& id) const
         RWTHRTHROWS1(RWTHRInternalError);

      // Remove all previously added callback entries that call the specified 
      // functor.  This function locks the runnable object while removing the 
      // callback and unlocks it when done.
      void
      removeCallback(const RWFunctor2<const RWRunnable&,RWExecutionState>& functor)
         RWTHRTHROWS1(RWTHRInternalError);

      // Query for the id of the thread currently active within the runnable.
      // The exception RWTHRThreadNotActive is thrown if there is not a thread
      // executing within the runnable.
      RWThreadId  
      threadId(void) const
         RWTHRTHROWS2(RWTHRThreadNotActive,
                      RWTHRInternalError);

   /////////////////////////////////////////////////////////////////////////
   //  The following functions may be accessed by any thread except 
   //  the thread currently executing within the runnable instance.
   /////////////////////////////////////////////////////////////////////////

      // Wait for this runnable to complete execution.
      
      // Returns immediately if the runnable has already 
      // been started and has completed and is currently inactive.

      // If the runnable object is going to be restarted, then this function 
      // should be used with care by threads other than the thread starting 
      // the runnable; user code will need to synchronize the thread(s) 
      // starting the runnable with the thread(s) joining the runnable so 
      // that joining threads will know which iteration they are joining!
      
      void
      join(void)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Wait for this runnable to complete execution or for the
      // specified time period to elapse.
      
      // Returns immediately if the runnable has already been started and 
      // completed and is currently inactive.  Waits for the runnable to 
      // complete if it has never been started, or if it has been started 
      // and is currently active.

      // If the runnable object is going to be restarted, then this function 
      // should be used with care by threads other than the thread starting 
      // the runnable; user code will need to synchronize the thread(s) 
      // starting the runnable with the thread(s) joining the runnable so 
      // that joining threads will know which iteration they are joining!

      RWWaitStatus
      join(unsigned long milliseconds)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Force the exception, if any, produced during the execution of this 
      // runnable to be rethrown.  This function simply returns if no 
      // exception was produced during the most recently completed execution 
      // of this runnable.
      void
      raise(void) const
         RWTHRTHROWSANY;

      // Release the thread executing with the runnable from an interrupt.
      void
      releaseInterrupt(void)
         RWTHRTHROWS3(RWTHRIllegalAccess,
                      RWTHRThreadActive,
                      RWTHRInternalError);

      // Request and wait for the runnable to cancel itself.
      // Returns:
      // RW_THR_COMPLETED 
      //    To indicate that the runnable was canceled or was not active anyway
      // RW_THR_ABORTED 
      //    To indicate that cancellation started, but did not complete.
      RWWaitStatus
      requestCancellation(void)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Request and wait for the runnable to cancel itself within the 
      // specified time period.
      // Returns:
      // RW_THR_COMPLETED 
      //    To indicate that the runnable was canceled or was not active anyway
      // RW_THR_ABORTED 
      //    To indicate that cancellation started, but did not complete.
      // RW_THR_TIMEOUT
      //    To indicate that the runnable failed to complete cancellation within
      //    the specified time period.  The runnable may have started cancellation
      //    (the execution state may be RW_THR_CANCELING), but did not complete it 
      //    within the specified time-out period.
      RWWaitStatus
      requestCancellation(unsigned long milliseconds) 
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Requests the thread executing within the runnable to interrupt and
      // waits until the thread is interrupted. To respond to the request 
      // for an interrupt, the thread executing within the runnable must call 
      // the serviceInterrupt() member.  If this member is called while there 
      // is an outstanding request for an interrupt, the runnable thread is 
      // blocked inside the serviceInterrupt() call until some other thread 
      // releases the interrupt by calling releaseInterrupt();
      // This function returns:
      //    RW_THR_ACQUIRED - If the runnable *was* interrupted - a return 
      //                      value of RW_THR_ACQUIRED does not guarantee 
      //                      that the runnable is currently interrupted, 
      //                      as any thread may release a runnable from an 
      //                      interrupt, and the user may design an 
      //                      application in such a manner that another 
      //                      thread or callback is able to release the 
      //                      interrupt before this function returns.
      //    RW_THR_ABORTED - If the runnable is not active
      RWWaitStatus
      requestInterrupt(void)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);


      // Same as previous function except the user may limit the time the
      // calling thread will wait for the runnable thread to interrupt.
      // Returns:
      //    RW_THR_ACQUIRED - If the runnable was interrupted (see prior 
      //                      discussion above).
      //    RW_THR_TIMEOUT - If the runnable did not interrupt prior to the 
      //                     expiration of the time-out interval
      //    RW_THR_ABORTED - If the runnable is not active
      RWWaitStatus
      requestInterrupt(unsigned long milliseconds)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Start execution of the runnable object. 
      // For RWRunnableImp instances, this causes the calling thread to
      // execute synchronously "within" the runnable object.  For RWThreadImp 
      // instances, start() results in the creation of a new thread of 
      // execution that will execute "within" the runnable object.
      RWCompletionState
      start(void)
         RWTHRTHROWS3(RWTHRThreadActive,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Waits until the runnable object enters an execution state referenced
      // by the mask.  It returns the execution state value that satisfied 
      // the specified mask.
      RWExecutionState
      wait(unsigned long stateMask)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Waits until the runnable object enters a state contained in the
      // mask, or until the time-out period of milliseconds has elapsed.
      // If a masked state is entered prior to the expiration of the 
      // time-out period, this member will write the state value that
      // satisfied the wait in the state argument location (if not rwnil),
      // and returns a value of RW_THR_SIGNALED.  If the time-out period
      // elapses without a state change that satisfies the mask, then
      // the function returns RW_THR_TIMEOUT without changing the 
      // storage specified by the state argument.
      RWWaitStatus
      wait(unsigned long stateMask,
           RWExecutionState* state, 
           unsigned long milliseconds)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);


   /////////////////////////////////////////////////////////////////////////
   //  The following functions may only be accessed by the thread
   //  currently executing within the runnable instance.
   /////////////////////////////////////////////////////////////////////////

      // Interrupts the calling thread executing within the runnable until 
      // the runnable is released by another thread.
      void
      interrupt(void)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Checks for cancellation requests, throwing an RWCancellation object
      // if cancellation is requested, and returning if otherwise.
      void
      serviceCancellation(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Checks for interrupt requests, blocking the calling thread if an 
      // interrupt has been requested and returning immediately if no 
      // interrupt requests are pending.
      RWBoolean
      serviceInterrupt(void)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Causes the calling thread, executing within the runnable, to sleep 
      // for the specified time period, yielding execution to other threads.
      void
      sleep(unsigned long milliseconds)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      // Causes the calling thread executing within the runnable to yield 
      // execution to other threads.
      void
      yield(void)
         RWTHRTHROWS2(RWTHRIllegalAccess,
                      RWTHRInternalError);

      
      /////////////////////////////////////////////////////////////////////////
      //  The following functions are used internally or by derived classes.
      /////////////////////////////////////////////////////////////////////////

      // Construct a new runnable instance
      RWRunnableImp(void)
         RWTHRTHROWSANY;

      // Construct a new runnable instance, but don't copy any members
      RWRunnableImp(const RWRunnableImp& second)
         RWTHRTHROWSANY;

      // Assignment operator that doesn't copy anything 
      RWRunnableImp&
      operator=(const RWRunnableImp& second)
         RWTHRTHROWSNONE;

      // Virtual method called by requestCancellation() members. Allows
      // derived classes define behavior to support cancellation requests.
      // A typical implementation might take some action to unblock the
      // the derived-class runnable so that it might check for cancellation.
      // The RWRunnable monitor mutex will be LOCKED upon entry.
      virtual
      void
      _checkCancellation(void)
         RWTHRTHROWSANY;

      // Virtual method called by requestInterrupt() members. Allows
      // derived classes define behavior to support interrupt requests.
      // A typical implementation might take some action to unblock the
      // the derived-class runnable so that it might check for interrupt.
      // The RWRunnable monitor mutex will be LOCKED upon entry.
      void
      _checkInterrupt(void)
         RWTHRTHROWSANY;

      // Determines execution concurrency mechanism to use for running the 
      // exec() member.
      // The RWRunnableImp implementation simply makes a synchronous call to 
      // the exec() member.  RWThreadImp overrides this member to create
      // a thread that eventually calls the exec() member.
      // This function expects the RWRunnable monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      virtual
      void
      _dispatchExec(void)
         RWTHRTHROWSANY;

      // Perform setup, execution, and cleanup of the run() member 
      // Catches any exceptions produced during execution of run().
      // This member should be called with the monitor mutex UNLOCKED!
      void
      exec(void)
         RWTHRTHROWSNONE;

      // Release the thread executing with the runnable from an interrupt.
      // This function expects the RWRunnable monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      void
      _releaseInterrupt(void)
         RWTHRTHROWSANY;

      // Internal version of requestInterrupt(void) member.
      // This function expects the RWRunnable monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex while waiting
      // for the interrupt and is therefore, NOT ATOMIC.
      RWWaitStatus
      _requestInterrupt(void)
         RWTHRTHROWSANY;

      // Restores the execution state to the state that existed 
      // prior to the last call to _setExecutionState(), notifying
      // waiting threads that the state has changed, and firing callbacks
      // whose mask matches the restored state.
      // This function expects the RWRunnable monitor mutex to be UNLOCKED
      // upon entry, and will lock and unlock the mutex internally.
      RWExecutionState
      restoreExecutionState(void)
         RWTHRTHROWS1(RWTHRInternalError);

      // Restores the execution state to the state that existed 
      // prior to the last call to _setExecutionState(), notifying
      // waiting threads that the state has changed, and firing callbacks
      // whose mask matches the restored state.
      // This function expects the RWRunnable monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      RWExecutionState
      _restoreExecutionState(void)
         RWTHRTHROWSANY;

      // Function representing the work to be done by this runnable.
      // Must be overridden in derived classes (The default implementation
      // will produce an assert!).
      // This member should be called with the monitor mutex UNLOCKED!
      virtual
      void
      run(void)
         RWTHRTHROWSANY;

      // Perform cleanup following execution of the virtual run() member.
      // This function expects the RWRunnable monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      virtual
      void
      _runEpilog(void)
         RWTHRTHROWSANY;

      // Perform setup prior execution of the virtual run() member.
      // This function expects the RWRunnable monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      virtual 
      void
      _runProlog(void)
         RWTHRTHROWSANY;

      // Return a handle instance bound to the current RWRunnableImp instance
      RWRunnable
      self(void) const
         RWTHRTHROWSANY;

      // Checks for interrupt requests, blocking the calling thread if an 
      // interrupt has been requested and returning immediately if no 
      // interrupt requests are pending.  The function returns TRUE if
      // the calling thread was interrupted, and FALSE if otherwise.
      // This function expects the RWRunnable monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      RWBoolean
      _serviceInterrupt(void)
         RWTHRTHROWSANY;

      // Checks for cancellation requests, throwing an RWCancellation object
      // if cancellation is requested, and returning if otherwise.
      void
      _serviceCancellation(void)
         RWTHRTHROWSANY;

      // Sets the execution state to the specified state, notifying
      // waiting threads that the state has changed, and firing callbacks
      // whose mask matches the new state.
      // This function expects the RWRunnable monitor mutex to be UNLOCKED
      // upon entry, and will lock and unlock the mutex internally.
      RWExecutionState
      setExecutionState(RWExecutionState newState)
         RWTHRTHROWS1(RWTHRInternalError);

      // Sets the execution state to the specified state, notifying
      // waiting threads that the state has changed, and firing callbacks
      // whose mask matches the new state.
      // This function expects the RWRunnable monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      RWExecutionState
      _setExecutionState(RWExecutionState newState)
         RWTHRTHROWSANY;

      // Register a single runnable object so that it might be notified 
      // and serviced during cancellation.  This is done automatically when 
      // a runnable is started by a thread created by a RWThreadImp or 
      // derived class instance.
      void
      setNestedRunnable(const RWRunnable&)
         RWTHRTHROWS1(RWTHRInternalError);

      // Signal any threads that are waiting for this runnable to
      // enter the current execution state.
      void
      _signalWaitingThreads(RWExecutionState newState)
         RWTHRTHROWSANY;

      // Waits until the runnable object enters a state contained in the
      // mask. Returns the execution state value that ended the wait.
      // This function must be called with the monitor mutex LOCKED,
      // and the mutex will be unlocked while waiting for state changes,
      // so this function is NOT ATOMIC.
      RWExecutionState
      _wait(unsigned long stateMask)
         RWTHRTHROWSANY;

      // Waits until the runnable object enters a state contained in the
      // mask, or until the time-out period of milliseconds has elapsed.
      // It returns the actual state value in the state argument, and
      // a return value of RW_THR_SIGNALED if a masked state was entered, 
      // or RW_THR_TIMEOUT, if a masked state was not encountered within the
      // specified time period.
      // This function must be called with the monitor mutex LOCKED,
      // and the mutex will be unlocked while waiting for state changes,
      // so this function is NOT ATOMIC.
      RWWaitStatus
      _wait(unsigned long stateMask,
            RWExecutionState* state, 
            unsigned long milliseconds)
         RWTHRTHROWSANY;

   private:

      // Used by the RWCancellationImp object to indicate that 
      // cancellation was aborted (the cancellation object was destroyed).
      void 
      abortCancellation(void)
         RWTHRTHROWSNONE;

      // Get a reference to the current callback list instance, if there
      // is one, otherwise construct a barrier object and then return
      // its reference.
      // This function must be called with the monitor mutex LOCKED,
      // and it does not unlock the mutex, so this function is ATOMIC.
      RWMaskedCallback2List<RWMutexLock,const RWRunnable&,RWExecutionState>&
      _callbacks(void)
         RWTHRTHROWSANY;

      // Common initialization for runnable object construction.
      // Locking is not required with this member as access to a runnable 
      // under construction is assumed to be synchronized within user code.
      void
      init(void)
         RWTHRTHROWSANY;

      // Called by the RWCancellationImp object to indicate that 
      // cancellation has started.  
      void
      initiateCancellation(void)
         RWTHRTHROWSNONE;
   
      // Invoke any callbacks associated with an execution state.  
      // The runnable object must not be locked when this member is 
      // called, as the runnable object will be locked and unlocked
      // as RWRunnable handles are copied during the callback invocation 
      // process (and the callback code will probably want to access 
      // the runnable object anyway!)
      // This function must be called with the monitor mutex LOCKED,
      // and the mutex will be unlocked and locked while updating the list,
      // then re-locked, so this function is NOT ATOMIC.
      void
      _invokeCallbacks(RWExecutionState state)
         RWTHRTHROWSANY;
   
      // Test to see if we need to worry about callbacks
      // The callback list object will not get constructed until a request 
      // is made to install a callback on the current instance.
      // This function must be called with the monitor mutex LOCKED,
      // and it does not unlock the mutex, so this function is ATOMIC.
      RWBoolean 
      _isCallbacks(void) const
         RWTHRTHROWSANY;

};

// Function that retrieves the runnable that the calling thread is
// is currently executing within (if any).  The handle returned may
// be tested by calling the handle's isValid() member to determine 
// whether or not the calling thread is executing within any runnable.
extern rwthrexport
RWRunnableSelf
rwRunnable(void)
   RWTHRTHROWSANY;

// The following functions simplify the creation of callback functors
// for use with runnable objects.

#define rwMakeRunnableCallbackG(function)                 \
   RWFunctor2GImp<const RWRunnable&,        /* S1 */      \
                  RWExecutionState,         /* S2 */      \
                  void,                     /* DR */      \
                  const RWRunnable&,        /* D1 */      \
                  RWExecutionState          /* D2 */      \
                 >::make(function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

RWFunctor2<const RWRunnable&,RWExecutionState>
rwMakeRunnableCallback(void (*function)(const RWRunnable&,RWExecutionState))
   RWTHRTHROWSANY;

#  endif

#define rwMakeRunnableCallbackGA1(function,A1,a1)           \
   RWFunctor2GA1Imp<const RWRunnable&,        /* S1 */      \
                    RWExecutionState,         /* S2 */      \
                    void,                     /* DR */      \
                    const RWRunnable&,        /* D1 */      \
                    RWExecutionState,         /* D2 */      \
                    A1                        /* A1 */      \
                   >::make(function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class A1, class AA1>
RWFunctor2<const RWRunnable&,RWExecutionState>
rwMakeRunnableCallback(void (*function)(const RWRunnable&,RWExecutionState,A1),
                       AA1 a1)
   RWTHRTHROWSANY
{
   return rwMakeRunnableCallbackGA1(function,A1,a1);
}

#  endif

#define rwMakeRunnableCallbackGA2(function,A1,a1,A2,a2)     \
   RWFunctor2GA2Imp<const RWRunnable&,        /* S1 */      \
                    RWExecutionState,         /* S2 */      \
                    void,                     /* DR */      \
                    const RWRunnable&,        /* D1 */      \
                    RWExecutionState,         /* D2 */      \
                    A1,                       /* A1 */      \
                    A2                        /* A2 */      \
                   >::make(function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class A1, class A2, class AA1, class AA2>
RWFunctor2<const RWRunnable&,RWExecutionState>
rwMakeRunnableCallback(void (*function)(const RWRunnable&,RWExecutionState,A1,A2),
                       AA1 a1,
                       AA2 a2)
   RWTHRTHROWSANY
{
   return rwMakeRunnableCallbackGA2(function,A1,a1,A2,a2);
}

#  endif

#define rwMakeRunnableCallbackM(Callee,callee,function) \
   RWFunctor2MImp<const RWRunnable&,        /* S1 */        \
                  RWExecutionState,         /* S2 */        \
                  Callee,                   /* Callee */    \
                  void,                     /* DR */        \
                  const RWRunnable&,        /* D1 */        \
                  RWExecutionState          /* D2 */        \
                 >::make(callee,function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class Callee>
RWFunctor2<const RWRunnable&,RWExecutionState>
rwMakeRunnableCallback(Callee& callee,
                       void (Callee::*function)(const RWRunnable&,RWExecutionState))
   RWTHRTHROWSANY
{
   return rwMakeRunnableCallbackM(Callee,callee,function);
}

#  endif

#define rwMakeRunnableCallbackMA1(Callee,callee,function,A1,a1) \
   RWFunctor2MA1Imp<const RWRunnable&,        /* S1 */              \
                    RWExecutionState,         /* S2 */              \
                    Callee,                   /* Callee */          \
                    void,                     /* DR */              \
                    const RWRunnable&,        /* D1 */              \
                    RWExecutionState,         /* D2 */              \
                    A1                        /* A1 */              \
                   >::make(callee,function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class Callee, class A1, class AA1>
RWFunctor2<const RWRunnable&,RWExecutionState>
rwMakeRunnableCallback(Callee& callee,
                       void (Callee::*function)(const RWRunnable&,RWExecutionState,A1),
                       AA1 a1)
   RWTHRTHROWSANY
{
   return rwMakeRunnableCallbackMA1(Callee,callee,function,A1,a1);
}

#  endif

#define rwMakeRunnableCallbackMA2(Callee,callee,function,A1,a1,A2,a2) \
   RWFunctor2MA2Imp<const RWRunnable&,        /* S1 */                    \
                    RWExecutionState,         /* S2 */                    \
                    Callee,                   /* Callee */                \
                    void,                     /* DR */                    \
                    const RWRunnable&,        /* D1 */                    \
                    RWExecutionState,         /* D2 */                    \
                    A1,                       /* A1 */                    \
                    A2                        /* A2 */                    \
                   >::make(callee,function,a1,a2)


#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class Callee, class A1, class A2, class AA1, class AA2>
RWFunctor2<const RWRunnable&,RWExecutionState>
rwMakeRunnableCallback(Callee& callee,
                       void (Callee::*function)(const RWRunnable&,RWExecutionState,A1,A2),
                       AA1 a1,
                       AA2 a2)
   RWTHRTHROWSANY
{
   return rwMakeRunnableCallbackMA2(Callee,callee,function,A1,a1,A2,a2);
}

#  endif

/*****************************************************************************/

inline
RWRunnableHandle::RWRunnableHandle(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableHandle,RWRunnableHandle(void));
}

inline
RWRunnableHandle::RWRunnableHandle(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWRunnableHandle,RWRunnableHandle(RWStaticCtor));
}

inline
RWRunnableHandle::RWRunnableHandle(RWRunnableImp* runnableImpP)
   RWTHRTHROWSANY
   :
      RWTHRHandle(runnableImpP)
{
   RWTHRTRACEMF(RWRunnableHandle,RWRunnableHandle(RWRunnableImp*));
}
      
inline
RWRunnableHandle::RWRunnableHandle(const RWRunnableHandle& second)
   RWTHRTHROWSANY
   :
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWRunnableHandle,RWRunnableHandle(const RWRunnableHandle&));
}

inline
RWRunnableHandle&
RWRunnableHandle::operator=(const RWRunnableHandle& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableHandle,operator=(const RWRunnableHandle&):RWRunnableHandle&);
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

// protected
inline
RWRunnableImp&
RWRunnableHandle::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWRunnableHandle,body(void) const:RWRunnableImp&);
   return (RWRunnableImp&)RWTHRHandle::body();
}

inline
void
RWRunnableHandle::addCallback(const RWFunctor2<const RWRunnable&,RWExecutionState>& functor,
                              unsigned long stateMask,
                              RWCallbackScope scope)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableHandle,"addCallback(const RWFunctor2<const RWRunnable&,RWExecutionState>&,unsigned long,RWCallbackScope):void");
   body().addCallback(functor,stateMask,scope);
}

inline
RWCompletionState
RWRunnableHandle::getCompletionState(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableHandle,getCompletionState(void):RWCompletionState);
   return body().getCompletionState();
}

inline
RWExecutionState
RWRunnableHandle::getExecutionState(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableHandle,getExecutionState(void):RWExecutionState);
   return body().getExecutionState();
}

inline
RWBoolean
RWRunnableHandle::isInterruptRequested(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableHandle,isInterruptRequested(void):RWBoolean);
   return body().isInterruptRequested();
}
      
inline
RWBoolean
RWRunnableHandle::isSelf(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableHandle,isSelf(void):RWBoolean);
   return body().isSelf();
}

inline
RWBoolean
RWRunnableHandle::isSelf(const RWThreadId& id) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableHandle,isSelf(const RWThreadId&):RWBoolean);
   return body().isSelf(id);
}

inline
void
RWRunnableHandle::removeCallback(const RWFunctor2<const RWRunnable&,RWExecutionState>& functor)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableHandle,"removeCallback(const RWFunctor2<const RWRunnable&,RWExecutionState>&):void");
   body().removeCallback(functor);
}

inline
RWThreadId  
RWRunnableHandle::threadId(void) const
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableHandle,threadId(void):RWThreadId);
   return body().threadId();
}

/*****************************************************************************/

inline
RWRunnable::RWRunnable(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnable,RWRunnable(void));
}

inline
RWRunnable::RWRunnable(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWRunnableHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWRunnable,RWRunnable(RWStaticCtor));
}

inline
RWRunnable::RWRunnable(RWRunnableImp* runnableImpP)
   RWTHRTHROWSANY
   :
      RWRunnableHandle(runnableImpP)
{
   RWTHRTRACEMF(RWRunnable,RWRunnable(RWRunnableImp*));
}
      
inline
RWRunnable::RWRunnable(const RWRunnable& second)
   RWTHRTHROWSANY
   :
      RWRunnableHandle(second)
{
   RWTHRTRACEMF(RWRunnable,RWRunnable(const RWRunnable&));
}

inline
RWRunnable::RWRunnable(const RWRunnableSelf& second)
   RWTHRTHROWSANY
   :
      RWRunnableHandle(second)
{
   RWTHRTRACEMF(RWRunnable,RWRunnable(const RWRunnableSelf&));
}

inline
RWRunnable&
RWRunnable::operator=(const RWRunnable& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnable,operator=(const RWRunnable&):RWRunnable&);
   if (&second != this)
      RWRunnableHandle::operator=(second);
   return *this;
}

inline
void
RWRunnable::join(void)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,join(void):void);
   body().join();
}

inline
RWWaitStatus
RWRunnable::join(unsigned long milliseconds)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,join(unsigned long):RWWaitStatus);
   return body().join(milliseconds);
}

inline
void
RWRunnable::raise(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnable,raise(void):void);
   body().raise();
}

inline
void
RWRunnable::releaseInterrupt(void)
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRThreadActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,releaseInterrupt(void):void);
   body().releaseInterrupt();
}

inline
RWWaitStatus
RWRunnable::requestCancellation(void)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,requestCancellation(void):RWWaitStatus);
   return body().requestCancellation();
}

inline
RWWaitStatus
RWRunnable::requestCancellation(unsigned long milliseconds)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,requestCancellation(unsigned long):RWWaitStatus);
   return body().requestCancellation(milliseconds);
}

inline
RWWaitStatus
RWRunnable::requestInterrupt(void)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,requestInterrupt(void):RWWaitStatus);
   return body().requestInterrupt();
}

inline
RWWaitStatus
RWRunnable::requestInterrupt(unsigned long milliseconds)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,requestInterrupt(unsigned long):RWWaitStatus);
   return body().requestInterrupt(milliseconds);
}

inline
RWCompletionState
RWRunnable::start(void)
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWTHRThreadActive,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,start(void):RWCompletionState);
   return body().start();
}

inline
RWExecutionState
RWRunnable::wait(unsigned long stateMask)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,wait(unsigned long):RWExecutionState);
   return body().wait(stateMask);
}

inline
RWWaitStatus
RWRunnable::wait(unsigned long stateMask,
                             RWExecutionState* state, 
                             unsigned long milliseconds)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnable,wait(unsigned long,RWExecutionState*,unsigned long):RWWaitStatus);
   return body().wait(stateMask,state,milliseconds);
}

/*****************************************************************************/

inline
RWRunnableSelf::RWRunnableSelf(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableSelf,RWRunnableSelf(void));
}

inline
RWRunnableSelf::RWRunnableSelf(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWRunnableHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWRunnableSelf,RWRunnableSelf(RWStaticCtor));
}

inline
RWRunnableSelf::RWRunnableSelf(const RWRunnableSelf& second)
   RWTHRTHROWSANY
   :
      RWRunnableHandle(second)
{
   RWTHRTRACEMF(RWRunnableSelf,RWRunnableSelf(const RWRunnableSelf&));
}

inline
RWRunnableSelf::RWRunnableSelf(const RWRunnable& second)
   RWTHRTHROWSANY
   :
      RWRunnableHandle(second)
{
   RWTHRTRACEMF(RWRunnableSelf,RWRunnableSelf(const RWRunnable&));
}

inline
RWRunnableSelf&
RWRunnableSelf::operator=(const RWRunnableSelf& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableSelf,operator=(const RWRunnableSelf&):RWRunnableSelf&);
   if (&second != this)
      RWRunnableHandle::operator=(second);
   return *this;
}

inline
void
RWRunnableSelf::interrupt(void)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableSelf,interrupt(void):void);
   body().interrupt();
}

inline
void
RWRunnableSelf::serviceCancellation(void)
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWCancellation,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableSelf,serviceCancellation(void):void);
   body().serviceCancellation();
}

inline
RWBoolean
RWRunnableSelf::serviceInterrupt(void)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableSelf,serviceInterrupt(void):RWBoolean);
   return body().serviceInterrupt();
}

// protected
inline
void
RWRunnableSelf::setNestedRunnable(const RWRunnable& runnable) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableSelf,setNestedRunnable(const RWRunnable&):void);
   body().setNestedRunnable(runnable);
}

inline
void
RWRunnableSelf::sleep(unsigned long milliseconds)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableSelf,sleep(unsigned long):void);
   body().sleep(milliseconds);
}

inline
void
RWRunnableSelf::yield(void)
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableSelf,yield(void):void);
   body().yield();
}

/*****************************************************************************/

// Implement a do-nothing copy-constructor to keep the 
// compiler from trying to copy members.
inline
RWRunnableImp::RWRunnableImp(const RWRunnableImp& second)
   RWTHRTHROWSANY
   :
      RWTHRBody(second)
{
   RWTHRTRACEMF(RWRunnableImp,RWRunnableImp(const RWRunnableImp&):void);
}

// Implement a do-nothing assignment operator to keep the 
// compiler from trying to assign members.
inline
RWRunnableImp&
RWRunnableImp::operator=(const RWRunnableImp& second)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableImp,operator=(const RWRunnableImp&):RWRunnableImp&);
   if (&second != this)
      RWTHRBody::operator=(second);
   return *this;
}

// Return a handle instance bound to the current RWRunnableImp instance
inline
RWRunnable
RWRunnableImp::self(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableImp,self(void) const:RWRunnable);
   return RWRunnable(RW_THR_CONST_CAST(RWRunnableImp*,this));   
}

/*****************************************************************************/
            
inline
RWRunnableImp::MaskedSemaphore::MaskedSemaphore(unsigned long mask)
   RWTHRTHROWSANY
   :
      semaphore_(0),
      mask_(mask)
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,MaskedSemaphore(unsigned long));
}

inline
unsigned long 
RWRunnableImp::MaskedSemaphore::mask(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,mask(void):unsigned long);
   return mask_;
}

inline
RWExecutionState 
RWRunnableImp::MaskedSemaphore::state(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,state(void):RWExecutionState);
   return state_;
}

inline
void 
RWRunnableImp::MaskedSemaphore::state(RWExecutionState state)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,state(RWExecutionState):void);
   state_ = state;
}

inline
void 
RWRunnableImp::MaskedSemaphore::acquire(void) 
   RWTHRTHROWS2(RWTHRResourceLimit, 
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,acquire(void):void);
   semaphore_.acquire();
}

inline
RWWaitStatus 
RWRunnableImp::MaskedSemaphore::acquire(unsigned long milliseconds) 
   RWTHRTHROWS2(RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,acquire(unsigned long):RWWaitStatus);
   return semaphore_.acquire(milliseconds);
}

inline
void 
RWRunnableImp::MaskedSemaphore::release(void) 
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,release(void):void);
   semaphore_.release();
}

inline
RWBoolean 
RWRunnableImp::MaskedSemaphore::tryAcquire(void) 
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,tryAcquire(void):RWBoolean);
   return semaphore_.tryAcquire();
}

inline
RWRunnableImp::MaskedSemaphore::~MaskedSemaphore(void) 
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableImp::MaskedSemaphore,~MaskedSemaphore(void));
}
#endif // __RWTHRRUNNABLE_H__

