#if !defined(__RWTHRRWLOCK_H__)
#  define __RWTHRRWLOCK_H__
/*****************************************************************************
 *
 * rwlock.h
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

rwlock.h - Class declarations for:
         
   RWReadersWriterLock - Readers/Writer mutex lock which favors writers over
                         readers.

See Also:

   rwlock.cpp - Out-of-line function definitions.

*****************************************************************************/


#  if !defined(__RWTHRSYNCH_H__)
#     include <rw/thr/synch.h>
#  endif

#  if !defined(__RWTHRGUARD_H__)
#     include <rw/thr/guard.h>
#  endif

#     if defined(RW_THR_DEBUG)
//       Use tools mutex in debug implementation
#        if !defined(__RWTHRTHREADID_H__)
#           include <rw/thr/threadid.h>
#        endif
#        if !defined(__RWTVSLIST_H__)
#           include <rw/tvslist.h>
#        endif
#        if !defined(__RWMUTEX_H__)
#           include <rw/mutex.h>
#        endif
#     endif

#  if defined(RW_THR_THREAD_API_OS2) || \
        defined(RW_THR_THREAD_API_POSIX) || \
        defined(RW_THR_THREAD_API_SOLARIS) || \
        defined(RW_THR_THREAD_API_WIN32)
#     if !defined(__RWTHRMUTEX_H__)
#        include <rw/thr/mutex.h>
#     endif
#     if !defined(__RWTHRCONDTION_H__)
#        include <rw/thr/condtion.h>
#     endif

#if 0 // rw_tryrdlock() is broken on Solaris, emulate rw lock.
//#  elif defined(RW_THR_THREAD_API_SOLARIS)
//#     if !defined(__RW_SYNCH_H__)
//#        define __RW_SYNCH_H__ <synch.h>
//#        include __RW_SYNCH_H__
//#     endif
//// Solaris has an underlying data type that can be accessed...
//typedef rwlock_t  RWReadersWriterLockRep;
#endif //#if 0  // rw_tryrdlock() is broken on Solaris, emulate rw lock.

#  else
#     error Class RWReadersWriterLock is not supported in this environment!
#  endif


class RWTHRExport RWReadersWriterLock :
   public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

   private:
      
#  if defined(RW_THR_THREAD_API_OS2) || \
        defined(RW_THR_THREAD_API_POSIX) || \
        defined(RW_THR_THREAD_API_SOLARIS) || \
        defined(RW_THR_THREAD_API_WIN32)

      RWMutexLock lock_;
      RWCondition readers_;
      RWCondition writers_;
      int         numReaders_;
      int         numWriters_;
      int         count_;

#if 0  // rw_tryrdlock() is broken on Solaris, emulate rw lock.
//#  elif defined(RW_THR_THREAD_API_SOLARIS)
//
//      RWReadersWriterLockRep  rwlock_;
//
#endif  //#if 0  // rw_tryrdlock() is broken on Solaris, emulate rw lock.

#endif

#     if defined(RW_THR_DEBUG) 
      // Debug implementation members
      RWMutex                 mutex_;
      RWTValSlist<RWThreadId> threadIds_;
