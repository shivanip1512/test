#ifndef __RWDB_ODBTAB_H__
#define __RWDB_ODBTAB_H__

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
 * ODBC access library implementation of RWDBDatabaseTableImp
 *
 **************************************************************************/

#include <rw/db/odbcsrc/odbdefs.h>
#include <rw/db/odbcsrc/odbkread.h>
#include <rw/db/dbsrc/dbtablei.h>


class RWDBAccessExport RWDBODBCLibDatabaseTableImp : public RWDBDatabaseTableImp {
public:
   RWDBODBCLibDatabaseTableImp(const RWDBStatus& status,
                               const RWDBDatabase& dbase,
                               const RWCString& name,
                               int caseIdentifier);
   ~RWDBODBCLibDatabaseTableImp();

   RWBoolean exists(const RWDBConnection& connection,
                           RWBoolean forceLookup);

   RWDBSchema primaryKey(const RWDBConnection& conn);
   RWDBStatus referredToBy( const RWDBConnection& conn, RWDBForeignKeyList& keyList);

   RWDBStatus foreignKeys(const RWCString& refName,
                          const RWDBConnection& conn, RWDBForeignKeyList& keyList);

   RWDBStatus drop(const RWDBConnection& connection);
   RWDBStatus addColumn(const RWDBColumn& aCol, const RWDBConnection& aConn);
   RWDBStatus dropColumn(const RWDBColumn&, const RWDBConnection&);
   RWDBBulkInserterImp* bulkInserterImp(const RWDBTable& tab, const RWDBConnection& conn);

   RWDBBulkReaderImp* bulkReaderImp(const RWDBTable&, const RWDBConnection&);
   RWDBStatus privilege (const RWCString& command, const RWCString& priv,
                          const RWDBSchema& schema, const RWCString& user,
                          const RWDBConnection& aConn);

   RWDBInserterImp * inserterImp(const RWDBTable& table) const;
   RWDBInserterImp * inserterImp(const RWDBTable& table,
                         const RWDBSelector& selector) const;
   RWDBInserterImp * inserterImp(const RWDBTable& table,
                         const RWDBCompoundSelector& selector) const;
   RWDBInserterImp * inserterImp(const RWDBTable& table,
                         const RWDBSchema& colList) const;
   RWDBInserterImp * inserterImp(const RWDBTable& table,
                         const RWDBSelector& selector,
                         const RWDBSchema& colList) const;
   RWDBInserterImp * inserterImp(const RWDBTable& table,
                         const RWDBCompoundSelector& selector,
                         const RWDBSchema& colList) const;

   RWDBCursorImp * cursorImp(const RWDBConnection& connection,
                           RWDBCursor::CursorType type,
                           RWDBCursor::CursorAccess access);
   RWDBCursorImp * cursorImp(const RWDBSchema& sch,
                     const RWDBConnection& connection,
                           RWDBCursor::CursorType type,
                           RWDBCursor::CursorAccess access);

   RWDBUpdaterImp * updaterImp(const RWDBTable& theTable) const;
   RWDBUpdaterImp * updaterImp(const RWDBTable& theTable,
                       const RWDBCriterion& theCrit) const;

   RWDBDeleterImp * deleterImp(const RWDBTable& table) const;
   RWDBDeleterImp * deleterImp(const RWDBTable& table,
                       const RWDBCriterion& criterion) const;

   RWDBStatus dropIndex(const RWCString& idxName,
                        const RWDBConnection& connection);
   RWDBStatus createIndex(const RWCString& indexName,
                          const RWDBSchema& columns,
                          const RWDBConnection& connection,
                                RWBoolean unique,
                                RWBoolean clustered);
private:

//  int fetchSchema(const char *tableName, RWDBHSTMT hstmt);
  int fetchSchema(const RWDBConnection& conn, const char *tableName, RWDBHSTMT hstmt);
  RWBoolean hasBeenLookedUp_;
  int caseIdentifier_;
    // Not Implemented:
  RWDBODBCLibDatabaseTableImp(const RWDBODBCLibDatabaseTableImp&);
  RWDBODBCLibDatabaseTableImp& operator=(const RWDBODBCLibDatabaseTableImp&);

};

#endif


