#pragma once

#include "dlldefs.h"
#include <string>

class IM_EX_CTIBASE CtiNumStr
{
private:

    long          _integer;
    unsigned long _unsigned;
    double  _float;

    int    _precision;  //  stores floating precision
    int    _bits;       //  stores integer bit sizes for negative hex representations
    int    _padding;
    bool   _zeroes;

    enum Formats
    {
        Format_Default,
        Format_Exponential,
        Format_Hex,
        Format_XHex,
    } _fmt;

    enum DataTypes
    {
        DataType_FloatingPoint,
        DataType_Integer,
        DataType_Unsigned,
    } _dataType;

    //  both of these are undefined - no one should call these...  CtiNumStr is a one-shot object
    CtiNumStr &operator=( const CtiNumStr &aRef );
    CtiNumStr( const CtiNumStr &aRef );

    void init( unsigned int width );

protected:

    void buildIntString    ( std::string &s ) const;
    void buildFloatString  ( std::string &s ) const;

public:

    explicit CtiNumStr( double dVal, int precision = DefaultPrecision );
    explicit CtiNumStr( float  fVal, int precision = DefaultPrecision );

    explicit CtiNumStr( unsigned char  ucVal );
    explicit CtiNumStr(          char   cVal );
    explicit CtiNumStr( unsigned short usVal );
    explicit CtiNumStr(          short  sVal );
    explicit CtiNumStr( unsigned int   uiVal );
    explicit CtiNumStr(          int    iVal );
    explicit CtiNumStr( unsigned long  ulVal );
    explicit CtiNumStr(          long   lVal );

    ~CtiNumStr( );

    operator std::string() const;

    std::string toString( void ) const;

    CtiNumStr &hex ( int pad = -1 );
    CtiNumStr &xhex( int pad = -1 );

    CtiNumStr &exp ( void );

    CtiNumStr &spad( unsigned int pad );  //  includes the "0x" prefix - defines total space-padded width
    CtiNumStr &zpad( unsigned int pad );  //  excludes the "0x" prefix - defines zero-padded width of the number itself

    enum Limits
    {
        DefaultPrecision =  3,
        MaxPrecision     = 15,
        MaxBits          = 32,
        MaxZeroPadding   = 20,
        MaxSpacePadding  = 20,
    };
};

//  overload the + operator for (string, CtiNumStr)
IM_EX_CTIBASE std::string operator +(const std::string &str, CtiNumStr &numStr);
IM_EX_CTIBASE std::string operator +(CtiNumStr &numStr, const std::string &str);

//  overload the + operator for (char *, CtiNumStr)
IM_EX_CTIBASE std::string operator +(const char *s, CtiNumStr &numStr);
IM_EX_CTIBASE std::string operator +(CtiNumStr &numStr, const char *s);

//  overload the << operator for ostream
IM_EX_CTIBASE std::ostream &operator <<(std::ostream &o, CtiNumStr &numStr);

