#ifndef __RWDB_DBTABLEI_H__
#define __RWDB_DBTABLEI_H__

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
 * Definition of RWDBDatabaseTableImp
 *
 *     RWDBDatabaseTableImp is a base class for a family of database-
 *     specific database table implementations.
 *
 **************************************************************************/

#include <rw/db/dbsrc/bkinsi.h>

#include <rw/db/dbsrc/tablei.h>


//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBDatabaseTableImp : public RWDBTableImp {
public:
    RWDBDatabaseTableImp( const RWDBStatus& status,
                          const RWDBDatabase& db, 
                          const RWCString& name );
    virtual ~RWDBDatabaseTableImp();

    virtual RWBoolean    existsAllocateConnection(
                                 RWBoolean forceLookup = FALSE );
    virtual RWBoolean    exists(const RWDBConnection& connection,
                                RWBoolean forceLookup = FALSE);
    virtual RWBoolean    isViewAllocateConnection();
    virtual RWBoolean    isView(const RWDBConnection& connection);

    virtual RWDBStatus   privilege(const RWCString& command,
                                   const RWCString& priv,
                                   const RWDBSchema& schema,
                                   const RWCString& user,
                                   const RWDBConnection& conn);

    virtual RWDBStatus   addColumn(const RWDBColumn& aCol,
                                   const RWDBConnection& aConn);
    virtual RWDBStatus   dropColumn(const RWDBColumn& aCol,
                                    const RWDBConnection& conn);
    virtual RWDBBulkInserterImp* bulkInserterImp(const RWDBTable&, const RWDBConnection& conn);
    virtual RWDBDeleterImp *  deleterImp(const RWDBTable&) const;
    virtual RWDBDeleterImp *  deleterImp(const RWDBTable&,
                                         const RWDBCriterion&) const;
    virtual RWDBUpdaterImp *  updaterImp(const RWDBTable&) const;
    virtual RWDBUpdaterImp *  updaterImp(const RWDBTable&,
                                         const RWDBCriterion&) const;
    virtual RWDBInserterImp * inserterImp(const RWDBTable&) const;
    virtual RWDBInserterImp * inserterImp(const RWDBTable&,
                                          const RWDBSelector&) const;
    virtual RWDBInserterImp * inserterImp(const RWDBTable&,
                                  const RWDBCompoundSelector&) const;
    virtual RWDBInserterImp * inserterImp(const RWDBTable&,
                                  const RWDBSchema&) const;
    virtual RWDBInserterImp * inserterImp(const RWDBTable&, const RWDBSelector&,
                                  const RWDBSchema&) const;
    virtual RWDBInserterImp * inserterImp(const RWDBTable&,
                                  const RWDBCompoundSelector&,
                                  const RWDBSchema&) const;
    virtual RWDBReaderImp *   readerImp(const RWDBTable&);
    virtual RWDBReaderImp *   readerImp(const RWDBTable&,
                                        const RWDBConnection&);
    virtual RWDBCursorImp *   cursorImp(const RWDBConnection& connection,
                                RWDBCursor::CursorType type,
                                RWDBCursor::CursorAccess access);
    virtual RWDBCursorImp *   cursorImp(const RWDBSchema& updateCols,
                                const RWDBConnection& connection,
                                RWDBCursor::CursorType type,
                                RWDBCursor::CursorAccess access);
protected:
    RWBoolean            schemaFetched_;
    RWBoolean            isView_;

private:
// not implemented
    RWDBDatabaseTableImp( const RWDBDatabaseTableImp& );
    RWDBDatabaseTableImp& operator=( const RWDBDatabaseTableImp& );
};

#endif
