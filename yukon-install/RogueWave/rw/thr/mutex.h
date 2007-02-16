#if !defined(__RWTHRMUTEX_H__)
#  define __RWTHRMUTEX_H__
/*****************************************************************************
 *
 * mutex.h
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

mutex.h - Class declarations for:

   RWMutexLock - A simple blocking mutex.
   RWNullMutexLock - Null, non-blocking mutex (a substitute for RWMutexLock
                     in single-threaded applications).

See Also:

   mutex.cpp  - Out-of-line function definitions.

*****************************************************************************/


#  if !defined(__RWTHRSYNCH_H__)
#     include <rw/thr/synch.h>
#  endif

#  if !defined(__RWTHRGUARD_H__)
#     include <rw/thr/guard.h>
#  endif

#  if defined(RW_MULTI_THREAD)

#     if defined(RW_THR_DEBUG)
#        if !defined(__RWTHRTHREADID_H__)
#           include <rw/thr/threadid.h>
#        endif
//       Use tools mutex in debug implementation
#        if !defined(__RWMUTEX_H__)
#           include <rw/mutex.h>
#        endif
#     endif

//
//    Define Internal Representation For Mutex
//

#     if defined(RW_THR_THREAD_API_OS2)

#        if !defined(__RW_OS2_H__)
#           define INCL_BASE
#           define __RW_OS2_H__ <os2.h>
#           include __RW_OS2_H__
#        endif

         typedef HMTX RWMutexLockRep;

#     elif defined(RW_THR_THREAD_API_POSIX)

         typedef pthread_mutex_t RWMutexLockRep;

#     elif defined(RW_THR_THREAD_API_SOLARIS)

#        if !defined(__RW_SYNCH_H__)
#           define __RW_SYNCH_H__ <synch.h>
#           include __RW_SYNCH_H__
#        endif
         typedef mutex_t RWMutexLockRep;

#     elif defined(RW_THR_THREAD_API_WIN32)

#        if !defined(__RW_WINDOWS_H__)
#           define __RW_WINDOWS_H__ <windows.h>
#           include __RW_WINDOWS_H__
#        endif
         typedef HANDLE RWMutexLockRep;

#     else
#        error RWMutexLockRep declaration is missing!
#     endif

#  endif // RW_MULTI_THREAD

class RWTHRExport RWMutexLock :
   public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

#  if defined(RW_THR_DEBUG) && defined(RW_MULTI_THREAD) && \
      (defined(RW_THR_THREAD_API_SOLARIS) || defined(RW_THR_THREAD_API_POSIX))

   // Give access to acquireId and releaseId debug members:
   friend class RWCondition; 

#  endif

   private:

#  if defined(RW_MULTI_THREAD)

      RWMutexLockRep    lock_;

#     if defined(RW_THR_DEBUG)
      // Debug implementation members
      RWMutex           mutex_;
      RWThreadId        threadId_;
#     endif

      int initFlag_;  // Assume zero initialization during static allocation:

