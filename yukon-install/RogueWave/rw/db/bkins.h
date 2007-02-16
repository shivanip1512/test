#ifndef BKINS_H
#define BKINS_H

/**************************************************************************
 *
 * $Id:
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


// Interface level, what the user sees.
#include <rw/db/dbref.h>
#include <rw/db/dbvector.h>

class RWDBDateVector;
class RWDBBulkInserterImp;
class RWDBExport RWDBBulkInserter
{
public:
    RWDBBulkInserter();
        RWDBBulkInserter(const RWDBBulkInserter& anInserter);
        RWDBBulkInserter& operator=(const RWDBBulkInserter& bins);

    RWDBBulkInserter(RWDBBulkInserterImp* imp);

    virtual     ~RWDBBulkInserter();

        RWDBResult     execute();  
        RWDBResult  execute(size_t iters);


    RWBoolean isValid() const;
    RWDBBulkInserter& operator[](size_t index);
    RWDBBulkInserter& operator[](const RWCString& columnName);
    RWDBBulkInserter& operator[](const RWDBColumn& column);
        
        RWDBBulkInserter& operator<<(RWDBVector<int>& val);
        RWDBBulkInserter& operator<<(RWDBVector<unsigned int>& val);
        RWDBBulkInserter& operator<<(RWDBVector<short>& val);
        RWDBBulkInserter& operator<<(RWDBVector<unsigned short>& val);
        RWDBBulkInserter& operator<<(RWDBVector<long>& val);
        RWDBBulkInserter& operator<<(RWDBVector<unsigned long>& val);
        RWDBBulkInserter& operator<<(RWDBVector<float>& val);
        RWDBBulkInserter& operator<<(RWDBVector<double>& val);
        RWDBBulkInserter& operator<<(RWDBBinaryVector& val);
        RWDBBulkInserter& operator<<(RWDBStringVector& val);
        RWDBBulkInserter& operator<<(RWDBDecimalVector& val);

        RWDBBulkInserter& operator<<(RWDBDateVector& val);
        
private:
// not implemented
    RWDBBulkInserterImp* impl_;

};





#endif

