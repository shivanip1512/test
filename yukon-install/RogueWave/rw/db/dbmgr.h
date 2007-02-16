#ifndef __RWDB_DBMGR_H__
#define __RWDB_DBMGR_H__


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

#include  <rw/db/defs.h>
#include  <rw/db/status.h>


#define RWDBNEWIMPARGS  const RWCString&, const RWCString&, const RWCString&,\
                        const RWCString&, const RWCString&, const RWCString&

#if defined(__OS2__)
extern "C" {
#endif
typedef RWDBDatabaseImp* (rwdbfar *RWDBNewImpFunc)(RWDBNEWIMPARGS);
#if defined(__OS2__)
}
#endif


//////////////////////////////////////////////////////////////////////////
//
// RWDBManager
//
//      The RWDBManager fills requests for databases. Supplied with
//      server type and login information, the manager arranges to supply
//      the correct implementation.
//
//      The library contains a single, global instance of RWDBManager.
//      Applications ought not to instantiate additional RWDBManager
//      instances (though doing so should be harmless enough).
//
//      The manager maintains a collection of database implementations
//      known to be active.  It also orchestrates dynamic linking & loading
//      by interacting with the native operating system, and initializes/
//      deinitializes the persistence mechanism used by certain Tools.h++
//      classes.
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBManager {
public:
  static RWDBDatabase             database(const RWCString& serverType,
                                           const RWCString& serverName,
                                           const RWCString& userName,
                                           const RWCString& password,
                                           const RWCString& databaseName,
                                           const RWCString& role,
                                           const RWCString& connectString);
  static RWDBDatabase             database(const RWCString& serverType,
                                           const RWCString& serverName,
                                           const RWCString& userName,
                                           const RWCString& password,
                                           const RWCString& databaseName,
                                           const RWCString& role);
  static RWDBDatabase             database(const RWCString& serverType,
                                           const RWCString& serverName,
                                           const RWCString& userName,
                                           const RWCString& password,
                                           const RWCString& databaseName);

  static RWDBStatus::ErrorHandler setErrorHandler(
                                           RWDBStatus::ErrorHandler handler);
  static RWDBStatus::ErrorHandler errorHandler();

  static RWCString        version();

protected:
  static void             getConnection(const RWDBDatabase& db);
  static void             registerDatabase(const RWDBDatabase& db);
  static RWDBEntry*       add(const RWCString& serverType,
                                  RWDBNewImpFunc fptr = 0);

  static RWDBEntry*       add( RWDBEntry* entry);

  static RWDBNewImpFunc   find(const RWCString& serverType, 
                               const RWCString& procName);
private:
  static RWDBStatus::ErrorHandler handleErrorHandler
                       (RWBoolean get, RWDBStatus::ErrorHandler newHandler);
};

#endif
