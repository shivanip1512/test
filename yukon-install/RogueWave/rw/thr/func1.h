#if !defined(__RWTHRFUNC1_H__)
#  define __RWTHRFUNC1_H__
/*****************************************************************************
 *
 * func1.h 
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

func1.h - Class declarations for:

   RWFunctor1<S1> - Handle class for RWFunctor1Imp functor family
   RWFunctor1Imp<S1> - Base class body for Functor1 functor family

   RWFunctor1GImp<S1,DR,D1>
   RWFunctor1GA1Imp<S1,DR,D1,A1>
   RWFunctor1GA2Imp<S1,DR,D1,A1,A2>

   RWFunctor1MImp<S1,Callee,DR,D1>
   RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>
   RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>
            
See Also:

   func1.cc  - Out-of-line function definitions.

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
   void (*func)(S1);
******************************************************************************/
 
template <class S1>
class RWTHRTExport RWFunctor1Imp :
   public RWTHRBody {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(S1);
      typedef S1 S1Type;

      virtual
      void
      run(S1 s1) const = 0
         RWTHRTHROWSANY;
};

/******************************************************************************
   Handle for functor implementations with caller signature:
   void (*func)(S1);
******************************************************************************/

template <class S1>
class RWTHRTExport RWFunctor1 :
   public RWTHRHandle {
   
   RW_THR_DECLARE_TRACEABLE

   public:
   
      typedef void (*CallerSignature)(S1);
      typedef S1 S1Type;

      // Construct an empty, invalid, handle instance
      RWFunctor1(void)
         RWTHRTHROWSNONE;

      // Construct a global static handle instance
      // (may be used before constructed)
      RWFunctor1(RWStaticCtor)
         RWTHRTHROWSNONE;

      // Bind a new handle instance to a functor instance 
      RWFunctor1(RWFunctor1Imp<S1>* functorImp)
         RWTHRTHROWSANY;

      // Bind a new handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctor1(const RWFunctor1<S1>& second)
         RWTHRTHROWSANY;

      // Bind this handle instance to the functor instance, if any,
      // pointed-to by another handle instance
      RWFunctor1<S1>&
      operator=(const RWFunctor1<S1>& second)
         RWTHRTHROWSANY;

      // Invoke the functor instance, if any, 
      // pointed-to by this handle instance
      void 
      operator()(S1 s1) const
         RWTHRTHROWSANY;
         // throws RWTHRInvalidPointer if no imp assigned!

      // Get a reference to the functor instance, if any, 
      // pointed-to by this handle instance
      RWFunctor1Imp<S1>&
      body(void) const
         RWTHRTHROWS1(RWTHRInvalidPointer);

};


/******************************************************************************

RWFunctor1Imp<S1> Family:

   RWFunctor1GImp<S1,DR,D1>
   RWFunctor1GA1Imp<S1,DR,D1,A1>
   RWFunctor1GA2Imp<S1,DR,D1,A1,A2>

   RWFunctor1MImp<S1,Callee,DR,D1>
   RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>
   RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>

******************************************************************************/

/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1GImp<S1,DR,D1>
   void (*caller)(S1) -> DR (*callee)(D1)
******************************************************************************/

