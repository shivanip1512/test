#if !defined( __RWIOU_H__ )
#define __RWIOU_H__
/*****************************************************************************
 *
 * iou.h
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
 
iou.h - Helper functions for creating IOU callbacks and RWEscrowImp 
   instances:

      template<class Redeemable> rwMakeThreadIOU(const Redeemable *)
      rwMakeIOUCallback*() 
 
See Also:
 
   ioureslt.h   - Definition of RWIOUResult<R>
   iouescro.h   - Definition of RWIOUEscrow<R>
   escrohnd.h   - Definition of RWEscrowHandle<R>
   escrothr.h   - Definition of RWThreadEscrowImp<R>
   escroimp.h   - Definition of RWEscrowImp<R>
 
*****************************************************************************/

#include <rw/thr/escrothr.h>
#include <rw/thr/iouescro.h>
#include <rw/thr/ioureslt.h>
#include <rw/thr/func1.h>

/******************************************************************************
   Function for creating an RWThreadEscrowImp<R>. This function returns an
   RWEscrowHandle<R> which can be used to initialize either an RWIOUResult<R> 
   or an RWIOUEscrow<R>:

   rwMakeThreadIOU(const RedeemableType *)

   Example usage:
      RWIOUResult<int> ioures = rwMakeThreadIOU((int*)0);
        - or -
      RWIOUEscrow<int> iouesc = rwMakeThreadIOU((int*)0);

   You can also create a Threaded escrow, which is the only type that we
   currently have, by calling the static make function on RWThreadEscrowImp<R>.
   
   For example:
  
      RWIOUResult<int> ioures = RWThreadEscrowImp<int>::make();
        - or -
      RWIOUEscrow<int> iouesc = RWThreadEscrowImp<int>::make();

******************************************************************************/

// Create an RWThreadEscrowImp<R>.
template <class Redeemable>
inline
RWEscrowHandle<Redeemable>
rwMakeThreadIOU(const Redeemable *r)
{
   (void)r;
   return RWThreadEscrowImp<Redeemable>::make();
}

/******************************************************************************
   Template functions and macros for creating IOU callback functors:
   rwMakeIOUCallback*()
******************************************************************************/

// Macro and template function to create an IOU callback functor from a global 
// function which takes an RWIOUResult<R> as its only argument.

#define rwMakeIOUCallbackG(function, Redeemable)  \
   RWFunctor1GImp< RWEscrowHandle<Redeemable >,   \
                   void, RWIOUResult<Redeemable > >::make(function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
  
// Template function to create an IOU callback functor from a global function
// which takes an RWIOUResult<R> as its only argument.
template <class Redeemable>
inline
RWFunctor1< RWEscrowHandle<Redeemable> >
rwMakeIOUCallback(void (*function)(RWIOUResult<Redeemable>))
{
   return rwMakeIOUCallbackG(function, Redeemable); 
}

#  endif

// Macro and template function to create an IOU callback functor from a member 
// function which takes an RWIOUResult<R> as its only argument.
 
#define rwMakeIOUCallbackM(Callee,callee,function,Redeemable) \
   RWFunctor1MImp<RWEscrowHandle<Redeemable >,                \
                  Callee, void,                               \
                  RWIOUResult<Redeemable > >::make(callee, function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
  
template <class Redeemable, class Callee>
inline
RWFunctor1< RWEscrowHandle<Redeemable> >
rwMakeIOUCallback(Callee& callee,
              void (Callee::*function)(RWIOUResult<Redeemable>))
{
   return rwMakeIOUCallbackM(Callee,callee,function,Redeemable); 
}

#  endif

// Macro and template function to create an IOU callback functor from a global 
// function which takes an RWIOUResult<R>, and an additional argument as
// parameters.

#define rwMakeIOUCallbackGA1(function, Redeemable, A1, a1) \
   RWFunctor1GA1Imp<RWEscrowHandle<Redeemable >, void,     \
                    RWIOUResult<Redeemable >, A1 >::make(function, a1)  

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
  
template<class Redeemable, class A1>
inline
RWFunctor1< RWEscrowHandle<Redeemable> >
rwMakeIOUCallback(void (*function)(RWIOUResult<Redeemable>, A1),
              A1 a1)
{
   return rwMakeIOUCallbackGA1(function, Redeemable, A1, a1); 
}

#  endif

// Macro and template function to create an IOU callback functor from a member 
// function which takes an RWIOUResult<R>, and an additional argument as
// parameters.

#define rwMakeIOUCallbackMA1(Callee,callee,function,Redeemable,A1,a1) \
   RWFunctor1MA1Imp<RWEscrowHandle<Redeemable >, Callee, void,        \
                    RWIOUResult<Redeemable >, A1 >::make(callee, function, a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
  
template <class Redeemable, class Callee, class A1>
inline
RWFunctor1< RWEscrowHandle<Redeemable> >
rwMakeIOUCallback(Callee& callee,
                  void (Callee::*function)(RWIOUResult<Redeemable>, A1),
                  A1 a1)
{
   return rwMakeIOUCallbackMA1(Callee,callee,function,Redeemable,A1,a1); 
}

#endif

// Macro and template function to create an IOU callback functor from a global 
// function which takes an RWIOUResult<R>, and two additional arguments as
// parameters.

#define rwMakeIOUCallbackGA2(function, Redeemable, A1, a1, A2, a2) \
   RWFunctor1GA2Imp<RWEscrowHandle<Redeemable >, void,             \
                    RWIOUResult<Redeemable >, A1, A2 >::make(function, a1, a2)  

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
  
template<class Redeemable, class A1, class A2>
inline
RWFunctor1< RWEscrowHandle<Redeemable> >
rwMakeIOUCallback(void (*function)(RWIOUResult<Redeemable>, A1, A2),
                  A1 a1,
                  A2 a2)
{
   return rwMakeIOUCallbackGA2(function, Redeemable, A1, a1, A2, a2); 
}

#  endif

// Macro and template function to create an IOU callback functor from a member 
// function which takes an RWIOUResult<R>, and two additional arguments as
// parameters.

#define rwMakeIOUCallbackMA2(Callee,callee,function,Redeemable,A1,a1,A2,a2) \
   RWFunctor1MA2Imp<RWEscrowHandle<Redeemable >, Callee, void,              \
                    RWIOUResult<Redeemable >,                               \
                    A1, A2 >::make(callee, function, a1, a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
  
template <class Redeemable, class Callee, class A1, class A2>
inline
RWFunctor1< RWEscrowHandle<Redeemable> >
rwMakeIOUCallback(Callee& callee,
                  void (Callee::*function)(RWIOUResult<Redeemable>, A1, A2),
                  A1 a1,
                  A2 a2)
{
   return rwMakeIOUCallbackMA2(Callee,callee,function,Redeemable,A1,a1,A2,a2); 
}

#endif



#endif

