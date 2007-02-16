#ifndef ORABKINSI_H
#define ORABKINSI_H

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
#include <rw/db/connect.h>
#include <rw/db/datetime.h>
#include <rw/db/orasrc/oracda.h>
#include <rw/db/dbsrc/bkinsi.h>



class RWDBAccessExport RWDBOracleBulkInserterImp : public RWDBBulkInserterImp
{
public:
        RWDBOracleBulkInserterImp(const RWDBStatus& stat, const RWDBTable& tab, const RWDBConnection& conn);
        ~RWDBOracleBulkInserterImp();

    virtual RWCString vendorPlaceHolder(const int pos) const;

        virtual int vendorParse(const char* sqlStr);
    virtual int vendorBind(RWDBDateTime* data, int pos,  void* indicator, int /* offSet */);
    virtual int vendorBind(int* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(unsigned int* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(short* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(unsigned short* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(long* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(unsigned long* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(float* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(double* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(RWDBBlob* data, int pos,  void* indicator, int offSet, int width, int length);
    virtual int vendorBind(char* data, int pos,  void* indicator, int offSet, int width, int length);
    virtual int vendorBind(RWDecimalPortable* data, int pos,  void* indicator, int offSet, int width, int length);

    virtual int bindN(int offSet );

        virtual RWDBResultImp* vendorExecute(const int iters);
    virtual RWDBBulkNullIndicatorImp* nullIndicatorImp(size_t len) const;

    void vendorAllocateStatement(const RWDBConnection& conn);

private:
// not implemented
    RWDBOracleBulkInserterImp( const RWDBOracleBulkInserterImp& );
    RWDBOracleBulkInserterImp& operator=( const RWDBOracleBulkInserterImp& );

        RWDBOracleCDA cda_;

    // Added flags to improve bulk inserter performance.
    RWBoolean parsed_;
    RWBoolean bound_;

};





#endif

