#if !defined(__RWTHRPRODCONS_CC__)
#  define __RWTHRPRODCONS_CC__
/*****************************************************************************
 *
 * prodcons.cc
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

prodcons.cc  - Out-of-line function definitions for:

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

   prodcons.h - Class declarations.

******************************************************************************/

#  if !defined(__RWTHRPRODCONS_H__)
#     include <rw/thr/prodcons.h> 
#  endif

#  if !defined(__RWTIMER_H__)
#     include <rw/timer.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValBufferBase,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValQueue,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValStack,Type)

RW_THR_IMPLEMENT_TRACEABLE_T1(RWDecorator,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWGuardDecorator,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPriorityDecorator,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWGuardAndPriorityDecorator,Type)

RW_THR_IMPLEMENT_TRACEABLE_T2(RWPCValBufferBaseDecorated,Type,Decorator)

RW_THR_IMPLEMENT_TRACEABLE_T2(RWPCValBufferBaseGuarded,Type,GuardDecorator)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValQueueGuarded,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValStackGuarded,Type)

RW_THR_IMPLEMENT_TRACEABLE_T2(RWPCValBufferBasePrioritized,Type,PriorityDecorator)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValQueuePrioritized,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValStackPrioritized,Type)

RW_THR_IMPLEMENT_TRACEABLE_T2(RWPCValBufferBaseGuardedPrioritized,Type,GuardAndPriorityDecorator)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValQueueGuardedPrioritized,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCValStackGuardedPrioritized,Type)

RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCPtrBufferBase,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCPtrQueue,Type)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWPCPtrStack,Type)


template <class Type>
RWPCValBufferBase<Type>::~RWPCValBufferBase(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,~RWPCValBufferBase(void):);
}

template <class Type>
size_t
RWPCValBufferBase<Type>::_entries(void) const
        RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,_entries(void) const:size_t);
   return buffer_.entries();
}

/* Deprecated */
template <class Type>
RWBoolean
RWPCValBufferBase<Type>::isReadable(void) const
   RWTHRTHROWSANY
{   
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,isReadable(void) const:RWBoolean);
   return buffer_.entries() > 0;
}

/* Deprecated */
template <class Type>
RWBoolean
RWPCValBufferBase<Type>::isWriteable(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,isWriteable(void) const:RWBoolean);
   return this->maxEntries_ == 0 || this->buffer_.entries() < this->maxEntries_;
}

template <class Type>
Type
RWPCValBufferBase<Type>::read(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,read(void):Type);

   RWBoolean canRead;

   LockGuard lock(monitor());

   // Keep looping until there is something to read or the buffer is closed...
   while(!(canRead = _canRead()) && this->isOpen_) {

      // Each time we execute within this while-loop, we will either invoke 
      // the callback or wait for a write, but cannot do both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for 
      // a write!

      // Is this the first thread to find the buffer empty since the last
      // write or callback registration? Is there a valid functor to invoke?

      if (!this->hasInvokedEmptyCallback_ && 
          this->onEmptyCallback_.isValid()) {

         // Yes, this thread is the first reader thread to find the buffer
         // empty since the last write or callback registration - it will need
         // to invoke the callback functor.
         this->hasInvokedEmptyCallback_ = TRUE;

         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onEmptyCallback_;

         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canRead() == TRUE:

         //  The buffer may have exited the empty state or an entry may have
         //  become "readable" as a result of write operations, so this thread 
         //  will no longer need to wait for a write.

         //  isOpen_ == FALSE && _canRead() == FALSE

         //  The buffer has exited the open state and is now closed, so unless
         //  the buffer contains entries that this thread can read, the thread 
         //  must not be allowed to wait for a write, since writes will not be 
         //  allowed while the buffer is closed, so this method must exit with 
         //  an exception.

         //  hasInvokedEmptyCallback_ == FALSE;

         //  The buffer may have exited and reentered the empty state as a 
         //  result of some combination of read and write operations, but the
         //  empty callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onEmptyCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.
      }
      else {

         // The buffer is empty and this is not the first thread to
         // find the buffer is empty, or there is no callback to invoke.
         this->waitingReaders_++;

         try {
            // Unlock the buffer and wait for a write operation, a new empty 
            // callback registration, a close operation, or an interrupt.
            notEmpty_.wait();
            this->waitingReaders_--;
         }
         catch(...) {
            this->waitingReaders_--;
            throw;
         }
      }
   }

   Type result;
   
   if (canRead) {
      result = _read();
      if (_entries() < this->maxEntries_) {
         // Indicate that the queue has exited the full state
         this->hasInvokedFullCallback_ = FALSE;
         // If there are writers waiting, wake one up so it can write.
         if (this->waitingWriters_ > 0) notFull_.signal();
      }
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   return result;
}

template <class Type>
RWBoolean
RWPCValBufferBase<Type>::tryRead(Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,tryRead(Type&):RWBoolean);

   RWBoolean result;

   LockGuard lock(monitor());

   if (_canRead() && this->isOpen_) {
      value = _read();
      if (_entries() < this->maxEntries_) {
         // Indicate that the queue has left the full state
         this->hasInvokedFullCallback_ = FALSE;
         // If there are writers waiting, wake one up so it can write.
         if (this->waitingWriters_ > 0) notFull_.signal();
      }
      result = TRUE;
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   else {
      result = FALSE;
   }

   return result;
}

template <class Type>
RWWaitStatus
RWPCValBufferBase<Type>::read(Type& result,unsigned long milliseconds)

   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,read(Type&,unsigned long):RWWaitStatus);

   RWTimer  timer;
   unsigned long elapsedMilliseconds; 
   RWWaitStatus waitStatus = RW_THR_COMPLETED;     

   LockGuard lock(monitor());

   RWBoolean canRead = _canRead();

   // Keep looping until there is something to read, the buffer is closed,
   // or the operation times-out...
   
   while(!canRead && this->isOpen_ && RW_THR_COMPLETED == waitStatus) {
   
      // Reset and re-start the timer...
      timer.reset();
      timer.start();
      
      // Each time we execute within this while-loop, we will either invoke 
      // the callback or wait for a write, but cannot do both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for 
      // a write!

      // Is this the first thread to find the buffer empty since the last
      // write or callback registration?  Is there a valid functor to invoke?

      if (!this->hasInvokedEmptyCallback_ && 
          this->onEmptyCallback_.isValid()) {

         // Yes, this thread is the first reader thread to find the buffer
         // empty since the last write or callback registration - it will need
         // to invoke the callback functor.
         this->hasInvokedEmptyCallback_ = TRUE;
         
         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onEmptyCallback_;
         
         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());
         
         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canRead() == TRUE:

         //  The buffer may have exited the empty state as a result of write 
         //  operations, so this thread will no longer need to wait for an 
         //  entry to be written to the buffer.

         //  isOpen_ == FALSE && _canRead() == FALSE

         //  The buffer has exited the open state and is now closed, so unless
         //  the buffer contains entries that this thread can read, the thread 
         //  must not be allowed to wait for a write, since writes will not be 
         //  allowed while the buffer is closed. 

         //  hasInvokedEmptyCallback_ == FALSE;

         //  The buffer may have exited and re-entered the empty state as a 
         //  result of some combination of read and write operations, but the
         //  empty callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onEmptyCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.

         //  elapsed time >= milliseconds

         //  The time-out period may have elapsed.
      }
      else {
         // The buffer is empty and this is not the first thread to
         // find the buffer is empty, or there is no callback to invoke.
         this->waitingReaders_++;
         try {
            // Unlock the buffer and wait for a write operation, a new empty 
            // callback registration, a close operation, or an interrupt.
            waitStatus = notEmpty_.wait(milliseconds);
            this->waitingReaders_--;
         }
         catch(...) {
            this->waitingReaders_--;
            throw;
         }
      }
      // Do we need to go back and wait again?
      if (!(canRead = _canRead()) && this->isOpen_ && RW_THR_COMPLETED == waitStatus) {

         // Yes, the thread either hasn't waited yet, or the thread did wait 
         // and received a signal to indicate that a write has occurred, that a 
         // new empty callback functor has been registered, or that the buffer 
         // has been closed.

         // Determine how much time has elapsed, and use that to calculate 
         // how much time, if any, remains from the original time-out period.
         timer.stop();
         elapsedMilliseconds = (unsigned long)(timer.elapsedTime()/1000.0);
         if (elapsedMilliseconds < milliseconds) {
            // The operation has not timed-out.
            // Calculate the time remaining before the operation must time-out.
            milliseconds -= elapsedMilliseconds;
         }
         else {
            // The operation has timed-out.
            waitStatus = RW_THR_TIMEOUT;
         }
      }
   }
   if (canRead) {
      result = _read();
      waitStatus = RW_THR_COMPLETED;
      if (_entries() < this->maxEntries_) {
         // Indicate that the queue has exited the full state
         this->hasInvokedFullCallback_ = FALSE;
         // If there are writers waiting, wake one up so it can write.
         if (this->waitingWriters_ > 0) notFull_.signal();
      }
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   return waitStatus;
}

