#if !defined(__RWTHRTRACE_H__)
#  define __RWTHRTRACE_H__
/*****************************************************************************
 *
 * trace.h
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

trace.h - Declarations for:
         
   RWTHRTrace - trace class.
   
   Trace macros:
   
   RW_THR_DECLARE_TRACEABLE
   RW_THR_IMPLEMENT_TRACEABLE
   RW_THR_IMPLEMENT_TRACEABLE_T1
   RW_THR_IMPLEMENT_TRACEABLE_T2
   RW_THR_IMPLEMENT_TRACEABLE_T3
   RW_THR_IMPLEMENT_TRACEABLE_T4
   RW_THR_IMPLEMENT_TRACEABLE_T5
   RW_THR_IMPLEMENT_TRACEABLE_T6
   RW_THR_IMPLEMENT_TRACEABLE_T7
   RW_THR_IMPLEMENT_TRACEABLE_T8
   RW_THR_IMPLEMENT_TRACEABLE_T9

   RWTHRTRACE

   RWTHRTRACEMF
   RWTHRTRACEMFT1
   RWTHRTRACEMFT2
   RWTHRTRACEMFT3
   RWTHRTRACEMFT4
   RWTHRTRACEMFT5
   RWTHRTRACEMFT6
   RWTHRTRACEMFT7
   RWTHRTRACEMFT8
   RWTHRTRACEMFT9

   RWTHRTRACESMF
   RWTHRTRACESMFT1
   RWTHRTRACESMFT2
   RWTHRTRACESMFT3
   RWTHRTRACESMFT4
   RWTHRTRACESMFT5
   RWTHRTRACESMFT6
   RWTHRTRACESMFT7
   RWTHRTRACESMFT8
   RWTHRTRACESMFT9

   RWTHRENABLETRACE
   RWTHRENABLETRACET1
   RWTHRENABLETRACET2
   RWTHRENABLETRACET3
   RWTHRENABLETRACET4
   RWTHRENABLETRACET5
   RWTHRENABLETRACET6
   RWTHRENABLETRACET7
   RWTHRENABLETRACET8
   RWTHRENABLETRACET9

   RWTHRDISABLETRACE
   RWTHRDISABLETRACET1
   RWTHRDISABLETRACET2
   RWTHRDISABLETRACET3
   RWTHRDISABLETRACET4
   RWTHRDISABLETRACET5
   RWTHRDISABLETRACET6
   RWTHRDISABLETRACET7
   RWTHRDISABLETRACET8
   RWTHRDISABLETRACET9

See Also:

   trace.cpp  - Out-of-line function definitions.

******************************************************************************/

#  if !defined(__RWDEFS_H__)
#     include <rw/defs.h>
#  endif

#  if defined(RW_THR_TRACE)

#    if !defined(__RWMUTEX_H__)
#       include <rw/mutex.h>
#    endif

#    if !defined(__RW_IOSTREAM_H__)
#       if defined(RWTHR_IOSTD)
#          define __RW_IOSTREAM_H__ <iostream>
#       else
#          define __RW_IOSTREAM_H__ <iostream.h>
#       endif
#       include __RW_IOSTREAM_H__
#    endif

class RWTHRExport RWTHRTrace {

   protected:
      // Output stream
      static RW_SL_IO_STD(ostream)*   ostreamP_;
      // Use tools mutex as lock for output stream
      static RWMutex    mutex_;

      RWBoolean   trace_;
      char*       file_;
      int         line_;
      char*       text_;
      const void* address_;

   public:

      RWTHRTrace(RWBoolean trace,
                 const char* file,
                 int line,
                 const char* text);

      RWTHRTrace(RWBoolean trace,
                 const char* file,
                 int line,
                 const void* address,
                 const char* text);

      ~RWTHRTrace(void);

      // Get the current trace output stream
      static
      RW_SL_IO_STD(ostream)&
      stream(void);

      // Set the current trace output stream
      static
      void
      stream(RW_SL_IO_STD(ostream)& stream);
      
      // Trace routine for global use
      static
      void
      trace(const char* file,
            int line,
            const char* text1,
            const char* text2);

      // Trace routine for member functions
      static
      void
      trace(const char* file,
            int line,
            const void* address,
            const char* text1,
            const char* text2);

   private:

      // Strip directory path from filename
      static
      const char *
      stripPath(const char* file);

      // Get the current trace output stream
      // For internal use, assumes that the mutex is locked when called
      static
      RW_SL_IO_STD(ostream)&
      _stream(void);

      // Initialize the stream
      static
      void
      init(void);
};


// Only define macros if trace is on...

