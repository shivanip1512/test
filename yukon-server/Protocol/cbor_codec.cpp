#include "precompiled.h"

#include "cbor_codec.h"

#include <boost/range/algorithm_ext.hpp>

//#include <cstring>
//#include <winsock2.h>   // hton{s,l,ll} and ntoh{s,l,ll} -- should incorporate...maybe
//#include <optional>

// Lots of work needed inthis stuff to make it much more robust... whats currently
//  here is just supporting the oscore stuff and has no error checking...

namespace Cti::Codec::Cbor
{

enum class Type
{
    UnsignedInt,
    SignedInt,
    ByteString,
    TextString,
    Array,
    Map,
    Tag,
    FloatingPoint
};

Buffer cbor_encode_uint( const uint64_t x )
{
    struct
    {
        int byte_count;
        int log2_byte_count;
    }
    converters = { 8, 3 };  // defaults to 64-bit

    if      ( x < 0x100 )       converters = { 1, 0 };
    else if ( x < 0x10000 )     converters = { 2, 1 };
    else if ( x < 0x100000000 ) converters = { 4, 2 };

    // we are little endian - find the high order byte - should probably find a hardware agnostic way to do this -- encoding is always in network byte order

    unsigned char * p = (unsigned char*)(&x) + converters.byte_count - 1;

    Buffer temp;

    if ( x >= 0x18 )
    {
        temp.push_back( 0x18 + converters.log2_byte_count );
    }

    for ( int i = 0 ; i < converters.byte_count; ++i )
    {
        temp.push_back( *p-- );
    }

    return temp;
}

uint64_t cbor_decode_uint( const Buffer & x )
{
    uint64_t value = 0;

    // look at the first byte - higher 3 bits should be 000,  lower bits the length

    if ( ( x[0] & 0xe0 ) != 0x00  )
    {
        // not a uint or formatting error...  throw ..?  <ignore for now>
    }

    int length = x[ 0 ] & 0x1f;

    if ( length < 0x18  )
    {
        value = x[0];
    }
    else if (  length > 0x1b )
    {
        // invalid length...  throw..? 
    }
    else
    {
        int byte_count = ( 1 << ( length - 0x18 ) );

        // should probably validate that we have enough bytes in the buffer before we do this...
        // bytes are in network byte order

        for ( int i = 1; i <= byte_count; ++i )
        {
            value = ( value << 8 ) | x[ i ];
        }
    }

    return value;
}

Buffer cbor_encode_sint( const int64_t x )
{
    if ( x >= 0 )
    {
        return cbor_encode_uint( x );
    }

    int64_t y = -1 - x;

    Buffer b = cbor_encode_uint( y );

    b[ 0 ] |= 0x20;     // mark as major type 1

    return b;
}

int64_t cbor_decode_sint( const Buffer & x )
{
    int64_t value = 0;

    // look at the first byte - higher 3 bits should be 001,  lower bits the length

    if ( ( x[0] & 0xe0 ) != 0x20  )
    {
        if ( ( x[0] & 0xe0 ) == 0x00  )
        {
            // we are a positive int...

            return cbor_decode_uint( x );
        }

        // not a int or formatting error...  throw ..?  <ignore for now>
    }

    int length = x[ 0 ] & 0x1f;

    if ( length < 0x18  )
    {
//        value = x[0];
        value = length;
    }
    else if (  length > 0x1b )
    {
        // invalid length...  throw..? 
    }
    else
    {
        int byte_count = ( 1 << ( length - 0x18 ) );

        // should probably validate that we have enough bytes in the buffer before we do this...
        // bytes are in network byte order

        for ( int i = 1; i <= byte_count; ++i )
        {
            value = ( value << 8 ) | x[ i ];
        }
    }

    return -1 - value;
}

Buffer cbor_encode_bstr( const Buffer & x )
{
    // encode the length of the bstr as a uint

    Buffer header = cbor_encode_uint( x.size() );

    // change major type from : 0 (uint) --> 2 (bstr)

    header[ 0 ] |= ( 0x02 << 5 );

    // append the byte data 

    boost::range::push_back( header, x );

    return header;
}

Buffer cbor_decode_bstr( const Buffer & x )
{
    Buffer value;

    // check the major type - should be 2

    if ( ( x[0] & 0xe0 ) != 0x20  )
    {
        // not a uint or formatting error...  throw ..?  <ignore for now>
    }

    // get the length...

    int length = x[ 0 ] & 0x1f;

    if ( length == 0 )
    {
        return {  };
    }

    if ( length < 0x18 )
    {
        return { x.begin() + 1, x.end() };        
    }
    else if (  length > 0x1b )
    {
        // invalid length...  throw..? 
    }
    else
    {
        int byte_count = ( 1 << ( length - 0x18 ) );

        // should probably validate that we have enough bytes in the buffer before we do this...
        // bytes are in network byte order

    //    uint64_t stream_length = 0;

     //   for ( int i = 1; i <= byte_count; ++i )
     //   {
     //       stream_length = ( stream_length << 8 ) | x[ i ];
     //   }
 //       value = { &x[ byte_count ], &x[ byte_count + stream_length ]  };

        return { x.begin() + 1 + byte_count, x.end() };        
    }

    return { }; // error case
}

Buffer cbor_encode_tstr( const std::string & x )
{
    // encode the length of the tstr as a uint

    Buffer header = cbor_encode_uint( x.size() );

    // change major type from : 0 (uint) --> 3 (tstr)

    header[ 0 ] |= ( 0x03 << 5 );

    // append the byte data 

    boost::range::push_back( header, x );

    return header;
}

std::string cbor_decode_tstr( const Buffer & x )
{
    // TODO


    return "";
}

/*
 
    A streaming encoder/decoder like the DatabaseReader/Writer would be interesting... 
 
*/

}

