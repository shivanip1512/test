#if !defined(__RWTHRFUNCR1_CC__)
#  define __RWTHRFUNCR1_CC__
/*****************************************************************************
 *
 * funcr1.cc
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

funcr1.cc  - Out-of-line function definitions for:

   RWFunctorR1<SR,S1> - Handle class for RWFunctorR1Imp functor family
   RWFunctorR1Imp<SR,S1> - Base class body for FunctorR1 functor family

   RWFunctorR1GImp<SR,S1,DR,D1>
   RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>
   RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>

   RWFunctorR1MImp<SR,S1,Callee,DR,D1>
   RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>
   RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>
            
See Also:

   funcr1.h - Class declarations.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor1Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if defined(__RWTHRFUNCR1_H__)
#     include <rw/thr/funcr1.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T2(RWFunctorR1,SR,S1)

RW_THR_IMPLEMENT_TRACEABLE_T4(RWFunctorR1GImp,SR,S1,DR,D1)
RW_THR_IMPLEMENT_TRACEABLE_T5(RWFunctorR1GA1Imp,SR,S1,DR,D1,A1)
RW_THR_IMPLEMENT_TRACEABLE_T6(RWFunctorR1GA2Imp,SR,S1,DR,D1,A1,A2)

RW_THR_IMPLEMENT_TRACEABLE_T5(RWFunctorR1MImp,SR,S1,Callee,DR,D1)
RW_THR_IMPLEMENT_TRACEABLE_T6(RWFunctorR1MA1Imp,SR,S1,Callee,DR,D1,A1)
RW_THR_IMPLEMENT_TRACEABLE_T7(RWFunctorR1MA2Imp,SR,S1,Callee,DR,D1,A1,A2)

template <class SR, class S1>
SR
RWFunctorR1<SR,S1>::operator()(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWFunctorR1,SR,S1,operator()(S1):SR);
   // throws RWTHRInvalidPointer if no imp assigned!
   return body().run(s1);
}

/******************************************************************************

RWFunctorR1Imp<SR,S1> Family:

   RWFunctorR1GImp<SR,S1,DR,D1>
   RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>
   RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>

   RWFunctorR1MImp<SR,S1,Callee,DR,D1>
   RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>
   RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>

******************************************************************************/

// SR (*caller)(S1) -> DR (*callee)(D1)

template <class SR, class S1, class DR, class D1>
// virtual
SR 
RWFunctorR1GImp<SR,S1,DR,D1>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT4(RWFunctorR1GImp,SR,S1,DR,D1,run(S1) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (*function_)(s1);
#else
  return SR((*function_)(s1));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1) -> DR (*callee)(D1,A1)

template <class SR, class S1, class DR, class D1, class A1>
// virtual
SR
RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT5(RWFunctorR1GA1Imp,SR,S1,DR,D1,A1,run(S1) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (*function_)(s1,a1_);
#else
  return SR((*function_)(s1,a1_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1) -> DR (*callee)(D1,A1,A2)

template <class SR, class S1, class DR, class D1, class A1, class A2>
// virtual
SR 
RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT6(RWFunctorR1GA2Imp,SR,S1,DR,D1,A1,A2,run(S1) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (*function_)(s1,a1_,a2_);
#else
  return SR((*function_)(s1,a1_,a2_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1) -> DR (Callee::*func)(D1)

template <class SR, class S1, class Callee, class DR, class D1>
// virtual
SR 
RWFunctorR1MImp<SR,S1,Callee,DR,D1>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT5(RWFunctorR1MImp,SR,S1,Callee,DR,D1,run(S1) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(s1);
#else
  return SR((callee_.*function_)(s1));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1) -> DR (Callee::*func)(D1,A1)

template <class SR, class S1, class Callee, class DR, class D1, class A1>
// virtual
SR 
RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT6(RWFunctorR1MA1Imp,SR,S1,Callee,DR,D1,A1,run(S1) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(s1,a1_);
#else
  return SR((callee_.*function_)(s1,a1_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1) -> DR (Callee::*func)(D1,A1,A2)

template <class SR, class S1, class Callee, class DR, class D1, class A1, class A2>
// virtual
SR 
RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT7(RWFunctorR1MA2Imp,SR,S1,Callee,DR,D1,A1,A2,run(S1) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(s1,a1_,a2_);
#else
  return SR((callee_.*function_)(s1,a1_,a2_));  // stops Borland from losing temporary
#endif   
}

#endif // __RWTHRFUNCR1_CC__
