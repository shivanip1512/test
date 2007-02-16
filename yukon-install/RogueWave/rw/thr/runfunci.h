#if !defined(__RWTHRRUNFUNCRI_H__)
#  define __RWTHRRUNFUNCRI_H__
/*****************************************************************************
 *
 * runfunci.h 
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

runfunci.h - Contains:

   Class declarations for:

      RWRunnableIOUFunction<Return> - Handle for RWRunnableIOUFunctionImp
      instance.

      RWRunnableIOUFunctionImp<Return> - RWFunctorR0-based 
      runnable class that uses IOUs to capture return values.

      The RWRunableIOUFunction class is a synchronous runnable that when started, 
      invokes a RWFunctorR0 instance.  The runnable attempts to use an IOU 
      escrow instance to capture the results of that invocation.  The 
      RWFunctorR0 instance allows any function that conforms to the 
      RWFunctorR0 family signature to be executed, and the use of an IOU 
      instance allows other threads that maintain a handle to that instance
      to retrieve the result of the operation at any time in the future.

      The 'Return' template parameter may not be a type reference because an
      instance of the Return type must be created within the IOU escrow
      object.  This instance is used for storing the result of the functor 
      invocation so that it may be retrieved later.  Since a reference may not 
      be constructed without initialization, and the since the value we would 
      like to initialize the reference with isn't available until the functor 
      is invoked, we can't support use of references as return values with 
      this class.

      >This class may only be used with functions that return a result BY-VALUE!<

   Template function definitions:

      The following template functions construct a runnable object
      These functions construct a runnable that, when started, executes the 
      specified function using the specified argument values (if any).
      They return a RWRunnable instance that references the runnable object.

      RWRunnableIOUFunction rwMakeRunnableIOUFunction(R (*function)(void))
      RWRunnableIOUFunction rwMakeRunnableIOUFunction(R (*function)(A1),
                                                      AA1 a1)
      RWRunnableIOUFunction rwMakeRunnableIOUFunction(R (*function)(A1,A2),
                                                      AA1 a1,
                                                      AA2 a2)
      RWRunnableIOUFunction rwMakeRunnableIOUFunction(R (*function)(A1,A2,A3),
                                                      AA1 a1,
                                                      AA2 a2,
                                                      AA3 a3)

      RWRunnableIOUFunction rwMakeRunnableIOUFunction(Callee& callee,
                                                      R (Callee::*function)(void))
      RWRunnableIOUFunction rwMakeRunnableIOUFunction(Callee& callee,
                                                      R (Callee::*function)(A1),
                                                      AA1 a1)
      RWRunnableIOUFunction rwMakeRunnableIOUFunction(Callee& callee,
                                                      R (Callee::*function)(A1,A2),
                                                      AA1 a1,
                                                      AA2 a2)
      RWRunnableIOUFunction rwMakeRunnableIOUFunction(Callee& callee,
                                                      R (Callee::*function)(A1,A2,A3),
                                                      AA1 a1,
                                                      AA2 a2,
                                                      AA3 a3)
   
   Macro definitions:

      The following macros construct a runnable object resulting in a 
      RWRunnable instance that may be used as the right-hand value in an 
      assignment or function call:
   
    For global functions...
    
      rwMakeRunnableIOUFunctionG(R,function) 
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(void).
    
      rwMakeRunnableIOUFunctionGA1(R,function,A1,a1)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1).
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
    
      rwMakeRunnableIOUFunctionGA2(R,function,A1,a1,A2,a2)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1,A2).
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the second argument.
         a2 - is an expression that can be converted to an instance of A2.

      rwMakeRunnableIOUFunctionGA3(R,function,A1,a1,A2,a2,A3,a3)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1,A2,A3).
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the second argument.
         a2 - is an expression that can be converted to an instance of A2.
         A3 - is the type name of the second argument.
         a3 - is an expression that can be converted to an instance of A3.

    For member functions...
    
      rwMakeRunnableIOUFunctionM(Callee,callee,R,function) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(void)
    
      rwMakeRunnableIOUFunctionMA1(Callee,callee,R,function,A1,a1) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(A1)
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.

      rwMakeRunnableIOUFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(A1,A2)
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the first argument.
         a2 - is an expression that can be converted to an instance of A2.

      rwMakeRunnableIOUFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2,A3) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(A1,A2,A3)
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the first argument.
         a2 - is an expression that can be converted to an instance of A2.
         A3 - is the type name of the first argument.
         a3 - is an expression that can be converted to an instance of A3.

See Also:

   runfunci.cpp - Out-of-line function definitions.

   funcr0.h - RWFunctorR0Imp<SR> family of functor classes:
   
******************************************************************************/

#  if !defined(__RWTHRRUNNABLE_H__)
#     include <rw/thr/runnable.h>
#  endif

