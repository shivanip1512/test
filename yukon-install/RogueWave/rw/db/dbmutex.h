#ifndef __RWDBMUTEX_H__
#define __RWDBMUTEX_H__

/**************************************************************************
 *
 * $Id$
 *
 ***************************************************************************
 *
 * Copyright (c) 1994-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 ***************************************************************************
 *
 * This class is a portable implementation of a simple mutex lock
 * to be used for synchronizing multiple threads within a single process.
 * It is not suitable for use among threads of different processes.
 *
 **************************************************************************/

#include <rw/db/defs.h>

#include <rw/mutex.h>
#include <rw/rstream.h>

#ifdef RW_MULTI_THREAD

#   ifdef RW_POSIX_D10_THREADS
#   include <pthread.h>
#      define RWDBTHREADSELF     pthread_self
       typedef pthread_t          RWDBThreadType;
#      define RWDBTHREADCOMPARE(A, B)  pthread_equal(A, B)

#   elif defined(RW_DCE_THREADS)
#     if defined(RW_NO_DCE_PTHREAD_H) || defined (__WIN32__)
#       include <pthread.h>
#     else
#       include <dce/pthread.h>
#     endif
#      define RWDBTHREADSELF     pthread_self
       typedef pthread_t          RWDBThreadType;
#      define RWDBTHREADCOMPARE(A, B)  pthread_equal(A, B)

#   elif defined(RW_SOLARIS_THREADS)
#      include <thread.h>
#      define RWDBTHREADSELF     thr_self
       typedef thread_t           RWDBThreadType;
#      define RWDBTHREADCOMPARE(A, B)  A == B

#   elif defined(__WIN32__)
#      include <winbase.h>
#      define RWDBTHREADSELF     GetCurrentThread
       typedef DWORD              RWDBThreadType;
#      define RWDBTHREADCOMPARE(A, B)  A == B

#   elif defined(__OS2__)
        unsigned long rwGetOS2Thread();
#      define RWDBTHREADSELF      rwGetOS2Thread
        typedef unsigned long       RWDBThreadType;
#      define RWDBTHREADCOMPARE(A, B)  A==B
#   endif /* POSIX_THREADS */
#endif /* RW_MULTI_THREAD */

enum RWDBStaticCtor {rwdbStaticCtor=0};

#ifdef RW_MULTI_THREAD
#   ifdef __WIN16__
#      error Error, Windows can not be Multithreaded.
#   endif

#   if defined(__WIN32__) && !defined(RW_DCE_THREADS)
#      include <windows.h>

               class RWDBExport RWDBMutex {
                 public:
                     RWDBMutex();
                     RWDBMutex(RWDBStaticCtor);
                     ~RWDBMutex();
                     void init();
                     void acquire();
                     void release();
                 protected:

//               HANDLE    mutexLock_;
                        CRITICAL_SECTION mutexLock_;
                     int       initFlag_;
           };
#   else  // UNIX multi threaded
        class RWDBExport RWDBMutex : public RWMutex {
                 public:
                      RWDBMutex();
                      RWDBMutex(RWDBStaticCtor);
                      ~RWDBMutex();

                      void init();
                      void acquire();
                      void release();

                  protected:
                         RWMutex mutexLock_;

                 private:
                         RWDBThreadType  threadID_;
                         int             count_;
                };
#   endif // Unix multi-threaded
#else  // Not multi threaded
        class RWDBExport RWDBMutex {
                 public:
                      RWDBMutex() {;}
                      RWDBMutex(RWDBStaticCtor) {;}
                      ~RWDBMutex() {;}

                      void init() {;}
                      void acquire() {;}
                      void release() {;}
                };
#endif


/*
 *
 * This class can be used to lock a mutex, or lock a class
 * that has acquire and release defined.
 *
 */

#ifdef RW_MULTI_THREAD

extern int deadLockIndent__;

extern void indent__();
extern void deindent__();

template<class T>
class RWDBGuard {
    T&   guard_;
  public:



   RWDBGuard(const T& t, const char *) : guard_((T&)t) 
      { guard_.acquire();

      }
    ~RWDBGuard()
        { guard_.release();

    }
};

//This guard is for objects that hold an implementation.
template<class T>
class RWDBGuardRef {
    T   guardImp_;
  public:


   RWDBGuardRef(const T& t, const char *) : guardImp_(t)
      { guardImp_.acquire();
      }
    ~RWDBGuardRef()
     { guardImp_.release();
    }
};

#define RWDBGUARD(C, O, S)          RWDBGuard<C>  dummyguard(O, S)
#define RWDBGUARD2(C, O)            RWDBGUARD(C, O, "")

#define RWDBGUARD_2(C, O, S)       RWDBGuard<C>  dummyguard2(O, S)
#define RWDBGUARD2_2(C, O)         RWDBGUARD_2(C, O, "")

#define RWDBGUARDREF(C, O, S)     RWDBGuardRef<C>  dummyguard(O, S)
#define RWDBGUARD2REF(C, O)       RWDBGUARDREF(C, O, "")

#define RWDBGUARDREF_2(C, O, S)     RWDBGuardRef<C>  dummyguard2(O, S)
#define RWDBGUARD2REF_2(C, O)       RWDBGUARDREF_2(C, O, "")

#else
#define RWDBGUARD(C, O, S)
#define RWDBGUARD2(C, O)
#define RWDBGUARDREF(C, O, S)    
#define RWDBGUARD2REF(C, O)      
#define RWDBGUARDREF_2(C, O, S)     
#define RWDBGUARD_2(C, O, S)      
#define RWDBGUARD2_2(C, O)       
#define RWDBGUARD2REF_2(C, O)      

#endif

#endif  /* __RWDBMUTEX_H__ */







