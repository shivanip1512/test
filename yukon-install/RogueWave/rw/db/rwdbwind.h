#ifndef __RWDB_WIND_H__
#define __RWDB_WIND_H__

/**************************************************************************
 *
 * $Id$
 *
 ***************************************************************************
 *
 * Copyright (c) 1994-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 **************************************************************************
 *
 * Special stuff for 16-bit Windows (__WIN16__)
 * and Windows NT / Win32s (__WIN32__).
 * Under Windows, these header files can be used in two different modes:
 * as part of a DLL, or as part of a regular (static) library.
 * For each of these two ways, we can either be COMPILING the library,
 * or USING the library.  This makes a matrix of four possible situations.
 *
 ******************************  DLL *************************************
 *                         Creating a DLL
 * The macro __DLL__ should be define when compiling to create a DLL.
 *     The Borland compilers automatically do this if either the -WD or
 *     -WDE switch is being used.  In this situation, the macros
 *     RWExport and rwexport expand to _export.
 *     For other compilers, we must define __DLL__ where appropriate
 *     if the compiler doesn't.  See the example for Microsoft below.
 *     RWExport and rwexport expand to _export by default.  Massage as
 *     necessary for your compiler; again see below for the Microsoft
 *     specific directives.
 *                       Using the resultant DLL
 * In order to use the resultant DLL, you must define the macro _RWCLASSDLL
 * when compiling.  This will communicate to the Rogue Wave header files
 * your intention to use a DLL version of the Rogue Wave library.
 * If you intend to use a DLL version of the Borland RTL as well, then you
 * should also define the macro _CLASSDLL, as per Borland's instructions.
 * You must also specify the -WS switch ("smart callbacks") for the
 * Borland Compiler.
 ***********************  Static Windows Library **************************
 *                Creating a RW static Windows library
 * If your intention is to create a Rogue Wave library to be used as
 * as a static Windows library, then one of the macros __WIN16__ or __WIN32__
 * must have been defined in compiler.h (or by the compiler directly).
 * Borland:     __WIN16__ will be defined if both _Windows and __MSDOS__
 *              is defined.  _Windows will be defined automatically if
 *              any -W switch is used.  Borland defines __WIN32__ automatically
 *              in their 32-bit compiler for Windows NT / Win32s.
 * Microsoft:   Microsoft automatically defines _WINDOWS if you use the
 *              /GA (preferred) or /GW switch.  __WIN16__ will be defined
 *              if both _WINDOWS and _MSDOS are defined.
 *              __WIN32__ will only be defined if either it, or WIN32,
 *              is explicitly defined on the cl386 command line.
 *                 Using a RW static Windows library
 * Nothing special needs to be done.  Just link compile with the appropriate
 * compile switches and link in the RW static Windows library.
 *
 **************************************************************************/


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


/*
 * Check for DLL builds / usage
 */
#if defined(RWDLL)
     // Compiling the DBtools.h++ Core DLL.
#  if defined(_RWDBBUILDDLL)
#    if !defined(_MSC_VER)
#      define RWDBExport _export  /* Mark classes as exported */
#      define rwdbexport _export  /*    & functions as well   */
#    else    /* Microsoft */
#      define RWDBExport __declspec(dllexport)
#      define rwdbexport __declspec(dllexport)
#    endif
#  else
     // Using the DBtools.h++ DLL.
#    if defined(__WIN32__)
#      if !defined(_MSC_VER)
#        define RWDBExport   _import
#        define rwdbexport   _import
#      else  /* Microsoft */
#        define RWDBExport  __declspec(dllimport)
#        define rwdbexport
#      endif
#    elif   defined(__OS2__) && defined (__BORLANDC__)
#      define RWDBExport
#      define rwdbexport
#    endif
#  endif

     // Compiling the DBtools.h++ Access library DLL.
#  if defined(_RWDBBUILDACCESSDLL)
#    if !defined(_MSC_VER)
#      define RWDBAccessExport _export  /* Mark classes as exported */
#      define rwdbaccessexport _export  /*    & functions as well   */
#    else    /* Microsoft */
#      define RWDBAccessExport __declspec(dllexport)
#      define rwdbaccessexport __declspec(dllexport)
#    endif
#  else
     // Using the DBtools.h++ Access library DLL.
#    if defined(__WIN32__)
#      if !defined(_MSC_VER)
#        define RWDBAccessExport   _import
#        define rwdbaccessexport
#      else  /* Microsoft */
#        define RWDBAccessExport  __declspec(dllimport)
#        define rwdbaccessexport
#      endif
#    endif
#  endif

#else
   // Neither compiling, nor using the DLLs
#  define RWDBExport
#  define rwdbexport
#  define RWDBAccessExport
#  define rwdbaccessexport
#endif

/*
 * Disable near/far pointers and pascal calling convention
 * if they are still present in our code.
 */
#  define rwdbnear
#  define rwdbfar
#  define rwdbhuge
#  define rwdbpascal

#endif
