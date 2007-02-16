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

#ifdef _MSC_VER
#pragma warning( disable : 4251)
#endif

// Interface level, what the user sees.
#ifndef BKREADI_H
#define BKREADI_H
#include <rw/db/select.h>
#include <rw/db/dbref.h>
#include <rw/db/dbsrc/stmti.h>
#include <rw/db/connect.h>
#include <rw/db/status.h>
#include <rw/db/dbsrc/param.h>
#include <rw/tpvector.h>

class RWDBBulkNullIndicatorImp;
class RWDBSelector;

class RWDBExport RWDBBulkReaderImp : public RWDBReference, public RWDBStatus
{
public:
        RWDBBulkReaderImp(const RWDBStatus& stat, const RWDBSelector& sel, const RWDBConnection& conn);
        virtual        ~RWDBBulkReaderImp();
    
        void setPosition(size_t index);
        inline int columnsUpperBound() const {return aMaxResultsColumns_;}

    RWDBBulkReaderImp& shiftIn(const short& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const unsigned short& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const int& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const unsigned int& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const long& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const unsigned long& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const float& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const double& firstElement, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const char* firstElement, size_t width, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(RWDBBlob* firstElement, size_t width, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const RWDecimalPortable* firstElement, size_t width, size_t length, 
                                    void* nullIndicatorData);
    RWDBBulkReaderImp& shiftIn(const RWDBDateTime& firstElement, size_t length, 
                                    void* nullIndicatorData);
    int operator()();
    int cancel();
        RWBoolean      safeBulkSize(const size_t sz);

        virtual int vendorParse(const char* sqlStr);
    
        virtual int vendorDescribe(int* param, int pos, void* indicator);
        virtual int vendorDescribe(unsigned int* param, int pos, void* indicator);
        virtual int vendorDescribe(short* param, int pos, void* indicator);
        virtual int vendorDescribe(unsigned short* param, int pos, void* indicator);
        virtual int vendorDescribe(long* param, int pos, void* indicator);
        virtual int vendorDescribe(unsigned long* param, int pos, void* indicator);
        virtual int vendorDescribe(float* param, int pos, void* indicator);
        virtual int vendorDescribe(double* param, int pos, void* indicator);
        virtual int vendorDescribe(char* param, int pos, void* indicator, int width);
        virtual int vendorDescribe(RWDBBlob* param, int pos, void* indicator, int width);
        virtual int vendorDescribe(RWDecimalPortable* param, int pos, void* indicator, int width);
    virtual int vendorDescribe(RWDBDateTime* param, int pos, void* indicator);
    virtual int vendorCancel();

    virtual int vendorBind(int* data, int pos,  void*  indicator);
    virtual int vendorBind(unsigned int* data, int pos,  void* indicator);
    virtual int vendorBind(short* data, int pos,  void* indicator);
    virtual int vendorBind(unsigned short* data, int pos,  void* indicator);
    virtual int vendorBind(long* data, int pos,  void* indicator);
    virtual int vendorBind(unsigned long* data, int pos,  void* indicator);
    virtual int vendorBind(float* data, int pos,  void* indicator);
    virtual int vendorBind(double* data, int pos,  void* indicator);
    virtual int vendorBind(RWDBDateTime* data, int pos,  void* indicator);
    virtual int vendorBind(char* data, int pos,  void* indicator);
    virtual int vendorBind(RWDecimalPortable* data, int pos,  void* indicator);
    virtual int vendorBind(RWDBBlob* data, int pos,  void* indicator);

    virtual RWBoolean hasBeenBound();
    
        virtual void vendorAllocateBindSpace(int n);
    virtual int vendorFetch(int nrows);
        virtual RWDBBulkNullIndicatorImp* nullIndicatorImp(size_t len) const;
    virtual int describeN();
    virtual int bindN(RWDBBoundObjectList& boundList);
        
private:
        void     safeBounds(void);

// not implemented
    RWDBBulkReaderImp( const RWDBBulkReaderImp& );
    RWDBBulkReaderImp& operator=( const RWDBBulkReaderImp& );

protected:
        size_t         bulkSize_;
   RWBoolean    described_;
   RWBoolean    bound_;
   RWBoolean    firstFetched_;
   RWTPtrVector<RWDBParam> readParam_;
   int nextReadParam_;
   size_t numberOfReadParams_;
   int aMaxResultsColumns_;     // for vector re-size
   RWCString columnList_; // used for inserting values with column list

   RWDBSelector sel_;
   RWDBConnection conn_;
};





#endif

