#ifndef __RWDB_BLOB_H__
#define __RWDB_BLOB_H__

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
 * Definition of class RWDBBlob
 *
 * This class provides interfaces to manipulate unknown blocks of data,
 * roughly corresponding to the BinaryLargeOBjects defined by many RDBMS's.
 * Like the RDBMS's BLOB, RWDBBlob adds no semantics to the data it stores.
 * Applications may well wish to derive from RWDBBlob to add semantics.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/collect.h>

#include <rw/db/dbref.h>

/*
 ***************************************************************************
 * RWDBBlobData - reference counted data storage for manipulation by RWDBBlob
 *
 */

class RWDBExport RWDBBlobData : public RWDBReference
{
    static RWDBBlobData*    newData(size_t allocation);
    static RWDBBlobData*    newData(void* data, size_t length);
    void                    deleteData();

    // Accessors
    size_t                  length() const;
    size_t                  capacity() const;
    RWspace                 binaryStoreSize() const;
    unsigned char*          data() const;

    // Manipulators
    void                    setLength( size_t length );

    // Controls
    RWBoolean               allocated_; // allocated by us?
    size_t                  length_;    // length of data in the buffer
    size_t                  capacity_;  // allocate size of data buffer
    unsigned char*          data_;      // data buffer

      // multi-threading data
    RWDBMutex               mutex_;

    // Friends
    friend class RWDBExport RWDBBlob;
};

/*
 ***************************************************************************
 */

class RWDBExport RWDBBlob : public RWCollectable
{
    RWDBDECLARE_COLLECTABLE(RWDBBlob);

public:

    // constructors
    RWDBBlob                ();
    RWDBBlob                (size_t size);
    RWDBBlob                (void* data, size_t length);
    RWDBBlob                (const RWDBBlob& blob);

    // destructor
    virtual ~RWDBBlob       ();

    // public member functions
    void                     clear(size_t size = 0);
    void                     getBytes(void *buffer, size_t size,
                                                   size_t offset = 0 ) const;
    void                     putBytes(const void *buffer, size_t size,
                                     size_t offset = 0, size_t resize = 256);
    size_t                   length() const;
    size_t                   capacity() const;
    unsigned char*           data() const;    // returns pointer to actual data

    // Redefined from RWCollectable
    virtual RWspace          binaryStoreSize() const;
    virtual int              compareTo(const RWCollectable* c) const;
    virtual RWBoolean        isEqual(const RWCollectable* c) const;
    virtual unsigned         hash() const;
    virtual void             saveGuts(RWFile& file) const;
    virtual void             saveGuts(RWvostream& stream) const;
    virtual void             restoreGuts(RWFile& file);
    virtual void             restoreGuts(RWvistream& stream);

      // multi-threading operations
            void             acquire() const;
            void             release() const;

    // public member operators
    RWDBBlob&        operator=(const RWDBBlob& blob);
    RWBoolean        operator==(const RWDBBlob& blob) const  {
			return isEqual(&blob); 
		     }

protected:
    RWDBBlobData*            data_;

};

#endif
