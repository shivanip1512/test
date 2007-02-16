#if !defined(__RWTHRIOUTRAP_CC__)
#  define __RWTHRIOUTRAP_CC__
/*****************************************************************************
 *
 * ioutrap.cc
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

ioutrap.cc - Out-of-line function definitions for:

   RWIOUTrap - Handle class for IOU trap.
   RWIOUTrapImp - Body class for IOU trap.

See Also:

   ioutrap.h - Class declarations.

******************************************************************************/

#  if !defined(__RWTHRIOUTRAP_H__)
#    include<rw/thr/ioutrap.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWIOUTrap,Redeemable)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWIOUTrapImp,Redeemable)

template <class Redeemable>
void
RWIOUTrap<Redeemable>::setTrap(RWIOUResult<Redeemable> iou) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,setTrap(RWIOUResult<Redeemable>):void);
   body().setTrap(iou);
}


template <class Redeemable>
void
RWIOUTrap<Redeemable>::trip(RWIOUResult<Redeemable> iou, RWIOUTrap<Redeemable> iouTrap)
   RWTHRTHROWSANY
{
   RWTHRTRACESMF(RWIOUTrap<Redeemable>,trip(RWIOUResult<Redeemable>):void);
   iouTrap.body().trip(iou);
}



template <class Redeemable>
void
RWIOUTrapImp<Redeemable>::trip(RWIOUResult<Redeemable> iou)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrapImp<Redeemable>,trip(RWIOUResult<Redeemable>):void);
   iouQueue_.write(iou);
}


template <class Redeemable>
void RWIOUTrapImp<Redeemable>::setTrap(RWIOUResult<Redeemable>& iou)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrapImp<Redeemable>,setTrap(RWIOUResult<Redeemable>):void);
   RWFunctor1< RWEscrowHandle<Redeemable> > trapCallback;
   trapCallback =
   RWFunctor1GA1Imp<RWEscrowHandle<Redeemable>,
                    void, 
                    RWIOUResult<Redeemable>, 
                    RWIOUTrap<Redeemable> >::make(RWIOUTrap<Redeemable>::trip,RWIOUTrap<Redeemable>(this));
   iou.addCallback(trapCallback);
}


#endif // __RWTHRIOUTRAP_CC__

