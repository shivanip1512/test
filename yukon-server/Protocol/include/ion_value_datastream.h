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

    vector<CtiIONValue *> _streamValues;
    typedef vector<CtiIONValue *>::iterator streamIterator;

public:

    CtiIONDataStream( unsigned char *buf, unsigned long len );
    CtiIONDataStream( );
    ~CtiIONDataStream( );

    CtiIONDataStream &initialize( unsigned char *buf, unsigned long len );
    void clear( void );

    CtiIONDataStream  &appendItem( CtiIONValue *toInsert );
    CtiIONValue *getItem( int index ) const;
    CtiIONValue *operator[]( int index ) const;
    int  getItemCount( void ) const;
    bool empty( void ) const;

    bool contains( CtiIONValue::IONValueTypes type );
    bool contains( CtiIONArray::IONArrayTypes type );
    bool contains( CtiIONStruct::IONStructTypes type );

    static bool itemIs( CtiIONValue *toCheck, CtiIONValue::IONValueTypes type );
    static bool itemIs( CtiIONValue *toCheck, CtiIONArray::IONArrayTypes type );
    static bool itemIs( CtiIONValue *toCheck, CtiIONStruct::IONStructTypes type );

    virtual void putSerialized( unsigned char *buf ) const;
    virtual unsigned int getSerializedLength( void ) const;
};


#endif  //  #ifndef __ION_VALUE_DATASTREAM_H__

