#if !defined(__RWTHRSYNCH_H__)
#  define __RWTHRSYNCH_H__
/*****************************************************************************
 *
 * synch.h
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

synch.h - Declarations for:

   RWCancellationState - enum for enabling/disabling cancellation detection
   RWSynchObject - base class for synchronization objects
         
See Also:

   synch.cpp  - Out-of-line function definitions.

*****************************************************************************/

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RWTHRTRACE_H__)
#     include <rw/thr/trace.h>
#  endif

#  if !defined(__RWTHREXCEPT_H__)
#     include <rw/thr/except.h>
#  endif

enum RWCancellationState {
      RW_CANCELLATION_DISABLED,
      RW_CANCELLATION_ENABLED
   };

// Forward reference for throw specs...
class RWTHRExport RWCancellation;

class RWTHRExport RWSynchObject {
   
   RW_THR_DECLARE_TRACEABLE

   protected:
      RWCancellationState  cancellationState_;

   public:

      // Turn-on automatic cancellation detection
      void 
      enableCancellation(void)
         RWTHRTHROWSNONE;   

      // Turn-off automatic cancellation detection
      void 
      disableCancellation(void)
         RWTHRTHROWSNONE;   

      // Is automatic cancellation detection enabled?
      RWBoolean
      isCancellationEnabled(void) const
         RWTHRTHROWSNONE;

   protected:

      RWSynchObject(RWCancellationState state=RW_CANCELLATION_DISABLED)
         RWTHRTHROWSNONE;

      // Test current runnable object (if any) for cancellation if
      // automatic cancellation detection is enabled...
      void
      testCancellation(void)
         RWTHRTHROWS1(RWCancellation);

   private:

      // Test the current runnable object (if any) for cancellation
      void
      serviceCancellation(void)
         RWTHRTHROWS1(RWCancellation);

};

/*****************************************************************************/

inline
RWSynchObject::RWSynchObject(RWCancellationState state)
   RWTHRTHROWSNONE
   :
      cancellationState_(state)
{
   RWTHRTRACEMF(RWSynchObject,RWSynchObject(RWCancellationState));
}

inline
void
RWSynchObject::enableCancellation(void) 
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWSynchObject,enableCancellation(void):void);
   cancellationState_ = RW_CANCELLATION_ENABLED;
}

inline
void
RWSynchObject::disableCancellation(void) 
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWSynchObject,disableCancellation(void):void);
   cancellationState_ = RW_CANCELLATION_DISABLED;
}

inline
RWBoolean
RWSynchObject::isCancellationEnabled(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWSynchObject,isCancellationEnabled);
   return cancellationState_ == RW_CANCELLATION_ENABLED;
}

inline
void
RWSynchObject::testCancellation(void)
   RWTHRTHROWS1(RWCancellation)
{
   RWTHRTRACEMF(RWSynchObject,testCancellation);
   if (cancellationState_ == RW_CANCELLATION_ENABLED)
      serviceCancellation();
}

#endif  // __RWTHRSYNCH_H__

