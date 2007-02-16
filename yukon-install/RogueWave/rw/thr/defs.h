#if !defined(__RWTHRDEFS_H__)
#  define __RWTHRDEFS_H__
/*****************************************************************************
 *
 * defs.h
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

defs.h - Macro declarations.

This file contains preprocessor logic and definitions that are used to
define the current Threads.h++ product configuration and to determine
the identity and capabilities of the current target environment,
including the current compiler, operating system, and processor
architecture.

*****************************************************************************/

#  if !defined(__RWDEFS_H__)
#     include <rw/defs.h>
#  endif

// RW_NO_IOSTD is defined by Tools.h++ if Standard C++ iostreams headers are NOT available
#  if !defined(RW_NO_IOSTD) || (0 == RW_NO_IOSTD)
#    define RWTHR_IOSTD 1
#  endif

// must include iostream.h before pthread.h on HP due to the cma_ define problem.
#  if !defined(__RW_IOSTREAM_H__) && defined(__hpux) && defined(hpux) && !defined(__CLCC__) 
#     if defined(RWTHR_IOSTD) // indirectly from rw/defs.h
#        define __RW_IOSTREAM_H__ <iostream>
#     else
#        define __RW_IOSTREAM_H__ <iostream.h>
#     endif
#     include __RW_IOSTREAM_H__   
#  endif

#  if !defined(__RW_ASSERT_H__) && !defined(assert)
#     define __RW_ASSERT_H__ <assert.h> 
#     include __RW_ASSERT_H__ 
#  endif


/*****************************************************************************
   
Threads.h++ Compiler Command-Line Option Macros
==================================================

The following list identifies those macros that may be used to control
various attributes of the Threads.h++ product by defining them on the
compiler command line:

    RW_THR_BUILD
    RW_THR_DEBUG
    RW_THR_DEBUG_ASSERT
    RW_THR_DEBUG_THROW
    RW_THR_DISABLE_CERTIFIED_ONLY
    RW_THR_DISABLE_EXCEPTION_SPECS (default for HP C++ A1011) 
    RW_THR_DISABLE_SUPPORTED_ONLY
    RWDLL (Turns on RW_THR_DYNAMIC_BINDING)
    RW_THR_DYNAMIC_BINDING
  * RW_THR_ENABLE_CERTIFIED_ONLY  
  * RW_THR_ENABLE_EXCEPTION_SPECS (except HP C++ A1011)
  * RW_THR_ENABLE_SUPPORTED_ONLY 
  * RW_THR_RELEASE 
  * RW_THR_STATIC_BINDING 
    RW_THR_TEST
  * RW_THR_USE 

   * - Default

Command-Line Macros By Functional Area
--------------------------------------

   The command-line macros listed above can logically organized into 
   following functional groups:

      Exception Specifications
      Assertion Handling
      Include File Usage
      Link Binding
      Product Configurations
      Target Environment Acceptance


Exception Specifications:

   The following macros control the use of function exception specifications
   within the library:

      RW_THR_DISABLE_EXCEPTION_SPECS (Default for HP C++ A1011)
      RW_THR_ENABLE_EXCEPTION_SPECS (Default except HP C++ A1011)

Assertion Handling:

   The following macros control the behavior of RWTHRASSERT assertion macros
   within a debug-version library:

      RW_THR_DEBUG_ASSERT (Default)
      RW_THR_DEBUG_THROW

Include File Usage:

   The following macros control how the Threads.h++ public include files 
   interpret how they are being used:
   
      RW_THR_BUILD
      RW_THR_USE (Default)

   The RW_THR_BUILD macro is automatically defined by the standard 
   Threads.h++ makefile(s).

Link Binding:

   The following macros control whether a dynamic-link or static version
   of the product is currently being built or used:

      RWDLL (Turns on RW_THR_DYAMIC_BINDING.)
      RW_THR_DYNAMIC_BINDING
      RW_THR_STATIC_BINDING (Default)

   When compiling using the standard product makefile(s), the macro that is
   defined is determined by the value of Make macro THR_BINDING.

Product Configuration:

   The following macros control the whether or not certain behaviors
   or assertions should be implemented within the library that is currently
   being built or used:

      RW_THR_TEST 
      RW_THR_DEBUG
      RW_THR_RELEASE (Default)

   When compiling using the standard product makefile(s), the macro that is
   defined is determined by the value of Make macro BMODE.

Target Environment Acceptance:

   Certified Environments:
   
      The following macros control the whether or not the library should be
      restricted to being built or used in certified environments only:

         RW_THR_DISABLE_CERTIFIED_ONLY
         RW_THR_ENABLE_CERTIFIED_ONLY (Default)

   Supported Environments:   
      
      The following macros control the whether or not the library should be
      restricted to being built or used in supported environments only:

         RW_THR_DISABLE_SUPPORTED_ONLY
         RW_THR_ENABLE_SUPPORTED_ONLY (Default)


Command Line Macros
-------------------

RW_THR_BUILD                                    

   If defined, this macro indicates to the library include files that they
   are being used to build the library, otherwise the include files assume
   that they are being used to access the library.

   See also: Macro RW_THR_USE.


RW_THR_DEBUG                                    

   If defined, this macro indicates that a debug version of the library is 
   being built or used, otherwise a release version of the library is being 
   built or used.

   See also: Macro RW_THR_TEST and RW_THR_RELEASE.


RW_THR_DEBUG_ASSERT

   If defined while RW_THR_DEBUG is defined, this macro indicates that the 
   RWTHRASSERT macro should be expanded to produce a call to assert().

   See also: Macro RW_THR_DEBUG and RW_THR_DEBUG_THROW.


RW_THR_DEBUG_THROW

   If defined while RW_THR_DEBUG is defined, this macro indicates that the 
   RWTHRASSERT macro should be expanded to throw an RWTHRInternalError 
   exception object (with the same message content as would be produced by a 
   call to assert()).
   implies definition of the RW_THR_ENABLE_SUPPORTED_ONLY macro.  This macro
   See also: Macro RW_THR_DEBUG and RW_THR_DEBUG_ASSERT.


RW_THR_DISABLE_CERTIFIED_ONLY

   If defined, this macro indicates to the library include files that
   preprocessor tests that limit the library to being built or used only in 
   certified environments should be disabled.

   See also: Macros RW_THR_ENABLE_CERTIFIED_ONLY, 
             RW_THR_DISABLE_SUPPORTED_ONLY, and RW_THR_ENABLE_SUPPORTED_ONLY.


RW_THR_DISABLE_EXCEPTION_SPECS                                    

   If defined, this macro indicates to the library include files that
   exception specifications (declared using the Threads.h++ RWTHRTHROWS*) 
   should be removed from function declarations and definitions, otherwise 
   it is assumed that the library will retain any existing exception 
   specifications.

   See also: Macros RW_THR_ENABLE_EXCEPTION_SPECS, RWTHRTHROWSANY, 
             RWTHRTHROWSNONE, RWTHRTHROWS*.


RW_THR_DYNAMIC_BINDING, RWDLL

   If defined, this macro indicates to the library include files that they
   are being used to build or access a shared or dynamic-link version of 
   the library, otherwise the include files assume that they are being use 
   to build or access a static version of the library.

   See also: Macros RW_THR_STATIC_BINDING, RWTHRExport, RWTHRTExport, 
             rwthrexport, and rwthrtexport.


RW_THR_ENABLE_CERTIFIED_ONLY

   If defined, this macro indicates to the library include files that
   preprocessor tests that limit the library to being built or used only in 
   certified environments should be applied.  Definition of this macro
   implies definition of the RW_THR_ENABLE_SUPPORTED_ONLY macro.  This macro
   is always defined if RW_THR_DISABLE_CERTIFIED_ONLY is not defined and
   RW_THR_DISABLE_SUPPORTED_ONLY is not defined.

   See also: Macros RW_THR_DISABLE_CERTIFIED_ONLY, 
             RW_THR_DISABLE_SUPPORTED_ONLY, and RW_THR_ENABLE_SUPPORTED_ONLY.


RW_THR_ENABLE_EXCEPTION_SPECS

   If defined, this macro indicates to the library include files that
   any exception specifications should be left on function declarations
   and definitions.  This is macro is always defined if 
   RW_THR_DISABLE_EXCEPTION_SPECS is not defined.

   See also: Macros RW_THR_DISABLE_EXCEPTION_SPECS, RWTHRTHROWSANY, 
             RWTHRTHROWSNONE, RWTHRTHROWS*.


RW_THR_RELEASE

   If defined, this macro indicates that a release version of the library is 
   being built or used.

   See also: Preprocessor macros RW_THR_DEBUG.


RW_THR_STATIC_BINDING

   If defined, this macro indicates to the library include files that they
   are being used to build or access a shared or dynamic-link version of 
   the library, otherwise the include files assume that they are being used
   to build or access a static version of the library.

   See also: Preprocessor Macros RW_THR_DYNAMIC_BINDING, RWTHRExport, 
             RWTHRTExport, rwthrexport, and rwthrtexport.
   

RW_THR_TEST

   If defined, this macro indicates that a test version of the library is 
   being built or used.  This macro has no effect in the current 
   implementation!

   See also: Preprocessor macros RW_THR_DEBUG and RW_THR_RELEASE.


RW_THR_USE

   If defined, this macro indicates that the library include files are 
   being used to access the library.

   See also: Preprocessor macro RW_THR_BUILD.


******************************************************************************/


//////////////////////////////////////////////////////////////////////////////
//
//    Detect and Verify Command-Line Variant Macros
//
//////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////////////
//    Exception Specificiations:
//////////////////////////////////////////////////////////////////////////////

//       Are we going to remove function exception specifications or allow them?

#  if defined(RW_THR_DISABLE_EXCEPTION_SPECS)
//    Exception specifications disabled
#     if defined(RW_THR_ENABLE_EXCEPTION_SPECS)
#        error Conflicting definitions of RW_THR_DISABLE_EXCEPTION_SPECS and RW_THR_ENABLE_EXCEPTION_SPECS!
#        undef RW_THR_ENABLE_EXCEPTION_SPECS
#     endif
#  elif defined(RW_THR_ENABLE_EXCEPTION_SPECS)
//    Exception specifications enabled
#  else
//    Enable exception specifications by default
#     define RW_THR_ENABLE_EXCEPTION_SPECS
#  endif


//////////////////////////////////////////////////////////////////////////////
//    Include File Usage:
//////////////////////////////////////////////////////////////////////////////

//       Are we compiling the library include files to build the library or use the library?

#  if defined(RW_THR_BUILD)
//    Building the library
#     if defined(RW_THR_USE)
#        error Conflicting definitions of RW_THR_BUILD and RW_THR_USE!
#        undef RW_THR_USE
#     endif
#  elif defined(RW_THR_USE)
//    Using the library
#  else
//    Use the library by default
#     define RW_THR_USE
#  endif

//////////////////////////////////////////////////////////////////////////////
//    Link Binding:
//////////////////////////////////////////////////////////////////////////////

//       Are we building or using a dynamic-link or static version of the library?

#if defined(RWDLL)
#     define RW_THR_DYNAMIC_BINDING
#endif

#  if defined(RW_THR_DYNAMIC_BINDING)
//    Building or using a dynamic-link version
#     if defined(RW_THR_STATIC_BINDING)
#        error Conflicting definitions of RW_THR_DYNAMIC_BINDING and RW_THR_STATIC_BINDING
#     endif
#  elif defined(RW_THR_STATIC_BINDING)
//    Building or using a static version
#  else
//    Build or use static version by default
#     define RW_THR_STATIC_BINDING
#  endif


//////////////////////////////////////////////////////////////////////////////
//    Product Configuration:
//////////////////////////////////////////////////////////////////////////////

//       Are we building or using a test, debug or release configuration of the library?

#  if defined(RW_THR_TEST)
//    Test configuration
#     if defined(RW_THR_DEBUG) 
#        error Conflicting definitions of RW_THR_TEST and RW_THR_DEBUG!
#        undef RW_THR_DEBUG
#        undef RW_THR_RELEASE
#     elif defined(RW_THR_RELEASE)
#        error Conflicting definitions of RW_THR_TEST and RW_THR_RELEASE!
#        undef RW_THR_RELEASE
#     endif
#  elif defined(RW_THR_DEBUG)
//    Debug configuration
#     if defined(RW_THR_RELEASE)
#        error Conflicting definitions of RW_THR_DEBUG and RW_THR_RELEASE!
#        undef RW_THR_RELEASE
#     endif
#  elif defined(RW_THR_RELEASE)
//    Release configuration
#  else
//    Choose Release configuration as the default 
#     define RW_THR_RELEASE
#  endif

#  if defined(RW_THR_TEST)
#     define RW_THR_PRODUCT_CONFIGURATION "Test"
#  elif defined(RW_THR_DEBUG)
#     define RW_THR_PRODUCT_CONFIGURATION "Debug"
#  else
#     define RW_THR_PRODUCT_CONFIGURATION "Release"
#  endif


//////////////////////////////////////////////////////////////////////////////
//    Target Environment Acceptance:
//////////////////////////////////////////////////////////////////////////////

//       Are we going to restrict building or use to certified environments only?

#  if defined(RW_THR_DISABLE_CERTIFIED_ONLY)
//    Allow non-certified environments
#     if defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#        error Conflicting definitions of RW_THR_DISABLE_CERTIFIED_ONLY and RW_THR_ENABLE_CERTIFIED_ONLY!
#        undef RW_THR_ENABLE_CERTIFIED_ONLY
#     endif
#  elif defined(RW_THR_ENABLE_CERTIFIED_ONLY)
//    Allow certified environments only  
#  else
#     if defined(RW_THR_DISABLE_SUPPORTED_ONLY)
//       Allow un-certified environments if unsupported environments are allowed
#        define RW_THR_DISABLE_CERTIFIED_ONLY
#     else
//       Allow only certified environments by default
#        define RW_THR_ENABLE_CERTIFIED_ONLY
#     endif
#  endif

#  if defined(_RW_THR_ENABLE_CERTIFIED_ONLY)
//    Certified only implies supported only also...
#     define RW_THR_ENABLE_SUPPORTED_ONLY 
#  endif

//       Are we going to restrict building or use to supported environments only?

#  if defined(RW_THR_DISABLE_SUPPORTED_ONLY)
//    Allow unsupported environments
#     if defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#        error Conflicting definitions of RW_THR_DISABLE_SUPPORTED_ONLY and RW_THR_ENABLE_CERTIFIED_ONLY!
#        undef RW_THR_ENABLE_CERTIFIED_ONLY
#        define RW_THR_DISABLE_CERTIFIED_ONLY
#     elif defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#        error Conflicting definitions of RW_THR_DISABLE_SUPPORTED_ONLY and RW_THR_ENABLE_SUPPORTED_ONLY!
#        undef RW_THR_ENABLE_SUPPORTED_ONLY
#     endif
#  elif defined(RW_THR_ENABLE_SUPPORTED_ONLY)
//    Allow supported environments only
#  else
//    Allow only supported environments by default
#     define RW_THR_ENABLE_SUPPORTED_ONLY
#  endif

//////////////////////////////////////////////////////////////////////////////
//
//    Define common values used in identifying target environments
//
//////////////////////////////////////////////////////////////////////////////

#  define RW_THR_UNKNOWN                           "Unknown"
#  define RW_THR_VERSION_UNKNOWN                   -1

//    Vendors

#  define RW_THR_VENDOR_APPLE          "Apple Computer Inc. (Apple)"
#  define RW_THR_VENDOR_BORLAND        "Borland International (Borland)"
#  define RW_THR_VENDOR_CENTERLINE     "CenterLine"
#  define RW_THR_VENDOR_DEC            "Digital Equipment Corp. (DEC)"
#  define RW_THR_VENDOR_HP             "Hewlett-Packard (HP)"
#  define RW_THR_VENDOR_IBM            "International Business Machines (IBM)"
#  define RW_THR_VENDOR_EGCS_LINUX     "Free Software Foundation (Linux)"
#  define RW_THR_VENDOR_METAWARE       "Metrowerks"
#  define RW_THR_VENDOR_MICROSOFT      "Microsoft (MS)"
#  define RW_THR_VENDOR_SGI            "Silicon Graphics Inc. (SGI)"
#  define RW_THR_VENDOR_SUN            "Sun Microsystems (Sun)"
#  define RW_THR_VENDOR_SYMANTEC       "Symantec"
#  define RW_THR_VENDOR_WATCOM         "Watcom"

