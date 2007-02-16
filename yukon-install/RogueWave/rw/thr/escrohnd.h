#if !defined(__RWTHRESCROHND_H__)
#  define __RWTHRESCROHND_H__
/*****************************************************************************
 *
 * escrohnd.h
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

escrohnd.h - Class declarations for:

   RWEscrowHandle<RedeemableType> - Handle to RWEscrowImp*. Base class of
      RWIOUResult<R> and RWIOUEscrow<R>. Only usable as a handle through
      derived classes. 


*****************************************************************************/

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RWTHRIOUIMP_H__)
#     include <rw/thr/escrob.h>
#  endif

template <class Redeemable>
class RWTHRTExport RWEscrowHandle : public RWTHRHandle {

   RW_THR_DECLARE_TRACEABLE
     
   // Member Variables
   public:

      typedef Redeemable   RedeemableType;
      
   // Member Functions
   public:

      ~RWEscrowHandle(void)
         RWTHRTHROWSANY;

      // Create uninitialized pointer.
      RWEscrowHandle(void)
         RWTHRTHROWSNONE;

      // Create pointer that points at an RWEscrowImp<Redeemable>.
      RWEscrowHandle(RWEscrowImpBase* imp)
         RWTHRTHROWSANY;

      // Copy constructor
      RWEscrowHandle(const RWEscrowHandle< Redeemable >& second)
         RWTHRTHROWSANY;

      // Assign this to point to same RWEscrowImp<Redeemable> as second.
      RWEscrowHandle<Redeemable>&
      operator=(const RWEscrowHandle< Redeemable >& second)
         RWTHRTHROWSANY;

};


/*****************************************************************************/

template <class Redeemable>
inline
RWEscrowHandle<Redeemable>::~RWEscrowHandle(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowHandle<Redeemable>,~RWEscrowHandle(void));
}


template <class Redeemable>
inline
RWEscrowHandle<Redeemable>::RWEscrowHandle(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowHandle<Redeemable>,RWEscrowHandle(void));  
}

template <class Redeemable>
inline
RWEscrowHandle<Redeemable>::RWEscrowHandle(RWEscrowImpBase* imp)
   RWTHRTHROWSANY
   :
      RWTHRHandle((RWTHRBody*)imp)
{
   RWTHRTRACEMF(RWEscrowHandle<Redeemable>,RWEscrowHandle(RWEscrowImpBase*));  
}

template <class Redeemable>
inline
RWEscrowHandle<Redeemable>::RWEscrowHandle(const RWEscrowHandle< Redeemable >& second)
   RWTHRTHROWSANY
   :
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWEscrowHandle<Redeemable>,RWEscrowHandle(const RWEscrowHandle<Redeemable>&));
}

template <class Redeemable>
inline
RWEscrowHandle<Redeemable>&
RWEscrowHandle<Redeemable>::operator=(const RWEscrowHandle< Redeemable >& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowHandle<Redeemable>,operator=(const RWEscrowHandle<Redeemable>&):RWEscrowHandle<Redeemable>&);
   if (&second != this)
       RWTHRHandle::operator=(second);
   return *this;
}


#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/escrohnd.cc>
#  endif


#endif // __RWTHRESCROHND_H__

