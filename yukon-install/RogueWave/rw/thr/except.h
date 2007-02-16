#if !defined(__RWTHREXCEPT_H__)
#define __RWTHREXCEPT_H__
/*****************************************************************************
 *
 * except.h
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

except.h - Declarations for:

   RWTHRxmsg class - Base class for thread-compatible exceptions.
      RWTHROperationNotAvailable
      RWTHROperationNotImplemented
      RWTHROperationCanceled
      RWTHROperationTerminated
      RWTHROperationAborted
      RWTHRInternalError
      RWTHRExternalError
      RWTHRResourceLimit   
      RWTHRIllegalUsage
         RWTHRBoundsError
         RWTHRIllegalAccess
         RWTHRInvalidPointer
         RWTHROperationNotSupported
         RWTHRThreadNotActive
         RWTHRThreadActive

   rwGetLastErrorMsg - function to retreive last Win32 error number.
   rwErrorMsg - function to retrieve message for specified error number.
   rwErrnoMsg - function to retrieve message for current errno value.

   A thread compatible exception possesses clone() and raise() members that
   allow the exception to be copied and stored so that it may be rethrown in
   the future.  This capability allows these exceptions to be passed across 
   thread boundaries.

   The RW_THR_DECLARE_EXECEPTION(name) and RW_THR_IMPLEMENT_EXCEPTION(name)
   macros may be used to define the raise and clone members in user-defined
   exceptions.

See Also:

   except.cpp - Out-of-line function definitions.

*****************************************************************************/


#  if !defined(__RWTHRDEFS_H__)
#     include <rw/thr/defs.h>
#  endif

#  if !defined(__RWERR_H__)
#     include <rw/rwerr.h>
#  endif

#  if !defined(__RWTHRMSG_H__)
#     include <rw/thr/thrmsg.h>
#  endif

#  if !defined(__RWCSTRING_H__)
#     include <rw/cstring.h>
#  endif

extern rwthrexport RWCString rwGetLastErrorMsg(void);
extern rwthrexport RWCString rwErrorMsg(int error);
extern rwthrexport RWCString rwErrnoMsg(void);

//    Ideally, clone() should return an RWOnlyPointer
//    to the clone instance, but cfront-based compilers
//    can't handle the embedded declaration of 
//    RWOnlyPointer<RWTHRxmsg> within the RWTHRxmsg class
//    declaration - they complain that RWTHRxmsg is undefined
//    when they try to instantiate the template.

#define RW_THR_DECLARE_EXCEPTION(Name)                      \
   public:                                                  \
      /* Throw the derived exception type! */               \
      virtual                                               \
      void                                                  \
      raise(void) const                                     \
         RWTHRTHROWSANY;                                    \
                                                            \
      virtual                                               \
      RWTHRxmsg*                                            \
      clone(void) const                                     \
         RWTHRTHROWSANY;

// For compilers (Like MSVC) that need to have nested members be inline...

#define RW_THR_DECLARE_INLINE_EXCEPTION(Name)               \
   public:                                                  \
      /* Throw the derived exception type! */               \
      virtual                                               \
      void                                                  \
      raise(void) const                                     \
         RWTHRTHROWSANY                                     \
      {                                                     \
         throw Name(*this);                                 \
      }                                                     \
                                                            \
      virtual                                               \
      RWTHRxmsg*                                            \
      clone(void) const                                     \
         RWTHRTHROWSANY                                     \
      {                                                     \
         return new Name(*this);                            \
      }

#define RW_THR_IMPLEMENT_EXCEPTION(Name)                    \
/* virtual */                                               \
void                                                        \
Name::raise(void) const                                     \
   RWTHRTHROWSANY                                           \
{                                                           \
   throw Name(*this);                                       \
}                                                           \
                                                            \
/* virtual */                                               \
RWTHRxmsg*                                                  \
Name::clone(void) const                                     \
   RWTHRTHROWSANY                                           \
{                                                           \
   return new Name(*this);                                  \
}


#define RW_THR_IMPLEMENT_EXCEPTION_T1(Name,TP1)             \
template <class TP1>                                        \
/* virtual */                                               \
void                                                        \
Name::raise(void) const                                     \
   RWTHRTHROWSANY                                           \
{                                                           \
   throw Name(*this);                                       \
}                                                           \
                                                            \
template <class TP1>                                        \
/* virtual */                                               \
RWTHRxmsg*                                                  \
Name::clone(void) const                                     \
   RWTHRTHROWSANY                                           \
{                                                           \
   return new Name(*this);                                  \
}


class RWTHRExport RWTHRxmsg :
   public RWxmsg {

   RW_THR_DECLARE_EXCEPTION(RWTHRxmsg)

   RW_THR_DECLARE_TRACEABLE

   public:

      // Construct an exception with an empty message string
      RWTHRxmsg(void)
         RWTHRTHROWSNONE;

      // Construct an exception with the specified message string
      RWTHRxmsg(const RWCString &msg)
         RWTHRTHROWSNONE;

      // Construct an exception initializing its message string
      // using the message string value of another exception
      RWTHRxmsg(const RWTHRxmsg &second)
         RWTHRTHROWSNONE;

      // This function has virtual members, so it needs 
      // a virtual destructor...
      virtual 
      ~RWTHRxmsg(void)
         RWTHRTHROWSNONE;

};


