#ifndef __RWDB_CONNECTI_H__
#define __RWDB_CONNECTI_H__

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
 * Definition of RWDBConnectionImp
 *
 **************************************************************************/

#include <rw/db/dbref.h>
#include <rw/db/dbase.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBConnectionImp
//
//    Base Class for Connection Implementations
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBConnectionImp : public RWDBReference, public RWDBStatus {

friend class RWDBExport RWDBDatabaseImp;

public:

    RWDBConnectionImp( const RWDBStatus&      status,
                       const RWDBDatabase&    dbase,
                             RWDBConnection::ConnectionType connType );
    virtual ~RWDBConnectionImp();

    virtual void              open();
    virtual RWDBStatus        close();
    virtual RWDBStatus        cleanup();          
    virtual void              returnToPool();

    virtual RWDBStatus        beginTransaction(const RWCString& name);
    virtual RWDBStatus        commitTransaction(const RWCString& name);
    virtual RWDBStatus        rollbackTransaction(const RWCString& name);
    virtual     RWDBStatus        setSavepoint(const RWCString& name);
    virtual RWDBStatus        setIsolation(RWDBConnection::IsolationType level);
    virtual RWDBConnection::IsolationType
                              isolation() const;
    virtual RWDBStatus        connectionType( 
                                     RWDBConnection::ConnectionType type );

    virtual RWDBStatus        changeConnectionType( 
                                     RWDBConnection::ConnectionType type);

    virtual RWDBResultImp*    executeSql( const RWCString& sql,
                                                RWDBStatus& status );

    RWDBStatus                executeSqlNoResult( const RWCString& sql,
                                                  RWDBStatus& status );

// accessors
    virtual RWBoolean         inUse() const;
    virtual RWBoolean         isOpen() const;
    virtual RWDBConnection::ConnectionType 
                              connectionType() const;
    virtual RWDBDatabase      database() const;
    virtual RWDBSystemHandle* systemHandle() const;

// multi-threading functions
            void              acquire() const;
            void              release() const;

protected:
    RWDBDatabase                   dbase_;
    RWDBConnection::ConnectionType connType_;
    RWBoolean                      isOpen_; // derived classes should set this

// multi-threading data
    RWDBMutex                      mutex_;

private:
// not implemented
    RWDBConnectionImp( const RWDBConnectionImp& );
    RWDBConnectionImp& operator=( const RWDBConnectionImp& );
};

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBConnectionHelper {

  public:

    static RWDBConnectionImp *getImp( const RWDBConnection& conn );

};

#endif

