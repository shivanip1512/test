#ifndef __RWDB_CURSORI_H__
#define __RWDB_CURSORI_H__

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
#include <rw/db/connect.h>
#include <rw/db/cursor.h>
#include <rw/db/schema.h>

#include <rw/db/dbsrc/taggen.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBCursorImp
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBCursorImp : public RWDBReference, public RWDBStatus {

public:
// constructors, destructor
    RWDBCursorImp( const RWDBStatus& status );
    RWDBCursorImp( const RWDBStatus&         status,
                   RWDBCursor::CursorType    type,
                   RWDBCursor::CursorAccess  access,
                   const RWCString&          select,
                   const RWDBSchema&         updateCols,
                   const RWDBConnection&     connection );
    virtual ~RWDBCursorImp();

// accessors
    virtual RWCString                   name() const;
    virtual RWDBCursor::CursorType      type() const;
    virtual RWDBCursor::CursorAccess    access() const;
    virtual RWDBConnection              connection() const;
    virtual RWDBSchema                  schema() const;

// multi-threading operations
            void                acquire() const;
            void                release() const;

// cursor operations
    virtual RWDBStatus          fetchRow(RWDBCursor::CursorPosition position,
                                                 int offset);
    virtual RWDBStatus          updateRow(const RWCString& tableName);
    virtual RWDBStatus          deleteRow(const RWCString& tableName);
    virtual RWDBStatus          insertRow(const RWCString& tableName);

// support for RWDBCursor::operator<< and []
    virtual void                appendValue(RWDBValue::ValueType type,
                                            void* appData);
    virtual RWDBStatus          setPosition(size_t columnPosition);
    virtual RWDBStatus          setPosition(const RWCString& columnName);

// utilities
    virtual RWDBStatus          unbind();
    virtual RWBoolean           isNull(size_t colPosition);

protected:
    RWCString                   name_;
    RWDBCursor::CursorType      type_;
    RWDBCursor::CursorAccess    access_;
    RWDBConnection              connection_;
    RWDBSchema                  schema_;        // schema of cursor not updatable columns
    RWDBSchema                  updateCols_;    // schema of updatable columns

      // multi-threading data
    RWDBMutex                   mutex_;

    RWDBTagGenerator&           tagGenerator();

private:
// not implemented
    RWDBCursorImp(const RWDBCursorImp& cursor);
    RWDBCursorImp&              operator=(const RWDBCursorImp& cursor);
};

#endif
