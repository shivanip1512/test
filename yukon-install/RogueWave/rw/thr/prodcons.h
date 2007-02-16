#if !defined(__RWTHRPRODCONS_H__)
#  define __RWTHRPRODCONS_H__
/*****************************************************************************
 *
 * prodcons.h
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

prodcons.h - Class declarations for:

   RWPCValBufferBase 
      Templatized base class for buffers with producer-consumer 
      synchronization semantics whose entries are stored by-value.
   RWPCValQueue
      Templatized class for queues with producer-consumer 
      synchronization semantics whose entries are stored by-value.
   RWPCValStack 
      Templatized class for stacks with producer-consumer 
      synchronization semantics whose entries are stored by-value.
   RWPCValBufferBaseDecorated
      Templatized base class for buffers whose entries are stored
      by-value and decorated with other data, such as guard objects 
      or priority values.
   RWPCValBufferBaseGuarded
      Templatized base class for buffers with guarded, producer-consumer 
      synchronization semantics whose entries are stored by-value.
   RWPCValQueueGuarded
      Templatized class for queues with producer-consumer 
      guarded synchronization semantics whose entries are stored by-value.
   RWPCValStackGuarded
      Templatized class for stacks with producer-consumer 
      guarded synchronization semantics whose entries are stored by-value.
   RWPCValBufferBasePrioritized
      Templatized base class for buffers with producer-consumer 
      synchronization semantics whose entries are stored by-value and
      ordered according to a priority value.
   RWPCValQueuePrioritized
      Templatized class for queues with producer-consumer 
      synchronization semantics whose entries are stored by-value and
      ordered according to a priority value.
   RWPCValStackPrioritized
      Templatized class for stacks with producer-consumer 
      synchronization semantics whose entries are stored by-value and
      ordered according to a priority value.
   RWPCValBufferBaseGuardedPrioritized
      Templatized base class for buffers with guarded, producer-consumer 
      synchronization semantics whose entries are stored by-value and
      ordered according to a priority value.
   RWPCValQueueGuardedPrioritized
      Templatized class for queues with guarded, producer-consumer 
      synchronization semantics whose entries are stored by-value and
      ordered according to a priority value.
   RWPCValStackGuardedPrioritized
      Templatized class for stacks with guarded, producer-consumer 
      synchronization semantics whose entries are stored by-value and
      ordered according to a priority value.

   RWPCPtrBufferBase
      Templatized base class for buffers with producer-consumer 
      synchronization semantics whose entries are stored by-address.
   RWPCPtrQueue
      Templatized class for queues with producer-consumer 
      synchronization semantics whose entries are stored by-address.
   RWPCPtrStack
      Templatized class for stacks with producer-consumer 
      synchronization semantics whose entries are stored by-address.

See Also:

   prodcons.cc - Out-of-line function definitions.

******************************************************************************/


#  if !defined(__RWTHRPCBASE_H__)
#     include <rw/thr/pcbase.h>
#  endif

#  if !defined(__RWTVSLIST_H__)
#     include <rw/tvslist.h>
#  endif

#  if !defined(__RWTPSLIST_H__)
#     include <rw/tpslist.h>
#  endif

#  if !defined(__RWTHRFUNCR0_H__)
#     include <rw/thr/funcr0.h>
#  endif

#if defined(RW_THR_COMPILER_BORLAND_CPP)
// Export or import templates instantiated by this header file...
template class RWTHRIExport RWFunctorR0<RWBoolean>;
#endif

template <class Type>
class RWTHRTExport RWDecorator {

   RW_THR_DECLARE_TRACEABLE

   public:

      Type value_;

      RWDecorator(void);

      RWDecorator(const Type& value);

      RWBoolean
      operator==(const RWDecorator<Type>& second) const
         RWTHRTHROWSANY;

      RWBoolean
      operator<(const RWDecorator<Type>& second) const
         RWTHRTHROWSANY;
};

template <class Type>
class RWTHRTExport RWGuardDecorator : virtual public RWDecorator<Type> {

   RW_THR_DECLARE_TRACEABLE

   public:

      RWFunctorR0<RWBoolean>  guard_;

      RWGuardDecorator(void);

      RWGuardDecorator(const Type& value);

      RWGuardDecorator(const Type& value,
                       const RWFunctorR0<RWBoolean>& guard);
};

template <class Type>
class RWTHRTExport RWPriorityDecorator : virtual public RWDecorator<Type> {

   RW_THR_DECLARE_TRACEABLE

   public:

      long priority_;

      RWPriorityDecorator(void);

