#pragma warning( disable : 4786 )

#ifndef __ION_VALUEBASIC_CHAR_H__
#define __ION_VALUEBASIC_CHAR_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_char.h
 *
 * Class:  CtiIONChar
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_rootclasses.h"

class IM_EX_PROT CtiIONChar : public CtiIONValue
{
public:

    CtiIONChar( unsigned char initialValue );
    ~CtiIONChar( );

    CtiIONChar &setValue( unsigned char value );
    unsigned char getValue( void );

protected:

    typedef CtiIONValue Inherited;
    friend Inherited;
    friend class CtiIONCharArray;

    CtiIONChar( unsigned char *byteStream, unsigned long streamLength );

    void putSerializedValue( unsigned char *buf ) const;
    unsigned int getSerializedValueLength( void ) const;

private:

    unsigned char _char;
};


#endif  //  #ifndef __ION_VALUEBASIC_CHAR_H__

