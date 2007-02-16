#if !defined(__RWTHRTHREADID_H__)
#  define __RWTHRTHREADID_H__
/*****************************************************************************
 *
 * threadid.h
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

threadid.h - Declarations for:
   
   RWThreadIdRep - A typedef for platform-specific thread id types.
   RWThreadId - A wrapper for platform-specific thread ids.

   Global functions related to RWThreadId:

   rwIsSelf - test whether a specified thread id refers to current thread.
   rwThreadHash - convert a RWThreadId instance to a hash value.
   rwThreadId - returns a RWThreadId instance that can be used to identify
                the calling thread.

   Note: A RWThreadId represents the identification given by the underlying
         thread API to a specific thread of execution.  These IDs are used to
         identify and control these threads, but are not necessarily related
         to any Threads.h++ objects.  The rwThread() and rwRunnable() functions
         may be used to determine the Threads.h++ object(s) (if any) that are 
         associated with the current thread of execution.  A thread id is valid
         only while the separate thread of execution exists. A thread
         "object" may continue to exist both before and after any associated 
         thread and its associated id may have existed.

See Also:

   threadid.cpp - Out-of-line function definitions.

*****************************************************************************/

//
// Typedef API-specific thread ids to common type
//
#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RW_STRING_H__)
//    For memset() function
#     define __RW_STRING_H__ <string.h>
#     include __RW_STRING_H__
#  endif

#  if defined(RW_THR_THREAD_API_OS2)

#     define INCL_BASE
#     if !defined(__RW_OS2_H__)
#        define __RW_OS2_H__ <os2.h>
#        include __RW_OS2_H__
#     endif

typedef TID  RWThreadIdRep;

#  elif defined(RW_THR_THREAD_API_POSIX)

typedef pthread_t RWThreadIdRep;

#  elif defined(RW_THR_THREAD_API_SOLARIS)
#     if !defined(__RW_THREAD_H__)
#        define __RW_THREAD_H__ <thread.h>
#        include __RW_THREAD_H__
#     endif

typedef thread_t  RWThreadIdRep;

#  elif defined(RW_THR_THREAD_API_WIN32)

#     if !defined(__RW_WINDOWS_H__)
#        define __RW_WINDOWS_H__ <windows.h>
#        include __RW_WINDOWS_H__
#     endif
#     if !defined(__RW_PROCESS_H__)
#        define __RW_PROCESS_H__ <process.h>
#        include __RW_PROCESS_H__
#     endif

#     if defined(RW_THR_THREAD_API_WIN32_MICROSOFT_CPP)

typedef unsigned  RWThreadIdRep;

#     elif defined(RW_THR_THREAD_API_WIN32_BORLAND_CPP)

typedef DWORD RWThreadIdRep;

#     endif
#  else
#     error RWThreadId declaration missing!
#  endif

// POSIX implementations based on CMA threads use a structure 
// for thread ids.  Cfront-based compiler's skip this file when doing 
// template instantiation, so we can't define a global comparison 
// operator for pthread_t, a requirement if we are going to use 
// pthread_t in Tools.h++ templatized collections.  To solve this 
// problem, we've had to wrap the native thread id structures with 
// another class.  Now we can define a comparison operator that can be 
// used by Tools.h++ template collections under all APIs.

class RWTHRExport RWThreadId {
   
   // Do not add trace stuff to this class - RWThreadId is used *by* the trace facility!

   private:

      RWThreadIdRep   threadId_;

   public:
      
      RWThreadId(void)
         RWTHRTHROWSNONE;

      RWThreadId(const RWThreadIdRep& threadId) 
         RWTHRTHROWSNONE;

      // Do nothing destructor to keep stdlib happy!
      ~RWThreadId(void)
         RWTHRTHROWSNONE;

      // Clear the native thread id so it won't match other native ids
      void
      clear(void)
         RWTHRTHROWSNONE;

      unsigned
      hash(void) const
         RWTHRTHROWSNONE;

      // Assign one id to another
      RWThreadId&
      operator=(const RWThreadId& second)
         RWTHRTHROWSNONE;

      // Return a reference to the native thread id
      operator RWThreadIdRep&() const
         RWTHRTHROWSNONE;
      
      // Return the address of the native thread id member
      RWThreadIdRep*
      address(void) const
         RWTHRTHROWSNONE;

      // Compare native thread ids for equality
      RWBoolean
      operator==(const RWThreadId& second) const
         RWTHRTHROWSNONE;

      // Compare native thread ids for inequality
      RWBoolean
      operator!=(const RWThreadId& second) const
         RWTHRTHROWSNONE;

      // Compare native thread ids for inequality
      // (Required for Standard C++ Library collections)
      RWBoolean
      operator<(const RWThreadId& second) const
         RWTHRTHROWSNONE;

};

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
#     include <rw/tvslist.h>
// Export or import template instantiations using this class...
template class RWTHRIExport RWTValSlist<RWThreadId>;
#    if defined(RW_NO_STL)
template class RWTHRIExport RWTValSlink<RWThreadId>;
template class RWTHRIExport RWTIsvSlist<RWTValSlink<RWThreadId> >;
#    endif
#  endif

