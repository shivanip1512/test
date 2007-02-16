#if !defined(__RWTHRESCROIMP_H__)
#  define __RWTHRESCROIMP_H__
/*****************************************************************************
 *
 * escroimp.h
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

escroimp.h - Class declarations for:

   RWEscrowImp<Redeemable> - common base class for Escrow implementation classes.

See Also:

   escroimp.cpp  - Out-of-line function definitions.

*****************************************************************************/


#  if !defined(__RWCSTRING_H__)
#     include <rw/cstring.h>
#  endif

#  if !defined(__RWTHRESCROB_H__)
#     include <rw/thr/escrob.h>
#  endif

#  if !defined(__RWTHRESCROHND_H__)
#     include <rw/thr/escrohnd.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

#  if !defined(__RWTHREXCEPT_H__)
#     include <rw/thr/except.h>
#  endif

#  if !defined(__RWTHRFUNC1_H__)
#     include <rw/thr/func1.h>
#  endif

#  if !defined(__RWTHRONLYPTR_H__)
#     include <rw/thr/onlyptr.h>
#  endif

#  if !defined(__RWTHRCB1_H__)
#     include <rw/thr/cb1list.h>
#  endif


class RWTHRExport RWTHREscrowAlreadyClosed :
   public RWTHRxmsg {
 
   RW_THR_DECLARE_EXCEPTION(RWTHREscrowAlreadyClosed)
 
   // Member Functions
   public:
      RWTHREscrowAlreadyClosed(const RWCString& reason="")
         RWTHRTHROWSNONE;
};

