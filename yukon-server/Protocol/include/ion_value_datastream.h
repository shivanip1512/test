#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_DATASTREAM_H__
#define __ION_VALUE_DATASTREAM_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_datastream.h
 *
 * Class:  CtiIONDataStream
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <vector>
using namespace std;

#include "ion_value_basic_array.h"
#include "ion_valuestructtypes.h"

class IM_EX_PROT CtiIONDataStream : public CtiIONSerializable
{
private:

    void parseByteStream( unsigned char *buf, unsigned long len );

    typedef vector<CtiIONValue *> DSVector;
    typedef DSVector::iterator       DSIterator;
    typedef DSVector::const_iterator DSConstIterator;

    DSVector _streamValues;

public:

    CtiIONDataStream( unsigned char *buf, unsigned long len );
    CtiIONDataStream( );
    ~CtiIONDataStream( );

    CtiIONDataStream &initialize( unsigned char *buf, unsigned long len );

    CtiIONDataStream  &push_back( CtiIONValue *toInsert );

    //  note that this function call does not delete the pointed-to object
    //    before it removes it from the vector - this is so you can grab things out of the list
    CtiIONDataStream  &erase( int index );

    //  this function, however, does delete all of the entries
    void clearAndDestroy( void );

    CtiIONValue *at( int index ) const;
    CtiIONValue *operator[]( int index ) const;

    int  size( void ) const;
    bool empty( void ) const;

    bool itemIs( int index, CtiIONValue::IONValueTypes type );
    bool itemIs( int index, CtiIONArray::IONArrayTypes type );
    bool itemIs( int index, CtiIONStruct::IONStructTypes type );

    bool itemsAre( CtiIONValue::IONValueTypes type );
    bool itemsAre( CtiIONArray::IONArrayTypes type );
    bool itemsAre( CtiIONStruct::IONStructTypes type );

    bool isValid( void );

    virtual void putSerialized( unsigned char *buf ) const;
    virtual unsigned int getSerializedLength( void ) const;
};


#endif  //  #ifndef __ION_VALUE_DATASTREAM_H__

