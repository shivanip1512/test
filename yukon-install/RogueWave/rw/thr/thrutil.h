#if !defined(__RWTHRTHRUTIL_H__)
#  define __RWTHRTHRUTIL_H__
/*****************************************************************************
 *
 * thrutil.h
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

thrutil.h - Declarations for:

   rwSleep - puts calling thread to sleep for specified time period.
   rwYield - causes calling thread to yield execution to another thread.
   rwThread - Returns a pointer to the thread object associated with
              the current thread (if any).  The thread handle returned
              should be tested for validity before using if the code
              calling the function does not know if it is running in
              a thread started by one of the library's thread objects.


******************************************************************************/

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RWTHRMGR_H__)
#     include <rw/thr/thrmgr.h>
#  endif

inline
void
rwSleep(unsigned long milliseconds)
   RWTHRTHROWSNONE;

inline
void
rwYield(void)
   RWTHRTHROWSNONE;

extern rwthrexport
RWThreadSelf
rwThread(void)
   RWTHRTHROWSNONE;

/*****************************************************************************/

#  if defined(RW_THR_THREAD_API_OS2)
#     ifndef __RW_OS2_H__
#        define INCL_BASE
#        define __RW_OS2_H__ <os2.h>
#        include __RW_OS2_H__
#     endif
#  endif

#  if defined(RW_THR_THREAD_API_POSIX) && \
      !defined(RW_THR_THREAD_API_DCE_TRANSARC)
#     if defined(RW_THR_OS_AIX)
#        ifndef __RW_SYS_SCHED_H__
#           define __RW_SYS_SCHED_H__ <sys/sched.h>
#           include __RW_SYS_SCHED_H__
#        endif
#     else
#        ifndef __RW_SCHED_H__
#           define __RW_SCHED_H__ <sched.h>
#           include __RW_SCHED_H__
#        endif
#     endif
#  endif

#  if defined(RW_THR_THREAD_API_POSIX) || \
         defined(RW_THR_THREAD_API_SOLARIS)
#     if !defined(__RWTHRCONDTION_H__)
#        include <rw/thr/condtion.h>
#     endif
#     if !defined(__RWTHRMUTEX_H__)
#        include <rw/thr/mutex.h>
#     endif
#  endif

#  if defined(RW_THR_THREAD_API_SOLARIS)
#    if !defined(__RW_THREAD_H__)
#      define __RW_THREAD_H__ <thread.h>
#      include __RW_THREAD_H__
#    endif
#  endif

inline
void
rwSleep(unsigned long milliseconds)
   RWTHRTHROWSNONE
{

#  if defined(RW_THR_THREAD_API_OS2)
   ::DosSleep(milliseconds);
#  elif defined(RW_THR_THREAD_API_POSIX) || \
        defined(RW_THR_THREAD_API_SOLARIS)

   try {
      RWMutexLock    dummyMutex;
      RWCondition    dummyCondition(dummyMutex);
      RWCondition::LockGuard guard(dummyCondition);
      dummyCondition.wait(milliseconds);
   }
   catch(...) {
   }

#  elif defined(RW_THR_THREAD_API_WIN32)
   ::Sleep(milliseconds);
#  endif

}

inline
void
rwYield(void)
   RWTHRTHROWSNONE
{

#  if defined(RW_THR_THREAD_API_OS2)
   ::DosSleep(0);

#  elif defined(RW_THR_THREAD_API_POSIX)

#     if defined(RW_THR_THREAD_API_DCE) || \
         defined(RW_THR_THREAD_API_POSIX_1003_1C_D7_AIX)
   ::pthread_yield();

#     elif defined(RW_THR_THREAD_API_POSIX_1003_1C_IRIX) || \
           defined(RW_THR_THREAD_API_POSIX_1003_1C_OSF1) || \
           defined(RW_THR_THREAD_API_POSIX_1003_1C_AIX)
   ::sched_yield();

#     else
   // Default for other platforms
   ::sched_yield();

#     endif

#  elif defined(RW_THR_THREAD_API_SOLARIS)
   ::thr_yield();

#  elif defined(RW_THR_THREAD_API_WIN32)
   ::Sleep(0);

#  endif

}

#endif // __RWTHRTHRUTIL_H__

