#if !defined(__RWTHRRNBLTRAP_H__)
#  define __RWTHRRNBLTRAP_H__
/*****************************************************************************
 *
 * rnbltrap.h
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

rnbltrap.h - Declarations for:
         
   RWRunnableTrap - Handle class for runnable trap.
   RWRunnableTrapImp - Body class for runnable trap.

See Also:

   runnable.h  - RWRunnable definition

 RWRunnableTrap allows you to block, or poll, waiting for the next, in a 
 number of RWRunnable objects, to reach a given execution state.

******************************************************************************/

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

#  if !defined(__RWTHRRunnable_H__)
#     include <rw/thr/runnable.h>
#  endif

#  if !defined(__RWTHRPRODCONS_H__)
#     include <rw/thr/prodcons.h>
#  endif

struct RWRunnableExecutionStatePair {
   RWRunnable runnable;
   RWExecutionState state;
   RWBoolean operator==(const RWRunnableExecutionStatePair& second) const {
      return runnable==second.runnable && state==second.state;
   }
   // Required for stdlib version of RWTValSlist.
   RWBoolean operator<(const RWRunnableExecutionStatePair& second) const {
      return state < second.state;
   }
};

// Do not use RWRunnableTrapImp directly. Use RWRunnableTrap.

class RWTHRExport RWRunnableTrapImp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWPCValQueue< RWRunnableExecutionStatePair > runnableQueue_;
      unsigned long executionStateMask_;

   public:

      // Construct Runnable trap to catch runnables as they reach the
      // given execution state(s). The default is to wait until a 
      // runnable finishes execution and returns to its initial
      // state. (See the RWExecutionState enum in runnable.h.)
      RWRunnableTrapImp(unsigned long executionStateMask=RW_THR_INITIAL)
         RWTHRTHROWSNONE;

      // Is the queue empty.
      RWBoolean 
      isEmpty(void) const
         RWTHRTHROWSANY;

      // Trap the given runnable when it enters executionState_.
      // An RWCallbackScope may be specified as either RW_THR_CALL_ONCE
      // or RW_THR_CALL_REPEATEDLY. This determines whether the 
      // runnable should enter the trap only once, when it enters
      // the target execution state, or repeatedly each time it 
      // enters that state. 
      void
      setTrap(RWRunnable& runnable, RWCallbackScope scope=RW_THR_CALL_ONCE)
         RWTHRTHROWSANY;

      // Get next RWRunnable that has entered executionState_.
      // Blocks if no RWRunnable trapped.
      RWRunnable
      getNext(void)
         RWTHRTHROWSANY;

      // Get next RWRunnable that has entered executionState_.
      // Times-out after given milliseconds if no RWRunnable trapped.
      // Returns either RW_THR_TIMEOUT or RW_THR_COMPLETED.
      RWWaitStatus
      getNext(RWRunnable &r, unsigned long milliseconds)
         RWTHRTHROWSANY;

      // Get next RWRunnable that has entered executionState_. 
      // Blocks if no RWRunnable trapped. Sets "es" to the
      // execution state that triggered the trap.
      void
      getNext(RWRunnable& r, RWExecutionState& es)
         RWTHRTHROWSANY;

      // Get next trapped RWRunnable and the execution state that caused for
      // it to be trapped. Times-out after given milliseconds if no RWRunnable 
      // trapped. Returns either RW_THR_TIMEOUT or RW_THR_COMPLETED.
      RWWaitStatus
      getNext(RWRunnable& r, RWExecutionState& es, unsigned long milliseconds)
         RWTHRTHROWSANY;

      // Get next runnable to reach the target execution state, if one 
      // is available. Returns TRUE and sets the runnable parameter if one
      // could be read, otherwise returns FALSE
      RWBoolean
      tryGetNext(RWRunnable& runnable)
         RWTHRTHROWSANY;

      // Get next runnable to reach the target execution state, if one 
      // is available. Returns TRUE and sets the runnable parameter if one
      // could be read, otherwise returns FALSE. Sets "es" to execution
      // state that triggered the trap.
      RWBoolean
      tryGetNext(RWRunnable& runnable, RWExecutionState& es)
         RWTHRTHROWSANY;

   private:

      // Callback function which is called by the runnable when it
      // enters the target state. It "trips" the trap.
      void
      trip(const RWRunnable& r, RWExecutionState es)
         RWTHRTHROWSANY;

};


