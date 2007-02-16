#if !defined(__RWTHRCANCEL_H__)
#  define __RWTHRCANCEL_H__
/*****************************************************************************
 *
 * cancel.h
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

cancel.h - Declarations for:
         
   RWCancellation - Handle class for thread cancellation object.
   
   RWCancellationImp - Body class for thread cancellation object.

   rwServiceCancellation - global function that calls serviceCancellation on 
                           the current runnable.

See Also:

   cancel.cpp  - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

#  if !defined(__RWTHREXCEPT_H__)
#     include <rw/thr/except.h>
#  endif

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

class RWTHRExport RWRunnableImp;

class RWTHRExport RWCancellationImp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWBoolean         isAcknowledged_;
      RWRunnableImp*    runnableImpP_;

   public:

      // Construct a cancellation object and notify the target runnable
      // that cancellation has started.
      RWCancellationImp(RWRunnableImp* runnableImpP)
         RWTHRTHROWSNONE;

      // If this instance has not been acknowledged, notify the target 
      // runnable that this cancellation instance is being destroyed so 
      // it will know that the cancellation attempt was unsuccessful.
      ~RWCancellationImp(void)
         RWTHRTHROWSANY;

      // Acknowledge receipt of cancellation to turn-off 
      // automated cancellation abort processing.
      void
      acknowledge(void)
         RWTHRTHROWSNONE;

      // Runnable target for cancellation 
      RWRunnableImp*
      target(void) const
         RWTHRTHROWSNONE;
};

/*****************************************************************************/


class RWTHRExport RWCancellation :
   public RWTHRxmsg,
   public RWTHRHandle {

   RW_THR_DECLARE_TRACEABLE

   RW_THR_DECLARE_EXCEPTION(RWCancellation)
   
   friend class RWRunnableImp;

   public:
      
      // Construct a cancellation instance that is 
      // targeted at the specified runnable
      RWCancellation(RWRunnableImp* runnableImpP)
         RWTHRTHROWSANY;

      // Copy construct a cancellation instance
      // (required for exception propagation)
      RWCancellation(const RWCancellation& second)
         RWTHRTHROWSANY;

   protected:

      // Only RWRunnableImp may access these functions...

      // Acknowledge receipt of cancellation to turn-off 
      // automated cancellation abort processing.
      void
      acknowledge(void)
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Acknowledge receipt of cancellation to turn-off 
      // automated cancellation abort processing.
      RWRunnableImp*
      target(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Return reference to RWCancellationImp
      RWCancellationImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};


/*****************************************************************************/

// Global function for servicing cancellation requests.
// This function used rwRunnable() to get the current runnable
// instance for the calling thread, if there is one, and calls
// the serviceCancellation member on that runnable. 
extern rwthrexport
void
rwServiceCancellation(void)
   RWTHRTHROWS1(RWCancellation);

/*****************************************************************************/

inline
void
RWCancellationImp::acknowledge(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCancellationImp,acknowledge(void):void);
   isAcknowledged_ = TRUE;
}

inline
RWRunnableImp*
RWCancellationImp::target(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCancellationImp,target(void):RWRunnableImp*);
   return runnableImpP_;
}

/*****************************************************************************/

inline
RWCancellation::RWCancellation(RWRunnableImp* runnableImpP)
   RWTHRTHROWSANY
   :
      RWTHRxmsg("Runnable Canceled"),
      RWTHRHandle(new RWCancellationImp(runnableImpP))
{
   RWTHRTRACEMF(RWCancellation,RWCancellation(RWRunnableImp*));
}

inline
RWCancellation::RWCancellation(const RWCancellation& second)
   RWTHRTHROWSANY
   :
      RWTHRxmsg((const RWTHRxmsg&)second),
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWCancellation,RWCancellation(const RWCancellation&));
}

inline
RWCancellationImp&
RWCancellation::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWCancellation,body(void) const:RWCancellationImp&);
   return (RWCancellationImp&)RWTHRHandle::body();
}

inline
void
RWCancellation::acknowledge(void)
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWCancellation,acknowledge(void):void);
   body().acknowledge();
}

inline
RWRunnableImp*
RWCancellation::target(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWCancellation,target(void) const:RWRunnableImp*);
   return body().target();
}

#endif // __RWTHRCANCEL_H__