#  endif // RW_MULTI_THREAD

   public:

      // Constructors

      RWMutexLock(RWStaticCtor)
         RWTHRTHROWSNONE;

      RWMutexLock(RWCancellationState state=RW_CANCELLATION_DISABLED)
         RWTHRTHROWS2(RWTHRResourceLimit,
                      RWTHRInternalError);

      // Destructor

      ~RWMutexLock(void)
         RWTHRTHROWS1(RWTHRInternalError);


      // Block until the mutex is released, acquire it, and continue
      void
      acquire(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // True timed-acquisition is not supported on all platforms!
      // OS/2 and Win32 support it.
      // Solaris and POSIX do not - if the mutex cannot be acquired
      // immediately, then this function returns RW_THR_TIMEOUT.
      RWWaitStatus
      acquire(unsigned long milliseconds)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      void
      acquireRead(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      // True timed-acquisition is not supported on all platforms!
      // OS/2 and Win32 support it.
      // Solaris and POSIX do not - if the mutex cannot be acquired
      // immediately, then this function returns RW_THR_TIMEOUT.
      RWWaitStatus
      acquireRead(unsigned long milliseconds)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      void
      acquireWrite(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      // True timed-acquisition is not supported on all platforms!
      // OS/2 and Win32 support it.
      // Solaris and POSIX do not - if the mutex cannot be acquired
      // immediately, then this function returns RW_THR_TIMEOUT.
      RWWaitStatus
      acquireWrite(unsigned long milliseconds)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

#  if defined(RW_MULTI_THREAD)

      RWMutexLockRep*
      getMutexLockRep(void) const
         RWTHRTHROWSNONE;

#     if defined(RW_THR_DEBUG)

      // Members used to specify debug-mode assertions in other classes.

      // Determine whether calling thread currently owns mutex
      RWBoolean
      isAcquired(void) const
         RWTHRTHROWSANY;

#     endif

#  endif


      // Release the mutex
      void
      release(void)
         RWTHRTHROWS1(RWTHRInternalError);

      // Try to acquire mutex without blocking, return TRUE for success
      RWBoolean
      tryAcquire(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWBoolean
      tryAcquireRead(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWBoolean
      tryAcquireWrite(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Predefined types for compatible guards...

      typedef RWLockGuard<RWMutexLock>          LockGuard;
      typedef RWReadLockGuard<RWMutexLock>      ReadLockGuard;
      typedef RWWriteLockGuard<RWMutexLock>     WriteLockGuard;

      typedef RWTryLockGuard<RWMutexLock>       TryLockGuard;
      typedef RWTryReadLockGuard<RWMutexLock>   TryReadLockGuard;
      typedef RWTryWriteLockGuard<RWMutexLock>  TryWriteLockGuard;

      typedef RWUnlockGuard<RWMutexLock>        UnlockGuard;
      typedef RWReadUnlockGuard<RWMutexLock>    ReadUnlockGuard;
      typedef RWWriteUnlockGuard<RWMutexLock>   WriteUnlockGuard;

   private:

#  if defined(RW_THR_DEBUG) && defined(RW_MULTI_THREAD) && \
      (defined(RW_THR_THREAD_API_SOLARIS) || defined(RW_THR_THREAD_API_POSIX))

      // For use with condition variables under solaris and POSIX:

      void
      acquireId(void)
         RWTHRTHROWSANY;

      void
      releaseId(void)
         RWTHRTHROWSANY;

#  endif

#  if defined(RW_MULTI_THREAD)
      // Initialize the RWMutexLock instance
      // (separate routine required to support static instance initialization)
      void
      init(void)
         RWTHRTHROWS2(RWTHRResourceLimit,
                      RWTHRInternalError);
#  endif

      // Prohibit automatic compiler generation of these member functions...

      RWMutexLock(const RWMutexLock& second);

      RWMutexLock&
      operator=(const RWMutexLock& second);

};

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file and used by the library
template class RWTHRIExport RWGuardBase<RWMutexLock>;
template class RWTHRIExport RWLockGuardBase<RWMutexLock>;
template class RWTHRIExport RWLockGuard<RWMutexLock>;
template class RWTHRIExport RWUnlockGuard<RWMutexLock>;
#  endif

/*****************************************************************************/

class RWTHRExport RWNullMutexLock :
   public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

   public:

      // Constructor

      RWNullMutexLock(RWStaticCtor)
         RWTHRTHROWSNONE;

      RWNullMutexLock(RWCancellationState state=RW_CANCELLATION_DISABLED)
         RWTHRTHROWSNONE;

      // Methods

      void
      acquire(void)
         RWTHRTHROWSNONE;

      RWWaitStatus
      acquire(unsigned long milliseconds)
         RWTHRTHROWSNONE;

      void
      acquireRead(void)
         RWTHRTHROWSNONE;

      // Provide compatibility with Read/Write locks
      RWWaitStatus
      acquireRead(unsigned long milliseconds)
         RWTHRTHROWSNONE;

      void
      acquireWrite(void)
         RWTHRTHROWSNONE;

      // Provide compatibility with Read/Write locks
      RWWaitStatus
      acquireWrite(unsigned long milliseconds)
         RWTHRTHROWSNONE;


#  if defined(RW_MULTI_THREAD)
#     if defined(RW_THR_DEBUG)

      // Members used to specify debug-mode assertions in other classes.

      // Determine whether calling thread currently owns mutex
      RWBoolean
      isAcquired(void) const
         RWTHRTHROWSNONE;

#     endif
#  endif

      void
      release(void)
         RWTHRTHROWSNONE;

      RWBoolean
      tryAcquire(void)
         RWTHRTHROWSNONE;

      RWBoolean
      tryAcquireRead(void)
         RWTHRTHROWSNONE;

      RWBoolean
      tryAcquireWrite(void)
         RWTHRTHROWSNONE;

      typedef RWLockGuard<RWNullMutexLock>          LockGuard;
      typedef RWReadLockGuard<RWNullMutexLock>      ReadLockGuard;
      typedef RWWriteLockGuard<RWNullMutexLock>     WriteLockGuard;

      typedef RWTryLockGuard<RWNullMutexLock>       TryLockGuard;
      typedef RWTryReadLockGuard<RWNullMutexLock>   TryReadLockGuard;
      typedef RWTryWriteLockGuard<RWNullMutexLock>  TryWriteLockGuard;

      typedef RWUnlockGuard<RWNullMutexLock>        UnlockGuard;
      typedef RWReadUnlockGuard<RWNullMutexLock>    ReadUnlockGuard;
      typedef RWWriteUnlockGuard<RWNullMutexLock>   WriteUnlockGuard;

   private:

      // Prohibit automatic compiler generation of these member functions...

      RWNullMutexLock(const RWNullMutexLock& second);

      RWNullMutexLock&
      operator=(const RWNullMutexLock& second);

};


#  if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file and used by the library...
template class RWTHRIExport RWGuardBase<RWNullMutexLock>;
template class RWTHRIExport RWLockGuardBase<RWNullMutexLock>;
template class RWTHRIExport RWLockGuard<RWNullMutexLock>;
template class RWTHRIExport RWUnlockGuard<RWNullMutexLock>;
#  endif

/*****************************************************************************/

// Static mutex objects cannot be initialized with a cancellation state
// and default to cancellation disabled...
// Provide compatibility with Read/Write locks
inline
void
RWMutexLock::acquireRead(void)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWMutexLock,acquireRead(void):void);
   acquire();
}

// Provide compatibility with Read/Write locks
inline
void
RWMutexLock::acquireWrite(void)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWMutexLock,acquireWrite(void):void);
   acquire();
}

