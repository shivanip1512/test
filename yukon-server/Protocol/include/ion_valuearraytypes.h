#pragma warning( disable : 4786 )

#ifndef __ION_ARRAYTYPES_H__
#define __ION_ARRAYTYPES_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_arraytypes.h
 *
 * Class:  CtiION
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 *         Array value classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_value_basic_array.h"
#include "ion_value_basic_char.h"
#include "ion_value_basic_boolean.h"
#include "ion_value_basic_float.h"
#include "ion_value_basic_intsigned.h"
#include "ion_value_basic_intunsigned.h"



class IM_EX_PROT CtiIONCharArray : public CtiIONArray
{
public:
    CtiIONCharArray( );

protected:

    typedef CtiIONArray Inherited;
    friend Inherited;

    CtiIONCharArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed );
    ~CtiIONCharArray( ) { };

    CtiIONChar *getElement( unsigned long index );
    CtiIONChar *operator[]( unsigned long index );
};



class IM_EX_PROT CtiIONBooleanArray : public CtiIONArray
{
public:
    CtiIONBooleanArray( );
    CtiIONBooleanArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed );
    ~CtiIONBooleanArray( );

    CtiIONBoolean *getElement( unsigned long index );
    CtiIONBoolean *operator[]( unsigned long index );
};



class IM_EX_PROT CtiIONFloatArray : public CtiIONArray
{
public:
    CtiIONFloatArray( );
    CtiIONFloatArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed );
    ~CtiIONFloatArray( ) { };

    CtiIONFloat *getElement( unsigned long index );
    CtiIONFloat *operator[]( unsigned long index );
};



class IM_EX_PROT CtiIONSignedIntArray : public CtiIONArray
{
public:
    CtiIONSignedIntArray( );
    CtiIONSignedIntArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed );
    ~CtiIONSignedIntArray( ) { };

    CtiIONSignedInt *getElement( unsigned long index );
    CtiIONSignedInt *operator[]( unsigned long index );
};



class IM_EX_PROT CtiIONUnsignedIntArray : public CtiIONArray
{
public:
    CtiIONUnsignedIntArray( );
    CtiIONUnsignedIntArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed );
    ~CtiIONUnsignedIntArray( ) { };

    CtiIONUnsignedInt *getElement( unsigned long index );
    CtiIONUnsignedInt *operator[]( unsigned long index );
};



class IM_EX_PROT CtiIONStruct : public CtiIONArray
{
public:
    enum IONStructTypes;
    enum Elements;

    CtiIONStruct( IONStructTypes structType, unsigned long numElements = 0 ) :
        CtiIONArray(IONStruct),
        _structType(structType),
        _numElements(numElements)
        { };
    ~CtiIONStruct( ) { };

    void init( vector< CtiIONValue * > &structValues );

    virtual CtiIONValue *operator[]( Elements index );

    unsigned char getStructKey( void );
    IONStructTypes getStructType( void ) { return _structType; };

    static CtiIONArray *restoreObject( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    virtual enum Elements
    {
        Undefined
    };

    enum IONStructTypes
    {
        IONLogRecord   = 0x0,
        IONAlarm       = 0x1,
        IONEvent       = 0x2,
        IONRange       = 0x4,
        IONList        = 0x5,
        IONException   = 0x7,
        IONWaveform    = 0x8,
        IONDate        = 0xA,
        IONCalendar    = 0xB,
        IONProfile     = 0xC,
        IONStringArray = 0xF
    };

protected:
    unsigned long  _numElements;

private:
    IONStructTypes _structType;
};



class IM_EX_PROT CtiIONStructArray : public CtiIONArray
{
public:
    enum IONStructArrayTypes;

    CtiIONStructArray( IONStructArrayTypes structArrayType ) :
        CtiIONArray(IONStructArray),
        _structArrayType(structArrayType)
        { };
    ~CtiIONStructArray( ) { };

    unsigned char getStructArrayKey( void );
    IONStructArrayTypes getStructArrayType( void ) { return _structArrayType; };

    static CtiIONArray *restoreObject( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    enum IONStructArrayTypes
    {
        IONLogArray         = 0x0,
        IONAlarmArray       = 0x1,
        IONStringArrayArray = 0xF,
        IONMultiArray       = 0xFF  //  unknown - unspecified in document
    };

protected:

private:

    IONStructArrayTypes _structArrayType;
};


#endif  //  #ifndef __ION_ARRAYTYPES_H__
