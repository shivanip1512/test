#ifndef __RWDB_COMSEL_H__
#define __RWDB_COMSEL_H__

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
 * A compound selector is the result of applying set operations (union,
 * intersection, difference) to selectors and/or other compound
 * selectors. Unfortunately, it is not the case that such an object
 * "is_a" selector: one cannot intellegently apply methods such as
 * where() to it (this is a direct consequence of SQL semantics).
 * So, rather than deriving compound selector from selector, they are
 * both derived from a common base class.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/select.h>


class RWDBExport RWDBCompoundSelector : public RWDBSelectorBase
{
public:
    RWDBCompoundSelector( const RWDBCompoundSelector& selector );
    RWDBCompoundSelector( RWDBCompoundSelectorImp* imp );
    RWDBCompoundSelector& operator= ( const RWDBCompoundSelector& selector );
    virtual ~RWDBCompoundSelector();

    //RWCString  asString( const RWDBPhraseBook& pb ) const;

private:
    RWDBCompoundSelector ();
};


#endif
