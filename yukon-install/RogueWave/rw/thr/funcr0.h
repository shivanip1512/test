#if !defined(__RWTHRFUNCR0_H__)
#  define __RWTHRFUNCR0_H__
/*****************************************************************************
 *
 * funcr0.h 
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

funcr0.h - Class declarations for:

   RWFunctorR0<SR> - Handle class for RWFunctorR0Imp functor family
   RWFunctorR0Imp<SR> - Base class body for FunctorR0 functor family

   RWFunctorR0GImp<SR,DR>
   RWFunctorR0GA1Imp<SR,DR,A1>
   RWFunctorR0GA2Imp<SR,DR,A1,A2>
   RWFunctorR0GA2Imp<SR,DR,A1,A2,A3>

   RWFunctorR0MImp<SR,Callee,DR>
   RWFunctorR0MA1Imp<SR,Callee,DR,A1>
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2,A3>
            
See Also:

   funcr0.cc  - Out-of-line function definitions.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor1Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

/******************************************************************************
   Base class body implementation for functors with caller signature:
   SR (*func)(void);
******************************************************************************/
 
template <class SR>
class RWTHRTExport RWFunctorR0Imp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef SR (*CallerSignature)(void);
      typedef SR SRType;

      virtual
      SR
      run(void) const = 0
         RWTHRTHROWSANY;
};

/******************************************************************************
   Handle for functor implementations with caller signature:
   SR (*func)(S1);
******************************************************************************/