/*****************************************************************************/

inline
RWBoolean
rwIsSelf(const RWThreadId& id)
   RWTHRTHROWSNONE;

extern rwthrexport
unsigned 
rwThreadHash(const RWThreadId& id)
   RWTHRTHROWSNONE;

inline
RWThreadId  
rwThreadId(void)
   RWTHRTHROWSNONE;

/*****************************************************************************/

inline
RWThreadId::RWThreadId(void) 
   RWTHRTHROWSNONE
{
}

inline
RWThreadId::RWThreadId(const RWThreadIdRep& threadId) 
   RWTHRTHROWSNONE
   : 
      threadId_(threadId) 
{
}

inline
RWThreadId::~RWThreadId(void) 
   RWTHRTHROWSNONE
{
}

// Clear the native thread id so it won't match other native ids
inline
void
RWThreadId::clear(void)
   RWTHRTHROWSNONE
{
   ::memset(&threadId_,0,sizeof(RWThreadIdRep));
}

// Produce a unique id which can be used to identify the thread.
inline
unsigned
RWThreadId::hash(void) const
   RWTHRTHROWSNONE
{
#  if defined(RW_THR_CMA_THREAD_ID)
   // CMA-based threads use a structure for thread ids
   // The second field is supposed to be a unique identifier.
   return (unsigned)threadId_.field2;
#  elif defined(RW_THR_TEB_THREAD_ID)
   // DEC thread id points to DECThreads TEB structure
   // that contains a ulong sequence number...
   return threadId_->_Pfield(sequence);
#  else
   return (unsigned)threadId_;
#  endif
}

// Assign one id to another
inline
RWThreadId&
RWThreadId::operator=(const RWThreadId& second)
   RWTHRTHROWSNONE
{
   threadId_ = second.threadId_;
   return *this;
}

// Return a reference to the native thread id
inline
RWThreadId::operator RWThreadIdRep&() const
   RWTHRTHROWSNONE
{ 
   return (RWThreadIdRep&)threadId_; 
}
      
// Return the address of the native thread id member
inline
RWThreadIdRep*
RWThreadId::address(void) const
   RWTHRTHROWSNONE
{ 
   return (RWThreadIdRep*)&threadId_; 
}

// Compare native thread ids
inline
RWBoolean
RWThreadId::operator==(const RWThreadId& second) const
   RWTHRTHROWSNONE
{ 
#  if defined(RW_THR_THREAD_API_POSIX)
   return pthread_equal(threadId_,second.threadId_); 
#  else
   return threadId_ == second.threadId_;
#  endif
}

// Compare native thread ids
inline
RWBoolean
RWThreadId::operator!=(const RWThreadId& second) const
   RWTHRTHROWSNONE
{ 
#  if defined(RW_THR_THREAD_API_POSIX)
   return !pthread_equal(threadId_,second.threadId_); 
#  else
   return threadId_ != second.threadId_;
#  endif
}

// Compare native thread ids for inequality
inline
RWBoolean
RWThreadId::operator<(const RWThreadId& second) const
  RWTHRTHROWSNONE
{
#  if defined(RW_THR_CMA_THREAD_ID)
   return (unsigned)(threadId_.field2) < (unsigned)(second.threadId_.field2);
#  elif defined(RW_THR_TEB_THREAD_ID)
   return threadId_->_Pfield(sequence) < second.threadId_->_Pfield(sequence);
#  else
   return threadId_ < second.threadId_;
#  endif
}


/*****************************************************************************/

inline
RWBoolean
rwIsSelf(const RWThreadId& id)
   RWTHRTHROWSNONE
{
   return id == rwThreadId();
}

inline
RWThreadId  
rwThreadId(void)
   RWTHRTHROWSNONE
{

#  if defined(RW_THR_THREAD_API_OS2)

      PTIB  pptib;
      PPIB  pppib;
      TID   tid;
      int rv;
      if (0 == (rv = ::DosGetInfoBlocks(&pptib,&pppib)))
         tid = pptib->tib_ptib2->tib2_ultid;
      return tid;

#  elif defined(RW_THR_THREAD_API_POSIX)
#    if defined(_PTHREAD_USE_MANGLED_NAMES_)
      // During the transition from POSIX 1003.4a Draft 4 (DCE Threads) to
      // POSIX 1003.1c-1995, DEC has mangled the new POSIX interface names
      // to allow both interfaces to coexist in the same program.  
      // The only problem is that they undef'd some of the macros used
      // to mangle the names (and I don't know why...).  
      // The macro for pthread_self is one that got undef'd... 
      // Use mangled name until DEC fixes the problem (next major release...)
      return ::__pthread_self();
#    else
      return ::pthread_self();
#    endif
#  elif defined(RW_THR_THREAD_API_SOLARIS)
      return ::thr_self();
#  elif defined(RW_THR_THREAD_API_WIN32)
      return ::GetCurrentThreadId();
#  endif

}

#endif // __RWTHRTHREADID_H__
