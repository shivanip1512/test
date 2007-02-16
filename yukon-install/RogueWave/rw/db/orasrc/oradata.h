#ifndef __RWDB_ORADATA_H__
#define __RWDB_ORADATA_H__

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
 * Oracle OCI access library definition of RWDBOracleData
 *
 **************************************************************************/


#include <rw/collect.h>
#include <rw/db/blob.h>
#include <rw/db/value.h>


//////////////////////////////////////////////////////////////////////////
//
// RWDBOracleData - helper class for binding buffers to Oracle
//
// NOTES:
// 1) The data buffer is actually in the RWDBBlob
// 2) All of RWDBBlob's protected parts can be used if necessary
//////////////////////////////////////////////////////////////////////////
class RWDBOracleData : public RWDBBlob {
public:
    RWDBOracleData( size_t               length,
                    osb2                 colItype,
                    RWDBValue::ValueType colRWtype,
                    osword               colEtype, 
                    RWBoolean            isUpdate = FALSE);  // if the column is a FOR UPDATE column (cursor)

    void      convertToData   ( const RWDBValue&  value,
                                      RWDBStatus& status );
    void      convertToApp    ( RWDBStatus& status );
    void      convertOciToApp ( RWDBStatus& status );
    RWCString convertToString ( RWDBPhraseBook* phrase,
                                RWDBStatus&     status );

    inline RWBoolean isUpdate() const { return isUpdate_; }

    RWDBValue::ValueType    colRWtype_;
    osb2                    colItype_;
    osword                  colEtype_;
    osb2                    colIndicator_;
    oub2                    colDataLength_;
    RWDBValue::ValueType    appType_;
    void*                   appData_;
    RWBoolean               appBound_;
    

 private :
   RWDBOracleData& operator=(const RWDBOracleData& data);
   RWBoolean               isUpdate_;
};


#endif

