#pragma once

#include "ion_value_variable.h"
#include "ion_value_variable_fixedarray_element.h"

#include "ion_value_fixed_char.h"
#include "ion_value_variable_boolean.h"
#include "ion_value_fixed_float.h"
#include "ion_value_fixed_intsigned.h"
#include "ion_value_fixed_intunsigned.h"


template < class T > class CtiIONFixedArrayTemplate;

typedef CtiIONFixedArrayTemplate< CtiIONChar >            CtiIONCharArray;
typedef CtiIONFixedArrayTemplate< CtiIONBoolean >         CtiIONBooleanArray;
typedef CtiIONFixedArrayTemplate< CtiIONFloat >           CtiIONFloatArray;
typedef CtiIONFixedArrayTemplate< CtiIONSignedInt >       CtiIONSignedIntArray;
typedef CtiIONFixedArrayTemplate< CtiIONUnsignedInt >     CtiIONUnsignedIntArray;


class CtiIONFixedArray : public CtiIONValueVariable
{
    enum FixedArrayTypes;

private:

    FixedArrayTypes _arrayType;

    enum FixedArrayItemsDescriptor
    {
        ItemsDescriptor_ItemsNibbleMax  = 0xd,
        ItemsDescriptor_ItemsNextByte   = 0xe,
        ItemsDescriptor_ItemsNext4Bytes = 0xf
    };

    enum FixedArrayLengthDescriptor
    {
        LengthDescriptor_LengthNibbleMax  = 0xc,
        LengthDescriptor_LengthNextByte   = 0xd,
        LengthDescriptor_LengthNext4Bytes = 0xe,
        LengthDescriptor_Reserved         = 0xf
    };

    enum FixedArrayClassDescriptor
    {
        ClassDescriptor_FixedArray_Char        = 0xa,
        ClassDescriptor_FixedArray_Boolean     = 0xb,
        ClassDescriptor_FixedArray_Float       = 0xc,
        ClassDescriptor_FixedArray_SignedInt   = 0xd,
        ClassDescriptor_FixedArray_UnsignedInt = 0xe
    };
/*
    unsigned char low_nibble ( unsigned char byte )  {  return  (byte & 0x0f);        };
    unsigned char high_nibble( unsigned char byte )  {  return ((byte & 0xf0) >> 4);  };
*/
protected:

    typedef vector< CtiIONFixedArrayElement * > array_element_vector;
    array_element_vector _array;

    unsigned char getVariableClassDescriptor( void ) const;

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

    unsigned int getArrayHeaderLength( void ) const;
    void putArrayHeader( unsigned char *buf ) const;

    unsigned int getArrayElementsLength( void ) const;
    void putArrayElements( unsigned char *buf ) const;

    void setFixedArrayType( FixedArrayTypes type );

    friend class CtiIONValueVariable;

    static CtiIONValueVariable *restoreFixedArray( unsigned char classDescriptor, const unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

public:

    //  heh, all of the other functions here are const or static - who cares if they can instantiate an orphan copy of the class
    CtiIONFixedArray();

    //  make sure this always gets called
    virtual ~CtiIONFixedArray();

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

    void clear( void );
    int  size( void );


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
            CTILOG_ERROR(dout, "unknown variable type");

            retVal = false;
        }

        return retVal;
    }

    char *_string;

protected:

    friend CtiIONValueVariable *CtiIONFixedArray::restoreFixedArray( unsigned char classDescriptor, const unsigned char *buf, unsigned long len, unsigned long *bytesUsed );

    CtiIONFixedArrayTemplate( unsigned long itemCount, unsigned long itemLength, const unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
    {
        CtiIONFixedArrayElement *tmpElement;
        T *tmpValue;
        unsigned long pos = 0;

        _string = NULL;

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
                    delete tmpValue;

                    setValid(false);
                    pos = len;
                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Invalid array type");

            pos = len;
        }

        *bytesUsed = pos;
    }

public:

    CtiIONFixedArrayTemplate( )
    {
        setValid(assignArrayType());
    }

    virtual ~CtiIONFixedArrayTemplate( )
    {
        delete [] _string;
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

    const char *toString( void )
    {
        int i;

        switch( getFixedArrayType() )
        {
            case FixedArray_Char:
            {
                delete [] _string;

                _string = new char[size() + 1];

                for( i = 0; i < _array.size(); i++ )
                {
                    _string[i] = ((CtiIONChar *)(_array.at(i)))->getValue();
                }

                _string[_array.size()] = '\0';

                return _string;

                break;
            }

            default:
            {
                return CtiIONValue::toString();
            }
        }
    }
};
