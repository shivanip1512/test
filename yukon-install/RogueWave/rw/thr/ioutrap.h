#if !defined(__RWTHRIOUTRAP_H__)
#  define __RWTHRIOUTRAP_H__
/*****************************************************************************
 *
 * ioutrap.h
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

ioutrap.h - Declarations for:
         
   RWIOUTrap - Handle class for IOU trap.
   RWIOUTrapImp - Body class for IOU trap.

See Also:

   ioureslt.h  - RWIOUResult<R>

RWIOUTrap<R> allows you to block, or poll, waiting for the next, in a number
of RWIOUResult<R> objects, to become redeemable.

******************************************************************************/

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

#  if !defined(__RWTHRIOURESLT_H__)
#     include <rw/thr/ioureslt.h>
#  endif

#  if !defined(__RWTHRPRODCONS_H__)
#     include <rw/thr/prodcons.h>
#  endif


// Do not use RWIOUTrapImp<R> directly. Use RWIOUTrap<R>.

template <class Redeemable>
class RWTHRTExport RWIOUTrapImp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWPCValQueue< RWIOUResult<Redeemable> > iouQueue_;

   public:

      // Is the queue empty.
      RWBoolean 
      isEmpty(void) const
         RWTHRTHROWSANY;

      // Trap the given IOUResult when it is redeemable.
      void
      setTrap(RWIOUResult<Redeemable>& iou)
         RWTHRTHROWSANY;

      // Get next RWIOUResult<R> that is redeemable. Blocks if
      // no RWIOUResult<R> trapped.
      RWIOUResult<Redeemable> 
      getNext(void)
         RWTHRTHROWSANY;

      // Get next RWIOUResult<R> that is redeemable. Returns with 
      // RW_THR_TIMEOUT status if we couldn't get the result in the
      // specified milliseconds, otherwise the returned status is 
      // RW_THR_COMPLETED. 
      RWWaitStatus
      getNext(RWIOUResult<Redeemable>& iouResult, unsigned long milliseconds)
         RWTHRTHROWSANY;

      // Get next redeemable RWIOUResult<R> if one is available. 
      // Returns TRUE and sets the iouResult parameter if one
      // could be read, otherwise returns FALSE
      RWBoolean
      tryGetNext(RWIOUResult<Redeemable>& iouResult)
         RWTHRTHROWSANY;

//   private:

      // Callback function which is called by the IOU when it
      // becomes redeemable. It "trips" the trap.
      void
      trip(RWIOUResult<Redeemable> iou)
         RWTHRTHROWSANY;

};


template <class Redeemable>
class RWTHRTExport RWIOUTrap :
   public RWTHRHandle {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      // Construct an RWIOUTrap
      RWIOUTrap(void)
         RWTHRTHROWSNONE;

      // Construct a static RWIOUTrap
      RWIOUTrap(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Copy construct an RWIOUTrap<R> handle.
      RWIOUTrap(const RWIOUTrap<Redeemable>& second)
         RWTHRTHROWSANY;

      // Bind a new handle instance to a body instance 
      RWIOUTrap(RWIOUTrapImp<Redeemable>* bodyPtr)
         RWTHRTHROWSANY;

      // Destructor. 
      ~RWIOUTrap(void)
         RWTHRTHROWSANY;

      // Assign one RWIOUTrap handle to another. 
      RWIOUTrap<Redeemable>&
      operator=(const RWIOUTrap<Redeemable>& second)
         RWTHRTHROWSANY;

      // Is the queue empty.
      RWBoolean 
      isEmpty(void) const
         RWTHRTHROWSANY;

      // Trap the given IOUResult when it is redeemable.
      void
      setTrap(RWIOUResult<Redeemable> iou) const
         RWTHRTHROWSANY;

      // Get next RWIOUResult<R> that is redeemable.
      RWIOUResult<Redeemable> 
      getNext(void)
         RWTHRTHROWSANY;

      // Get next RWIOUResult<R> that is redeemable. Returns with 
      // RW_THR_TIMEOUT status if we couldn't get the result in the 
      // specified milliseconds, otherwise the returned status is 
      // RW_THR_COMPLETED.
      RWWaitStatus
      getNext(RWIOUResult<Redeemable>& iouResult, unsigned long milliseconds)
         RWTHRTHROWSANY;

      // Get next redeemable RWIOUResult<R> if one is available. 
      // Returns TRUE and sets the iouResult parameter if one
      // could be read, otherwise returns FALSE
      RWBoolean
      tryGetNext(RWIOUResult<Redeemable>& iouResult)
         RWTHRTHROWSANY;

      // Callback function which is called by the IOU when it
      // becomes redeemable. It "trips" the trap.
      static void
      trip(RWIOUResult<Redeemable> iou, RWIOUTrap<Redeemable> iouTrap)
         RWTHRTHROWSANY;

   protected:
   
      // Returns a reference to the body
      RWIOUTrapImp<Redeemable>&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

/*****************************************************************************/
 
template <class Redeemable>
inline
RWIOUTrap<Redeemable>::RWIOUTrap(void)
   RWTHRTHROWSNONE
   :
      RWTHRHandle(new RWIOUTrapImp<Redeemable>)
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,RWIOUTrap(void));   
}

