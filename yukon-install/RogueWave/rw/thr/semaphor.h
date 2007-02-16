#if !defined(__RWTHRSEMAPHOR_H__)
#  define __RWTHRSEMAPHOR_H__
/*****************************************************************************
 *
 * semaphor.h
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

semaphor.h - Class declarations for:

   RWSemaphore - Semaphore

See Also:

   semaphor.cpp - Out-of-line function definitions for classes.

******************************************************************************/

#  if !defined(__RWTHRSYNCH_H__)
#     include <rw/thr/synch.h>
#  endif

#  if !defined(__RWTHRGUARD_H__)
#     include <rw/thr/guard.h>
#  endif

#  if defined(RW_MULTI_THREAD)

//
//    Define internal semaphore representation, RWSemaphoreRep.
//
//    OS/2, POSIX, and Solaris use internal structure.
//    All other platforms typedef from underlying platform-defined type.

#     if defined(RW_THR_THREAD_API_OS2)
//    OS/2 Doesn't have a true counting semaphore, so we'll have to emulate it.
//    Note that the OS/2 implementation is different from the POSIX/Solaris
//    emulation.  The POSIX and Solaris semaphore implementations use a 
//    condition variable, which is available as a native mechanism under POSIX
//    and Solaris, but must be emulated on the OS/2 platform.  The OS/2 
//    implementation of condition variable uses a semaphore, hence, the OS/2 
//    semaphore cannot make use of a condition variable in its 
//    implementation...
#        if !defined(__RWTIMER_H__)
#           include<rw/timer.h>
#        endif
#        if !defined(__RW_OS2_H__)
#           define INCL_BASE
#           define __RW_OS2_H__ <os2.h>
#           include __RW_OS2_H__
#        endif
#        if !defined(__RWTHRMUTEX_H__)
#           include <rw/thr/mutex.h>     // Required for emulation
#        endif
#     elif defined(RW_THR_THREAD_API_POSIX) || \
           defined(RW_THR_THREAD_API_SOLARIS)
//    POSIX 1003.4a/1c does not provide a semaphore mechanism.
//    POSIX 1003.1b does provide one, but it is really a process-level 
//    mechanism and is subject to several restrictions including kernal limits 
//    on the number that may be supported at once.  We don't want to apply 
//    these restrictions on thread programmers, so we'll emulate semaphores on 
//    POSIX platforms.
//
//    Solaris provides a native semaphore, but it does not allow for an 
//    acquire with timeout. We view this as being a vital feature, especially 
//    with respect to thread cancellation. We could either remove semaphore 
//    acquire-with-timeout functionality from RWSemaphore on all platforms, 
//    for the sake of consistency; or we could emulate semaphores on Solaris 
//    as well. We decided on the latter. The only issue perhaps is native 
//    semaphores on Solaris are said to by async. safe; meaning that they can 
//    safely be posted from signal handlers. Our recommendation would be: don't 
//    install signal handlers. If you really have to, and you need to post a 
//    semaphore, use the raw native semaphore calls.
#        if !defined(__RWTHRMUTEX_H__)
#           include <rw/thr/mutex.h>     // Required for emulation
#        endif
#        if !defined(__RWTHRCONDTION_H__)
#           include <rw/thr/condtion.h>  // Required for emulation
#        endif

#if 0  // Native Solaris semaphores not used due to no timed acquire.
//#     elif defined(RW_THR_THREAD_API_SOLARIS)
//#        if !defined(__RW_SYNCH_H__)
//#           define __RW_SYNCH_H__ <synch.h>
//#           include __RW_SYNCH_H__
//#        endif
//
//         typedef sema_t RWSemaphoreRep;
#endif // #if 0 // Native Solaris semaphores not used due to no timed acquire.

#     elif defined(RW_THR_THREAD_API_WIN32)
#        define RW_MAX_SEMAPHORE_COUNT 0x7FFFFFFFL
#        if !defined(__RW_WINDOWS_H__)
#           define __RW_WINDOWS_H__ <windows.h>
#           include __RW_WINDOWS_H__
#        endif

         typedef HANDLE RWSemaphoreRep;

#     else
#        error RWSemaphoreRep declaration is missing!
#     endif

#  endif // RW_MULTI_THREAD


class RWTHRExport RWSemaphore :
   public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

   // Member Variables
   protected:

#  if defined(RW_MULTI_THREAD)
#     if defined(RW_THR_THREAD_API_OS2)

//    OS/2 Doesn't have a true counting semaphore, so we'll have to
//    emulate it.  Note that the OS/2 implementation is different from the
//    POSIX/Solaris emulation.  The POSIX/Solaris semaphore implementation use 
//    a condition variable, which is available as a native mechanism under 
//    POSIX, but must be emulated on the OS/2 platform.  The OS/2 
//    implementation of condition variable uses a semaphore, hence, the OS/2 
//    semaphore cannot make use of a condition variable in its implementation.

      RWMutexLock                acqLock_;
      RWMutexLock                countLock_;
      HEV                        nonZero_;
      volatile unsigned long     count_;

