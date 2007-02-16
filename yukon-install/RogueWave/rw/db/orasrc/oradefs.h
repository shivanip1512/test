#ifndef __RWDB_ORADEFS_H__
#define __RWDB_ORADEFS_H__

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
 **************************************************************************
 *
 * Definitions for Oracle OCI Access Library
 *
 **************************************************************************/


#include  <rw/db/defs.h>
#include  <rw/defs.h>

//////////////////////////////////////////////////////////////////////////
//
// Common headers.
//
//////////////////////////////////////////////////////////////////////////

#ifdef __cplusplus
extern "C" {
#endif

# include <ociapr.h>

#ifdef __cplusplus
}
#endif /* __cplusplus */

// can't use Oracle macros min and max from oratypes.h via ociapr.h
#undef min
#undef max
 
typedef sword osword;
typedef dvoid ovoid;
typedef text otext;
 
typedef ub1 oub1;
typedef sb1 osb1;
typedef ub2 oub2;
typedef sb2 osb2;

typedef ub4 oub4;
typedef sb4 osb4;

//////////////////////////////////////////////////////////////////////////
//                                                                      
// Define default limitations
//                                                                     
//////////////////////////////////////////////////////////////////////////

/* limits */
#define OCI_MAXERRORMESSAGE     1000
#define OCI_MAXOBJECTNAME       30
#define OCI_MAXFETCH            32767
 
/* data type lengths */
#define OCI_MAXCHAR             255
#define OCI_MAXVARCHAR          4000
#define OCI_MAXNVARCHAR         4000
#define OCI_MAXRAW              255
 
/* display lengths; conversions to character strings */
#define OCI_MAXNUMBERDISPLAY    40
#define OCI_MAXROWIDDISPLAY     18
#define OCI_MAXMSLABELDISPLAY   20

/* internal data types */
#define OCI_ICHR  SQLT_CHR   /* oracle character string */
#define OCI_INUM  SQLT_NUM   /* oracle numeric */
#define OCI_ILNG  SQLT_LNG   /* oracle long character string */
#define OCI_IRID  SQLT_RID   /* oracle rowid */
#define OCI_IDAT  SQLT_DAT   /* oracle date */
#define OCI_IBIN  SQLT_BIN   /* oracle binary */
#define OCI_ILBI  SQLT_LBI   /* oracle long binary */
#define OCI_IAFC  SQLT_AFC   /* oracle fixed char */
#define OCI_ICUR  SQLT_CUR   /* oracle cursor variable */
#define OCI_IOSL  SQLT_OSL   /* oracle oslabel type */

/* external data types */
#define OCI_ECHR  SQLT_CHR  /* (ORANET TYPE) character string */
#define OCI_ENUM  SQLT_NUM  /* (ORANET TYPE) oracle numeric */
#define OCI_EINT  SQLT_INT  /* (ORANET TYPE) integer */
#define OCI_EFLT  SQLT_FLT  /* (ORANET TYPE) floating point number */
#define OCI_ESTR  SQLT_STR  /* zero terminated string */
#define OCI_EVNU  SQLT_VNU  /* NUM with preceding length byte */
#define OCI_EPDN  SQLT_PDN  /* (ORANET TYPE) Packed Decimal Numeric */
#define OCI_ELNG  SQLT_LNG  /* long */
#define OCI_EVCS  SQLT_VCS  /* variable character string */
#define OCI_ENON  SQLT_NON  /* null/empty PCC Descriptor entry */
#define OCI_ERID  SQLT_RID  /* rowid */
#define OCI_EDAT  SQLT_DAT  /* date in oracle format */
#define OCI_EVBI  SQLT_VBI  /* binary in VCS format */
#define OCI_EBIN  SQLT_BIN  /* binary data(DTYBIN) */
#define OCI_ELBI  SQLT_LBI  /* long binary */
#define OCI_EUIN  SQLT_UIN  /* unsigned integer */
#define OCI_ESLS  SQLT_SLS  /* display sign leading separate */
#define OCI_ELVC  SQLT_LVC  /* longer longs (char) */
#define OCI_ELVB  SQLT_LVB  /* longer long binary */
#define OCI_EAFC  SQLT_AFC  /* Ansi fixed char */
#define OCI_EAVC  SQLT_AVC  /* Ansi Var char */
#define OCI_ECUR  SQLT_CUR  /* cursor type */
#define OCI_ELAB  SQLT_LAB  /* label type */
#define OCI_EOSL  SQLT_OSL  /* oslabel type */

#define OCI_FLOAT_SCALE    -127
 
