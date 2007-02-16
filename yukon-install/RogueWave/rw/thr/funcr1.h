#if !defined(__RWTHRFUNCR1_H__)
#  define __RWTHRFUNCR1_H__
/*****************************************************************************
 *
 * funcr1.h 
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

funcr1.h - Class declarations for:

   RWFunctorR1<SR,S1> - Handle class for RWFunctorR1Imp functor family
   RWFunctorR1Imp<SR,S1> - Base class body for FunctorR1 functor family

   RWFunctorR1GImp<SR,S1,DR,D1>
   RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>
   RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>

   RWFunctorR1MImp<SR,S1,Callee,DR,D1>
   RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>
   RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>
            
See Also:

   funcr1.cc  - Out-of-line function definitions.

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
   SR (*func)(S1);
******************************************************************************/
 
template <class SR, class S1>
class RWTHRTExport RWFunctorR1Imp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef SR (*CallerSignature)(S1);
      typedef SR SRType;
      typedef S1 S1Type;

      virtual
      SR
      run(S1 s1) const = 0
         RWTHRTHROWSANY;
};

/******************************************************************************
   Handle for functor implementations with caller signature:
   SR (*func)(S1);
******************************************************************************/

template <class SR, class S1>
class RWTHRTExport RWFunctorR1 :
   public RWTHRHandle {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef SR (*CallerSignature)(S1);
      typedef SR SRType;
      typedef S1 S1Type;

      // Construct an empty, invalid, handle instance
      RWFunctorR1(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWFunctorR1(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to a functor instance 
      RWFunctorR1(RWFunctorR1Imp<SR,S1>* functorImp)
         RWTHRTHROWSANY;
      
      // Bind a new handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctorR1(const RWFunctorR1<SR,S1>& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctorR1<SR,S1>&
      operator=(const RWFunctorR1<SR,S1>& second)
         RWTHRTHROWSANY;

      // Invoke the functor instance, if any, 
      // pointed-to by this handle instance
      SR 
      operator()(S1 s1) const
         RWTHRTHROWSANY;
         // throws RWTHRInvalidPointer if no imp assigned!

      // Get a reference to the functor instance, if any, 
      // pointed-to by this handle instance
      RWFunctorR1Imp<SR,S1>&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};


/******************************************************************************

RWFunctorR1Imp<SR,S1> Family:

   RWFunctorR1GImp<SR,S1,DR,D1>
   RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>
   RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>

   RWFunctorR1MImp<SR,S1,Callee,DR,D1>
   RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>
   RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>

******************************************************************************/

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1GImp<SR,S1,DR,D1>
   SR (*caller)(S1) -> DR (*callee)(D1)
******************************************************************************/

template <class SR, class S1, class DR, class D1>
class RWTHRTExport RWFunctorR1GImp :
   public RWFunctorR1Imp<SR,S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1);
      typedef DR (*CalleeSignature)(D1);
      typedef SR SRType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;

      static
      RWFunctorR1<SR,S1>
      make(CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR1GImp(CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
};

#define rwMakeFunctorR1G(SR,S1,DR,function,D1) \
   RWFunctorR1GImp<SR,S1,DR,D1 >::make(function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class DR, class D1>
RWFunctorR1<SR,S1>
rwMakeFunctorR1(SR (*caller)(S1),
                DR (*callee)(D1))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR1G(SR,S1,DR,callee,D1); 
}

#  endif

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>
   SR (*caller)(S1) -> DR (*callee)(D1,A1)
******************************************************************************/

template <class SR, class S1, class DR, class D1,class A1>
class RWTHRTExport RWFunctorR1GA1Imp :
   public RWFunctorR1Imp<SR,S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1);
      typedef DR (*CalleeSignature)(D1,A1);
      typedef SR SRType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef A1 A1Type;

      static
      RWFunctorR1<SR,S1>
      make(CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual 
      SR 
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR1GA1Imp(CalleeSignature function, 
                        A1 a1)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctorR1GA1(SR,S1,DR,function,D1,A1,a1) \
   RWFunctorR1GA1Imp<SR,S1,DR,D1,A1 >::make(function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class DR, class D1, class A1, class AA1>
RWFunctorR1<SR,S1>
rwMakeFunctorR1(SR (*caller)(S1),
                DR (*callee)(D1,A1),
                AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR1GA1(SR,S1,DR,callee,D1,A1,a1); 
}

#  endif

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>
   SR (*caller)(S1) -> DR (*callee)(D1,A1,A2)
******************************************************************************/

template <class SR, class S1, class DR, class D1, class A1, class A2>
class RWTHRTExport RWFunctorR1GA2Imp :
   public RWFunctorR1Imp<SR,S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1);
      typedef DR (*CalleeSignature)(D1,A1,A2);
      typedef SR SRType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctorR1<SR,S1>
      make(CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      SR 
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR1GA2Imp(CalleeSignature function,
                        A1 a1,
                        A2 a2)
         RWTHRTHROWSANY;
   
   private:
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
};

#define rwMakeFunctorR1GA2(SR,S1,DR,function,D1,A1,a1,A2,a2) \
   RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2 >::make(function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class DR, class D1, class A1, class A2, class AA1, class AA2>
RWFunctorR1<SR,S1>
rwMakeFunctorR1(SR (*caller)(S1),
                DR (*callee)(D1,A1,A2),
                AA1 a1,
                AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR1GA2(SR,S1,DR,callee,D1,A1,a1,A2,a2); 
}

#  endif

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1MImp<SR,S1,Callee,DR,D1>
   SR (*caller)(S1) -> DR (Callee::*func)(D1)
******************************************************************************/

template <class SR, class S1, class Callee, class DR, class D1>
class RWTHRTExport RWFunctorR1MImp :
   public RWFunctorR1Imp<SR,S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1);
      typedef DR (Callee::*CalleeSignature)(D1);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;

      static
      RWFunctorR1<SR,S1>
      make(Callee& callee,
           CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR1MImp(Callee& callee,
                      CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
};

#define rwMakeFunctorR1M(SR,S1,Callee,callee,DR,function,D1) \
   RWFunctorR1MImp<SR,S1,Callee,DR,D1 >::make(callee,function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class Callee, class DR, class D1>
RWFunctorR1<SR,S1>
rwMakeFunctorR1(SR (*caller)(S1),
                Callee& callee,
                DR (Callee::*function)(D1))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR1M(SR,S1,Callee,callee,DR,function,D1); 
}                        

#  endif

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>
   SR (*caller)(S1) -> DR (Callee::*func)(D1,A1)
******************************************************************************/

template <class SR, class S1, class Callee, class DR, class D1, class A1>
class RWTHRTExport RWFunctorR1MA1Imp :
   public RWFunctorR1Imp<SR,S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1);
      typedef DR (Callee::*CalleeSignature)(D1,A1);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef A1 A1Type;

      static
      RWFunctorR1<SR,S1>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(S1 s1) const
         RWTHRTHROWSANY;
   
   protected:
      RWFunctorR1MA1Imp(Callee& callee,
                        CalleeSignature function,
                        A1 a1)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctorR1MA1(SR,S1,Callee,callee,DR,function,D1,A1,a1) \
   RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1 >::make(callee,function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class Callee, class DR, class D1, class A1, class AA1>
RWFunctorR1<SR,S1>
rwMakeFunctorR1(SR (*caller)(S1),
                Callee& callee,
                DR (Callee::*function)(D1,A1),
                AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR1MA1(SR,S1,Callee,callee,DR,function,D1,A1,a1); 
}                        

#  endif

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>
   SR (*caller)(S1) -> DR (Callee::*func)(D1,A1,A2)
******************************************************************************/

template <class SR, class S1, class Callee, class DR, class D1, class A1, class A2>
class RWTHRTExport RWFunctorR1MA2Imp :
   public RWFunctorR1Imp<SR,S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1);
      typedef DR (Callee::*CalleeSignature)(D1,A1,A2);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctorR1<SR,S1>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      SR
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR1MA2Imp(Callee& callee,
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

#define rwMakeFunctorR1MA2(SR,S1,Callee,callee,DR,function,D1,A1,a1,A2,a2) \
   RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2 >::make(callee,function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class Callee, class DR, class D1, class A1, class A2, class AA1, class AA2>
RWFunctorR1<SR,S1>
rwMakeFunctorR1(SR (*caller)(S1),
                Callee& callee,
                DR (Callee::*function)(D1,A1,A2),
                AA1 a1,
                AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctorR1MA2(SR,S1,Callee,callee,DR,function,D1,A1,a1,A2,a2); 
}                        

#  endif

/*****************************************************************************/

template <class SR, class S1>
inline
RWFunctorR1<SR,S1>::RWFunctorR1(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT2(RWFunctorR1,SR,S1,"RWFunctorR1(void)");
}
      
template <class SR, class S1>
inline
RWFunctorR1<SR,S1>::RWFunctorR1(RWStaticCtor)
   RWTHRTHROWSNONE
   : 
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMFT2(RWFunctorR1,SR,S1,"RWFunctorR1(RWStaticCtor)");
}
      
template <class SR, class S1>
inline
RWFunctorR1<SR,S1>::RWFunctorR1(RWFunctorR1Imp<SR,S1>* functorImp)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(functorImp)
{
   RWTHRTRACEMFT2(RWFunctorR1,SR,S1,"RWFunctorR1(RWFunctorR1Imp<SR,S1>*)");
}
      
template <class SR, class S1>
inline
RWFunctorR1<SR,S1>::RWFunctorR1(const RWFunctorR1<SR,S1>& second)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(second)
{
   RWTHRTRACEMFT2(RWFunctorR1,SR,S1,"RWFunctorR1(const RWFunctorR1<SR,S1>&)");
}

template <class SR, class S1>
inline
RWFunctorR1<SR,S1>&
RWFunctorR1<SR,S1>::operator=(const RWFunctorR1<SR,S1>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWFunctorR1,SR,S1,"operator=(const RWFunctorR1<SR,S1>&):RWFunctorR1<SR,S1>&");
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

template <class SR, class S1>
inline
RWFunctorR1Imp<SR,S1>&
RWFunctorR1<SR,S1>::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMFT2(RWFunctorR1,SR,S1,"body(void):RWFunctorR1Imp<SR,S1>&");
   // throws RWTHRInvalidPointer if no imp assigned!
   return (RWFunctorR1Imp<SR,S1>&)RWTHRHandle::body();
}

/******************************************************************************

RWFunctorR1Imp<SR,S1> Family:

   RWFunctorR1GImp<SR,S1,DR,D1>
   RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>
   RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>

   RWFunctorR1MImp<SR,S1,Callee,DR,D1>
   RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>
   RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>

******************************************************************************/

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1GImp<SR,S1,DR,D1>
   SR (*caller)(S1) -> DR (*callee)(D1)
******************************************************************************/

template <class SR, class S1, class DR, class D1>
inline
// static
RWFunctorR1<SR,S1>
RWFunctorR1GImp<SR,S1,DR,D1>::make(CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT4(RWFunctorR1GImp,SR,S1,DR,D1,"make(CalleeSignature):RWFunctorR1<SR,S1>");
   return new RWFunctorR1GImp<SR,S1,DR,D1>(function);
}

template <class SR, class S1, class DR, class D1>
inline
RWFunctorR1GImp<SR,S1,DR,D1>::RWFunctorR1GImp(CalleeSignature function)
   RWTHRTHROWSANY
   : 
      function_(function)
{
   RWTHRTRACEMFT4(RWFunctorR1GImp,SR,S1,DR,D1,RWFunctorR1GImp(CalleeSignature));
}
    
/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>
   SR (*caller)(S1) -> DR (*callee)(D1,A1)
******************************************************************************/

template <class SR, class S1, class DR, class D1, class A1>
inline
// static
RWFunctorR1<SR,S1>
RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>::make(CalleeSignature function,
                                        A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT5(RWFunctorR1GA1Imp,SR,S1,DR,D1,A1,"make(CalleeSignature,A1):RWFunctorR1<SR,S1>");
   return new RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>(function,a1);
}

template <class SR, class S1, class DR, class D1,class A1>
inline
RWFunctorR1GA1Imp<SR,S1,DR,D1,A1>::RWFunctorR1GA1Imp(CalleeSignature function,
                                                     A1 a1)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT5(RWFunctorR1GA1Imp,SR,S1,DR,D1,A1,RWFunctorR1GA1Imp(CalleeSignature,A1));
}
      
/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>
   SR (*caller)(S1) -> DR (*callee)(D1,A1,A2)
******************************************************************************/

template <class SR, class S1, class DR, class D1, class A1, class A2>
inline
// static
RWFunctorR1<SR,S1>
RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>::make(CalleeSignature function,
                                           A1 a1,
                                           A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT6(RWFunctorR1GA2Imp,SR,S1,DR,D1,A1,A2,"make(CalleeSignature,A1,A2):RWFunctorR1<SR,S1>");
   return new RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>(function,a1,a2);
}

template <class SR, class S1, class DR, class D1, class A1, class A2>
inline
RWFunctorR1GA2Imp<SR,S1,DR,D1,A1,A2>::RWFunctorR1GA2Imp(CalleeSignature function,
                                                        A1 a1,
                                                        A2 a2)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1),
      a2_(a2)
{
   RWTHRTRACEMFT6(RWFunctorR1GA2Imp,SR,S1,DR,D1,A1,A2,RWFunctorR1GA2Imp(CalleeSignature,A1,A2));
}
      
