#if !defined(__RWTHRIOURESLT_H__)
#  define __RWTHRIOURESLT_H__
/*****************************************************************************
 *
 * ioureslt.h
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

ioureslt.h - Class declarations for:

   RWIOUResult<Redeemable> - IOU result interface class.

See Also:

   ioureslt.cc  - Out-of-line function definitions.

*****************************************************************************/


#  if !defined(__RWTHRESCROHND_H__)
#     include <rw/thr/escrohnd.h>
#  endif

#  if !defined(__RWTHRIOUIMP_H__)
#     include <rw/thr/escroimp.h>
#  endif

template <class Redeemable>
class RWTHRTExport RWIOUResult :
   public RWEscrowHandle<Redeemable> {

   RW_THR_DECLARE_TRACEABLE

   // Member Variables
   public:

      typedef Redeemable RedeemableType;
      typedef RWFunctor1< RWEscrowHandle<Redeemable> > RWIOUResultCallback;

   protected:
   private:

   // Member Functions
   public:

      ~RWIOUResult(void)
         RWTHRTHROWSANY;

      // Create invalid handle. Use of an instance created by the default
      // constructor will result in an RWTHRInvalidPointer exception being
      // thrown.
      RWIOUResult(void)
         RWTHRTHROWSNONE;

      // Create handle from RWEscrowHandle<Redeemable>&.
      RWIOUResult(const RWEscrowHandle<Redeemable>& escrowHandle)
         RWTHRTHROWSANY;

      // Copy constructor
      RWIOUResult(const RWIOUResult<Redeemable>& second)
         RWTHRTHROWSANY;

      // Point this to same RWEscrowImp<Redeemable> as second.
      RWIOUResult<Redeemable>&
      operator=(const RWIOUResult<Redeemable>& second)
         RWTHRTHROWSANY;

      // Tell the service provider that result is no longer needed. Notify
      // anyone waiting for the Escrow so they can detect abort and catch an exception. 
      // Subsequently calls to redeem the IOU will immediately receive an exception 
      // indicating that the operation associated with the Escrow has been aborted. 
      // Ignored if operation already aborted.
      void
      abort(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Returns whether the operation has been aborted or not.
      RWBoolean
      aborted(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
   
      // Add a callback. Will be called when IOU escrow is closed.
      void
      addCallback(const RWIOUResultCallback& callback)
         RWTHRTHROWSANY;
   
      // Was the IOU closed with an error? If true then, if the IOU is
      // redeemed, an exception will be thrown.
      RWBoolean
      inError(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Equivalent to redeem().
      Redeemable
      operator()(void) const
         RWTHRTHROWSANY;

      // Equivalent to redeem().
      operator Redeemable() const
         RWTHRTHROWSANY;
   
      // Get the result, blocking if the result is not yet available.
      // Throws RWTHROperationAborted if the IOU (or the associated 
      // operation) have been aborted. If an exception is thrown and 
      // stored in the IOU, then that exception is re-thrown by this
      // routine.
      Redeemable
      redeem(void) const
         RWTHRTHROWSANY;

      // Returns true if a proper value has been set, if an error has 
      // been set, or if the operation has been aborted. This function
      // is used to poll the Escrow for the availability of a result.
      RWBoolean
      redeemable(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Returns whether the result has been redeemed by anyone?
      RWBoolean
      redeemed(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Remove callback. If doesn't exist, does nothing.
      void
      removeCallback(const RWIOUResultCallback& callback)
         RWTHRTHROWSANY;  

   protected:
  
      // Return reference to RWEscrowImp<Redeemable> body.
      RWEscrowImp<Redeemable>&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

   private:

};

/****************************************************************************/

template <class Redeemable>
inline
RWIOUResult<Redeemable>::~RWIOUResult(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,~RWIOUResult(void));
}

template <class Redeemable>
inline
RWIOUResult<Redeemable>::RWIOUResult(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,RWIOUResult(void));
}

template <class Redeemable>
inline
RWIOUResult<Redeemable>::RWIOUResult(const RWEscrowHandle<Redeemable>& escrowHandle)
   RWTHRTHROWSANY
   :
      RWEscrowHandle<Redeemable>(escrowHandle)
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,RWIOUResult(const RWEscrowHandle<Redeemable>&));
}

template <class Redeemable>
inline
RWIOUResult<Redeemable>::RWIOUResult(const RWIOUResult<Redeemable>& second)
   RWTHRTHROWSANY
   :
      RWEscrowHandle<Redeemable>(second)
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,RWIOUResult(const RWIOUResult<Redeemable>&));
}

template <class Redeemable>
inline
RWIOUResult<Redeemable>&
RWIOUResult<Redeemable>::operator=(const RWIOUResult<Redeemable>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,operator=(const RWIOUResult<Redeemable>&):RWIOUResult<Redeemable>&);
   if (&second != this)
      RWEscrowHandle<Redeemable>::operator=(second);
   return *this;
}

template <class Redeemable>
inline
void
RWIOUResult<Redeemable>::abort(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,abort(void) const:void);
   body().abort();
}

template <class Redeemable>
inline
RWBoolean
RWIOUResult<Redeemable>::aborted(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,aborted(void) const:RWBoolean);
   return body().aborted();
}

template <class Redeemable>
inline
void
RWIOUResult<Redeemable>::addCallback(const RWIOUResultCallback& callback)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,addCallback(const RWIOUResultCallback&):void);
   body().addCallback(callback);
}

template <class Redeemable>
inline
RWEscrowImp<Redeemable>&
RWIOUResult<Redeemable>::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,body(void) const:RWEscrowImp<Redeemable>&);
   
   // The following cast is safe since an RWEscrowHandle<Redeemable> 
   // is ultimately initialized by an RWEscrowImp<Redeemable>. 
   return (RWEscrowImp<Redeemable>&)RWTHRHandle::body();
}

template <class Redeemable>
inline
RWBoolean
RWIOUResult<Redeemable>::inError(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,inError(void) const:RWBoolean);
   return body().inError();
}

template <class Redeemable>
inline
Redeemable
RWIOUResult<Redeemable>::operator()(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,operator()(void) const:Redeemable);
   return body().redeem();
}

template <class Redeemable>
inline
RWIOUResult<Redeemable>::operator Redeemable() const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,operator Redeemable() const);
   return body().redeem();
}

template <class Redeemable>
inline
Redeemable
RWIOUResult<Redeemable>::redeem(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,redeem(void) const:Redeemable);
   return body().redeem();
}

template <class Redeemable>
inline
RWBoolean
RWIOUResult<Redeemable>::redeemable(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,redeemable(void) const:RWBoolean);
   return body().redeemable();   
}

template <class Redeemable>
inline
RWBoolean
RWIOUResult<Redeemable>::redeemed(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,redeemed(void) const:RWBoolean);
   return body().redeemed();
}

template <class Redeemable>
inline
void
RWIOUResult<Redeemable>::removeCallback(const RWIOUResultCallback& callback)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUResult<Redeemable>,removeCallback(const RWIOUResultCallback&):void);
   body().removeCallback(callback);
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/ioureslt.cc>
#  endif


#endif // __RWTHRIOURESLT_H__
