#if !defined(__RWTHRTHRFUNC_H__)
#  define __RWTHRTHRFUNC_H__
/*****************************************************************************
 *
 * thrfunc.h 
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

thrfunc.h - Contains:

   Class declarations for:

      RWThreadFunctionImp - RWFunctor0-based thread class.
      RWThreadFunction - Handle for RWThreadFunctionImp instances.

   Template function definitions:

      The following template functions construct a thread object
      These functions construct a thread that, when started, executes the 
      specified function using the specified argument values (if any).
      They return a RWThreadFunction handle instance that references the 
      thread object and may be assigned to another RWThreadFunction, 
      RWThread or RWRunnable handle instance.

      RWThreadFunction rwMakeThreadFunction(R (*function)(void))
      RWThreadFunction rwMakeThreadFunction(R (*function)(A1),A1 a1)
      RWThreadFunction rwMakeThreadFunction(R (*function)(A1,A2),AA1 a1,AA2 a2)
      RWThreadFunction rwMakeThreadFunction(R (*function)(A1,A2,A3),AA1 a1,AA2 a2,AA3 a3)

      RWThreadFunction rwMakeThreadFunction(Callee& callee,R (Callee::*function)(void))
      RWThreadFunction rwMakeThreadFunction(Callee& callee,R (Callee::*function)(A1),AA1 a1)
      RWThreadFunction rwMakeThreadFunction(Callee& callee,R (Callee::*function)(A1,A2),AA1 a1,AA2 a2)
      RWThreadFunction rwMakeThreadFunction(Callee& callee,R (Callee::*function)(A1,A2,A3),AA1 a1,AA2 a2,AA3 a3)
   
   Macro definitions:

      The following macros construct a thread object resulting in a 
      RWThreadFunction instance that may be used as the right-hand value in an 
      assignment or function call:
   
    For global functions...
    
      rwMakeThreadFunctionG(R,function) 
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(void).
    
      rwMakeThreadFunctionGA1(R,function,A1,a1)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1).
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
    
      rwMakeThreadFunctionGA2(R,function,A1,a1,A2,a2)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1,A2).
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the second argument.
         a2 - is an expression that can be converted to an instance of A2.

      rwMakeThreadFunctionGA3(R,function,A1,a1,A2,a2,A3,a3)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1,A2,A3).
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the second argument.
         a2 - is an expression that can be converted to an instance of A2.
         A3 - is the type name of the second argument.
         a3 - is an expression that can be converted to an instance of A3.

    For member functions...
    
      rwMakeThreadFunctionM(Callee,callee,R,function) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(void)
    
      rwMakeThreadFunctionMA1(Callee,callee,R,function,A1,a1) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(A1)
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.

      rwMakeThreadFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(A1,A2)
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the first argument.
         a2 - is an expression that can be converted to an instance of A2.

      rwMakeThreadFunctionMA3(Callee,callee,R,function,A1,a1,A2,a2,A3,a3) 
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

   thrfunc.cpp - Out-of-line function definitions.

   thrfunci.h - Declarations for RWFunctorR0-based threads that return RWIOUs.
      
      RWThreadIOUR0Imp - RWFunctorR0 based thread class for use with
         functions where RWIOU synchronization is required for the return value.

   functor0.h - RWFunctor0 handle and base implementation class.
   func0.h - Remaining members of RWFunctor0Imp-family of functor classes.
   
******************************************************************************/

#  if !defined(__RWTHRTHREAD_H__)
#     include <rw/thr/thread.h>
#  endif

#  if !defined(__RWTHRFUNC0_H__)
#     include <rw/thr/func0.h>
#  endif

#  if !defined(__RWTHRUTIL_H__)
#     include <rw/thr/thrutil.h>
#  endif

class RWTHRExport RWThreadFunctionImp;
class RWTHRExport RWThreadFunction;
class RWTHRExport RWThreadFunctionSelf;

