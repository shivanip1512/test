#ifndef __RWDB_ORACURSR_H__
#define __RWDB_ORACURSR_H__

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
 * Oracle OCI access library implementation of RWDBCursorImp
 *
 **************************************************************************/


#include <rw/ordcltn.h>
#include <rw/collect.h>
#include <rw/db/dbref.h>

#include <rw/db/dbsrc/cursori.h>

#include <rw/db/orasrc/orafutur.h>
#include <rw/db/orasrc/orafutur.h>
#include <rw/db/orasrc/orares.h>
#include <rw/db/orasrc/oracda.h>

////////////////////////////////////////////////////////////////////////////
//
// Most of the RWDBOracleCursorImp functions are implemented by
// RWDBOracleCursorBodyImp. 
//
////////////////////////////////////////////////////////////////////////////
class RWDBAccessExport RWDBOracleCursorBodyImp : public RWDBReference{

friend class RWDBAccessExport RWDBOracleCursorFutureImp;
friend class RWDBAccessExport RWDBOracleCursorImp;

public:
    enum Caller{ Constructor = 0, Fetch, Update, Delete, Insert, Other };

private :
    enum    CursorBinding {NoBinding, DeleteBinding, FetchBinding,
                           InsertBinding, UpdateBinding, BreakBinding};

    RWDBOracleCursorBodyImp( const RWDBConnection&    connection,
                             const RWCString&         select,
                             const RWDBSchema&        columns,
//                             const RWDBSchema&        cursorSchema,
                             RWDBSchema               *cursorSchema,
                             RWDBCursor::CursorAccess access,
                             RWDBStatus               *cursorStatus
                           );
    ~RWDBOracleCursorBodyImp();

    void                   createCursor( const RWCString&  select,
                                         RWDBCursor::CursorAccess access,
                                               RWDBStatus  *cursorStatus);
    void                   destroyCursor();
    void                   fetchRow( RWDBCursor::CursorPosition pos,
                                     RWDBStatus  *retStatus,
                                     RWDBStatus  *cursorStatus );
    void                   updateRow( const RWCString& tableName, 
                                      RWDBStatus  *retStatus,
                                      RWDBStatus  *cursorStatus );
    void                   deleteRow( const RWCString& tableName, 
                                      RWDBStatus  *retStatus,
                                      RWDBStatus  *cursorStatus );
    void                   insertRow( const RWCString& tableName, 
                                      RWDBStatus  *retStatus,
                                      RWDBStatus  *cursorStatus );
    void                   appendValue( RWDBValue::ValueType type,
                                        void       *appData,
                                        RWDBStatus *cursorStatus );
    void                   setPosition( size_t columnPosition,
                                        RWDBStatus *retStatus );
    void                   setPosition( const RWCString& columnName,
                                        RWDBStatus *retStatus );
    void                   unbind( RWDBStatus *retStatus );
    RWBoolean              isNull( size_t colPosition,
                                   RWDBStatus *cursorStatus );
    void                   bind( CursorBinding binding, RWDBStatus *status );
    void                   bindAppToLong( RWDBOracleData* longData,
                                          osword          position,
                                          RWDBStatus*     status );
    void                   convertToApp( RWDBStatus& status );
    void                   addSchema();
    RWBoolean              populateColumnData(RWDBStatus *retStatus);
    void                   prepareUpdateStmt( RWCString& updateStmt,
                                              RWDBStatus *status,
                                              const RWCString& tableName );

    void                   doParse( RWDBStatus *status, 
                                    const RWCString& stmt, 
                                    Cda_Def *cda );

    void                   doBind( RWDBStatus *status, Caller callType );
    void                   doExecute( RWDBStatus *retStatus, 
                               RWDBStatus *cursorStatus,
                               const RWDBOracleCDA& aCda );
    void                   doUpdateBind( RWDBStatus *status );
    void                   doDeleteBind( RWDBStatus *status );
    void                   doInsertBind( RWDBStatus *status );
    

    RWDBSchema             *schema_; // copy of the cursor schema
    RWDBSchema             updateCols_; // list of columns to update

    RWDBConnection         connection_;
    Lda_Def                *lda_;
    RWDBOracleCDA          cda1_;
    RWDBOracleCDA          cda2_;

    RWBoolean              executed_;
    RWDBPhraseBook         *phrasebook_;

    RWOrdered              columnData_;
    size_t                 columnPosition_;
    int                    columnsBound_;

    CursorBinding          binding1_;

    oub4                   fetchedRows_;

    RWDBOraDescribe        oraDescr_;

    RWBoolean              openedSecondCda_;
    int                    previousCommitState_;
    RWCString              serverName_;

    // Not implemented
    RWDBOracleCursorBodyImp( const RWDBOracleCursorBodyImp& );
    RWDBOracleCursorBodyImp& operator=(const RWDBOracleCursorBodyImp& );
};

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBOracleCursorImp
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBAccessExport RWDBOracleCursorImp : public RWDBCursorImp {
public:

    RWDBOracleCursorImp     ( RWDBCursor::CursorType    type,
                              RWDBCursor::CursorAccess  access,
                              const RWCString&          select,
                              const RWDBSchema&         columns,
                              const RWDBConnection&     connection,
                              const RWDBStatus&         status );

    ~RWDBOracleCursorImp    ();

   // cursor manipulations; always set return
    virtual RWDBStatus       fetchRow( RWDBCursor::CursorPosition position,
                                       int                        offset );
    virtual RWDBStatus       updateRow( const RWCString& tableName );
    virtual RWDBStatus       deleteRow( const RWCString& tableName );
    virtual RWDBStatus       insertRow( const RWCString& tableName );

    virtual void             appendValue( RWDBValue::ValueType type,
                                          void*                appData );

    virtual RWDBStatus       setPosition( size_t           columnPosition );
    virtual RWDBStatus       setPosition( const RWCString& columnName );

    virtual RWDBStatus       unbind();
    virtual RWBoolean        isNull( size_t colPosition );

private:
    RWDBOracleCursorBodyImp  *cursorBodyImp_;

    RWDBOracleCursorImp     ( const RWDBOracleCursorImp& cursor );
    RWDBOracleCursorImp&     operator=( const RWDBOracleCursorImp& cursor );
};


class RWDBAccessExport RWDBOracleCursorFutureImp : public RWDBOracleFutureImp {
public :
    RWDBOracleCursorFutureImp( const RWDBConnection&           conn,
                               const RWDBStatus&               status,
                               RWDBOraOciCalls                 oci, 
                               short                           stages, 
                               const RWDBStatus&               cursorStatus,
                               RWDBOracleCursorBodyImp         *cursorBody,
                               RWDBOracleCursorBodyImp::Caller callType 
                             );
    ~RWDBOracleCursorFutureImp();

protected :
     RWDBOraRetCode doNext();
     RWDBOraRetCode doConstructor();
     RWDBOraRetCode doFetch();
     RWDBOraRetCode doOthers();
     RWDBOraRetCode doFailure();

private :
     RWDBOracleCursorBodyImp         *cursorBodyImp_;
     RWDBStatus                      cursorStatus_;
     RWDBOracleCursorBodyImp::Caller callType_;

     // Not implemented
     RWDBOracleCursorFutureImp( const RWDBOracleCursorFutureImp& );
     RWDBOracleCursorFutureImp& operator=(const RWDBOracleCursorFutureImp& );
};

#endif

