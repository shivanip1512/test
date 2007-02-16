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

//DBMGR implementation
#include  <rw/rwset.h>
#include  <rw/db/dbmgr.h>
#include  <rw/db/dbase.h>
#include  <rw/db/dbsrc/dbentry.h>

////////////////////////////////////////////////////////////////////////
//
// RWDBManagerProxy
// In case of multi threading the access to the static data of
// RWDBMangerProxy is guarded by:
//   * RWDBManager::database(),
//   * RWDBManager::add(),
//   * RWDBManagerProxyAnchor::RWDBManagerProxyAnchor()
//   * static RWDBManagerProxy::deregisterDb()
//
//
//////////////////////////////////////////////////////////////////////////

class  RWDBManagerProxy
{
friend class RWDBManager;
friend class RWDBManagerProxyAnchor;

public:
   ~RWDBManagerProxy();

private:
// helper functions for managing singleton
   static RWDBManagerProxy* instance();
   static void deallocate();

// helper functions from RWDBDatabaseImp
   void registerDb(RWDBDatabaseImp* db);
   static void deregisterDb();

   RWDBNewImpFunc findDatabaseImp(RWDBEntry* entry);
   RWDBNewImpFunc find(const RWCString& dbType, const RWCString& procName);
   RWDBEntry* add(const RWCString& dbType, const RWCString& procName, RWDBNewImpFunc fptr);
   RWDBEntry* addEntry(RWDBEntry*  fptr);

   RWDBManagerProxy();

   RWSet* entries_;
   static RWDBManagerProxy* instance_;
};

//////////////////////////////////////////////////////////////////////////
//
// RWDBManagerProxy
// Anchors a static object into this translation unit, that additonally
// to databaseImps controlls the destruction of RWDBManager::instance_
//
//////////////////////////////////////////////////////////////////////////
class RWDBManagerProxyAnchor
{
friend class RWDBManagerProxy;

public:
    RWDBManagerProxyAnchor();
    ~RWDBManagerProxyAnchor();

private:
    static int counter_;
};

