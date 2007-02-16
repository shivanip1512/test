#ifndef __RWDB_RESTABI_H__
#define __RWDB_RESTABI_H__

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
 * Definition of RWDBResultTableImp
 *
 * Base class for family of database-specific result table implementations.
 *
 **************************************************************************/

#include <rw/db/dbsrc/tablei.h>


class RWDBExport RWDBResultTableImp : public RWDBTableImp {
public:
    RWDBResultTableImp( const RWDBStatus&     status );
    RWDBResultTableImp( const RWDBStatus&     status,
                        const RWDBConnection& conn,
                        const RWCString&      name );
    virtual ~RWDBResultTableImp();

    virtual RWDBConnection connection () const;
    virtual RWDBReaderImp* readerImp( const RWDBTable&);
    virtual RWDBReaderImp* readerImp( const RWDBTable&, const RWDBConnection& );

    virtual RWDBColumn     addAColumn(const RWCString& name,
                                 RWDBValue::ValueType type = RWDBValue::NoType,
                                 long storageLength = RWDB_NO_TRAIT, 
                                 int nativeType = RWDB_NO_TRAIT,
                                 int precision = RWDB_NO_TRAIT, 
                                 int scale = RWDB_NO_TRAIT,
                                 RWBoolean nullAllowed = TRUE,
                                 RWDBColumn::ParamType paramType =
                                 RWDBColumn::notAParameter);
protected:
    RWDBConnection         theConnection_;
    RWBoolean              readerExists_;

private:
// not implemented
    RWDBResultTableImp( const RWDBResultTableImp& );
    RWDBResultTableImp& operator=( const RWDBResultTableImp& );
};

#endif
