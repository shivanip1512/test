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
 
#include "ion_valuebasictypes.h"


class CtiIONCharArray : public CtiIONArray
{
public:
    CtiIONCharArray( ) :
        CtiIONArray(IONCharArray)
        { };

protected:

    typedef CtiIONArray Inherited;
    friend Inherited;

    CtiIONCharArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream );
    ~CtiIONCharArray( ) { };

    CtiIONChar *operator[]( unsigned long index );
};



class CtiIONBooleanArray : CtiIONArray
{
public:
    CtiIONBooleanArray( ) :
        CtiIONArray(IONBooleanArray)
        { };
    CtiIONBooleanArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream );
    ~CtiIONBooleanArray( ) { };

    CtiIONBoolean *operator[]( unsigned long index );
};



class CtiIONFloatArray : CtiIONArray
{
public:
    CtiIONFloatArray( ) :
        CtiIONArray(IONFloatArray)
        { };
    CtiIONFloatArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream );
    ~CtiIONFloatArray( ) { };

    CtiIONFloat *operator[]( unsigned long index );
};



class CtiIONSignedIntArray : CtiIONArray
{
public:
    CtiIONSignedIntArray( ) :
        CtiIONArray(IONSignedIntArray)
        { };
    CtiIONSignedIntArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream );
    ~CtiIONSignedIntArray( ) { };

    CtiIONSignedInt *operator[]( unsigned long index );
};



class CtiIONUnsignedIntArray : CtiIONArray
{
public:
    CtiIONUnsignedIntArray( ) :
        CtiIONArray(IONUnsignedIntArray)
        { };
    CtiIONUnsignedIntArray( unsigned long itemCount, unsigned long itemLength, unsigned char *byteStream );
    ~CtiIONUnsignedIntArray( ) { };

    CtiIONUnsignedInt *operator[]( unsigned long index );
};



class CtiIONStruct : public CtiIONArray
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

    static CtiIONArray *restoreObject( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength );
    
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



class CtiIONStructArray : public CtiIONArray
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
    
    static CtiIONArray *restoreObject( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength );

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
