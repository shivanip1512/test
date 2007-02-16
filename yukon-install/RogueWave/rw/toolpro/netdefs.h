#ifndef __RWNET__DEFS_H__
#define __RWNET__DEFS_H__
/***************************************************************************
 *
 * defs.h
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
 * Commercial Computer Software – Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 **************************************************************************/
/*
 * NetDefs: defines for the net part of Tools.h++ Professional
 *
 * This is the section for defines common to all of Net.h++.
 */

#include <rw/toolpro/tprdefs.h> /* get mutual toolpro defs */
#include <rw/defs.h>            /*  and the tools defs     */
#include <rw/cstring.h>
#include <rw/toolpro/netwind.h> /* definitions for net dll */

#if (defined(__SUNPRO_CC) && (__SUNPRO_CC == 0x500) && defined(_LP64) ) || defined(__IBMCPP__)
	#include <sys/socket.h>
#endif


#define USE_RWTOOLS 1 /* if tools is dll, get correct interface */

/*
 * Are the getXbyY functions (gethostbyname, eg) multi-thread safe?  They
 * usually are not.
 */
#if defined(RW_MULTI_THREAD) && !defined(_WIN32_)
# define RWNET_GETXBYY_NOT_REENTRANT 1
#endif

#if defined(__IBMCPP__) && defined(__OS2__)
#define RWNET_OS2
#endif


/* If platform is HP, which compiler are we using? */
#if defined (__hpux)
#  if defined (__cplusplus) && __cplusplus >= 199707L
#    define HPUX_ANSI
#  else
#    define HPUX_CFRONT
#  endif
#endif

/*
 * Set which socket address types this platform knows about
 */
#define RWNET_HAS_INET_SOCKETS 1
/*#define RWNET_HAS_UNIX_SOCKETS 1*/

/*
 * Does this platform support struct msghdr directly
 */
#if defined(RWNET_WINSOCK)
# define RWNET_NO_MSGHDR 1
#endif

/*
 * do the system calls take a const char* or just a char*
 */
#if defined(RWNET_OS2)
#define RWNET_NO_CONST_CHARPTR_PARAMS
#endif

/*
 * do socket system calls take a int or a size_t for socket addrlen
 */

#if (defined(__SUNPRO_CC) && (__SUNPRO_CC == 0x500) && defined(_LP64) ) || defined(__IBMCPP__)
typedef socklen_t RWSockCallIntType;
#else
#	if defined (RW_SOCKET_SYSCALLS_USE_SIZE_T)
	typedef size_t RWSockCallIntType;
#	else
	typedef int    RWSockCallIntType;
#	endif
#endif


/*
 * Uncomment the following if your compiler uses a select
 * prototype that takes pointers to integers for the file
 * descriptor sets, rather than the standard pointers to
 * struct fd_set objects.
 */
/* #define RW_SELECT_USES_INT_POINTERS */
#if defined(__hpux)
#  if (RWOSVERSION < 1020)
#    define RW_SELECT_USES_INT_POINTERS
#  endif
#endif

/*
 * Uncomment the following if your compiler uses cma_select
 * in posix compliant multi-threaded applications.
 */
/* #define RW_USE_CMA_SELECT */
#if defined(RW_MULTI_THREAD) && defined(RW_POSIX_THREADS) && !defined(__osf__) && !defined(__hpux) 
# define RW_USE_CMA
#endif

/*
 * Uncomment the following if your compiler uses an ioctl 
 * prototype that takes integers for the command argument
 * instead of longs.
 */
#if defined(__hpux) || defined(RWNET_OS2)
# define RW_IOCTL_USES_INT_ARG 
#endif

/*
 * Uncomment the following if your networking library's
 * non-blocking connect doesn't have failure detection 
 * by select working. I.e. it doesn't return an error condition.
 */
#if defined(RWNET_OS2) || defined(__osf__) 
# define RWNET_BAD_CONNECT_DETECT
#endif

#if defined(RW_MULTI_THREAD) && defined(RW_POSIX_THREADS) && defined(HPUX_CFRONT) 
#define RWNET_CANT_USE_EXCEPTION_ATTRIBUTE
#endif

/*
 * Constant values used in the request argument in the
 * ioctl function are defined in separate header files
 * on Solaris and SunOS systems (sockio.h and filio.h).  Definitions
 * are combined into one header file on HPUX and IBM AIX systems. 
 */
