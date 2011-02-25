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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:19:58 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __ION_VALUE_STRUCTARRAY_H__
#define __ION_VALUE_STRUCTARRAY_H__
#pragma warning( disable : 4786)


#include "ion_value.h"
#include "ion_value_struct_types.h"
#include "string_utility.h"


class CtiIONStructArray : public CtiIONValue
{
    enum StructArrayTypes;

private:

    StructArrayTypes _structArrayType;

    enum StructArrayClassDescriptor
    {
        ClassDescriptor_StructArray_LogRecord      = 0x0,
        ClassDescriptor_StructArray_Alarm          = 0x1,
        ClassDescriptor_StructArray_Event          = 0x2,
        ClassDescriptor_StructArray_Range          = 0x4,
        ClassDescriptor_StructArray_List           = 0x5,
        ClassDescriptor_StructArray_Exception      = 0x7,
        ClassDescriptor_StructArray_Waveform       = 0x8,
        ClassDescriptor_StructArray_Date           = 0xa,
        ClassDescriptor_StructArray_Calendar       = 0xb,
        ClassDescriptor_StructArray_Profile        = 0xc,
        ClassDescriptor_StructArray_StringArray    = 0xf
    };

    enum StructArrayIONClassTypes
    {
        IONClass_StructArray = 0x08
    };

    enum
    {
        StructArrayEnd = 0xf9
    };

protected:

    CtiIONStructArray( );
    virtual ~CtiIONStructArray( );

    typedef vector< CtiIONStruct * > ion_struct_vector;
    ion_struct_vector _structArrayElements;

    void setStructArrayType( StructArrayTypes structArrayType );

    void putSerialized( unsigned char *buf ) const;
    unsigned int getSerializedLength( void ) const;

    unsigned char getStructArrayClassDescriptor( void ) const;

    friend class CtiProtocolION;
    friend CtiIONValue *CtiIONValue::restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed );
//    friend class CtiIONStructArrayTemplate;

    static CtiIONValue *restoreStructArray( unsigned char ionClass, unsigned char classDescriptor,
                                            unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    void init( ion_struct_vector structElements );
    void push_back( CtiIONStruct *toAppend );

public:

    static bool      isStructArrayType ( CtiIONValue *toCheck, StructArrayTypes structArrayType );
    bool             isStructArrayType ( StructArrayTypes structArrayType ) const;
    StructArrayTypes getStructArrayType( void ) const;

    typedef ion_struct_vector::const_iterator const_iterator;

    void clear( void );
    int  size( void );

    const_iterator begin( void );
    const_iterator end( void );

    enum StructArrayTypes
    {
        StructArrayType_LogArray,
        StructArrayType_AlarmArray,
//        StructArray_MultiArray  //  eh?  no clear definition in the spec
    };
};



//  ************************  NOTE  ************************
//  The code for CtiIONStructArrayTemplate is included in the
//    class definition in order to get around an MSVC bug
//    that prevents template exporting.


template < class T >
class CtiIONStructArrayTemplate : public CtiIONStructArray
{
private:

    bool assignStructArrayType( void )
    {
        T element;

        bool retVal = true;

        if(      Struct::isStructType(&element, Struct::StructType_Alarm) )         setStructArrayType(StructArrayType_AlarmArray);
        else if( Struct::isStructType(&element, Struct::StructType_LogRecord) )     setStructArrayType(StructArrayType_LogArray);
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << FO(__FILE__) << " (" << __LINE__ << ")" << endl;
            }

            retVal = false;
        }

        return retVal;
    }

protected:

    friend CtiIONValue *restoreStructArray( unsigned char ionClass, unsigned char classDescriptor,
                                            unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    CtiIONStructArrayTemplate( )
    {
        setValid(assignStructArrayType());
    }

    virtual ~CtiIONStructArrayTemplate( )
    {
        clear( );
    }

    T *operator[]( unsigned long index )
    {
        return at(index);
    };

    T *at( unsigned long index )
    {
        T *tmp = NULL;

        if( index < _structArrayElements.size( ) )
        {
            tmp = (T *)_structArrayElements[index];
        }

        return tmp;
    }

    //  this is basically just a compile-time type-check for convenience
    void push_back( T *toAppend )
    {
        //  this function is protected, so it won't be listed among the public members
        CtiIONStructArray::push_back((CtiIONStruct *)toAppend);
    }
};


typedef CtiIONStructArrayTemplate<CtiIONAlarm>          CtiIONAlarmArray;
typedef CtiIONStructArrayTemplate<CtiIONLogRecord>      CtiIONLogArray;


#endif // #ifndef __ION_VALUE_STRUCTARRAY_H__
