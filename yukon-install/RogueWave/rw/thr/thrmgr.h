#if !defined(__RWTHRTHRMGR_H__)
#  define __RWTHRTHRMGR_H__
/*****************************************************************************
 *
 * thrmgr.h
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

thrmgr.h - Declarations for:

   rwThreadManager - Returns a pointer to singleton RWThreadManagerImp.  A 
                     call to this function may result in the construction of 
                     the thread manager if it has not yet been created.
   rwEnableShutdown - Enables the safe automatic shutdown of running threads 
                      in the advent of an process exit or terminate request 
                      by a thread. An attempt to shutdown remaining thread 
                      objects will occur regardless (the library will always 
                      attempt an orderly shutdown during global static 
                      destruction), but calling rwEnableShutdown() after 
                      main() has been entered allows the library to attempt 
                      a shutdown before global static destruction begins. 
                      This early shutdown is preferred as it allow threads to 
                      exit before global objects they may require are destroyed.
   RWThreadManager - Counted pointer handle for RWThreadManagerImp instance.
   RWThreadManagerImp - Maintains dictionary of current thread object and 
                        attempts orderly shutdown of threads at process exit.
               
See Also:

   thrmgr.cpp - Out-of-line function definitions.

******************************************************************************/


#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

#  if !defined(__RWTHRRWLOCK_H__)
#     include <rw/thr/rwlock.h>
#  endif

#  if !defined(__RWTHRCONDTION_H__)
#     include <rw/thr/condtion.h>
#  endif

#  if !defined(__RWTHRTHREAD_H__)
#     include <rw/thr/thread.h>
#  endif

#  if !defined(__RWTVHDICT_H__)
#     include <rw/tvhdict.h>
#  endif

#define RW_THR_DEFAULT_CANCEL_TIME_OUT 5000 /* Milliseconds */

class RWTHRExport RWThreadManagerImp;

