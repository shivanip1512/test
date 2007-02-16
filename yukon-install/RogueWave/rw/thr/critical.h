#if !defined(__RWTHRCRITICAL_H__)
#  define __RWTHRCRITICAL_H__
/*****************************************************************************
 *
 * critical.h
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

critical.h - Class declarations for:

   RWCriticalSection

See Also:

   critical.cpp - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRSYNCH_H__)
#     include <rw/thr/synch.h>
#  endif

#  if !defined(__RWTHRCANCEL_H__)
#     include <rw/thr/cancel.h>
#  endif


#  if defined(RW_THR_THREAD_API_POSIX)   || \
      defined(RW_THR_THREAD_API_SOLARIS) || \
      defined(RW_THR_THREAD_API_OS2)

// Solaris, POSIX, OS/2 implementations don't support WIN32-style critical 
// sections, so use an RWMutexLock. OS/2 has something called "critical 
// sections" but semantically they are very different than the WIN32 variety.
// Basically, they stop all threads in the current process except for the 
// thread that entered the critical section --- seems a bit extreme to our 
// sensibilities, thus we have elected not to encapsulate them here.

#     if !defined(__RWTHRMUTEX_H__)
#        include <rw/thr/mutex.h>
#     endif

typedef RWMutexLockRep  RWCriticalSectionRep;

class RWTHRExport RWCriticalSection : public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

   protected:
      RWMutexLock   lock_;

   public:
      RWCriticalSection(RWCancellationState state=RW_CANCELLATION_DISABLED)
         RWTHRTHROWS1(RWTHRInternalError);

      RWCriticalSectionRep*
      getCriticalSectionRep(void) const
         RWTHRTHROWSNONE;

      // Block until the critical section is available
      void
      acquire(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError) { lock_.acquire(); }

      // Release the critical section
      void
      release(void)
         RWTHRTHROWS1(RWTHRInternalError) { lock_.release(); }

      // Provide compatibility with Read/Write locks
      void
      acquireRead(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError) { acquire(); }

      // Provide compatibility with Read/Write locks
      void
      acquireWrite(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError) { acquire(); }

      typedef RWLockGuard<RWCriticalSection>     LockGuard;
      typedef RWLockGuard<RWCriticalSection>     ReadLockGuard;
      typedef RWLockGuard<RWCriticalSection>     WriteLockGuard;
      typedef RWUnlockGuard<RWCriticalSection>   UnlockGuard;
      typedef RWUnlockGuard<RWCriticalSection>   ReadUnlockGuard;
      typedef RWUnlockGuard<RWCriticalSection>   WriteUnlockGuard;
};

#     elif defined(RW_THR_THREAD_API_WIN32)

// WIN32 supports native "critical sections"

#     if !defined(__RWTHRGUARD_H__)
#        include <rw/thr/guard.h>
#     endif

#        if !defined(__RW_WINDOWS_H__)
#           define __RW_WINDOWS_H__ <windows.h>
#           include __RW_WINDOWS_H__
#        endif

/*
 * WIN32's critical section is a pair of calls that operate on a
 * critical section structure in memory.
 *
 * Win32 critical sections are implemented as "spin-locks" and are best used
 * when there is little contention for the critical section otherwise blocked
 * threads simply spin for the duration of their time-slice.
 */

typedef CRITICAL_SECTION RWCriticalSectionRep;

class RWTHRExport RWCriticalSection :
   public RWSynchObject {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWCriticalSectionRep   lock_;

   public:
      // Create and initialize the critical section
      RWCriticalSection(RWCancellationState state=RW_CANCELLATION_DISABLED)
         RWTHRTHROWSNONE;

      // Destroy the critical section
      ~RWCriticalSection(void)
         RWTHRTHROWSNONE;

      // Block until the critical section is available
      void
      acquire(void)
         RWTHRTHROWS2(RWCancellation,
                      RWTHRInternalError);

      // Release the critical section
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

      // Give access to underlying mechanism 
      RWCriticalSectionRep*
      getCriticalSectionRep(void) const
         RWTHRTHROWSNONE;

      typedef RWLockGuard<RWCriticalSection>     LockGuard;
      typedef RWLockGuard<RWCriticalSection>     ReadLockGuard;
      typedef RWLockGuard<RWCriticalSection>     WriteLockGuard;

      typedef RWUnlockGuard<RWCriticalSection>   UnlockGuard;
      typedef RWUnlockGuard<RWCriticalSection>   ReadUnlockGuard;
      typedef RWUnlockGuard<RWCriticalSection>   WriteUnlockGuard;

};

#     if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file...
template class RWTHRIExport RWGuardBase<RWCriticalSection>;
template class RWTHRIExport RWLockGuardBase<RWCriticalSection>;
template class RWTHRIExport RWLockGuard<RWCriticalSection>;
template class RWTHRIExport RWUnlockGuard<RWCriticalSection>;
#     endif

#  endif


/*****************************************************************************/

#if defined(RW_THR_THREAD_API_POSIX)   || \
    defined(RW_THR_THREAD_API_SOLARIS) || \
    defined(RW_THR_THREAD_API_OS2)

inline
RWCriticalSection::RWCriticalSection(RWCancellationState state)
   RWTHRTHROWS1(RWTHRInternalError)
   :
      lock_(state)
{
   RWTHRTRACEMF(RWCriticalSection,RWCriticalSection(RWCancellationState));
}

#endif

inline
RWCriticalSectionRep*
RWCriticalSection::getCriticalSectionRep(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCriticalSection,
         getCriticalSectionRep(void) const:RWCriticalSectionRep*);

#if defined(RW_THR_THREAD_API_WIN32)
 
   return (RWCriticalSectionRep*)
                  &(RW_THR_CONST_CAST(RWCriticalSection*,this)->lock_);
 
#else

   return (RWCriticalSectionRep*)lock_.getMutexLockRep();
 
#endif
}

#if defined(RW_THR_THREAD_API_WIN32)

inline
RWCriticalSection::RWCriticalSection(RWCancellationState state)
   RWTHRTHROWSNONE
   :
      RWSynchObject(state)
{
   RWTHRTRACEMF(RWCriticalSection,RWCriticalSection(RWCancellationState));

   ::InitializeCriticalSection(&lock_);
}

inline
RWCriticalSection::~RWCriticalSection(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCriticalSection,~RWCriticalSection(void));

   ::DeleteCriticalSection(&lock_);
}

inline
void
RWCriticalSection::acquire(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWCriticalSection,acquire(void):void);

   // Test for cancellation if enabled...
   testCancellation();

   ::EnterCriticalSection(&lock_);
}

// Provide compatibility with Read/Write locks
inline
void
RWCriticalSection::acquireRead(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWCriticalSection,acquireRead(void):void);
   acquire();
}

// Provide compatibility with Read/Write locks
inline
void
RWCriticalSection::acquireWrite(void)
   RWTHRTHROWS2(RWCancellation,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWCriticalSection,acquireWrite(void):void);
   acquire();
}

inline
void
RWCriticalSection::release(void)
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWCriticalSection,release(void):void);

   ::LeaveCriticalSection(&lock_);
}

#  endif

#endif  // __RWTHRCRITICAL_H__

