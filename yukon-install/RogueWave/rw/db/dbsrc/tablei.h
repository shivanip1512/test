#ifndef __RWDB_TABLEI_H__
#define __RWDB_TABLEI_H__

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
#include <rw/db/dbref.h>
#include <rw/db/status.h>

#include <rw/db/dbase.h>
#include <rw/db/forkey.h>
#include <rw/db/schema.h>

#include <rw/db/dbsrc/taggen.h>


//////////////////////////////////////////////////////////////////////////
//
//  RWDBTableImp
//
//////////////////////////////////////////////////////////////////////////
class RWDBBulkInserterImp;
class RWDBBulkReaderImp;

class RWDBExport RWDBTableImp : public RWDBReference, public RWDBStatus
{
public:
   RWDBTableImp( const RWDBStatus& status );
   RWDBTableImp( const RWDBStatus& status, const RWDBDatabase& db,
                 const RWCString& name );
   virtual ~RWDBTableImp();

   // indexing
   virtual RWDBColumn     column(size_t position) const;
   virtual RWDBColumn     column(const RWCString& name) const;
   virtual RWDBColumn     column(const RWCString& name,
                                 RWCString::caseCompare caseCompare) const;
   virtual size_t         index(const RWCString& name) const;
   virtual size_t         index(const RWCString& name,
                                RWCString::caseCompare caseCompare) const;
   virtual size_t         index(const RWDBColumn& column) const;

   // accessors
   virtual size_t         numberOfColumns() const;
   virtual RWDBDatabase   database() const;
   virtual RWCString      name() const;
   virtual RWCString      tag() const;
   virtual RWDBSchema     schema();
 
   // mutators
   virtual void           name(const RWCString&);
   virtual void           tag(const RWCString& newtag);

   // DDL support
   virtual RWDBStatus     referredToBy( const RWDBConnection& conn,
                                              RWDBForeignKeyList& keyList ); 

   virtual RWDBStatus     foreignKeys(const RWCString& refName,
                                      const RWDBConnection& conn, 
                                            RWDBForeignKeyList& keyList  ); 

   virtual RWDBSchema     primaryKey(const RWDBConnection& ); 
   virtual RWBoolean      existsAllocateConnection(
                                      RWBoolean forceLookup = FALSE);
   virtual RWBoolean      exists(const RWDBConnection& connection,
                                      RWBoolean forceLookup = FALSE);
   virtual RWBoolean      isViewAllocateConnection();
   virtual RWBoolean      isView(const RWDBConnection& conn);

   virtual RWDBStatus     addColumn(const RWDBColumn& aCol,
                                    const RWDBConnection& conn);

   virtual RWDBStatus     dropColumn(const RWDBColumn& aCol,
                                     const RWDBConnection& conn);

   virtual RWDBStatus     privilege(const RWCString& command,
                                    const RWCString& priv,
                                    const RWDBSchema& schema,
                                    const RWCString& user,
                                    const RWDBConnection& conn);

   virtual RWDBStatus     createIndex(const RWCString& name,
                                      const RWDBSchema& columns,
                                      const RWDBConnection& connection,
                                      RWBoolean unique = TRUE,
                                      RWBoolean clustered = TRUE);

   virtual RWDBStatus     drop(const RWDBConnection& connection);
   virtual RWDBStatus     dropIndex(const RWCString& indexName,
                                    const RWDBConnection& connection);

   // producers
   virtual RWDBDeleterImp *    deleterImp(const RWDBTable&) const;
   virtual RWDBDeleterImp *    deleterImp(const RWDBTable&,
                                  const RWDBCriterion& criterion) const;
   virtual RWDBUpdaterImp *    updaterImp(const RWDBTable&) const;
   virtual RWDBUpdaterImp *    updaterImp(const RWDBTable&,
                                  const RWDBCriterion& criterion) const;
   virtual RWDBInserterImp *   inserterImp(const RWDBTable&) const;
   virtual RWDBInserterImp *   inserterImp(const RWDBTable&,
                                   const RWDBSelector&) const;
   virtual RWDBInserterImp *   inserterImp(const RWDBTable&,
                                   const RWDBCompoundSelector&) const;
   virtual RWDBInserterImp *   inserterImp(const RWDBTable&,
                                   const RWDBSchema&) const;
   virtual RWDBInserterImp *   inserterImp(const RWDBTable&,
                                   const RWDBSelector&,
                                   const RWDBSchema&) const;
   virtual RWDBInserterImp *   inserterImp(const RWDBTable&,
                                   const RWDBCompoundSelector&,
                                   const RWDBSchema&) const;
   
   virtual RWDBBulkInserterImp* bulkInserterImp(const RWDBTable&, const RWDBConnection& conn);
   virtual RWDBBulkReaderImp* bulkReaderImp(const RWDBTable&, const RWDBConnection&);
   
   virtual RWDBReaderImp *     readerImp(const RWDBTable&);
   virtual RWDBReaderImp *     readerImp(const RWDBTable&,
                                         const RWDBConnection&);
   virtual RWDBCursorImp *     cursorImp(const RWDBConnection& connection,
                                 RWDBCursor::CursorType type,
                                 RWDBCursor::CursorAccess access);
   virtual RWDBCursorImp *     cursorImp(const RWDBSchema& updateCols,
                                 const RWDBConnection& connection,
                                 RWDBCursor::CursorType type,
                                 RWDBCursor::CursorAccess access);

protected:
    RWDBTagGenerator&          tagGenerator();

    RWDBConnection        connection() const;

    RWCString             tag_;
    RWCString             name_;
    RWDBDatabase          parentDatabase_;
    RWDBSchema            schema_;

private:
// not implemented
    RWDBTableImp( const RWDBTableImp& );
    RWDBTableImp& operator=( const RWDBTableImp& );

    friend class RWDBTableHelper;

};

class RWDBExport RWDBTableHelper {

  public:

    static RWDBTableImp *getImp(const RWDBTable&);

};


#endif