class RWTHRExport RWThreadManager :
   public RWTHRHandle {
   
   RW_THR_DECLARE_TRACEABLE
   
   // Give RWThreadImp access to protected handle members
   friend class RWThreadImp;

   public:
      
      // Construct an empty, invalid handle instance   
      RWThreadManager(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWThreadManager(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to the thread manager instance, if any,
      // pointed-to by another handle instance
      RWThreadManager(const RWThreadManager& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the thread manager instance, if any,
      // pointed-to by another handle instance
      RWThreadManager&
      operator=(const RWThreadManager& second)
         RWTHRTHROWSANY;

      // Get a reference to the thread manager instance, if any, 
      // pointed-to by this handle instance
      RWThreadManagerImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

      // Delegated functions...

      void
      enableEarlyShutdown(void) 
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      unsigned long
      getCancelTimeOut(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWThreadSelf
      getCurrentThread(void)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

#  if defined(RW_THR_THREAD_API_SOLARIS)
      // Get the signal Threads.h++ uses for termination
      int
      getTerminationSignal(void)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);
#  endif

      // Create and/or get the current singleton thread manager instance
      static
      RWThreadManager
      instance(void)
         RWTHRTHROWSANY;

      void
      setCancelTimeOut(unsigned long milliseconds)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

#  if defined(RW_THR_THREAD_API_SOLARIS)
      // Change the signal Threads.h++ uses for termination
      void
      setTerminationSignal(int termSignal)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);
#  endif

      void
      shutdown(void)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

   protected:

      // Bind a new handle instance to a thread manager instance 
      RWThreadManager(RWThreadManagerImp* imp)
         RWTHRTHROWSANY;

      // Deregister thread runnable instance (called by RWThreadImp)
      void
      deregisterThread(const RWThreadId& threadID)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Register thread runnable instance (called by RWThreadImp)
      void
      registerThread(const RWThreadId& threadID,RWThreadImp* imp)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

   protected:
      
      static RWMutexLock         mutex_;
      static RWThreadManager     instance_;

};

#  if defined(RW_THR_SUPERCLASSES_MUST_BE_EXPORTED)
// Microsoft can't doesn't support explicit exporting of
// templates class and produces warnings for inheriting 
// from the non-exported RWCountingBody<> class.
// This pragma turns these warnings off...
#    pragma warning( disable : 4275 ) 
#  endif

#  if defined(RW_THR_COMPILER_MICROSOFT_CPP)
// The STDLIB version of Tools.h++ causes the Microsoft 
// compiler to complain mightily about the length of the
// symbols created by the RWTValHashDictionary declaration
// in the following class.  This pragma is used to temporarily
// disable some of these warnings.
#    pragma warning( disable : 4786 ) 
#  endif

class RWTHRExport RWThreadManagerImp :
   public RWTHRBody {
   
   RW_THR_DECLARE_TRACEABLE

   friend class RWThreadManager;

   protected:

      unsigned long              cancelTimeOut_;
      RWBoolean                  isEarlyShutdownEnabled_;
      RWBoolean                  shuttingDown_;
      RWReadersWriterLock        threadsLock_;

      RWTValHashDictionary<RWThreadId,RWThreadImp* RWDefHArgs(RWThreadId)> threads_;

#  if defined(RW_THR_HAS_SET_TERMINATE_PER_PROCESS)
      void (*prevTerminate_)(void);
#  endif

#  if defined(RW_THR_THREAD_API_SOLARIS)
      int                        termSignal_;
      struct sigaction           oldSigAction_;
#  endif      
   
   protected:

      // Protect constructor so that only instance can get to it
      RWThreadManagerImp(void)
         RWTHRTHROWSANY;

      ~RWThreadManagerImp(void)
         RWTHRTHROWSANY;

      void
      deregisterThread(const RWThreadId& threadID)
         RWTHRTHROWS1(RWTHRInternalError);

      void
      enableEarlyShutdown(void) 
         RWTHRTHROWS1(RWTHRInternalError);

      unsigned long
      getCancelTimeOut(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      RWThreadSelf
      getCurrentThread(void)
         RWTHRTHROWS1(RWTHRInternalError);

#  if defined(RW_THR_THREAD_API_SOLARIS)
      // Get the signal Threads.h++ uses for termination
      int
      getTerminationSignal(void)
         RWTHRTHROWS1(RWTHRInternalError);
#  endif

      // 'atexit' handler that attempts thread shutdown
      static
      void
      onExit(void)
         RWTHRTHROWSANY;

#  if defined(RW_THR_THREAD_API_SOLARIS)
      // Signal handler for thread termination under Solaris
      static
      void
#  if (RW_THR_OS_VERSION_SOLARIS < 0x0205)
      onSignal(void)
#  else
      onSignal(int)
#endif
         RWTHRTHROWSANY;
#  endif

#  if defined(RW_THR_HAS_SET_TERMINATE_PER_PROCESS)
      // Terminate() handler that attempts shutdown
      static
      void
      onTerminate(void)
         RWTHRTHROWSANY;
#  endif

      void
      registerThread(const RWThreadId& threadID,RWThreadImp* imp)
         RWTHRTHROWS1(RWTHRInternalError);

      void
      setCancelTimeOut(unsigned long milliseconds)
         RWTHRTHROWS1(RWTHRInternalError);

#  if defined(RW_THR_THREAD_API_SOLARIS)
      // Change the signal Threads.h++ uses for termination
      void
      setTerminationSignal(int termSignal)
         RWTHRTHROWS1(RWTHRInternalError);
#  endif

      void
      shutdown(void)
         RWTHRTHROWS1(RWTHRInternalError);

   private:

};

#  if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file... 
// (Must be changed if stdlib support is added!)
#    if defined(RW_NO_STL)
template class RWTHRIExport RWTValHashDictionary<RWThreadId,RWThreadImp*>;
template class RWTHRIExport RWTValAssocLink<RWThreadId,RWThreadImp*>;
template class RWTHRIExport RWTIsvSlistIterator<RWTValAssocLink<RWThreadId,RWThreadImp*> >;
#    endif
#  endif

#  if defined(RW_THR_SUPERCLASSES_MUST_BE_EXPORTED)
#     pragma warning( default : 4275 ) 
#  endif

#  if defined(RW_THR_COMPILER_MICROSOFT_CPP)
#    pragma warning( default : 4786 ) 
#  endif

/*****************************************************************************/

// 
//    Declare global thread functions that access RWThreadManagerImp
//

extern rwthrexport
RWThreadManager
rwThreadManager(void)
   RWTHRTHROWSNONE;

/*****************************************************************************/

// Deregister thread runnable instance (called by RWThreadImp)
inline
void
RWThreadManager::deregisterThread(const RWThreadId& threadId)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadManager,deregisterThread(const RWThreadId&):void);
   body().deregisterThread(threadId);
}

inline
void
RWThreadManager::enableEarlyShutdown(void) 
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadManager,enableEarlyShutdown(void):void);
   body().enableEarlyShutdown();
}

inline
unsigned long
RWThreadManager::getCancelTimeOut(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadManager,getCancelTimeOut(void):unsigned long);
   return body().getCancelTimeOut();
}

#  if defined(RW_THR_THREAD_API_SOLARIS)
// Get the signal Threads.h++ uses for termination
inline
int
RWThreadManager::getTerminationSignal(void)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadManager,getTerminationSignal(void):int);
   return body().getTerminationSignal();
}
#  endif

// Register thread runnable instance (called by RWThreadImp)
inline
void
RWThreadManager::registerThread(const RWThreadId& threadId,RWThreadImp* imp)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadManager,registerThread(const RWThreadId&,RWThreadImp*):void);
   body().registerThread(threadId,imp);
}

inline
void
RWThreadManager::setCancelTimeOut(unsigned long milliseconds)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadManager,setCancelTimeOut(unsigned long):void);
   body().setCancelTimeOut(milliseconds);
}

#  if defined(RW_THR_THREAD_API_SOLARIS)
// Change the signal Threads.h++ uses for termination
inline
void
RWThreadManager::setTerminationSignal(int termSignal)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadManager,setTerminationSignal(int):void);
   body().setTerminationSignal(termSignal);
}
#  endif

inline
void
RWThreadManager::shutdown(void) 
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadManager,shutdown(void):void);
   body().shutdown();
}

#endif /// __RWTHRTHRMGR_H__
