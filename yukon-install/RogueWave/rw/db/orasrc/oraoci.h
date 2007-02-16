#ifndef __RWDB_ORAOCI_H__
#define __RWDB_ORAOCI_H__

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
 * Oracle OCI Access Library Utility Methods
 *
 **************************************************************************/


#include <rw/db/orasrc/orasysh.h>
#include <rw/db/orasrc/orares.h>
#include <rw/db/orasrc/orafutur.h>

#include <rw/db/result.h>

class RWDBOracleCDA;

class RWDBAccessExport RWDBOracleCallInterface {

public:
// execution methods

    static
    RWBoolean               changeToAsync( Lda_Def *lda );

    static
    RWBoolean               changeToSync( Lda_Def *lda );

    static
    RWBoolean               testForAsync( Lda_Def *lda );


    static
    RWDBResultImp*          execute( const RWDBOracleCDA&  aCda,
                                     RWDBStatus *status,      RWDBStatement& stmt);

    static
    RWDBResultImp*          executeSql( const RWCString&      statement,
                                              RWDBConnection& conn,
                                              RWDBStatus*     status, RWDBStatement&  stmt );
    static
    RWDBOraRetCode          doExecuteSql( const RWDBOracleCDA&   aCda,
                                                RWDBStatus       *resStatus );
    static
    RWDBOraRetCode          doExecute( const RWDBOracleCDA&   aCda,
                                             RWDBStatus       *resStatus );
       static
    RWDBOraRetCode                 parse(       osword          type,
                                   const RWCString&      statement,
                                   const RWDBOracleCDA&  aCda,
                                         RWDBStatus*     status );
    static
    void                    trace( const RWCString&      statement,
                                   const RWDBConnection& conn );

    static
    void                    syncCancel( const RWDBOracleCDA& aCda );

// error conversions
    static
    void                    ldaError( Lda_Def*         lda,
                                      const RWCString& serverName,
                                      RWDBStatus*      status );
    static
    void                    cdaError( Lda_Def*         lda,
                                      Cda_Def*         cda,
                                      const RWCString& serverName,
                                      RWDBStatus*      status );
    static
    void                    convertError( Lda_Def*         lda,
                                          const RWCString& serverName,
                                          oub2             returnCode,
                                          osword           osError,
                                          RWDBStatus*      status );

// bindings
    static
    void                    bindString( const RWCString*        value,
                                        osword                  position,
                                        const RWDBOracleCDA&    aCda,
                                        RWDBStatus*             status );
    static
    void                    bindBlob( const RWDBBlob*         value,
                                      osword                  position,
                                      const RWDBOracleCDA&    aCda,
                                      RWDBStatus*             status );
    static
    void                    bind( const RWDBOracleCDA&    aCda,
                                  osword                  position,
                                  oub1*                   dataptr,
                                  osword                  dataLength,
                                  osword                  dataType,
                                  RWDBStatus*             status,
                                  osb2*                   nullIndicator);
    static
    void                    bind( const RWDBOracleCDA&    aCda,
                                  const RWCString&        position, 
                                  oub1*                   dataptr,
                                  osword                  dataLength,
                                  osword                  dataType,
                                  RWDBStatus*             status ,
                                  osb2*                   nullIndicator);
    static
    void                    bind( const RWDBOracleCDA&    aCda,
                                  const RWCString&        position, 
                                  oub1*                   dataPointers,
                                  osword                  maxLength,
                                  osword                  dataType,
                                  osb2*                   indicators,
                                  oub2*                   lengths,
                                  oub2*                   errors, 
                                  oub4                    arraySize, 
                                  RWDBStatus*             status );
};

class RWDBAccessExport RWDBOracleOciFutureImp : public RWDBOracleFutureImp {

public :
    enum Caller{ ExecuteSql, ExecuteDml };

    RWDBOracleOciFutureImp( const RWDBConnection& conn,
                            const RWDBStatus&     status,
                            RWDBOraOciCalls       oci,
                            short                 stages,
                            Caller                callType
                         );
    ~RWDBOracleOciFutureImp();

protected :
    RWDBOraRetCode doNext();

private :
    Caller   callType_;

    RWDBOracleOciFutureImp( const RWDBOracleOciFutureImp& );
    RWDBOracleOciFutureImp& operator=( const RWDBOracleOciFutureImp& );
};

#endif