template <class S1, class DR, class D1>
class RWTHRTExport RWFunctor1GImp :
   public RWFunctor1Imp<S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1);
      typedef DR (*CalleeSignature)(D1);
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;

      static
      RWFunctor1<S1>
      make(CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      void 
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor1GImp(CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
};

#define rwMakeFunctor1G(S1,DR,function,D1) \
   RWFunctor1GImp<S1,DR,D1 >::make(function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class DR, class D1>
RWFunctor1<S1>
rwMakeFunctor1(void (*caller)(S1),
               DR (*callee)(D1))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor1G(S1,DR,callee,D1); 
}

#  endif

/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1GA1Imp<S1,DR,D1,A1>
   void (*caller)(S1) -> DR (*callee)(D1,A1)
******************************************************************************/

template <class S1, class DR, class D1,class A1>
class RWTHRTExport RWFunctor1GA1Imp :
   public RWFunctor1Imp<S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1);
      typedef DR (*CalleeSignature)(D1,A1);
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef A1 A1Type;

      static
      RWFunctor1<S1>
      make(CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor1GA1Imp(CalleeSignature function, 
                       A1 a1)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctor1GA1(S1,DR,function,D1,A1,a1) \
   RWFunctor1GA1Imp<S1,DR,D1,A1 >::make(function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class DR, class D1, class A1, class AA1>
RWFunctor1<S1>
rwMakeFunctor1(void (*caller)(S1),
               DR (*callee)(D1,A1),
               AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor1GA1(S1,DR,callee,D1,A1,a1); 
}

#  endif

/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1GA2Imp<S1,DR,D1,A1,A2>
   void (*caller)(S1) -> DR (*callee)(D1,A1,A2)
******************************************************************************/

template <class S1, class DR, class D1, class A1, class A2>
class RWTHRTExport RWFunctor1GA2Imp :
   public RWFunctor1Imp<S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1);
      typedef DR (*CalleeSignature)(D1,A1,A2);
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctor1<S1>
      make(CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor1GA2Imp(CalleeSignature function,
                       A1 a1,
                       A2 a2)
         RWTHRTHROWSANY;
   
   private:
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
};

#define rwMakeFunctor1GA2(S1,DR,function,D1,A1,a1,A2,a2) \
   RWFunctor1GA2Imp<S1,DR,D1,A1,A2 >::make(function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class DR, class D1, class A1, class A2, class AA1, class AA2>
RWFunctor1<S1>
rwMakeFunctor1(void (*caller)(S1),
               DR (*callee)(D1,A1,A2),
               AA1 a1,
               AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor1GA2(S1,DR,callee,D1,A1,a1,A2,a2); 
}

#  endif

/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1MImp<S1,Callee,DR,D1>
   void (*caller)(S1) -> DR (Callee::*func)(D1)
******************************************************************************/

template <class S1, class Callee, class DR, class D1>
class RWTHRTExport RWFunctor1MImp :
   public RWFunctor1Imp<S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1);
      typedef DR (Callee::*CalleeSignature)(D1);
      typedef Callee   CalleeType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;

      static
      RWFunctor1<S1>
      make(Callee& callee,
           CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      void 
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor1MImp(Callee& callee,
                     CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
};

#define rwMakeFunctor1M(S1,Callee,callee,DR,function,D1) \
   RWFunctor1MImp<S1,Callee,DR,D1 >::make(callee,function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class Callee, class DR, class D1>
RWFunctor1<S1>
rwMakeFunctor1(void (*caller)(S1),
               Callee& callee,
               DR (Callee::*function)(D1))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor1M(S1,Callee,callee,DR,function,D1); 
}                        

#  endif


/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>
   void (*caller)(S1) -> DR (Callee::*func)(D1,A1)
******************************************************************************/

template <class S1, class Callee, class DR, class D1, class A1>
class RWTHRTExport RWFunctor1MA1Imp :
   public RWFunctor1Imp<S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1);
      typedef DR (Callee::*CalleeSignature)(D1,A1);
      typedef Callee   CalleeType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef A1 A1Type;

      static
      RWFunctor1<S1>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual
      void 
      run(S1 s1) const
         RWTHRTHROWSANY;
   
   protected:
      RWFunctor1MA1Imp(Callee& callee,
                       CalleeSignature function,
                       A1 a1);

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctor1MA1(S1,Callee,callee,DR,function,D1,A1,a1) \
   RWFunctor1MA1Imp<S1,Callee,DR,D1,A1 >::make(callee,function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class Callee, class DR, class D1, class A1, class AA1>
RWFunctor1<S1>
rwMakeFunctor1(void (*caller)(S1),
               Callee& callee,
               DR (Callee::*function)(D1,A1),
               AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor1MA1(S1,Callee,callee,DR,function,D1,A1,a1); 
}                        

#  endif

/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>
   void (*caller)(S1) -> DR (Callee::*func)(D1,A1,A2)
******************************************************************************/

template <class S1, class Callee, class DR, class D1, class A1, class A2>
class RWTHRTExport RWFunctor1MA2Imp :
   public RWFunctor1Imp<S1> {

   RW_THR_DECLARE_TRACEABLE

   public:
      typedef void (*CallerSignature)(S1);
      typedef DR (Callee::*CalleeSignature)(D1,A1,A2);
      typedef Callee   CalleeType;
      typedef S1 S1Type;
      typedef DR DRType;
      typedef D1 D1Type;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctor1<S1>
      make(Callee& callee,
           CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      void
      run(S1 s1) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor1MA2Imp(Callee& callee,
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

#define rwMakeFunctor1MA2(S1,Callee,callee,DR,function,D1,A1,a1,A2,a2) \
   RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2 >::make(callee,function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class S1, class Callee, class DR, class D1, class A1, class A2, class AA1, class AA2>
RWFunctor1<S1>
rwMakeFunctor1(void (*caller)(S1),
               Callee& callee,
               DR (Callee::*function)(D1,A1,A2),
               AA1 a1,
               AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor1MA2(S1,Callee,callee,DR,function,D1,A1,a1,A2,a2); 
}                        

#  endif

/*****************************************************************************/

template <class S1>
inline
RWFunctor1<S1>::RWFunctor1(void)
   RWTHRTHROWSNONE
{
   RWTHRTRACEMFT1(RWFunctor1,S1,RWFunctor1(void));
}
      
template <class S1>
inline
RWFunctor1<S1>::RWFunctor1(RWStaticCtor)
   RWTHRTHROWSNONE
   : 
      RWTHRHandle(RW_STATIC_CTOR)
{
   RWTHRTRACEMFT1(RWFunctor1,S1,RWFunctor1(RWStaticCtor));
}
      
template <class S1>
inline
RWFunctor1<S1>::RWFunctor1(RWFunctor1Imp<S1>* functorImp)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(functorImp)
{
   RWTHRTRACEMFT1(RWFunctor1,S1,RWFunctor1(RWFunctor1Imp<S1>*));
}
      
template <class S1>
inline
RWFunctor1<S1>::RWFunctor1(const RWFunctor1<S1>& second)
   RWTHRTHROWSANY
   : 
      RWTHRHandle(second)
{
   RWTHRTRACEMFT1(RWFunctor1,S1,RWFunctor1(const RWFunctor1<S1>&));
}

template <class S1>
inline
RWFunctor1<S1>&
RWFunctor1<S1>::operator=(const RWFunctor1<S1>& second)
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWFunctor1,S1,operator=(const RWFunctor1<S1>&):RWFunctor1<S1>&);
   if (&second != this)
      RWTHRHandle::operator=(second);
   return *this;
}

template <class S1>
inline
RWFunctor1Imp<S1>&
RWFunctor1<S1>::body(void) const
   RWTHRTHROWS1(RWTHRInvalidPointer)
{
   RWTHRTRACEMF(RWFunctor1<S1>,body(void):RWFunctor1Imp<S1>&);
   // throws RWTHRInvalidPointer if no imp assigned!
   return (RWFunctor1Imp<S1>&)RWTHRHandle::body();
}

template <class S1>
inline
void
RWFunctor1<S1>::operator()(S1 s1) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMFT1(RWFunctor1,S1,operator()(S1):void);
   // throws RWTHRInvalidPointer if no imp assigned!
   body().run(s1);
}

/******************************************************************************

RWFunctor1Imp<S1> Family:

   RWFunctor1GImp<S1,DR,D1>
   RWFunctor1GA1Imp<S1,DR,D1,A1>
   RWFunctor1GA2Imp<S1,DR,D1,A1,A2>

   RWFunctor1MImp<S1,Callee,DR,D1>
   RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>
   RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>

******************************************************************************/

/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1GImp<S1,DR,D1>
   void (*caller)(S1) -> DR (*callee)(D1)
******************************************************************************/

template <class S1, class DR, class D1>
inline
// static
RWFunctor1<S1>
RWFunctor1GImp<S1,DR,D1>::make(CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT3(RWFunctor1GImp,S1,DR,D1,make(CalleeSignature):RWFunctor1<S1>);
   return new RWFunctor1GImp<S1,DR,D1>(function);
}

template <class S1, class DR, class D1>
inline
RWFunctor1GImp<S1,DR,D1>::RWFunctor1GImp(CalleeSignature function)
   RWTHRTHROWSANY
   : 
      function_(function)
{
   RWTHRTRACEMFT3(RWFunctor1GImp,S1,DR,D1,RWFunctor1GImp(CalleeSignature));
}
    
/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1GA1Imp<S1,DR,D1,A1>
   void (*caller)(S1) -> DR (*callee)(D1,A1)
******************************************************************************/

template <class S1, class DR, class D1, class A1>
inline
// static
RWFunctor1<S1>
RWFunctor1GA1Imp<S1,DR,D1,A1>::make(CalleeSignature function,
                                    A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT4(RWFunctor1GA1Imp,S1,DR,D1,A1,make(CalleeSignature,A1):RWFunctor1<S1>);
   return new RWFunctor1GA1Imp<S1,DR,D1,A1>(function,a1);
}

template <class S1, class DR, class D1,class A1>
inline
RWFunctor1GA1Imp<S1,DR,D1,A1>::RWFunctor1GA1Imp(CalleeSignature function,
                                                A1 a1)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT4(RWFunctor1GA1Imp,S1,DR,D1,A1,RWFunctor1GA1Imp(CalleeSignature,A1));
}
      
/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1GA2Imp<S1,DR,D1,A1,A2>
   void (*caller)(S1) -> DR (*callee)(D1,A1,A2)
******************************************************************************/

template <class S1, class DR, class D1, class A1, class A2>
inline
// static
RWFunctor1<S1>
RWFunctor1GA2Imp<S1,DR,D1,A1,A2>::make(CalleeSignature function,
                                       A1 a1,
                                       A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT5(RWFunctor1GA2Imp,S1,DR,D1,A1,A2,make(CalleeSignature,A1,A2):RWFunctor1<S1>);
   return new RWFunctor1GA2Imp<S1,DR,D1,A1,A2>(function,a1,a2);
}

template <class S1, class DR, class D1, class A1, class A2>
inline
RWFunctor1GA2Imp<S1,DR,D1,A1,A2>::RWFunctor1GA2Imp(CalleeSignature function,
                                                   A1 a1,
                                                   A2 a2)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1),
      a2_(a2)
{
   RWTHRTRACEMFT5(RWFunctor1GA2Imp,S1,DR,D1,A1,A2,RWFunctor1GA2Imp(CalleeSignature,A1,A2));
}
      
/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1MImp<S1,Callee,DR,D1>
   void (*caller)(S1) -> DR (Callee::*func)(D1)
******************************************************************************/

template <class S1, class Callee, class DR, class D1>
inline
// static
RWFunctor1<S1>
RWFunctor1MImp<S1,Callee,DR,D1>::make(Callee& callee,
                                      CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT4(RWFunctor1MImp,S1,Callee,DR,D1,make(Callee&,CalleeSignature):RWFunctor1<S1>);
   return new RWFunctor1MImp<S1,Callee,DR,D1>(callee,function);
}

template <class S1, class Callee, class DR, class D1>
inline
RWFunctor1MImp<S1,Callee,DR,D1>::RWFunctor1MImp(Callee& callee,
                                                CalleeSignature function)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function)
{
   RWTHRTRACEMFT4(RWFunctor1MImp,S1,Callee,DR,D1,RWFunctor1MImp(Callee&,CalleeSignature));
}

