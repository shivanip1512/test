#ifndef __RWDB_COLUMN_H__
#define __RWDB_COLUMN_H__

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


#include <rw/db/defs.h>
#include <rw/db/status.h>
#include <rw/db/value.h>


//////////////////////////////////////////////////////////////////////////
//
//  RWDBColumn
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBColumn
{
public:
    RWDBColumn              ();
    RWDBColumn              (RWDBColumnImp* imp);
    RWDBColumn              (const RWDBColumn& column);
    RWDBColumn&             operator=(const RWDBColumn& column);
    virtual ~RWDBColumn     ();
    RWDBColumn              clone() const;

    enum ParamType {notAParameter, inParameter, outParameter, inOutParameter};

    RWCString               name() const;

    RWCString               qualifiedName() const;
    long                    storageLength() const;
    int                     precision() const;
    int                     scale() const;
    RWBoolean               nullAllowed() const;
    RWDBValue::ValueType    type() const;
    int                     nativeType() const;
    ParamType               paramType() const;
    RWDBTable               table() const;


    RWDBColumn&             name(const RWCString& name);
    RWDBColumn&             nullAllowed(RWBoolean nullAllowed);
    RWDBColumn&             type(RWDBValue::ValueType type);
    RWDBColumn&             nativeType(int type);
    RWDBColumn&             storageLength(long length);
    RWDBColumn&             precision(int precision);
    RWDBColumn&             scale(int scale);
    RWDBColumn&             paramType(RWDBColumn::ParamType type);
    RWDBColumn&             table(const RWDBTable& table);

    RWDBColumn&             clearTable ();

    RWDBCriterion           isNull() const;
    RWDBCriterion           like(const RWDBExpr& expr) const;
    RWDBCriterion           matchUnique(const RWDBExpr& expr) const;
    RWDBCriterion           between(const RWDBExpr& expr1,
                                    const RWDBExpr& expr2) const;
    RWDBCriterion           in(const RWDBExpr& expr) const;
    RWDBCriterion           leftOuterJoin(const RWDBExpr& expr) const;
    RWDBCriterion           rightOuterJoin(const RWDBExpr& expr) const;
    RWDBCriterion           fullOuterJoin(const RWDBExpr& expr) const;
    RWDBAssignment          assign(char value) const;
    RWDBAssignment          assign(unsigned char value) const;
    RWDBAssignment          assign(short int value) const;
    RWDBAssignment          assign(unsigned short int value) const;
    RWDBAssignment          assign(long int value) const;
    RWDBAssignment          assign(unsigned long int value) const;
    RWDBAssignment          assign(int value) const;
    RWDBAssignment          assign(unsigned int value) const;
    RWDBAssignment          assign(float value) const;
    RWDBAssignment          assign(double value) const;
    RWDBAssignment          assign(const char* value) const;
#ifndef RW_NO_WSTR
    RWDBAssignment          assign(const wchar_t* value) const;
#endif
    RWDBAssignment          assign(const RWCString& value) const;
    RWDBAssignment          assign(const RWDBMBString& value) const;
#ifndef RW_NO_WSTR
    RWDBAssignment          assign(const RWWString& value) const;
#endif
    RWDBAssignment          assign(const RWDate& value) const;
    RWDBAssignment          assign(const RWTime& value) const;
    RWDBAssignment          assign(const RWDBDateTime& value) const;
    RWDBAssignment          assign(const RWDBDuration& value) const;
    RWDBAssignment          assign(const RWDecimalPortable& value) const;
    RWDBAssignment          assign(const RWDBBlob& value) const;
    RWDBAssignment          assign(const RWDBExpr& expr) const;
    RWDBAssignment          assign(RWDBValueManip manip ) const;


    void                     setErrorHandler(RWDBStatus::ErrorHandler handler);
    RWDBStatus::ErrorHandler errorHandler() const;
    RWBoolean                isValid() const;
    RWDBStatus               status() const;
    RWBoolean                isEquivalent(const RWDBColumn& column) const;

      // multi-threading support
    void                    acquire() const;
    void                    release() const;

    // allow use of a second data type ID
    int                     auxiliaryType() const;
    RWDBColumn&             auxiliaryType(int type);

private:
    RWDBColumnImp* impl_;

};

#endif 
