/***************************************************************************
 *
 * handle.cc
 *
 * $Id$
 *
 * Copyright (c) 1998-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 **************************************************************************/
/*
 * defs for Handle classes 
 */

#include <rw/rwerr.h>
#include <rw/toolpro/handle.h>

#ifdef RW_MULTI_THREAD
# if !defined(RW_TEMPLATE_STATIC_INIT_BUG)
   template <class C> RWMutex RWNetHandle<C>::mutexLock(RWMutex::staticCtor); 
#  define RWNETHANDLE_MULTITHREAD_LOCK (RWNetHandle<C>::mutexLock)
# else
#  if defined(__TURBOC__)
#    define RWNETHANDLE_MULTITHREAD_LOCK (RWNetHandle<C>::mutexLock)
#  else
#    define RWNETHANDLE_MULTITHREAD_LOCK (RWNetHandleMutexLock)
#  endif
# endif
#else
# define RWNETHANDLE_MULTITHREAD_LOCK /* nothing */
#endif


template <class C>
RWNetHandle<C>::RWNetHandle()
#if defined RW_MULTI_THREAD && defined(RW_TEMPLATE_STATIC_INIT_BUG) && defined(__TURBOC__)
:mutexLock(RWGetHandleMutex<C>())
#endif
{
  attach(&RWNetHandleCounter::nilCounter);
}

template <class C>
RWNetHandle<C>::RWNetHandle(C* obj)
#if defined RW_MULTI_THREAD && defined(RW_TEMPLATE_STATIC_INIT_BUG) && defined(__TURBOC__)
:mutexLock(RWGetHandleMutex<C>()) 
#endif
{
  attach(new RWNetHandleCounter(obj));
}

template <class C>
RWNetHandle<C>::RWNetHandle(const RWNetHandle<C>& x)
#if defined RW_MULTI_THREAD && defined(RW_TEMPLATE_STATIC_INIT_BUG) && defined(__TURBOC__)
:mutexLock(RWGetHandleMutex<C>()) 
#endif
{
  attach(x.counter_);
}

template <class C>
RWNetHandle<C>::~RWNetHandle()
{
  detach();
}

template <class C>
RWNetHandle<C>& RWNetHandle<C>::operator=(const RWNetHandle<C>& x)
{
  detach();
  attach(x.counter_);
  return *this;
}

template <class C>
void RWNetHandle<C>::attach(RWNetHandleCounter* counter)
{
  counter_ = counter;
  counter_->addReference(RWNETHANDLE_MULTITHREAD_LOCK);
  obj_ = (C*)counter_->data_;
}

template <class C>
void RWNetHandle<C>::detach()
{
   if (counter_->removeReference(RWNETHANDLE_MULTITHREAD_LOCK)==0) {
     delete obj_;
     delete counter_;
  }
}

template <class C>
void RWNetHandle<C>::nilErr() const
{
  RWTHROW( RWInternalErr("attempt to dereference a nil handle") );
}

#if defined(RW_MULTI_THREAD) && defined(RW_TEMPLATE_STATIC_INIT_BUG) && defined(__TURBOC__)
template <class C> RWMutex& RWGetHandleMutex(void) {
 static RWMutex RWNetHandleMutexLock(RWMutex::staticCtor);
 return RWNetHandleMutexLock;
}
#endif
