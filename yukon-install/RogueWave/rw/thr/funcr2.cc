#if !defined(__RWTHRFUNCR2_CC__)
#  define __RWTHRFUNCR2_CC__
/*****************************************************************************
 *
 * funcr2.cc
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

funcr2.cc  - Out-of-line function definitions for:

   RWFunctorR2<SR,S1,S2> - Handle class for RWFunctorR2Imp functor family
   RWFunctorR2Imp<SR,S1,S2> - Base class body for FunctorR2 functor family

   RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>
   RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>
   RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>

   RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>
   RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>
   RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>
            
See Also:

   funcr2.h - Class declarations.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if defined(__RWTHRFUNCR2_H__)
#     include <rw/thr/funcr2.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T3(RWFunctorR2,SR,S1,S2)

RW_THR_IMPLEMENT_TRACEABLE_T6(RWFunctorR2GImp,SR,S1,S2,DR,D1,D2)
RW_THR_IMPLEMENT_TRACEABLE_T7(RWFunctorR2GA1Imp,SR,S1,S2,DR,D1,D2,A1)
RW_THR_IMPLEMENT_TRACEABLE_T8(RWFunctorR2GA2Imp,SR,S1,S2,DR,D1,D2,A1,A2)

RW_THR_IMPLEMENT_TRACEABLE_T7(RWFunctorR2MImp,SR,S1,S2,Callee,DR,D1,D2)
RW_THR_IMPLEMENT_TRACEABLE_T8(RWFunctorR2MA1Imp,SR,S1,S2,Callee,DR,D1,D2,A1)
RW_THR_IMPLEMENT_TRACEABLE_T9(RWFunctorR2MA2Imp,SR,S1,S2,Callee,DR,D1,D2,A1,A2)

template <class SR, class S1, class S2>
SR
RWFunctorR2<SR,S1,S2>::operator()(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWFunctorR2,SR,S1,S2,operator()(S1,S2):SR);
   // throws RWTHRInvalidPointer if no imp assigned!
   return body().run(s1, s2);
}

/******************************************************************************

RWFunctorR2Imp<SR,S1,S2> Family:

   RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>
   RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>
   RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>

   RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>
   RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>
   RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>

******************************************************************************/

// SR (*caller)(S1,S2) -> DR (*callee)(D1,D2)

template <class SR, class S1, class S2, class DR, class D1, class D2>
// virtual
SR 
RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT6(RWFunctorR2GImp,SR,S1,S2,DR,D1,D2,run(S1,S2) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (*function_)(s1,s2);
#else
  return SR((*function_)(s1,s2));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1)

template <class SR, class S1, class S2, class DR, class D1, class D2, class A1>
// virtual
SR
RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT7(RWFunctorR2GA1Imp,SR,S1,S2,DR,D1,D2,A1,run(S1,S2) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (*function_)(s1,s2,a1_);
#else
  return SR((*function_)(s1,s2,a1_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1,A2)

template <class SR, class S1, class S2, class DR, class D1, class D2, class A1, class A2>
// virtual
SR 
RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT8(RWFunctorR2GA2Imp,SR,S1,S2,DR,D1,D2,A1,A2,run(S1,S2) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (*function_)(s1,s2,a1_,a2_);
#else
  return SR((*function_)(s1,s2,a1_,a2_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2)

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2>
// virtual
SR 
RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT7(RWFunctorR2MImp,SR,S1,S2,Callee,DR,D1,D2,run(S1,S2) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(s1,s2);
#else
  return SR((callee_.*function_)(s1,s2));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1)

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1>
// virtual
SR 
RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT8(RWFunctorR2MA1Imp,SR,S1,S2,Callee,DR,D1,D2,A1,run(S1,S2) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(s1,s2,a1_);
#else
  return SR((callee_.*function_)(s1,s2,a1_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1,A2)

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2>
// virtual
SR 
RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT9(RWFunctorR2MA2Imp,SR,S1,S2,Callee,DR,D1,D2,A1,A2,run(S1,S2) const:SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(s1,s2,a1_,a2_);
#else
  return SR((callee_.*function_)(s1,s2,a1_,a2_));  // stops Borland from losing temporary
#endif   
}

#endif // __RWTHRFUNCR2_CC__
