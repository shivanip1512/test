#ifndef __RWDB_SCHEMAI_H__
#define __RWDB_SCHEMAI_H__

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
 * Definition of class RWDBSchemaImp, the reference-counted implementation
 * of class RWDBSchema.
 * A schema is implemented as an RWOrdered (collection) of collectable
 * RWDBColumns.  Class RWDBCollectableColumn is defined here as well.
 *
 * Note: this is NOT a base class for database-specific variants.
 *
 **************************************************************************/

#include <rw/db/schema.h>
#include <rw/db/dbref.h>
#include <rw/db/status.h>
#include <rw/db/forkey.h>

#include <rw/db/column.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBSchemaImp
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBSchemaImp : public RWDBReference, public RWOrdered,
                                 public RWDBStatus {
public:
    RWDBSchemaImp( const RWDBStatus& );
    virtual ~RWDBSchemaImp();

    virtual RWDBSchemaImp*      clone() const;  // for deep copies

      // multi-threading functions
            void                acquire() const;
            void                release() const;

    virtual RWDBColumn          column( size_t ) const;
    virtual RWDBColumn          column( const RWCString& name,
                                              RWCString::caseCompare cc ) const;
    virtual RWCString           columnName(size_t) const;

    virtual size_t              locate( const RWCString& name,
                                             RWCString::caseCompare cc ) const;

    virtual RWDBColumn          appendColumn( const RWCString& name,
                                              RWDBValue::ValueType type,
                                              long storageLength,
                                              int nativeType,
                                              int precision,
                                              int scale,
                                              RWBoolean nullAllowed,
                                              RWDBColumn::ParamType paramType );
    virtual RWDBColumn          appendColumn( const RWDBColumn& );
    virtual RWCString           keysAsString(const RWDBPhraseBook& pb);

    virtual RWCString           asString( const RWCString& delimiter, 
                                          const RWCString& whiteSpace ) const;
    virtual RWDBForeignKey      foreignKey( const RWDBForeignKey& fk );
    virtual RWDBSchema          primaryKey( const RWDBSchema& pk );
    virtual RWDBSchema          primaryKey();

private:
    RWDBForeignKeyList               fkList_; 
    RWDBSchema*                      pkPtr_;

    RWDBMutex                        mutex_;

// not implemented:
    RWDBSchemaImp( const RWDBSchemaImp& impl );
    RWDBSchemaImp& operator=( const RWDBSchemaImp& impl );
};

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBCollectableColumn
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBCollectableColumn : public RWDBColumn, public RWCollectable
{
public:
   RWDBCollectableColumn (const RWDBColumn& c);
   virtual ~RWDBCollectableColumn();

   // Redefined from RWOrdered:
   virtual int        compareTo(const RWCollectable* v) const;
   virtual RWBoolean  isEqual(const RWCollectable* v) const;

   // Inherited from RWOrdered:
   //virtual RWspace  binaryStoreSize() const;
   //virtual unsigned hash() const;
   //virtual void     saveGuts(RWFile& s) const;
   //virtual void     saveGuts(RWvostream& s) const;
   //virtual void     restoreGuts(RWFile& s);
   //virtual void     restoreGuts(RWvistream& s);
};

#endif