/*****************************************************************************

Target Environment Macros

The following section documents the macros used to identify and describe
the platform/environment variants currently recognized by the Threads.h++ 
library.


Architecture
------------

RW_THR_ARCHITECTURE

This macro defines a string containing the name of the current target
architecture.  This macro is given its value by preprocessor directives
in the library header files that choose and assign one of the following 
macros to the RW_THR_ARCHITECTURE macro as is appropriate:

 RW_THR_ARCHITECTURE_DEC_WORKSTATION   "DEC Workstation"
 RW_THR_ARCHITECTURE_HP_9000           "HP-9000 Workstation"
 RW_THR_ARCHITECTURE_IBM_RS6000        "IBM RS6000 Workstation"
 RW_THR_ARCHITECTURE_MAC               "Apple Macintosh"
 RW_THR_ARCHITECTURE_PC_AT             "IBM PC/AT Compatible"
 RW_THR_ARCHITECTURE_POWER_PC          "Power PC"
 RW_THR_ARCHITECTURE_SGI_WORKSTATION   "SGI Workstation"
 RW_THR_ARCHITECTURE_SUN_SPARCSTATION  "Sun SPARCstation"

This list will change as new architectures are supported, and support
for old architectures is dropped.


Processor
---------

RW_THR_PROCESSOR

This macro defines a string containing the name of the current operating
system vendor.  This macro is given its value by preprocessor directives 
in the library header files that choose and assign one of the following 
macros to the RW_THR_PROCESSOR macro as is appropriate:
   
 RW_THR_PROCESSOR_68K                  "Motorola 68K"
 RW_THR_PROCESSOR_ALPHA                "DEC Alpha"
 RW_THR_PROCESSOR_MIPS                 "MIPS R4X00"
 RW_THR_PROCESSOR_PA_RISC              "HP PA-RISC"
 RW_THR_PROCESSOR_POWERPC              "IBM/Motorola/Apple PowerPC"
 RW_THR_PROCESSOR_IX86                 "Intel iX86"
 RW_THR_PROCESSOR_SPARC                "Sun SPARC"
       
This list may change as new processors are supported, and support 
for old processors is dropped.


Compiler
--------

   Compiler Name
   -------------

RW_THR_COMPILER_NAME
   
This macro defines a string containing the name of the current 
compiler.  This macro is given its value by preprocessor directives in 
the library header files that choose and assign one of the following 
macros to the RW_THR_COMPILER_NAME macro as is appropriate:
   
 RW_THR_COMPILER_NAME_BORLAND_CPP      "C/C++ Compiler"
 RW_THR_COMPILER_NAME_CENTERLINE_CPP   "C++"
 RW_THR_COMPILER_NAME_DEC_CXX          "CXX Compiler"
 RW_THR_COMPILER_NAME_EGCS_CPP         "EGCS C++ Compiler"
 RW_THR_COMPILER_NAME_HP_ACC           "Ansi C++ Compiler"
 RW_THR_COMPILER_NAME_HP_CPP           "C++ Compiler"
 RW_THR_COMPILER_NAME_IBM_CSET         "CSet++ Compiler"
 RW_THR_COMPILER_NAME_IBM_XLC          "xlC Compiler"
 RW_THR_COMPILER_NAME_METAWARE_HIGHC   "High C/C++"
 RW_THR_COMPILER_NAME_MICROSOFT_CPP    "Optimizing C/C++ Compiler"
 RW_THR_COMPILER_NAME_SGI_CPP          "C++ Compiler"
 RW_THR_COMPILER_NAME_SUN_SUNPRO       "SunPro Compiler"
 RW_THR_COMPILER_NAME_SYMANTEC_CPP     "C/C++ Compiler"
 RW_THR_COMPILER_NAME_WATCOM_CPP       "C/C++ Compiler"

This list will change as new compilers are supported, and support 
for old compilers is dropped.


   Compiler Vendor
   ---------------

RW_THR_COMPILER_VENDOR

This macro defines a string containing the name of the current compiler
vendor.  This macro is given its value by preprocessor directives in 
the library header files that choose and assign one of the following 
macros to the RW_THR_COMPILER_VENDOR macro as is appropriate:
   
 RW_THR_COMPILER_VENDOR_BORLAND        RW_THR_VENDOR_BORLAND
 RW_THR_COMPILER_VENDOR_CENTERLINE     RW_THR_VENDOR_CENTERLINE
 RW_THR_COMPILER_VENDOR_EGCS           RW_THR_VENDOR_EGCS_LINUX
 RW_THR_COMPILER_VENDOR_HP             RW_THR_VENDOR_HP
 RW_THR_COMPILER_VENDOR_IBM            RW_THR_VENDOR_IBM
 RW_THR_COMPILER_VENDOR_METAWARE       RW_THR_VENDOR_METAWARE
 RW_THR_COMPILER_VENDOR_MICROSOFT      RW_THR_VENDOR_MICROSOFT
 RW_THR_COMPILER_VENDOR_SGI            RW_THR_VENDOR_SGI
 RW_THR_COMPILER_VENDOR_SUN            RW_THR_VENDOR_SUN
 RW_THR_COMPILER_VENDOR_SYMANTEC       RW_THR_VENDOR_SYMANTEC
 RW_THR_COMPILER_VENDOR_WATCOM         RW_THR_VENDOR_WATCOM

This list may change as new compilers are supported, and support 
for old compilers is dropped.


   Compiler Version
   ----------------

RW_THR_COMPILER_VERSION
      
This macro defines a numerical value representing the version number of the 
current compiler.  This macro is given its value by preprocessor directives 
in the library header files that determine the compiler version number.
   
   Compiler Version Names
   ----------------------

RW_THR_COMPILER_VERSION_NAME
      
This macro defines a string containing a represention of the version number 
of the current compiler (if known).  This macro is given its value by 
preprocessor directives in the library header files that determine the 
compiler version number.  The version number string is assigned to the 
appropriate macro from the following list, which is then assigned to the 
RW_THR_COMPILER_VERSION_NAME macro:
   
 RW_THR_COMPILER_BORLAND_CPP               ""
 RW_THR_COMPILER_BORLAND_CPP_4_52          "4.52"
 RW_THR_COMPILER_BORLAND_CPP_5_0           "5.0"
 RW_THR_COMPILER_BORLAND_CPP_5_0_1         "5.0.1"
 RW_THR_COMPILER_BORLAND_CPP_5_0_2         "5.0.2"
 RW_THR_COMPILER_BORLAND_CPP_5_3           "5.3"
 RW_THR_COMPILER_BORLAND_CPP_5_4           "5.4"

 RW_THR_COMPILER_DEC_CXX                   ""
 RW_THR_COMPILER_DEC_CXX_5_3               "5.3"
 RW_THR_COMPILER_DEC_CXX_5_4               "5.4"
 RW_THR_COMPILER_DEC_CXX_5_5               "5.5"
 RW_THR_COMPILER_DEC_CXX_5_7               "5.7"
 RW_THR_COMPILER_DEC_CXX_6_0               "6.0"
 RW_THR_COMPILER_DEC_CXX_6_1               "6.1"

 RW_THR_COMPILER_EGCS                      ""
 RW_THR_COMPILER_EGCS_1_1                  "1.1"

 RW_THR_COMPILER_HP_CPP                    ""
 RW_THR_COMPILER_HP_CPP_10_11              "10.11"
 RW_THR_COMPILER_HP_CPP_10_22              "10.22"
 RW_THR_COMPILER_HP_CPP_10_30              "10.30"
 RW_THR_COMPILER_HP_CPP_10_36              "10.36"

 RW_THR_COMPILER_HP_ACC                    ""
 RW_THR_COMPILER_HP_ACC_A_01_12            "A.01.12"
 RW_THR_COMPILER_HP_ACC_A_01_15            "A.01.15"
 RW_THR_COMPILER_HP_ACC_A_01_18            "A.01.18"
 RW_THR_COMPILER_HP_ACC_A_03_05            "A.03.05"
 RW_THR_COMPILER_HP_ACC_A_03_10            "A.03.10"

 RW_THR_COMPILER_IBM_CSET                  ""
 RW_THR_COMPILER_IBM_CSET_3_0              "3.0"

 RW_THR_COMPILER_IBM_XLC                   ""
 RW_THR_COMPILER_IBM_XLC_3_1               "3.1"
 RW_THR_COMPILER_IBM_XLC_3_6               "3.6"

 RW_THR_COMPILER_MICROSOFT_CPP             ""
 RW_THR_COMPILER_MICROSOFT_CPP_10_00       "10.00"
 RW_THR_COMPILER_MICROSOFT_CPP_10_10       "10.10"
 RW_THR_COMPILER_MICROSOFT_CPP_10_20       "10.20"
 RW_THR_COMPILER_MICROSOFT_CPP_11_00       "11.00"
 RW_THR_COMPILER_MICROSOFT_CPP_12_00       "12.00"
 RW_THR_COMPILER_MICROSOFT_MSVC            ""
 RW_THR_COMPILER_MICROSOFT_MSVC_4_0        "4.0"
 RW_THR_COMPILER_MICROSOFT_MSVC_4_1        "4.1"
 RW_THR_COMPILER_MICROSOFT_MSVC_4_2        "4.2"
 RW_THR_COMPILER_MICROSOFT_MSVC_5_0        "5.0"
 RW_THR_COMPILER_MICROSOFT_MSVC_6_0        "6.0"

 RW_THR_COMPILER_SGI_CPP                   ""
 RW_THR_COMPILER_SGI_CPP_6_2               "6.2"
 RW_THR_COMPILER_SGI_CPP_7_1               "7.1"
 RW_THR_COMPILER_SGI_CPP_7_2_1             "7.2.1"

 RW_THR_COMPILER_SUN_SUNPRO                ""
 RW_THR_COMPILER_SUN_SUNPRO_4_0_1          "4.0.1"
 RW_THR_COMPILER_SUN_SUNPRO_4_1_0          "4.1.0"
 RW_THR_COMPILER_SUN_SUNPRO_4_2_0          "4.2.0"
 RW_THR_COMPILER_SUN_SUNPRO_5_0_0          "5.0.0"

 RW_THR_COMPILER_SYMANTEC_CPP              ""
 RW_THR_COMPILER_SYMANTEC_CPP_7_20         "7.20"

This list may change as new compiler versions are supported, and support 
for old compiler versions is dropped.


Operating System
----------------

   Operating System Name
   ---------------------

RW_THR_OS_NAME

This macro defines a string containing the name of the current target
operating system.  This macro is given its value by preprocessor 
directives in the library header files that choose and assign one of 
the following macros to the RW_THR_OS_NAME macro as is appropriate:

 RW_THR_OS_NAME_AIX                 "AIX (Unix)"
 RW_THR_OS_NAME_HPUX                "HP-UX (Unix)"
 RW_THR_OS_NAME_LINUX               "Linux (Unix)"
 RW_THR_OS_NAME_IRIX                "IRIX (Unix)"
 RW_THR_OS_NAME_IRIX64              "IRIX64 (Unix)"
 RW_THR_OS_NAME_MAC_OS              "MacOS"
 RW_THR_OS_NAME_OS2                 "OS/2"
 RW_THR_OS_NAME_OSF1                "OSF/1 (Unix)"
 RW_THR_OS_NAME_SOLARIS             "SunOS/Solaris (Unix)"
 RW_THR_OS_NAME_WIN32               "Win32"
 RW_THR_OS_NAME_WINNT               "Windows NT"
 RW_THR_OS_NAME_WIN95               "Windows 95"

This list may change as new operating systems are supported, and support 
for old operating systems is dropped.


   Operating System Vendor
   -----------------------

RW_THR_OS_VENDOR
      
This macro defines a string containing the name of the current operating
system vendor.  This macro is given its value by preprocessor directives 
in the library header files that choose and assign one of the following 
macros to the RW_THR_OS_VENDOR macro as is appropriate:
   
 RW_THR_OS_VENDOR_HP                       RW_THR_VENDOR_HP
 RW_THR_OS_VENDOR_IBM                      RW_THR_VENDOR_IBM
 RW_THR_OS_VENDOR_MICROSOFT                RW_THR_VENDOR_MICROSOFT
 RW_THR_OS_VENDOR_DEC                      RW_THR_VENDOR_DEC
 RW_THR_OS_VENDOR_LINUX                    RW_THR_VENDOR_EGCS_LINUX
 RW_THR_OS_VENDOR_SGI                      RW_THR_VENDOR_SGI
 RW_THR_OS_VENDOR_SUN                      RW_THR_VENDOR_SUN

This list may change as new operating systems are supported, and support 
for old operating systems is dropped.


   Operating System Version
   ------------------------

RW_THR_OS_VERSION

This macro defines a numerical value representing the version number of the 
current operating system.  This macro is given its value by preprocessor 
directives in the library header files that determine the operating system 
version number.  

   Operating System Version Name
   -----------------------------

RW_THR_OS_VERSION_NAME

This macro defines a string containing a represention of the version number 
of the current operating system (if known).  This macro is given its value by 
preprocessor directives in the library header files that determine the 
operating system version number.  The version number string is assigned to the 
appropriate macro from the following list, which is then assigned to the 
RW_THR_COMPILER_VERSION_NAME macro:

 RW_THR_OS_AIX                             ""
 RW_THR_OS_AIX_4_1                         "4.1"
 RW_THR_OS_AIX_4_2                         "4.2"
 RW_THR_OS_AIX_4_3                         "4.3"

 RW_THR_OS_HPUX                            ""
 RW_THR_OS_HPUX_10_01                      "10.01"
 RW_THR_OS_HPUX_10_10                      "10.10"
 RW_THR_OS_HPUX_10_20                      "10.20"
 RW_THR_OS_HPUX_11_00                      "11.00"

 RW_THR_OS_IRIX                            ""
 RW_THR_OS_IRIX_6_2                        "6.2"
 RW_THR_OS_IRIX_6_4                        "6.4"

 RW_THR_OS_IRIX64                          ""
 RW_THR_OS_IRIX64_6_2                      "6.2"
 RW_THR_OS_IRIX64_6_4                      "6.4"
 RW_THR_OS_IRIX64_6_5                      "6.5"
 
 RW_THR_OS_LINUX                           ""
 RW_THR_OS_LINUX_2_2                       "2.2"

 RW_THR_OS_OS2                             ""
 RW_THR_OS_OS2_3_0                         "3.0"
 RW_THR_OS_OS2_4_0                         "4.0"
 RW_THR_OS_OS2_WARP                        "Warp"

 RW_THR_OS_OSF1                            ""
 RW_THR_OS_OSF1_4_0                        "4.0"

 RW_THR_OS_SOLARIS                         ""
 RW_THR_OS_SOLARIS_2_4                     "2.4"
 RW_THR_OS_SUNOS_5_4                       "5.4"
 RW_THR_OS_SOLARIS_2_5                     "2.5"
 RW_THR_OS_SUNOS_5_5                       "5.5"
 RW_THR_OS_SOLARIS_2_5_1                   "2.5.1"
 RW_THR_OS_SUNOS_5_5_1                     "5.5.1"
 RW_THR_OS_SOLARIS_2_6                     "2.6"
 RW_THR_OS_SUNOS_5_6                       "5.6"
 RW_THR_OS_SOLARIS_2_7                     "2.7"
 RW_THR_OS_SUNOS_5_7                       "5.7"

 RW_THR_OS_WIN32                           ""
 RW_THR_OS_WIN32_95                        "Windows 95"
 RW_THR_OS_WIN32_NT                        "Windows NT"
 RW_THR_OS_WIN32_3_51                      "Win32 3.51 (NT)"
 RW_THR_OS_WIN32_4_00                      "Win32 4.0 (NT/95)"

 RW_THR_OS_WIN95                           "Windows 95"
 RW_THR_OS_WIN98                           "Windows 98"
 RW_THR_OS_WINNT                           "Windows NT"

This list may change as new operating system versions are supported, and 
support for old operating system versions is dropped.


Target Environment Idiosyncracies
---------------------------------

RW_THR_ALLOWS_DYNAMIC_BINDING

The definition of this macro indicates that the current environment 
supports dynamic-link or shared libraries, else the platform only 
supports static binding.


RW_TEMPLATE_EXCEPTION_SPECS_BROKEN

The definition of this macro indicates that the current compiler can't 
parse template class member function implementations with exception 
specifications.

   Example:

      template <class T>
      class MyClass {
         void myFunc() throw(int);
      };
      template <class T>
      void MyClass<T>::myFunc() throw(int) {} // Choke!
      MyClass<int>   myClass;
       
   In MSVC Produces:

      <file-name>(<line-number>) : error C2961: syntax error : 'throw' : unexpected token in template declaration
      <file-name>(<line-number>) : error C2988: unrecognizable template declaration/definition

   or this...

      <file-name>(<line-number>) : fatal error C1001: INTERNAL COMPILER ERROR
      (compiler file 'msc1.cpp', line 899)
         Please choose the Technical Support command on the Visual C++
         Help menu, or open the Technical Support help file for more information


RW_CTOR_EXCEPTION_SPEC_BROKEN

The definition of this macro indicates that the current compiler can't 
parse constructor declarations with exception specifications and no body.

   Example:

      class Foo {
         public:
            Foo() throw(int);
      };
       
   In MSVC Produces:
          
      <file-name>(<line-number>) : error C2059: syntax error : ';'
      <file-name>(<line-number>) : error C2238: unexpected token(s) preceding ';'


RW_OVERLOADED_TEMPLATE_FUNCS_BROKEN

The definition of this macro indicates that the current compiler can't 
handle overloaded template functions where the function signatures are the 
same, but the number or types of template parameters are different.

   Example:

      template <class Foo> class {};
      template <class A> void func(const Foo<A>& first, const Foo<A>& second); // This is ok...
      template <class A, class B> void func(const Foo<A>& first, const Foo<B>& second); // Choke!
      main() {
         Foo<int> a,b;
         Foo<long> c;
         func(a,b);
         func(a,c);
      }
       
   In MSVC Produces:

      <file-name>(<line-number>) : fatal error C1001: INTERNAL COMPILER ERROR
      (compiler file 'msc1.cpp', line 899)
         Please choose the Technical Support command on the Visual C++
         Help menu, or open the Technical Support help file for more information


RW_PARAM_DEFS_IN_TEMPLATE_FUNCTIONS_BROKEN

The definition of this macro indicates that the current compiler can't 
handle default parameters values in a template function declaration when 
there is more than one template argument.

   Example:

      template <class A, class B> void func(const A& first,B* second); // Choke!
      main() {
         int a,b;
         func(a);
      }

   Produces:

      <file-name>(<line-number>) : fatal error C1001: INTERNAL COMPILER ERROR
      (compiler file 'msc1.cpp', line 899)
         Please choose the Technical Support command on the Visual C++
         Help menu, or open the Technical Support help file for more information


RW_THR_NO_CONST_CAST_OPERATOR

The current compiler does not provide a const_cast<> operator for removing the
const attribute from a type.


RW_THR_NO_REINTERPRET_CAST_OPERATOR

The current compiler does not provide a reinterpret_cast<> operator for removing the
const attribute from a type.


RW_THR_NO_EXCEPTION_SPECS        

The definition of this macro indicates that the current compiler does not 
support exception specs


RW_THR_NO_INLINE_EXCEPTION_SPECS 

The definition of this macro indicates that the current compiler does not 
support exception specs on inlined functions.


RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS

Some compilers have problems with inlining on certain types of functions.  This 
macro is used to selectively turn off inlining for the affected functions.

*****************************************************************************/

//////////////////////////////////////////////////////////////////////////////
//
// Identify Target Environment
//    Compiler
//       Compiler Vendor
//       Compiler Name
//       Compiler Version
//    Operating System
//       OS Vendor
//       OS Name
//       OS Version
//
///////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////
//    Microsoft Compiler and Win32
///////////////////////////////////////////////////////////////////////////////

#  if defined(_MSC_VER)

//    Turn off benign but pervasive warning.
#     pragma warning( disable : 4251 )

//    Compiling with a Microsoft C++ compiler (Under WIN16 or WIN32)

#     define RW_THR_COMPILER_VENDOR_MICROSOFT         RW_THR_VENDOR_MICROSOFT
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_MICROSOFT

#     define RW_THR_COMPILER_NAME_MICROSOFT_CPP       "Optimizing C/C++ Compiler"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_MICROSOFT_CPP

#     define RW_THR_COMPILER_MICROSOFT_CPP             ""
#     define RW_THR_COMPILER_MICROSOFT_MSVC            ""

//    Determine compiler version acceptance

#     if (_MSC_VER < 1000)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with Microsoft C++, version 10.0 (MSVC++ v4.0) or newer!
#        endif
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_MICROSOFT_CPP
#     elif (_MSC_VER == 1000)
//       Microsoft WIN32 C++ v10.0 Certified (MSVC++ 4.0)
#        define RW_THR_COMPILER_VERSION_MICROSOFT_CPP     0x00100000
#        define RW_THR_COMPILER_VERSION                   RW_THR_COMPILER_VERSION_MICROSOFT_CPP
#        define RW_THR_COMPILER_MICROSOFT_CPP_10_00       "10.00"
#        define RW_THR_COMPILER_MICROSOFT_MSVC_4_0        "4.0"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_MICROSOFT_CPP_10_00

#        define RW_THR_NO_EXCEPTION_SPECS          
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS // Since no exception specs
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED

#        define RW_TEMPLATE_EXCEPTION_SPECS_BROKEN          
#        define RW_CTOR_EXCEPTION_SPEC_BROKEN               
#        define RW_OVERLOADED_TEMPLATE_FUNCS_BROKEN         
#        define RW_PARAM_DEFS_IN_TEMPLATE_FUNCTIONS_BROKEN  
#        define RW_THR_HAS_SET_TERMINATE_PER_THREAD
#        define RW_THR_SUPERCLASSES_MUST_BE_EXPORTED
#        define RW_THR_MEMBERS_MUST_BE_EXPORTED
#        define RW_THR_NO_THIS_IN_INITIALIZER_LIST

#     elif (_MSC_VER == 1010)
//       Microsoft WIN32 C++ v10.10 Certified (MSVC++ 4.1)
#        define RW_THR_COMPILER_VERSION_MICROSOFT_CPP     0x00101000
#        define RW_THR_COMPILER_VERSION                   RW_THR_COMPILER_VERSION_MICROSOFT_CPP
#        define RW_THR_COMPILER_MICROSOFT_CPP_10_10       "10.10"
#        define RW_THR_COMPILER_MICROSOFT_MSVC_4_1        "4.1"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_MICROSOFT_CPP_10_10