#if defined(__hpux) || defined(_AIX) || defined(__osf__) || defined(RWNET_OS2) || defined(linux)
#define RW_NO_FILIO_HEADER
#endif


/*
 * Avoid reinterpret cast warning messages in HPUX ANSI compiler.
 */

#if defined(HPUX_ANSI)
# define RW_NO_REINTERPRET_CAST
#endif


/**************************************************************
 * The following things really belong in the Tools.h++ defs.h *
 * or compiler.h                                              *
 **************************************************************/
/*
 * For now, we attempt to set these automatically for the problem
 * compilers.  This will be migrated to config.
 */

/*
 * Uncomment the following if your compiler has a problem initializing
 * static member data in template classes.
 */

/* #define RW_TEMPLATE_STATIC_INIT_BUG 1 */

/*
 * Uncomment the following if your compiler does not provide the
 * strerror() function.  An attempt will be made to use the sys_errlist
 * interface instead.
 */

/* #define RW_NO_STRERROR */
/* 
 * This applies to sun compilers before the cafe compiler 
 */
#if defined(sun) && !defined(__SUNPRO_CC)
# define RW_NO_STRERROR
#endif


/* 
 * Uncomment the following if your compiler does not supply a prototype
 * for the shutdown(2) system call.  SunOS 4.1.3, for example, seems to
 * have forgotten this prototype.
 */

#if (defined(sun) && !defined(__SVR4)) || defined(_AIX)
# define RW_NO_SHUTDOWN_PROTOTYPE
#endif


/*
 * Uncomment the following if your compiler supports template functions
 * where none of the arguments are of the template parameter type,
 * for example "template <class X> X f()".
 */

/* #define RW_NO_EXPLICIT_TEMPLATE_FUNCTIONS */
/*
 * For now, I believe no compilers support this
 */
#define RW_NO_EXPLICIT_TEMPLATE_FUNCTIONS

/*
 * Uncomment the following if your compiler can't throw exceptions
 * under some circumstances when arrays are initialized with null pointers
 * instead of explicit calls to new T[0].
 */
/* #define RW_NEED_TO_CALL_NEW_ZERO_WITH_EXCEPTIONS */

#if defined(HPUX_CFRONT)
# define RW_NEED_TO_CALL_NEW_ZERO_WITH_EXCEPTIONS
#endif

/*
 * Uncomment the following if your compiler forces you to pragma
 * define template instantiations.
 */
/* #define RW_INSTANTIATE_TEMPLATES_WITH_PRAGMA  */
#if defined(__osf__)
#  define RW_INSTANTIATE_TEMPLATES_WITH_PRAGMA  
#endif


/*
 * Handle Microsoft C++ 2.1.  Tools.h++ takes care of this in versions
 * later than 6.1.0.
 */

#if defined(_MSC_VER) && (_MSC_VER >= 900) && (RWTOOLS <= 0x700)
#undef RW_NO_EXCEPTIONS
#undef RW_NO_TEMPLATES
#define RW_COMPILE_INSTANTIATE 1
#define RW_TEMPLATE_STATIC_INIT_BUG 1
#endif

/*  Borland 4.x and below has a problem initializing
    static member data in template classes. It seems that
    Borland Builder 3 also has problems.          */
#if (__TURBOC__ < 0x500) || (__TURBOC__ == 0x530) || (__TURBOC__ == 0x540)
#define RW_TEMPLATE_STATIC_INIT_BUG 1

#endif

/**************************************************************
 * These next things are part of the implementation of        *
 * Tools.h++ Professional.  They may go away!                 *
 **************************************************************/

RWCString RWNETExport RWNetItoa(int i);
// A little (inefficient!) routine for making strings out of integers.
// We need to do this all the time for the net lib: port numbers, error
// numbers, ...  all need to be strings sometimes.
// Some sort of streaming to string abstraction would be even better,
// of course.

#endif


#if _MSC_VER > 1000
// Disable MSVC's "bool is reserve word" warning where appropriate
#define RWSTD_MSVC_BOOL_WARNING 1
#endif


#ifdef RWSTD_MSVC_BOOL_WARNING
#pragma warning (disable : 4237)
#endif