class RWTHRExport RWThreadFunction :
   public RWThread {

   RW_THR_DECLARE_TRACEABLE

   friend class RWThreadFunctionImp;
   friend class RWThreadFunctionSelf;

   public:

      // Construct an empty (invalid) handle
      RWThreadFunction(void)
         RWTHRTHROWSANY;

      // Construct an external interface handle to the thread object 
      // pointed-to by a second handle (if any).
      RWThreadFunction(const RWThreadFunction& second)
         RWTHRTHROWSANY;

      // Bind this external interface handle to the thread object 
      // pointed-to by a second handle (if any)
      RWThreadFunction&
      operator=(const RWThreadFunction& second)
         RWTHRTHROWSANY;

      // Return an internal interface handle to the thread object 
      // pointed-to by an external interface handle (if any).
      RWThreadFunctionSelf
      getRWThreadFunctionSelf(void) const
         RWTHRTHROWSANY;

      // Construct and return a handle to a new RWThreadFunctionImp instance.
      // The setFunctor() member must be used to supply a functor prior to starting.
      static
      RWThreadFunction
      make(void)
         RWTHRTHROWSANY;

      // Construct and return a handle to a new RWThreadFunctionImp instance.
      // The new instance will execute the supplied functor when started.
      static
      RWThreadFunction
      make(const RWFunctor0& functor)
         RWTHRTHROWSANY;

      // Construct and return a handle to a new RWThreadFunctionImp instance.
      // The new instance will execute the supplied functor instance when started.
      // Any threads started by the runnable will be created using the 
      // attributes given by the specified thread attribute instance.
      static
      RWThreadFunction
      make(const RWFunctor0& functor,
           const RWThreadAttribute& attr)
         RWTHRTHROWSANY;

      // Construct and return a handle to a new RWThreadFunctionImp instance.
      // The setFunctor() member must be used to supply a functor prior to starting.
      // Any threads started by the runnable will be created using the 
      // attributes given by the specified thread attribute instance.
      static
      RWThreadFunction
      make(const RWThreadAttribute& attr)
         RWTHRTHROWSANY;

      void
      setFunctor(const RWFunctor0& functor)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      RWFunctor0
      getFunctor(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

   protected:

      // Construct an external interface handle to the RWThreadImp instance
      // pointed-to by a base class handle instance (if any)
      RWThreadFunction(RWThreadFunctionImp* imp)
         RWTHRTHROWSANY;
      
      // Construct an external interface handle to the RWThreadImp 
      // instance (if any) pointed-to by an internal interface handle 
      RWThreadFunction(const RWThreadFunctionSelf& second)
         RWTHRTHROWSANY;

      // Override the parent class body() function to provide
      // version that casts to this handle's body class
      RWThreadFunctionImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

class RWTHRExport RWThreadFunctionSelf :
   public RWThreadSelf {

   RW_THR_DECLARE_TRACEABLE

   friend class RWThreadFunctionImp;
   friend class RWThreadFunction;

   public:

      // Construct an empty (invalid) handle
      RWThreadFunctionSelf(void)
         RWTHRTHROWSANY;

      // Construct an external interface handle to the thread object 
      // pointed-to by a second handle (if any).
      RWThreadFunctionSelf(const RWThreadFunctionSelf& second)
         RWTHRTHROWSANY;

      // Bind this external interface handle to the thread object 
      // pointed-to by a second handle (if any)
      RWThreadFunctionSelf&
      operator=(const RWThreadFunctionSelf& second)
         RWTHRTHROWSANY;

      // Return an internal interface handle to the thread object 
      // pointed-to by an external interface handle (if any).
      RWThreadFunction
      getRWThreadFunction(void) const
         RWTHRTHROWSANY;

   protected:

      // Construct an internal interface handle to the RWThreadImp 
      // instance (if any) pointed-to by an external interface handle 
      RWThreadFunctionSelf(const RWThreadFunction& second)
         RWTHRTHROWSANY;
      
      // Override the parent class body() function to provide
      // a version that casts to this handle's body class 
      RWThreadFunctionImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

class RWTHRExport RWThreadFunctionImp :
   public RWThreadImp {

   RW_THR_DECLARE_TRACEABLE

   friend class RWThreadFunction;
   friend class RWThreadFunctionSelf;

   private:

      RWFunctor0       functor_;

   protected:

      // Make a RWFunctor0-based thread with an undefined functor instance.
      static
      RWThreadFunction
      make(void)
         RWTHRTHROWSANY;

      // Make a RWFunctor0-based thread using the specified functor instance.
      static
      RWThreadFunction
      make(const RWFunctor0& functor)
         RWTHRTHROWSANY;

      // Make a RWFunctor0-based thread using the specified functor 
      // and specified thread attribute instance.
      static
      RWThreadFunction
      make(const RWFunctor0& functor,
           const RWThreadAttribute& attr)
         RWTHRTHROWSANY;

      // Make a RWFunctor0-based thread with an undefined functor instance and
      // using the specified thread attribute instance.
      static
      RWThreadFunction
      make(const RWThreadAttribute& attr)
         RWTHRTHROWSANY;

      // Set or change the functor instance that will execute when this 
      // thread runnable is started.
      void
      setFunctor(const RWFunctor0& functor)
         RWTHRTHROWS1(RWTHRInternalError);

      // Get the current functor instance associated with this thread runnable.
      RWFunctor0
      getFunctor(void) const
         RWTHRTHROWS1(RWTHRInternalError);

   protected:

      // Protect constructors (only accessible by make functions)

      RWThreadFunctionImp(void)
         RWTHRTHROWSANY;

      RWThreadFunctionImp(const RWFunctor0& functor)
         RWTHRTHROWSANY;

      RWThreadFunctionImp(const RWFunctor0& functor,
                          const RWThreadAttribute& attr)
         RWTHRTHROWSANY;

      RWThreadFunctionImp(const RWThreadAttribute& attr)
         RWTHRTHROWSANY;

      // Override the virtual run() member to invoke functor at start-up.
      virtual
      void
      run(void)
         RWTHRTHROWSANY;

      // Make these protected to prevent compiler generation...

      RWThreadFunctionImp(const RWThreadFunctionImp& second)
         RWTHRTHROWSANY;

      RWThreadFunctionImp&
      operator=(const RWThreadFunctionImp& second)
         RWTHRTHROWSANY;

};

/*
      
The following template functions and macros construct thread objects
that, when started, execute the specified function using the specified 
argument values (if any).

Each returns a RWThread instance that references the newly created
thread object.

Example using the functions:
   
   void myFunc(void);

   RWThreadFunction myThread = rwMakeThreadFunction(myFunc);
   myThread.start();
   ...

Example using the macro (for compilers that can't handle the template functions):

   class MyClass {
      public:
         void myFunc(int arg);
   };
   
   :

   MyClass myClass;

   RWThreadFunction myThread = rwMakeThreadFunctionMA1(MyClass,myClass,&MyClass::myFunc,int,10);
   myThread.start();
   ...

*/

#define rwMakeThreadFunctionG(R,function) \
   RWThreadFunction::make(RWFunctor0GImp<R >::make(function))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline 
#    endif
RWThreadFunction
rwMakeThreadFunction(R (*function)(void))
{
   return rwMakeThreadFunctionG(R,function);
}
#  endif


#define rwMakeThreadFunctionGA1(R,function,A1,a1) \
   RWThreadFunction::make(RWFunctor0GA1Imp<R,A1  >::make(function,a1))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class AA1>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline 
#    endif
RWThreadFunction
rwMakeThreadFunction(R (*function)(A1),
                     AA1 a1)
{
   return rwMakeThreadFunctionGA1(R,function,A1,a1);
}
#  endif


#define rwMakeThreadFunctionGA2(R,function,A1,a1,A2,a2) \
   RWThreadFunction::make(RWFunctor0GA2Imp<R,A1,A2  >::make(function,a1,a2))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class A2, class AA1, class AA2>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline 
#    endif
RWThreadFunction
rwMakeThreadFunction(R (*function)(A1,A2),
                     AA1 a1,
                     AA2 a2)
{
   return rwMakeThreadFunctionGA2(R,function,A1,a1,A2,a2);
}
#  endif


#define rwMakeThreadFunctionGA3(R,function,A1,a1,A2,a2,A3,a3) \
   RWThreadFunction::make(RWFunctor0GA3Imp<R,A1,A2,A3 >::make(function,a1,a2,a3))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class A2, class A3, class AA1, class AA2, class AA3>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline 
#    endif
RWThreadFunction
rwMakeThreadFunction(R (*function)(A1,A2,A3),
                     AA1 a1,
                     AA2 a2,
                     AA3 a3)
{
   return rwMakeThreadFunctionGA3(R,function,A1,a1,A2,a2,A3,a3);
}
#  endif


#define rwMakeThreadFunctionM(Callee,callee,R,function) \
   RWThreadFunction::make(RWFunctor0MImp<Callee,R >::make(callee,function))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)     
template <class Callee, class R>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline 
#    endif
RWThreadFunction
rwMakeThreadFunction(Callee& callee,
                     R (Callee::*function)(void))
{
   return rwMakeThreadFunctionM(Callee,callee,R,function);
}
#  endif

#define rwMakeThreadFunctionMA1(Callee,callee,R,function,A1,a1) \
   RWThreadFunction::make(RWFunctor0MA1Imp<Callee,R,A1 >::make(callee,function,a1))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class AA1>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline 
#    endif
RWThreadFunction
rwMakeThreadFunction(Callee& callee,
                     R (Callee::*function)(A1),
                     AA1 a1)
{
   return rwMakeThreadFunctionMA1(Callee,callee,R,function,A1,a1);
}
#  endif


#define rwMakeThreadFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2) \
   RWThreadFunction::make(RWFunctor0MA2Imp<Callee,R,A1,A2 >::make(callee,function,a1,a2))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class A2, class AA1, class AA2>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline 
#    endif
RWThreadFunction
rwMakeThreadFunction(Callee& callee,
                     R (Callee::*function)(A1,A2),
                     AA1 a1,
                     AA2 a2)
{
   return rwMakeThreadFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2);
}
#endif


#define rwMakeThreadFunctionMA3(Callee,callee,R,function,A1,a1,A2,a2,A3,a3) \
   RWThreadFunction::make(RWFunctor0MA3Imp<Callee,R,A1,A2,A3 >::make(callee,function,a1,a2,a3))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class A2, class A3,class AA1, class AA2, class AA3>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline 
#    endif
RWThreadFunction
rwMakeThreadFunction(Callee& callee,
                     R (Callee::*function)(A1,A2,A3),
                     AA1 a1,
                     AA2 a2,
                     AA3 a3)
{
   return rwMakeThreadFunctionMA3(Callee,callee,R,function,A1,a1,A2,a2,A3,a3);
}
#endif

/*****************************************************************************/

// protected
inline
RWThreadFunctionImp::RWThreadFunctionImp(void) 
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadFunctionImp,RWThreadFunctionImp);
}

