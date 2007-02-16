#ifndef __RWDB_DBASEI_H__
#define __RWDB_DBASEI_H__

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

#ifdef _MSC_VER
#pragma warning( disable : 4251)
#endif

#include <rw/gdlist.h>

#include <rw/db/dbref.h>
#include <rw/db/status.h>
#include <rw/db/cursor.h>
#include <rw/db/connect.h>


declare(RWGDlist, RWDBConnectionImp)
typedef RWGDlist(RWDBConnectionImp) RWDBConnectionList;

//////////////////////////////////////////////////////////////////////////
//                                                                      
// Externs
//                                                                     
//////////////////////////////////////////////////////////////////////////
extern "C" {
#ifndef RW_TRAILING_RWEXPORT
rwdbexport RWDBDatabaseImp*
#else
RWDBDatabaseImp* rwdbexport
#endif
newNotFounddbDatabaseImp(
                const RWCString&,
                const RWCString&,
                const RWCString&,
                const RWCString& 
            );
}

class RWDBExport RWDBDateVectorImp;
//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBDatabaseImp
//
//    A DatabaseImp is a base class for a family of database
//    implementations. Services requested from a database are forwarded
//    to its implementation, which may either provide them directly
//    or through its derived class.
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBDatabaseImp : public RWDBReference, public RWDBStatus {
friend class RWDBExport RWDBDatabase;
friend class  RWDBManagerProxy;
public:
    RWDBDatabaseImp              ( const RWDBStatus& status );
    RWDBDatabaseImp              ( const RWDBStatus& status,
                                   const RWCString& serverType,
                                   const RWCString& serverName,
                                   const RWCString& userName,
                                   const RWCString& passWord,
                                   const RWCString& dbName );
    virtual ~RWDBDatabaseImp     ();
    virtual void                 cleanup();


    // connection management
    virtual RWDBConnectionImp*   addConnectionImp(RWDBConnectionImp* imp);
    virtual void                 removeConnectionImp(RWDBConnectionImp* imp);
    virtual RWDBConnectionImp*   getFromConnectionPool(
                                       RWDBConnection::ConnectionType);

    // accessor methods
    virtual size_t               totalConnections() const;
    virtual size_t               defaultConnections() const;
    virtual RWCString::caseCompare caseCompare() const;
    virtual RWDBEnvironmentHandle* environmentHandle() const;
    virtual const RWDBPhraseBook&  phraseBook() const;
    virtual RWDBStatus             status() const;
    virtual RWCString              version() const;

    virtual RWCString              serverType() const;
    virtual RWCString              serverName() const;
    virtual RWCString              userName() const;
    virtual RWCString              passWord() const;
    virtual RWCString              dbName() const;
    virtual RWDBTracer&            tracer();

// mutator methods
    virtual size_t                defaultConnections(size_t size);
    virtual RWCString::caseCompare caseCompare(RWCString::caseCompare);

// multi-threading methods
            void                 acquire() const;
            void                 release() const;

// producer methods
    virtual RWDBConnectionImp*   connectionImp(
                                     RWDBConnection::ConnectionType type);
    virtual RWCString            pkConstraint(RWDBSchema schema);

    virtual RWDBTableImp*        tableImp(const RWDBDatabase& dbase,
                                          const RWCString& name);
    virtual RWDBSelectorImp*     selectorImp(const RWDBDatabase& dbase,
                                             const RWDBCriterion& criterion);
    virtual RWDBCursorImp*       cursorImp(RWDBCursor::CursorType type,
                                           RWDBCursor::CursorAccess access,
                                           const RWCString& select,
                                           const RWDBConnection& connection);
    virtual RWDBCursorImp*       cursorImp(RWDBCursor::CursorType type,
                                           RWDBCursor::CursorAccess access,
                                           const RWCString& select,
                                           const RWDBSchema& updateCols,
                                           const RWDBConnection& connection);
    virtual RWDBStoredProcImp*   storedProcImp(const RWDBDatabase& dbase,
                                               const RWCString& name,
                                               const RWDBStatus& status,
                                               const RWDBConnection& conn);
    virtual RWDBDateVectorImp*   dateVectorImp(size_t size) ;
    virtual RWDBStoredProcImp*   storedProcImp(const RWDBDatabase& dbase,
                                               const RWCString& name,
                                               const RWDBStatus& status,
                                               const RWDBConnection& conn,
                                                            const RWDBSchema&);

// DDL methods
    virtual RWDBStatus           createTable(const RWCString& name,
                                             const RWDBSchema& updateCols,
                                             const RWDBConnection& connection);
    virtual RWDBStatus           createView(const RWCString& name,
                                            const RWDBSelectorBase& select,
                                            const RWDBConnection& connection);
    virtual RWDBStatus           createView(const RWCString& name,
                                            const RWDBSchema& columnList,
                                            const RWDBSelectorBase& select,
                                            const RWDBConnection& connection);
    virtual RWDBStatus           createView(const RWCString& name,
                                            const RWCString& sql,
                                            const RWDBConnection& connection);
    virtual RWDBStatus           createView(const RWCString& name,
                                            const RWDBSchema& columnList,
                                            const RWCString& sql,
                                            const RWDBConnection& connection);
    virtual RWDBStatus           createProcedure(const RWCString& name,
                                                 const RWCString& sql,
                                                 const RWDBSchema& params,
                                                 const RWDBConnection& conn);
    virtual RWDBStatus           createProcedure(const RWDBStoredProc& proc,
                                                 const RWDBConnection& conn);

    virtual RWDBStatus           dropView(const RWCString& name,
                                          const RWDBConnection& connection);


    virtual RWDBTable            dbTables(const RWDBConnection& conn,
                                          const RWCString& name,
                                          const RWCString& owner,
                                                int type);

    virtual RWDBTable            dbStoredProcedures(const RWDBConnection& conn,
                                                    const RWCString& name,
                                                    const RWCString& owner);

    static void                  traceSql(RWDBTracer& tracer, const RWCString& sql);

protected:
    // the mutex_ variable has to be declared before passWord_ in order
    // that it is available (constructed) by the time the crypt() is used
    // to initialize passWord_ in the constructor, for the reason that
    // the crypt() function needs to be multi-thread protected.
    RWDBMutex                    mutex_;

    RWDBConnectionList           connectionList_;
    RWCString::caseCompare       caseCompare_;
    RWCString                    serverType_;
    RWCString                    serverName_;
    RWCString                    userName_;
    RWCString                    passWord_;
    RWCString                    dbName_;
    RWDBTracer                   tracer_;

    RWBoolean                    firstConnection_;
    size_t                       connectionPoolSize_;

    void                         destroyConnectionPool();
    void                         (*deregisterDb_)(void);
    static void                  defaultDeregisterFunction();
    RWCString                    crypt(const RWCString&) const;

private:
// not implemented:
    RWDBDatabaseImp( const RWDBDatabaseImp& );
    RWDBDatabaseImp& operator=( const RWDBDatabaseImp& );
};

 
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBDatabaseHelper {

  public:

    static RWDBDatabaseImp *getImp( const RWDBDatabase& dbase );

};

#endif

