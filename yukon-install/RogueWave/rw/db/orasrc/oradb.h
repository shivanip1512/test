#ifndef __RWDB_ORADB_H__
#define __RWDB_ORADB_H__

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
 **************************************************************************
 *
 *  Oracle OCI Library implementation of RWDBDatabaseImp
 *
 **************************************************************************/


#include  <rw/db/dbsrc/dbasei.h>
#include  <rw/db/result.h>
#include  <rw/db/table.h>

#include  <rw/db/orasrc/orafutur.h>
#include  <rw/db/orasrc/orarestb.h>
#include  <rw/db/orasrc/oraenvh.h>

class RWDBAccessExport RWDBOracleDatabaseImp : public RWDBDatabaseImp {

friend class RWDBAccessExport RWDBOracleConnectionImp;

public:
    RWDBOracleDatabaseImp  ( const RWCString& serverName,
                             const RWCString& userName,
                             const RWCString& passWord,
                             const RWCString& dbName );
    ~RWDBOracleDatabaseImp ();

// producers
     RWDBDateVectorImp*     dateVectorImp(size_t size);
        RWDBConnectionImp*      connectionImp( RWDBConnection::ConnectionType type);
    RWDBCursorImp*          cursorImp( RWDBCursor::CursorType   type,
                                       RWDBCursor::CursorAccess access,
                                       const RWCString&         select,
                                       const RWDBConnection&    conn );
    RWDBCursorImp*          cursorImp( RWDBCursor::CursorType   type,
                                       RWDBCursor::CursorAccess access,
                                       const RWCString&         select,
                                       const RWDBSchema&        updateCols,
                                       const RWDBConnection&    conn );
    RWDBSelectorImp*        selectorImp( const RWDBDatabase&  dbase,
                                         const RWDBCriterion& crit );
    RWDBStoredProcImp*      storedProcImp( const RWDBDatabase&   dbase,
                                           const RWCString&      name,
                                           const RWDBStatus&     status,
                                           const RWDBConnection& conn );
    RWDBStoredProcImp*      storedProcImp( const RWDBDatabase&   dbase,
                                           const RWCString&      name,
                                           const RWDBStatus&     status,
                                           const RWDBConnection& conn,
                                           const RWDBSchema& schema );
    RWDBTableImp*           tableImp( const RWDBDatabase& dbase,
                                      const RWCString&    name );

    RWDBStatus              createTable( const RWCString&      name,
                                         const RWDBSchema&     schema,
                                         const RWDBConnection& conn );

    RWDBStatus              createView( const RWCString&      name,
                                        const RWCString&      sql,
                                        const RWDBConnection& conn );
    RWDBStatus              createView( const RWCString&        name,
                                        const RWDBSelectorBase& select,
                                        const RWDBConnection&   conn );
    RWDBStatus              createView( const RWCString&        name,
                                        const RWDBSchema&       columnList,
                                        const RWDBSelectorBase& select,
                                        const RWDBConnection&   conn );
    RWDBStatus              createView( const RWCString&      name,
                                        const RWDBSchema&     columnList,
                                        const RWCString&      sql,
                                        const RWDBConnection& conn );
    RWDBStatus              dropView( const RWCString&      name,
                                      const RWDBConnection& connection );

    RWDBStatus              createProcedure( const RWCString&      name,
                                             const RWCString&      sql,
                                             const RWDBSchema&     params,
                                             const RWDBConnection& con );
    RWDBStatus              createProcedure( const RWDBStoredProc& proc,
                                             const RWDBConnection& con );

    RWDBTable               dbTables(const RWDBConnection& conn,
                                     const RWCString& name,
                                     const RWCString& owner, int type);

    RWDBTable               dbStoredProcedures(const RWDBConnection& conn,
                                               const RWCString& name,
                                               const RWCString& owner);

// accessor methods
    RWDBEnvironmentHandle*  environmentHandle() const;

    const RWDBPhraseBook&   phraseBook() const;

    RWCString               version() const;
    static const char       version_[];

// helper methods
    static RWDBStatus       appendElement( const RWDBColumn&     column,
                                           const RWDBPhraseBook& phrase,
                                           RWCString&            statement );
    static RWDBStatus       appendParameter( const RWDBColumn&     column,
                                             const RWDBPhraseBook& phrase,
                                             RWCString&            statement );
    static RWDBStatus       rwdatatype( RWCString&            datatype,
                                        const RWDBColumn&     column,
                                        const RWDBPhraseBook& phrase );
    static RWDBStatus       oradatatype( RWCString&            datatype,
                                         const RWDBColumn&     column,
                                         const RWDBPhraseBook& phrase );
    static RWDBStatus       rwparamtype( RWCString&            datatype,
                                         const RWDBColumn&     column,
                                         const RWDBPhraseBook& phrase );
    static RWDBStatus       oraparamtype( RWCString&            datatype,
                                          const RWDBColumn&     column,
                                          const RWDBPhraseBook& phrase );

private:
    RWDBEnvironmentHandle*       environmentHandle_;

// not implemented
    RWDBOracleDatabaseImp  ( const RWDBOracleDatabaseImp& );
    RWDBOracleDatabaseImp&  operator=( const RWDBOracleDatabaseImp& );
};


class RWDBAccessExport RWDBOracleDbFutureImp : public RWDBOracleFutureImp{
public :
    RWDBOracleDbFutureImp( const RWDBConnection&        conn,
                           const RWDBStatus&            status,
                           RWDBOraOciCalls              oci,
                           short                        stages,
                           const RWDBResult&            result,
                           RWDBOracleResultTableBodyImp *tableImp
                         );
    ~RWDBOracleDbFutureImp();

protected :
    RWBoolean isDbReady();
    void      finishDbAsync();

private :
    void setTable();

    RWDBOracleResultTableBodyImp *tableBodyImp_;
    RWDBResult                   result_;
    RWBoolean                    tableDone_;
    RWDBTable                    table_;

    // Not Implemented
    RWDBOracleDbFutureImp( const RWDBOracleDbFutureImp& );
    RWDBOracleDbFutureImp& operator=( const RWDBOracleDbFutureImp& );
};

#endif

