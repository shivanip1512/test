/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed_char.h
 *
 * Class:  CtiIONChar
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __ION_VALUE_FIXED_CHAR_H__
#define __ION_VALUE_FIXED_CHAR_H__
#pragma warning( disable : 4786 )


#include "ion_value_fixed.h"
#include "ion_value_variable_fixedarray_element.h"


class CtiIONChar : public CtiIONValueFixed, public CtiIONFixedArrayElement
{
private:

    unsigned char _char;
    char          _string[2];

protected:

    unsigned int getElementLength( void ) const;
    void putElement( unsigned char *buf ) const;

    friend CtiIONValue *CtiIONValueFixed::restoreFixed( unsigned char ionClass, unsigned char classDescriptor,
                                                        unsigned char *buf, unsigned long len, unsigned long *bytesUsed );
    friend class CtiIONFixedArrayTemplate<CtiIONChar>;

    void putSerializedValue( unsigned char *buf ) const;
    unsigned int getSerializedValueLength( void ) const;

    CtiIONChar( unsigned char *byteStream, unsigned long streamLength );

public:

    CtiIONChar( unsigned char initialValue );
    ~CtiIONChar( );

    CtiIONChar &setValue( unsigned char value );
    unsigned char getValue( void ) const;

    const char *toString( void );
};


#endif  //  #ifndef __ION_VALUE_FIXED_CHAR_H__