template <class Type>
Type
RWPCValBufferBase<Type>::peek(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,peek(void):Type);

   RWBoolean canRead;

   LockGuard lock(monitor());

   // Keep looping until there is something to read or the buffer is closed...
   while(!(canRead = _canRead()) && this->isOpen_) {

      // Each time we execute within this while-loop, we will either invoke 
      // the callback or wait for a write, but cannot do both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for 
      // a write!

      // Is this the first thread to find the buffer empty since the last
      // write or callback registration? Is there a valid functor to invoke?

      if (!this->hasInvokedEmptyCallback_ && 
          this->onEmptyCallback_.isValid()) {

         // Yes, this thread is the first reader thread to find the buffer
         // empty since the last write or callback registration - it will need
         // to invoke the callback functor.
         this->hasInvokedEmptyCallback_ = TRUE;

         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onEmptyCallback_;

         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canRead() == TRUE:

         //  The buffer may have exited the empty state as a result of write 
         //  operations, so this thread will no longer need to wait for a 
         //  write.

         //  isOpen_ == FALSE && _canRead() == FALSE

         //  The buffer has exited the open state and is now closed, so unless
         //  the buffer contains entries that this thread can read, the thread 
         //  must not be allowed to wait for a write, since writes will not be 
         //  allowed while the buffer is closed, so this method must exit with 
         //  an exception.

         //  hasInvokedEmptyCallback_ == FALSE;

         //  The buffer may have exited and reentered the empty state as a 
         //  result of some combination of read and write operations, but the
         //  empty callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onEmptyCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.
      }
      else {

         // The buffer is empty and this is not the first thread to
         // find the buffer is empty, or there is no callback to invoke.
         this->waitingReaders_++;

         try {
            // Unlock the buffer and wait for a write operation, a new empty 
            // callback registration, a close operation, or an interrupt.
            notEmpty_.wait();
            this->waitingReaders_--;
         }
         catch(...) {
            this->waitingReaders_--;
            throw;
         }
      }
   }

   Type result;   
   if (canRead) {
      result = _peek();
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   return result;
}

template <class Type>
RWBoolean
RWPCValBufferBase<Type>::tryPeek(Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,tryPeek(Type&):RWBoolean);

   RWBoolean result;
   
   LockGuard lock(monitor());
   
   if (_canRead() && this->isOpen_) {
      value = _peek();
      result = TRUE;
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   else {
      result = FALSE;
   }
   return result;
}

template <class Type>
RWWaitStatus
RWPCValBufferBase<Type>::peek(Type& result,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,peek(Type&,unsigned long):RWWaitStatus);

   RWTimer  timer;
   unsigned long elapsedMilliseconds; 
   RWWaitStatus waitStatus = RW_THR_COMPLETED;     

   LockGuard lock(monitor());

   RWBoolean canRead = _canRead();

   // Keep looping until there is something to read, the buffer is closed,
   // or the operation times-out...
   
   while(!canRead && this->isOpen_ && RW_THR_COMPLETED == waitStatus) {
   
      // Reset and re-start the timer...
      timer.reset();
      timer.start();
      
      // Each time we execute within this while-loop, we will either invoke 
      // the callback or wait for a write, but cannot do both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for 
      // a write!

      // Is this the first thread to find the buffer empty since the last
      // write or callback registration?  Is there a valid functor to invoke?

      if (!this->hasInvokedEmptyCallback_ && 
          this->onEmptyCallback_.isValid()) {

         // Yes, this thread is the first reader thread to find the buffer
         // empty since the last write or callback registration - it will need
         // to invoke the callback functor.
         this->hasInvokedEmptyCallback_ = TRUE;
         
         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onEmptyCallback_;
         
         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());
         

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canRead() == TRUE:

         //  The buffer may have exited the empty state as a result of write 
         //  operations, so this thread will no longer need to wait for an 
         //  entry to be written to the buffer.

         //  isOpen_ == FALSE && _canRead() == FALSE

         //  The buffer has exited the open state and is now closed, so unless
         //  the buffer contains entries that this thread can read, the thread 
         //  must not be allowed to wait for a write, since writes will not be 
         //  allowed while the buffer is closed. 

         //  hasInvokedEmptyCallback_ == FALSE;

         //  The buffer may have exited and re-entered the empty state as a 
         //  result of some combination of read and write operations, but the
         //  empty callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onEmptyCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.

         //  elapsed time >= milliseconds

         //  The time-out period may have elapsed.
      }
      else {
         // The buffer is empty and this is not the first thread to
         // find the buffer is empty, or there is no callback to invoke.
         this->waitingReaders_++;
         try {
            // Unlock the buffer and wait for a write operation, a new empty 
            // callback registration, a close operation, or an interrupt.
            waitStatus = notEmpty_.wait(milliseconds);
            this->waitingReaders_--;
         }
         catch(...) {
            this->waitingReaders_--;
            throw;
         }
      }
      // Do we need to go back and wait again?
      if (!(canRead = _canRead()) && this->isOpen_ && RW_THR_COMPLETED == waitStatus) {

         // Yes, the thread either hasn't waited yet, or the thread did wait 
         // and received a signal to indicate that a write has occurred, that a 
         // new empty callback functor has been registered, or that the buffer 
         // has been closed.

         // Determine how much time has elapsed, and use that to calculate 
         // how much time, if any, remains from the original time-out period.
         timer.stop();
         elapsedMilliseconds = (unsigned long)(timer.elapsedTime()/1000.0);
         if (elapsedMilliseconds < milliseconds) {
            // The operation has not timed-out.
            // Calculate the time remaining before the operation must time-out.
            milliseconds -= elapsedMilliseconds;
         }
         else {
            // The operation has timed-out.
            waitStatus = RW_THR_TIMEOUT;
         }
      }
   }
   if (canRead) {
      result = _peek();
      waitStatus = RW_THR_COMPLETED;
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   return waitStatus;
}