class RWTHRExport RWTHROperationNotAvailable :
   public RWTHRxmsg {

   RW_THR_DECLARE_EXCEPTION(RWTHROperationNotAvailable)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHROperationNotAvailable(const RWCString& reason=RW_THR_Not_Available)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHROperationNotImplemented :
   public RWTHRxmsg {

   RW_THR_DECLARE_EXCEPTION(RWTHROperationNotImplemented)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHROperationNotImplemented(const RWCString& 
                                   reason=RW_THR_Not_Implemented)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHROperationAborted :
   public RWTHRxmsg {
 
   RW_THR_DECLARE_EXCEPTION(RWTHROperationAborted)
 
   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHROperationAborted(const RWCString& reason=RW_THR_Aborted)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHROperationCanceled :
   public RWTHRxmsg {
 
   RW_THR_DECLARE_EXCEPTION(RWTHROperationCanceled)
 
   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHROperationCanceled(const RWCString& reason=RW_THR_Canceled)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHROperationTerminated :
   public RWTHRxmsg {
 
   RW_THR_DECLARE_EXCEPTION(RWTHROperationTerminated)
 
   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHROperationTerminated(const RWCString& reason=RW_THR_Terminated)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHRIllegalUsage :
   public RWTHRxmsg {

   RW_THR_DECLARE_EXCEPTION(RWTHRIllegalUsage)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRIllegalUsage(const RWCString& reason=RW_THR_Invalid_Usage)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHROperationNotSupported :
   public RWTHRIllegalUsage {

   RW_THR_DECLARE_EXCEPTION(RWTHROperationNotSupported)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHROperationNotSupported(const RWCString& reason=RW_THR_Not_Supported)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHRInternalError :
   public RWTHRxmsg {

   RW_THR_DECLARE_EXCEPTION(RWTHRInternalError)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRInternalError(const RWCString& reason=RW_THR_InternalError)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHRExternalError :
   public RWTHRxmsg {

   RW_THR_DECLARE_EXCEPTION(RWTHRExternalError)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRExternalError(const RWCString& reason=RW_THR_ExternalError)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHRResourceLimit :
   public RWTHRxmsg {

   RW_THR_DECLARE_EXCEPTION(RWTHRResourceLimit)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRResourceLimit(const RWCString& reason=RW_THR_Resource_Limit)
         RWTHRTHROWSNONE;
};


class RWTHRExport RWTHRBoundsError :
   public RWTHRIllegalUsage {

   RW_THR_DECLARE_EXCEPTION(RWTHRBoundsError)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRBoundsError(const RWCString& reason=RW_THR_Bounds_Error)
         RWTHRTHROWSNONE;
};


// Thrown in situations where an external thread attempted to use a 
// thread object member that is intended for use only by the thread
// represented by the thread object, or when the internal thread
// attempted to use a thread object member that is intended for use
// only by threads that were created external to the thread object.

class RWTHRExport RWTHRIllegalAccess :
   public RWTHRIllegalUsage {
   
   RW_THR_DECLARE_EXCEPTION(RWTHRIllegalAccess)
   
   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRIllegalAccess(const RWCString& reason=RW_THR_Illegal_Access)
         RWTHRTHROWSNONE;
};

// Thrown by pointer classes when an attempt is made to
// dereference a pointer that is not pointing to anything.
// Typically thrown by operator* and operator-> members.

class RWTHRExport RWTHRInvalidPointer :
   public RWTHRIllegalUsage {

   RW_THR_DECLARE_EXCEPTION(RWTHRInvalidPointer)

   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRInvalidPointer(const RWCString& reason=RW_THR_Invalid_Pointer)
         RWTHRTHROWSNONE;
};

// Thrown by thread object initialization members when the thread
// object has already started a thread, at which point, initialization 
// has no effect.

class RWTHRExport RWTHRThreadActive :
   public RWTHRIllegalUsage {
   
   RW_THR_DECLARE_EXCEPTION(RWTHRThreadActive)
   
   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRThreadActive(const RWCString& reason=RW_THR_Thread_Active)
         RWTHRTHROWSNONE;
};


// Thrown by thread object members that require that the thread
// object be started and possess an active thread.

class RWTHRExport RWTHRThreadNotActive :
   public RWTHRIllegalUsage {
   
   RW_THR_DECLARE_EXCEPTION(ThreadNotActive)
   
   RW_THR_DECLARE_TRACEABLE

   public:

      RWTHRThreadNotActive(const RWCString& reason=RW_THR_Thread_Not_Active)
         RWTHRTHROWSNONE;
};

/*****************************************************************************/

inline
RWTHRxmsg::RWTHRxmsg(void)
   RWTHRTHROWSNONE
   :
      RWxmsg("")
{
   RWTHRTRACEMF(RWTHRxmsg,RWTHRxmsg(void));
}

inline
RWTHRxmsg::RWTHRxmsg(const RWCString& msg)
   RWTHRTHROWSNONE
   :
      RWxmsg(msg)
{
   RWTHRTRACEMF(RWTHRxmsg,RWTHRxmsg(const RWCString&));
}

inline
RWTHRxmsg::RWTHRxmsg(const RWTHRxmsg &second) 
   RWTHRTHROWSNONE
   : 
      RWxmsg(second) 
{
   RWTHRTRACEMF(RWTHRxmsg,RWTHRxmsg(const RWTHRxmsg&));
}

#endif  // __RWTHREXCEPT_H__