#        define RW_THR_NO_EXCEPTION_SPECS          
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS // Since no exception specs
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_SUPERCLASSES_MUST_BE_EXPORTED
#        define RW_THR_MEMBERS_MUST_BE_EXPORTED

//#        define RW_TEMPLATE_EXCEPTION_SPECS_BROKEN          
//#        define RW_CTOR_EXCEPTION_SPEC_BROKEN               
//#        define RW_OVERLOADED_TEMPLATE_FUNCS_BROKEN         
//#        define RW_PARAM_DEFS_IN_TEMPLATE_FUNCTIONS_BROKEN  
#        define RW_THR_HAS_SET_TERMINATE_PER_THREAD
#        define RW_THR_NO_THIS_IN_INITIALIZER_LIST

#     elif (_MSC_VER == 1020)
//       Microsoft WIN32 C++ v10.20 Certified (MSVC++ 4.2)
#        define RW_THR_COMPILER_VERSION_MICROSOFT_CPP     0x00102000
#        define RW_THR_COMPILER_VERSION                   RW_THR_COMPILER_VERSION_MICROSOFT_CPP
#        define RW_THR_COMPILER_MICROSOFT_CPP_10_20       "10.20"
#        define RW_THR_COMPILER_MICROSOFT_MSVC_4_2        "4.2"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_MICROSOFT_CPP_10_20

#        define RW_THR_NO_EXCEPTION_SPECS          
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS // Since no exception specs
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_SUPERCLASSES_MUST_BE_EXPORTED
#        define RW_THR_MEMBERS_MUST_BE_EXPORTED

//#        define RW_TEMPLATE_EXCEPTION_SPECS_BROKEN          
//#        define RW_CTOR_EXCEPTION_SPEC_BROKEN               
//#        define RW_OVERLOADED_TEMPLATE_FUNCS_BROKEN         
//#        define RW_PARAM_DEFS_IN_TEMPLATE_FUNCTIONS_BROKEN  
#        define RW_THR_HAS_SET_TERMINATE_PER_THREAD
#        define RW_THR_NO_THIS_IN_INITIALIZER_LIST

#     elif (_MSC_VER == 1100)
//       Microsoft WIN32 C++ v11.00 Certified (MSVC++ 5.0)
#        define RW_THR_COMPILER_VERSION_MICROSOFT_CPP     0x00110000
#        define RW_THR_COMPILER_VERSION                   RW_THR_COMPILER_VERSION_MICROSOFT_CPP
#        define RW_THR_COMPILER_MICROSOFT_CPP_11_00       "11.00"
#        define RW_THR_COMPILER_MICROSOFT_MSVC_5_0        "5.0"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_MICROSOFT_CPP_11_00

#        define RW_THR_NO_EXCEPTION_SPECS          
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS // Since no exception specs
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_SUPERCLASSES_MUST_BE_EXPORTED
#        define RW_THR_MEMBERS_MUST_BE_EXPORTED
#        define RW_THR_NO_THIS_IN_INITIALIZER_LIST
#        define RW_THR_HAS_SET_TERMINATE_PER_THREAD

#     elif (_MSC_VER == 1200) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
//       Microsoft WIN32 C++ v12.00 Certified (MSVC++ 6.0)
#        define RW_THR_COMPILER_VERSION_MICROSOFT_CPP     0x00120000
#        define RW_THR_COMPILER_VERSION                   RW_THR_COMPILER_VERSION_MICROSOFT_CPP
#        define RW_THR_COMPILER_MICROSOFT_CPP_12_00       "12.00"
#        define RW_THR_COMPILER_MICROSOFT_MSVC_6_0        "6.0"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_MICROSOFT_CPP_12_00

#        define RW_THR_NO_EXCEPTION_SPECS          
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS // Since no exception specs
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_SUPERCLASSES_MUST_BE_EXPORTED
#        define RW_THR_MEMBERS_MUST_BE_EXPORTED
#        define RW_THR_NO_THIS_IN_INITIALIZER_LIST
#        define RW_THR_HAS_SET_TERMINATE_PER_THREAD
//#        define RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT

#     else
#        error This version of Threads.h++ has not been certified for use with this Microsoft compiler!
#     endif

//    Identify Target Operating System

#     if defined(_WIN32)

//       Compiling for Microsoft Win32 Operating System (NT or 95)

#        define RW_THR_OS_VENDOR_MICROSOFT               RW_THR_VENDOR_MICROSOFT
#        define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_MICROSOFT

#        define RW_THR_OS_NAME_WIN32                     "Windows NT/95 Win32"
#        define RW_THR_OS_NAME                           RW_THR_OS_NAME_WIN32

//       If OS version is not specified then default to latest certified version

#        if !defined(RW_THR_OS_VERSION_WINNT) && !defined(RW_THR_OS_VERSION_WIN95)
#           if defined(RW_THR_OS_VERSION)
#              define RW_THR_OS_VERSION_WIN32            RW_THR_OS_VERSION
#           else
#              define RW_THR_OS_VERSION_WIN32            0x0400
#           endif
#        elif defined(RW_THR_OS_VERSION_WINNT)
#           define RW_THR_OS_VERSION_WIN32               RW_THR_OS_VERSION_WINNT
#           define RW_THR_OS_WIN32_NT                    "Windows NT"
#        elif defined(RW_THR_OS_VERSION_WIN95)
#           define RW_THR_OS_VERSION_WIN32               RW_THR_OS_VERSION_WIN95
#           define RW_THR_OS_WIN32_95                    "Windows 95"
#        endif
#        if !defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_WIN32
#        endif

#        define RW_THR_OS_WIN32                          "Win32"

//       Determine OS version acceptence

#        if (RW_THR_OS_VERSION_WIN32 <= 0x0351) 
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with Win32 under Windows 95, or Windows NT 3.51 or newer!
#           endif
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32
#        elif (RW_THR_OS_VERSION_WIN32 == 0x0351)
#           define RW_THR_OS_WIN32_3_51                  "Win32 3.51 (NT)"
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32_3_51
#        elif (RW_THR_OS_VERSION_WIN32 == 0x0400) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#           define RW_THR_OS_WIN32_4_00                  "Win32 4.0 (NT/95)"
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32_4_00
#        else
#           error This version of Threads.h++ has not been certified for use with this version of Win32!
#        endif

//       Identify architecture and processor

#        if defined(_M_IX86)
#           define RW_THR_PROCESSOR_IX86                 "Intel iX86"
#           define RW_THR_PROCESSOR                      RW_THR_PROCESSOR_IX86

#           define RW_THR_ARCHITECTURE_PC_AT             "IBM PC/AT Compatible"
#           define RW_THR_ARCHITECTURE                   RW_THR_ARCHITECTURE_PC_AT
#        else
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error Threads.h++ is not certified nor supported on this Microsoft compiler and/or its target processor!
#           endif
#        endif

//       Define environment specific features

#        define RW_THR_ALLOWS_DYNAMIC_BINDING

#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this Microsoft compiler and/or its target operating system!
#        endif
#     endif


///////////////////////////////////////////////////////////////////////////////
//    Borland Compiler and Win32 or OS/2
///////////////////////////////////////////////////////////////////////////////

#  elif defined(__BORLANDC__)

//    Compiling with a Borland Compiler (under WIN16, WIN32 or OS/2)

#     define RW_THR_COMPILER_VENDOR_BORLAND           RW_THR_VENDOR_BORLAND
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_BORLAND

#     define RW_THR_COMPILER_NAME_BORLAND_CPP         "C/C++ Compiler"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_BORLAND_CPP

#     define RW_THR_COMPILER_BORLAND_CPP             ""

//    Determine compiler version acceptance

#     if (__BORLANDC__ < 0x0460)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with Borland C++, version 4.52 or newer!
#        endif
#        define RW_THR_COMPILER_VERSION_NAME             RW_THR_COMPILER_BORLAND_CPP
#     elif (__BORLANDC__ == 0x0460)
         // Borland WIN32 C++ v4.52 Certified
#        define RW_THR_COMPILER_VERSION_BORLAND_CPP      0x00045200
#        define RW_THR_COMPILER_VERSION                  RW_THR_COMPILER_VERSION_BORLAND_CPP
#        define RW_THR_COMPILER_BORLAND_CPP_4_52         "4.52"
#        define RW_THR_COMPILER_VERSION_NAME             RW_THR_COMPILER_BORLAND_CPP_4_52
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS   

         // Borland 4.52 has minor problems with friend declarations 
         // that refer to a template class that is parameterized
         // on the class containing the friend declaration.  
#        define RW_THR_NO_SELF_REFERENCING_FRIEND_DECLARATIONS
#        define RW_THR_HAS_SET_TERMINATE_PER_PROCESS
#        define RW_THR_TEMPLATE_INSTANTIATION_BROKEN

#     elif (__BORLANDC__ == 0x0500)
         // Borland WIN32 C++ v5.0 Certified
#        define RW_THR_COMPILER_VERSION_BORLAND_CPP      0x00050000
#        define RW_THR_COMPILER_VERSION                  RW_THR_COMPILER_VERSION_BORLAND_CPP
#        define RW_THR_COMPILER_BORLAND_CPP_5_0          "5.0"
#        define RW_THR_COMPILER_VERSION_NAME             RW_THR_COMPILER_BORLAND_CPP_5_0
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS   
#        define RW_THR_INLINE_STATIC_TEMPLATE_MEMBER_FUNCTIONS

         // Borland 5.0 has major problems with friend declarations 
         // that refer to a template class that is parameterized
         // on the class containing the friend declaration.  
#        define RW_THR_NO_SELF_REFERENCING_FRIEND_DECLARATIONS
#        define RW_THR_HAS_SET_TERMINATE_PER_PROCESS
#        define RW_THR_TEMPLATE_INSTANTIATION_BROKEN

#     elif (__BORLANDC__ == 0x0520)
         // Borland WIN32 C++ v5.02 Certified
#        define RW_THR_COMPILER_VERSION_BORLAND_CPP      0x00050200
#        define RW_THR_COMPILER_VERSION                  RW_THR_COMPILER_VERSION_BORLAND_CPP
#        define RW_THR_COMPILER_BORLAND_CPP_5_0_2        "5.02"
#        define RW_THR_COMPILER_VERSION_NAME             RW_THR_COMPILER_BORLAND_CPP_5_0_2
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS   
#        define RW_THR_INLINE_STATIC_TEMPLATE_MEMBER_FUNCTIONS

         // Borland 5.02 also has major problems with friend declarations 
         // that refer to a template class that is parameterized
         // on the class containing the friend declaration.  
#        define RW_THR_NO_SELF_REFERENCING_FRIEND_DECLARATIONS
#        define RW_THR_HAS_SET_TERMINATE_PER_PROCESS
#        define RW_THR_TEMPLATE_INSTANTIATION_BROKEN

         // Template instantianted upon nested classes which are instance data must
         // be explicitly instantiated
#        define RW_THR_CANT_NEST_PROTECTED_TEMPLATE

#     elif (__BORLANDC__ == 0x0530)
         // Borland WIN32 C++ v5.3 Certified
#        define RW_THR_COMPILER_VERSION_BORLAND_CPP      0x00050300
#        define RW_THR_COMPILER_VERSION                  RW_THR_COMPILER_VERSION_BORLAND_CPP
#        define RW_THR_COMPILER_BORLAND_CPP_5_3          "5.3"
#        define RW_THR_COMPILER_VERSION_NAME             RW_THR_COMPILER_BORLAND_CPP_5_3
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS   
#        define RW_THR_INLINE_STATIC_TEMPLATE_MEMBER_FUNCTIONS

         // Borland also has major problems with friend declarations 
         // that refer to a template class that is parameterized
         // on the class containing the friend declaration.  
#        define RW_THR_NO_SELF_REFERENCING_FRIEND_DECLARATIONS
#        define RW_THR_HAS_SET_TERMINATE_PER_PROCESS
#        define RW_THR_TEMPLATE_INSTANTIATION_BROKEN

         // Template instantianted upon nested classes which are instance data must
         // be explicitly instantiated
#        define RW_THR_CANT_NEST_PROTECTED_TEMPLATE
#        define RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT

#     elif (__BORLANDC__ == 0x0540) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
         // Borland WIN32 C++ v5.4 Certified
#        define RW_THR_COMPILER_VERSION_BORLAND_CPP      0x00050400
#        define RW_THR_COMPILER_VERSION                  RW_THR_COMPILER_VERSION_BORLAND_CPP
#        define RW_THR_COMPILER_BORLAND_CPP_5_4          "5.4"
#        define RW_THR_COMPILER_VERSION_NAME             RW_THR_COMPILER_BORLAND_CPP_5_4
#        define RW_THR_NO_INLINE_EXCEPTION_SPECS   
#        define RW_THR_INLINE_STATIC_TEMPLATE_MEMBER_FUNCTIONS

         // Borland also has major problems with friend declarations 
         // that refer to a template class that is parameterized
         // on the class containing the friend declaration.  
#        define RW_THR_NO_SELF_REFERENCING_FRIEND_DECLARATIONS
#        define RW_THR_HAS_SET_TERMINATE_PER_PROCESS
#        define RW_THR_TEMPLATE_INSTANTIATION_BROKEN

         // Template instantianted upon nested classes which are instance data must
         // be explicitly instantiated
#        define RW_THR_CANT_NEST_PROTECTED_TEMPLATE

// Borland C++ Builder 4.0 can't handle "using" statements for member access adjustments.
// "using" statements will cause an internal compiler error.
// #        define RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT

#     else
#        error This version of Threads.h++ is not certified for use with this Borland compiler!
#     endif

//    Identify Target Operating System

#     if defined(__WIN32__)

//       Compiling for Microsoft Win32 Operating System (NT or 95)

#        define RW_THR_OS_VENDOR_MICROSOFT               RW_THR_VENDOR_MICROSOFT
#        define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_MICROSOFT

#        define RW_THR_OS_NAME_WIN32                     "Windows NT/95 Win32"
#        define RW_THR_OS_NAME                           RW_THR_OS_NAME_WIN32

//       If OS version is not specified then default to latest certified version

#        if !defined(RW_THR_OS_VERSION_WINNT) && !defined(RW_THR_OS_VERSION_WIN95)
#           if defined(RW_THR_OS_VERSION)
#              define RW_THR_OS_VERSION_WIN32            RW_THR_OS_VERSION
#           else
#              define RW_THR_OS_VERSION_WIN32            0x0400
#           endif
#        elif defined(RW_THR_OS_VERSION_WINNT)
#           define RW_THR_OS_VERSION_WIN32               RW_THR_OS_VERSION_WINNT
#           define RW_THR_OS_WIN32_NT                    "Windows NT"
#        elif defined(RW_THR_OS_VERSION_WIN95)
#           define RW_THR_OS_VERSION_WIN32               RW_THR_OS_VERSION_WIN95
#           define RW_THR_OS_WIN32_95                    "Windows 95"
#        endif
#        if !defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_WIN32
#        endif

#        define RW_THR_OS_WIN32                          "Win32"

//       Determine OS version acceptence

#        if (RW_THR_OS_VERSION_WIN32 <= 0x0351) 
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with Win32 under Windows 95, or Windows NT 3.51 or newer!
#           endif
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32
#        elif (RW_THR_OS_VERSION_WIN32 == 0x0351)
#           define RW_THR_OS_WIN32_3_51                  "Win32 3.51 (NT)"
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32_3_51
#        elif (RW_THR_OS_VERSION_WIN32 == 0x0400) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#           define RW_THR_OS_WIN32_4_00                  "Win32 4.0 (NT/95)"
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32_4_00
#        else
#           error This version of Threads.h++ has not been certified for use with this version of Win32!
#        endif

//       Make some assumptions about processor and architecture

#        define RW_THR_PROCESSOR_IX86                    "Intel iX86"
#        define RW_THR_PROCESSOR                         RW_THR_PROCESSOR_IX86

#        define RW_THR_ARCHITECTURE_PC_AT                "IBM PC/AT Compatible"
#        define RW_THR_ARCHITECTURE                      RW_THR_ARCHITECTURE_PC_AT

//       Define environment specific features

#        define RW_THR_ALLOWS_DYNAMIC_BINDING

#     elif defined(__OS2__)

//       Compiling for IBM OS/2 Operating System

#        define RW_THR_OS_VENDOR_IBM                     RW_THR_VENDOR_IBM
#        define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_IBM

#        define RW_THR_OS_NAME_OS2                       "OS/2"
#        define RW_THR_OS_NAME                           RW_THR_OS_NAME_OS2

//       If OS version is not specified then default to latest certified version

#        if !defined(RW_THR_OS_VERSION_OS2)
#           if defined(RW_THR_OS_VERSION)
#              define RW_THR_OS_VERSION_OS2              RW_THR_OS_VERSION
#           else
#              define RW_THR_OS_VERSION_OS2              0x0300
#           endif
#        endif
#        if !defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_OS2
#        endif

#        define RW_THR_OS_OS2                            ""

//       Determine OS version acceptance

#        if (RW_THR_OS_VERSION_OS2 < 0x0300)
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with OS/2 v3.0 or newer!
#           endif
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_OS2
#        elif (RW_THR_OS_VERSION_OS2 == 0x0300) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#           define RW_THR_OS_OS2_3_0                        "3.0"
#           define RW_THR_OS_OS2_WARP                       "Warp"
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_OS2_3_0
#        else
#           error This version of Threads.h++ has not been certified for use with this version of OS/2!
#        endif

//       Make some assumptions about processor and architecture

#        define RW_THR_PROCESSOR_IX86                    "Intel iX86"
#        define RW_THR_PROCESSOR                         RW_THR_PROCESSOR_IX86

#        define RW_THR_ARCHITECTURE_PC_AT                "IBM PC/AT Compatible"
#        define RW_THR_ARCHITECTURE                      RW_THR_ARCHITECTURE_PC_AT

//       Define environment specific features

#        define RW_THR_ALLOWS_DYNAMIC_BINDING

#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this Borland compiler and/or its target operating system!
#        endif
#     endif


///////////////////////////////////////////////////////////////////////////////
//    IBM CSet++ Compiler and OS/2 or Win32
///////////////////////////////////////////////////////////////////////////////

#  elif defined(__IBMCPP__) && !defined(_AIX32)

//    Compiling with an IBM C-Set++ Compiler (under OS/2)

#     define RW_THR_COMPILER_VENDOR_IBM               RW_THR_VENDOR_IBM
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_IBM

#     define RW_THR_COMPILER_NAME_IBM_CSET            "CSet++ Compiler"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_IBM_CSET

#     define RW_THR_COMPILER_IBM_CSET                 ""

//       Determine compiler version acceptance

#     if (__IBMCPP__ < 300)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with IBM Visual Age C++, version 3.0 or newer!
#        endif
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_IBM_CSET
#     elif (__IBMCPP__ == 300) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
         // IBM Visual Age C++ v3.00 Certified
#        define RW_THR_COMPILER_VERSION_IBM_CSET          0x00030000
#        define RW_THR_COMPILER_VERSION                   RW_THR_COMPILER_VERSION_CSET
#        define RW_THR_COMPILER_IBM_CSET_3_0              "3.0"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_IBM_CSET_3_0
#     else
#        error This version of Threads.h++ has not been certified for use with this IBM compiler!
#     endif

