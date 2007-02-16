#if !defined(__RWTHRTHRLOCAL_CC__)
#  define __RWTHRTHRLOCAL_CC__
/*****************************************************************************
 *
 * thrlocal.cc
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

thrlocal.cc - Out-of-line function definitions for:

   RWThreadLocal<Type> - Declare thread-local storage object.

See Also:

   thrlocal.h - Class declaration.

******************************************************************************/

#  if !defined(__RWTHRTHRLOCAL_H__)
#     include <rw/thr/thrlocal.h>
#  endif

#  if !defined(__RWTHRTHRUTIL_H__)
#     include <rw/thr/thrutil.h>
#  endif

#  if !defined(__RWTHRTHRMSG_H__)
#     include <rw/thr/thrmsg.h>
#  endif

#  if defined(RW_THR_THREAD_API_SOLARIS) || defined(RW_THR_THREAD_API_POSIX)
#    if !defined(__RW_ERRNO_H__) && !defined(EINVAL)
#      undef errno
#      define __RW_ERRNO_H__ <errno.h>
#      include __RW_ERRNO_H__
#    endif
#  endif

#  if defined(RW_THR_THREAD_API_OS2)
const size_t RW_THR_THREAD_LOCAL_INITIAL_BUCKETS = 32;
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWThreadLocal,Type)

// Unsafe
template <class Type>
RWThreadLocal<Type>::RWThreadLocal(RWStaticCtor)
   RWTHRTHROWSANY
   :
      RWMonitor<RWMutexLock>(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWThreadLocal<Type>,RWThreadLocal(RWStaticCtor));
   if (!isInitialized_)
      init();
}

// Unsafe
template <class Type>
RWThreadLocal<Type>::RWThreadLocal(void)
   RWTHRTHROWSANY
   :
      isInitialized_(FALSE)
{
   RWTHRTRACEMF(RWThreadLocal<Type>,RWThreadLocal(void));
   init();
}

template <class Type>
RWThreadLocal<Type>::~RWThreadLocal(void)
   RWTHRTHROWSANY
   // RWTHRInternalError
{
   RWTHRTRACEMF(RWThreadLocal<Type>,~RWThreadLocal(void));
   static const char msgHeader[] = "RWThreadLocal<Type>::~RWThreadLocal(void) - ";

   // Only one thread may destroy an instance, but other
   // threads may still be attempting to access the instance
   // if the instance is being destroyed as part of the static   
   // destruction phase of program termination...
   
   if (isInitialized_) {
      // Lock the key state so we can cleanup the instance
      LockGuard lock(monitor());

      if (keyCreated_) {

#  if defined(RW_THR_THREAD_API_OS2)
         RWTValHashDictionaryIterator<RWThreadId,Type*> iter(*dict_);

         // Delete any Type instances created threads that were not
         // started by RWThreads.h++ thread runnables...
         while(iter()) {
            delete iter.value();
         }

         // Delete the hash dictionary
         delete dict_;

#  elif defined(RW_THR_THREAD_API_POSIX)
#     if defined(RW_THR_THREAD_API_DCE)
         Type* valptr;
         if (-1 == ::pthread_getspecific(key_,(void **)&valptr)) {
            switch(errno) {
               case EINVAL:
                  RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
               default:
                  RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
                  { throw RWTHRInternalError(RWCString(msgHeader)+::rwErrnoMsg()); } 
            }
         }
         if (rwnil != valptr) {
            // Instance has been created...
            delete valptr;
            valptr = rwnil;
            // Clear the old thread local instance...
            if (-1 == ::pthread_setspecific(key_,(void *)valptr)) {
               switch(errno) {
                  case EINVAL:
                     RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
                  default:
                     RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
                     { throw RWTHRInternalError(RWCString(msgHeader)+::rwErrnoMsg()); } 
               }
            }
         }
#     else
         // Draft 7,9,10/Final
         Type* valptr;
         int rv;
         if (rwnil != (valptr = (Type *)::pthread_getspecific(key_))) {
            // Instance has been created...
            delete valptr;
            valptr = rwnil;
            // Clear the old thread local instance...
            if (0 != (rv = ::pthread_setspecific(key_,(void *)valptr))) {
               switch(rv) {
                  case ENOMEM:
                     // Not enough memory 
                     throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Memory);
                  case EINVAL:
                     RWTHRASSERT(0); // INTERNAL ERROR - Invalid key!
                  default:
                     RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
                     throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
               }
            }
         }
         if (0 != (rv =::pthread_key_delete(key_))) {
            RWTHRASSERT(0); // INTERNAL ERROR - Couldn't delete the key for some reason!
            throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); // ???
         }
#     endif

#  elif defined(RW_THR_THREAD_API_SOLARIS)
         Type* valptr;
         int rv;
         if (0 != (rv = ::thr_getspecific(key_,(void **)&valptr))) {
            switch(rv) {
               case EINVAL:
                  RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
               default:
                  RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
                  throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
            }
         }
         if (valptr != rwnil) {
            // Instance has been created...
            delete valptr;
            valptr = rwnil;
            // Clear the old thread local instance...
            if (0 != (rv = ::thr_setspecific(key_,(void *)valptr))) {
               switch(rv) {
                  case EINVAL:
                     RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
                  default:
                     RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
                     throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
               }
            }
         }

#  elif defined(RW_THR_THREAD_API_WIN32)
         // Assume other threads have already destroyed their entries
         if (!::TlsFree(key_)) {
            RWTHRASSERT(0); // INTERNAL ERROR - Couldn't delete the key for some reason!
            throw RWTHRInternalError(RWCString(msgHeader)+::rwGetLastErrorMsg());
         }

         objectsToDelete_.clearAndDestroy();

#  endif
         keyCreated_ = FALSE;
         isInitialized_ = FALSE;
      }
   }
}

