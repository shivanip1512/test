#include "precompiled.h"

#include "EmetconWords.h"
#include "dlldefs.h"
#include "cti_asmc.h"

#include <bitset>
#include <boost/scoped_array.hpp>

using namespace std;

namespace Cti {
namespace Simulator {

unsigned EmetconWord::extract_bits(const bytes &buf, unsigned bit_offset, unsigned length)
{
    if( (buf.size() * 8) < (bit_offset + length) )
    {
        return 0;
    }

    unsigned value = 0;

    while( length )
    {
        const unsigned sub_offset = bit_offset % 8;
        const unsigned bits_used  = min(length, 8 - sub_offset);

        bytes::value_type chunk = buf[bit_offset / 8];

        if( sub_offset )                  chunk  &= 0xff >> sub_offset;
        if( sub_offset + bits_used < 8 )  chunk >>= (8 - sub_offset - bits_used);

        value <<= bits_used;
        value  |= chunk;

        bit_offset += bits_used;
        length     -= bits_used;
    }

    return value;
}

void EmetconWord::append_bits(long long &destination, unsigned bits, unsigned value)
{
    destination <<= bits;
    destination  |= value & ((0x1 << bits) - 1);
}

void EmetconWord::write_bits(long long source, const unsigned bits, byte_appender output)
{
    //  if it needs to be zero-padded
    if( bits % 8 )
    {
        source <<= 8 - (bits % 8);
    }

    unsigned char *begin = reinterpret_cast<unsigned char *>(&source);

    unsigned byte_length = (bits + 7) / 8;

    //  copy it out MSB
    reverse_copy(begin, begin + byte_length, output);
}

unsigned EmetconWord::bch_calc(long long source, const unsigned bits)
{
    //  if it needs to be zero-padded
    if( bits % 8 )
    {
        source <<= 8 - (bits % 8);
    }

    unsigned char *begin = reinterpret_cast<unsigned char *>(&source);

    unsigned byte_length = (bits + 7) / 8;

    boost::scoped_array<unsigned char> temp(new unsigned char[byte_length]);

    //  copy it out MSB
    reverse_copy(begin, begin + byte_length, temp.get());

    return BCHCalc_C(temp.get(), bits) >> 2;
}


bool EmetconWord::restoreWords(const bytes &buf, words_t &words)
{
    if( buf.empty() )  return false;

    unsigned pos = 0, d_words = 0;

    while( pos < buf.size() )
    {
        bytes word_buf = bytes(buf.begin() + pos, buf.end());

        const EmetconWord *word = 0;
        unsigned len = 0;

        switch( extract_bits(word_buf, 0, 4) )
        {
//  TODO-P3: support repeat A and B words?
            //case EmetconWordA::TypeCodeDouble:
            case EmetconWordA::TypeCode:
            {
                word = EmetconWordA::restore(word_buf);
                len  = EmetconWordA::Length;
                break;
            }
            //case EmetconWordB::TypeCodeDouble:
            case EmetconWordB::TypeCode:
            {
                word = EmetconWordB::restore(word_buf);
                len  = EmetconWordB::Length;
                break;
            }
            case EmetconWordC::TypeCode:
            {
                word = EmetconWordC::restore(word_buf);
                len  = EmetconWordC::Length;
                break;
            }
            case EmetconWordD::TypeCode:
            {
                switch( d_words )
                {
                    case 0:  word = EmetconWordD1::restore(word_buf);  d_words++; break;
                    case 1:  word = EmetconWordD2::restore(word_buf);  d_words++; break;
                    case 2:  word = EmetconWordD3::restore(word_buf);  d_words++; break;
                }

                len = EmetconWordD::Length;
                break;
            }
            case EmetconWordE::TypeCode:
            {
                word = EmetconWordE::restore(word_buf);
                len  = EmetconWordE::Length;
                break;
            }
            default:
            {
                return false;
            }
        }

        if( !word || !len )
        {
            return false;
        }

        words.push_back(word_t(word));
        pos += len;
    }

    return true;
}

const EmetconWord *EmetconWordA::restore(const bytes &buf)
{
    if( (buf.size() * 8) < BitLength )         return 0;
    if( extract_bits(buf, 0, 4) != TypeCode )  return 0;

    EmetconWordA *new_word = new EmetconWordA;

    new_word->type = WordType_A;
    new_word->repeater_fixed    = extract_bits(buf,  4, 5);
    new_word->repeater_variable = extract_bits(buf,  9, 3);
    new_word->group_address     = extract_bits(buf, 12, 6);

    switch( extract_bits(buf, 18, 2) )
    {
        case ShedTime_0450:  new_word->shed_time = ShedTime_0450;  break;
        case ShedTime_0900:  new_word->shed_time = ShedTime_0900;  break;
        case ShedTime_1800:  new_word->shed_time = ShedTime_1800;  break;
        case ShedTime_3600:  new_word->shed_time = ShedTime_3600;  break;
    }

    new_word->function_code     = extract_bits(buf, 20, 3);
    new_word->bch               = extract_bits(buf, 24, BitLength_Bch);

    return new_word;
}

bool EmetconWordA::serialize(byte_appender output) const
{
    long long serialized = 0;

    append_bits(serialized, 4, TypeCode);
    append_bits(serialized, 5, repeater_fixed);
    append_bits(serialized, 3, repeater_variable);
    append_bits(serialized, 6, group_address);
    append_bits(serialized, 2, shed_time);
    append_bits(serialized, 3, function_code);

    if( bch == numeric_limits<unsigned>::max() )
    {
        append_bits(serialized,  BitLength_Bch, bch_calc(serialized, BitLength - BitLength_Bch));
    }
    else
    {
        append_bits(serialized,  BitLength_Bch, bch);
    }

    write_bits(serialized, BitLength, output);

    return true;
}

EmetconWordB::EmetconWordB() :
    dlc_address      (0),
    repeater_fixed   (0),
    repeater_variable(0),
    write            (0),
    function         (0),
    function_code    (0),
    words_to_follow  (0)
{
    type = WordType_B;
    bch  = 0;
}

EmetconWordB::EmetconWordB(unsigned repeater_fixed_,
                           unsigned repeater_variable_,
                           unsigned dlc_address_,
                           unsigned words_to_follow_,
                           unsigned function_code_,
                           bool function_,
                           bool write_,
                           unsigned bch_) :
    repeater_fixed   (repeater_fixed_),
    repeater_variable(repeater_variable_),
    dlc_address      (dlc_address_),
    function_code    (function_code_),
    function         (function_),
    write            (write_),
    words_to_follow  (words_to_follow_)
{
    type = WordType_B;
    bch  = bch_;
}

const EmetconWord *EmetconWordB::restore(const bytes &buf)
{
    if( (buf.size() * 8) < BitLength )         return 0;
    if( extract_bits(buf, 0, 4) != TypeCode )  return 0;

    return new EmetconWordB(extract_bits(buf,  4,  5),
                            extract_bits(buf,  9,  3),
                            extract_bits(buf, 12, 22),
                            extract_bits(buf, 34,  2),
                            extract_bits(buf, 36,  8),
                            extract_bits(buf, 44,  1),
                            !extract_bits(buf, 45,  1),
                            extract_bits(buf, 46,  BitLength_Bch));
}

bool EmetconWordB::serialize(byte_appender output) const
{
    long long serialized = 0;

    append_bits(serialized,  4, TypeCode);
    append_bits(serialized,  5, repeater_fixed);
    append_bits(serialized,  3, repeater_variable);
    append_bits(serialized, 22, dlc_address);
    append_bits(serialized,  2, words_to_follow);
    append_bits(serialized,  8, function_code);
    append_bits(serialized,  1, function);
    append_bits(serialized,  1, !write);

    if( bch == numeric_limits<unsigned>::max() )
    {
        append_bits(serialized,  BitLength_Bch, bch_calc(serialized, BitLength - BitLength_Bch));
    }
    else
    {
        append_bits(serialized,  BitLength_Bch, bch);
    }

    write_bits(serialized, BitLength, output);

    return true;
}

EmetconWordC::EmetconWordC(const bytes &data_, bool flag_, unsigned bch_) :
    data(data_),
    flag(flag_)
{
    type = WordType_C;
    bch  = bch_;
}

unsigned EmetconWordC::words_needed( unsigned payload_length )
{
    return (payload_length + EmetconWordC::PayloadLength - 1) / EmetconWordC::PayloadLength;
}

const EmetconWord *EmetconWordC::restore(const bytes &buf)
{
    if( (buf.size() * 8) < BitLength )         return 0;
    if( extract_bits(buf, 0, 4) != TypeCode )  return 0;

    unsigned bytes_used = PayloadLength;

    //  is the partial bit set?
    if( extract_bits(buf, 45, 1) )
    {
        //  don't assume the partial-length byte is valid - it might be corrupt
        bytes_used = min(PayloadLength - 1U, extract_bits(buf, 36, 8));
    }

    unsigned data_start_bit = 4;
    bytes    myData;

    while( bytes_used-- )
    {
        myData.push_back(extract_bits(buf, data_start_bit, 8));
        data_start_bit += 8;
    }

    return new EmetconWordC(myData,
                            extract_bits(buf, 44, 1),  // flag
                            extract_bits(buf, 46, BitLength_Bch));
}

bool EmetconWordC::serialize(byte_appender output) const
{
    long long serialized = 0;

    append_bits(serialized, 4, TypeCode);

    bytes::const_iterator data_itr = data.begin(),
                            data_end = data.end();

    while( data_itr != data_end )
    {
        append_bits(serialized, 8, *data_itr++);
    }

    switch( data.size() )
    {
        case 0:  append_bits(serialized, 8, 0);
        case 1:  append_bits(serialized, 8, 0);
        case 2:  append_bits(serialized, 8, 0);
        case 3:  append_bits(serialized, 8, 0);
        case 4:  append_bits(serialized, 8, data.size());
    }

    append_bits(serialized,  1, flag);
    append_bits(serialized,  1, data.size() < PayloadLength);

    if( bch == numeric_limits<unsigned>::max() )
    {
        append_bits(serialized,  BitLength_Bch, bch_calc(serialized, BitLength - BitLength_Bch));
    }
    else
    {
        append_bits(serialized,  BitLength_Bch, bch);
    }


    write_bits(serialized, BitLength, output);

    return true;
}

unsigned EmetconWordD::words_needed( unsigned payload_length )
{
    if( !payload_length )
    {
        return 0;
    }

    if( payload_length <= EmetconWordD1::PayloadLength )
    {
        return 1;
    }

    if( payload_length <= EmetconWordD1::PayloadLength +
                          EmetconWordD2::PayloadLength )
    {
        return 2;
    }

    if( payload_length <= EmetconWordD1::PayloadLength +
                          EmetconWordD2::PayloadLength +
                          EmetconWordD3::PayloadLength )
    {
        return 3;
    }

    return 0;
}

EmetconWordD1::EmetconWordD1(unsigned repeater_variable_,
                             unsigned echo_address_,
                             unsigned char data0,
                             unsigned char data1,
                             unsigned char data2,
                             bool power_fail_,
                             bool alarm_,
                             unsigned bch_) :
    repeater_variable(repeater_variable_),
    echo_address     (echo_address_),
    power_fail       (power_fail_),
    alarm            (alarm_)
{
    type = WordType_D1;

    data[0] = data0;
    data[1] = data1;
    data[2] = data2;

    bch     = bch_;
}

const EmetconWord *EmetconWordD1::restore(const bytes &buf)
{
    if( (buf.size() * 8) < BitLength )         return 0;
    if( extract_bits(buf, 0, 4) != TypeCode )  return 0;

    return new EmetconWordD1(extract_bits(buf,  4,  3),
                             extract_bits(buf,  7, 13),
                             extract_bits(buf, 20,  8),
                             extract_bits(buf, 28,  8),
                             extract_bits(buf, 36,  8),
                             extract_bits(buf, 44,  1),
                             extract_bits(buf, 45,  1),
                             extract_bits(buf, 46,  BitLength_Bch));
}

bool EmetconWordD1::serialize(byte_appender output) const
{
    long long serialized = 0;

    append_bits(serialized,  4, TypeCode);
    append_bits(serialized,  3, repeater_variable);
    append_bits(serialized, 13, echo_address);
    append_bits(serialized,  8, data[0]);
    append_bits(serialized,  8, data[1]);
    append_bits(serialized,  8, data[2]);
    append_bits(serialized,  1, power_fail);
    append_bits(serialized,  1, alarm);

    if( bch == numeric_limits<unsigned>::max() )
    {
        append_bits(serialized,  BitLength_Bch, bch_calc(serialized, BitLength - BitLength_Bch));
    }
    else
    {
        append_bits(serialized,  BitLength_Bch, bch);
    }

    write_bits(serialized, BitLength, output);

    return true;
}

EmetconWordD2::EmetconWordD2(unsigned char data0,
                             unsigned char data1,
                             unsigned char data2,
                             unsigned char data3,
                             unsigned char data4,
                             bool time_sync_loss_,
                             bool spare_,
                             unsigned bch_) :
    time_sync_loss(time_sync_loss_),
    spare         (spare_)
{
    type = WordType_D2;

    data[0] = data0;
    data[1] = data1;
    data[2] = data2;
    data[3] = data3;
    data[4] = data4;

    bch     = bch_;
}

const EmetconWord *EmetconWordD2::restore(const bytes &buf)
{
    if( (buf.size() * 8) < BitLength )         return 0;
    if( extract_bits(buf, 0, 4) != TypeCode )  return 0;

    return new EmetconWordD2(extract_bits(buf,  4, 8),
                             extract_bits(buf, 12, 8),
                             extract_bits(buf, 20, 8),
                             extract_bits(buf, 28, 8),
                             extract_bits(buf, 36, 8),
                             extract_bits(buf, 44, 1),
                             extract_bits(buf, 45, 1),
                             extract_bits(buf, 46, BitLength_Bch));
}

bool EmetconWordD2::serialize(byte_appender output) const
{
    long long serialized = 0;

    append_bits(serialized,  4, TypeCode);
    append_bits(serialized,  8, data[0]);
    append_bits(serialized,  8, data[1]);
    append_bits(serialized,  8, data[2]);
    append_bits(serialized,  8, data[3]);
    append_bits(serialized,  8, data[4]);
    append_bits(serialized,  1, time_sync_loss);
    append_bits(serialized,  1, spare);

    if( bch == numeric_limits<unsigned>::max() )
    {
        append_bits(serialized,  BitLength_Bch, bch_calc(serialized, BitLength - BitLength_Bch));
    }
    else
    {
        append_bits(serialized,  BitLength_Bch, bch);
    }

    write_bits(serialized, BitLength, output);

    return true;
}

EmetconWordD3::EmetconWordD3(unsigned char data0,
                             unsigned char data1,
                             unsigned char data2,
                             unsigned char data3,
                             unsigned char data4,
                             bool spare1_,
                             bool spare2_,
                             unsigned bch_) :
    spare1        (spare1_),
    spare2        (spare2_)
{
    type = WordType_D3;

    data[0] = data0;
    data[1] = data1;
    data[2] = data2;
    data[3] = data3;
    data[4] = data4;

    bch     = bch_;
}

const EmetconWord *EmetconWordD3::restore(const bytes &buf)
{
    if( (buf.size() * 8) < BitLength )         return 0;
    if( extract_bits(buf, 0, 4) != TypeCode )  return 0;

    return new EmetconWordD3(extract_bits(buf,  4, 8),
                             extract_bits(buf, 12, 8),
                             extract_bits(buf, 20, 8),
                             extract_bits(buf, 28, 8),
                             extract_bits(buf, 36, 8),
                             extract_bits(buf, 44, 1),
                             extract_bits(buf, 45, 1),
                             extract_bits(buf, 46, BitLength_Bch));
}

bool EmetconWordD3::serialize(byte_appender output) const
{
    long long serialized = 0;

    append_bits(serialized,  4, TypeCode);
    append_bits(serialized,  8, data[0]);
    append_bits(serialized,  8, data[1]);
    append_bits(serialized,  8, data[2]);
    append_bits(serialized,  8, data[3]);
    append_bits(serialized,  8, data[4]);
    append_bits(serialized,  1, spare1);
    append_bits(serialized,  1, spare2);

    if( bch == numeric_limits<unsigned>::max() )
    {
        append_bits(serialized,  BitLength_Bch, bch_calc(serialized, BitLength - BitLength_Bch));
    }
    else
    {
        append_bits(serialized,  BitLength_Bch, bch);
    }

    write_bits(serialized, BitLength, output);

    return true;
}

const EmetconWord *EmetconWordE::restore(const bytes &buf)
{
    if( (buf.size() * 8) < BitLength )         return 0;
    if( extract_bits(buf, 0, 4) != TypeCode )  return 0;

    EmetconWordE *new_word = new EmetconWordE;

    new_word->type = WordType_E;
    new_word->repeater_variable  = extract_bits(buf,  4,  3);
    new_word->echo_address       = extract_bits(buf,  7, 13);
    new_word->diagnostic_data[0] = extract_bits(buf, 20,  8);
    new_word->diagnostic_data[1] = extract_bits(buf, 28,  8);
    new_word->diagnostic_data[2] = extract_bits(buf, 36,  8);

    new_word->power_fail = extract_bits(buf, 44, 1);
    new_word->alarm      = extract_bits(buf, 45, 1);

    new_word->bch        = extract_bits(buf, 46, BitLength_Bch);

    return new_word;
}

bool EmetconWordE::serialize(byte_appender output) const
{
    long long serialized = 0;

    append_bits(serialized,  4, TypeCode);
    append_bits(serialized,  3, repeater_variable);
    append_bits(serialized, 13, echo_address);
    append_bits(serialized,  8, diagnostic_data[0]);
    append_bits(serialized,  8, diagnostic_data[1]);
    append_bits(serialized,  8, diagnostic_data[2]);
    append_bits(serialized,  1, power_fail);
    append_bits(serialized,  1, alarm);

    if( bch == numeric_limits<unsigned>::max() )
    {
        append_bits(serialized,  BitLength_Bch, bch_calc(serialized, BitLength - BitLength_Bch));
    }
    else
    {
        append_bits(serialized,  BitLength_Bch, bch);
    }

    write_bits(serialized, BitLength, output);

    return true;
}

}
}