#     if defined(__OS2__)

//       Compiling for IBM OS/2 Operating System

#        define RW_THR_OS_VENDOR_IBM                     RW_THR_VENDOR_IBM
#        define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_IBM

#        define RW_THR_OS_NAME_OS2                       "OS/2"
#        define RW_THR_OS_NAME                           RW_THR_OS_NAME_OS2

//       If OS version is not specified then default to latest certified version

#        if !defined(RW_THR_OS_VERSION_OS2)
#           if defined(RW_THR_OS_VERSION)
#              define RW_THR_OS_VERSION_OS2              RW_THR_OS_VERSION
#           else
#              define RW_THR_OS_VERSION_OS2              0x0300
#           endif
#        endif
#        if !defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_OS2
#        endif

#        define RW_THR_OS_OS2                            ""

//       Determine OS version acceptance

#        if (RW_THR_OS_VERSION_OS2 < 0x0300)
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with OS/2 v3.0 or newer!
#           endif
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_OS2
#        elif (RW_THR_OS_VERSION_OS2 == 0x0300) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#           define RW_THR_OS_OS2_3_0                        "3.0"
#           define RW_THR_OS_OS2_WARP                       "Warp"
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_OS2_3_0
#        else
#           error This version of Threads.h++ has not been certified for use with this version of OS/2!
#        endif

//       Make some assumptions about processor and architecture

#        define RW_THR_ARCHITECTURE_PC_AT                "IBM PC/AT Compatible"
#        define RW_THR_ARCHITECTURE                      RW_THR_ARCHITECTURE_PC_AT

#        define RW_THR_PROCESSOR_IX86                    "Intel iX86"
#        define RW_THR_PROCESSOR                         RW_THR_PROCESSOR_IX86

//       Define environment specific features

#        define RW_THR_ALLOWS_DYNAMIC_BINDING
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR

#     elif defined(__WIN32__)

//       Compiling for Microsoft Win32 Operating System (NT or 95)

#        define RW_THR_OS_VENDOR_MICROSOFT               RW_THR_VENDOR_MICROSOFT
#        define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_MICROSOFT

#        define RW_THR_OS_NAME_WIN32                     "Windows NT/95 Win32"
#        define RW_THR_OS_NAME                           RW_THR_OS_NAME_WIN32

//       If OS version is not specified then default to latest certified version

#        if !defined(RW_THR_OS_VERSION_WINNT) && !defined(RW_THR_OS_VERSION_WIN95)
#           if defined(RW_THR_OS_VERSION)
#              define RW_THR_OS_VERSION_WIN32            RW_THR_OS_VERSION
#           else
#              define RW_THR_OS_VERSION_WIN32            0x0400
#           endif
#        elif defined(RW_THR_OS_VERSION_WINNT)
#           define RW_THR_OS_VERSION_WIN32               RW_THR_OS_VERSION_WINNT
#           define RW_THR_OS_WIN32_NT                    "Windows NT"
#        elif defined(RW_THR_OS_VERSION_WIN95)
#           define RW_THR_OS_VERSION_WIN32               RW_THR_OS_VERSION_WIN95
#           define RW_THR_OS_WIN32_95                    "Windows 95"
#        endif
#        if !defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_WIN32
#        endif

#        define RW_THR_OS_WIN32                          "Win32"

//       Determine OS version acceptence

#        if (RW_THR_OS_VERSION_WIN32 <= 0x0351) 
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with Win32 under Windows 95, or Windows NT 3.51 or newer!
#           endif
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32
#        elif (RW_THR_OS_VERSION_WIN32 == 0x0351)
#           define RW_THR_OS_WIN32_3_51                  "Win32 3.51 (NT)"
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32_3_51
#        elif (RW_THR_OS_VERSION_WIN32 == 0x0400) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#           define RW_THR_OS_WIN32_4_00                  "Win32 4.0 (NT/95)"
#           define RW_THR_OS_VERSION_NAME                RW_THR_OS_WIN32_4_00
#        else
#           error This version of Threads.h++ has not been certified for use with this version of Win32!
#        endif

//       Make some assumptions about processor and architecture

#        define RW_THR_PROCESSOR_IX86                    "Intel iX86"
#        define RW_THR_PROCESSOR                         RW_THR_PROCESSOR_IX86

#        define RW_THR_ARCHITECTURE_PC_AT                "IBM PC/AT Compatible"
#        define RW_THR_ARCHITECTURE                      RW_THR_ARCHITECTURE_PC_AT

//       Define environment specific features

#        define RW_THR_ALLOWS_DYNAMIC_BINDING

#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this IBM compiler and/or its target operating system!
#        endif
#     endif

///////////////////////////////////////////////////////////////////////////////
//    IBM xlC Compiler and AIX OS
///////////////////////////////////////////////////////////////////////////////

#  elif defined(__xlC__) && defined(_AIX32) 

//    Assume we are compiling with an IBM xlC Compiler under AIX

#     define RW_THR_COMPILER_VENDOR_IBM               RW_THR_VENDOR_IBM
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_IBM

#     define RW_THR_COMPILER_NAME_XLC                 "xlC Compiler"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_XLC

#     define RW_THR_COMPILER_IBM_XLC                  ""

//       Determine compiler version acceptance

#     if (__xlC__ < 0x0301)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with IBM xlC, version 3.1 or newer!
#        endif
#        define RW_THR_COMPILER_VERSION_NAME           RW_THR_COMPILER_IBM_XLC
#     elif (__xlC__ == 0x0301) 
         // IBM xlC C++ v3.1 Certified
#        define RW_THR_COMPILER_IBM_XLC_3_1            "3.1"
#        define RW_THR_COMPILER_VERSION_NAME           RW_THR_COMPILER_IBM_XLC_3_1
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#     elif (__xlC__ == 0x0306) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
         // IBM xlC C++ v3.6 Certified (AIX 4.3 only!)
#        define RW_THR_COMPILER_IBM_XLC_3_6            "3.6"
#        define RW_THR_COMPILER_VERSION_NAME           RW_THR_COMPILER_IBM_XLC_3_6
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#     else
#        error This version of Threads.h++ has not been certified for use with this IBM compiler!
#     endif

//    Compiling for IBM AIX

#     define RW_THR_OS_VENDOR_IBM                     RW_THR_VENDOR_IBM
#     define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_IBM

#     define RW_THR_OS_NAME_AIX                       "AIX (Unix)"
#     define RW_THR_OS_NAME                           RW_THR_OS_NAME_AIX

//    If OS version is not specified then default to latest certified version

#     if !defined(RW_THR_OS_VERSION_AIX)
#        if defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION_AIX              RW_THR_OS_VERSION
#        else
#           define RW_THR_OS_VERSION_AIX              0x0403
#        endif
#     endif
#     if !defined(RW_THR_OS_VERSION)
#        define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_AIX
#     endif

#     define RW_THR_OS_AIX                            ""

//    Determine OS version acceptance

#     if (RW_THR_OS_VERSION_AIX < 0x0401)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with AIX v4.1 or newer!
#        endif
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_AIX
#     elif (RW_THR_OS_VERSION_AIX == 0x0401) 
#        define RW_THR_OS_AIX_4_1                        "4.1"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_AIX_4_1
#     elif (RW_THR_OS_VERSION_AIX == 0x0402) 
#        define RW_THR_OS_AIX_4_2                        "4.2"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_AIX_4_2
#     elif (RW_THR_OS_VERSION_AIX == 0x0403) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#        define RW_THR_OS_AIX_4_3                        "4.3"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_AIX_4_3
#     else
#        error This version of Threads.h++ has not been certified for use with this version of AIX!
#     endif

//    Determine architecture and processor

#     if defined(_POWER)

#        define RW_THR_ARCHITECTURE_IBM_RS6000        "IBM RS6000 Workstation"
#        define RW_THR_ARCHITECTURE                   RW_THR_ARCHITECTURE_IBM_RS6000

#        define RW_THR_PROCESSOR_POWERPC              "IBM/Motorola/Apple PowerPC"
#        define RW_THR_PROCESSOR                      RW_THR_PROCESSOR_POWERPC

#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this IBM compiler and/or its target processor!
#        endif
#     endif

//    Define environment specific features

#     define RW_THR_ALLOWS_DYNAMIC_BINDING

///////////////////////////////////////////////////////////////////////////////
//    SGI C++ Compiler and IRIX OS
///////////////////////////////////////////////////////////////////////////////

#  elif defined(__sgi)

//    Compiling with SGI C++ Compiler under IRIX

#     define RW_THR_COMPILER_VENDOR_SGI               RW_THR_VENDOR_SGI
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_SGI

#     define RW_THR_COMPILER_NAME_SGI_CPP             "C++ Compiler"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_SGI_CPP

#     define RW_THR_COMPILER_SGI_CPP                  ""

//    The SGI MIPSPro compiler (specified by using -n32 or -64) provides 
//    a builtin macro that gives version number, so...

#     if !defined(_COMPILER_VERSION) 
#           error This version of Threads.h++ is only supported for use with the SGI MIPSPro C++ compiler (Use -n32 or -64)!
#     elif (_COMPILER_VERSION < 710)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with SGI C++, version 7.1 or newer!
#        endif
#        define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_SGI_CPP
#     elif (_COMPILER_VERSION == 710 && _COMPILER_VERSION < 720)
#        define RW_THR_COMPILER_VERSION_SGI_CPP     0x00070100
#        define RW_THR_COMPILER_VERSION             RW_THR_COMPILER_VERSION_SGI_CPP
         // SGI C++ v7.1 Certified
#        define RW_THR_COMPILER_SGI_CPP_7_1           "7.1"
#        define RW_THR_COMPILER_VERSION_NAME           RW_THR_COMPILER_SGI_CPP_7_1
#        define RW_THR_DISABLE_EXCEPTION_SPECS 
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#     elif (_COMPILER_VERSION == 721) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#        define RW_THR_COMPILER_VERSION_SGI_CPP     0x00070201
#        define RW_THR_COMPILER_VERSION             RW_THR_COMPILER_VERSION_SGI_CPP
         // SGI C++ v7.2.1 Certified
#        define RW_THR_COMPILER_SGI_CPP_7_2_1         "7.2.1"
#        define RW_THR_COMPILER_VERSION_NAME           RW_THR_COMPILER_SGI_CPP_7_2_1
#        define RW_THR_DISABLE_EXCEPTION_SPECS 
#        define RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT
#     else
#        error This version of Threads.h++ has not been certified for use with this SGI C++ compiler!
#     endif

#     define RW_THR_OS_VENDOR_SGI                     RW_THR_VENDOR_SGI
#     define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_SGI

#     define RW_THR_OS_IRIX                           ""

#     if !defined(RW_THR_OS_VERSION_IRIX64)
#        define RW_THR_OS_NAME_IRIX                   "IRIX (Unix)"
#        define RW_THR_OS_NAME                        RW_THR_OS_NAME_IRIX

//       If OS version is not specified then default to latest certified version

#        if !defined(RW_THR_OS_VERSION_IRIX)
#           if defined(RW_THR_OS_VERSION)
#              define RW_THR_OS_VERSION_IRIX             RW_THR_OS_VERSION
#           else
#              define RW_THR_OS_VERSION_IRIX             0x0602
#           endif
#        endif
#        if !defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_IRIX
#        endif


//       Determine OS version acceptance

#        if (RW_THR_OS_VERSION_IRIX < 0x0602)
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with IRIX v6.2 or newer!
#           endif
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_IRIX
#        elif (RW_THR_OS_VERSION_IRIX == 0x0602) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#           define RW_THR_OS_IRIX_6_2                       "6.2"
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_IRIX_6_2
#        else
#           error This version of Threads.h++ has not been certified for use with this version of IRIX!
#        endif

#     else
#        define RW_THR_OS_NAME_IRIX64                 "IRIX64 (Unix)"
#        define RW_THR_OS_NAME                        RW_THR_OS_NAME_IRIX64

//       If OS version is not specified then default to latest certified version

#        define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_IRIX64


//       Determine OS version acceptance

#        if (RW_THR_OS_VERSION_IRIX64 < 0x0602)
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with IRIX64 v6.2 or newer!
#           endif
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_IRIX
#        elif (RW_THR_OS_VERSION_IRIX64 == 0x0602) 
#           define RW_THR_OS_IRIX64_6_2                     "6.2"
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_IRIX64_6_2
#        elif (RW_THR_OS_VERSION_IRIX64 == 0x0605) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#           define RW_THR_OS_IRIX64_6_5                     "6.5"
#           define RW_THR_OS_VERSION_NAME                   RW_THR_OS_IRIX64_6_5
#           define RW_THR_CAPABILITY_API_POSIX_1003_6_D14
#        else
#           error This version of Threads.h++ has not been certified for use with this version of IRIX!
#        endif

#     endif

//    Determine architecture and processor

#     if defined(__mips)
#        define RW_THR_PROCESSOR_MIPS                   "MIPS R4X00"
#        define RW_THR_PROCESSOR                         RW_THR_PROCESSOR_MIPS

#        define RW_THR_ARCHITECTURE_SGI_WORKSTATION     "SGI Workstation"
#        define RW_THR_ARCHITECTURE                      RW_THR_ARCHITECTURE_SGI_WORKSTATION

#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this SGI compiler and/or its target platform!
#        endif
#     endif

//    Define environment specific features

#     define RW_THR_ALLOWS_DYNAMIC_BINDING



///////////////////////////////////////////////////////////////////////////////
//    DEC CXX Compiler and DEC OSF/1 (UNIX)
///////////////////////////////////////////////////////////////////////////////

#  elif defined(__DECCXX_VER)

//    Compiling with DEC C++ Compiler under Digital UNIX 

#     define RW_THR_COMPILER_VENDOR_DEC               RW_THR_VENDOR_DEC
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_DEC

#     define RW_THR_COMPILER_NAME_DEC_CXX             "DEC CXX Compiler"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_DEC_CXX

#     define RW_THR_COMPILER_DEC_CXX                  ""

#     if (__DECCXX_VER < 50400000)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with DEC C++, version 5.4 or newer!
#        endif
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_DEC_CXX
#     elif (__DECCXX_VER >= 50400000 && __DECCXX_VER < 50500000)
         // DEC CXX C++ v5.4 Certified
#        define RW_THR_COMPILER_DEC_CXX_5_4               "5.4"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_DEC_CXX_5_4
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_CANT_NEST_PROTECTED_TEMPLATE
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_INLINE_IN_DECLARATION
#     elif (__DECCXX_VER >= 50500000 && __DECCXX_VER < 50700000) 
         // DEC CXX C++ v5.5 Certified
#        define RW_THR_COMPILER_DEC_CXX_5_5               "5.5"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_DEC_CXX_5_5
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_CANT_NEST_PROTECTED_TEMPLATE
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_INLINE_IN_DECLARATION
#     elif (__DECCXX_VER >= 50700000 && __DECCXX_VER < 60000000) 
         // DEC CXX C++ v5.7 Certified
#        define RW_THR_COMPILER_DEC_CXX_5_7               "5.7"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_DEC_CXX_5_7
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_CANT_NEST_PROTECTED_TEMPLATE
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_INLINE_IN_DECLARATION
#     elif (__DECCXX_VER >= 60000000 && __DECCXX_VER < 60100000)
         // DEC CXX C++ v6.0 Certified
         // v6.0 has a bug that can cause multiple copies of 
         // a template instantion to be linked into the library
#        define RW_THR_COMPILER_DEC_CXX_6_0               "6.0"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_DEC_CXX_6_0
#        define RW_THR_DISABLE_EXCEPTION_SPECS 
#        define RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT
#     elif (__DECCXX_VER >= 60100000 && __DECCXX_VER < 70000000) || !defined(RW_THR_ENABLED_CERTIFIED_ONLY)
         // DEC CXX C++ v6.1 Certified
#        define RW_THR_COMPILER_DEC_CXX_6_1               "6.1"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_DEC_CXX_6_1
#        define RW_THR_DISABLE_EXCEPTION_SPECS 
#        define RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT
#     else
#        error This version of Threads.h++ has not been certified for use with this DEC compiler!
#     endif

#     define RW_THR_OS_VENDOR_DEC                     RW_THR_VENDOR_DEC
#     define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_DEC

#     define RW_THR_OS_NAME_OSF1                      "OSF/1 (Digital UNIX)"
#     define RW_THR_OS_NAME                           RW_THR_OS_NAME_OSF1

//    If OS version is not specified then default to latest certified version

#     if !defined(RW_THR_OS_VERSION_OSF1)
#        if defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION_OSF1             RW_THR_OS_VERSION
#        else
#           define RW_THR_OS_VERSION_OSF1             0x0400
#        endif
#     endif
#     if !defined(RW_THR_OS_VERSION)
#        define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_OSF1
#     endif

#     define RW_THR_OS_OSF1                           ""

//    Determine OS version acceptance

#     if (RW_THR_OS_VERSION_OSF1 < 0x0400)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with DEC OSF/1 v4.0 or newer!
#        endif
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_OSF1
#     elif (RW_THR_OS_VERSION_OSF1 == 0x0400) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#        define RW_THR_OS_OSF1_4_0                       "4.0"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_OSF1_4_0
#     else
#        error This version of Threads.h++ has not been certified for use with this version of DEC OSF/1!
#     endif

//    Determine architecture and processor

#     if defined(__alpha)
#        define RW_THR_PROCESSOR_ALPHA                   "DEC Alpha"
#        define RW_THR_PROCESSOR                         RW_THR_PROCESSOR_ALPHA

#        define RW_THR_ARCHITECTURE_DEC_WORKSTATION     "DEC Workstation"
#        define RW_THR_ARCHITECTURE                      RW_THR_ARCHITECTURE_DEC_WORKSTATION

#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this DEC compiler and/or its target platform!
#        endif
#     endif

//    Define environment specific features

#     define RW_THR_ALLOWS_DYNAMIC_BINDING



///////////////////////////////////////////////////////////////////////////////
//    Sun SunPro Cafe Compiler and Solaris OS
///////////////////////////////////////////////////////////////////////////////

#  elif defined(__SUNPRO_CC)

//    Compiling with SunPro C++ Compiler under Solaris

#     define RW_THR_COMPILER_VENDOR_SUN               RW_THR_VENDOR_SUN
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_SUN

#     define RW_THR_COMPILER_NAME_SUNPRO              "SunPro Compiler"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_SUNPRO

#     define RW_THR_COMPILER_SUN_SUNPRO               ""

#     if (__SUNPRO_CC < 0x0401)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with Sunpro C++, version 4.0.1 or newer!
#        endif
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_SUN_SUNPRO
#     elif (__SUNPRO_CC == 0x0401)
         // Sun Sunpro C++ v4.0.1 Certified
#        define RW_THR_COMPILER_SUN_SUNPRO_4_0_1          "4.0.1"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_SUN_SUNPRO_4_0_1
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#     elif (__SUNPRO_CC == 0x0410) 
#        define RW_THR_COMPILER_SUN_SUNPRO_4_1_0          "4.1.0"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_SUN_SUNPRO_4_1_0
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#     elif (__SUNPRO_CC == 0x0420)
         // Sun Sunpro C++ v4.2.0 Certified
