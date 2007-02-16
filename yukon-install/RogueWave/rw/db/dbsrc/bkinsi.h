#ifndef BKINSI_H
#define BKINSI_H

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
#ifdef _MSC_VER
#pragma warning( disable : 4251)
#endif

#include <rw/db/dbref.h>
#include <rw/db/connect.h>
#include <rw/db/status.h>
#include <rw/db/table.h>
#include <rw/tpvector.h>

#include <rw/db/dbsrc/param.h>

class RWDBBulkNullIndicatorImp;
class RWDBExport RWDBBulkInserterImp : public RWDBReference, public RWDBStatus
{
public:
        RWDBBulkInserterImp(const RWDBStatus& stat, const RWDBTable& tb, const RWDBConnection& conn);
        virtual        ~RWDBBulkInserterImp();

        void setPosition(size_t index);
        void setColumn(const RWCString& columnName);
        RWBoolean      safeBulkSize(const size_t sz);
        inline int columnsUpperBound() const {return aMaxResultsColumns_;}

    RWDBBulkInserterImp& shiftIn(const int& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const unsigned int& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const short& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const unsigned short& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const long& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const unsigned long& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const float& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const double& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(RWDBBlob* firstElement, size_t width, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const char* firstElement, size_t width, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkInserterImp& shiftIn(const RWDecimalPortable* firstElement, size_t width, size_t length, 
                                    void* nullIndicatorData);

    RWDBBulkInserterImp& shiftIn(const RWDBDateTime& firstElement, size_t length, 
                                    void* nullIndicatorData);

    RWDBResultImp*      execute();
        RWDBResultImp* execute(size_t iters);


        virtual        RWCString      asString();

        virtual int vendorParse(const char* sqlStr);
        virtual RWDBResultImp* vendorExecute(const int iters);
    

    virtual int vendorBind(int* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(unsigned int* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(short* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(unsigned short* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(long* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(unsigned long* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(float* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(double* data, int pos,  void* indicator, int offSet);
    virtual int vendorBind(char* data, int pos,  void* indicator, int offSet, int width, int length);
    virtual int vendorBind(RWDecimalPortable* data, int pos,  void* indicator, int offSet, int width, int length);
    virtual int vendorBind(RWDBBlob* data, int pos,  void* indicator, int offSet, int width, int length);
    virtual int vendorBind(RWDBDateTime* data, int pos,  void* indicator, int offSet);

    virtual RWCString vendorPlaceHolder(const int pos) const;
        virtual void vendorAllocateBindSpace(int n);
        virtual RWDBBulkNullIndicatorImp* nullIndicatorImp(size_t len) const;
    virtual int bindN(int offSet );
        
private:
// not implemented
    RWDBBulkInserterImp( const RWDBBulkInserterImp& );
    RWDBBulkInserterImp& operator=( const RWDBBulkInserterImp& );
        void   safeBounds(void);

protected:
        size_t         bulkSize_;
   RWTPtrVector<RWDBParam> insertParam_;
   int nextInsertParam_;
   int numberOfInsertParams_;
   int aMaxResultsColumns_;     // for vector re-shape
   RWDBConnection conn_;
   RWDBTable tab_;
   RWCString columnList_; // used for inserting values with column list
};





#endif

