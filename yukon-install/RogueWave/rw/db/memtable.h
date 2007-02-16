#ifndef __RWDB_MEMTABLE_H__
#define __RWDB_MEMTABLE_H__

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
 * RWDBMemTable represents tabular data stored in program memory.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/table.h>

class RWDBExport RWDBMemTable : public RWDBTable
{ public:
    RWDBMemTable( size_t = 0 );
    RWDBMemTable( const RWDBTable& table, size_t size = 0 );
    RWDBMemTable( const RWDBTable& table, const RWDBConnection& conn,
                  size_t size = 0 );
    RWDBMemTable( const RWDBSelectorBase& sel, size_t size = 0 );
    RWDBMemTable( const RWDBSelectorBase& sel, const RWDBConnection& conn,
                  size_t size = 0 );
    RWDBMemTable( RWDBReader& reader, size_t size = 0 );
    RWDBMemTable( const RWDBMemTable& memtable );

    virtual ~RWDBMemTable();

    RWDBMemTable&       operator=( const RWDBMemTable& memtable );

    size_t              entries() const;
    
    RWDBRow&            operator[]( size_t index );

    RWBoolean           populate( RWDBReader& reader );
    RWBoolean           populate( const RWDBTable& table );

protected:
    RWDBColumn operator[](const RWCString& name) const;
    RWDBColumn operator[](size_t           idx) const;
};

#endif
