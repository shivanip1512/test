#ifndef RWDBVECTOR_H
#define RWDBVECTOR_H

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
#include <rw/tvvector.h>
#include <rw/db/blob.h>
#include <rw/db/bknull.h>
#include <rw/db/decport.h>
#include <rw/db/value.h>

class RWDBBulkReader;

template <class T>
class RWDBExport RWDBVector : public RWTValVector<T>
{
public:
        RWDBVector(size_t n) : RWTValVector<T>(n), indicator_(n)
        {
        }

        ~RWDBVector()
        {
        }

        RWBoolean
        isNull(size_t n)
        {
               return indicator_.isNull(n);
        }

        void
        setNull(size_t n)
        {
               indicator_.setNull(n);
        }

        void
        unsetNull(size_t n)
        {
                indicator_.unsetNull(n);
        }

        friend class RWDBBulkReader;
        friend class RWDBBulkInserter;


        RWDBBulkNullIndicator&
        getIndicator()
        {
               return indicator_;
        }

private:

        RWDBBulkNullIndicator indicator_;
};




class RWDBArrayVector
{
public:
    RWDBArrayVector(size_t width, size_t length) : width_(width), length_(length), indicator_(length)
        {
       theArray_ = new char[length_ * width_];
        }

    RWDBArrayVector(const RWDBArrayVector& anArray) : indicator_(0)
        {
       size_t size = anArray.length_ * anArray.width_;
       theArray_ = new char[size];
           width_ = anArray.width_, 
           length_ = anArray.length_, 
           indicator_ = anArray.indicator_;    
        }

        RWDBArrayVector&
        operator=(const RWDBArrayVector& anArray)
        {
               if (&anArray != this) { 
                     delete [] theArray_;
            theArray_ = new char[anArray.length_ * anArray.width_];
                width_ = anArray.width_, 
                length_ = anArray.length_, 
                indicator_ = anArray.indicator_;       
               }

           return *this;
        }

    virtual ~RWDBArrayVector()
        {
               delete [] theArray_;
        }

    size_t width()
        {
               return width_;
        }

    RWDBBulkNullIndicator& indicator()
        {
               return indicator_;
        }

    size_t length()
        {
               return length_;
        }

        char* operator[](size_t index)
        {
                RWASSERT( index < length_ );
               return theArray_ + (index * width_);
        }

        RWBoolean
        isNull(size_t n)
        {
               return indicator_.isNull(n);
        }

        void
        setNull(size_t n)
        {
               indicator_.setNull(n);
        }

        void
        unsetNull(size_t n)
        {
                indicator_.unsetNull(n);
        }

        friend class RWDBBulkReader;
        friend class RWDBBulkInserter;



protected:
        RWDBBulkNullIndicator& getIndicator()
        {
               return indicator_;
        }

    void *firstElem()
        {
               return theArray_;
        }

    char* theArray_;
        RWDBBulkNullIndicator indicator_;
        size_t length_;
        size_t width_;
};

class RWDBBinaryVector;
class RWDBBinaryVectorElement
{
public:
        RWDBBinaryVectorElement& operator=(RWDBBlob& b)
        {
               memcpy( data_, b.data(), b.length() );
               indicator_->width(index_, b.length() );
               return *this;
        }

        operator unsigned char*()
        {
               return (unsigned char*) data_;
        }
private:
        RWDBBinaryVectorElement(char *data, RWDBBulkNullIndicator* ni, size_t index)
               : data_(data), indicator_(ni), index_(index)
        {
        }

        char* data_;
        RWDBBulkNullIndicator* indicator_;
        size_t index_;

        friend class RWDBBinaryVector;
};


class RWDBStringVector;
class RWDBStringVectorElement
{
public:
        RWDBStringVectorElement& operator=(RWCString& s)
        {
               strcpy( data_, s.data());
               return *this;
        }

        operator char*()
        {
               return ( char*) data_;
        }
private:
        RWDBStringVectorElement(char *data)
               : data_(data)
        {
        }

        char* data_;

        friend class RWDBStringVector;
};

class RWDBDecimalVector;
class RWDBDecimalVectorElement
{
public:
// for performance reason, we do not check data validity (assume user did it already).
    RWDBDecimalVectorElement& operator=(const RWDecimalPortable& val)
    {
      strcpy( data_, RWCString(val).data());
      return *this;
    }

    RWDBDecimalVectorElement& operator=(const char* ptr)
    {
          if( ptr )
                 strcpy( data_, ptr);
          else
                 strcpy( data_, "0"); // for aDec[i] = 0; 
      return *this;
    }

    RWDBDecimalVectorElement(long val)
    {
      RWDBValue tmp(val);
      strcpy( data_, tmp.asString().data());
    }

    RWDBDecimalVectorElement(int val)
    {
      RWDBValue tmp(val);
      strcpy( data_, tmp.asString().data());
    }

    RWDBDecimalVectorElement& operator=(const RWDBDecimalVectorElement& val)
    {
      strcpy( data_, val.data_);
      return *this;
    }

    operator char*()
    {
      return ( char*) data_;
    }

    operator RWDecimalPortable()
    {
      return data_;
    }
private:
    RWDBDecimalVectorElement(char *data)
      : data_(data)
    {
    }

    char* data_;

    friend class RWDBDecimalVector;
};

class RWDBBinaryVector : public RWDBArrayVector
{
public:
    RWDBBinaryVector(size_t width, size_t length) 
               : RWDBArrayVector(width, length)
        {
        }

    size_t      width()
        {
               return RWDBArrayVector::width();
        }

    size_t      width(size_t index)
        {
               return indicator_[index];
        }

    void        width(size_t index, size_t w)
        {
               indicator_.width(index, w);
        }

   RWDBBinaryVectorElement operator[](size_t index)
        {
               return 
                     RWDBBinaryVectorElement(this->RWDBArrayVector::operator[](index), &indicator_, index);
        }


        friend class RWDBBulkReader;
        friend class RWDBBulkInserter;
};

class RWDBStringVector : public RWDBArrayVector
{
public:
    RWDBStringVector(size_t width, size_t length) 
               : RWDBArrayVector(width, length)
        {
        }

        RWDBStringVectorElement operator[](size_t index)
        {
               return 
                     RWDBStringVectorElement(this->RWDBArrayVector::operator[](index) );
        }

        friend class RWDBBulkReader;
        friend class RWDBBulkInserter;
};

class RWDBDecimalVector : public RWDBArrayVector
{
public:
    RWDBDecimalVector(size_t width, size_t length) 
      : RWDBArrayVector(width, length)
    { }

    RWDBDecimalVectorElement operator[](size_t index)
    {
      return 
      RWDBDecimalVectorElement(this->RWDBArrayVector::operator[](index) );
    }

    friend class RWDBBulkReader;
    friend class RWDBBulkInserter;
};


#endif

