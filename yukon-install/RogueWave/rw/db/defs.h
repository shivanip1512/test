#ifndef __RWDB_DEFS_H__
#define __RWDB_DEFS_H__


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
 **************************************************************************/
#define RWDBTOOLS 0x0310  /* Compile time version number */

#include <rw/compiler.h>  //defines __WIN32__ and RW_MULTI_THREAD

#  ifdef __WIN32__
#    include <windows.h>
#  endif

// The following two preprocessing blocks are provided for backwards compatibility for our old shared library and 
// DLL macros
#ifndef RWDLL 
#   if defined(_RWDBDLL) || defined(_RWDBBUILDDLL) || defined(_RWDBDYNLINK) || defined(_RWDBBUILDACCESSDLL) 
#      define RWDLL 1  
#   endif
#endif

//#ifndef __RWDEFS_H__
# include "rw/defs.h"    /* Get compiler-specific flags and Tools.h++ defines */
//#endif

#ifdef RW_MULTI_THREAD
//# include <rw/mutex.h>
//   extern RWMutex rwdbRefLock;
//#  define RWDBREFLOCK  rwdbRefLock

// FIXME: This is a hack to make the compiler (winnt/bcc4.5) stop
// complaining about multiply defined BOOL when windows.h and sybfront.h
// are #include'd. sybfront.h typedefs BOOL, if not already #define'd.
// It doesn't detect, at preprocessing time, that BOOL has already been
// typedef'd in windows.h.

// This is probably an implementation level problem.  SHould be resolved
// when the tests are run on different compilers.... -JH
//#  define BOOL int

//#else
//#  define RWDBREFLOCK
#endif // RW_MULTI_THREAD

/*
 * Windows specific
 */
#if defined(__WIN16__) || defined(__WIN32__)   || (defined (__OS2__) && defined(__BORLANDC__))
#include "rw/db/rwdbwind.h"
#else
#define RWDBExport
#define rwdbexport
#define rwdbfar
#define RWDBAccessExport
#define rwdbaccessexport
#endif


/*
 * The following allows getting the declaration for RTL classes
 * right without having to include the appropriate header file
 */
#ifdef __TURBOC__
#  include <_defs.h>    /* Looking for _CLASSTYPE */
#  define _RWDBCLASSTYPE _CLASSTYPE
#else
#  define _RWDBCLASSTYPE
#endif


//////////////////////////////////////////////////////////////////////////
// Collectable Class IDs
//////////////////////////////////////////////////////////////////////////
#define __RWDBVALUE  0x8DB0 
#define __RWDBBLOB   0x8DB1 

//////////////////////////////////////////////////////////////////////////
// Classes
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBAssignment;
class RWDBExport RWDBBlob;
class RWDBExport RWDBColumn;
class RWDBExport RWDBCompoundSelector;
class RWDBExport RWDBConnection;
class RWDBExport RWDBCriterion;
class RWDBExport RWDBCursor;
class RWDBExport RWDBDatabase;
class RWDBExport RWDBDateTime;
class RWDBExport RWDBDeleter;
class RWDBExport RWDBDuration;
class RWDBExport RWDBEntry;
class RWDBExport RWDBEnvironmentHandle;
class RWDBExport RWDBExpr;
class RWDBExport RWDBJoinExpr;
class RWDBExport RWDBForeignKey;
class RWDBExport RWDBFuture;
class RWDBExport RWDBInserter;
class RWDBExport RWDBManager;
class RWDBExport RWDBMemTable;
class RWDBExport RWDBNullIndicator;
class RWDBExport RWDBPhraseBook;
class RWDBExport RWDBPrecedence;
class RWDBExport RWDBReader;
class RWDBExport RWDBResult;
class RWDBExport RWDBRow;
class RWDBExport RWDBSchema;
class RWDBExport RWDBSelector;
class RWDBExport RWDBSelectorBase;
class RWDBExport RWDBShiftableRow;
class RWDBExport RWDBStatus;
class RWDBExport RWDBStoredProc;
class RWDBExport RWDBSystemHandle;
class RWDBExport RWDBTable;
class RWDBExport RWDBTracer;
class RWDBExport RWDBUpdater;
class RWDBExport RWDBValue;

class RWDBExport RWDBColumnImp;
class RWDBExport RWDBCompoundSelectorImp;
class RWDBExport RWDBConnectionImp;
class RWDBExport RWDBCursorImp;
class RWDBExport RWDBDatabaseImp;
class RWDBExport RWDBDatabaseTableImp;
class RWDBExport RWDBDeleterImp;
class RWDBExport RWDBExprImp;
class RWDBExport RWDBForeignKeyImp;
class RWDBExport RWDBFutureImp;
class RWDBExport RWDBInserterImp;
class RWDBExport RWDBReaderImp;
class RWDBExport RWDBResultImp;
class RWDBExport RWDBResultTableImp;
class RWDBExport RWDBSchemaImp;
class RWDBExport RWDBSelectorBaseImp;
class RWDBExport RWDBSelectorImp;
class RWDBExport RWDBStatusImp;
class RWDBExport RWDBStatementImp;
class RWDBExport RWDBStoredProcImp;
class RWDBExport RWDBTableImp;
class RWDBExport RWDBUpdaterImp;


//////////////////////////////////////////////////////////////////////////
// Borrowed Classes
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDecimalPortable;

#if RWTOOLS < 0x0700
// Tools.h++ 6.x Classes
class RWExport RWCString;
class RWExport RWDate;
class RWExport RWTime;
class RWExport RWSet;
#else
// Tools.h++ 7.x Classes
class RWSExport RWCString;
class RWExport  RWDate;
class RWExport  RWTime;
class RWExport  RWSet;
#endif

//////////////////////////////////////////////////////////////////////////
// Macros
//////////////////////////////////////////////////////////////////////////

#define RWDBNEWDBPROCNAME            newDatabaseImp

#if defined(__TURBOC__) && ( defined(__WIN16__) || defined(__WIN32__) || defined(__OS2__) )
#  define RWDBNEWDLLPROCNAME _newDatabaseImp
#else
#  define RWDBNEWDLLPROCNAME newDatabaseImp
#endif

#define RWDBENQUOTE(name)            # name
#define RWDBQUOTE(name)              RWDBENQUOTE(name)

#define RWDBSPRINTF                  sprintf

#include <limits.h>
#define RWDB_NO_TRAIT                -INT_MAX


#endif
