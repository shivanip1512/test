#ifndef __RW_DB_STATEMENT_H__
#define __RW_DB_STATEMENT_H__

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

#include <rw/db/result.h>
#include <rw/db/value.h>


//////////////////////////////////////////////////////////////////////////
//
// class RWDBBoundObject
//
// A BoundObject is an object that represents
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBBoundObject {

  public:
    RWDBBoundObject();
    RWBoolean operator==(const RWDBBoundObject&) const;

    enum PtrType {
      Short,      UnsignedShort,
      Int,        UnsignedInt,
      Long,       UnsignedLong,
      Float,
      Double,
      String,
      MBString,
#ifndef RW_NO_WSTR
      WString,
#endif
      DecimalPortable,
      Date,
      DateTime,
      Duration,
      Blob
    }                      type_;
    
    void                   *userdata_;
    void                   *oldAddress_;
    void                   *data_;
    size_t                 length_;
    size_t                 oldLength_;
    RWCString              place_;
    RWBoolean              *isNull_; 
        RWBoolean              oldIsNull_;
    int                    nativeType_; // For distinguishing locators.
};


class RWDBExport RWDBStatement {

  public:

    RWDBStatement();
    RWDBStatement(RWDBStatementImp *);
    RWDBStatement(const RWDBStatement&);
    ~RWDBStatement();

    RWDBStatement& operator=(const RWDBStatement&);

    void         cancel(const RWDBConnection&, RWDBStatus&);
    RWDBResult   parse(const RWCString&, const RWDBConnection&,
                       RWDBStatus&);
    RWDBResult   bind(const RWDBConnection&, RWDBStatus&);
    RWDBResult   perform(const RWDBConnection&, RWDBStatus&);
    void         clear(const RWDBConnection&, RWDBStatus&);

    RWCString    addBoundObject(const RWCString&,
                                void *,
                                RWDBBoundObject::PtrType,
                                size_t len);
    RWCString    addBoundObject(const RWDBBoundObject&);

    RWCString    newPlaceHolder(void);
    RWBoolean    needsPlaceHolder(const RWDBValue&) const;
    RWBoolean    needsPlaceHolder(RWDBValue::ValueType type,
                                  void *data) const;
// accessors
    size_t           entries() const;
    RWDBStatementImp *implementation() const;

  protected:

    RWDBStatementImp    *imp_;
    
  private:

};


#endif

