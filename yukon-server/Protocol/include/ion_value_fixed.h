#pragma warning( disable : 4786)
#ifndef __ION_VALUE_FIXED_H__
#define __ION_VALUE_FIXED_H__

/*-----------------------------------------------------------------------------*
*
* File:   ion_value_fixed
*
* Class:  CtiIONValueFixed
* Date:   1/30/2003
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/02/14 16:53:49 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "ion_value.h"
#include "ion_value_numeric.h"

//  ACH:  sometime, make the Fixed types into templatized versions...  the code repetition is kind of silly

class CtiIONValueFixed : public CtiIONValue
{
    enum FixedTypes;

private:

    FixedTypes _fixedType;

    enum FixedClassDescriptor
    {
        ClassDescriptor_LengthNibbleMax  = 0xc,
        ClassDescriptor_LengthNextByte   = 0xd,
        ClassDescriptor_LengthNext4Bytes = 0xe,
        ClassDescriptor_Reserved         = 0xf
    };

    enum FixedIONClassTypes
    {
        IONClass_Char         = 0x01,
        IONClass_Float        = 0x03,
        IONClass_SignedInt    = 0x04,
        IONClass_Time         = 0x05,
        IONClass_UnsignedInt  = 0x06,
    };

protected:

    void putSerializedHeader( unsigned char *buf ) const;
    unsigned int getSerializedHeaderLength( void ) const;

    unsigned char getFixedIONClassType( void ) const;

    virtual void putSerializedValue( unsigned char *buf ) const = 0;
    virtual unsigned int getSerializedValueLength( void ) const = 0;

    friend CtiIONValue *CtiIONValue::restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    static CtiIONValue *restoreFixed( unsigned char ionClass, unsigned char classDescriptor,
                                      unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    CtiIONValueFixed( FixedTypes fixedType );
    CtiIONValueFixed( const CtiIONValueFixed &aRef );

    virtual ~CtiIONValueFixed();

    CtiIONValueFixed &operator=( const CtiIONValueFixed &aRef );

    void putSerialized( unsigned char *buf ) const;
    unsigned int getSerializedLength( void ) const;

    static bool isFixedType ( CtiIONValue *toCheck, FixedTypes type );
    bool        isFixedType ( FixedTypes type ) const;
    FixedTypes  getFixedType( void ) const;

    enum FixedTypes
    {
        Fixed_Char,
        Fixed_Float,
        Fixed_SignedInt,
        Fixed_Time,
        Fixed_UnsignedInt,
    };
};


#endif // #ifndef __ION_VALUE_FIXED_H__
