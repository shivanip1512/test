#ifndef __RWDB_ORASYSH_H__
#define __RWDB_ORASYSH_H__

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
 * Oracle OCI Access Library implementation of RWDBSystemHandle
 *
 **************************************************************************/



// Need full path here, since this file needs to be #included by
// an application wishing to access the Oracle OCI system handle directly.
#include  <rw/db/orasrc/oradefs.h> 

#include  <rw/cstring.h>
#include  <rw/db/syshandl.h>

class RWDBAccessExport RWDBOracleSystemHandle : public RWDBSystemHandle {

friend class RWDBAccessExport RWDBOracleConnectionImp;
friend class RWDBAccessExport RWDBOracleCDAImp;
friend class RWDBAccessExport RWDBOracleCursorBodyImp;
friend class RWDBAccessExport RWDBOracleDatabaseTableImp;
friend class RWDBAccessExport RWDBOraOciCalls;
friend class RWDBAccessExport RWDBOracleCallInterface;
friend class RWDBAccessExport RWDBOracleReaderBodyImp;
friend class RWDBAccessExport RWDBOracleSelectorImp;
friend class RWDBAccessExport RWDBOracleStoredProcImp;

public:
    RWDBOracleSystemHandle  ();
    ~RWDBOracleSystemHandle ();

    Lda_Def*                 lda() const { return lda_; }
    Hda_Def*                 hda() const { return hda_; }

    unsigned long            fetchSize() const { return fetchSize_; }
    unsigned long            fetchSize( unsigned long newSize )
      {
        unsigned long olds = fetchSize_;
        fetchSize_ = newSize;
        return olds;
      }

    unsigned long            maxStoredProcParameterSize() const;
    unsigned long            maxStoredProcParameterSize(unsigned long newSize);

    void                     parameterSize(size_t number)
      { parameterSize_ = number; }
    size_t                   parameterSize() const
      { return parameterSize_; }

protected:
    RWCString                serverName() const
      { return serverName_; }
    RWCString                userName() const
      { return userName_; }
    RWCString                dbName() const
      { return dbName_; }

private:
    RWCString                serverName_;
    RWCString                userName_;
    RWCString                dbName_;

    Lda_Def*                 lda_;
    Hda_Def*                 hda_;

    unsigned long            fetchSize_;
    unsigned long            maxParamSize_;
    size_t                   parameterSize_;


// not implemented
    RWDBOracleSystemHandle  ( const RWDBOracleSystemHandle& );
    RWDBOracleSystemHandle& operator=( const RWDBOracleSystemHandle&);
};


#endif
