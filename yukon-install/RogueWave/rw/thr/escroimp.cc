#if !defined(__RWTHRESCROIMP_CC__)
#  define __RWTHRESCROIMP_CC__
/*****************************************************************************
 *
 * escroimp.cc
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

escroimp.cc  - Out-of-line function definitions for:

   RWEscrowImp<Redeemable> - Escrow implementation base class.
         
See Also:

   escroimp.h - Class declarations.

******************************************************************************/

#  if !defined(__RWTHRESCROIMP_H__)
#     include <rw/thr/escroimp.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWEscrowImp,Redeemable)

template <class Redeemable>
RWEscrowImp<Redeemable>::~RWEscrowImp(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,~RWEscrowImp(void));
}

template <class Redeemable>
RWEscrowImp<Redeemable>::RWEscrowImp(void)
   RWTHRTHROWSNONE
   :
      isRedeemed_(FALSE),
      isAborted_(FALSE),
      cbList_(mutex())    // mutex_ comes from RWMonitor<> base
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,RWEscrowImp(void));  
}

template <class Redeemable>
void
RWEscrowImp<Redeemable>::abort(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,abort(void):void);
   LockGuard lock(monitor());

   if (!isRedeemable()) {
      isAborted_ = TRUE;
      _abort();          // child hook.
      lock.release();    // since callbacks will be calling redeem() on this.
      fireAllCallbacks(); // allow anyone waiting to reap status.
   }
   // else ignore... the final state of the Escrow has been set.
}

template <class Redeemable>
void
RWEscrowImp<Redeemable>::addCallback(const RWIOUResultCallback& iouResultCallback)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,addCallback(const RWIOUResultCallback&):void);
   LockGuard lock(monitor());

   // If the escrow isn't redeemable yet, then store the callback
   // in the callback list.
   if (!isRedeemable()) {
      cbList_.add(iouResultCallback, RW_THR_CALL_ONCE);
   }
   else {
      // An exception, abort condition, or valid result is already available;
      // immediately fire-off the callback
      lock.release(); // Since callbacks will likely call redeem() on this.
      iouResultCallback(RWEscrowHandle<Redeemable>(this));
   }
}

template <class Redeemable>
RWBoolean
RWEscrowImp<Redeemable>::aborted(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,aborted(void) const:RWBoolean);
   LockGuard lock(monitor());
   return isAborted();
}

template <class Redeemable>
void 
RWEscrowImp<Redeemable>::close(Redeemable value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,close(Redeemable value):void);
   LockGuard lock(monitor());

   if (isAborted())
      throw RWTHROperationAborted();
   if (isException())
      (*exception_).raise();
   if (!_closed())
      _close(value);   // close!
   else
      throw RWTHREscrowAlreadyClosed();
   
   if (_closed()) {   // ensure that it was actually closed.
      lock.release(); // since callbacks will be calling redeem() on this.
      fireAllCallbacks();
   }
}

template <class Redeemable>
RWBoolean
RWEscrowImp<Redeemable>::closeable(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,closeable(void) const:RWBoolean);
   LockGuard lock(monitor());   
   return !isRedeemable();
}

template <class Redeemable>
RWBoolean
RWEscrowImp<Redeemable>::closed(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,closed(void) const:RWBoolean);
   LockGuard lock(monitor());
   return _closed();
}

template <class Redeemable>
void
RWEscrowImp<Redeemable>::fireAllCallbacks(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,fireAllCallbacks(void):void);
   RWTHRASSERT(!isAcquired()); // mutex must NOT already be acquired
   
   // We don't guard since callbacks are going to be calling redeem() on
   // this. If we guarded, the call to redeem() would try to acquire the
   // guard resulting in deadlock.

   // Fire the callbacks. They will be removed from the callback list
   // following executing.
   cbList_(RWEscrowHandle<Redeemable>(this));
}

template <class Redeemable>
RWBoolean
RWEscrowImp<Redeemable>::inError(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,inError(void) const:RWBoolean);
   LockGuard lock(monitor());   
   return isException();
}

