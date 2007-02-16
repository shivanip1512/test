#if !defined(__RWTHRVARIANT_H__)
#  define __RWTHRVARIANT_H__
/*****************************************************************************
 *
 * variant.h
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

variant.h - Run-time Variant Compatibility Test Functions

This file declares and defines functions used to verify the compatibility of
preprocessor macro definitions used when building the Thread.h++ library,
versus those defined when using (linking to) the library.  When this file is 
included in a single translation unit of user code (library or application), 
static objects are defined that will automatically test for build and usage
compatibility during the static initialization phase of linked executables.

*****************************************************************************/

#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RW_STDIO_H__)
#     define __RW_STDIO_H__ <stdio.h>
#     include __RW_STDIO_H__
#  endif

#  if !defined(__RW_STDLIB_H__)
#     define __RW_STDLIB_H__ <stdlib.h>
#     include __RW_STDLIB_H__
#  endif

#  if defined(NDEBUG)
#     define RW_THR_NDEBUG_ON
//    Turn off NDEBUG so asserts will always work in this file...
#     undef NDEBUG
#  endif
#  if !defined(__RW_ASSERT_H__)
#     if defined(assert) 
#        undef assert
#     endif
#     define __RW_ASSERT_H__ <assert.h>
#     include __RW_ASSERT_H__
#  endif
#  if defined(RW_THR_NDEBUG_ON)
#     define NDEBUG
#  endif

enum RWTHRBindingVariant {
      DYNAMIC,
      STATIC
   };

enum RWTHRConfigurationVariant {
      TEST,
      DEBUG,
      RELEASE
   };

// Declare functions (and types) for run-time testing of Thread.h++ variant variables.

// Exception specifications test function

RWBoolean rwthrexport 
rwTHRExceptionSpecs(void);

// Library binding test functions

RWTHRBindingVariant rwthrexport 
rwTHRBinding(void);

RWTHRBindingVariant rwthrexport 
rwTHRtoTLSBinding(void);

#  if !defined(RW_NO_STL)
RWTHRBindingVariant rwthrexport 
rwTHRtoSTDBinding(void);
#  endif

RWTHRBindingVariant rwthrexport 
rwTHRtoRTLBinding(void);

// Build configuration test functions

RWTHRConfigurationVariant rwthrexport 
rwTHRConfiguration(void);

RWTHRConfigurationVariant rwthrexport 
rwTHRtoTLSConfiguration(void);

#  if !defined(RW_NO_STL)
RWTHRConfigurationVariant rwthrexport 
rwTHRtoSTDConfiguration(void);
#  endif

RWTHRConfigurationVariant rwthrexport 
rwTHRtoRTLConfiguration(void);

#  if defined(RW_THR_USE) 

// Define functions for run-time verification of Thread.h++ variant variables.

// These functions are executed during the static initialization phase of an 
// an executable to determine whether the libraries or applications that linked to 
// the Thread.h++ library (and included this file in a translation unit),
// used the same variant macros definitions as were used when the Thread.h++ 
// library was built.  An error message is sent to stderr and the executable is 
// exited with -1 if an illegal mismatch is discovered. 

// These functions must be INLINED in order to capture the macro definitions as
// they appear within the client code.

inline
RWBoolean
rwThrIsBindingCompatible(void)
{
   RWTHRBindingVariant  variant = rwTHRBinding();
#     if defined(RW_THR_DYNAMIC_BINDING)
   if (STATIC == variant) {
      assert(!"\
The Threads.h++ library was built for static binding, but this \
program was compiled with dynamic binding to Threads.h++!");
   }
#     else // RW_THR_STATIC_BINDING
   if (DYNAMIC == variant) {
      assert(!"\
The Threads.h++ library was built for dynamic binding, but this \
program was compiled with static binding to Threads.h++!");
   }
#     endif
   variant = rwTHRtoTLSBinding();
#     if defined(RW_THR_TLS_DLL)
   if (STATIC == variant) {
      assert(!"\n\
The Threads.h++ library was built with static binding to Tools.h++, \
but this program was compiled with dynamic binding to Tools.h++!");
   }
#     else // RW_THR_STATIC_BINDING
   if (DYNAMIC == variant) {
      assert(!"\
The Threads.h++ library was built with dynamic binding to Tools.h++, \
but this program was compiled with static binding to Tools.h++!");
   }
#     endif
#  if !defined(RW_NO_STL)
   variant = rwTHRtoSTDBinding();
#     if defined(RW_THR_STD_DLL)
   if (STATIC == variant) {
      assert(!"\
The Threads.h++ library was built with static binding to the standard library, \
but this program was compiled with dynamic binding to the standard library!");
   }
#     else // RW_THR_STATIC_BINDING
   if (DYNAMIC == variant) {
      assert(!"\
The Threads.h++ library was built with dynamic binding to the standard library, \
but this program was compiled with static binding to the standard library!");
   }
#     endif
#  endif
   return TRUE;
}

