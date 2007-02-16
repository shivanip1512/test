#if !defined(__RWTHRCB1LIST_CC__)
#  define __RWTHRCB1LIST_CC__
/*****************************************************************************
 *
 * cb1list.cc
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

         
   RWCallback1Elem<S1> - Callback for RWFunctor1<S1> instances.
   RWCallback1List<Mutex> - Callback list for RWCallback1Elem.

See Also:

   cb1list.h - Class declarations.
   cb1list.cc  - Out-of-line function definitions for:

*****************************************************************************/

RW_THR_IMPLEMENT_TRACEABLE_T1(RWCallback1Elem,S1)

template <class S1>
RWFunctor1<S1>
RWCallback1Elem<S1>::functor(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWCallback1Elem,S1,"functor(void):RWFunctor1<S1>");
   return functor_;
}

RW_THR_IMPLEMENT_TRACEABLE_T2(RWCallback1List,Mutex,S1)

template <class Mutex, class S1>
void
RWCallback1List<Mutex,S1>::add(const RWFunctor1<S1>& functor,
                               RWCallbackScope scope)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWCallback1List,Mutex,S1,add(const RWFunctor1<S1>&,RWCallbackScope):void);

   RWTHRASSERT((this->mutex()).isAcquired()); // USAGE ERROR - Mutex must be locked when this member is called!

   addList_.append(RWCallback1Elem<S1>(functor,scope));
}

template <class Mutex, class S1>
void
RWCallback1List<Mutex,S1>::operator()(S1 s1)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWCallback1List,Mutex,S1,operator()(S1):void);

   // Clients of this class are responsible for providing synchronization
   // for this member - only one thread should execute this function 
   // at one time!

   RWTHRASSERT(!(this->mutex()).isAcquired()); // USAGE ERROR - Mutex must be unlocked when calling this member!

   // Update the list first...
   update();

   // Are there *any* callbacks with a mask that matches?
   // Yes there is at least one, so traverse the list...
   RWTValSlistIterator<RWCallback1Elem<S1> > iter(currentList_);
   while(iter()) {
      RWCallback1Elem<S1> current = iter.key();
      // Yes, then invoke it... and let any exceptions fly!
      current(s1);
      // Are we going to use this callback anymore?
      if (RW_THR_CALL_ONCE == current.scope()) {
         // Nope, so remove it from the list
         iter.remove();
      }
   }
}

template <class Mutex, class S1>
void
RWCallback1List<Mutex,S1>::remove(const RWFunctor1<S1>& functor)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWCallback1List,Mutex,S1,remove(const RWFunctor1<S1>&));

   RWTHRASSERT((this->mutex()).isAcquired()); // USAGE ERROR - Mutex must be locked when this member is called!

   removeList_.append(RWCallback1Elem<S1>(functor));
}

template <class Mutex, class S1>
//private
void
RWCallback1List<Mutex,S1>::update(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWCallback1List,Mutex,S1,update(void):void);

   // Lock to prevent changes to the update lists
   RWLockGuard<Mutex> guard(this->mutex());

   // Do additions first then removals 
   // (in case an entry was added and removed since the last update)
   
   // Additions result in an update of the list mask, but removals do not,
   // because there may be other entries in the list that have the same 
   // mask as the entry being removed...
   
   // Update from add list...
   while(!addList_.isEmpty()) {
      RWCallback1Elem<S1> current = addList_.removeFirst();
      currentList_.append(current);
   }

   // Update from remove list
   while(!removeList_.isEmpty()) {
      // Remove all matching entries from list
      RWCallback1Elem<S1> current = removeList_.removeFirst();
      while(currentList_.remove(current)){}
   }
}

#endif // __RWTHRCB1LIST_CC__
