#ifndef __RWDB_STOREDI_H__
#define __RWDB_STOREDI_H__

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

#include <rw/db/dbref.h>
#include <rw/db/status.h>
#include <rw/db/value.h>
#include <rw/db/blob.h>
#include <rw/db/column.h>
#include <rw/db/dbase.h>
#include <rw/db/schema.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBParamValue
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBParamValue : public RWDBValue {
public:
    RWDBParamValue             ();
    RWDBParamValue             ( const RWDBValue& value,
                                 void* pAddr = 0 );
    RWDBParamValue             ( const RWDBValue& value,
                                 void* pAddr,
                                 const RWDBBlob& blob );
    virtual ~RWDBParamValue    ();

    RWDBColumn::ParamType      pType() const;
    RWBoolean                  isSet() const;
    void                       unSet();
    RWBoolean                  isNull() const;
    void                       isNull( RWBoolean toggle );
    void*                      pAddr() const;
    void                       unSetAddr();
    RWDBBlob                   pBlob() const;

private:
    RWDBColumn::ParamType      pType_;
    RWBoolean                  isSet_;
    RWBoolean                  isNull_;
    void*                      pAddr_;
    RWDBBlob                   pBlob_;

    // unimplemented
    RWDBParamValue             (const RWDBParamValue&);
    RWDBParamValue& operator=  (const RWDBParamValue&);
};

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBArgList
//
//    Used to store actual arguments.
//                                                                     
//////////////////////////////////////////////////////////////////////////
typedef RWDBParamValue *RWDBParamValueP;
class RWDBExport RWDBArgList {
public:
    RWDBArgList                   (size_t size = 0);
    virtual ~RWDBArgList          ();
    void reshape                  (size_t size);
    RWDBParamValueP operator[]    (size_t i);
    void replaceAt                (size_t i, RWDBParamValueP);
        void insertAt (int k, RWDBParamValueP element);

        size_t entries();
    void   entries(size_t n);

private:
    size_t npts_;
    RWDBParamValueP* array_;
        size_t           entries_;

    // unimplemented
    RWDBArgList(const RWDBArgList&);
    RWDBArgList& operator=(const RWDBArgList&);
};

//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBStoredProcImp
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBStoredProcImp : public RWDBReference, public RWDBStatus {

public:
    RWDBStoredProcImp           ( const RWDBStatus& status );
    RWDBStoredProcImp           ( const RWDBStatus&   status,
                                  const RWDBDatabase& dbase,
                                  const RWCString&    name );
    virtual ~RWDBStoredProcImp  ();

// accessors
    virtual RWDBDatabase        database() const;
    virtual RWCString           name() const;

// multi-threading functions
            void                acquire() const;
            void                release() const;

// DDL methods
    virtual RWCString           text(const RWDBConnection& connection,
                                       RWBoolean forceLookup);
    virtual RWDBSchema          params(const RWDBConnection& connection,
                                       RWBoolean forceLookup);
    virtual RWBoolean           exists(const RWDBConnection& connection,
                                       RWBoolean forceLookup);
    virtual RWDBStatus          drop(const RWDBConnection& connection);

// execution methods
    virtual RWDBResultImp*      execute(const RWDBConnection& connection);
    virtual RWDBStatus          fetchReturnParams();
    void                        rebindParam( unsigned index );
    virtual RWDBValue           returnValue();
    virtual RWBoolean           isNull( size_t position );

// helper functions
    virtual void                setPosition( size_t position );
    virtual void                addParamValue( const RWDBValue& value,
                                               void* paramAddress = 0 );
    virtual int                 paramIndex( const RWCString& paramName ) const;

protected:

    RWDBDatabase                database_;
          RWBoolean                   storedProcCheckParams_;
    RWCString                   name_;
    RWDBSchema                  paramList_;
    RWDBArgList                 argList_;
    size_t                      position_;
    RWBoolean                   schemaFetched_;
    RWBoolean                   returnsFetched_;
    RWDBValue                   returnValue_;
    RWCString                   text_;

// multi-threading data
    RWDBMutex                   mutex_;

    void                        reset();
    
private:
// not implemented
    RWDBStoredProcImp           ( const RWDBStoredProcImp& );
    RWDBStoredProcImp&          operator=( const RWDBStoredProcImp& );
};

#endif

