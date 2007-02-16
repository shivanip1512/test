#ifndef __RWDB_SCHEMA_H__
#define __RWDB_SCHEMA_H__

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
 * Class RWDBBSchema is an ordered collection of RWDBColumns.  As such,
 * it serves as an encapsulation of the database notion of schema, a set
 * of attributes defining a table.  We extend the notion slightly: an
 * RWDBSchema may also be used to define a stored procedure's formal
 * parameters.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/column.h>


//////////////////////////////////////////////////////////////////////////
//
//  RWDBSchema
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBSchema
{
public:
    // constructors, destructor, assignment
    RWDBSchema             ();
    RWDBSchema             (const RWDBStatus& status);
    RWDBSchema             (const RWDBSchema& schema);
    RWDBSchema             (RWDBSchemaImp* imp);
    virtual ~RWDBSchema    ();
    RWDBSchema& operator=  (const RWDBSchema& schema);
    RWBoolean   operator== (const RWDBSchema& schema) const;

    // deep copy
    RWDBSchema clone       () const;

    // multi-threading functions
    void                   acquire() const;
    void                   release() const;

    // indexing
    RWDBColumn operator[]  (size_t position) const;
    RWDBColumn operator[]  (const RWCString& name) const;
    RWDBColumn column      (size_t position) const;
    RWDBColumn column      (const RWCString& name) const;
    RWDBColumn column      (const RWCString& name,
                                  RWCString::caseCompare caseCompare ) const;
    RWCString  columnName  (size_t position) const;

    size_t index           (const RWCString& name ) const;
    size_t index           (const RWCString& name,
                                  RWCString::caseCompare caseCompare ) const;
    size_t index           (const RWDBColumn& column) const;
    RWCString       keysAsString(const RWDBPhraseBook& pb);
    RWCString       keysAsString(); 
    // accessors
    size_t entries         () const;
    RWBoolean isEmpty      () const;

    // mutators
    RWDBSchema&     foreignKey(const RWDBForeignKey& fk);
    RWDBSchema&     primaryKey(const RWDBSchema& fk);
    RWDBSchema      primaryKey();

    RWDBColumn appendColumn (const RWCString&      name,
                             RWDBValue::ValueType  type = RWDBValue::NoType,
                             long                  storageLength =RWDB_NO_TRAIT,
                             int                   nativeType = RWDB_NO_TRAIT,
                             int                   precision = RWDB_NO_TRAIT, 
                             int                   scale = RWDB_NO_TRAIT,
                             RWBoolean             nullAllowed = TRUE,
                             RWDBColumn::ParamType paramType =
                                               RWDBColumn::notAParameter);
    RWDBColumn appendColumn (const RWDBColumn& column);

    // format self as SQL string
    RWCString asString     () const;
    RWCString asString     (const RWDBPhraseBook& phrasebook) const;

    // error handling
    void                     setErrorHandler(RWDBStatus::ErrorHandler handler);
    RWDBStatus::ErrorHandler errorHandler() const;
    RWBoolean                isValid() const;
    RWDBStatus               status() const;

private:
    RWDBSchemaImp* impl_;

    void cow();
    void cloneSelf();

};


#endif
