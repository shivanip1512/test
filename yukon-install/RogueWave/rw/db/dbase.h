#ifndef __RWDB_DBASE_H__
#define __RWDB_DBASE_H__

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
 

#include    <rw/db/defs.h>
#include    <rw/db/status.h>
#include    <rw/db/cursor.h>
#include    <rw/db/connect.h>
#include    <rw/db/envhandl.h>
#include    <rw/db/dbdatevec.h>


//  This macro is used in preference to a static const member, owing
//  to difficulties some compilers have with static initialization
//  in shared libraries and DLL's. It is used as a default argument
//  for RWDBDatabase::dbTables
#define     rwdbAllTypes    SystemTable | UserTable | View | Synonym

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBDatabase
//                                                                     
//    A database object manages connections with a server.
//
//    A database object represents a server, a user on that server,
//    and a database opened for that user.  Database objects are created by
//    the global database manager.
//
//    Database provides an interface for tables, queries, and direct SQL
//    transactions.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBDatabase {

friend class RWDBManager;
friend class RWDBExport RWDBDatabaseHelper;

public:
    // Used with dbTables()
    enum { UserTable = 1, SystemTable = 2, View = 4, Synonym = 8 };

    // Constructors, destructor, operators
    RWDBDatabase              ();
    RWDBDatabase              ( const RWDBDatabase& dbase );
    RWDBDatabase              ( RWDBDatabaseImp* imp );
    virtual ~RWDBDatabase     ();
    RWDBDatabase&             operator=(const RWDBDatabase& dbase);

    RWBoolean                 operator==(const RWDBDatabase& dbase) const;
    RWBoolean                 operator!=(const RWDBDatabase& dbase) const;

    // Accessors
    size_t                    totalConnections() const;
    size_t                    defaultConnections() const;
    RWDBConnection            connection(
                                   RWDBConnection::ConnectionType type = 
                                   RWDBConnection::Synchronous ) const;

    RWBoolean                 autoCommit() const;
    const RWDBPhraseBook&     phraseBook() const;
    RWDBTracer&               tracer();
    RWCString::caseCompare    caseCompare() const;
    RWCString                 userName() const;
    RWCString                 passWord() const;
    RWCString                 dbName() const;
    RWCString                 serverName() const;
    RWCString                 serverType() const;

    RWCString                 version() const;

      // error handling support
    RWDBStatus                status() const;
    RWBoolean                 isValid() const;
    RWBoolean                 isReady() const;
    RWDBStatus::ErrorHandler  errorHandler() const;
    void                      setErrorHandler(RWDBStatus::ErrorHandler handler);

      // multi-threading support
    void                      acquire(void) const;
    void                      release(void) const;

    // Mutators
    size_t                    defaultConnections(size_t size);
    RWBoolean                 autoCommit(RWBoolean autoCommit);
    RWCString::caseCompare    caseCompare(RWCString::caseCompare cmp);
                                
    // Producers
    RWDBTable          table(const RWCString& name) const;
    RWDBMemTable       memTable(const RWCString& name,
                                size_t capacity = 0) const;
    RWDBMemTable       memTable(const RWCString& name,
                                const RWDBConnection& connection,
                                size_t capacity = 0) const;
    RWDBSelector       selector() const;
    RWDBSelector       selector(const RWDBCriterion& criterion) const;

    RWDBInserter       inserter(const RWDBTable& table) const;
    RWDBInserter       inserter(const RWDBTable& table,
                                const RWDBSelector& selector) const;
    RWDBInserter       inserter(const RWDBTable& table,
                                const RWDBCompoundSelector& selector) const;
    RWDBInserter       inserter(const RWDBTable& table,
                                const RWDBSchema& columnList) const;
    RWDBInserter       inserter(const RWDBTable& table,
                                const RWDBSelector& selector,
                                const RWDBSchema& columnList) const;
        RWDBDateVector     dateVector(size_t size) const;
    RWDBInserter       inserter(const RWDBTable& table,
                                const RWDBCompoundSelector& selector,
                                const RWDBSchema& columnList) const;

    RWDBDeleter        deleter(const RWDBTable& table) const;
    RWDBDeleter        deleter(const RWDBTable& table,
                               const RWDBCriterion& criterion) const;

    RWDBUpdater        updater(const RWDBTable& table) const;
    RWDBUpdater        updater(const RWDBTable& table,
                               const RWDBCriterion& criterion) const;

    // cursor variants w/o schema
    RWDBCursor         cursor(const RWCString& select,
                              const RWDBConnection& connection,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWCString& select,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWDBSelector& selector,
                              const RWDBConnection& connection,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWDBSelector& selector,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWDBCompoundSelector& selector,
                              const RWDBConnection& connection,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWDBCompoundSelector& selector,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;


    // cursor variants w schema
    RWDBCursor         cursor(const RWCString& select,
                              const RWDBSchema& updateCols,
                              const RWDBConnection& connection,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWCString& select,
                              const RWDBSchema& updateCols,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWDBSelector& selector,
                              const RWDBSchema& updateCols,
                              const RWDBConnection& connection,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWDBSelector& selector,
                              const RWDBSchema& updateCols,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWDBCompoundSelector& selector,
                              const RWDBSchema& updateCols,
                              const RWDBConnection& connection,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;
    RWDBCursor         cursor(const RWDBCompoundSelector& selector,
                              const RWDBSchema& updateCols,
                              RWDBCursor::CursorType type =
                                          RWDBCursor::Sequential,
                              RWDBCursor::CursorAccess access =
                                          RWDBCursor::Read) const;

    RWDBStoredProc     storedProc(const RWCString& name) const;
    RWDBStoredProc     storedProc(const RWCString& name,
                                  const RWDBConnection& connection) const;
    RWDBStoredProc     storedProc(const RWCString& name,
                                  const RWDBConnection& connection, const RWDBSchema&) const;
    RWDBStoredProc     storedProc(const RWCString& name,
                                  const RWDBSchema&) const;

    // Direct DDL Support
    RWDBStatus         createTable(const RWDBTable& table) const;
    RWDBStatus         createTable(const RWDBTable& table,
                                   const RWDBConnection& connection) const;
    RWDBStatus         createTable(const RWCString& name,
                                   const RWDBSchema& schema) const;
    RWDBStatus         createTable(const RWCString& name,
                                   const RWDBSchema& schema,
                                   const RWDBConnection& connection) const;
    RWDBStatus         createView(const RWCString& name,
                                  const RWDBSelectorBase& select) const;
    RWDBStatus         createView(const RWCString& name,
                                  const RWDBSchema& columnList,
                                  const RWDBSelectorBase& select) const;
    RWDBStatus         createView(const RWCString& name,
                                  const RWDBSelectorBase& select,
                                  const RWDBConnection& connection) const;
    RWDBStatus         createView(const RWCString& name,
                                  const RWDBSchema& columnList,
                                  const RWDBSelectorBase& select,
                                  const RWDBConnection& connection) const;
    RWDBStatus         createView(const RWCString& name,
                                  const RWCString& sql) const;
    RWDBStatus         createView(const RWCString& name,
                                  const RWDBSchema& columnList,
                                  const RWCString& sql) const;
    RWDBStatus         createView(const RWCString& name,
                                  const RWCString& sql,
                                  const RWDBConnection& connection) const;
    RWDBStatus         createView(const RWCString& name,
                                  const RWDBSchema& columnList,
                                  const RWCString& sql,
                                  const RWDBConnection& connection) const;
    RWDBStatus         createProcedure(const RWCString& name,
                                       const RWCString& sql) const;
    RWDBStatus         createProcedure(const RWCString& name,
                                       const RWCString& sql,
                                       const RWDBConnection& connection) const;
    RWDBStatus         createProcedure(const RWCString& name,
                                       const RWCString& sql,
                                       const RWDBSchema& params) const;
    RWDBStatus         createProcedure(const RWCString& name,
                                       const RWCString& sql,
                                       const RWDBSchema& params,
                                       const RWDBConnection& connection) const;
    RWDBStatus         createProcedure(const RWDBStoredProc& proc) const;
    RWDBStatus         createProcedure(const RWDBStoredProc& proc,
                                       const RWDBConnection& connection) const;

    RWDBStatus         dropView(const RWCString& name);
    RWDBStatus         dropView(const RWCString& name,
                                const RWDBConnection& connection);
    /*
    ** Not all compilers support default values for "complicated"
    ** arguments. These overloads mimic default values for CStrings.
    */
    RWDBTable          dbTables(int type = rwdbAllTypes) const;
    RWDBTable          dbTables(const RWCString& name,
                                      int type = rwdbAllTypes) const;
    RWDBTable          dbTables(const RWCString& name, const RWCString& owner,
                                      int type = rwdbAllTypes) const;
    RWDBTable          dbTables(const RWDBConnection& conn,
                                      int type = rwdbAllTypes) const;
    RWDBTable          dbTables(const RWDBConnection& conn,
                                const RWCString& name,
                                      int type = rwdbAllTypes) const;
    RWDBTable          dbTables(const RWDBConnection& conn,
                                const RWCString& name, const RWCString& owner,
                                      int type = rwdbAllTypes) const;

    RWDBTable          dbStoredProcedures() const;
    RWDBTable          dbStoredProcedures(const RWCString& name) const;
    RWDBTable          dbStoredProcedures(const RWCString& name, const RWCString& owner) const;
    RWDBTable          dbStoredProcedures(const RWDBConnection& conn) const;
    RWDBTable          dbStoredProcedures(const RWDBConnection& conn, const RWCString& name) const;
    RWDBTable          dbStoredProcedures(const RWDBConnection& conn, const RWCString& name, const RWCString& owner) const;

    // Direct Execution of SQL
    RWDBResult         executeSql(const RWCString& sqlProgram) const;
    RWDBResult         executeSql(const RWCString& sqlProgram,
                                  const RWDBConnection& connection) const;

    // Handle to the environment. Can be set before getting a connection
    RWDBEnvironmentHandle*    environmentHandle() const;

    // Connect to the server when producing this db object
    static void        connect(RWBoolean value);
 
private:
    RWDBDatabaseImp*   impl_;

    static RWBoolean   connect_;
};

#endif

