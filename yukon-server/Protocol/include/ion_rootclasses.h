#pragma warning( disable : 4786 )

#ifndef __ION_ROOTCLASSES_H__
#define __ION_ROOTCLASSES_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_rootclasses.h
 *
 * Class:  CtiIONSerializable, CtiIONValue, CtiIONClass, CtiIONDataStream
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <vector>
using namespace std;

#include "dlldefs.h"


class IM_EX_PROT CtiIONSerializable
{
public:
    CtiIONSerializable( )  { };
    ~CtiIONSerializable( )  { };

    virtual void putSerialized( unsigned char *buf ) const;
    virtual unsigned int getSerializedLength( void ) const;
};


class IM_EX_PROT CtiIONValue : protected CtiIONSerializable
{
public:

    ~CtiIONValue( ) { };

    enum IONValueTypes;

    IONValueTypes getType( void ) const;
    int           isNumeric( void );
    int           isValid( void );

    enum IONValueTypes
    {
        IONChar,
        IONFloat,
        IONSignedInt,
        IONTime,
        IONUnsignedInt,
        IONNumeric,
        IONBoolean,
        IONArray,
        IONProgram,
        IONInvalid
    };

    enum IONValueErrors
    {
        Restore_EndOfBuffer
    };

protected:

    CtiIONValue( IONValueTypes valueType ) :
        _valueType(valueType)
        { };
    IONValueTypes _valueType;
    void setValid( bool valid );

    friend class CtiIONDataStream;  //  ideally, only DataStream would be friend to IONValue, and only for the sake of
    friend class CtiIONStruct;      //    restoreObject.  however, Struct and Method do need to pull in subsequent values,
    friend class CtiIONMethod;      //    so they are given access as well.
    friend class CtiIONArray;
    friend class CtiIONStructArray;

    void putSerialized( unsigned char *buf ) const;
    unsigned int getSerializedLength( void ) const;

    virtual void putSerializedHeader( unsigned char *buf ) const;
    virtual unsigned int getSerializedHeaderLength( void ) const;

    virtual void putSerializedValue( unsigned char *buf ) const = 0;
    virtual unsigned int getSerializedValueLength( void ) const = 0;

    virtual unsigned char *putClassSize( unsigned char key, unsigned char *buf ) const;

    static CtiIONValue *restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

private:
    bool _valid;
};


#endif  //  #ifndef __ION_ROOTCLASSES_H__