template <class Type>
void
RWPCValBufferBase<Type>::write(const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,write(const Type&):void);
   LockGuard lock(this->monitor());

   // Keep looping until there is room to write or the buffer is closed...
   while(!_canWrite() && this->isOpen_) {

      // Each time we execute within this while-loop, we will either invoke 
      // the full callback or wait for room to write, but not both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for room!

      // Is this the first thread to find the buffer full since the last read,
      // capacity change, or callback registration?  
      // Is there a valid functor to invoke?

      if (!this->hasInvokedFullCallback_ && 
          this->onFullCallback_.isValid()) {

         // Yes, this thread is the first writer thread to find the buffer
         // full since the last time the buffer was less than full, or 
         // since the last callback registration - it will need to invoke the 
         // callback functor.
         this->hasInvokedFullCallback_ = TRUE;

         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onFullCallback_;

         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canWrite() == TRUE:

         //  The buffer may have exited the full state as a result of read 
         //  operations or a capacity change, or the buffer may have been 
         //  closed, so this thread will no longer need to wait for any more 
         //  reads, and if the buffer has been closed, the method must exit 
         //  with an exception.

         //  hasInvokedFullcallback_ == FALSE;

         //  The buffer may have exited and reentered the full state as a 
         //  result of some combination of read and write operations, but the
         //  full callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onFullCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.
      }
      else {

         // The buffer is full and this is not the first thread to
         // find the buffer is full, or there is no callback to invoke.

         this->waitingWriters_++;
         try {
            this->notFull_.wait();
            this->waitingWriters_--;
         }
         catch(...) {
            this->waitingWriters_--;
            throw;
         }
      }
   }
   if (this->_canWrite()) {
      _write(value);
      // Indicate that the queue has exited the empty state
      this->hasInvokedEmptyCallback_ = FALSE;
      // If there are readers waiting, wake one up so it can read.
      if (this->waitingReaders_ > 0) this->notEmpty_.signal();
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is closed.
      throw RWTHRClosedException();
   }
}

template <class Type>
RWBoolean
RWPCValBufferBase<Type>::tryWrite(const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,tryWrite(const Type&):RWBoolean);

   RWBoolean result;

   LockGuard lock(this->monitor());

   if (this->_canWrite() && this->isOpen_) {
      _write(value);
      // Indicate that the queue has exited the empty state
      this->hasInvokedEmptyCallback_ = FALSE;
      // If there are readers waiting, wake one up so it can read.
      if (this->waitingReaders_ > 0) this->notEmpty_.signal();
      result = TRUE;
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is closed.
      throw RWTHRClosedException();
   }
   else {
      result = FALSE;
   }
   return result;
}

template <class Type>
RWWaitStatus
RWPCValBufferBase<Type>::write(const Type& value,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,write(const Type&,unsigned long):RWWaitStatus);

   RWTimer  timer;
   unsigned long elapsedMilliseconds; 
   RWWaitStatus waitStatus = RW_THR_COMPLETED;     

   LockGuard lock(this->monitor());

   // Keep looping until there is room, the buffer is closed, or until the
   // operation times out...

   while(!this->_canWrite() && this->isOpen_ && RW_THR_COMPLETED == waitStatus) {
      
      // Reset and restart the timer
      timer.reset();
      timer.start();

      // Each time we execute within this while-loop, we will either invoke
      // the full callback or wait for room to write, but not both.

      // We must not do both because the callback may directly or indirectly
      // change the state of the buffer, eliminating the need to wait for room!

      // Is this the first thread to find the buffer full since the last read,
      // capacity change, or callback registration?  
      // Is there a valid functor to invoke?

      if (!this->hasInvokedFullCallback_ && 
          this->onFullCallback_.isValid()) {

         // Yes, this thread is the first writer thread to find the buffer
         // full since the last time the buffer was less than full, or 
         // since the last callback registration - it will need to invoke the 
         // callback functor.
         this->hasInvokedFullCallback_ = TRUE;

         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onFullCallback_;

         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canWrite() == TRUE:

         //  The buffer may have exited the full state as a result of read 
         //  operations or a capacity change, or the buffer may have been 
         //  closed, so this thread will no longer need to wait for any more 
         //  reads, and if the buffer has been closed, the method must exit 
         //  with an exception.

         //  hasInvokedFullcallback_ == FALSE;

         //  The buffer may have exited and reentered the full state as a 
         //  result of some combination of read and write operations, but the
         //  full callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onFullCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.

         //  elapsed time >= milliseconds

         //  The time-out period may have elapsed.
      }
      else {

         // The buffer is full and this is not the first thread to
         // find the buffer is full, or there is no callback to invoke.

         this->waitingWriters_++;
         try {
            waitStatus = (this->notFull_).wait(milliseconds);
            this->waitingWriters_--;
         }
         catch(...) {
            this->waitingWriters_--;
            throw;
         }
      }
      // Has the operation timed-out?
      if (this->isOpen_ && RW_THR_COMPLETED == waitStatus) {

         // No, the thread either hasn't waited yet, or the thread did wait
         // and received a signal to indicate that the buffer is no longer full,
         // a new full callback functor has been registered, or that the buffer
         // has been closed.

         // Is the buffer still full and open? Does the thread still need to wait?
         if (!this->_canWrite()) {

            // The buffer is full and open - the thread still needs to wait.
            // Determine how much time has elapsed, and use that to calculate
            // how much time, if any, remains from the original time-out period.
            timer.stop();
            elapsedMilliseconds = (unsigned long)(timer.elapsedTime()/1000.0);
            if (elapsedMilliseconds < milliseconds) {
               // The operation has not timed-out.
               // Calculate the time remaining before the operation must time-out.
               milliseconds -= elapsedMilliseconds;
            }
            else {
               // The operation has timed-out.
               waitStatus = RW_THR_TIMEOUT;
            }
         }
      }
   }
   // Can we write, or did we time-out?
   if (this->_canWrite()) {
      _write(value);
      waitStatus = RW_THR_COMPLETED;
      // Indicate that the queue has exited the empty state.
      this->hasInvokedEmptyCallback_ = FALSE;
      // If there are readers waiting, wake one up so it can read.
      if (this->waitingReaders_ > 0) (this->notEmpty_).signal();
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is closed.
      throw RWTHRClosedException();
   }
   return waitStatus;
}

template <class Type>
void
RWPCValBufferBase<Type>::_flush(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,_flush(void):void);
   buffer_.clear();
   // Indicate that the queue has exited the full state
   this->hasInvokedFullCallback_ = FALSE;
   // If there are writers waiting, wake one up so it can write.
   if (this->waitingWriters_ > 0) notFull_.signal();
}

template <class Type>
Type 
RWPCValBufferBase<Type>::_read(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,_read(void):Type);
   return buffer_.removeFirst();
}

