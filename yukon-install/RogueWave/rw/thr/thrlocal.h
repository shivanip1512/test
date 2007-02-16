#if !defined(__RWTHRTHRLOCAL_H__)
#  define __RWTHRTHRLOCAL_H__
/*****************************************************************************
 *
 * thrlocal.h
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

thrlocal.h - Class declaration for:

   RWThreadLocal<Type> - Declare thread-local storage object.
   
   The RWThreadLocal class provide a thread-local storage class with simple
   value semantics.  Each thread local instance may be shared between multiple
   threads, but each thread accessing the thread-local object is actually 
   manipulating its own distinct instances of the template parameter class.  
   
   Thread local variables are especially useful in situations where a global 
   scope variable is desired, but each thread needs to maintain its own value 
   within that variable (the global variable 'errno' is a common example).
   
   The class type specified as a template parameter must possess both a 
   default and a copy constructor and an assignment operator (all instrinsic 
   types have these).

   The value or instance associated with each thread is created the first time
   a thread-local instance is accessed.  A thread-local value is stored in
   an instance using the assignment operator.  A value is retrieved from an
   instance by using the instance as the right-hand term in an expression
   requiring a value of the template parameter Type:

   RWThreadLocal<int>   myInt(RW_STATIC_CTOR);
   
   void func()
   {
      ...
      // Write a value...
      myInt = 5;
      
      // Read a value...
      int anotherInt = myInt;      
   }
   
   If a thread-local instance is evaluated without first assigning it value, 
   then the value retrieved will be that produced by the default constructor
   for the Type-parameter class.  

   Assignment does not actually result in assignment from the right-hand Type-
   instance to the internal Type-instance; instead, the internal Type-instance 
   is deleted, and a new instance is copy-constructed from the right-hand
   Type-instance.

   Thread-local objects may be allocated as static instances, such as global 
   variables or a static class-members, or may be allocated as automatic or
   dynamic instances.  Static instances must be constructed using a special
   static constructor that is selected by passing the RW_STATIC_CTOR value from 
   the enumerated type RWStaticCtor.  Automatically and dynamically allocated
   instances must use the default constructor to properly initialize the 
   thread-local instance. 
   
   There is probably little need for automatic allocation of a thread-local 
   storage object on the stack, since this instance can only be accessed by 
   the allocating thread (unless a reference or pointer to the object is 
   passed to other threads, which is arguably, a dangerous thing to do!).

   The initialization of global and static class member instances is not multi-
   thread safe.  One thread must complete construction or an access operation
   on an instance before other threads may safely access that same instance.

See Also:

   thrlocal.cc - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRMONITOR_H__)
#     include <rw/thr/monitor.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

#  if defined(RW_THR_THREAD_API_OS2)
#     include <rw/tvhdict.h>
#     include <rw/thr/thrmgr.h>
#     include <rw/thr/threadid.h>

extern const size_t RW_THR_THREAD_LOCAL_INITIAL_BUCKETS;

#  elif defined(RW_THR_THREAD_API_POSIX)

typedef pthread_key_t   RWThreadLocalRep;

#  elif defined(RW_THR_THREAD_API_SOLARIS)
#     if !defined(__RW_THREAD_H__)
#        define __RW_THREAD_H__ <thread.h>
#        include __RW_THREAD_H__
#     endif

typedef thread_key_t    RWThreadLocalRep;

#  elif defined(RW_THR_THREAD_API_WIN32)
#     if !defined(__RW_THREAD_H__)
#        define __RW_WINDOWS_H__ <windows.h>
#        include __RW_WINDOWS_H__
#     endif
#     include <rw/thr/thrmgr.h>
#     include <rw/tpslist.h>

typedef DWORD           RWThreadLocalRep;

#  else
#     error Class RWThreadLocal<Type> is not supported in this environment!
#  endif


template <class Type>
class RWTHRTExport RWThreadLocal :
   public RWMonitor<RWMutexLock>{

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;

   protected:

#  if defined(RW_THR_THREAD_API_OS2)
      
      typedef RWTValHashDictionary<RWThreadId,Type*>    RWThreadLocalDict;
      RWThreadLocalDict*   dict_;

#  else

      RWThreadLocalRep     key_;

#  endif

      RWBoolean            isInitialized_;
      RWBoolean            keyCreated_;

#  if defined(RW_THR_THREAD_API_WIN32)
      RWTPtrSlist<Type>    objectsToDelete_;
#  endif

   public:

      // Construct global scope instance 
      // (initialized when first used)
      RWThreadLocal(RWStaticCtor)
         RWTHRTHROWSANY;

      // Construct an automatically or dynamically-allocated instance 
      // (initialized when constructed)
      RWThreadLocal(void)
         RWTHRTHROWSANY;

      // Destroy the instance
      ~RWThreadLocal(void)
         RWTHRTHROWSANY;

      // Assign a thread-local value to the instance
      RWThreadLocal<Type>&
      operator=(const Type& value)
         RWTHRTHROWSANY;

      // Retrieve the value previously stored by this thread (if any)
      operator Type(void) const
         RWTHRTHROWSANY;

   private:

      void
      checkKey(void)
         RWTHRTHROWSANY;

#  if defined(RW_THR_THREAD_API_OS2)
      void
      RWThreadLocal<Type>::deregisterThread(RWThreadId threadId)
         RWTHRTHROWSANY;

      static
      void
      destroy(void *object, RWThreadId threadId)
         RWTHRTHROWSANY;
#  else

      static
      void
      destroy(void *object)
         RWTHRTHROWSANY;

#  endif

      Type*
      getValue(void) const
         RWTHRTHROWSANY;

      void
      setValue(const Type& value)
         RWTHRTHROWSANY;

#  if defined(RW_THR_THREAD_API_WIN32)
      void
      registerObject(Type* ptr)
         RWTHRTHROWSANY;
#  endif

      void
      init(void)
         RWTHRTHROWSANY;
};

/*****************************************************************************/

template <class Type>
inline
RWThreadLocal<Type>&
RWThreadLocal<Type>::operator=(const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadLocal<Type>,operator=(const Type&):RWThreadLocal<Type>&);
   setValue(value);
   return *this;
}

template <class Type>
inline
RWThreadLocal<Type>::operator Type(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadLocal<Type>,operator Type(void));
   return *getValue();
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/thrlocal.cc>
#  endif

#endif // __RWTHRTHRLOCAL_H__


