#ifndef __RWNET_HANDLE_H__
#define __RWNET_HANDLE_H__
/***************************************************************************
 *
 * handle.h
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
 * RWNetHandle<C>: Reference counted handle to a C on the free store
 *
 * A Handle would be called a "smart pointer" in some C++ circles.  It
 * has the same semantics as a pointer, except that the thing it points
 * to is reference counted.  When the last handle referencing it is 
 * destructed, the referencee gets deleted.
 *
 * This class is used in the implementation of some of the Net.h++ classes.
 * It is not part of the supported customer interface of Net.h++.  It may
 * change without notice in future revisions.
 *
 * This simple Handle class does not have any of the smarts needed to convert
 * from one type of Handle to another, for example, to convert from a 
 * Handle<Derived> to a Handle<Base>.  To do this requires member function
 * templates, and no compilers support those yet.
 *
 * Since these classes contain only a single pointer, a reasonable
 * compiler ought to stick them in a register just like a regular pointer.
 *
 * It is possible (and very useful) to use this class to point to objects
 * that are derived from C, rather than being exactly of type C.  For this
 * to work properly, be sure that C has a virtual destructor.  Otherwise, the
 * call to delete the object when all pointers to it are gone will not likely
 * do the right thing.
 *
 * There is a const hole that I'm not sure how to plug.  
 * It is the same one present in the Tools.h++ collection classes, and the
 * Math.h++ classes.
 * The problem is due to the definition of op=.  You can do this:
 * <pre>
 *   const Handle p(o);  // If Handle has pointer semantics, I can't change p
 *   Handle q;
 *   Handle q=p;         // Oh-oh, q is not const
 *   q->nonConstFn();
 * </pre>
 *
 * If either of the preprocessor symbols RWDEBUG or RWHANDLE_CHECK is set,
 * then a test for
 * nil pointers is done before any operation involving a dereference, except
 * for the conversion to a pointer.  The conversion to a pointer is not
 * tested, because often this is called explicitly to test if we are refering
 * to a nil pointer.
 */

/*
 * Need to include rw/toolpro/netdefs.h in here, even though this is not 
 * really a net specific class, so that I get the extra compiler specific 
 * stuff Microsoft C++ 2.1 needs even with Tools.h++ 6.1
 */
#include <rw/toolpro/netdefs.h>
#include <rw/ref.h>
#ifdef RW_MULTI_THREAD
#include <rw/mutex.h>
#endif
class RWNETExport RWNetHandleCounter;

#ifdef RWDEBUG
#define RWHANDLE_CHECK
#endif


// This template function is used to initialize the RWNetHandle mutex.
// It is necessary for Borland compiler that can't handle template static members. 
#if defined(RW_MULTI_THREAD) && defined(RW_TEMPLATE_STATIC_INIT_BUG) && defined(__TURBOC__)
template <class C> RWMutex& RWGetHandleMutex(void);
#endif