template <class Type>
void
RWThreadLocal<Type>::checkKey(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadLocal<Type>,checkKey(void):void);
   static const char msgHeader[] = "RWThreadLocal<Type>::checkKey - ";

   if (!isInitialized_)
      init();

   // Check once - Has a key been create for this instance?
   if (!keyCreated_) {
      // No, so try to get one...
      
      // Lock the thread local state
      LockGuard lock(monitor());

      // Check again - Has a key been created for this instance?
      if (!keyCreated_) {
         
         // Still no, so create one...

#    if defined(RW_THR_THREAD_API_OS2)
         // OS2 maintains its own hash dictionary for thread local objects
         // This dictionary is keyed by thread id.
         dict_ = new RWThreadLocalDict(rwThreadHash, RW_THR_THREAD_LOCAL_INITIAL_BUCKETS);

#    elif defined(RW_THR_THREAD_API_POSIX)
#     if defined(RW_THR_THREAD_API_DCE)
         // DCE/Draft4
         if (-1 == ::pthread_keycreate(&key_,RWThreadLocal<Type>::destroy)) {
            // Couldn't create the key for some reason...
            switch(errno) {
               case EAGAIN:
                  // Not enough memory
                  { throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Memory); }
               case ENOMEM:
                  // Key name space is exhausted
                  { throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Resources); }
               case EINVAL:
                  RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
               default:
                  RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
                  { throw RWTHRInternalError(RWCString(msgHeader)+::rwErrnoMsg()); }
            }
         }
#     else
         // Draft 7,9,10/Final
         int rv;
         if (0 != (rv =::pthread_key_create(&key_,RWThreadLocal<Type>::destroy))) {
            switch(rv) {
               case EAGAIN:
                  // Not enough memory 
                  throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Memory);
               case ENOMEM:
                  // Key name space is exhausted (exceeded per process limit)
                  throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Resources);
               case EINVAL:
                  RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
               default:
                  RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
                  throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
            }
         }
#     endif

#    elif defined(RW_THR_THREAD_API_SOLARIS)
         int rv;
         if (0 != (rv =::thr_keycreate(&key_,RWThreadLocal<Type>::destroy))) {
            switch(rv) {
               case EAGAIN:
                  // Not enough memory 
                  throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Memory);
               case ENOMEM:
                  // Key name space is exhausted (exceeded per process limit)
                  throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Resources);
               case EINVAL:
                  RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
               default:
                  RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
                  throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
            }
         }

