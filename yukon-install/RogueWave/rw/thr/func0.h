#if !defined(__RWTHRFUNC0_H__)
#  define __RWTHRFUNC0_H__
/*****************************************************************************
 *
 * func0.h 
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

func0.h - Class declarations for:

   RWFunctor0GImp<DR>
   RWFunctor0GA1Imp<DR,A1>
   RWFunctor0GA2Imp<DR,A1,A2>
   RWFunctor0GA3Imp<DR,A1,A2,A3>

   RWFunctor0MImp<Callee,DR>
   RWFunctor0MA1Imp<Callee,DR,A1>
   RWFunctor0MA2Imp<Callee,DR,A1,A2>
   RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>
            
See Also:

   func0.cc  - Out-of-line function definitions.

   functor0.h - Declares RWFunctor0 and RWFunctor0Imp classes.

   funcbase.h - Declares RWFunctorImpBase.

   functor.h - Includes all of the following include files:

      func0.h - RWFunctor0Imp family of functor classes:
      funcr0.h - RWFunctorR0Imp family of functor classes:
      func1.h - RWFunctor1Imp family of functor classes:
      funcr1.h - RWFunctorR1Imp family of functor classes:
      func2.h - RWFunctor2Imp family of functor classes:
      funcr2.h - RWFunctorR2Imp family of functor classes:
   
******************************************************************************/

#  if !defined(__RWTHRFUNCTOR0_H__)
#     include <rw/thr/functor0.h>
#  endif

/******************************************************************************

RWFunctor0Imp Family:

   RWFunctor0GImp<DR>
   RWFunctor0GA1Imp<DR,A1>
   RWFunctor0GA2Imp<DR,A1,A2>
   RWFunctor0GA3Imp<DR,A1,A2,A3>

   RWFunctor0MImp<Callee,DR>
   RWFunctor0MA1Imp<Callee,DR,A1>
   RWFunctor0MA2Imp<Callee,DR,A1,A2>
   RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>

******************************************************************************/

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0GImp<DR>
   void (*caller)(void) -> DR (*callee)(void)
******************************************************************************/