#     elif defined(RW_THR_THREAD_API_POSIX) || \
           defined(RW_THR_THREAD_API_SOLARIS)

//    Emulate semaphores on POSIX and Solaris platforms using a mutex, a 
//    condition variable, and a count.

      RWMutexLock                countLock_;
      RWCondition                nonZero_;
      unsigned long              count_;
      unsigned long              waitingThreads_;

#if 0  // Native Solaris semaphores not used due to no timed acquire.
//#     elif defined(RW_THR_THREAD_API_SOLARIS) || /**\**/
//           defined(RW_THR_THREAD_API_WIN32)
#endif // #if 0 // Native Solaris semaphores not used due to no timed acquire. 

#     elif defined(RW_THR_THREAD_API_WIN32) 

      RWSemaphoreRep    semaphore_;

#     endif
#  endif // RW_MULTI_THREAD

   // Member Functions
   public:

      // Create semaphore with an initial count (default to zero)
      RWSemaphore(unsigned count=0,
                  RWCancellationState state=RW_CANCELLATION_DISABLED)
         RWTHRTHROWS2(RWTHRResourceLimit,
                      RWTHRInternalError);

      // Destroy the semaphore
      ~RWSemaphore(void)
         RWTHRTHROWS1(RWTHRInternalError);

      // Decrement (acquire) the semaphore, blocking while zero.
      void
      acquire(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Decrement (acquire) the semaphore, blocking while zero,
      // until unblocked when another thread increments (releases)
      // the semaphore or until the specified amount of time passes.
      RWWaitStatus
      acquire(unsigned long milliseconds)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

#if 0  // Native Solaris semaphores not used due to no timed acquire.
//#  if defined(RW_MULTI_THREAD) && /**\**/
//      (defined(RW_THR_THREAD_API_SOLARIS) || defined(RW_THR_THREAD_API_WIN32))
#endif // #if 0  // Native Solaris semaphores not used due to no timed acquire.


#  if defined(RW_MULTI_THREAD) && \
      defined(RW_THR_THREAD_API_WIN32)

      // Give access to the underlying mechanism on platforms that provide one...
      RWSemaphoreRep*
      getSemaphoreRep(void) const
         RWTHRTHROWSNONE;

#  endif

      // This operation is synonymous with acquire(void). P stands for "passeren" which 
      // is Dutch for "to pass". (Dijkstra, the inventor of semaphores, is Dutch.) 
      void
      P(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);
      
      // This operation is synonymous with acquire(unsigned long milliseconds).
      RWWaitStatus
      P(unsigned long milliseconds)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Increment (release) the semaphore and unblock one waiting thread (if any)
      void
      release(void)
         RWTHRTHROWS1(RWTHRInternalError);

      // Try to decrement (acquire) semaphore without blocking
      // return TRUE for success and FALSE if the semaphore is zero.
      RWBoolean
      tryAcquire(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // This operation is synonymous with release(void). V stands for "vrygeven" which
      // is Dutch for "to release".
      void
      V(void)
         RWTHRTHROWS1(RWTHRInternalError);

      typedef RWLockGuard<RWSemaphore>     LockGuard;
      typedef RWTryLockGuard<RWSemaphore>  TryGuard;
      typedef RWUnlockGuard<RWSemaphore>   UnlockGuard;
};

/*****************************************************************************/

#if 0  // Native Solaris semaphores not used due to no timed acquire.
//#  if defined(RW_MULTI_THREAD) && /**\**/
//      (defined(RW_THR_THREAD_API_SOLARIS) || defined(RW_THR_THREAD_API_WIN32))
#endif // #if 0  // Native Solaris semaphores not used due to no timed acquire.

#  if defined(RW_MULTI_THREAD) && \
      defined(RW_THR_THREAD_API_WIN32)

inline
RWSemaphoreRep*
RWSemaphore::getSemaphoreRep(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWSemaphore,getSemaphoreRep);
   return &(RW_THR_CONST_CAST(RWSemaphore*,this)->semaphore_);
}

#  endif // RW_MULTI_THREAD

inline
void
RWSemaphore::P(void)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   acquire();
}
      
inline
RWWaitStatus
RWSemaphore::P(unsigned long milliseconds)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   return acquire(milliseconds);
}

inline
void
RWSemaphore::V(void)
   RWTHRTHROWS1(RWTHRInternalError)
{
   release();
}


#  if !defined(__RWTHRCANCEL_H__)
#     include <rw/thr/cancel.h>
#  endif

#endif // __RWTHRSEMAPHOR_H__