static RWBoolean binding = rwThrIsBindingCompatible();

inline
RWBoolean
rwThrIsExceptionSpecsCompatible(void)
{
   RWBoolean variant = rwTHRExceptionSpecs();
// Were Threads Exception Specs Enabled?
#     if defined(RW_THR_ENABLE_EXCEPTION_SPECS)
   if (!variant) {
      assert(!"\
The Threads.h++ library was built with exception specifications disabled, but this \
program was compiled using Threads.h++ with exception specifications enabled!");
   }
#     else // RW_THR_DISABLE_EXCEPTION_SPECS
   if (variant) {
      assert(!"\
The Threads.h++ library was built with exception specifications enabled, but this \
program was compiled using Threads.h++ with exception specifications disabled!");
   }
#     endif
   return TRUE;
}

static RWBoolean exceptionSpecs = rwThrIsExceptionSpecsCompatible();

inline
RWBoolean
rwThrIsConfigurationCompatible(void)
{
   RWTHRConfigurationVariant  variant;   

// Does the Threads.h++ configuration match?
   variant = rwTHRConfiguration();
#     if defined(RW_THR_TEST)
   if (variant == DEBUG) {
      assert(!"\
The Threads.h++ library was built in the debug configuration, but this \
program was compiled using Threads.h++ in the test configuration!");
   }
   else if (variant == RELEASE) {
      assert(!"\
The Threads.h++ library was built in the release configuration, but this \
program was compiled using Threads.h++ in the test configuration!");
   }
#     elif defined(RW_THR_DEBUG)
   if (variant == TEST) {
      assert(!"\
The Threads.h++ library was built in the test configuration, but this \
program was compiled using Threads.h++ in the debug configuration!");
   }
   else if (variant == RELEASE) {
      assert(!"\
The Threads.h++ library was built in the release configuration, but this \
program was compiled using Threads.h++ in the debug configuration!");
   }
#     elif defined(RW_THR_RELEASE)
   if (variant == TEST) {
      assert(!"\
The Threads.h++ library was built in the test configuration, but this \
program was compiled using Threads.h++ in the release configuration!");
   }
   else if (variant == DEBUG) {
      assert(!"\
The Threads.h++ library was built in the debug configuration, but this \
program was compiled using Threads.h++ in the release configuration!");
   }
#     endif

// Does the Tools.h++ configuration match?
   variant = rwTHRtoTLSConfiguration();
#     if defined(RW_THR_TLS_DEBUG)
   if (variant == RELEASE) {
      assert(!"\
The Threads.h++ library was built using Tools.h++ in the release configuration, \
but this program was compiled using Tools.h++ in the debug configuration!");
   }
#     else
   if (variant == DEBUG) {
      assert(!"\
The Threads.h++ library was built using Tools.h++ in the debug configuration, \
but this program was compiled using Tools.h++ in the release configuration!");
   }
#     endif

// Does the Rogue Wave Stdlib configuration match?
#  if !defined(RW_NO_STL)
   variant = rwTHRtoSTDConfiguration();
#     if defined(RW_THR_STD_DEBUG)
   if (variant == RELEASE) {
      assert(!"\
The Threads.h++ library was built using stdlib in the release configuration, \
but this program was compiled using stdlib in the debug configuration!");
   }
#     else
   if (variant == DEBUG) {
      assert(!"\
The Threads.h++ library was built using stdlib in the debug configuration, \
but this program was compiled using stdlib in the release configuration!");
   }
#     endif
#  endif

   variant = rwTHRtoRTLConfiguration();
#     if defined(RW_THR_RTL_DEBUG)
   if (variant == RELEASE) {
      assert(!"\
The Threads.h++ library was built using the C run-time library in the release configuration, \
but this program was compiled using the C run-time library in the debug configuration!");
   }
#     else
   if (variant == DEBUG) {
      assert(!"\
The Threads.h++ library was built using the C run-time library in the debug configuration, \
but this program was compiled using the C run-time library in the release configuration!");
   }
#  endif
   return TRUE;
}

static RWBoolean config = rwThrIsConfigurationCompatible();

#  endif // RW_THR_USE 

#endif // __RWTHRVARIANT_H__