template <class C> class RWNETExportTemplate RWNetHandle {
public:                             
  RWNetHandle();
  // Create a null pointer handle.

  RWNetHandle(C* obj);
  // Initialize the handle to point to obj.  obj must be an object allocated
  // on the heap.  It will be deleted automatically when no more Handles 
  // reference it.

  RWNetHandle(const RWNetHandle<C>& x);
  // Construct a handle to the same data that x points to.

  ~RWNetHandle();
  // Deletes the object being referenced if no more Handles are pointing to it.

  RWNetHandle<C>& operator=(const RWNetHandle<C>& x);
  // Set this pointer to point to the same thing as x.  The object currently
  // being pointed to is deleted if this was the last pointer to it.

  inline RWBoolean operator==(const RWNetHandle<C>& rhs) const;
  inline RWBoolean operator!=(const RWNetHandle<C>& rhs) const;
  inline C&       operator*() const;
  inline C*       operator->() const;
  // Dereference operators.  These return the object pointed to.  If either
  // of the RWDEBUG or RWHANDLE_CHECK macros is set, then an exception is
  // thrown if the pointer being dereferenced is the null pointer.

  inline operator C*() const;
  // Obtain a native C++ pointer to the object pointed to.
  // Unlike the dereference operators, this does not check first if the 
  // pointer is nil.
  // That's because often this function is 
  // called specifically to check if the reference is nil.

private:
  RWNetHandleCounter *counter_;
  C *obj_;
  // This is a wrapper for the object being reference counted.  The pointer
  // obj_ is provided to avoid having to go through the counter to access
  // the pointer.  It is strictly an optimization.

#if defined(RW_MULTI_THREAD) && !defined(RW_TEMPLATE_STATIC_INIT_BUG) && !defined(__TURBOC__)
  static RWMutex mutexLock;
#elif defined(RW_MULTI_THREAD) && defined(RW_TEMPLATE_STATIC_INIT_BUG) && defined(__TURBOC__)
  RWMutex& mutexLock;
#endif

  // This lock is used to control access to the reference counting.
  // We lock on a class by class basis.
  // This is safe as long as two different handle classes don't share the
  // same counter.
  // This will be an issue if we add the ability to do type conversion
  // from one handle class to another.
  // If the compiler has a problem with static members of a template
  // class, then we use a global template function to initialize the mutex
  // (for Borland) or a global mutex for other problematic compilers.

  void attach(RWNetHandleCounter *counter);
  void detach();
  // Change what's being pointed to.

  inline void nilCheck() const;
  void nilErr() const;
  // Test for nil pointer when dereferencing.  If RWDEBUG or RWHANDLE_CHECK
  // is set, and if this is a nil pointer, then rwNilCheck will call
  // rwNilErr.
};

  #if defined(RW_MULTI_THREAD) && defined(RW_TEMPLATE_STATIC_INIT_BUG) && !defined(__TURBOC__)
    extern RWNETExportFunc(RWMutex) RWNetHandleMutexLock;
  #endif
  // The global version of the RWNetHandle<C>::mutexLock.  Used by compilers
  // that can't handle template static members.

/*
 * The RWNetHandleCounter class is part of the implementation of
 * RWNetHandle.  It is not part of the public interface.  It needs
 * to be included in the header file because many of the member functions
 * of RWNetHandle are inline.
 *
 * Unfortunately, we can't make RWNetHandleCounter all private, because
 * there is no syntax for granting friend access to a whole set of template
 * classes.  So everything is public, but you have to promise not to use
 * it.
 * 
 * It would be possible to templatize HandleCounter, and then have a strongly
 * typed pointer to the data, but this adds too much complexity for only
 * small gain.  As it is, the only casting necessary is in the Handle
 * implementation.
 */

class RWNETExport RWNetHandleCounter : public RWReference {
public:
  RWNetHandleCounter(void *dat) : data_(dat) {}
  RWNetHandleCounter(RWReferenceFlag) : RWReference(STATIC_INIT), data_(0) {}
  ~RWNetHandleCounter()  {}
  // The second constructor is used to construct the nil object.  The destructor
  // is not called until no more references exist.

  void* const data_;
  // A pointer to the data being reference counted.  The data lives on the
  // heap.  It must be deleted by the user of the counter at the same time
  // the counter is deleted.  The counter can not delete it because it does 
  // not know its type, and so the proper destructor would not get called.

  static RWNetHandleCounter nilCounter;
  // A reference counted nil pointer.
};


/*****************************************************************
 *                                                               *
 *  Inline member function definitions                           *
 *                                                               *
 *****************************************************************/

template <class C>
inline void RWNetHandle<C>::nilCheck() const
{
#if defined(RWHANDLE_CHECK)
  if (obj_==0) {nilErr();}
#endif
}

template <class C>
inline RWBoolean RWNetHandle<C>::operator==(const RWNetHandle<C> &rhs) const
{
  return obj_ == rhs.obj_;
}

template <class C>
inline RWBoolean RWNetHandle<C>::operator!=(const RWNetHandle<C> &rhs) const
{
  return obj_ != rhs.obj_;
}

template <class C>
inline C& RWNetHandle<C>::operator*() const
{
  nilCheck();
  return *obj_;
}

template <class C>
inline C* RWNetHandle<C>::operator->() const
{
  nilCheck();
  return obj_;
}

template <class C>
inline RWNetHandle<C>::operator C* () const
{
  return obj_;
}

#ifdef RW_COMPILE_INSTANTIATE
# include "rw/toolpro/handle.cc"
#endif

#endif