template <class Type>
Type
RWPCValBufferBase<Type>::_peek(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValBufferBase,Type,_peek(void):Type);
   return buffer_.first();
}

template <class Type>
void 
RWPCValQueue<Type>::_write(const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValQueue,Type,_write(const Type&):void);
   // Write at end of list for queue implementation
   (this->buffer_).append(value); 
}

template <class Type>
void 
RWPCValStack<Type>::_write(const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValStack,Type,_write(const Type&):void);
   // Write at beginning of list for stack implementation
   (this->buffer_).prepend(value); 
}

template <class Type, class Decorator>
RWPCValBufferBaseDecorated<Type,Decorator>::~RWPCValBufferBaseDecorated(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,~RWPCValBufferBaseDecorated(void):);
}

template <class Type, class Decorator>
Type
RWPCValBufferBaseDecorated<Type,Decorator>::read(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,read(void):Type);
   return RWPCValBufferBase<Decorator>::read().value_;
}

template <class Type, class Decorator>
RWBoolean
RWPCValBufferBaseDecorated<Type,Decorator>::tryRead(Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,tryRead(Type&):RWBoolean);
   Decorator decorator;
   RWBoolean result = RWPCValBufferBase<Decorator>::tryRead(decorator);
   value = decorator.value_;
   return result;
}

template <class Type, class Decorator>
RWWaitStatus
RWPCValBufferBaseDecorated<Type,Decorator>::read(Type& value,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,read(Type&,unsigned long):RWWaitStatus);
   Decorator decorator;
   RWWaitStatus result = RWPCValBufferBase<Decorator>::read(decorator,milliseconds);
   value = decorator.value_;
   return result;
}

template <class Type, class Decorator>
Type
RWPCValBufferBaseDecorated<Type,Decorator>::peek(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,peek(void):Type);
   return RWPCValBufferBase<Decorator>::peek().value_;
}

template <class Type, class Decorator>
RWBoolean
RWPCValBufferBaseDecorated<Type,Decorator>::tryPeek(Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,tryPeek(Type&):RWBoolean);
   Decorator decorator;
   RWBoolean result = RWPCValBufferBase<Decorator>::tryPeek(decorator);
   value = decorator.value_;
   return result;
}

template <class Type, class Decorator>
RWWaitStatus
RWPCValBufferBaseDecorated<Type,Decorator>::peek(Type& value,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,peek(Type&,unsigned long):RWWaitStatus);
   Decorator decorator;
   RWWaitStatus result = RWPCValBufferBase<Decorator>::peek(decorator,milliseconds);
   value = decorator.value_;
   return result;
}

template <class Type, class Decorator>
void
RWPCValBufferBaseDecorated<Type,Decorator>::write(const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,write(const Type& value):void);
   RWPCValBufferBase<Decorator>::write(Decorator(value));
}

template <class Type, class Decorator>
RWBoolean
RWPCValBufferBaseDecorated<Type,Decorator>::tryWrite(const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,tryWrite(const Type& value):RWBoolean);
   return RWPCValBufferBase<Decorator>::tryWrite(Decorator(value));
}

template <class Type, class Decorator>
RWWaitStatus
RWPCValBufferBaseDecorated<Type,Decorator>::write(const Type& value,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseDecorated,Type,Decorator,write(const Type& value,unsigned long):RWWaitStatus);
   return RWPCValBufferBase<Decorator>::write(Decorator(value),milliseconds);
}

template <class Type, class GuardDecorator>
RWPCValBufferBaseGuarded<Type,GuardDecorator>::~RWPCValBufferBaseGuarded(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,~RWPCValBufferBaseGuarded(void):);
}

template <class Type, class GuardDecorator>
void
RWPCValBufferBaseGuarded<Type,GuardDecorator>::write(const Type& value,const RWFunctorR0<RWBoolean>& guard)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,write(const Type& value,const RWFunctorR0<RWBoolean>&):void);
   RWPCValBufferBase<GuardDecorator>::write(GuardDecorator(value,guard));
}

template <class Type, class GuardDecorator>
RWBoolean
RWPCValBufferBaseGuarded<Type,GuardDecorator>::tryWrite(const Type& value,const RWFunctorR0<RWBoolean>& guard)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,tryWrite(const Type& value,const RWFunctorR0<RWBoolean>&):RWBoolean);
   return RWPCValBufferBase<GuardDecorator>::tryWrite(GuardDecorator(value,guard));
}

template <class Type, class GuardDecorator>
RWWaitStatus
RWPCValBufferBaseGuarded<Type,GuardDecorator>::write(const Type& value,const RWFunctorR0<RWBoolean>& guard,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,write(const Type& value,const RWFunctorR0<RWBoolean>&,unsigned long):RWWaitStatus);
   return RWPCValBufferBase<GuardDecorator>::write(GuardDecorator(value,guard),milliseconds);
}

template <class Type, class GuardDecorator>
RWBoolean
RWPCValBufferBaseGuarded<Type,GuardDecorator>::_canRead(void) const
   RWTHRTHROWSANY
{   
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,_canRead(void) const:RWBoolean);
   RWBoolean canRead = FALSE;
   // Is there anything in the buffer?
   if (this->_entries() > 0) {
      // Yes, there are entries in the buffer - now check to see if any are readable...
      // Reset iterator to leave it pointing at the beginning of the list
      RW_THR_CONST_CAST_C1(RWPCValBufferBaseGuarded<Type,GuardDecorator>*,this)->iter_.reset();
      // Iterate until we find an entry whose guard evaluates 
      // to TRUE or until we run out of entries.
      while(!canRead && RW_THR_CONST_CAST_C1(RWPCValBufferBaseGuarded<Type,GuardDecorator>*,this)->iter_()) {
         // Get the guard
         RWFunctorR0<RWBoolean> guard = this->iter_.key().guard_;
         // Invoke guard functor for the current entry (if one exists)
         canRead = !guard.isValid() || guard();
      }
   }
   return canRead;
}

template <class Type, class GuardDecorator>
GuardDecorator
RWPCValBufferBaseGuarded<Type,GuardDecorator>::_read(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,_read(void):GuardDecorator);
   // Use the value whose location was cached by an earlier call to _canRead()
   GuardDecorator decorator = this->iter_.key();
   this->iter_.remove();
   return decorator;
}

template <class Type, class GuardDecorator>
GuardDecorator
RWPCValBufferBaseGuarded<Type,GuardDecorator>::_peek(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuarded,Type,GuardDecorator,_peek(void):GuardDecorator);
   // Use the value whose location was cached by an earlier call to _canRead()
   return this->iter_.key();
}

template <class Type>
void 
RWPCValQueueGuarded<Type>::_write(const RWGuardDecorator<Type>& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValQueue,Type,_write(const RWGuardDecorator<Type>&):void);
   // Write at end of list for queue implementation
   (this->buffer_).append(value); 
}

template <class Type>
void 
RWPCValStackGuarded<Type>::_write(const RWGuardDecorator<Type>& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValStack,Type,_write(const RWGuardDecorator<Type>&):void);
   // Write at beginning of list for stack implementation
   (this->buffer_).prepend(value); 
}

template <class Type, class PriorityDecorator>
RWPCValBufferBasePrioritized<Type,PriorityDecorator>::~RWPCValBufferBasePrioritized(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT2(RWPCValBufferBasePrioritized,Type,PriorityDecorator,~RWPCValBufferBasePrioritized(void):);
}