template <class SR>
class RWTHRTExport RWFunctorR0 :
   public RWTHRHandle {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef SR (*CallerSignature)(void);
      typedef SR SRType;

      // Construct an empty, invalid, handle instance
      RWFunctorR0(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWFunctorR0(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to a functor instance 
      RWFunctorR0(RWFunctorR0Imp<SR>* functorImp)
         RWTHRTHROWSANY;
      
      // Bind a new handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctorR0(const RWFunctorR0<SR>& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctorR0<SR>&
      operator=(const RWFunctorR0<SR>& second)
         RWTHRTHROWSANY;

      // Invoke the functor instance, if any, 
      // pointed-to by this handle instance
      SR 
      operator()(void) const
         RWTHRTHROWSANY;
         // throws RWTHRInvalidPointer if no imp assigned!

      // Get a reference to the functor instance, if any, 
      // pointed-to by this handle instance
      RWFunctorR0Imp<SR>&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};


/******************************************************************************

RWFunctorR0Imp<SR> Family:

   RWFunctorR0GImp<SR,DR>
   RWFunctorR0GA1Imp<SR,DR,A1>
   RWFunctorR0GA2Imp<SR,DR,A1,A2>
   RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>

   RWFunctorR0MImp<SR,Callee,DR>
   RWFunctorR0MA1Imp<SR,Callee,DR,A1>
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>
   RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>

******************************************************************************/

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0GImp<SR,DR>
   SR (*caller)(void) -> DR (*callee)(void)
******************************************************************************/

template <class SR, class DR>
class RWTHRTExport RWFunctorR0GImp :
   public RWFunctorR0Imp<SR> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(void);
      typedef DR (*CalleeSignature)(void);
      typedef SR SRType;
      typedef DR DRType;

      static
      RWFunctorR0<SR>
      make(CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR0GImp(CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
};

#define rwMakeFunctorR0G(SR,DR,function) \
   RWFunctorR0GImp<SR,DR >::make(function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class DR>
RWFunctorR0<SR>
rwMakeFunctorR0(SR (*caller)(void),
                DR (*callee)(void))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR0G(SR,DR,callee); 
}

#  endif

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0GA1Imp<SR,DR,A1>
   SR (*caller)(void) -> DR (*callee)(A1)
******************************************************************************/

template <class SR, class DR,class A1>
class RWTHRTExport RWFunctorR0GA1Imp :
   public RWFunctorR0Imp<SR> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(void);
      typedef DR (*CalleeSignature)(A1);
      typedef SR SRType;
      typedef DR DRType;
      typedef A1 A1Type;

      static
      RWFunctorR0<SR>
      make(CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual 
      SR 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR0GA1Imp(CalleeSignature function, 
                        A1 a1)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctorR0GA1(SR,DR,function,A1,a1) \
   RWFunctorR0GA1Imp<SR,DR,A1 >::make(function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class DR, class A1, class AA1>
RWFunctorR0<SR>
rwMakeFunctorR0(SR (*caller)(void),
                DR (*callee)(A1),
                AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR0GA1(SR,DR,callee,A1,a1); 
}

#  endif

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0GA2Imp<SR,DR,A1,A2>
   SR (*caller)(void) -> DR (*callee)(A1,A2)
******************************************************************************/

template <class SR, class DR, class A1, class A2>
class RWTHRTExport RWFunctorR0GA2Imp :
   public RWFunctorR0Imp<SR> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(void);
      typedef DR (*CalleeSignature)(A1,A2);
      typedef SR SRType;
      typedef DR DRType;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctorR0<SR>
      make(CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      SR 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR0GA2Imp(CalleeSignature function,
                        A1 a1,
                        A2 a2)
         RWTHRTHROWSANY;
   
   private:
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
};

#define rwMakeFunctorR0GA2(SR,DR,function,A1,a1,A2,a2) \
   RWFunctorR0GA2Imp<SR,DR,A1,A2 >::make(function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class DR, class A1, class A2, class AA1, class AA2>
RWFunctorR0<SR>
rwMakeFunctorR0(SR (*caller)(void),
                DR (*callee)(A1,A2),
                AA1 a1,
                AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR0GA2(SR,DR,callee,A1,a1,A2,a2); 
}

#  endif

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>
   SR (*caller)(void) -> DR (*callee)(A1,A2,A3)
******************************************************************************/

template <class SR, class DR, class A1, class A2, class A3>
class RWTHRTExport RWFunctorR0GA3Imp :
   public RWFunctorR0Imp<SR> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(void);
      typedef DR (*CalleeSignature)(A1,A2,A3);
      typedef SR SRType;
      typedef DR DRType;
      typedef A1 A1Type;
      typedef A2 A2Type;
      typedef A3 A3Type;

      static
      RWFunctorR0<SR>
      make(CalleeSignature function,
           A1 a1,
           A2 a2,
           A3 a3)
         RWTHRTHROWSANY;

      virtual 
      SR 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR0GA3Imp(CalleeSignature function,
                        A1 a1,
                        A2 a2,
                        A3 a3)
         RWTHRTHROWSANY;
   
   private:
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
      A3                a3_;
};

#define rwMakeFunctorR0GA3(SR,DR,function,A1,a1,A2,a2,A3,a3) \
   RWFunctorR0GA3Imp<SR,DR,A1,A2,A3 >::make(function,a1,a2,a3)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class DR, class A1, class A2, class A3, class AA1, class AA2, class AA3>
RWFunctorR0<SR>
rwMakeFunctorR0(SR (*caller)(void),
                DR (*callee)(A1,A2,A3),
                AA1 a1,
                AA2 a2,
                AA3 a3)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR0GA3(SR,DR,callee,A1,a1,A2,a2,A3,a3); 
}

#  endif

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0MImp<SR,Callee,DR>
   SR (*caller)(void) -> DR (Callee::*func)(void)
******************************************************************************/

template <class SR, class Callee, class DR>
class RWTHRTExport RWFunctorR0MImp :
   public RWFunctorR0Imp<SR> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(void);
      typedef DR (Callee::*CalleeSignature)(void);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef DR DRType;

      static
      RWFunctorR0<SR>
      make(Callee& callee,
           CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR0MImp(Callee& callee,
                      CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
};

#define rwMakeFunctorR0M(SR,Callee,callee,DR,function) \
   RWFunctorR0MImp<SR,Callee,DR >::make(callee,function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class Callee, class DR>
RWFunctorR0<SR>
rwMakeFunctorR0(SR (*caller)(void),
                Callee& callee,
                DR (Callee::*function)(void))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR0M(SR,Callee,callee,DR,function); 
}                        

#  endif

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0MA1Imp<SR,Callee,DR,A1>
   SR (*caller)(void) -> DR (Callee::*func)(A1)
******************************************************************************/

template <class SR, class Callee, class DR, class A1>
class RWTHRTExport RWFunctorR0MA1Imp :
   public RWFunctorR0Imp<SR> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(void);
      typedef DR (Callee::*CalleeSignature)(A1);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef DR DRType;
      typedef A1 A1Type;

      static
      RWFunctorR0<SR>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(void) const
         RWTHRTHROWSANY;
   
   protected:
      RWFunctorR0MA1Imp(Callee& callee,
                        CalleeSignature function,
                        A1 a1)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctorR0MA1(SR,Callee,callee,DR,function,A1,a1) \
   RWFunctorR0MA1Imp<SR,Callee,DR,A1 >::make(callee,function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class Callee, class DR, class A1, class AA1>
RWFunctorR0<SR>
rwMakeFunctorR0(SR (*caller)(void),
                Callee& callee,
                DR (Callee::*function)(A1),
                AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR0MA1(SR,Callee,callee,DR,function,A1,a1); 
}                        

#  endif

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>
   SR (*caller)(void) -> DR (Callee::*func)(A1,A2)
******************************************************************************/

template <class SR, class Callee, class DR, class A1, class A2>
class RWTHRTExport RWFunctorR0MA2Imp :
   public RWFunctorR0Imp<SR> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(void);
      typedef DR (Callee::*CalleeSignature)(A1,A2);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef DR DRType;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctorR0<SR>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      SR
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR0MA2Imp(Callee& callee,
                        CalleeSignature function,
                        A1 a1,
                        A2 a2)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
};

#define rwMakeFunctorR0MA2(SR,Callee,callee,DR,function,A1,a1,A2,a2) \
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2 >::make(callee,function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class Callee, class DR, class A1, class A2, class AA1, class AA2>
RWFunctorR0<SR>
rwMakeFunctorR0(SR (*caller)(void),
                Callee& callee,
                DR (Callee::*function)(A1,A2),
                AA1 a1,
                AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR0MA2(SR,Callee,callee,DR,function,A1,a1,A2,a2); 
}                        

#  endif

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>
   SR (*caller)(void) -> DR (Callee::*func)(A1,A2,A3)
******************************************************************************/

template <class SR, class Callee, class DR, class A1, class A2, class A3>
class RWTHRTExport RWFunctorR0MA3Imp :
   public RWFunctorR0Imp<SR> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(void);
      typedef DR (Callee::*CalleeSignature)(A1,A2,A3);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef DR DRType;
      typedef A1 A1Type;
      typedef A2 A2Type;
      typedef A3 A3Type;

      static
      RWFunctorR0<SR>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1,
           A2 a2,
           A3 a3)
         RWTHRTHROWSANY;

      virtual 
      SR
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR0MA3Imp(Callee& callee,
                        CalleeSignature function,
                        A1 a1,
                        A2 a2,
                        A3 a3)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
      A3                a3_;
};

#define rwMakeFunctorR0MA3(SR,Callee,callee,DR,function,A1,a1,A2,a2,A3,a3) \
   RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3 >::make(callee,function,a1,a2,a3)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class Callee, class DR, class A1, class A2, class A3, class AA1, class AA2, class AA3>
RWFunctorR0<SR>
rwMakeFunctorR0(SR (*caller)(void),
                Callee& callee,
                DR (Callee::*function)(A1,A2,A3),
                AA1 a1,
                AA2 a2,
                AA3 a3)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR0MA3(SR,Callee,callee,DR,function,A1,a1,A2,a2,A3,a3); 
}                        

#  endif

/*****************************************************************************/

template <class SR>
inline
RWFunctorR0<SR>::RWFunctorR0(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMF(RWFunctorR0<SR>,RWFunctorR0(void));
}

template <class SR>
inline
RWFunctorR0<SR>::RWFunctorR0(RWStaticCtor)
   RWTHRTHROWSNONE
   : 
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMF(RWFunctorR0<SR>,RWFunctorR0(RWStaticCtor));
}

template <class SR>
inline
RWFunctorR0<SR>::RWFunctorR0(RWFunctorR0Imp<SR>* functorImp)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(functorImp)
{
   RWTHRTRACEMF(RWFunctorR0<SR>,RWFunctorR0(RWFunctorROImp<SR>*));
}
      
template <class SR>
inline
RWFunctorR0<SR>::RWFunctorR0(const RWFunctorR0<SR>& second)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(second)
{
   RWTHRTRACEMF(RWFunctorR0<SR>,RWFunctorR0(const RWFunctorRO<SR>&));
}

template <class SR>
inline
RWFunctorR0<SR>&
RWFunctorR0<SR>::operator=(const RWFunctorR0<SR>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWFunctorR0<SR>,operator=(const RWFunctorRO<SR>&):RWFunctorR0<SR>&);
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

template <class SR>
inline
RWFunctorR0Imp<SR>&
RWFunctorR0<SR>::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWFunctorR0<SR>,body(void):RWFunctorR0Imp<SR>&);
   // throws RWTHRInvalidPointer if no imp assigned!
   return (RWFunctorR0Imp<SR>&)RWTHRHandle::body();
}