#  if !defined(__RWTHRFUNCR0_H__)
#     include <rw/thr/funcr0.h>
#  endif

#  if !defined(__RWTHRIOUESCRO_H__)
#     include <rw/thr/iouescro.h>
#  endif

#  if !defined(__RWTHRIOURESLT_H__)
#     include <rw/thr/ioureslt.h>
#  endif

template <class Return>
class RWTHRTExport RWRunnableIOUFunction :
   public RWRunnable {

   RW_THR_DECLARE_TRACEABLE

   public:

      // Construct an empty (invalid) handle
      RWRunnableIOUFunction(void)
         RWTHRTHROWSANY;

      // Construct an external interface handle to the thread object 
      // pointed-to by a second handle (if any).
      RWRunnableIOUFunction(const RWRunnableIOUFunction<Return>& second)
         RWTHRTHROWSANY;

      // Bind this external interface handle to the thread object 
      // pointed-to by a second handle (if any)
      RWRunnableIOUFunction<Return>&
      operator=(const RWRunnableIOUFunction<Return>& second)
         RWTHRTHROWSANY;

      // Get an RWIOUResult handle bound to the escrow instance currently
      // bound to this runnable (if any!)
      RWIOUResult<Return>
      operator()(void) const
         RWTHRTHROWSANY;
         
      // Get a handle bound to the RWFunctorR0 instance, if any, currently 
      // associated with the RWRunnableIOUFunction instance, if any, pointed-to by 
      // this handle.
      RWFunctorR0<Return>
      getFunctor(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Construct a handle to a new RWRunnableIOUFunctionImp instance. 
      // The new runnable will have an invalid functor. The functor will
      // have to be set before the runnable may be started.
      static
      RWRunnableIOUFunction<Return>
      make(void)
         RWTHRTHROWSANY;

      // Construct and return a handle to a new RWRunnableIOUFunctionImp instance.
      // The new runnable instance will use a RWThreadEscrow instance to store the 
      // value produced when the supplied functor instance is executed.
      static
      RWRunnableIOUFunction<Return>
      make(const RWFunctorR0<Return>& functor)
         RWTHRTHROWSANY;

      // Construct a RWRunnableIOUFunctionImp instance that will use the escrow 
      // and functor instances, if any, bound to the specified handles.
      static
      RWRunnableIOUFunction<Return>
      make(const RWIOUEscrow<Return>& escrow,
           const RWFunctorR0<Return>& functor)
         RWTHRTHROWSANY;

      // Get an RWIOUResult handle bound to the escrow instance, if any, 
      // currently bound to this runnable
      RWIOUResult<Return>
      result(void) const
         RWTHRTHROWSANY;

      // Bind the internal functor handle to the functor 
      // instance pointed-to by another handle.
      void
      setFunctor(const RWFunctorR0<Return>& functor)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Use this member to specify a new escrow instance for the runnable to 
      // use in capturing the results of a functor invocation.  Changing the 
      // escrow instance will not have any affect until the next time the 
      // runnable is started.  Each time a RWThread0 runnable is restarted, 
      // it checks its current escrow instance to see if it is valid, and if 
      // so, then checks to see whether or not it  has already been used to 
      // capture a result, an exception, or has been aborted.  If the escrow 
      // is found to be in any of these "redeemable" states, then a new escrow 
      // instance is automatically created to capture the next result.
      void
      setIOUEscrow(const RWIOUEscrow<Return>& escrow)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

   protected:

      // Construct an external interface handle to a RWRunnableIOUFunctionImp 
      // instance cast as a RWRunnableImp* (to avoid a circular dependency between
      // the handle and the body template classes!)
      RWRunnableIOUFunction(RWRunnableImp* imp)
         RWTHRTHROWSANY;
      
};

template <class Return>
class RWTHRTExport RWRunnableIOUFunctionImp :
   public RWRunnableImp {

   RW_THR_DECLARE_TRACEABLE

   friend class RWRunnableIOUFunction<Return>;

   private:

      // Handle for functor instance
      RWFunctorR0<Return>  functor_;

      // Handle for escrow instance
      RWIOUEscrow<Return>  escrow_;   

   protected:

      RWRunnableIOUFunctionImp(const RWIOUEscrow<Return>& escrow)
         RWTHRTHROWSANY;

      RWRunnableIOUFunctionImp(const RWIOUEscrow<Return>& escrow,
                         const RWFunctorR0<Return>& functor)
         RWTHRTHROWSANY;

      // Get a handle bound to the RWFunctorR0 instance, if any, currently 
      // associated with this RWRunnableIOUFunction instance.
      RWFunctorR0<Return>
      getFunctor(void) const
         RWTHRTHROWS1(RWTHRInternalError);

      // Get an RWIOUResult handle bound to the 
      // current escrow instance (if any!)
      RWIOUResult<Return>
      result(void) const
         RWTHRTHROWSANY;

      // Bind the functor handle to the functor 
      // instance pointed-to by another handle.
      void
      setFunctor(const RWFunctorR0<Return>& functor)
         RWTHRTHROWS1(RWTHRInternalError);

      // Use this member to specify a new escrow instance for the runnable to 
      // use in capturing the results of a functor invocation.  Changing the 
      // escrow instance will not have any affect until the next time the 
      // runnable is started.  Each time a RWThread0 runnable is restarted, 
      // it checks its current escrow instance to see if it is valid, and if 
      // so, then checks to see whether or not it  has already been used to 
      // capture a result, an exception, or has been aborted.  If the escrow 
      // is found to be in any of these "redeemable" states, then a new escrow 
      // instance is automatically created to capture the next result.
      void
      setIOUEscrow(const RWIOUEscrow<Return>& escrow)
         RWTHRTHROWS1(RWTHRInternalError);

      // Override RWRunnableImp run() member to call the specified functor, if 
      // any, and use the specified escrow, if any, to capture the results of 
      // the functor invocation.
      virtual
      void
      run(void)
         RWTHRTHROWSANY;

      // Make these protected to prevent compiler generation...

      RWRunnableIOUFunctionImp(const RWRunnableIOUFunctionImp<Return>& second)
         RWTHRTHROWSANY;

      RWRunnableIOUFunctionImp<Return>&
      operator=(const RWRunnableIOUFunctionImp<Return>& second)
         RWTHRTHROWSANY;

};

/*
      
The following template functions and macros construct runnable objects
that, when started, execute the specified function using the specified 
argument values (if any).

Each returns a RWRunnable instance that references the newly created
runnable object.

Example using the functions:
   
   int myFunc(void);

   RWRunnableIOUFunction myRunnable = rwMakeRunnableIOUFunction(myFunc);
   myRunnable.start();
   ...
   // Retrieve the IOU handle from the runnable
   RWIOUResult<int> asyncResult = myRunnable.result();
   ...
   int result = asyncResult; // Redeem the IOU (blocking until the result is ready)
   ...

Example using the macro (for compilers that can't handle the template functions):

   class MyClass {
      public:
         int myFunc(int arg);
   };
   
   :

   MyClass myClass;

   RWRunnableIOUFunction myRunnable = rwMakeRunnableIOUFunctionMA1(MyClass,myClass,int,&MyClass::myFunc,int,10);
   myRunnable.start();
   ...
   // Retrieve the IOU handle from the runnable (using function operator())
   RWIOUResult<int> asyncResult = myRunnable();
   ...
   int result = asyncResult; // Redeem the IOU (blocking until the result is ready)
   ...

*/

#define rwMakeRunnableIOUFunctionG(R,function) \
   RWRunnableIOUFunction<R >::make(RWFunctorR0GImp<R,R >::make(function))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableIOUFunction<R>
rwMakeRunnableIOUFunction(R (*function)(void))
{
   return rwMakeRunnableIOUFunctionG(R,function);
}
#endif

#define rwMakeRunnableIOUFunctionGA1(R,function,A1,a1) \
   RWRunnableIOUFunction<R >::make(RWFunctorR0GA1Imp<R,R,A1 >::make(function,a1))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class AA1>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableIOUFunction<R>
rwMakeRunnableIOUFunction(R (*function)(A1),
                          AA1 a1)
{
   return rwMakeRunnableIOUFunctionGA1(R,function,A1,a1);
}
#  endif

#define rwMakeRunnableIOUFunctionGA2(R,function,A1,a1,A2,a2) \
   RWRunnableIOUFunction<R >::make(RWFunctorR0GA2Imp<R,R,A1,A2 >::make(function,a1,a2))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class A2, class AA1, class AA2>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableIOUFunction<R>
rwMakeRunnableIOUFunction(R (*function)(A1,A2),
                          AA1 a1,
                          AA2 a2)
{
   return rwMakeRunnableIOUFunctionGA2(R,function,A1,a1,A2,a2);
}
#  endif

#define rwMakeRunnableIOUFunctionGA3(R,function,A1,a1,A2,a2,A3,a3) \
   RWRunnableIOUFunction<R >::make(RWFunctorR0GA3Imp<R,R,A1,A2,A3 >::make(function,a1,a2,a3))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class A2, class A3, class AA1, class AA2, class AA3>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableIOUFunction<R>
rwMakeRunnableIOUFunction(R (*function)(A1,A2,A3),
                          AA1 a1,
                          AA2 a2,
                          AA3 a3)
{
   return rwMakeRunnableIOUFunctionGA3(R,function,A1,a1,A2,a2,A3,a3);
}
#  endif

#define rwMakeRunnableIOUFunctionM(Callee,callee,R,function) \
   RWRunnableIOUFunction<R >::make(RWFunctorR0MImp<R,Callee,R >::make(callee,function))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableIOUFunction<R>
rwMakeRunnableIOUFunction(Callee& callee,
                          R (Callee::*function)(void))
{
   return rwMakeRunnableIOUFunctionM(Callee,callee,R,function);
}
#  endif

#define rwMakeRunnableIOUFunctionMA1(Callee,callee,R,function,A1,a1) \
   RWRunnableIOUFunction<R >::make(RWFunctorR0MA1Imp<R,Callee,R,A1 >::make(callee,function,a1))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class AA1>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableIOUFunction<R>
rwMakeRunnableIOUFunction(Callee& callee,
                          R (Callee::*function)(A1),
                          AA1 a1)
{
   return rwMakeRunnableIOUFunctionMA1(Callee,callee,R,function,A1,a1);
}
#  endif

#define rwMakeRunnableIOUFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2) \
   RWRunnableIOUFunction<R >::make(RWFunctorR0MA2Imp<R,Callee,R,A1,A2 >::make(callee,function,a1,a2))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class A2, class AA1, class AA2>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableIOUFunction<R>
rwMakeRunnableIOUFunction(Callee& callee,
                          R (Callee::*function)(A1,A2),
                          AA1 a1,
                          AA2 a2)
{
   return rwMakeRunnableIOUFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2);
}
#  endif

