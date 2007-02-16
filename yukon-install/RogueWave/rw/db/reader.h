#ifndef __RWDB_READER_H__
#define __RWDB_READER_H__

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
 * Definition  of class RWDBReader
 *
 * A reader is an object used to obtain rows of data from a table.
 * Sequential access to the next row is through operator(), whereas
 * random access within the current row is through operator[].
 * Like an iterator, a reader needs to be advanced to the first row,
 * using operator() before any data can be obtained through operator>>.
 *
 **************************************************************************/

#include <rw/db/defs.h>
#include <rw/db/inserter.h>

#ifndef RW_NO_WSTR
#include <rw/wstring.h>
#endif
#include <rw/db/mbstring.h>


class RWDBExport RWDBReader
{
public:
    RWDBReader          ();
    RWDBReader          (const RWDBReader& aReader);
    RWDBReader          (RWDBReaderImp* imp);
    RWDBReader&         operator=(const RWDBReader& aReader);
    virtual ~RWDBReader ();

// accessors
    RWDBTable           table() const;
    RWDBConnection      connection() const;

// error handling functions
    void                     setErrorHandler(RWDBStatus::ErrorHandler);
    RWDBStatus::ErrorHandler errorHandler() const;
    RWDBStatus               status() const;
    RWBoolean                isValid() const;
    RWBoolean                isReady() const;

      // multi-threading support
    void                acquire(void) const;
    void                release(void) const;

// indexing
    RWDBReader&         operator[](size_t indx);
    RWDBReader&         operator[](const RWCString& value);
    RWDBReader&         operator[](const RWDBColumn& value);

// execution
    void*               operator()();
    RWDBReader&         operator>>(RWDBNullIndicator& aNullIndictator);
    RWDBReader&         operator>>(RWDBRow& aRow);
    RWDBReader&         operator>>(char& value);
    RWDBReader&         operator>>(unsigned char& value);
    RWDBReader&         operator>>(short& value);
    RWDBReader&         operator>>(unsigned short& value);
    RWDBReader&         operator>>(int& value);
    RWDBReader&         operator>>(unsigned int& value);
    RWDBReader&         operator>>(long& value);
    RWDBReader&         operator>>(unsigned long& value);
    RWDBReader&         operator>>(float& value);
    RWDBReader&         operator>>(double& value);
    RWDBReader&         operator>>(RWDecimalPortable& value);
    RWDBReader&         operator>>(RWDate& value);
    RWDBReader&         operator>>(RWTime& value);
    RWDBReader&         operator>>(RWDBDateTime& value);
    RWDBReader&         operator>>(RWDBDuration& value);
    RWDBReader&         operator>>(RWCString& value);
    RWDBReader&         operator>>(RWDBBlob& value);
    RWDBReader&         operator>>(RWDBValue& value);
    RWDBReader&         operator>>(RWDBMBString& value);
#ifndef RW_NO_WSTR
    RWDBReader&         operator>>(RWWString& value);
#endif

#ifndef RW_TRAILING_RWEXPORT
    friend rwdbexport RWDBInserter& RWDBInserter::operator<<(RWDBReader&);
#else
    friend RWDBInserter& rwdbexport RWDBInserter::operator<<(RWDBReader&);
#endif

private:
    RWDBReaderImp*      impl_;
    RWDBShiftableRow&   currentRow();
};

#endif
