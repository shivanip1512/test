#ifndef PARAM_H
#define PARAM_H

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
#include <rw/db/value.h>

#define         MAXRESULTSCOLUMNS      2000

class RWDBParam
{
public:
        RWDBParam(const void* t, RWDBValue::ValueType type, int s, 
               void* indicator): length_(s), firstElem_(t), valueType_(type), indicator_(indicator), width_(0)
        {
        }

        RWDBParam(const void* t, RWDBValue::ValueType type, int w, int s, 
               void* indicator): length_(s), firstElem_(t), valueType_(type), indicator_(indicator), width_(w)
        {
        }

        ~RWDBParam()
        {
           ;
        }

        void
        setIndicator(void* indicator)
        {
               indicator_ = indicator;
        }

        static         RWDBValue::ValueType
        valueType( const int* )
        {
               return RWDBValue::Int;
        }

        static         RWDBValue::ValueType
        valueType( const RWDBDateTime* )
        {
               return RWDBValue::DateTime;
        }

        static         RWDBValue::ValueType
                     valueType( const short* )
        {
               return RWDBValue::Short;
        }

        static         RWDBValue::ValueType
                     valueType( const unsigned short* )
        {
               return RWDBValue::UnsignedShort;
        }

        static         RWDBValue::ValueType
                     valueType( const unsigned int* )
        {
               return RWDBValue::UnsignedInt;
        }

        static         RWDBValue::ValueType
                     valueType( const long* )
        {
               return RWDBValue::Long;
        }

        static         RWDBValue::ValueType
                     valueType( const unsigned long* )
        {
               return RWDBValue::UnsignedLong;
        }

        static         RWDBValue::ValueType
                     valueType( const float* )
        {
               return RWDBValue::Float;
        }

        static         RWDBValue::ValueType
                     valueType( const double* )
        {
               return RWDBValue::Double;
        }

        static         RWDBValue::ValueType
                     valueType( const RWDBBlob* )
        {
               return RWDBValue::Blob;
        }

        static         RWDBValue::ValueType
                     valueType( const RWDecimalPortable* )
        {
               return RWDBValue::Decimal;
        }

        static         RWDBValue::ValueType
                     valueType( const char* )
        {
               return RWDBValue::String;
        }


        RWDBValue::ValueType valueType_;
        const void* firstElem_;
        int width_;
        int length_;
        void* indicator_;
};




#endif

