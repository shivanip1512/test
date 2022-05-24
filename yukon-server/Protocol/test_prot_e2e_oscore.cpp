#include <boost/test/unit_test.hpp>

#include "prot_e2eDataTransfer.h"
#include "e2e_exceptions.h"

#include "boost_test_helpers.h"


#include <openssl/aes.h>
#include <openssl/evp.h>
#include <openssl/hmac.h>
#include <openssl/err.h>

//#include "encryption.h"

#include <cstdint>
#include <cstring>
#include <boost/range/algorithm_ext.hpp>
#include <winsock2.h>   // hton{s,l,ll} and ntoh{s,l,ll} -- should incorporate...maybe
#include <optional>

#include "cbor_codec.h"

using namespace Cti::Codec::Cbor;


using Cti::Test::byte_str;

BOOST_AUTO_TEST_SUITE( test_prot_e2e_oscore )

const Cti::RfnIdentifier endpointId { "FOO", "BAR", "BAZ" };

struct test_E2eDataTransferProtocol : Cti::Protocols::E2eDataTransferProtocol
{
    unsigned short id;

    unsigned short getOutboundId() override
    {
        return id++;
    }
};


Buffer generate_prk(const Buffer& salt, const Buffer& secret)
{
    unsigned char   prk[48] = {};
    unsigned int    prk_length = 0;

    Buffer theSalt = salt;

    if ( theSalt.empty() )
    {
        theSalt = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    }

    HMAC( EVP_sha256(),
          &theSalt[0],  theSalt.size(),
          &secret[0],   secret.size(),
          prk,         &prk_length );

    return { prk, prk + prk_length };
}

/*
CDDL for the 'info' structure 
 
info = 
[ 
    id:  sender / recipient id as bstr
    id_context : bstr / nil
    alg_aead : int / tstr
    type : tstr
    L : uint
 ]
*/

struct Info
{
    Buffer      id;
    Buffer     *id_context;
    int         alg_aead;       // also could be a string but we don;t need that (yet)...
    std::string type;
    unsigned    L;
};


Buffer cbor_info_encode( const Info & info )
{
    Buffer cbor_data;

    // 5 element array

    cbor_data.push_back( 0x85 );    // TO DO -- array encoding...

    // element 1 - bstr

    boost::range::push_back( cbor_data, cbor_encode_bstr( info.id ) );

    // element 2 - bstr or nil

    if ( ! info.id_context )
    {
        cbor_data.push_back( 0xf6 );    // nil -- special value
    }
    else
    {
        boost::range::push_back( cbor_data, cbor_encode_bstr( *info.id_context ) );
    }

    // element 3 - int -- ignoring tstr representation completely

    boost::range::push_back( cbor_data, cbor_encode_uint( info.alg_aead ) );

    // element 4  - tstr

    boost::range::push_back( cbor_data, cbor_encode_tstr( info.type ) );

    // element 5 - int -- size of the key in bytes

    boost::range::push_back( cbor_data, cbor_encode_uint( info.L ) );

    return cbor_data;
}

Buffer generate_key( const Buffer & prk, const Info & info )
{
    unsigned char   key[48] = {};
    unsigned int    key_length = 0;

    Buffer key_info = cbor_info_encode( info );

    // Normally if info.L is bigger than prk.length then we need to loop and append stuff, but ours isn't so i'll ignore for now

    key_info.push_back( 0x01 );

    HMAC( EVP_sha256(),
          &prk[0],      prk.size(),
          &key_info[0], key_info.size(),
          key,          &key_length );

    return { key, key + info.L };
}

Buffer generate_nonce( const Buffer & id, const Buffer & partial_iv, const Buffer & common_iv)
{
    // hardcoded to our user case of 13 byte length nonce (same as the common_iv)

    Buffer nonce;

    // first byte is the length of the id

    nonce.push_back( id.size() );

    // then we have the id left zero padded to 13 - 6 - id.size bytes

    for ( int i = 0; i < 7 - id.size(); ++i )
    {
        nonce.push_back( 0x00 );
    }

    // now append the id bytes

    nonce.insert( nonce.end(), id.begin(), id.end()  );

    // now pad the partial_iv to 5 bytes

    for ( int i = 0; i < 5 - partial_iv.size(); ++i )
    {
        nonce.push_back( 0x00 );
    }

    // now append the partial_iv bytes

    nonce.insert( nonce.end(), partial_iv.begin(), partial_iv.end()  );

    // now xor the nonce with the common_iv

    for ( int i = 0; i < common_iv.size(); ++i  )
    {
        nonce[ i ] ^= common_iv[ i ];
    }

    return nonce;
}

