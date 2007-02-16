#if !defined(__RWTHRHANDBODY_H__)
#  define __RWTHRHANDBODY_H__
/*****************************************************************************
 *
 * handbody.h 
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

handbody.h - Class declarations for:

   RWTHRHandle - Base class for counted-reference handle implementations
   RWTHRBody - Base class for reference-counting body implementations 

See Also:

   handbody.cpp - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRCOUNTPTR_H__)
#     include <rw/thr/countptr.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

class RWTHRExport RWTHRBody;
class RWTHRExport RWTHRHandle;

#  if defined(RW_MULTI_THREAD)
// Multi-thread version
typedef RWCountingBody<RWMutexLock> RWTHRBodyBase;  
#  else
// Single-thread version
typedef RWCountingBody<RWNullMutexLock> RWTHRBodyBase;  
#  endif

#  if defined(RW_THR_SUPERCLASSES_MUST_BE_EXPORTED)
// Microsoft can't doesn't support explicit exporting of
// templates classes and produces warnings for inheriting 
// from the non-exported RWCountingBody<> class.
// This pragma turns these warnings off...
#     pragma warning( disable : 4275 ) 
#  endif

/******************************************************************************
   Base class body implementation:
******************************************************************************/
 
class RWTHRExport RWTHRBody :
   // Inherit protected to hide RWCountingBody's public
   // addReference, removeReference, and references members
#  if !defined(RW_THR_BROKEN_PROTECTED_INHERITANCE)
   protected RWTHRBodyBase {
#  else
   public RWTHRBodyBase {
#  endif


   RW_THR_DECLARE_TRACEABLE

   // Give RWCountedPointer access to public RWCountingBody members...
#  if defined(RW_THR_NO_SELF_REFERENCING_FRIEND_DECLARATIONS)
   private:
      // Define typedef for friend class
      typedef RWCountedPointer<RWTHRBody> RWCountedPointerRWTHRBody;
   friend class RWCountedPointerRWTHRBody;
#  else
   friend class RWCountedPointer<RWTHRBody>;
#  endif      

   protected:

      // Abstract base class - all constructors are protected
            
      // Construct a default body instance 
      // (reference count starts at zero)
      RWTHRBody(void)
         RWTHRTHROWSANY;

      // Construct a static body instance 
      // (reference count starts at one to prevent deletion by handles)
      RWTHRBody(RWStaticCtor)
         RWTHRTHROWSANY;

      // We don't want to copy the reference count during copy-construction,
      // so this member calls the RWCountingBody copy constructor which copies
      // nothing...
      RWTHRBody(const RWTHRBody& second)
         RWTHRTHROWSANY;

      // Make the destructor virtual so that RWTHRHandle will call the proper
      // destructor for classes derived from RWTHRBody.
      virtual
      ~RWTHRBody(void)
         RWTHRTHROWSNONE;

      // We don't want to copy the reference count during assignment,
      // so this operator calls the RWCountingBody assignment operator
      // which assigns nothing...
      RWTHRBody&
      operator=(const RWTHRBody& second)
         RWTHRTHROWSNONE;
};

#  if defined(RW_THR_SUPERCLASSES_MUST_BE_EXPORTED)
#     pragma warning( default : 4275 )
#  endif

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
template class RWTHRIExport RWPointer<RWTHRBody>;
template class RWTHRIExport RWCountedPointer<RWTHRBody>;
#  endif

/******************************************************************************
   Base class handle implementation:
******************************************************************************/

class RWTHRExport RWTHRHandle {
      
   RW_THR_DECLARE_TRACEABLE
   
   protected:
      
      RWCountedPointer<RWTHRBody>  body_;

   public:
      
      // Does this handle point to the same body instance, if any,
      // as pointed-to by a second handle?
      RWBoolean
      operator==(const RWTHRHandle& second) const
         RWTHRTHROWSNONE;

      // Required for stdlib-based collections
      // (should be redefined in derived classes as necessary)
      RWBoolean
      operator<(const RWTHRHandle& second) const
         RWTHRTHROWSNONE;

      // Does this handle point to a different body instance, if any,
      // than pointed-to by a second handle?
      RWBoolean
      operator!=(const RWTHRHandle& second) const
         RWTHRTHROWSNONE;

      // Does this handle point to a valid body instance?
      RWBoolean
      isValid(void) const
         RWTHRTHROWSNONE;
   
   protected:

      // Abstract base class - all constructors are protected

      // Construct an empty, invalid, handle instance
      RWTHRHandle(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance 
      // (may used prior to construction)
      RWTHRHandle(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to a body instance.
      RWTHRHandle(RWTHRBody* body)
         RWTHRTHROWSANY;

      // Bind a new handle instance to the same body instance, if any, 
      // pointed-to by a second handle instance.
      RWTHRHandle(const RWTHRHandle& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the same body instance, if any,
      // pointed-to by a second handle instance.
      RWTHRHandle&
      operator=(const RWTHRHandle& second)
         RWTHRTHROWSANY;      

      // Get a reference for the body instance, if any, 
      // otherwise throw an exception
      RWTHRBody&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};

/*****************************************************************************/

// Construct a default body instance 
// (reference count starts at zero)
inline
RWTHRBody::RWTHRBody(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWTHRBody,RWTHRBody(void));
}

// Construct a static body instance 
// (reference count starts at one to prevent deletion by handles)
inline
RWTHRBody::RWTHRBody(RWStaticCtor)
   RWTHRTHROWSANY
   :
      RWTHRBodyBase(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWTHRBody,RWTHRBody(RWStaticCtor));
}

// We don't want to copy the reference count during copy-construction,
// so this member calls the RWCountingBody copy constructor which copies
// nothing...
inline
RWTHRBody::RWTHRBody(const RWTHRBody& second)
   RWTHRTHROWSANY
   :
      RWTHRBodyBase(second)
{
   RWTHRTRACEMF(RWTHRBody,RWTHRBody(const RWTHRBody&));
}

// We don't want to copy the reference count during assignment,
// so this operator calls the RWCountingBody assignment operator
// which assigns nothing...
inline
RWTHRBody&
RWTHRBody::operator=(const RWTHRBody& second)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWTHRBody,operator=(const RWTHRBody&):RWTHRBody&);
   if (&second != this)
      RWTHRBodyBase::operator=(second);
   return *this;
}