      RWPriorityDecorator(const Type& value);

      RWPriorityDecorator(const Type& value,
                          long priority);
};

template <class Type>
class RWTHRTExport RWGuardAndPriorityDecorator : public RWGuardDecorator<Type>, public RWPriorityDecorator<Type> {

   RW_THR_DECLARE_TRACEABLE

   public:

      RWGuardAndPriorityDecorator(void);

      RWGuardAndPriorityDecorator(const Type& value);

      RWGuardAndPriorityDecorator(const Type& value,
                                  const RWFunctorR0<RWBoolean>& guard);

      RWGuardAndPriorityDecorator(const Type& value,
                                  long priority);

      RWGuardAndPriorityDecorator(const Type& value,
                                  const RWFunctorR0<RWBoolean>& guard,
                                  long priority);
};


template <class Type>
class RWTHRTExport RWPCValBufferBase : 
   public RWPCBufferBase {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef Type     DataType;
   
   protected:

      RWTValSlist<Type>          buffer_;
   
   public:
      
      Type 
      read(void)
         RWTHRTHROWSANY;

      RWBoolean 
      tryRead(Type& result)
         RWTHRTHROWSANY;

      RWWaitStatus
      read(Type& result,unsigned long milliseconds)
         RWTHRTHROWSANY;

      Type 
      peek(void)
         RWTHRTHROWSANY;

      RWBoolean 
      tryPeek(Type& result)
         RWTHRTHROWSANY;

      RWWaitStatus
      peek(Type& result,unsigned long milliseconds)
         RWTHRTHROWSANY;

      void 
      write(const Type& value)
         RWTHRTHROWSANY;

      RWBoolean 
      tryWrite(const Type& value)
         RWTHRTHROWSANY;

      RWWaitStatus 
      write(const Type& value,unsigned long milliseconds)
         RWTHRTHROWSANY;

      virtual
      ~RWPCValBufferBase()
         RWTHRTHROWSNONE;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      RWPCValBufferBase(size_t maxCapacity=0, 
                        RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

      // deprecated
      RWBoolean
      isWriteable(void) const
         RWTHRTHROWSANY;

      // deprecated
      RWBoolean
      isReadable(void) const
         RWTHRTHROWSANY;

      virtual
      void
      _flush(void)
         RWTHRTHROWSANY;

      virtual
      size_t 
      _entries(void) const
         RWTHRTHROWSANY;

      virtual
      Type 
      _read(void)
         RWTHRTHROWSANY;

      virtual
      Type 
      _peek(void)
         RWTHRTHROWSANY;

      virtual
      void
      _write(const Type& value)
         RWTHRTHROWSANY = 0;

};


template <class Type>
class RWTHRTExport RWPCValQueue : 
   public RWPCValBufferBase<Type> {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCValQueue(size_t maxCapacity=0,
                   RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(const Type& value)
         RWTHRTHROWSANY;

};


template <class Type>
class RWTHRTExport RWPCValStack : 
   public RWPCValBufferBase<Type> {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCValStack(size_t maxCapacity=0,
                   RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(const Type& value)
         RWTHRTHROWSANY;

};

template <class Type, class Decorator>
class RWTHRTExport RWPCValBufferBaseDecorated : 

#if defined(RW_THR_BROKEN_TEMPLATE_ACCESS_TO_BASE_CLASS)
   public RWPCValBufferBase<Decorator> {
#else 
   protected RWPCValBufferBase<Decorator> {
#endif
   
   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Type     DataType;

      // Adjust access to base class members that we want to expose

      RWUSING RWPCValBufferBase<Decorator>::setCapacity;
      RWUSING RWPCValBufferBase<Decorator>::getCapacity;
      RWUSING RWPCValBufferBase<Decorator>::open;
      RWUSING RWPCValBufferBase<Decorator>::isOpen;
      RWUSING RWPCValBufferBase<Decorator>::close;
      RWUSING RWPCValBufferBase<Decorator>::canRead;
      RWUSING RWPCValBufferBase<Decorator>::canWrite;
      RWUSING RWPCValBufferBase<Decorator>::entries;
      RWUSING RWPCValBufferBase<Decorator>::flush;
      RWUSING RWPCValBufferBase<Decorator>::getEmptyCallback;
      RWUSING RWPCValBufferBase<Decorator>::setEmptyCallback;
      RWUSING RWPCValBufferBase<Decorator>::getFullCallback;
      RWUSING RWPCValBufferBase<Decorator>::setFullCallback;
      RWUSING RWPCValBufferBase<Decorator>::getOpenCallback;
      RWUSING RWPCValBufferBase<Decorator>::setOpenCallback;
      RWUSING RWPCValBufferBase<Decorator>::getCloseCallback;
      RWUSING RWPCValBufferBase<Decorator>::setCloseCallback;
         
      Type 
      read(void)
         RWTHRTHROWSANY;

      RWBoolean 
      tryRead(Type& result)
         RWTHRTHROWSANY;

      RWWaitStatus
      read(Type& result,
           unsigned long milliseconds)
         RWTHRTHROWSANY;

      Type 
      peek(void)
         RWTHRTHROWSANY;

      RWBoolean 
      tryPeek(Type& result)
         RWTHRTHROWSANY;

      RWWaitStatus
      peek(Type& result,unsigned long milliseconds)
         RWTHRTHROWSANY;

      void 
      write(const Type& value)
         RWTHRTHROWSANY;

      RWBoolean 
      tryWrite(const Type& value)
         RWTHRTHROWSANY;

      RWWaitStatus 
      write(const Type& value,
            unsigned long milliseconds)
         RWTHRTHROWSANY;

      virtual
      ~RWPCValBufferBaseDecorated(void)
         RWTHRTHROWSNONE;

   protected:

      RWPCValBufferBaseDecorated(void)
         RWTHRTHROWSANY;

      RWPCValBufferBaseDecorated(size_t maxCapacity,
                                 RWBoolean isOpen)
         RWTHRTHROWSANY;

};

template <class Type, class GuardDecorator>
class RWTHRTExport RWPCValBufferBaseGuarded : 
   virtual public RWPCValBufferBaseDecorated<Type,GuardDecorator> {
   
   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Type     DataType;

   protected:
      
      RWTValSlistIterator<GuardDecorator>  iter_;

   public:

#     if defined(RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT)      
      // Import super-class methods that would be hidden by 
      // overloaded names defined in this class...
      RWUSING RWPCValBufferBaseDecorated<Type,GuardDecorator>::write;
      RWUSING RWPCValBufferBaseDecorated<Type,GuardDecorator>::tryWrite;
#     else
      // Wrap calls to super-class methods that would be hidden by
      // overloaded names defined in this class...
      void write(const Type& value) RWTHRTHROWSANY { RWPCValBufferBaseDecorated<Type,GuardDecorator>::write(value); }
      RWBoolean tryWrite(const Type& value) RWTHRTHROWSANY { return RWPCValBufferBaseDecorated<Type,GuardDecorator>::tryWrite(value); }
      RWWaitStatus write(const Type& value,unsigned long milliseconds) RWTHRTHROWSANY { return RWPCValBufferBaseDecorated<Type,GuardDecorator>::write(value,milliseconds); }
#     endif

      void 
      write(const Type& value,
            const RWFunctorR0<RWBoolean>& guard)
         RWTHRTHROWSANY;

      RWBoolean 
      tryWrite(const Type& value,
               const RWFunctorR0<RWBoolean>& guard)
         RWTHRTHROWSANY;

      RWWaitStatus 
      write(const Type& value,
            const RWFunctorR0<RWBoolean>& guard,
            unsigned long milliseconds)
         RWTHRTHROWSANY;

      virtual
      ~RWPCValBufferBaseGuarded()
         RWTHRTHROWSNONE;

   protected:

      RWPCValBufferBaseGuarded(void)
         RWTHRTHROWSANY;

      RWPCValBufferBaseGuarded(size_t maxCapacity,
                               RWBoolean isOpen)
         RWTHRTHROWSANY;

      virtual
      RWBoolean
      _canRead(void) const
         RWTHRTHROWSANY;

      virtual
      GuardDecorator 
      _read(void)
         RWTHRTHROWSANY;

      virtual
      GuardDecorator
      _peek(void)
         RWTHRTHROWSANY;

};

template <class Type>
class RWTHRTExport RWPCValQueueGuarded : 
   public RWPCValBufferBaseGuarded<Type,RWGuardDecorator<Type> > {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCValQueueGuarded(size_t maxCapacity1=0,  // Arg names changed to keep aCC 3.10 happy!
                          RWBoolean isOpen1=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(const RWGuardDecorator<Type>& value)
         RWTHRTHROWSANY;

};


template <class Type>
class RWTHRTExport RWPCValStackGuarded : 
   public RWPCValBufferBaseGuarded<Type,RWGuardDecorator<Type> > {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCValStackGuarded(size_t maxCapacity=0,
                          RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(const RWGuardDecorator<Type>& value)
         RWTHRTHROWSANY;

};

template <class Type, class PriorityDecorator>
class RWTHRTExport RWPCValBufferBasePrioritized : 
   virtual public RWPCValBufferBaseDecorated<Type,PriorityDecorator> {
   
   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Type     DataType;

#     if defined(RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT)      
      // Import super-class methods that would be hidden by 
      // overloaded names defined in this class...
      RWUSING RWPCValBufferBaseDecorated<Type,PriorityDecorator>::write;
      RWUSING RWPCValBufferBaseDecorated<Type,PriorityDecorator>::tryWrite;
#     else
      // Wrap calls to super-class methods that would be hidden by
      // overloaded names defined in this class...
      void write(const Type& value) RWTHRTHROWSANY { RWPCValBufferBaseDecorated<Type,PriorityDecorator>::write(value); }
      RWBoolean tryWrite(const Type& value) RWTHRTHROWSANY { return RWPCValBufferBaseDecorated<Type,PriorityDecorator>::tryWrite(value); }
      RWWaitStatus write(const Type& value,unsigned long milliseconds) RWTHRTHROWSANY { return RWPCValBufferBaseDecorated<Type,PriorityDecorator>::write(value,milliseconds); }
#     endif

      void 
      write(long priority,
            const Type& value)
         RWTHRTHROWSANY;

      RWBoolean 
      tryWrite(long priority,
               const Type& value)
         RWTHRTHROWSANY;

      RWWaitStatus 
      write(long priority,
            const Type& value,
            unsigned long milliseconds)
         RWTHRTHROWSANY;

      virtual
      ~RWPCValBufferBasePrioritized()
         RWTHRTHROWSNONE;

   protected:

      RWPCValBufferBasePrioritized(void)
         RWTHRTHROWSANY;

      RWPCValBufferBasePrioritized(size_t maxCapacity,
                                   RWBoolean isOpen)
         RWTHRTHROWSANY;

      void
      _writeBack(const PriorityDecorator& value)
         RWTHRTHROWSANY;

      void
      _writeFront(const PriorityDecorator& value)
         RWTHRTHROWSANY;

};

template <class Type>
class RWTHRTExport RWPCValQueuePrioritized : 
   public RWPCValBufferBasePrioritized<Type,RWPriorityDecorator<Type> > {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCValQueuePrioritized(size_t maxCapacity2=0,  // Arg names changed to keep aCC 3.10 happy!
                              RWBoolean isOpen2=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(const RWPriorityDecorator<Type>& value)
         RWTHRTHROWSANY;

};

template <class Type>
class RWTHRTExport RWPCValStackPrioritized : 
   public RWPCValBufferBasePrioritized<Type, RWPriorityDecorator<Type> > {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCValStackPrioritized(size_t maxCapacity=0,
                              RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(const RWPriorityDecorator<Type>& value)
         RWTHRTHROWSANY;

};

template <class Type, class GuardAndPriorityDecorator>
class RWTHRTExport RWPCValBufferBaseGuardedPrioritized : 
   public RWPCValBufferBaseGuarded<Type,GuardAndPriorityDecorator>,
   public RWPCValBufferBasePrioritized<Type,GuardAndPriorityDecorator> {
   
   RW_THR_DECLARE_TRACEABLE

   public:

      typedef Type     DataType;

#     if defined(RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT)      
      // Import super-class methods that would be hidden by 
      // overloaded names defined in this class...
      RWUSING RWPCValBufferBaseGuarded<Type,GuardAndPriorityDecorator>::write;
      RWUSING RWPCValBufferBaseGuarded<Type,GuardAndPriorityDecorator>::tryWrite;
      RWUSING RWPCValBufferBasePrioritized<Type,GuardAndPriorityDecorator>::write;
      RWUSING RWPCValBufferBasePrioritized<Type,GuardAndPriorityDecorator>::tryWrite;
#     else
      // Wrap calls to super-class methods that would be hidden by
      // overloaded names defined in this class...
      void write(const Type& value) RWTHRTHROWSANY { RWPCValBufferBaseDecorated<Type,GuardAndPriorityDecorator>::write(value); }
      RWBoolean tryWrite(const Type& value) RWTHRTHROWSANY { return RWPCValBufferBaseDecorated<Type,GuardAndPriorityDecorator>::tryWrite(value); }
      RWWaitStatus write(const Type& value,unsigned long milliseconds) RWTHRTHROWSANY { return RWPCValBufferBaseDecorated<Type,GuardAndPriorityDecorator>::write(value,milliseconds); }
      void write(const Type& value,const RWFunctorR0<RWBoolean>& guard) RWTHRTHROWSANY { RWPCValBufferBaseGuarded<Type,GuardAndPriorityDecorator>::write(value,guard); }
      RWBoolean tryWrite(const Type& value,const RWFunctorR0<RWBoolean>& guard) RWTHRTHROWSANY { return RWPCValBufferBaseGuarded<Type,GuardAndPriorityDecorator>::tryWrite(value,guard); }
      RWWaitStatus write(const Type& value,const RWFunctorR0<RWBoolean>& guard,unsigned long milliseconds) RWTHRTHROWSANY { return RWPCValBufferBaseGuarded<Type,GuardAndPriorityDecorator>::write(value,guard,milliseconds); }
      void write(long priority,const Type& value) RWTHRTHROWSANY { RWPCValBufferBasePrioritized<Type,GuardAndPriorityDecorator>::write(priority,value); }
      RWBoolean tryWrite(long priority,const Type& value) RWTHRTHROWSANY { return RWPCValBufferBasePrioritized<Type,GuardAndPriorityDecorator>::tryWrite(priority,value); }
      RWWaitStatus write(long priority,const Type& value,unsigned long milliseconds) RWTHRTHROWSANY { return RWPCValBufferBasePrioritized<Type,GuardAndPriorityDecorator>::write(priority,value,milliseconds); }
#     endif

      void 
      write(long priority,
            const Type& value,
            const RWFunctorR0<RWBoolean>& guard)
         RWTHRTHROWSANY;

      RWBoolean 
      tryWrite(long priority,
               const Type& value,
               const RWFunctorR0<RWBoolean>& guard)
         RWTHRTHROWSANY;

      RWWaitStatus 
      write(long priority,
            const Type& value,
            const RWFunctorR0<RWBoolean>& guard,
            unsigned long milliseconds)
         RWTHRTHROWSANY;

      virtual
      ~RWPCValBufferBaseGuardedPrioritized()
         RWTHRTHROWSNONE;

   protected:

      RWPCValBufferBaseGuardedPrioritized(void)
         RWTHRTHROWSANY;

      RWPCValBufferBaseGuardedPrioritized(size_t maxCapacity,
                                          RWBoolean isOpen)
         RWTHRTHROWSANY;

};


template <class Type>
class RWTHRTExport RWPCValQueueGuardedPrioritized : 
   public RWPCValBufferBaseGuardedPrioritized<Type,RWGuardAndPriorityDecorator<Type> > {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCValQueueGuardedPrioritized(size_t maxCapacity3=0,  // Arg names changed to keep aCC 3.10 happy!
                                     RWBoolean isOpen3=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(const RWGuardAndPriorityDecorator<Type>& value)
         RWTHRTHROWSANY;

};

template <class Type>
class RWTHRTExport RWPCValStackGuardedPrioritized : 
   public RWPCValBufferBaseGuardedPrioritized<Type,RWGuardAndPriorityDecorator<Type> > {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCValStackGuardedPrioritized(size_t maxCapacity=0,
                                     RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(const RWGuardAndPriorityDecorator<Type>& value)
         RWTHRTHROWSANY;

};


// deprecated
template <class Type>
class RWTHRTExport RWPCPtrBufferBase : 
   public RWPCBufferBase {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef Type     DataType;
   
   protected:

      RWTPtrSlist<Type>          buffer_;
   
   public:
      
      Type*
      read(void)
         RWTHRTHROWSANY;

      RWBoolean
      tryRead(Type*& result)
         RWTHRTHROWSANY;

      RWWaitStatus 
      read(Type*& value,unsigned long milliseconds)
         RWTHRTHROWSANY;

      Type*
      peek(void)
         RWTHRTHROWSANY;

      RWBoolean
      tryPeek(Type*& result)
         RWTHRTHROWSANY;

      RWWaitStatus 
      peek(Type*& value,unsigned long milliseconds)
         RWTHRTHROWSANY;

      void 
      write(Type* value)
         RWTHRTHROWSANY;

      RWBoolean 
      tryWrite(Type* value)
         RWTHRTHROWSANY;

      RWWaitStatus 
      write(Type* value,unsigned long milliseconds)
         RWTHRTHROWSANY;

      virtual
      ~RWPCPtrBufferBase()
         RWTHRTHROWSNONE;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      RWPCPtrBufferBase(size_t maxCapacity=0,
                        RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

      // deprecated
      RWBoolean
      isWriteable(void) const
         RWTHRTHROWSANY;

      // deprecated
      RWBoolean
      isReadable(void) const
         RWTHRTHROWSANY;

      virtual
      size_t 
      _entries(void) const
         RWTHRTHROWSANY;

      virtual
      void
      _flush(void)
         RWTHRTHROWSANY;

      virtual
      Type* 
      _read(void)
         RWTHRTHROWSANY;

      virtual
      Type* 
      _peek(void)
         RWTHRTHROWSANY;

      virtual
      void
      _write(Type* value)
         RWTHRTHROWSANY = 0;

};


template <class Type>
class RWTHRTExport RWPCPtrQueue :
   public RWPCPtrBufferBase<Type> {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCPtrQueue(size_t maxCapacity=0,
                   RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(Type* value)
         RWTHRTHROWSANY;

};

template <class Type>
class RWTHRTExport RWPCPtrStack : 
   public RWPCPtrBufferBase<Type> {

   RW_THR_DECLARE_TRACEABLE

   public:
      
      RWPCPtrStack(size_t maxCapacity=0,
                   RWBoolean isOpen=TRUE)
         RWTHRTHROWSANY;

#  if defined(RW_THR_BROKEN_TYPEDEF_INHERITANCE)
      // Some compilers don't see the super-class declaration of these.
      typedef RWLockGuard< RWMonitor< RWMutexLock > >     LockGuard;
      typedef RWUnlockGuard< RWMonitor< RWMutexLock > >   UnlockGuard;
      typedef RWTryLockGuard< RWMonitor< RWMutexLock > >  TryLockGuard;
#  endif

   protected:

      virtual
      void
      _write(Type* value)
         RWTHRTHROWSANY;

};

/*****************************************************************************/

template <class Type>
inline
RWDecorator<Type>::RWDecorator(void)
{
   RWTHRTRACEMFT1(RWDecorator,Type,RWDecorator(void));
}


template <class Type>
inline
RWDecorator<Type>::RWDecorator(const Type& value) :
   value_(value)
{
   RWTHRTRACEMFT1(RWDecorator,Type,RWDecorator(const Type&));
}

template <class Type>
inline
RWBoolean
RWDecorator<Type>::operator==(const RWDecorator<Type>& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWDecorator,Type,operator==(const RWDecorator<Type>&) const:RWBoolean);
   return this->value_ == second.value_;
}

template <class Type>
inline
RWBoolean
RWDecorator<Type>::operator<(const RWDecorator<Type>& second) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWDecorator,Type,operator<(const RWDecorator<Type>&) const:RWBoolean);
   return this->value_ < second.value_;
}

/*****************************************************************************/

template <class Type>
inline
RWGuardDecorator<Type>::RWGuardDecorator(void) :
   guard_(RWFunctorR0<RWBoolean>())
{
   RWTHRTRACEMFT1(RWGuardDecorator,Type,RWGuardDecorator(void));
}

template <class Type>
inline
RWGuardDecorator<Type>::RWGuardDecorator(const Type& value) :
   RWDecorator<Type>(value),
   guard_(RWFunctorR0<RWBoolean>())
{
   RWTHRTRACEMFT1(RWGuardDecorator,Type,RWGuardDecorator(const Type&));
}

template <class Type>
inline
RWGuardDecorator<Type>::RWGuardDecorator(const Type& value,
                                         const RWFunctorR0<RWBoolean>& guard) :
   RWDecorator<Type>(value),
   guard_(guard)
{
   RWTHRTRACEMFT1(RWGuardDecorator,Type,RWGuardDecorator(const Type&,const RWFunctorR0<RWBoolean>&));
}

/*****************************************************************************/

template <class Type>
inline
RWPriorityDecorator<Type>::RWPriorityDecorator(void) :
   priority_(0)
{
   RWTHRTRACEMFT1(RWPriorityDecorator,Type,RWPriorityDecorator(void));
}

template <class Type>
inline
RWPriorityDecorator<Type>::RWPriorityDecorator(const Type& value) :
   RWDecorator<Type>(value),
   priority_(0)
{
   RWTHRTRACEMFT1(RWPriorityDecorator,Type,RWPriorityDecorator(const Type&));
}

template <class Type>
inline
RWPriorityDecorator<Type>::RWPriorityDecorator(const Type& value,
                                               long priority) :
   RWDecorator<Type>(value),
   priority_(priority)
{
   RWTHRTRACEMFT1(RWPriorityDecorator,Type,RWPriorityDecorator(const Type&,long));
}

/*****************************************************************************/

template <class Type>
inline
RWGuardAndPriorityDecorator<Type>::RWGuardAndPriorityDecorator(void)
{
   RWTHRTRACEMFT1(RWGuardAndPriorityDecorator,Type,RWGuardAndPriorityDecorator(void));
}

template <class Type>
inline
RWGuardAndPriorityDecorator<Type>::RWGuardAndPriorityDecorator(const Type& value) :
   RWDecorator<Type>(value)
{
   RWTHRTRACEMFT1(RWGuardAndPriorityDecorator,Type,RWGuardAndPriorityDecorator(const Type&));
}

template <class Type>
inline
RWGuardAndPriorityDecorator<Type>::RWGuardAndPriorityDecorator(const Type& value,
                                                               const RWFunctorR0<RWBoolean>& guard) :
   RWDecorator<Type>(value)
{
   RWTHRTRACEMFT1(RWGuardAndPriorityDecorator,Type,RWGuardAndPriorityDecorator(const Type&,const RWFunctorR0<RWBoolean>&));
   // Use member assigment instead of constructor-based initialization
   // to avoid having to make multiple copies of value
   this->guard_=guard;
}

template <class Type>
inline
RWGuardAndPriorityDecorator<Type>::RWGuardAndPriorityDecorator(const Type& value,
                                                               long priority) :
   RWDecorator<Type>(value)
{
   RWTHRTRACEMFT1(RWGuardAndPriorityDecorator,Type,RWGuardAndPriorityDecorator(const Type&,long));
   // Use member assigment instead of constructor-based initialization
   // to avoid having to make multiple copies of value
   this->priority_ = priority;
}

template <class Type>
inline
RWGuardAndPriorityDecorator<Type>::RWGuardAndPriorityDecorator(const Type& value,
                                                               const RWFunctorR0<RWBoolean>& guard,
                                                               long priority) :
   RWDecorator<Type>(value)
{
   RWTHRTRACEMFT1(RWGuardAndPriorityDecorator,Type,RWGuardAndPriorityDecorator(const Type&,const RWFunctorR0<RWBoolean>&,long));
   // Use member assigment instead of constructor-based initialization
   // to avoid having to make multiple copies of value
   this->guard_ = guard;
   this->priority_ = priority;
}

/*****************************************************************************/

template <class Type>
inline
RWPCValBufferBase<Type>::RWPCValBufferBase(size_t maxCapacity,
                                           RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCBufferBase(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,RWPCValBufferBase(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCValQueue<Type>::RWPCValQueue(size_t maxCapacity,
                                 RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBase<Type>(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValQueue,Type,RWPCValQueue(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCValStack<Type>::RWPCValStack(size_t maxCapacity,
                                 RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBase<Type>(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValStack,Type,RWPCValStack(size_t,RWBoolean));
}


/*****************************************************************************/

template <class Type, class Decorator>
inline
RWPCValBufferBaseDecorated<Type,Decorator>::RWPCValBufferBaseDecorated(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,RWPCValBufferBaseDecorated(void));
}

template <class Type, class Decorator>
inline
RWPCValBufferBaseDecorated<Type,Decorator>::RWPCValBufferBaseDecorated(size_t maxCapacity,
                                                                       RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBase<Decorator>(maxCapacity,isOpen)
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,RWPCValBufferBaseDecorated(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type, class GuardDecorator>
inline
RWPCValBufferBaseGuarded<Type,GuardDecorator>::RWPCValBufferBaseGuarded(void)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseDecorated<Type,GuardDecorator>(),
      iter_(RWPCValBufferBase<GuardDecorator>::buffer_)
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,RWPCValBufferBaseGuarded(void));
}

template <class Type, class GuardDecorator>
inline
RWPCValBufferBaseGuarded<Type,GuardDecorator>::RWPCValBufferBaseGuarded(size_t maxCapacity,
                                                                        RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseDecorated<Type,GuardDecorator>(maxCapacity,isOpen),
      iter_(RWPCValBufferBase<GuardDecorator>::buffer_)
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,RWPCValBufferBaseGuarded(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCValQueueGuarded<Type>::RWPCValQueueGuarded(size_t maxCapacity,
                                               RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseGuarded<Type,RWGuardDecorator<Type> >(),
      RWPCValBufferBaseDecorated<Type,RWGuardDecorator<Type> >(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValQueueGuarded,Type,RWPCValQueueGuarded(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCValStackGuarded<Type>::RWPCValStackGuarded(size_t maxCapacity,
                                               RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseGuarded<Type,RWGuardDecorator<Type> >(),
      RWPCValBufferBaseDecorated<Type,RWGuardDecorator<Type> >(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValStackGuarded,Type,RWPCValStackGuarded(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type, class PriorityDecorator>
inline
RWPCValBufferBasePrioritized<Type,PriorityDecorator>::RWPCValBufferBasePrioritized(void)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseDecorated<Type,PriorityDecorator>()
{
   RWTHRTRACEMFT2(RWPCValBufferBasePrioritized,Type,PriorityDecorator,RWPCValBufferBasePrioritized(void));
}

template <class Type, class PriorityDecorator>
inline
RWPCValBufferBasePrioritized<Type,PriorityDecorator>::RWPCValBufferBasePrioritized(size_t maxCapacity,
                                                                                   RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseDecorated<Type,PriorityDecorator>(maxCapacity,isOpen)
{
   RWTHRTRACEMFT2(RWPCValBufferBasePrioritized,Type,PriorityDecorator,RWPCValBufferBasePrioritized(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCValQueuePrioritized<Type>::RWPCValQueuePrioritized(size_t maxCapacity,
                                                       RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBasePrioritized<Type,RWPriorityDecorator<Type> >(),
      RWPCValBufferBaseDecorated<Type,RWPriorityDecorator<Type> >(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValQueuePrioritized,Type,RWPCValQueuePrioritized(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCValStackPrioritized<Type>::RWPCValStackPrioritized(size_t maxCapacity,
                                                       RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBasePrioritized<Type,RWPriorityDecorator<Type> >(),
      RWPCValBufferBaseDecorated<Type,RWPriorityDecorator<Type> >(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValStackPrioritized,Type,RWPCValStackPrioritized(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type, class GuardAndPriorityDecorator>
inline
RWPCValBufferBaseGuardedPrioritized<Type,GuardAndPriorityDecorator>::RWPCValBufferBaseGuardedPrioritized(void)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseGuarded<Type,GuardAndPriorityDecorator>(),
      RWPCValBufferBasePrioritized<Type,GuardAndPriorityDecorator>(),
      RWPCValBufferBaseDecorated<Type,GuardAndPriorityDecorator>()
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuardedPrioritized,Type,GuardAndPriorityDecorator,RWPCValBufferBaseGuardedPrioritized(void));
}

template <class Type, class GuardAndPriorityDecorator>
inline
RWPCValBufferBaseGuardedPrioritized<Type,GuardAndPriorityDecorator>::RWPCValBufferBaseGuardedPrioritized(size_t maxCapacity,
                                                                                                         RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseGuarded<Type,GuardAndPriorityDecorator>(),
      RWPCValBufferBasePrioritized<Type,GuardAndPriorityDecorator>(),
      RWPCValBufferBaseDecorated<Type,GuardAndPriorityDecorator>(maxCapacity, isOpen)
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuardedPrioritized,Type,GuardAndPriorityDecorator,RWPCValBufferBaseGuardedPrioritized(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCValQueueGuardedPrioritized<Type>::RWPCValQueueGuardedPrioritized(size_t maxCapacity,
                                                                     RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseGuardedPrioritized<Type,RWGuardAndPriorityDecorator<Type> >(),
      RWPCValBufferBaseDecorated<Type,RWGuardAndPriorityDecorator<Type> >(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValQueuePrioritized,Type,RWPCValQueuePrioritized(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCValStackGuardedPrioritized<Type>::RWPCValStackGuardedPrioritized(size_t maxCapacity,
                                                                     RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCValBufferBaseGuardedPrioritized<Type,RWGuardAndPriorityDecorator<Type> >(),
      RWPCValBufferBaseDecorated<Type,RWGuardAndPriorityDecorator<Type> >(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCValStackPrioritized,Type,RWPCValStackPrioritized(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCPtrBufferBase<Type>::RWPCPtrBufferBase(size_t maxCapacity,
                                           RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCBufferBase(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,RWPCPtrBufferBase(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCPtrQueue<Type>::RWPCPtrQueue(size_t maxCapacity,
                                 RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCPtrBufferBase<Type>(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCPtrQueue,Type,RWPCPtrQueue(size_t,RWBoolean));
}

/*****************************************************************************/

template <class Type>
inline
RWPCPtrStack<Type>::RWPCPtrStack(size_t maxCapacity,
                                 RWBoolean isOpen)
   RWTHRTHROWSANY
   :
      RWPCPtrBufferBase<Type>(maxCapacity,isOpen)
{
   RWTHRTRACEMFT1(RWPCPtrStack,Type,RWPCPtrStack(size_t,RWBoolean));
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/prodcons.cc>
#  endif

#endif // __RWTHRPRODCONS_H__
