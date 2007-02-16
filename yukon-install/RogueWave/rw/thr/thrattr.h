#if !defined(__RWTHRTHRATTR_H__)
#  define __RWTHRTHRATTR_H__
/*****************************************************************************
 *
 * thrattr.h
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

thrattr.h - Declarations for:

   RWConcurrencyPolicy - enumerated thread concurrency generation policies.
   RWContentionScope - enumerated thread scheduling contention scope policies.
   RWInheritancePolicy - enumerated thread scheduling inheritance policies.
   RWPriority - typedef of platform-specific type used to represent priorities.
   RWSchedulingPolicy - enumerated thread scheduling policies.
   RWStartPolicy - enumerated thread start-up policies.

   RWThreadAttribute - Handle class of thread attributes object.
   RWThreadAttributeImp - Body class of thread attributes object.
      
See Also:

   thrattr.cpp - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

///////////////////////////////////////////////////////////////////////////////
// Define typedef for priority values
///////////////////////////////////////////////////////////////////////////////
#  if defined(RW_THR_THREAD_API_OS2)

typedef int    RWPriority;

#  elif defined(RW_THR_THREAD_API_POSIX)

typedef int    RWPriority;

#  elif defined(RW_THR_THREAD_API_SOLARIS)

#     if !defined(__RW_SYS_TYPES_H__)
#        define __RW_SYS_TYPES_H__ <sys/types.h>
#        include __RW_SYS_TYPES_H__
#     endif

typedef pri_t  RWPriority;

#  elif defined(RW_THR_THREAD_API_WIN32)

typedef int    RWPriority;

#  endif

///////////////////////////////////////////////////////////////////////////////
// Define scheduling concurrency policies:
//    RW_THR_NO_CHANGE - The creation of a new thread will not force an 
//                       increase in the effective concurrency supplied by
//                       the threads system (no new kernel threads are created).
//    RW_THR_INCREASE  - The creation of a new thread will result in the 
//                       creation of a new kernel thread to support increased
//                       concurrency of execution for the new thread.
///////////////////////////////////////////////////////////////////////////////
enum RWConcurrencyPolicy {
   RW_THR_NO_CHANGE, 
   RW_THR_INCREASE   
};

///////////////////////////////////////////////////////////////////////////////
// Define scheduling contention scope policies:
//    RW_THR_PROCESS_SCOPE - Thread contends for processing resources with 
//                           other threads in the same process.
//    RW_THR_SYSTEM_SCOPE  - Thread contends for processing resources with
//                           other processes in the system.
///////////////////////////////////////////////////////////////////////////////
enum RWContentionScope {
   RW_THR_PROCESS_SCOPE, 
   RW_THR_SYSTEM_SCOPE   
};

///////////////////////////////////////////////////////////////////////////////
// Define scheduling attribute inheritance policies
//    RW_THR_INHERIT  - The default scheduling policy, priority, and time-slice 
//                      quantum are inherited from the creating thread.
//    RW_THR_EXPLICIT - The default scheduling policy, priority, and time-slice 
//                      quantum are provide by the thread attribute instance.
///////////////////////////////////////////////////////////////////////////////
enum RWInheritancePolicy {
   RW_THR_INHERIT,   
   RW_THR_EXPLICIT   
};

///////////////////////////////////////////////////////////////////////////////
// Define scheduling policies
//    RW_THR_OTHER               - Some kind of scheduling policy other than
//                                 those listed below.
//    RW_THR_TIME_SLICED         - Time-sliced with unspecified adjustments.
//    RW_THR_TIME_SLICED_FIXED   - Time-sliced with fixed priorities.
//    RW_THR_TIME_SLICED_DYNAMIC - Time-sliced with dynamic priorities and/or
//                                 time-slice quantums.
//    RW_THR_PREEMPTIVE          - No time slicing and fixed priorities.
///////////////////////////////////////////////////////////////////////////////
enum RWSchedulingPolicy {
   RW_THR_OTHER,                
   RW_THR_TIME_SLICED,          
   RW_THR_TIME_SLICED_FIXED,    
   RW_THR_TIME_SLICED_DYNAMIC,  
   RW_THR_PREEMPTIVE            
};

///////////////////////////////////////////////////////////////////////////////
// Define scheduling attribute inheritance policies
//    RW_THR_START_RUNNING     - A newly created thread is allowed to run as
//                               soon as the start() operation is complete.
//    RW_THR_START_INTERRUPTED - A newly created thread is to be left in an 
//                               interrupted state upon return from the start()
//                               invocation that created it.  The thread must
//                               be explicitly released from the interrupt 
//                               before it will execute the run() method.
///////////////////////////////////////////////////////////////////////////////
enum RWStartPolicy {
   RW_THR_START_RUNNING,
   RW_THR_START_INTERRUPTED
};

///////////////////////////////////////////////////////////////////////////////
// Feature Test Macros
//
// The following macros, defined in defs.h, are used to indicate whether
// the corresponding attribute is supported in the current environment.
// If the macro is NOT defined, attempts to get or set the corresponding 
// attribute will produce exceptions.  If the macro IS defined, the current
// environment has some level of support for the attribute, and MAY allow you
// to get or set the attribute value.
//
//    RW_THR_HAS_CONTENTION_SCOPE
//    RW_THR_HAS_INHERITANCE_POLICY
//    RW_THR_HAS_PRIORITY
//    RW_THR_HAS_PROCESS_SCOPE_PRIORITY
//    RW_THR_HAS_SCHEDULING_POLICY
//    RW_THR_HAS_STACK_COMMIT_SIZE
//    RW_THR_HAS_STACK_RESERVE_SIZE
//    RW_THR_HAS_START_POLICY
//    RW_THR_HAS_SYSTEM_SCOPE_PRIORITY
//    RW_THR_HAS_TIME_SLICE_QUANTUM
//    RW_THR_HAS_USER_STACK
//
// The RW_THR_HAS_DUAL_PRIORITY macro is used to indicate when the underlying
// system requires two priorities for threads with system contention scope;
// one priority value for use in resolving system-level scheduling conflicts, 
// and a second priority value for use in resolving contention for 
// synchronization resources that are shared between threads in a process.
//    
///////////////////////////////////////////////////////////////////////////////


// Forward references

class RWTHRExport RWThreadAttributeImp;
class RWTHRExport RWThreadAttribute;

class RWTHRExport RWThreadAttribute :
   public RWTHRHandle {
   
   RW_THR_DECLARE_TRACEABLE

   public:
      
      // Bind a new handle instance to a new thread attribute instance
      RWThreadAttribute(void)
         RWTHRTHROWSANY;

      // Construct a global static handle instance.
      // (may be used before constructed)
      // A new thread attribute instance will automatically be created
      // the first time the handle is used.
      RWThreadAttribute(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to the thread attribute instance, if any,
      // pointed-to by another handle instance
      RWThreadAttribute(const RWThreadAttribute& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the thread attribute instance, if any,
      // pointed-to by another handle instance
      RWThreadAttribute&
      operator=(const RWThreadAttribute& second)
         RWTHRTHROWSANY;

      // Copy the thread attribute values from the
      // the instance pointed-to by another handle
      void
      copy(const RWThreadAttribute& second)
         RWTHRTHROWSANY;

      // Deadlock safe comparison...
      RWBoolean
      isEqual(const RWThreadAttribute& second) const
         RWTHRTHROWS1(RWTHRInternalError);

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
      // If the corresponding attribute value has not yet be "set", then a 
      // return value of TRUE indicates that a default value is available and 
      // may be read.
      //
      // These functions always return TRUE if the corresponding attribute
      // still has the value previously defined by a call to the matching "set"
      // function.  In this case, the behavior is similar to that provided by
      // the "is set" functions (declared later).
      //
      // Some of these functions will throw the RWTHRInternalError exception if 
      // some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      canGetConcurrencyPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetContentionScope(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetInheritancePolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

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
      canGetStackCommitSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetStackReserveSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetStartPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetUserStack(void) const
         RWTHRTHROWS1(RWTHRInternalError);

    
      /////////////////////////////////////////////////////////////////////////
      // The "get" functions below can be used to query the default value 
      // of an attribute, if available, or the last value defined in a call to
      // the matching "set" function.
      // 
      // These functions will throw the RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw the RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but has not yet been defined
      // by a call the the matching "set" function, and no default value is 
      // available under the current circumstances.
      /////////////////////////////////////////////////////////////////////////

      RWConcurrencyPolicy
      getConcurrencyPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWContentionScope 
      getContentionScope(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWInheritancePolicy
      getInheritancePolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWPriority
      getPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWPriority
      getProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWSchedulingPolicy
      getSchedulingPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      size_t
      getStackCommitSize(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);
      size_t
      getStackReserveSize(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWStartPolicy
      getStartPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWPriority
      getSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      unsigned long
      getTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      void*
      getUserStackAddress(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      size_t
      getUserStackSize(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The public "is set" test functions below can be used to determine 
      // whether the corresponding attribute still has the value specified in 
      // an earlier call the the matching "set" function.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment, has not yet been set, or
      // has been forced to some other default value in response to a change
      // in another related attribute.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment, has been set, has not 
      // been reset, and has not forced to a default value as the result of a 
      // change in a some related attribute.
      //
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      isConcurrencyPolicySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isContentionScopeSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isInheritancePolicySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isPrioritySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isProcessScopePrioritySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isSchedulingPolicySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isStackCommitSizeSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isStackReserveSizeSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isStartPolicySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isSystemScopePrioritySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isTimeSliceQuantumSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isUserStackSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The public "can set" test functions below can be used to determine, at 
      // run-time, whether the corresponding attribute value may be set.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the specified 
      // policy, if any, is not supported under the current circumstances.
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
      canSetConcurrencyPolicy(RWConcurrencyPolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetContentionScope(RWContentionScope scope) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetInheritancePolicy(RWInheritancePolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

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
      canSetStackCommitSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetStackReserveSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetStartPolicy(RWStartPolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetUserStack(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      
      /////////////////////////////////////////////////////////////////////////
      // The "set" functions below can be used to set the corresponding
      // attribute.
      // 
      // These functions will throw the RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw the RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the specified value is
      // not supported under the current circumstances.
      // 
      // These functions will throw the RWTHRBoundsError exception if the
      // specified value is invalid or falls outside the current legal range
      // for that attribute.
      //
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      void
      setConcurrencyPolicy(RWConcurrencyPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setContentionScope(RWContentionScope scope) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setInheritancePolicy(RWInheritancePolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setPriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setProcessScopePriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setSchedulingPolicy(RWSchedulingPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setStackCommitSize(size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setStackReserveSize(size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setStartPolicy(RWStartPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setSystemScopePriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setTimeSliceQuantum(unsigned long milliseconds) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setUserStack(void* address, size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The public "reset" functions below can be used to restore an attribute 
      // value to its default setting, if any.
      //
      // These functions will always succeed, even if the target attribute is
      // not supported in the current environment.
      // 
      // There are several combinations of attributes and environments where it
      // is not possible to query for a default value of an attribute; using 
      // these functions will simply clear any previous setting so that 
      // Threads.h++ will know to rely on the underlying API to determine the 
      // appropriate default value.
      //
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      void
      resetConcurrencyPolicy(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetContentionScope(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetInheritancePolicy(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetPriority(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetProcessScopePriority(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetSchedulingPolicy(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetStackCommitSize(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetStackReserveSize(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetStartPolicy(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetSystemScopePriority(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetTimeSliceQuantum(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetUserStack(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      
      /////////////////////////////////////////////////////////////////////////
      // The following functions may be used to determine the legal range
      // of values for various attributes.
      /////////////////////////////////////////////////////////////////////////
      
      /////////////////////////////////////////////////////////////////////////
      // This function returns the maximum priority value supported by the 
      // current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if the macro RW_THR_HAS_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMaxPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the maximum process-scope priority value 
      // supported by the current process-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if RW_THR_HAS_PROCESS_SCOPE_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_SYSTEM_SCOPE and the current
      // environment does not support dual priority values, as indicated by
      // the definition of the macro RW_THR_HAS_DUAL_PRIORITY, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetProcessScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMaxProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the maximum system-scope priority value 
      // supported by the current system-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the system-scope priority attribute is not supported. This 
      // exception can be avoided by testing to see if the macro 
      // RW_THR_HAS_SYSTEM_SCOPE_PRIORITY is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_PROCESS_SCOPE, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetSystemScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMaxSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the maximum time-slice quantum value supported
      // by the current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the time-slice quantum attribute is not supported. This
      // exception can be avoided by testing to see if the macro
      // RW_THR_HAS_TIME_SLICE_QUANTUM is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not use or 
      // support the time-slice quantum attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetTimeSliceQuantum() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      unsigned long
      getMaxTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the minimum priority value supported by the 
      // current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if the macro RW_THR_HAS_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMinPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the minimum process-scope priority value 
      // supported by the current process-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if RW_THR_HAS_PROCESS_SCOPE_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_SYSTEM_SCOPE and the current
      // environment does not support dual priority values, as indicated by
      // the definition of the macro RW_THR_HAS_DUAL_PRIORITY, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMinProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This static function returns the minimum stack size required to launch 
      // a thread that calls a null function.  Threads.h++ automatically 
      // guarantees that the size of any stack allocated by the system will be 
      // larger than this value.
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if stack attributes are not supported. This exception can be 
      // avoided by testing to see if either of the macros 
      // RW_THR_HAS_STACK_RESERVE_SIZE or RW_THR_HAS_USER_STACK are currently
      // defined.
      //
      // These exceptions can be avoided by calling this function only when 
      // either of the feature test functions, canSetStackReserveSize() or
      // canSetUserStack() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      static
      size_t
      getMinStackSize(void)
         RWTHRTHROWS2(RWTHROperationNotSupported,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the minimum system-scope priority value 
      // supported by the current system-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the system-scope priority attribute is not supported. This 
      // exception can be avoided by testing to see if the macro 
      // RW_THR_HAS_SYSTEM_SCOPE_PRIORITY is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_PROCESS_SCOPE, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetSystemScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMinSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the minimum time-slice quantum value supported
      // by the current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the time-slice quantum attribute is not supported. This
      // exception can be avoided by testing to see if the macro
      // RW_THR_HAS_TIME_SLICE_QUANTUM is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not use or 
      // support the time-slice quantum attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetTimeSliceQuantum() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      unsigned long
      getMinTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

   protected:

      // Bind a new handle instance to a thread attribute instance 
      RWThreadAttribute(RWThreadAttributeImp* imp)
         RWTHRTHROWSANY;

      // Get a reference to the thread attibute instance, if any, 
      // pointed-to by this handle instance, and if the handle is
      // undefined (because it was a static instance) then bind
      // this handle instance to a new default thread attribute instance 
      // (this member was declared const even though it may change the 
      //  handle instance - this eliminates the need to cast away const
      //  on the this pointer in all const interface members...)
      RWThreadAttributeImp&
      body(void) const
         RWTHRTHROWSANY;

};

class RWTHRExport RWThreadAttributeImp :
   public RWTHRBody {
   
   RW_THR_DECLARE_TRACEABLE

   public:

      friend class RWThreadAttribute;

   protected:

      static RWMutexLock         commonMutex_;

      unsigned                   attributeMask_;


#  if defined(RW_THR_HAS_CONCURRENCY_POLICY)
      static const unsigned      CONCURRENCY_POLICY;
      RWConcurrencyPolicy        concurrencyPolicy_;
#  endif

#  if defined(RW_THR_HAS_CONTENTION_SCOPE)
      static const unsigned      CONTENTION_SCOPE;
      RWContentionScope          contentionScope_;
#  endif

#  if defined(RW_THR_HAS_INHERITANCE_POLICY)
      static const unsigned      INHERITANCE_POLICY;
      RWInheritancePolicy        inheritancePolicy_;
#  endif

#  if defined(RW_THR_HAS_PROCESS_SCOPE_PRIORITY)
      static const unsigned      PROCESS_SCOPE_PRIORITY;
      RWPriority                 processScopePriority_;
#  endif

#  if defined(RW_THR_HAS_SYSTEM_SCOPE_PRIORITY)
      static const unsigned      SYSTEM_SCOPE_PRIORITY;
      RWPriority                 systemScopePriority_;
#  endif

#  if defined(RW_THR_HAS_SCHEDULING_POLICY)
      static const unsigned      SCHEDULING_POLICY;
      RWSchedulingPolicy         schedulingPolicy_;
#  endif

#  if defined(RW_THR_HAS_STACK_COMMIT_SIZE)
      static const unsigned      STACK_COMMIT_SIZE;
      size_t                     stackCommitSize_;
#  endif

#  if defined(RW_THR_HAS_STACK_RESERVE_SIZE)
      static const unsigned      STACK_RESERVE_SIZE;
      size_t                     stackReserveSize_;
#  endif

#  if defined(RW_THR_HAS_START_POLICY)
      static const unsigned      START_POLICY;
      RWStartPolicy              startPolicy_;
#  endif

#  if defined(RW_THR_HAS_TIME_SLICE_QUANTUM)
      static const unsigned      TIME_SLICE_QUANTUM;
      unsigned long              timeSliceQuantum_;
#  endif

#  if defined(RW_THR_HAS_USER_STACK)
      static const unsigned      USER_STACK;
      void*                      userStackAddress_;
      size_t                     userStackSize_;
#  endif

   protected:

      // Prohibit direct construction (must use RWThreadAttribute)
      RWThreadAttributeImp(void)
         RWTHRTHROWSNONE;
     
      // Deadlock safe assignment...
      RWThreadAttributeImp&
      operator=(const RWThreadAttributeImp& second)
         RWTHRTHROWS1(RWTHRInternalError);

      // Deadlock safe comparison...
      RWBoolean
      isEqual(const RWThreadAttributeImp& second) const
         RWTHRTHROWS1(RWTHRInternalError);

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
      // If the corresponding attribute value has not yet be "set", then a 
      // return value of TRUE indicates that a default value is available and 
      // may be read.
      //
      // These functions always return TRUE if the corresponding attribute
      // still has the value previously defined by a call to the matching "set"
      // function.  In this case, the behavior is similar to that provided by
      // the "is set" functions (declared later).
      //
      // Some of these functions will throw the RWTHRInternalError exception if 
      // some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      canGetConcurrencyPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetContentionScope(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetInheritancePolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

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
      canGetStackCommitSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetStackReserveSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetStartPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canGetUserStack(void) const
         RWTHRTHROWS1(RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The public "get" functions below can be used to query the default 
      // value of an attribute, if the attribute is not "set" and there is a 
      // default value available, or the functions can be used to retreive the 
      // last value defined in a call to the matching "set" function.
      // 
      // These functions will throw the RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw the RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but has not yet been defined
      // by a call the the matching "set" function, and no default value is 
      // available under the current circumstances.
      //
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWConcurrencyPolicy
      getConcurrencyPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWContentionScope 
      getContentionScope(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWInheritancePolicy
      getInheritancePolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWPriority
      getPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWPriority
      getProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWSchedulingPolicy
      getSchedulingPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      size_t
      getStackCommitSize(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);
      size_t
      getStackReserveSize(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWStartPolicy
      getStartPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      RWPriority
      getSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      unsigned long
      getTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      void*
      getUserStackAddress(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      size_t
      getUserStackSize(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The public "is set" test functions below can be used to determine 
      // whether the corresponding attribute still has the value specified in 
      // an earlier call the the matching "set" function.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment, has not yet been set, or
      // has been forced to some other default value in response to a change
      // in another related attribute.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment, has been set, has not 
      // been reset, and has not forced to a default value as the result of a 
      // change in a some related attribute.
      //
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      isConcurrencyPolicySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isContentionScopeSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isInheritancePolicySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isPrioritySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isProcessScopePrioritySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isSchedulingPolicySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isStackCommitSizeSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isStackReserveSizeSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isStartPolicySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isSystemScopePrioritySet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isTimeSliceQuantumSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      isUserStackSet(void) const
         RWTHRTHROWS1(RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The public "can set" test functions below can be used to determine, at 
      // run-time, whether the corresponding attribute value may be set.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the specified 
      // policy, if any, is not supported under the current circumstances.
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
      canSetConcurrencyPolicy(RWConcurrencyPolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetContentionScope(RWContentionScope scope) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetInheritancePolicy(RWInheritancePolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

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
      canSetStackCommitSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetStackReserveSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetStartPolicy(RWStartPolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      canSetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      canSetUserStack(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      
      /////////////////////////////////////////////////////////////////////////
      // The public "set" functions below can be used to set the corresponding 
      // attribute value.
      // 
      // These functions will throw the RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw the RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the specified value is
      // not supported under the current circumstances.
      // 
      // These functions will throw the RWTHRBoundsError exception if the
      // specified value is invalid or falls outside the current legal range
      // for that attribute.
      //
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      void
      setConcurrencyPolicy(RWConcurrencyPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setContentionScope(RWContentionScope scope) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setInheritancePolicy(RWInheritancePolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setPriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setProcessScopePriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setSchedulingPolicy(RWSchedulingPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setStackCommitSize(size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setStackReserveSize(size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setStartPolicy(RWStartPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setSystemScopePriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setTimeSliceQuantum(unsigned long milliseconds) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      setUserStack(void* address, size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The "reset" functions below can be used to restore an attribute value
      // to its default setting, if any.
      //
      // These functions will always succeed, even if the target attribute is
      // not supported in the current environment.
      // 
      // There are several combinations of attributes and environments where it
      // is not possible to query for a default value of an attribute; using 
      // these functions will simply clear any previous setting so that 
      // Threads.h++ will know to rely on the underlying API to determine the 
      // appropriate default value.
      /////////////////////////////////////////////////////////////////////////

      void
      resetConcurrencyPolicy(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetContentionScope(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetInheritancePolicy(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetPriority(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetProcessScopePriority(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetSchedulingPolicy(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetStackCommitSize(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetStackReserveSize(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetStartPolicy(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);
      void
      resetSystemScopePriority(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetTimeSliceQuantum(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      void
      resetUserStack(void) 
         RWTHRTHROWS2(RWTHRInternalError,
                      RWTHROperationNotSupported);

      
      /////////////////////////////////////////////////////////////////////////
      // The following functions may be used to determine the legal range
      // of values for various attributes.
      /////////////////////////////////////////////////////////////////////////
      
      /////////////////////////////////////////////////////////////////////////
      // This function returns the maximum priority value supported by the 
      // current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if the macro RW_THR_HAS_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMaxPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the maximum process-scope priority value 
      // supported by the current process-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if RW_THR_HAS_PROCESS_SCOPE_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_SYSTEM_SCOPE and the current
      // environment does not support dual priority values, as indicated by
      // the definition of the macro RW_THR_HAS_DUAL_PRIORITY, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetProcessScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMaxProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the maximum system-scope priority value 
      // supported by the current system-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the system-scope priority attribute is not supported. This 
      // exception can be avoided by testing to see if the macro 
      // RW_THR_HAS_SYSTEM_SCOPE_PRIORITY is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_PROCESS_SCOPE, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetSystemScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMaxSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the maximum time-slice quantum value supported
      // by the current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the time-slice quantum attribute is not supported. This
      // exception can be avoided by testing to see if the macro
      // RW_THR_HAS_TIME_SLICE_QUANTUM is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not use or 
      // support the time-slice quantum attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetTimeSliceQuantum() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      unsigned long
      getMaxTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the minimum priority value supported by the 
      // current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if the macro RW_THR_HAS_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMinPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the minimum process-scope priority value 
      // supported by the current process-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if RW_THR_HAS_PROCESS_SCOPE_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_SYSTEM_SCOPE and the current
      // environment does not support dual priority values, as indicated by
      // the definition of the macro RW_THR_HAS_DUAL_PRIORITY, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMinProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This static function returns the minimum stack size required to launch 
      // a thread that calls a null function.  Threads.h++ automatically 
      // guarantees that the size of any stack allocated by the system will be 
      // larger than this value.
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if stack attributes are not supported. This exception can be 
      // avoided by testing to see if either of the macros 
      // RW_THR_HAS_STACK_RESERVE_SIZE or RW_THR_HAS_USER_STACK are currently
      // defined.
      //
      // These exceptions can be avoided by calling this function only when 
      // either of the feature test functions, canSetStackReserveSize() or
      // canSetUserStack() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      static
      size_t
      getMinStackSize(void)
         RWTHRTHROWS2(RWTHROperationNotSupported,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the minimum system-scope priority value 
      // supported by the current system-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the system-scope priority attribute is not supported. This 
      // exception can be avoided by testing to see if the macro 
      // RW_THR_HAS_SYSTEM_SCOPE_PRIORITY is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_PROCESS_SCOPE, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetSystemScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      getMinSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This function returns the minimum time-slice quantum value supported
      // by the current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the time-slice quantum attribute is not supported. This
      // exception can be avoided by testing to see if the macro
      // RW_THR_HAS_TIME_SLICE_QUANTUM is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not use or 
      // support the time-slice quantum attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetTimeSliceQuantum() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      unsigned long
      getMinTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

   private:

      /////////////////////////////////////////////////////////////////////////
      // The private "is inherited" functions below are used internally to 
      // determine whether or not the corresponding attribute is to be 
      // inherited.
      //
      // These functions return TRUE if the corresponding attribute is to be
      // inherited, and FALSE if the attribute is to be explicitly supplied.
      //
      // Some of these functions will throw the RWTHRInternalError exception if 
      // some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      _isPriorityInherited(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isProcessScopePriorityInherited(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isSystemScopePriorityInherited(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isSchedulingPolicyInherited(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isTimeSliceQuantumInherited(void) const
         RWTHRTHROWSNONE;


      /////////////////////////////////////////////////////////////////////////
      // The private "can get default" functions below are used internally to 
      // indicate whether or not a default value for the corresponding 
      // attribute is available under present circumstances.
      //
      // These functions return TRUE if the default value for the corresponding
      // attribute is defined and available.
      // 
      // These functions return FALSE if the default value for the 
      // corresponding attribute is not currently defined or available.
      //
      // Some of these functions will throw the RWTHRInternalError exception if 
      // some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      _canGetDefaultConcurrencyPolicy(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetDefaultContentionScope(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetDefaultInheritancePolicy(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetDefaultPriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetDefaultProcessScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetDefaultSchedulingPolicy(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetDefaultStackCommitSize(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetDefaultStackReserveSize(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetDefaultStartPolicy(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetDefaultSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetDefaultTimeSliceQuantum(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetDefaultUserStack(void) const
         RWTHRTHROWSNONE;


      /////////////////////////////////////////////////////////////////////////
      // The private "get default" functions below are used internally to get 
      // the current default value of the corresponding attribute.
      //
      // These functions will return the default value for the corresponding
      // attribute.  The current default value for any attribute may depend
      // on the settings of other related attributes.
      // If the default value of that attribute cannot be provided, these
      // functions will fail with an assert in the debug version of the 
      // library; these functions should NOT be called unless the corresponding
      // "can get default" function has indicated that a default value is
      // available.
      // 
      // Some of these functions will throw the RWTHRInternalError exception if 
      // some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWConcurrencyPolicy
      _getDefaultConcurrencyPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWContentionScope
      _getDefaultContentionScope(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWInheritancePolicy
      _getDefaultInheritancePolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWPriority
      _getDefaultPriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWPriority
      _getDefaultProcessScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWSchedulingPolicy
      _getDefaultSchedulingPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      size_t
      _getDefaultStackCommitSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      size_t
      _getDefaultStackReserveSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWStartPolicy
      _getDefaultStartPolicy(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWPriority
      _getDefaultSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      unsigned long
      _getDefaultTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      void*
      _getDefaultUserStackAddress(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      size_t
      _getDefaultUserStackSize(void) const
         RWTHRTHROWS1(RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The private "can get" test functions below are used internally to 
      // determine at run-time whether the corresponding attribute value may 
      // be read.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the corrsponding
      // "get" function CANNOT return a legal value under current circumstances.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment and the corresponding "get" 
      // function CAN return a legal value under current circumstances.
      //
      // If the corresponding attribute value has not yet be "set", then a 
      // return value of TRUE indicates that a default value is available and 
      // may be read.
      //
      // These functions always return TRUE if the corresponding attribute
      // still has the value previously defined by a call to the matching "set"
      // function.  In this case, the behavior is similar to that provided by
      // the "is set" functions (declared later).
      //
      // Some of these functions will throw the RWTHRInternalError exception if 
      // some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      _canGetConcurrencyPolicy(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetContentionScope(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetInheritancePolicy(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetPriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetProcessScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetSchedulingPolicy(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetStackCommitSize(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetStackReserveSize(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetStartPolicy(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canGetTimeSliceQuantum(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canGetUserStack(void) const
         RWTHRTHROWSNONE;


      /////////////////////////////////////////////////////////////////////////
      // The private "get" functions below are used internally to get the 
      // current value of the corresponding attribute.
      //
      // These functions return the value that was defined by a call to 
      // the matching "set" function, if any.  If an attribute value has not 
      // yet been set, these functions will return the default value for that 
      // attribute. If the default value of that attribute is provided by the 
      // underlying threads API, and cannot be queried by Threads.h++, these 
      // functions will throw an RWTHROperationNotAvailable exception.
      // 
      // These functions will throw the RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWConcurrencyPolicy
      _getConcurrencyPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWContentionScope
      _getContentionScope(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWInheritancePolicy
      _getInheritancePolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWPriority
      _getPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWPriority
      _getProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWSchedulingPolicy
      _getSchedulingPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      size_t
      _getStackCommitSize(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      size_t
      _getStackReserveSize(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWStartPolicy
      _getStartPolicy(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWPriority
      _getSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      unsigned long
      _getTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void*
      _getUserStackAddress(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      size_t
      _getUserStackSize(void) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // The private "is set" test functions below can be used to determine 
      // whether the corresponding attribute still has the value specified in 
      // an earlier call the the matching "set" function.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment, has not yet been set, or
      // has been forced to some other default value in response to a change
      // in another related attribute.
      //
      // Each of these functions returns TRUE if the corresponding attribute IS
      // supported in the current environment, has been set, has not 
      // been reset, and has not forced to a default value as the result of a 
      // change in a some related attribute.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      _isConcurrencyPolicySet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isContentionScopeSet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isInheritancePolicySet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isPrioritySet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isProcessScopePrioritySet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isSchedulingPolicySet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isStackCommitSizeSet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isStackReserveSizeSet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isStartPolicySet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isSystemScopePrioritySet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isTimeSliceQuantumSet(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _isUserStackSet(void) const
         RWTHRTHROWSNONE;


      /////////////////////////////////////////////////////////////////////////
      // The private "can set" test functions below can be used to determine, 
      // at run-time, whether the corresponding attribute value may be set.
      // 
      // Each of these functions returns FALSE if the corresponding attribute
      // is NOT supported in the current environment or if the specified 
      // policy, if any, is not supported under the current circumstances.
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
      _canSetConcurrencyPolicy(RWConcurrencyPolicy policy) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      _canSetContentionScope(RWContentionScope scope) const
         RWTHRTHROWS2(RWTHRBoundsError,
                      RWTHRInternalError);

      RWBoolean
      _canSetInheritancePolicy(RWInheritancePolicy policy) const
         RWTHRTHROWS1(RWTHRBoundsError);

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
      _canSetStackCommitSize(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canSetStackReserveSize(void) const
         RWTHRTHROWSNONE;

      RWBoolean
      _canSetStartPolicy(RWStartPolicy policy) const
         RWTHRTHROWS1(RWTHRBoundsError);

      RWBoolean
      _canSetSystemScopePriority(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canSetTimeSliceQuantum(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWBoolean
      _canSetUserStack(void) const
         RWTHRTHROWSNONE;

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
      // These functions will throw the RWTHROperationNotAvailable exception 
      // if the corresponding attribute is supported, but the specified value 
      // is not supported under the current circumstances.
      // 
      // These functions will throw the RWTHROperationNotSupported exception 
      // if the attribute is not supported in the current environment.
      //
      // Some of these functions will throw the RWTHRInternalError exception 
      // if some unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      RWBoolean
      _validateConcurrencyPolicy(RWConcurrencyPolicy policy)  const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWBoolean
      _validateContentionScope(RWContentionScope scope)  const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      RWBoolean
      _validateInheritancePolicy(RWInheritancePolicy policy) const
         RWTHRTHROWS3(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

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
      _validateStackCommitSize(size_t size) const
         RWTHRTHROWS2(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported);

      RWBoolean
      _validateStackReserveSize(size_t size) const
         RWTHRTHROWS2(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported);

      RWBoolean
      _validateStartPolicy(RWStartPolicy policy) const
         RWTHRTHROWS2(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported);

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

      RWBoolean
      _validateUserStack(void* address, size_t size) const
         RWTHRTHROWS2(RWTHROperationNotAvailable,
                      RWTHROperationNotSupported);

      /////////////////////////////////////////////////////////////////////////
      // The private "set" functions below are used internally to set the 
      // corresponding attribute value.
      // 
      // These functions will throw the RWTHROperationNotSupported exception if
      // the attribute is not supported in the current environment.
      // 
      // These functions will throw the RWTHROperationNotAvailable exception if
      // the corresponding attribute is supported, but the specified value is
      // not supported under the current circumstances.
      // 
      // These functions will throw the RWTHRBoundsError exception if the
      // specified value is invalid or falls outside the current legal range
      // for that attribute.
      //
      // These functions will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////

      void
      _setConcurrencyPolicy(RWConcurrencyPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setContentionScope(RWContentionScope scope) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setInheritancePolicy(RWInheritancePolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setPriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setProcessScopePriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setSchedulingPolicy(RWSchedulingPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setStackCommitSize(size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setStackReserveSize(size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setStartPolicy(RWStartPolicy policy) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setSystemScopePriority(RWPriority priority) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setTimeSliceQuantum(unsigned long milliseconds) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);

      void
      _setUserStack(void* address, size_t size) 
         RWTHRTHROWS4(RWTHRBoundsError,
                      RWTHROperationNotAvailable,
                      RWTHROperationNotSupported,
                      RWTHRInternalError);


      /////////////////////////////////////////////////////////////////////////
      // The private "reset" functions below can be used to restore an 
      // attribute value to its default setting, if any.
      //
      // These functions will always succeed, even if the target attribute is
      // not supported in the current environment.
      // 
      // There are several combinations of attributes and environments where it
      // is not possible to query for a default value of an attribute; using 
      // these functions will simply clear any previous setting so that 
      // Threads.h++ will know to rely on the underlying API to determine the 
      // appropriate default value.
      /////////////////////////////////////////////////////////////////////////

      void
      _resetConcurrencyPolicy(void) 
         RWTHRTHROWSNONE;

      void
      _resetContentionScope(void) 
         RWTHRTHROWSNONE;

      void
      _resetInheritancePolicy(void) 
         RWTHRTHROWSNONE;

      void
      _resetPriority(void) 
         RWTHRTHROWSNONE;

      void
      _resetProcessScopePriority(void) 
         RWTHRTHROWSNONE;

      void
      _resetSchedulingPolicy(void) 
         RWTHRTHROWSNONE;

      void
      _resetStackCommitSize(void) 
         RWTHRTHROWSNONE;

      void
      _resetStackReserveSize(void) 
         RWTHRTHROWSNONE;

      void
      _resetStartPolicy(void) 
         RWTHRTHROWSNONE;

      void
      _resetSystemScopePriority(void) 
         RWTHRTHROWSNONE;

      void
      _resetTimeSliceQuantum(void) 
         RWTHRTHROWSNONE;

      void
      _resetUserStack(void) 
         RWTHRTHROWSNONE;

      /////////////////////////////////////////////////////////////////////////
      // The following private functions may be used to determine the legal 
      // range of values for various attributes.
      /////////////////////////////////////////////////////////////////////////
      
      /////////////////////////////////////////////////////////////////////////
      // This private function returns the maximum priority value supported by 
      // the current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if the macro RW_THR_HAS_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      _getMaxPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This private function returns the maximum process-scope priority value 
      // supported by the current process-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if RW_THR_HAS_PROCESS_SCOPE_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_SYSTEM_SCOPE and the current
      // environment does not support dual priority values, as indicated by
      // the definition of the macro RW_THR_HAS_DUAL_PRIORITY, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetProcessScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      _getMaxProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This private function returns the maximum system-scope priority value 
      // supported by the current system-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the system-scope priority attribute is not supported. This 
      // exception can be avoided by testing to see if the macro 
      // RW_THR_HAS_SYSTEM_SCOPE_PRIORITY is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_PROCESS_SCOPE, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetSystemScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      _getMaxSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This private function returns the maximum time-slice quantum value 
      // supported by the current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the time-slice quantum attribute is not supported. This
      // exception can be avoided by testing to see if the macro
      // RW_THR_HAS_TIME_SLICE_QUANTUM is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not use or 
      // support the time-slice quantum attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetTimeSliceQuantum() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      unsigned long
      _getMaxTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This private function returns the minimum priority value supported by 
      // the current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if the macro RW_THR_HAS_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not
      // use or support the priority attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      _getMinPriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This private function returns the minimum process-scope priority value 
      // supported by the current process-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the priority attribute is not supported. This exception can be 
      // avoided by testing to see if RW_THR_HAS_PROCESS_SCOPE_PRIORITY is 
      // currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_SYSTEM_SCOPE and the current
      // environment does not support dual priority values, as indicated by
      // the definition of the macro RW_THR_HAS_DUAL_PRIORITY, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetPriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      _getMinProcessScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This private function returns the minimum system-scope priority value 
      // supported by the current system-scope scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the system-scope priority attribute is not supported. This 
      // exception can be avoided by testing to see if the macro 
      // RW_THR_HAS_SYSTEM_SCOPE_PRIORITY is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope is RW_THR_PROCESS_SCOPE, of if the 
      // current scheduling policy does not use or support the priority 
      // attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetSystemScopePriority() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      RWPriority
      _getMinSystemScopePriority(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

      /////////////////////////////////////////////////////////////////////////
      // This private function returns the minimum time-slice quantum value 
      // supported by the current contention scope and scheduling policy.  
      //
      // This function will throw an RWTHROperationNotSupported exception
      // if the time-slice quantum attribute is not supported. This
      // exception can be avoided by testing to see if the macro
      // RW_THR_HAS_TIME_SLICE_QUANTUM is currently defined.
      //
      // This function will throw an RWTHROperationNotAvailable exception if
      // the current contention scope and scheduling policy do not use or 
      // support the time-slice quantum attribute.  
      //
      // These exceptions can be avoided by calling this function only when the
      // feature test function, canSetTimeSliceQuantum() returns TRUE.
      //
      // This function will throw the RWTHRInternalError exception if some 
      // unexpected error or exception occurs within the library code.
      /////////////////////////////////////////////////////////////////////////
      unsigned long
      _getMinTimeSliceQuantum(void) const
         RWTHRTHROWS3(RWTHROperationNotSupported,
                      RWTHROperationNotAvailable,
                      RWTHRInternalError);

};

///////////////////////////////////////////////////////////////////////////////

inline
RWThreadAttribute::RWThreadAttribute(void)
   RWTHRTHROWSANY
   :
      RWTHRHandle(new RWThreadAttributeImp)
{
   RWTHRTRACEMF(RWThreadAttribute,RWThreadAttribute(void));
}

inline
RWThreadAttribute::RWThreadAttribute(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWThreadAttribute,RWThreadAttribute(RWStaticCtor));
}

inline
RWThreadAttribute::RWThreadAttribute(const RWThreadAttribute& second)
   RWTHRTHROWSANY
   :
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWThreadAttribute,RWThreadAttribute(const RWThreadAttribute&));
}

// protected
inline
RWThreadAttribute::RWThreadAttribute(RWThreadAttributeImp* imp)
   RWTHRTHROWSANY
   :
      RWTHRHandle(imp)
{
   RWTHRTRACEMF(RWThreadAttribute,RWThreadAttribute(RWThreadAttributeImp*));
}

inline
RWThreadAttribute&
RWThreadAttribute::operator=(const RWThreadAttribute& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadAttribute,operator=(const RWThreadAttribute&):RWThreadAttribute&);
   RWTHRHandle::operator=(second);
   return *this;
}

// protected
inline
RWThreadAttributeImp&
RWThreadAttribute::body(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadAttribute,body(void):RWThreadAttributeImp&);
   if (!isValid()) {
      // This handle is not currently bound to a thread attribute instance,
      // so create one and assign it to this handle instance...
      RW_THR_CONST_CAST(RWThreadAttribute&,*this) = RWThreadAttribute(new RWThreadAttributeImp);
   }
   return (RWThreadAttributeImp&)RWTHRHandle::body();
}

inline
void
RWThreadAttribute::copy(const RWThreadAttribute& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadAttribute,copy(const RWThreadAttribute&):void);
   body() = second.body();
}

inline
RWBoolean 
RWThreadAttribute::isEqual(const RWThreadAttribute& second) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isEqual(const RWThreadAttribute&) const:RWBoolean);
   return body().isEqual(second.body());
}

///////////////////////////////////////////////////////////////////////////////

inline
RWBoolean
RWThreadAttribute::canGetConcurrencyPolicy(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetConcurrencyPolicy(void) const:RWBoolean);
   return body().canGetConcurrencyPolicy();
}

inline
RWBoolean
RWThreadAttribute::canGetContentionScope(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetContentionScope(void) const:RWBoolean);
   return body().canGetContentionScope();
}

inline
RWBoolean
RWThreadAttribute::canGetInheritancePolicy(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetInheritancePolicy(void) const:RWBoolean);
   return body().canGetInheritancePolicy();
}

inline
RWBoolean
RWThreadAttribute::canGetPriority(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetPriority(void) const:RWBoolean);
   return body().canGetPriority();
}

inline
RWBoolean
RWThreadAttribute::canGetProcessScopePriority(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetProcessScopePriority(void) const:RWBoolean);
   return body().canGetProcessScopePriority();
}

inline
RWBoolean
RWThreadAttribute::canGetSchedulingPolicy(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetSchedulingPolicy(void) const:RWBoolean);
   return body().canGetSchedulingPolicy();
}

inline
RWBoolean
RWThreadAttribute::canGetStackCommitSize(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetStackCommitSize(void) const:RWBoolean);
   return body().canGetStackCommitSize();
}

inline
RWBoolean
RWThreadAttribute::canGetStackReserveSize(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetStackReserveSize(void) const:RWBoolean);
   return body().canGetStackReserveSize();
}

inline
RWBoolean
RWThreadAttribute::canGetStartPolicy(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetStartPolicy(void) const:RWBoolean);
   return body().canGetStartPolicy();
}

inline
RWBoolean
RWThreadAttribute::canGetSystemScopePriority(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetSystemScopePriority(void) const:RWBoolean);
   return body().canGetSystemScopePriority();
}

inline
RWBoolean
RWThreadAttribute::canGetTimeSliceQuantum(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetTimeSliceQuantum(void) const:RWBoolean);
   return body().canGetTimeSliceQuantum();
}

inline
RWBoolean
RWThreadAttribute::canGetUserStack(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canGetUserStack(void) const:RWBoolean);
   return body().canGetUserStack();
}

///////////////////////////////////////////////////////////////////////////////

inline
RWConcurrencyPolicy
RWThreadAttribute::getConcurrencyPolicy(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getConcurrencyPolicy(void) const:RWConcurrencyPolicy);
   return body().getConcurrencyPolicy();
}

inline
RWContentionScope 
RWThreadAttribute::getContentionScope(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getContentionScope(void) const:RWContentionScope);
   return body().getContentionScope();
}

inline
RWInheritancePolicy
RWThreadAttribute::getInheritancePolicy(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getInheritancePolicy(void) const:RWInheritancePolicy);
   return body().getInheritancePolicy();
}

inline
RWPriority
RWThreadAttribute::getPriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getPriority(void) const:RWPriority);
   return body().getPriority();
}

inline
RWPriority
RWThreadAttribute::getProcessScopePriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getProcessScopePriority(void) const:RWPriority);
   return body().getProcessScopePriority();
}

inline
RWSchedulingPolicy
RWThreadAttribute::getSchedulingPolicy(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getSchedulingPolicy(void) const:RWSchedulingPolicy);
   return body().getSchedulingPolicy();
}

inline
size_t
RWThreadAttribute::getStackCommitSize(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getStackCommitSize(void) const:size_t);
   return body().getStackCommitSize();
}

inline
size_t
RWThreadAttribute::getStackReserveSize(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getStackReserveSize(void) const:size_t);
   return body().getStackReserveSize();
}

inline
RWStartPolicy
RWThreadAttribute::getStartPolicy(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getStartPolicy(void) const:RWStartPolicy);
   return body().getStartPolicy();
}

inline
RWPriority
RWThreadAttribute::getSystemScopePriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getSystemScopePriority(void) const:RWPriority);
   return body().getSystemScopePriority();
}

inline
unsigned long
RWThreadAttribute::getTimeSliceQuantum(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getTimeSliceQuantum(void) const:unsigned long);
   return body().getTimeSliceQuantum();
}

inline
void*
RWThreadAttribute::getUserStackAddress(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getUserStackAddress(void) const:void*);
   return body().getUserStackAddress();
}

inline
size_t
RWThreadAttribute::getUserStackSize(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getUserStackSize(void) const:size_t);
   return body().getUserStackSize();
}

///////////////////////////////////////////////////////////////////////////////

inline
RWBoolean
RWThreadAttribute::isConcurrencyPolicySet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isConcurrencyPolicySet(void) const:RWBoolean);
   return body().isConcurrencyPolicySet();
}

inline
RWBoolean
RWThreadAttribute::isContentionScopeSet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isContentionScopeSet(void) const:RWBoolean);
   return body().isContentionScopeSet();
}

inline
RWBoolean
RWThreadAttribute::isInheritancePolicySet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isInheritancePolicySet(void) const:RWBoolean);
   return body().isInheritancePolicySet();
}

inline
RWBoolean
RWThreadAttribute::isPrioritySet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isPrioritySet(void) const:RWBoolean);
   return body().isPrioritySet();
}

inline
RWBoolean
RWThreadAttribute::isProcessScopePrioritySet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isProcessScopePrioritySet(void) const:RWBoolean);
   return body().isProcessScopePrioritySet();
}

inline
RWBoolean
RWThreadAttribute::isSchedulingPolicySet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isSchedulingPolicySet(void) const:RWBoolean);
   return body().isSchedulingPolicySet();
}

inline
RWBoolean
RWThreadAttribute::isStackCommitSizeSet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isStackCommitSizeSet(void) const:RWBoolean);
   return body().isStackCommitSizeSet();
}

inline
RWBoolean
RWThreadAttribute::isStackReserveSizeSet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isStackReserveSizeSet(void) const:RWBoolean);
   return body().isStackReserveSizeSet();
}

inline
RWBoolean
RWThreadAttribute::isStartPolicySet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isStartPolicySet(void) const:RWBoolean);
   return body().isStartPolicySet();
}

inline
RWBoolean
RWThreadAttribute::isSystemScopePrioritySet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isSystemScopePrioritySet(void) const:RWBoolean);
   return body().isSystemScopePrioritySet();
}

inline
RWBoolean
RWThreadAttribute::isTimeSliceQuantumSet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isTimeSliceQuantumSet(void) const:RWBoolean);
   return body().isTimeSliceQuantumSet();
}

inline
RWBoolean
RWThreadAttribute::isUserStackSet(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,isUserStackSet(void) const:RWBoolean);
   return body().isUserStackSet();
}

///////////////////////////////////////////////////////////////////////////////

inline
RWBoolean
RWThreadAttribute::canSetConcurrencyPolicy(RWConcurrencyPolicy policy) const
   RWTHRTHROWS2(RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetConcurrencyPolicy(RWConcurrencyPolicy) const:RWBoolean);
   return body().canSetConcurrencyPolicy(policy);
}

inline
RWBoolean
RWThreadAttribute::canSetContentionScope(RWContentionScope scope) const
   RWTHRTHROWS2(RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetContentionScope(RWContentionScope) const:RWBoolean);
   return body().canSetContentionScope(scope);
}

inline
RWBoolean
RWThreadAttribute::canSetInheritancePolicy(RWInheritancePolicy policy) const
   RWTHRTHROWS2(RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetInheritancePolicy(RWInheritancePolicy) const:RWBoolean);
   return body().canSetInheritancePolicy(policy);
}

inline
RWBoolean
RWThreadAttribute::canSetPriority(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetPriority(void) const:RWBoolean);
   return body().canSetPriority();
}

inline
RWBoolean
RWThreadAttribute::canSetProcessScopePriority(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetProcessScopePriority(void) const:RWBoolean);
   return body().canSetProcessScopePriority();
}

inline
RWBoolean
RWThreadAttribute::canSetSchedulingPolicy(RWSchedulingPolicy policy) const
   RWTHRTHROWS2(RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetSchedulingPolicy(RWSchedulingPolicy) const:RWBoolean);
   return body().canSetSchedulingPolicy(policy);
}

inline
RWBoolean
RWThreadAttribute::canSetStackCommitSize(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetStackCommitSize(void) const:RWBoolean);
   return body().canSetStackCommitSize();
}

inline
RWBoolean
RWThreadAttribute::canSetStackReserveSize(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetStackReserveSize(void) const:RWBoolean);
   return body().canSetStackReserveSize();
}

inline
RWBoolean
RWThreadAttribute::canSetStartPolicy(RWStartPolicy policy) const
   RWTHRTHROWS2(RWTHRBoundsError,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetStartPolicy(RWStartPolicy) const:RWBoolean);
   return body().canSetStartPolicy(policy);
}

inline
RWBoolean
RWThreadAttribute::canSetSystemScopePriority(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetSystemScopePriority(void) const:RWBoolean);
   return body().canSetSystemScopePriority();
}

inline
RWBoolean
RWThreadAttribute::canSetTimeSliceQuantum(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetTimeSliceQuantum(void) const:RWBoolean);
   return body().canSetTimeSliceQuantum();
}

inline
RWBoolean
RWThreadAttribute::canSetUserStack(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,canSetUserStack(void) const:RWBoolean);
   return body().canSetUserStack();
}

///////////////////////////////////////////////////////////////////////////////

inline
void
RWThreadAttribute::setConcurrencyPolicy(RWConcurrencyPolicy policy) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setConcurrencyPolicy(RWConcurrencyPolicy):void);
   body().setConcurrencyPolicy(policy);
}

inline
void
RWThreadAttribute::setContentionScope(RWContentionScope scope) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setContentionScope(RWContentionScope):void);
   body().setContentionScope(scope);
}

inline
void
RWThreadAttribute::setInheritancePolicy(RWInheritancePolicy policy) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setInheritancePolicy(RWInheritancePolicy):void);
   body().setInheritancePolicy(policy);
}

inline
void
RWThreadAttribute::setPriority(RWPriority priority) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setPriority(RWPriority):void);
   body().setPriority(priority);
}

inline
void
RWThreadAttribute::setProcessScopePriority(RWPriority priority) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setProcessScopePriority(RWPriority):void);
   body().setProcessScopePriority(priority);
}

inline
void
RWThreadAttribute::setSchedulingPolicy(RWSchedulingPolicy policy) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setSchedulingPolicy(RWSchedulingPolicy):void);
   body().setSchedulingPolicy(policy);
}

inline
void
RWThreadAttribute::setStackCommitSize(size_t size) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setStackCommitSize(size_t):void);
   body().setStackCommitSize(size);
}

inline
void
RWThreadAttribute::setStackReserveSize(size_t size) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setStackReserveSize(size_t):void);
   body().setStackReserveSize(size);
}

inline
void
RWThreadAttribute::setStartPolicy(RWStartPolicy policy) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setStartPolicy(policy):void);
   body().setStartPolicy(policy);
}

inline
void
RWThreadAttribute::setSystemScopePriority(RWPriority priority) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setSystemScopePriority(RWPriority):void);
   body().setSystemScopePriority(priority);
}

inline
void
RWThreadAttribute::setTimeSliceQuantum(unsigned long milliseconds) 
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setTimeSliceQuantum(unsigned long):void);
   body().setTimeSliceQuantum(milliseconds);
}

inline
void
RWThreadAttribute::setUserStack(void* address, size_t size)
   RWTHRTHROWS4(RWTHRBoundsError,
                RWTHROperationNotAvailable,
                RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,setUserStack(void*,size_t):void);
   body().setUserStack(address,size);
}

///////////////////////////////////////////////////////////////////////////////

inline
void
RWThreadAttribute::resetConcurrencyPolicy(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetConcurrencyPolicy(void):void);
   body().resetConcurrencyPolicy();
}

inline
void
RWThreadAttribute::resetContentionScope(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetContentionScope(void):void);
   body().resetContentionScope();
}

inline
void
RWThreadAttribute::resetInheritancePolicy(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetInheritancePolicy(void):void);
   body().resetInheritancePolicy();
}

inline
void
RWThreadAttribute::resetPriority(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetPriority(void):void);
   body().resetPriority();
}

inline
void
RWThreadAttribute::resetProcessScopePriority(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetProcessScopePriority(void):void);
   body().resetProcessScopePriority();
}

inline
void
RWThreadAttribute::resetSchedulingPolicy(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetSchedulingPolicy(void):void);
   body().resetSchedulingPolicy();
}

inline
void
RWThreadAttribute::resetStackCommitSize(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetStackCommitSize(void):void);
   body().resetStackCommitSize();
}

inline
void
RWThreadAttribute::resetStackReserveSize(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetStackReserveSize(void):void);
   body().resetStackReserveSize();
}

inline
void
RWThreadAttribute::resetStartPolicy(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetStartPolicy(void):void);
   body().resetStartPolicy();
}

inline
void
RWThreadAttribute::resetSystemScopePriority(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetSystemScopePriority(void):void);
   body().resetSystemScopePriority();
}

inline
void
RWThreadAttribute::resetTimeSliceQuantum(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetTimeSliceQuantum(void):void);
   body().resetTimeSliceQuantum();
}

inline
void
RWThreadAttribute::resetUserStack(void) 
   RWTHRTHROWS2(RWTHRInternalError,
                RWTHROperationNotSupported)
{
   RWTHRTRACEMF(RWThreadAttribute,resetUserStack(void):void);
   body().resetUserStack();
}

///////////////////////////////////////////////////////////////////////////////

inline
RWPriority
RWThreadAttribute::getMaxPriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getMaxPriority(void) const:RWPriority);
   return body().getMaxPriority();
}

inline
RWPriority
RWThreadAttribute::getMaxProcessScopePriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getMaxProcessScopePriority(void) const:RWPriority);
   return body().getMaxProcessScopePriority();
}

inline
RWPriority
RWThreadAttribute::getMaxSystemScopePriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getMaxSystemScopePriority(void) const:RWPriority);
   return body().getMaxSystemScopePriority();
}
                                   
inline
unsigned long
RWThreadAttribute::getMaxTimeSliceQuantum(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getMaxTimeSliceQuantum(void) const:unsigned long);
   return body().getMaxTimeSliceQuantum();
}

///////////////////////////////////////////////////////////////////////////////

inline
RWPriority
RWThreadAttribute::getMinPriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getMinPriority(void) const:RWPriority);
   return body().getMinPriority();
}

inline
RWPriority
RWThreadAttribute::getMinProcessScopePriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getMinProcessScopePriority(void) const:RWPriority);
   return body().getMinProcessScopePriority();
}

inline
//static
size_t
RWThreadAttribute::getMinStackSize(void)
   RWTHRTHROWS2(RWTHROperationNotSupported,
                RWTHRInternalError)
{
   RWTHRTRACESMF(RWThreadAttribute,getMinStackSize(void) const:size_t);
   return RWThreadAttributeImp::getMinStackSize();
}

inline
RWPriority
RWThreadAttribute::getMinSystemScopePriority(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getMinSystemScopePriority(void) const:RWPriority);
   return body().getMinSystemScopePriority();
}

inline
unsigned long
RWThreadAttribute::getMinTimeSliceQuantum(void) const
   RWTHRTHROWS3(RWTHROperationNotSupported,
                RWTHROperationNotAvailable,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadAttribute,getMinTimeSliceQuantum(void) const:unsigned long);
   return body().getMinTimeSliceQuantum();
}

#endif // __RWTHRTHRATTR_H__
