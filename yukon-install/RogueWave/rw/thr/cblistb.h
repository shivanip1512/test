#if !defined(__RWTHRCBLISTB_H__)
#  define __RWTHRCBLISTB_H__
/*****************************************************************************
 *
 * cblistb.h
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

cblistb.h - Class declarations for:
         
   RWCallbackListBase<Mutex> - Base class for callback list classes.
   RWCallbackElemBase - Base class for all callback classes.
   
See Also:

   cblistb.cc  - Out-of-line function definitions.

   cb1list.h - RWCallback1Elem<S1> and RWCallback1List<Mutex,S1>
   cb2list.h - RWCallback2Elem<S1,S2> and RWCallback2List<Mutex,S1,S2>

   cbmlistb.h - RWMaskedCallbackListBase<Mutex> and RWMaskedCallbackElemBase
                class declarations.

   cb1mlist.h - RWMaskedCallback1Elem<S1> and RWMaskedCallback1List<Mutex,S1>
   cb2mlist.h - RWMaskedCallback2Elem<S1,S2> and RWMaskedCallback2List<Mutex,S1,S2>


*****************************************************************************/

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

enum RWCallbackScope {
      RW_THR_CALL_ONCE,
      RW_THR_CALL_REPEATEDLY
   };      
      
class RWTHRExport RWCallbackElemBase {

   RW_THR_DECLARE_TRACEABLE

   protected:

      RWCallbackScope      scope_;

   public:

      // Let compiler generate:
      //    RWCallbackElemBase(const RWCallbackElemBase& second);
      //    RWCallbackElemBase& operator=(const RWCallbackElemBase& second);
      //    ~RWCallbackElemBase(void);

      RWBoolean
      operator==(const RWCallbackElemBase& second) const
         RWTHRTHROWSNONE;

      // Required for stdlib-based list collections
      RWBoolean
      operator<(const RWCallbackElemBase& second) const
         RWTHRTHROWSNONE;

      RWCallbackScope
      scope(void) const
         RWTHRTHROWSNONE;

      void
      scope(RWCallbackScope newScope)
         RWTHRTHROWSNONE;

   protected:
      // Make constructors protected since this is a base class

      RWCallbackElemBase(void)
         RWTHRTHROWSANY;

      RWCallbackElemBase(RWCallbackScope scope)
         RWTHRTHROWSANY;

   private:
};


template <class Mutex>
class RWTHRTExport RWCallbackListBase {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef  Mutex MutexType;
   
   protected:
   
      // Callback lists are generally owned by other objects, so
      // it uses the owner's mutex instead of creating a local one.
      Mutex&                                 mutex_; 

   public:
      
      // Let compiler generate:
      // RWCallbackListBase(const RWCallbackListBase<Mutex>& second);
      // RWCallbackListBase<Mutex>& operator=(const RWCallbackListBase<Mutex>& second);

   protected:

      // Construct the callback list object, passing in the mutex
      // that is to be used to protect the list state.
      // Make protected since this is a base class.
      RWCallbackListBase(Mutex& mutex)
         RWTHRTHROWSANY;

      // Get a reference to the mutex associated with the callback list
      Mutex&
      mutex(void) const
         RWTHRTHROWSNONE;

};

#if defined(RW_THR_COMPILER_BORLAND_CPP)
#include <rw/thr/mutex.h>
template class RWTHRIExport RWCallbackListBase<RWMutexLock>;
#endif

/*****************************************************************************/

inline
RWCallbackElemBase::RWCallbackElemBase(void)
   RWTHRTHROWSANY
   :
      scope_(RW_THR_CALL_ONCE)
{
}

inline
RWCallbackElemBase::RWCallbackElemBase(RWCallbackScope scope)
   RWTHRTHROWSANY
   :
      scope_(scope)
{
   RWTHRTRACEMF(RWCallbackElemBase,RWCallbackElemBase(RWCallbackScope));
}

inline
RWBoolean
RWCallbackElemBase::operator==(const RWCallbackElemBase& second) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCallbackElemBase,operator==(const RWCallbackElemBase&):RWBoolean);

   return scope_ == second.scope_;
}

inline
RWBoolean
RWCallbackElemBase::operator<(const RWCallbackElemBase& second) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCallbackElemBase,operator<(const RWCallbackElemBase&):RWBoolean);

   // Just compare scope (we don't really care what we compare)
   return scope_ < second.scope_;
}

inline
RWCallbackScope
RWCallbackElemBase::scope(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCallbackElemBase,scope(void):RWCallbackScope);
   return scope_;
}

inline
void
RWCallbackElemBase::scope(RWCallbackScope newScope)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCallbackElemBase,scope(RWCallbackScope):void);
   scope_ = newScope;
}

template <class Mutex>
// protected:
inline
RWCallbackListBase<Mutex>::RWCallbackListBase(Mutex& mutex)
   RWTHRTHROWSANY
   :
      mutex_(mutex)
{
   RWTHRTRACEMF(RWCallbackListBase<Mutex>,RWCallbackListBase(Mutex&));
}

template <class Mutex>
// protected
inline
Mutex&
RWCallbackListBase<Mutex>::mutex(void) const
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWCallbackListBase<Mutex>,mutex(void):Mutex&);
   return mutex_;
}

#  if defined(RW_COMPILE_INSTANTIATE)  
#     include <rw/thr/cblistb.cc>
#  endif

#endif // __RWTHRCBLISTB_H__

