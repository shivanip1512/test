#if !defined(__RWTHRFUNC2_CC__)
#  define __RWTHRFUNC2_CC__
/*****************************************************************************
 *
 * func2.cc
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

func2.cc  - Out-of-line function definitions for:

   RWFunctor2<S1,S2> - Handle class for RWFunctor2Imp functor family
   RWFunctor2Imp<S1,S2> - Base class body for Functor2 functor family

   RWFunctor2GImp<S1,S2,DR,D1,D2>
   RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>
   RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>

   RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>
   RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>
   RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>
            
See Also:

   func2.h - Class declarations.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor1Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if defined(__RWTHRFUNC2_H__)
#     include <rw/thr/func2.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T2(RWFunctor2,S1,S2)

RW_THR_IMPLEMENT_TRACEABLE_T5(RWFunctor2GImp,S1,S2,DR,D1,D2)
RW_THR_IMPLEMENT_TRACEABLE_T6(RWFunctor2GA1Imp,S1,S2,DR,D1,D2,A1)
RW_THR_IMPLEMENT_TRACEABLE_T7(RWFunctor2GA2Imp,S1,S2,DR,D1,D2,A1,A2)

RW_THR_IMPLEMENT_TRACEABLE_T6(RWFunctor2MImp,S1,S2,Callee,DR,D1,D2)
RW_THR_IMPLEMENT_TRACEABLE_T7(RWFunctor2MA1Imp,S1,S2,Callee,DR,D1,D2,A1)
RW_THR_IMPLEMENT_TRACEABLE_T8(RWFunctor2MA2Imp,S1,S2,Callee,DR,D1,D2,A1,A2)

/******************************************************************************

RWFunctor2Imp<S1,S2> Family:

   RWFunctor2GImp<S1,S2,DR,D1,D2>
   RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>
   RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>

   RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>
   RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>
   RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>

******************************************************************************/

// void (*caller)(S1,S2) -> DR (*callee)(D1,D2)

template <class S1, class S2, class DR, class D1, class D2>
// virtual
void 
RWFunctor2GImp<S1,S2,DR,D1,D2>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT5(RWFunctor2GImp,S1,S2,DR,D1,D2,run(S1,S2) const:void);
   (*function_)(s1,s2);
}

// void (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1)

template <class S1, class S2, class DR, class D1, class D2, class A1>
// virtual
void
RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT6(RWFunctor2GA1Imp,S1,S2,DR,D1,D2,A1,run(S1,S2) const:void);
   (*function_)(s1,s2,a1_);
}

// void (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1,A2)

template <class S1, class S2, class DR, class D1, class D2, class A1, class A2>
// virtual
void 
RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT7(RWFunctor2GA2Imp,S1,S2,DR,D1,D2,A1,A2,run(S1,S2) const:void);
   (*function_)(s1,s2,a1_,a2_);
}

// void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2)

template <class S1, class S2, class Callee, class DR, class D1, class D2>
// virtual
void 
RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT6(RWFunctor2MImp,S1,S2,Callee,DR,D1,D2,run(S1,S2) const:void);
   (callee_.*function_)(s1,s2);
}

// void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1)

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1>
// virtual
void 
RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT7(RWFunctor2MA1Imp,S1,S2,Callee,DR,D1,D2,A1,run(S1,S2) const:void);
   (callee_.*function_)(s1,s2,a1_);
}

// void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1,A2)

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2>
// virtual
void 
RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>::run(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT8(RWFunctor2MA2Imp,S1,S2,Callee,DR,D1,D2,A1,A2,run(S1,S2) const:void);
   (callee_.*function_)(s1,s2,a1_,a2_);
}

#endif // __RWTHRFUNC2_CC__
