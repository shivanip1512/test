#ifndef __RWDB_ODBCURSR_H__
#define __RWDB_ODBCURSR_H__

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
 * ODBC access library implementation of RWDBCursorImp
 *
 **************************************************************************/

#include <rw/db/odbcsrc/odbdefs.h>
#include <rw/db/odbcsrc/odbhstmt.h>
#include <rw/db/odbcsrc/odbsqlda.h>

#include <rw/db/dbsrc/cursori.h>


class RWDBAccessExport RWDBODBCLibCursorImp : public RWDBCursorImp {

public:
enum    CursorBinding {NoBinding, PointerBinding, ValueBinding, BreakBinding};

    RWDBODBCLibCursorImp(       RWDBCursor::CursorType   type,
                                RWDBCursor::CursorAccess access,
                          const RWCString&               select,
                          const RWDBConnection&          connection,
                          const RWDBStatus&              status,
                                RWBoolean                supportsDateTime,
                          const RWDBSchema&              sch );
    RWDBODBCLibCursorImp( const RWDBConnection&         connection,
                          const RWDBStatus&             status,
                                RWDBODBCLibHSTMT        hstmt,
                                RWBoolean               supportsDateTime);
    ~RWDBODBCLibCursorImp();


// cursor manipulations; always set return
    virtual RWDBStatus          fetchRow( RWDBCursor::CursorPosition position, int offset);
    virtual RWDBStatus          updateRow( const RWCString& tableName);
    virtual RWDBStatus          deleteRow( const RWCString& tableName);
    virtual RWDBStatus          insertRow( const RWCString& tableName);

    virtual void                appendValue( RWDBValue::ValueType type, void* appData);

    virtual RWDBStatus          setPosition( size_t   columnPosition);
    virtual RWDBStatus          setPosition( const RWCString& columnName);

    virtual RWDBStatus          unbind();
    virtual RWBoolean           isNull(size_t colPosition);

    void                        bind( CursorBinding binding);

private:
    RWDBODBCLibCursorImp&       operator=( const RWDBODBCLibCursorImp& cursor);
    int                                  previousFetch();
    int                                  absoluteFetch(int pos);
    int                                  relativeFetch(int pos);
    int                                  nextFetch();
    int                         lastFetch( );
    int                         firstFetch( );

    void                        executeSelect( const RWCString& selectStr);
    int                         position();
    RWCString                   colList();
    void                        fetchSchema();
    RWCString                   setClause();
    RWCString                   name() const;
    int                         *updateParamIndex_;
        UWORD                       *rowStatus_;
        size_t                       rowSet_;
        int                          rowSetSize_;
        size_t                       index_;
        RWBoolean                    bound_;
    RWDBODBCLibSqlda            theSqlda_;
    RWBoolean                   isAllocated_;
    RWBoolean                   isOpened_;
    RWBoolean                   supportsDateTime_;
    RWDBODBCLibHSTMT            selectHstmt_;
    RWDBSchema                  updateSchema_;  

    //RWDBConnection              conn_;
};

#endif




