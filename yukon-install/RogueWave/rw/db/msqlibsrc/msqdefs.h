#ifndef __RWDB_MSQDEFS_H__
#define __RWDB_MSQDEFS_H__

/***************************************************************************
 *
 * $Id:
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
 * Definitions for MS SQL Server access library.
 *
 **************************************************************************/


#include  <rw/db/defs.h>

//////////////////////////////////////////////////////////////////////////
//
// The next few sections reconcile the differences based on platform
// information from compiler.h.
//
//////////////////////////////////////////////////////////////////////////


// Define the necessary controls and include the header files
// for MS DB-Library 6.x

#  include <windows.h>

# if   defined(__WIN16__)
#  ifndef DBMSWIN
#  define DBMSWIN
#  endif

# elif defined(__WIN32__)
#  ifndef DBNTWIN32
#  define DBNTWIN32
#  endif

# elif defined(__OS2__)
#  ifndef DBMSOS2
#  define DBMSOS2
#  endif
# endif

extern "C" {
# include    <sqlfront.h>
# include    <sqldb.h>
}


//////////////////////////////////////////////////////////////////////////
//
// Make some corrections to DB-Library defines if necessary
//
//////////////////////////////////////////////////////////////////////////
#ifdef SQLDECIMAL
# ifndef SQLDECIMALN
#  undef SQLDECIMAL
#  define SQLDECIMAL    (BYTE)0x37
#  define SQLDECIMALN   (BYTE)0x6A
# endif
#endif

#ifdef SQLNUMERIC
# ifndef SQLNUMERICN
#  undef SQLNUMERIC
#  define SQLNUMERIC    (BYTE)0x3F
#  define SQLNUMERICN   (BYTE)0x6C
# endif
#endif

//////////////////////////////////////////////////////////////////////////
//
// Maximum data length of a Numeric, Decimal, or Money type
//
// NOTE : Current Sybase value for DB_MAX_NUMLEN (33) is insufficient to
//        handle the largest numeric values (38 digits).  And MSSQL 
//        include headers may not define it (v6.5, v7.0)!  Two additional
//        bytes will store decimal symbol and "end-of-string."  Bug 10468.
//////////////////////////////////////////////////////////////////////////
#ifdef DB_MAX_NUMLEN
#   define RWDB_MAX_NUMLEN    (DB_MAX_NUMLEN + 7)
#else
#   define RWDB_MAX_NUMLEN    40
#endif

//////////////////////////////////////////////////////////////////////////
//                                                                      
// Define standard max lengths
//                                                                     
//////////////////////////////////////////////////////////////////////////
#ifdef DBMAXCHAR
# define RWDBMAXCHAR DBMAXCHAR
#else
# define RWDBMAXCHAR 256
#endif

#ifdef DBMAXCOLNAME
# define RWDBMAXCOLNAME DBMAXCOLNAME
#else
# ifdef MAXNAME
#  define RWDBMAXCOLNAME MAXNAME
# else
#  define RWDBMAXCOLNAME 30
# endif
#endif

//////////////////////////////////////////////////////////////////////////
//
// Reconcile differences in dbfreelogin() vs dbloginfree
//
//////////////////////////////////////////////////////////////////////////
# define rwdbFreeLoginRec(p) dbfreelogin(p)

//////////////////////////////////////////////////////////////////////////
//
// Reconcile differences in the DBDATEREC structure member names.
//
//////////////////////////////////////////////////////////////////////////
#if defined(SQLDATETIME) 
# define RWDByear            year
# define RWDBmonth           month
# define RWDBdmonth          day
# define RWDBhour            hour
# define RWDBminute          minute
# define RWDBsecond          second
# define RWDBmsecond         millisecond
# define RWDBmonthoffset     0
#else
# define RWDByear            dateyear
# define RWDBmonth           datemonth
# define RWDBdmonth          datedmonth
# define RWDBhour            datehour
# define RWDBminute          dateminute
# define RWDBsecond          datesecond
# define RWDBmsecond         datemsecond
# define RWDBmonthoffset     1
#endif

//////////////////////////////////////////////////////////////////////////
//
// Reconcile different flavors of DBPROCESS, LOGINREC, DBCURSOR
//
//////////////////////////////////////////////////////////////////////////
#ifndef DBFAR
# if   defined(DBMSOS2)
#  define DBFAR far
# elif defined(DBMSWIN)
#  define DBFAR NEAR
# elif defined(DBNTWIN32)
#  define DBFAR
# else
#  define DBFAR
# endif
#endif

typedef LOGINREC  DBFAR *RWDBLoginRecP;
typedef DBPROCESS DBFAR *RWDBDbProcessP;
#ifdef CUR_READONLY
 typedef DBCURSOR DBFAR *RWDBCursorP;
#endif

//////////////////////////////////////////////////////////////////////////
//
// Reconcile different flavors for error and message handlers
//
//////////////////////////////////////////////////////////////////////////
#ifndef CS_PUBLIC
#  define CS_PUBLIC
#endif


//////////////////////////////////////////////////////////////////////////
//
// Reconcile different DB-Library Error IDs
//
//////////////////////////////////////////////////////////////////////////
#  define RWDBESMSG     SQLESMSG

//////////////////////////////////////////////////////////////////////////
//                                                                      
// The DBTools.h++ core library communicates with its access modules via
// a single C routine conventionally named 'newDatabaseImp.'
// This routine returns a pointer to a database implementation
// typed according to the access library in which the routine resides.
//                                                                     
// Where Dynamic Linking is not in use, the routine has to have a unique
// name, which is known at compile time to its "magic cookie"
// object.
//
// These macros are used to declare and reference the newDatabaseImp
// routine in this access library.
//                                                                     
//////////////////////////////////////////////////////////////////////////
#if defined(RWDLL)
#ifndef RW_TRAILING_RWEXPORT
#  define RWDBNEWDBPROCDECL rwdbaccessexport RWDBDatabaseImp* rwdbfar RWDBNEWDBPROCNAME
#else
#  define RWDBNEWDBPROCDECL  RWDBDatabaseImp* rwdbaccessexport rwdbfar RWDBNEWDBPROCNAME
#endif
#  define RWDBNEWMSDBLIBPROCNAME RWDBNEWDBPROCNAME
#else
#  define RWDBNEWMSDBLIBPROCNAME newMsDbLibDatabaseImp
#  define RWDBNEWDBPROCDECL RWDBDatabaseImp* rwdbfar RWDBNEWMSDBLIBPROCNAME
#endif

/////////////////////////////////////////////////////////////////////////////
//
// Default values for the environment parameters. These values are as
// defined in the DB-Library manual for properties. Default values are 
// maintained for properties for which there is no direct "get" call.
//
/////////////////////////////////////////////////////////////////////////////
#define RWDB_DB_MAXPARAMSIZEDEFAULT         2048
#define RWDB_DB_DEFAULT_LOGIN_TIMEOUT       60 
#define RWDB_DB_DEFAULT_LANGUAGE            "us_english" 
#define RWDB_DB_DEFAULT_IFILE               "interfaces" 
#define RWDB_DB_DEFAULT_TDS_PACKET_SIZE     512
#define RWDB_DB_HOSTNAME_LIMIT              30

#endif

