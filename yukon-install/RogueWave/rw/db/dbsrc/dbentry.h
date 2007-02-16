#ifndef __RWDB_ENTRY_H__
#define __RWDB_ENTRY_H__

/**************************************************************************
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
 **************************************************************************/


#include  <rw/rwset.h>
#include  <rw/db/dbmutex.h>

#include  <rw/db/dbmgr.h>
#include  <rw/db/dbase.h>
#include  <rw/db/dberr.h>

#include  <rw/db/dbsrc/dbasei.h>

#if defined(__WIN16__) || defined(__WIN32__)
#include  <windows.h>
typedef HINSTANCE  RWDBLibHandle;
#elif defined(_AIX)
#include <dlfcn.h>
# ifndef RTLD_LAZY
#  define RTLD_LAZY 1
# endif
typedef void* RWDBLibHandle;
#elif defined(sun) || defined(sgi) || defined(__osf__) || defined(__GNUC__)
#include <dlfcn.h>
typedef void* RWDBLibHandle;
#elif defined(hpux) || defined(__hpux)
#include <dl.h>
#include <errno.h>
typedef shl_t RWDBLibHandle;
#elif defined (__OS2__)
#define INCL_BASE
#include <os2.h>
typedef HMODULE RWDBLibHandle;
#else
typedef int RWDBLibHandle;
#endif
////////////////////////////////////////////////////////////////////////
//
// RWDBEntry
//
//      An RWDBEntry is an element of the RWDBManager's set of Imps.
//      It contains the name of an Implementation and a function
//      by which one obtains an instance of the Implementation, as
//      well as an opaque handle for use in environments which support
//      runtime linkage.
//
//      If runtime linkage is supported and in use, the Implementation
//      name (dbType) may be the name of a dynamically linkable library.
//
//      At link time, the Manager's set of imps is populated with
//      entries for all implementations which are statically linked.
//      These are identified by a RWDBLibHandle of 0.
//
//      At runtime, if dynamic linkage is supported and the manager is
//      asked to supply an implementation for a dbType not present,
//      a search is made for a dynamic library whose name matches
//      the dbType. If found, an attempt is made to find a function
//      in the library through which the application can obtain the
//      desired implementation. If all goes well, the function and the
//      handle are stored in the entry.
////////////////////////////////////////////////////////////////////////
class  RWDBEntry : public RWCollectable {
friend class  RWDBManagerProxy;
public:               // Borland 4.0 bug seems to preclude
                      // private destructor
   virtual ~RWDBEntry  ();
protected:
  RWDBEntry           (const RWCString& type,
                       const RWCString& name,
                       const RWDBNewImpFunc newimp = 0,
                       const RWDBLibHandle handle = 0);
  RWDBEntry           (const RWDBEntry& entry);

  RWBoolean           openLib();
  void                closeLib();

  unsigned            hash() const;
  RWBoolean           isEqual(const RWCollectable *t) const;

  RWCString               dbType_;
  RWCString               funcName_;
  RWDBNewImpFunc      newDatabaseImp_;
  RWDBLibHandle       libHandle_;
};

#endif
