/*-----------------------------------------------------------------------------*
*
* File:
*
* Class:  CtiNumStr
* Date:   12/7/2001
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2007/10/30 13:33:03 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __NUMSTR_H__
#define __NUMSTR_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include <string>

class IM_EX_CTIBASE CtiNumStr
{
private:

    enum
    {
        DataStringLength = 40
    };

    long          _integer;
    unsigned long _unsigned;
    double  _float;
    void   *_pointer;

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
        DataType_Pointer,
    } _dataType;

    //  both of these are undefined - no one should call these...  CtiNumStr is a one-shot object
    CtiNumStr &operator=( const CtiNumStr &aRef );
    CtiNumStr( const CtiNumStr &aRef );

    void init( unsigned int width );

protected:

    void buildIntString    ( std::string &s ) const;
    void buildFloatString  ( std::string &s ) const;
    void buildPointerString( std::string &s ) const;

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

    CtiNumStr( void *vpVal );

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

#endif // #ifndef __NUMSTR_H__