// protected
inline
RWThreadFunctionImp::RWThreadFunctionImp(const RWFunctor0& functor) 
   RWTHRTHROWSANY
   :
      functor_(functor)
{
   RWTHRTRACEMF(RWThreadFunctionImp,RWThreadFunctionImp);
}

// protected
inline
RWThreadFunctionImp::RWThreadFunctionImp(const RWFunctor0& functor,
                                         const RWThreadAttribute& attr) 
   RWTHRTHROWSANY
   :
      RWThreadImp(attr),
      functor_(functor)
{
   RWTHRTRACEMF(RWThreadFunctionImp,RWThreadFunctionImp);
}

// protected
inline
RWThreadFunctionImp::RWThreadFunctionImp(const RWThreadAttribute& attr) 
   RWTHRTHROWSANY
   :
      RWThreadImp(attr)
{
   RWTHRTRACEMF(RWThreadFunctionImp,RWThreadFunctionImp);
}

// Dummy Implementation
// private
inline
RWThreadFunctionImp::RWThreadFunctionImp(const RWThreadFunctionImp&) 
   RWTHRTHROWSANY
{
   RWTHRASSERT(0); // Shouldn't get here!
}

// Dummy Implementation
// private
inline
RWThreadFunctionImp&
RWThreadFunctionImp::operator=(const RWThreadFunctionImp&) 
   RWTHRTHROWSANY
{
   RWTHRASSERT(0); // Shouldn't get here!
   return *this;
}

