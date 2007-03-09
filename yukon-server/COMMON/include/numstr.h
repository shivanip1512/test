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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2007/03/09 21:31:12 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __NUMSTR_H__
#define __NUMSTR_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include <string>

using std::string;

class IM_EX_CTIBASE CtiNumStr
{
private:

    enum
    {
        DataStringLength = 40
    };

    union _types
    {
        double d;
        float f;

        int c;
        unsigned int uc;

        short s;
        unsigned short us;
        int i;
        unsigned int ui;
        long l;
        unsigned long ul;
        void *vp;
    } _data;

    char _dataString[DataStringLength];

    int    _precision;
    int    _padding;
    int    _zeroes;

    enum Formats
    {
        Format_Default,
        Format_Hex,
        Format_XHex,
        Format_Exponential
    } _fmt;

    enum DataTypes
    {
        DataType_Double,
        DataType_Float,
        DataType_Char,
        DataType_UChar,
        DataType_Short,
        DataType_UShort,
        DataType_Int,
        DataType_UInt,
        DataType_Long,
        DataType_ULong,
        DataType_Pointer
    } _dataType;

protected:

    void buildIntString( void );
    void buildFloatString( void );
    void buildPointerString( void );

public:

    explicit CtiNumStr( double dVal, int precision=DefaultPrecision );
    explicit CtiNumStr( float fVal,  int precision=DefaultPrecision );
    explicit CtiNumStr( char cVal );
    explicit CtiNumStr( unsigned char ucVal );
    explicit CtiNumStr( short sVal );
    explicit CtiNumStr( unsigned short usVal );
    explicit CtiNumStr( int iVal );
    explicit CtiNumStr( unsigned int uiVal );
    explicit CtiNumStr( long lVal );
    explicit CtiNumStr( unsigned long ulVal );
    CtiNumStr( void *vpVal );

    CtiNumStr( const CtiNumStr &aRef );

    ~CtiNumStr( );

    CtiNumStr &operator=( const CtiNumStr &aRef );
    operator string ( );

    void init( void );

    string toString(void);

    CtiNumStr &hex( void );
    CtiNumStr &xhex( void );

    CtiNumStr &exp( void );

    CtiNumStr &spad( unsigned int pad );
    CtiNumStr &zpad( unsigned int pad );

    enum Limits
    {
        DefaultPrecision =  3,
        MaxPrecision     = 15,
        MaxZeroPadding   = 20,
        MaxSpacePadding  = 20
    };
};

//overload the + operator for (string, CtiNumStr)
IM_EX_CTIBASE string operator +(const string & str, CtiNumStr & numStr);
IM_EX_CTIBASE string operator +(CtiNumStr & numStr, const string & str);

//overload the + operator for (char*, CtiNumStr)
IM_EX_CTIBASE string operator +(const char * s, CtiNumStr & numStr);
IM_EX_CTIBASE string operator +(CtiNumStr & numStr, const char * s);

IM_EX_CTIBASE string operator +(const string & str, const char* s);
IM_EX_CTIBASE string operator +(const char* s, const string& str);

#endif // #ifndef __NUMSTR_H__
