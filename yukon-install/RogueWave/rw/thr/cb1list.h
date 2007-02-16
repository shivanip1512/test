#if !defined(__RWTHRCB1LIST_H__)
#  define __RWTHRCB1LIST_H__
/*****************************************************************************
 *
 * cb1list.h
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

cb1list.h - Class declarations for:
         
   RWCallback1Elem<S1> - Callback for RWFunctor1<S1> instances.
   RWCallback1List<Mutex,S1> - Callback list for RWCallback1Elem.

See Also:

   cb1list.cc  - Out-of-line function definitions.

   cblistb.h - RWCallbackElemBase and RWCallbackListBase definition

   func1.h - RWFunctor1 family of functor definitions

*****************************************************************************/

#  if !defined(__RWTHRCBLISTB_H__)
//    get RWCallbackElemBase and RWCallbackListBase
#     include <rw/thr/cblistb.h>
#  endif

#  if !defined(__RWTHRFUNC1_H__)
//    get RWFunctor1<S1>
#     include <rw/thr/func1.h>
#  endif

#  if !defined(__RWTVSLIST_H__)
#     include <rw/tvslist.h>
#  endif

template <class S1>
class RWTHRTExport RWCallback1Elem :
   public RWCallbackElemBase {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWFunctor1<S1>    functor_;

   public:

      RWCallback1Elem(void)
         RWTHRTHROWSANY;

      RWCallback1Elem(const RWFunctor1<S1>& functor,
                  RWCallbackScope scope = RW_THR_CALL_ONCE)
         RWTHRTHROWSANY;

      // Let compiler generate:
      //    RWCallback1Elem(const RWCallback1Elem<S1>& second);
      //    RWCallback1Elem<S1>& operator=(const RWCallback1Elem<S1>& second);
      //    ~RWCallback1Elem(void);

      RWFunctor1<S1>
      functor(void) const
         RWTHRTHROWSANY;

      void
      functor(const RWFunctor1<S1>& newFunctor)
         RWTHRTHROWSANY;

      // Invoke the functor with the given argument values
      void
      operator()(S1 s1)
         RWTHRTHROWSANY;

      RWBoolean
      operator==(const RWCallback1Elem<S1>& second) const
         RWTHRTHROWSANY;

   protected:
   private:
};

template <class Mutex, class S1>
class RWTHRTExport RWCallback1List :
   public RWCallbackListBase<Mutex> {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef  S1    S1Type;
      typedef  Mutex MutexType;   

   protected:
   
      RWTValSlist<RWCallback1Elem<S1> > addList_;
      RWTValSlist<RWCallback1Elem<S1> > removeList_;
      RWTValSlist<RWCallback1Elem<S1> > currentList_;

   public:
      
      // Construct the callback list object, passing in the mutex
      // that is to be used to protect the list state.
      RWCallback1List(Mutex& mutex)
         RWTHRTHROWSANY;

      // Let compiler generate:
      // RWCallback1List(const RWCallback1List<Mutex,S1>& second);
      // RWCallback1List<Mutex,S1>& operator=(const RWCallback1List<Mutex,S1>& second);

      // Add an entry to the end of the callback list 
      // The same functor may be added more than once with different
      // s, but it is more efficient to install the functor once 
      // and combine the s with a bit-wise 'OR' operation!
      //    Adds an entry to the add list, whose entries are appended
      //    to the current list the next time the callback list is invoked.

      // NOTE: The mutex passed to the callback list constructor
      //       MUST be LOCKED when this member is called!
      void
      add(const RWFunctor1<S1>& functor,
          RWCallbackScope scope)
         RWTHRTHROWSANY;

      // Acquires the mutex, updates the list, releases the mutex, 
      // and for each callback entry, starting from the front of the list, 
      // invokes the callback passing the specified data, and then 
      // deletes the callback entry if its scope is found to be RW_THR_CALL_ONCE.

      // NOTE: The mutex passed to the callback list constructor
      //       MUST be UNLOCKED when this member is called!
      void
      operator()(S1 s1)
         RWTHRTHROWSANY;

      // Remove all entries from the callback list that contain the 
      // specified functor.
      //    Adds an entry to the remove list, whose entries are used
      //    to identify entries to be removed from the current list.

      // NOTE: The mutex passed to the callback list constructor
      //       MUST be LOCKED when this member is called!
      void
      remove(const RWFunctor1<S1>& functor)
         RWTHRTHROWSANY;

   private:
      // Acquire the mutex, update the current list from the additions and 
      // removals lists, and release the mutex.
      void 
      update(void)
         RWTHRTHROWSANY;

};


/*****************************************************************************/

template <class S1>
inline
RWCallback1Elem<S1>::RWCallback1Elem(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWCallback1Elem,S1,RWCallback1Elem(void));
}

template <class S1>
inline
RWCallback1Elem<S1>::RWCallback1Elem(const RWFunctor1<S1>& functor,
                             RWCallbackScope scope)
   RWTHRTHROWSANY
   :
      functor_(functor),
      RWCallbackElemBase(scope)
{
   RWTHRTRACEMFT1(RWCallback1Elem,S1,RWCallback1Elem(const RWFunctor1<S1>&,RWCallbackScope));
}

template <class S1>
inline
void
RWCallback1Elem<S1>::functor(const RWFunctor1<S1>& newFunctor)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWCallback1Elem,S1,functor(const RWFunctor1<S1>&):void);
   functor_ = newFunctor;
}

template <class S1>
inline
void
RWCallback1Elem<S1>::operator()(S1 s1)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWCallback1Elem,S1,operator()(S1 s1):void);
   functor_(s1);
}

template <class S1>
inline
RWBoolean
RWCallback1Elem<S1>::operator==(const RWCallback1Elem<S1>& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWCallback1Elem,S1,operator==(const RWCallback1Elem<S1>&):RWBoolean);
   // Only compare functors and ignore the rest... 
   return functor_ == second.functor_;
}

template <class Mutex, class S1>
inline
RWCallback1List<Mutex,S1>::RWCallback1List(Mutex& mutex)
   RWTHRTHROWSANY
   :
      RWCallbackListBase<Mutex>(mutex)
{
   RWTHRTRACEMFT2(RWCallback1List,Mutex,S1,RWCallback1List(Mutex& mutex));
}


#  if defined(RW_COMPILE_INSTANTIATE)  
#     include <rw/thr/cb1list.cc>
#  endif

#endif // __RWTHRCB1LIST_H__

