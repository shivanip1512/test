#if !defined(__RWTHRCB2MLIST_CC__)
#  define __RWTHRCB2MLIST_CC__
/*****************************************************************************
 *
 * cb2mlist.cc
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

         
   RWMaskedCallback2Elem<S1,S2> - Callback for RWFunctor2<S1,S2> instances.
   RWMaskedCallback2List<Mutex,S2,S2> - Callback list for RWMaskedCallback2Elem.

See Also:

   cb2mlist.h - Class declarations.
   cb2mlist.cc  - Out-of-line function definitions for:

*****************************************************************************/


RW_THR_IMPLEMENT_TRACEABLE_T2(RWMaskedCallback2Elem,S1,S2)

template <class S1, class S2>
RWFunctor2<S1,S2>
RWMaskedCallback2Elem<S1,S2>::functor(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWMaskedCallback2Elem,S1,S2,"functor(void):RWFunctor2<S1,S2>");
   return functor_;
}

RW_THR_IMPLEMENT_TRACEABLE_T3(RWMaskedCallback2List,Mutex,S1,S2)

template <class Mutex, class S1, class S2>
void
RWMaskedCallback2List<Mutex,S1,S2>::add(const RWFunctor2<S1,S2>& functor,
                                        unsigned long mask,
                                        RWCallbackScope scope)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWMaskedCallback2List,Mutex,S1,S2,add(const RWFunctor2<S1,S2>&,unsigned long,RWCallbackScope):void);

   RWTHRASSERT((this->mutex()).isAcquired()); // USAGE ERROR - Mutex must be locked when this member is called!

   addList_.append(RWMaskedCallback2Elem<S1,S2>(functor,mask,scope));
}

template <class Mutex, class S1, class S2>
void
RWMaskedCallback2List<Mutex,S1,S2>::operator()(S1 s1,
                                               S2 s2,
                                               unsigned long cbMask)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWMaskedCallback2List,Mutex,S1,S2,operator()(S1,S2,unsigned long):void);

   // Clients of this class are responsible for providing synchronization
   // for this member - only one thread should execute this function 
   // at one time!

   RWTHRASSERT(!(this->mutex()).isAcquired()); // USAGE ERROR - Mutex must be unlocked when calling this member!

   // Update the list first...
   update();

   // Are there *any* callbacks with a mask that matches?
   if (cbMask & this->mask()) {
      unsigned long newMask = 0;
      // Yes there is at least one, so traverse the list...
      RWTValSlistIterator<RWMaskedCallback2Elem<S1,S2> > iter(currentList_);
      while(iter()) {
         RWMaskedCallback2Elem<S1,S2> current = iter.key();
         //  Does this callback mask match?
         if (current.mask() & cbMask) {
            // Yes, then invoke it... and let any exceptions fly!
            current(s1,s2);
            // Are we going to use this callback anymore?
            if (RW_THR_CALL_ONCE == current.scope()) {
               // Nope, so remove it from the list
               iter.remove();
            }
            else {
               // We're keeping this callback - update the mask...
               newMask |= current.mask();
            }
         }
         else {
            // We're not calling this callback but might later - update the mask...
            newMask |= current.mask();
         }
      }
      this->mask(newMask);
   }
}

template <class Mutex, class S1, class S2>
void
RWMaskedCallback2List<Mutex,S1,S2>::remove(const RWFunctor2<S1,S2>& functor)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWMaskedCallback2List,Mutex,S1,S2,remove(const RWFunctor2<S1,S2>&));

   RWTHRASSERT((this->mutex()).isAcquired()); // USAGE ERROR - Mutex must be locked when this member is called!

   removeList_.append(RWMaskedCallback2Elem<S1,S2>(functor));
}

template <class Mutex, class S1, class S2>
//private
void
RWMaskedCallback2List<Mutex,S1,S2>::update(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWMaskedCallback2List,Mutex,S1,S2,update(void):void);

   // Lock to prevent changes to the update lists
   RWLockGuard<Mutex> guard(this->mutex());

   // Do additions first then removals 
   // (in case an entry was added and removed since the last update)
   
   // Additions result in an update of the list mask, but removals do not,
   // because there may be other entries in the list that have the same 
   // mask as the entry being removed...
   
   // Update from add list...
   while(!addList_.isEmpty()) {
      RWMaskedCallback2Elem<S1,S2> current = addList_.removeFirst();
      // Update mask
      unsigned long newMask = this->mask();
      newMask |= current.mask();
      this->mask(newMask);
      currentList_.append(current);
   }

   // Update from remove list
   while(!removeList_.isEmpty()) {
      // Remove all matching entries from list
      RWMaskedCallback2Elem<S1,S2> current = removeList_.removeFirst();
      while(currentList_.remove(current)){}
   }
}

#endif // __RWTHRCB2MLIST_CC__