class RWTHRExport RWRunnableTrap :
   public RWTHRHandle {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      // Construct an RWRunnableTrap
      RWRunnableTrap(void)
         RWTHRTHROWSNONE;

      // Construct a static RWRunnableTrap
      RWRunnableTrap(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Copy construct an RWRunnableTrap handle.
      RWRunnableTrap(const RWRunnableTrap& second)
         RWTHRTHROWSANY;

      // Destructor. 
      ~RWRunnableTrap(void)
         RWTHRTHROWSANY;

      // Assign one RWRunnableTrap handle to another. 
      RWRunnableTrap&
      operator=(const RWRunnableTrap& second)
         RWTHRTHROWSANY;

      // Construct Runnable trap to catch runnables as they reach the
      // given execution state(s). The default is to wait until a 
      // runnable finishes execution and returns to its initial
      // state. (See the RWExecutionState enum in runnable.h.)
      static
      RWRunnableTrap
      make(unsigned long executionStateMask=RW_THR_INITIAL)
         RWTHRTHROWSANY;

      // Is the queue empty.
      RWBoolean 
      isEmpty(void) const
         RWTHRTHROWSANY;

      // Trap the given runnable when it enters executionState_ (specified
      // when the runnable trap was created.) An RWCallbackScope may be 
      // specified as either RW_THR_CALL_ONCE or RW_THR_CALL_REPEATEDLY. 
      // This determines whether the runnable should enter the trap only 
      // once, when it enters the target execution state, or repeatedly 
      // each time it enters that state.
      void
      setTrap(RWRunnable runnable, RWCallbackScope scope=RW_THR_CALL_ONCE)
         RWTHRTHROWSANY;

      // Get next RWRunnable that has entered executionState_.
      // Blocks if no RWRunnable trapped.
      RWRunnable
      getNext(void)
         RWTHRTHROWSANY;

      // Get next RWRunnable that has entered executionState_.
      // Times-out after given milliseconds if no RWRunnable trapped.
      // Returns either RW_THR_TIMEOUT or RW_THR_COMPLETED.
      RWWaitStatus
      getNext(RWRunnable &r, unsigned long milliseconds)
         RWTHRTHROWSANY;

      // Get next RWRunnable that has entered target execution state.
      void 
      getNext(RWRunnable& r, RWExecutionState& es)
         RWTHRTHROWSANY;

      // Get next trapped RWRunnable and the execution state that caused for
      // it to be trapped. Times-out after given milliseconds if no RWRunnable 
      // trapped. Returns either RW_THR_TIMEOUT or RW_THR_COMPLETED.
      RWWaitStatus
      getNext(RWRunnable& r, RWExecutionState& es, unsigned long milliseconds)
         RWTHRTHROWSANY;

      // Get next runnable to reach the target execution state, if one
      // is available. Returns TRUE and sets the runnable parameter if one
      // could be read, otherwise returns FALSE.
      RWBoolean
      tryGetNext(RWRunnable& runnable)
         RWTHRTHROWSANY;

      // Get next runnable to reach the target execution state, if one 
      // is available. Returns TRUE and sets the runnable parameter if one
      // could be read, otherwise returns FALSE. Sets "es" to the 
      // execution state that tripped the trap.
      RWBoolean
      tryGetNext(RWRunnable& runnable, RWExecutionState& es)
         RWTHRTHROWSANY;

   protected:
   
      // Construct a handle from an RWRunnableTrapImp*
      RWRunnableTrap(RWRunnableTrapImp* runnableTrapImpP)
         RWTHRTHROWSANY;

      // Returns a reference to the body
      RWRunnableTrapImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};

/*****************************************************************************/

// Helper function for creating RWRunnableTrap.
inline
RWRunnableTrap
rwMakeRunnableTrap(unsigned long executionStateMask=RW_THR_INITIAL)
{
   return RWRunnableTrap::make(executionStateMask);
}

/*****************************************************************************/
 
inline
RWRunnableTrap::RWRunnableTrap(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableTrap,RWRunnableTrap(void));   
}

inline
RWRunnableTrap::RWRunnableTrap(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWRunnableTrap,RWRunnableTrap(RWStaticCtor));   
}

inline
RWRunnableTrap::RWRunnableTrap(const RWRunnableTrap& second)
   RWTHRTHROWSANY
   :
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWRunnableTrap,RWRunnableTrap(RWRunnableTrap&));
}

inline
RWRunnableTrap::RWRunnableTrap(RWRunnableTrapImp* runnableTrapImpP)
   RWTHRTHROWSANY
   :
      RWTHRHandle(runnableTrapImpP)
{
   RWTHRTRACEMF(RWRunnableTrap,RWRunnableTrap(RWRunnableTrapImp*));
}

inline
RWRunnableTrap::~RWRunnableTrap(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,~RWRunnableTrap(void));
   
}

inline
RWRunnableTrap&
RWRunnableTrap::operator=(const RWRunnableTrap& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,operator=(RWRunnableTrap&):RWRunnableTrap&);
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

inline
RWRunnableTrapImp&
RWRunnableTrap::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWRunnableTrap,body(void):RWRunnableTrapImp&);

   // throws RWTHRInvalidPointer if no imp assigned!
   return (RWRunnableTrapImp&)RWTHRHandle::body();
}

inline
RWRunnable
RWRunnableTrap::getNext(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,getNext(void):RWRunnable);
   return body().getNext();
}

inline
RWWaitStatus
RWRunnableTrap::getNext(RWRunnable &r, unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,getNext(RWRunnable&,unsigned long):RWWaitStatus);
   return body().getNext(r, milliseconds);
}

inline
void
RWRunnableTrap::getNext(RWRunnable& runnable, RWExecutionState& es)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,getNext(RWRunnable&,RWExecutionState&):void);
   body().getNext(runnable, es);
}
 
inline
RWWaitStatus
RWRunnableTrap::getNext(RWRunnable& r, RWExecutionState& es, unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,getNext(RWRunnable&,RWExecutionState&,unsigned long):RWWaitStatus);
   return body().getNext(r, es, milliseconds);
}

inline
RWBoolean
RWRunnableTrap::isEmpty(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,isEmpty(void):RWBoolean);
   return body().isEmpty();
}

inline
RWBoolean
RWRunnableTrap::tryGetNext(RWRunnable& runnable)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,tryGetNext(RWRunnable&):RWBoolean);
   return body().tryGetNext(runnable);
}

inline
RWBoolean
RWRunnableTrap::tryGetNext(RWRunnable& runnable, RWExecutionState& es)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableTrap,tryGetNext(RWRunnable&,RWExecutionState&):RWBoolean);
   return body().tryGetNext(runnable, es);
}


#endif // __RWTHRNBLTRAP_H__

