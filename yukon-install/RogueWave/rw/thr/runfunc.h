#if !defined(__RWTHRRUNFUNC_H__)
#  define __RWTHRRUNFUNC_H__
/*****************************************************************************
 *
 * runfunc.h 
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

runfunc.h - Contains:

   Class declarations for:

      RWRunnableFunctionImp - RWFunctor0-based runnable class.
      RWRunnableFunction - Handle for RWRunnableFunctionImp instances.

   Template function definitions:

      The following template functions construct a runnable object
      These functions construct a runnable that, when started, executes the 
      specified function using the specified argument values (if any).
      They return a RWRunnableFunction handle instance that references the 
      runnable object and may be assigned to another RWRunnableFunction
      or RWRunnable handle instance.

      RWRunnableFunction rwMakeRunnableFunction(R (*function)(void))
      RWRunnableFunction rwMakeRunnableFunction(R (*function)(A1),AA1 a1)
      RWRunnableFunction rwMakeRunnableFunction(R (*function)(A1,A2),AA1 a1,AA2 a2)
      RWRunnableFunction rwMakeRunnableFunction(R (*function)(A1,A2,A3),AA1 a1,AA2 a2,AA3 a3)

      RWRunnableFunction rwMakeRunnableFunction(Callee& callee,R (Callee::*function)(void))
      RWRunnableFunction rwMakeRunnableFunction(Callee& callee,R (Callee::*function)(A1),AA1 a1)
      RWRunnableFunction rwMakeRunnableFunction(Callee& callee,R (Callee::*function)(A1,A2),AA1 a1,AA2 a2)
      RWRunnableFunction rwMakeRunnableFunction(Callee& callee,R (Callee::*function)(A1,A2,A3),AA1 a1,AA2 a2,AA3 a3)
   
   Macro definitions:

      The following macros construct a runnable object resulting in a 
      RWRunnableFunction instance that may be used as the right-hand value in an 
      assignment or function call:
   
    For global functions...
    
      rwMakeRunnableFunctionG(R,function) 
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(void).
    
      rwMakeRunnableFunctionGA1(R,function,A1,a1)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1).
         A1 - is the type name of the argument.
         a1 - is an expression that can be converted to an instance of A1.
    
      rwMakeRunnableFunctionGA2(R,function,A1,a1,A2,a2)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1,A2).
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the second argument.
         a2 - is an expression that can be converted to an instance of A2.

      rwMakeRunnableFunctionGA3(R,function,A1,a1,A2,a2,A3,a3)
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(*)(A1,A2,A3).
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the second argument.
         a2 - is an expression that can be converted to an instance of A2.
         A3 - is the type name of the second argument.
         a3 - is an expression that can be converted to an instance of A3.

    For member functions...
    
      rwMakeRunnableFunctionM(Callee,callee,R,function) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(void)
    
      rwMakeRunnableFunctionMA1(Callee,callee,R,function,A1,a1) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(A1)
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.

      rwMakeRunnableFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(A1,A2)
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the second argument.
         a2 - is an expression that can be converted to an instance of A2.

      rwMakeRunnableFunctionMA3(Callee,callee,R,function,A1,a1,A2,a2,A3 a3) 
         Callee - is the type name of the function's class.
         callee - is an expression that results in a reference to a Callee instance.
         R - is the type name of the function return value.
         function - is function pointer with signature (void)(Callee::*)(A1,A2,A3)
         A1 - is the type name of the first argument.
         a1 - is an expression that can be converted to an instance of A1.
         A2 - is the type name of the second argument.
         a2 - is an expression that can be converted to an instance of A2.
         A3 - is the type name of the second argument.
         a3 - is an expression that can be converted to an instance of A3.

See Also:

   runfunc.cpp - Out-of-line function definitions.

   runfunci.h - Declarations for RWFunctorR0-based runnables that return IOUs.
      
      RWRunnableIOUFunction - Handle for RWRunnableIOUR0Imp.
      RWRunnableIOUFunctionImp - RWFunctorR0-based runnable class for use with
         functions where IOU synchronization is required for the return value.

   functor0.h - RWFunctor0 handle and base implementation class.
   func0.h - Remaining members of RWFunctor0Imp-family of functor classes.
   
******************************************************************************/