/******************************************************************************

RWFunctorR0Imp<SR> Family:

   RWFunctorR0GImp<SR,DR>
   RWFunctorR0GA1Imp<SR,DR,A1>
   RWFunctorR0GA2Imp<SR,DR,A1,A2>
   RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>

   RWFunctorR0MImp<SR,Callee,DR>
   RWFunctorR0MA1Imp<SR,Callee,DR,A1>
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>
   RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>

******************************************************************************/

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0GImp<SR,DR>
   SR (*caller)(void) -> DR (*callee)(void)
******************************************************************************/

template <class SR, class DR>
inline
// static
RWFunctorR0<SR>
RWFunctorR0GImp<SR,DR>::make(CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT2(RWFunctorR0GImp,SR,DR,make(CalleeSignature):RWFunctorR0<SR>);
   return new RWFunctorR0GImp<SR,DR>(function);
}

template <class SR, class DR>
inline
RWFunctorR0GImp<SR,DR>::RWFunctorR0GImp(CalleeSignature function)
   RWTHRTHROWSANY
   : function_(function)
{
   RWTHRTRACEMFT2(RWFunctorR0GImp,SR,DR,RWFunctorR0GImp(CalleeSignature));
}
    
/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0GA1Imp<SR,DR,A1>
   SR (*caller)(void) -> DR (*callee)(A1)