template <class DR>
class RWTHRTExport RWFunctor0GImp :
   public RWFunctor0Imp {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(void);
      typedef DR (*CalleeSignature)(void);
      typedef DR DRType;

      static
      RWFunctor0
      make(CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      void 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor0GImp(CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
};

#define rwMakeFunctor0G(DR,function) \
   RWFunctor0GImp<DR >::make(function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class DR>
RWFunctor0
rwMakeFunctor0(void (*caller)(void),
               DR (*callee)(void))
{
   (void)caller;
   return rwMakeFunctor0G(DR,callee);
}

#  endif

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0GA1Imp<DR,A1>
   void (*caller)(void) -> DR (*callee)(A1)
******************************************************************************/

template <class DR,class A1>
class RWTHRTExport RWFunctor0GA1Imp :
   public RWFunctor0Imp {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(void);
      typedef DR (*CalleeSignature)(A1);
      typedef DR DRType;
      typedef A1 A1Type;

      static
      RWFunctor0
      make(CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor0GA1Imp(CalleeSignature function, 
                       A1 a1)
         RWTHRTHROWSANY;

   private:
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctor0GA1(DR,function,A1,a1) \
   RWFunctor0GA1Imp<DR,A1 >::make(function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class DR, class A1, class AA1>
RWFunctor0
rwMakeFunctor0(void (*caller)(void),
               DR (*callee)(A1),
               AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor0GA1(DR,callee,A1,a1);
}

#  endif

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0GA2Imp<DR,A1,A2>
   void (*caller)(void) -> DR (*callee)(A1,A2)
******************************************************************************/

template <class DR, class A1, class A2>
class RWTHRTExport RWFunctor0GA2Imp :
   public RWFunctor0Imp {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(void);
      typedef DR (*CalleeSignature)(A1,A2);
      typedef DR DRType;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctor0
      make(CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor0GA2Imp(CalleeSignature function,
                     A1 a1,
                     A2 a2)
         RWTHRTHROWSANY;
   
   private:
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
};

#define rwMakeFunctor0GA2(DR,function,A1,a1,A2,a2) \
   RWFunctor0GA2Imp<DR,A1,A2 >::make(function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class DR, class A1, class A2, class AA1, class AA2>
RWFunctor0
rwMakeFunctor0(void (*caller)(void),
               DR (*callee)(A1,A2),
               AA1 a1,
               AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor0GA2(DR,callee,A1,a1,A2,a2);
}

#  endif

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0GA3Imp<DR,A1,A2,A3>
   void (*caller)(void) -> DR (*callee)(A1,A2,A3)
******************************************************************************/

template <class DR, class A1, class A2, class A3>
class RWTHRTExport RWFunctor0GA3Imp :
   public RWFunctor0Imp {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(void);
      typedef DR (*CalleeSignature)(A1,A2,A3);
      typedef DR DRType;
      typedef A1 A1Type;
      typedef A2 A2Type;
      typedef A3 A3Type;

      static
      RWFunctor0
      make(CalleeSignature function,
           A1 a1,
           A2 a2,
           A3 a3)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor0GA3Imp(CalleeSignature function,
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

#define rwMakeFunctor0GA3(DR,function,A1,a1,A2,a2,A3,a3) \
   RWFunctor0GA3Imp<DR,A1,A2,A3 >::make(function,a1,a2,a3)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class DR, class A1, class A2, class A3, class AA1, class AA2, class AA3>
RWFunctor0
rwMakeFunctor0(void (*caller)(void),
               DR (*callee)(A1,A2,A3),
               AA1 a1,
               AA2 a2,
               AA3 a3)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor0GA3(DR,callee,A1,a1,A2,a2,A3,a3);
}

#  endif

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0MImp<Callee,DR>
   void (*caller)(void) -> DR (Callee::*func)(void)
******************************************************************************/

template <class Callee, class DR>
class RWTHRTExport RWFunctor0MImp :
   public RWFunctor0Imp {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(void);
      typedef DR (Callee::*CalleeSignature)(void);
      typedef Callee   CalleeType;
      typedef DR DRType;

      static
      RWFunctor0
      make(Callee& callee,
           CalleeSignature function)
         RWTHRTHROWSANY;

      virtual
      void 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor0MImp(Callee& callee,
                     CalleeSignature function)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
};

#define rwMakeFunctor0M(Callee,callee,DR,function) \
   RWFunctor0MImp<Callee,DR >::make(callee,function)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class Callee, class DR>
RWFunctor0
rwMakeFunctor0(void (*caller)(void),
               Callee& callee,
               DR (Callee::*function)(void))
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor0M(Callee,callee,DR,function); 
}                        

#  endif


/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0MA1Imp<Callee,DR,A1>
   void (*caller)(void) -> DR (Callee::*func)(A1)
******************************************************************************/

template <class Callee, class DR, class A1>
class RWTHRTExport RWFunctor0MA1Imp :
   public RWFunctor0Imp {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(void);
      typedef DR (Callee::*CalleeSignature)(A1);
      typedef Callee   CalleeType;
      typedef DR DRType;
      typedef A1 A1Type;

      static
      RWFunctor0
      make(Callee& callee,
           CalleeSignature function,
           A1 a1)
         RWTHRTHROWSANY;

      virtual
      void 
      run(void) const
         RWTHRTHROWSANY;
   
   protected:
      RWFunctor0MA1Imp(Callee& callee,
                       CalleeSignature function,
                       A1 a1)
         RWTHRTHROWSANY;

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
};

#define rwMakeFunctor0MA1(Callee,callee,DR,function,A1,a1) \
   RWFunctor0MA1Imp<Callee,DR,A1 >::make(callee,function,a1)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class Callee, class DR, class A1, class AA1>
RWFunctor0
rwMakeFunctor0(void (*caller)(void),
               Callee& callee,
               DR (Callee::*function)(A1),
               AA1 a1)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor0MA1(Callee,callee,DR,function,A1,a1);
}                        

#  endif

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0MA2Imp<Callee,DR,A1,A2>
   void (*caller)(void) -> DR (Callee::*func)(A1,A2)
******************************************************************************/

template <class Callee, class DR, class A1, class A2>
class RWTHRTExport RWFunctor0MA2Imp :
   public RWFunctor0Imp {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(void);
      typedef DR (Callee::*CalleeSignature)(A1,A2);
      typedef Callee   CalleeType;
      typedef DR DRType;
      typedef A1 A1Type;
      typedef A2 A2Type;

      static
      RWFunctor0
      make(Callee& callee,
           CalleeSignature function,
           A1 a1,
           A2 a2)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor0MA2Imp(Callee& callee,
                       CalleeSignature function,
                       A1 a1,
                       A2 a2);

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
};

#define rwMakeFunctor0MA2(Callee,callee,DR,function,A1,a1,A2,a2) \
   RWFunctor0MA2Imp<Callee,DR,A1,A2 >::make(callee,function,a1,a2)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class Callee, class DR, class A1, class A2, class AA1, class AA2>
RWFunctor0
rwMakeFunctor0(void (*caller)(void),
               Callee& callee,
               DR (Callee::*function)(A1,A2),
               AA1 a1,
               AA2 a2)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor0MA2(Callee,callee,DR,function,A1,a1,A2,a2); 
}                        

#  endif

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>
   void (*caller)(void) -> DR (Callee::*func)(A1,A2,A3)
******************************************************************************/

template <class Callee, class DR, class A1, class A2, class A3>
class RWTHRTExport RWFunctor0MA3Imp :
   public RWFunctor0Imp {

   RW_THR_DECLARE_TRACEABLE
      
   public:
      typedef void (*CallerSignature)(void);
      typedef DR (Callee::*CalleeSignature)(A1,A2,A3);
      typedef Callee   CalleeType;
      typedef DR DRType;
      typedef A1 A1Type;
      typedef A2 A2Type;
      typedef A3 A3Type;

      static
      RWFunctor0
      make(Callee& callee,
           CalleeSignature function,
           A1 a1,
           A2 a2,
           A3 a3)
         RWTHRTHROWSANY;

      virtual 
      void 
      run(void) const
         RWTHRTHROWSANY;

   protected:
      RWFunctor0MA3Imp(Callee& callee,
                       CalleeSignature function,
                       A1 a1,
                       A2 a2,
                       A3 a3);

   private:
      Callee&           callee_;
      CalleeSignature   function_;
      A1                a1_;
      A2                a2_;
      A3                a3_;
};

#define rwMakeFunctor0MA3(Callee,callee,DR,function,A1,a1,A2,a2,A3,a3) \
   RWFunctor0MA3Imp<Callee,DR,A1,A2,A3 >::make(callee,function,a1,a2,a3)

#  if !defined(RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE)

template <class Callee, class DR, class A1, class A2, class A3, class AA1, class AA2, class AA3>
RWFunctor0
rwMakeFunctor0(void (*caller)(void),
               Callee& callee,
               DR (Callee::*function)(A1,A2,A3),
               AA1 a1,
               AA2 a2,
               AA3 a3)
   RWTHRTHROWSANY
{
   (void)caller;
   return rwMakeFunctor0MA3(Callee,callee,DR,function,A1,a1,A2,a2,A3,a3); 
}                        

#  endif

/******************************************************************************

RWFunctor0Imp Family:

   RWFunctor0GImp<DR>
   RWFunctor0GA1Imp<DR,A1>
   RWFunctor0GA2Imp<DR,A1,A2>
   RWFunctor0GA3Imp<DR,A1,A2,A3>

   RWFunctor0MImp<Callee,DR>
   RWFunctor0MA1Imp<Callee,DR,A1>
   RWFunctor0MA2Imp<Callee,DR,A1,A2>
   RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>

******************************************************************************/

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0GImp<DR>
   void (*caller)(void) -> DR (*callee)(void)
******************************************************************************/

template <class DR>
inline
// static
RWFunctor0
RWFunctor0GImp<DR>::make(CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT1(RWFunctor0GImp,DR,make(CalleeSignature):RWFunctor0);
   return new RWFunctor0GImp<DR>(function);
}

template <class DR>
inline
RWFunctor0GImp<DR>::RWFunctor0GImp(CalleeSignature function)
   RWTHRTHROWSANY
   : 
      function_(function)
{
   RWTHRTRACEMFT1(RWFunctor0GImp,DR,RWFunctor0GImp(CalleeSignature));
}
    
/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0GA1Imp<DR,A1>
   void (*caller)(void) -> DR (*callee)(A1)
******************************************************************************/

template <class DR, class A1>
inline
// static
RWFunctor0
RWFunctor0GA1Imp<DR,A1>::make(CalleeSignature function,
                              A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT2(RWFunctor0GA1Imp,DR,A1,make(CalleeSignature,A1):RWFunctor0);
   return new RWFunctor0GA1Imp<DR,A1>(function,a1);
}

template <class DR,class A1>
inline
RWFunctor0GA1Imp<DR,A1>::RWFunctor0GA1Imp(CalleeSignature function,
                                          A1 a1)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT2(RWFunctor0GA1Imp,DR,A1,RWFunctor0GA1Imp(CalleeSignature,A1));
}
      
/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0GA2Imp<DR,A1,A2>
   void (*caller)(void) -> DR (*callee)(A1,A2)
******************************************************************************/

template <class DR, class A1, class A2>
inline
// static
RWFunctor0
RWFunctor0GA2Imp<DR,A1,A2>::make(CalleeSignature function,
                                 A1 a1,
                                 A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT3(RWFunctor0GA2Imp,DR,A1,A2,make(CalleeSignature,A1,A2):RWFunctor0);
   return new RWFunctor0GA2Imp<DR,A1,A2>(function,a1,a2);
}

template <class DR, class A1, class A2>
inline
RWFunctor0GA2Imp<DR,A1,A2>::RWFunctor0GA2Imp(CalleeSignature function,
                                             A1 a1,
                                             A2 a2)
   RWTHRTHROWSANY
   : 
      function_(function),
      a1_(a1),
      a2_(a2)
{
   RWTHRTRACEMFT3(RWFunctor0GA2Imp,DR,A1,A2,RWFunctor0GA2Imp(CalleeSignature,A1,A2));
}
      
/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0GA3Imp<DR,A1,A2,A3>
   void (*caller)(void) -> DR (*callee)(A1,A2,A3)
******************************************************************************/

template <class DR, class A1, class A2, class A3>
inline
// static
RWFunctor0
RWFunctor0GA3Imp<DR,A1,A2,A3>::make(CalleeSignature function,
                                    A1 a1,
                                    A2 a2,
                                    A3 a3)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT4(RWFunctor0GA3Imp,DR,A1,A2,A3,make(CalleeSignature,A1,A2,A3):RWFunctor0);
   return new RWFunctor0GA3Imp<DR,A1,A2,A3>(function,a1,a2,a3);
}

template <class DR, class A1, class A2, class A3>
inline
RWFunctor0GA3Imp<DR,A1,A2,A3>::RWFunctor0GA3Imp(CalleeSignature function,
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
   RWTHRTRACEMFT4(RWFunctor0GA3Imp,DR,A1,A2,A3,RWFunctor0GA3Imp(CalleeSignature,A1,A2,A3));
}
      
/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0MImp<Callee,DR>
   void (*caller)(void) -> DR (Callee::*func)(void)
******************************************************************************/

template <class Callee, class DR>
inline
// static
RWFunctor0
RWFunctor0MImp<Callee,DR>::make(Callee& callee,
                                CalleeSignature function)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT2(RWFunctor0MImp,Callee,DR,make(Callee&,CalleeSignature):RWFunctor0);
   return new RWFunctor0MImp<Callee,DR>(callee,function);
}

template <class Callee, class DR>
inline
RWFunctor0MImp<Callee,DR>::RWFunctor0MImp(Callee& callee,
                                          CalleeSignature function)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function)
{
   RWTHRTRACEMFT2(RWFunctor0MImp,Callee,DR,RWFunctor0MImp(Callee&,CalleeSignature));
}

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0MA1Imp<Callee,DR,A1>
   void (*caller)(void) -> DR (Callee::*func)(A1)
******************************************************************************/

template <class Callee, class DR, class A1>
inline
// static
RWFunctor0
RWFunctor0MA1Imp<Callee,DR,A1>::make(Callee& callee,
                                     CalleeSignature function,
                                     A1 a1)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT3(RWFunctor0MA1Imp,Callee,DR,A1,make(Callee&,CalleeSignature,A1):RWFunctor0);
   return new RWFunctor0MA1Imp<Callee,DR,A1>(callee,function,a1);
}

template <class Callee, class DR, class A1>
inline
RWFunctor0MA1Imp<Callee,DR,A1>::RWFunctor0MA1Imp(Callee& callee,
                                                 CalleeSignature function,
                                                 A1 a1)
   RWTHRTHROWSANY
   : 
      callee_(callee),
      function_(function),
      a1_(a1)
{
   RWTHRTRACEMFT3(RWFunctor0MA1Imp,Callee,DR,A1,RWFunctor0MA1Imp(Callee&,CalleeSignature,A1));
}

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0MA2Imp<Callee,DR,A1,A2>
   void (*caller)(void) -> DR (Callee::*func)(A1,A2)
******************************************************************************/

template <class Callee, class DR, class A1, class A2>
inline
// static
RWFunctor0
RWFunctor0MA2Imp<Callee,DR,A1,A2>::make(Callee& callee,
                                        CalleeSignature function,
                                        A1 a1,
                                        A2 a2)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT4(RWFunctor0MA2Imp,Callee,DR,A1,A2,make(Callee&,CalleeSignature,A1,A2):RWFunctor0);
   return new RWFunctor0MA2Imp<Callee,DR,A1,A2>(callee,function,a1,a2);
}

