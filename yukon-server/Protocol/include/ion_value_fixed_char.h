#pragma once

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
    virtual ~CtiIONChar( );

    CtiIONChar &setValue( unsigned char value );
    unsigned char getValue( void ) const;

    const char *toString( void );
};
