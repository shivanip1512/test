#if !defined(__RWTHRRECURSIV_CC__)
#define __RWTHRRECURSIV_CC__
/*****************************************************************************
 *
 * recursiv.cc
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

recursiv.cc  - Out-of-line function definitions for:
         
   RWRecursiveLock<Mutex> - Adds recursive acquisition semantics to type Mutex.

See Also:

   recursiv.h - Class declarations.

*****************************************************************************/

#  if !defined(__RWTHRTHRMSG_H__)
#     include <rw/thr/thrmsg.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWRecursiveLock,Mutex)

template <class Mutex>
RWRecursiveLock<Mutex>::RWRecursiveLock(RWCancellationState state) 
   RWTHRTHROWS2(RWTHRResourceLimit,
                RWTHRInternalError)
   : 
      RWSynchObject(state),
      level_(0)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,RWRecursiveLock(RWCancellationState));
   threadId_.clear();
}

template <class Mutex>
void
RWRecursiveLock<Mutex>::acquire(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,acquire(void):void);
   static const char msgHeader[] = "RWRecursiveLock<Mutex>::acquire - ";

   // Test for cancellation if enabled...
   testCancellation();

   try {
      RWLockGuard<RWMutexLock>  guard(mutex_); // Protect threadId and level
      if (rwIsSelf(threadId_))
         level_++;
      else {
         guard.release();
         lock_.acquire();
         threadId_ = rwThreadId();
         level_ = 0;
      }
   }
   catch(RWxmsg& emsg) {
      throw RWTHRInternalError(RWCString(msgHeader)+emsg.why());
   }
   catch(...) {
      throw RWTHRInternalError(RWCString(msgHeader)+RW_THR_Unexpected_Exception);
   }
}

template <class Mutex>
RWWaitStatus
RWRecursiveLock<Mutex>::acquire(unsigned long milliseconds)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,acquire(unsigned long):RWWaitStatus);
   static const char msgHeader[] = "RWRecursiveLock<Mutex>::acquire - ";

   // Test for cancellation if enabled...
   testCancellation();

   RWWaitStatus status=RW_THR_ACQUIRED;
   try {
      RWLockGuard<RWMutexLock>  guard(mutex_); // Protect threadId and level
      if (rwIsSelf(threadId_))
         level_++;
      else {
         guard.release();
         if (RW_THR_ACQUIRED == (status = lock_.acquire(milliseconds))) {
            threadId_ = rwThreadId();
            level_ = 0;
         }
      }
   }
   catch(RWxmsg& emsg) {
      throw RWTHRInternalError(RWCString(msgHeader)+emsg.why());
   }
   catch(...) {
      throw RWTHRInternalError(RWCString(msgHeader)+RW_THR_Unexpected_Exception);
   }
   return status;
}

#  if defined(RW_THR_DEBUG)
template <class Mutex>
RWBoolean
RWRecursiveLock<Mutex>::isAcquired(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,isAcquired(void):int);
   RWLockGuard<RWMutexLock> guard(RW_THR_CONST_CAST(RWRecursiveLock<Mutex>*,this)->mutex_); // Protect threadId and level

   return ::rwIsSelf(threadId_);
}
#  endif

template <class Mutex>
RWBoolean
RWRecursiveLock<Mutex>::tryAcquire(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,tryAcquire(void):RWBoolean);
   static const char msgHeader[] = "RWRecursiveLock<Mutex>::tryAcquire - ";

   // Test for cancellation if enabled...
   testCancellation();

   RWBoolean result=TRUE;
   try {
      RWLockGuard<RWMutexLock>  guard(mutex_); // Protect threadId and level
      if (rwIsSelf(threadId_))
         level_++;
      else {
         guard.release();
         if (TRUE == (result = lock_.tryAcquire())) {
            threadId_ = rwThreadId();
            level_ = 0;
         }
      }
   }
   catch(RWxmsg emsg) {
      throw RWTHRInternalError(RWCString(msgHeader)+emsg.why());
   }
   catch(...) {
      throw RWTHRInternalError(RWCString(msgHeader)+RW_THR_Unexpected_Exception);
   }
   return result;
}

template <class Mutex>
void
RWRecursiveLock<Mutex>::release(void)
   RWTHRTHROWS2(RWTHRIllegalUsage,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRecursiveLock<Mutex>,release(void):void);
   static const char msgHeader[] = "RWRecursiveLock::release - ";

   try {

      RWLockGuard<RWMutexLock>  guard(mutex_); // Protect threadId and level

      // Only perform release if owned by thread...
      if (rwIsSelf(threadId_)) {
         if (level_ > 0)
            level_--;
         else {
            threadId_.clear();
            guard.release();
            lock_.release();
         }
      }
      else {
         RWTHRASSERT(0); // USAGE ERROR - Failure indicates attempt to release lock that was not acquired by current thread
         throw RWTHRIllegalUsage(RWCString(msgHeader)+RW_THR_Not_Owner);
      }
   }
   catch(RWTHRIllegalUsage&) {
      throw;
   }
   catch(RWxmsg& emsg) {
      throw RWTHRInternalError(RWCString(msgHeader)+emsg.why());
   }
   catch(...) {
      throw RWTHRInternalError(RWCString(msgHeader)+RW_THR_Unexpected_Exception);
   }
}

#endif // __RWTHRRECURSIV_CC__

                                  
