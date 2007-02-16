#ifndef __RWDB_READERI_H__
#define __RWDB_READERI_H__

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
 * Declaration of class RWDBReaderImp, the base class for a family of
 * database-specific reader implementations
 *
 **************************************************************************/

#ifndef RW_NO_WSTR
#include <rw/wstring.h>
#endif

#include <rw/db/dbref.h>
#include <rw/db/status.h>
#include <rw/db/connect.h>
#include <rw/db/table.h>
#include <rw/db/mbstring.h>

#include <rw/db/dbsrc/shiftrow.h>


class RWDBExport RWDBReaderImp : public RWDBReference, public RWDBStatus {
public:
    RWDBReaderImp              ( const RWDBStatus&     status );
    RWDBReaderImp              ( const RWDBStatus&     status,
                                 const RWDBTable&      table,
                                 const RWDBConnection& connection );

    virtual                    ~RWDBReaderImp();

    // Used by RWDBReader for error checking:
    RWBoolean                   checkBounds(const RWDBShiftableRow& r,
                                            const char* where);
    RWBoolean                   checkConversion(const RWDBShiftableRow& r,
                                                RWDBValue::ValueType t);

    // This group is forwarded by RWDBReader.
    virtual RWDBStatus          status();
    virtual void*               operator()();
    virtual RWDBTable           table();
    virtual RWDBConnection      connection();

      // multi-threading functions
            void                acquire() const;
            void                release() const;
    
    virtual RWDBReaderImp&      operator>>(RWDBNullIndicator& aNullIndictator);

    virtual RWDBReaderImp&      operator>>(RWDBRow& aRow);
    virtual RWDBReaderImp&      operator>>(char& value);
    virtual RWDBReaderImp&      operator>>(unsigned char& value);
    virtual RWDBReaderImp&      operator>>(short& value);
    virtual RWDBReaderImp&      operator>>(unsigned short& value);
    virtual RWDBReaderImp&      operator>>(int& value);
    virtual RWDBReaderImp&      operator>>(unsigned int& value);
    virtual RWDBReaderImp&      operator>>(long& value);
    virtual RWDBReaderImp&      operator>>(unsigned long& value);
    virtual RWDBReaderImp&      operator>>(float& value);
    virtual RWDBReaderImp&      operator>>(double& value);
    virtual RWDBReaderImp&      operator>>(RWDecimalPortable& value);
    virtual RWDBReaderImp&      operator>>(RWDate& value);
    virtual RWDBReaderImp&      operator>>(RWTime& value);
    virtual RWDBReaderImp&      operator>>(RWDBDateTime& value);
    virtual RWDBReaderImp&      operator>>(RWDBDuration& value);
    virtual RWDBReaderImp&      operator>>(RWCString& value);
    virtual RWDBReaderImp&      operator>>(RWDBBlob& value);
    virtual RWDBReaderImp&      operator>>(RWDBValue& value);
    virtual RWDBReaderImp&      operator>>(RWDBMBString& value);
#ifndef RW_NO_WSTR
    virtual RWDBReaderImp&      operator>>(RWWString& value);
#endif

    virtual int                 shiftInPosition();
    virtual int                 setShiftInPosition(int pos);
    virtual int                 shiftOutPosition();
    virtual void                setShiftOutPosition(size_t pos);
    virtual void                setShiftOutPosition(const RWCString& value);
    virtual void                setShiftOutPosition(const RWDBColumn& value);
    virtual RWDBShiftableRow&   currentRow();

protected:
    virtual RWBoolean           fetchRow(RWDBShiftableRow* valueList);

    RWDBTable                   table_;
    RWDBConnection              connection_;
    RWDBShiftableRow            valueList_;  // List of RWDBValues.

      // multi-threading data
    RWDBMutex                   mutex_;

private:
// Not implemented
    RWDBReaderImp(const RWDBReaderImp&);
    RWDBReaderImp& operator=(const RWDBReaderImp&);
};


class RWDBExport RWDBMemReaderImp : public RWDBReaderImp {
public:
    RWDBMemReaderImp           (const RWDBTable&   table,
                                const RWDBStatus&  status );
    virtual ~RWDBMemReaderImp  ();

protected:
    virtual RWBoolean          fetchRow( RWDBShiftableRow* row );
    size_t                     rowEntry_;

private:
// Not implemented
    RWDBMemReaderImp(const RWDBMemReaderImp&);
    RWDBMemReaderImp& operator=(const RWDBMemReaderImp);
};

#endif
