#ifndef __RWDB__ODBVAL_H__
#define __RWDB__ODBVAL_H__

/***************************************************************************
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

#include <rw/db/odbcsrc/odbdefs.h>
#include <rw/db/odbcsrc/odbutils.h>

#include <rw/db/value.h>


class RWDBODBCLibValue : public RWDBValue {
public:

    RWDBODBCLibValue(RWDBValue::ValueType type);

    static RWDBValue::ValueType toValueType( int ODBCType,
                                          RWBoolean isSigned = TRUE );
    static int                  toODBC_C_Type( RWDBValue::ValueType type );         
    static int                  toODBCType( RWDBValue::ValueType type );
    static RWCString            toSQLPhrase( RWDBValue::ValueType type,
                                          const RWDBPhraseBook& pb );
//    static int                  RWDBODBCLibValue::toClosestSupportedODBCType(
    static int                  toClosestSupportedODBCType(
                                     RWDBValue::ValueType type,
//                                     RWDBConnection connection,
                                     RWOrdered* aVec,
                                     long length );
    static int                  getTypeInfo( RWDBODBCLibTypeInfo* aVec,
                                          int vecMax,
                                          int& vecSize,
                                          RWDBConnection connection );
    static RWDBValue            toRWDBValue(RWDBValue::ValueType, void *data,
                                            RWBoolean isNull);

private:
// Not Implemented:
    RWDBODBCLibValue        ( const RWDBODBCLibValue& );
    RWDBODBCLibValue&        operator=( const RWDBODBCLibValue& );
};

#endif


