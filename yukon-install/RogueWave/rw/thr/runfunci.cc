#if !defined(__RWTHRRUNFUNCI_CC__)
#  define __RWTHRRUNFUNCI_CC__
/*****************************************************************************
 *
 * runfunci.cc
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

runfunci.cc - Out-of-line function definitions for:

   RWRunnableIOUFunctionImp - RWFunctorR0<R> based runnable class.

See Also:

   runfunci.h - Class declarations.

******************************************************************************/

#  if !defined(__RWTHRRUNFUNCI_H__)
#    include<rw/thr/runfunci.h>
#  endif

#  if !defined(__RWTHRTHRMSG_H__)
#     include <rw/thr/thrmsg.h>
#  endif

#  if !defined(__RWTHRCANCEL_H__)
#     include <rw/thr/cancel.h>
#  endif

#  if !defined(__RWTHRESCROTHR_H__)
#     include <rw/thr/escrothr.h>
#  endif


RW_THR_IMPLEMENT_TRACEABLE_T1(RWRunnableIOUFunction,Return)
RW_THR_IMPLEMENT_TRACEABLE_T1(RWRunnableIOUFunctionImp,Return)

template <class Return>
// protected
RWRunnableIOUFunctionImp<Return>::RWRunnableIOUFunctionImp(const RWIOUEscrow<Return>& escrow)
   RWTHRTHROWSANY
   :
      escrow_(escrow)
{
   RWTHRTRACEMF(RWRunnableIOUFunctionImp<Return>,RWRunnableIOUFunctionImp(const RWIOUEscrow<Return>&));
}
 
template <class Return>
// protected
RWRunnableIOUFunctionImp<Return>::RWRunnableIOUFunctionImp(const RWIOUEscrow<Return>& escrow,
                                                           const RWFunctorR0<Return>& functor) 
   RWTHRTHROWSANY
   :
      escrow_(escrow),
      functor_(functor)
{
   RWTHRTRACEMF(RWRunnableIOUFunctionImp<Return>,RWRunnableIOUFunctionImp(const RWIOUEscrow<Return>&,const RWFunctorR0<Return>&));
}

template <class Return>
RWFunctorR0<Return>
RWRunnableIOUFunctionImp<Return>::getFunctor(void) const
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableIOUFunctionImp<Return>,getFunctor(void) const:RWFunctorR0<Return>);
   static const char msgHeader[] = "RWRunnableIOUFunctionImp<Return>::getFunctor - ";

   try {
      // Lock the thread object
      LockGuard lock(monitor());
      return functor_;
   }
   catch(RWxmsg& emsg) {
      throw RWTHRInternalError(RWCString(msgHeader)+emsg.why());
   }
   catch(...) {
      throw RWTHRInternalError(RWCString(msgHeader)+RW_THR_Unrecognized_Exception);
   }
}

template <class Return>
RWIOUResult<Return>
RWRunnableIOUFunctionImp<Return>::result(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableIOUFunctionImp<Return>,result(void):RWIOUResult<Return>);
   return escrow_;
}

template <class Return>
// protected
// virtual
void
RWRunnableIOUFunctionImp<Return>::run(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableIOUFunctionImp<Return>,run(void):void);
   RWFunctorR0<Return> functor;
   RWIOUEscrow<Return> escrow;
   {
      LockGuard lock(monitor());
      // Use second handle so that we don't have to worry about changes to 
      // the functor_ member while executing the statements below.
      // (we don't want to lock the thread object across the functor invocation!)
      functor = functor_;
      
      // Is this escrow all used up (already closed or aborted)?
      if (!escrow_.closeable())
         // Get a new escrow since the current escrow has already been used.
         // (Perhaps because this runnable has been restarted...)
         escrow_ = escrow_.newInstance();

      // Make a local copy of the escrow handle so that the escrow
      // instance can't be changed in mid-stream...
      escrow = escrow_;
   }

   RWTHRASSERT(functor.isValid()); // USAGE ERROR - Functor handle is empty!
   RWTHRASSERT(escrow.isValid()); // USAGE ERROR - Escrow handle is empty!
 
   if (functor.isValid()) {
      
      // Create a temporary for the result
      Return result;
      
      try {
         result = functor();
         if (escrow.isValid()) 
            escrow.close(result);
      }
      catch (RWCancellation&) {
         // A RWCancellation exception indicates that the thread was canceled.
         // Disable abort reporting from cancellation object so we can destroy it...
         if (escrow.isValid()) 
            escrow.setException(RWTHROperationCanceled()); // ???
         throw; // Rethrow to caller
      }
      catch (RWTHRxmsg& msg) {
         // We got some other kind of thread-safe exception,
         // Store the exception so we can rethrow it when the IOU is evaluated.
         if (escrow.isValid()) 
            escrow.setException(msg);
      }
      catch (RWxmsg& msg) {
         // We got some other kind of exception,
         // Store the exception to throw later
         if (escrow.isValid()) 
            escrow.setException(msg.why());
      }
      catch (...) {
         // We got some other kind of exception that we don't recognize
         // Store the exception to throw later
         if (escrow.isValid()) 
            escrow.setException(RWTHRxmsg(RW_THR_Unrecognized_Exception));
      }         
   }
   else {
      // Set exception on escrow. Close with an exception so clients can
      // be informed that the runnable was started with an invalid functor. 
      if (escrow.isValid()) 
         escrow.setException(RWTHRInvalidPointer(RW_THR_Invalid_Runnable_Functor));
   }
}