******************************************************************************/

template <class SR, class DR, class A1>
inline
// static
RWFunctorR0<SR>
RWFunctorR0GA1Imp<SR,DR,A1>::make(CalleeSignature function,
                                  A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT3(RWFunctorR0GA1Imp,SR,DR,A1,make(CalleeSignature,A1):RWFunctorR0<SR>);
   return new RWFunctorR0GA1Imp<SR,DR,A1>(function,a1);
}

template <class SR, class DR,class A1>
inline
RWFunctorR0GA1Imp<SR,DR,A1>::RWFunctorR0GA1Imp(CalleeSignature function,
                                               A1 a1)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT3(RWFunctorR0GA1Imp,SR,DR,A1,RWFunctorR0GA1Imp(CalleeSignature,A1));
}
      
/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0GA2Imp<SR,DR,A1,A2>
   SR (*caller)(void) -> DR (*callee)(A1,A2)
******************************************************************************/

template <class SR, class DR, class A1, class A2>
inline
// static
RWFunctorR0<SR>
RWFunctorR0GA2Imp<SR,DR,A1,A2>::make(CalleeSignature function,
                                     A1 a1,
                                     A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT4(RWFunctorR0GA2Imp,SR,DR,A1,A2,make(CalleeSignature,A1,A2):RWFunctorR0<SR>);
   return new RWFunctorR0GA2Imp<SR,DR,A1,A2>(function,a1,a2);
}

template <class SR, class DR, class A1, class A2>
inline
RWFunctorR0GA2Imp<SR,DR,A1,A2>::RWFunctorR0GA2Imp(CalleeSignature function,
                                                  A1 a1,
                                                  A2 a2)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1),
      a2_(a2)
{
   RWTHRTRACEMFT4(RWFunctorR0GA2Imp,SR,DR,A1,A2,RWFunctorR0GA2Imp(CalleeSignature,A1,A2));
}
      
/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>
   SR (*caller)(void) -> DR (*callee)(A1,A2,A3)
******************************************************************************/

template <class SR, class DR, class A1, class A2, class A3>
inline
// static
RWFunctorR0<SR>
RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>::make(CalleeSignature function,
                                        A1 a1,
                                        A2 a2,
                                        A3 a3)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT5(RWFunctorR0GA3Imp,SR,DR,A1,A2,A3,make(CalleeSignature,A1,A2,A3):RWFunctorR0<SR>);
   return new RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>(function,a1,a2,a3);
}

template <class SR, class DR, class A1, class A2, class A3>
inline
RWFunctorR0GA3Imp<SR,DR,A1,A2,A3>::RWFunctorR0GA3Imp(CalleeSignature function,
                                                     A1 a1,
                                                     A2 a2,
                                                     A3 a3)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1),
      a2_(a2),
      a3_(a3)
{
   RWTHRTRACEMFT5(RWFunctorR0GA3Imp,SR,DR,A1,A2,A3,RWFunctorR0GA3Imp(CalleeSignature,A1,A2,A3));
}
      
/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0MImp<SR,Callee,DR>
   SR (*caller)(void) -> DR (Callee::*func)(void)