#  if !defined(__RWTHRRUNNABLE_H__)
#     include <rw/thr/runnable.h>
#  endif

#  if !defined(__RWTHRFUNC0_H__)
#     include <rw/thr/func0.h>
#  endif

class RWTHRExport RWRunnableFunctionImp;
class RWTHRExport RWRunnableFunction;
class RWTHRExport RWRunnableFunctionSelf;

class RWTHRExport RWRunnableFunction :
   public RWRunnable {

   RW_THR_DECLARE_TRACEABLE

   friend class RWRunnableFunctionImp;
   friend class RWRunnableFunctionSelf;

   public:

      // Construct an empty, invalid, handle instance
      RWRunnableFunction(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWRunnableFunction(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableFunction(const RWRunnableFunction& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableFunction&
      operator=(const RWRunnableFunction& second)
         RWTHRTHROWSANY;

      // Return an internal interface handle to the runnable object 
      // pointed-to by an external interface handle (if any).
      RWRunnableFunctionSelf
      getRWRunnableFunctionSelf(void) const
         RWTHRTHROWSANY;

      // Construct a runnable functor instance
      static
      RWRunnableFunction
      make(void)
         RWTHRTHROWSANY;

      // Construct a runnable functor instance using the specified functor
      static
      RWRunnableFunction
      make(const RWFunctor0& functor)
         RWTHRTHROWSANY;

      // Change the functor associated with this runnable functor instance.
      void
      setFunctor(const RWFunctor0& functor)
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

      // Get the functor, if any, associated with this runnable functor 
      // instance.
      RWFunctor0
      getFunctor(void) const
         RWTHRTHROWS2(RWTHRInvalidPointer,
                      RWTHRInternalError);

   protected:

      // Bind a new handle instance to a runnable instance 
      RWRunnableFunction(RWRunnableFunctionImp* imp)
         RWTHRTHROWSANY;
      
      // Bind an external interface handle instance to the RWRunnableImp 
      // instance, if any, pointed-to by an internal handle instance 
      RWRunnableFunction(const RWRunnableFunctionSelf& second)
         RWTHRTHROWSANY;

      // Override the parent class body() function to provide
      // version that casts to this handle's body class
      RWRunnableFunctionImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

class RWTHRExport RWRunnableFunctionSelf :
   public RWRunnableSelf {

   RW_THR_DECLARE_TRACEABLE

   friend class RWRunnableFunctionImp;
   friend class RWRunnableFunction;

   public:

      // Construct an empty, invalid, handle instance
      RWRunnableFunctionSelf(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWRunnableFunctionSelf(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableFunctionSelf(const RWRunnableFunctionSelf& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the runnable instance, if any,
      // pointed-to by another handle instance
      RWRunnableFunctionSelf&
      operator=(const RWRunnableFunctionSelf& second)
         RWTHRTHROWSANY;

      // Return an internal interface handle to the runnable object 
      // pointed-to by an external interface handle (if any).
      RWRunnableFunction
      getRWRunnableFunction(void) const
         RWTHRTHROWSANY;

   protected:

      // Bind an internal interface handle instance to the RWRunnableImp 
      // instance, if any, pointed-to by an external handle instance 
      RWRunnableFunctionSelf(const RWRunnableFunction& second)
         RWTHRTHROWSANY;
      
      // Override the parent class body() function to provide
      // a version that casts to this handle's body class 
      RWRunnableFunctionImp&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};

class RWTHRExport RWRunnableFunctionImp :
   public RWRunnableImp {

   RW_THR_DECLARE_TRACEABLE

   friend class RWRunnableFunction;
   friend class RWRunnableFunctionSelf;

   private:

      RWFunctor0       functor_;

   protected:

      // Make a RWFunctor0-based runnable with an undefined functor instance.
      static
      RWRunnableFunction
      make(void)
         RWTHRTHROWSANY;

      // Make a RWFunctor0-based runnable using the specified functor instance.
      static
      RWRunnableFunction
      make(const RWFunctor0& functor)
         RWTHRTHROWSANY;

      // Set or change the functor instance that will execute when this 
      // runnable runnable is started.
      void
      setFunctor(const RWFunctor0& functor)
         RWTHRTHROWS1(RWTHRInternalError);

      // Get the current functor instance associated with this runnable runnable.
      RWFunctor0
      getFunctor(void) const
         RWTHRTHROWS1(RWTHRInternalError);

   protected:

      // Protect constructors (only accessible by make functions)

      RWRunnableFunctionImp(void)
         RWTHRTHROWSANY;

      RWRunnableFunctionImp(const RWFunctor0& functor)
         RWTHRTHROWSANY;

      // Override the virtual run() member to invoke functor at start-up.
      virtual
      void
      run(void)
         RWTHRTHROWSANY;

      // Make these protected to prevent compiler generation...

      RWRunnableFunctionImp(const RWRunnableFunctionImp& second)
         RWTHRTHROWSANY;

      RWRunnableFunctionImp&
      operator=(const RWRunnableFunctionImp& second)
         RWTHRTHROWSANY;

};

/*
      
The following template functions and macros construct runnable objects
that, when started, execute the specified function using the specified 
argument values (if any).

Each returns a RWRunnableFunction instance that references the newly created
runnable object.

Example using the functions:
   
   void myFunc(void);

   RWRunnableFunction myRunnable = rwMakeRunnableFunction(myFunc);
   myRunnable.start();
   ...

Example using the macro (for compilers that can't handle the template functions):

   class MyClass {
      public:
         void myFunc(int arg);
   };
   
   :

   MyClass myClass;

   RWRunnableFunction myRunnable = rwMakeRunnableFunctionMA1(MyClass,myClass,&MyClass::myFunc,int,10);
   myRunnable.start();
   ...

*/

#define rwMakeRunnableFunctionG(R,function) \
   RWRunnableFunction::make(RWFunctor0GImp<R >::make(function))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R>
RWRunnableFunction
rwMakeRunnableFunction(R (*function)(void))
{
   return rwMakeRunnableFunctionG(R,function);
}
#endif


#define rwMakeRunnableFunctionGA1(R,function,A1,a1) \
   RWRunnableFunction::make(RWFunctor0GA1Imp<R,A1 >::make(function,a1))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class AA1>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableFunction
rwMakeRunnableFunction(R (*function)(A1),
                       AA1 a1)
{
   return rwMakeRunnableFunctionGA1(R,function,A1,a1);
}
#  endif


#define rwMakeRunnableFunctionGA2(R,function,A1,a1,A2,a2) \
   RWRunnableFunction::make(RWFunctor0GA2Imp<R,A1,A2 >::make(function,a1,a2))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class A2, class AA1, class AA2>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableFunction
rwMakeRunnableFunction(R (*function)(A1,A2),
                       AA1 a1,
                       AA2 a2)
{
   return rwMakeRunnableFunctionGA2(R,function,A1,a1,A2,a2);
}
#  endif


#define rwMakeRunnableFunctionGA3(R,function,A1,a1,A2,a2,A3,a3) \
   RWRunnableFunction::make(RWFunctor0GA3Imp<R,A1,A2,A3 >::make(function,a1,a2,a3))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class R, class A1, class A2, class A3, class AA1, class AA2, class AA3>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableFunction
rwMakeRunnableFunction(R (*function)(A1,A2,A3),
                       AA1 a1,
                       AA2 a2,
                       AA3 a3)
{
   return rwMakeRunnableFunctionGA3(R,function,A1,a1,A2,a2,A3,a3);
}
#  endif


#define rwMakeRunnableFunctionM(Callee,callee,R,function) \
   RWRunnableFunction::make(RWFunctor0MImp<Callee,R >::make(callee,function))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)     
template <class Callee, class R>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableFunction
rwMakeRunnableFunction(Callee& callee,
                       R (Callee::*function)(void))
{
   return rwMakeRunnableFunctionM(Callee,callee,R,function);
}
#  endif

#define rwMakeRunnableFunctionMA1(Callee,callee,R,function,A1,a1) \
   RWRunnableFunction::make(RWFunctor0MA1Imp<Callee,R,A1 >::make(callee,function,a1))

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class AA1>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableFunction
rwMakeRunnableFunction(Callee& callee,
                       R (Callee::*function)(A1),
                       AA1 a1)
{
   return rwMakeRunnableFunctionMA1(Callee,callee,R,function,A1,a1);
}
#endif


#define rwMakeRunnableFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2) \
   RWRunnableFunction::make(RWFunctor0MA2Imp<Callee,R,A1,A2 >::make(callee,function,a1,a2));

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class A2, class AA1, class AA2>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableFunction
rwMakeRunnableFunction(Callee& callee,
                       R (Callee::*function)(A1,A2),
                       AA1 a1,
                       AA2 a2)
{
   return rwMakeRunnableFunctionMA2(Callee,callee,R,function,A1,a1,A2,a2);
}
#endif

#define rwMakeRunnableFunctionMA3(Callee,callee,R,function,A1,a1,A2,a2,A3,a3) \
   RWRunnableFunction::make(RWFunctor0MA3Imp<Callee,R,A1,A2,A3 >::make(callee,function,a1,a2,a3));

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)
template <class Callee, class R, class A1, class A2, class A3, class AA1, class AA2, class AA3>
#    if !defined(RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS)
inline
#    endif
RWRunnableFunction
rwMakeRunnableFunction(Callee& callee,
                       R (Callee::*function)(A1,A2,A3),
                       AA1 a1,
                       AA2 a2,
                       AA3 a3)
{
   return rwMakeRunnableFunctionMA3(Callee,callee,R,function,A1,a1,A2,a2,A3,a3);
}
#endif

/*****************************************************************************/

// protected
inline
RWRunnableFunctionImp::RWRunnableFunctionImp(void) 
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableFunctionImp,RWRunnableFunctionImp);
}

// protected
inline
RWRunnableFunctionImp::RWRunnableFunctionImp(const RWFunctor0& functor) 
   RWTHRTHROWSANY
   :
      functor_(functor)
{
   RWTHRTRACEMF(RWRunnableFunctionImp,RWRunnableFunctionImp);
}

// Dummy Implementation
// protected
inline
RWRunnableFunctionImp::RWRunnableFunctionImp(const RWRunnableFunctionImp&) 
   RWTHRTHROWSANY
{
}

// Dummy Implementation
// protected
inline
RWRunnableFunctionImp&
RWRunnableFunctionImp::operator=(const RWRunnableFunctionImp&) 
   RWTHRTHROWSANY
{
   return *this;
}

/*****************************************************************************/

// Create an empty (invalid) external interface handle
inline
RWRunnableFunction::RWRunnableFunction(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableFunction,RWRunnableFunction(void));
}

// Create an empty (invalid) external interface handle
inline
RWRunnableFunction::RWRunnableFunction(RWStaticCtor)
   RWTHRTHROWSNONE
   : 
      RWRunnable(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWRunnableFunction,RWRunnableFunction(RWStaticCtor));
}

// Construct an external interface handle to the runnable object instance 
// protected
inline
RWRunnableFunction::RWRunnableFunction(RWRunnableFunctionImp* imp)
   RWTHRTHROWSANY
   :
      RWRunnable(imp)
{
   RWTHRTRACEMF(RWRunnableFunction,RWRunnableFunction(RWRunnableFunctionImp*));
}
      
// Construct an external interface handle to the runnable object (if any)
// pointed-to by another external interface handle 
inline
RWRunnableFunction::RWRunnableFunction(const RWRunnableFunction& second)
   RWTHRTHROWSANY
   :
      RWRunnable(second)
{
   RWTHRTRACEMF(RWRunnableFunction,RWRunnableFunction(const RWRunnableFunction&));
}

// Construct an external interface handle to the runnable object (if any)
// pointed-to by an internal interface handle 
// protected
inline
RWRunnableFunction::RWRunnableFunction(const RWRunnableFunctionSelf& second)
   RWTHRTHROWSANY
   :
      RWRunnable(second)
{
   RWTHRTRACEMF(RWRunnableFunction,RWRunnableFunction(const RWRunnableFunctionSelf&));
}

// Bind this external interface handle to the runnable object (if any)
// pointed-to by a second external interface handle.
inline
RWRunnableFunction&
RWRunnableFunction::operator=(const RWRunnableFunction& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableFunction,operator=(const RWRunnableFunction&):RWRunnableFunction&);
   if (&second != this)
      RWRunnable::operator=(second);
   return *this;
}

