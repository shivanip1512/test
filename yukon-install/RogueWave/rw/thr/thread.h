#if !defined(__RWTHRTHREAD_H__)
#  define __RWTHRTHREAD_H__
/*****************************************************************************
 *
 * thread.h
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

thread.h - Declarations for:

   RWThreadImp - Internal thread runnable implementation.

   RWThread - Handle class whose instances may be bound to  RWThreadImp
              instances.  A RWThread instance is used by threads
              executing "outside" of a RWThreadImp instance to access and
              manipulate that instance.  Only those RWThreadImp functions
              that are safe for use by external threads are made available
              in the interface of this class.

   RWThreadSelf - Handle class whose instances may be bound to a RWThreadImp
                  instances.  A RWThreadSelf instance is used by the thread
                  executing "inside" a RWThreadImp instance to access and
                  manipulate that instance.  Only those RWThreadImp functions
                  that are safe for use by the internal thread are made
                  available in the interface of this class.

See Also:

   thread.cpp - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if defined(RW_THR_THREAD_API_WIN32)
#     if !defined(__RWTVSLIST_H__)
#        include <rw/tvslist.h>
#     endif
#     if !defined(__RWTHRONLYPTR_H__)
#        include <rw/thr/onlyptr.h>
#     endif
#  endif

#  if defined(RW_THR_THREAD_API_OS2)
#     if !defined(__RW_FLOAT_H__)
#        define __RW_FLOAT_H__ <float.h>
#        include __RW_FLOAT_H__
#     endif
#     if !defined(__RW_STDLIB_H__)
#        define __RW_STDLIB_H__ <stdlib.h>
#        include __RW_STDLIB_H__
#     endif
#  endif

#  if !defined(__RWTHRTHRATTR_H__)
#     include <rw/thr/thrattr.h>
#  endif

#  if !defined(__RWTHRRUNNABLE_H__)
#     include <rw/thr/runnable.h>
#  endif

#  if defined(RW_THR_THREAD_API_SOLARIS)
//    signal.h is required to define signal value to be used in terminating Solaris threads.
#     if !defined(__RW_SIGNAL_H__)
#        define __RW_SIGNAL_H__ <signal.h>
#        include __RW_SIGNAL_H__
#     endif
#     define RW_THR_TERMINATE_SIGNAL  SIGPWR
#  endif

#  if defined(RW_THR_THREAD_API_SOLARIS) || defined(RW_THR_THREAD_API_POSIX_1003_1C_SOLARIS)
#     if !defined(__RW_SYS_LWP_H__)
#        define __RW_SYS_LWP_H__ <sys/lwp.h>
#        include __RW_SYS_LWP_H__
#     endif
#  endif

#  if defined(RW_THR_HAS_SUSPEND_RESUME)
#    include <rw/thr/condtion.h>
#  endif

class RWTHRExport RWCancellationImp;

class RWTHRExport RWThreadImp;
class RWTHRExport RWThread;
class RWTHRExport RWThreadSelf;

class RWTHRExport RWThread :
   public RWRunnable {

   friend class RWThreadImp;
   friend class RWThreadSelf;
   friend class RWThreadManagerImp;

   public:

      // Construct an empty, invalid, handle instance
      RWThread(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWThread(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to the thread runnable instance, if any,
      // pointed-to by another handle instance
      RWThread(const RWThread& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the thread runnable instance, if any,
      // pointed-to by another handle instance
      RWThread&
      operator=(const RWThread& second)
         RWTHRTHROWSANY;

      // Return an internal interface handle instance bound to the same thread
      // instance, if any, pointed-to by this handle instance.
      RWThreadSelf
      getRWThreadSelf(void) const
         RWTHRTHROWSANY;


      /////////////////////////////////////////////////////////////////////////
      // Common thread functions - May only accessed through either handle!
      /////////////////////////////////////////////////////////////////////////

      /////////////////////////////////////////////////////////////////////////
      // This function gets a handle to the thread attribute instance that will 
      // be used to initialize any threads created by future calls to the this
      // runnable's start() member.  If the thread attribute has not yet been 
      // set on the current threaded runnable instance, this function simply 
      // returns a handle to the default thread attribute instance created when 
      // the threaded runnable was constructed.  This internal instance may be 
      // manipulated through the returned handle.
      //
      // The attribute instance returned may not have the same attribute values 
      // as the thread attribute instance used to create any existing thread,
      // since a private copy of the current thread attribute object is made 
      // each time a thread is started.  Use the getActiveAttribute() function
      // to get a copy of the attribute instance actual used to initialize the 
      // most recently created thread.
      //
      // This function will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWThreadAttribute
      getAttribute(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function replaces the thread attribute instance that will 
      // be used to initialize any threads created by future calls to this 
      // runnable's start() member.  Changing a thread runnable's attribute 
      // object after the runnable has already been started will not affect its 
      // current thread; it can only affect the thread created the next time 
      // the runnable is started.
      //
      // This function will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      void
      setAttribute(const RWThreadAttribute& second)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The following function may be used to get a handle to a copy of the 
      // thread attribute instance used to initialize the most recently created 
      // thread.  If a thread has not yet been created, this function will
      // simply return a copy of a default thread attribute instance.
      //
      // This function will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWThreadAttribute
      getActiveAttribute(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The following functions may be used to determine the legal range
      // of values for various active thread attributes.
      //
      // These functions return a maximum or minimum value for a particular
      // active thread attribute.
      //
      // These functions will throw an RWTHROperationNotSupported exception
      // if the corresponding attribute is not supported. This exception can be 
      // avoided by testing to see if the corresponding feature test macro is 
      // currently defined (see each function below for the macro).
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These functions may throw a RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason)
      // and the thread must be active to determine the value.
      //
      // All of the exceptions listed above can be avoided by using an
      // appropriate "can get" function to test for availability prior to 
      // calling the "get min/max" function.  You must understand that even if 
      // you get a return value of TRUE from these test functions, the 
      // "get min/max" function may still produce several of these exceptions 
      // if other threads are simultaneously manipulating the thread 
      // attributes, or if the target thread exits before the "get min/max" 
      // function can be called.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Get the maximum priority value that may be specified for this thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getMaxPriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum process-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getMaxProcessScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum system-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getMaxSystemScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum time-slice quantum value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canGetTimeSliceQuantum()
      unsigned long
      getMaxTimeSliceQuantum(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      // Get the minimum priority value that may be specified for this thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getMinPriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum process-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getMinProcessScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum system-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getMinSystemScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the minimum time-slice quantum value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canGetTimeSliceQuantum()
      unsigned long
      getMinTimeSliceQuantum(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // This static function returns TRUE if the getMaxThreads() function is
      // supported in the current environment, and returns FALSE if not.
      /////////////////////////////////////////////////////////////////////////
      static
      RWBoolean
      canGetMaxThreads(void)
         RWTHRTHROWSNONE;

      /////////////////////////////////////////////////////////////////////////
      // This static function returns the maximum number of threads that may be 
      // created in this environment.  This function is not supported in all
      // environments.  Use the feature test macro RW_THR_HAS_MAX_THREADS or the
      // feature test function canGetMaxThreads() to test for availability.
      //
      // This function throws an RWTHROperationNotSupported exception if not
      // supported in the current environment.
      /////////////////////////////////////////////////////////////////////////
      static
      size_t
      getMaxThreads(void)
         RWTHRTHROWS1(RWTHROperationNotSupported);


      /////////////////////////////////////////////////////////////////////////
      // The "can get" test functions below can be used to determine at 
      // run-time whether the corresponding attribute value may be read.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the corrsponding
      // "get" function CANNOT return a legal value under current circumstances.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the corresponding "get" 
      // function CAN return a legal value under current circumstances.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      canGetPriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canGetProcessScopePriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canGetSchedulingPolicy(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canGetSystemScopePriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canGetTimeSliceQuantum(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      
      /////////////////////////////////////////////////////////////////////////
      // The "get" functions below can be used to query the corresponding
      // attribute.
      // 
      // These functions will throw an RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the value is not 
      // supported or cannot be queried under under the current circumstances.
      // 
      // These functions will throw an RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // Some of these functions will throw an RWTHRResourceLimit exception if 
      // the operation could not be performed due to memory or system resource
      // constraints.  The exception message will give the specific reason.
      //
      // All of the exceptions above can be avoided by using the appropriate 
      // "can get" function to test for availability prior to calling the "get"
      // function.  You must understand that even if you get a return value of 
      // TRUE from these test functions, a "get" function may still produce 
      // several of these exceptions of if other threads are simultaneously 
      // manipulating the same attributes, or if the target thread exits before 
      // the "get" function can be called.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Get the current priority for the active thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getPriority(void) const
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current process-scope priority for the active thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getProcessScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current scheduling policy for the active thread.
      // Feature Tests: RW_THR_HAS_SCHEDULING_POLICY, 
      //                canGetSchedulingPolicy()
      RWSchedulingPolicy
      getSchedulingPolicy(void) const
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current system-scope priority for the active thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getSystemScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current time slice quantum for the active thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      unsigned long
      getTimeSliceQuantum(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The public "can set" test functions below can be used to determine, at 
      // run-time, whether the corresponding attribute value may be set.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the specified 
      // policy, if any, is not supported under the current circumstances.
      // either because the specified value is not supported under the current 
      // scheduling options or because the calling thread or process does not 
      // possess sufficient privileges to perform the requested operation.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the specified policy is legal
      // under the current circumstances.
      //
      // Some of these functions will produce a RWTHRBoundsError exception if
      // the specified value is not a legal policy value for that attribute.  
      // This exception can be avoided by making sure that you are using the 
      // appropriate value names for enumerated attributes.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // Some of these functions will throw an RWTHRInternalError exception 
      // if some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      canSetPriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canSetProcessScopePriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canSetSchedulingPolicy(RWSchedulingPolicy policy) const
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetSystemScopePriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canSetTimeSliceQuantum(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The "set" functions below can be used to change the corresponding
      // attribute.
      // 
      // These functions will throw an RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the specified value is
      // not supported under the current circumstances, either because the
      // specified value is not supported under the current scheduling options
      // or because the calling thread or process does not possess sufficient
      // privileges to perform the requested operation.
      // 
      // These functions will throw an RWTHRBoundsError exception if the
      // specified value is invalid or falls outside the current legal range
      // for that attribute.
      //
      // These functions will throw an RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // Some of these functions will throw an RWTHRResourceLimit exception if 
      // the operation could not be performed due to memory or system resource
      // constraints.  The exception message will give the specific reason.
      //
      // All of the exceptions above can be avoided by using the appropriate 
      // "can set" function to test for availability prior to calling the "set"
      // function.  You must understand that even if you get a return value of 
      // TRUE from these test functions, a "set" function may still produce 
      // several of these exceptions of if other threads are simultaneously 
      // manipulating the same attributes, or if the target thread exits before 
      // the "set" function can be called.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Change the priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canSetPriority()
      void
      setPriority(RWPriority priority)
         RWTHRTHROWS7(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the process scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canSetProcessScopePriority()
      void
      setProcessScopePriority(RWPriority priority)
         RWTHRTHROWS7(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the scheduling policy currently appied to this runnable's 
      // thread.
      // Feature Tests: RW_THR_HAS_SCHEDULING_POLICY, 
      //                canSetSchedulingPolicy()
      void
      setSchedulingPolicy(RWSchedulingPolicy policy)
         RWTHRTHROWS7(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the system scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canSetSystemScopePriority()
      void
      setSystemScopePriority(RWPriority priority)
         RWTHRTHROWS7(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the system scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canSetTimeSliceQuantum()
      void
      setTimeSliceQuantum(unsigned long milliseconds)
         RWTHRTHROWS6(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns TRUE if the current environment supports the
      // suspend() and resume() functions, FALSE if otherwise
      /////////////////////////////////////////////////////////////////////////
      static
      RWBoolean
      canSuspendResume(void)
         RWTHRTHROWSNONE;


      /////////////////////////////////////////////////////////////////////////
      // Suspend the execution of this runnable's thread until a matching resume 
      // operation is performed.
      // Feature Tests: RW_THR_HAS_SUSPEND_RESUME, 
      //                canSuspendResume()
      // CAUTION: Use of this function may produce unexpected deadlock! See the 
      //          User's Guide for explanation.  Use requestInterrupt() and 
      //          serviceInterrupt() for dead-lock safe suspension!
      /////////////////////////////////////////////////////////////////////////
      unsigned
      suspend(void)
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // External thread functions - May only be access through this handle!
      /////////////////////////////////////////////////////////////////////////

      // Resume this thread after it has been suspended.
      // Feature Tests: RW_THR_HAS_SUSPEND_RESUME, 
      //                canSuspendResume()
      unsigned
      resume(void)
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHRIllegalAccess,
                      RWTHRThreadActive,
                      RWTHRInternalError);

      // Get the number of times this thread has been suspended without being 
      // released.  A return value of zero indicates that the thread is not 
      // currently suspended or is inactive. 
      // Feature Tests: RW_THR_HAS_SUSPEND_RESUME, 
      //                canSuspendResume()
      unsigned
      getSuspendCount(void) const
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      // Terminate the execution of this runnable's thread.
      // CAUTION: This operation kills the thread object's thread without giving
      //          it a chance to release locks, unwind the stack, or recover
      //          resources!
      //                      *** Use only as a last resort! ***
      //          Note that thread object storage cannot be recovered once
      //          terminate is used; each thread object creates a automatic
      //          reference to itself while executing within its started
      //          thread, and since termination does not unwind the stack,
      //          this reference will never be destroyed, leaving the thread
      //          object with a reference count that can never return to zero.
      void
      terminate(void)
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHRIllegalAccess,
                      RWTHRThreadNotActive,
                      RWTHRThreadActive,
                      RWTHRInternalError);

   protected:

      // Bind a new handle instance to a thread runnable instance 
      RWThread(RWThreadImp* threadImpP)
         RWTHRTHROWSANY;

      // Construct an external interface handle to the RWThreadImp
      // instance pointed-to by an internal interface handle
      // (used by the RWThreadSelf::getThread() member.
      RWThread(const RWThreadSelf& second)
         RWTHRTHROWSANY;

      // Override the parent class body() function to provide
      // version that casts to this handle's body class
      RWThreadImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

class RWTHRExport RWThreadSelf :
   public RWRunnableSelf {

   friend class RWThreadImp;
   friend class RWThread;

   public:

      // Construct an empty, invalid, handle instance
      RWThreadSelf(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWThreadSelf(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to the thread runnable instance, if any,
      // pointed-to by another handle instance
      RWThreadSelf(const RWThreadSelf& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the thread runnable instance, if any,
      // pointed-to by another handle instance
      RWThreadSelf&
      operator=(const RWThreadSelf& second)
         RWTHRTHROWSANY;

      // Return an external interface handle instance bound to the same thread
      // instance, if any, pointed-to by this handle instance.
      RWThread
      getRWThread(void) const
         RWTHRTHROWSANY;

      /////////////////////////////////////////////////////////////////////////
      // Common thread functions - May only accessed through either handle!
      /////////////////////////////////////////////////////////////////////////

      /////////////////////////////////////////////////////////////////////////
      // This function gets a handle to the thread attribute instance that will 
      // be used to initialize any threads created by future calls to the this
      // runnable's start() member.  If the thread attribute has not yet been 
      // set on the current threaded runnable instance, this function simply 
      // returns a handle to the default thread attribute instance created when 
      // the threaded runnable was constructed.  This internal instance may be 
      // manipulated through the returned handle.
      //
      // The attribute instance returned may not have the same attribute values 
      // as the thread attribute instance used to create any existing thread,
      // since a private copy of the current thread attribute object is made 
      // each time a thread is started.  Use the getActiveAttribute() function
      // to get a copy of the attribute instance actual used to initialize the 
      // most recently created thread.
      //
      // This function will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWThreadAttribute
      getAttribute(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function replaces the thread attribute instance that will 
      // be used to initialize any threads created by future calls to this 
      // runnable's start() member.  Changing a thread runnable's attribute 
      // object after the runnable has already been started will not affect its 
      // current thread; it can only affect the thread created the next time 
      // the runnable is started.
      //
      // This function will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      void
      setAttribute(const RWThreadAttribute& second)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The following function may be used to get a handle to a copy of the 
      // thread attribute instance used to initialize the most recently created 
      // thread.  If a thread has not yet been created, this function will
      // simply return a copy of a default thread attribute instance.
      //
      // This function will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWThreadAttribute
      getActiveAttribute(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The following functions may be used to determine the legal range
      // of values for various active thread attributes.
      //
      // These functions return a maximum or minimum value for a particular
      // active thread attribute.
      //
      // These functions will throw an RWTHROperationNotSupported exception
      // if the corresponding attribute is not supported. This exception can be 
      // avoided by testing to see if the corresponding feature test macro is 
      // currently defined (see each function below for the macro).
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These functions will throw a RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // All of the exceptions listed above can be avoided by using an
      // appropriate "can get" function to test for availability prior to 
      // calling the "get min/max" function.  You must understand that even if 
      // you get a return value of TRUE from these test functions, the 
      // "get min/max" function may still produce several of these exceptions 
      // if other threads are simultaneously manipulating the thread 
      // attributes, or if the target thread exits before the "get min/max" 
      // function can be called.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Get the maximum priority value that may be specified for this thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getMaxPriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum process-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getMaxProcessScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum system-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getMaxSystemScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum time-slice quantum value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canGetTimeSliceQuantum()
      unsigned long
      getMaxTimeSliceQuantum(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      // Get the minimum priority value that may be specified for this thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getMinPriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum process-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getMinProcessScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum system-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getMinSystemScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the minimum time-slice quantum value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canGetTimeSliceQuantum()
      unsigned long
      getMinTimeSliceQuantum(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // This static function returns TRUE if the getMaxThreads() function is
      // supported in the current environment, and returns FALSE if not.
      /////////////////////////////////////////////////////////////////////////
      static
      RWBoolean
      canGetMaxThreads(void)
         RWTHRTHROWSNONE;

      /////////////////////////////////////////////////////////////////////////
      // This static function returns the maximum number of threads that may be 
      // created in this environment.  This function is not supported in all
      // environments.  Use the feature test macro RW_THR_HAS_MAX_THREADS or the
      // feature test function canGetMaxThreads() to test for availability.
      //
      // This function throws an RWTHROperationNotSupported exception if not
      // supported in the current environment.
      /////////////////////////////////////////////////////////////////////////
      static
      size_t
      getMaxThreads(void)
         RWTHRTHROWS1(RWTHROperationNotSupported);


      /////////////////////////////////////////////////////////////////////////
      // The "can get" test functions below can be used to determine at 
      // run-time whether the corresponding attribute value may be read.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the corrsponding
      // "get" function CANNOT return a legal value under current circumstances.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the corresponding "get" 
      // function CAN return a legal value under current circumstances.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      canGetPriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canGetProcessScopePriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canGetSchedulingPolicy(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canGetSystemScopePriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canGetTimeSliceQuantum(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      
      /////////////////////////////////////////////////////////////////////////
      // The "get" functions below can be used to query the corresponding
      // attribute.
      // 
      // These functions will throw an RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the value is not 
      // supported or cannot be queried under under the current circumstances.
      // 
      // These functions will throw an RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // Some of these functions will throw an RWTHRResourceLimit exception if 
      // the operation could not be performed due to memory or system resource
      // constraints.  The exception message will give the specific reason.
      //
      // All of the exceptions above can be avoided by using the appropriate 
      // "can get" function to test for availability prior to calling the "get"
      // function.  You must understand that even if you get a return value of 
      // TRUE from these test functions, a "get" function may still produce 
      // several of these exceptions of if other threads are simultaneously 
      // manipulating the same attributes, or if the target thread exits before 
      // the "get" function can be called.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Get the current priority for the active thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getPriority(void) const
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current process-scope priority for the active thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getProcessScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current scheduling policy for the active thread.
      // Feature Tests: RW_THR_HAS_SCHEDULING_POLICY, 
      //                canGetSchedulingPolicy()
      RWSchedulingPolicy
      getSchedulingPolicy(void) const
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current system-scope priority for the active thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getSystemScopePriority(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current system-scope priority for the active thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      unsigned long
      getTimeSliceQuantum(void) const
         RWTHRTHROWS5(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The public "can set" test functions below can be used to determine, at 
      // run-time, whether the corresponding attribute value may be set.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the specified 
      // policy, if any, is not supported under the current circumstances.
      // either because the specified value is not supported under the current 
      // scheduling options or because the calling thread or process does not 
      // possess sufficient privileges to perform the requested operation.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the specified policy is legal
      // under the current circumstances.
      //
      // Some of these functions will produce a RWTHRBoundsError exception if
      // the specified value is not a legal policy value for that attribute.  
      // This exception can be avoided by making sure that you are using the 
      // appropriate value names for enumerated attributes.
      //
      // Some of these functions will throw an RWTHRInternalError exception 
      // if some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      canSetPriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canSetProcessScopePriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canSetSchedulingPolicy(RWSchedulingPolicy policy) const
         RWTHRTHROWS3(RWTHRInvalidPointer,
                      RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetSystemScopePriority(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWBoolean
      canSetTimeSliceQuantum(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The "set" functions below can be used to change the corresponding
      // attribute.
      // 
      // These functions will throw an RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the specified value is
      // not supported under the current circumstances, either because the
      // specified value is not supported under the current scheduling options
      // or because the calling thread or process does not possess sufficient
      // privileges to perform the requested operation.
      // 
      // These functions will throw an RWTHRBoundsError exception if the
      // specified value is invalid or falls outside the current legal range
      // for that attribute.
      //
      // These functions will throw an RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // Some of these functions will throw an RWTHRResourceLimit exception if 
      // the operation could not be performed due to memory or system resource
      // constraints.  The exception message will give the specific reason.
      //
      // All of the exceptions above can be avoided by using the appropriate 
      // "can set" function to test for availability prior to calling the "set"
      // function.  You must understand that even if you get a return value of 
      // TRUE from these test functions, a "set" function may still produce 
      // several of these exceptions of if other threads are simultaneously 
      // manipulating the same attributes, or if the target thread exits before 
      // the "set" function can be called.
      //
      // These functions will throw an RWTHRInvalidPointer exception if the 
      // handle is not currently bound to a threaded runnable instance.
      // This exception may be avoided by using this isValid() member to test
      // the handle instance to see if it points to a threaded runnable 
      // instance.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Change the priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canSetPriority()
      void
      setPriority(RWPriority priority)
         RWTHRTHROWS7(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the process scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canSetProcessScopePriority()
      void
      setProcessScopePriority(RWPriority priority)
         RWTHRTHROWS7(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the scheduling policy currently appied to this runnable's 
      // thread.
      // Feature Tests: RW_THR_HAS_SCHEDULING_POLICY, 
      //                canSetSchedulingPolicy()
      void
      setSchedulingPolicy(RWSchedulingPolicy policy)
         RWTHRTHROWS7(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the system scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canSetSystemScopePriority()
      void
      setSystemScopePriority(RWPriority priority)
         RWTHRTHROWS7(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the system scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canSetTimeSliceQuantum()
      void
      setTimeSliceQuantum(unsigned long milliseconds)
         RWTHRTHROWS6(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // This function returns TRUE if the current environment supports the
      // suspend() and resume() functions, FALSE if otherwise
      /////////////////////////////////////////////////////////////////////////
      static
      RWBoolean
      canSuspendResume(void)
         RWTHRTHROWSNONE;


      /////////////////////////////////////////////////////////////////////////
      // Suspend the execution of this runnable's thread until a matching resume 
      // operation is performed.
      // Feature Tests: RW_THR_HAS_SUSPEND_RESUME, 
      //                canSuspendResume()
      // CAUTION: Use of this function may produce unexpected deadlock! See the 
      //          User's Guide for explanation.  Use requestInterrupt() and 
      //          serviceInterrupt() for dead-lock safe suspension!
      /////////////////////////////////////////////////////////////////////////
      unsigned
      suspend(void)
         RWTHRTHROWS4(RWTHRInvalidPointer,
                      RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      // Public "Private" function that is used by RWThreadLocal objects
      // to register instances that need to be destroyed when this thread exits.
#   if defined(RW_THR_THREAD_API_WIN32)
      void
      registerThreadLocalDestructor(void (*destroyFunc)(void *),void* valptr)
         RWTHRTHROWSANY;
#  elif defined(RW_THR_THREAD_API_OS2)
      void
      registerThreadLocalDestructor(void (*destroyFunc)(void *,RWThreadId),void* valptr,RWThreadId)
         RWTHRTHROWSANY;
#  endif

   protected:

      // Construct an internal interface handle to the RWThreadImp
      // instance pointed-to by an external interface handle
      // (used by the RWThread::getThreadSelf() member.
      RWThreadSelf(const RWThread& second)
         RWTHRTHROWSANY;

      // Override the parent class body() function to provide
      // version that casts to this handle's body class
      RWThreadImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

class RWTHRExport RWThreadImp :
   public RWRunnableImp {

   RW_THR_DECLARE_TRACEABLE

   friend class RWThreadManagerImp;

   friend class RWThread;
   friend class RWThreadSelf;

   protected:

      RWThreadAttribute                   initialAttributes_; // May be shared with other threads
      RWThreadAttribute                   currentAttributes_; // Copied from initial - Not shared

#  if defined(RW_THR_THREAD_API_WIN32)
      HANDLE                              hThread_;
#  endif

#  if defined(RW_THR_HAS_SUSPEND_RESUME)
      unsigned                            suspendCount_;

      //This pointer will only be valid while the thread is in the
      //process of suspending itself, or has suspended itself but has
      //not completed being resumed
      RWOnlyPointer<RWCondition>          suspendSelfWaiter_;
#  endif

#  if defined(RW_THR_THREAD_API_SOLARIS) || defined(RW_THR_THREAD_API_POSIX_1003_1C_SOLARIS)
      lwpid_t                             threadLWPId_;
#  endif

#  if defined(RW_THR_THREAD_API_OS2)
      PTIB                                os2AttrBlock_;
#  endif

#   if defined(RW_THR_THREAD_API_WIN32) || \
      defined(RW_THR_THREAD_API_OS2)

#if !defined(RW_THR_CANT_NEST_PROTECTED_TEMPLATE)
   protected:
#else
   public:
#endif

      // OS/2 and Win32 do not provide a mechanism for automatically 
      // destroying instances of thread-local objects when a thread exits,
      // so Threads.h++ keeps track of each thread-local instance within
      // the RWThreadImp instance associated with the thread that created
      // the thread-local instance, so that these thread-local instances
      // may be destroyed at thread exit.  Thread-local instances that are
      // created by threads started outside of the Threads.h++ library are
      // not deleted until the thread-local object itself is deleted. 

      // ThreadLocalDestructor is a private helper class of RWThreadImp,
      // and is used to register each thread-local instance for automatic
      // destruction when the thread associated with a RWThreadImp instance
      // completes its execution and exits.

      class RWTHRExport ThreadLocalDestructor {

         RW_THR_DECLARE_TRACEABLE

         protected:

#     if defined(RW_THR_THREAD_API_WIN32)
            // Define Win32 thread-local destructor function
            void (*destroyFunc_)(void *);
#     else
            // Define OS/2 thread-local destructor function and thread id member
            void (*destroyFunc_)(void *,RWThreadId);
            RWThreadId  threadId_;
#     endif
            void*       valptr_;

         public:

#     if defined(RW_THR_THREAD_API_WIN32)
            // Define thread local destructor for Win32 thread-locals
            ThreadLocalDestructor(void (*destroyFunc)(void *),
                                  void* valptr);
#     else
            // Define thread local destructor for OS/2 thread-locals
            ThreadLocalDestructor(void (*destroyFunc)(void *,RWThreadId),
                                  void* valptr,
                                  RWThreadId threadId);
#     endif
            ~ThreadLocalDestructor(void);
            RWBoolean operator==(const ThreadLocalDestructor& ) const;
      };

      RWTValSlist<RWOnlyPointer<ThreadLocalDestructor> >  threadLocalDestructors_;

   public:

      // Public "Private" function that is used by RWThreadLocal objects
      // to register instances that need to be destroyed when this thread exits.
#     if defined(RW_THR_THREAD_API_WIN32)
      void
      registerThreadLocalDestructor(void (*destroyFunc)(void *),void* valptr)
         RWTHRTHROWSANY;
#     else
      void
      registerThreadLocalDestructor(void (*destroyFunc)(void *,RWThreadId),void* valptr,RWThreadId tid)
         RWTHRTHROWSANY;
#     endif


#  endif

   public:

      // Destroys RWThreadImp instance.
      virtual
      ~RWThreadImp(void)
         RWTHRTHROWSNONE;

   protected:

      // Default constructor used by derived classes when default thread
      // attributes are to be used, or when an attribute instance will be
      // specified after construction.
      RWThreadImp(void)
         RWTHRTHROWSANY;

      // Constructor used by derived classes when an attribute instance is
      // specified at construction time.
      RWThreadImp(const RWThreadAttribute& attr)
         RWTHRTHROWSANY;

      // Construct a new instance but don't copy any members
      RWThreadImp(const RWThreadImp& second)
         RWTHRTHROWSANY;

      // Assignment operator that doesn't assign any members
      RWThreadImp&
      operator=(const RWThreadImp& second)
         RWTHRTHROWSNONE;

      /////////////////////////////////////////////////////////////////////////
      // Common thread functions - May only accessed through either handle!
      /////////////////////////////////////////////////////////////////////////

      /////////////////////////////////////////////////////////////////////////
      // This function gets a handle to the thread attribute instance that will 
      // be used to initialize any threads created by future calls to the this
      // runnable's start() member.  If the thread attribute has not yet been 
      // set on the current threaded runnable instance, this function simply 
      // returns a handle to the default thread attribute instance created when 
      // the threaded runnable was constructed.  This internal instance may be 
      // manipulated through the returned handle.
      //
      // The attribute instance returned may not have the same attribute values 
      // as the thread attribute instance used to create any existing thread,
      // since a private copy of the current thread attribute object is made 
      // each time a thread is started.  Use the getActiveAttribute() function
      // to get a copy of the attribute instance actual used to initialize the 
      // most recently created thread.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWThreadAttribute
      getAttribute(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function replaces the thread attribute instance that will 
      // be used to initialize any threads created by future calls to this 
      // runnable's start() member.  Changing a thread runnable's attribute 
      // object after the runnable has already been started will not affect its 
      // current thread; it can only affect the thread created the next time 
      // the runnable is started.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      void
      setAttribute(const RWThreadAttribute& second)
         RWTHRTHROWS1(RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The following function may be used to get a handle to a copy of the 
      // thread attribute instance used to initialize the most recently created 
      // thread.  If a thread has not yet been created, this function will
      // simply return a copy of a default thread attribute instance.
      //
      // This function will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWThreadAttribute
      getActiveAttribute(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The following functions may be used to determine the legal range
      // of values for various active thread attributes.
      //
      // These functions return a maximum or minimum value for a particular
      // active thread attribute.
      //
      // These functions will throw an RWTHROperationNotSupported exception
      // if the corresponding attribute is not supported. This exception can be 
      // avoided by testing to see if the corresponding feature test macro is 
      // currently defined (see each function below for the macro).
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These functions will throw a RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // All of the exceptions listed above can be avoided by using an
      // appropriate "can get" function to test for availability prior to 
      // calling the "get min/max" function.  You must understand that even if 
      // you get a return value of TRUE from these test functions, the 
      // "get min/max" function may still produce several of these exceptions 
      // if other threads are simultaneously manipulating the thread 
      // attributes, or if the target thread exits before the "get min/max" 
      // function can be called.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Get the maximum priority value that may be specified for this thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getMaxPriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum process-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getMaxProcessScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum system-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getMaxSystemScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum time-slice quantum value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canGetTimeSliceQuantum()
      unsigned long
      getMaxTimeSliceQuantum(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      // Get the minimum priority value that may be specified for this thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getMinPriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum process-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getMinProcessScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum system-scope priority value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getMinSystemScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the minimum time-slice quantum value that may be 
      // specified for this thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canGetTimeSliceQuantum()
      unsigned long
      getMinTimeSliceQuantum(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // This static function returns TRUE if the getMaxThreads() function is
      // supported in the current environment, and returns FALSE if not.
      /////////////////////////////////////////////////////////////////////////
      static
      RWBoolean
      canGetMaxThreads(void)
         RWTHRTHROWSNONE;

      /////////////////////////////////////////////////////////////////////////
      // This static function returns the maximum number of threads that may be 
      // created in this environment.  This function is not supported in all
      // environments.  Use the feature test macro RW_THR_HAS_MAX_THREADS or the
      // feature test function canGetMaxThreads() to test for availability.
      //
      // This function throws an RWTHROperationNotSupported exception if not
      // supported in the current environment.
      /////////////////////////////////////////////////////////////////////////
      static
      size_t
      getMaxThreads(void)
         RWTHRTHROWS1(RWTHROperationNotSupported);


      /////////////////////////////////////////////////////////////////////////
      // The "can get" test functions below can be used to determine at 
      // run-time whether the corresponding attribute value may be read.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the corrsponding
      // "get" function CANNOT return a legal value under current circumstances.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the corresponding "get" 
      // function CAN return a legal value under current circumstances.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      canGetPriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetProcessScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetSchedulingPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      
      /////////////////////////////////////////////////////////////////////////
      // The "get" functions below can be used to query the corresponding
      // attribute.
      // 
      // These functions will throw an RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the value is not 
      // supported or cannot be queried under under the current circumstances.
      // 
      // These functions will throw an RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // Some of these functions will throw an RWTHRResourceLimit exception if 
      // the operation could not be performed due to memory or system resource
      // constraints.  The exception message will give the specific reason.
      //
      // All of the exceptions above can be avoided by using the appropriate 
      // "can get" function to test for availability prior to calling the "get"
      // function.  You must understand that even if you get a return value of 
      // TRUE from these test functions, a "get" function may still produce 
      // several of these exceptions of if other threads are simultaneously 
      // manipulating the same attributes, or if the target thread exits before 
      // the "get" function can be called.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Get the current priority for the active thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canGetPriority()
      RWPriority
      getPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current process-scope priority for the active thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canGetProcessScopePriority()
      RWPriority
      getProcessScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current scheduling policy for the active thread.
      // Feature Tests: RW_THR_HAS_SCHEDULING_POLICY, 
      //                canGetSchedulingPolicy()
      RWSchedulingPolicy
      getSchedulingPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current system-scope priority for the active thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      RWPriority
      getSystemScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current system-scope priority for the active thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canGetSystemScopePriority()
      unsigned long
      getTimeSliceQuantum(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The public "can set" test functions below can be used to determine, at 
      // run-time, whether the corresponding attribute value may be set.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the specified 
      // policy, if any, is not supported under the current circumstances.
      // either because the specified value is not supported under the current 
      // scheduling options or because the calling thread or process does not 
      // possess sufficient privileges to perform the requested operation.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the specified policy is legal
      // under the current circumstances.
      //
      // Some of these functions will produce a RWTHRBoundsError exception if
      // the specified value is not a legal policy value for that attribute.  
      // This exception can be avoided by making sure that you are using the 
      // appropriate value names for enumerated attributes.
      //
      // Some of these functions will throw an RWTHRInternalError exception 
      // if some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      canSetPriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetProcessScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetSchedulingPolicy(RWSchedulingPolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The "set" functions below can be used to change the corresponding
      // attribute.
      // 
      // These functions will throw an RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the specified value is
      // not supported under the current circumstances, either because the
      // specified value is not supported under the current scheduling options
      // or because the calling thread or process does not possess sufficient
      // privileges to perform the requested operation.
      // 
      // These functions will throw an RWTHRBoundsError exception if the
      // specified value is invalid or falls outside the current legal range
      // for that attribute.
      //
      // These functions will throw an RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // Some of these functions will throw an RWTHRResourceLimit exception if 
      // the operation could not be performed due to memory or system resource
      // constraints.  The exception message will give the specific reason.
      //
      // All of the exceptions above can be avoided by using the appropriate 
      // "can set" function to test for availability prior to calling the "set"
      // function.  You must understand that even if you get a return value of 
      // TRUE from these test functions, a "set" function may still produce 
      // several of these exceptions of if other threads are simultaneously 
      // manipulating the same attributes, or if the target thread exits before 
      // the "set" function can be called.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Change the priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canSetPriority()
      void
      setPriority(RWPriority priority)
         RWTHRTHROWS6(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the process scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canSetProcessScopePriority()
      void
      setProcessScopePriority(RWPriority priority)
         RWTHRTHROWS6(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the scheduling policy currently appied to this runnable's 
      // thread.
      // Feature Tests: RW_THR_HAS_SCHEDULING_POLICY, 
      //                canSetSchedulingPolicy()
      void
      setSchedulingPolicy(RWSchedulingPolicy policy)
         RWTHRTHROWS6(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the system scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canSetSystemScopePriority()
      void
      setSystemScopePriority(RWPriority priority)
         RWTHRTHROWS6(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the system scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canSetTimeSliceQuantum()
      void
      setTimeSliceQuantum(unsigned long milliseconds)
         RWTHRTHROWS5(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // This function returns TRUE if the current environment supports the
      // suspend() and resume() functions, FALSE if otherwise
      /////////////////////////////////////////////////////////////////////////
      static
      RWBoolean
      canSuspendResume(void)
         RWTHRTHROWSNONE;


      /////////////////////////////////////////////////////////////////////////
      // Suspend the execution of this runnable's thread until a matching resume 
      // operation is performed.  May only be invoked from a different thread.
      // Feature Tests: RW_THR_HAS_SUSPEND_RESUME, 
      //                canSuspendResume()
      // CAUTION: Use of this function may produce unexpected deadlock! See the 
      //          User's Guide for explanation.  Use requestInterrupt() and 
      //          serviceInterrupt() for dead-lock safe suspension!
      /////////////////////////////////////////////////////////////////////////
      unsigned
      suspendOther(void)
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError,
                      RWTHRIllegalAccess);


      /////////////////////////////////////////////////////////////////////////
      // Suspend the execution of this runnable's thread until a matching resume 
      // operation is performed.  May only be invoked from this runnable's thread
      // using an RWThreadSelf handle.
      // Feature Tests: RW_THR_HAS_SUSPEND_RESUME, 
      //                canSuspendResume()
      // CAUTION: Use of this function may produce unexpected deadlock! See the 
      //          User's Guide for explanation.  Use requestInterrupt() and 
      //          serviceInterrupt() for dead-lock safe suspension!
      /////////////////////////////////////////////////////////////////////////
      unsigned
      suspendSelf(void)
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError,
                      RWTHRIllegalAccess);



      /////////////////////////////////////////////////////////////////////////
      // External thread functions - May only be access through this handle!
      /////////////////////////////////////////////////////////////////////////

      // Resume this thread after it has been suspended.
      // Feature Tests: RW_THR_HAS_SUSPEND_RESUME, 
      //                canSuspendResume()
      unsigned
      resume(void)
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHRIllegalAccess,
                      RWTHRThreadActive,
                      RWTHRInternalError);

      // Get the number of times this thread has been suspended without being 
      // released.  A return value of zero indicates that the thread is not 
      // currently suspended or is inactive. 
      // Feature Tests: RW_THR_HAS_SUSPEND_RESUME, canSuspendResume()
      unsigned
      getSuspendCount(void) const
         RWTHRTHROWS2(RWTHROperationNotSupported,
                      RWTHRInternalError);


      // Terminate the execution of this runnable's thread.
      // CAUTION: This operation kills the thread object's thread without giving
      //          it a chance to release locks, unwind the stack, or recover
      //          resources!
      //                      *** Use only as a last resort! ***
      //          Note that thread object storage cannot be recovered once
      //          terminate is used; each thread object creates a automatic
      //          reference to itself while executing within its started
      //          thread, and since termination does not unwind the stack,
      //          this reference will never be destroyed, leaving the thread
      //          object with a reference count that can never return to zero.
      void
      terminate(void)
         RWTHRTHROWS4(RWTHRIllegalAccess,
                      RWTHRThreadNotActive,
                      RWTHRThreadActive,
                      RWTHRInternalError);

   protected:

      // Calls _createThread() to create a thread and then sets
      // the initial priority and scheduling policy for the newly
      // created thread.
      virtual
      void
      _dispatchExec(void)
         RWTHRTHROWSANY;
         // Actual exceptions thrown are as follows:
         //    RWTHRThreadActive - An illegal attempt was made to start the thread while it was already active.
         //    RWTHRResourceLimit - There was insufficient memory or system resources to create thread.
         //    RWTHRInternalError - An unexpected exception or error condition occurred.

      // Perform setup prior execution of the virtual run() member.
      // Thread mutex must be locked prior to calling this member
      // This function expects the RWThreadImp monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      virtual
      void
      _runProlog(void)
         RWTHRTHROWSANY;

      // Perform cleanup following execution of the virtual run() member.
      // Runnable mutex must be locked prior to calling this member
      // This function expects the RWThreadImp monitor mutex to be LOCKED
      // upon entry, and will unlock and re-lock the mutex internally and
      // is therefore, NOT ATOMIC.
      virtual
      void
      _runEpilog(void)
         RWTHRTHROWSANY;

   private:

      // Initialize and create a thread that when started executes
      // the static entry() member which calls RWThreadImp::exec().
      void
      _createThread(void)
         RWTHRTHROWSANY;



      // Overrides the RWRunnableImp::exec() member to wrap RWThreadImp-
      // specific initialization and clean-up operations around a call
      // to the RWRunnableImp::exec() member.

#    if defined(RW_THR_THREAD_API_OS2)
      // Must be public for OS2 global extern "C" function entry to call 
      public:
#    endif

      void
      exec(void)
         RWTHRTHROWSNONE;

#    if defined(RW_THR_THREAD_API_OS2)
      private:
#    endif

      // Common initialization for thread object construction
      void
      init(void)
         RWTHRTHROWSANY;

      // Actual thread entry point (first function called when thread is started)
      // This function simply calls the exec() member of the thread object that created the thread.
#     if defined(RW_THR_THREAD_API_OS2)

      // OS2 Requires extern "C" linkage for the entry point function!
      // See entry() definition in thrsrc/thread.cpp...

#     elif defined(RW_THR_THREAD_API_POSIX)

      static
      void*
      entry(void* thisObject);

#     elif defined(RW_THR_THREAD_API_SOLARIS)

      static
      void*
      entry(void* thisObject);

#     elif defined(RW_THR_THREAD_API_WIN32)

#        if defined(RW_THR_THREAD_API_WIN32_MICROSOFT_CPP)

      static
      unsigned
      __stdcall
      entry(void* thisObject);

#        elif defined(RW_THR_THREAD_API_WIN32_BORLAND_CPP)

      static
      void
      _USERENTRY
      entry(void* thisObject);

#        else
#           error Class RWThreadImp is not supported on this compiler!
#        endif
#     else
#        error Class RWThreadImp is not supported in this environment!
#     endif

#  if defined(RW_THR_THREAD_API_POSIX) || \
      defined(RW_THR_THREAD_API_SOLARIS)

      // "Private" signal-handler required to support thread termination
      static
      void
      onTerminate(void)
         RWTHRTHROWSNONE;

#  endif

      /////////////////////////////////////////////////////////////////////////
      // The following private functions are used internally to determine the 
      // legal range of values for various active thread attributes.
      //
      // These functions return a maximum or minimum value for a particular
      // active thread attribute.
      //
      // These functions will throw an RWTHROperationNotSupported exception
      // if the corresponding attribute is not supported. This exception can be 
      // avoided by testing to see if the corresponding feature test macro is 
      // currently defined (see each function below for the macro).
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These functions will throw a RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // All of the exceptions listed above can be avoided by using an
      // appropriate "can get" function to test for availability prior to 
      // calling the "get min/max" function.  You must understand that even if 
      // you get a return value of TRUE from these test functions, the 
      // "get min/max" function may still produce several of these exceptions 
      // if other threads are simultaneously manipulating the thread 
      // attributes, or if the target thread exits before the "get min/max" 
      // function can be called.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Get the maximum priority value that may be specified for this thread.
      RWPriority
      _getMaxPriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum process-scope priority value that may be 
      // specified for this thread.
      RWPriority
      _getMaxProcessScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum system-scope priority value that may be 
      // specified for this thread.
      RWPriority
      _getMaxSystemScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum time-slice quantum value that may be 
      // specified for this thread.
      unsigned long
      _getMaxTimeSliceQuantum(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      // Get the minimum priority value that may be specified for this thread.
      RWPriority
      _getMinPriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum process-scope priority value that may be 
      // specified for this thread.
      RWPriority
      _getMinProcessScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the maximum system-scope priority value that may be 
      // specified for this thread.
      RWPriority
      _getMinSystemScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the minimum time-slice quantum value that may be 
      // specified for this thread.
      unsigned long
      _getMinTimeSliceQuantum(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The "can get" test functions below can be used to determine at 
      // run-time whether the corresponding attribute value may be read.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the corrsponding
      // "get" function CANNOT return a legal value under current circumstances.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the corresponding "get" 
      // function CAN return a legal value under current circumstances.
      //
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      _canGetPriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetProcessScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetSchedulingPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      
      /////////////////////////////////////////////////////////////////////////
      // The "get" functions below can be used to query the corresponding
      // attribute.
      // 
      // These functions will throw an RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the value is not 
      // supported or cannot be queried under under the current circumstances.
      // 
      // These functions will throw an RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // Some of these functions will throw an RWTHRResourceLimit exception if 
      // the operation could not be performed due to memory or system resource
      // constraints.  The exception message will give the specific reason.
      //
      // All of the exceptions above can be avoided by using the appropriate 
      // "can get" function to test for availability prior to calling the "get"
      // function.  You must understand that even if you get a return value of 
      // TRUE from these test functions, a "get" function may still produce 
      // several of these exceptions of if other threads are simultaneously 
      // manipulating the same attributes, or if the target thread exits before 
      // the "get" function can be called.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Get the current priority for the active thread.
      RWPriority
      _getPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current process-scope priority for the active thread.
      RWPriority
      _getProcessScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current scheduling policy for the active thread.
      RWSchedulingPolicy
      _getSchedulingPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current system-scope priority for the active thread.
      RWPriority
      _getSystemScopePriority(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      // Get the current system-scope priority for the active thread.
      unsigned long
      _getTimeSliceQuantum(void) const
         RWTHRTHROWS4(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The public "can set" test functions below can be used to determine, at 
      // run-time, whether the corresponding attribute value may be set.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the specified 
      // policy, if any, is not supported under the current circumstances.
      // either because the specified value is not supported under the current 
      // scheduling options or because the calling thread or process does not 
      // possess sufficient privileges to perform the requested operation.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the specified policy is legal
      // under the current circumstances.
      //
      // Some of these functions will produce a RWTHRBoundsError exception if
      // the specified value is not a legal policy value for that attribute.  
      // This exception can be avoided by making sure that you are using the 
      // appropriate value names for enumerated attributes.
      //
      // Some of these functions will throw the RWTHRInternalError exception 
      // if some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      _canSetPriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canSetProcessScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canSetSchedulingPolicy(RWSchedulingPolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      _canSetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canSetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The private "validate" functions below are used internally to validate 
      // a candidate value for the corresponding attribute
      //
      // These functions return TRUE if the attribute is supported, and the 
      // value is a legal value for the attribute.
      //
      // These functions return FALSE if the attribute is supported, but the 
      // value is not a legal value for the attribute.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception 
      // if the corresponding attribute is supported, but the specified value 
      // is not supported under the current circumstances.
      // 
      // These functions will throw an RWTHROperationNotSupported exception 
      // if the attribute is not supported in the current environment.
      //
      // Some of these functions will throw an RWTHRInternalError exception 
      // if some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      _validatePriority(RWPriority priority) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWBoolean
      _validateProcessScopePriority(RWPriority priority) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWBoolean
      _validateSchedulingPolicy(RWSchedulingPolicy policy) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWBoolean
      _validateSystemScopePriority(RWPriority priority) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWBoolean
      _validateTimeSliceQuantum(unsigned long milliseconds) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The private "set" functions below are used internally to change the 
      // corresponding thread attribute.
      // 
      // These functions will throw an RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw an RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the specified value is
      // not supported under the current circumstances, either because the
      // specified value is not supported under the current scheduling options
      // or because the calling thread or process does not possess sufficient
      // privileges to perform the requested operation.
      // 
      // These functions will throw an RWTHRBoundsError exception if the
      // specified value is invalid or falls outside the current legal range
      // for that attribute.
      //
      // These functions will throw an RWTHRThreadNotActive exception if the
      // threaded runnable does not have an active thread (either because
      // the thread was not started, or the thread has exited for some reason).
      //
      // Some of these functions will throw an RWTHRResourceLimit exception if 
      // the operation could not be performed due to memory or system resource
      // constraints.  The exception message will give the specific reason.
      //
      // All of the exceptions above can be avoided by using the appropriate 
      // "can set" function to test for availability prior to calling the "set"
      // function.  You must understand that even if you get a return value of 
      // TRUE from these test functions, a "set" function may still produce 
      // several of these exceptions of if other threads are simultaneously 
      // manipulating the same attributes, or if the target thread exits before 
      // the "set" function can be called.
      //
      // These functions will throw an RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      // Change the priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_PRIORITY, 
      //                canSetPriority()
      void
      _setPriority(RWPriority priority)
         RWTHRTHROWS6(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the process scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_PROCESS_SCOPE_PRIORITY, 
      //                canSetProcessScopePriority()
      void
      _setProcessScopePriority(RWPriority priority)
         RWTHRTHROWS6(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the scheduling policy currently appied to this runnable's 
      // thread.
      // Feature Tests: RW_THR_HAS_SCHEDULING_POLICY, 
      //                canSetSchedulingPolicy()
      void
      _setSchedulingPolicy(RWSchedulingPolicy policy)
         RWTHRTHROWS6(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the system scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_SYSTEM_SCOPE_PRIORITY, 
      //                canSetSystemScopePriority()
      void
      _setSystemScopePriority(RWPriority priority)
         RWTHRTHROWS6(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Change the system scope priority of this runnable's thread.
      // Feature Tests: RW_THR_HAS_TIME_SLICE_QUANTUM, 
      //                canSetTimeSliceQuantum()
      void
      _setTimeSliceQuantum(unsigned long milliseconds)
         RWTHRTHROWS5(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRThreadNotActive,
                      RWTHRBoundsError,
                      RWTHRInternalError);

};

/////////////////////////////////////////////////////////////////////////
//
// RWThread inline function definitions
//
/////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////
// RWThread - Handle instance functions
/////////////////////////////////////////////////////////////////////////

inline
RWThread::RWThread(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWThread,RWThread(void));
}

inline
RWThread::RWThread(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWRunnable(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWThread,RWThread(RWStaticCtor));
}

// protected
inline
RWThread::RWThread(RWThreadImp* threadImpP)
   RWTHRTHROWSANY
   :
      RWRunnable((RWRunnableImp*)threadImpP)
{
   RWTHRTRACEMF(RWThread,RWThread(RWThreadImp*));
}

inline
RWThread::RWThread(const RWThread& second)
   RWTHRTHROWSANY
   :
      RWRunnable(second)
{
   RWTHRTRACEMF(RWThread,RWThread(const RWThread&));
}

inline
RWThread::RWThread(const RWThreadSelf& second)
   RWTHRTHROWSANY
   :
      RWRunnable(second)
{
   RWTHRTRACEMF(RWThread,RWThread(const RWThreadSelf&));
}

inline
RWThread&
RWThread::operator=(const RWThread& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThread,operator=(const RWThread&):RWThread&);
   RWRunnable::operator=(second);
   return *this;
}

// protected
inline
RWThreadImp&
RWThread::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWThread,body(void) const:RWThreadImp&);
   return (RWThreadImp&)RWRunnable::body();
}

/////////////////////////////////////////////////////////////////////////
// RWThread - Common thread functions 
/////////////////////////////////////////////////////////////////////////

inline
void
RWThread::setAttribute(const RWThreadAttribute& attr)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,setAttribute(const RWThreadAttribute&):void);
   body().setAttribute(attr);
}

/////////////////////////////////////////////////////////////////////////

inline
RWPriority
RWThread::getMaxPriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getMaxPriority(void) const:RWPriority);
   return body().getMaxPriority();
}

inline
RWPriority
RWThread::getMaxProcessScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getMaxProcessScopePriority(void) const:RWPriority);
   return body().getMaxProcessScopePriority();
}

inline
RWPriority
RWThread::getMaxSystemScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getMaxSystemScopePriority(void) const:RWPriority);
   return body().getMaxSystemScopePriority();
}

inline
unsigned long
RWThread::getMaxTimeSliceQuantum(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getMaxTimeSliceQuantum(void) const:unsigned long);
   return body().getMaxTimeSliceQuantum();
}

inline
RWPriority
RWThread::getMinPriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getMinPriority(void) const:RWPriority);
   return body().getMinPriority();
}

inline
RWPriority
RWThread::getMinProcessScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getMinProcessmScopePriority(void) const:RWPriority);
   return body().getMinProcessScopePriority();
}

inline
RWPriority
RWThread::getMinSystemScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getMinSystemScopePriority(void) const:RWPriority);
   return body().getMinSystemScopePriority();
}

inline
unsigned long
RWThread::getMinTimeSliceQuantum(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getMinTimeSliceQuantum(void) const:unsigned long);
   return body().getMinTimeSliceQuantum();
}


/////////////////////////////////////////////////////////////////////////

inline
// static
RWBoolean
RWThread::canGetMaxThreads(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACESMF(RWThread,canGetMaxThreads(void):RWBoolean);
   return RWThreadImp::canGetMaxThreads();
}

inline
// static
size_t
RWThread::getMaxThreads(void)
   RWTHRTHROWS1(RWTHROperationNotSupported)
{
   RWTHRTRACESMF(RWThread,getMaxThreads(void):size_t);
   return RWThreadImp::getMaxThreads();
}

/////////////////////////////////////////////////////////////////////////

inline
RWBoolean
RWThread::canGetPriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canGetPriority(void) const:RWBoolean);
   return body().canGetPriority();
}

inline
RWBoolean
RWThread::canGetProcessScopePriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canGetProcessScopePriority(void) const:RWBoolean);
   return body().canGetProcessScopePriority();
}

inline
RWBoolean
RWThread::canGetSchedulingPolicy(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canGetSchedulingPolicy(void) const:RWBoolean);
   return body().canGetSchedulingPolicy();
}

inline
RWBoolean
RWThread::canGetSystemScopePriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canGetSystemScopePriority(void) const:RWBoolean);
   return body().canGetSystemScopePriority();
}

inline
RWBoolean
RWThread::canGetTimeSliceQuantum(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canGetTimeSliceQuantum(void) const:RWBoolean);
   return body().canGetTimeSliceQuantum();
}

/////////////////////////////////////////////////////////////////////////

inline
RWPriority
RWThread::getPriority(void) const
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getPriority(void) const:RWPriority);
   return body().getPriority();
}

inline
RWPriority
RWThread::getProcessScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getProcessScopePriority(void) const:RWPriority);
   return body().getProcessScopePriority();
}

inline
RWSchedulingPolicy
RWThread::getSchedulingPolicy(void) const
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getSchedulingPolicy(void) const:RWSchedulingPolicy);
   return body().getSchedulingPolicy();
}

inline
RWPriority
RWThread::getSystemScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getSystemScopePriority(void) const:RWPriority);
   return body().getSystemScopePriority();
}

inline
unsigned long
RWThread::getTimeSliceQuantum(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getTimeSliceQuantum(void) const:RWPriority);
   return body().getTimeSliceQuantum();
}

/////////////////////////////////////////////////////////////////////////

inline
RWBoolean
RWThread::canSetPriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canSetPriority(void) const:RWBoolean);
   return body().canSetPriority();
}

inline
RWBoolean
RWThread::canSetProcessScopePriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canSetProcessScopePriority(void) const:RWBoolean);
   return body().canSetProcessScopePriority();
}

inline
RWBoolean
RWThread::canSetSchedulingPolicy(RWSchedulingPolicy policy) const
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canSetSchedulingPolicy(void) const:RWBoolean);
   return body().canSetSchedulingPolicy(policy);
}

inline
RWBoolean
RWThread::canSetSystemScopePriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canSetSystemScopePriority(void) const:RWBoolean);
   return body().canSetSystemScopePriority();
}

inline
RWBoolean
RWThread::canSetTimeSliceQuantum(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,canSetTimeSliceQuantum(void) const:RWBoolean);
   return body().canSetTimeSliceQuantum();
}


/////////////////////////////////////////////////////////////////////////

inline
void
RWThread::setPriority(RWPriority priority)
   RWTHRTHROWS7(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,setPriority(RWPriority):void);
   body().setPriority(priority);
}

inline
void
RWThread::setProcessScopePriority(RWPriority priority)
   RWTHRTHROWS7(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,setProcessScopePriority(RWPriority):void);
   body().setProcessScopePriority(priority);
}

inline
void
RWThread::setSchedulingPolicy(RWSchedulingPolicy policy)
   RWTHRTHROWS7(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,setSchedulingPolicy(RWSchedulingPolicy):void);
   body().setSchedulingPolicy(policy);
}

inline
void
RWThread::setSystemScopePriority(RWPriority priority)
   RWTHRTHROWS7(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,setSystemScopePriority(RWPriority):void);
   body().setSystemScopePriority(priority);
}

inline
void
RWThread::setTimeSliceQuantum(unsigned long milliseconds)
   RWTHRTHROWS6(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,setTimeSliceQuantum(unsigned long):void);
   body().setTimeSliceQuantum(milliseconds);
}

/////////////////////////////////////////////////////////////////////////

inline
// static
RWBoolean
RWThread::canSuspendResume(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACESMF(RWThread,canSuspendResume(void):RWBoolean);
   return RWThreadImp::canSuspendResume();
}


inline
unsigned
RWThread::suspend(void)
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,suspend(void):unsigned);
   return body().suspendOther();
}

/////////////////////////////////////////////////////////////////////////
// RWThread - External thread functions 
/////////////////////////////////////////////////////////////////////////

inline
unsigned
RWThread::getSuspendCount(void) const
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,getSuspendCount(void) const:unsigned);
   return body().getSuspendCount();
}

inline
unsigned
RWThread::resume(void)
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHRIllegalAccess,
                RWTHRThreadActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,resume(void):unsigned);
   return body().resume();
}

/////////////////////////////////////////////////////////////////////////

inline
void
RWThread::terminate(void)
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHRIllegalAccess,
                RWTHRThreadNotActive,
                RWTHRThreadActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThread,terminate(void):void);
   body().terminate();
}


/////////////////////////////////////////////////////////////////////////
//
// RWThreadSelf inline function definitions
//
/////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////
// RWThreadSelf - Handle instance functions
/////////////////////////////////////////////////////////////////////////

inline
RWThreadSelf::RWThreadSelf(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWThreadSelf,RWThreadSelf(void));
}

inline
RWThreadSelf::RWThreadSelf(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWRunnableSelf(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWThreadSelf,RWThreadSelf(RWStaticCtor));
}

inline
RWThreadSelf::RWThreadSelf(const RWThreadSelf& second)
   RWTHRTHROWSANY
   :
      RWRunnableSelf(second)
{
   RWTHRTRACEMF(RWThreadSelf,RWThreadSelf(const RWThreadSelf&));
}

inline
RWThreadSelf::RWThreadSelf(const RWThread& second)
   RWTHRTHROWSANY
   :
      RWRunnableSelf(second)
{
   RWTHRTRACEMF(RWThreadSelf,RWThreadSelf(const RWThreadSelf&));
}

inline
RWThreadSelf&
RWThreadSelf::operator=(const RWThreadSelf& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadSelf,operator=(const RWThreadSelf&):RWThreadSelf&);
   RWRunnableSelf::operator=(second);
   return *this;
}

// protected
inline
RWThreadImp&
RWThreadSelf::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWThreadSelf,body(void):RWThreadImp&);
   return (RWThreadImp&)RWRunnableSelf::body();
}


/////////////////////////////////////////////////////////////////////////
// RWThreadSelf - Common thread functions 
/////////////////////////////////////////////////////////////////////////

inline
void
RWThreadSelf::setAttribute(const RWThreadAttribute& attr)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,setAttribute(const RWThreadAttribute&):void);
   body().setAttribute(attr);
}

/////////////////////////////////////////////////////////////////////////

inline
RWPriority
RWThreadSelf::getMaxPriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getMaxPriority(void) const:RWPriority);
   return body().getMaxPriority();
}

inline
RWPriority
RWThreadSelf::getMaxProcessScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getMaxProcessScopePriority(void) const:RWPriority);
   return body().getMaxProcessScopePriority();
}

inline
RWPriority
RWThreadSelf::getMaxSystemScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getMaxSystemScopePriority(void) const:RWPriority);
   return body().getMaxSystemScopePriority();
}

inline
unsigned long
RWThreadSelf::getMaxTimeSliceQuantum(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getMaxTimeSliceQuantum(void) const:unsigned long);
   return body().getMaxTimeSliceQuantum();
}

inline
RWPriority
RWThreadSelf::getMinPriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getMinPriority(void) const:RWPriority);
   return body().getMinPriority();
}

inline
RWPriority
RWThreadSelf::getMinProcessScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getMinProcessmScopePriority(void) const:RWPriority);
   return body().getMinProcessScopePriority();
}

inline
RWPriority
RWThreadSelf::getMinSystemScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getMinSystemScopePriority(void) const:RWPriority);
   return body().getMinSystemScopePriority();
}

inline
unsigned long
RWThreadSelf::getMinTimeSliceQuantum(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getMinTimeSliceQuantum(void) const:unsigned long);
   return body().getMinTimeSliceQuantum();
}


/////////////////////////////////////////////////////////////////////////

inline
// static
RWBoolean
RWThreadSelf::canGetMaxThreads(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACESMF(RWThreadSelf,canGetMaxThreads(void):RWBoolean);
   return RWThreadImp::canGetMaxThreads();
}

inline
// static
size_t
RWThreadSelf::getMaxThreads(void)
   RWTHRTHROWS1(RWTHROperationNotSupported)
{
   RWTHRTRACESMF(RWThreadSelf,getMaxThreads(void):size_t);
   return RWThreadImp::getMaxThreads();
}

/////////////////////////////////////////////////////////////////////////

inline
RWBoolean
RWThreadSelf::canGetPriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canGetPriority(void) const:RWBoolean);
   return body().canGetPriority();
}

inline
RWBoolean
RWThreadSelf::canGetProcessScopePriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canGetProcessScopePriority(void) const:RWBoolean);
   return body().canGetProcessScopePriority();
}

inline
RWBoolean
RWThreadSelf::canGetSchedulingPolicy(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canGetSchedulingPolicy(void) const:RWBoolean);
   return body().canGetSchedulingPolicy();
}

inline
RWBoolean
RWThreadSelf::canGetSystemScopePriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canGetSystemScopePriority(void) const:RWBoolean);
   return body().canGetSystemScopePriority();
}

inline
RWBoolean
RWThreadSelf::canGetTimeSliceQuantum(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canGetTimeSliceQuantum(void) const:RWBoolean);
   return body().canGetTimeSliceQuantum();
}

/////////////////////////////////////////////////////////////////////////

inline
RWPriority
RWThreadSelf::getPriority(void) const
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getPriority(void) const:RWPriority);
   return body().getPriority();
}

inline
RWPriority
RWThreadSelf::getProcessScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getProcessScopePriority(void) const:RWPriority);
   return body().getProcessScopePriority();
}

inline
RWSchedulingPolicy
RWThreadSelf::getSchedulingPolicy(void) const
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getSchedulingPolicy(void) const:RWSchedulingPolicy);
   return body().getSchedulingPolicy();
}

inline
RWPriority
RWThreadSelf::getSystemScopePriority(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getSystemScopePriority(void) const:RWPriority);
   return body().getSystemScopePriority();
}

inline
unsigned long
RWThreadSelf::getTimeSliceQuantum(void) const
   RWTHRTHROWS5(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,getTimeSliceQuantum(void) const:RWPriority);
   return body().getTimeSliceQuantum();
}

/////////////////////////////////////////////////////////////////////////

inline
RWBoolean
RWThreadSelf::canSetPriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canSetPriority(void) const:RWBoolean);
   return body().canSetPriority();
}

inline
RWBoolean
RWThreadSelf::canSetProcessScopePriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canSetProcessScopePriority(void) const:RWBoolean);
   return body().canSetProcessScopePriority();
}

inline
RWBoolean
RWThreadSelf::canSetSchedulingPolicy(RWSchedulingPolicy policy) const
   RWTHRTHROWS3(RWTHRInvalidPointer,
                RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canSetSchedulingPolicy(void) const:RWBoolean);
   return body().canSetSchedulingPolicy(policy);
}

inline
RWBoolean
RWThreadSelf::canSetSystemScopePriority(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canSetSystemScopePriority(void) const:RWBoolean);
   return body().canSetSystemScopePriority();
}

inline
RWBoolean
RWThreadSelf::canSetTimeSliceQuantum(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,canSetTimeSliceQuantum(void) const:RWBoolean);
   return body().canSetTimeSliceQuantum();
}


/////////////////////////////////////////////////////////////////////////

inline
void
RWThreadSelf::setPriority(RWPriority priority)
   RWTHRTHROWS7(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,setPriority(RWPriority):void);
   body().setPriority(priority);
}

inline
void
RWThreadSelf::setProcessScopePriority(RWPriority priority)
   RWTHRTHROWS7(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,setProcessScopePriority(RWPriority):void);
   body().setProcessScopePriority(priority);
}

inline
void
RWThreadSelf::setSchedulingPolicy(RWSchedulingPolicy policy)
   RWTHRTHROWS7(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,setSchedulingPolicy(RWSchedulingPolicy):void);
   body().setSchedulingPolicy(policy);
}

inline
void
RWThreadSelf::setSystemScopePriority(RWPriority priority)
   RWTHRTHROWS7(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,setSystemScopePriority(RWPriority):void);
   body().setSystemScopePriority(priority);
}

inline
void
RWThreadSelf::setTimeSliceQuantum(unsigned long milliseconds)
   RWTHRTHROWS6(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRThreadNotActive,
                RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,setTimeSliceQuantum(unsigned long):void);
   body().setTimeSliceQuantum(milliseconds);
}

/////////////////////////////////////////////////////////////////////////

inline
// static
RWBoolean
RWThreadSelf::canSuspendResume(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACESMF(RWThreadSelf,canSuspendResume(void):RWBoolean);
   return RWThreadImp::canSuspendResume();
}


inline
unsigned
RWThreadSelf::suspend(void)
   RWTHRTHROWS4(RWTHRInvalidPointer,
                RWTHROperationNotSupported,
                RWTHRThreadNotActive,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadSelf,suspend(void):unsigned);
   return body().suspendSelf();
}

/////////////////////////////////////////////////////////////////////////
// RWThreadSelf - Internal thread functions 
/////////////////////////////////////////////////////////////////////////

#  if defined(RW_THR_THREAD_API_WIN32)
inline
void
RWThreadSelf::registerThreadLocalDestructor(void (*destroyFunc)(void *),void* valptr)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadSelf,registerThreadLocalDestructor(void (*)(void *),void*):void);
   body().registerThreadLocalDestructor(destroyFunc,valptr);
}
#endif

#  if defined(RW_THR_THREAD_API_OS2)
inline
void
RWThreadSelf::registerThreadLocalDestructor(void (*destroyFunc)(void *, RWThreadId),void* valptr, RWThreadId tid)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadSelf,registerThreadLocalDestructor(void (*)(void *,RWThreadId),void*,RWThreadId):void);
   body().registerThreadLocalDestructor(destroyFunc,valptr,tid);
}
#endif




/////////////////////////////////////////////////////////////////////////
//
// RWThreadImp inline function definitions
//
/////////////////////////////////////////////////////////////////////////

// Implement a do-nothing copy-constructor to keep the 
// compiler from trying to copy members.
inline
RWThreadImp::RWThreadImp(const RWThreadImp& second)
   RWTHRTHROWSANY
   :
      RWRunnableImp(second)
{
   RWTHRTRACEMF(RWThreadImp,RWThreadImp(const RWThreadImp&):void);
}

// Implement a do-nothing assignment operator to keep the 
// compiler from trying to assign members.
inline
RWThreadImp&
RWThreadImp::operator=(const RWThreadImp& second)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWThreadImp,operator=(const RWThreadImp&):RWThreadImp&);
   RWRunnableImp::operator=(second);
   return *this;
}

/////////////////////////////////////////////////////////////////////////

#if !defined(RW_THR_CAN_RECOVER_THREAD_LOCALS)
// Define an operator== to keep some compilers happy when instantiating
// RWTValSlist template.
inline
RWBoolean
RWThreadImp::ThreadLocalDestructor::operator==(const RWThreadImp::ThreadLocalDestructor&) const
{
   return TRUE;
}
#endif

#endif // __RWTHRTHREAD_H__

