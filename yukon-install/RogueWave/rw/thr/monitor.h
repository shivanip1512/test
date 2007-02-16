#if !defined(__RWTHRMONITOR_H__)
#define __RWTHRMONITOR_H__
/*****************************************************************************
 *
 * monitor.h
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

monitor.h - Class declarations for:

   RWMonitor<Mutex> - Base class for all classes that require monitor-style
                      synchronization semantics.  
                    
   In a monitor class, some member functions are synchronized in such a manner 
   so as to allow only one thread at a time to have access to a monitor class
   instance.  This synchronization is provided including a mutex lock in each
   monitor class instance.  Each monitor member function that is to be 
   sychronized must acquire this lock on entry and release on exit, thereby
   blocking other threads that may attempt to execute the same or other 
   similarly synchronized functions.  RWMonitor provides several typedefs to
   use in acquiring (locking) and releasing (unlocking) the monitor.

   The following code illustrates the use of the RWMonitor class as a base 
   class for another class:

      class MyClass :
         public RWMonitor<RWMutexLock> {

         public:
            void myFunc1(void); // Synchronized Function (Non-atomic)
            void myFunc2(void) const; // Synchronized Function (Atomic)
         private:
            void helper(void) const; // Assumes lock is already acquired!
      }

      void
      MyClass::myFunc1(void)
      {
         // Use a guard to lock the monitor (and automatically unlock at exit)
         LockGuard lock(monitor()); 
         // Do something useful...
         {
            // Temporarily unlock the monitor so we can call another synchronized
            // function on this (or even another) monitor.
            UnlockGuard unlock(monitor());
            // Call another synchronized function
            myFunc2();
         }
      }

      void
      MyClass::myFunc2(void) const
      {
         // Use a guard to lock the monitor (and automatically unlock at exit)
         LockGuard lock(monitor()); 
         helper(); // Call a private helper function
      }

      void 
      MyClass::helper(void)
      {
         // Debug Mode - Make sure the monitor is acquired by this thread!
         RWTHRASSERT(monitor().isAcquired()); 
         // Do something useful...
      }
            
See Also:

   monitor.cc  - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRGUARD_H__)
#     include <rw/thr/guard.h>
#  endif

template <class Mutex>
class RWTHRTExport RWMonitor {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Mutex  MutexType;

   protected:

      Mutex          mutex_;
   
   protected:

      // This class is intended for use as an abstract base class, so the 
      // constructors are protected but is accessible by derived classes.

      // Construct a default instance (initializes the mutex)
      RWMonitor(void)
         RWTHRTHROWSANY;

      // Construct a static instance (does not initialize the mutex; the
      // mutex will be initialized the first time it is accessed)
      RWMonitor(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Define copy-constructor so derived classes can copy-construct, 
      // but initialize the mutex; don't copy it...
      // (mutexes cannot be copied)
      RWMonitor(const RWMonitor<Mutex>& second)
         RWTHRTHROWSNONE;

      // Define an assignment operator so derived classes can do assignments, 
      // but don't assign the mutex, leave it alone... 
      // (mutexes cannot be assigned)
      RWMonitor<Mutex>&
      operator=(const RWMonitor<Mutex>&)
         RWTHRTHROWSNONE;

      // Lock the monitor; acquires the monitor's mutex.
      void
      acquire(void)
         RWTHRTHROWSANY;

#     if defined(RW_THR_DEBUG)

      // Determine whether calling thread currently owns the monitor (mutex)
      // Primarily intended for use in precondition assertions.
      RWBoolean
      isAcquired(void) const
         RWTHRTHROWSANY;

#     endif

      // Cast away const on this and return a reference. Used to lock a 
      // monitor via LockGuard. This is necessary to avoid having to 
      // explicitly cast away const on the this pointer in every const 
      // member function that needs to lock the monitor.
      RWMonitor<Mutex>&
      monitor(void) const
         RWTHRTHROWSANY;

      // Accessor function for the monitor's mutex.
      Mutex&
      mutex(void)
         RWTHRTHROWSNONE;

      // Unlock the monitor; releases the monitor's mutex.
      void
      release(void)
         RWTHRTHROWSANY;

      // Conditionally lock the monitor, but only if it can be locked 
      // without blocking.
      RWBoolean
      tryAcquire(void)
         RWTHRTHROWSANY;

   
   // Give the guard classes access to the acquire and release members.

#  if defined(RW_THR_NO_SELF_REFERENCING_FRIEND_DECLARATIONS)
   
   // Predefine guards for use with monitors.
   typedef RWLockGuard< RWMonitor< Mutex > >     LockGuard;
   typedef RWUnlockGuard< RWMonitor< Mutex > >   UnlockGuard;
   typedef RWTryLockGuard< RWMonitor< Mutex > >  TryLockGuard;

   private:
      // Additional typedefs required for friend declarations
      typedef RWGuardBase< RWMonitor< Mutex > >         GuardBase;
      typedef RWLockGuardBase< RWMonitor< Mutex > >     LockGuardBase;

   friend class GuardBase;
   friend class LockGuardBase;
   friend class LockGuard;
   friend class UnlockGuard;
   friend class TryLockGuard;

#  else

   friend class RWGuardBase< RWMonitor< Mutex > >;
   friend class RWLockGuardBase< RWMonitor< Mutex > >;
   friend class RWLockGuard< RWMonitor< Mutex > >;
   friend class RWTryLockGuard< RWMonitor< Mutex > >;
   friend class RWUnlockGuard< RWMonitor< Mutex > >;

   // Predefine guards for use with monitors.
   typedef RWLockGuard< RWMonitor< Mutex > >     LockGuard;
   typedef RWUnlockGuard< RWMonitor< Mutex > >   UnlockGuard;
   typedef RWTryLockGuard< RWMonitor< Mutex > >  TryLockGuard;

#  endif
   
};

#  if defined(RW_THR_COMPILER_BORLAND_CPP) && !defined(__RWTHRCOUNTPTR_H__)
#     if !defined(__RWTHRMUTEX_H__)
#        include <rw/thr/mutex.h>
#     endif
template class RWTHRIExport RWMonitor<RWMutexLock>;
template class RWTHRIExport RWGuardBase<RWMonitor<RWMutexLock> >;
template class RWTHRIExport RWLockGuardBase<RWMonitor<RWMutexLock> >;
template class RWTHRIExport RWLockGuard<RWMonitor<RWMutexLock> >;
template class RWTHRIExport RWTryLockGuard<RWMonitor<RWMutexLock> >;
template class RWTHRIExport RWUnlockGuard<RWMonitor<RWMutexLock> >;
#  endif

/*****************************************************************************/

