#  if !defined(__RWTHRGRDRNBLQ_H__)
#  define __RWTHRGRDRNBLQ_H__
/*****************************************************************************
 *
 * grdrnblq.h
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

grdrnblq.h - Declarations for:
         
   RWGuardedRunnable - An RWRunnable/RWRunnableGuard pair.
   RWGuardedRunnableQueue - An RWGuardedRunnable  producer-consumer queue. 

See Also:

   grdrnblq.cpp  - implementation.

 RWGuardedRunnableQueue is a producer-consumer queue specialized for 
 Runnables. It will only deque an RWRunnable if it has an 
 associated guard that evaluates to TRUE, or if there is no associated 
 guard.

 These classes are used in the implementation of RWRunnableServer. They
 are not otherwise documented.

******************************************************************************/

#  if !defined(__RWTHRFUNCR0_H__)
#     include <rw/thr/funcr0.h>
#  endif

#  if !defined(__RWTHRRUNNABLE_H__)
#     include <rw/thr/runnable.h>
#  endif

#  if !defined(__RWTHRPRODCONS_H__)
#     include <rw/thr/prodcons.h>
#  endif

#if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file...
template class RWTHRIExport RWFunctorR0<RWBoolean>;
#endif

typedef RWFunctorR0<RWBoolean> RWRunnableGuard;

class RWTHRExport RWGuardedRunnable {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWRunnable runnable_;
      RWRunnableGuard runnableGuard_;
      RWBoolean noGuard_;

   public:
      
      // Construct with RWRunnable and RWRunnableGuard
      RWGuardedRunnable(RWRunnable r, RWRunnableGuard g)
         RWTHRTHROWSANY;

      // Construct with RWRunnable, default guard always evaluates to TRUE
      RWGuardedRunnable(RWRunnable r)
         RWTHRTHROWSANY;

      // Construct with no runnable, default guard always evaluates to TRUE
      // This constructor is required for stdlib version of RWTValSlist<>
      // DO NOT USE!
      RWGuardedRunnable(void)
         RWTHRTHROWSANY;

      // Does the guard for this runnable evaluate to true.
      RWBoolean 
      isRunnable(void) const
         RWTHRTHROWSANY;

      // Get RWRunnable out of this RWGuardedRunnable.
      operator RWRunnable(void) const
         RWTHRTHROWSANY;

      // Equality
      RWBoolean
      operator==(const RWGuardedRunnable& second) const
         RWTHRTHROWSANY;

      // Required for stdlib-based collections
      RWBoolean
      operator<(const RWGuardedRunnable& second) const
         RWTHRTHROWSANY;
};

#if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file...
template class RWTHRIExport RWPCValQueue<RWGuardedRunnable>;
template class RWTHRIExport RWPCValBufferBase<RWGuardedRunnable>;
template class RWTHRIExport RWTValSlist<RWGuardedRunnable>;
#  if defined(RW_NO_STL)
template class RWTHRIExport RWTValSlink<RWGuardedRunnable>;
template class RWTHRIExport RWTIsvSlist<RWTValSlink<RWGuardedRunnable> >;
#  endif
#endif

#  if defined(RW_THR_SUPERCLASSES_MUST_BE_EXPORTED)
// Microsoft can't doesn't support explicit exporting of
// templates classes and produces warnings for inheriting
// from the non-exported RWCountingBody<> class.
// This pragma turns these warnings off...
#     pragma warning( disable : 4275 )
#  endif

class RWTHRExport RWGuardedRunnableQueue :
   public RWPCValQueue<RWGuardedRunnable> {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWBoolean guards_;  // true if any guards set

   public:
      
      // Construct an RWGuardedRunnableQueue
      RWGuardedRunnableQueue(size_t maxEntries=0)
         RWTHRTHROWSANY;

      // Dequeue the next runnable, regardless of whether it is ready
      // to run not. 
      RWRunnable
      readRunnable(void)
         RWTHRTHROWSANY;

      // Dequeue the next runnable that is ready for execution. 
      RWRunnable
      readReadyRunnable(void)
         RWTHRTHROWSANY;

      // Write a runnable with no guard into the queue. The absence of
      // a guard means that the guard for that runnable always
      // evaluates to TRUE. 
      void  
      writeRunnable(const RWRunnable& r)
         RWTHRTHROWSANY;

      // Write a runnable and guard into the queue. The guard is a
      // functor which is called to determine whether a runnable is 
      // ready to execute.
      void  
      writeRunnable(const RWRunnable& r, const RWRunnableGuard& g)
         RWTHRTHROWSANY;

};

#  if defined(RW_THR_SUPERCLASSES_MUST_BE_EXPORTED)
#     pragma warning( default : 4275 )
#  endif


/****************************************************************************/

 
#define rwMakeRunnableGuardG(DR,function) \
   rwMakeFunctorR0G(RWBoolean,DR,function)
 
#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
 
template <class DR>
RWRunnableGuard
rwMakeRunnableGuard(DR (*callee)(void))
   RWTHRTHROWSANY
{
   return rwMakeFunctorR0G(RWBoolean,DR,callee);
}
 
#  endif

#define rwMakeRunnableGuardGA1(DR,function,A1,a1) \
   rwMakeFunctorR0GA1(RWBoolean,DR,function,A1,a1)
 
#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
 
