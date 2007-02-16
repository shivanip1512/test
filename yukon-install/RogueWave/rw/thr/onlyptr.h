#if !defined(__RWTHRONLYPTR_H__)
#define __RWTHRONLYPTR_H__
/*****************************************************************************
 *
 * onlyptr.h
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

onlyptr.h - Class declarations for:

   RWOnlyPointer<Body>
            
See Also:

   onlyptr.cc  - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRPOINTER_H__)
#     include <rw/thr/pointer.h>
#  endif

template <class Body>
class RWTHRTExport RWOnlyPointer : 
   public RWPointer<Body> {

   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Body BodyType;  // Can't depend on inheritance!

   public:
      
      // Constructor for static instances
      RWOnlyPointer(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Assume responsibility for a new body
      RWOnlyPointer(Body* bodyP=rwnil)
         RWTHRTHROWSNONE;

      // Assume responsibility for another handle's body
      //   Even though we will orphan the body from second, we must
      //   declare second const to allow assignment from temporaries!
      RWOnlyPointer(const RWOnlyPointer<Body>& second)
         RWTHRTHROWSNONE;

      // Delete the current body
#  if defined(RW_THR_INLINE_IN_DECLARATION)
      inline
#  endif
      ~RWOnlyPointer(void)
         RWTHRTHROWSNONE;

      // Assume responsibility for another handle's body
      //   Even though we will orphan the body from second, we must
      //   declare second const to allow assignment from temporaries!
      RWOnlyPointer<Body>&
      operator=(const RWOnlyPointer<Body>& second)
         RWTHRTHROWSNONE;

      // Assign a new pointer value to the handle (required for handle conversions!)
      RWOnlyPointer<Body>&
      operator=(Body* ptr)
         RWTHRTHROWSNONE;

      // Body pointer conversion operator for handle (required for handle conversions!)
      // Gives up responsibility for deleting body!
      operator Body*(void)
         RWTHRTHROWSANY;

      // Exchange bodies with another handle (one or both may be nil!)
      void
      swapWith(RWOnlyPointer<Body>& second)
         RWTHRTHROWSNONE;

   protected:

      // Relinquish control over the current body (if any)
      Body*
      orphan(void)
         RWTHRTHROWSNONE;

      // Delete the current body (if any), and assume control of another body (if any)
      void
      transfer(Body* bodyP=rwnil)
         RWTHRTHROWSNONE;

   private:
};

template <class Body>
inline
RWOnlyPointer<Body>::RWOnlyPointer(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWPointer<Body>(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWOnlyPointer<Body>,RWOnlyPointer(RWStaticCtor));
}

template <class Body>
inline
RWOnlyPointer<Body>::RWOnlyPointer(Body* bodyP)
   RWTHRTHROWSNONE
   : 
      RWPointer<Body>(bodyP)
{
   RWTHRTRACEMF(RWOnlyPointer<Body>,RWOnlyPointer(Body*));
}

template <class Body>
inline
RWOnlyPointer<Body>::RWOnlyPointer(const RWOnlyPointer<Body>& second)
   RWTHRTHROWSNONE
   : 
      // Detach the second handle from the body and attach this one to the body
      RWPointer<Body>(RW_THR_CONST_CAST(RWOnlyPointer<Body>&, second).orphan())
{
   RWTHRTRACEMF(RWOnlyPointer<Body>,RWOnlyPointer(const RWOnlyPointer<Body>&));
}


template <class Body>
inline
RWOnlyPointer<Body>::~RWOnlyPointer(void)
   RWTHRTHROWSNONE
{
   // Always delete the body when the handle destructs...
   delete this->bodyP_;  // may still be null (but that's ok!)
}

template <class Body>
inline
RWOnlyPointer<Body>&
RWOnlyPointer<Body>::operator=(const RWOnlyPointer<Body>& second)
   RWTHRTHROWSNONE
{ 
   RWTHRTRACEMF(RWOnlyPointer<Body>,operator=(const RWOnlyPointer<Body>&):RWOnlyPointer<Body>&);
   if (&second != this) 
      // Detach the second handle from the body and attach this one to the body
      transfer(RW_THR_CONST_CAST(RWOnlyPointer<Body>&, second).orphan());
   return *this; 
}

template <class Body>
inline
RWOnlyPointer<Body>&
RWOnlyPointer<Body>::operator=(Body* ptr)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWOnlyPointer<Body>,operator=(Body*):RWOnlyPointer<Body>&);
   transfer(ptr);
   return *this;
}

template <class Body>
inline
RWOnlyPointer<Body>::operator Body*(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWOnlyPointer<Body>,operator Body*(void));
   return orphan();
}

template <class Body>
inline
void
RWOnlyPointer<Body>::swapWith(RWOnlyPointer<Body>& second)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWOnlyPointer<Body>,swapWith(RWOnlyPointer<Body>&):void);
   // Exchange bodies with another handle (either or both may be rwnil)
   Body* temp;
   temp = second.bodyP_;
   second.bodyP_ = this->bodyP_;
   this->bodyP_ = temp;
}

template <class Body>
// protected
inline
Body*
RWOnlyPointer<Body>::orphan(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWOnlyPointer<Body>,orphan(void):Body*);
   // Detach from the body
   Body* temp = this->bodyP_;
   this->bodyP_=rwnil;
   return temp;
}

template <class Body>
// protected
inline
void
RWOnlyPointer<Body>::transfer(Body* bodyP)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWOnlyPointer<Body>,transfer(Body*):void);
   // Delete this handle's current body (if any)
   delete this->bodyP_;
   // And attach to the new one...
   this->bodyP_ = bodyP;
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/onlyptr.cc>
#  endif

#endif // __RWTHRONLYPTR_H__


