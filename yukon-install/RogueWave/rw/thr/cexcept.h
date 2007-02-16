#if !defined(__RWTHRCEXCEPT_H__)
#  define __RWTHRCEXCEPT_H__
/*****************************************************************************
 *
 * cexcept.h
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

cexcept.h   - Declarations for:

   RWTHRCompatibleException<Exception> - A template class that can be used 
   to create a thread-compatible exception from an existing exception class.
   A thread-compatible exception is derived from RWTHRxmsg, and possesses 
   clone() and raise() members that allow for a copy of the exception to be 
   stored so that it may be rethrown in the future.  This allows exceptions 
   to be passed across thread boundaries.

See Also:

   cexcept.cc  - Out-of-line member functions for RWTHRCompatibleException.

*****************************************************************************/

#  if !defined(__RWTHREXCEPT_H__)
#     include <rw/thr/except.h>
#  endif

template <class Exception>
class RWTHRTExport RWTHRCompatibleException :
   public RWTHRxmsg {

   RW_THR_DECLARE_TRACEABLE

   protected:

      Exception   exception_;

   public:

      // Construct a new instance, initializing the internal 
      // exception instance from another exception instance
      RWTHRCompatibleException(const Exception& exception)
         RWTHRTHROWSANY;

      // Construct a new instance whose exception value 
      // is initialized from a second instance.
      RWTHRCompatibleException(const RWTHRCompatibleException<Exception>& second)
         RWTHRTHROWSANY;

      // Cast to Exception. Returns the internal Exception.
      operator Exception(void) const
         RWTHRTHROWSANY;

      // Copy the value of the exception in another instance 
      // to exception contained within this instance
      RWTHRCompatibleException<Exception>&
      operator=(const RWTHRCompatibleException<Exception>& second)
         RWTHRTHROWSANY;

      // Make a copy of this instance (so it may be stored)
      virtual
      RWTHRxmsg*
      clone(void) const
         RWTHRTHROWSANY;

      // Throw the exception stored in this instance
      virtual
      void
      raise(void) const
         RWTHRTHROWSANY; 
};

/*****************************************************************************/

template <class Exception>
inline
RWTHRCompatibleException<Exception>::RWTHRCompatibleException(const Exception& exception) 
   RWTHRTHROWSANY
   : exception_(exception)
{
   RWTHRTRACEMF(RWTHRCompatibleException<Exception>,RWTHRCompatibleException(const Exception&));
}

template <class Exception>
inline
RWTHRCompatibleException<Exception>::RWTHRCompatibleException(const RWTHRCompatibleException<Exception>& second) 
   RWTHRTHROWSANY
   : 
      RWTHRxmsg(second),
      exception_(second.exception_)
{
   RWTHRTRACEMF(RWTHRCompatibleException<Exception>,RWTHRCompatibleException(const RWTHRCompatibleException<Exception>&));
}

template <class Exception>
inline
RWTHRCompatibleException<Exception>::operator Exception(void) const
   RWTHRTHROWSANY
{ 
   RWTHRTRACEMF(RWTHRCompatibleException<Exception>,operator Exception(void));
   return exception_; 
}

template <class Exception>
inline
RWTHRCompatibleException<Exception>& 
RWTHRCompatibleException<Exception>::operator=(const RWTHRCompatibleException<Exception>& second) 
   RWTHRTHROWSANY
{ 
   RWTHRTRACEMF(RWTHRCompatibleException<Exception>,operator=(const RWTHRCompatibleException<Exception>&));
   if (&second != this) 
      exception_ = second.exception_; 
   return *this; 
}

#  if defined(RW_COMPILE_INSTANTIATE)
#     include <rw/thr/cexcept.cc>
#  endif

#endif // __RWTHRCEXCEPT_H__