template <class Type, class PriorityDecorator>
void
RWPCValBufferBasePrioritized<Type,PriorityDecorator>::write(long priority,const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBasePrioritized,Type,PriorityDecorator,write(long,const Type& value):void);
   RWPCValBufferBase<PriorityDecorator>::write(PriorityDecorator(value,priority));
}

template <class Type, class PriorityDecorator>
RWBoolean
RWPCValBufferBasePrioritized<Type,PriorityDecorator>::tryWrite(long priority,const Type& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBasePrioritized,Type,PriorityDecorator,tryWrite(long,const Type& value):RWBoolean);
   return RWPCValBufferBase<PriorityDecorator>::tryWrite(PriorityDecorator(value,priority));
}

template <class Type, class PriorityDecorator>
RWWaitStatus
RWPCValBufferBasePrioritized<Type,PriorityDecorator>::write(long priority,const Type& value,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBasePrioritized,Type,PriorityDecorator,write(long,const Type& value,unsigned long):RWWaitStatus);
   return RWPCValBufferBase<PriorityDecorator>::write(PriorityDecorator(value,priority),milliseconds);
}

template <class Type, class PriorityDecorator>
void 
RWPCValBufferBasePrioritized<Type,PriorityDecorator>::_writeBack(const PriorityDecorator& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBasePrioritized,Type,PriorityDecorator,_writeBack(const PriorityDecorator&):void);
   // Write at end of list of equal priorities for queue implementation
   if (this->buffer_.isEmpty() || this->buffer_.last().priority_ >= value.priority_) {
      // The queue is empty or the new priority is less than that of the last entry
      // Insert at the end of the list
      this->buffer_.append(value); 
   }
   else if (this->buffer_.first().priority_ < value.priority_) {
      // The new priority is greater than that of the first entry.
      // Insert at the beginning of the list
      this->buffer_.prepend(value);
   }
   else {
      // Start at the beginning of the queue and search for the first
      // entry whose priority is less-than the new priority
      PriorityDecorator test;

      // We want to insert the new element BEFORE the element with the 
      // lesser priority.  Unfortunately, an RWTValSlistIterator can 
      // only insert AFTER the current position, so we must track our 
      // position with two iterators.
      RWTValSlistIterator<PriorityDecorator> iter(this->buffer_),last(this->buffer_);

      // Keep going till we find an entry
      // We don't need to worry about iterating past the end of the list
      // as the first two tests above insure that we will find an entry
      // between the first and last entry...
      ++iter;
      test = iter.key();
      while (test.priority_ >= value.priority_) {
         ++iter;++last;
         test = iter.key();
      }
      last.insertAfterPoint(value);
   }
}

template <class Type, class PriorityDecorator>
void 
RWPCValBufferBasePrioritized<Type,PriorityDecorator>::_writeFront(const PriorityDecorator& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBasePrioritized,Type,PriorityDecorator,_writeFront(const PriorityDecorator&):void);
   // Write at beginning of list of equal priorities for stack implementation
   if (this->buffer_.isEmpty() || this->buffer_.last().priority_ >= value.priority_) {
      // The queue is empty or the new priority is less than that of the last entry
      // Insert at the end of the list
      this->buffer_.append(value); 
   }
   else if (this->buffer_.first().priority_ < value.priority_) {
      // The new priority is greater than that of the first entry.
      // Insert at the beginning of the list
      this->buffer_.prepend(value);
   }
   else {
      // Start at the beginning of the stack and search for the first
      // entry whose priority is equal to the new priority
      PriorityDecorator test;

      // We want to insert the new element BEFORE the element with the 
      // equal or lesser priority.  Unfortunately, an RWTValSlistIterator can 
      // only insert AFTER the current position, so we must track our 
      // position with two iterators.
      RWTValSlistIterator<PriorityDecorator> iter(this->buffer_),last(this->buffer_);

      // Keep going till we find an entry
      // We don't need to worry about iterating past the end of the list
      // as the first two tests above insure that we will find an entry
      // between the first and last entry...
      ++iter;
      test = iter.key();
      while (test.priority_ > value.priority_) {
         ++iter;++last;
         test = iter.key();
      }
      last.insertAfterPoint(value);
   }
}

template <class Type>
void 
RWPCValQueuePrioritized<Type>::_write(const RWPriorityDecorator<Type>& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValQueuePrioritized,Type,_write(const RWPriorityDecorator<Type>&):void);
   _writeBack(value);
}

template <class Type>
void 
RWPCValStackPrioritized<Type>::_write(const RWPriorityDecorator<Type>& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValStackPrioritized,Type,_write(const RWPriorityDecorator<Type>&):void);
   _writeFront(value);
}

template <class Type, class GuardAndPriorityDecorator>
RWPCValBufferBaseGuardedPrioritized<Type,GuardAndPriorityDecorator>::~RWPCValBufferBaseGuardedPrioritized(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuardedPrioritized,Type,GuardAndPriorityDecorator,~RWPCValBufferBaseGuardedPrioritized(void));
}

template <class Type, class GuardAndPriorityDecorator>
void
RWPCValBufferBaseGuardedPrioritized<Type,GuardAndPriorityDecorator>::write(long priority,const Type& value,const RWFunctorR0<RWBoolean>& guard)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuardedPrioritized,Type,GuardAndPriorityDecorator,write(long,const Type& value,const RWFunctorR0<RWBoolean>&):void);
   RWPCValBufferBase<GuardAndPriorityDecorator>::write(GuardAndPriorityDecorator(value,guard,priority));
}

template <class Type, class GuardAndPriorityDecorator>
RWBoolean
RWPCValBufferBaseGuardedPrioritized<Type,GuardAndPriorityDecorator>::tryWrite(long priority, const Type& value,const RWFunctorR0<RWBoolean>& guard)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuardedPrioritized,Type,GuardAndPriorityDecorator,tryWrite(long,const Type& value,const RWFunctorR0<RWBoolean>&):RWBoolean);
   return RWPCValBufferBase<GuardAndPriorityDecorator>::tryWrite(GuardAndPriorityDecorator(value,guard,priority));
}

template <class Type, class GuardAndPriorityDecorator>
RWWaitStatus
RWPCValBufferBaseGuardedPrioritized<Type,GuardAndPriorityDecorator>::write(long priority, const Type& value,const RWFunctorR0<RWBoolean>& guard,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWPCValBufferBaseGuardedPrioritized,Type,GuardAndPriorityDecorator,write(long,const Type& value,const RWFunctorR0<RWBoolean>&,unsigned long):RWWaitStatus);
   return RWPCValBufferBase<GuardAndPriorityDecorator>::write(GuardAndPriorityDecorator(value,guard,priority),milliseconds);
}

template <class Type>
void 
RWPCValQueueGuardedPrioritized<Type>::_write(const RWGuardAndPriorityDecorator<Type>& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValQueueGuardedPrioritized,Type,_write(const RWGuardAndPriorityDecorator<Type>&):void);
   _writeBack(value);
}

template <class Type>
void 
RWPCValStackGuardedPrioritized<Type>::_write(const RWGuardAndPriorityDecorator<Type>& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCValStackGuardedPrioritized,Type,_write(const RWGuardAndPriorityDecorator<Type>&):void);
   _writeFront(value);
}

