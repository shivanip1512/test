#ifndef __RWDB_INSERTRI_H__
#define __RWDB_INSERTRI_H__

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
 * Definition of class RWDBInserterImp
 * 
 * This is a base class for a family of database-specific inserter
 * implementations.  An inserter is an object used to insert data into
 * a database table.
 *
 **************************************************************************/

#include <rw/ordcltn.h>
#include <rw/db/connect.h>
#include <rw/db/dbref.h>
#include <rw/db/status.h>
#include <rw/db/expr.h>
#include <rw/db/select.h>
#include <rw/db/table.h>

#include <rw/db/dbsrc/stmt.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBInserterEntry
//    Contents of valueList_.
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBInserterEntry : public RWCollectable {
public:
    RWDBInserterEntry          ();
    RWDBInserterEntry          ( const RWCString& asStr);
    RWDBInserterEntry          ( const RWDBExpr& sc );
//    RWDBInserterEntry          ( const RWCString& name, const RWDBExpr& scalar_ );
    ~RWDBInserterEntry();

    RWCString                  name() const;
    void                       setName(const RWCString& n);
    RWDBExpr                   expr() const;
    RWCString                  asString() const;
    void                       setAsString(const RWCString& str);

    void                       scalar( const RWDBExpr& scalar );
    RWBoolean                  hasScalar() const;

static void                    clearScalar( RWCollectable* c, void* );

// to make the entry a collectable
    int                        compareTo( const RWCollectable* ) const;
    unsigned                   hash() const;
    RWBoolean                  isEqual( const RWCollectable* ) const;
    
private:
// not implemented:
    RWDBInserterEntry( const RWDBInserterEntry& entry );
    RWDBInserterEntry& operator=( const RWDBInserterEntry& entry );

    RWCString       name_;
    RWDBExpr *      expr_;
    RWCString       asString_;
};

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBInserterImp
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBInserterImp : public RWDBReference, public RWDBStatus {

public:
    RWDBInserterImp            ( const RWDBStatus& status );
    RWDBInserterImp            ( const RWDBStatus& status,
                                 const RWDBTable& table,
                                 const RWDBSelectorBase& selector,
                                 const RWDBSchema& columnList );
    virtual ~RWDBInserterImp   ();

    virtual void               initialize( const RWDBSchema& columnList );

// accessors
    virtual RWDBTable          table() const;

// for SQL expansion
    virtual RWCString          asString();
    virtual const RWDBPhraseBook&   phraseBook() const;

// for execution
    virtual RWDBResult         execute( const RWDBConnection& conn );
    virtual void               cancel( const RWDBConnection& conn,
                                             RWDBStatus&     status );
    virtual RWDBResult         parse( const RWDBConnection& conn,
                                            RWDBStatus&     status );
    virtual RWDBResult         bind( const RWDBConnection& conn,
                                           RWDBStatus&     status );
    virtual RWDBResult         perform( const RWDBConnection& conn,
                                              RWDBStatus&     status );
    virtual RWDBStatus         clear( );

    virtual RWDBStatementImp * statement() const;

// for modifying scalar expressions
    virtual void               addScalar( const RWDBExpr& scalar );
    virtual void               addConstant( const RWCString& asStr );
    virtual size_t             position( const RWCString& name );

    virtual RWBoolean          hasSchema() const;

protected:
    RWDBTable                  table_;

    RWDBSelectorBase           selector_;

    RWOrdered                  scalarList_;
    size_t                     position_;

    RWCString                  sql_;
    RWBoolean                  changed_;        // has the state changed?

    RWDBStatement              statement_;
    
private:
// not implemented:
    RWDBInserterImp(const RWDBInserterImp& inserter);
    RWDBInserterImp& operator=(const RWDBInserterImp& inserter);

    RWDBConnection             conn_;

};

#endif
