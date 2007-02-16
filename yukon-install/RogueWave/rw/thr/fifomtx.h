#if !defined(__RWTHRFIFOMTX_H__)
#  define __RWTHRFIFOMTX_H__
/*****************************************************************************
 *
 * fifomtx.h
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

fifomtx.h - Class declarations for:
   
   RWFIFOMutexLock - A mutex that guarantees a FIFO acquisition order 
                     reqardless of thread priority (as opposed to the 
                     RWMutexLock which neither imposes nor defines any 
                     acquisition ordering).
         
See Also:

   fifomtx.cpp - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRSYNCH_H__)
#     include <rw/thr/synch.h>
#  endif

#  if !defined(__RWTHRCONDTION_H__)
#     include <rw/thr/condtion.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

#  if !defined(__RWTHRTHREADID_H__)
#     include <rw/thr/threadid.h> 
#  endif

#  if !defined(__RWTVSLIST_H__)
#     include <rw/tvslist.h>
#  endif

class RWTHRExport RWFIFOMutexLock :
   public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

   private:

      RWMutexLock                lock_;
      RWCondition                waiters_;
      RWTValSlist<RWThreadId>    queue_;

   // Member Functions
   public:

      RWFIFOMutexLock(RWCancellationState state=RW_CANCELLATION_DISABLED)
         RWTHRTHROWSANY; 

      ~RWFIFOMutexLock(void)
         RWTHRTHROWSANY; 

      void 
      acquire(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      RWWaitStatus 
      acquire(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

#  if defined(RW_THR_DEBUG)
      
      // Members used to specify debug-mode assertions in other classes.
   
      // Determine whether calling thread currently owns mutex
      RWBoolean
      isAcquired(void) const
         RWTHRTHROWSANY;

#  endif

      void 
      release(void)
         RWTHRTHROWS1(RWTHRInternalError);
 
      // Provide compatibility with Read/Write locks
      void 
      acquireRead(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      void 
      acquireWrite(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWWaitStatus 
      acquireRead(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWWaitStatus 
      acquireWrite(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      RWBoolean 
      tryAcquire(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      RWBoolean 
      tryAcquireRead(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with Read/Write locks
      RWBoolean 
      tryAcquireWrite(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      typedef RWLockGuard<RWFIFOMutexLock>        LockGuard;
      typedef RWLockGuard<RWFIFOMutexLock>        ReadLockGuard;
      typedef RWLockGuard<RWFIFOMutexLock>        WriteLockGuard;

      typedef RWTryLockGuard<RWFIFOMutexLock>     TryLockGuard;
      typedef RWTryLockGuard<RWFIFOMutexLock>     TryReadLockGuard;
      typedef RWTryLockGuard<RWFIFOMutexLock>     TryWriteLockGuard;

      typedef RWUnlockGuard<RWFIFOMutexLock>      UnlockGuard;
      typedef RWUnlockGuard<RWFIFOMutexLock>      ReadUnlockGuard;
      typedef RWUnlockGuard<RWFIFOMutexLock>      WriteUnlockGuard;

   private:

      // Prohibit copy construction and assigment of Mutex Objects

      RWFIFOMutexLock(const RWFIFOMutexLock& second);

      RWFIFOMutexLock& 
      operator=(const RWFIFOMutexLock& second);

};

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
template class RWTHRIExport RWGuardBase<RWFIFOMutexLock>;
template class RWTHRIExport RWLockGuardBase<RWFIFOMutexLock>;
template class RWTHRIExport RWLockGuard<RWFIFOMutexLock>;
template class RWTHRIExport RWTryLockGuard<RWFIFOMutexLock>;
template class RWTHRIExport RWUnlockGuard<RWFIFOMutexLock>;
#  endif

/*****************************************************************************/

inline
RWFIFOMutexLock::RWFIFOMutexLock(RWCancellationState state)
   RWTHRTHROWSANY
   :
      RWSynchObject(state),
      waiters_(lock_)
{
   RWTHRTRACEMF(RWFIFOMutexLock,RWFIFOMutexLock(RWCancellationState));
}

inline
RWFIFOMutexLock::~RWFIFOMutexLock(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWFIFOMutexLock,~RWFIFOMutexLock(void));
}

// Provide compatibility with Read/Write locks
inline
void 
RWFIFOMutexLock::acquireRead(void)
    RWTHRTHROWS2(RWCancellation,
                 RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWFIFOMutexLock,acquire(void):void);
   acquire(); 
}

// Provide compatibility with Read/Write locks
inline
void 
RWFIFOMutexLock::acquireWrite(void)
    RWTHRTHROWS2(RWCancellation,
                 RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWFIFOMutexLock,acquireWrite(void):void);
   acquire(); 
}

// Provide compatibility with Read/Write locks
inline
RWWaitStatus 
RWFIFOMutexLock::acquireRead(unsigned long milliseconds)
    RWTHRTHROWS2(RWCancellation,
                 RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWFIFOMutexLock,acquireRead(unsigned long):RWWaitStatus);
   return acquire(milliseconds); 
}

// Provide compatibility with Read/Write locks
inline
RWWaitStatus 
RWFIFOMutexLock::acquireWrite(unsigned long milliseconds)
    RWTHRTHROWS2(RWCancellation,
                 RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWFIFOMutexLock,acquireWrite(unsigned long):RWWaitStatus);
   return acquire(milliseconds); 
}

// Provide compatibility with Read/Write locks
inline
RWBoolean 
RWFIFOMutexLock::tryAcquireRead(void)
    RWTHRTHROWS2(RWCancellation,
                 RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWFIFOMutexLock,tryAcquireRead(void):RWBoolean);
   return tryAcquire(); 
}

// Provide compatibility with Read/Write locks
inline
RWBoolean 
RWFIFOMutexLock::tryAcquireWrite(void)
    RWTHRTHROWS2(RWCancellation,
                 RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWFIFOMutexLock,tryAcquireWrite(void):RWBoolean);
   return tryAcquire(); 
}

// Define this member to prohibit the compiler from generating it automatically...
inline
RWFIFOMutexLock::RWFIFOMutexLock(const RWFIFOMutexLock&)
   :
      waiters_(lock_)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation - shouldn't get here!
}

// Define this member to prohibit the compiler from generating it automatically...
inline
RWFIFOMutexLock& 
RWFIFOMutexLock::operator=(const RWFIFOMutexLock&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation - shouldn't get here!
   return *this;
}


#  if !defined(__RWTHRCANCEL_H__)
#     include <rw/thr/cancel.h>
#  endif

#endif // __RWTHRFIFOMTX_H__

