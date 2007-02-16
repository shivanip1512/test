#if !defined(__RWTHRCONDTION_H__)
#  define __RWTHRCONDTION_H__
/*****************************************************************************
 *
 * condtion.h
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

condtion.h - Class declarations for:
         
   RWCondition - Condition variable 

See Also:

   condtion.cpp  - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRSYNCH_H__)
#     include <rw/thr/synch.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

#  if defined(RW_THR_THREAD_API_OS2) || \
      defined(RW_THR_THREAD_API_WIN32) 
#     if !defined(__RWTVSLIST_H__)
#        include <rw/tvslist.h>
#     endif
#     if !defined(__RWTHRSEMAPHOR_H__)
#        include <rw/thr/semaphor.h>
#     endif

#  elif defined(RW_THR_THREAD_API_POSIX)

//
//    Define internal condition variable representation, RWConditionRep.
//

      typedef pthread_cond_t RWConditionRep;

#  elif defined(RW_THR_THREAD_API_SOLARIS)
#     if !defined(__RW_SYNCH_H__)
#        define __RW_SYNCH_H__ <synch.h>
#        include __RW_SYNCH_H__
#     endif

//
//    Define internal condition variable representation, RWConditionRep.
//

      typedef cond_t RWConditionRep;

#  else
#     error Class RWCondition is not supported in this environment!
#  endif

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated in this header file...
template class RWTHRIExport RWTValSlist<RWSemaphore*>;
#    if defined(RW_NO_STL)
template class RWTHRIExport RWTValSlink<RWSemaphore*>;
template class RWTHRIExport RWTIsvSlist<RWTValSlink<RWSemaphore*> >;
#    endif
#  endif

/*****************************************************************************/

class RWTHRExport RWCondition :
   public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

   protected:

#  if defined(RW_THR_THREAD_API_OS2) || \
      defined(RW_THR_THREAD_API_WIN32) 
      
      // OS2 and Win32 do not provide a condition variable mechanism
      // Threads.h++ uses a list of per-thread semaphores to implement...
      RWTValSlist<RWSemaphore*> waiters_;

#  elif defined(RW_THR_THREAD_API_POSIX) || \
        defined(RW_THR_THREAD_API_SOLARIS)

      RWConditionRep condition_;

#  endif

      // Reference to mutex used to protect condition.
      // The mutex is provided by the condition variable's owner, and must 
      // already be acquired when wait(), signal(), and signalAll() is called.
      RWMutexLock&   mutex_;

   // Member Functions
   public:

      // Create a condition variable that is protected 
      // by the supplied mutex instance.  The mutex given must
      // exist as long as the condition variable exists!
      RWCondition(RWMutexLock& mutex,
                  RWCancellationState state=RW_CANCELLATION_DISABLED)
         RWTHRTHROWS2(RWTHRResourceLimit,
                      RWTHRInternalError);

      ~RWCondition(void)
         RWTHRTHROWSANY;

      // Block until the condition's mutex is released, acquire it, and 
      // continue
      void 
      acquire(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Acquire the mutex associated with the condition variable, and if not
      // available, block for the specified number of milliseconds, or until 
      // the mutex is released, and if released, acquire it, and continue, 
      // otherwise return time-out indication.
      RWWaitStatus 
      acquire(unsigned long milliseconds)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

#  if defined(RW_THR_THREAD_API_POSIX) || \
      defined(RW_THR_THREAD_API_SOLARIS)

      // Give access to underlying mechanism on platforms that have one...
      RWConditionRep* 
      getConditionRep(void) const 
         RWTHRTHROWSNONE;

#  endif

      // Release the mutex associated with the condition variable
      void 
      release(void)
         RWTHRTHROWS1(RWTHRInternalError);         

      // Signal one waiting thread that the condition protected by the 
      // condition variable has changed.
      void 
      signal(void)
         RWTHRTHROWS1(RWTHRInternalError);

      // Signal all waiting threads that the state condition protected by the 
      // condition variable has changed.
      void 
      signalAll(void)
         RWTHRTHROWS1(RWTHRInternalError);

      // Try to acquire the condition's mutex (without blocking), returning
      // TRUE if acquired, or FALSE if the mutex is already owned by 
      // another thread.
      RWBoolean 
      tryAcquire(void)
         RWTHRTHROWS3(RWCancellation,
                      RWTHRResourceLimit,
                      RWTHRInternalError);

      // Wait for a thread to signal that the state condition 
      // protected by the condition variable has changed
      void
      wait(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Wait for a thread to signal that the state condition 
      // protected by the condition variable has changed, or until
      // the specified amount of time elapses.  Return RW_THR_SIGNALED
      // if a thread has signaled this thread, or return RW_THR_TIMEOUT
      // if the specified time elapsed without a signal.
      RWWaitStatus 
      wait(unsigned long milliseconds)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Predefined types for compatible guards...

      typedef RWLockGuard<RWCondition >          LockGuard;
      typedef RWTryLockGuard<RWCondition >       TryLockGuard;
      typedef RWUnlockGuard<RWCondition >        UnLockGuard;

   protected:
      
      // Return a reference to the mutex associated 
      // with this condition variable
      RWMutexLock&
      mutex(void) const;

   private:
      
      // Prohibit automatic generation of these member functions...

      RWCondition(const RWCondition& second);

      RWCondition& operator=(const RWCondition& second);

};


#  if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this class...
template class RWTHRIExport RWGuardBase<RWCondition>;
template class RWTHRIExport RWLockGuardBase<RWCondition>;
template class RWTHRIExport RWLockGuard<RWCondition>;
template class RWTHRIExport RWTryLockGuard<RWCondition>;
template class RWTHRIExport RWUnlockGuard<RWCondition>;
#  endif

/*****************************************************************************/

inline
void 
RWCondition::acquire(void)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWCondition,acquire(void):void);
   mutex_.acquire();
}

inline
RWWaitStatus 
RWCondition::acquire(unsigned long milliseconds)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWCondition,acquire(unsigned long):RWWaitStatus);
   return mutex_.acquire(milliseconds);
}

#  if defined(RW_THR_THREAD_API_POSIX) || \
      defined(RW_THR_THREAD_API_SOLARIS)

// Give access to underlying mechanism on platforms that have one...
inline
RWConditionRep* 
RWCondition::getConditionRep(void) const 
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCondition,getConditionRep(void):const RWConditionRep*);
   return &(RW_THR_CONST_CAST(RWCondition*,this)->condition_);
}

#  endif

inline
void 
RWCondition::release(void)
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWCondition,release(void):void);
   mutex_.release();
}


inline
RWBoolean 
RWCondition::tryAcquire(void)
   RWTHRTHROWS3(RWCancellation,
                RWTHRResourceLimit,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWCondition,tryAcquire(void):RWBoolean);
   return mutex_.tryAcquire();
}

// Return reference to the mutex associated with this condition variable
inline
RWMutexLock&
RWCondition::mutex(void) const
{
   RWTHRTRACEMF(RWCondition,mutex(void) const:RWMutexLock&);
   return RW_THR_CONST_CAST(RWCondition&,*this).mutex_;
}

// Prohibit automatic generation of these member functions...

inline
RWCondition::RWCondition(const RWCondition& second)
   :
      mutex_(second.mutex_)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation - Shouldn't get here!
}

inline
RWCondition& 
RWCondition::operator=(const RWCondition& )
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation - Shouldn't get here!
   return *this;
}


#  if !defined(__RWTHRCANCEL_H__)
#     include <rw/thr/cancel.h>
#  endif

#endif // __RWTHRCONDTION_H__