template <class Redeemable>
inline
RWIOUTrap<Redeemable>::RWIOUTrap(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,RWIOUTrap(RWStaticCtor));   
}

template <class Redeemable>
inline
RWIOUTrap<Redeemable>::RWIOUTrap(const RWIOUTrap<Redeemable>& second)
   RWTHRTHROWSANY
   :
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,RWIOUTrap(RWIOUTrap<Redeemable>&));
}

template <class Redeemable>
inline
RWIOUTrap<Redeemable>::RWIOUTrap(RWIOUTrapImp<Redeemable>* bodyPtr)
   RWTHRTHROWSANY
   :
      RWTHRHandle(bodyPtr)
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,RWIOUTrap(const RWIOUTrapImp<Redeemable>*));
}

template <class Redeemable>
inline
RWIOUTrap<Redeemable>::~RWIOUTrap(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,~RWIOUTrap(void));
   
}

template <class Redeemable>
inline
RWIOUTrap<Redeemable>&
RWIOUTrap<Redeemable>::operator=(const RWIOUTrap<Redeemable>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,operator=(RWIOUTrap<Redeemable>&):RWIOUTrap<Redeemable>&);
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

template <class Redeemable>
inline
RWIOUTrapImp<Redeemable>&
RWIOUTrap<Redeemable>::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,body(void):RWIOUTrapImp<Redeemable>&);

   // throws RWTHRInvalidPointer if no imp assigned!
   return (RWIOUTrapImp<Redeemable>&)RWTHRHandle::body();
}

template <class Redeemable>
inline
RWBoolean
RWIOUTrap<Redeemable>::isEmpty(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,isEmpty(void):RWBoolean);
   return body().isEmpty();
}

template <class Redeemable>
inline
RWIOUResult<Redeemable>
RWIOUTrap<Redeemable>::getNext(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,getNext(void):RWIOUResult<Redeemable>);
   return body().getNext();
}

template <class Redeemable>
inline
RWWaitStatus
RWIOUTrap<Redeemable>::getNext(RWIOUResult<Redeemable>& iouResult, unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,getNext(RWIOUResult<Redeemable>&,unsigned long):RWWaitStatus);
   return body().getNext(iouResult, milliseconds);
}
 
template <class Redeemable>
inline
RWBoolean
RWIOUTrap<Redeemable>::tryGetNext(RWIOUResult<Redeemable>& iouResult)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrap<Redeemable>,tryGetNext(RWIOUResult<Redeemable>&):RWBoolean);
   return body().tryGetNext(iouResult);
}

/*****************************************************************************/

template <class Redeemable>
inline
RWBoolean
RWIOUTrapImp<Redeemable>::isEmpty(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrapImp<Redeemable>,isEmpty(void):RWBoolean);
   return !iouQueue_.canRead();
}

template <class Redeemable>
inline
RWIOUResult<Redeemable>
RWIOUTrapImp<Redeemable>::getNext(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrapImp<Redeemable>,getNext(void):RWIOUResult<Redeemable>);
   return iouQueue_.read(); 
}
 
template <class Redeemable>
inline
RWWaitStatus
RWIOUTrapImp<Redeemable>::getNext(RWIOUResult<Redeemable>& iouResult, unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrapImp<Redeemable>,getNext(RWIOUResult<Redeemable>&,unsigned long):RWWaitStatus);
   return iouQueue_.read(iouResult, milliseconds);
}
 
template <class Redeemable>
inline
RWBoolean
RWIOUTrapImp<Redeemable>::tryGetNext(RWIOUResult<Redeemable>& iouResult)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWIOUTrapImp<Redeemable>,tryGetNext(RWIOUResult<Redeemable>&):RWBoolean);
   return iouQueue_.tryRead(iouResult);
}


#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/ioutrap.cc>
#  endif


#endif // __RWTHRIOUTRAP_H__

