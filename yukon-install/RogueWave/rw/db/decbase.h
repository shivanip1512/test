#ifndef __RW_DECBASE_H__
#define __RW_DECBASE_H__

/***************************************************************************
 *
 * decbase.h -- RWDecimalBase class
 *   provide a scope for the enums used in the different RWDecimal classes.
 *
 * $Id$
 *
 * Copyright (c) 1993, 1996-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 **************************************************************************/

/*************************************************************************
 * Special stuff for 16-bit Windows (__WIN16__)
 * and Windows NT / Win32s (__WIN32__).
 *
 * Under Windows, these header files can be used in two different modes:
 * as part of a DLL, or as part of a regular (static) library.
 * For each of these two ways, we can either be COMPILING the library,
 * or USING the library.  This makes a matrix of four possible situations.
 *
 *************************************************************************
 ******************************  DLL *************************************
 *************************************************************************
 *
 *                         Creating a DLL
 *
 * The macro __DLL__ should be define when compiling to create a DLL.
 *
 *     The Borland compilers automatically do this if either the -WD or
 *     -WDE switch is being used.  In this situation, the macros
 *     RWExport and rwexport expand to _export.
 *
 *     For other compilers, we must define __DLL__ where appropriate
 *     if the compiler doesn't.  See the example for Microsoft below.
 *
 *     RWExport and rwexport expand to _export by default.  Massage as
 *     necessary for your compiler; again see below for the Microsoft
 *     specific directives.
 *
 *                       Using the resultant DLL
 *
 * In order to use the resultant DLL, you must define the macro _RWCLASSDLL
 * when compiling.  This will communicate to the Rogue Wave header files
 * your intention to use a DLL version of the Rogue Wave library.
 *
 * If you intend to use a DLL version of the Borland RTL as well, then you
 * should also define the macro _CLASSDLL, as per Borland's instructions.
 *
 * You must also specify the -WS switch ("smart callbacks") for the
 * Borland Compiler.
 *
 *
 *************************************************************************
 ***********************  Static Windows Library **************************
 *************************************************************************
 *
 *
 *                Creating a RW static Windows library
 *
 * If your intention is to create a Rogue Wave library to be used as
 * as a static Windows library, then one of the macros __WIN16__ or __WIN32__
 * must have been defined in compiler.h (or by the compiler directly).
 *
 * Borland:     __WIN16__ will be defined if both _Windows and __MSDOS__
 *              is defined.  _Windows will be defined automatically if
 *              any -W switch is used.  Borland defines __WIN32__ automatically
 *              in their 32-bit compiler for Windows NT / Win32s.
 *
 * Microsoft:   Microsoft automatically defines _WINDOWS if you use the
 *              /GA (preferred) or /GW switch.  __WIN16__ will be defined
 *              if both _WINDOWS and _MSDOS are defined.
 *              __WIN32__ will only be defined if either it, or WIN32,
 *              is explicitly defined on the cl386 command line.
 *
 *
 *                 Using a RW static Windows library
 *
 * Nothing special needs to be done.  Just link compile with the appropriate
 * compile switches and link in the RW static Windows library.
 *
 */

#include <rw/db/dcmldefs.h>

// Support for new product wide using DLL macro:
#ifdef RWDLL
#  ifndef _RWDCMLDLL
#    define _RWDCMLDLL 1
#  endif
#endif


/*
 * Check for Microsoft C/C++ and massage as necessary.
 */
#if defined(_MSC_VER)
#  if defined(_WINDLL) && !defined(__DLL__)
#    define __DLL__ 1
#  endif
#  if !defined(_export)
#    define _export __export
#  endif
#endif


#if defined(_RWDCMLBUILDDLL) || defined(_RWBUILDDLL) || defined(_RWDBBUILDDLL) 
#  ifndef _RWDCMLDLL
#    define _RWDCMLDLL 1
#  endif
#  ifndef _RWTOOLSDLL
#    define _RWTOOLSDLL 1
#  endif
#  ifndef _RWDBDLL
#    define _RWDBDLL 1
#  endif
#endif

