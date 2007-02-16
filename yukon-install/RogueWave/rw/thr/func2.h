#if !defined(__RWTHRFUNC2_H__)
#  define __RWTHRFUNC2_H__
/*****************************************************************************
 *
 * func2.h 
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

func2.h - Class declarations for:

   RWFunctor2<S1,S2> - Handle class for RWFunctor2Imp functor family
   RWFunctor2Imp<S1,S2> - Base class body for Functor2 functor family

   RWFunctor2GImp<S1,S2,DR,D1,D2>
   RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>
   RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>

   RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>
   RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>
   RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>
            
See Also:

   func2.cc  - Out-of-line function definitions.

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
   void (*func)(S1,S2);
******************************************************************************/
 
template <class S1, class S2>
class RWTHRTExport RWFunctor2Imp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE
      
   public:

      typedef void (*CallerSignature)(S1,S2);
      typedef S1 S1Type;
      typedef S2 S2Type;

      virtual
      void
      run(S1 s1, S2 s2) const = 0
         RWTHRTHROWSANY;
};

/******************************************************************************
   Handle for functor implementations with caller signature:
   void (*func)(S1,S2);
******************************************************************************/

template <class S1, class S2>
class RWTHRTExport RWFunctor2 :
   public RWTHRHandle {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef void (*CallerSignature)(S1,S2);
      typedef S1 S1Type;
      typedef S2 S2Type;

      // Construct an empty, invalid, handle instance
      RWFunctor2(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWFunctor2(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to a functor instance 
      RWFunctor2(RWFunctor2Imp<S1,S2>* functorImp)
         RWTHRTHROWSANY;
      
      // Bind a new handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctor2(const RWFunctor2<S1,S2>& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctor2<S1, S2>&
      operator=(const RWFunctor2<S1,S2>& second)
         RWTHRTHROWSANY;

      // Invoke the functor instance, if any, 
      // pointed-to by this handle instance
      void 
      operator()(S1 s1, S2 s2) const
         RWTHRTHROWSANY;
         // throws RWTHRInvalidPointer if no imp assigned!

      // Get a reference to the functor instance, if any, 
      // pointed-to by this handle instance
      RWFunctor2Imp<S1,S2>&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};


/******************************************************************************

RWFunctor2Imp<S1,S2> Family:

   RWFunctor2GImp<S1,S2,DR,D1,D2>
   RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>
   RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>

   RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>
   RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>
   RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>

******************************************************************************/

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2GImp<S1,S2,DR,D1,D2>
   void (*caller)(S1,S2) -> DR (*callee)(D1,D2)
******************************************************************************/

template <class S1, class S2, class DR, class D1, class D2>
class RWTHRTExport RWFunctor2GImp :
   public RWFunctor2Imp<S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1,S2);
      typedef DR (*CalleeSignature)(D1,D2);
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;

      static
      RWFunctor2<S1,S2>
      make(CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      void 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor2GImp(CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
};

#define rwMakeFunctor2G(S1,S2,DR,function,D1,D2) \
   RWFunctor2GImp<S1,S2,DR,D1,D2 >::make(function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class S2, class DR, class D1, class D2>
RWFunctor2<S1,S2>
rwMakeFunctor2(void (*caller)(S1,S2),
               DR (*callee)(D1,D2))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor2G(S1,S2,DR,callee,D1,D2); 
}

#  endif

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>
   void (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1)
******************************************************************************/

template <class S1, class S2, class DR, class D1, class D2,class A1>
class RWTHRTExport RWFunctor2GA1Imp :
   public RWFunctor2Imp<S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1,S2);
      typedef DR (*CalleeSignature)(D1,D2,A1);
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;
      typedef A1 A1Type;

      static
      RWFunctor2<S1,S2>
      make(CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor2GA1Imp(CalleeSignature function, 
                       A1 a1)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctor2GA1(S1,S2,DR,function,D1,D2,A1,a1) \
   RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1 >::make(function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class S2, class DR, class D1, class D2, class A1, class AA1>
RWFunctor2<S1,S2>
rwMakeFunctor2(void (*caller)(S1,S2),
               DR (*callee)(D1,D2,A1),
               AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor2GA1(S1,S2,DR,callee,D1,D2,A1,a1); 
}

#  endif

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>
   void (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1,A2)
******************************************************************************/

template <class S1, class S2, class DR, class D1, class D2, class A1, class A2>
class RWTHRTExport RWFunctor2GA2Imp :
   public RWFunctor2Imp<S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1,S2);
      typedef DR (*CalleeSignature)(D1,D2,A1,A2);
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctor2<S1,S2>
      make(CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor2GA2Imp(CalleeSignature function,
                       A1 a1,
                       A2 a2)
         RWTHRTHROWSANY;
   
   private:
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
};

#define rwMakeFunctor2GA2(S1,S2,DR,function,D1,D2,A1,a1,A2,a2) \
   RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2 >::make(function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class S2, class DR, class D1, class D2, class A1, class A2, class AA1, class AA2>
RWFunctor2<S1,S2>
rwMakeFunctor2(void (*caller)(S1,S2),
               DR (*callee)(D1,D2,A1,A2),
               AA1 a1,
               AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor2GA2(S1,S2,DR,callee,D1,D2,A1,a1,A2,a2); 
}

#  endif

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>
   void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2)
******************************************************************************/

template <class S1, class S2, class Callee, class DR, class D1, class D2>
class RWTHRTExport RWFunctor2MImp :
   public RWFunctor2Imp<S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1,S2);
      typedef DR (Callee::*CalleeSignature)(D1,D2);
      typedef Callee   CalleeType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;

      static
      RWFunctor2<S1,S2>
      make(Callee& callee,
           CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      void 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor2MImp(Callee& callee,
                     CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
};

#define rwMakeFunctor2M(S1,S2,Callee,callee,DR,function,D1,D2) \
   RWFunctor2MImp<S1,S2,Callee,DR,D1,D2 >::make(callee,function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class S2, class Callee, class DR, class D1, class D2>
RWFunctor2<S1,S2>
rwMakeFunctor2(void (*caller)(S1,S2),
               Callee& callee,
               DR (Callee::*function)(D1,D2))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor2M(S1,S2,Callee,callee,DR,function,D1,D2); 
}                        

#  endif

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>
   void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1)
******************************************************************************/

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1>
class RWTHRTExport RWFunctor2MA1Imp :
   public RWFunctor2Imp<S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1,S2);
      typedef DR (Callee::*CalleeSignature)(D1,D2,A1);
      typedef Callee   CalleeType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;
      typedef A1 A1Type;

      static
      RWFunctor2<S1,S2>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual
      void 
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;
   
   protected:
      RWFunctor2MA1Imp(Callee& callee,
                       CalleeSignature function,
                       A1 a1)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctor2MA1(S1,S2,Callee,callee,DR,function,D1,D2,A1,a1) \
   RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1 >::make(callee,function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class AA1>
RWFunctor2<S1,S2>
rwMakeFunctor2(void (*caller)(S1,S2),
               Callee& callee,
               DR (Callee::*function)(D1,D2,A1),
               AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor2MA1(S1,S2,Callee,callee,DR,function,D1,D2,A1,a1); 
}                        

#  endif

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>
   void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1,A2)