/*****************************************************************************/

// Construct an empty, invalid, handle instance
inline
RWTHRHandle::RWTHRHandle(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWTHRHandle,RWTHRHandle(void));
}

// Construct a global static handle instance 
// (may used prior to construction)
inline
RWTHRHandle::RWTHRHandle(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      body_(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWTHRHandle,RWTHRHandle(RWStaticCtor));
}

// Bind a new handle instance to a body instance.
inline
RWTHRHandle::RWTHRHandle(RWTHRBody* body)
   RWTHRTHROWSANY
   :
      body_(body)
{
   RWTHRTRACEMF(RWTHRHandle,RWTHRHandle(RWTHRBody*));
}

// Bind a new handle instance to the same body instance, if any, 
// pointed-to by a second handle instance.
inline
RWTHRHandle::RWTHRHandle(const RWTHRHandle& second)
   RWTHRTHROWSANY
   :
      body_(second.body_)
{
   RWTHRTRACEMF(RWTHRHandle,RWTHRHandle(const RWTHRHandle&));
}

// Bind this handle instance to the same body instance, if any,
// pointed-to by a second handle instance.
inline
RWTHRHandle&
RWTHRHandle::operator=(const RWTHRHandle& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWTHRHandle,operator=(const RWTHRHandle&):RWTHRHandle&);
   if (&second != this)
      body_ = second.body_;
   return *this;
}

// Does this handle point to the same body instance, if any,
// as pointed-to by a second handle?
inline
RWBoolean
RWTHRHandle::operator==(const RWTHRHandle& second) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWTHRHandle,operator==(const RWTHRHandle&) const:RWBoolean);
   return body_ == second.body_;
}

inline
RWBoolean
RWTHRHandle::operator<(const RWTHRHandle& ) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWTHRHandle,operator<(const RWTHRHandle&) const:RWBoolean);
   // Return something (looks like equality)
   return FALSE;
}

// Does this handle point to a different body instance, if any,
// than pointed-to by a second handle?
inline
RWBoolean
RWTHRHandle::operator!=(const RWTHRHandle& second) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWTHRHandle,operator!=(const RWTHRHandle&) const:RWBoolean);
   return body_ != second.body_;
}

// Does this handle point to a valid body instance?
inline
RWTHRBody&
RWTHRHandle::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWTHRHandle,body(void) const:RWTHRBody&);
   return (*body_);
}

// Does this handle point to a valid body instance?
inline
RWBoolean
RWTHRHandle::isValid(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWTHRHandle,isValid(void) const:RWBoolean);
   return body_.isValid();
}

// Include cancel.h for those files that included handbody.h
// directly and still need to replace the forward declaration
// of RWCancellation with the real declaration.
// This include is required due to the circular dependency
// introduced when cancel.h was included in mutex.h to
// workaround a problem some compilers have when a forward 
// declaration of RWCancellation is used in an exception spec,
// and the RWCancellation class was never defined in the
// translation unit...
#  if !defined(__RWTHRCANCEL_H__)
#     include <rw/thr/cancel.h>
#  endif 

#endif // __RWTHRHANDBODY_H__

