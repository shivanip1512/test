#if !defined(__RWTHRIOUESCRO_H__)
#  define __RWTHRIOUESCRO_H__
/*****************************************************************************
 *
 * iouescro.h
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

iouescro.h - Class declarations for:

   RWIOUEscrow<Redeemable> - IOU escrow interface class.

See Also:

   iouescro.cc  - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRESCROIMP_H__)
#     include <rw/thr/escroimp.h>
#  endif

#  if !defined(__RWTHRESCROHND_H__)
#     include <rw/thr/escrohnd.h>
#  endif

template <class Redeemable>
class RWTHRTExport RWIOUEscrow :
   public RWEscrowHandle<Redeemable> {

   RW_THR_DECLARE_TRACEABLE

   // Member Variables
   public:

      typedef Redeemable RedeemableType;
  
   // Member Functions
   public:

      ~RWIOUEscrow(void)
         RWTHRTHROWSANY;

      // Create invalid handle. Use of an instance created by the default
      // constructor will result in an RWTHRInvalidPointer exception being
      // thrown.
      RWIOUEscrow(void)
         RWTHRTHROWSNONE;         

      // Create handle from RWEscrowHandle<Redeemable>&.
      RWIOUEscrow(const RWEscrowHandle<Redeemable>& escrowHandle)
         RWTHRTHROWSANY;

      // Copy constructor
      RWIOUEscrow(const RWIOUEscrow<Redeemable>& second)
         RWTHRTHROWSANY;
 
      // Point this to same RWEscrowImp<Redeemable> as second.
      RWIOUEscrow<Redeemable>&
      operator=(const RWIOUEscrow<Redeemable>& second)
         RWTHRTHROWSANY;
   
      // Returns whether the operation has been aborted or not.
      RWBoolean
      aborted(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
   
      // Store a value into the Escrow. If the Escrow has already been closed,
      // the function throws an RWTHREscrowAlreadyClosed exception. If the 
      // escrow operation has been aborted then an RWTHROperationAborted 
      // exception is thrown. If an exception has been set then that exception 
      // is thrown.
      void 
      close(Redeemable value)
         RWTHRTHROWSANY;

      // Can a value be written into the Escrow? True if no value has
      // been written and the escrow isn't in error or aborted.
      RWBoolean
      closeable(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Has a value been successfully written into the IOU?
      RWBoolean
      closed(void)
         RWTHRTHROWS1(RWTHRInvalidPointer);
   
      // Was the IOU closed with an error? If true then, if the IOU is
      // redeemed, an exception will be thrown.
      RWBoolean
      inError(void)
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Create a new instance of the RWEscrowImp<R> body. Returns
      // a new RWIOUEscrow<R> handle which points to the new body.
      RWIOUEscrow<Redeemable>
      newInstance(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Equivalent to close().
      void
      operator()(Redeemable value)
        RWTHRTHROWSANY;

      // Equivalent to close().
      void 
      operator=(Redeemable value)
         RWTHRTHROWSANY;

      // Has the escrow been redeemed?
      RWBoolean
      redeemed(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

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
   
      // Return reference to RWEscrowImp<Redeemable> body.
      RWEscrowImp<Redeemable>&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};

/*****************************************************************************/

template <class Redeemable>
inline
RWIOUEscrow<Redeemable>::~RWIOUEscrow(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,~RWIOUEscrow(void));
}

template <class Redeemable>
inline
RWIOUEscrow<Redeemable>::RWIOUEscrow(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,RWIOUEscrow(void));
}

template <class Redeemable>
inline
RWIOUEscrow<Redeemable>::RWIOUEscrow(const RWEscrowHandle<Redeemable>& escrowHandle)
   RWTHRTHROWSANY
   :
      RWEscrowHandle<Redeemable>(escrowHandle)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,RWIOUEscrow(const RWEscrowHandle<Redeemable>&));
}

template <class Redeemable>
inline
RWIOUEscrow<Redeemable>::RWIOUEscrow(const RWIOUEscrow<Redeemable>& second)
   RWTHRTHROWSANY
   :
      RWEscrowHandle<Redeemable>(second)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,RWIOUEscrow(const RWIOUEscrow<Redeemable>&));
}

template <class Redeemable>
inline
RWIOUEscrow<Redeemable>&
RWIOUEscrow<Redeemable>::operator=(const RWIOUEscrow<Redeemable>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,operator=(const RWIOUEscrow<Redeemable>&):RWIOUEscrow<Redeemable>&);
   if (&second != this)
      RWEscrowHandle<Redeemable>::operator=(second);
   return *this;
}

template <class Redeemable>
inline
RWBoolean
RWIOUEscrow<Redeemable>::aborted(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,aborted(void) const:RWBoolean);
   return body().aborted();
}

template <class Redeemable>
inline
RWEscrowImp<Redeemable>&
RWIOUEscrow<Redeemable>::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,body(void) const:RWEscrowImp<Redeemable>&);
   
   // The following cast is safe since an RWEscrowHandle<Redeemable> 
   // is ultimately initialized by an RWEscrowImp<Redeemable>. 
   return (RWEscrowImp<Redeemable>&)RWTHRHandle::body();
}

template <class Redeemable>
inline
void
RWIOUEscrow<Redeemable>::close(Redeemable value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,close(Redeemable):void);
   body().close(value);
}

template <class Redeemable>
inline
RWBoolean
RWIOUEscrow<Redeemable>::closeable(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,closeable(void) const:RWBoolean);
   return body().closeable();
}

template <class Redeemable>
inline
RWBoolean
RWIOUEscrow<Redeemable>::closed(void)
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,closed(void):RWBoolean);
   return body().closed();
}

template <class Redeemable>
inline
RWBoolean
RWIOUEscrow<Redeemable>::inError(void)
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,inError(void):RWBoolean);
   return body().inError();
}

template <class Redeemable>
inline
RWIOUEscrow<Redeemable>
RWIOUEscrow<Redeemable>::newInstance(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,newInstance(void) const:RWIOUEscrow<Redeemable>);
   return body().newInstance();
}

template <class Redeemable>
inline
void
RWIOUEscrow<Redeemable>::operator()(Redeemable value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,operator()(Redeemable):void);
   body().close(value);
}

template <class Redeemable>
inline
void
RWIOUEscrow<Redeemable>::operator=(Redeemable value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,operator=(Redeemable):void);
   body().close(value);
}

template <class Redeemable>
inline
RWBoolean
RWIOUEscrow<Redeemable>::redeemed(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,redeemed(void) const:RWBoolean);
   return body().redeemed();
}

template <class Redeemable>
inline
void
RWIOUEscrow<Redeemable>::setException(const RWCString& msg)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,setException(const RWCString&):void);
   body().setException(msg);
}

template <class Redeemable>
inline
void
RWIOUEscrow<Redeemable>::setException(const RWTHRxmsg& xmsg)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUEscrow<Redeemable>,setException(const RWTHRxmsg&):void);
   body().setException(xmsg);
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/iouescro.cc>
#  endif


#endif // __RWTHRIOUESCRO_H__