******************************************************************************/

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2>
class RWTHRTExport RWFunctor2MA2Imp :
   public RWFunctor2Imp<S1,S2> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1,S2);
      typedef DR (Callee::*CalleeSignature)(D1,D2,A1,A2);
      typedef Callee   CalleeType;
      typedef S1 S1Type;
      typedef S2 S2Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef D2 D2Type;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctor2<S1,S2>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      void
      run(S1 s1, S2 s2) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor2MA2Imp(Callee& callee,
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

#define rwMakeFunctor2MA2(S1,S2,Callee,callee,DR,function,D1,D2,A1,a1,A2,a2) \
   RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2 >::make(callee,function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2, class AA1, class AA2>
RWFunctor2<S1,S2>
rwMakeFunctor2(void (*caller)(S1,S2),
               Callee& callee,
               DR (Callee::*function)(D1,D2,A1,A2),
               AA1 a1,
               AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor2MA2(S1,S2,Callee,callee,DR,function,D1,D2,A1,a1,A2,a2); 
}                        

#  endif

/*****************************************************************************/

template <class S1, class S2>
inline
RWFunctor2<S1,S2>::RWFunctor2(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT2(RWFunctor2,S1,S2,RWFunctor2(void));
}

template <class S1, class S2>
inline
RWFunctor2<S1,S2>::RWFunctor2(RWStaticCtor)
   RWTHRTHROWSNONE
   : 
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMFT2(RWFunctor2,S1,S2,RWFunctor2(RWStaticCtor));
}

template <class S1, class S2>
inline
RWFunctor2<S1,S2>::RWFunctor2(RWFunctor2Imp<S1,S2>* functorImp)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(functorImp)
{
   RWTHRTRACEMFT2(RWFunctor2,S1,S2,"RWFunctor2(RWFunctor2Imp<S1,S2>*)");
}
      
template <class S1, class S2>
inline
RWFunctor2<S1,S2>::RWFunctor2(const RWFunctor2<S1,S2>& second)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(second)
{
   RWTHRTRACEMFT2(RWFunctor2,S1,S2,"RWFunctor2(const RWFunctor2<S1,S2>&)");
}

template <class S1, class S2>
inline
RWFunctor2<S1,S2>&
RWFunctor2<S1,S2>::operator=(const RWFunctor2<S1,S2>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWFunctor2,S1,S2,"operator=(const RWFunctor2<S1,S2>&):RWFunctor2<S1,S2>&");
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

template <class S1, class S2>
inline
RWFunctor2Imp<S1,S2>&
RWFunctor2<S1,S2>::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMFT2(RWFunctor2,S1,S2,"body(void):RWFunctor2Imp<S1,S2>&");
   // throws RWTHRInvalidPointer if no imp assigned!
   return (RWFunctor2Imp<S1,S2>&)RWTHRHandle::body();
}

