#pragma warning( disable : 4786)
#ifndef __ION_VALUE_STRUCTARRAY_H__
#define __ION_VALUE_STRUCTARRAY_H__

/*-----------------------------------------------------------------------------*
*
* File:   ion_value_structarray
*
* Class:  CtiIONValueStructArray
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

#include "ion_value.h"

class CtiIONStructArray : public CtiIONValue
{
    enum StructArrayTypes;

private:

    StructArrayTypes _structArrayType;

    enum StructArrayClassDescriptor
    {
        ClassDescriptor_LogRecordArray   = 0x0,
        ClassDescriptor_AlarmArray       = 0x1,
        ClassDescriptor_EventArray       = 0x2,
        ClassDescriptor_RangeArray       = 0x4,
        ClassDescriptor_ListArray        = 0x5,
        ClassDescriptor_ExceptionArray   = 0x7,
        ClassDescriptor_WaveformArray    = 0x8,
        ClassDescriptor_DateArray        = 0xa,
        ClassDescriptor_CalendarArray    = 0xb,
        ClassDescriptor_ProfileArray     = 0xc,
        ClassDescriptor_StringArrayArray = 0xf
    };

    enum StructArrayIONClassTypes
    {
        IONClass_StructArray = 0x07,
    };

protected:

    unsigned char getStructArrayKey( void ) const;

    friend CtiIONValue *CtiIONValue::restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    static CtiIONValue *restoreStructArray( unsigned char ionClass, unsigned char classDescriptor,
                                            unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    CtiIONStructArray( StructArrayTypes structArrayType );
    ~CtiIONStructArray( );

    static bool      isStructArrayType ( CtiIONValue *toCheck, StructArrayTypes structArrayType );
    bool             isStructArrayType ( StructArrayTypes structArrayType ) const;
    StructArrayTypes getStructArrayType( void ) const;

    enum StructArrayTypes
    {
        LogArray,
        AlarmArray,
        StringArrayArray,
        MultiArray  //  unknown - unspecified in document
    };

};

#endif // #ifndef __ION_VALUE_STRUCTARRAY_H__
