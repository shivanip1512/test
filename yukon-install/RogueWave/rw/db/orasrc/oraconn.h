#ifndef __RWDB_ORACONN_H__
#define __RWDB_ORACONN_H__

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
 * Oracle OCI Access Library implementation of RWDBConnectionImp
 *
 **************************************************************************/


#include <rw/db/orasrc/orafutur.h>
#include <rw/db/orasrc/orasysh.h>
#include <rw/db/dbsrc/connecti.h>


class RWDBAccessExport RWDBOracleConnectionImp : public RWDBConnectionImp {

friend class RWDBAccessExport RWDBOracleConnFutureImp;

public:
    enum TransType { Begin, Commit, Rollback, Savepoint };
    RWDBOracleConnectionImp( const RWDBStatus&   status,
                             const RWDBDatabase& db,
                             RWDBConnection::ConnectionType connType );
    ~RWDBOracleConnectionImp();

    virtual void                open();
    virtual RWDBStatus          close();

// accessor methods
    virtual RWDBSystemHandle*   systemHandle() const;
    RWDBConnection::ConnectionType connType() const { return connType_; }

// producer methods
    virtual RWDBStatus          beginTransaction(const RWCString&);
    virtual RWDBStatus          commitTransaction(const RWCString&);
    virtual RWDBStatus          rollbackTransaction(const RWCString& name);
    virtual RWDBStatus          setSavepoint(const RWCString& name);
    virtual RWDBStatus          setIsolation(
                                       RWDBConnection::IsolationType level);
    virtual RWDBConnection::IsolationType
                                isolation() const;
    RWDBStatus                  changeConnectionType( 
                                    RWDBConnection::ConnectionType connType );
    virtual RWDBResultImp *     executeSql(const RWCString& sql,
                                                 RWDBStatus& status);

protected:
    Lda_Def                     lda_;
    Hda_Def                     hda_;
    RWBoolean                   ologofOK_;

    static int                  initialized_;

private:
    RWDBOracleSystemHandle*     systemHandle_;

// not implemented
    RWDBOracleConnectionImp(const RWDBOracleConnectionImp&);
    RWDBOracleConnectionImp& operator=(const RWDBOracleConnectionImp&);
};

class RWDBAccessExport RWDBOracleConnFutureImp : public RWDBOracleFutureImp {
public :
    RWDBOracleConnFutureImp( const RWDBConnection& conn,
                             const RWDBStatus&     status,
                             RWDBOraOciCalls       oci,
                             short                 stages );
    ~RWDBOracleConnFutureImp();

protected :
    RWDBOraRetCode doNext();

private :    
    //Not implemented
    RWDBOracleConnFutureImp( const RWDBOracleConnFutureImp& );
    RWDBOracleConnFutureImp& operator=( const RWDBOracleConnFutureImp& );
};

#endif