// Construct a default instance (initializes the mutex)
template <class Mutex>
inline
RWMonitor<Mutex>::RWMonitor(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWMonitor<Mutex>,RWMonitor(void));
}

// Construct a static instance (does not initialize the mutex; the
// mutex will be initialized the first time it is accessed)
template <class Mutex>
inline
RWMonitor<Mutex>::RWMonitor(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      mutex_(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWMonitor<Mutex>,RWMonitor(RWStaticCtor));
}

// Define a copy-constructor so derived classes can copy-construct, 
// but initialize the mutex; don't copy it...
// (mutexes cannot be copied)
template <class Mutex>
inline
RWMonitor<Mutex>::RWMonitor(const RWMonitor<Mutex>& )
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMonitor<Mutex>,RWMonitor(const RWMonitor<Mutex>&));
}

// Define an assignment operator so derived classes can do assignments, 
// but don't assign the mutex, leave it alone... 
// (mutexes cannot be assigned)
template <class Mutex>
inline
RWMonitor<Mutex>&
RWMonitor<Mutex>::operator=(const RWMonitor<Mutex>&)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMonitor<Mutex>,operator=(const RWMonitor<Mutex>&):RWMonitor<Mutex>&);
   return *this;
}

// Lock the monitor; acquires the monitor's mutex.
template <class Mutex>
inline
void
RWMonitor<Mutex>::acquire(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWMonitor<Mutex>,acquire(void):void);
   mutex_.acquire();
}

#  if defined(RW_THR_DEBUG)

// Determine whether calling thread currently owns the monitor (mutex)
// Primarily intended for use in precondition assertions.
template <class Mutex>
inline
RWBoolean
RWMonitor<Mutex>::isAcquired(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWMonitor<Mutex>,isAcquired(void):RWBoolean);
   return RW_THR_CONST_CAST(RWMonitor<Mutex>&, *this).mutex_.isAcquired();
}

#endif

// Cast away const on this and return a reference. Used to lock a 
// monitor via LockGuard. This is necessary to avoid having to 
// explicitly cast away const on the this pointer in every const 
// member function that needs to lock the monitor.
template <class Mutex>
inline
RWMonitor<Mutex>&
RWMonitor<Mutex>::monitor(void) const
   RWTHRTHROWSANY
{   
   return RW_THR_CONST_CAST(RWMonitor<Mutex>&, *this);
}

// Accessor function for the monitor's mutex.
template <class Mutex>
inline
Mutex&
RWMonitor<Mutex>::mutex(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMonitor<Mutex>,mutex(void):Mutex&);
   return mutex_;
}

// Unlock the monitor; releases the monitor's mutex.
template <class Mutex>
inline
void
RWMonitor<Mutex>::release(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWMonitor<Mutex>,release(void):void);
   mutex_.release();
}

// Conditionally lock the monitor, but only if it can be locked 
// without blocking.
template <class Mutex>
inline
RWBoolean
RWMonitor<Mutex>::tryAcquire(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWMonitor<Mutex>,tryAcquire(void):RWBoolean);
   return mutex_.tryAcquire();
}

/*****************************************************************************/

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/monitor.cc>
#  endif

#endif // __RWTHRMONITOR_H__
