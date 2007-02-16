#if !defined(__RWTHRFUNC1_CC__)
#  define __RWTHRFUNC1_CC__
/*****************************************************************************
 *
 * func1.cc
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

func1.cc  - Out-of-line function definitions for:

   RWFunctor1<S1> - Handle class for RWFunctor1Imp functor family
   RWFunctor1Imp<S1> - Base class body for Functor1 functor family

   RWFunctor1GImp<S1,DR,D1>
   RWFunctor1GA1Imp<S1,DR,D1,A1>
   RWFunctor1GA2Imp<S1,DR,D1,A1,A2>

   RWFunctor1MImp<S1,Callee,DR,D1>
   RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>
   RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>
            
See Also:

   func1.h - Class declarations.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFuncto1Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if defined(__RWTHRFUNC1_H__)
#     include <rw/thr/func1.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWFunctor1,S1)

RW_THR_IMPLEMENT_TRACEABLE_T3(RWFunctor1GImp,S1,DR,D1)
RW_THR_IMPLEMENT_TRACEABLE_T4(RWFunctor1GA1Imp,S1,DR,D1,A1)
RW_THR_IMPLEMENT_TRACEABLE_T5(RWFunctor1GA2Imp,S1,DR,D1,A1,A2)

RW_THR_IMPLEMENT_TRACEABLE_T4(RWFunctor1MImp,S1,Callee,DR,D1)
RW_THR_IMPLEMENT_TRACEABLE_T5(RWFunctor1MA1Imp,S1,Callee,DR,D1,A1)
RW_THR_IMPLEMENT_TRACEABLE_T6(RWFunctor1MA2Imp,S1,Callee,DR,D1,A1,A2)

/******************************************************************************

RWFunctor1Imp<S1> Family:

   RWFunctor1GImp<S1,DR,D1>
   RWFunctor1GA1Imp<S1,DR,D1,A1>
   RWFunctor1GA2Imp<S1,DR,D1,A1,A2>

   RWFunctor1MImp<S1,Callee,DR,D1>
   RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>
   RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>

******************************************************************************/

// void (*caller)(S1) -> DR (*callee)(D1)

template <class S1, class DR, class D1>
// virtual
void 
RWFunctor1GImp<S1,DR,D1>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWFunctor1GImp,S1,DR,D1,run(S1) const:void);
   (*function_)(s1);
}

// void (*caller)(S1) -> DR (*callee)(D1,A1)

template <class S1, class DR, class D1, class A1>
// virtual
void
RWFunctor1GA1Imp<S1,DR,D1,A1>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT4(RWFunctor1GA1Imp,S1,DR,D1,A1,run(S1) const:void);
   (*function_)(s1,a1_);
}

// void (*caller)(S1) -> DR (*callee)(D1,A1,A2)

template <class S1, class DR, class D1, class A1, class A2>
// virtual
void 
RWFunctor1GA2Imp<S1,DR,D1,A1,A2>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT5(RWFunctor1GA2Imp,S1,DR,D1,A1,A2,run(S1) const:void);
   (*function_)(s1,a1_,a2_);
}

// void (*caller)(S1) -> DR (Callee::*func)(D1)

template <class S1, class Callee, class DR, class D1>
// virtual
void 
RWFunctor1MImp<S1,Callee,DR,D1>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT4(RWFunctor1MImp,S1,Callee,DR,D1,run(S1) const:void);
   (callee_.*function_)(s1);
}

// void (*caller)(S1) -> DR (Callee::*func)(D1,A1)

template <class S1, class Callee, class DR, class D1, class A1>
// virtual
void 
RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT5(RWFunctor1MA1Imp,S1,Callee,DR,D1,A1,run(S1) const:void);
   (callee_.*function_)(s1,a1_);
}

// void (*caller)(S1) -> DR (Callee::*func)(D1,A1,A2)

template <class S1, class Callee, class DR, class D1, class A1, class A2>
// virtual
void 
RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>::run(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT6(RWFunctor1MA2Imp,S1,Callee,DR,D1,A1,A2,run(S1) const:void);
   (callee_.*function_)(s1,a1_,a2_);
}

#endif // __RWTHRFUNC1_CC__
