#if !defined(__RWTHRFUNCR2_H__)
#  define __RWTHRFUNCR2_H__
/*****************************************************************************
 *
 * funcr2.h 
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

funcr2.h - Class declarations for:

   RWFunctorR2<SR,S1,S2> - Handle class for RWFunctorR2Imp functor family
   RWFunctorR2Imp<SR,S1,S2> - Base class body for FunctorR2 functor family

   RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>
   RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>
   RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>

   RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>
   RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>
   RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>
            
See Also:

   funcr2.cc  - Out-of-line function definitions.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor2Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if !defined(__RWTHRHANDBODY_H__)
#     include <rw/thr/handbody.h>
#  endif

/******************************************************************************
   Base class body implementation for functors with caller signature:
   SR (*func)(S1,S2);
******************************************************************************/
 
template <class SR, class S1, class S2>
class RWTHRTExport RWFunctorR2Imp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef SR (*CallerSignature)(S1,S2);
      typedef SR SRType;
      typedef S1 S1Type;
      typedef S2 S2Type;

      virtual
      SR
      run(S1 s1, S2 s2) const = 0
         RWTHRTHROWSANY;
};

/******************************************************************************
   Handle for functor implementations with caller signature:
   SR (*func)(S1,S2);
******************************************************************************/

template <class SR, class S1, class S2>
class RWTHRTExport RWFunctorR2 :
   public RWTHRHandle {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef SR (*CallerSignature)(S1,S2);
      typedef SR SRType;
      typedef S1 S1Type;
      typedef S2 S2Type;

      // Construct an empty, invalid, handle instance
      RWFunctorR2(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWFunctorR2(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to a functor instance 
      RWFunctorR2(RWFunctorR2Imp<SR,S1,S2>* functorImp)
         RWTHRTHROWSANY;
      
      // Bind a new handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctorR2(const RWFunctorR2<SR,S1,S2>& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctorR2<SR,S1,S2>&
      operator=(const RWFunctorR2<SR,S1,S2>& second)
         RWTHRTHROWSANY;

      // Invoke the functor instance, if any, 
      // pointed-to by this handle instance
      SR 
      operator()(S1 s1, S2 s2) const
         RWTHRTHROWSANY;
         // throws RWTHRInvalidPointer if no imp assigned!

      // Get a reference to the functor instance, if any, 
      // pointed-to by this handle instance
      RWFunctorR2Imp<SR,S1,S2>&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);
};


/******************************************************************************

RWFunctorR2Imp<SR,S1,S2> Family:

   RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>
   RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>
   RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>

   RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>
   RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>
   RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>

******************************************************************************/

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>
   SR (*caller)(S1,S2) -> DR (*callee)(D1,D2)
******************************************************************************/

template <class SR, class S1, class S2, class DR, class D1, class D2>
class RWTHRTExport RWFunctorR2GImp :
   public RWFunctorR2Imp<SR,S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1,S2);
      typedef DR (*CalleeSignature)(D1,D2);
      typedef SR SRType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;

      static
      RWFunctorR2<SR,S1,S2>
      make(CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR2GImp(CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
};

#define rwMakeFunctorR2G(SR,S1,S2,DR,function,D1,D2) \
   RWFunctorR2GImp<SR,S1,S2,DR,D1,D2 >::make(function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class S2, class DR, class D1, class D2>
RWFunctorR2<SR,S1,S2>
rwMakeFunctorR2(SR (*caller)(S1,S2),
                DR (*callee)(D1,D2))
{
   (void)caller;
   return rwMakeFunctorR2G(SR,S1,S2,DR,callee,D1,D2);
}

#  endif

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>
   SR (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1)
******************************************************************************/

template <class SR, class S1, class S2, class DR, class D1, class D2,class A1>
class RWTHRTExport RWFunctorR2GA1Imp :
   public RWFunctorR2Imp<SR,S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1,S2);
      typedef DR (*CalleeSignature)(D1,D2,A1);
      typedef SR SRType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;
      typedef A1 A1Type;

      static
      RWFunctorR2<SR,S1,S2>
      make(CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual 
      SR 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR2GA1Imp(CalleeSignature function, 
                        A1 a1)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctorR2GA1(SR,S1,S2,DR,function,D1,D2,A1,a1) \
   RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1 >::make(function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class S2, class DR, class D1, class D2, class A1, class AA1>
RWFunctorR2<SR,S1,S2>
rwMakeFunctorR2(SR (*caller)(S1,S2),
                DR (*callee)(D1,D2,A1),
                AA1 a1)
{
   (void)caller;
   return rwMakeFunctorR2GA1(SR,S1,S2,DR,callee,D1,D2,A1,a1); 
}

#  endif

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>
   SR (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1,A2)
******************************************************************************/

template <class SR, class S1, class S2, class DR, class D1, class D2, class A1, class A2>
class RWTHRTExport RWFunctorR2GA2Imp :
   public RWFunctorR2Imp<SR,S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1,S2);
      typedef DR (*CalleeSignature)(D1,D2,A1,A2);
      typedef SR SRType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctorR2<SR,S1,S2>
      make(CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      SR 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR2GA2Imp(CalleeSignature function,
                        A1 a1,
                        A2 a2)
         RWTHRTHROWSANY;
   
   private:
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
};

#define rwMakeFunctorR2GA2(SR,S1,S2,DR,function,D1,D2,A1,a1,A2,a2) \
   RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2 >::make(function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class S2, class DR, class D1, class D2, class A1, class A2, class AA1, class AA2>
RWFunctorR2<SR,S1,S2>
rwMakeFunctorR2(SR (*caller)(S1,S2),
                DR (*callee)(D1,D2,A1,A2),
                AA1 a1,
                AA2 a2)
{
   (void)caller;
   return rwMakeFunctorR2GA2(SR,S1,S2,DR,callee,D1,D2,A1,a1,A2,a2); 
}

#  endif

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>
   SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2)
******************************************************************************/

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2>
class RWTHRTExport RWFunctorR2MImp :
   public RWFunctorR2Imp<SR,S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1,S2);
      typedef DR (Callee::*CalleeSignature)(D1,D2);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;

      static
      RWFunctorR2<SR,S1,S2>
      make(Callee& callee,
           CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR2MImp(Callee& callee,
                      CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
};

#define rwMakeFunctorR2M(SR,S1,S2,Callee,callee,DR,function,D1,D2) \
   RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2 >::make(callee,function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2>
RWFunctorR2<SR,S1,S2>
rwMakeFunctorR2(SR (*caller)(S1,S2),
                Callee& callee,
                DR (Callee::*function)(D1,D2))
{
   (void)caller;
   return rwMakeFunctorR2M(SR,S1,S2,Callee,callee,DR,function,D1,D2); 
}                        

#  endif

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>
   SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1)
******************************************************************************/

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1>
class RWTHRTExport RWFunctorR2MA1Imp :
   public RWFunctorR2Imp<SR,S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1,S2);
      typedef DR (Callee::*CalleeSignature)(D1,D2,A1);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;
      typedef A1 A1Type;

      static
      RWFunctorR2<SR,S1,S2>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual
      SR 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;
   
   protected:
      RWFunctorR2MA1Imp(Callee& callee,
                        CalleeSignature function,
                        A1 a1)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctorR2MA1(SR,S1,S2,Callee,callee,DR,function,D1,D2,A1,a1) \
   RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1 >::make(callee,function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class AA1>
RWFunctorR2<SR,S1,S2>
rwMakeFunctorR2(SR (*caller)(S1,S2),
                Callee& callee,
                DR (Callee::*function)(D1,D2,A1),
                AA1 a1)
{
   (void)caller;
   return rwMakeFunctorR2MA1(SR,S1,S2,Callee,callee,DR,function,D1,D2,A1,a1); 
}                        

#  endif

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>
   SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1,A2)
******************************************************************************/

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2>
class RWTHRTExport RWFunctorR2MA2Imp :
   public RWFunctorR2Imp<SR,S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef SR (*CallerSignature)(S1,S2);
      typedef DR (Callee::*CalleeSignature)(D1,D2,A1,A2);
      typedef Callee   CalleeType;
      typedef SR SRType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctorR2<SR,S1,S2>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      SR
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctorR2MA2Imp(Callee& callee,
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

#define rwMakeFunctorR2MA2(SR,S1,S2,Callee,callee,DR,function,D1,D2,A1,a1,A2,a2) \
   RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2 >::make(callee,function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2, class AA1, class AA2>
RWFunctorR2<SR,S1,S2>
rwMakeFunctorR2(SR (*caller)(S1,S2),
                Callee& callee,
                DR (Callee::*function)(D1,D2,A1,A2),
                AA1 a1,
                AA2 a2)
{
   (void)caller;
   return rwMakeFunctorR2MA2(SR,S1,S2,Callee,callee,DR,function,D1,D2,A1,a1,A2,a2); 
}                        

#  endif

/*****************************************************************************/

template <class SR, class S1, class S2>
inline
RWFunctorR2<SR,S1,S2>::RWFunctorR2(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT3(RWFunctorR2,SR,S1,S2,RWFunctorR2(void));
}
      
template <class SR, class S1, class S2>
inline
RWFunctorR2<SR,S1,S2>::RWFunctorR2(RWStaticCtor)
   RWTHRTHROWSNONE
   : 
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMFT3(RWFunctorR2,SR,S1,S2,RWFunctorR2(RWStaticCtor));
}
      
template <class SR, class S1, class S2>
inline
RWFunctorR2<SR,S1,S2>::RWFunctorR2(RWFunctorR2Imp<SR,S1,S2>* functorImp)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(functorImp)
{
   RWTHRTRACEMFT3(RWFunctorR2,SR,S1,S2,"RWFunctorR2(RWFunctorR2Imp<SR,S1,S2>*)");
}
      
template <class SR, class S1, class S2>
inline
RWFunctorR2<SR,S1,S2>::RWFunctorR2(const RWFunctorR2<SR,S1,S2>& second)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(second)
{
   RWTHRTRACEMFT3(RWFunctorR2,SR,S1,S2,"RWFunctorR2(const RWFunctorR2<SR,S1,S2>&)");
}

template <class SR, class S1, class S2>
inline
RWFunctorR2<SR,S1,S2>&
RWFunctorR2<SR,S1,S2>::operator=(const RWFunctorR2<SR,S1,S2>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT3(RWFunctorR2,SR,S1,S2,"operator=(const RWFunctorR2<SR,S1,S2>&):RWFunctorR2<SR,S1,S2>&");
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

template <class SR, class S1, class S2>
inline
RWFunctorR2Imp<SR,S1,S2>&
RWFunctorR2<SR,S1,S2>::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMFT3(RWFunctorR2,SR,S1,S2,"body(void):RWFunctorR2Imp<SR,S1,S2>&");
   // throws RWTHRInvalidPointer if no imp assigned!
   return (RWFunctorR2Imp<SR,S1,S2>&)RWTHRHandle::body();
}

/******************************************************************************

RWFunctorR2Imp<SR,S1,S2> Family:

   RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>
   RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>
   RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>

   RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>
   RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>
   RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>

******************************************************************************/

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>
   SR (*caller)(S1,S2) -> DR (*callee)(D1,D2)
******************************************************************************/

template <class SR, class S1, class S2, class DR, class D1, class D2>
inline
// static
RWFunctorR2<SR,S1,S2>
RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>::make(CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT6(RWFunctorR2GImp,SR,S1,S2,DR,D1,D2,"make(CalleeSignature):RWFunctorR2<SR,S1,S2>");
   return new RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>(function);
}

template <class SR, class S1, class S2, class DR, class D1, class D2>
inline
RWFunctorR2GImp<SR,S1,S2,DR,D1,D2>::RWFunctorR2GImp(CalleeSignature function)
   RWTHRTHROWSANY
   : 
      function_(function)
{
   RWTHRTRACEMFT6(RWFunctorR2GImp,SR,S1,S2,DR,D1,D2,RWFunctorR2GImp(CalleeSignature));
}
    
/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>
   SR (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1)
******************************************************************************/

template <class SR, class S1, class S2, class DR, class D1, class D2, class A1>
inline
// static
RWFunctorR2<SR,S1,S2>
RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>::make(CalleeSignature function,
                                              A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT7(RWFunctorR2GA1Imp,SR,S1,S2,DR,D1,D2,A1,"make(CalleeSignature,A1):RWFunctorR2<SR,S1,S2>");
   return new RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>(function,a1);
}

template <class SR, class S1, class S2, class DR, class D1, class D2,class A1>
inline
RWFunctorR2GA1Imp<SR,S1,S2,DR,D1,D2,A1>::RWFunctorR2GA1Imp(CalleeSignature function,
                                                           A1 a1)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT7(RWFunctorR2GA1Imp,SR,S1,S2,DR,D1,D2,A1,RWFunctorR2GA1Imp(CalleeSignature,A1));
}
      
/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>
   SR (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1,A2)
******************************************************************************/

template <class SR, class S1, class S2, class DR, class D1, class D2, class A1, class A2>
inline
// static
RWFunctorR2<SR,S1,S2>
RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>::make(CalleeSignature function,
                                                 A1 a1,
                                                 A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT8(RWFunctorR2GA2Imp,SR,S1,S2,DR,D1,D2,A1,A2,"make(CalleeSignature,A1,A2):RWFunctorR2<SR,S1,S2>");
   return new RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>(function,a1,a2);
}

template <class SR, class S1, class S2, class DR, class D1, class D2, class A1, class A2>
inline
RWFunctorR2GA2Imp<SR,S1,S2,DR,D1,D2,A1,A2>::RWFunctorR2GA2Imp(CalleeSignature function,
                                                              A1 a1,
                                                              A2 a2)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1),
      a2_(a2)
{
   RWTHRTRACEMFT8(RWFunctorR2GA2Imp,SR,S1,S2,DR,D1,D2,A1,A2,RWFunctorR2GA2Imp(CalleeSignature,A1,A2));
}
      
