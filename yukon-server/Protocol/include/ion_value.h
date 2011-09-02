#pragma once

#include <vector>

#include "dlldefs.h"

#include "ion_serializable.h"

#include "numstr.h"


class CtiIONValue : public CtiIONSerializable
{
private:

    enum ValueTypes;

    bool _valid;

    ValueTypes _valueType;

    //  these are not inherited to IONValueFixed and the like -
    //    they have to define their own
    enum ValueIONClassTypes
    {
        IONClass_Fixed0       = 0x01,
        IONClass_Fixed1       = 0x03,
        IONClass_Fixed2       = 0x04,
        IONClass_Fixed3       = 0x05,
        IONClass_Fixed4       = 0x06,
        IONClass_Struct       = 0x07,
        IONClass_StructArray  = 0x08,
        IONClass_VariableSize = 0x0f
    };

protected:

    CtiIONValue( ValueTypes valueType );

    CtiIONValue &setValid( bool valid );

    //  for convenience inside the IONValue classes only
    typedef class CtiIONValue         Value;
    typedef class CtiIONValueVariable Variable;
    typedef class CtiIONValueFixed    Fixed;
    typedef class CtiIONStruct        Struct;
    typedef class CtiIONStructArray   StructArray;
    typedef class CtiIONFixedArray    FixedArray;

    //  both of these need to call restoreObject
    friend class CtiIONDataStream;
    friend class CtiIONMethod;

    static CtiIONValue *restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    virtual ~CtiIONValue( );

    enum ValueTypes
    {
        ValueType_Fixed,
        ValueType_Variable,
        ValueType_Struct,
        ValueType_StructArray
    };
/*
    enum IONValueErrors
    {
        Error_Restore_EndOfBuffer
    };
*/
    virtual void putSerialized( unsigned char *buf ) const = 0;
    virtual unsigned int getSerializedLength( void ) const = 0;

    bool         isValid( void ) const;

    static bool  isValueType ( CtiIONValue *toCheck, ValueTypes type );
    bool         isValueType ( ValueTypes type ) const;
    ValueTypes   getValueType( void ) const;

    virtual bool   isNumeric( void ) const;
    virtual double getNumericValue( void ) const;

    virtual const char *toString( void );

    static bool  isFixed( CtiIONValue *toCheck );
    bool         isFixed( void ) const;

    static bool  isVariable( CtiIONValue *toCheck );
    bool         isVariable( void ) const;

    static bool  isStruct( CtiIONValue *toCheck );
    bool         isStruct( void ) const;

    static bool  isStructArray( CtiIONValue *toCheck );
    bool         isStructArray( void ) const;
};
