#if !defined(__RWTHRUTIL_H__)
#  define __RWTHRUTIL_H__
/*****************************************************************************
 *
 * util.h
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

util.h - Declarations for utility functions:

   rwGetRelAbsTime - convert relative time to absolute time.
   rwToRWCString - convert integer value to RWCString.
   
See Also:

   util.cpp  - Out-of-line function definitions.


*****************************************************************************/

#if !defined(__RWTHRDEFS_H__)
#  include <rw/thr/defs.h>
#endif

#  if !defined(__RWCSTRING_H__)
#     include <rw/cstring.h>
#  endif

#  if defined(RW_THR_THREAD_API_SOLARIS) || defined(RW_THR_THREAD_API_POSIX)
#     if !defined(__RW_SYS_TIME_H__)
#        define __RW_SYS_TIME_H__ <sys/time.h>
#        include __RW_SYS_TIME_H__
#     endif

#     if defined(RW_THR_HAS_POSIX_TIME)
void
rwGetRelAbsTime(unsigned long milliseconds,timespec* timeout);

#     else
void
rwGetRelAbsTime(unsigned long milliseconds,timestruc_t* timeout);

#     endif

#  endif

            
extern rwthrexport RWCString rwToRWCString(int i);
 
#endif // __RWTHRUTIL_H__