/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1MImp<SR,S1,Callee,DR,D1>
   SR (*caller)(S1) -> DR (Callee::*func)(D1)
******************************************************************************/

template <class SR, class S1, class Callee, class DR, class D1>
inline
// static
RWFunctorR1<SR,S1>
RWFunctorR1MImp<SR,S1,Callee,DR,D1>::make(Callee& callee,
                                          CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT5(RWFunctorR1MImp,SR,S1,Callee,DR,D1,"make(Callee&,CalleeSignature):RWFunctorR1<SR,S1>");
   return new RWFunctorR1MImp<SR,S1,Callee,DR,D1>(callee,function);
}

template <class SR, class S1, class Callee, class DR, class D1>
inline
RWFunctorR1MImp<SR,S1,Callee,DR,D1>::RWFunctorR1MImp(Callee& callee,
                                                     CalleeSignature function)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function)
{
   RWTHRTRACEMFT5(RWFunctorR1MImp,SR,S1,Callee,DR,D1,RWFunctorR1MImp(Callee&,CalleeSignature));
}

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>
   SR (*caller)(S1) -> DR (Callee::*func)(D1,A1)
******************************************************************************/

template <class SR, class S1, class Callee, class DR, class D1, class A1>
inline
// static
RWFunctorR1<SR,S1>
RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>::make(Callee& callee,
                                               CalleeSignature function,
                                               A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT6(RWFunctorR1MA1Imp,SR,S1,Callee,DR,D1,A1,"make(Callee&,CalleeSignature,A1):RWFunctorR1<SR,S1>");
   return new RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>(callee,function,a1);
}

