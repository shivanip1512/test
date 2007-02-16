#ifndef __RWDB_ODBCCONN_H__
#define __RWDB_ODBCCONN_H__

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
 * ODBC Library implementation of RWDBConnectionImp
 *
 **************************************************************************/

#include <rw/db/odbcsrc/odbdefs.h>
#include <rw/db/odbcsrc/odbsysh.h>

#include <rw/db/dbsrc/connecti.h>


class RWDBAccessExport RWDBODBCLibConnectionImp : public RWDBConnectionImp {
public:
    RWDBODBCLibConnectionImp(const RWDBStatus& status,
                             const RWDBDatabase& dbase,
                             RWDBConnection::ConnectionType conntype);

    ~RWDBODBCLibConnectionImp();
    void                       open();
    RWDBStatus                 close();
    RWDBStatus                 cleanup();

// accessor methods
    RWDBSystemHandle*          systemHandle() const;

// execution methods
    RWDBStatus                 beginTransaction(const RWCString&);
    RWDBStatus                 commitTransaction(const RWCString&);
    RWDBStatus                 rollbackTransaction(const RWCString&);
    RWDBStatus                 setIsolation(
                                  RWDBConnection::IsolationType level);
    RWDBConnection::IsolationType isolation() const;

    virtual RWDBResultImp *    executeSql( const RWCString& sql,
                                                 RWDBStatus& status );

        void                       setSystemHandle(RWDBODBCLibSystemHandle*);
        void                       init();
private:
    RWDBODBCLibSystemHandle*   systemHandle_;
    RWBoolean                  hasBeenSet_;
    RWCString                  serverName_;
    SWORD                      txnCapable_; 

    RWBoolean                  supportsTransactions( );
    RWDBODBCLibDatabaseImp*    dbaseImp();

// Not Imlemented:
    RWDBODBCLibConnectionImp(const RWDBODBCLibConnectionImp&);
    RWDBODBCLibConnectionImp& operator=(const RWDBODBCLibConnectionImp&);
};

#endif