#    elif defined(RW_THR_THREAD_API_WIN32)
         if ((DWORD)-1 == (key_ = ::TlsAlloc()))
            throw RWTHRInternalError(RWCString(msgHeader)+::rwGetLastErrorMsg());

#    endif
         keyCreated_ = TRUE;
      }
   }
}

template <class Type>
// private
// static
void
#  if defined(RW_THR_THREAD_API_OS2)

RWThreadLocal<Type>::destroy(void* ptr,            // The thread local instance
                             RWThreadId threadId)  // The id of the thread that is exiting
   RWTHRTHROWSANY
{
   RWTHRTRACESMF(RWThreadLocal<Type>,destroy(void*,RWThreadId):void);

#  else

RWThreadLocal<Type>::destroy(void* ptr)      // The Type-instance to delete
   RWTHRTHROWSANY
{
   RWTHRTRACESMF(RWThreadLocal<Type>,destroy(void *):void);

#  endif

#  if !defined(RW_THR_THREAD_API_OS2)
   
   // POSIX, Solaris, Win32
   delete (Type*)ptr;

#  else 

   // OS/2
   // Remove and delete the dictionary entry 
   // associated with the specified thread id.
   ((RWThreadLocal<Type>*)ptr)->deregisterThread(threadId);

#  endif

}

#  if defined(RW_THR_THREAD_API_OS2)
// private
template<class Type>
void
RWThreadLocal<Type>::deregisterThread(RWThreadId threadId)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadLocal<Type>,deregisterThread(RWThreadId):void);
   
   // Find the Type-instance (if any) associated with the
   // specified thread, delete it, and remove the corresponding
   // entry from the hash dictionary.

   Type* valptr;

   // Delete the Type-instance
   if (dict_->findValue(threadId,valptr)) {
      
      delete valptr;

      // Remove the entry from the
      dict_->remove(threadId);
   }
}
#  endif

template <class Type>
Type*
RWThreadLocal<Type>::getValue(void) const
   RWTHRTHROWSANY
   // RWTHRResourceLimit - Insufficient memory or system resources
   // RWTHRInternalError
{
   RWTHRTRACEMF(RWThreadLocal<Type>,getValue(void):Type*);
   static const char msgHeader[] = "RWThreadLocal<Type>::getValue - ";

   // Is this the first access?
   RW_THR_CONST_CAST(RWThreadLocal<Type>*,this)->checkKey();

   Type* valptr;

#  if defined(RW_THR_THREAD_API_OS2)
   
   RWThreadId  threadId = ::rwThreadId();

   // Lock the hash dictionary
   LockGuard lock(monitor());

   if(!dict_->findValue(threadId, valptr)) {
      
      // No instance for this thread yet...
      // Construct a default instance
      valptr = new Type;

      dict_->insertKeyAndValue(threadId,valptr);

      RWThreadSelf curThread = ::rwThread();
      if (curThread.isValid()) {
         // Register this thread-local object with the current thread object 
         // so the thread-local Type-instance can be destroyed when the thread 
         // exits or is terminated...
         curThread.registerThreadLocalDestructor(RWThreadLocal<Type>::destroy,(void*)this,threadId);
      }
   }

#  elif defined(RW_THR_THREAD_API_POSIX)
#     if defined(RW_THR_THREAD_API_DCE)
   if (-1 == ::pthread_getspecific(key_,(void **)&valptr)) {
      switch(errno) {
         case EINVAL:
            RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
         default:
            RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
            { throw RWTHRInternalError(RWCString(msgHeader)+::rwErrnoMsg()); } 
      }
   }
   if (rwnil == valptr) {
      // Instance has not yet been created...
      valptr = new Type;
      if (-1 == ::pthread_setspecific(key_,(void *)valptr)) {
         delete valptr; // Recover storage!
         switch(errno) {
            case EINVAL:
               RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
            default:
               RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
               { throw RWTHRInternalError(RWCString(msgHeader)+::rwErrnoMsg()); } 
         }
      }
   }
#     else
   // Draft 7,9,10/Final
   if (rwnil == (valptr = (Type *)::pthread_getspecific(key_))) {
      // Instance has not yet been created...
      valptr = new Type;
      int rv;
      if (0 != (rv = ::pthread_setspecific(key_,(void *)valptr))) {
         delete valptr; // Recover storage!
         switch(rv) {
            case ENOMEM:
               // Not enough memory 
               throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Memory);
            case EINVAL:
               RWTHRASSERT(0); // INTERNAL ERROR - Invalid key!
            default:
               RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
               throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
         }
      }
   }
