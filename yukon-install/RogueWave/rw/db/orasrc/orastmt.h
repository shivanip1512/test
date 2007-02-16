#ifndef __RWDB_ORASTMT_H__
#define __RWDB_ORASTMT_H__

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
 * Oracle OCI Access Library implementation of RWDBStatementImp
 *
 **************************************************************************/


#include <rw/db/dbsrc/stmti.h>
#include <rw/db/orasrc/oraoci.h>
#include <rw/db/orasrc/orafutur.h>
#include <rw/db/orasrc/oracda.h>

class RWDBAccessExport RWDBOracleStatementImp : public RWDBStatementImp {
friend class RWDBAccessExport RWDBOracleStmtFutureImp;

  public:

    RWDBOracleStatementImp();
    virtual ~RWDBOracleStatementImp();

// execution stuff
    virtual void            cancel(const RWDBConnection&, RWDBStatus&,
                                   RWBoolean changed = FALSE);
    virtual RWDBResultImp * parse(const RWCString&, const RWDBConnection&,
                                  RWDBStatus&);
    virtual RWDBResultImp * bind(const RWDBConnection&, RWDBStatus&);
    virtual RWDBResultImp * perform(const RWDBConnection&, RWDBStatus&);
    
    virtual void doFinalBind( const RWDBConnection& conn, RWDBStatus& status );

    virtual RWCString newPlaceHolder(void);

    virtual RWBoolean needsPlaceHolder(const RWDBValue&) const;
    virtual RWBoolean needsPlaceHolder(RWDBValue::ValueType, void *) const;
    RWDBOracleCDA  getCda() const ;

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
                         RWDBDuration *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDate *, size_t);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBDateTime *, size_t);

    void         bindDate(       RWDBStatus&   status,
                           const RWCString&    place,
                           const RWDBDateTime& date,
                                 void**        userdata,
                                   size_t         pos );
    void         bindDecimal(       RWDBStatus&   status,
                              const RWCString&    place,
                              const RWDecimalPortable& decimal,
                                    void**        userdata,
                                   size_t         pos );
                                     
// this is protected so that other statements can be created
// to handle special cases (such as inserter)
    RWDBOracleCDA            aCda_;
    RWBoolean                needCancel_;
    
  private:

// implementation specific data
    RWDBOracleSystemHandle  *handle_;
    osb2                    *nullIndicator_;

    RWDBResultImp* getDefaultImp();

// not implemented
    RWDBOracleStatementImp(const RWDBOracleStatementImp&);
    RWDBOracleStatementImp& operator=(const RWDBOracleStatementImp&);
    
};

//////////////////////////////////////////////////////////////////////////
//
// RWDBOracleStmtFutureImp :: Stmt specific future imp
//
//////////////////////////////////////////////////////////////////////////
class RWDBAccessExport RWDBOracleStmtFutureImp : public RWDBOracleFutureImp{
public :
    RWDBOracleStmtFutureImp( const RWDBConnection&  conn,
                             const RWDBStatus&      status,
                             RWDBOraOciCalls        oci,
                             short                  stages,
                             RWDBStatement          stmt );
    ~RWDBOracleStmtFutureImp();

protected :
    RWDBOraRetCode           doNext();

private :
    RWDBStatement            stmt_;
};

#endif