/* indicator codes from fetches */
#define OCI_VALUENORMAL     0
#define OCI_VALUENULL       -1

 
/* SQL function types */
/*    just DML, transaction control and session control statements */
#define OCI_FT_SETROLE          2       // SC
#define OCI_FT_INSERT           3       // DML
#define OCI_FT_SELECT           4       // DML
#define OCI_FT_UPDATE           5       // DML
#define OCI_FT_DELETE           9       // DML
#define OCI_FT_EXPLAIN          27      // DML
#define OCI_FT_SETTRANSACTION   33      // TC
#define OCI_FT_PLSQL            34      // DML
#define OCI_FT_LOCKTABLE        35      // DML
#define OCI_FT_ALTERSESSION     52      // SC
#define OCI_FT_COMMIT           54      // TC
#define OCI_FT_ROLLBACK         55      // TC
#define OCI_FT_SAVEPOINT        56      // TC
 
/* return codes */
#define OCI_ENDOFFETCH          1403
#define OCI_OBJECTDOESNTEXIST   6564
#define OCI_INVALIDOBJECT       20003
 
/*
 ***************************************************************************
 * Host Data Area (HDA)
 ***************************************************************************
 */
#define HDA_SIZE  (sizeof(void*) * 8 * 8)
typedef oub1           Hda_Def[HDA_SIZE];
 
/*
 ***************************************************************************
 * Oracle Internal Data Structures
 ***************************************************************************
 */
struct oci_date {
    oub1    century;
    oub1    year;
    oub1    month;
    oub1    day;
    oub1    hour;
    oub1    minute;
    oub1    second;
};
 
/*
 ***************************************************************************
 * Oracle Call Interface
 ***************************************************************************
 */
 
/* common parameters */
#define OUNUSEDPTR      0           /* for unused pointer parameters */
#define OUNUSED         -1          /* for unused parameters */
#define ONULLSTR        -1          /* for null string parameters */

/* "oexfet" parameters */
#define ONORMALEXEC     0           /* for normal executions */
#define ONORMALFETCH    0           /* for normal fetchs */
 
/* "oparse" parameters */
#define ONORMALPARSE    0           /* parse immediately */
#define ODEFERPARSE     1           /* defer parse until necessary */
#define OVERSION7       2           /* Version 7 server parsing */
 
/* "odessp" parameters */
#define OINPARAMETER    0           /* input parameter to procedure */
#define OOUTPARAMETER   1           /* output parameter to procedure */
#define OINOUTPARAMETER 2           /* input / output parameter to procedure */
#define ORETURNPOSITION 0           /* position of return types */
 


#define RWDBORACLEFETCHSIZE     ((long)102400)
#define RWDBORACLELONGFETCHSIZE ((long)65535-32)    /* do not change */
#define RWDBORACLELONGCURSORFETCHSIZE (UINT_MAX-32)    
#define RWDBORACLEALLOCSIZE     (UINT_MAX-32)
#define RWDBORACLEPARAMLISTSIZE ((int)32)
#define RWDBORACLEMAXPARAMETERSIZE ((int)2000)
#define RWDBORACLELONGBINDSIZE (UINT_MAX-32)

typedef int RWDBOraRetCode;

#define RWDB_ORA_SUCCESS 0
#define RWDB_ORA_PENDING -3123  /* ORA -03123 */
#define RWDB_ORA_FAIL    -1

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
#ifndef RW_TRAILING_RWEXPORT
#  define RWDBNEWDBPROCDECL rwdbaccessexport RWDBDatabaseImp* rwdbfar RWDBNEWDBPROCNAME
#else
#  define RWDBNEWDBPROCDECL  RWDBDatabaseImp* rwdbaccessexport rwdbfar RWDBNEWDBPROCNAME
#endif
#  define RWDBNEWORALIBPROCNAME RWDBNEWDBPROCNAME
#else
#  define RWDBNEWORALIBPROCNAME RWDBNEWORALIBPROCNAME
#  define RWDBNEWDBPROCDECL RWDBDatabaseImp* rwdbfar RWDBNEWORALIBPROCNAME
#endif


//////////////////////////////////////////////////////////////////////////
//                                                                      
// The following macros used to be set by oracfg.exe & oraasync.bat
// last used in the 3.0.0 release where all tested clients supported
// MT, Async and stored procs (oci 7.3.3 generally).  Problems with 
// the executables lead to removing the tests and defaulting to 
// 'SUPPORTS'.  The macros remain in place for use by customers using 
// older versions of clients.  NO_ODESSP used to be set in modify by
// determining if stored procs were supported via odessp.exe in the client.
//                                                                     
//////////////////////////////////////////////////////////////////////////

#define RWDB_ORACLE_SUPPORTS_ASYNC
//#define RWDB_ORACLE_DOES_NOT_SUPPORT_ASYNC
#define RWDB_ORACLE_SUPPORTS_THREADS
//#define RWDB_ORACLE_DOES_NOT_SUPPORT_THREADS
//#ifdef RWDB_NO_ODESSP
#endif

