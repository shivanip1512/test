/*-----------------------------------------------------------------------------*
*
* File:   numstr.cpp
*
* Date:   12/7/2001
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:45 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <stdio.h>  //  for _snprintf
#include "numstr.h"

CtiNumStr::CtiNumStr( double dVal, int precision )  {  _data.d = dVal;  _dataType = Double;  _precision = precision;  init( );  }
CtiNumStr::CtiNumStr( float fVal,  int precision )  {  _data.f = fVal;  _dataType = Float;   _precision = precision;  init( );  }

CtiNumStr::CtiNumStr( char cVal )             {  _data.c  = (int)cVal;              _dataType = Char;   init( );  }
CtiNumStr::CtiNumStr( unsigned char ucVal )   {  _data.uc = (unsigned int)ucVal;    _dataType = UChar;  init( );  }

CtiNumStr::CtiNumStr( short sVal )            {  _data.s   =  sVal;  _dataType = Short;  init( );  }
CtiNumStr::CtiNumStr( unsigned short usVal )  {  _data.us  = usVal;  _dataType = UShort; init( );  }
CtiNumStr::CtiNumStr( int iVal )              {  _data.i   =  iVal;  _dataType = Int;    init( );  }
CtiNumStr::CtiNumStr( unsigned int uiVal )    {  _data.ui  = uiVal;  _dataType = UInt;   init( );  }
CtiNumStr::CtiNumStr( long lVal )             {  _data.l   =  lVal;  _dataType = Long;   init( );  }
CtiNumStr::CtiNumStr( unsigned long ulVal )   {  _data.ul  = ulVal;  _dataType = ULong;  init( );  }

CtiNumStr::CtiNumStr( void *vpVal )  {  _data.vp  = vpVal;  _dataType = Pointer;  init( );  }

CtiNumStr::CtiNumStr( const CtiNumStr &aRef )
{
    *this = aRef;
}

CtiNumStr::~CtiNumStr( )
{

}


CtiNumStr &CtiNumStr::operator=( const CtiNumStr &aRef )
{
    if( this != &aRef )
    {
        this->_data = aRef._data;
    }

    return *this;
}

CtiNumStr::operator char *( )
{
    switch( _dataType )
    {
        case Char:
        case UChar:
        case Short:
        case UShort:
        case Int:
        case UInt:
        case Long:
        case ULong:
            buildIntString( );
            break;

        case Float:
        case Double:
            buildFloatString( );
            break;

        case Pointer:
            buildPointerString( );
            break;

        default:
            _snprintf( _dataString, DataStringLength, "*** CtiNumStr Error ***" );
            _dataString[DataStringLength-1] = 0;
    }

    return _dataString;
}


void CtiNumStr::init( void )
{
    if( _dataType == Float || _dataType == Double )
    {
        if( _precision > MaxPrecision )
        {
            _precision = MaxPrecision;
        }
    }
    else
    {
        _precision = 0;
    }

    _fmt = Default;

    _padding = 0;
    _zeroes  = 0;
}


CtiNumStr &CtiNumStr::hex( void )
{
    _fmt = Hex;
    return *this;
}

CtiNumStr &CtiNumStr::xhex( void )
{
    _fmt = XHex;
    return *this;
}

CtiNumStr &CtiNumStr::exp( void )
{
    _fmt = Exponential;
    return *this;
}


CtiNumStr &CtiNumStr::spad( unsigned int pad )
{
    if( pad > MaxSpacePadding )
    {
        pad = MaxSpacePadding;
    }

    _padding = pad;
    _zeroes  = 0;

    return *this;
}

CtiNumStr &CtiNumStr::zpad( unsigned int pad )
{
    if( pad > MaxZeroPadding )
    {
        pad = MaxZeroPadding;
    }

    _padding = pad;
    _zeroes  = 1;

    return *this;
}


void CtiNumStr::buildIntString( void )
{
    char fmtString[10];
    int  fmtChars = 0,
         overflow;

    if( _fmt == XHex )
    {
        fmtString[fmtChars++] = '0';
        fmtString[fmtChars++] = 'x';
    }

    fmtString[fmtChars++] = '%';

    if( _zeroes )
    {
        fmtString[fmtChars++] = '0';
    }

    fmtString[fmtChars++] = '*';

    switch( _fmt )
    {
        case Hex:
        case XHex:
        {
            fmtString[fmtChars++] = 'x';
            break;
        }

        case Default:
        {
            switch( _dataType )
            {
                case ULong:
                    fmtString[fmtChars++] = 'l';
                    fmtString[fmtChars++] = 'u';
                    break;

                case Long:
                    fmtString[fmtChars++] = 'l';
                    fmtString[fmtChars++] = 'd';
                    break;

                case UChar:
                case UInt:
                    fmtString[fmtChars++] = 'u';
                    break;

                case Char:
                case Int:
                    fmtString[fmtChars++] = 'd';
                    break;

                case UShort:
                    fmtString[fmtChars++] = 'h';
                    fmtString[fmtChars++] = 'u';
                    break;

                case Short:
                    fmtString[fmtChars++] = 'h';
                    fmtString[fmtChars++] = 'd';
                    break;
            }

            break;
        }
    }

    fmtString[fmtChars] = 0;

    //  _data.l encapsulates all integer types, as it'll copy sizeof(long) length
    //    of data, and _snprintf will only look at what it needs.  Thank You Little Endian!
    if( _snprintf( _dataString, DataStringLength, fmtString, _padding, _data.l ) < 0 )
    {
        //  set the last character to "*" if we overflow
        _dataString[DataStringLength-2] = '*';
        _dataString[DataStringLength-1] = 0;
    }
}

void CtiNumStr::buildFloatString( void )
{
    char fmtString[10];
    int  fmtChars = 0;

    fmtString[fmtChars++] = '%';

    if( _zeroes )
    {
        fmtString[fmtChars++] = '0';
    }

    fmtString[fmtChars++] = '*';
    fmtString[fmtChars++] = '.';
    fmtString[fmtChars++] = '*';

    switch( _fmt )
    {
        case Exponential:
        {
            fmtString[fmtChars++] = 'g';
            break;
        }

        default:
        {
            fmtString[fmtChars++] = 'f';
            break;
        }
    }

    fmtString[fmtChars] = 0;

    if( _dataType == Float )
    {
        if( _snprintf( _dataString, DataStringLength, fmtString, _padding, _precision, _data.f ) < 0 )
        {
            //  set the last character to "*" if we overflow
            _dataString[DataStringLength-2] = '*';
            _dataString[DataStringLength-1] = 0;
        }
    }
    else if( _dataType == Double )
    {
        if( _snprintf( _dataString, DataStringLength, fmtString, _padding, _precision, _data.d ) < 0 )
        {
            //  set the last character to "*" if we overflow
            _dataString[DataStringLength-2] = '*';
            _dataString[DataStringLength-1] = 0;
        }
    }
}

void CtiNumStr::buildPointerString( void )
{
    char fmtString[10];
    int  fmtChars = 0;

    fmtString[fmtChars++] = '%';
    fmtString[fmtChars++] = '*';

    fmtString[fmtChars++] = 'p';
    fmtString[fmtChars]   = 0;

    if( _snprintf( _dataString, DataStringLength, fmtString, _padding, _data.vp ) < 0 )
    {
        //  set the last character to "*" if we overflow
        _dataString[DataStringLength-2] = '*';
        _dataString[DataStringLength-1] = 0;
    }
}
