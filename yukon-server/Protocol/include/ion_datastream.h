#pragma once

#include <vector>

#include "ion_value.h"
#include "ion_value_fixed.h"
#include "ion_value_variable.h"
#include "ion_value_variable_fixedarray.h"
#include "ion_value_struct.h"
#include "ion_value_structarray.h"

class CtiIONDataStream : public CtiIONSerializable
{
private:

    void parseByteStream( unsigned char *buf, unsigned long len );

    typedef std::vector<CtiIONValue *> DSVector;
    typedef DSVector::iterator       DSIterator;
    typedef DSVector::const_iterator DSConstIterator;

    DSVector _streamValues;

public:

    CtiIONDataStream( unsigned char *buf, unsigned long len );
    CtiIONDataStream( );
    virtual ~CtiIONDataStream( );

    CtiIONDataStream &initialize( unsigned char *buf, unsigned long len );

    CtiIONDataStream &push_back( CtiIONValue *toInsert );

    //  note that this function call does not delete the pointed-to object
    //    before it removes it from the vector - this is so you can grab things out of the list
    CtiIONDataStream &erase( int index );

    //  this function, however, does delete all of the entries
    void clearAndDestroy( void );

    CtiIONValue *at( int index ) const;
    //CtiIONValue *operator[]( int index ) const;

    int  size( void ) const;
    bool empty( void ) const;

    bool itemIsType( int index, CtiIONValueFixed::FixedTypes        type );
    bool itemIsType( int index, CtiIONValueVariable::VariableTypes  type );
    bool itemIsType( int index, CtiIONFixedArray::FixedArrayTypes   type );
    bool itemIsType( int index, CtiIONStruct::StructTypes           type );
    bool itemIsType( int index, CtiIONStructArray::StructArrayTypes type );

    template <class T>
    bool itemsAreType( T type )
    {
        bool result = true;

        for( int i = 0; (i < _streamValues.size()) && result; i++ )
        {
            result &= itemIsType(i, type);
        }

        return result;
    }

    bool isValid( void );

    virtual void putSerialized( unsigned char *buf ) const;
    virtual unsigned int getSerializedLength( void ) const;
};