template <class Return>
void
RWRunnableIOUFunctionImp<Return>::setFunctor(const RWFunctorR0<Return>& functor)
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableIOUFunctionImp<Return>,setFunctor(const RWFunctorR0<Return>&));
   static const char msgHeader[] = "RWRunnableIOUFunctionImp<Return>::setFunctor - ";

   try {
      // Lock the thread object
      LockGuard lock(monitor());
      functor_ = functor;
   }
   catch(RWxmsg& emsg) {
      throw RWTHRInternalError(RWCString(msgHeader)+emsg.why());
   }
   catch(...) {
      throw RWTHRInternalError(RWCString(msgHeader)+RW_THR_Unrecognized_Exception);
   }
}

template <class Return>
void
RWRunnableIOUFunctionImp<Return>::setIOUEscrow(const RWIOUEscrow<Return>& escrow)
   RWTHRTHROWS1(RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableIOUFunctionImp<Return>,setIOUEscrow(const RWIOUEscrow<Return>&));
   static const char msgHeader[] = "RWRunnableIOUFunctionImp<Return>::setIOUEscrow - ";

   try {
      // Lock the thread object
      LockGuard lock(monitor());
      escrow_ = escrow;
   }
   catch(RWxmsg& emsg) {
      throw RWTHRInternalError(RWCString(msgHeader)+emsg.why());
   }
   catch(...) {
      throw RWTHRInternalError(RWCString(msgHeader)+RW_THR_Unrecognized_Exception);
   }
}

/*****************************************************************************/

template <class Return>
RWIOUResult<Return>
RWRunnableIOUFunction<Return>::operator()(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableIOUFunction<Return>,operator()(void) const:RWIOUResult<Return>);
   return ((RWRunnableIOUFunctionImp<Return>&)body()).result();
}
         
template <class Return>
RWFunctorR0<Return>
RWRunnableIOUFunction<Return>::getFunctor(void) const
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableIOUFunction<Return>,getFunctor(void) const:RWFunctorR0<Return>);
   return ((RWRunnableIOUFunctionImp<Return>&)body()).getFunctor();
}

template <class Return>
// static
RWRunnableIOUFunction<Return>
RWRunnableIOUFunction<Return>::make(void)
   RWTHRTHROWSANY
{
   RWTHRTRACESMF(RWRunnableIOUFunction<Return>,make(void):RWRunnableIOUFunction<Return>);
   RWIOUEscrow<Return> escrow = RWThreadEscrowImp<Return>::make();
   return new RWRunnableIOUFunctionImp<Return>(escrow);
}

template <class Return>
// static
RWRunnableIOUFunction<Return>
RWRunnableIOUFunction<Return>::make(const RWFunctorR0<Return>& functor)
   RWTHRTHROWSANY
{
   RWTHRTRACESMF(RWRunnableIOUFunction<Return>,make(const RWFunctorR0<Return>&):RWRunnableIOUFunction<Return>);
   RWIOUEscrow<Return> escrow = RWThreadEscrowImp<Return>::make();
   return new RWRunnableIOUFunctionImp<Return>(escrow,functor);
}

template <class Return>
// static
RWRunnableIOUFunction<Return>
RWRunnableIOUFunction<Return>::make(const RWIOUEscrow<Return>& escrow,
                                    const RWFunctorR0<Return>& functor)
   RWTHRTHROWSANY
{
   RWTHRTRACESMF(RWRunnableIOUFunction<Return>,make(const RWIOUEscrow<Return>&, const RWFunctorR0<Return>&):RWRunnableIOUFunction<Return>);
   return new RWRunnableIOUFunctionImp<Return>(escrow,functor);
}

template <class Return>
RWIOUResult<Return>
RWRunnableIOUFunction<Return>::result(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableIOUFunction<Return>,result(void) const:RWIOUResult<Return>);
   return ((RWRunnableIOUFunctionImp<Return>&)body()).result();
}

#endif // __RWTHRRUNFUNCI_H__