#  define RW_THR_DECLARE_TRACEABLE           \
   public:                                   \
      static                                 \
      void                                   \
      enableTrace(void)                      \
      {                                      \
         isTraceable()=TRUE;                 \
      }                                      \
      static                                 \
      void                                   \
      disableTrace(void)                     \
      {                                      \
         isTraceable()=FALSE;                \
      }                                      \
      static                                 \
      RWBoolean&                             \
      isTraceable(void);



#  define RW_THR_IMPLEMENT_TRACEABLE(className)          \
      RWBoolean&                                         \
      className::isTraceable(void)                       \
      {                                                  \
         static RWBoolean isTraceEnabled=FALSE;          \
         return isTraceEnabled;                          \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T1(className,TP1)   \
      template <class TP1>                               \
      RWBoolean&                                         \
      className<TP1>::isTraceable(void)                  \
      {                                                  \
         static RWBoolean isTraceEnabled=FALSE;          \
         return isTraceEnabled;                          \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T2(className,TP1,TP2)  \
      template <class TP1, class TP2>                       \
      RWBoolean&                                            \
      className<TP1,TP2>::isTraceable(void)                 \
      {                                                     \
         static RWBoolean isTraceEnabled=FALSE;             \
         return isTraceEnabled;                             \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T3(className,TP1,TP2,TP3)    \
      template <class TP1, class TP2, class TP3>                  \
      RWBoolean&                                                  \
      className<TP1,TP2,TP3>::isTraceable(void)                   \
      {                                                           \
         static RWBoolean isTraceEnabled=FALSE;                   \
         return isTraceEnabled;                                   \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T4(className,TP1,TP2,TP3,TP4)   \
      template <class TP1, class TP2, class TP3, class TP4>          \
      RWBoolean&                                                     \
      className<TP1,TP2,TP3,TP4>::isTraceable(void)                  \
      {                                                              \
         static RWBoolean isTraceEnabled=FALSE;                      \
         return isTraceEnabled;                                      \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T5(className,TP1,TP2,TP3,TP4,TP5)  \
      template <class TP1, class TP2, class TP3, class TP4, class TP5>  \
      RWBoolean&                                                        \
      className<TP1,TP2,TP3,TP4,TP5>::isTraceable(void)                 \
      {                                                                 \
         static RWBoolean isTraceEnabled=FALSE;                         \
         return isTraceEnabled;                                         \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T6(className,TP1,TP2,TP3,TP4,TP5,TP6)          \
      template <class TP1, class TP2, class TP3, class TP4, class TP5, class TP6>   \
      RWBoolean&                                                                    \
      className<TP1,TP2,TP3,TP4,TP5,TP6>::isTraceable(void)                         \
      {                                                                             \
         static RWBoolean isTraceEnabled=FALSE;                                     \
         return isTraceEnabled;                                                     \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T7(className,TP1,TP2,TP3,TP4,TP5,TP6,TP7)               \
      template <class TP1, class TP2, class TP3, class TP4, class TP5, class TP6, class TP7> \
      RWBoolean&                                                                             \
      className<TP1,TP2,TP3,TP4,TP5,TP6,TP7>::isTraceable(void)                              \
      {                                                                                      \
         static RWBoolean isTraceEnabled=FALSE;                                              \
         return isTraceEnabled;                                                              \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T8(className,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8)                       \
      template <class TP1, class TP2, class TP3, class TP4, class TP5, class TP6, class TP7, class TP8>  \
      RWBoolean&                                                                                         \
      className<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8>::isTraceable(void)                                      \
      {                                                                                                  \
         static RWBoolean isTraceEnabled=FALSE;                                                          \
         return isTraceEnabled;                                                                          \
      }

#  define RW_THR_IMPLEMENT_TRACEABLE_T9(className,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9)                               \
      template <class TP1, class TP2, class TP3, class TP4, class TP5, class TP6, class TP7, class TP8, class TP9>   \
      RWBoolean&                                                                                                     \
      className<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9>::isTraceable(void)                                              \
      {                                                                                                              \
         static RWBoolean isTraceEnabled=FALSE;                                                                      \
         return isTraceEnabled;                                                                                      \
      }

#  define RWTHRTRACE(A) \
      RWTHRTrace::trace(__FILE__,__LINE__,#A,"");

#  define RWTHRTRACESMF(classname,function)              \
      RWTHRTrace rwTHRTrace(classname::isTraceable(),__FILE__,__LINE__,#classname"::"#function);

#  define RWTHRTRACEMF(classname,function)               \
      RWTHRTrace rwTHRTrace(classname::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"::"#function);

#  define RWTHRTRACESMFT1(classname,TP,function)              \
      RWTHRTrace rwTHRTrace(classname<TP>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP">::"#function);

#  define RWTHRTRACEMFT1(classname,TP,function)              \
      RWTHRTrace rwTHRTrace(classname<TP>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP">::"#function);

#  define RWTHRTRACESMFT2(classname,TP1,TP2,function)             \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP1","#TP2">::"#function);

#  define RWTHRTRACEMFT2(classname,TP1,TP2,function)              \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP1","#TP2">::"#function);

#  define RWTHRTRACESMFT3(classname,TP1,TP2,TP3,function)             \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP1","#TP2","#TP3">::"#function);

#  define RWTHRTRACEMFT3(classname,TP1,TP2,TP3,function)              \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP1","#TP2","#TP3">::"#function);

#  define RWTHRTRACESMFT4(classname,TP1,TP2,TP3,TP4,function)             \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP1","#TP2","#TP3","#TP4">::"#function);

#  define RWTHRTRACEMFT4(classname,TP1,TP2,TP3,TP4,function)              \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP1","#TP2","#TP3","#TP4">::"#function);

#  define RWTHRTRACESMFT5(classname,TP1,TP2,TP3,TP4,TP5,function)             \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5">::"#function);

#  define RWTHRTRACEMFT5(classname,TP1,TP2,TP3,TP4,TP5,function)              \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5">::"#function);

#  define RWTHRTRACESMFT6(classname,TP1,TP2,TP3,TP4,TP5,TP6,function)             \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5,TP6>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5","#TP6">::"#function);

#  define RWTHRTRACEMFT6(classname,TP1,TP2,TP3,TP4,TP5,TP6,function)              \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5,TP6>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5","#TP6">::"#function);

#  define RWTHRTRACESMFT7(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,function)             \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5","#TP6","#TP7">::"#function);

#  define RWTHRTRACEMFT7(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,function)              \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5","#TP6","#TP7">::"#function);

#  define RWTHRTRACESMFT8(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,function)             \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5","#TP6","#TP7","#TP8">::"#function);

#  define RWTHRTRACEMFT8(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,function)              \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5","#TP6","#TP7","#TP8">::"#function);

#  define RWTHRTRACESMFT9(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9,function)             \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9>::isTraceable(),__FILE__,__LINE__,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5","#TP6","#TP7","#TP8","#TP9">::"#function);

#  define RWTHRTRACEMFT9(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9,function)              \
      RWTHRTrace rwTHRTrace(classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9>::isTraceable(),__FILE__,__LINE__,(const void*)this,#classname"<"#TP1","#TP2","#TP3","#TP4","#TP5","#TP6","#TP7","#TP8","#TP9">::"#function);

#  define RWTHRENABLETRACE(classname) classname::enableTrace();
#  define RWTHRDISABLETRACE(classname) classname::disableTrace();

#  define RWTHRENABLETRACET1(classname,TP1) classname<TP1>::enableTrace();
#  define RWTHRDISABLETRACET1(classname,TP1) classname<TP1>::disableTrace();

#  define RWTHRENABLETRACET2(classname,TP1,TP2) classname<TP1,TP2>::enableTrace();
#  define RWTHRDISABLETRACET2(classname,TP1,TP2) classname<TP1,TP2>::disableTrace();

#  define RWTHRENABLETRACET3(classname,TP1,TP2,TP3) classname<TP1,TP2,TP3>::enableTrace();
#  define RWTHRDISABLETRACET3(classname,TP1,TP2,TP3) classname<TP1,TP2,TP3>::disableTrace();

#  define RWTHRENABLETRACET4(classname,TP1,TP2,TP3,TP4) classname<TP1,TP2,TP3,TP4>::enableTrace();
#  define RWTHRDISABLETRACET4(classname,TP1,TP2,TP3,TP4) classname<TP1,TP2,TP3,TP4>::disableTrace();

#  define RWTHRENABLETRACET5(classname,TP1,TP2,TP3,TP4,TP5) classname<TP1,TP2,TP3,TP4,TP5>::enableTrace();
#  define RWTHRDISABLETRACET5(classname,TP1,TP2,TP3,TP4,TP5) classname<TP1,TP2,TP3,TP4,TP5>::disableTrace();

#  define RWTHRENABLETRACET6(classname,TP1,TP2,TP3,TP4,TP5,TP6) classname<TP1,TP2,TP3,TP4,TP5,TP6>::enableTrace();
#  define RWTHRDISABLETRACET6(classname,TP1,TP2,TP3,TP4,TP5,TP6) classname<TP1,TP2,TP3,TP4,TP5,TP6>::disableTrace();

#  define RWTHRENABLETRACET7(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7) classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7>::enableTrace();
#  define RWTHRDISABLETRACET7(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7) classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7>::disableTrace();

#  define RWTHRENABLETRACET8(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8) classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8>::enableTrace();
#  define RWTHRDISABLETRACET8(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8) classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8>::disableTrace();

#  define RWTHRENABLETRACET9(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9) classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9>::enableTrace();
#  define RWTHRDISABLETRACET9(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9) classname<TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9>::disableTrace();

#else

#  define RW_THR_DECLARE_TRACEABLE

#  define RW_THR_IMPLEMENT_TRACEABLE(className)
#  define RW_THR_IMPLEMENT_TRACEABLE_T1(className,TP1)
#  define RW_THR_IMPLEMENT_TRACEABLE_T2(className,TP1,TP2)
#  define RW_THR_IMPLEMENT_TRACEABLE_T3(className,TP1,TP2,TP3)
#  define RW_THR_IMPLEMENT_TRACEABLE_T4(className,TP1,TP2,TP3,TP4)
#  define RW_THR_IMPLEMENT_TRACEABLE_T5(className,TP1,TP2,TP3,TP4,TP5)
#  define RW_THR_IMPLEMENT_TRACEABLE_T6(className,TP1,TP2,TP3,TP4,TP5,TP6)
#  define RW_THR_IMPLEMENT_TRACEABLE_T7(className,TP1,TP2,TP3,TP4,TP5,TP6,TP7)
#  define RW_THR_IMPLEMENT_TRACEABLE_T8(className,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8)
#  define RW_THR_IMPLEMENT_TRACEABLE_T9(className,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9)

#  define RWTHRTRACE(A)

#  define RWTHRTRACESMF(classname,function)
#  define RWTHRTRACEMF(classname,function)
#  define RWTHRTRACESMFT1(classname,TP,function)
#  define RWTHRTRACEMFT1(classname,TP,function)
#  define RWTHRTRACESMFT2(classname,TP1,TP2,function)
#  define RWTHRTRACEMFT2(classname,TP1,TP2,function)
#  define RWTHRTRACESMFT3(classname,TP1,TP2,TP3,function)
#  define RWTHRTRACEMFT3(classname,TP1,TP2,TP3,function)
#  define RWTHRTRACESMFT4(classname,TP1,TP2,TP3,TP4,function)
#  define RWTHRTRACEMFT4(classname,TP1,TP2,TP3,TP4,function)
#  define RWTHRTRACESMFT5(classname,TP1,TP2,TP3,TP4,TP5,function)
#  define RWTHRTRACEMFT5(classname,TP1,TP2,TP3,TP4,TP5,function)
#  define RWTHRTRACESMFT6(classname,TP1,TP2,TP3,TP4,TP5,TP6,function)
#  define RWTHRTRACEMFT6(classname,TP1,TP2,TP3,TP4,TP5,TP6,function)
#  define RWTHRTRACESMFT7(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,function)
#  define RWTHRTRACEMFT7(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,function)
#  define RWTHRTRACESMFT8(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,function)
#  define RWTHRTRACEMFT8(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,function)
#  define RWTHRTRACESMFT9(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9,function)
#  define RWTHRTRACEMFT9(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9,function)

#  define RWTHRENABLETRACE(classname) 
#  define RWTHRDISABLETRACE(classname)
#  define RWTHRENABLETRACET1(classname,TP1)
#  define RWTHRDISABLETRACET1(classname,TP1)
#  define RWTHRENABLETRACET2(classname,TP1,TP2)
#  define RWTHRDISABLETRACET2(classname,TP1,TP2)
#  define RWTHRENABLETRACET3(classname,TP1,TP2,TP3)
#  define RWTHRDISABLETRACET3(classname,TP1,TP2,TP3)
#  define RWTHRENABLETRACET4(classname,TP1,TP2,TP3,TP4)
#  define RWTHRDISABLETRACET4(classname,TP1,TP2,TP3,TP4)
#  define RWTHRENABLETRACET5(classname,TP1,TP2,TP3,TP4,TP5)
#  define RWTHRDISABLETRACET5(classname,TP1,TP2,TP3,TP4,TP5)
#  define RWTHRENABLETRACET6(classname,TP1,TP2,TP3,TP4,TP5,TP6)
#  define RWTHRDISABLETRACET6(classname,TP1,TP2,TP3,TP4,TP5,TP6)
#  define RWTHRENABLETRACET7(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7)
#  define RWTHRDISABLETRACET7(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7)
#  define RWTHRENABLETRACET8(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8)
#  define RWTHRDISABLETRACET8(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8)
#  define RWTHRENABLETRACET9(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9)
#  define RWTHRDISABLETRACET9(classname,TP1,TP2,TP3,TP4,TP5,TP6,TP7,TP8,TP9)

#endif

#endif // __RWTHRTRACE_H__

