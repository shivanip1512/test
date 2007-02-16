#if !defined(__RWTHRBARRIER_H__)
#  define __RWTHRBARRIER_H__
/*****************************************************************************
 *
 * barrier.h
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

barrier.h - Class declarations for:

   RWBarrier - Barrier class for synchronizing a specified number of threads
               at a common synchronization point.  The number of threads to be
               synchronized must be specified in the constructor.  Each thread
               calls wait() to indicate that it has reached the desired
               synchronization point.  Each calling thread is blocked within
               the wait() member until the "last" thread calls wait() at 
               which time, all the blocked threads are released and all 
               threads are allowed to return from the wait() function.
               The count may be reset but there can't be any threads 
               blocked in wait() when the count is reset.

See Also:

   barrier.cpp - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

#  if !defined(__RWTHRCONDTION_H__)
#     include <rw/thr/condtion.h>
#  endif

class RWTHRExport RWBarrier {

   RW_THR_DECLARE_TRACEABLE

   private:

      RWMutexLock mutex_;
      RWCondition barrier_;
      int         count_;
      int         countTotal_;

   public:

      // Construct a barrier for synchronizating 'count' (>0) threads. 
      // The default count is 1. This is a ridiculous count, but it 
      // allows one to create a barrier with a constructor that takes 
      // no arguments--and then later reset it.
      RWBarrier(int count=1)
         RWTHRTHROWSANY;

      // There should be no threads blocked on wait() when the 
      // RWBarrier object is destroyed. If there are then an
      // assertion will be raised in debug mode.
      ~RWBarrier(void)
         RWTHRTHROWSANY;

      // Reset the barrier count. This function must only be called 
      // when there are no threads waiting on the barrier. Otherwise
      // a the program will fail with an assertion in debug mode or 
      // an RWTHRInternalError will be thrown in build mode.
      void
      setCount(int count)
         RWTHRTHROWSANY;

      // Wait for count threads to reach the barrier. When the last
      // thread calls wait(), all the threads are released.
      void
      wait(void)
         RWTHRTHROWSANY;

   private:
 
      // Copy construction and assignment prohibited.
      RWBarrier(const RWBarrier& second);
      RWBarrier& operator=(const RWBarrier& second);
 
};

/*****************************************************************************/

inline
RWBarrier::RWBarrier(const RWBarrier&)
   : barrier_(mutex_)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation - Shouldn't get here!
}
 
inline
RWBarrier&
RWBarrier::operator=(const RWBarrier&)
{
   RWTHRASSERT(0); // INTERNAL ERROR - Dummy implementation - Shouldn't get here!
   return *this;
}

#endif // __RWTHRBARRIER_H__


