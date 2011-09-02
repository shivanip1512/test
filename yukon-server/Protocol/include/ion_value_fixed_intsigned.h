#pragma once

#include "ion_value_fixed.h"
#include "ion_value_numeric.h"
#include "ion_value_variable_fixedarray_element.h"

class CtiIONSignedInt : public CtiIONValueFixed, /*public CtiIONNumeric, */public CtiIONFixedArrayElement
{
private:

    long _signedInt;
    char _string[20];

protected:

    unsigned int getElementLength( void ) const;
    void putElement( unsigned char *buf ) const;

    friend CtiIONValue *CtiIONValueFixed::restoreFixed( unsigned char ionClass, unsigned char classDescriptor,
                                                        unsigned char *buf, unsigned long len, unsigned long *bytesUsed );
    friend class CtiIONFixedArrayTemplate<CtiIONSignedInt>;

    CtiIONSignedInt( unsigned char *byteStream, unsigned long streamLength );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

public:

    CtiIONSignedInt( long initialValue );
    virtual ~CtiIONSignedInt( );

    CtiIONSignedInt &setValue( long value );
    long             getValue( void ) const;

    const char *toString( void );

    virtual bool isNumeric( void ) const  {  return true;  };
    double getNumericValue( void ) const;
};
