#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_BASIC_ARRAY_H__
#define __ION_VALUE_BASIC_ARRAY_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_array.h
 *
 * Class:  CtiIONArray
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_rootclasses.h"
#include "ctitypes.h"


class IM_EX_PROT CtiIONArray : public CtiIONValue
{
public:
    enum IONArrayTypes
    {
        IONCharArray        = 0xA,  //  class descriptors for the fixed
        IONBooleanArray     = 0xB,  //    size arrays
        IONFloatArray       = 0xC,  //
        IONSignedIntArray   = 0xD,  //
        IONUnsignedIntArray = 0xE,  //
//        IONNumericArray,
        IONStruct,      //  these two get spawned off into seperate instantiators,
        IONStructArray  //    and never numerically use these enum values
    };

    IONArrayTypes getArrayType( void ) const;
    int isNumericArray( void );

    virtual int getSize( void );
    //T *operator[]( unsigned int index );

protected:

    friend class CtiIONValue;

    CtiIONArray( IONArrayTypes arrayType );
    ~CtiIONArray( );

    virtual unsigned int getSerializedValueLength( void ) const;
    virtual void putSerializedValue( unsigned char *buf ) const;

    virtual unsigned int getSerializedHeaderLength( void ) const;
    virtual void putSerializedHeader( unsigned char *buf ) const;

    static CtiIONArray *restoreStruct     ( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed );
    static CtiIONArray *restoreStructArray( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed  );
    static CtiIONArray *restoreFixedArray ( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed  );

    unsigned char *putClassSize( unsigned char key, unsigned char *buf ) const;
    unsigned char  getArrayKey( void );

    CtiIONValue *getArrayElement( unsigned long index );
    CtiIONArray &appendArrayElement( CtiIONValue *toAppend );
    void clearArrayElements( void );

private:

    vector< CtiIONValue * > _array;
    IONArrayTypes _arrayType;
    int numEntries;

    void setArrayType( IONArrayTypes arrayType );
};


#endif  //  #ifndef __ION_VALUE_BASIC_ARRAY_H__


