#ifndef __RWDB_ORATAB_H__
#define __RWDB_ORATAB_H__

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
 * Oracle OCI Access Library definition of RWDBDatabaseTableImp
 *
 **************************************************************************/


#include  <rw/db/dbsrc/dbtablei.h>
#include  <rw/db/orasrc/oraname.h>


class RWDBAccessExport RWDBOracleDatabaseTableImp : public RWDBDatabaseTableImp {
public:
    RWDBOracleDatabaseTableImp  (const RWDBDatabase& dbase,
                                 const RWCString& name);
    RWDBOracleDatabaseTableImp  (const RWDBStatus& status,
                                 const RWDBDatabase& dbase,
                                 const RWCString& name);
    ~RWDBOracleDatabaseTableImp ();

    RWDBStatus           status();
    RWBoolean            exists(const RWDBConnection& conn,
                                RWBoolean forceLookup );
    RWDBStatus           privilege(const RWCString& command,
                                   const RWCString& priv,
                                   const RWDBSchema& schema,
                                   const RWCString& user,
                                   const RWDBConnection& conn);
    RWDBStatus           createIndex(const RWCString& name,
                                     const RWDBSchema& columns,
                                     const RWDBConnection& conn,
                                     RWBoolean unique,
                                     RWBoolean clustered);
    RWDBSchema           primaryKey(const RWDBConnection& ); 
    RWDBStatus           referredToBy( const RWDBConnection& conn, 
                                             RWDBForeignKeyList& keyList);

    RWDBStatus           foreignKeys(const RWCString& targetTableName, 
                                     const RWDBConnection& conn,
                                           RWDBForeignKeyList& keyList );

    RWDBStatus           foreignKeysFromView( const RWCString& pKeyName, 
                                            const RWDBConnection& conn, 
                                         RWDBForeignKeyList& keyList);

    RWDBStatus           drop(const RWDBConnection& conn);
    RWDBStatus           dropIndex(const RWCString& indexName,
                                   const RWDBConnection& conn);
    RWDBStatus           addColumn(const RWDBColumn& column,
                                   const RWDBConnection& conn);
    RWDBStatus           dropColumn(const RWDBColumn& column,
                                    const RWDBConnection& conn);
      // producers
    RWDBInserterImp *    inserterImp(const RWDBTable& table) const;
    RWDBInserterImp *    inserterImp(const RWDBTable& table,
                                  const RWDBSelector& selector) const;
    RWDBInserterImp *    inserterImp(const RWDBTable& table,
                                  const RWDBCompoundSelector& selector) const;
    RWDBInserterImp *    inserterImp(const RWDBTable& table,
                                  const RWDBSchema& columnList) const;
    RWDBInserterImp *    inserterImp(const RWDBTable& table,
                                  const RWDBSelector& selector,
                                  const RWDBSchema& columnList) const;
    RWDBInserterImp *    inserterImp(const RWDBTable& table,
                                  const RWDBCompoundSelector& selector,
                                  const RWDBSchema& columnList) const;
    RWDBDeleterImp *     deleterImp(const RWDBTable& table) const;
    RWDBDeleterImp *     deleterImp(const RWDBTable& table,
                                 const RWDBCriterion& crit) const;
    RWDBUpdaterImp *     updaterImp(const RWDBTable& table) const;
    RWDBUpdaterImp *     updaterImp(const RWDBTable& table,
                                  const RWDBCriterion& crit) const;
    
        RWDBBulkReaderImp*   bulkReaderImp(const RWDBTable& tab, const RWDBConnection& conn);
    RWDBBulkInserterImp* bulkInserterImp(const RWDBTable& tab, const RWDBConnection& conn);

private:
    RWBoolean            locate( const RWCString& userName,
                                 const RWCString& ownerName,
                                 const RWCString& objectName,
                                 const RWDBConnection connection,
                                       RWSet& recurCheck );
    RWBoolean            getSchema( const RWCString& ownerName,
                                    const RWCString& objectName,
                                    const RWDBConnection connection );
    RWBoolean            translateSynonym(const RWCString& userName,
                                          const RWCString& objName,
                                                RWCString& tranOwner,
                                                RWCString& tranName,
                                          const RWDBConnection& connection);
        static RWBoolean createdForeignKeyView_;
    static RWBoolean createdForeignKeyProc_;
// not implemented
    RWDBOracleDatabaseTableImp  (const RWDBOracleDatabaseTableImp&);
    RWDBOracleDatabaseTableImp&  operator=(const RWDBOracleDatabaseTableImp&);
};

#endif

