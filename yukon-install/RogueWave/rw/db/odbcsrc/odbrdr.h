#ifndef __RWDB__ODBRDR_H__
#define __RWDB__ODBRDR_H__

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

#include <rw/db/odbcsrc/odbdefs.h>
#include <rw/db/odbcsrc/odbhstmt.h>

#include <rw/db/dbsrc/readeri.h>
#include <rw/db/odbcsrc/odbcursr.h>

class RWDBODBCNullableValue : public RWDBValue
{
public :
    RWDBODBCNullableValue( RWDBValue::ValueType t );
};

class RWDBAccessExport RWDBODBCLibReaderImp : public RWDBReaderImp {

  public:
    RWDBODBCLibReaderImp       ( const RWDBTable& table,
                                 const RWDBConnection& connection,
                                 const RWDBStatus& status,
                                 RWDBODBCLibHSTMT hstmt );
    ~RWDBODBCLibReaderImp      ();
    virtual RWBoolean          fetchRow( RWDBShiftableRow* row );
#if 0
    virtual void               setShiftOutPosition(size_t pos);
    virtual void               setShiftOutPosition(const RWDBColumn& pos);
    virtual void               setShiftOutPosition(const RWCString& pos);

    virtual RWDBReaderImp&     operator>>(RWDBNullIndicator& anInd);
    virtual RWDBReaderImp&     operator>>(RWDBRow& aRow);
    virtual RWDBReaderImp&     operator>>(char& value);
    virtual RWDBReaderImp&     operator>>(unsigned char& value);
    virtual RWDBReaderImp&     operator>>(short& value);
    virtual RWDBReaderImp&     operator>>(unsigned short& value);
    virtual RWDBReaderImp&     operator>>(int& value);
    virtual RWDBReaderImp&     operator>>(unsigned int& value);
    virtual RWDBReaderImp&     operator>>(long& value);
    virtual RWDBReaderImp&     operator>>(unsigned long& value);
    virtual RWDBReaderImp&     operator>>(float& value);
    virtual RWDBReaderImp&     operator>>(double& value);
    virtual RWDBReaderImp&     operator>>(RWDecimalPortable& value);
    virtual RWDBReaderImp&     operator>>(RWDate& value);
    virtual RWDBReaderImp&     operator>>(RWTime& value);
    virtual RWDBReaderImp&     operator>>(RWDBDateTime& value);
    virtual RWDBReaderImp&     operator>>(RWDBDuration& value);
    virtual RWDBReaderImp&     operator>>(RWCString& value);
    virtual RWDBReaderImp&     operator>>(RWDBBlob& value);
    virtual RWDBReaderImp&     operator>>(RWDBValue& value);
#endif
  private:
    RWDBODBCLibReaderImp        operator=( const RWDBODBCLibReaderImp& r );

    void                        loadRow( RWDBShiftableRow* row );
    void                        loadCursor();
    RWDBCursor                  cursor_;
    RWDBSchema                  schema_;
    void**                      dataPtr_;
    size_t                      position_;
    int                         numColumns_;
};

#endif