******************************************************************************/

template <class SR, class Callee, class DR>
inline
// static
RWFunctorR0<SR>
RWFunctorR0MImp<SR,Callee,DR>::make(Callee& callee,
                                    CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT3(RWFunctorR0MImp,SR,Callee,DR,make(Callee&,CalleeSignature):RWFunctorR0<SR>);
   return new RWFunctorR0MImp<SR,Callee,DR>(callee,function);
}

template <class SR, class Callee, class DR>
inline
RWFunctorR0MImp<SR,Callee,DR>::RWFunctorR0MImp(Callee& callee,
                                               CalleeSignature function)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function)
{
   RWTHRTRACEMFT3(RWFunctorR0MImp,SR,Callee,DR,RWFunctorR0MImp(Callee&,CalleeSignature));
}

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0MA1Imp<SR,Callee,DR,A1>
   SR (*caller)(void) -> DR (Callee::*func)(A1)
******************************************************************************/

template <class SR, class Callee, class DR, class A1>
inline
// static
RWFunctorR0<SR>
RWFunctorR0MA1Imp<SR,Callee,DR,A1>::make(Callee& callee,
                                         CalleeSignature function,
                                         A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT4(RWFunctorR0MA1Imp,SR,Callee,DR,A1,make(Callee&,CalleeSignature,A1):RWFunctorR0<SR>);
   return new RWFunctorR0MA1Imp<SR,Callee,DR,A1>(callee,function,a1);
}

template <class SR, class Callee, class DR, class A1>
inline
RWFunctorR0MA1Imp<SR,Callee,DR,A1>::RWFunctorR0MA1Imp(Callee& callee,
                                                      CalleeSignature function,
                                                      A1 a1)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT4(RWFunctorR0MA1Imp,SR,Callee,DR,A1,RWFunctorR0MA1Imp(Callee&,CalleeSignature,A1));
}

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>
   SR (*caller)(void) -> DR (Callee::*func)(A1,A2)
******************************************************************************/

template <class SR, class Callee, class DR, class A1, class A2>
inline
// static
RWFunctorR0<SR>
RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>::make(Callee& callee,
                                            CalleeSignature function,
                                            A1 a1,
                                            A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT5(RWFunctorR0MA2Imp,SR,Callee,DR,A1,A2,make(Callee&,CalleeSignature,A1,A2):RWFunctorR0<SR>);
   return new RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>(callee,function,a1,a2);
}

template <class SR, class Callee, class DR, class A1, class A2>
inline
RWFunctorR0MA2Imp<SR,Callee,DR,A1,A2>::RWFunctorR0MA2Imp(Callee& callee,
                                                         CalleeSignature function,
                                                         A1 a1,
                                                         A2 a2)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function),
      a1_(a1),
      a2_(a2)
{
   RWTHRTRACEMFT5(RWFunctorR0MA2Imp,SR,Callee,DR,A1,A2,RWFunctorR0MA2Imp(Callee&,CalleeSignature,A1,A2));
}

/******************************************************************************
   RWFunctorR0Imp<SR> Family:
   RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>
   SR (*caller)(void) -> DR (Callee::*func)(A1,A2,A3)
******************************************************************************/

template <class SR, class Callee, class DR, class A1, class A2, class A3>
inline
// static
RWFunctorR0<SR>
RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>::make(Callee& callee,
                                               CalleeSignature function,
                                               A1 a1,
                                               A2 a2,
                                               A3 a3)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT6(RWFunctorR0MA3Imp,SR,Callee,DR,A1,A2,A3,make(Callee&,CalleeSignature,A1,A2,A3):RWFunctorR0<SR>);
   return new RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>(callee,function,a1,a2,a3);
}

template <class SR, class Callee, class DR, class A1, class A2, class A3>
inline
RWFunctorR0MA3Imp<SR,Callee,DR,A1,A2,A3>::RWFunctorR0MA3Imp(Callee& callee,
                                                            CalleeSignature function,
                                                            A1 a1,
                                                            A2 a2,
                                                            A3 a3)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function),
      a1_(a1),
      a2_(a2),
      a3_(a3)
{
   RWTHRTRACEMFT6(RWFunctorR0MA3Imp,SR,Callee,DR,A1,A2,A3,RWFunctorR0MA3Imp(Callee&,CalleeSignature,A1,A2,A3));
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/funcr0.cc>
#  endif

#endif // __RWTHRFUNCR0_H__