template <class Redeemable>
class RWTHRTExport RWEscrowImp :
   public RWEscrowImpBase {

   RW_THR_DECLARE_TRACEABLE

   // Member Variables
   public:

      typedef Redeemable RedeemableType;
   
      typedef RWFunctor1< RWEscrowHandle<Redeemable>  > RWIOUResultCallback;

   private:
      RWBoolean isRedeemed_;
      RWBoolean isAborted_;
      RWOnlyPointer<RWTHRxmsg> exception_;
      RWCallback1List< MutexType, RWEscrowHandle<Redeemable> > cbList_;

   // Member Functions
   public:

      virtual
      ~RWEscrowImp(void)
         RWTHRTHROWSNONE;
      
      // Tell the service provider that result is no longer needed. Notify
      // anyone waiting for the Escrow so they can detect abort and catch an
      // exception. Subsequently calls to redeem the IOU will immediately
      // receive an exception indicating that the operation associated with
      // the Escrow has been aborted. Ignored if operation already aborted.
      void
      abort(void)
         RWTHRTHROWSNONE;

      // Returns whether the operation has been aborted or not.
      RWBoolean
      aborted(void) const
         RWTHRTHROWSNONE;

      // Add a callback. Will be called when Escrow closed.
      void
      addCallback(const RWIOUResultCallback& iouResultCallback)
         RWTHRTHROWSANY;
   
      // Store a value into the Escrow. If the Escrow has already been closed,
      // this function throws an exception indicating this. If the escrow
      // operation has been aborted then an RWTHROperationAborted exception
      // is thrown. If an exception has been set then that exception is
      // thrown.
      void 
      close(Redeemable value)
         RWTHRTHROWSANY;

      // Can a value be written into the Escrow? True if no value has
      // been written and the escrow isn't in error or aborted.
      RWBoolean
      closeable(void) const
         RWTHRTHROWSNONE;

      // Has a value been successfully written into the Escrow? (Note,
      // that redeemable is better for polling as it will inform of
      // errors and abortion in addition to valid closures.)
      RWBoolean
      closed(void) const
         RWTHRTHROWSNONE;

      // Was the Escrow closed with an error? If it was then the function returns
      // true and the caller can expect an exception to be thrown on the next
      // call to redeem.
      RWBoolean
      inError() const
         RWTHRTHROWSNONE;

      // Create a new instance of this RWEscrowImp<R> type. Implemented
      // by most derived type.
      RWEscrowHandle<Redeemable>
      newInstance(void) const
         RWTHRTHROWSANY;
   
      // Get the result, blocking if the result is not yet available.
      // Throws RWTHROperationAborted if the Escrow (or the associated 
      // operation) have been aborted. If an exception is thrown and 
      // stored in the Escrow, then that exception is re-thrown by this
      // routine.
      Redeemable
      redeem(void) const
         RWTHRTHROWSANY;

      // Returns true if a proper value has been set, if an error has 
      // been set, or if the operation has been aborted. This function
      // is used primarily by the reader side to poll the Escrow for
      // the availability of a result.
      RWBoolean
      redeemable(void) const
         RWTHRTHROWSANY;

      // Returns whether the result has been successfully redeemed or 
      // not. Note that redeems that result in an exception being 
      // thrown do not count as successfull.
      RWBoolean
      redeemed(void) const
         RWTHRTHROWSNONE;

      // Remove callback. If doesn't exist, does nothing.
      void
      removeCallback(const RWIOUResultCallback& iouResultCallback)
         RWTHRTHROWSANY;  

      // Set an error on the Escrow. Causes an exception to be stored inside
      // the Escrow. Notifies callers waiting for the Escrow to be closed.
      // so that they can redeem IOU and catch the error. If the Escrow has 
      // already been closed normally, closed with an error, or aborted then 
      // the error is ignored.
      void 
      setException(const RWTHRxmsg& xmsg)
         RWTHRTHROWSANY;

      // Set the error with a string. The string is packaged into an RWTHRxmsg
      // and stored within the EscrowImp until it is redeemed, at which point
      // it is thrown. If the Escrow has already been closed normally, closed 
      // with an error, or aborted then the error is ignored.
      void 
      setException(const RWCString& msg)
         RWTHRTHROWSANY;

   protected:

      // Only inheriting classes can construct RWEscrowImp<Redeemable>.
      RWEscrowImp(void)
         RWTHRTHROWSNONE;
      
      // The following helper functions assume that a guard has already
      // been acquired. They will not acquire the mutex.

      // Has the escrow been aborted?
      RWBoolean
      isAborted(void) const
         RWTHRTHROWSNONE;

      // Has an exception been set on the escrow?
      RWBoolean
      isException(void) const
         RWTHRTHROWSNONE;

      // Has the escrow been closed, aborted, or put in error
      RWBoolean
      isRedeemable(void) const
         RWTHRTHROWSNONE;

      // Throw abort exception. 
      void
      throwAbort(void) const
         RWTHRTHROWS1(RWTHROperationAborted);

      // Throw stored exception, if any. 
      void
      throwStoredException(void) const
         RWTHRTHROWSANY;

      // Virtual functions... These are meant to be overloaded by concrete Escrow 
      // implementations. Pure virtuals must be implemented (of course); other
      // virtuals may optionally be specialized. Note that all virtual functions
      // are called while the mutex "mutex_" is acquired. In order to
      // in a call to a member function) on this instance, mutex_ must
      // be released and then reaquired before returning; the best way
      // to do this is to use an UnlockGuard which is defined in the Monitor<>
      // base class.
      
      // Hook to catch aborts.
      virtual
      void
      _abort(void)
         RWTHRTHROWSNONE;

      // Implementation of Escrow closure. Stores value for later redemption.
      // Must be implemented by derived class.
      virtual
      void 
      _close(const Redeemable &value)
         RWTHRTHROWSANY = 0;

      // Returns whether the escrow has been successfully closed or not.
      // Must be implemented by derived class.
      virtual
      RWBoolean 
      _closed(void) const
         RWTHRTHROWSANY = 0;

      // Implementation of _newInstance(). Must be implemented by
      // derived types. 
      virtual
      RWEscrowHandle<Redeemable>
      _newInstance(void) const
         RWTHRTHROWSANY = 0;
   
      // Implementation of redeem. Returns stored value. Blocks until
      // the value is available or an exception is thrown. Must be 
      // implemented by derived class.
      virtual
      Redeemable
      _redeem(void) const
         RWTHRTHROWSANY = 0;

      // Hook to intercept errors.
      virtual
      void 
      _setException(const RWTHRxmsg& msg)
         RWTHRTHROWSNONE;
    
   private:

      void
      fireAllCallbacks(void)
         RWTHRTHROWSANY;

};

/****************************************************************************/

template <class Redeemable>
inline
RWBoolean
RWEscrowImp<Redeemable>::isAborted(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,isAborted(void) const:RWBoolean);
   RWTHRASSERT(isAcquired()); // mutex must already be acquired
   return isAborted_;
}

template <class Redeemable>
inline
RWBoolean
RWEscrowImp<Redeemable>::isException(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,isException(void) const:RWBoolean);
   RWTHRASSERT(isAcquired()); // mutex must already be acquired
   return exception_.isValid();
}

template <class Redeemable>
inline
RWBoolean
RWEscrowImp<Redeemable>::isRedeemable(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWEscrowImp<Redeemable>,isRedeemable(void) const:RWBoolean);
   RWTHRASSERT(isAcquired()); // mutex must already be acquired
   return (_closed() || exception_.isValid() || isAborted_);
}


#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/escroimp.cc>
#  endif

#endif // __RWTHRESCROIMP_H__