// protected
inline
RWRunnableFunctionImp&
RWRunnableFunction::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWRunnableFunction,body(void):RWRunnableFunctionImp&);
   return (RWRunnableFunctionImp&)RWRunnable::body();
}

inline
void
RWRunnableFunction::setFunctor(const RWFunctor0& functor)
   RWTHRTHROWS2(RWTHRInvalidPointer,
                RWTHRInternalError)
{
   RWTHRTRACEMF(RWRunnableFunction,setFunctor(const RWFunctor0&):void);
   body().setFunctor(functor);
}

/*****************************************************************************/

// Create an empty (invalid) internal interface handle
inline
RWRunnableFunctionSelf::RWRunnableFunctionSelf(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWRunnableFunctionSelf,RWRunnableFunctionSelf(void));
}

// Create a global static handle instance
inline
RWRunnableFunctionSelf::RWRunnableFunctionSelf(RWStaticCtor)
   RWTHRTHROWSNONE
   :
      RWRunnableSelf(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWRunnableFunctionSelf,RWRunnableFunctionSelf(RWStaticCtor));
}

// Construct an internal interface handle to the runnable functor object (if any)
// pointed-to by another internal interface handle 
inline
RWRunnableFunctionSelf::RWRunnableFunctionSelf(const RWRunnableFunctionSelf& second)
   RWTHRTHROWSANY
   :
      RWRunnableSelf(second)
{
   RWTHRTRACEMF(RWRunnableFunctionSelf,RWRunnableFunctionSelf(const RWRunnableFunctionSelf&));
}

// Construct an internal interface handle to the runnable functor object (if any)
// pointed-to by an external interface handle 
// protected
inline
RWRunnableFunctionSelf::RWRunnableFunctionSelf(const RWRunnableFunction& second)
   RWTHRTHROWSANY
   :
      RWRunnableSelf(second)
{
   RWTHRTRACEMF(RWRunnableFunctionSelf,RWRunnableFunctionSelf(const RWRunnableFunction&));
}
      
// Bind this internal interface handle to the runnable functor object (if any)
// pointed-to by a second internal interface handle.
inline
RWRunnableFunctionSelf&
RWRunnableFunctionSelf::operator=(const RWRunnableFunctionSelf& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWRunnableFunctionSelf,operator=(const RWRunnableFunctionSelf&):RWRunnableFunctionSelf&);
   if (&second != this)
      RWRunnableSelf::operator=(second);
   return *this;
}

// protected
inline
RWRunnableFunctionImp&
RWRunnableFunctionSelf::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWRunnableFunctionSelf,body(void):RWRunnableFunctionImp&);
   return (RWRunnableFunctionImp&)RWRunnableSelf::body();
}

#endif // __RWTHRRUNFUNC_H__
