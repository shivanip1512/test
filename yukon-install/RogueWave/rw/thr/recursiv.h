#if !defined(__RWTHRRECURSIV_H__)
#  define __RWTHRRECURSIV_H__
/*****************************************************************************
 *
 * recursiv.h
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

recursiv.h - Class declarations for:
         
   RWRecursiveLock<Lock> - Adds recursive acquisition semantics to type Mutex.

   NOTE: At present, this template may only be instantiated using the mutex 
         classes RWMutexLock, and RWFIFOMutexLock.

See Also:

   recursiv.cc  - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRSYNCH_H__)
#     include <rw/thr/synch.h>
#  endif

#  if !defined(__RWTHRTHREADID_H__)
#     include <rw/thr/threadid.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

template <class Mutex>
class RWTHRTExport RWRecursiveLock :
   public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

   // Member Variables
   protected:

      // The actual recursive lock (protected to allow access by derived classes)
      Mutex          lock_;

   private:
      
      // To ensure mutually exclusive access to the level_ and threadID_ members
      RWMutexLock    mutex_;

      // The current owner of the lock
      RWThreadId     threadId_;

      // The recursive nesting level
      int            level_;

   public:

      RWRecursiveLock(RWCancellationState state=RW_CANCELLATION_DISABLED) 
         RWTHRTHROWS2(RWTHRResourceLimit,
                      RWTHRInternalError);

      ~RWRecursiveLock(void)
         RWTHRTHROWS1(RWTHRInternalError);

      // Acquire the recursive lock, incrementing the nesting level
      // for each time the lock owner calls this method
      void 
      acquire(void) 
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Acquire the recursive lock, incrementing the nesting level
      // for each time the lock owner calls this method, but timing-out
      // if force to wait for the mutex longer the the specified 
      // number of milliseconds.
      RWWaitStatus 
      acquire(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      void 
      acquireRead(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWWaitStatus
      acquireRead(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      void 
      acquireWrite(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWWaitStatus 
      acquireWrite(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

#  if defined(RW_THR_DEBUG)
      RWBoolean
      isAcquired(void) const
         RWTHRTHROWSANY;
#  endif

      // Release the recursive lock, decrementing the nesting level
      // each time the lock owner calls this method, and releasing the
      // actual lock when the level reaches zero.
      void
      release(void)
         RWTHRTHROWS2(RWTHRIllegalUsage,     // Thread attempted to release lock it does not own - Mutex not released!
                      RWTHRInternalError);

      // Conditionally acquire the recursive lock, incrementing the nesting level
      // for each time the lock owner calls this method.  This method returns
      // immediately if the lock is unavailable.
      RWBoolean
      tryAcquire(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWBoolean 
      tryAcquireRead(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWBoolean 
      tryAcquireWrite(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      typedef RWLockGuard< RWRecursiveLock< Mutex > >          LockGuard;
      typedef RWReadLockGuard< RWRecursiveLock< Mutex > >      ReadLockGuard;
      typedef RWWriteLockGuard< RWRecursiveLock< Mutex > >     WriteLockGuard;

      typedef RWTryLockGuard< RWRecursiveLock< Mutex > >       TryLockGuard;
      typedef RWTryReadLockGuard< RWRecursiveLock< Mutex > >   TryReadLockGuard;
      typedef RWTryWriteLockGuard< RWRecursiveLock< Mutex > >  TryWriteLockGuard;
   
      typedef RWUnlockGuard< RWRecursiveLock< Mutex > >        UnlockGuard;
      typedef RWReadUnlockGuard< RWRecursiveLock< Mutex > >    ReadUnlockGuard;
      typedef RWWriteUnlockGuard< RWRecursiveLock< Mutex > >   WriteUnlockGuard;

      //These typedefs came with the first release of Threads.h++ but they do
      //not follow the naming convention for similar typedefs in the other 
      //synchronization classes.  Therefore these names are now deprecated.
      typedef RWTryLockGuard< RWRecursiveLock< Mutex > >       TryGuard;
      typedef RWTryReadLockGuard< RWRecursiveLock< Mutex > >   TryReadGuard;
      typedef RWTryWriteLockGuard< RWRecursiveLock< Mutex > >  TryWriteGuard;
  
   private:

      // Prohibit automatic compiler generation of these members...

      RWRecursiveLock(const RWRecursiveLock<Mutex>& second);

      RWRecursiveLock<Mutex>& 
      operator=(const RWRecursiveLock<Mutex>& second);

};

/*****************************************************************************/

template <class Mutex>
inline
RWRecursiveLock<Mutex>::~RWRecursiveLock(void)
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,~RWRecursiveLock(void));
}

// Provide compatibility with Read/Write locks
template <class Mutex>
inline
void 
RWRecursiveLock<Mutex>::acquireRead(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,acquire(void):void);
   acquire();
}

      // Provide compatibility with Read/Write locks
template <class Mutex>
inline
RWWaitStatus 
RWRecursiveLock<Mutex>::acquireRead(unsigned long milliseconds)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,acquireRead(unsigned long):RWWaitStatus);
   return acquire(milliseconds);
}

      // Provide compatibility with Read/Write locks
template <class Mutex>
inline
void 
RWRecursiveLock<Mutex>::acquireWrite(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,acquireWrite(void):void);
   acquire();
}

      // Provide compatibility with Read/Write locks
template <class Mutex>
inline
RWWaitStatus 
RWRecursiveLock<Mutex>::acquireWrite(unsigned long milliseconds)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,acquireWrite(unsigned long):RWWaitStatus);
   return acquire(milliseconds);
}

// Provide compatibility with Read/Write locks
template <class Mutex>
inline
RWBoolean 
RWRecursiveLock<Mutex>::tryAcquireRead(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,tryAcquireRead(void):RWBoolean);
   return tryAcquire();
}

// Provide compatibility with Read/Write locks
template <class Mutex>
inline
RWBoolean 
RWRecursiveLock<Mutex>::tryAcquireWrite(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,tryAcquireWrite(void):RWBoolean);
   return tryAcquire();
}

// Implement prohibited function function to allow explicit template instantiation without errors
template <class Mutex>
inline
RWRecursiveLock<Mutex>::RWRecursiveLock(const RWRecursiveLock<Mutex>&)
{
   RWTHRASSERT(0); // USAGE ERROR - Dummy copy constructor called!
}

// Implement prohibited function to allow explicit template instantiation without errors
template <class Mutex>
inline
RWRecursiveLock<Mutex>& 
RWRecursiveLock<Mutex>::operator=(const RWRecursiveLock<Mutex>&)
{
   RWTHRASSERT(0); // USAGE ERROR - Dummy assignment operator called!
   return *this;
}


#  if !defined(__RWTHRCANCEL_H__)
#     include <rw/thr/cancel.h>
#  endif


#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/recursiv.cc>
#  endif

#endif // __ RWTHRRECURSIV_H__