template <class Type>
RWPCPtrBufferBase<Type>::~RWPCPtrBufferBase(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,~RWPCPtrBufferBase(void):);
}

template <class Type>
size_t
RWPCPtrBufferBase<Type>::_entries(void) const
        RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,_entries(void) const:size_t);
   return buffer_.entries();
}

/* Deprecated */
template <class Type>
RWBoolean
RWPCPtrBufferBase<Type>::isReadable(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,isReadable(void) const:RWBoolean);
   return buffer_.entries() > 0;
}

/* Deprecated */
template <class Type>
RWBoolean
RWPCPtrBufferBase<Type>::isWriteable(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,isWriteable(void) const:RWBoolean);
   if (0 == maxEntries_)
      return TRUE;
   return buffer_.entries() < maxEntries_;
}

template <class Type>
Type*
RWPCPtrBufferBase<Type>::read(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,read(void):Type*);
   LockGuard lock(monitor());

   // Keep looping until there is something to read or the buffer is closed...
   while(!_canRead() && this->isOpen_) {

      // Each time we execute within this while-loop, we will either invoke 
      // the callback or wait for a write, but cannot do both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for 
      // a write!

      // Is this the first thread to find the buffer empty since the last
      // write or callback registration? Is there a valid functor to invoke?

      if (!this->hasInvokedEmptyCallback_ && 
          this->onEmptyCallback_.isValid()) {

         // Yes, this thread is the first reader thread to find the buffer
         // empty since the last write or callback registration - it will need
         // to invoke the callback functor.
         this->hasInvokedEmptyCallback_ = TRUE;

         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onEmptyCallback_;

         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canRead() == TRUE:

         //  The buffer may have exited the empty state as a result of write 
         //  operations, so this thread will no longer need to wait for a 
         //  write.

         //  isOpen_ == FALSE && _canRead() == FALSE

         //  The buffer has exited the open state and is now closed, so unless
         //  the buffer contains entries that this thread can read, the thread 
         //  must not be allowed to wait for a write, since writes will not be 
         //  allowed while the buffer is closed, so this method must exit with 
         //  an exception.

         //  hasInvokedEmptyCallback_ == FALSE;

         //  The buffer may have exited and reentered the empty state as a 
         //  result of some combination of read and write operations, but the
         //  empty callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onEmptyCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.
      }
      else {

         // The buffer is empty and this is not the first thread to
         // find the buffer is empty, or there is no callback to invoke.
         this->waitingReaders_++;

         try {
            // Unlock the buffer and wait for a write operation, a new empty 
            // callback registration, a close operation, or an interrupt.
            notEmpty_.wait();
            this->waitingReaders_--;
         }
         catch(...) {
            this->waitingReaders_--;
            throw;
         }
      }
   }

   Type* result;
   
   if (_canRead()) {
      result = _read();
      if (_entries() < this->maxEntries_) {
         // Indicate that the queue has exited the full state
         this->hasInvokedFullCallback_ = FALSE;
         // If there are writers waiting, wake one up so it can write.
         if (this->waitingWriters_ > 0) notFull_.signal();
      }
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   return result;
}

template <class Type>
RWBoolean
RWPCPtrBufferBase<Type>::tryRead(Type*& result)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,tryRead(Type*&):RWBoolean);
   LockGuard lock(monitor());
   if (_canRead() && this->isOpen_) {
      result = _read();
      if (_entries() < this->maxEntries_) {
         // Indicate that the queue has left the full state
         this->hasInvokedFullCallback_ = FALSE;
         // If there are writers waiting, wake one up so it can write.
         if (this->waitingWriters_ > 0) notFull_.signal();
      }
      return TRUE;
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   else
      return FALSE;
}

template <class Type>
RWWaitStatus
RWPCPtrBufferBase<Type>::read(Type*& result,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,read(Type*&,unsigned long):RWWaitStatus);

   RWTimer  timer;
   unsigned long elapsedMilliseconds; 
   RWWaitStatus waitStatus = RW_THR_COMPLETED;     

   LockGuard lock(monitor());

   // Keep looping until there is something to read, the buffer is closed,
   // or the operation times-out...
   
   while(!_canRead() && this->isOpen_ && RW_THR_COMPLETED == waitStatus) {
   
      // Reset and re-start the timer...
      timer.reset();
      timer.start();
      
      // Each time we execute within this while-loop, we will either invoke 
      // the callback or wait for a write, but cannot do both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for 
      // a write!

      // Is this the first thread to find the buffer empty since the last
      // write or callback registration?  Is there a valid functor to invoke?

      if (!this->hasInvokedEmptyCallback_ && 
          this->onEmptyCallback_.isValid()) {

         // Yes, this thread is the first reader thread to find the buffer
         // empty since the last write or callback registration - it will need
         // to invoke the callback functor.
         this->hasInvokedEmptyCallback_ = TRUE;
         
         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onEmptyCallback_;
         
         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());
         
         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canRead() == TRUE:

         //  The buffer may have exited the empty state as a result of write 
         //  operations, so this thread will no longer need to wait for an 
         //  entry to be written to the buffer.

         //  isOpen_ == FALSE && _canRead() == FALSE

         //  The buffer has exited the open state and is now closed, so unless
         //  the buffer contains entries that this thread can read, the thread 
         //  must not be allowed to wait for a write, since writes will not be 
         //  allowed while the buffer is closed. 

         //  hasInvokedEmptyCallback_ == FALSE;

         //  The buffer may have exited and re-entered the empty state as a 
         //  result of some combination of read and write operations, but the
         //  empty callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onEmptyCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.

         //  elapsed time >= milliseconds

         //  The time-out period may have elapsed.
      }
      else {
         // The buffer is empty and this is not the first thread to
         // find the buffer is empty, or there is no callback to invoke.
         this->waitingReaders_++;
         try {
            // Unlock the buffer and wait for a write operation, a new empty 
            // callback registration, a close operation, or an interrupt.
            waitStatus = notEmpty_.wait(milliseconds);
            this->waitingReaders_--;
         }
         catch(...) {
            this->waitingReaders_--;
            throw;
         }
      }
      // Has the operation timed-out?
      if (RW_THR_COMPLETED == waitStatus) {

         // No, the thread either hasn't waited yet, or the thread did wait 
         // and received a signal to indicate that a write has occurred, a 
         // new empty callback functor has been registered, or that the buffer 
         // has been closed.

         // Is the buffer still empty and open? Does the thread still need to wait?
         if (!_canRead() && this->isOpen_) {

            // The buffer is empty and open - the thread still needs to wait.
            // Determine how much time has elapsed, and use that to calculate 
            // how much time, if any, remains from the original time-out period.
            timer.stop();
            elapsedMilliseconds = (unsigned long)(timer.elapsedTime()/1000.0);
            if (elapsedMilliseconds < milliseconds) {
               // The operation has not timed-out.
               // Calculate the time remaining before the operation must time-out.
               milliseconds -= elapsedMilliseconds;
            }
            else {
               // The operation has timed-out.
               waitStatus = RW_THR_TIMEOUT;
            }
         }
      }
   }
   if (_canRead()) {
      result = _read();
      waitStatus = RW_THR_COMPLETED;
      if (_entries() < this->maxEntries_) {
         // Indicate that the queue has exited the full state
         this->hasInvokedFullCallback_ = FALSE;
         // If there are writers waiting, wake one up so it can write.
         if (this->waitingWriters_ > 0) notFull_.signal();
      }
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   return waitStatus;
}

