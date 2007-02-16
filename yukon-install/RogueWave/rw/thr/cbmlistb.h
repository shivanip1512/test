#if !defined(__RWTHRCBMLISTB_H__)
#  define __RWTHRCBMLISTB_H__
/*****************************************************************************
 *
 * cbmlistb.h
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

cbmlistb.h - Class declarations for:
         
   RWMaskedCallbackListBase<Mutex> - Base class for masked callback lists.
   RWMaskedCallbackElemBase - Base class for masked callback element classes

See Also:

   cbmlistmb.cc  - Out-of-line function definitions.

   cblistb.h - RWCallbackListBase<Mutex>

   cb1mlist.h - RWMaskedCallback1Elem<S1> and RWMaskedCallback1List<Mutex,S1>
   cb2mlist.h - RWMaskedCallback2Elem<S1,S2> and RWMaskedCallback2List<Mutex,S1,S2>

*****************************************************************************/

#  if !defined(__RWTHRCBLISTB_H__)
#     include <rw/thr/cblistb.h>
#  endif

class RWTHRExport RWMaskedCallbackElemBase :
   public RWCallbackElemBase {

   RW_THR_DECLARE_TRACEABLE

   protected:

      unsigned long        mask_;

   public:

      // Let compiler generate:
      //    RWMaskedCallbackElemBase(const RWMaskedCallbackElemBase& second);
      //    RWMaskedCallbackElemBase& operator=(const RWMaskedCallbackElemBase& second);
      //    ~RWMaskedCallbackElemBase(void);

      unsigned long
      mask(void) const
         RWTHRTHROWSNONE;

      void
      mask(unsigned long newMask)
         RWTHRTHROWSNONE;

      RWBoolean
      operator==(const RWMaskedCallbackElemBase& second)
         RWTHRTHROWSNONE;

   protected:
      // Make constructors protected since this is a base class

      RWMaskedCallbackElemBase(void)
         RWTHRTHROWSANY;

      RWMaskedCallbackElemBase(unsigned long mask,
                           RWCallbackScope scope)
         RWTHRTHROWSANY;

   private:
};


template <class Mutex>
class RWTHRTExport RWMaskedCallbackListBase :
   public RWCallbackListBase<Mutex> {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef  Mutex MutexType;
   
   protected:
   
      // An OR'd value of the masks of all the callbacks in list_.
      unsigned long                          mask_;
      
   public:
      
      // Let compiler generate:
      // RWMaskedCallbackListBase(const RWMaskedCallbackListBase<Mutex>& second);
      // RWMaskedCallbackListBase<Mutex>& operator=(const RWMaskedCallbackListBase<Mutex>& second);

   protected:

      // Construct the callback list object, passing in the mutex
      // that is to be used to protect the list state.
      // Make protected since this is a base class.
      RWMaskedCallbackListBase(Mutex& mutex)
         RWTHRTHROWSANY;

      // Get the mask associated with the list
      unsigned long
      mask(void) const
         RWTHRTHROWSNONE;

      // Set the mask associated with the list
      void
      mask(unsigned long newMask)
         RWTHRTHROWSNONE;

};

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
#     include <rw/thr/mutex.h>
template class RWTHRIExport RWMaskedCallbackListBase<RWMutexLock>;
#  endif

/*****************************************************************************/

inline
RWMaskedCallbackElemBase::RWMaskedCallbackElemBase(void)
   RWTHRTHROWSANY
   :
      mask_(0)
{
}

inline
RWMaskedCallbackElemBase::RWMaskedCallbackElemBase(unsigned long mask,
                                           RWCallbackScope scope)
   RWTHRTHROWSANY
   :
      mask_(mask),
      RWCallbackElemBase(scope)
{
   RWTHRTRACEMF(RWMaskedCallbackElemBase,RWMaskedCallbackElemBase(unsigned long,RWCallbackScope));
}

inline
unsigned long
RWMaskedCallbackElemBase::mask(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMaskedCallbackElemBase,mask(void):unsigned long);
   return mask_;
}

inline
void
RWMaskedCallbackElemBase::mask(unsigned long newMask)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMaskedCallbackElemBase,mask(unsigned long):void);
   mask_ = newMask;
}

inline
RWBoolean
RWMaskedCallbackElemBase::operator==(const RWMaskedCallbackElemBase& second)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMaskedCallbackElemBase,operator==(const RWMaskedCallbackElemBase&):RWBoolean);

   return mask_ == second.mask_ && RWCallbackElemBase::operator==(second);
}

template <class Mutex>
// protected:
inline
RWMaskedCallbackListBase<Mutex>::RWMaskedCallbackListBase(Mutex& mutex)
   RWTHRTHROWSANY
   :
      RWCallbackListBase<Mutex>(mutex),
      mask_(0)
{
   RWTHRTRACEMF(RWMaskedCallbackListBase<Mutex>,RWMaskedCallbackListBase(Mutex&));
}

template <class Mutex>
inline
// protected:
unsigned long
RWMaskedCallbackListBase<Mutex>::mask(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMaskedCallbackListBase<Mutex>,mask(void):unsigned long);
   return mask_;
}

template <class Mutex>
inline
// protected:
void
RWMaskedCallbackListBase<Mutex>::mask(unsigned long newMask)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWMaskedCallbackListBase<Mutex>,mask(unsigned long):void);
   mask_ = newMask;
}


#  if defined(RW_COMPILE_INSTANTIATE)  
#     include <rw/thr/cbmlistb.cc>
#  endif

#endif // __RWTHRCBLISTMB_H__

