#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_VARIABLE_FIXEDARRAY_H__
#define __ION_VALUE_VARIABLE_FIXEDARRAY_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_variable_fixedarray.h
 *
 * Class:  CtiIONFixedArray
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_value_variable.h"
#include "ion_value_variable_fixedarray_element.h"

#include "ion_value_fixed_char.h"
#include "ion_value_variable_boolean.h"
#include "ion_value_fixed_float.h"
#include "ion_value_fixed_intsigned.h"
#include "ion_value_fixed_intunsigned.h"


class CtiIONFixedArray : public CtiIONValueVariable
{
    enum FixedArrayTypes;

private:

    FixedArrayTypes _arrayType;

    enum FixedArrayClassDescriptor
    {
        ClassDescriptor_FixedArray_Char        = 0xa,
        ClassDescriptor_FixedArray_Boolean     = 0xb,
        ClassDescriptor_FixedArray_Float       = 0xc,
        ClassDescriptor_FixedArray_SignedInt   = 0xd,
        ClassDescriptor_FixedArray_UnsignedInt = 0xe
    };

protected:

    vector< CtiIONFixedArrayElement * > _array;

    virtual unsigned int getSerializedValueLength( void ) const;
    virtual void putSerializedValue( unsigned char *buf ) const;

    unsigned char getVariableClassDescriptor( void ) const;

    virtual unsigned int getSerializedHeaderLength( void ) const;
    virtual void putSerializedHeader( unsigned char *buf ) const;

    void setFixedArrayType( FixedArrayTypes type );

    friend class CtiIONValueVariable;

    static CtiIONValueVariable *restoreFixedArray( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    //  heh, all of the other functions here are const or static - who cares if they can instantiate an orphan copy of the class
    CtiIONFixedArray();

    FixedArrayTypes getFixedArrayType( void ) const;
    static bool     isFixedArrayType ( CtiIONValue *toCheck, FixedArrayTypes arrayType );
    bool            isFixedArrayType ( FixedArrayTypes arrayType ) const;
    static bool     isNumericArray( CtiIONValue *toCheck );
    bool            isNumericArray( void ) const;

    enum FixedArrayTypes
    {
        FixedArray_Char,
        FixedArray_Boolean,
        FixedArray_Float,
        FixedArray_SignedInt,
        FixedArray_UnsignedInt
    };
};



//  ************************  NOTE  ************************
//  The code for CtiIONFixedArrayTemplate is included in the
//    class definition in order to get around an MSVC bug
//    that prevents template exporting.


template < class T >
class CtiIONFixedArrayTemplate : public CtiIONFixedArray
{
private:

    bool assignArrayType( void )
    {
        T element(0);

        bool retVal = true;

        if( Variable::isVariableType(&element, Variable::Variable_Boolean) )    setFixedArrayType(FixedArray_Boolean);
        else if( Fixed::isFixedType(&element,  Fixed::Fixed_Char) )             setFixedArrayType(FixedArray_Char);
        else if( Fixed::isFixedType(&element,  Fixed::Fixed_Float) )            setFixedArrayType(FixedArray_Float);
        else if( Fixed::isFixedType(&element,  Fixed::Fixed_SignedInt) )        setFixedArrayType(FixedArray_SignedInt);
        else if( Fixed::isFixedType(&element,  Fixed::Fixed_UnsignedInt) )      setFixedArrayType(FixedArray_UnsignedInt);
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = false;
        }

        return retVal;
    }

protected:

    friend CtiIONValueVariable *CtiIONFixedArray::restoreFixedArray( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    CtiIONFixedArrayTemplate( unsigned long itemCount, unsigned long itemLength, unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
    {
        CtiIONFixedArrayElement *tmpElement;
        T *tmpValue;
        unsigned long pos = 0;

        //  this, and the isValid check below, should ensure that type T is always of type FixedArrayElement
        setValid(assignArrayType());

        if( isValid() )
        {
            //  ACH:  maybe check to see that (itemCount * itemLength) < len
            for( unsigned long i = 0; i < itemCount && isValid(); i++ )
            {
                tmpValue = CTIDBG_new T((buf + pos), itemLength);

                if( tmpValue != NULL && tmpValue->isValid() )
                {
                    push_back(tmpValue);
                    pos += itemLength;
                }
                else
                {
                    setValid(false);
                    pos = len;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            pos = len;
        }

        *bytesUsed = pos;
    }

    ~CtiIONFixedArrayTemplate( )
    {
        clear( );
    }

public:

    CtiIONFixedArrayTemplate( )
    {
        setValid(assignArrayType());
    }

    T *operator[]( unsigned long index )
    {
        return at(index);
    };

    T *at( unsigned long index )
    {
        T *tmp = NULL;

        if( index < _array.size( ) )
        {
            tmp = (T *)_array[index];
        }

        return tmp;
    }

    void push_back( T *toAppend )
    {
        //  ACH:  check for appropriate type
        _array.push_back( toAppend );
    }

    void clear( void )
    {
        while( !_array.empty() )
        {
            delete _array.back();

            _array.pop_back();
        }
    }

    int size( void )
    {
        return _array.size( );
    }
};


typedef CtiIONFixedArrayTemplate<CtiIONChar>            CtiIONCharArray;
typedef CtiIONFixedArrayTemplate<CtiIONBoolean>         CtiIONBooleanArray;
typedef CtiIONFixedArrayTemplate<CtiIONFloat>           CtiIONFloatArray;
typedef CtiIONFixedArrayTemplate<CtiIONSignedInt>       CtiIONSignedIntArray;
typedef CtiIONFixedArrayTemplate<CtiIONUnsignedInt>     CtiIONUnsignedIntArray;


#endif  //  #ifndef __ION_VALUE_VARIABLE_FIXEDARRAY_H__


