#include "precompiled.h"

#include <ostream>
#include <sstream>
#include "math.h"
#include "numstr.h"

using namespace std;

CtiNumStr::CtiNumStr( double d, int precision ) : _dataType(DataType_FloatingPoint)  {  init(precision);  _float =  d;  }
CtiNumStr::CtiNumStr( float  f, int precision ) : _dataType(DataType_FloatingPoint)  {  init(precision);  _float =  f;  }

CtiNumStr::CtiNumStr( char  c ) : _dataType(DataType_Integer)  {  init( 8);  _integer = c;  }
CtiNumStr::CtiNumStr( short s ) : _dataType(DataType_Integer)  {  init(16);  _integer = s;  }
CtiNumStr::CtiNumStr( int   i ) : _dataType(DataType_Integer)  {  init(32);  _integer = i;  }
CtiNumStr::CtiNumStr( long  l ) : _dataType(DataType_Integer)  {  init(32);  _integer = l;  }

CtiNumStr::CtiNumStr( unsigned char  uc ) : _dataType(DataType_Unsigned)  {  init( 8);  _unsigned = uc;  }
CtiNumStr::CtiNumStr( unsigned short us ) : _dataType(DataType_Unsigned)  {  init(16);  _unsigned = us;  }
CtiNumStr::CtiNumStr( unsigned int   ui ) : _dataType(DataType_Unsigned)  {  init(32);  _unsigned = ui;  }
CtiNumStr::CtiNumStr( unsigned long  ul ) : _dataType(DataType_Unsigned)  {  init(32);  _unsigned = ul;  }

CtiNumStr::~CtiNumStr( )
{
}

//  change this to accept bits for the integer types, and digits of accuracy for the floating-point
//    actually, two seperate functions would be ideal
void CtiNumStr::init( unsigned int width )
{
    _fmt = Format_Default;

    _padding = -1;
    _zeroes  = false;

    _float     = 0;
    _integer   = 0;
    _unsigned  = 0;

    switch( _dataType )
    {
        case DataType_FloatingPoint:
        {
            _precision = min(width, (unsigned int)MaxPrecision);
            _bits = 0;

            break;
        }

        //  maybe we should complain if we try to assign a bitsize larger than 32 bits?
        default:
        {
            _precision = 0;
            _bits = min(width, (unsigned int)MaxBits);

            break;
        }
    }
}

string CtiNumStr::toString() const
{
    return string(*this);
}

CtiNumStr::operator string() const
{
    int padding   = _padding;
    int precision = _precision;

    ostringstream stream;

    if( _dataType == DataType_Integer || _dataType == DataType_Unsigned )
    {
        if( _fmt == Format_XHex )
        {
            //  special case for "0" - we have to output the "0x" ourselves
            if( !_integer && !_unsigned )
            {
                if( !_zeroes )
                {
                    padding -= 3;

                    while( padding-- > 0 )
                    {
                        stream << " ";
                    }
                }

                stream << "0x";
            }
            else
            {
                if( _zeroes )
                {
                    padding += 2;
                }

                stream.setf(ios::showbase);
            }
        }

        if( _fmt == Format_Hex || _fmt == Format_XHex )
        {
            stream.setf(ios::hex, ios::basefield);
        }
    }

    if( padding > 0 )
    {
        if( _dataType == DataType_FloatingPoint )
        {
            if( _fmt == Format_Exponential )
            {
                if( _zeroes && ((_padding - 6) > _precision)  )
                {
                    precision = _padding - 6;
                }
            }
            else
            {
                stream.setf((_zeroes)?(ios::internal):(ios::right), ios::adjustfield);
            }
        }
        else
        {
            stream.setf((_zeroes)?(ios::internal):(ios::right), ios::adjustfield);
        }

        if( _zeroes )
        {
            stream.fill('0');
        }

        stream.width(padding);
    }

    switch( _dataType )
    {
        case DataType_Integer:
        {
            if( _integer < 0 && _bits < 32 && stream.flags() & ios::hex )
            {
                stream << ((unsigned long)_integer & (~0UL >> (32 - _bits)));
            }
            else
            {
                stream << _integer;
            }

            break;
        }

        case DataType_Unsigned:
        {
            stream << _unsigned;
            break;
        }

        case DataType_FloatingPoint:
        {
            if( precision > 0 )
            {
                stream.precision(precision);

                if( _fmt == Format_Exponential )  stream.setf(ios::scientific);
                else                              stream.setf(ios::fixed);

                stream.setf(ios::showpoint);

                stream << _float;
            }
            else
            {
                if( _fmt == Format_Exponential )
                {
                    int exponent = 0;
                    if( _float != 0 )
                    {
                        exponent = floor(log10(_float));
                    }

                    stream << setw(padding - 5) << (int)((_float / pow((double)10, exponent)) + ((_float < 0)?(-0.5):(0.5)));

                    stream << setw(0) << "e" << showpos << internal <<  setw(4) << setfill('0') << exponent;
                }
                else
                {
                    stream << (int)_float;
                }
            }

            break;
        }
    }

    return stream.str();
}


CtiNumStr &CtiNumStr::hex( int pad )
{
    _fmt = Format_Hex;

    if( pad >= 0 )  zpad(pad);

    return *this;
}

CtiNumStr &CtiNumStr::xhex( int pad )
{
    _fmt = Format_XHex;

    if( pad >= 0 )  zpad(pad);

    return *this;
}

CtiNumStr &CtiNumStr::exp( void )
{
    _fmt = Format_Exponential;

    return *this;
}

CtiNumStr &CtiNumStr::spad( unsigned int pad )
{
    _padding = (pad > MaxSpacePadding)?(MaxSpacePadding):(pad);
    _zeroes  = false;

    return *this;
}

CtiNumStr &CtiNumStr::zpad( unsigned int pad )
{
    _padding = (pad > MaxSpacePadding)?(MaxSpacePadding):(pad);
    _zeroes  = true;

    return *this;
}


string operator +(const string &str, CtiNumStr &numStr)
{
    return str + (string)numStr;
}

string operator +(CtiNumStr &numStr, const string &str)
{
    return (string)numStr + str;
}


string operator +(const char *s, CtiNumStr &numStr)
{
    return s + (string)numStr;
}

string operator +(CtiNumStr &numStr, const char *s)
{
    return (string)numStr + s;
}

ostream &operator <<(ostream &o, CtiNumStr &numStr)
{
    return o << (string)numStr;
}

