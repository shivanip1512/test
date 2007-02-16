#ifndef __RWDB_ORARESTB_H__
#define __RWDB_ORARESTB_H__

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
 * Oracle OCI Access Library definition of RWDBResultTableImp
 *
 **************************************************************************/

#include <rw/db/dbsrc/stmt.h>
#include <rw/db/dbsrc/restabi.h>
#include <rw/db/orasrc/orafutur.h>
#include <rw/db/orasrc/oracda.h>

class RWDBBulkReaderImp;
///////////////////////////////////////////////////////////////////////////
//
// RWDBOraDescribe :: Consists of the params for odescr an oci routine
//
///////////////////////////////////////////////////////////////////////////
typedef struct RWDBOraDescribe {
    osword    xcolumn;
    osb2      xItype;
    osb2      xprecision;
    osb2      xscale;
    osb2      xnullable;
    osb4      xmaxSize;
    osb4      xnameLen;
    osb4      xdisplaySize;
    char      xname[OCI_MAXOBJECTNAME+1];
}RWDBOraDescribe;


class RWDBAccessExport RWDBOracleResultTableBodyImp : public RWDBReference {

friend class RWDBAccessExport RWDBOracleResultTableFutureImp;

public :

    RWDBOracleResultTableBodyImp( RWDBSchema *schema,
                                  const RWDBOracleCDA& aCda );

    ~RWDBOracleResultTableBodyImp();

    void                constructTable( const RWDBConnection& conn,
                                              RWDBStatus      *status );
    void                addSchema();
    void                addAColumn( 
                            const RWCString& name,
                            RWDBValue::ValueType type = RWDBValue::NoType,
                            long storageLength = RWDB_NO_TRAIT,
                            int nativeType = RWDB_NO_TRAIT,
                            int precision = RWDB_NO_TRAIT,
                            int scale     = RWDB_NO_TRAIT,
                            RWBoolean nullAllowed = TRUE,
                            RWDBColumn::ParamType paramType =
                            RWDBColumn::notAParameter );

    const RWDBOracleCDA&   cda() const { return aCda_; }

private :
    // This is a copy of table's schema. 
    RWDBSchema          *schema_; 
    RWDBOracleCDA       aCda_;
    RWDBOraDescribe     oraDescr_;

    RWDBOracleResultTableBodyImp( const RWDBOracleResultTableBodyImp& );
    RWDBOracleResultTableBodyImp& 
            operator=(const RWDBOracleResultTableBodyImp& );
};


class RWDBAccessExport RWDBOracleResultTableImp : public RWDBResultTableImp {

public:

    // Constructs without building a table
    RWDBOracleResultTableImp   ( const RWDBStatus& status, 
                                 const RWDBOracleCDA&  aCda,
                                 const RWCString&      name,
                                       int             dummy );

    RWDBOracleResultTableImp   ( const RWDBStatus&     status,
                                 const RWDBOracleCDA&  aCda,
                                 const RWCString&      name,
                                     RWDBStatement& stmt);
    ~RWDBOracleResultTableImp  ();

    RWDBReaderImp*              readerImp( const RWDBTable& );
    RWDBReaderImp*              readerImp( const RWDBTable&,
                                           const RWDBConnection& );

    // The life of this pointer is not guranteed. Caller should ref. count
    RWDBOracleResultTableBodyImp* body() const;

        RWDBBulkReaderImp* bulkReaderImp(const RWDBTable&, const RWDBConnection&);

    const RWDBOracleCDA& cda() const { return resTabBodyImp_->cda(); }

private:
    RWDBOracleResultTableBodyImp *resTabBodyImp_;
        RWDBStatement stmt_;

    // not implemented
    RWDBOracleResultTableImp   ( const RWDBOracleResultTableImp& );
    RWDBOracleResultTableImp&   operator=(const RWDBOracleResultTableImp& );
};

class RWDBAccessExport RWDBOracleResultTableFutureImp : public RWDBOracleFutureImp{

public :
    RWDBOracleResultTableFutureImp( 
                             const RWDBConnection&        conn,
                             const RWDBStatus&            status,
                             RWDBOraOciCalls              oci,
                             short                        stages,
                             RWDBOracleResultTableBodyImp *resTabBodyImp_
                             );
    ~RWDBOracleResultTableFutureImp();

protected :
    RWDBOraRetCode               doNext();
    RWDBOraRetCode               doFailure();

private :
    RWDBOracleResultTableBodyImp *resTabBodyImp_;

    // Not implemented
    RWDBOracleResultTableFutureImp(const RWDBOracleResultTableFutureImp& );
    RWDBOracleResultTableFutureImp& 
               operator=(const RWDBOracleResultTableFutureImp& );
};

#endif

