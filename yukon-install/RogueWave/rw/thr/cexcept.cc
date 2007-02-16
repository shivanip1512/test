#if !defined(__RWTHRCEXCEPT_CC__)
#  define __RWTHRCEXCEPT_CC__
/*****************************************************************************
 *
 * cexcept.cc
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

cexcept.cc  - Out-of-line function definitions for:

   RWTHRCompatibleException<Exception> - wrapper class to convert template 
   parameter type to a RWTHRxmsg exception.

See Also:

   cexcept.h   - Class declaration.

*****************************************************************************/

#  if !defined(__RWTHRCEXCEPT_H__)
#     include <rw/thr/cexcept.h>
#  endif

RW_THR_IMPLEMENT_TRACEABLE_T1(RWTHRCompatibleException,Exception)

template <class Exception>
void
RWTHRCompatibleException<Exception>::raise(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWTHRCompatibleException<Exception>,raise(void) const:void);
   // xlC differentiates between throwing a "const Exception" and throwing an "Exception".
   // We want to throw the latter, hence the cast:
   throw (Exception&)exception_;
}

template <class Exception>
RWTHRxmsg*
RWTHRCompatibleException<Exception>::clone(void) const
   RWTHRTHROWSANY
{
   RWTHRTRACEMF(RWTHRCompatibleException<Exception>,clone(void) const:RWTHRxmsg*);
   return new RWTHRCompatibleException<Exception>(*this);
}

#endif // __RWTHRCEXCEPT_CC__


