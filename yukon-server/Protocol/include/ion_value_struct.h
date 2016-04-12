#pragma once

#include <vector>

#include "ion_value.h"

class CtiIONStruct : public CtiIONValue
{
    enum StructTypes;
    enum Elements;

private:

    typedef std::vector< CtiIONValue * > ion_value_vector;
    ion_value_vector _structElements;

    StructTypes _structType;

    enum StructClassDescriptor
    {
        ClassDescriptor_Struct_LogRecord    = 0x0,
        ClassDescriptor_Struct_Alarm        = 0x1,
        ClassDescriptor_Struct_Event        = 0x2,
        ClassDescriptor_Struct_Range        = 0x4,
        ClassDescriptor_Struct_List         = 0x5,
        ClassDescriptor_Struct_Exception    = 0x7,
        ClassDescriptor_Struct_Waveform     = 0x8,
        ClassDescriptor_Struct_Date         = 0xa,
        ClassDescriptor_Struct_Calendar     = 0xb,
        ClassDescriptor_Struct_Profile      = 0xc,
        ClassDescriptor_Struct_StringArray  = 0xf
    };

    enum StructIONClassTypes
    {
        IONClass_Struct = 0x07,
    };

protected:

    unsigned long  _numElements;

    void init( ion_value_vector &structValues );

    void putSerialized( unsigned char *buf ) const;
    void putElements  ( unsigned char *buf ) const;

    unsigned int getSerializedLength( void ) const;
    unsigned int getElementsLength  ( void ) const;

    unsigned char getStructClassDescriptor( void ) const;

    friend CtiIONValue *CtiIONValue::restoreObject( const unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    friend class CtiIONStructArray;

    static CtiIONValue *restoreStruct( unsigned char ionClass, unsigned char classDescriptor,
                                       const unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    CtiIONStruct( StructTypes structType, unsigned long numElements );
    virtual ~CtiIONStruct( );

    virtual CtiIONValue *at( int index );
    virtual CtiIONValue *operator[]( int index );

    static bool isStructType ( CtiIONValue *toCheck, StructTypes structType );
    bool        isStructType ( StructTypes structType ) const;
    StructTypes getStructType( void ) const;

    enum Elements
    {
        Undefined
    };

    enum StructTypes
    {
        StructType_LogRecord,
        StructType_Alarm,
        StructType_Event,
        StructType_Range,
        StructType_List,
        StructType_Exception,
        StructType_Waveform,
        StructType_Date,
        StructType_Calendar,
        StructType_Profile,
        StructType_StringArray
    };
};