#        define RW_THR_COMPILER_SUN_SUNPRO_4_2_0          "4.2.0"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_SUN_SUNPRO_4_2_0
#        define RW_THR_WONT_INSTANTIATE_TYPEDEFS_ON_TEMPLATE_PARAMETER
#        define RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS
#     elif (__SUNPRO_CC == 0x0500) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
         // Sun Sunpro C++ v5.0.0 Certified
#        define RW_THR_COMPILER_SUN_SUNPRO_5_0_0          "5.0.0"
#        define RW_THR_COMPILER_VERSION_NAME              RW_THR_COMPILER_SUN_SUNPRO_5_0_0
#        define RW_THR_WONT_INSTANTIATE_TYPEDEFS_ON_TEMPLATE_PARAMETER
#        define RW_THR_CANT_INLINE_TEMPLATE_FUNCTIONS
#        define RW_THR_SUN_BUG_4227111
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_NAMESPACE_AMBIGUITY
#     else
#        error This version of Threads.h++ has not been certified for use with this Sun compiler!
#     endif

#     define RW_THR_OS_VENDOR_SUN                     RW_THR_VENDOR_SUN
#     define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_SUN

#     define RW_THR_OS_NAME_SOLARIS                   "SunOS/Solaris (Unix)"
#     define RW_THR_OS_NAME                           RW_THR_OS_NAME_SOLARIS

#     define RW_THR_OS_SOLARIS                        ""

//    Determine OS version acceptance

//    If the makefile doesn't define the OS version, use the predefined compiler macro

#     if !defined(RW_THR_OS_VERSION_SUNOS)
#        if (__SunOS_5_3)
#           define RW_THR_OS_VERSION_SUNOS                  0x0530
#        elif defined(__SunOS_5_4)
#           define RW_THR_OS_VERSION_SUNOS                  0x0540
#        elif defined(__SunOS_5_5)  
#           define RW_THR_OS_VERSION_SUNOS                  0x0550
#        elif defined(__SunOS_5_5_1) 
#           define RW_THR_OS_VERSION_SUNOS                  0x0551
#        elif defined(__SunOS_5_6) 
#           define RW_THR_OS_VERSION_SUNOS                  0x0560
#        elif defined(__SunOS_5_7)
#           define RW_THR_OS_VERSION_SUNOS                  0x0570
#        endif
#     endif

#     if (RW_THR_OS_VERSION_SUNOS <= 0x0530)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with SunOS/Solaris v2.4 or newer!
#        endif
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_SOLARIS
#     elif (RW_THR_OS_VERSION_SUNOS == 0x0540)
#        define RW_THR_OS_VERSION_SOLARIS                0x0240
#        define RW_THR_OS_SOLARIS_2_4                    "2.4"
#        define RW_THR_OS_SUNOS_5_4                      "5.4"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_SOLARIS_2_4
#     elif (RW_THR_OS_VERSION_SUNOS == 0x0550)
#        define RW_THR_OS_VERSION_SOLARIS                0x0250
#        define RW_THR_OS_SOLARIS_2_5                    "2.5"
#        define RW_THR_OS_SUNOS_5_5                      "5.5"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_SOLARIS_2_5
#     elif (RW_THR_OS_VERSION_SUNOS == 0x0551)
#        define RW_THR_OS_VERSION_SOLARIS                0x0251
#        define RW_THR_OS_SOLARIS_2_5_1                  "2.5.1"
#        define RW_THR_OS_SUNOS_5_5_1                    "5.5.1"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_SOLARIS_2_5_1
#     elif (RW_THR_OS_VERSION_SUNOS == 0x0560)
#        define RW_THR_OS_VERSION_SOLARIS                0x0260 
#        define RW_THR_OS_SOLARIS_2_6                    "2.6"
#        define RW_THR_OS_SUNOS_5_6                      "5.6"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_SOLARIS_2_6
#     elif (RW_THR_OS_VERSION_SUNOS == 0x0570) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#        define RW_THR_OS_VERSION_SOLARIS                0x0270 
#        define RW_THR_OS_SOLARIS_2_7                    "2.7"
#        define RW_THR_OS_SUNOS_5_7                      "5.7"
#        define RW_THR_OS_VERSION_NAME                   RW_THR_OS_SOLARIS_2_7
#        if (__SUNPRO_CC < 0x0420) && defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ does not support the use of compiler versions earlier than 4.2 under Solaris 2.6!
#        endif
#     else
#        error This version of Threads.h++ has not been certified for use with this version of SunOS/Solaris!
#     endif
#     if !defined(RW_THR_OS_VERSION)
#        define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_SOLARIS
#     endif

//    Determine architecture and processor

#     if defined(sparc) || defined(__sparc)
#        define RW_THR_PROCESSOR_SPARC                   "Sun SPARC"
#        define RW_THR_PROCESSOR                         RW_THR_PROCESSOR_SPARC

#        define RW_THR_ARCHITECTURE_SUN_SPARCSTATION     "Sun SPARCstation"
#        define RW_THR_ARCHITECTURE                      RW_THR_ARCHITECTURE_SUN_SPARCSTATION

#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this Sun compiler and/or its target platform!
#        endif
#     endif

//    Define environment specific features

#     define RW_THR_ALLOWS_DYNAMIC_BINDING


///////////////////////////////////////////////////////////////////////////////
//    HP C++ CFront Compiler and HP-UX OS
///////////////////////////////////////////////////////////////////////////////

// Distinguish from CenterLine compiler and the HP Ansi C++ Compiler
#  elif defined(__hpux) && defined(hpux) && !defined(__CLCC__) && !(__cplusplus >= 199707L)

//    Compiling with HP C++ Compiler under HPUX

#     define RW_THR_COMPILER_VENDOR_HP                RW_THR_VENDOR_HP
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_HP

#     define RW_THR_COMPILER_NAME_HP_CPP              "C++ Compiler (CFront)"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_HP_CPP

//    HP compiler doesn't provide builtin macro that gives version number, so...
//    If compiler version is not specified then default to latest certified version

#     if !defined(RW_THR_COMPILER_VERSION_HP_CPP)
#        if defined(RW_THR_COMPILER_VERSION)
#           define RW_THR_COMPILER_VERSION_HP_CPP     RW_THR_COMPILER_VERSION
#        else                                       
#           define RW_THR_COMPILER_VERSION_HP_CPP     0x00103600
#        endif
#     endif
#     if !defined(RW_THR_COMPILER_VERSION)
#        define RW_THR_COMPILER_VERSION               RW_THR_COMPILER_VERSION_HP_CPP
#     endif

#     define RW_THR_COMPILER_HP_CPP                   ""

#     if (RW_THR_COMPILER_VERSION_HP_CPP < 0x00100100)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with HP C++, version 10.01 or newer!
#        endif
#        define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_CPP
#     elif (RW_THR_COMPILER_VERSION_HP_CPP == 0x00100100)
         // HP C++ v10.01 Certified
#        define RW_THR_COMPILER_HP_CPP_10_01          "10.01"
#        define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_CPP_10_01
#        define RW_THR_INLINE_IN_DECLARATION
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_BROKEN_TYPEDEF_INHERITANCE
#        define RW_THR_BROKEN_PROTECTED_INHERITANCE
#     elif (RW_THR_COMPILER_VERSION_HP_CPP == 0x00101100) 
         // HP C++ v10.11 Certified
#        define RW_THR_COMPILER_HP_CPP_10_11          "10.11"
#        define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_CPP_10_11

#        define RW_THR_INLINE_IN_DECLARATION
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_BROKEN_TYPEDEF_INHERITANCE
#        define RW_THR_BROKEN_PROTECTED_INHERITANCE

//HP C++ won't inline function with exception specifications
#        define RW_THR_DISABLE_EXCEPTION_SPECS 

#     elif (RW_THR_COMPILER_VERSION_HP_CPP == 0x00102200) 
         // HP C++ v10.22 Certified
#        define RW_THR_COMPILER_HP_CPP_10_22          "10.22"
#        define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_CPP_10_22

#        define RW_THR_INLINE_IN_DECLARATION
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_BROKEN_TYPEDEF_INHERITANCE
#        define RW_THR_BROKEN_PROTECTED_INHERITANCE

//       This gets rid of that pesky Signal 11 from the linker
#        define RW_THR_DISABLE_EXCEPTION_SPECS 


#     elif (RW_THR_COMPILER_VERSION_HP_CPP == 0x00103000)
         // HP C++ v10.30 Certified
#        define RW_THR_COMPILER_HP_CPP_10_30          "10.30"
#        define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_CPP_10_30

#        define RW_THR_INLINE_IN_DECLARATION
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_BROKEN_TYPEDEF_INHERITANCE
#        define RW_THR_BROKEN_PROTECTED_INHERITANCE

//       This gets rid of that pesky Signal 11 from the linker
#        define RW_THR_DISABLE_EXCEPTION_SPECS 

#     elif (RW_THR_COMPILER_VERSION_HP_CPP == 0x00103600) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
         // HP C++ v10.36 Certified
#        define RW_THR_COMPILER_HP_CPP_10_36          "10.36"
#        define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_CPP_10_36

#        define RW_THR_INLINE_IN_DECLARATION
#        define RW_THR_NO_REINTERPRET_CAST_OPERATOR
#        define RW_THR_NO_CONST_CAST_OPERATOR
#        define RW_THR_UNREACHABLE_RETURN_REQUIRED
#        define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#        define RW_THR_BROKEN_PROTECTED_INHERITANCE
#        define RW_THR_DISABLE_EXCEPTION_SPECS 

#     else
#        error This version of Threads.h++ has not been certified for use with this HP compiler!
#     endif

#     define RW_THR_OS_VENDOR_HP                      RW_THR_VENDOR_HP
#     define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_HP

#     define RW_THR_OS_NAME_HPUX                      "HP-UX (Unix)"
#     define RW_THR_OS_NAME                           RW_THR_OS_NAME_HPUX


//    If OS version is not specified then default to latest certified version

#     if !defined(RW_THR_OS_VERSION_HPUX)
#        if defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION_HPUX             RW_THR_OS_VERSION
#        else
#           define RW_THR_OS_VERSION_HPUX             0x1020
#        endif
#     endif
#     if !defined(RW_THR_OS_VERSION)
#        define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_HPUX
#     endif

#     define RW_THR_OS_HPUX                           ""

//    Determine OS version acceptance

#     if (RW_THR_OS_VERSION_HPUX < 0x1001)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with HP-UX v10.01 or newer!
#        endif
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX
#     elif (RW_THR_OS_VERSION_HPUX >= 0x1100)
#        error Incorrect OS version specified - The old HP C++ compiler is not supported under HPUX 11.X!  
#     elif (RW_THR_OS_VERSION_HPUX == 0x1001)
#        define RW_THR_OS_HPUX_10_01                  "10.01"
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX_10_01
#     elif (RW_THR_OS_VERSION_HPUX == 0x1010)
#        define RW_THR_OS_HPUX_10_01                  "10.10"
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX_10_10
#     elif (RW_THR_OS_VERSION_HPUX == 0x1020) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#        define RW_THR_OS_HPUX_10_20                  "10.20"
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX_10_20
#     else
#        error This version of Threads.h++ has not been certified for use with this version of HP-UX!
#     endif

//    Determine architecture and processor

#     if defined(__hp9000s700) || defined(__hp9000s800) 
#        define RW_THR_ARCHITECTURE_HP_9000           "HP-9000 Workstation"
#        define RW_THR_ARCHITECTURE                   RW_THR_ARCHITECTURE_HP_9000
#        if defined(_PARISC) || defined(_PA_RISC1_1) || defined(_PA_RISC2_0)
#           define RW_THR_PROCESSOR_PA_RISC           "HP PA-Risc"
#           define RW_THR_PROCESSOR                   RW_THR_PROCESSOR_PA_RISC
#        endif
#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this HP compiler and/or its target platform!
#        endif
#     endif

//    Define environment specific features

#     define RW_THR_ALLOWS_DYNAMIC_BINDING


///////////////////////////////////////////////////////////////////////////////
//    HP Ansi C++ and HP-UX OS
///////////////////////////////////////////////////////////////////////////////

// Distinguish from CenterLine compiler and the HP CFront C++ Compiler
#  elif defined(__hpux) && !defined(__CLCC__) && (__cplusplus >= 199707L)

//    Compiling with HP C++ Compiler under HPUX

//    Support for compiler versions depends on OS version

//    If OS version is not specified then default to latest certified version

#     if !defined(RW_THR_OS_VERSION_HPUX)
#        if defined(RW_THR_OS_VERSION)
#           define RW_THR_OS_VERSION_HPUX             RW_THR_OS_VERSION
#        else
#           define RW_THR_OS_VERSION_HPUX             0x1100
#        endif
#     endif
#     if !defined(RW_THR_OS_VERSION)
#        define RW_THR_OS_VERSION                     RW_THR_OS_VERSION_HPUX
#     endif

#     define RW_THR_OS_HPUX                           ""

#     define RW_THR_COMPILER_VENDOR_HP                RW_THR_VENDOR_HP
#     define RW_THR_COMPILER_VENDOR                   RW_THR_COMPILER_VENDOR_HP

#     define RW_THR_COMPILER_NAME_HP_ACC             "Ansi C++ Compiler"
#     define RW_THR_COMPILER_NAME                     RW_THR_COMPILER_NAME_HP_ACC

//    HP compiler doesn't provide builtin macro that gives version number, so...
//    If compiler version is not specified then default to latest certified version

#     if !defined(RW_THR_COMPILER_VERSION_HP_ACC)
#        if defined(RW_THR_COMPILER_VERSION)
#           define RW_THR_COMPILER_VERSION_HP_ACC     RW_THR_COMPILER_VERSION
#        else                                       
#           if (RW_THR_OS_VERSION_HPUX >= 0x1100)
//             Latest compiler version supported under HPUX 11.X systems
#              define RW_THR_COMPILER_VERSION_HP_ACC     0x031000
#           else
//             Latest compiler version supported under HPUX 10.X systems
#              define RW_THR_COMPILER_VERSION_HP_ACC     0x011800
#           endif
#        endif
#     endif
#     if !defined(RW_THR_COMPILER_VERSION)
#        define RW_THR_COMPILER_VERSION               RW_THR_COMPILER_VERSION_HP_ACC
#     endif

#     define RW_THR_COMPILER_HP_ACC                   ""

#     if (RW_THR_OS_VERSION_HPUX < 0x1100)

         // Compiler versions supported under HPUX 10.X
#        if (RW_THR_COMPILER_VERSION_HP_ACC < 0x010100)
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with HP C++, version A.01.XX or newer!
#           endif
#           define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_ACC

#        elif (RW_THR_COMPILER_VERSION_HP_ACC == 0x010100)
            // HP C++ vA.01.01 Certified
#           define RW_THR_COMPILER_HP_ACC_A_01_01        "A.01.01"
#           define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_ACC_A_01_01

#           define RW_THR_UNREACHABLE_RETURN_REQUIRED
#           define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#           define RW_THR_BROKEN_TYPEDEF_INHERITANCE
#           define RW_THR_BROKEN_PROTECTED_INHERITANCE
#           define RW_THR_CANT_THROW_FROM_INLINE

#        elif (RW_THR_COMPILER_VERSION_HP_ACC == 0x011200)
            // HP C++ vA.01.12 Certified (HPUX 10.X only!)
#           define RW_THR_COMPILER_HP_ACC_A_01_12        "A.01.12"
#           define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_ACC_A_01_12

#           define RW_THR_UNREACHABLE_RETURN_REQUIRED
#           define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#           define RW_THR_BROKEN_TYPEDEF_INHERITANCE
#           define RW_THR_BROKEN_PROTECTED_INHERITANCE
#           define RW_THR_CANT_THROW_FROM_INLINE

#        elif (RW_THR_COMPILER_VERSION_HP_ACC == 0x011500)
            // HP C++ vA.01.15 Certified (HPUX 10.X only!)
#           define RW_THR_COMPILER_HP_ACC_A_01_15        "A.01.15"
#           define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_ACC_A_01_15

#           define RW_THR_UNREACHABLE_RETURN_REQUIRED
#           define RW_THR_BROKEN_PROTECTED_INHERITANCE
//#           define RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT   Doesn't work!

#        elif (RW_THR_COMPILER_VERSION_HP_ACC == 0x011800) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
            // HP C++ vA.01.15 Certified (HPUX 10.X only!)
#           define RW_THR_COMPILER_HP_ACC_A_01_18        "A.01.18"
#           define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_ACC_A_01_18

#           define RW_THR_UNREACHABLE_RETURN_REQUIRED
#           define RW_THR_BROKEN_PROTECTED_INHERITANCE
#        endif

#     else

         // Compiler versions supported under HPUX 11.X
#        if (RW_THR_COMPILER_VERSION_HP_ACC < 0x030500)
#           if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#              error This version of Threads.h++ is only supported for use with HP C++, version A.03.05 or newer!
#           endif
#           define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_ACC

#        elif (RW_THR_COMPILER_VERSION_HP_ACC == 0x030500)
            // HP C++ vA.03.05 Certified (HPUX 11.X only!)
#           define RW_THR_COMPILER_HP_ACC_A_03_05        "A.03.05"
#           define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_ACC_A_03_05

#           define RW_THR_UNREACHABLE_RETURN_REQUIRED
#           define RW_THR_CANT_EXTRACT_TEMPLATE_ARGS_FROM_SIGNATURE
#           define RW_THR_BROKEN_TYPEDEF_INHERITANCE
#           define RW_THR_BROKEN_PROTECTED_INHERITANCE
#           define RW_THR_CANT_THROW_FROM_INLINE

#        elif (RW_THR_COMPILER_VERSION_HP_ACC == 0x031000) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
            // HP C++ vA.03.10 Certified (HPUX 11.X only!)
#           define RW_THR_COMPILER_HP_ACC_A_03_10        "A.03.10"
#           define RW_THR_COMPILER_VERSION_NAME          RW_THR_COMPILER_HP_ACC_A_03_10

#           define RW_THR_UNREACHABLE_RETURN_REQUIRED
//#           define RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT   Doesn't work!

#        else
#           error This version of Threads.h++ has not been certified for use with this Ansi HP compiler!
#        endif
#     endif

#     define RW_THR_OS_VENDOR_HP                      RW_THR_VENDOR_HP
#     define RW_THR_OS_VENDOR                         RW_THR_OS_VENDOR_HP

#     define RW_THR_OS_NAME_HPUX                      "HP-UX (Unix)"
#     define RW_THR_OS_NAME                           RW_THR_OS_NAME_HPUX
//    Determine OS version acceptance

#     if (RW_THR_OS_VERSION_HPUX < 0x1001)
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error This version of Threads.h++ is only supported for use with HP-UX v10.01 or newer!
#        endif
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX
#     elif (RW_THR_OS_VERSION_HPUX == 0x1001) 
#        define RW_THR_OS_HPUX_10_01                  "10.01"
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX_10_01

#     elif (RW_THR_OS_VERSION_HPUX == 0x1010) 
#        define RW_THR_OS_HPUX_10_10                  "10.10"
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX_10_10

#     elif (RW_THR_OS_VERSION_HPUX == 0x1020) 
#        define RW_THR_OS_HPUX_10_20                  "10.20"
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX_10_20

#     elif (RW_THR_OS_VERSION_HPUX == 0x1100) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
#        define RW_THR_OS_HPUX_11_00                  "11.00"
#        define RW_THR_OS_VERSION_NAME                RW_THR_OS_HPUX_11_00
#     else
#        error This version of Threads.h++ has not been certified for use with this version of HP-UX!
#     endif

