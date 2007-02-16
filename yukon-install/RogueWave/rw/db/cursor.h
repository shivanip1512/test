#ifndef __RWDB_CURSOR_H__
#define __RWDB_CURSOR_H__

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
 * Definition of class RWDBCursor
 *
 * This class provides a standard interface to the specific database
 * implementation of "cursors".
 *
 * NOTE: RWDBCursor's are constructed from a database or a table, which was
 *       created from a database.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/status.h>
#include <rw/db/value.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBCursor
//                                                                     
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBCursor {
public:

enum    CursorType {Sequential, Scrolling};
enum    CursorAccess {Read, Write};
enum    CursorPosition {First, Last, Next, Previous, Absolute, Relative};

        RWDBCursor();
        RWDBCursor(const RWDBCursor& cursor);
        RWDBCursor(RWDBCursorImp* impl);

        virtual ~RWDBCursor();

// access functions
        RWCString       name() const;
        CursorType      type() const;
        CursorAccess    access() const;
        RWDBConnection  connection() const;
        RWDBSchema      schema() const;

// error handling functions
        void                     setErrorHandler(RWDBStatus::ErrorHandler);
        RWDBStatus::ErrorHandler errorHandler() const;
        RWDBStatus               status() const;
        RWBoolean                isValid() const;
        RWBoolean                isReady() const;

// multi-threading functions
        void                     acquire() const;
        void                     release() const;

// cursor functions
        RWDBStatus      fetchRow(CursorPosition position=Next, int offset=1);
        RWDBStatus      updateRow(const RWCString& tableName);
        RWDBStatus      deleteRow(const RWCString& tableName);
        RWDBStatus      insertRow(const RWCString& tableName);

// manipulating of values for fetchs, updates and inserts
        RWDBStatus      unbind();
        RWBoolean       isNull(unsigned int colPosition) const;

        RWDBCursor&     operator<<(short* val);
        RWDBCursor&     operator<<(unsigned short* val);
        RWDBCursor&     operator<<(int* val);
        RWDBCursor&     operator<<(unsigned int* val);
        RWDBCursor&     operator<<(long* val);
        RWDBCursor&     operator<<(unsigned long* val);
        RWDBCursor&     operator<<(float* val);
        RWDBCursor&     operator<<(double* val);
        RWDBCursor&     operator<<(RWDecimalPortable* val);
        RWDBCursor&     operator<<(RWDate* val);
        RWDBCursor&     operator<<(RWDBDateTime* val);
        RWDBCursor&     operator<<(RWDBDuration* val);
        RWDBCursor&     operator<<(RWCString* val);
        RWDBCursor&     operator<<(RWDBMBString* val);
#ifndef RW_NO_WSTR
        RWDBCursor&     operator<<(RWWString* val);
#endif
        RWDBCursor&     operator<<(RWDBBlob* val);
        RWDBCursor&     operator<<(RWDBValueManip);

        RWDBCursor&     operator[](size_t            index );
        RWDBCursor&     operator[](const RWCString&  name  );
        RWDBCursor&     operator[](const RWDBColumn& column);

        RWDBCursor&     operator=(const RWDBCursor& cursor);
private:
        RWDBCursorImp   *impl_;
};
#endif