BOOST_AUTO_TEST_CASE( test_oscore_key_generation_C_1_1 )
{
/*
    https://datatracker.ietf.org/doc/html/rfc8613.html#appendix-C.1.1
 
  Inputs:

   o  Master Secret: 0x0102030405060708090a0b0c0d0e0f10 (16 bytes)
   o  Master Salt: 0x9e7ca92223786340 (8 bytes)
   o  Sender ID: 0x (0 byte)
   o  Recipient ID: 0x01 (1 byte)

  From the previous parameters,

   o  info (for Sender Key): 0x8540f60a634b657910 (9 bytes)
   o  info (for Recipient Key): 0x854101f60a634b657910 (10 bytes)
   o  info (for Common IV): 0x8540f60a6249560d (8 bytes)

  Outputs:

   o  Sender Key: 0xf0910ed7295e6ad4b54fc793154302ff (16 bytes)
   o  Recipient Key: 0xffb14e093c94c9cac9471648b4f98710 (16 bytes)
   o  Common IV: 0x4622d4dd6d944168eefb54987c (13 bytes)

  From the previous parameters and a Partial IV equal to 0 (both for sender and recipient):

   o  sender nonce: 0x4622d4dd6d944168eefb54987c (13 bytes)
   o  recipient nonce: 0x4722d4dd6d944169eefb54987c (13 bytes)
*/

    // Inputs:

    Buffer
        secret          { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 },
        salt            { 0x9e, 0x7c, 0xa9, 0x22, 0x23, 0x78, 0x63, 0x40 },
        sender_id       {   },
        recipient_id    { 0x01 };

    // Generate the psuedo-random key (32-byte)

    Buffer
        prk = generate_prk( salt, secret );
    
    // Verify the info encodings

    Buffer
        expected_sender_info    { 0x85, 0x40, 0xf6, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_recipient_info { 0x85, 0x41, 0x01, 0xf6, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_common_iv_info { 0x85, 0x40, 0xf6, 0x0a, 0x62, 0x49, 0x56, 0x0d };

    // Sender info
    
    Buffer send_info = cbor_info_encode(
        {
            sender_id,
            nullptr,    // no ID Context
            10,         // AES-CCM-16-64-128
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( send_info, expected_sender_info );

    // Recipient info

    Buffer recipient_info = cbor_info_encode(
        {
            recipient_id,
            nullptr,
            10,
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( recipient_info, expected_recipient_info );

    // Common-IV info

    Buffer common_iv_info = cbor_info_encode(
        {
            { },    // empty bstr for Common-IV
            nullptr,
            10,
            "IV",
            13
        } );

    BOOST_CHECK_EQUAL_RANGES( common_iv_info, expected_common_iv_info );

    // Outputs

    Buffer
        expected_sender_key     { 0xf0, 0x91, 0x0e, 0xd7, 0x29, 0x5e, 0x6a, 0xd4, 0xb5, 0x4f, 0xc7, 0x93, 0x15, 0x43, 0x02, 0xff },
        expected_recipient_key  { 0xff, 0xb1, 0x4e, 0x09, 0x3c, 0x94, 0xc9, 0xca, 0xc9, 0x47, 0x16, 0x48, 0xb4, 0xf9, 0x87, 0x10 },
        expected_common_iv      { 0x46, 0x22, 0xd4, 0xdd, 0x6d, 0x94, 0x41, 0x68, 0xee, 0xfb, 0x54, 0x98, 0x7c };

    // Sender key

    Buffer sender_key = generate_key(
       prk,
       {
            sender_id,
            nullptr,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( sender_key, expected_sender_key );

    // Recipient key

    Buffer recipient_key = generate_key(
       prk,
       {
            recipient_id,
            nullptr,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( recipient_key, expected_recipient_key );

    // Common-IV

    Buffer common_iv = generate_key(
       prk,
       {
            { },
            nullptr,
            10,
            "IV",
            13
       } );

    BOOST_CHECK_EQUAL_RANGES( common_iv, expected_common_iv );

    // Nonces

    Buffer
        expected_sender_nonce       { 0x46, 0x22, 0xd4, 0xdd, 0x6d, 0x94, 0x41, 0x68, 0xee, 0xfb, 0x54, 0x98, 0x7c },
        expected_recipient_nonce    { 0x47, 0x22, 0xd4, 0xdd, 0x6d, 0x94, 0x41, 0x69, 0xee, 0xfb, 0x54, 0x98, 0x7c };

    // Sender nonce

    Buffer sender_nonce = generate_nonce(
       sender_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( sender_nonce, expected_sender_nonce );

    // Recipient nonce

    Buffer recipient_nonce = generate_nonce(
       recipient_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( recipient_nonce, expected_recipient_nonce );
}

BOOST_AUTO_TEST_CASE( test_oscore_key_generation_C_1_2 )
{
/*
    https://datatracker.ietf.org/doc/html/rfc8613.html#appendix-C.1.2
 
  Inputs:

   o  Master Secret: 0x0102030405060708090a0b0c0d0e0f10 (16 bytes)

   o  Master Salt: 0x9e7ca92223786340 (8 bytes)

   o  Sender ID: 0x01 (1 byte)

   o  Recipient ID: 0x (0 byte)

  From the previous parameters,

   o  info (for Sender Key): 0x854101f60a634b657910 (10 bytes)

   o  info (for Recipient Key): 0x8540f60a634b657910 (9 bytes)

   o  info (for Common IV): 0x8540f60a6249560d (8 bytes)

  Outputs:

   o  Sender Key: 0xffb14e093c94c9cac9471648b4f98710 (16 bytes)

   o  Recipient Key: 0xf0910ed7295e6ad4b54fc793154302ff (16 bytes)

   o  Common IV: 0x4622d4dd6d944168eefb54987c (13 bytes)

  From the previous parameters and a Partial IV equal to 0 (both for
   sender and recipient):

   o  sender nonce: 0x4722d4dd6d944169eefb54987c (13 bytes)

   o  recipient nonce: 0x4622d4dd6d944168eefb54987c (13 bytes)
*/

    // Inputs:

    Buffer
        secret          { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 },
        salt            { 0x9e, 0x7c, 0xa9, 0x22, 0x23, 0x78, 0x63, 0x40 },
        sender_id       { 0x01 },
        recipient_id    {  };

    // Generate the psuedo-random key (32-byte)

    Buffer
        prk = generate_prk( salt, secret );

    // Verify the info encodings

    Buffer
        expected_sender_info    { 0x85, 0x41, 0x01, 0xf6, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_recipient_info { 0x85, 0x40, 0xf6, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_common_iv_info { 0x85, 0x40, 0xf6, 0x0a, 0x62, 0x49, 0x56, 0x0d };

    // Sender info

    Buffer send_info = cbor_info_encode(
        {
            sender_id,
            nullptr,    // no ID Context
            10,         // AES-CCM-16-64-128
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( send_info, expected_sender_info );

    // Recipient info

    Buffer recipient_info = cbor_info_encode(
        {
            recipient_id,
            nullptr,
            10,
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( recipient_info, expected_recipient_info );

    // Common-IV info

    Buffer common_iv_info = cbor_info_encode(
        {
            { },    // empty bstr for Common-IV
            nullptr,
            10,
            "IV",
            13
        } );

    BOOST_CHECK_EQUAL_RANGES( common_iv_info, expected_common_iv_info );

    // Outputs

    Buffer
        expected_sender_key     { 0xff, 0xb1, 0x4e, 0x09, 0x3c, 0x94, 0xc9, 0xca, 0xc9, 0x47, 0x16, 0x48, 0xb4, 0xf9, 0x87, 0x10 },
        expected_recipient_key  { 0xf0, 0x91, 0x0e, 0xd7, 0x29, 0x5e, 0x6a, 0xd4, 0xb5, 0x4f, 0xc7, 0x93, 0x15, 0x43, 0x02, 0xff },
        expected_common_iv      { 0x46, 0x22, 0xd4, 0xdd, 0x6d, 0x94, 0x41, 0x68, 0xee, 0xfb, 0x54, 0x98, 0x7c };

    // Sender key

    Buffer sender_key = generate_key(
       prk,
       {
            sender_id,
            nullptr,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( sender_key, expected_sender_key );

    // Recipient key

    Buffer recipient_key = generate_key(
       prk,
       {
            recipient_id,
            nullptr,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( recipient_key, expected_recipient_key );

    // Common-IV

    Buffer common_iv = generate_key(
       prk,
       {
            { },
            nullptr,
            10,
            "IV",
            13
       } );

    BOOST_CHECK_EQUAL_RANGES( common_iv, expected_common_iv );

    // Nonces

    Buffer
        expected_sender_nonce       { 0x47, 0x22, 0xd4, 0xdd, 0x6d, 0x94, 0x41, 0x69, 0xee, 0xfb, 0x54, 0x98, 0x7c },
        expected_recipient_nonce    { 0x46, 0x22, 0xd4, 0xdd, 0x6d, 0x94, 0x41, 0x68, 0xee, 0xfb, 0x54, 0x98, 0x7c };

    // Sender nonce

    Buffer sender_nonce = generate_nonce(
       sender_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( sender_nonce, expected_sender_nonce );

    // Recipient nonce

    Buffer recipient_nonce = generate_nonce(
       recipient_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( recipient_nonce, expected_recipient_nonce );
}

BOOST_AUTO_TEST_CASE( test_oscore_key_generation_C_2_1 )
{
/*
    https://datatracker.ietf.org/doc/html/rfc8613.html#appendix-C.2.1 
 
  Inputs:

   o  Master Secret: 0x0102030405060708090a0b0c0d0e0f10 (16 bytes)

   o  Sender ID: 0x00 (1 byte)

   o  Recipient ID: 0x01 (1 byte)

  From the previous parameters,

   o  info (for Sender Key): 0x854100f60a634b657910 (10 bytes)

   o  info (for Recipient Key): 0x854101f60a634b657910 (10 bytes)

   o  info (for Common IV): 0x8540f60a6249560d (8 bytes)

  Outputs:

   o  Sender Key: 0x321b26943253c7ffb6003b0b64d74041 (16 bytes)

   o  Recipient Key: 0xe57b5635815177cd679ab4bcec9d7dda (16 bytes)

   o  Common IV: 0xbe35ae297d2dace910c52e99f9 (13 bytes)

  From the previous parameters and a Partial IV equal to 0 (both for
   sender and recipient):

   o  sender nonce: 0xbf35ae297d2dace910c52e99f9 (13 bytes)

   o  recipient nonce: 0xbf35ae297d2dace810c52e99f9 (13 bytes)
*/

    // Inputs:

    Buffer
        secret          { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 },
        sender_id       { 0x00 },
        recipient_id    { 0x01 };

    // Generate the psuedo-random key (32-byte)

    Buffer
        prk = generate_prk( {  }, secret );

    // Verify the info encodings

    Buffer
        expected_sender_info    { 0x85, 0x41, 0x00, 0xf6, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_recipient_info { 0x85, 0x41, 0x01, 0xf6, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_common_iv_info { 0x85, 0x40, 0xf6, 0x0a, 0x62, 0x49, 0x56, 0x0d };

    // Sender info

    Buffer send_info = cbor_info_encode(
        {
            sender_id,
            nullptr,    // no ID Context
            10,         // AES-CCM-16-64-128
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( send_info, expected_sender_info );

    // Recipient info

    Buffer recipient_info = cbor_info_encode(
        {
            recipient_id,
            nullptr,
            10,
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( recipient_info, expected_recipient_info );

    // Common-IV info

    Buffer common_iv_info = cbor_info_encode(
        {
            { },    // empty bstr for Common-IV
            nullptr,
            10,
            "IV",
            13
        } );

    BOOST_CHECK_EQUAL_RANGES( common_iv_info, expected_common_iv_info );

    // Outputs

    Buffer
        expected_sender_key     { 0x32, 0x1b, 0x26, 0x94, 0x32, 0x53, 0xc7, 0xff, 0xb6, 0x00, 0x3b, 0x0b, 0x64, 0xd7, 0x40, 0x41 },
        expected_recipient_key  { 0xe5, 0x7b, 0x56, 0x35, 0x81, 0x51, 0x77, 0xcd, 0x67, 0x9a, 0xb4, 0xbc, 0xec, 0x9d, 0x7d, 0xda },
        expected_common_iv      { 0xbe, 0x35, 0xae, 0x29, 0x7d, 0x2d, 0xac, 0xe9, 0x10, 0xc5, 0x2e, 0x99, 0xf9 };

    // Sender key

    Buffer sender_key = generate_key(
       prk,
       {
            sender_id,
            nullptr,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( sender_key, expected_sender_key );

    // Recipient key

    Buffer recipient_key = generate_key(
       prk,
       {
            recipient_id,
            nullptr,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( recipient_key, expected_recipient_key );

    // Common-IV

    Buffer common_iv = generate_key(
       prk,
       {
            { },
            nullptr,
            10,
            "IV",
            13
       } );

    BOOST_CHECK_EQUAL_RANGES( common_iv, expected_common_iv );

    // Nonces

    Buffer
        expected_sender_nonce       { 0xbf, 0x35, 0xae, 0x29, 0x7d, 0x2d, 0xac, 0xe9, 0x10, 0xc5, 0x2e, 0x99, 0xf9 },
        expected_recipient_nonce    { 0xbf, 0x35, 0xae, 0x29, 0x7d, 0x2d, 0xac, 0xe8, 0x10, 0xc5, 0x2e, 0x99, 0xf9 };

    // Sender nonce

    Buffer sender_nonce = generate_nonce(
       sender_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( sender_nonce, expected_sender_nonce );

    // Recipient nonce

    Buffer recipient_nonce = generate_nonce(
       recipient_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( recipient_nonce, expected_recipient_nonce );
}

BOOST_AUTO_TEST_CASE( test_oscore_key_generation_C_2_2 )
{
/*
    https://datatracker.ietf.org/doc/html/rfc8613.html#appendix-C.2.2
 
  Inputs:

   o  Master Secret: 0x0102030405060708090a0b0c0d0e0f10 (16 bytes)

   o  Sender ID: 0x01 (1 byte)

   o  Recipient ID: 0x00 (1 byte)

  From the previous parameters,

   o  info (for Sender Key): 0x854101f60a634b657910 (10 bytes)

   o  info (for Recipient Key): 0x854100f60a634b657910 (10 bytes)

   o  info (for Common IV): 0x8540f60a6249560d (8 bytes)

  Outputs:

   o  Sender Key: 0xe57b5635815177cd679ab4bcec9d7dda (16 bytes)

   o  Recipient Key: 0x321b26943253c7ffb6003b0b64d74041 (16 bytes)

   o  Common IV: 0xbe35ae297d2dace910c52e99f9 (13 bytes)

  From the previous parameters and a Partial IV equal to 0 (both for
   sender and recipient):

   o  sender nonce: 0xbf35ae297d2dace810c52e99f9 (13 bytes)

   o  recipient nonce: 0xbf35ae297d2dace910c52e99f9 (13 bytes)
*/

    // Inputs:

    Buffer
        secret          { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 },
        sender_id       { 0x01 },
        recipient_id    { 0x00 };

    // Generate the psuedo-random key (32-byte)

    Buffer
        prk = generate_prk( {  }, secret );

    // Verify the info encodings

    Buffer
        expected_sender_info    { 0x85, 0x41, 0x01, 0xf6, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_recipient_info { 0x85, 0x41, 0x00, 0xf6, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_common_iv_info { 0x85, 0x40, 0xf6, 0x0a, 0x62, 0x49, 0x56, 0x0d };

    // Sender info

    Buffer send_info = cbor_info_encode(
        {
            sender_id,
            nullptr,    // no ID Context
            10,         // AES-CCM-16-64-128
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( send_info, expected_sender_info );

    // Recipient info

    Buffer recipient_info = cbor_info_encode(
        {
            recipient_id,
            nullptr,
            10,
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( recipient_info, expected_recipient_info );

    // Common-IV info

    Buffer common_iv_info = cbor_info_encode(
        {
            { },    // empty bstr for Common-IV
            nullptr,
            10,
            "IV",
            13
        } );

    BOOST_CHECK_EQUAL_RANGES( common_iv_info, expected_common_iv_info );

    // Outputs

    Buffer
        expected_sender_key     { 0xe5, 0x7b, 0x56, 0x35, 0x81, 0x51, 0x77, 0xcd, 0x67, 0x9a, 0xb4, 0xbc, 0xec, 0x9d, 0x7d, 0xda },
        expected_recipient_key  { 0x32, 0x1b, 0x26, 0x94, 0x32, 0x53, 0xc7, 0xff, 0xb6, 0x00, 0x3b, 0x0b, 0x64, 0xd7, 0x40, 0x41 },
        expected_common_iv      { 0xbe, 0x35, 0xae, 0x29, 0x7d, 0x2d, 0xac, 0xe9, 0x10, 0xc5, 0x2e, 0x99, 0xf9 };

    // Sender key

    Buffer sender_key = generate_key(
       prk,
       {
            sender_id,
            nullptr,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( sender_key, expected_sender_key );

    // Recipient key

    Buffer recipient_key = generate_key(
       prk,
       {
            recipient_id,
            nullptr,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( recipient_key, expected_recipient_key );

    // Common-IV

    Buffer common_iv = generate_key(
       prk,
       {
            { },
            nullptr,
            10,
            "IV",
            13
       } );

    BOOST_CHECK_EQUAL_RANGES( common_iv, expected_common_iv );

    // Nonces

    Buffer
        expected_sender_nonce       { 0xbf, 0x35, 0xae, 0x29, 0x7d, 0x2d, 0xac, 0xe8, 0x10, 0xc5, 0x2e, 0x99, 0xf9 },
        expected_recipient_nonce    { 0xbf, 0x35, 0xae, 0x29, 0x7d, 0x2d, 0xac, 0xe9, 0x10, 0xc5, 0x2e, 0x99, 0xf9 };

    // Sender nonce

    Buffer sender_nonce = generate_nonce(
       sender_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( sender_nonce, expected_sender_nonce );

    // Recipient nonce

    Buffer recipient_nonce = generate_nonce(
       recipient_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( recipient_nonce, expected_recipient_nonce );
}

BOOST_AUTO_TEST_CASE( test_oscore_key_generation_C_3_1 )
{
/*
    https://datatracker.ietf.org/doc/html/rfc8613.html#appendix-C.3.1
 
  Inputs:

   o  Master Secret: 0x0102030405060708090a0b0c0d0e0f10 (16 bytes)

   o  Master Salt: 0x9e7ca92223786340 (8 bytes)

   o  Sender ID: 0x (0 byte)

   o  Recipient ID: 0x01 (1 byte)

   o  ID Context: 0x37cbf3210017a2d3 (8 bytes)

  From the previous parameters,

   o  info (for Sender Key): 0x85404837cbf3210017a2d30a634b657910 (17
      bytes)

   o  info (for Recipient Key): 0x8541014837cbf3210017a2d30a634b657910
      (18 bytes)

   o  info (for Common IV): 0x85404837cbf3210017a2d30a6249560d (16
      bytes)

  Outputs:

   o  Sender Key: 0xaf2a1300a5e95788b356336eeecd2b92 (16 bytes)

   o  Recipient Key: 0xe39a0c7c77b43f03b4b39ab9a268699f (16 bytes)

   o  Common IV: 0x2ca58fb85ff1b81c0b7181b85e (13 bytes)

  From the previous parameters and a Partial IV equal to 0 (both for
   sender and recipient):

   o  sender nonce: 0x2ca58fb85ff1b81c0b7181b85e (13 bytes)

   o  recipient nonce: 0x2da58fb85ff1b81d0b7181b85e (13 bytes)
*/

    // Inputs:

    Buffer
        secret          { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 },
        salt            { 0x9e, 0x7c, 0xa9, 0x22, 0x23, 0x78, 0x63, 0x40 },
        sender_id       {  },
        recipient_id    { 0x01 },
        id_context      { 0x37, 0xcb, 0xf3, 0x21, 0x00, 0x17, 0xa2, 0xd3 };

    // Generate the psuedo-random key (32-byte)

    Buffer
        prk = generate_prk( salt, secret );

    // Verify the info encodings

    Buffer
        expected_sender_info    { 0x85, 0x40, 0x48, 0x37, 0xcb, 0xf3, 0x21, 0x00, 0x17, 0xa2, 0xd3, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_recipient_info { 0x85, 0x41, 0x01, 0x48, 0x37, 0xcb, 0xf3, 0x21, 0x00, 0x17, 0xa2, 0xd3, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_common_iv_info { 0x85, 0x40, 0x48, 0x37, 0xcb, 0xf3, 0x21, 0x00, 0x17, 0xa2, 0xd3, 0x0a, 0x62, 0x49, 0x56, 0x0d };

    // Sender info

    Buffer send_info = cbor_info_encode(
        {
            sender_id,
           &id_context, // ID Context
            10,         // AES-CCM-16-64-128
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( send_info, expected_sender_info );

    // Recipient info

    Buffer recipient_info = cbor_info_encode(
        {
            recipient_id,
           &id_context,
            10,
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( recipient_info, expected_recipient_info );

    // Common-IV info

    Buffer common_iv_info = cbor_info_encode(
        {
            { },    // empty bstr for Common-IV
           &id_context,
            10,
            "IV",
            13
        } );

    BOOST_CHECK_EQUAL_RANGES( common_iv_info, expected_common_iv_info );

    // Outputs

    Buffer
        expected_sender_key     { 0xaf, 0x2a, 0x13, 0x00, 0xa5, 0xe9, 0x57, 0x88, 0xb3, 0x56, 0x33, 0x6e, 0xee, 0xcd, 0x2b, 0x92 },
        expected_recipient_key  { 0xe3, 0x9a, 0x0c, 0x7c, 0x77, 0xb4, 0x3f, 0x03, 0xb4, 0xb3, 0x9a, 0xb9, 0xa2, 0x68, 0x69, 0x9f },
        expected_common_iv      { 0x2c, 0xa5, 0x8f, 0xb8, 0x5f, 0xf1, 0xb8, 0x1c, 0x0b, 0x71, 0x81, 0xb8, 0x5e };

    // Sender key

    Buffer sender_key = generate_key(
       prk,
       {
            sender_id,
           &id_context,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( sender_key, expected_sender_key );

    // Recipient key

    Buffer recipient_key = generate_key(
       prk,
       {
            recipient_id,
           &id_context,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( recipient_key, expected_recipient_key );

    // Common-IV

    Buffer common_iv = generate_key(
       prk,
       {
            { },
           &id_context,
            10,
            "IV",
            13
       } );

    BOOST_CHECK_EQUAL_RANGES( common_iv, expected_common_iv );

    // Nonces

    Buffer
        expected_sender_nonce       { 0x2c, 0xa5, 0x8f, 0xb8, 0x5f, 0xf1, 0xb8, 0x1c, 0x0b, 0x71, 0x81, 0xb8, 0x5e },
        expected_recipient_nonce    { 0x2d, 0xa5, 0x8f, 0xb8, 0x5f, 0xf1, 0xb8, 0x1d, 0x0b, 0x71, 0x81, 0xb8, 0x5e };

    // Sender nonce

    Buffer sender_nonce = generate_nonce(
       sender_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( sender_nonce, expected_sender_nonce );

    // Recipient nonce

    Buffer recipient_nonce = generate_nonce(
       recipient_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( recipient_nonce, expected_recipient_nonce );
}

BOOST_AUTO_TEST_CASE( test_oscore_key_generation_C_3_2 )
{
/*
    https://datatracker.ietf.org/doc/html/rfc8613.html#appendix-C.3.2
 
  Inputs:

   o  Master Secret: 0x0102030405060708090a0b0c0d0e0f10 (16 bytes)

   o  Master Salt: 0x9e7ca92223786340 (8 bytes)

   o  Sender ID: 0x01 (1 byte)

   o  Recipient ID: 0x (0 byte)

   o  ID Context: 0x37cbf3210017a2d3 (8 bytes)

  From the previous parameters,

   o  info (for Sender Key): 0x8541014837cbf3210017a2d30a634b657910 (18
      bytes)

   o  info (for Recipient Key): 0x85404837cbf3210017a2d30a634b657910 (17
      bytes)

   o  info (for Common IV): 0x85404837cbf3210017a2d30a6249560d (16
      bytes)

  Outputs:

   o  Sender Key: 0xe39a0c7c77b43f03b4b39ab9a268699f (16 bytes)

   o  Recipient Key: 0xaf2a1300a5e95788b356336eeecd2b92 (16 bytes)

   o  Common IV: 0x2ca58fb85ff1b81c0b7181b85e (13 bytes)

  From the previous parameters and a Partial IV equal to 0 (both for
   sender and recipient):

   o  sender nonce: 0x2da58fb85ff1b81d0b7181b85e (13 bytes)

   o  recipient nonce: 0x2ca58fb85ff1b81c0b7181b85e (13 bytes)
*/

    // Inputs:

    Buffer
        secret          { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 },
        salt            { 0x9e, 0x7c, 0xa9, 0x22, 0x23, 0x78, 0x63, 0x40 },
        sender_id       { 0x01 },
        recipient_id    {  },
        id_context      { 0x37, 0xcb, 0xf3, 0x21, 0x00, 0x17, 0xa2, 0xd3 };

    // Generate the psuedo-random key (32-byte)

    Buffer
        prk = generate_prk( salt, secret );

    // Verify the info encodings

    Buffer
        expected_sender_info    { 0x85, 0x41, 0x01, 0x48, 0x37, 0xcb, 0xf3, 0x21, 0x00, 0x17, 0xa2, 0xd3, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_recipient_info { 0x85, 0x40, 0x48, 0x37, 0xcb, 0xf3, 0x21, 0x00, 0x17, 0xa2, 0xd3, 0x0a, 0x63, 0x4b, 0x65, 0x79, 0x10 },
        expected_common_iv_info { 0x85, 0x40, 0x48, 0x37, 0xcb, 0xf3, 0x21, 0x00, 0x17, 0xa2, 0xd3, 0x0a, 0x62, 0x49, 0x56, 0x0d };

    // Sender info

    Buffer send_info = cbor_info_encode(
        {
            sender_id,
           &id_context, // ID Context
            10,         // AES-CCM-16-64-128
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( send_info, expected_sender_info );

    // Recipient info

    Buffer recipient_info = cbor_info_encode(
        {
            recipient_id,
           &id_context,
            10,
            "Key",
            16
        } );

    BOOST_CHECK_EQUAL_RANGES( recipient_info, expected_recipient_info );

    // Common-IV info

    Buffer common_iv_info = cbor_info_encode(
        {
            { },    // empty bstr for Common-IV
           &id_context,
            10,
            "IV",
            13
        } );

    BOOST_CHECK_EQUAL_RANGES( common_iv_info, expected_common_iv_info );

    // Outputs

    Buffer
        expected_sender_key     { 0xe3, 0x9a, 0x0c, 0x7c, 0x77, 0xb4, 0x3f, 0x03, 0xb4, 0xb3, 0x9a, 0xb9, 0xa2, 0x68, 0x69, 0x9f },
        expected_recipient_key  { 0xaf, 0x2a, 0x13, 0x00, 0xa5, 0xe9, 0x57, 0x88, 0xb3, 0x56, 0x33, 0x6e, 0xee, 0xcd, 0x2b, 0x92 },
        expected_common_iv      { 0x2c, 0xa5, 0x8f, 0xb8, 0x5f, 0xf1, 0xb8, 0x1c, 0x0b, 0x71, 0x81, 0xb8, 0x5e };

    // Sender key

    Buffer sender_key = generate_key(
       prk,
       {
            sender_id,
           &id_context,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( sender_key, expected_sender_key );

    // Recipient key

    Buffer recipient_key = generate_key(
       prk,
       {
            recipient_id,
           &id_context,
            10,
            "Key",
            16
       } );

    BOOST_CHECK_EQUAL_RANGES( recipient_key, expected_recipient_key );

    // Common-IV

    Buffer common_iv = generate_key(
       prk,
       {
            { },
           &id_context,
            10,
            "IV",
            13
       } );

    BOOST_CHECK_EQUAL_RANGES( common_iv, expected_common_iv );

    // Nonces

    Buffer
        expected_sender_nonce       { 0x2d, 0xa5, 0x8f, 0xb8, 0x5f, 0xf1, 0xb8, 0x1d, 0x0b, 0x71, 0x81, 0xb8, 0x5e },
        expected_recipient_nonce    { 0x2c, 0xa5, 0x8f, 0xb8, 0x5f, 0xf1, 0xb8, 0x1c, 0x0b, 0x71, 0x81, 0xb8, 0x5e };

    // Sender nonce

    Buffer sender_nonce = generate_nonce(
       sender_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( sender_nonce, expected_sender_nonce );

    // Recipient nonce

    Buffer recipient_nonce = generate_nonce(
       recipient_id,
       { },
       common_iv );

    BOOST_CHECK_EQUAL_RANGES( recipient_nonce, expected_recipient_nonce );
}

/*
external_aad = bstr .cbor aad_array

   aad_array = [
     oscore_version : uint,
     algorithms : [ alg_aead : int / tstr ],
     request_kid : bstr,
     request_piv : bstr,
     options : bstr,
   ]
*/

struct External_Aad
{
    unsigned            oscore_version;
    std::vector<int>    alg_aead;
    Buffer              request_kid;
    Buffer              request_piv;
    Buffer              options;
};

Buffer cbor_external_aad_encode( const External_Aad & aad )
{
    Buffer cbor_data;

    // 5 element array

    cbor_data.push_back( 0x85 );

    // element 1 -- oscore version - 1

    cbor_data.push_back( 0x01 );

    // element 2 -- array of int algorithms

    cbor_data.push_back( 0x80 | aad.alg_aead.size() );  // assuming size() is small enough to encode in 5 bits... needs work
    for ( auto algorithm : aad.alg_aead )
    {
        cbor_data.push_back( algorithm );   // data encoding -- assuming all are single byte encodings ( x < 17 )
    }

    // element 3 -- value of 'kid' in COSE object -- bstr

    boost::range::push_back( cbor_data, cbor_encode_bstr( aad.request_kid ) );

    // element 4 -- partial iv -- bstr

    boost::range::push_back( cbor_data, cbor_encode_bstr( aad.request_piv ) );

    // element 4 -- already encoded options

    boost::range::push_back( cbor_data, cbor_encode_bstr( aad.options ) );

    return cbor_data;
}

/*
AAD 

Enc_structure = [
    context : "Encrypt" / "Encrypt0" / "Enc_Recipient" / "Mac_Recipient" / "Rec_Recipient",     -- tstr - we always use "Encrypt0" -- content encryption of COSE_Encrypt0
    protected : empty_or_serialized_map, -- it says map here but the doc refers to it as a bstr (RFC-8152 Sect. 5.3) -- usually empty for us...
    external_aad : bstr
  ]
*/

struct Aad
{
    std::string context;
    Buffer      protect;
    Buffer      external_aad;
};

Buffer cbor_aad_encode( const Aad & aad )
{
    Buffer cbor_data;

    // 3 element array

    cbor_data.push_back( 0x83 );

    // element 1 -- tstr context

    boost::range::push_back( cbor_data, cbor_encode_tstr( aad.context ) );

    // element 2 -- protected -- bstr -- usually empty for us(?)

    boost::range::push_back( cbor_data, cbor_encode_bstr( aad.protect ) );

    // element 3 - external_aad - wrapped in a bstr...

    boost::range::push_back( cbor_data, cbor_encode_bstr( aad.external_aad ) );

    return cbor_data;
}

/*
    The OSCORE Option Value

         0 1 2 3 4 5 6 7 <------------- n bytes -------------->
        +-+-+-+-+-+-+-+-+--------------------------------------
        |0 0 0|h|k|  n  |       Partial IV (if any) ...
        +-+-+-+-+-+-+-+-+--------------------------------------

         <- 1 byte -> <----- s bytes ------>
        +------------+----------------------+------------------+
        | s (if any) | kid context (if any) | kid (if any) ... |
        +------------+----------------------+------------------+
 
        h: 1 == kid context present, 0 == no kid context
        k: 1 == kid present, 0 == no kid
        n: 3 bit partial iv size -- allowed values 0 - 5 - 0 == no partial iv
        s: length of the kid context if h == 1, else not present
*/

Buffer encode_oscore_option_value(const std::optional<Buffer>& piv, const std::optional<Buffer>& kid_context, const std::optional<Buffer>& kid)
{
    Buffer value{ 0x00 };


    if ( piv && piv->size() <= 5 )
    {
        value[ 0 ] |= piv->size();
        boost::range::push_back( value, *piv );
    }

    if ( kid_context && kid_context->size() > 0 )       // should this be >= 0 ??
    {
        value[ 0 ] |= 0x10;
        value.push_back( kid_context->size() );
        boost::range::push_back( value, *kid_context );
    }

    if ( kid && kid->size() >= 0 )
    {
        value[ 0 ] |= 0x08;
        boost::range::push_back( value, *kid );
    }

    return value;
}


BOOST_AUTO_TEST_CASE( test_oscore_option_value_encoding )
{
    struct TestData
    {
        std::optional<Buffer>
            piv,
            context,
            kid;

        Buffer
            option_value;

        TestData( const std::optional<Buffer>& piv_,
                  const std::optional<Buffer>& kid_context_, const std::optional<Buffer>& kid_, const Buffer& expected_ )
            :
              piv( piv_ ),
              context( kid_context_ ),
              kid( kid_ ),
              option_value( expected_ )
        {

        }
    };

    // Data from https://datatracker.ietf.org/doc/html/rfc8613.html#section-6.3

    static const std::vector<TestData>  test_cases
    {
        { 
            {  },           // same as - std::nullopt in all these test cases...
            {  },
            {  },
            { 0x00 }
        },
        { 
            Buffer { 0x05 },
            {  },
            Buffer { 0x25 },
            { 0x09, 0x05, 0x25 }
        },
        { 
            Buffer { 0x00 },
            {  },                   
            Buffer {  },    // whereas this is a empty string buffer that exists...
            { 0x09, 0x00 }
        },
        { 
            Buffer { 0x05 },
            Buffer { 0x44, 0x61, 0x6c, 0x65, 0x6b },
            Buffer {  },
            { 0x19, 0x05, 0x05, 0x44, 0x61, 0x6c, 0x65, 0x6b }
        },
        { 
            Buffer { 0x07 },
            {  },
            {  },
            { 0x01, 0x07 }
        }
    };

    for ( auto test_case : test_cases )
    {
        BOOST_CHECK_EQUAL_RANGES( encode_oscore_option_value( test_case.piv, test_case.context, test_case.kid ),
                                  test_case.option_value );
    }

//    for ( auto test_case : test_cases )
//    {
//        BOOST_CHECK_EQUAL( cbor_decode_tstr( test_case.second ), "" /*test_case.first*/ );
//    }
}


Buffer encrypt_payload( const Buffer & key, const Buffer & nonce, const Buffer & plaintext, const Buffer & aad, const int tag_length )
{
    // The code below was adapted from:  
    // 
    //  https://wiki.openssl.org/index.php/EVP_Authenticated_Encryption_and_Decryption
    //  https://wiki.openssl.org/images/e/e1/Evp-ccm-encrypt.c

    // Create a new encryption context
    EVP_CIPHER_CTX *context = EVP_CIPHER_CTX_new();

    // Set cipher type and mode
    EVP_EncryptInit_ex( context, EVP_aes_128_ccm(), nullptr, nullptr, nullptr );

    // Set nonce length (it default 96 bits)
    EVP_CIPHER_CTX_ctrl( context, EVP_CTRL_CCM_SET_IVLEN, nonce.size(), nullptr );

    // Set tag length
    EVP_CIPHER_CTX_ctrl( context, EVP_CTRL_CCM_SET_TAG, tag_length, nullptr );

    // Initialise key and IV
    EVP_EncryptInit_ex( context, nullptr, nullptr, &key[ 0 ], &nonce[ 0 ] );

    int len = 0;

    // Provide the total plaintext length
    if ( 1 != EVP_EncryptUpdate( context, nullptr, &len, nullptr, plaintext.size() ) )
    {
       // handleErrors();   // what should we do in this and other error cases?
    }

    // Provide any AAD data. This can be called zero or one times as required
    if ( 1 != EVP_EncryptUpdate( context, nullptr, &len, &aad[ 0 ], aad.size() ) )
    {
      //  handleErrors();
    }

    unsigned char ciphertext[ 512 ] = {};
    int ciphertext_len = 0;

    /*
        Provide the message to be encrypted, and obtain the encrypted output.
            EVP_EncryptUpdate can only be called once for this.
    */
    if ( 1 != EVP_EncryptUpdate( context, ciphertext, &len, &plaintext[ 0 ], plaintext.size() ) )
    {
     //   handleErrors();
    }
    ciphertext_len = len;

    /*
        Finalise the encryption. Normally ciphertext bytes may be written at
        this stage, but this does not occur in CCM mode.
    */
    if ( 1 != EVP_EncryptFinal_ex( context, ciphertext + len, &len ) )
    {
       // handleErrors();
    }
    ciphertext_len += len;

    unsigned char tag[ 16 ] = {};

    // Get the tag
    if ( 1 != EVP_CIPHER_CTX_ctrl( context, EVP_CTRL_CCM_GET_TAG, tag_length, tag ) )
    {
     //   handleErrors();
    }

    // Build the payload

    Buffer payload { ciphertext, ciphertext + ciphertext_len };

    boost::range::push_back( payload, Buffer { tag, tag + tag_length } );

    return payload;
}

Buffer partial_iv_encode( uint64_t * value )
{
    if ( ! value )
    {
        return {  };    // no sequence number is an empty partial_iv
    }

    Buffer encoded = cbor_encode_uint( *value );

    if ( encoded.size() == 1 )
    {
        return encoded; // the raw value (no header byte)
    }

    // we have a header byte attached, skip it, plus we want to remove leading 0x00 bytes.
    //      find the first non-zero byte at index >= 1

    auto start = encoded.begin() + 1;

    while ( 0 == *start )
    {
        ++start;
    }

    return { start, encoded.end() };
}

BOOST_AUTO_TEST_CASE( test_oscore_partial_iv_encoding )
{
    static const std::map<uint64_t, Buffer>  test_cases
    {
        { UINT64_C(                    0 ),  { 0x00 } },
        { UINT64_C(                    1 ),  { 0x01 } },
        { UINT64_C(                    5 ),  { 0x05 } },
        { UINT64_C(                   23 ),  { 0x17 } },
        { UINT64_C(                   24 ),  { 0x18 } },
        { UINT64_C(                  255 ),  { 0xff } },
        { UINT64_C(                  256 ),  { 0x01, 0x00 } },
        { UINT64_C(                  257 ),  { 0x01, 0x01 } },
        { UINT64_C(                20000 ),  { 0x4e, 0x20 } },
        { UINT64_C(                65534 ),  { 0xff, 0xfe } },
        { UINT64_C(                65535 ),  { 0xff, 0xff } },
        { UINT64_C(                65536 ),  { 0x01, 0x00, 0x00 } },
        { UINT64_C(                65537 ),  { 0x01, 0x00, 0x01 } },
        { UINT64_C(             10000000 ),  { 0x98, 0x96, 0x80 } },
        { UINT64_C(            100000000 ),  { 0x05, 0xf5, 0xe1, 0x00 } },
        { UINT64_C(           4294967294 ),  { 0xff, 0xff, 0xff, 0xfe } },
        { UINT64_C(           4294967295 ),  { 0xff, 0xff, 0xff, 0xff } },
        { UINT64_C(           4294967296 ),  { 0x01, 0x00, 0x00, 0x00, 0x00 } },
        { UINT64_C(        1000000000000 ),  { 0xe8, 0xd4, 0xa5, 0x10, 0x00 } }
    };

    for ( auto test_case : test_cases )
    {
        uint64_t placeholder = test_case.first;

        BOOST_CHECK_EQUAL_RANGES( partial_iv_encode( &placeholder ), test_case.second );
    }

    // plus the null case

    BOOST_CHECK_EQUAL_RANGES( partial_iv_encode( nullptr ), Buffer {  } );
}

BOOST_AUTO_TEST_CASE( test_oscore_encrypted_request_generation_C_4   )
{
/* 
    https://datatracker.ietf.org/doc/html/rfc8613.html#appendix-C.4
 
Unprotected CoAP request: 
 
   0x44015d1f00003974396c6f63616c686f737483747631 (22 bytes)

  Common Context:

   o  AEAD Algorithm: 10 (AES-CCM-16-64-128)

   o  Key Derivation Function: HKDF SHA-256

   o  Common IV: 0x4622d4dd6d944168eefb54987c (13 bytes)

  Sender Context:

   o  Sender ID: 0x (0 byte)

   o  Sender Key: 0xf0910ed7295e6ad4b54fc793154302ff (16 bytes)

   o  Sender Sequence Number: 20
 
  The following COSE and cryptographic parameters are derived:

   o  Partial IV: 0x14 (1 byte)

   o  kid: 0x (0 byte)

   o  aad_array: 0x8501810a40411440 (8 bytes)

   o  AAD: 0x8368456e63727970743040488501810a40411440 (20 bytes)

   o  plaintext: 0x01b3747631 (5 bytes)

   o  encryption key: 0xf0910ed7295e6ad4b54fc793154302ff (16 bytes)

   o  nonce: 0x4622d4dd6d944168eefb549868 (13 bytes)

  From the previous parameter, the following is derived:

   o  OSCORE option value: 0x0914 (2 bytes)

   o  ciphertext: 0x612f1092f1776f1c1668b3825e (13 bytes)

  From there:

   o  Protected CoAP request (OSCORE message):
 
    0x44025d1f00003974396c6f63616c686f7374620914ff612f1092f1776f1c1668b3825e (35 bytes)
*/

    // The partial iv is the sender sequence number, if it is zero then it is the bstr { 0x00 }...  seems the max is 2^40 - 1 - need 64bits...?  -- no zero padding...

    uint64_t sender_sequence_number = 20;

    Buffer partial_iv = partial_iv_encode( &sender_sequence_number );

    // The 'kid' parameter is the sender id... so { }

    Buffer kid;

    // external_aad

    Buffer encoded_external_aad = cbor_external_aad_encode( 
        {
            1,          // OSCORE version == 1
            { 10 },     // AES-CCM-16-64-128 -- an array of algorithms...
            kid,
            partial_iv,
            {  }        // options...?
        } );

    Buffer expected_encoded_external_aad { 0x85, 0x01, 0x81, 0x0a, 0x40, 0x41, 0x14, 0x40 };

    BOOST_CHECK_EQUAL_RANGES( encoded_external_aad, expected_encoded_external_aad );

    // actual AAD

    Buffer encoded_aad = cbor_aad_encode(
       {
           "Encrypt0",  // context
           {  },        // protected - empty...
           { 0x85, 0x01, 0x81, 0x0a, 0x40, 0x41, 0x14, 0x40 }   // external_aad
       } );

    Buffer expected_encoded_aad { 0x83, 0x68, 0x45, 0x6e, 0x63, 0x72, 0x79, 0x70, 0x74, 0x30, 0x40, 0x48, 0x85, 0x01, 0x81, 0x0a, 0x40, 0x41, 0x14, 0x40 };

    BOOST_CHECK_EQUAL_RANGES( encoded_aad, expected_encoded_aad );

    // The key and nonce were established in the unit test above

    Buffer
        key         { 0xf0, 0x91, 0x0e, 0xd7, 0x29, 0x5e, 0x6a, 0xd4, 0xb5, 0x4f, 0xc7, 0x93, 0x15, 0x43, 0x02, 0xff },
        common_iv   { 0x46, 0x22, 0xd4, 0xdd, 0x6d, 0x94, 0x41, 0x68, 0xee, 0xfb, 0x54, 0x98, 0x7c },
        nonce       { 0x46, 0x22, 0xd4, 0xdd, 0x6d, 0x94, 0x41, 0x68, 0xee, 0xfb, 0x54, 0x98, 0x68 };

/* 
    Payload
        Original code
        Options
        Original payload ( 0xff + bytestream ) -- optional of course...
 
    0x01,                   -- Code: GET
    0xb3, 0x74, 0x76, 0x31  -- Option 11 (Uri-Path), Length: 3  "tv1"
*/

    Buffer plaintext { 0x01, 0xb3, 0x74, 0x76, 0x31 };


    // Option value

    Buffer option_value = encode_oscore_option_value( partial_iv, std::nullopt, kid );

    Buffer expected_option_value { 0x09, 0x14 };

    BOOST_CHECK_EQUAL_RANGES( option_value, expected_option_value );

    // Absolute OSCORE option -- note: the absolute option number is 9, will need to be delta adjusted when packed into the CoAP message

    Buffer oscore_option_absolute;

    oscore_option_absolute.push_back( 0x90 | option_value.size() );
    boost::range::push_back( oscore_option_absolute, option_value );

    Buffer expected_option_value_absolute { 0x92, 0x09, 0x14 };

    BOOST_CHECK_EQUAL_RANGES( oscore_option_absolute, expected_option_value_absolute );

    // Encryption

    Buffer ciphertext = encrypt_payload( key, nonce, plaintext, encoded_aad, 8 );

    Buffer expected_ciphertext  { 0x61, 0x2f, 0x10, 0x92, 0xf1, 0x77, 0x6f, 0x1c, 0x16, 0x68, 0xb3, 0x82, 0x5e };

    BOOST_CHECK_EQUAL_RANGES( ciphertext, expected_ciphertext );

    // TODO -- need to build the expected output from the given input -- but we have all the pieces now

    test_E2eDataTransferProtocol e2e;

    const byte_str packet =

        "44"                                            // Version 1, Type = 0 (Confirmable), Token Length = 4
        " 01"                                           // Code = 0.01 (GET)
        " 5d 1f"                                        // Message ID = 0x5d1f (23839)
        " 00 00 39 74"                                  // Token (4 bytes) 0x00003974 (14708)
        " 39"                                           // Option - Delta: 3 - Option: 3 (Uri-Host), Length: 9
        " 6c 6f 63 61 6c 68 6f 73 74"                   // Option - Value: "localhost"
        " 83"                                           // Option - Delta: 8 - Option 11 (Uri-Path), Length: 3
        " 74 76 31";                                    // Option - Value: "tv1"

    const auto request = e2e.handleIndication( packet.bytes, endpointId );

    const byte_str expected_encrypted =

        "44"                                            // Version 1, Type = 0 (Confirmable), Token Length = 4
        " 02"                                           // Code = 0.02 (POST)
        " 5d 1f"                                        // Message ID = 0x5d1f (23839)
        " 00 00 39 74"                                  // Token (4 bytes) 0x00003974 (14708)
        " 39"                                           // Option - Delta: 3 - Option: 3 (Uri-Host), Length: 9
        " 6c 6f 63 61 6c 68 6f 73 74"                   // Option - Value: "localhost"
        " 62"                                           // Option - Delta: 6 - Option: 9 (OSCORE), Length: 2
        " 09 14"                                        // Option - Value: 'k' = 1 (kid present), partial-iv length: 1 - partial-iv: 0x14
        " ff"                                           // Payload start token (ff)
        " 61 2f 10 92 f1 77 6f 1c 16 68 b3 82 5e";      // Ciphertext of COSE object

    BOOST_CHECK( true );
}

BOOST_AUTO_TEST_SUITE_END()

