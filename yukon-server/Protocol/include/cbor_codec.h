#pragma once

#include "dlldefs.h"

#include <cstdint>


// CBOR encoding/decoding - See RFC-7049 -- https://datatracker.ietf.org/doc/html/rfc7049

namespace Cti::Codec::Cbor
{

using Buffer = std::vector<unsigned char>;

IM_EX_PROT  Buffer cbor_encode_uint( const uint64_t x );

IM_EX_PROT  uint64_t cbor_decode_uint( const Buffer & x );


IM_EX_PROT  Buffer cbor_encode_sint( const int64_t x );

IM_EX_PROT  int64_t cbor_decode_sint( const Buffer & x );


IM_EX_PROT  Buffer cbor_encode_bstr( const Buffer & x );

IM_EX_PROT  Buffer cbor_decode_bstr( const Buffer & x );


IM_EX_PROT  Buffer cbor_encode_tstr( const std::string & x );

IM_EX_PROT  std::string cbor_decode_tstr( const Buffer & x );


}

