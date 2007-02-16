#ifndef __RW_DCMLDEFS_H__
#define __RW_DCMLDEFS_H__

/***************************************************************************
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

#define RWDCML 0x0160      /* Version number */

#include "rw/defs.h"
#include "limits.h"    // CHAR_BIT
 
/* For some compilers, we cannot access iostream stuff
 * without a qualified name, so make the necessary
 * names global with using-declarations
 */
 
#if (defined(RW_NAMESPACE_REQUIRED) || ( defined(__TURBOC__) && ( __TURBOC__ >= 0x530 ))) && !defined(RW_NO_IOSTD)
/* rstream.h will include the proper iostream library
 * (although we know it is <iostream>).
 */
#  include <rw/rstream.h>
 
/* some classes access old-style strstreams. rw/strstream.h
 * will include the proper file. (Note that if your
 * compiler does not support old-style strstreams, you
 * may have to include <sstream> instead. You'll also
 * have to change all instances of istrstream and
 * ostrstream in the code to istringstream and ostringstream
 * and god knows what functionality will have to be changed.)
 */
#  include <rw/strstrea.h>
/* 
   using std::ios;
   using std::istream;
   using std::ostream;
   using std::istrstream;
   using std::ostrstream;
   using std::cin;
   using std::cerr;
   using std::cout;
   using std::endl;
   using std::ends;
   using std::ws;
   using std::flush;
*/
#endif

#if defined(_MSC_VER) 
#  if (_MSC_VER >= 900) 
#    define RW_NO_LOCAL_STATIC_CONST 1
#  endif
#endif

#if defined(__SUNPRO_CC)
#  define RW_MONEY_COMPILE_INSTANTIATE 1
#else
#  ifdef RW_COMPILE_INSTANTIATE
#    define RW_MONEY_COMPILE_INSTANTIATE 1
#  endif
#endif

#if defined(__hppa) && !defined(HP_ANSI)
#define RW_MONEY_NO_INLINE_ARRAYS
#endif

#if !defined(RW_MONEY_COMPILE_INSTANTIATE) || defined(__BORLANDC__)
#define RW_MONEY_SPECIALIZATION_IN_LIBRARY
#endif

#endif /* ndef __RW_DCMLDEFS_H__ */
