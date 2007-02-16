#ifndef ORABKREADI_H
#define ORABKREADI_H

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
#include <rw/db/blob.h>
#include <rw/db/dbsrc/bkreadi.h>


class RWDBBulkNullIndicatorImp;
class RWDBAccessExport RWDBOracleBulkReaderImp : public RWDBBulkReaderImp
{
public:
        RWDBOracleBulkReaderImp(const RWDBStatus& stat, const RWDBSelector& sel, const RWDBConnection& conn);
        RWDBOracleBulkReaderImp(const RWDBStatus& stat, const RWDBConnection& conn);

        ~RWDBOracleBulkReaderImp();

    RWDBBulkNullIndicatorImp* nullIndicatorImp(size_t len) const;

        virtual int vendorParse(const char* sqlStr);

    virtual int vendorDescribe(int* param, int pos, void* indicator);
    virtual int vendorDescribe(unsigned int* param, int pos, void* indicator);
    virtual int vendorDescribe(short* param, int pos, void* indicator);
    virtual int vendorDescribe(unsigned short* param, int pos, void* indicator);
    virtual int vendorDescribe(long* param, int pos, void* indicator);
    virtual int vendorDescribe(unsigned long* param, int pos, void* indicator);
    virtual int vendorDescribe(float* param, int pos, void* indicator);
    virtual int vendorDescribe(double* param, int pos, void* indicator);
    virtual int vendorDescribe(RWDBBlob* param, int pos, void* indicator, int width);
    virtual int vendorDescribe(char* param, int pos, void* indicator, int width);
    virtual int vendorDescribe(RWDecimalPortable* param, int pos, void* indicator, int width);
    virtual int vendorDescribe(RWDBDateTime* param, int pos, void* indicator);
    
        virtual int vendorBind(int* data, int pos,  void* indicator);
        virtual int vendorBind(unsigned int* data, int pos,  void* indicator);
    virtual int vendorBind(RWDBDateTime* data, int pos,  void* indicator);
        virtual int vendorBind(char* data, int pos,  void* indicator);
        virtual int vendorBind(RWDecimalPortable* data, int pos,  void* indicator);
        virtual int vendorBind(short* data, int pos,  void* indicator);
        virtual int vendorBind(unsigned short* data, int pos,  void* indicator);
        virtual int vendorBind(long* data, int pos,  void* indicator);
        virtual int vendorBind(unsigned long* data, int pos,  void* indicator);
        virtual int vendorBind(float* data, int pos,  void* indicator);
        virtual int vendorBind(double* data, int pos,  void* indicator);
        virtual int vendorBind(RWDBBlob* data, int pos,  void* indicator);

    virtual int vendorFetch(int nrows);
        virtual int vendorCancel(); 
        RWCString vendorPlaceHolder(const int pos) const;
    virtual void vendorAllocateStatement(const RWDBConnection& conn);
    virtual Cda_Def* cdaPtr();

private:        
        void        setReturnLength(size_t count);

// not implemented
    RWDBOracleBulkReaderImp( const RWDBOracleBulkReaderImp& );
    RWDBOracleBulkReaderImp& operator=( const RWDBOracleBulkReaderImp& );
        int         rowsFetched_;
        RWBoolean      endOfFetch_;  // for abort fetch (only for oacle acc).
        RWBoolean shouldClose_;

protected:
    short** indicatorCache_;
    unsigned short** bindLen_;
    Cda_Def               cda_;
};


class RWDBAccessExport RWDBOracleBulkResTabReaderImp : public  RWDBOracleBulkReaderImp
{
public : 
        RWDBOracleBulkResTabReaderImp(const RWDBStatus& stat, const RWDBOracleCDA& aCda, const RWDBConnection& conn);
    virtual void vendorAllocateStatement(const RWDBConnection& conn);
        virtual int vendorParse(const char* sqlStr);
    Cda_Def* cdaPtr();
        RWDBOracleCDA aCda_;
};




#endif

