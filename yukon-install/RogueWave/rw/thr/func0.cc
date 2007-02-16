#if !defined(__RWTHRFUNC0_CC__)
#  define __RWTHRFUNC0_CC__
/*****************************************************************************
 *
 * func0.cc
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

func0.cc  - Out-of-line function definitions for:

   RWFunctor0GImp<DR>
   RWFunctor0GA1Imp<DR,A1>
   RWFunctor0GA2Imp<DR,A1,A2>
   RWFunctor0GA3Imp<DR,A1,A2,A3>

   RWFunctor0MImp<Callee,DR>
   RWFunctor0MA1Imp<Callee,DR,A1>
   RWFunctor0MA2Imp<Callee,DR,A1,A2>
   RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>
            
See Also:

   func0.h - Class declarations.

   functor0.h - Declares RWFunctor0 and RWFunctor0Imp.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor1Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if !defined(__RWTHRFUNC0_H__)
#     include <rw/thr/func0.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWFunctor0GImp,DR)
RW_THR_IMPLEMENT_TRACEABLE_T2(RWFunctor0GA1Imp,DR,A1)
RW_THR_IMPLEMENT_TRACEABLE_T3(RWFunctor0GA2Imp,DR,A1,A2)
RW_THR_IMPLEMENT_TRACEABLE_T4(RWFunctor0GA3Imp,DR,A1,A2,A3)

RW_THR_IMPLEMENT_TRACEABLE_T2(RWFunctor0MImp,Callee,DR)
RW_THR_IMPLEMENT_TRACEABLE_T3(RWFunctor0MA1Imp,Callee,DR,A1)
RW_THR_IMPLEMENT_TRACEABLE_T4(RWFunctor0MA2Imp,Callee,DR,A1,A2)
RW_THR_IMPLEMENT_TRACEABLE_T5(RWFunctor0MA3Imp,Callee,DR,A1,A2,A3)

/******************************************************************************

RWFunctor0Imp Family:

   RWFunctor0GImp<DR>
   RWFunctor0GA1Imp<DR,A1>
   RWFunctor0GA2Imp<DR,A1,A2>
   RWFunctor0GA3Imp<DR,A1,A2,A3>

   RWFunctor0MImp<Callee,DR>
   RWFunctor0MA1Imp<Callee,DR,A1>
   RWFunctor0MA2Imp<Callee,DR,A1,A2>
   RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>

******************************************************************************/

// void (*caller)(void) -> DR (*callee)(void)

template <class DR>
// virtual
void 
RWFunctor0GImp<DR>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWFunctor0GImp,DR,run(void) const:void);
   // Ignore return value (if any)
   (*function_)();
}

// void (*caller)(void) -> DR (*callee)(A1)

template <class DR,class A1>
// virtual
void 
RWFunctor0GA1Imp<DR,A1>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWFunctor0GA1Imp,DR,A1,run(void) const:void);
   // Ignore return value (if any)
   (*function_)(a1_);
}

// void (*caller)(void) -> DR (*callee)(A1,A2)

template <class DR, class A1, class A2>
// virtual
void 
RWFunctor0GA2Imp<DR,A1,A2>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWFunctor0GA2Imp,DR,A1,A2,run(void) const:void);
   // Ignore return value (if any)
   (*function_)(a1_,a2_);
}

// void (*caller)(void) -> DR (*callee)(A1,A2,A3)

template <class DR, class A1, class A2, class A3>
// virtual
void 
RWFunctor0GA3Imp<DR,A1,A2,A3>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT4(RWFunctor0GA3Imp,DR,A1,A2,A3,run(void) const:void);
   // Ignore return value (if any)
   (*function_)(a1_,a2_,a3_);
}

// void (*caller)(void) -> DR (Callee::*func)(void)

template <class Callee, class DR>
// virtual
void 
RWFunctor0MImp<Callee,DR>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWFunctor0MImp,Callee,DR,run(void) const:void);
   // Ignore return value (if any)
   (callee_.*function_)();
}

// void (*caller)(void) -> DR (Callee::*func)(A1)

template <class Callee, class DR, class A1>
// virtual
void 
RWFunctor0MA1Imp<Callee,DR,A1>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWFunctor0MA1Imp,Callee,DR,A1,run(void) const:void);
   // Ignore return value (if any)
   (callee_.*function_)(a1_);
}

// void (*caller)(void) -> DR (Callee::*func)(A1,A2)

template <class Callee, class DR, class A1, class A2>
// virtual
void 
RWFunctor0MA2Imp<Callee,DR,A1,A2>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT4(RWFunctor0MA2Imp,Callee,DR,A1,A2,run(void) const:void);
   // Ignore return value (if any)
   (callee_.*function_)(a1_,a2_);
}

// void (*caller)(void) -> DR (Callee::*func)(A1,A2,A3)

template <class Callee, class DR, class A1, class A2, class A3>
// virtual
void 
RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT5(RWFunctor0MA3Imp,Callee,DR,A1,A2,A3,run(void) const:void);
   // Ignore return value (if any)
   (callee_.*function_)(a1_,a2_,a3_);
}

#endif // __RWTHRFUNC0_CC__