#define rwMakeRunnableIOUFunctionMA3(Callee,callee,R,function,A1,a1,A2,a2,A3,a3) \
   RWRunnableIOUFunction<R >::make(RWFunctorR0MA3Imp<R,Callee,R,A1,A2,A3 >::make(callee,function,a1,a2,a3))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class A2, class A3, class AA1, class AA2, class AA3>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableIOUFunction<R>
rwMakeRunnableIOUFunction(Callee& callee,
                          R (Callee::*function)(A1,A2,A3),
                          AA1 a1,
                          AA2 a2,
                          AA3 a3)
{
   return rwMakeRunnableIOUFunctionMA3(Callee,callee,R,function,A1,a1,A2,a2,A3,a3);
}
#  endif

/*****************************************************************************/

template <class Return>
inline
RWRunnableIOUFunction<Return>::RWRunnableIOUFunction(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableIOUFunction<Return>,RWRunnableIOUFunction(void));
}

template <class Return>
inline
RWRunnableIOUFunction<Return>::RWRunnableIOUFunction(const RWRunnableIOUFunction<Return>& second)
   RWTHRTHROWSANY
   :
      RWRunnable(second)
{
   RWTHRTRACEMF(RWRunnableIOUFunction<Return>,RWRunnableIOUFunction(const RWRunnableIOUFunction<Return>&));
}

