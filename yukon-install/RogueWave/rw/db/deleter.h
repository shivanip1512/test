#ifndef __RWDB_DELETER_H__
#define __RWDB_DELETER_H__

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
 * Definition of class RWDBDeleter
 *
 * This class provides a standard interface to the specific database
 * implementation of a "deleter", i.e. an object used to delete data
 * in the database.
 *
 * NOTE: RWDBDeleter's are constructed from a table or directly from
 *       a database.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/status.h>

//////////////////////////////////////////////////////////////////////////
//                                                                      
//  RWDBDeleter
//                                                                     
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBDeleter {
public:
    RWDBDeleter();
    RWDBDeleter(const RWDBDeleter& deleter);
    RWDBDeleter(RWDBDeleterImp *imp);
    RWDBDeleter&    operator=(const RWDBDeleter& deleter);

    virtual ~RWDBDeleter();

// access functions
    RWCString       asString() const;
    RWDBTable       table() const;

// error handling functions
    void                     setErrorHandler( RWDBStatus::ErrorHandler );
    RWDBStatus::ErrorHandler errorHandler() const;
    RWDBStatus               status() const;
    RWBoolean                isValid() const;
    RWBoolean                isReady() const;

      // multi-threading support
    void            acquire(void) const;
    void            release(void) const;

// modification and execution
    RWDBDeleter&    where( const RWDBCriterion& criterion );
    RWDBResult      execute();
    RWDBResult      execute( const RWDBConnection& connection );
    RWDBStatus      clear();

// inquire where clause
    RWDBCriterion   where() const;

private:
    RWDBDeleterImp  *impl_;
};
#endif