#     endif

#  elif defined(RW_THR_THREAD_API_SOLARIS)
   int rv;
   if (0 != (rv = ::thr_getspecific(key_,(void **)&valptr))) {
      switch(rv) {
         case EINVAL:
            RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
         default:
            RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
            throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
      }
   }
   if (valptr == rwnil) {
      // Instance has not yet been created...
      valptr = new Type;
      if (0 != (rv = ::thr_setspecific(key_,(void *)valptr))) {
         delete valptr; // Recover storage!
         switch(rv) {
            case EINVAL:
               RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
            default:
               RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
               throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
         }
      }
   }

#  elif defined(RW_THR_THREAD_API_WIN32)
   if (rwnil == (valptr = (Type*)::TlsGetValue(key_))) {
      // Instance has not yet been created...
      valptr = new Type;
      if (!::TlsSetValue(key_,(void *)valptr)) {
         RWTHRASSERT(0); // INTERNAL ERROR - Unexpected error!
         throw RWTHRInternalError(RWCString(msgHeader)+rwGetLastErrorMsg());
      }

      // Win32 provides no mechanism for deleting the thread-local
      // object when the creating thread exits.

      // We will either register this object with the current thread object,
      // if any, or register the object with the thread-local object.

      // The thread object (if one exists) will delete any thread-local objects
      // allocated just prior to thread exit.

      // The thread-local object will delete any objects allocated by
      // threads that were not spawned from thread class objects, when the
      // thread-local object goes out of scope and is destructed.

      RWThreadSelf curThread = rwThread();
      if (curThread.isValid()) {
         // Register with the current thread object
         curThread.registerThreadLocalDestructor(RWThreadLocal<Type>::destroy,(void*)valptr);
      }
      else {
         // Register with the current thread-local object
         RW_THR_CONST_CAST(RWThreadLocal<Type>*,this)->registerObject(valptr);
      }
   }

#  endif

   return valptr;
}

template<class Type>
void
RWThreadLocal<Type>::init(void)
{
   RWTHRTRACEMF(RWThreadLocal<Type>,init(void):void);

   LockGuard lock(monitor());
   if (!isInitialized_) {
      keyCreated_ = FALSE;
#  if defined(RW_THR_THREAD_API_OS2)
      dict_ = rwnil;
#  else
      key_ = 0;
#  endif  
      isInitialized_ = TRUE;
   }
}


#  if defined(RW_THR_THREAD_API_WIN32)
template <class Type>
void
RWThreadLocal<Type>::registerObject(Type* ptr)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadLocal<Type>,registerObject(Type*):void);
   objectsToDelete_.append(ptr);
}
#  endif

