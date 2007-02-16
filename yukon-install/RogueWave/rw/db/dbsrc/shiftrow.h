#ifndef __RWDB_SHIFTROW_H__
#define __RWDB_SHIFTROW_H__

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
 * Declaration  of class RWDBShiftableRow
 *
 **************************************************************************/

#include <rw/db/row.h>
#include <rw/db/value.h>


class RWDBExport RWDBShiftableRow : public RWDBRow
{
public:
    RWDBShiftableRow();

    virtual size_t                  shiftInPosition();
    virtual size_t                  shiftOutPosition();
    virtual size_t                  setShiftInPosition(size_t pos);
    virtual size_t                  setShiftOutPosition(size_t pos);
    virtual RWDBValue&              operator[](size_t indx) const;
    virtual RWDBShiftableRow&       operator<<(const RWDBValue& aVal);

    RWDBShiftableRow&       operator>>(RWDBNullIndicator& aNullIndictator);
    RWDBShiftableRow&       operator>>(char& aVal);
    RWDBShiftableRow&       operator>>(unsigned char& aVal);
    RWDBShiftableRow&       operator>>(short& aVal);
    RWDBShiftableRow&       operator>>(unsigned short& aVal);
    RWDBShiftableRow&       operator>>(int& aVal);
    RWDBShiftableRow&       operator>>(unsigned int& aVal);
    RWDBShiftableRow&       operator>>(long& aVal);
    RWDBShiftableRow&       operator>>(unsigned long& aVal);
    RWDBShiftableRow&       operator>>(float& aVal);
    RWDBShiftableRow&       operator>>(double& aVal);
    RWDBShiftableRow&       operator>>(RWDecimalPortable& aVal);
    RWDBShiftableRow&       operator>>(RWTime& aVal);
    RWDBShiftableRow&       operator>>(RWDate& aVal);
    RWDBShiftableRow&       operator>>(RWDBDateTime& aVal);
    RWDBShiftableRow&       operator>>(RWDBDuration& aVal);
    RWDBShiftableRow&       operator>>(RWCString& aVal);
    RWDBShiftableRow&       operator>>(RWDBBlob& aVal);
    RWDBShiftableRow&       operator>>(RWDBValue& aVal);
    RWDBShiftableRow&       operator>>(RWDBMBString& aVal);
#ifndef RW_NO_WSTR
    RWDBShiftableRow&       operator>>(RWWString& aVal);
#endif

    RWBoolean               checkBounds() const;
    RWBoolean               checkConversion(RWDBValue::ValueType aType,
                                            size_t pos) const;

private:
    // Disabled. Entry into the list is by operator<<() only.
    RWCollectable* append(RWCollectable*);
    RWCollectable* insert(RWCollectable*);
    RWCollectable* insertAt(size_t, RWCollectable*);

    // Not Implemented
    RWDBShiftableRow(const RWDBShiftableRow&);
    RWDBShiftableRow& operator=(const RWDBShiftableRow&);

    size_t                     shiftInPosition_;
    size_t                     shiftOutPosition_;
};

#endif
