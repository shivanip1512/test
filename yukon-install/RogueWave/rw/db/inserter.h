#ifndef __RWDB_INSERTER_H__
#define __RWDB_INSERTER_H__

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
 * Definition of class RWDBInserter
 *
 * This class provides a standard interface to the database-specific 
 * implementation of "inserter", i.e. and object used to insert data
 * into a database table.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/expr.h>
#include <rw/db/status.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBInserter
//                                                                     
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBInserter {
public:
    RWDBInserter        ();
    RWDBInserter        ( const RWDBInserter& ins );
    RWDBInserter        ( RWDBInserterImp* impl );
    RWDBInserter&       operator=( const RWDBInserter& ins );

    virtual ~RWDBInserter();

// accessors
    RWCString           asString() const;
    RWDBTable           table() const;

// error handling functions
    void                     setErrorHandler(RWDBStatus::ErrorHandler handler);
    RWDBStatus::ErrorHandler errorHandler() const;
    RWDBStatus               status() const;
    RWBoolean                isValid() const;
    RWBoolean                isReady() const;

      // multi-threading support
    void                acquire(void) const;
    void                release(void) const;

// execution
    RWDBResult          execute();
    RWDBResult          execute( const RWDBConnection& conn );
    RWDBStatus          clear();

// indexing
    RWDBInserter&       operator[]( const RWCString& columnName );
    RWDBInserter&       operator[]( const RWDBColumn& column );

// adding values to insert
    RWDBInserter&       addValue( const RWDBExpr& scalar );

// adding scalar expressions, i.e. values, to insert
    RWDBInserter&       operator<<( const RWDBExpr& expr );

    // Some compilers have problem in implicit converts to Expr. Hence this
    RWDBInserter&       operator<<( RWDBValueManip manip );
    RWDBInserter&       operator<<( const RWDBValue& value );
    RWDBInserter&       operator<<( char value );
    RWDBInserter&       operator<<( unsigned char value );
    RWDBInserter&       operator<<( short value );
    RWDBInserter&       operator<<( unsigned short value );
    RWDBInserter&       operator<<( int value );
    RWDBInserter&       operator<<( unsigned int value );
    RWDBInserter&       operator<<( long value );
    RWDBInserter&       operator<<( unsigned long value );
    RWDBInserter&       operator<<( float value );
    RWDBInserter&       operator<<( double value );
    RWDBInserter&       operator<<( const char* value );
    RWDBInserter&       operator<<( const RWDecimalPortable& value );
    RWDBInserter&       operator<<( const RWDate& value );
    RWDBInserter&       operator<<( const RWDBDateTime& value );
    RWDBInserter&       operator<<( const RWDBDuration& value );
    RWDBInserter&       operator<<( const RWCString& value );
    RWDBInserter&       operator<<( const RWDBMBString& value );
#ifndef RW_NO_WSTR
    RWDBInserter&       operator<<( const RWWString& value );
#endif
    RWDBInserter&       operator<<( const RWDBBlob& value );

    RWDBInserter&       operator<<( RWDBReader& rdr );

private:
    RWDBInserterImp         *impl_;
};
#endif