template <class S1, class S2>
inline
void
RWFunctor2<S1,S2>::operator()(S1 s1, S2 s2) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT2(RWFunctor2,S1,S2,operator()(S1,S2):void);
   // throws RWTHRInvalidPointer if no imp assigned!
   body().run(s1, s2);
}

/******************************************************************************

RWFunctor2Imp<S1,S2> Family:

   RWFunctor2GImp<S1,S2,DR,D1,D2>
   RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>
   RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>

   RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>
   RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>
   RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>

******************************************************************************/

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2GImp<S1,S2,DR,D1,D2>
   void (*caller)(S1,S2) -> DR (*callee)(D1,D2)
******************************************************************************/

template <class S1, class S2, class DR, class D1, class D2>
inline
// static
RWFunctor2<S1,S2>
RWFunctor2GImp<S1,S2,DR,D1,D2>::make(CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT5(RWFunctor2GImp,S1,S2,DR,D1,D2,"make(CalleeSignature):RWFunctor2<S1,S2>");
   return new RWFunctor2GImp<S1,S2,DR,D1,D2>(function);
}

template <class S1, class S2, class DR, class D1, class D2>
inline
RWFunctor2GImp<S1,S2,DR,D1,D2>::RWFunctor2GImp(CalleeSignature function)
   RWTHRTHROWSANY
   : 
      function_(function)
{
   RWTHRTRACEMFT5(RWFunctor2GImp,S1,S2,DR,D1,D2,RWFunctor2GImp(CalleeSignature));
}
    
/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>
   void (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1)
******************************************************************************/

template <class S1, class S2, class DR, class D1, class D2, class A1>
inline
// static
RWFunctor2<S1,S2>
RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>::make(CalleeSignature function,
                                              A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT6(RWFunctor2GA1Imp,S1,S2,DR,D1,D2,A1,"make(CalleeSignature,A1):RWFunctor2<S1,S2>");
   return new RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>(function,a1);
}

template <class S1, class S2, class DR, class D1, class D2,class A1>
inline
RWFunctor2GA1Imp<S1,S2,DR,D1,D2,A1>::RWFunctor2GA1Imp(CalleeSignature function,
                                                      A1 a1)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT6(RWFunctor2GA1Imp,S1,S2,DR,D1,D2,A1,RWFunctor2GA1Imp(CalleeSignature,A1));
}
      
