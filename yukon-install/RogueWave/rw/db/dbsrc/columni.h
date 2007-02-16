#ifndef __RWDB_COLUMNI_H__
#define __RWDB_COLUMNI_H__

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
#include <rw/db/column.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBColumnImp
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBColumnImp : public RWDBReference, public RWDBStatus {
public:
    RWDBColumnImp( const RWDBStatus& status );
    RWDBColumnImp( const RWDBStatus&, const RWCString&,
                    RWDBValue::ValueType = RWDBValue::NoType, 
                    int = RWDB_NO_TRAIT,
                    long = RWDB_NO_TRAIT, 
                    int = RWDB_NO_TRAIT, 
                    int = RWDB_NO_TRAIT,
                    RWBoolean = TRUE,
                    RWDBColumn::ParamType = RWDBColumn::notAParameter,
                    int = RWDB_NO_TRAIT);
    virtual ~RWDBColumnImp();

    virtual RWCString             name() const;
    virtual RWCString             qualifiedName() const;
    virtual RWBoolean             nullAllowed() const;
    virtual RWDBValue::ValueType  type() const;
    virtual int                   nativeType() const;
    virtual int                   auxiliaryType() const;
    virtual long                  storageLength() const;
    virtual int                   precision() const;
    virtual int                   scale() const;
    virtual RWDBColumn::ParamType paramType() const;
    virtual RWDBTable             table() const;

    virtual void                  name (const RWCString&);
    virtual void                  nullAllowed (RWBoolean);
    virtual void                  type (RWDBValue::ValueType);
    virtual void                  nativeType(int);
    virtual void                  auxiliaryType(int);
    virtual void                  storageLength (long);
    virtual void                  precision (int);
    virtual void                  scale (int);
    virtual void                  paramType (RWDBColumn::ParamType);
    virtual void                  table (const RWDBTable&);

    virtual void                  clearTable ();

      // multi-threading functions
            void                  acquire() const;
            void                  release() const;

private:
    RWCString             name_ ;
    RWDBValue::ValueType  type_;
    int                   nativeType_;
    int                   auxiliaryType_;
    long                  storageLength_;
    long                  precision_;
    long                  scale_;
    RWDBTable*            associatedTable_;
    RWDBColumn::ParamType paramType_;
    RWBoolean             nullAllowed_;

      // multi-threading data
    RWDBMutex             mutex_;

// not implemented
    RWDBColumnImp( const RWDBColumnImp& );
    RWDBColumnImp& operator=( const RWDBColumnImp& );

};

#endif
