#if !defined(__RWTHRFUNCTOR0_H__)
#  define __RWTHRFUNCTOR0_H__
/*****************************************************************************
 *
 * functor0.h 
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

functor0.h - Class declarations for:

   RWFunctor0 - Handle for no caller arguments functor classes.

   RWFunctor0Imp - Base class for no caller arguments functor classes.

See Also:

   functor0.cpp - Out-of-line function definitions.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor1Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

/******************************************************************************
   Base class body implementation for functors with caller signature:
   void (*func)(void);
******************************************************************************/
 
class RWTHRExport RWFunctor0Imp :
   public RWTHRBody {
   
   RW_THR_DECLARE_TRACEABLE
   
   public:
      typedef void (*CallerSignature)(void);

      virtual
      void
      run(void) const = 0
         RWTHRTHROWSANY;
};

/******************************************************************************
   Handle for functor implementations with caller signature:
   void (*func)(S1);
******************************************************************************/

class RWTHRExport RWFunctor0 :
   public RWTHRHandle {
      
   RW_THR_DECLARE_TRACEABLE
   
   public:
      
      typedef void (*CallerSignature)(void);

      // Construct an empty, invalid, handle instance
      RWFunctor0(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWFunctor0(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to a functor instance 
      RWFunctor0(RWFunctor0Imp* functorImp)
         RWTHRTHROWSANY;
      
      // Bind a new handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctor0(const RWFunctor0& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctor0&
      operator=(const RWFunctor0& second)
         RWTHRTHROWSANY;

      // Invoke the functor instance, if any, 
      // pointed-to by this handle instance
      void 
      operator()(void) const
         RWTHRTHROWSANY;
         // throws RWTHRInvalidPointer if no imp assigned!

      // Get a reference to the functor instance, if any, 
      // pointed-to by this handle instance
      RWFunctor0Imp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};

/*****************************************************************************/

inline
RWFunctor0::RWFunctor0(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWFunctor0,RWFunctor0(void));
}

inline
RWFunctor0::RWFunctor0(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWFunctor0,RWFunctor0(RWStaticCtor));
}

inline
RWFunctor0::RWFunctor0(RWFunctor0Imp* functorImp)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(functorImp)
{
   RWTHRTRACEMF(RWFunctor0,RWFunctor0(RWFunctor0Imp*));
}
      
inline
RWFunctor0::RWFunctor0(const RWFunctor0& second)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWFunctor0,RWFunctor0(const RWFunctor0&));
}

inline
RWFunctor0&
RWFunctor0::operator=(const RWFunctor0& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWFunctor0,operator=(const RWFunctor0&):RWFunctor0&);
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

inline
RWFunctor0Imp&
RWFunctor0::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWFunctor0,body(void):RWFunctor0Imp&);
   return (RWFunctor0Imp&)RWTHRHandle::body();
}

inline
void 
RWFunctor0::operator()(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWFunctor0,operator()(void):void);
   // throws RWTHRInvalidPointer if no imp assigned!
   body().run();
}

#endif // __RWTHRFUNCTOR0_H__
