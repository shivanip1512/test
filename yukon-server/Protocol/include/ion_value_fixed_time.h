#pragma once

#include "ion_value_fixed.h"

class CtiIONTime : public CtiIONValueFixed
{
private:

    unsigned long _seconds;
    unsigned long _fractionalSeconds;

protected:

    friend CtiIONValueFixed;

    CtiIONTime( unsigned char *byteStream, unsigned long streamLength );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

public:

    CtiIONTime( unsigned long seconds, unsigned long fractionalSeconds );
    virtual ~CtiIONTime( );

    CtiIONTime    &setSeconds( unsigned long value );
    unsigned long  getSeconds( void ) const;

    CtiIONTime    &setFractionalSeconds( unsigned long value );
    unsigned long  getFractionalSeconds( void ) const;
    unsigned long  getMilliseconds( void ) const;

    enum
    {
        MillisecondsToFractionalMultiplier = 2147000
    };
};