//    Determine architecture and processor

#     if defined(__hp9000s700) || defined(__hp9000s800) 
#        define RW_THR_ARCHITECTURE_HP_9000           "HP-9000 Workstation"
#        define RW_THR_ARCHITECTURE                   RW_THR_ARCHITECTURE_HP_9000
#        if defined(_PARISC) || defined(_PA_RISC1_1) || defined(_PA_RISC2_0)
#           define RW_THR_PROCESSOR_PA_RISC           "HP PA-Risc"
#           define RW_THR_PROCESSOR                   RW_THR_PROCESSOR_PA_RISC
#        endif
#     else
#        if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#           error Threads.h++ is not certified nor supported on this HP compiler and/or its target platform!
#        endif
#     endif

//    Define environment specific features

#     define RW_THR_ALLOWS_DYNAMIC_BINDING


///////////////////////////////////////////////////////////////////////////////
//    EGCS Compiler on Linux
///////////////////////////////////////////////////////////////////////////////

#  elif defined(__GNUC__)

// Compiling with EGCS/GCC under Linux

#    define RW_THR_COMPILER_VENDOR_EGCS               RW_THR_VENDOR_EGCS_LINUX
#    define RW_THR_COMPILER_VENDOR                    RW_THR_COMPILER_VENDOR_EGCS

#    define RW_THR_COMPILER_NAME_EGCS                 "EGCS Compiler"
#    define RW_THR_COMPILER_NAME                      RW_THR_COMPILER_NAME_EGCS

#    define RW_THR_COMPILER_EGCS                 ""

#    if (__GNUC__ < 2 || (__GNUC__ == 2 && __GNUC_MINOR__ < 91))
#       if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#          error This version of Threads.h++ is only supported for use with GCC 2.91 or newer!
#       endif
#       define RW_THR_COMPILER_VERSION_NAME           RW_THR_COMPILER_EGCS
#    elif (__GNUC__ == 2 && __GNUC_MINOR__ == 91)
#       // Linux GCC/EGCS v2.91 certified
#       define RW_THR_COMPILER_EGCS_1_1              "1.1"
#       define RW_THR_COMPILER_VERSION_NAME           RW_THR_COMPILER_EGCS_1_1
#    elif !defined(RW_THR_ENABLE_CERTIFIED_ONLY)
        // uncertified compiler
#       define RW_THR_COMPILER_VERSION_NAME           RW_THR_COMPILER_EGCS
#    else
#       error This version of Threads.h++ has not been certified for use with this Linux compiler!
#    endif

#    define RW_THR_OS_VENDOR_LINUX                    RW_THR_VENDOR_EGCS_LINUX
#    define RW_THR_OS_VENDOR                          RW_THR_OS_VENDOR_LINUX

#    define RW_THR_OS_NAME_LINUX                      "Linux"
#    define RW_THR_OS_NAME                            RW_THR_OS_NAME_LINUX

//   If OS version is not specified then default to latest certified version

#    if !defined(RW_THR_OS_VERSION_LINUX)
#       if defined(RW_THR_OS_VERSION)
#          define RW_THR_OS_VERSION_LINUX             RW_THR_OS_VERSION
#       else
#          define RW_THR_OS_VERSION_LINUX             0x0220
#       endif
#    endif
#    if !defined(RW_THR_OS_VERSION)
#       define RW_THR_OS_VERSION                      RW_THR_OS_VERSION_LINUX
#    endif

#    define RW_THR_OS_LINUX                           ""

//   Determine OS version acceptance

#    if (RW_THR_OS_VERSION_LINUX < 0x0220)
#       if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#          error This version of Threads.h++ is only supported for use with Linux v2.2 or newer!
#       endif
#       define RW_THR_OS_VERSION_NAME                 RW_THR_OS_LINUX
#    elif (RW_THR_OS_VERSION_LINUX == 0x0220)
#       define RW_THR_OS_LINUX_2_2                    "2.2"
#       define RW_THR_OS_VERSION_NAME                 RW_THR_OS_LINUX_2_2

//      egcs has problems with access modifiers on base classes
#       define RW_THR_BROKEN_TEMPLATE_ACCESS_TO_BASE_CLASS
#    else
#       error This version of Threads.h++ has not been certified for use with this version of Linux!
#    endif

//   Determine architecture and processor

#    if defined(i386)
#       define RW_THR_PROCESSOR_IX86                  "Intel iX86"
#       define RW_THR_PROCESSOR                       RW_THR_PROCESSOR_IX86

#       define RW_THR_ARCHITECTURE_PC_AT              "IBM PC/AT Compatible"
#       define RW_THR_ARCHITECTURE                    RW_THR_ARCHITECTURE_PC_AT

#    else
#       if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#          error Threads.h++ is not certified nor supported on this platform!
#       endif
#    endif

//   Define environment specific

#    define RW_THR_ALLOWS_DYNAMIC_BINDING


#  endif //End compiler identification


///////////////////////////////////////////////////////////////////////////////
//    Supply default values for missing target environment macros
///////////////////////////////////////////////////////////////////////////////

#  if !defined(RW_THR_COMPILER_NAME)
#     define RW_THR_COMPILER_NAME                     RW_THR_UNKNOWN
#  endif

#  if !defined(RW_THR_COMPILER_VENDOR)
#     define RW_THR_COMPILER_VENDOR                   RW_THR_UNKNOWN
#  endif

#  if !defined(RW_THR_COMPILER_VERSION)
#     define RW_THR_COMPILER_VERSION                  RW_THR_VERSION_UNKOWN
#  endif

#  if !defined(RW_THR_OS_NAME)
#     define RW_THR_OS_NAME                           RW_THR_UNKNOWN
#  endif

#  if !defined(RW_THR_OS_VENDOR)
#     define RW_THR_OS_VENDOR                         RW_THR_UNKNOWN
#  endif

#  if !defined(RW_THR_OS_VERSION)
#     define RW_THR_OS_VERSION                        RW_THR_VERSION_UNKNOWN
#  endif

#  if !defined(RW_THR_ARCHITECTURE)
#     define RW_THR_ARCHITECTURE                      RW_THR_UNKNOWN
#  endif

#  if !defined(RW_THR_PROCESSOR)
#     define RW_THR_PROCESSOR                         RW_THR_UNKNOWN
#  endif



//////////////////////////////////////////////////////////////////////////////
//
// Verify Threads.h++ Compatibility With Tools.h++ Version
//
//////////////////////////////////////////////////////////////////////////////
#  if !defined(__RWTOOLDEFS_H__)
#     include <rw/tooldefs.h>
#  endif

#  if !defined(RWTOOLS) 
#     if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#        error Unrecognized version of Tools.h++ - RWTOOLS macro missing!
#     endif
#  elif (RWTOOLS < 0x0702)
//    Not supported
#     if defined(RW_THR_ENABLE_SUPPORTED_ONLY)
#        error Threads.h++ must be compiled with Tools.h++ v7.0.2 or higher!
#     endif
#  elif (RWTOOLS <= 0x0710) || !defined(RW_THR_ENABLE_CERTIFIED_ONLY) 
//    Certified
#  else
//    Supported, but not certified...
#     error This version of Threads.h++ is not certified for use with this version of Tools.h++!
#  endif

///////////////////////////////////////////////////////////////////////////////
// Identify current thread API
///////////////////////////////////////////////////////////////////////////////

#  if defined(RW_THR_OS_AIX)
#     define RW_THR_THREAD_API_POSIX
#     if (RW_THR_OS_VERSION_AIX < 0x0401)
#        define RW_THR_THREAD_API_DCE
#        define RW_THR_THREAD_API_DCE_AIX
#        define RW_THR_THREAD_API_DCE_IBM
#        define RW_THR_THREAD_API               "IBM DCE 1.1 (IEEE POSIX 1003.4a Draft 4)"
#        define RW_DCE_PTHREAD_H <pthread.h>
#        if !defined(RW_DCE_THREADS)
#           error Threads.h++ only supports the DCE threads API under this version of AIX 
#        endif
#     elif (RW_THR_OS_VERSION_AIX < 0x0403)
#        if defined(RW_DCE_THREADS)
#           define RW_THR_THREAD_API_DCE
#           define RW_THR_THREAD_API_DCE_AIX
#           define RW_THR_THREAD_API_DCE_IBM
#           define RW_THR_THREAD_API               "IBM DCE 1.1 (IEEE POSIX 1003.4a Draft 4)"
#           define RW_DCE_PTHREAD_H <pthread.h>
#        elif defined(RW_POSIX_D10_THREADS)                          
#           define RW_THR_THREAD_API_POSIX_1003_1C
#           define RW_THR_THREAD_API_POSIX_1003_1C_AIX
#           define RW_THR_THREAD_API_POSIX_1003_1C_D7_AIX
#           define RW_THR_THREAD_API_POSIX_1003_1C_IBM
#           define RW_THR_THREAD_API_POSIX_1003_1C_D7_IBM
#           define RW_THR_THREAD_API               "IBM/AIX IEEE POSIX 1003.1c Draft 7"
#        else
#          error A valid thread API was not selected! (DCE or POSIX) 
#        endif
#     else
#        if defined(RW_DCE_THREADS)
#           define RW_THR_THREAD_API_DCE
#           define RW_THR_THREAD_API_DCE_AIX
#           define RW_THR_THREAD_API_DCE_IBM
#           define RW_THR_THREAD_API               "IBM DCE 1.1 (IEEE POSIX 1003.4a Draft 4)"
#           define RW_DCE_PTHREAD_H <pthread.h>
#        elif defined(RW_POSIX_D10_THREADS)                          
#           define RW_THR_THREAD_API_POSIX_1003_1C
#           define RW_THR_THREAD_API_POSIX_1003_1C_AIX
#           define RW_THR_THREAD_API_POSIX_1003_1C_IBM
#           define RW_THR_THREAD_API               "IBM/AIX IEEE POSIX 1003.1c Final"
#        else
#          error A valid thread API was not selected! (DCE or POSIX) 
#        endif
#     endif
#  elif defined(RW_THR_OS_HPUX)
#     define RW_THR_THREAD_API_POSIX
#     if (RW_THR_OS_VERSION_HPUX < 0x1100)
#        define RW_THR_THREAD_API_DCE
#        define RW_THR_THREAD_API_DCE_HPUX
#        define RW_THR_THREAD_API_DCE_HP
#        define RW_THR_CMA_THREAD_ID
#        define RW_THR_THREAD_API               "HP DCE 1.1 (IEEE POSIX 1003.4a Draft 4)"
#        define RW_DCE_PTHREAD_H <pthread.h>
#        if !defined(RW_DCE_THREADS)
#           error Threads.h++ only supports the DCE threads API under this version of HPUX
#        endif
#     else // > 11.0    
#        if defined(RW_DCE_THREADS)
#          define RW_THR_THREAD_API_DCE
#          define RW_THR_THREAD_API_DCE_HPUX
#          define RW_THR_THREAD_API_DCE_HP
#          define RW_THR_CMA_THREAD_ID
#          define RW_THR_THREAD_API             "HP DCE 1.1 (IEEE POSIX 1003.4a Draft 4)"
#          define RW_DCE_PTHREAD_H <dce/pthread.h>
#        elif defined(RW_POSIX_D10_THREADS)                          
#          define RW_THR_THREAD_API_POSIX_1003_1C
#          define RW_THR_THREAD_API_POSIX_1003_1C_HPUX
#          define RW_THR_THREAD_API_POSIX_1003_1C_HP
#          define RW_THR_THREAD_API             "HP/HPUX IEEE POSIX 1003.1c Final"
#        else
#          error A valid thread API was not selected! (DCE or POSIX)
#        endif
#     endif
#  elif defined(RW_THR_OS_IRIX)
#     define RW_THR_THREAD_API_POSIX
#     define RW_THR_THREAD_API_POSIX_1003_1C
#     define RW_THR_THREAD_API_POSIX_1003_1C_IRIX
#     define RW_THR_THREAD_API_POSIX_1003_1C_SGI
#     define RW_THR_THREAD_API               "SGI/IRIX IEEE POSIX 1003.1c Final"
#     if defined(RW_THR_CAPABILITY_API_POSIX_1003_6_D14)
#        define RW_THR_SYSTEM_SCOPE_REQUIRES_CAP_SCHED_MGT
#     endif
#     if !defined(RW_POSIX_D10_THREADS)
#        error Threads.h++ only supports the POSIX 1003.1c threads API under this version of IRIX 
#     endif
#  elif defined(RW_THR_OS_OS2)
#     define RW_THR_THREAD_API_OS2              
#     if defined(RW_THR_COMPILER_BORLAND_CPP)
#        define RW_THR_THREAD_API_OS2_BORLAND_CPP
#        define RW_THR_THREAD_API               "IBM OS/2 & Borland RTL"
#     elif defined(RW_THR_COMPILER_IBM_CSET)
#        define RW_THR_THREAD_API_OS2_IBM_CSET
#        define RW_THR_THREAD_API               "IBM OS/2 & IBM CSet++ RTL"
#     else
#        error A valid thread API was not selected! (unrecognized compiler and runtime-library) 
#     endif
#  elif defined(RW_THR_OS_OSF1)
#     define RW_THR_THREAD_API_POSIX
#     define RW_THR_THREAD_API_POSIX_1003_1C
#     define RW_THR_THREAD_API_POSIX_1003_1C_OSF1
#     define RW_THR_THREAD_API_POSIX_1003_1C_DEC
#     define RW_THR_TEB_THREAD_ID
#     include <pthread.h>
#     if !defined(PTHREAD_TEB_VERSION)
//      System has old pthread library installed (dUnix 4.0)
#       define RW_THR_THREAD_API                  "DEC/OSF1 IEEE POSIX 1003.1c v0 - June 1995"
#       define RW_THR_THREAD_API_POSIX_1003_1C_OSF1_V0
#     else
//      System has new pthread library installed (dUnix 4.0d)
#       define RW_THR_THREAD_API                  "DEC/OSF1 IEEE POSIX 1003.1c v1 - June 1995"
#       define RW_THR_THREAD_API_POSIX_1003_1C_OSF1_V1
#     endif
#     if !defined(RW_POSIX_D10_THREADS)
#        error Threads.h++ only supports the POSIX 1003.1c threads API under this version of OSF/dUnix
#     endif
#  elif defined(RW_THR_OS_SOLARIS)
#     if defined(RW_SOLARIS_THREADS)
#       define RW_THR_THREAD_API_SOLARIS
#       define RW_THR_THREAD_API                "Sun/Solaris Threads (Unix International Threads)"
#     elif defined(RW_POSIX_D10_THREADS)
#        define RW_THR_THREAD_API_POSIX
#        define RW_THR_THREAD_API_POSIX_1003_1C
#        define RW_THR_THREAD_API_POSIX_1003_1C_SOLARIS
#        define RW_THR_THREAD_API_POSIX_1003_1C_SUN
#        define RW_THR_THREAD_API               "Sun/Solaris IEEE POSIX 1003.1c Final"
#     elif defined(RW_DCE_THREADS)
#        define RW_THR_THREAD_API_POSIX
#        define RW_THR_THREAD_API_DCE
#        define RW_THR_THREAD_API_DCE_TRANSARC 
#        define RW_THR_THREAD_API               "Transarc DCE (IEEE POSIX 1003.4a Draft 4)"
#        define RW_DCE_PTHREAD_H <dce/pthread.h>
#     else
#        error A valid thread API was not selected! (Solaris, POSIX, or DCE)
#     endif
#  elif defined(RW_THR_OS_WIN32)
#     if defined(RW_DCE_THREADS)
#        define RW_THR_THREAD_API_POSIX
#        define RW_THR_THREAD_API_DCE
#        define RW_THR_THREAD_API_DCE_GRADIENT 
#        define RW_THR_THREAD_API               "Gradient PC-DCE/32 (IEEE POSIX 1003.4a Draft 4)"
#     else
#        define RW_THR_THREAD_API_WIN32            
#        if defined(RW_THR_COMPILER_BORLAND_CPP)
#           define RW_THR_THREAD_API_WIN32_BORLAND_CPP   
#           define RW_THR_THREAD_API               "Microsoft Win32 & Borland RTL"
#        elif defined(RW_THR_COMPILER_MICROSOFT_CPP)
#           define RW_THR_THREAD_API_WIN32_MICROSOFT_CPP 
#           define RW_THR_THREAD_API               "Microsoft Win32 & Microsoft RTL"
#        else
#           error A valid thread API was not selected! (unrecognized compiler and run-time library) 
#        endif
#     endif
#  elif defined(RW_THR_OS_LINUX)
#     if defined(RW_POSIX_D10_THREADS)
#        define RW_THR_THREAD_API_POSIX
#        define RW_THR_THREAD_API_POSIX_1003_1C
#        define RW_THR_THREAD_API_POSIX_1003_1C_LINUX
#        define RW_THR_THREAD_API                  "LinuxThreads (IEEE POSIX 1003.1c Final)"
#     else
#        error A valid thread API was not selected! (POSIX)
#     endif
#  else
#     error Unrecognized Operating System - Cannot determine thread API!
#  endif

#  if !defined(RW_THR_THREAD_API)
#     error No thread API defined!
#  endif

///////////////////////////////////////////////////////////////////////////////
// Identify and define thread API characteristics
///////////////////////////////////////////////////////////////////////////////