/*****************************************************************************/

// Create an empty (invalid) external interface handle
inline
RWThreadFunction::RWThreadFunction(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadFunction,RWThreadFunction(void));
}

// Construct an external interface handle to the thread object instance 
// protected
inline
RWThreadFunction::RWThreadFunction(RWThreadFunctionImp* imp)
   RWTHRTHROWSANY
   :
      RWThread(imp)
{
   RWTHRTRACEMF(RWThreadFunction,RWThreadFunction(RWThreadFunctionImp*));
}
      
// Construct an external interface handle to the thread object (if any)
// pointed-to by another external interface handle 
inline
RWThreadFunction::RWThreadFunction(const RWThreadFunction& second)
   RWTHRTHROWSANY
   :
      RWThread(second)
{
   RWTHRTRACEMF(RWThreadFunction,RWThreadFunction(const RWThreadFunction&));
}

// Construct an external interface handle to the thread object (if any)
// pointed-to by an internal interface handle 
// protected
inline
RWThreadFunction::RWThreadFunction(const RWThreadFunctionSelf& second)
   RWTHRTHROWSANY
   :
      RWThread(second)
{
   RWTHRTRACEMF(RWThreadFunction,RWThreadFunction(const RWThreadFunctionSelf&));
}


// Construct an internal interface handle to the thread functor object (if any)
// pointed-to by an external interface handle 
// (This function must be defined before used in RWThreadFunction:getRWThreadFunctionSelf)
// protected
inline
RWThreadFunctionSelf::RWThreadFunctionSelf(const RWThreadFunction& second)
   RWTHRTHROWSANY
   :
      RWThreadSelf(second)
{
   RWTHRTRACEMF(RWThreadFunctionSelf,RWThreadFunctionSelf(const RWThreadFunction&));
}
      
