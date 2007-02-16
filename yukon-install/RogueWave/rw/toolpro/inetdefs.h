#ifndef _RWINDEFS_
#define _RWINDEFS_
/***************************************************************************
 *
 * inetdefs.h
 *
 * $Id$
 *
 * Copyright (c) 1998-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 * Commercial Computer Software � Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 **************************************************************************/

#include <rw/thr/defs.h> /* get cma wrapper protection if needed*/
#include <rw/thr/except.h>
#include <rw/defs.h>
#include <rw/cstring.h>
#include <rw/toolpro/inetwind.h>     // definitions for dll

#define USE_RWTOOLS 1             // always using tools

#define RWINET_IGNORE_OPERATION_ABORT_THROW(xx) \
    try { xx; } catch (const RWTHROperationAborted&) {}

/////////////////////////////////////////////////////////////////////////
//    Microsoft Visual C++ Compiler and Win32
/////////////////////////////////////////////////////////////////////////
 
#  if defined(_MSC_VER)
//    Turn off benign but pervasive warning, globally
#     pragma warning(disable : 4251)
#     define RW_INET_SUPERCLASSES_MUST_BE_EXPORTED
#  endif

/////////////////////////////////////////////////////////////////////////
//    Borland C++ Compiler and Win32
/////////////////////////////////////////////////////////////////////////
 
#  if defined(__BORLANDC__)
#     define RW_INET_INLINE_FUNCTIONS_NEED_BE_EXPANDED
#  endif

#endif