template <class Callee, class DR, class A1, class A2>
inline
RWFunctor0MA2Imp<Callee,DR,A1,A2>::RWFunctor0MA2Imp(Callee& callee,
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
   RWTHRTRACEMFT4(RWFunctor0MA2Imp,Callee,DR,A1,A2,RWFunctor0MA2Imp(Callee&,CalleeSignature,A1,A2));
}

/******************************************************************************
   RWFunctor0Imp Family:
   RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>
   void (*caller)(void) -> DR (Callee::*func)(A1,A2,A3)
******************************************************************************/

template <class Callee, class DR, class A1, class A2, class A3>
inline
// static
RWFunctor0
RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>::make(Callee& callee,
                                           CalleeSignature function,
                                           A1 a1,
                                           A2 a2,
                                           A3 a3)
   RWTHRTHROWSANY
{
   RWTHRTRACESMFT5(RWFunctor0MA3Imp,Callee,DR,A1,A2,A3,make(Callee&,CalleeSignature,A1,A2,A3):RWFunctor0);
   return new RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>(callee,function,a1,a2,a3);
}

template <class Callee, class DR, class A1, class A2, class A3>
inline
RWFunctor0MA3Imp<Callee,DR,A1,A2,A3>::RWFunctor0MA3Imp(Callee& callee,
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
   RWTHRTRACEMFT5(RWFunctor0MA3Imp,Callee,DR,A1,A2,A3,RWFunctor0MA3Imp(Callee&,CalleeSignature,A1,A2,A3));
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/func0.cc>
#  endif

#endif // __RWTHRFUNC0_H__
