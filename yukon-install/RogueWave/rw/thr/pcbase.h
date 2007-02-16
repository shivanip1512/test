#if !defined(__RWTHRPCBASE_H__)
#  define __RWTHRPCBASE_H__
/*****************************************************************************
 *
 * pcbase.h
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

pcbase.h - Class declarations for:

   RWPCBufferBase - Base class for producer-consumer buffer classes

See Also:

   pcbase.cpp - Out-of-line function definitions.

******************************************************************************/


#  if !defined(__RWTHRMONITOR_H__)
#     include <rw/thr/monitor.h>
#  endif

#  if !defined(__RWTHRMUTEX_H__)
#     include <rw/thr/mutex.h>
#  endif

#  if !defined(__RWCONDTION_H__)
#     include <rw/thr/condtion.h>
#  endif

#  if !defined(__RWFUNC0_H__)
#     include <rw/thr/func0.h>
#  endif

#  if defined(RW_THR_SUPERCLASSES_MUST_BE_EXPORTED)
// Microsoft doesn't support explicit exporting of
// templates classes and produces warnings for inheriting
// from the non-exported RWCountingBody<> class.
// This pragma turns these warnings off...
#     pragma warning( disable : 4275 )
#  endif

class RWTHRExport RWTHRClosedException :
   public RWTHRxmsg {
 
   RW_THR_DECLARE_TRACEABLE
   RW_THR_DECLARE_EXCEPTION(RWTHRClosedException)
 
   // Member Functions
   public:
      RWTHRClosedException(const RWCString& reason="")
         RWTHRTHROWSNONE;
};

class RWTHRExport RWPCBufferBase :
   public RWMonitor<RWMutexLock> {

   RW_THR_DECLARE_TRACEABLE

   // Member Variables
   protected:

      size_t         maxEntries_;
      size_t         waitingReaders_;
      size_t         waitingWriters_;

      RWCondition    notEmpty_;
      RWCondition    notFull_;

      RWBoolean      hasInvokedEmptyCallback_;
      RWFunctor0     onEmptyCallback_;

      RWBoolean      hasInvokedFullCallback_;
      RWFunctor0     onFullCallback_;

      RWBoolean      isOpen_;
      RWFunctor0     onOpenCallback_;
      RWFunctor0     onCloseCallback_;

   // Member Functions
   public:

      size_t 
      setCapacity(size_t maxCapacity) 
         RWTHRTHROWSANY;

      size_t 
      getCapacity() const 
         RWTHRTHROWSANY;

      void 
      open(void)
         RWTHRTHROWSANY;

      RWBoolean
      isOpen(void) const
         RWTHRTHROWSANY;

      void 
      close(void)
         RWTHRTHROWSANY;

      void
      flush(void)
         RWTHRTHROWSANY;

      RWBoolean 
      canRead(void) const
         RWTHRTHROWSANY;

      RWBoolean 
      canWrite(void) const
         RWTHRTHROWSANY;

      size_t 
      entries(void) const
         RWTHRTHROWSANY;

      RWFunctor0
      getEmptyCallback(void) const
         RWTHRTHROWSANY;

      void
      setEmptyCallback(const RWFunctor0& onEmptyCallback)
         RWTHRTHROWSANY;

      RWFunctor0
      getFullCallback(void) const
         RWTHRTHROWSANY;

      void
      setFullCallback(const RWFunctor0& onFullCallback)
         RWTHRTHROWSANY;

      RWFunctor0
      getOpenCallback(void) const
         RWTHRTHROWSANY;

      void
      setOpenCallback(const RWFunctor0& onOpenCallback)
         RWTHRTHROWSANY;

      RWFunctor0
      getCloseCallback(void) const
         RWTHRTHROWSANY;

      void
      setCloseCallback(const RWFunctor0& onCloseCallback)
         RWTHRTHROWSANY;

      virtual
      ~RWPCBufferBase()
         RWTHRTHROWSNONE;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      RWPCBufferBase(size_t maxCapacity=0, 
                     RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

      virtual
      void
      _flush(void)
         RWTHRTHROWSANY = 0;

      virtual
      size_t 
      _entries(void) const
         RWTHRTHROWSANY = 0;

      virtual
      RWBoolean
      _canRead(void) const
         RWTHRTHROWSANY;

      virtual
      RWBoolean 
      _canWrite(void) const
         RWTHRTHROWSANY;

};

#  if defined(RW_THR_SUPERCLASSES_MUST_BE_EXPORTED)
#     pragma warning( default : 4275 )
#  endif

/*****************************************************************************/

inline
RWTHRClosedException::RWTHRClosedException(const RWCString& reason)
   RWTHRTHROWSNONE
   :
      RWTHRxmsg(reason)
{
   RWTHRTRACEMF(RWTHRClosedException,RWTHRClosedException(const RWCString&));
}

#endif // __RWTHRPCBASE_H__


