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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2003/03/13 19:35:27 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __NUMSTR_H__
#define __NUMSTR_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"

class IM_EX_CTIBASE CtiNumStr
{
    enum Format;

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
    Format _fmt;

    enum
    {
        Double,
        Float,
        Char,
        UChar,
        Short,
        UShort,
        Int,
        UInt,
        Long,
        ULong,
        Pointer
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
    operator char *( );

    void init( void );

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

    enum Format
    {
        Default,
        Hex,
        XHex,
        Exponential
    };
};
#endif // #ifndef __NUMSTR_H__
