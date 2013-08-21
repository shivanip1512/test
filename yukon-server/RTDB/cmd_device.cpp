#include "precompiled.h"

#include "cmd_device.h"

#include "yukon.h"

using namespace std;

namespace Cti {
namespace Devices {
namespace Commands {

//  throws CommandException
unsigned DeviceCommand::getValueFromBits(const Bytes &data, const unsigned start_offset, const unsigned length)
{
    if( start_offset + length > data.size() * 8 )
    {
        throw CommandException(NOTNORMAL, "Payload too small");
    }

    unsigned value = 0;

    for( unsigned pos = start_offset; pos < start_offset + length; )
    {
        unsigned bit_offset  = pos % 8;
        unsigned bit_length  = std::min<unsigned>(start_offset + length - pos, 8 - bit_offset);

        unsigned char tmp = data[pos / 8];

        tmp &= 0xff >> bit_offset;

        tmp >>= 8 - (bit_offset + bit_length);

        value <<= bit_length;

        value |= tmp;

        pos += bit_length;
    }

    return value;
}


//  throws CommandException
unsigned DeviceCommand::getValueFromBitsLE(const Bytes &data, const unsigned start_offset, const unsigned length)
{
    const unsigned end_offset = start_offset + length; // = 8

    if( end_offset > data.size() * 8 )
    {
        throw CommandException(NOTNORMAL, "Payload too small");
    }

    unsigned value = 0;

    for( unsigned pos = start_offset; pos < end_offset; )
    {
        const unsigned bit_offset   = pos % 8;
        const unsigned byte_offset  = pos / 8;
        const unsigned bits_to_read = std::min(end_offset - pos, 8 - bit_offset);

        unsigned char tmp = data[byte_offset];
        tmp >>= bit_offset;

        unsigned char mask = 0xff >> ( 8 - bits_to_read );
        tmp &= mask;

        value |= tmp << (pos - start_offset);

        pos += bits_to_read;
    }

    return value;
}


//  throws CommandException
vector<unsigned> DeviceCommand::getValueVectorFromBits(const Bytes &data, const unsigned start_offset, const unsigned length, const unsigned count)
{
    vector<unsigned> values;

    for( unsigned i = 0; i < count; ++i )
    {
        values.push_back(getValueFromBits(data, start_offset + i * length, length));
    }

    return values;
}


void DeviceCommand::setBits(Bytes &data, const unsigned start_offset, const unsigned length, const unsigned value)
{
    if( ! length )
    {
        return;
    }

    const unsigned end_offset = start_offset + length; // = 8

    const unsigned bytes_required = (end_offset + 7) / 8; // = 1

    if( data.size() < bytes_required )
    {
        data.resize(bytes_required);
    }

    for( unsigned pos = start_offset; pos < end_offset; )
    {
        //  start_offset = 1, length = 7
        const unsigned bit_offset  = pos % 8; // = 1
        const unsigned byte_offset = pos / 8; // = 0

        const unsigned bits_to_write = std::min(end_offset - pos, 8U - bit_offset);  //  min(8 - 1, 8 - 1) = 7

        unsigned char mask = 0xff;

        mask &= 0xff << bit_offset; //  mask << 1
        mask &= 0xff >> (8 - bits_to_write - bit_offset);  //  mask &= 0xff >> 8 - 7 - 1

        unsigned char data_to_write;

        data_to_write = value >> (pos - start_offset);
        data_to_write <<= bit_offset;

        data[byte_offset] &= ~mask;
        data[byte_offset] |= data_to_write & mask;

        pos += bits_to_write;
    }
}


}
}
}
