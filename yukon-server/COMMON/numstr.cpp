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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2007/03/09 21:31:12 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <stdio.h>  //  for _snprintf
#include "numstr.h"

CtiNumStr::CtiNumStr( double dVal, int precision )  {  _data.ul = 0;    _data.d   =  dVal;               _dataType = DataType_Double;  _precision = precision;  init();  }
CtiNumStr::CtiNumStr( float fVal,  int precision )  {  _data.ul = 0;    _data.f   =  fVal;               _dataType = DataType_Float;   _precision = precision;  init();  }

CtiNumStr::CtiNumStr( char cVal )                   {  _data.ul = 0;    _data.c   = (int)cVal;           _dataType = DataType_Char;       init();  }
CtiNumStr::CtiNumStr( unsigned char ucVal )         {  _data.ul = 0;    _data.uc  = (unsigned int)ucVal; _dataType = DataType_UChar;      init();  }

CtiNumStr::CtiNumStr( short sVal )                  {  _data.ul = 0;    _data.s   =  sVal;               _dataType = DataType_Short;      init();  }
CtiNumStr::CtiNumStr( unsigned short usVal )        {  _data.ul = 0;    _data.us  = usVal;               _dataType = DataType_UShort;     init();  }
CtiNumStr::CtiNumStr( int iVal )                    {  _data.ul = 0;    _data.i   =  iVal;               _dataType = DataType_Int;        init();  }
CtiNumStr::CtiNumStr( unsigned int uiVal )          {  _data.ul = 0;    _data.ui  = uiVal;               _dataType = DataType_UInt;       init();  }
CtiNumStr::CtiNumStr( long lVal )                   {  _data.ul = 0;    _data.l   =  lVal;               _dataType = DataType_Long;       init();  }
CtiNumStr::CtiNumStr( unsigned long ulVal )         {  _data.ul = 0;    _data.ul  = ulVal;               _dataType = DataType_ULong;      init();  }

CtiNumStr::CtiNumStr( void *vpVal )                 {  _data.ul = 0;    _data.vp  = vpVal;               _dataType = DataType_Pointer;    init();  }

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

string CtiNumStr::toString()
{
    switch( _dataType )
    {
        case DataType_Char:
        case DataType_UChar:
        case DataType_Short:
        case DataType_UShort:
        case DataType_Int:
        case DataType_UInt:
        case DataType_Long:
        case DataType_ULong:
            buildIntString( );
            break;

        case DataType_Float:
        case DataType_Double:
            buildFloatString( );
            break;

        case DataType_Pointer:
            buildPointerString( );
            break;

        default:
            _snprintf( _dataString, DataStringLength, "*** CtiNumStr Error ***" );
            _dataString[DataStringLength-1] = 0;
    }

    return string(_dataString);

}



CtiNumStr::operator string ( )
{
    switch( _dataType )
    {
        case DataType_Char:
        case DataType_UChar:
        case DataType_Short:
        case DataType_UShort:
        case DataType_Int:
        case DataType_UInt:
        case DataType_Long:
        case DataType_ULong:
            buildIntString( );
            break;

        case DataType_Float:
        case DataType_Double:
            buildFloatString( );
            break;

        case DataType_Pointer:
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
    if( _dataType == DataType_Float ||
        _dataType == DataType_Double )
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

    _fmt = Format_Default;

    _padding = 0;
    _zeroes  = 0;
}


CtiNumStr &CtiNumStr::hex( void )
{
    _fmt = Format_Hex;
    return *this;
}

CtiNumStr &CtiNumStr::xhex( void )
{
    _fmt = Format_XHex;
    return *this;
}

CtiNumStr &CtiNumStr::exp( void )
{
    _fmt = Format_Exponential;
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

    if( _fmt == Format_XHex )
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
        case Format_Hex:
        case Format_XHex:
        {
            if( _dataType == DataType_Short || _dataType == DataType_UShort ||
                _dataType == DataType_Char  || _dataType == DataType_UChar )
            {
                //  anything shorter than 4 bytes needs this (see printf format specification for details)
                fmtString[fmtChars++] = 'h';
            }

            fmtString[fmtChars++] = 'x';
            break;
        }

        case Format_Default:
        {
            switch( _dataType )
            {
                case DataType_ULong:
                    fmtString[fmtChars++] = 'l';
                    fmtString[fmtChars++] = 'u';
                    break;

                case DataType_Long:
                    fmtString[fmtChars++] = 'l';
                    fmtString[fmtChars++] = 'd';
                    break;

                case DataType_UChar:
                case DataType_UInt:
                    fmtString[fmtChars++] = 'u';
                    break;

                case DataType_Char:
                case DataType_Int:
                    fmtString[fmtChars++] = 'd';
                    break;

                case DataType_UShort:
                    fmtString[fmtChars++] = 'h';
                    fmtString[fmtChars++] = 'u';
                    break;

                case DataType_Short:
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
        case Format_Exponential:
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

    if( _dataType == DataType_Float )
    {
        if( _snprintf( _dataString, DataStringLength, fmtString, _padding, _precision, _data.f ) < 0 )
        {
            //  set the last character to "*" if we overflow
            _dataString[DataStringLength-2] = '*';
            _dataString[DataStringLength-1] = 0;
        }
    }
    else if( _dataType == DataType_Double )
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


string operator +(const string &str, CtiNumStr &numStr)
{
    return str + numStr.toString();
}

string operator +(CtiNumStr &numStr, const string &str)
{
    return numStr.toString() + str;
}


string operator +(const string & str, const char *s)
{
    return str + string(s);
}

string operator +(const char *s, const string &str)
{
    return string(s) + str;
}

string operator +(const char *s, CtiNumStr &numStr)
{
    return string(s) + numStr.toString();
}

string operator +(CtiNumStr &numStr, const char *s)
{
    return numStr.toString() + string(s);
}

