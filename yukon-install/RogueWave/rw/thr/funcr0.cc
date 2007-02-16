#if !defined(__RWTHRFUNCR0_CC__)
#  define __RWTHRFUNCR0_CC__
/*****************************************************************************
 *
 * funcr0.cc
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

funcr0.cc  - Out-of-line function definitions for:

   RWFunctorR0<SR> - Handle class for RWFunctorR0Imp functor family
   RWFunctorR0Imp<SR> - Base class body for FunctorR0 functor family

   RWFunctorR0GImp<SR,DR>
   RWFunctorR0GA1Imp<SR,DR,A1>
   RWFunctorR0GA2Imp<SR,DR,A1,A2>
   RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>

   RWFunctorR0MImp<SR,Callee,DR>
   RWFunctorR0MA1Imp<SR,Callee,DR,A1>
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>
   RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>
            
See Also:

   funcr0.h - Class declarations.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor1Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if defined(__RWTHRFUNCR0_H__)
#     include <rw/thr/funcr0.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWFunctorR0,SR)

RW_THR_IMPLEMENT_TRACEABLE_T2(RWFunctorR0GImp,SR,DR)
RW_THR_IMPLEMENT_TRACEABLE_T3(RWFunctorR0GA1Imp,SR,DR,A1)
RW_THR_IMPLEMENT_TRACEABLE_T4(RWFunctorR0GA2Imp,SR,DR,A1,A2)
RW_THR_IMPLEMENT_TRACEABLE_T5(RWFunctorR0GA3Imp,SR,DR,A1,A2,A3)

RW_THR_IMPLEMENT_TRACEABLE_T3(RWFunctorR0MImp,SR,Callee,DR)
RW_THR_IMPLEMENT_TRACEABLE_T4(RWFunctorR0MA1Imp,SR,Callee,DR,A1)
RW_THR_IMPLEMENT_TRACEABLE_T5(RWFunctorR0MA2Imp,SR,Callee,DR,A1,A2)
RW_THR_IMPLEMENT_TRACEABLE_T6(RWFunctorR0MA3Imp,SR,Callee,DR,A1,A2,A3)

template <class SR>
SR
RWFunctorR0<SR>::operator()(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWFunctorR0<SR>,operator()(void):SR);
   // throws RWTHRInvalidPointer if no imp assigned!
   return body().run();
}

/******************************************************************************

RWFunctorR0Imp<SR> Family:

   RWFunctorR0GImp<SR,DR>
   RWFunctorR0GA1Imp<SR,DR,A1>
   RWFunctorR0GA2Imp<SR,DR,A1,A2>
   RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>

   RWFunctorR0MImp<SR,Callee,DR>
   RWFunctorR0MA1Imp<SR,Callee,DR,A1>
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>
   RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>

******************************************************************************/

// SR (*caller)(void) -> DR (*callee)(void)

template <class SR, class DR>
// virtual
SR 
RWFunctorR0GImp<SR,DR>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWFunctorR0GImp,SR,DR,run(void) const: SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
  return (*function_)();
#else
  return SR((*function_)());  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(void) -> DR (*callee)(A1)

template <class SR, class DR, class A1>
// virtual
SR
RWFunctorR0GA1Imp<SR,DR,A1>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWFunctorR0GA1Imp,SR,DR,A1,run(void) const: SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
  return (*function_)(a1_);
#else
  return SR((*function_)(a1_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(void) -> DR (*callee)(A1,A2)

template <class SR, class DR, class A1, class A2>
// virtual
SR 
RWFunctorR0GA2Imp<SR,DR,A1,A2>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT4(RWFunctorR0GA2Imp,SR,DR,A1,A2,run(void) const: SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
  return (*function_)(a1_,a2_);
#else
  return SR((*function_)(a1_,a2_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(void) -> DR (*callee)(A1,A2,A3)

template <class SR, class DR, class A1, class A2, class A3>
// virtual
SR 
RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT5(RWFunctorR0GA3Imp,SR,DR,A1,A2,A3,run(void) const: SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
  return (*function_)(a1_,a2_,a3_);
#else
  return SR((*function_)(a1_,a2_,a3_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(void) -> DR (Callee::*func)(void)

template <class SR, class Callee, class DR>
// virtual
SR 
RWFunctorR0MImp<SR,Callee,DR>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWFunctorR0MImp,SR,Callee,DR,run(void) const: SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)();
#else
  return SR((callee_.*function_)());  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(void) -> DR (Callee::*func)(A1)

template <class SR, class Callee, class DR, class A1>
// virtual
SR 
RWFunctorR0MA1Imp<SR,Callee,DR,A1>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT4(RWFunctorR0MA1Imp,SR,Callee,DR,A1,run(void) const: SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(a1_);
#else
  return SR((callee_.*function_)(a1_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(void) -> DR (Callee::*func)(A1,A2)

template <class SR, class Callee, class DR, class A1, class A2>
// virtual
SR 
RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT5(RWFunctorR0MA2Imp,SR,Callee,DR,A1,A2,run(void) const: SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(a1_,a2_);
#else
  return SR((callee_.*function_)(a1_,a2_));  // stops Borland from losing temporary
#endif   
}

// SR (*caller)(void) -> DR (Callee::*func)(A1,A2,A3)

template <class SR, class Callee, class DR, class A1, class A2, class A3>
// virtual
SR 
RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>::run(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT6(RWFunctorR0MA3Imp,SR,Callee,DR,A1,A2,A3,run(void) const: SR);
#ifndef RW_THR_THREAD_API_WIN32_BORLAND_CPP
   return (callee_.*function_)(a1_,a2_,a3_);
#else
  return SR((callee_.*function_)(a1_,a2_,a3_));  // stops Borland from losing temporary
#endif   
}

#endif // __RWTHRFUNCR0_CC__
