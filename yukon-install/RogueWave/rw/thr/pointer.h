#if !defined(__RWTHRPOINTER_H__)
#define __RWTHRPOINTER_H__
/*****************************************************************************
 *
 * pointer.h
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

pointer.h - Class declarations for:

   RWPointer<Body> - Base class for:
                        RWOnlyPointer, 
                        RWCountedPointer, 
            
See Also:

   pointer.cc  - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RWTHREXCEPT_H__)
#     include <rw/thr/except.h>
#  endif

template <class Body>
class RWTHRTExport RWPointer {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Body                  BodyType;

   protected:

      Body* bodyP_;

   public:

#if defined(RW_THR_INLINE_IN_DECLARATION)
      inline
#endif
      ~RWPointer(void)
         RWTHRTHROWSNONE;

      // Throws exception if pointer is nil
      void
      validate(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Returns TRUE if pointer is non-nil, False if is nil
      RWBoolean
      isValid(void) const
         RWTHRTHROWSNONE;

      // Dereference the handle to get a reference to the body
      Body&
      operator*(void) const
         RWTHRTHROWSANY;

      // Test to see if this instance points to the same body as the second instance
      RWBoolean
      operator==(const RWPointer<Body>& second) const
         RWTHRTHROWSANY;

      // Test to see if this instance points to a different body than the second instance points to.
      RWBoolean
      operator!=(const RWPointer<Body>& second) const
         RWTHRTHROWSANY;

      // Provide dummy less-than operator to allow smart-pointer
      // classes to be used in stdlib-based collections. 
      // This member compares the body addresses and should
      // be replaced in derived classes if other behavior is desired!
      RWBoolean
      operator<(const RWPointer<Body>& second) const
         RWTHRTHROWSANY;

      // Test to see if this instance points to the same body as the argument
      RWBoolean
      operator==(const Body* ptr) const
         RWTHRTHROWSNONE;

      // Test to see if this instance points to a different body than the argument
      RWBoolean
      operator!=(const Body* ptr) const
         RWTHRTHROWSNONE;

   protected:

      // RWPointer is intended to be an abstract base class
      // We'll hide the following functions by making them protected

      // Construct a handle instance with a pointer value of rwnil
      RWPointer(void)
         RWTHRTHROWSNONE;

      // Constructor for static pointers that may be used before constructed.
      RWPointer(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Construct a handle instance that points to the specified body
      RWPointer(Body* bodyP)
         RWTHRTHROWSNONE;

      // Construct a handle instance that points to the same body as the second
      RWPointer(const RWPointer<Body>& second)
         RWTHRTHROWSNONE;

      // Retrieve the handle's pointer value without validating it
      Body*
      get(void) const
         RWTHRTHROWSNONE;

      // Assign a new pointer value to the handle
      RWPointer<Body>&
      operator=(Body* ptr)
         RWTHRTHROWSNONE;
};

/*****************************************************************************/

template <class Body>
inline
RWPointer<Body>::~RWPointer(void)
   RWTHRTHROWSNONE
{
}

template <class Body>
inline
RWBoolean
RWPointer<Body>::isValid(void) const
   RWTHRTHROWSNONE
{ 
   RWTHRTRACEMF(RWPointer<Body>,isValid(void) const:RWBoolean);
   return (rwnil != bodyP_); 
}

template <class Body>
#if !defined(RW_THR_CANT_THROW_FROM_INLINE)
inline
#endif
void
RWPointer<Body>::validate(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{ 
   RWTHRTRACEMF(RWPointer<Body>,validate(void) const:void);
   if (!isValid()) 
      throw RWTHRInvalidPointer(); 
}

// protected
template <class Body>
inline
Body*
RWPointer<Body>::get(void) const
   RWTHRTHROWSNONE
{ 
   RWTHRTRACEMF(RWPointer<Body>,get(void) const:Body*);
   return bodyP_; 
}

template <class Body>
inline
Body&
RWPointer<Body>::operator*(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWPointer<Body>,operator*(void) const:Body&);
   validate();
   return *get();
}

template <class Body>
inline
RWBoolean
RWPointer<Body>::operator==(const RWPointer<Body>& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWPointer<Body>,operator==(const RWPointer<Body>&) const:RWBoolean);
   // Note! Pointers are equal if they are both rwnil!
   return bodyP_ == second.bodyP_;
}

template <class Body>
inline
RWBoolean
RWPointer<Body>::operator!=(const RWPointer<Body>& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWPointer<Body>,operator!=(const RWPointer<Body>&) const:RWBoolean);
   return bodyP_ != second.bodyP_;
}

template <class Body>
inline
RWBoolean
RWPointer<Body>::operator<(const RWPointer<Body>& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWPointer<Body>,operator<(const RWPointer<Body>&) const:RWBoolean);
   // Note! Pointers are equal if they are both rwnil!
   return bodyP_ < second.bodyP_;
}

template <class Body>
inline
RWBoolean
RWPointer<Body>::operator==(const Body* ptr) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWPointer<Body>,operator==(const Body*) const:RWBoolean);
   return bodyP_ == ptr;
}

template <class Body>
inline
RWBoolean
RWPointer<Body>::operator!=(const Body* ptr) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWPointer<Body>,operator!=(const Body*) const:RWBoolean);
   return bodyP_ != ptr;
}

template <class Body>
inline
RWPointer<Body>::RWPointer(void)
   RWTHRTHROWSNONE
   : bodyP_(rwnil)
{
   RWTHRTRACEMF(RWPointer<Body>,RWPointer(void));
}

template <class Body>
inline
RWPointer<Body>::RWPointer(RWStaticCtor)
   RWTHRTHROWSNONE
{
   // Do nothing to change bodyP_ (it may already have a value)
   RWTHRTRACEMF(RWPointer<Body>,RWPointer(RWStaticCtor));
}

template <class Body>
inline
RWPointer<Body>::RWPointer(Body* bodyP)
   RWTHRTHROWSNONE
   : bodyP_(bodyP)
{
   RWTHRTRACEMF(RWPointer<Body>,RWPointer(Body*));
}

template <class Body>
inline
RWPointer<Body>::RWPointer(const RWPointer<Body>& second)
   RWTHRTHROWSNONE
   : bodyP_(second.bodyP_)
{
   RWTHRTRACEMF(RWPointer<Body>,RWPointer(RWPointer<Body>&));
}

template <class Body>
inline
RWPointer<Body>&
RWPointer<Body>::operator=(Body* ptr)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWPointer<Body>,operator=(Body*):RWPointer<Body>&);
   bodyP_ = ptr; return *this;
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/pointer.cc>
#  endif

#endif //__RWTHRPOINTER_H__
