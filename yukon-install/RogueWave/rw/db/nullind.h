#ifndef __RWDB_NULLIND_H__
#define __RWDB_NULLIND_H__

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
 * Null Indicators are used to signal the presence of NULL data fetched
 * from a database.  They can be used in conjunction with RWDBReader.
 * Applying the extraction RWDBReader::operator>>() to null indicator,
 * sets the null indicator to TRUE/FALSE if the next data item to be
 * read is/not NULL.
 * eg:
 * RWDBNullIndicator n1, n2;
 * int x, y;
 * rdr >> n1 >> x >> n2 >> y;  // rdr is an RWDBReader instance
 * After the above, n1 is TRUE iff x is NULL; n2 is TRUE iff y is NULL
 *
 **************************************************************************/


#include <rw/db/defs.h>


class RWDBExport RWDBNullIndicator
{
public:
    RWDBNullIndicator(RWBoolean val = 0);

    operator RWBoolean() const;

private:
    RWBoolean value_;
};

#endif
