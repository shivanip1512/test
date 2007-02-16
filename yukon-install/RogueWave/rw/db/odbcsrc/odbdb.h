#ifndef __RWDB_ODBDB_H__
#define __RWDB_ODBDB_H__

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
 ***************************************************************************
 *
 * ODBC access library implementation of RWDBCursorImp
 *
 **************************************************************************/

#include <rw/db/odbcsrc/odbdefs.h>
#include <rw/db/odbcsrc/odbhstmt.h>
#include <rw/db/odbcsrc/odbenvh.h>
#include <rw/db/odbcsrc/odbutils.h>

#include <rw/db/dbsrc/dbasei.h>


class RWDBAccessExport RWDBODBCLibDatabaseImp  : public RWDBDatabaseImp {
public:
    RWDBODBCLibDatabaseImp( const RWCString& serverName,
                            const RWCString& userName,
                            const RWCString& passWord,
                            const RWCString& dbName );
protected:
            RWDBODBCLibDatabaseImp( const RWCString& serverType,       // ADDED FOR VELOCIS SUBCLASS
                                   const RWCString& serverName, 
                            const RWCString& userName,
                            const RWCString& passWord,
                            const RWCString& dbName );
public:
   ~RWDBODBCLibDatabaseImp() ;

   void                 initialize();
   RWDBDateVectorImp*   dateVectorImp(size_t size);
   RWDBTableImp*        tableImp( const RWDBDatabase& dbase,
                                  const RWCString& name);
   RWDBCursorImp*       cursorImp( RWDBCursor::CursorType   type,
                                   RWDBCursor::CursorAccess access,
                                   const RWCString&         select,
                                   const RWDBConnection&    connection);
  RWDBCursorImp*        cursorImp( RWDBCursor::CursorType   type,
                                   RWDBCursor::CursorAccess access,
                                   const RWCString&         select,
                                   const RWDBSchema& schema,
                                   const RWDBConnection&    connection);
//   RWDBResultImp*       executeSql( const RWCString& sqlProgram,
//                                   const RWDBConnection& conn);
   RWDBSelectorImp*     selectorImp( const RWDBDatabase& dbase,
                                     const RWDBCriterion& crit);
   RWDBConnectionImp*   connectionImp( RWDBConnection::ConnectionType type );
   RWDBStoredProcImp*   storedProcImp( const RWDBDatabase& dbase,
                                       const RWCString& name,
                                       const RWDBStatus& status,
                                       const RWDBConnection& conn);
   RWDBStoredProcImp*   storedProcImp( const RWDBDatabase& dbase,
                                       const RWCString& name,
                                       const RWDBStatus& status,
                                       const RWDBConnection& conn,
                                       const RWDBSchema& schema);
    RWDBStatus          createView( const RWCString& name,
                                    const RWDBSelectorBase& select,
                                    const RWDBConnection& connection);
    RWDBStatus          createView( const RWCString& name,
                                    const RWDBSchema& columnList,
                                    const RWDBSelectorBase& select,
                                    const RWDBConnection& connection);
    RWDBStatus          createView( const RWCString& name,
                                    const RWCString& sql,
                                    const RWDBConnection& connection);
    RWDBStatus          createView( const RWCString& name,
                                    const RWDBSchema& columnList,
                                    const RWCString& sql,
                                    const RWDBConnection& connection);
   RWDBStatus           createProcedure( const RWDBStoredProc& proc,
                                         const RWDBConnection& connection);
   RWDBStatus           createProcedure( const RWCString&,
                                         const RWCString&,
                                         const RWDBSchema&,
                                         const RWDBConnection&);
   RWDBStatus           dropView( const RWCString& name,
                                  const RWDBConnection& connection);

    RWDBTable           dbTables( const RWDBConnection& conn,
                                  const RWCString& name,
                                  const RWCString& owner, int type);
    RWDBTable           dbStoredProcedures( const RWDBConnection& conn,
                                            const RWCString& name,
                                            const RWCString& owner);
   const RWDBPhraseBook& phraseBook() const;
   RWDBHENV                 henv();
//   void                 cleanup();
   RWCString            version() const;
   RWDBResultImp*       do_executeSql( const RWCString& sqlProgram,
                                       const RWDBConnection& conn,
                                       RWDBODBCLibHSTMT hstmt,
                                       RWBoolean* needsData );
// Info about the database capabilities.
   int                  caseIdentifier();
   RWBoolean            supportsUserName( const RWDBConnection& conn );
   RWBoolean            supportsQualifier( const RWDBConnection& conn );
   RWBoolean            supportsTableName( const RWDBConnection& conn ) const;
   RWBoolean            supportsProcedureName( const RWDBConnection& conn ) const;
   RWBoolean            nonNullableColumns( const RWDBConnection& conn);
   static RWBoolean     supportsODBCType( SWORD anODBCType,
                                          const RWCString& servername);

   void                 setCaseSensitive(RWDBHDBC aHdbc);
   RWBoolean            caseSensitivityInfoRetrieved();
   RWBoolean            typeInfoLoaded() const;
   void                 typeInfoLoaded(const RWBoolean& tinfo);
   static  RWDBHashDictionary& nameToTypeInfoListHandler();  // Keyed by serverName(),
   RWDBEnvironmentHandle*       environmentHandle() const;
                                    // valued by a list of RWDBODBCLibTypeInfo.

   void                insertInTypeInfoList(const RWDBConnection& conn);
   void                insertInTypeInfoList(RWDBHDBC hdbc);
private:
    int                caseIdentifier_;
    RWBoolean          isInitialized_;
    static const char   version_[];

    RWDBHENV                theHenv_;
    void                insertInTypeInfoList();
    int                 getTypeInfo(const RWDBHDBC& hdbc, RWOrdered& aVec);

    RWDBStatus          createTable( const RWCString&, const RWDBSchema&,
                                     const RWDBConnection&);
    RWDBEnvironmentHandle*      environmentHandle_;
    RWBoolean           caseSensitivity_;
    RWBoolean           typeInfoLoaded_;
        RWCString               serverType_;

};

extern "C" {
RWDBNEWDBPROCDECL ( const RWCString& serverName,
                    const RWCString& userName,
                    const RWCString& passWord,
                    const RWCString& dbName,
                    const RWCString& role,
                    const RWCString& connectString );
};
#endif








