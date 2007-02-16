#if !defined(__RWTHRCOUNTPTR_H__)
#  define __RWTHRCOUNTPTR_H__
/*****************************************************************************
 *
 * countptr.h
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

countptr.h - Class declarations for:

   RWCountedPointer<Body> - A reference-counting pointer class.
   RWCountingBody<Mutex> - Referencing-counting body base-class; provides 
                           interface required by RWCountedPointer<Body> for
                           safely maintaining a reference count.

See Also:

   countptr.cc  - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRPOINTER_H__)
#     include <rw/thr/pointer.h>
#  endif

#  if !defined(__RWTHRMONITOR_H__)
//    Base class for RWCountingBody
#     include <rw/thr/monitor.h>
#  endif

template <class Body>
class RWTHRTExport RWCountedPointer :
   public RWPointer<Body> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Body BodyType; // Can't count on inheritance!

   public:
      
      RWCountedPointer(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Attach to and increment the reference count on a body
      RWCountedPointer(Body* bodyP=rwnil)
         RWTHRTHROWSANY;

      // Attach to and increment the reference count on second's body
      RWCountedPointer(const RWCountedPointer<Body>& second)
         RWTHRTHROWSANY;

      ~RWCountedPointer(void)
         RWTHRTHROWSANY;

      // Detach from the current body (if any), decrement its reference count, 
      // and delete it if there are no other references, then attach to second's 
      // body, and increment its reference count.
      RWCountedPointer<Body>&
      operator=(const RWCountedPointer<Body>& second)
         RWTHRTHROWSANY;

      // Detach from the current body (if any), decrement its reference count, 
      // and delete it if there are no other references, then attach to second's 
      // body, and increment its reference count.
      RWCountedPointer<Body>&
      operator=(Body* ptr)
         RWTHRTHROWSANY;

      // This operator should always be used to dereference the handle as it
      // will validate the pointer, throwing an exception if it is not valid (==rwnil)!
      Body*
      operator->(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Body pointer conversion operator for handle
      // Allows assigments from derived class handles to base class handles.
      // Unfortunately this must be made public to support handle conversion,
      // but also gives user direct access to body (which may not be desirable!)
      // But what the heck, the operator->() also gives user access to the body!
      operator Body*(void) const
         RWTHRTHROWSANY;

      // Detach from the current body (if any), decrement its reference count
      // and delete it if there are no other references.
      void
      orphan(void)
         RWTHRTHROWSANY;

      // Swap bodies (if any) with another handle.
      void
      swapWith(RWCountedPointer<Body>& second)
         RWTHRTHROWSNONE;

   protected:

      // Detach from the current body (if any), decrementing its reference count
      // and deleting it if there are no other references, and attach to the 
      // new body, incrementing its reference count.
      void
      transfer(Body* bodyP=rwnil)
         RWTHRTHROWSANY;

};

//
// The RWCountingBody class is intended as a base class replacement for 
// RWReference in user classes that are to be reference counted in a 
// multithreaded environment.

// It works just like RWReference except the mutex is a member 
// (the mutex is inherited from RWMonitor<Mutex>)

template <class Mutex>
class RWTHRTExport RWCountingBody : 
   public RWMonitor<Mutex> {

   RW_THR_DECLARE_TRACEABLE

   protected:

      unsigned refs_;

   public:

      // Unfortunately these must be public to let 
      // the template class RWCountedPointer get access!

      // Query the current reference count
      unsigned
      references(void) const
         RWTHRTHROWSNONE;

      // Increment the reference count
      void
      addReference(void)
         RWTHRTHROWSANY;

      // Decrement the reference count
      unsigned
      removeReference(void)
         RWTHRTHROWSANY;

   protected:

      // This class is intended for use as an abstract base class, so the 
      // constructors are protected but is accessible by derived classes.

      // Construct a default instance (initializes the reference count)
      RWCountingBody(unsigned initCount = 0)
         RWTHRTHROWSNONE;

      // Construct a static instance (does not initialize the reference count)
      RWCountingBody(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Define copy-constructor so derived classes can copy-construct, 
      // but initialize the reference count; don't copy it...
      RWCountingBody(const RWCountingBody<Mutex>& second)
         RWTHRTHROWSNONE;

      // Define an assignment operator so derived classes can do assignments, 
      // but don't assign the reference count, leave it alone...
      RWCountingBody<Mutex>&
      operator=(const RWCountingBody<Mutex>& second)
         RWTHRTHROWSNONE;

      // Predefine guards for use with counting body.
      typedef RWLockGuard< RWMonitor< Mutex > >     Guard;
      typedef RWLockGuard< RWMonitor< Mutex > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< Mutex > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< Mutex > >  TryLockGuard;

};

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
#     if !defined(__RWTHRMUTEX_H__) 
#        include <rw/thr/mutex.h>
//       These explicit instantiations did't get declared 
//       because countptr.h was included before monitor.h
template class RWTHRIExport RWMonitor<RWMutexLock>;
template class RWTHRIExport RWGuardBase<RWMonitor<RWMutexLock> >;
template class RWTHRIExport RWLockGuardBase<RWMonitor<RWMutexLock> >;
template class RWTHRIExport RWLockGuard<RWMonitor<RWMutexLock> >;
template class RWTHRIExport RWTryLockGuard<RWMonitor<RWMutexLock> >;
template class RWTHRIExport RWUnlockGuard<RWMonitor<RWMutexLock> >;
#     endif
template class RWTHRIExport RWCountingBody<RWMutexLock>;
#  endif

/*****************************************************************************/

