#pragma warning( disable : 4786 )

#ifndef __ION_VALUEBASICTYPES_H__
#define __ION_VALUEBASICTYPES_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasictypes.h
 *
 * Class:  CtiION*
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 *         Basic value type classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
 
#include "ion_rootclasses.h"
#include "ctitypes.h"

class CtiIONChar : public CtiIONValue
{
public:

    CtiIONChar( char initialValue='\0' ) : 
        CtiIONValue(IONChar),
        _char(initialValue & 0x7F)
        { };
    ~CtiIONChar( ) { };

    CtiIONChar &setValue( char value ) { _char = value & 0x7F;  return *this; };
    char getValue( void )              { return _char; };

protected:
    
    typedef CtiIONValue Inherited;
    friend Inherited;
    friend class CtiIONCharArray;
    
    CtiIONChar( unsigned char *byteStream, unsigned long streamLength );
    
    unsigned int getSerializedValueLength( void ) { return 1; };
    void putSerializedValue( unsigned char *buf ) { memcpy( buf, &_char, getSerializedValueLength( ) ); };

private:
    
    char _char;
};



class CtiIONNumeric : public CtiIONValue
{
public:
    CtiIONNumeric( CtiIONValue::IONValueTypes valueType ) :
        CtiIONValue(valueType)
        { };
    ~CtiIONNumeric( ) { };
};


class CtiIONFloat : public CtiIONNumeric
{
public:

    CtiIONFloat( float initialValue=0.0 ) :
        CtiIONNumeric(IONFloat),
        _float(initialValue)
        { };
    CtiIONFloat( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONFloat( ) { };
    
    unsigned int getSerializedValueLength( void ) { return 4; };
    void putSerializedValue( unsigned char *buf ) { memcpy( buf, &_float, getSerializedValueLength( ) ); };

    CtiIONFloat &setValue( float value ) { _float = value;  return *this; };
    float        getValue( void )       { return _float; };

private:

    float _float;
};



class CtiIONSignedInt : public CtiIONNumeric
{
public:

    CtiIONSignedInt( long initialValue=0 ) :
        CtiIONNumeric(IONSignedInt),
        _signedInt(initialValue)
        { };
    CtiIONSignedInt( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONSignedInt( ) { };

    unsigned int getSerializedValueLength( void ) { return 4; };
    void putSerializedValue( unsigned char *buf ) { memcpy( buf, &_signedInt, getSerializedValueLength( ) ); };

    CtiIONSignedInt &setValue( long value ) { _signedInt = value;  return *this; };
    long             getValue( void )       { return _signedInt; };

private:

    long _signedInt;
};



class CtiIONUnsignedInt : public CtiIONNumeric
{
public:

    CtiIONUnsignedInt( unsigned int initialValue=0 ) :
        CtiIONNumeric(IONUnsignedInt),
        _unsignedInt(initialValue)
        { };
    CtiIONUnsignedInt( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONUnsignedInt( ) { };

    unsigned int getSerializedValueLength( void ) { return 4; };
    void putSerializedValue( unsigned char *buf ) { memcpy( buf, &_unsignedInt, getSerializedValueLength( ) ); };

    CtiIONUnsignedInt &setValue( unsigned int value ) { _unsignedInt = value;  return *this; };
    unsigned int       getValue( void )               { return _unsignedInt; };

private:

    unsigned int _unsignedInt;
};



class CtiIONBoolean : public CtiIONNumeric
{
public:

    CtiIONBoolean( int initialValue=FALSE ) :
        CtiIONNumeric(IONBoolean),
        _bool(initialValue) 
        { };
    CtiIONBoolean( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONBoolean( ) { };

    unsigned int getSerializedValueLength( void ) { return 0; };  //  it's all in the header
    void putSerializedValue( unsigned char *buf ) { };            //

    CtiIONBoolean &setValue( int value ) { _bool = value; };
    int           getValue( void )       { return _bool; };

private:

    int _bool;
};



class CtiIONTime : public CtiIONValue
{
public:

    CtiIONTime( unsigned long seconds = 0, unsigned long fractionalSeconds = 0 ) :
        CtiIONValue(IONTime)
    {
        setSeconds( seconds );
        setFractionalSeconds( fractionalSeconds );
    };
    CtiIONTime( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONTime( ) { };

    CtiIONTime    &setSeconds( unsigned long value ) { _seconds = value;  
                                                       return *this; };
    unsigned long  getSeconds( void )                { return _seconds; };
    CtiIONTime    &setFractionalSeconds( unsigned long value );
    unsigned long  getFractionalSeconds( void )      { return _fractionalSeconds; };
    
protected:

    unsigned int getSerializedValueLength( void ) { return 8; };
    void putSerializedValue( unsigned char *buf );

private:

    unsigned long _seconds;
    unsigned long _fractionalSeconds;
};



class CtiIONArray : public CtiIONValue
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
    
    IONArrayTypes getArrayType( void )   { return _arrayType; };
    int isNumericArray( void );

protected:

    friend class CtiIONValue;
    
    CtiIONArray( IONArrayTypes arrayType ) :
        CtiIONValue(IONArray),
        _arrayType(arrayType)
        { };

    ~CtiIONArray( )
        {  clearArrayElements( );  };
    
    virtual unsigned int getSerializedValueLength( void );
    virtual void putSerializedValue( unsigned char *buf );

    virtual unsigned int getSerializedHeaderLength( void );
    virtual void putSerializedHeader( unsigned char *buf );

    static CtiIONArray *restoreStruct( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength );
    static CtiIONArray *restoreStructArray( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength );
    static CtiIONArray *restoreFixedArray( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength );
    
    unsigned char *putClassSize( unsigned char key, unsigned char *buf );
    unsigned char getArrayKey( void );
        
    int getSize( void )                  
        {  return _array.size( );  };

    CtiIONValue *getArrayElement( unsigned long index );
    CtiIONArray &appendArrayElement( CtiIONValue *toAppend );
    void clearArrayElements( void );
    
private:

    vector< CtiIONValue * > _array;
    IONArrayTypes _arrayType;
    int numEntries;

    void setArrayType( IONArrayTypes arrayType );
};




class CtiIONProgram : public CtiIONValue
{
public:

    CtiIONProgram( CtiIONStatement *initial );
    CtiIONProgram( ) :
        CtiIONValue(IONProgram)
        { };
    CtiIONProgram( unsigned char *byteStream, unsigned long streamLength );

    ~CtiIONProgram( );

    CtiIONProgram &addStatement( CtiIONStatement *toAdd ) 
    { 
        _statements.push_back( toAdd ); 
        return *this; 
    };
        
    unsigned int getSerializedValueLength( void );
    void putSerializedValue( unsigned char *buf );

private:

    vector< CtiIONStatement * > _statements;
};


#endif  //  #ifndef __ION_VALUEBASICTYPES_H__
    
