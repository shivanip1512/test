#include "precompiled.h"

#include "cmd_dlc.h"

using namespace std;

namespace Cti {
namespace Devices {
namespace Commands {

//  throws CommandException
unsigned DlcCommand::getValueFromBits(const payload_t &data, const unsigned start_offset, const unsigned length)
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
vector<unsigned> DlcCommand::getValueVectorFromBits(const payload_t &data, const unsigned start_offset, const unsigned length, const unsigned count)
{
    vector<unsigned> values;

    for( unsigned i = 0; i < count; ++i )
    {
        values.push_back(getValueFromBits(data, start_offset + i * length, length));
    }

    return values;
}

}
}
}
