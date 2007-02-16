#ifndef __RWDB_ForeignKey_H__
#define __RWDB_ForeignKey_H__

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
 * Class RWDBBForeignKey is an ordered collection of RWDBColumns.  As such,
 * it serves as an encapsulation of the database notion of ForeignKey, a set
 * of attributes defining an key.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/status.h>
#include <rw/collect.h>
#include <rw/ordcltn.h>

//////////////////////////////////////////////////////////////////////////
//
//  RWDBForeignKey
//
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBForeignKey : public RWCollectable
{
public:
    enum Constraint { cascade = 'C', restrict='R', nullify = 'N' };

    // constructors, destructor, assignment
    RWDBForeignKey(); 
    RWDBForeignKey( const RWCString& refName, 
                    Constraint updateConstraint=restrict,
                    Constraint deleteConstraint=restrict); 

    RWDBForeignKey             (const RWDBForeignKey& ForeignKey);
    virtual ~RWDBForeignKey    ();
    RWDBForeignKey& operator=  (const RWDBForeignKey& ForeignKey);
    RWBoolean operator==       (const RWDBForeignKey& fk) const;

    // indexing
    RWDBColumn operator[]  (size_t position) const;
    RWDBColumn operator[]  (const RWCString& name) const;
    RWDBColumn column      (size_t position) const;
    RWDBColumn column      (const RWCString& name) const;
    RWDBColumn column      (const RWCString& name,
                                  RWCString::caseCompare caseCompare ) const;
    size_t index           (const RWCString& name ) const;
    size_t index           (const RWCString& name,
                                  RWCString::caseCompare caseCompare ) const;
    size_t index           (const RWDBColumn& column) const;

    // accessors
    size_t entries         () const;
    RWBoolean isEmpty      () const;

    // mutators
    RWDBColumn appendColumn (const RWDBColumn& column);

    // format self as SQL string
    RWCString asString     (const RWDBPhraseBook& phrasebook) const;

    // error handling
    void                     setErrorHandler(RWDBStatus::ErrorHandler handler);
    RWDBStatus::ErrorHandler errorHandler() const;
    RWBoolean                isValid() const;
    RWDBStatus               status() const;

    RWCString referenceName();
    Constraint updateConstraint();
    Constraint deleteConstraint();

private:
    RWDBForeignKeyImp* impl_;
};

class RWDBExport RWDBForeignKeyList : public RWOrdered
{
public:
    RWDBForeignKeyList();
    RWDBForeignKey& operator[](size_t indx) const;
    virtual ~RWDBForeignKeyList();
};

#endif