// Provide compatibility with Read/Write locks
inline
RWWaitStatus
RWMutexLock::acquireRead(unsigned long milliseconds)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWMutexLock,acquireRead(unsigned long):RWWaitStatus);
   return acquire(milliseconds);
}

// Provide compatibility with Read/Write locks
inline
RWWaitStatus
RWMutexLock::acquireWrite(unsigned long milliseconds)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWMutexLock,acquireWrite(void):RWWaitStatus);
   return acquire(milliseconds);
}

// Provide compatibility with Read/Write locks
inline
RWBoolean
RWMutexLock::tryAcquireRead(void)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWMutexLock,tryAcquireRead(void):RWBoolean);
   return tryAcquire();
}

// Provide compatibility with Read/Write locks
inline
RWBoolean
RWMutexLock::tryAcquireWrite(void)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWMutexLock,tryAcquireWrite(void):RWBoolean);
   return tryAcquire();
}

#  if defined(RW_MULTI_THREAD)
inline
RWMutexLockRep*
RWMutexLock::getMutexLockRep(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMutexLock,getMutexLockRep(void) const:RWMutexLockRep*);
   return &(RW_THR_CONST_CAST(RWMutexLock*, this)->lock_);
}
#  endif

/*****************************************************************************/

inline
void
RWNullMutexLock::acquire(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,acquire(void):void);
}

inline
RWWaitStatus
RWNullMutexLock::acquire(unsigned long)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,acquire(unsigned long):RWWaitStatus);
   return RW_THR_ACQUIRED;
}

inline
void
RWNullMutexLock::acquireRead(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,acquireRead(void):void);
}

inline
RWWaitStatus
RWNullMutexLock::acquireRead(unsigned long)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,acquireRead(unsigned long):RWWaitStatus);
   return RW_THR_ACQUIRED;
}

inline
void
RWNullMutexLock::acquireWrite(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,acquireWrite(void):void);
}

inline
RWWaitStatus
RWNullMutexLock::acquireWrite(unsigned long)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,acquireWrite(unsigned long):RWWaitStatus);
   return RW_THR_ACQUIRED;
}

#if defined(RW_THR_DEBUG) && defined(RW_MULTI_THREAD)

inline
RWBoolean
RWNullMutexLock::isAcquired(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,isAcquired(void) const:int);
   return TRUE;
}

#endif

inline
void
RWNullMutexLock::release(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,release(void));
}

inline
RWNullMutexLock::RWNullMutexLock(RWStaticCtor)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,RWNullMutexLock(RWStaticCtor));
}

inline
RWNullMutexLock::RWNullMutexLock(RWCancellationState)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,RWNullMutexLock);
}

inline
RWBoolean
RWNullMutexLock::tryAcquire(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,tryAcquire(void):RWBoolean);
   return TRUE;
}

inline
RWBoolean
RWNullMutexLock::tryAcquireRead(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,tryAcquireRead(void):RWBoolean);
   return TRUE;
}

inline
RWBoolean
RWNullMutexLock::tryAcquireWrite(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWNullMutexLock,tryAcquireWrite(void):RWBoolean);
   return TRUE;
}


/*****************************************************************************/

#  if !defined(__RWTHRCANCEL_H__)
#     if !defined(__RWTHRHANDBODY_H__)
#        include <rw/thr/cancel.h>
#     endif
#  endif


#endif  // __RWTHRMUTEX_H__