/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>
   SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2)
******************************************************************************/

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2>
inline
// static
RWFunctorR2<SR,S1,S2>
RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>::make(Callee& callee,
                                                CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT7(RWFunctorR2MImp,SR,S1,S2,Callee,DR,D1,D2,"make(Callee&,CalleeSignature):RWFunctorR2<SR,S1,S2>");
   return new RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>(callee,function);
}

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2>
inline
RWFunctorR2MImp<SR,S1,S2,Callee,DR,D1,D2>::RWFunctorR2MImp(Callee& callee,
                                                           CalleeSignature function)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function)
{
   RWTHRTRACEMFT7(RWFunctorR2MImp,SR,S1,S2,Callee,DR,D1,D2,RWFunctorR2MImp(Callee&,CalleeSignature));
}

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>
   SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1)
******************************************************************************/

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1>
inline
// static
RWFunctorR2<SR,S1,S2>
RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>::make(Callee& callee,
                                                     CalleeSignature function,
                                                     A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT8(RWFunctorR2MA1Imp,SR,S1,S2,Callee,DR,D1,D2,A1,"make(Callee&,CalleeSignature,A1):RWFunctorR2<SR,S1,S2>");
   return new RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>(callee,function,a1);
}

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1>
inline
RWFunctorR2MA1Imp<SR,S1,S2,Callee,DR,D1,D2,A1>::RWFunctorR2MA1Imp(Callee& callee,
                                                                  CalleeSignature function,
                                                                  A1 a1)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT8(RWFunctorR2MA1Imp,SR,S1,S2,Callee,DR,D1,D2,A1,RWFunctorR2MA1Imp(Callee&,CalleeSignature,A1));
}

/******************************************************************************
   RWFunctorR2Imp<SR,S1,S2> Family:
   RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>
   SR (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1,A2)
******************************************************************************/

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2>
inline
// static
RWFunctorR2<SR,S1,S2>
RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>::make(Callee& callee,
                                                  CalleeSignature function,
                                                  A1 a1,
                                                  A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT9(RWFunctorR2MA2Imp,SR,S1,S2,Callee,DR,D1,D2,A1,A2,"make(Callee&,CalleeSignature,A1,A2):RWFunctorR2<SR,S1,S2>");
   return new RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>(callee,function,a1,a2);
}

template <class SR, class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2>
inline
RWFunctorR2MA2Imp<SR,S1,S2,Callee,DR,D1,D2,A1,A2>::RWFunctorR2MA2Imp(Callee& callee,
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
   RWTHRTRACEMFT9(RWFunctorR2MA2Imp,SR,S1,S2,Callee,DR,D1,D2,A1,A2,RWFunctorR2MA2Imp(Callee&,CalleeSignature,A1,A2));
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/funcr2.cc>
#  endif

#endif // __RWTHRFUNCR2_H__