template <class Return>
inline
RWRunnableIOUFunction<Return>&
RWRunnableIOUFunction<Return>::operator=(const RWRunnableIOUFunction<Return>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableIOUFunction<Return>,operator=(const RWRunnableIOUFunction<Return>&):RWRunnableIOUFunction<Return>&);
   if (&second != this)
      RWRunnable::operator=(second);
   return *this;
}

template <class Return>
inline
void
RWRunnableIOUFunction<Return>::setFunctor(const RWFunctorR0<Return>& functor)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableIOUFunction<Return>,setFunctor(const RWFunctorR0<Return>&):void);
   ((RWRunnableIOUFunctionImp<Return>&)body()).setFunctor(functor);
}

template <class Return>
// protected
inline
RWRunnableIOUFunction<Return>::RWRunnableIOUFunction(RWRunnableImp* imp)
   RWTHRTHROWSANY
   :
      RWRunnable(imp)
{
   RWTHRTRACEMF(RWRunnableIOUFunction<Return>,RWRunnableIOUFunction(RWRunnableImp*));
}

template <class Return>
inline
void
RWRunnableIOUFunction<Return>::setIOUEscrow(const RWIOUEscrow<Return>& escrow)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadIOUFunction<Return>,setIOUEscrow(const RWIOUEscrow<Return>&):void);
   ((RWRunnableIOUFunctionImp<Return>&)body()).setIOUEscrow(escrow);
}

/*****************************************************************************/
      
// Dummy Implementation
template <class Return>
// protected
inline
RWRunnableIOUFunctionImp<Return>::RWRunnableIOUFunctionImp(const RWRunnableIOUFunctionImp<Return>& second) 
   RWTHRTHROWSANY
{
   (void)second;
}

// Dummy Implementation
template <class Return>
// protected
inline
RWRunnableIOUFunctionImp<Return>&
RWRunnableIOUFunctionImp<Return>::operator=(const RWRunnableIOUFunctionImp<Return>& second) 
   RWTHRTHROWSANY
{
   (void)second;
   return *this;
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/runfunci.cc>
#  endif

#endif // __RWTHRRUNFUNCI_H__
