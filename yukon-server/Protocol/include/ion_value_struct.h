#pragma warning( disable : 4786)
#ifndef __ION_VALUE_STRUCT_H__
#define __ION_VALUE_STRUCT_H__

/*-----------------------------------------------------------------------------*
*
* File:   ion_value_struct
*
* Class:  CtiIONStruct
* Date:   1/30/2003
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/02/12 01:14:54 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <vector>
using namespace std;

#include "ion_value.h"


class CtiIONStruct : public CtiIONValue
{
    enum StructTypes;
    enum Elements;

private:

    vector< CtiIONValue * > _structElements;
    typedef vector< CtiIONValue * >::const_iterator ionStructIterator_const;

    StructTypes _structType;

    enum StructClassDescriptor
    {
        StructClassDescriptor_LogRecord   = 0x0,
        StructClassDescriptor_Alarm       = 0x1,
        StructClassDescriptor_Event       = 0x2,
        StructClassDescriptor_Range       = 0x4,
        StructClassDescriptor_List        = 0x5,
        StructClassDescriptor_Exception   = 0x7,
        StructClassDescriptor_Waveform    = 0x8,
        StructClassDescriptor_Date        = 0xa,
        StructClassDescriptor_Calendar    = 0xb,
        StructClassDescriptor_Profile     = 0xc,
        StructClassDescriptor_StringArray = 0xf
    };

    enum StructIONClassTypes
    {
        IONClass_Struct = 0x07,
    };

protected:

    unsigned long  _numElements;

    void init( vector< CtiIONValue * > &structValues );

    void putSerialized( unsigned char *buf ) const;
    unsigned int getSerializedLength( void ) const;

    friend CtiIONValue *CtiIONValue::restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    static CtiIONValue *restoreStruct( unsigned char ionClass, unsigned char classDescriptor,
                                       unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    CtiIONStruct( StructTypes structType, unsigned long numElements );
    CtiIONStruct( CtiIONStruct &aRef );
    ~CtiIONStruct( );

    virtual CtiIONValue *operator[]( Elements index );

    static bool isStructType ( CtiIONValue *toCheck, StructTypes structType );
    bool        isStructType ( StructTypes structType ) const;
    StructTypes getStructType( void ) const;

    virtual enum Elements
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




#endif // #ifndef __ION_VALUE_STRUCT_H__