template <class Body>
inline
RWCountedPointer<Body>::RWCountedPointer(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWPointer<Body>(RW_STATIC_CTOR)
{
   // Do nothing to change pointer since it may already have a value!
   RWTHRTRACEMF(RWCountedPointer<Body>,RWCountedPointer(RWStaticCtor));
}

template <class Body>
inline
RWCountedPointer<Body>::RWCountedPointer(Body* bodyP)
   RWTHRTHROWSANY
   : RWPointer<Body>(bodyP)
{
   RWTHRTRACEMF(RWCountedPointer<Body>,RWCountedPointer(Body*));
   if (this->bodyP_ != rwnil)
      (this->bodyP_)->addReference();
}

template <class Body>
inline
RWCountedPointer<Body>::RWCountedPointer(const RWCountedPointer<Body>& second)
   RWTHRTHROWSANY
   : RWPointer<Body>(second.bodyP_)
{
   RWTHRTRACEMF(RWCountedPointer<Body>,RWCountedPointer(const RWCountedPointer<Body>&));
   if (this->bodyP_ != rwnil)
      (this->bodyP_)->addReference(); 
}

template <class Body>
inline
RWCountedPointer<Body>::~RWCountedPointer(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWCountedPointer<Body>,~RWCountedPointer(void));
   // Detach and delete if necessary...
   orphan();
}

template <class Body>
inline
RWCountedPointer<Body>&
RWCountedPointer<Body>::operator=(const RWCountedPointer<Body>& second)
   RWTHRTHROWSANY
{ 
   RWTHRTRACEMF(RWCountedPointer<Body>,operator=(const RWCountedPointer<Body>&):RWCountedPointer<Body>&);
   if (&second != this) 
      // Detach current and delete if necessary, then attach to second's body.
      transfer(second.bodyP_); 
   return *this; 
}

template <class Body>
inline
RWCountedPointer<Body>&
RWCountedPointer<Body>::operator=(Body* ptr)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWCountedPointer<Body>,operator=(Body*):RWCountedPointer<Body>&);
   transfer(ptr);
   return *this;
}

template <class Body>
inline
Body*
RWCountedPointer<Body>::operator->(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWCountedPointer<Body>,operator->(void):Body*);
   this->validate();
   return this->get();
}

template <class Body>
inline
RWCountedPointer<Body>::operator Body*(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWCountedPointer<Body>,operator Body*(void));
   return this->get();
}

template <class Body>
inline
void
RWCountedPointer<Body>::orphan(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWCountedPointer<Body>,orphan(void):void);
   if (this->bodyP_ != rwnil) {
      // We have a body...decrement the reference count to detach
      if ((this->bodyP_)->removeReference() == 0)
         // No more references, delete the body
         delete this->bodyP_;
      // Indicate that this handle no longer points to a body
      this->bodyP_ = rwnil;
   }
}

template <class Body>
inline
void
RWCountedPointer<Body>::transfer(Body* bodyP)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWCountedPointer<Body>,transfer(Body*):void);
   // Detach from the current body and attach to the new body (if any)
   orphan();
   this->bodyP_ = bodyP;
   if (this->bodyP_ != rwnil)
      (this->bodyP_)->addReference();
}

template <class Body>
inline
void
RWCountedPointer<Body>::swapWith(RWCountedPointer<Body>& second)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCountedPointer<Body>,swapWith(RWCountedPointer<Body>&):void);
   // Swapping doesn't require a change to the reference counts!
   Body* temp;
   temp = second.bodyP_;
   second.bodyP_ = this->bodyP_;
   this->bodyP_ = temp;
}

/*****************************************************************************/

// Construct a default instance (initializes the reference count)
template <class Mutex>
inline
RWCountingBody<Mutex>::RWCountingBody(unsigned initCount)
   RWTHRTHROWSNONE
   :
      refs_((unsigned)initCount-1)
{
   RWTHRTRACEMF(RWCountingBody<Mutex>,RWCountingBody(unsigned));
}

// Construct a static instance (does not initialize the reference count)
template <class Mutex>
inline
RWCountingBody<Mutex>::RWCountingBody(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWMonitor<Mutex>(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWCountingBody<Mutex>,RWCountingBody(RWStaticCtor));
   // Leave refs_ alone for initial static instances.
   // The storage where static objects are constructed is
   // initialized with zero, and refs_ = 0 indicates a 
   // reference count of 1. This prevents double destruction
   // of the static body that would normally occur when the 
   // last handle was destroyed.
}

// Define copy-constructor so derived classes can copy-construct, 
// but initialize the reference count; don't copy it...
template <class Mutex>
RWCountingBody<Mutex>::RWCountingBody(const RWCountingBody<Mutex>&)
   RWTHRTHROWSNONE
   :
      refs_((unsigned)-1)
{
   // Don't copy anything
}

// Define an assignment operator so derived classes can do assignments, 
// but don't assign the reference count, leave it alone...
template <class Mutex>
RWCountingBody<Mutex>&
RWCountingBody<Mutex>::operator=(const RWCountingBody<Mutex>&)
   RWTHRTHROWSNONE
{
   // Don't assign anything
   return *this;
}

/*****************************************************************************/

#  if defined(RW_COMPILE_INSTANTIATE)  
#     include <rw/thr/countptr.cc>
#  endif

#endif // __RWTHRCOUNTPTR_H__

