#ifndef __RWDB_ORASPROC_H__
#define __RWDB_ORASPROC_H__

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
 * Oracle OCI Library implementation of RWDBStoredProcImp
 *
 **************************************************************************/


#include <rw/db/dbsrc/storedi.h>
#include <rw/db/orasrc/oracda.h>
#include <rw/db/orasrc/orafutur.h>

class RWDBAccessExport RWDBOracleStoredProcImp : public RWDBStoredProcImp {

friend class RWDBOracleSpFutureImp;
public:
    RWDBOracleStoredProcImp ( const RWDBStatus&     status,
                              const RWDBDatabase&   dbase,
                              const RWCString&      name,
                              const RWDBConnection& connection );
    RWDBOracleStoredProcImp ( const RWDBStatus&     status,
                              const RWDBDatabase&   dbase,
                              const RWCString&      name,
                              const RWDBConnection& connection,
                              const RWDBSchema&     schema );
    ~RWDBOracleStoredProcImp();

    RWCString               text( const RWDBConnection& connection,
                                  RWBoolean forceLookup = FALSE );
    RWDBSchema              params( const RWDBConnection& connection,
                                    RWBoolean forceLookup = FALSE );
    RWBoolean               exists( const RWDBConnection& connection,
                                    RWBoolean forceLookup = FALSE );
    RWDBStatus              drop( const RWDBConnection& connection );

    RWDBResultImp*          execute( const RWDBConnection& connection );
    RWDBStatus              fetchReturnParams();
    RWDBValue               returnValue();

    void                    addParamValue( const RWDBValue& value,
                                           void* paramAddress = 0 );
    RWBoolean               isNull( size_t position );

private:
    RWDBSchema              getParams( const RWCString&      name,
                                       const RWDBConnection& connection,
                                             RWDBStatus*     status );
    RWDBSchema              paramList( const RWDBConnection& connection,
                                             RWBoolean forceLookup = FALSE );
    RWCString               asString();  // includes placeholders 
    void                    convert( RWDBStatus&     status );    
    void                    bind( RWDBStatus*     status );    
    void                    reset();
    RWDBValue               convertToValue( const void *appData,
                                            const RWDBValue::ValueType appType );

// not implemented:
    RWDBOracleStoredProcImp( const RWDBOracleStoredProcImp& );
    RWDBOracleStoredProcImp& operator=( const RWDBOracleStoredProcImp& );

// private data
    enum Exists { Yes, No, DontKnow };
    RWOrdered               bindList_;
    RWBoolean               bound_;
    Exists                  exists_;
    RWBoolean               usable_;
    RWDBOracleData         *returnData_;
    RWCString               stmt_;        // SQL statement for stored procedure invocation

    RWDBOracleCDA           cda_;      // cda for statement
    RWDBOracleCDAQueue*     cdaQueue_; // cda's for cursor variables
    RWBoolean               cursorParamPresent_;    // Are there any cursor parameters
};

/////////////////////////////////////////////////////////////////////////////
//
// Stored Procedure Specific Future Imp
//
/////////////////////////////////////////////////////////////////////////////
class RWDBOracleSpFutureImp : public RWDBOracleFutureImp
{

public :
    RWDBOracleSpFutureImp( const RWDBConnection&   conn,
                           const RWDBStatus&       status,
                           RWDBOraOciCalls         oci,
                           short                   stages,
                           RWDBOracleStoredProcImp *spImp
                         );

    ~RWDBOracleSpFutureImp();

protected :
    RWDBOraRetCode          doNext();

private :
    RWDBOracleStoredProcImp *spImp_;

    RWDBOracleSpFutureImp( const RWDBOracleSpFutureImp& );
    RWDBOracleSpFutureImp& operator=( const RWDBOracleSpFutureImp& );
};

/////////////////////////////////////////////////////////////////////////////
//
// Helper class to hold stored procedure parameters. 
// This should allow run-time specification of number of parameters
// used in a stored procedure.
//
/////////////////////////////////////////////////////////////////////////////

class RWDBAccessExport RWDBOracleParamHelp {

public:
    RWDBOracleParamHelp(size_t xarraysize);
    ~RWDBOracleParamHelp();

oub2* xvorlds(size_t i=0);
oub2* xpositions(size_t i=0);
oub2* xlevels(size_t i=0);
oub2* xarglens(size_t i=0);
oub2* xtypes(size_t i=0);

osb2* xprecisions(size_t i=0);
osb2* xscales(size_t i=0);

oub1* xdefaults(size_t i=0);
oub1* xmodes(size_t i=0);
oub1* xradixs(size_t i=0);

oub4* xsizes(size_t i=0);
oub4* xspares(size_t i=0);

otext* xargnames(size_t i=0);

private:
 oub2*     oub2Pointer_;
 osb2*     osb2Pointer_;
 oub1*     oub1Pointer_;
 oub4*     oub4Pointer_;
 otext*    oTextPointer_;
 size_t    arraySize_;

};

#endif