template <class Type>
Type*
RWPCPtrBufferBase<Type>::peek(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,peek(void):Type*);
   LockGuard lock(monitor());

   // Keep looping until there is something to read or the buffer is closed...
   while(!_canRead() && this->isOpen_) {

      // Each time we execute within this while-loop, we will either invoke 
      // the callback or wait for a write, but cannot do both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for 
      // a write!

      // Is this the first thread to find the buffer empty since the last
      // write or callback registration? Is there a valid functor to invoke?

      if (!this->hasInvokedEmptyCallback_ && 
          this->onEmptyCallback_.isValid()) {

         // Yes, this thread is the first reader thread to find the buffer
         // empty since the last write or callback registration - it will need
         // to invoke the callback functor.
         this->hasInvokedEmptyCallback_ = TRUE;

         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onEmptyCallback_;

         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canRead() == TRUE:

         //  The buffer may have exited the empty state as a result of write 
         //  operations, so this thread will no longer need to wait for a 
         //  write.

         //  isOpen_ == FALSE && _canRead() == FALSE

         //  The buffer has exited the open state and is now closed, so unless
         //  the buffer contains entries that this thread can read, the thread 
         //  must not be allowed to wait for a write, since writes will not be 
         //  allowed while the buffer is closed, so this method must exit with 
         //  an exception.

         //  hasInvokedEmptyCallback_ == FALSE;

         //  The buffer may have exited and reentered the empty state as a 
         //  result of some combination of read and write operations, but the
         //  empty callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onEmptyCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.
      }
      else {

         // The buffer is empty and this is not the first thread to
         // find the buffer is empty, or there is no callback to invoke.
         this->waitingReaders_++;

         try {
            // Unlock the buffer and wait for a write operation, a new empty 
            // callback registration, a close operation, or an interrupt.
            notEmpty_.wait();
            this->waitingReaders_--;
         }
         catch(...) {
            this->waitingReaders_--;
            throw;
         }
      }
   }

   Type* result;   
   if (_canRead()) {
      result = _peek();
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   return result;
}

template <class Type>
RWBoolean
RWPCPtrBufferBase<Type>::tryPeek(Type*& value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,tryPeek(Type*&):RWBoolean);
   LockGuard lock(monitor());
   RWBoolean result;
   if (_canRead() && this->isOpen_) {
      value = _peek();
      result = TRUE;
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   else {
      result = FALSE;
   }
   return result;
}

template <class Type>
RWWaitStatus
RWPCPtrBufferBase<Type>::peek(Type*& result,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,peek(Type*&,unsigned long):RWWaitStatus);

   RWTimer  timer;
   unsigned long elapsedMilliseconds; 
   RWWaitStatus waitStatus = RW_THR_COMPLETED;     

   LockGuard lock(monitor());

   // Keep looping until there is something to read, the buffer is closed,
   // or the operation times-out...
   
   while(!_canRead() && this->isOpen_ && RW_THR_COMPLETED == waitStatus) {
   
      // Reset and re-start the timer...
      timer.reset();
      timer.start();
      
      // Each time we execute within this while-loop, we will either invoke 
      // the callback or wait for a write, but cannot do both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for 
      // a write!

      // Is this the first thread to find the buffer empty since the last
      // write or callback registration?  Is there a valid functor to invoke?

      if (!this->hasInvokedEmptyCallback_ && 
          this->onEmptyCallback_.isValid()) {

         // Yes, this thread is the first reader thread to find the buffer
         // empty since the last write or callback registration - it will need
         // to invoke the callback functor.
         this->hasInvokedEmptyCallback_ = TRUE;
         
         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onEmptyCallback_;
         
         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());
         
         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canRead() == TRUE:

         //  The buffer may have exited the empty state as a result of write 
         //  operations, so this thread will no longer need to wait for an 
         //  entry to be written to the buffer.

         //  isOpen_ == FALSE && _canRead() == FALSE

         //  The buffer has exited the open state and is now closed, so unless
         //  the buffer contains entries that this thread can read, the thread 
         //  must not be allowed to wait for a write, since writes will not be 
         //  allowed while the buffer is closed. 

         //  hasInvokedEmptyCallback_ == FALSE;

         //  The buffer may have exited and re-entered the empty state as a 
         //  result of some combination of read and write operations, but the
         //  empty callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onEmptyCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.

         //  elapsed time >= milliseconds

         //  The time-out period may have elapsed.
      }
      else {
         // The buffer is empty and this is not the first thread to
         // find the buffer is empty, or there is no callback to invoke.
         this->waitingReaders_++;
         try {
            // Unlock the buffer and wait for a write operation, a new empty 
            // callback registration, a close operation, or an interrupt.
            waitStatus = notEmpty_.wait(milliseconds);
            this->waitingReaders_--;
         }
         catch(...) {
            this->waitingReaders_--;
            throw;
         }
      }
      // Has the operation timed-out?
      if (RW_THR_COMPLETED == waitStatus) {

         // No, the thread either hasn't waited yet, or the thread did wait 
         // and received a signal to indicate that a write has occurred, a 
         // new empty callback functor has been registered, or that the buffer 
         // has been closed.

         // Is the buffer still empty and open? Does the thread still need to wait?
         if (!_canRead() && this->isOpen_) {

            // The buffer is empty and open - the thread still needs to wait.
            // Determine how much time has elapsed, and use that to calculate 
            // how much time, if any, remains from the original time-out period.
            timer.stop();
            elapsedMilliseconds = (unsigned long)(timer.elapsedTime()/1000.0);
            if (elapsedMilliseconds < milliseconds) {
               // The operation has not timed-out.
               // Calculate the time remaining before the operation must time-out.
               milliseconds -= elapsedMilliseconds;
            }
            else {
               // The operation has timed-out.
               waitStatus = RW_THR_TIMEOUT;
            }
         }
      }
   }
   if (_canRead()) {
      result = _peek();
      waitStatus = RW_THR_COMPLETED;
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is empty and is closed.
      throw RWTHRClosedException();
   }
   return waitStatus;
}