/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>
   void (*caller)(S1,S2) -> DR (*callee)(D1,D2,A1,A2)
******************************************************************************/

template <class S1, class S2, class DR, class D1, class D2, class A1, class A2>
inline
// static
RWFunctor2<S1,S2>
RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>::make(CalleeSignature function,
                                             A1 a1,
                                             A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT7(RWFunctor2GA2Imp,S1,S2,DR,D1,D2,A1,A2,"make(CalleeSignature,A1,A2):RWFunctor2<S1,S2>");
   return new RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>(function,a1,a2);
}

template <class S1, class S2, class DR, class D1, class D2, class A1, class A2>
inline
RWFunctor2GA2Imp<S1,S2,DR,D1,D2,A1,A2>::RWFunctor2GA2Imp(CalleeSignature function,
                                                         A1 a1,
                                                         A2 a2)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1),
      a2_(a2)
{
   RWTHRTRACEMFT7(RWFunctor2GA2Imp,S1,S2,DR,D1,D2,A1,A2,RWFunctor2GA2Imp(CalleeSignature,A1,A2));
}
      
/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>
   void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2)
******************************************************************************/

template <class S1, class S2, class Callee, class DR, class D1, class D2>
inline
// static
RWFunctor2<S1,S2>
RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>::make(Callee& callee,
                                            CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT6(RWFunctor2MImp,S1,S2,Callee,DR,D1,D2,"make(Callee&,CalleeSignature):RWFunctor2<S1,S2>");
   return new RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>(callee,function);
}

template <class S1, class S2, class Callee, class DR, class D1, class D2>
inline
RWFunctor2MImp<S1,S2,Callee,DR,D1,D2>::RWFunctor2MImp(Callee& callee,
                                                      CalleeSignature function)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function)
{
   RWTHRTRACEMFT6(RWFunctor2MImp,S1,S2,Callee,DR,D1,D2,RWFunctor2MImp(Callee&,CalleeSignature));
}

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>
   void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1)
******************************************************************************/

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1>
inline
// static
RWFunctor2<S1,S2>
RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>::make(Callee& callee,
                                                 CalleeSignature function,
                                                 A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT7(RWFunctor2MA1Imp,S1,S2,Callee,DR,D1,D2,A1,"make(Callee&,CalleeSignature,A1):RWFunctor2<S1,S2>");
   return new RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>(callee,function,a1);
}

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1>
inline
RWFunctor2MA1Imp<S1,S2,Callee,DR,D1,D2,A1>::RWFunctor2MA1Imp(Callee& callee,
                                                             CalleeSignature function,
                                                             A1 a1)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT7(RWFunctor2MA1Imp,S1,S2,Callee,DR,D1,D2,A1,RWFunctor2MA1Imp(Callee&,CalleeSignature,A1));
}

/******************************************************************************
   RWFunctor2Imp<S1,S2> Family:
   RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>
   void (*caller)(S1,S2) -> DR (Callee::*func)(D1,D2,A1,A2)
******************************************************************************/

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2>
inline
// static
RWFunctor2<S1,S2>
RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>::make(Callee& callee,
                                                    CalleeSignature function,
                                                    A1 a1,
                                                    A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT8(RWFunctor2MA2Imp,S1,S2,Callee,DR,D1,D2,A1,A2,"make(Callee&,CalleeSignature,A1,A2):RWFunctor2<S1,S2>");
   return new RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>(callee,function,a1,a2);
}

template <class S1, class S2, class Callee, class DR, class D1, class D2, class A1, class A2>
inline
RWFunctor2MA2Imp<S1,S2,Callee,DR,D1,D2,A1,A2>::RWFunctor2MA2Imp(Callee& callee,
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
   RWTHRTRACEMFT8(RWFunctor2MA2Imp,S1,S2,Callee,DR,D1,D2,A1,A2,RWFunctor2MA2Imp(Callee&,CalleeSignature,A1,A2));
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/func2.cc>
#  endif

#endif // __RWTHRFUNC2_H__
