#pragma once

#include "ion_value.h"
#include "ion_value_numeric.h"

class CtiIONValueVariable : public CtiIONValue
{
private:

    enum VariableTypes;

    VariableTypes _variableType;

    enum VariableIONClassTypes
    {
        IONClass_Variable = 0x0f,
    };

protected:

    CtiIONValueVariable( VariableTypes variableType );
    CtiIONValueVariable( const CtiIONValueVariable &aRef );

    void putSerialized( unsigned char *buf ) const;
    unsigned int getSerializedLength( void ) const;

    void putSerializedHeader( unsigned char *buf ) const;
    unsigned int getSerializedHeaderLength( void ) const;

    enum VariableClassDescriptor
    {
        ClassDescriptor_Variable_BooleanFalse   = 0x01,
        ClassDescriptor_Variable_BooleanTrue    = 0x02,
        ClassDescriptor_Variable_StructEnd      = 0x03,
        ClassDescriptor_Variable_Program        = 0x06,
        ClassDescriptor_Variable_StructArrayEnd = 0x09,

        ClassDescriptor_Variable_FixedArray0    = 0x0a,
        ClassDescriptor_Variable_FixedArray1    = 0x0b,
        ClassDescriptor_Variable_FixedArray2    = 0x0c,
        ClassDescriptor_Variable_FixedArray3    = 0x0d,
        ClassDescriptor_Variable_FixedArray4    = 0x0e
    };

    virtual unsigned char getVariableClassDescriptor( void ) const = 0;

    virtual void putSerializedValue( unsigned char *buf ) const = 0;
    virtual unsigned int getSerializedValueLength( void ) const = 0;

    friend CtiIONValue *CtiIONValue::restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    static CtiIONValue *restoreVariable( unsigned char ionClass, unsigned char classType,
                                         unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    virtual ~CtiIONValueVariable();

    CtiIONValueVariable &operator=( const CtiIONValueVariable &aRef );

    static bool   isVariableType( CtiIONValue *toCheck, VariableTypes type );
    bool          isVariableType( VariableTypes type ) const;
    VariableTypes getVariableType( void )              const;

    enum VariableTypes
    {
        Variable_Boolean,
        Variable_StructEnd,
        Variable_Program,
        Variable_StructArrayEnd,
        Variable_FixedArray
    };
};


class CtiIONStructEnd : public CtiIONValueVariable
{
    friend class CtiIONStruct;

private:

    void putSerializedValue( unsigned char *buf ) const
    {
        //  no-op
    }

    unsigned int getSerializedValueLength( void ) const
    {
        return 0;
    }

protected:

    unsigned char getVariableClassDescriptor( void ) const  {  return ClassDescriptor_Variable_StructEnd;  };

public:

    CtiIONStructEnd( ) :
        CtiIONValueVariable(Variable_StructEnd)
    {
        setValid(true);
    }

    virtual ~CtiIONStructEnd()
    {
    }

};


class CtiIONStructArrayEnd : public CtiIONValueVariable
{
    friend class CtiIONStructArray;

private:

    void putSerializedValue( unsigned char *buf ) const  {  /*  no-op  */  };
    unsigned int getSerializedValueLength( void ) const  {  return 0;      };

protected:

    unsigned char getVariableClassDescriptor( void ) const  {  return ClassDescriptor_Variable_StructArrayEnd;  };

public:

    CtiIONStructArrayEnd( ) :
        CtiIONValueVariable(Variable_StructArrayEnd)
    {
        setValid(true);
    }
};
