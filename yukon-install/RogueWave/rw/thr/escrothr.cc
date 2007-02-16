#if !defined(__RWTHRESCROTHR_CC__)
#  define __RWTHRESCROTHR_CC__
/*****************************************************************************
 *
 * escrothr.cc
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

escrothr.cc  - Out-of-line function definitions for:

   RWThreadEscrowImp<Redeemable> - Multi-thread escrow implementation. 
         
See Also:

   escrothr.h - Class declarations.

******************************************************************************/

#  if !defined(__RWTHRESCROSYN_H__)
#     include <rw/thr/escrothr.h>
#  endif

#  if !defined(__RWTHRTHRFUNC_H__)
#     include <rw/thr/thrfunc.h>
#  endif

#  if !defined(__RWTHRTHRMSG_H__)
#     include <rw/thr/thrmsg.h>
#  endif



RW_THR_IMPLEMENT_TRACEABLE_T1(RWThreadEscrowImp,Redeemable)

template <class Redeemable>
RWThreadEscrowImp<Redeemable>::RWThreadEscrowImp(void)
   RWTHRTHROWSANY
   :
#if !defined(RW_THR_NO_THIS_IN_INITIALIZER_LIST)
     closedCondition_(this->mutex_)
#else
     closedCondition_(mutex_)
#endif
{
   RWTHRTRACEMF(RWThreadEscrowImp<Redeemable>,RWThreadEscrowImp(void));
}

template <class Redeemable>
RWThreadEscrowImp<Redeemable>::~RWThreadEscrowImp(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWThreadEscrowImp<Redeemable>,~RWThreadEscrowImp(void));
}

template <class Redeemable>
void
RWThreadEscrowImp<Redeemable>::_abort(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWThreadEscrowImp<Redeemable>,_abort(void):void);
   
   // Signal threads blocked in _redeem().
   closedCondition_.signalAll();
}

template <class Redeemable>
void
RWThreadEscrowImp<Redeemable>::_close(const Redeemable& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadEscrowImp<Redeemable>,_close(const Redeemable&):void);
   RWTHRASSERT(!_closed()); 

   // Make a copy of the value.
   valueP_ = new Redeemable(value);

   // Signal threads blocked in _redeem().
   closedCondition_.signalAll();
}

template <class Redeemable>
RWBoolean 
RWThreadEscrowImp<Redeemable>::_closed(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadEscrowImp<Redeemable>,_closed(void) const:RWBoolean);
   return (valueP_.isValid());
}

template <class Redeemable>
RWEscrowHandle<Redeemable>
RWThreadEscrowImp<Redeemable>::_newInstance(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadEscrowImp<Redeemable>,_newInstance(void) const:RWEscrowHandle<Redeemable>);
   return new RWThreadEscrowImp<Redeemable>;
}
   
template <class Redeemable>
Redeemable
RWThreadEscrowImp<Redeemable>::_redeem(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadEscrowImp<Redeemable>,_redeem(void) const:Redeemable);

   // Cast away constness.
   RWThreadEscrowImp<Redeemable>* me = RW_THR_CONST_CAST(RWThreadEscrowImp<Redeemable>*, this);
   
   while (!this->isRedeemable()) {
      me->closedCondition_.wait();
   }

   if (this->isAborted())
      this->throwAbort();

   if (this->isException())
      this->throwStoredException();      

   RWTHRASSERT(valueP_.isValid());

   return *valueP_;
}

template <class Redeemable>
void
RWThreadEscrowImp<Redeemable>::_setException(const RWTHRxmsg&)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWThreadEscrowImp<Redeemable>,_setException(const RWTHRxmsg&):void);
   
   // Signal threads blocked in _redeem().
   closedCondition_.signalAll();
}

template <class Redeemable>
// static
#if defined(RW_THR_INLINE_STATIC_TEMPLATE_MEMBER_FUNCTIONS)
inline
#endif
RWEscrowHandle<Redeemable>
RWThreadEscrowImp<Redeemable>::make(void)
   RWTHRTHROWSANY
{
   RWTHRTRACESMF(RWThreadEscrowImp<Redeemable>,make(void):RWEscrowHandle<Redeemable>);
   return new RWThreadEscrowImp<Redeemable>;
}

template <class Redeemable>
// static
#if defined(RW_THR_INLINE_STATIC_TEMPLATE_MEMBER_FUNCTIONS)
inline
#endif
RWEscrowHandle<Redeemable>
RWThreadEscrowImp<Redeemable>::make(const Redeemable& immediateValue)
   RWTHRTHROWSANY
{
   RWTHRTRACESMF(RWThreadEscrowImp<Redeemable>,make(const Redeemable&):RWEscrowHandle<Redeemable>);
   
   RWEscrowHandle<Redeemable> escrowHandle =
      new RWThreadEscrowImp<Redeemable>();

   // Close escrow with immediateValue
   RWIOUEscrow<Redeemable> iouEscrow(escrowHandle);
   iouEscrow.close(immediateValue);

   return escrowHandle;  
}


#endif // __RWTHRESCROTHR_CC__