template <class SR, class S1, class Callee, class DR, class D1, class A1>
inline
RWFunctorR1MA1Imp<SR,S1,Callee,DR,D1,A1>::RWFunctorR1MA1Imp(Callee& callee,
                                                            CalleeSignature function,
                                                            A1 a1)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT6(RWFunctorR1MA1Imp,SR,S1,Callee,DR,D1,A1,RWFunctorR1MA1Imp(Callee&,CalleeSignature,A1));
}

/******************************************************************************
   RWFunctorR1Imp<SR,S1> Family:
   RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>
   SR (*caller)(S1) -> DR (Callee::*func)(D1,A1,A2)
******************************************************************************/

template <class SR, class S1, class Callee, class DR, class D1, class A1, class A2>
inline
// static
RWFunctorR1<SR,S1>
RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>::make(Callee& callee,
                                                  CalleeSignature function,
                                                  A1 a1,
                                                  A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT7(RWFunctorR1MA2Imp,SR,S1,Callee,DR,D1,A1,A2,"make(Callee&,CalleeSignature,A1,A2):RWFunctorR1<SR,S1>");
   return new RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>(callee,function,a1,a2);
}

template <class SR, class S1, class Callee, class DR, class D1, class A1, class A2>
inline
RWFunctorR1MA2Imp<SR,S1,Callee,DR,D1,A1,A2>::RWFunctorR1MA2Imp(Callee& callee,
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
   RWTHRTRACEMFT7(RWFunctorR1MA2Imp,SR,S1,Callee,DR,D1,A1,A2,RWFunctorR1MA2Imp(Callee&,CalleeSignature,A1,A2));
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/funcr1.cc>
#  endif

#endif // __RWTHRFUNCR1_H__
