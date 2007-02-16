#ifndef __RWDB_TABLE_H__
#define __RWDB_TABLE_H__

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
 * RWDBTable represents a tablular collection of data whose physical
 * location is unknown.  The data may reside on disk (database table),
 * in memory (memory table), or be an SQL 'table expression' (result table).
 * Data is read from a table using RWDBReader.  Table also offers DDL
 * support (drop(), addColumn(), etc).
 *
 **************************************************************************/

#include <rw/db/bkread.h>
#include <rw/db/bkins.h>
#include <rw/db/defs.h>
#include <rw/db/column.h>
#include <rw/db/cursor.h>
#include <rw/db/forkey.h>

#include <rw/ordcltn.h>


////////////////////////////////////////////////////////////////////////////
//
// RWDBImpControl:: To faciltate construction of handles with out imps.
//     Made it a class to avoid differential treatment of enums by different
//     compilers
//
////////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBImpControl{

public :
    enum RWDBImpControlFlag { suppressDefaultImp };

    RWDBImpControl( RWDBImpControlFlag = suppressDefaultImp );
    ~RWDBImpControl();

protected:

private:
};


//////////////////////////////////////////////////////////////////////////
//
//  RWDBTable
//
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBTable
{
  public:
    RWDBTable();
    RWDBTable(const RWDBTable& table);
    RWDBTable(RWDBTableImp* imp);
    RWDBTable& operator=(const RWDBTable& table);
    virtual ~RWDBTable();

    // indexing
    RWDBColumn   column(size_t position) const;
    RWDBColumn   column(const RWCString& name ) const;
    RWDBColumn   column(const RWCString& name,
                         RWCString::caseCompare caseCompare) const;
    RWDBColumn   operator[](const RWCString& name) const;
    RWDBColumn   operator[](size_t           idx) const;

    size_t       index(const RWCString& name ) const;
    size_t       index(const RWCString&  name,
                        RWCString::caseCompare caseCompare) const;
    size_t       index(const RWDBColumn& aCol) const;

    // accessors
    RWDBDatabase database() const;
    RWCString    name() const;
    RWCString    tag() const;
    RWDBSchema   schema() const;
    size_t       numberOfColumns() const;

    // mutators
    RWDBTable&   name(const RWCString& name);
    RWDBTable&   tag(const RWCString& newtag);

      // multi-threading support
    void         acquire(void) const;
    void         release(void) const;

    // DDL support
    RWDBSchema   primaryKey(const RWDBConnection& ); 
    RWDBSchema   primaryKey(); 
    RWDBStatus   referredToBy(const RWDBConnection& conn, RWDBForeignKeyList& keyList); 
    RWDBStatus   referredToBy(RWDBForeignKeyList& keyList); 
    RWDBStatus   foreignKeys( const RWDBConnection& conn,
                              const RWCString& refName, RWDBForeignKeyList& keyList ); 

    RWDBStatus foreignKeys(const RWCString& refName, RWDBForeignKeyList& keyList); 

    RWBoolean            exists(RWBoolean forceLookup = FALSE);
    RWBoolean            exists(const RWDBConnection& conn,
                                RWBoolean forceLookup = FALSE);
    virtual RWBoolean    fetchSchema();
    virtual RWBoolean    fetchSchema(const RWDBConnection& conn);
    RWBoolean            isView() const;
    RWBoolean            isView(const RWDBConnection& conn) const;
    RWDBStatus           grant(const RWCString& priv, const RWCString& user );
    RWDBStatus           grant(const RWCString& priv, const RWCString& user,
                               const RWDBConnection& conn );
    RWDBStatus           grant(const RWCString& priv, const RWDBSchema& colList,
                               const RWCString& user);
    RWDBStatus           grant(const RWCString& priv, const RWDBSchema& colList,
                               const RWCString& user,
                               const RWDBConnection& conn);
    RWDBStatus           revoke(const RWCString& priv, const RWCString& user);
    RWDBStatus           revoke(const RWCString& priv, const RWCString& user,
                                const RWDBConnection& conn );
    RWDBStatus           revoke(const RWCString& priv,
                                const RWDBSchema& colList,
                                const RWCString& user);
    RWDBStatus           revoke(const RWCString& priv,
                                const RWDBSchema& colList,
                                const RWCString& user,
                                const RWDBConnection& conn );
    RWDBStatus           addColumn(const RWDBColumn& aCol);
    RWDBStatus           addColumn(const RWDBColumn& aCol,
                                   const RWDBConnection& conn );
    RWDBStatus           addColumn(const RWCString& name,
                            RWDBValue::ValueType  type = RWDBValue::NoType,
                            long                  storageLength = 0,
                            int                   nativeType = -1,
                            int                   precision = -1, 
                            int                   scale = -1,
                            RWBoolean             nullAllowed = TRUE,
                            RWDBColumn::ParamType paramType =
                                                  RWDBColumn::notAParameter);
    RWDBStatus          addColumn(const RWCString& name,
                            const RWDBConnection& conn,
                            RWDBValue::ValueType  type = RWDBValue::NoType,
                            long                  storageLength = 0, 
                            int                   nativeType = -1,
                            int                   precision = -1, 
                            int                   scale = -1,
                            RWBoolean             nullAllowed = TRUE,
                            RWDBColumn::ParamType paramType =
                                                  RWDBColumn::notAParameter);
    RWDBStatus          dropColumn(const RWDBColumn& aCol);
    RWDBStatus          dropColumn(const RWDBColumn& aCol,
                             const RWDBConnection& conn);
    RWDBStatus          createIndex(const RWCString& name,
                                    const RWDBSchema& columns,
                                    RWBoolean unique = TRUE,
                                    RWBoolean clustered = TRUE );
    RWDBStatus          createIndex(const RWCString& name,
                                    const RWDBSchema& columns,
                                    const RWDBConnection& conn,
                                    RWBoolean unique = TRUE,
                                    RWBoolean clustered = TRUE);
    RWDBStatus          drop();
    RWDBStatus          drop(const RWDBConnection& conn);
    RWDBStatus          dropIndex(const RWCString& name);
    RWDBStatus          dropIndex(const RWCString& name,
                                  const RWDBConnection& conn);
    // error handling
    void                     setErrorHandler(RWDBStatus::ErrorHandler handler);
    RWDBStatus::ErrorHandler errorHandler() const;
    RWBoolean                isValid() const;
    RWBoolean                isReady() const;
    RWDBStatus               status() const;

    // utilities
    operator     void*() const;
    RWBoolean    isEquivalent(const RWDBTable& table) const;

    // producers
    RWDBDeleter  deleter() const;
    RWDBDeleter  deleter(const RWDBCriterion& criterion) const;
    RWDBUpdater  updater() const;
    RWDBUpdater  updater(const RWDBCriterion& criterion) const;
    RWDBInserter inserter() const;
    RWDBInserter inserter(const RWDBSelector& selector) const;
    RWDBInserter inserter(const RWDBCompoundSelector& selector) const;
    RWDBInserter inserter(const RWDBSchema& columnList) const;
    RWDBInserter inserter(const RWDBSelector& selector,
                          const RWDBSchema& columnList) const;
    RWDBInserter inserter(const RWDBCompoundSelector& selector,
                          const RWDBSchema& columnList) const;
    RWDBReader   reader() const;
    RWDBReader   reader(const RWDBConnection& conn) const;

    RWDBBulkReader   bulkReader(const RWDBConnection& conn) const;
    RWDBBulkInserter bulkInserter(const RWDBConnection& conn) const;

    RWDBCursor   cursor(RWDBCursor::CursorType type = RWDBCursor::Sequential,
                        RWDBCursor::CursorAccess access =
                                         RWDBCursor::Read) const;
    RWDBCursor   cursor(const RWDBConnection& conn,
                        RWDBCursor::CursorType type = RWDBCursor::Sequential,
                        RWDBCursor::CursorAccess access =
                                         RWDBCursor::Read) const;
    RWDBCursor   cursor(const RWDBSchema& updateCols,
                        RWDBCursor::CursorType type = RWDBCursor::Sequential,
                        RWDBCursor::CursorAccess access =
                                         RWDBCursor::Read) const;
    RWDBCursor   cursor(const RWDBSchema& updateCols,
                        const RWDBConnection& conn,
                        RWDBCursor::CursorType type = RWDBCursor::Sequential,
                        RWDBCursor::CursorAccess access =
                                         RWDBCursor::Read) const;

  protected:
    RWDBTableImp* impl_;

    RWDBTable ( const RWDBImpControl& );
    RWDBTable (const RWDBDatabase&, const RWCString&);

    friend class RWDBTableHelper;

};




#endif