template <class Type>
void
RWThreadLocal<Type>::setValue(const Type& value)
   RWTHRTHROWSANY
   // RWTHRResourceLimit - Insufficient memory or system resources
   // RWTHRInternalError
{
   RWTHRTRACEMF(RWThreadLocal<Type>,setValue(const Type&):Type*);
   static const char msgHeader[] = "RWThreadLocal<Type>::setValue - ";

   // Is this the first access?
   checkKey();

   Type* valptr;

#  if defined(RW_THR_THREAD_API_OS2)

   RWThreadId  threadId = ::rwThreadId();

   // Lock the hash dictionary
   LockGuard lock(monitor());

   if(!dict_->findValue(threadId, valptr)) {
      
      // No instance for this thread yet...
      // Copy-construct an instance
      valptr = new Type(value);

      dict_->insertKeyAndValue(threadId,valptr);

      RWThreadSelf curThread = ::rwThread();
      if (curThread.isValid()) {
         // Register this thread-local object with the current thread object 
         // so the thread-local Type-instance can be destroyed when the thread 
         // exits or is terminated...
         curThread.registerThreadLocalDestructor(RWThreadLocal<Type>::destroy,(void*)this,threadId);
      }
   }
   else{
      // Assignment
      *valptr = value;
   }

#  elif defined(RW_THR_THREAD_API_POSIX)
#     if defined(RW_THR_THREAD_API_DCE)
   if (-1 == ::pthread_getspecific(key_,(void **)&valptr)) {
      switch(errno) {
         case EINVAL:
            RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
         default:
            RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
            { throw RWTHRInternalError(RWCString(msgHeader)+::rwErrnoMsg()); } 
      }
   }
   if (rwnil == valptr) {
      // Instance has not yet been created...
      valptr = new Type(value);
      if (-1 == ::pthread_setspecific(key_,(void *)valptr)) {
         delete valptr; // Recover storage
         switch(errno) {
            case EINVAL:
               RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
            default:
               RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
               { throw RWTHRInternalError(RWCString(msgHeader)+::rwErrnoMsg()); }
         }
      }
   }
   else {
      // Assignment
      *valptr = value;
   }
#     else
   // Draft 7,9,10/Final
   if (rwnil == (valptr = (Type *)::pthread_getspecific(key_))) {
      // Instance has not yet been created...
      valptr = new Type(value);
      int rv;
      if (0 != (rv = ::pthread_setspecific(key_,(void *)valptr))) {
         delete valptr; // Recover storage
         switch(rv) {
            case ENOMEM:
               // Not enough memory 
               throw RWTHRResourceLimit(RWCString(msgHeader)+RW_THR_No_Memory);
            case EINVAL:
               RWTHRASSERT(0); // INTERNAL ERROR - Invalid key!
            default:
               RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
               throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
         }
      }
   }
   else {
      // Assignment
      *valptr = value;
   }   
#     endif


#  elif defined(RW_THR_THREAD_API_SOLARIS)
   int rv;
   if (0 != (rv = ::thr_getspecific(key_,(void **)&valptr))) {
      switch(rv) {
         case EINVAL:
            RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
         default:
            RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
            throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
      }
   }
   if (valptr == rwnil) {
      // Instance has not yet been created...
      valptr = new Type(value);
      if (0 != (rv = ::thr_setspecific(key_,(void *)valptr))) {
         delete valptr; // Recover storage!
         switch(rv) {
            case EINVAL:
               RWTHRASSERT(0); // INTERNAL ERROR - Invalid argument!
            default:
               RWTHRASSERT(0); // INTERNAL ERROR - Unexpected or unrecognized error!
               throw RWTHRInternalError(RWCString(msgHeader)+::rwErrorMsg(rv)); 
         }
      }
   }
   else {
      // Assignment
      *valptr = value;
   }   
#  elif defined(RW_THR_THREAD_API_WIN32)
   if (rwnil == (valptr = (Type*)::TlsGetValue(key_))) {
      // Instance has not yet been created...
      valptr = new Type(value);
      if (!::TlsSetValue(key_,(void *)valptr)) {
         delete valptr;
         RWTHRASSERT(0); // INTERNAL ERROR - Unexpected error!
         throw RWTHRInternalError(RWCString(msgHeader)+::rwGetLastErrorMsg());
      }
      // Win32 provides no mechanism for deleting the thread-local
      // object when the creating thread exits.

      // We will either register this object with the current thread object,
      // if any, or register the object with the thread-local object.

      // The thread object (if one exists) will delete any thread-local objects
      // allocated just prior to thread exit.

      // The thread-local object will delete any objects allocated by
      // threads that were not spawned from thread class objects, when the
      // thread-local object goes out of scope and is destructed.

      RWThreadSelf curThread = rwThread();
      if (curThread.isValid()) {
         // Register with the current thread object
         curThread.registerThreadLocalDestructor(RWThreadLocal<Type>::destroy,(void*)valptr);
      }
      else {
         // Register with the current thread-local object
         registerObject(valptr);
      }
   }
   else {
      // Assignment
      *valptr = value;
   }
#  endif
}

#endif // __RWTHRTHRLOCAL_CC__


