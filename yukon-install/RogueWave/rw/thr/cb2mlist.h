#if !defined(__RWTHRCB2MLIST_H__)
#  define __RWTHRCB2MLIST_H__
/*****************************************************************************
 *
 * cb2mlist.h
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

cb2mlist.h - Class declarations for:
         
   RWMaskedCallback2Elem<S1,S2> - Callback for RWFunctor2<S1,S2> instances.
   RWMaskedCallback2List<Mutex,S2,S2> - Callback list for RWMaskedCallback2Elem.

See Also:

   cb2mlist.cc  - Out-of-line function definitions.
   cbmlistb.h RWMaskCallbackListBase and RWMaskedCallbackElemBase class declaration.

*****************************************************************************/

#  if !defined(__RWTHRCBMB_H__)
//    get RWMaskedCallbackElemBase
#     include <rw/thr/cbmlistb.h>
#  endif

#  if !defined(__RWTHRFUNC2_H__)
//    get RWFunctor2<S1,S2>
#     include <rw/thr/func2.h>
#  endif

#  if !defined(__RWTVSLIST_H__)
#     include <rw/tvslist.h>
#  endif

template <class S1, class S2>
class RWTHRTExport RWMaskedCallback2Elem :
   public RWMaskedCallbackElemBase {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWFunctor2<S1,S2>    functor_;

   public:

      RWMaskedCallback2Elem(void)
         RWTHRTHROWSANY;

      RWMaskedCallback2Elem(const RWFunctor2<S1,S2>& functor,
                        unsigned long mask=0,
                        RWCallbackScope scope=RW_THR_CALL_ONCE)
         RWTHRTHROWSANY;

      // Let compiler generate:
      //    RWMaskedCallback2Elem(const RWMaskedCallback2Elem<S1,S2>& second);
      //    RWMaskedCallback2Elem<S1,S2>& operator=(const RWMaskedCallback2Elem<S1,S2>& second);
      //    ~RWMaskedCallback2Elem(void);

      RWFunctor2<S1,S2>
      functor(void) const
         RWTHRTHROWSANY;

      void
      functor(const RWFunctor2<S1,S2>& newFunctor)
         RWTHRTHROWSANY;

      // Invoke the functor with the given argument values
      void
      operator()(S1 s1,
                 S2 s2)
         RWTHRTHROWSANY;

      RWBoolean
      operator==(const RWMaskedCallback2Elem<S1,S2>& second) const
         RWTHRTHROWSANY;

   protected:
   private:
};

template <class Mutex, class S1, class S2>
class RWTHRTExport RWMaskedCallback2List :
   public RWMaskedCallbackListBase<Mutex> {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef  S1    S1Type;
      typedef  S2    S2Type;
      typedef  Mutex MutexType;   

   protected:
   
      RWTValSlist<RWMaskedCallback2Elem<S1,S2> > addList_;
      RWTValSlist<RWMaskedCallback2Elem<S1,S2> > removeList_;
      RWTValSlist<RWMaskedCallback2Elem<S1,S2> > currentList_;

   public:
      
      // Construct the callback list object, passing in the mutex
      // that is to be used to protect the list state.
      RWMaskedCallback2List(Mutex& mutex)
         RWTHRTHROWSANY;

      // Let compiler generate:
      // RWMaskedCallback2List(const RWMaskedCallback2List<Mutex,S1,S2>& second);
      // RWMaskedCallback2List<Mutex,S1,S2>& operator=(const RWMaskedCallback2List<Mutex,S1,S2>& second);

      // Add an entry to the end of the callback list 
      // The same functor may be added more than once with different
      // masks, but it is more efficient to install the functor once 
      // and combine the masks with a bit-wise 'OR' operation!
      //    Adds an entry to the add list, whose entries are appended
      //    to the current list the next time the callback list is invoked.

      // NOTE: The mutex passed to the callback list constructor
      //       MUST be LOCKED when this member is called!
      void
      add(const RWFunctor2<S1,S2>& functor,
          unsigned long mask,
          RWCallbackScope scope)
         RWTHRTHROWSANY;

      // Acquires the mutex, updates the list, releases the mutex, 
      // and for each callback entry, starting from the front of the list, 
      // compares the entry's callback mask to the specified mask,
      // and if there are any matching 1's, invokes the callback,
      // passing the specified data, and then deletes the callback entry
      // if its scope is found to be RW_THR_CALL_ONCE.

      // NOTE: The mutex passed to the callback list constructor
      //       MUST be UNLOCKED when this member is called!
      void
      operator()(S1 s1,
                 S2 s2,
                 unsigned long mask)
         RWTHRTHROWSANY;

      // Remove all entries from the callback list that contain the 
      // specified functor.
      //    Adds an entry to the remove list, whose entries are used
      //    to identify entries to be removed from the current list.

      // NOTE: The mutex passed to the callback list constructor
      //       MUST be LOCKED when this member is called!
      void
      remove(const RWFunctor2<S1,S2>& functor)
         RWTHRTHROWSANY;

   private:
      // Acquire the mutex, update the current list from the additions and 
      // removals lists, and release the mutex.
      void 
      update(void)
         RWTHRTHROWSANY;

};



/*****************************************************************************/

template <class S1, class S2>
inline
RWMaskedCallback2Elem<S1,S2>::RWMaskedCallback2Elem(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWMaskedCallback2Elem,S1,S2,RWMaskedCallback2Elem(void));
}

template <class S1, class S2>
inline
RWMaskedCallback2Elem<S1,S2>::RWMaskedCallback2Elem(const RWFunctor2<S1,S2>& functor,
                                            unsigned long mask,
                                            RWCallbackScope scope)
   RWTHRTHROWSANY
   :
      functor_(functor),
      RWMaskedCallbackElemBase(mask,scope)
{
   RWTHRTRACEMFT2(RWMaskedCallback2Elem,S1,S2,RWMaskedCallback2Elem(const RWFunctor2<S1,S2>&,unsigned long,RWCallbackScope));
}

template <class S1, class S2>
inline
void
RWMaskedCallback2Elem<S1,S2>::functor(const RWFunctor2<S1,S2>& newFunctor)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWMaskedCallback2Elem,S1,S2,functor(const RWFunctor2<S1,S2>&):void);
   functor_ = newFunctor;
}

template <class S1, class S2>
inline
void
RWMaskedCallback2Elem<S1,S2>::operator()(S1 s1,
                                    S2 s2)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWMaskedCallback2Elem,S1,S2,operator()(S1 s1, S2 s2):void);
   functor_(s1,s2);
}

template <class S1, class S2>
inline
RWBoolean
RWMaskedCallback2Elem<S1,S2>::operator==(const RWMaskedCallback2Elem<S1,S2>& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWMaskedCallback2Elem,S1,S2,operator==(const RWMaskedCallback2Elem<S1,S2>&):RWBoolean);
   // Only compare functors and ignore the rest... 
   return functor_ == second.functor_;
}

template <class Mutex, class S1, class S2>
inline
RWMaskedCallback2List<Mutex,S1,S2>::RWMaskedCallback2List(Mutex& mutex)
   RWTHRTHROWSANY
   :
      RWMaskedCallbackListBase<Mutex>(mutex)
{
   RWTHRTRACEMFT3(RWMaskedCallback2List,Mutex,S1,S2,RWMaskedCallback2List(Mutex& mutex));
}

#  if defined(RW_COMPILE_INSTANTIATE)  
#     include <rw/thr/cb2mlist.cc>
#  endif

#endif // __RWTHRCB2MLIST_H__

