#ifndef __RWDB_ODBCSTMT_H__
#define __RWDB_ODBCSTMT_H__

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
 **************************************************************************
 *
 * ODBC Access Library implementation of RWDBStatementImp
 *
 **************************************************************************/


#include <rw/db/dbsrc/stmti.h>
#include <rw/db/odbcsrc/odbsysh.h>
#include <rw/db/odbcsrc/odbsqlda.h>
#include <rw/db/odbcsrc/odbhstmt.h>

class RWDBODBCLibStatementImp : public RWDBStatementImp {

  public:

    RWDBODBCLibStatementImp();
    virtual ~RWDBODBCLibStatementImp();

      // Statement stuff
    virtual void            cancel(const RWDBConnection&, RWDBStatus&, RWBoolean changed);
    virtual RWDBResultImp * parse(const RWCString& sql,
                              const RWDBConnection& conn,
                              RWDBStatus&);
    virtual RWDBResultImp * bind(const RWDBConnection&, RWDBStatus&);
    virtual RWDBResultImp * perform(const RWDBConnection&, RWDBStatus&);
//    virtual void            clear(const RWDBConnection&, RWDBStatus&);
    
    virtual void doFinalBind(const RWDBConnection& conn,
                             RWDBStatus& status );

    virtual void        clearBoundObjects(void);
    virtual RWCString newPlaceHolder(void);

    virtual RWBoolean needsPlaceHolder(const RWDBValue&) const;
    virtual RWBoolean needsPlaceHolder(RWDBValue::ValueType,
                                       void *data) const;
    virtual RWCString addBoundObject(const RWDBBoundObject& obj);
    virtual RWCString addBoundObject(const RWCString& place,
                              void *data,
                              RWDBBoundObject::PtrType type,
                              size_t len, int nativeType=-1);

  protected:

    // implementations are required to override this behavior
    virtual void doBind( RWDBStatus&, const RWCString&,
                         char *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         short *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         int *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         long *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         float *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         double *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWCString *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBMBString *, size_t);
#ifndef RW_NO_WSTR
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWWString *, size_t);
#endif
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBBlob *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDecimalPortable *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDate *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBDateTime *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBDuration *, size_t);

    void putBlob(const RWDBBlob& blob, RWDBODBCLibHSTMT *hstmt);
    void putData(const char *data, size_t length, RWDBODBCLibHSTMT *hstmt);
 
  private:

      // implementation specific data
    RWDBODBCLibSystemHandle  *handle_;

    SQLDA                    *sqlda_;
    RWDBODBCLibHSTMT         hstmt_;
    //RWDBODBCLibHSTMT         *hstmt_;
   
    static long          cbValue_;
    static const size_t bufSize_;
 
      // not implemented
    RWDBODBCLibStatementImp(const RWDBODBCLibStatementImp&);
    RWDBODBCLibStatementImp& operator=(const RWDBODBCLibStatementImp&);
    
};


#endif

