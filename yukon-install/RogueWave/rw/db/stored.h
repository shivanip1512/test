#ifndef __RWDB_STORED_H__
#define __RWDB_STORED_H__

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
 * Definition of class RWDBStoredProc
 *
 * This class provides a standard interface to the specific database
 * implementation of "stored procedures", i.e. the method used to manipulate
 * data in the database via procedure like calls.
 *
 * The schema of the parameters is fetched from the database whenever the
 * stored procedure is first created. From that point on, the schema is kept
 * as part of the stored procedure to check parameter types, positioning, etc.
 * For more information about the specific implementation,
 * see the header files for the specific database.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/status.h>
#include <rw/db/value.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBStoredProc
//                                                                     
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBStoredProc {
public:
    RWDBStoredProc();
    RWDBStoredProc(const RWDBStoredProc& proc);
    RWDBStoredProc(RWDBStoredProcImp* impl);
    virtual ~RWDBStoredProc();
    RWDBStoredProc& operator=(const RWDBStoredProc& proc);

// Accessors
    RWDBDatabase        database() const;
    RWCString           name() const;
    RWCString           text(RWBoolean forceLookup = FALSE) const;
    RWCString           text(const RWDBConnection& connection,
                             RWBoolean forceLookup = FALSE) const;
    RWDBSchema          params(RWBoolean forceLookup = FALSE) const;
    RWDBSchema          params(const RWDBConnection& connection,
                               RWBoolean forceLookup = FALSE) const;
    RWBoolean           exists(RWBoolean forceLookup = FALSE) const;
    RWBoolean           exists(const RWDBConnection& connection,
                               RWBoolean forceLookup = FALSE) const;

      // multi-threading support
    void                acquire(void) const;
    void                release(void) const;

// Error Handling
    void                     setErrorHandler(RWDBStatus::ErrorHandler handler);
    RWDBStatus::ErrorHandler errorHandler() const;
    RWDBStatus               status() const;
    RWBoolean                isValid() const;
    RWBoolean                isReady() const;

// Execution
    RWDBResult          execute();
    RWDBResult          execute(const RWDBConnection& conn);
    RWDBStatus          fetchReturnParams();
    RWDBValue           returnValue() const;

// DDL
    RWDBStatus          drop();
    RWDBStatus          drop(const RWDBConnection& connection);

// Parameter Setting
    RWDBStoredProc&     operator<<(RWDBValueManip value);
    RWDBStoredProc&     operator<<(const RWDBValue& value);
    RWDBStoredProc&     operator<<(char value);
    RWDBStoredProc&     operator<<(unsigned char value);
    RWDBStoredProc&     operator<<(short value);
    RWDBStoredProc&     operator<<(unsigned short value);
    RWDBStoredProc&     operator<<(int value);
    RWDBStoredProc&     operator<<(unsigned int value);
    RWDBStoredProc&     operator<<(long value);
    RWDBStoredProc&     operator<<(unsigned long value);
    RWDBStoredProc&     operator<<(float value);
    RWDBStoredProc&     operator<<(double value);
    RWDBStoredProc&     operator<<(const RWDecimalPortable& value);
    RWDBStoredProc&     operator<<(const RWDate& value);
    RWDBStoredProc&     operator<<(const RWDBDateTime& value);
    RWDBStoredProc&     operator<<(const RWDBDuration& value);
    RWDBStoredProc&     operator<<(const RWCString& value);
    RWDBStoredProc&     operator<<(const RWDBMBString& value);
#ifndef RW_NO_WSTR
    RWDBStoredProc&     operator<<(const RWWString& value);
#endif
    RWDBStoredProc&     operator<<(const RWDBBlob& value);

    RWDBStoredProc&     operator<<(short* val);
    RWDBStoredProc&     operator<<(unsigned short* val);
    RWDBStoredProc&     operator<<(int* val);
    RWDBStoredProc&     operator<<(unsigned int* val);
    RWDBStoredProc&     operator<<(long* val);
    RWDBStoredProc&     operator<<(unsigned long* val);
    RWDBStoredProc&     operator<<(float* val);
    RWDBStoredProc&     operator<<(double* val);
    RWDBStoredProc&     operator<<(RWDecimalPortable* val);
    RWDBStoredProc&     operator<<(RWDate* val);
    RWDBStoredProc&     operator<<(RWDBDateTime* val);
    RWDBStoredProc&     operator<<(RWDBDuration* val);
    RWDBStoredProc&     operator<<(RWDBBlob* val);
    RWDBStoredProc&     operator<<(RWCString* val);
    RWDBStoredProc&     operator<<(RWDBMBString* val);
#ifndef RW_NO_WSTR
    RWDBStoredProc&     operator<<(RWWString* val);
#endif

// Parameter Access
    RWDBStoredProc&     operator[]( const RWDBColumn& paramColumn );
    RWDBStoredProc&     operator[]( const RWCString& paramName );
    RWDBStoredProc&     operator[]( size_t paramPos );
    RWBoolean           isNull( const RWDBColumn& paramColumn ) const;
    RWBoolean           isNull( const RWCString& paramName ) const;
    RWBoolean           isNull( size_t paramPos ) const;

private:
    RWDBStoredProcImp   *impl_;
};
#endif