/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>
   void (*caller)(S1) -> DR (Callee::*func)(D1,A1)
******************************************************************************/

template <class S1, class Callee, class DR, class D1, class A1>
inline
// static
RWFunctor1<S1>
RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>::make(Callee& callee,
                                           CalleeSignature function,
                                           A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT5(RWFunctor1MA1Imp,S1,Callee,DR,D1,A1,make(Callee&,CalleeSignature,A1):RWFunctor1<S1>);
   return new RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>(callee,function,a1);
}

template <class S1, class Callee, class DR, class D1, class A1>
inline
RWFunctor1MA1Imp<S1,Callee,DR,D1,A1>::RWFunctor1MA1Imp(Callee& callee,
                                                       CalleeSignature function,
                                                       A1 a1)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT5(RWFunctor1MA1Imp,S1,Callee,DR,D1,A1,RWFunctor1MA1Imp(Callee&,CalleeSignature,A1));
}

/******************************************************************************
   RWFunctor1Imp<S1> Family:
   RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>
   void (*caller)(S1) -> DR (Callee::*func)(D1,A1,A2)
******************************************************************************/

template <class S1, class Callee, class DR, class D1, class A1, class A2>
inline
// static
RWFunctor1<S1>
RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>::make(Callee& callee,
                                              CalleeSignature function,
                                              A1 a1,
                                              A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT6(RWFunctor1MA2Imp,S1,Callee,DR,D1,A1,A2,make(Callee&,CalleeSignature,A1,A2):RWFunctor1<S1>);
   return new RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>(callee,function,a1,a2);
}

template <class S1, class Callee, class DR, class D1, class A1, class A2>
inline
RWFunctor1MA2Imp<S1,Callee,DR,D1,A1,A2>::RWFunctor1MA2Imp(Callee& callee,
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
   RWTHRTRACEMFT6(RWFunctor1MA2Imp,S1,Callee,DR,D1,A1,A2,RWFunctor1MA2Imp(Callee&,CalleeSignature,A1,A2));
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/func1.cc>
#  endif

#endif // __RWTHRFUNC1_H__
