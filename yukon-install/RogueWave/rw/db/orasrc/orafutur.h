#ifndef __ORA_FUTUR_H__
#define __ORA_FUTUR_H__

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
 * Oracle Access Library Implementation of Future
 *
 **************************************************************************/

#include <rw/db/orasrc/oradefs.h>
#include <rw/db/dbsrc/futurei.h>
#include <rw/db/connect.h>
#include <rw/db/orasrc/oracda.h>

#define RWDB_ORA_STAGE_1       1
#define RWDB_ORA_STAGE_2       2
#define RWDB_ORA_STAGE_3       3
#define RWDB_ORA_STAGE_4       4
#define RWDB_ORA_STAGE_5       5

class RWDBOraOciCallsData;
class RWDBOracleSystemHandle;

//////////////////////////////////////////////////////////////////////////////
//
// RWDBOraOciCalls :: Provides an interface for the asynchronous 
//   oci calls used in the oracle access library implementation. 
//
//////////////////////////////////////////////////////////////////////////////
class RWDBOraOciCalls{

public :
    enum ErrorCode { LDA_ERROR, CDA_ERROR, FETCH_ERROR, 
                     DESCRIBE_ERROR, OTHER };
    typedef RWDBOraRetCode (RWDBOraOciCalls::*OciFunctionP)(void *outArg);

    RWDBOraOciCalls( const RWDBOracleCDA& aCda );
    RWDBOraOciCalls( const RWDBOracleCDA& aCda, OciFunctionP func );
    RWDBOraOciCalls( const RWDBOracleCDA& aCda, OciFunctionP func,
                     void *outArg );
    RWDBOraOciCalls( const RWDBOracleCDA& aCda, OciFunctionP func,
                     const RWCString& sqlStmt );
    RWDBOraOciCalls( const RWDBOracleCDA& aCda, OciFunctionP func,
                     int rowsToFetch ); 

    RWDBOraOciCalls( const RWDBOraOciCalls& oci );
    RWDBOraOciCalls& operator=( const RWDBOraOciCalls& oci );

    ~RWDBOraOciCalls();

    RWDBOraRetCode pendingCall();
    void           outputArg( void *out );
    void           currentCall( OciFunctionP func );
    void           parseType( osword parseType ); 
//  void           cda(Cda_Def *cda);
    void           cda(const RWDBOracleCDA& cda);
    void           setStatus( RWDBOraRetCode ret, RWDBStatus *statusP );

    RWDBOraRetCode Oraocon(void *outArg);
    RWDBOraRetCode Oraocof(void *outArg);
    RWDBOraRetCode Oraorol(void *outArg);
    RWDBOraRetCode Oraocom(void *outArg);
    RWDBOraRetCode Oraoparse(void *outArg);
    RWDBOraRetCode Oraoexec(void *outArg);
    RWDBOraRetCode Oraoexfet(void *outArg);
    RWDBOraRetCode Oraofen(void *outArg);
    RWDBOraRetCode Oraoopen(void *outArg);
    RWDBOraRetCode Oraodescr(void *outArg);

    OciFunctionP   currentCall() const;
//  Cda_Def*       cda() const;
    const RWDBOracleCDA& cda() const;

    Lda_Def*       lda() const;
    Hda_Def*       hda() const;

private :
    void                initialize( const RWDBOracleCDA& aCda,
                                    OciFunctionP func = NULL,
                                    void *outArg = NULL, 
                                    osword parseType = ONORMALPARSE );
    RWDBOraOciCallsData *data_;
    OciFunctionP        currentCall_;
};

////////////////////////////////////////////////////////////////////////////
//
// RWDBOraOciCallsData :: Reference counted Data sink for RWDBOraOciCalls
//
////////////////////////////////////////////////////////////////////////////
class RWDBOraOciCallsData : public RWDBReference {

public :
    RWDBOracleSystemHandle        *sysHandle_;
                  
    Lda_Def                       *lda_;
    Hda_Def                       *hda_;
//  Cda_Def                       *cda_;
    RWDBOracleCDA                  cda_;

    RWCString                     sqlStmt_;
    int                           rowsToFetch_; 
    osword                        parseType_;

    void                          *outArg_;
    RWDBOraOciCalls::ErrorCode    whichError_;
};


////////////////////////////////////////////////////////////////////////////
//
// RWDBOracleFutureImp :: Oracle Access Library implementation of RWDBFuture
//
////////////////////////////////////////////////////////////////////////////
class RWDBAccessExport RWDBOracleFutureImp : public RWDBFutureImp {

public :
    RWDBOracleFutureImp( const RWDBConnection& conn,
                         const RWDBStatus&     status,
                         RWDBOraOciCalls       ociCalls,
                         short                 maxStages );
    ~RWDBOracleFutureImp();

protected :
    virtual RWBoolean      isDbReady();
    virtual void           finishDbAsync();

    virtual RWDBOraRetCode doNext();
    virtual RWDBOraRetCode doFailure();

    RWDBConnection         conn_;
    RWDBStatus             status_;
    RWDBOraOciCalls        ociCalls_;

    // Maximum number of stages to process this async call
    short                  maxStages_;
    short                  currentStage_;

    //Code returned by a completed oci call
    RWDBOraRetCode         compRetCode_;

private :
    RWDBOraRetCode  poll();
    RWDBOraRetCode  next();

    // Not implemented
    RWDBOracleFutureImp(const RWDBOracleFutureImp& imp);
    RWDBOracleFutureImp operator=(const RWDBOracleFutureImp& imp);

};//::RWDBOracleFutureImp()

#endif // __ORA_FUTUR_H__
