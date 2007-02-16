#if !defined(__RWTHRESCROTHR_H__)
#  define __RWTHRESCROTHR_H__
/*****************************************************************************
 *
 * escrothr.h
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

escrothr.h - Class declarations for:

   RWThreadEscrowImp<Redeemable> - Multi-thread escrow implementation. 

See Also:

   escrothr.cc   - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRESCROIMP_H__)
#     include <rw/thr/escroimp.h>
#  endif

#  if !defined(__RWTHRIOUESCRO_H__)
#     include <rw/thr/iouescro.h>
#  endif

#  if !defined(__RWTHRCONDTION_H__)
#     include <rw/thr/condtion.h>
#  endif

template <class Redeemable>
class RWTHRTExport RWThreadEscrowImp :
   public RWEscrowImp<Redeemable> {

   RW_THR_DECLARE_TRACEABLE

   // Member Variables
   public:

      typedef Redeemable RedeemableType;

   protected:

      RWOnlyPointer<Redeemable> valueP_;
      RWCondition closedCondition_;

   // Member Functions
   public:

      virtual
      ~RWThreadEscrowImp(void)
         RWTHRTHROWSNONE;

      // Create a new RWThreadEscrowImp<Redeemable>. Return an
      // RWEscrowHandle<Redeemable> handle which can be used to create
      // an RWIOUResult<Redeemable> or RWIOUEscrow<Redeemable> handle to
      // the newly created Escrow implementation.
      static
      RWEscrowHandle<Redeemable>
      make(void)
         RWTHRTHROWSANY;
   
      // Create an RWThreadEscrowImp<Redeemable> and assign it to an
      // RWEscrowHandle<Redeemable>. The escrow is closed immediately
      // with "immediateValue". The returned RWEscrowHandle<Redeemable>
      // can be used to initialize an RWIOUEscrow<Redeemable> or and
      // RWIOUResult<Redeemable>. Once the escrow is made it is 
      // immediately redeemable.
      static
      RWEscrowHandle<Redeemable>
      make(const Redeemable& immediateValue)
         RWTHRTHROWSANY;
 
      // The following virtual functions are called by the base class
      // RWEscrowImp<Redeemable>. It is assumed that a guard (mutex_) has been
      // acquired before these functions are called, so access to instance
      // members is safe.

      // Escrow operation aborted. Release threads blocked on redeem
      // so they can reap the status.
      virtual
      void
      _abort(void)
         RWTHRTHROWSNONE;

      // Implementation of pure virtual function _close defined in
      // RWEscrowImp<Redeemable>. Makes a local copy of the value for
      // later redemption. Unblocks any threads blocked in _redeem.
      virtual
      void 
      _close(const Redeemable &value)
         RWTHRTHROWSANY;

      // Implementation of pure virtual function _closed defined in
      // RWEscrowImp<Redeemable>. Returns TRUE if a value has been stored.
      virtual
      RWBoolean 
      _closed(void) const
         RWTHRTHROWSANY;

      // Returns new copy of this RWEscrowImp<R> implementation. 
      virtual
      RWEscrowHandle<Redeemable>
      _newInstance(void) const
         RWTHRTHROWSANY;

      // Implementation of pure virtual function _redeem defined in
      // RWEscrowImp<Redeemable>. Returns the internally stored value
      // if available. Otherwise blocks until the value is available.
      virtual
      Redeemable
      _redeem(void) const
         RWTHRTHROWSANY;

      // Escrow in error. Release threads blocked on redeem so they
      // can reap the status.
      virtual
      void 
      _setException(const RWTHRxmsg& msg)
         RWTHRTHROWSNONE;

   private:
      
      // Constuctor private. Must use make to create instances.
      RWThreadEscrowImp(void)
         RWTHRTHROWSANY;

   
};



#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/escrothr.cc>
#  endif

#endif // __RWTHRESCROTHR_H__
