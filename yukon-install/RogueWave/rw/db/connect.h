#ifndef __RWDB_CONNECT_H__
#define __RWDB_CONNECT_H__

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
 

#include  <rw/db/defs.h>
#include  <rw/db/status.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBConnection
//
//     Connections represent a scarce resource that applications must be
//     allowed to allocate and manage themselves.  A connection is an
//     object that may be requested from a Database, and passed to certain
//     functions to specify that the already open connection is to be used.
//
//     Operations requested without supplying a connection are performed
//     using a connection supplied by the Database object, invisibly; if
//     none are available, an error results.  Thus, the use of explicit
//     connections eliminates one likely source of errors, at some cost
//     in program complexity.
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBConnection {

friend class RWDBExport RWDBConnectionHelper;

public:
    enum    IsolationType {Unknown, ANSILevel1, ANSILevel2, ANSILevel3};
    enum    ConnectionType { Synchronous = 1, Asynchronous };
    RWDBConnection        ();
    RWDBConnection        (RWDBConnectionImp* imp);
    RWDBConnection        (const RWDBConnection& conn);

    RWDBConnection&       operator=( const RWDBConnection& conn );

    virtual ~RWDBConnection();

    RWDBStatus            open();
    RWDBStatus            close();
    RWDBDatabase          database() const;
    RWDBStatus            beginTransaction(const RWCString& name= RWCString());
    RWDBStatus            commitTransaction(const RWCString& name= RWCString());
    RWDBStatus            rollbackTransaction(const RWCString& name= RWCString());
    RWDBStatus            setSavepoint(const RWCString& name);
    RWDBStatus            autoCommit(RWBoolean autoCommit);
    RWBoolean             autoCommit() const;
    RWDBStatus            isolation(IsolationType level);
    IsolationType         isolation() const;
    ConnectionType        connectionType() const;
    RWDBStatus            connectionType(ConnectionType);

    RWDBSystemHandle*     systemHandle() const;

      // error handling
    RWDBStatus               status() const;
    RWBoolean                isValid() const;
    RWBoolean                isReady() const;
    void                     setErrorHandler(RWDBStatus::ErrorHandler handler);
    RWDBStatus::ErrorHandler errorHandler() const;

      // multi-threading support
    void                  acquire(void) const;
    void                  release(void) const;
 
// Direct Execution of SQL
    RWDBResult            executeSql(const RWCString& sqlProgram) const;

    RWBoolean             operator==(const RWDBConnection& conn) const;
    RWBoolean             operator!=(const RWDBConnection& conn) const;

private:

    RWDBConnectionImp*    impl_;
};

#endif