template <class Redeemable>
RWEscrowHandle<Redeemable>
RWEscrowImp<Redeemable>::newInstance(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,newInstance(void) const:RWEscrowHandle<Redeemable>);
   LockGuard lock(monitor());
   return _newInstance();
}

template <class Redeemable>
Redeemable
RWEscrowImp<Redeemable>::redeem(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,redeem(void) const:Redeemable);
   LockGuard lock(monitor());

   if (isException())
      (*exception_).raise();

   if (isAborted()) 
      throw RWTHROperationAborted();

   Redeemable value = _redeem(); // call concrete implementation 

   // If we get here then everything went well. Set isRedeemed_ flag.
   RWEscrowImp<Redeemable>* me = RW_THR_CONST_CAST(RWEscrowImp<Redeemable>*, this);
   me->isRedeemed_ = TRUE;  

   return value;    
}

template <class Redeemable>
RWBoolean
RWEscrowImp<Redeemable>::redeemable(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,redeemable(void) const:RWBoolean);
   LockGuard lock(monitor());
   return isRedeemable();
}

template <class Redeemable>
RWBoolean
RWEscrowImp<Redeemable>::redeemed(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,redeemed(void) const:RWBoolean);
   LockGuard lock(monitor());
   return isRedeemed_;
}

template <class Redeemable>
void
RWEscrowImp<Redeemable>::removeCallback(const RWIOUResultCallback& iouResultCallback)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,removeCallback(const RWIOUResultCallback&):void);
   LockGuard lock(monitor());
   cbList_.remove(iouResultCallback);   
}

template <class Redeemable>
void 
RWEscrowImp<Redeemable>::setException(const RWTHRxmsg& xmsg)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,setException(const RWTHRxmsg&):void);
   LockGuard lock(monitor());

   // If the escrow isn't already redeemable then set exception_
   if (!isRedeemable()) {
      exception_ = xmsg.clone();
      _setException(xmsg); // child hook
      lock.release();     // since callbacks will likely call redeem() on this.
      fireAllCallbacks();
   }
   // else ignore... the final state of the Escrow has been set.
}

template <class Redeemable>
void 
RWEscrowImp<Redeemable>::setException(const RWCString& msg)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,setException(const RWCString&):void);
   LockGuard lock(monitor());

   // If the escrow isn't already redeemable then set exception_
   if (!isRedeemable()) {
      RWTHRxmsg xmsg = RWTHRxmsg(msg);
      exception_ = xmsg.clone();
      _setException(xmsg); // child hook
      lock.release();     // since callbacks will likely call redeem() on this.
      fireAllCallbacks();
   }
   // else ignore... the final state of the Escrow has been set.
}

template <class Redeemable>
void
RWEscrowImp<Redeemable>::throwAbort(void) const
   RWTHRTHROWS1(RWTHROperationAborted)
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,throwAbort(void) const:void);
   RWTHRASSERT(isAcquired()); // mutex must already be acquired
   RWTHRASSERT(isAborted());  // shouldn't reach here if not aborted

   throw RWTHROperationAborted();
}

template <class Redeemable>
void
RWEscrowImp<Redeemable>::throwStoredException(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,throwStoredException(void) const:void);
   RWTHRASSERT(isAcquired());  // mutex must already be acquired
   RWTHRASSERT(isException()); // shouldn't reach here if no exception

   (*exception_).raise();
}

template <class Redeemable>
// virtual
void
RWEscrowImp<Redeemable>::_abort(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,_abort(void):void);
   // Default implementation does nothing.
}

template <class Redeemable>
// virtual
void
RWEscrowImp<Redeemable>::_setException(const RWTHRxmsg& xmsg)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,_setException(const RWTHRxmsg&):void);
   // Default implementation does nothing.
   (void)xmsg;  // keep some compilers from complaining
}


#endif // __RWTHRESCROIMP_CC__