/*

The following macros describe Threads.h++ features that can be supported 
under the current thread API:

   RW_THR_CAN_RECOVER_THREAD_LOCALS - The environment supports automatic
   recovery of thread local storage.  If not, Threads.h++ must do the
   recovery itself, either at the time of thread exit for thread started
   by RWThread instances, or at the time the thread-local storage object
   is destructed.

   RW_THR_HAS_CONCURRENCY_POLICY - The environment recognizes or supports 
   the concurrency policy attribute.  This attribute is used to control
   whether a creation of a new process-scope thread should result in the
   creation of a new underlying kernel thread (LWP) in order to provide
   a sufficient level of concurrency in environments with N to M thread
   scheduling. This macro applies to RWThreadAttribute instances only.

   RW_THR_HAS_CONTENTION_SCOPE - The environment recognizes or supports 
   the scheduling contention scope attribute.  This does not necessarily
   imply that you may change the contention scope specified by a thread
   attribute, only that you may query it.  Contention scope is fixed in 
   many environments. This macro applies to RWThreadAttribute instances 
   only.
   
   RW_THR_HAS_DUAL_PRIORITY - The environment requires two priorities for 
   threads with system contention scope;  one priority value for use in 
   resolving system-level scheduling conflicts, and a second priority value 
   for use in resolving contention for synchronization resources that are 
   shared between threads in a process.  This macro is provided for
   informational purposes; it implies that both the proces-scope and
   system-scope priority attributes are simultaneously accessible for
   when the contention scope is RW_THR_SYSTEM_SCOPE.  This macro applies to 
   RWThreadAttribute instances and active instances of the RWThread class 
   and its subclasses.

   RW_THR_HAS_INHERITANCE_POLICY - The environment supports the
   scheduling attribute inheritance attribute.  This does not necessarily
   imply that you may change the inheritance policy, only that you can
   query it.  Inheritance policy may be fixed in some environments, but
   is available in all currently supported environments. This macro applies 
   to RWThreadAttribute instances only.

   RW_THR_HAS_MAX_THREADS - The environment defines an upper limit on 
   the number of threads that may be created.  If this macro is not 
   defined, the number of threads is generally limited by system
   resources.

   RW_THR_HAS_PARTIAL_STACK_COMMITMENT - The environment allows the user
   to partially commit the memory reserved for the stack to physical 
   memory at the time of thread creation.  If this macro is not defined, 
   and the macro RW_THR_HAS_STACK_COMMIT_SIZE is defined, then the commit 
   size also defines the amount of memory to reserve for the stack, so 
   the entire amount must be committed at thread creation.

   RW_THR_HAS_PRIORITY - The environment supports priority scheduling and
   the priority value of a thread attribute or active thread may be set or 
   queried.  This macro applies to RWThreadAttribute instances and active 
   instances of the RWThread class and its subclasses.

   RW_THR_HAS_PROCESS_SCOPE_PRIORITY - The environment supports priority 
   scheduling and defines a process-scope priority attribute.  This
   attribute may be set or queried depending on the current contention
   scope and whether or not the environment supports dual priorities.
   This macro applies to RWThreadAttribute instances and active instances 
   of the RWThread class and its subclasses.

   RW_THR_CAN_GET_SCHEDULING_POLICY - The environment supports the
   scheduling policy attribute.  This does not necessarily imply that you 
   may change the scheduling policy, only that you can query it.  Scheduling
   policy may be fixed under some contention scopes and in some environments.
   This macro applies to RWThreadAttribute instances and active instances 
   of the RWThread class and its subclasses.

   RW_THR_HAS_STACK_COMMIT_SIZE - The environment allows the user to
   dictate the amount of the memory reserved for a thread stack that is to
   be committed or mapped to physical memory when a thread is created.  
   This allows the user to pre-commit physical memory and pagefile space
   prior to thread execution.  This macro applies to RWThreadAttribute 
   instances only.

   RW_THR_HAS_STACK_RESERVE_SIZE - The environment allows the user to
   dictate the amount of memory to allocate for a thread stack when a 
   thread is created.  This memory is generally mapped, or committed 
   on-demand as the stack grows. This macro applies to RWThreadAttribute 
   instances only.

   RW_THR_HAS_START_POLICY - The environment supports the start policy
   attribute.  The start policy dictates whether a new thread is to be 
   left interrupted at creation or allowed to run immediately.  This
   attribute is currently available in all environments. This macro 
   applies to RWThreadAttribute instances only.

   RW_THR_HAS_SUSPEND_RESUME - The environment allows the user to 
   suspend and resume an active thread running inside an instance of the 
   RWThread class and its subclasses. 

   RW_THR_HAS_SYSTEM_SCOPE_PRIORITY - The environment supports priority 
   scheduling and defines a system-scope priority attribute.  This
   attribute may be set or queried depending on the current contention
   scope.  This macro applies to RWThreadAttribute instances and active 
   instances of the RWThread class and its subclasses.

   RW_THR_HAS_TIME_SLICE_QUANTUM - The environment supports the time-slice 
   quantum attribute under certain circumstances.  This attribute is used
   to control the maximum execution time for threads executing under a
   time-sliced scheduling policy.  This attribite is only supported under
   certain content-scope and scheduling policy combinations, and is 
   supported by few environments.  This macro applies to RWThreadAttribute 
   instances and active instances of the RWThread class and its subclasses.

   RW_THR_HAS_TIMED_MUTEX_ACQUIRE - The environment provides support for 
   the timed acquisition of a mutex, otherwise a timed acquisition is 
   emulated by returning TIMED_OUT if the mutex cannot be acquired 
   immediately.

   RW_THR_HAS_USER_STACK - The environment allows the user to define and
   manager their own stack memory.  If this macro is defined, the user may
   supply their own memory for the thread stack by specifying values for 
   the starting address and size attributes.  This macro applies to 
   RWThreadAttribute instances only.


*/

////////////////////////////////////////////////////////////////////////
//    OS/2 Dos Control Program Thread API Characteristics: 
//       Currently Supported Environments:
//          OS/2 Warp 3.0+ w/IBM xlC compiler
////////////////////////////////////////////////////////////////////////
#  if defined(RW_THR_THREAD_API_OS2) 

#     undef RW_THR_CAN_RECOVER_THREAD_LOCALS
#     undef RW_THR_HAS_CONCURRENCY_POLICY
#     define RW_THR_HAS_CONTENTION_SCOPE
#     undef RW_THR_HAS_DUAL_PRIORITY
#     define RW_THR_HAS_INHERITANCE_POLICY
#     define RW_THR_HAS_MAX_THREADS
#     undef RW_THR_HAS_PARTIAL_STACK_COMMITMENT
#     define RW_THR_HAS_PRIORITY
#     undef RW_THR_HAS_PROCESS_SCOPE_PRIORITY
#     define RW_THR_HAS_SCHEDULING_POLICY
#     define RW_THR_HAS_STACK_COMMIT_SIZE
#     define RW_THR_HAS_STACK_RESERVE_SIZE
#     define RW_THR_HAS_START_POLICY
#     define RW_THR_HAS_SUSPEND_RESUME
#     define RW_THR_HAS_SYSTEM_SCOPE_PRIORITY
#     undef RW_THR_HAS_TIME_SLICE_QUANTUM
#     define RW_THR_HAS_TIMED_MUTEX_ACQUIRE
#     undef RW_THR_HAS_USER_STACK

//    OS/2 Constants 
#     define RW_THR_STACK_SIZE_GRANULARITY_OS2      4096

////////////////////////////////////////////////////////////////////////
//    DCE Threads POSIX 1003.4a (Draft 4) Thread API Characteristics 
//       Currently Supported Environments:
//          AIX v4.x (IBM DCE) 
//          DEC v3.x OSF1 (OSF/1 DCE)
//          HP-UX v10.x (HP DCE) 
//          HP-UX v11.x (HP DCE) 
//          Solaris v2.5x (Transarc DCE)
////////////////////////////////////////////////////////////////////////
#  elif defined(RW_THR_THREAD_API_DCE) 

#     if !defined(__RW_PTHREAD_H__)
#        if defined(RW_DCE_PTHREAD_H)
#          define __RW_PTHREAD_H__ RW_DCE_PTHREAD_H
#        else
#          define __RW_PTHREAD_H__ <pthread.h>
#        endif
#        include __RW_PTHREAD_H__
#     endif

#     define RW_THR_HAS_POSIX_TIME
#     define RW_THR_CAN_RECOVER_THREAD_LOCALS
#     undef RW_THR_HAS_CONCURRENCY_POLICY
#     define RW_THR_HAS_CONTENTION_SCOPE
#     undef RW_THR_HAS_DUAL_PRIORITY
#     define RW_THR_HAS_INHERITANCE_POLICY
#     if !defined(RW_THR_THREAD_API_DCE_TRANSARC)
#        define RW_THR_HAS_MAX_THREADS
#     endif
#     undef RW_THR_HAS_PARTIAL_STACK_COMMITMENT
#     define RW_THR_HAS_PRIORITY
#     define RW_THR_HAS_PROCESS_SCOPE_PRIORITY
#     define RW_THR_HAS_SCHEDULING_POLICY
#     undef RW_THR_HAS_STACK_COMMIT_SIZE
#     define RW_THR_HAS_STACK_RESERVE_SIZE
#     define RW_THR_HAS_START_POLICY
#     undef RW_THR_HAS_SUSPEND_RESUME
#     undef RW_THR_HAS_SYSTEM_SCOPE_PRIORITY
#     undef RW_THR_HAS_TIME_SLICE_QUANTUM
#     undef RW_THR_HAS_TIMED_MUTEX_ACQUIRE
#     undef RW_THR_HAS_USER_STACK

////////////////////////////////////////////////////////////////////////
//    Pthreads POSIX 1003.1c (Draft 7,9,10) Thread API Characteristics 
//       Currently Supported Environments:
//          AIX  v4.2   (POSIX 1003.1c Draft 7)
//          AIX  v4.3   (POSIX 1003.1c - Final) (mid-1998)
//          SGI  v6.x   (POSIX 1003.1c Draft 7)
//          DEC  v4.0   (POSIX 1003.1c - June 1995)
//          HPUX v11.x  (POSIX 1003.1c - Final)
//          Linux v2.2.5 (POSIX 1003.1c - Final)
////////////////////////////////////////////////////////////////////////
#  elif defined(RW_THR_THREAD_API_POSIX_1003_1C) 

#     if !defined(__RW_UNISTD_H__)
#        define __RW_UNISTD_H__ <unistd.h>
#        include __RW_UNISTD_H__
#     endif

#     if !defined(__RW_PTHREAD_H__)
#        define __RW_PTHREAD_H__ <pthread.h>
#        include __RW_PTHREAD_H__
#     endif

#     define RW_THR_HAS_POSIX_TIME
#     define RW_THR_CAN_RECOVER_THREAD_LOCALS
#     undef RW_THR_HAS_CONCURRENCY_POLICY
#     if defined(_POSIX_THREAD_PRIORITY_SCHEDULING) 
#        define RW_THR_HAS_CONTENTION_SCOPE
#     else
#        undef RW_THR_HAS_CONTENTION_SCOPE
#     endif
#     if defined(RW_THR_THREAD_API_POSIX_1003_1C_SOLARIS)
#        define RW_THR_HAS_DUAL_PRIORITY
#     else
#        undef RW_THR_HAS_DUAL_PRIORITY
#     endif
#     if defined(_POSIX_THREAD_PRIORITY_SCHEDULING) 
#        define RW_THR_HAS_INHERITANCE_POLICY
#     else
#        undef RW_THR_HAS_INHERITANCE_POLICY
#     endif
#     define RW_THR_HAS_MAX_THREADS
#     undef RW_THR_HAS_PARTIAL_STACK_COMMITMENT
#     if defined(_POSIX_THREAD_PRIORITY_SCHEDULING) 
#        define RW_THR_HAS_PRIORITY
#        if defined(RW_THR_THREAD_API_POSIX_1003_1C_LINUX)
#           undef RW_THR_HAS_PROCESS_SCOPE_PRIORITY
#        else
#           define RW_THR_HAS_PROCESS_SCOPE_PRIORITY
#        endif
#        define RW_THR_HAS_SCHEDULING_POLICY
#     else
#        undef RW_THR_HAS_PRIORITY
#        undef RW_THR_HAS_PROCESS_SCOPE_PRIORITY
#        undef RW_THR_HAS_SCHEDULING_POLICY
#     endif
#     undef RW_THR_HAS_STACK_COMMIT_SIZE
#     if defined(_POSIX_THREAD_ATTR_STACKSIZE)
#        define RW_THR_HAS_STACK_RESERVE_SIZE
#     else
#        undef RW_THR_HAS_STACK_RESERVE_SIZE
#     endif
#     define RW_THR_HAS_START_POLICY
#     undef RW_THR_HAS_SUSPEND_RESUME
#     if defined(_POSIX_THREAD_PRIORITY_SCHEDULING) 
#        define RW_THR_HAS_SYSTEM_SCOPE_PRIORITY
#     else
#        undef RW_THR_HAS_SYSTEM_SCOPE_PRIORITY
#     endif
#     if defined(RW_THR_THREAD_API_POSIX_1003_1C_SOLARIS)
#        define RW_THR_HAS_TIME_SLICE_QUANTUM
#     else
#        undef RW_THR_HAS_TIME_SLICE_QUANTUM
#     endif
#     undef RW_THR_HAS_TIMED_MUTEX_ACQUIRE
#     if defined(_POSIX_THREAD_ATTR_STACKADDR) && !defined(RW_THR_THREAD_API_POSIX_1003_1C_LINUX)
#        define RW_THR_HAS_USER_STACK
#     else
#        undef RW_THR_HAS_USER_STACK
#     endif

////////////////////////////////////////////////////////////////////////
//    Solaris (Unix International) Thread API Characteristics 
//       Currently Supported Environments:
//          SunOS v5.4 (Solaris v2.4)
//          SunOS v5.5 (Solaris v2.5)
//          SunOS v5.5.1 (Solaris v2.5.1)
//          SunOS v5.6 (Solaris v2.6)
////////////////////////////////////////////////////////////////////////
#  elif defined(RW_THR_THREAD_API_SOLARIS) 

#     define RW_THR_CAN_RECOVER_THREAD_LOCALS
#     define RW_THR_HAS_CONCURRENCY_POLICY
#     define RW_THR_HAS_CONTENTION_SCOPE
#     define RW_THR_HAS_DUAL_PRIORITY
#     define RW_THR_HAS_INHERITANCE_POLICY
#     undef RW_THR_HAS_MAX_THREADS
#     undef RW_THR_HAS_PARTIAL_STACK_COMMITMENT
#     define RW_THR_HAS_PRIORITY
#     define RW_THR_HAS_PROCESS_SCOPE_PRIORITY
#     define RW_THR_HAS_SCHEDULING_POLICY
#     undef RW_THR_HAS_STACK_COMMIT_SIZE
#     define RW_THR_HAS_STACK_RESERVE_SIZE
#     define RW_THR_HAS_START_POLICY
#     define RW_THR_HAS_SUSPEND_RESUME
#     define RW_THR_HAS_SYSTEM_SCOPE_PRIORITY
#     define RW_THR_HAS_TIME_SLICE_QUANTUM
#     undef RW_THR_HAS_TIMED_MUTEX_ACQUIRE
#     define RW_THR_HAS_USER_STACK

////////////////////////////////////////////////////////////////////////
//    Win32 Thread API Characteristics 
//       Currently Supported Environments:
//          Windows NT v3.5x and v4.0 
//          Windows 95 
////////////////////////////////////////////////////////////////////////
#  elif defined(RW_THR_THREAD_API_WIN32) 

#     undef RW_THR_CAN_RECOVER_THREAD_LOCALS
#     undef RW_THR_HAS_CONCURRENCY_POLICY
#     define RW_THR_HAS_CONTENTION_SCOPE
#     undef RW_THR_HAS_DUAL_PRIORITY
#     define RW_THR_HAS_INHERITANCE_POLICY
#     undef RW_THR_HAS_MAX_THREADS
#     define RW_THR_HAS_PARTIAL_STACK_COMMITMENT
#     define RW_THR_HAS_PRIORITY
#     undef RW_THR_HAS_PROCESS_SCOPE_PRIORITY
#     define RW_THR_HAS_SCHEDULING_POLICY
#     define RW_THR_HAS_STACK_COMMIT_SIZE
#     undef RW_THR_HAS_STACK_RESERVE_SIZE
#     define RW_THR_HAS_START_POLICY
#     define RW_THR_HAS_SUSPEND_RESUME
#     define RW_THR_HAS_SYSTEM_SCOPE_PRIORITY
#     undef RW_THR_HAS_TIME_SLICE_QUANTUM
#     define RW_THR_HAS_TIMED_MUTEX_ACQUIRE
#     undef RW_THR_HAS_USER_STACK
#  endif

////////////////////////////////////////////////////////////////////////
//
//    Define Threads.h++ Product Attributes
//
////////////////////////////////////////////////////////////////////////

#  define RW_THR_PRODUCT_NAME                   "Threads.h++"
#  define RW_THR_PRODUCT_VENDOR                 "Rogue Wave Software, Inc."
#  define RW_THR_PRODUCT_COPYRIGHT              "Copyright 1999, Rogue Wave Software, Inc., All rights reserved."
#  define RW_THR_PRODUCT_TYPE                   "C++ Library"
#  define RW_THR_PRODUCT_MAJOR_VERSION          "1"
#  define RW_THR_PRODUCT_MINOR_VERSION          "4"
#  define RW_THR_PRODUCT_MAINTENANCE_VERSION    "0"
#  define RW_THR_PRODUCT_VERSION_LABEL          "Undefined"
#  define RW_THR_PRODUCT_RELEASE_TYPE           RW_THR_PRODUCT_CONFIGURATION
#  define RW_THR_PRODUCT_THREAD_SAFETY          "MT-Safe"
#  define RW_THR_PRODUCT_ADDITIONAL_INFO        ""
#  define RW_THR_PRODUCT_VARIANT_DESCRIPTOR     ""

//////////////////////////////////////////////////////////////////////////////
//
// Define dependent product binding and build configurations
//
//////////////////////////////////////////////////////////////////////////////

// Tools.h++ Binding
#  if defined(_RWTOOLSDLL)
#     define RW_THR_TLS_DLL
#  endif

// Standard Library Binding
#  if !defined(RW_NO_STL) && defined(_RWSTDDLL)
#     define RW_THR_STD_DLL
#  endif

// C Run-time Library Binding
#  if (defined(_MSC_VER) && defined(_DLL)) || \
      (defined(__BORLANDC__) && defined(_RTLDLL))
#     define RW_THR_RTL_DLL
#  endif

// Tools.h++ Configuration
#  if defined(RWDEBUG)
#     define RW_THR_TLS_DEBUG
#  endif

// Standard Library Configuration
#  if defined(RWDEBUG)
#     define RW_THR_STD_DEBUG
#  endif

// Does the C Run-time Library Configuration
#  if (defined(_MSC_VER) && defined(_DEBUG))
#     define RW_THR_RTL_DEBUG
#  endif

//////////////////////////////////////////////////////////////////////////////
//
// Define Dynamic-Link Library Import/Export Linkage Macros
//
//////////////////////////////////////////////////////////////////////////////

//    Does the current platform allow dynamic linking?

#  if !defined(RW_THR_ALLOWS_DYNAMIC_BINDING) && defined(RW_THR_DYNAMIC_BINDING)
#     error Dynamic binding may only be specified on platforms that support shared or dynamic-link libraries!
#     undef RW_THR_DYNAMIC_BINDING
#     define RW_THR_STATIC_BINDING
#  endif

// OS/2 DLL Linkage

#  if defined(RW_THR_OS_NAME_OS2) && \
      defined(RW_THR_DYNAMIC_BINDING)

#  endif

// Windows DLL Linkage

#  if defined(RW_THR_OS_NAME_WIN32) && \
      defined(RW_THR_DYNAMIC_BINDING)
 
/*****************************************************************************

Windows NT/95 WIN32 Linkage Definitions
=======================================

Under Windows, library header files can be used in two ways:

   1. When building the library.
   2. When using the library to build another product.

For each of these uses, the header files must account for two variants
under MS-Windows:

   1.  Building or using a static version of the library.
   2.  Building or using a dynamic-link (DLL) version of the library.

Building A Static Version Of Threads.h++
----------------------------------------

The macro RW_THR_BUILD must be defined.

Building A DLL Version Of Threads.h++
-------------------------------------

The macro RW_THR_BUILD and RW_THR_DYNAMIC_BINDING must be defined to 
create a DLL version of the library.

This macro must be defined by the user, but its existence must match
the target implied (static or DLL) by switches supplied to the compiler.

Building A Threads.h++ DLL Under Borland
----------------------------------------

When using the Borland compilers, one of the following switches must be 
specified to build the library as a DLL:
   
   -WCD  Console-mode DLL with all functions exported
   -WCDE Console-mode DLL with explicit functions exported
   -WD   GUI-mode DLL with all functions exported
   -WDE  GUI-mode DLL with explicit functions exported

When any of these switches are supplied, the macro __DLL__ is 
automatically defined.  The existence of this macro is compared with 
the existence of the _RWTHRBUILDDLL macro to test the validity of the 
build variant.

The library must be linked with startup code object, COD32.OBJ
 
Building A Threads.h++ DLL Under Microsoft
------------------------------------------

When using the Microsoft compiler, the /LD option must be supplied to
the compiler to build the library as a DLL.  Microsoft does not define
any macros to differentiate between a static and dynamic build.  The
/LD option

-  Passes the /DLL option to the linker. The linker looks for, 
   but does not require, a DllMain function. If you do not write 
   a DllMain function, the linker inserts a DllMain function that 
   returns TRUE.
-  Links the DLL startup code.
-  Creates an import library (.LIB), if an export (.EXP) file is 
   not specified on the command line; you link the import library 
   to applications that call your DLL.
-  Interprets /Fe as naming a DLL rather than an .EXE file; the 
   default program name becomes basename.DLL instead of basename.EXE.  
-  Changes default run-time library support to /MT if you have not 
   explicitly specified /MD, /ML, or /MT.  

Using A Static Version Of Threads.h++
-------------------------------------

Nothing special needs to be done, as RW_THR_USE and RW_THR_STATIC_BINDING
are the defaults.

Using A DLL Version Of Threads.h++
----------------------------------

In order to use a DLL-version of the Threads.h++ library, you must 
define the macro RW_THR_DYNAMIC_BINDING (and optionally define RW_THR_USE)
when compiling your source files.  This macros will indicate to the 
Threads.h++ header files that you intend to use a DLL version of the 
Threads.h++ library.
 
*****************************************************************************/

