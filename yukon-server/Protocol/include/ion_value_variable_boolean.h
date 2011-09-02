#pragma once

#include "ion_value_variable.h"
#include "ion_value_variable_fixedarray_element.h"

class CtiIONBoolean : public CtiIONValueVariable, /*public CtiIONNumeric, */public CtiIONFixedArrayElement
{
private:

    bool _bool;
    char _string[10];

protected:

    unsigned int getElementLength( void ) const;
    void putElement( unsigned char *buf ) const;

    friend CtiIONValue *CtiIONValueVariable::restoreVariable( unsigned char ionClass, unsigned char classDescriptor,
                                                              unsigned char *buf, unsigned long len, unsigned long *bytesUsed );
    friend class CtiIONFixedArrayTemplate<CtiIONBoolean>;

    unsigned char getVariableClassDescriptor( void ) const;

    CtiIONBoolean( unsigned char *byteStream, unsigned long streamLength );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

public:

    CtiIONBoolean( bool initialValue );
    virtual ~CtiIONBoolean( );

    CtiIONBoolean &setValue( bool value );
    bool           getValue( void ) const;

    const char *toString( void );

    virtual bool isNumeric( void ) const  {  return true;  };
    double getNumericValue( void ) const;
};
