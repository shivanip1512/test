#ifndef __RWDB_ODBUTILS_H__
#define __RWDB_ODBUTILS_H__

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

#include <rw/db/value.h>

#include <rw/ordcltn.h>

#ifdef __hpux // to avoid HP cma_select on mt build.
#define select select
#endif
#include <rw/hashdict.h>
#ifdef __hpux
#undef select
#endif

extern const size_t maxSize_t;
extern size_t rwdbMaxStringSize_;
extern size_t rwdbMaxBlobSize_;


// Notice these classes are NOT RWDBExport'ed'

class RWDBODCTypeList : public RWOrdered
{
public:
    ~RWDBODCTypeList() { clearAndDestroy(); }
};


class RWDBODBCLibTypeInfo : public RWCollectable
{
public:
   RWDBODBCLibTypeInfo();
   RWDBODBCLibTypeInfo( SWORD anOdbcType);
   RWBoolean isEqual(const RWCollectable* anInfo) const;

   SWORD        odbcType;
   char         driverName[RWDBODBCDRIVERNAMESIZE];
   char         createParams[RWDBODBCDRIVERNAMESIZE];
   SWORD        nullable;
};


class RWDBHashDictionary : public RWHashDictionary
{
public:
   ~RWDBHashDictionary() { clearAndDestroy(); }
};


//////////////////////////////////////////////////////////////////////////
//
// RWDBODBCLibFullName
//
//    This class stores the broken out parts of a possibly-qualified
//     object name.
//
//////////////////////////////////////////////////////////////////////////

class RWDBODBCLibFullName {
public:
    RWDBODBCLibFullName    (const RWCString& name);
    RWCString dbName        ();
    RWCString ownerName     ();
    RWCString objectName    ();
    RWCString revision      ();
private:
    RWCString dbName_;
    RWCString ownerName_;
    RWCString objectName_;
    RWCString revision_;
};



void rwdbaccessexport pascal winmsg(const char *str, const char *str2);

void rwdbaccessexport pascal winmsg(const char *str, long aNum);

void* convertToBuffer( RWDBValue::ValueType type, void *data,
                       int colSize, int& retSize);

void      bufferToData( RWDBValue::ValueType, void *data, void* buffer);
RWCString typeAndPrecision( RWDBColumn aCol,
                            RWBoolean nonNullable,
                            RWOrdered& aVec);
RWCString buildTypeNameAndPrecision( const RWDBODBCLibTypeInfo& tInfo,
                                    RWDBColumn aCol,
                                    RWBoolean nonNullable);


#endif



