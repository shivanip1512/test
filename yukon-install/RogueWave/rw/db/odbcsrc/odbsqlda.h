#ifndef __RWDB_ODBSQLDA_H__
#define __RWDB_ODBSQLDA_H__

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


class SQLVAR {
public:
    short   sqltype;
    SDWORD  sqllen;
    SWORD   sqlctype;
    SDWORD  sqlBytesAvailable;
    char   *sqldata;
    SWORD   scale;
    UDWORD  precision;
    SWORD   nullable;
    struct {
        SWORD sqlnamel;
        char  sqlnamec[34];
    } sqlname;
};

// NOTE:  This class has recently been modified to include a default
// constructor.  See bug #9885 for details.  Basically, the default
// constructor assumes that memory allocation (for sqlvar) will be done,
// later in the describe() method.  The other constructor dynamically
// pre-allocates an arrary of numvars (forsqlvar).  If you're about to use 
// this class, be careful of the two modes.
class SQLDA {
public:
    SQLDA();
    SQLDA(int numVars);
    ~SQLDA();

    short           sqln;
    short           sqld;
    SQLVAR*         sqlvar;
};

class RWDBODBCLibAppData{
public:
    RWDBODBCLibAppData() ;
    ~RWDBODBCLibAppData();

    friend class RWDBODBCLibSqlda;

private:
    void                 *appData_;
    RWDBValue::ValueType  appType_;
};

class  RWDBODBCLibSqlda {
public:
    RWDBODBCLibSqlda();
    ~RWDBODBCLibSqlda();

    RWDBSchema& schema(RWDBSchema& aSchema) const;
    int         numColumns() const;
    RETCODE     describe(RWDBHSTMT hstmt);
    void        appendValue(RWDBValue::ValueType type, void* appData);
    RETCODE     allocate(const RWDBConnection& conn, RWDBHSTMT hstmt, int isScrollingCursor);
    RETCODE     allocateNoBind(const RWDBConnection& conn, RWDBHSTMT hstmt);
    void        allocate(RWDBValue* val, int indx,
                         RWBoolean nullAllowed, void* pAddr);

    void        sqldaToParams();
    void        bindToSqldaAndThenToParams(RWDBHSTMT hstmt, int isScrollingCursor);

    RETCODE     paramsToSqlda(RWDBHSTMT hstmt, RWBoolean supportsDateTime,
                          int* updateParamIndex, int numParams);
    void        deallocate();
    SQLDA*      sqlda();
    int         position() const;
    int         position(int newPos);
//     void allocate(RWDBValue* val, int indx, RWBoolean nullAllowed);
//     void paramToSqlda(RWDBValue* val, int indx);

    SQLDA anSQLDA_;

private:
static int rwdbSqlVarCount;

    int                 position_;
    RWDBODBCLibAppData* appDat_;
    RWBoolean*          allocated_;
};

// Global functions.
RWDBDateTime
rwdbODBCToRWDBDateTime(TIMESTAMP_STRUCT* dateStruct);

RWDate
rwdbODBCToRWDate(TIMESTAMP_STRUCT* dateStruct);

void rwdbDateToODBC(DATE_STRUCT* ts, RWDate& dt);

void
rwdbDateTimeToODBC(TIMESTAMP_STRUCT* ts, RWDBDateTime& dt);



#endif