template <class DR, class A1, class AA1>
RWRunnableGuard
rwMakeRunnableGuard(DR (*callee)(A1),AA1 a1)
   RWTHRTHROWSANY
{
   return rwMakeFunctorR0GA1(RWBoolean,DR,callee,A1,a1);
}
 
#  endif

#define rwMakeRunnableGuardGA2(DR,function,A1,a1,A2,a2) \
   rwMakeFunctorR0GA2(RWBoolean,DR,function,A1,a1,A2,a2)
 
#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
 
template <class DR, class A1, class A2, class AA1, class AA2>
RWRunnableGuard
rwMakeRunnableGuard(DR (*callee)(A1,A2),AA1 a1,AA2 a2)
   RWTHRTHROWSANY
{
   return rwMakeFunctorR0GA2(RWBoolean,DR,callee,A1,a1,A2,a2);
}

#  endif

#define rwMakeRunnableGuardGA3(DR,function,A1,a1,A2,a2,A3,a3) \
   rwMakeFunctorR0GA3(RWBoolean,DR,function,A1,a1,A2,a2,A3,a3)
 
#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
 
template <class DR, class A1, class A2, class A3, class AA1, class AA2, class AA3>
RWRunnableGuard
rwMakeRunnableGuard(DR (*callee)(A1,A2,A3),AA1 a1,AA2 a2,AA3 a3)
   RWTHRTHROWSANY
{
   return rwMakeFunctorR0GA3(RWBoolean,DR,callee,A1,a1,A2,a2,A3,a3);
}

#  endif

#define rwMakeRunnableGuardM(Callee,callee,DR,function) \
   rwMakeFunctorR0M(RWBoolean,Callee,callee,DR,function)
 
#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
 
template <class Callee, class DR>
RWRunnableGuard
rwMakeRunnableGuard(Callee& callee, DR (Callee::*function)(void))
   RWTHRTHROWSANY
{
   return rwMakeFunctorR0M(RWBoolean,Callee,callee,DR,function);
}
 
#  endif

#define rwMakeRunnableGuardMA1(Callee,callee,DR,function,A1,a1) \
   rwMakeFunctorR0MA1(RWBoolean,Callee,callee,DR,function,A1,a1)
 
#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
 
template <class Callee, class DR, class A1, class AA1>
RWRunnableGuard
rwMakeRunnableGuard(Callee& callee, DR (Callee::*function)(A1), AA1 a1)
   RWTHRTHROWSANY
{
   return rwMakeFunctorR0MA1(RWBoolean,Callee,callee,DR,function,A1,a1);
}
 
#  endif
 
#define rwMakeRunnableGuardMA2(Callee,callee,DR,function,A1,a1,A2,a2) \
   rwMakeFunctorR0MA2(RWBoolean,Callee,callee,DR,function,A1,a1,A2,a2)
 
#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
 
template <class Callee, class DR, class A1, class AA1, class A2, class AA2>
RWRunnableGuard
rwMakeRunnableGuard(Callee& callee, DR (Callee::*function)(A1,A2), AA1 a1, AA2 a2)
   RWTHRTHROWSANY
{
   return rwMakeFunctorR0MA2(RWBoolean,Callee,callee,DR,function,A1,a1,A2,a2);
}
 
#endif

#define rwMakeRunnableGuardMA3(Callee,callee,DR,function,A1,a1,A2,a2,A3,a3) \
   rwMakeFunctorR0MA3(RWBoolean,Callee,callee,DR,function,A1,a1,A2,a2,A3,a3)
 
#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
 
template <class Callee, class DR, class A1, class AA1, class A2, class AA2, class A3, class AA3>
RWRunnableGuard
rwMakeRunnableGuard(Callee& callee, DR (Callee::*function)(A1,A2,A3), AA1 a1, AA2 a2, AA3 a3)
   RWTHRTHROWSANY
{
   return rwMakeFunctorR0MA3(RWBoolean,Callee,callee,DR,function,A1,a1,A2,a2,A3,a3);
}
 
#endif


/****************************************************************************/

inline
RWBoolean
RWGuardedRunnable::isRunnable(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWGuardedRunnable,isRunnable(void):RWBoolean);
   return noGuard_ ? TRUE : runnableGuard_();
}
 
inline
RWGuardedRunnable::operator RWRunnable(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWGuardedRunnable,operator RWRunnable(void));
   return runnable_;
}

/****************************************************************************/

inline
RWGuardedRunnableQueue::RWGuardedRunnableQueue(size_t maxEntries)
   RWTHRTHROWSANY
   :
      RWPCValQueue<RWGuardedRunnable>(maxEntries),
      guards_(FALSE)
{
   RWTHRTRACEMF(RWGuardedRunnableQueue,RWGuardedRunnableQueue(size_t));
}
 
inline
void
RWGuardedRunnableQueue::writeRunnable(const RWRunnable& r)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWGuardedRunnableQueue,writeRunnable(const RWRunnable&):void);
   RWPCValQueue<RWGuardedRunnable>::write(RWGuardedRunnable(r));
}
 
inline
void
RWGuardedRunnableQueue::writeRunnable(const RWRunnable& r, const RWRunnableGuard& g)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWGuardedRunnableQueue,writeRunnable(const RWRunnable&,const RWRunnableGuard&):void);
   RWTHRASSERT(g.isValid());
   guards_ = TRUE;
   RWPCValQueue<RWGuardedRunnable>::write(RWGuardedRunnable(r,g));
}


#endif // __RWTHRGRDRNBLQ_H__