template <class Type>
void
RWPCPtrBufferBase<Type>::write(Type* value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,write(Type*):void);
   LockGuard lock(this->monitor());

   // Keep looping until there is room to write or the buffer is closed...
   while(!_canWrite() && this->isOpen_) {

      // Each time we execute within this while-loop, we will either invoke 
      // the full callback or wait for room to write, but not both.
      
      // We must not do both because the callback may directly or indirectly 
      // change the state of the buffer, eliminating the need to wait for room!

      // Is this the first thread to find the buffer full since the last read,
      // capacity change, or callback registration?  
      // Is there a valid functor to invoke?

      if (!this->hasInvokedFullCallback_ && 
          this->onFullCallback_.isValid()) {

         // Yes, this thread is the first writer thread to find the buffer
         // full since the last time the buffer was less than full, or 
         // since the last callback registration - it will need to invoke the 
         // callback functor.
         this->hasInvokedFullCallback_ = TRUE;

         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onFullCallback_;

         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canWrite() == TRUE:

         //  The buffer may have exited the full state as a result of read 
         //  operations or a capacity change, or the buffer may have been 
         //  closed, so this thread will no longer need to wait for any more 
         //  reads, and if the buffer has been closed, the method must exit 
         //  with an exception.

         //  hasInvokedFullcallback_ == FALSE;

         //  The buffer may have exited and reentered the full state as a 
         //  result of some combination of read and write operations, but the
         //  full callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onFullCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.
      }
      else {

         // The buffer is full and this is not the first thread to
         // find the buffer is full, or there is no callback to invoke.

         this->waitingWriters_++;
         try {
            this->notFull_.wait();
            this->waitingWriters_--;
         }
         catch(...) {
            this->waitingWriters_--;
            throw;
         }
      }
   }
   if (this->_canWrite()) {
      _write(value);
      // Indicate that the queue has exited the empty state
      this->hasInvokedEmptyCallback_ = FALSE;
      // If there are readers waiting, wake one up so it can read.
      if (this->waitingReaders_ > 0) this->notEmpty_.signal();
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is closed.
      throw RWTHRClosedException();
   }
}

template <class Type>
RWBoolean
RWPCPtrBufferBase<Type>::tryWrite(Type* value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,tryWrite(Type*):RWBoolean);
   LockGuard lock(this->monitor());
   RWBoolean result;
   if (this->_canWrite() && this->isOpen_) {
      _write(value);
      // Indicate that the queue has exited the empty state
      this->hasInvokedEmptyCallback_ = FALSE;
      // If there are readers waiting, wake one up so it can read.
      if (this->waitingReaders_ > 0) this->notEmpty_.signal();
      result = TRUE;
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is closed.
      throw RWTHRClosedException();
   }
   else {
      result = FALSE;
   }
   return result;
}

template <class Type>
RWWaitStatus
RWPCPtrBufferBase<Type>::write(Type* value,unsigned long milliseconds)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,write(Type*,unsigned long):RWWaitStatus);

   RWTimer  timer;
   unsigned long elapsedMilliseconds; 
   RWWaitStatus waitStatus = RW_THR_COMPLETED;     

   LockGuard lock(this->monitor());

   // Keep looping until there is room, the buffer is closed, or until the
   // operation times out...

   while(!this->_canWrite() && this->isOpen_ && RW_THR_COMPLETED == waitStatus) {
      
      // Reset and restart the timer
      timer.reset();
      timer.start();

      // Each time we execute within this while-loop, we will either invoke
      // the full callback or wait for room to write, but not both.

      // We must not do both because the callback may directly or indirectly
      // change the state of the buffer, eliminating the need to wait for room!

      // Is this the first thread to find the buffer full since the last read,
      // capacity change, or callback registration?  
      // Is there a valid functor to invoke?

      if (!this->hasInvokedFullCallback_ && 
          this->onFullCallback_.isValid()) {

         // Yes, this thread is the first writer thread to find the buffer
         // full since the last time the buffer was less than full, or 
         // since the last callback registration - it will need to invoke the 
         // callback functor.
         this->hasInvokedFullCallback_ = TRUE;

         // Save the callback functor in a local variable 
         RWFunctor0 callback = this->onFullCallback_;

         // Temporarily unlock the buffer so the callback can access the buffer
         UnlockGuard unlock(monitor());

         // Invoke the callback.
         callback();
         
         // What important state changes might have occurred while the buffer 
         // was unlocked, and what are the consequences of these changes?

         //  _canWrite() == TRUE:

         //  The buffer may have exited the full state as a result of read 
         //  operations or a capacity change, or the buffer may have been 
         //  closed, so this thread will no longer need to wait for any more 
         //  reads, and if the buffer has been closed, the method must exit 
         //  with an exception.

         //  hasInvokedFullcallback_ == FALSE;

         //  The buffer may have exited and reentered the full state as a 
         //  result of some combination of read and write operations, but the
         //  full callback has not yet been invoked, so this thread must
         //  re-invoke the callback.

         //  previous != onFullCallback_

         //  The callback functor may have been changed, so the thread must
         //  use the new functor if the callback is re-invoked.

         //  elapsed time >= milliseconds

         //  The time-out period may have elapsed.
      }
      else {

         // The buffer is full and this is not the first thread to
         // find the buffer is full, or there is no callback to invoke.

         this->waitingWriters_++;
         try {
            waitStatus = (this->notFull_).wait(milliseconds);
            this->waitingWriters_--;
         }
         catch(...) {
            this->waitingWriters_--;
            throw;
         }
      }
      // Has the operation timed-out?
      if (RW_THR_COMPLETED == waitStatus) {

         // No, the thread either hasn't waited yet, or the thread did wait
         // and received a signal to indicate that the buffer is no longer full,
         // a new full callback functor has been registered, or that the buffer
         // has been closed.

         // Is the buffer still full and open? Does the thread still need to wait?
         if (!this->_canWrite() && this->isOpen_) {

            // The buffer is full and open - the thread still needs to wait.
            // Determine how much time has elapsed, and use that to calculate
            // how much time, if any, remains from the original time-out period.
            timer.stop();
            elapsedMilliseconds = (unsigned long)(timer.elapsedTime()/1000.0);
            if (elapsedMilliseconds < milliseconds) {
               // The operation has not timed-out.
               // Calculate the time remaining before the operation must time-out.
               milliseconds -= elapsedMilliseconds;
            }
            else {
               // The operation has timed-out.
               waitStatus = RW_THR_TIMEOUT;
            }
         }
      }
   }
   // Can we write, or did we time-out?
   if (this->_canWrite()) {
      _write(value);
      waitStatus = RW_THR_COMPLETED;
      // Indicate that the queue has exited the empty state.
      this->hasInvokedEmptyCallback_ = FALSE;
      // If there are readers waiting, wake one up so it can read.
      if (this->waitingReaders_ > 0) (this->notEmpty_).signal();
   }
   else if (!this->isOpen_) {
      // Throw an exception to indicate that the buffer is closed.
      throw RWTHRClosedException();
   }
   return waitStatus;
}

template <class Type>
Type* 
RWPCPtrBufferBase<Type>::_read(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,_read(void):Type*);
   return buffer_.removeFirst();
}

template <class Type>
Type* 
RWPCPtrBufferBase<Type>::_peek(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,_peek(void):Type*);
   return buffer_.first();
}

template <class Type>
void
RWPCPtrBufferBase<Type>::_flush(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrBufferBase,Type,_flush(void):void);
   buffer_.clear();
   // Indicate that the queue has exited the full state
   this->hasInvokedFullCallback_ = FALSE;
   // If there are writers waiting, wake one up so it can write.
   if (this->waitingWriters_ > 0) notFull_.signal();
}

template <class Type>
void 
RWPCPtrQueue<Type>::_write(Type* value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrQueue,Type,_write(Type*):void);
   // Write at end of list for queue implementation
   (this->buffer_).append(value); 
}

template <class Type>
void 
RWPCPtrStack<Type>::_write(Type* value)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWPCPtrStack,Type,_write(Type*):void);
   // Write at beginning of list for stack implementation
   (this->buffer_).prepend(value); 
}


#endif // __RWTHRPRODCONS_CC__