// The Money DLL related defines.
#if defined(_RWDCMLDLL) || defined(_RWTOOLSDLL) || defined(_RWDBDLL)
#  if defined(__WIN16__) && !defined(__LARGE__)
#    error   Must use large memory model when compiling or using the Money DLL!
#  endif
#  if defined(_RWDCMLBUILDDLL) || defined(_RWBUILDDLL) || defined(_RWDBBUILDDLL) 
// Compiling the Money DLL.
#    if defined (__OS2__) && defined (__BORLANDC__)
#      define RWDCMLExport _export
#      define rwdcmlexport _export
#    elif defined (__OS2__) && defined (__IBMCPP__)
#      define RWDCMLExport _Export
#      define rwdcmlexport _Export
#    elif !defined(_MSC_VER) && !defined(__SC__) && !defined(__WATCOMC__)
#      define RWDCMLExport _export  
#      define rwdcmlexport _export 
#      define RWDCMLTExport RWDCMLExport
#    else /* Microsoft: */
#      define RWDCMLExport __declspec(dllexport)
#      define rwdcmlexport __declspec(dllexport)
#      define RWDCMLTExport
#    endif
#  else // Using the Money DLL.
#    if defined(__WIN16__)
#      define RWDCMLExport _import  /* Mark classes as huge  */
#      define rwdcmlexport far      /*    & functions as far */
#    elif defined(__WIN32__)
#      if !defined(_MSC_VER) && !defined(__WATCOMC__)
#        if defined(__SC__)
#          define RWDCMLExport
#          define rwdcmlexport
#        else 
#          define RWDCMLExport _import
#          define rwdcmlexport
#        endif
#      else
#        define RWDCMLExport  __declspec(dllimport)
#        define rwdcmlexport
#      endif  
#    elif defined(__OS2__) 
#      define RWDCMLExport 
#      define rwdcmlexport 
#    endif
#  endif
#else
   // Neither compiling, nor using the Money DLL.
#  define RWDCMLExport
#  define rwdcmlexport
#endif

/*
 * If the programmer is building a DLL that uses the Money DLL,
 * code for the template and generic classes will actually find its
 * way into the programmer's DLL.  To have those classes exported,
 * define _RWDCMLEXPORT_INSTANTIATED (feature not available with Microsoft)
 */

#ifndef _RWDCMLBUILDDLL
#  ifdef _RWDCMLEXPORT_INSTANTIATED
#    if !defined(_MSC_VER) && !defined(__WATCOMC__)
#      define RWDCMLTExport _export
#    else
#      define RWDCMLTExport /* MS does not allow dllexport for templates */
#    endif
#  else
#    define RWDCMLTExport
#  endif
#endif


/*
 * RWDecimalBase class
 *
 * The basic use of this class is to provide a scope for the enums used
 * in the different RWDecimal classes.
 *
 * The member state_ would, in a pure design sense, be better included with
 * each individual RWDecimal class so that RWDecimalBase existed purely for
 * scoping.  The trouble with a class purely for scoping is that the compiler
 * will put a dummy variable in it to give it non-zero size.  Obviously, this
 * just won't do, so state_ is included since it is used in all the decimal
 * classes anyway.
 */

class RWDCMLExport RWDecimalBase {
public:
  enum RoundingMethod { PLAIN=0, UP=1, DOWN=2, BANKERS=3, TRUNCATE=2 };
  enum State          { normal = 0, nullstate = 1, NaNstate = 2, ctorError = 4,
                        missingstate = 8 };
  enum Op             { assign, add, sub, mult, div, powop, pow10op, conversion };

  /*
   * Now the one data member, used mostly just to give the class non-zero size
   */
protected:
  State state_;
};

#endif