// Bind this external interface handle to the thread object (if any)
// pointed-to by a second external interface handle.
inline
RWThreadFunction&
RWThreadFunction::operator=(const RWThreadFunction& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadFunction,operator=(const RWThreadFunction&):RWThreadFunction&);
   if (&second != this)
      RWThread::operator=(second);
   return *this;
}

// protected
inline
RWThreadFunctionImp&
RWThreadFunction::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWThreadFunction,body(void):RWThreadFunctionImp&);
   return (RWThreadFunctionImp&)RWThread::body();
}

inline
void
RWThreadFunction::setFunctor(const RWFunctor0& functor)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWThreadFunction,setFunctor(const RWFunctor0&):void);
   body().setFunctor(functor);
}

/*****************************************************************************/

// Create an empty (invalid) internal interface handle
inline
RWThreadFunctionSelf::RWThreadFunctionSelf(void)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadFunctionSelf,RWThreadFunctionSelf(void));
}

// Construct an internal interface handle to the thread functor object (if any)
// pointed-to by another internal interface handle 
inline
RWThreadFunctionSelf::RWThreadFunctionSelf(const RWThreadFunctionSelf& second)
   RWTHRTHROWSANY
   :
      RWThreadSelf(second)
{
   RWTHRTRACEMF(RWThreadFunctionSelf,RWThreadFunctionSelf(const RWThreadFunctionSelf&));
}

// Bind this internal interface handle to the thread functor object (if any)
// pointed-to by a second internal interface handle.
inline
RWThreadFunctionSelf&
RWThreadFunctionSelf::operator=(const RWThreadFunctionSelf& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWThreadFunctionSelf,operator=(const RWThreadFunctionSelf&):RWThreadFunctionSelf&);
   if (&second != this)
      RWThreadSelf::operator=(second);
   return *this;
}

// protected
inline
RWThreadFunctionImp&
RWThreadFunctionSelf::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWThreadFunctionSelf,body(void):RWThreadFunctionImp&);
   return (RWThreadFunctionImp&)RWThreadSelf::body();
}

#endif // __RWTHRTHRFUNC_H__
