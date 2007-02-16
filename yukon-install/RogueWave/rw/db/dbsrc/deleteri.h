#ifndef __RWDB_DELETERI_H__
#define __RWDB_DELETERI_H__

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
#include <rw/db/expr.h>
#include <rw/db/table.h>
#include <rw/db/connect.h>

#include <rw/db/dbsrc/stmt.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBDeleterImp
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBDeleterImp : public RWDBReference, public RWDBStatus {

public:
    RWDBDeleterImp( const RWDBStatus& status );
    RWDBDeleterImp( const RWDBStatus& status,
                    const RWDBTable&    table,
                    const RWDBCriterion& criterion );

    virtual ~RWDBDeleterImp();

// accessors and mutators
    virtual RWDBTable     table() const;
    virtual RWDBCriterion where();
    virtual void          where( const RWDBCriterion& criterion );

// multi-threading functions
            void          acquire() const;
            void          release() const;

// for SQL expansion
    virtual RWCString     asString();
    virtual const RWDBPhraseBook&   phraseBook() const;

// for execution
    virtual RWDBResult    execute( const RWDBConnection& connection );
    virtual void          cancel( const RWDBConnection& conn,
                                       RWDBStatus&     status );
    virtual RWDBResult    parse( const RWDBConnection& conn,
                                      RWDBStatus&     status );
    virtual RWDBResult    bind( const RWDBConnection& conn,
                                     RWDBStatus&     status );
    virtual RWDBResult    perform( const RWDBConnection& conn,
                                        RWDBStatus&     status );
    virtual RWDBStatus    clear( );

    virtual RWDBStatementImp * statement() const;

protected:
    RWDBTable            table_;
    RWDBCriterion        criterion_;

    RWCString            sql_;
    RWBoolean            changed_;

    RWDBStatement        statement_;

      // multi-threading data
    RWDBMutex            mutex_;

private:
// not implemented
    RWDBDeleterImp( const RWDBDeleterImp& deleter );
    RWDBDeleterImp&      operator=( const RWDBDeleterImp& deleter );

    RWDBConnection       conn_;

};

#endif