// Define the DLL interface linkage declarators

#     if defined(RW_THR_COMPILER_NAME_MICROSOFT_CPP)
         // Microsoft linkage specifications
#        define RWTHRDLLImport __declspec(dllimport)
#        define RWTHRDLLExport __declspec(dllexport)
#     else
         // Borland linkage specifications
#        define RWTHRDLLImport _import
#        define RWTHRDLLExport _export
#     endif

#     if defined(RW_THR_USE)

         // Using the Threads.h++ DLL.

         // Import classes and functions
#        define RWTHRExport   RWTHRDLLImport
#        define rwthrexport   RWTHRDLLImport

         // Do not import template class and function declarations
#        define RWTHRTExport
#        define rwthrtexport

         // Import explicit template class and function instantiations
#        define RWTHRIExport RWTHRDLLImport
#        define rwthriexport RWTHRDLLImport

#     else 

         // Compiling the Threads.h++ DLL.

         // Export classes and functions
#        define RWTHRExport   RWTHRDLLExport
#        define rwthrexport   RWTHRDLLExport 

         // Do not export template class and function declarations
#        define RWTHRTExport
#        define rwthrtexport

         // Export explicit template class and function instantiations
#        define RWTHRIExport RWTHRDLLExport
#        define rwthriexport RWTHRDLLExport

#     endif

#  else

      // Disable DLL linkage declarators if we are not compiling for WIN32

#     define RWTHRExport
#     define RWTHRTExport
#     define rwthrexport
#     define rwthrtexport
#     define RWTHRIExport
#     define rwthriexport

#  endif // RW_THR_WIN32


//////////////////////////////////////////////////////////////////////////////
//
//    General Purpose Macros
//
//////////////////////////////////////////////////////////////////////////////

/*****************************************************************************

Const Cast macros:

   The following macro is used to perform const casts of simple
   non-templatized types.  These macros are typically used for
   casting-away-const in const member functions. (Only where it
   makes sense, of course.)

      RW_THR_CONST_CAST

   The following macros are used to perform const casts of types
   which contain "," characters, such as template types. These
   special macros are necessary because the pre-processor uses
   "," to delineate arguments in a macro invocation.

      RW_THR_CONST_CAST_C1
      RW_THR_CONST_CAST_C2
      RW_THR_CONST_CAST_C3
      RW_THR_CONST_CAST_C4
      RW_THR_CONST_CAST_C5
      RW_THR_CONST_CAST_C6
      RW_THR_CONST_CAST_C7
   
   The macros are named RW_THR_CONST_CAST_Cn, where n is the number
   of commas in the type name. Note that this will also work for
   template types which are instantiated with multiple types
   which are also templates which are instantiated with multiple
   types; you just select the macro that corresponds to the number
   of commas in the type name.

   The implementation of the const cast macros depends on whether the
   C++ standard const_cast<> macro is supported by the current compiler
   or not. If const_cast<> isn't available then a C-style cast is
   performed.

*****************************************************************************/

// RW_THR_CONST_CAST
#  if !defined(RW_THR_NO_CONST_CAST_OPERATOR)
#     define RW_THR_CONST_CAST(t, instance) (const_cast<t>(instance))
#  else
#     define RW_THR_CONST_CAST(t, instance) ((t)(instance))     
#  endif

// RW_THR_CONST_CAST_C1
#  if !defined(RW_THR_NO_CONST_CAST_OPERATOR)
#     define RW_THR_CONST_CAST_C1(t_part1, t_part2, instance) \
         (const_cast<t_part1,t_part2>(instance))
#  else
#     define RW_THR_CONST_CAST_C1(t_part1, t_part2, instance) \
         ((t_part1,t_part2)(instance))     
#  endif

// RW_THR_CONST_CAST_C2     
#  if !defined(RW_THR_NO_CONST_CAST_OPERATOR)
#     define RW_THR_CONST_CAST_C2(t_part1, t_part2, t_part3, instance) \
         (const_cast<t_part1,t_part2,t_part3>(instance))
#  else
#     define RW_THR_CONST_CAST_C2(t_part1, t_part2, t_part3, instance) \
         ((t_part1,t_part2,t_part3)(instance))     
#  endif

// RW_THR_CONST_CAST_C3     
#  if !defined(RW_THR_NO_CONST_CAST_OPERATOR)
#     define RW_THR_CONST_CAST_C3(t_part1, t_part2, t_part3, t_part4, instance) \
         (const_cast<t_part1,t_part2,t_part3,t_part4>(instance))
#  else
#     define RW_THR_CONST_CAST_C3(t_part1, t_part2, t_part3, t_part4, instance) \
         ((t_part1,t_part2,t_part3,t_part4)(instance))     
#  endif

// RW_THR_CONST_CAST_C4     
#  if !defined(RW_THR_NO_CONST_CAST_OPERATOR)
#     define RW_THR_CONST_CAST_C4(t_part1, t_part2, t_part3, t_part4, t_part5, instance) \
         (const_cast<t_part1,t_part2,t_part3,t_part4,t_part5>(instance))
#  else
#     define RW_THR_CONST_CAST_C4(t_part1, t_part2, t_part3, t_part4, t_part5, instance) \
         ((t_part1,t_part2,t_part3,t_part4,t_part5)(instance))     
#  endif

// RW_THR_CONST_CAST_C5     
#  if !defined(RW_THR_NO_CONST_CAST_OPERATOR)
#     define RW_THR_CONST_CAST_C5(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, instance) \
         (const_cast<t_part1,t_part2,t_part3,t_part4,t_part5,t_part6>(instance))
#  else
#     define RW_THR_CONST_CAST_C5(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, instance) \
         ((t_part1,t_part2,t_part3,t_part4,t_part5,t_part6)(instance))  
#  endif

// RW_THR_CONST_CAST_C6     
#  if !defined(RW_THR_NO_CONST_CAST_OPERATOR)
#     define RW_THR_CONST_CAST_C6(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, t_part7, instance) \
         (const_cast<t_part1,t_part2,t_part3,t_part4,t_part5,t_part6,t_part7>(instance))
#  else
#     define RW_THR_CONST_CAST_C6(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, t_part7, instance) \
         ((t_part1,t_part2,t_part3,t_part4,t_part5,t_part6,t_part7)(instance)) 
#  endif     

// RW_THR_CONST_CAST_C7
#  if !defined(RW_THR_NO_CONST_CAST_OPERATOR)
#     define RW_THR_CONST_CAST_C7(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, t_part7, t_part8, instance) \
         (const_cast<t_part1,t_part2,t_part3,t_part4,t_part5,t_part6,t_part7,t_part8>(instance))
#  else
#     define RW_THR_CONST_CAST_C7(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, t_part7, t_part8, instance) \
         ((t_part1,t_part2,t_part3,t_part4,t_part5,t_part6,t_part7,t_part8)(instance)) 
#  endif     

/*****************************************************************************

Reinterpret Cast macros:

   The following macro is used to perform reinterpret casts of simple
   non-templatized types.  These macros are used as naive replacements for
   the old C-style casts. 

      RW_THR_REINTERPRET_CAST

   The following macros are used to perform reinterpret casts of types
   which contain "," characters, such as template types. These
   special macros are necessary because the pre-processor uses
   "," to delineate arguments in a macro invocation.

      RW_THR_REINTERPRET_CAST_C1
      RW_THR_REINTERPRET_CAST_C2
      RW_THR_REINTERPRET_CAST_C3
      RW_THR_REINTERPRET_CAST_C4
      RW_THR_REINTERPRET_CAST_C5
      RW_THR_REINTERPRET_CAST_C6
      RW_THR_REINTERPRET_CAST_C7
   
   The macros are named RW_THR_REINTERPRET_CAST_Cn, where n is the number
   of commas in the type name. Note that this will also work for
   template types which are instantiated with multiple types
   which are also templates which are instantiated with multiple
   types; you just select the macro that corresponds to the number
   of commas in the type name.

   The implementation of the reinterpret cast macros depends on whether the
   C++ standard reinterpret_cast<> macro is supported by the current compiler
   or not. If reinterpret_cast<> isn't available then a C-style cast is
   performed.

*****************************************************************************/

// RW_THR_REINTERPRET_CAST
#  if !defined(RW_THR_NO_REINTERPRET_CAST_OPERATOR)
#     define RW_THR_REINTERPRET_CAST(t, instance) (reinterpret_cast<t>(instance))
#  else
#     define RW_THR_REINTERPRET_CAST(t, instance) ((t)(instance))     
#  endif

// RW_THR_REINTERPRET_CAST_C1
#  if !defined(RW_THR_NO_REINTERPRET_CAST_OPERATOR)
#     define RW_THR_REINTERPRET_CAST_C1(t_part1, t_part2, instance) \
         (reinterpret_cast<t_part1,t_part2>(instance))
#  else
#     define RW_THR_REINTERPRET_CAST_C1(t_part1, t_part2, instance) \
         ((t_part1,t_part2)(instance))     
#  endif

// RW_THR_REINTERPRET_CAST_C2     
#  if !defined(RW_THR_NO_REINTERPRET_CAST_OPERATOR)
#     define RW_THR_REINTERPRET_CAST_C2(t_part1, t_part2, t_part3, instance) \
         (reinterpret_cast<t_part1,t_part2,t_part3>(instance))
#  else
#     define RW_THR_REINTERPRET_CAST_C2(t_part1, t_part2, t_part3, instance) \
         ((t_part1,t_part2,t_part3)(instance))     
#  endif

// RW_THR_REINTERPRET_CAST_C3     
#  if !defined(RW_THR_NO_REINTERPRET_CAST_OPERATOR)
#     define RW_THR_REINTERPRET_CAST_C3(t_part1, t_part2, t_part3, t_part4, instance) \
         (reinterpret_cast<t_part1,t_part2,t_part3,t_part4>(instance))
#  else
#     define RW_THR_REINTERPRET_CAST_C3(t_part1, t_part2, t_part3, t_part4, instance) \
         ((t_part1,t_part2,t_part3,t_part4)(instance))     
#  endif

// RW_THR_REINTERPRET_CAST_C4     
#  if !defined(RW_THR_NO_REINTERPRET_CAST_OPERATOR)
#     define RW_THR_REINTERPRET_CAST_C4(t_part1, t_part2, t_part3, t_part4, t_part5, instance) \
         (reinterpret_cast<t_part1,t_part2,t_part3,t_part4,t_part5>(instance))
#  else
#     define RW_THR_REINTERPRET_CAST_C4(t_part1, t_part2, t_part3, t_part4, t_part5, instance) \
         ((t_part1,t_part2,t_part3,t_part4,t_part5)(instance))     
#  endif

// RW_THR_REINTERPRET_CAST_C5     
#  if !defined(RW_THR_NO_REINTERPRET_CAST_OPERATOR)
#     define RW_THR_REINTERPRET_CAST_C5(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, instance) \
         (reinterpret_cast<t_part1,t_part2,t_part3,t_part4,t_part5,t_part6>(instance))
#  else
#     define RW_THR_REINTERPRET_CAST_C5(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, instance) \
         ((t_part1,t_part2,t_part3,t_part4,t_part5,t_part6)(instance))  
#  endif

// RW_THR_REINTERPRET_CAST_C6     
#  if !defined(RW_THR_NO_REINTERPRET_CAST_OPERATOR)
#     define RW_THR_REINTERPRET_CAST_C6(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, t_part7, instance) \
         (reinterpret_cast<t_part1,t_part2,t_part3,t_part4,t_part5,t_part6,t_part7>(instance))
#  else
#     define RW_THR_REINTERPRET_CAST_C6(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, t_part7, instance) \
         ((t_part1,t_part2,t_part3,t_part4,t_part5,t_part6,t_part7)(instance)) 
#  endif     

// RW_THR_REINTERPRET_CAST_C7
#  if !defined(RW_THR_NO_REINTERPRET_CAST_OPERATOR)
#     define RW_THR_REINTERPRET_CAST_C7(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, t_part7, t_part8, instance) \
         (reinterpret_cast<t_part1,t_part2,t_part3,t_part4,t_part5,t_part6,t_part7,t_part8>(instance))
#  else
#     define RW_THR_REINTERPRET_CAST_C7(t_part1, t_part2, t_part3, t_part4, t_part5, t_part6, t_part7, t_part8, instance) \
         ((t_part1,t_part2,t_part3,t_part4,t_part5,t_part6,t_part7,t_part8)(instance)) 
#  endif     

/*****************************************************************************

RWUSING Access Adjustment Macro

Some compilers support the deprecated access adjustment style, "Base::member;" 
while newer compilers require the standard method of "using Base::member;".

The RWUSING macro expands to "using" or nothing, as appropriate, and should be 
used for all access adjustment declarations.

*****************************************************************************/
#  if !defined(RW_USING)
#     if defined(RW_THR_USE_STANDARD_ACCESS_ADJUSTMENT)
#        define RWUSING using
#     else
#        define RWUSING
#     endif
#  endif

/*****************************************************************************

Exception Specification Macros

Some compilers do not allow exception specifications at all, or do not 
allow exception specifications on inlined functions, so these macros are
used to wrap exception specifications.  These macros should be applied 
to every function in the library as documentation for library users.

*****************************************************************************/

// First undefine any previous definitions of the exception specification macros

#  undef RWTHRTHROWSANY            
#  undef RWTHRTHROWSNONE
#  undef RWTHRTHROWS0
#  undef RWTHRTHROWS1
#  undef RWTHRTHROWS2
#  undef RWTHRTHROWS3
#  undef RWTHRTHROWS4
#  undef RWTHRTHROWS5

// Check to see if exception specifications 
// are allowed in the current context

#  if defined(RW_THR_DISABLE_EXCEPTION_SPECS) ||     \
      defined(RW_THR_NO_EXCEPTION_SPECS) ||          \
      defined(RW_THR_NO_INLINE_EXCEPTION_SPECS)   

// Exceptions specifications are currently disabled or not supported so
// define versions of the macros that expand to nothing during preprocessing

#     define RWTHRTHROWSANY            
#     define RWTHRTHROWSNONE
#     define RWTHRTHROWS0
#     define RWTHRTHROWS1(A)
#     define RWTHRTHROWS2(A,B)
#     define RWTHRTHROWS3(A,B,C)
#     define RWTHRTHROWS4(A,B,C,D)
#     define RWTHRTHROWS5(A,B,C,D,E)
#     define RWTHRTHROWS6(A,B,C,D,E,F)
#     define RWTHRTHROWS7(A,B,C,D,E,F,G)

#  else

// Exception specifications are currently enabled and supported so 
// define macros that expand to exception specifications during preprocessing

#     define RWTHRTHROWSANY                           // can throw any exception (for documentation purposes)
#     define RWTHRTHROWSNONE             throw()        // throws no exceptions
#     define RWTHRTHROWS0                throw()        // throws no exceptions
#     define RWTHRTHROWS1(A)             throw(A)
#     define RWTHRTHROWS2(A,B)           throw(A,B)
#     define RWTHRTHROWS3(A,B,C)         throw(A,B,C)
#     define RWTHRTHROWS4(A,B,C,D)       throw(A,B,C,D)
#     define RWTHRTHROWS5(A,B,C,D,E)     throw(A,B,C,D,E)
#     define RWTHRTHROWS6(A,B,C,D,E,F)   throw(A,B,C,D,E,F)
#     define RWTHRTHROWS7(A,B,C,D,E,F,G) throw(A,B,C,D,E,F,G)

#  endif

// Define a enumerated to use to identify static instance constructors

enum RWStaticCtor {RW_STATIC_CTOR};

// Define the status return values for calls that accept a time-out value...

enum RWWaitStatus {
   RW_THR_TIMEOUT=0,    // The operation timed-out before completion
                        // Define some interchangeable names for success...
   RW_THR_SIGNALED=1,   // The calling thread was signaled by another thread
   RW_THR_ACQUIRED=1,   // The calling thread acquired the resource
   RW_THR_COMPLETED=1,  // The operation completed
   RW_THR_ABORTED=2     // The operation was aborted
};

// Include the trace definitions for every file that includes defs.h

#  if !defined(__RWTHRTRACE_H__)
#     include <rw/thr/trace.h>
#  endif

// Thread Debug macro. If RW_THR_DEBUG is turned on then RWTHRASSERT
// expands to a throw or an assert, depending on whether RW_THR_DEBUG_THROW
// macro is defined.  The default behavior is to assert.

#  if defined(RW_THR_DEBUG)

// Debug Mode
// Expand occurances of RWTHRASSERT

#     if defined(RW_THR_DEBUG_THROW)

#        if !defined(__RWTHREXCEPT_H__)
#           include <rw/thr/except.h>
#        endif
#        if !defined(__RWTHRUTIL_H__)
#           include <rw/thr/util.h>
#        endif

//    Throw an exception if the assertion fails
#        define RWTHRASSERT(a) rwthrassert(((a) != 0),#a,__FILE__,__LINE__)

//    Define inline function for throwing the assert exception.
//    An inline function is used to hide the if statement so
//    that the RWTHRASSERT macro may be used in a block like this:
//          if (c1)
//             RWTHRASSERT(c2);
//          else {
//             ...
//          }
//    without making the else-clause following the macro illegal. 
inline
void
rwthrassert(RWBoolean condition,
            const char *conditionText,
            const char *fileName,
            int lineNumber)
   RWTHRTHROWS1(RWTHRInternalError)
{
   if (!condition)
      throw RWTHRInternalError(
            RWCString("Assertion Failed: (")+
            conditionText+
            ") != 0, file "+
            fileName+
            ", line "+
            rwToRWCString(lineNumber));
}

#     else
//    Call assert and abort the assertion fails
#        define RWTHRASSERT(a) assert((a) != 0)
#     endif

#  else

// Release Mode
// Ignore occurances of RWTHRASSERT

#     define RWTHRASSERT(a)

#  endif


// Define a default for RW_SL_STD_USING in case we are using
// an older version of Tools.h++
#  if !defined(RW_SL_STD_USING)
#    define RW_SL_STD_USING
#  endif


#endif // __RWTHRDEFS_H__