#     endif

   // Member Functions
   public:
      
      RWReadersWriterLock(RWCancellationState state=RW_CANCELLATION_DISABLED) 
         RWTHRTHROWS1(RWTHRInternalError); 
      
      ~RWReadersWriterLock(void)
         RWTHRTHROWS1(RWTHRInternalError);

      // Provide compatibility with simple mutex locks
      void 
      acquire(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      void 
      acquireRead(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      void 
      acquireWrite(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with simple mutex locks
      RWWaitStatus 
      acquire(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      RWWaitStatus 
      acquireRead(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      RWWaitStatus 
      acquireWrite(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Provide compatibility with simple mutex locks
      RWBoolean 
      tryAcquire(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      RWBoolean 
      tryAcquireRead(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      RWBoolean 
      tryAcquireWrite(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      void 
      release(void)
         RWTHRTHROWS1(RWTHRInternalError);

#if 0  // rw_tryrdlock() is broken on Solaris, emulate rw lock.
//#  if defined(RW_THR_THREAD_API_SOLARIS)
//      RWReadersWriterLockRep* 
//      getReadersWriterLockRep(void) const 
//         RWTHRTHROWSNONE;
//#  endif
#endif  //#if 0  // rw_tryrdlock() is broken on Solaris, emulate rw lock.

      typedef RWLockGuard<RWReadersWriterLock>         LockGuard;
      typedef RWReadLockGuard<RWReadersWriterLock>     ReadLockGuard;
      typedef RWWriteLockGuard<RWReadersWriterLock>    WriteLockGuard;

      typedef RWTryLockGuard<RWReadersWriterLock>      TryLockGuard;
      typedef RWTryReadLockGuard<RWReadersWriterLock>  TryReadLockGuard;
      typedef RWTryWriteLockGuard<RWReadersWriterLock> TryWriteLockGuard;
      
      typedef RWUnlockGuard<RWReadersWriterLock>       UnlockGuard;
      typedef RWReadUnlockGuard<RWReadersWriterLock>   ReadUnlockGuard;
      typedef RWWriteUnlockGuard<RWReadersWriterLock>  WriteUnlockGuard;

   private:
      
      // Prohibit copy construction and assigment of RWReadersWriterLock Objects

      RWReadersWriterLock(const RWReadersWriterLock& second);

      RWReadersWriterLock& 
      operator=(const RWReadersWriterLock& second);

};

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
template class RWTHRIExport RWGuardBase<RWReadersWriterLock>;
template class RWTHRIExport RWLockGuardBase<RWReadersWriterLock>;
template class RWTHRIExport RWLockGuard<RWReadersWriterLock>;
template class RWTHRIExport RWTryLockGuard<RWReadersWriterLock>;
template class RWTHRIExport RWUnlockGuard<RWReadersWriterLock>;
template class RWTHRIExport RWReadGuardBase<RWReadersWriterLock>;
template class RWTHRIExport RWReadLockGuardBase<RWReadersWriterLock>;
template class RWTHRIExport RWReadLockGuard<RWReadersWriterLock>;
template class RWTHRIExport RWTryReadLockGuard<RWReadersWriterLock>;
template class RWTHRIExport RWReadUnlockGuard<RWReadersWriterLock>;
template class RWTHRIExport RWWriteGuardBase<RWReadersWriterLock>;
template class RWTHRIExport RWWriteLockGuardBase<RWReadersWriterLock>;
template class RWTHRIExport RWWriteLockGuard<RWReadersWriterLock>;
template class RWTHRIExport RWTryWriteLockGuard<RWReadersWriterLock>;
template class RWTHRIExport RWWriteUnlockGuard<RWReadersWriterLock>;
#  endif

/*****************************************************************************/

inline
void 
RWReadersWriterLock::acquire(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWReadersWriterLock,acquire(void):void);
   acquireWrite(); 
} 

inline
RWWaitStatus 
RWReadersWriterLock::acquire(unsigned long milliseconds)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWReadersWriterLock,acquire(unsigned long):RWWaitStatus);
   return acquireWrite(milliseconds); 
}

inline
RWBoolean 
RWReadersWriterLock::tryAcquire(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{ 
   RWTHRTRACEMF(RWReadersWriterLock,tryAcquire(void):RWBoolean);
   return tryAcquireWrite(); 
}

#if 0  // rw_tryrdlock() is broken on Solaris, emulate rw lock.
//#  if defined(RW_THR_THREAD_API_SOLARIS)
//inline
//RWReadersWriterLockRep*
//RWReadersWriterLock::getReadersWriterLockRep(void) const 
//   RWTHRTHROWSNONE
//{ 
//   RWTHRTRACEMF(RWReadersWriterLock,
//         getReadersWriterLockRep(void):RWReadersWriterLockRep*);
//
//   return &(RW_THR_CONST_CAST(RWReadersWriterLock*,this)->rwlock_);
//}
//#  endif
#endif  //#if 0  // rw_tryrdlock() is broken on Solaris, emulate rw lock.


#  if !defined(__RWTHRCANCEL_H__)
#     include <rw/thr/cancel.h>
#  endif

#endif  // __RWTHRRWLOCK_H__
