#ifndef __RWDB_ODBDEFS_H__
#define __RWDB_ODBDEFS_H__

/***************************************************************************
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
 ***************************************************************************
 *
 * Definitions for ODBC Access Library
 *
 **************************************************************************/

#include <rw/db/defs.h>
#include <rw/defs.h>
#include <sql.h>

//////////////////////////////////////////////////////////////////////////
//
// Common headers.
//
//////////////////////////////////////////////////////////////////////////

#if defined(__WIN16__) || defined(__WIN32__)
#include <windows.h>
#else
#define pascal
#endif


//////////////////////////////////////////////////////////////////////////
//
// Encapsulate differences between IBM_CLI and MS_ODBC
//
/////////////////////////////////////////////////////////////////////////
#ifdef IBM_CLI_DB2
  #define RWDBHENV   SQLHENV
  #define RWDBHDBC   SQLHDBC
  #define RWDBHSTMT  SQLHSTMT
#  ifdef __OS2__
     #define DB2OS2
#  endif
#else
  #define RWDBHENV   HENV
  #define RWDBHDBC   HDBC
  #define RWDBHSTMT  HSTMT
#  ifdef QE_OS2
   #define FAR
   #define CALLBACK
   #define EXPORT
#  endif
#endif

//////////////////////////////////////////////////////////////////////////
//
// Define multi-threading controls
//
//////////////////////////////////////////////////////////////////////////
#ifdef RW_MULTI_THREAD
# include <rw/db/dbmutex.h>
   extern RWDBMutex rwdbODBCRefLock;
#  define RWDBODBCREFLOCK       rwdbODBCRefLock
#else
#ifdef __OS2__
#  include <os2.h>
#endif
#  define RWDBODBCREFLOCK
#endif

/////////////////////////////////////////////////////////////////////////
//
//Include the correct header files
//
////////////////////////////////////////////////////////////////////////
#ifdef IBM_CLI_DB2
  // IBM-CLI Drivers
  #include <sqlcli1.h>
#else
  //Other Drivers
  #include <sql.h>
  #include <sqlext.h>
#endif

//////////////////////////////////////////////////////////////////////////
//
// Define default limitations
//
//////////////////////////////////////////////////////////////////////////

#define RWDBODBCDRIVERNAMESIZE  128
#define RWDBODBCCURSORNAMESIZE  80
#define RWDBODBCTYPENAMESIZE    80
#define RWDBODBCCOLUMNNAMESIZE  128
#define RWDBODBCBLOBPARTSIZE    1024
// precision and scale are needed when inserting decimal. Since ODBC does not
// have default one, we just put these magic numbers here. 
#define RWDBODBCPRECISIONSIZE   SQL_MAX_NUMERIC_LEN 
#define RWDBODBCSCALESIZE       6


//////////////////////////////////////////////////////////////////////////
//
// Define some Macros
//
//////////////////////////////////////////////////////////////////////////

#define RWDB_DEFAULT_LOGIN_TIMEOUT      0
#ifndef IBM_CLI_DB2
#define RWDB_DEFAULT_CURSOR_OPTION     SQL_CUR_USE_DRIVER
#define RWDB_DEFAULT_TRACE_OPTION      SQL_OPT_TRACE_OFF
#else
#define RWDB_DEFAULT_CURSOR_OPTION     1
#define RWDB_DEFAULT_TRACE_OPTION      1
#endif

#define RWDB_DEFAULT_TRACE_FILENAME    "SQL.LOG"
#define RWDB_DEFAULT_ACCESS_MODE       SQL_MODE_READ_WRITE
#define RWDB_DEFAULT_QUALIFIER ""


//////////////////////////////////////////////////////////////////////////
//
// Forward Define some key classes
//
//////////////////////////////////////////////////////////////////////////

class RWDBAccessExport RWDBODBCLibDatabaseImp;


//////////////////////////////////////////////////////////////////////////
//                                                                      
// The core library communicates with its access modules via
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
#if defined(RWDLL) || defined (_RWBUILDSHARED)
#  ifndef RW_TRAILING_RWEXPORT
#    define RWDBNEWDBPROCDECL rwdbaccessexport RWDBDatabaseImp* rwdbfar RWDBNEWDBPROCNAME
#  else
#    define RWDBNEWDBPROCDECL RWDBDatabaseImp* rwdbaccessexport rwdbfar RWDBNEWDBPROCNAME
#  endif
#  define RWDBNEWODBCLIBPROCNAME RWDBNEWDBPROCNAME
#else
#  define RWDBNEWODBCLIBPROCNAME RWDBNEWODBCLIBPROCNAME
#  define RWDBNEWDBPROCDECL RWDBDatabaseImp* rwdbfar RWDBNEWODBCLIBPROCNAME
#endif

#endif //  __RWDB_ODBDEFS_H__
