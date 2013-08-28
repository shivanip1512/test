#include "precompiled.h"

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

// Construct a byte vector request
RfnCommand::RfnRequest RfnCommand::executeCommand(const CtiTime now)
{
    RfnRequest req;

    prepareCommandData(now);

    req.push_back(getCommandCode());
    req.push_back(getOperation());

    Bytes data = getCommandData();

    req.insert(req.end(), data.begin(), data.end());

    return req;
}

// Convert type-length-value vector to a byte vector
RfnCommand::Bytes RfnCommand::getBytesFromTlvs( const std::vector<TypeLengthValue> &tlvs )
{
    Bytes tlvs_bytes;

    // the first byte correspond to the number of tlv items
    tlvs_bytes.push_back( tlvs.size() );

    for each( const TypeLengthValue &tlv in tlvs )
    {
        tlvs_bytes.push_back( tlv.type );
        tlvs_bytes.push_back( tlv.value.size() );
        tlvs_bytes.insert( tlvs_bytes.end(), tlv.value.begin(), tlv.value.end() );
    }

    return tlvs_bytes;
}

// Convert a byte vector to a type-length-value vector
std::vector<RfnCommand::TypeLengthValue> RfnCommand::getTlvsFromBytes( const Bytes &bytes )
{
    return getTlvsFromBytesWithLength(1, bytes);
}


// Convert a byte vector to a type-length-value vector
std::vector<RfnCommand::TypeLengthValue> RfnCommand::getLongTlvsFromBytes( const Bytes &bytes )
{
    return getTlvsFromBytesWithLength(2, bytes);
}


// Convert a byte vector to a type-length-value vector
std::vector<RfnCommand::TypeLengthValue> RfnCommand::getTlvsFromBytesWithLength( const unsigned length, const Bytes &bytes )
{
    Bytes::const_iterator itr = bytes.begin();

    std::vector<TypeLengthValue> tlvs;

    if( itr == bytes.end() )
    {
        throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
    }

    unsigned count = *itr++;

    while( count-- )
    {
        if( itr == bytes.end() )
        {
            throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
        }

        TypeLengthValue tlv(*itr++);

        if( itr == bytes.end() )
        {
            throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
        }

        unsigned tlv_length = *itr++;

        if( length == 2 )
        {
            if( itr == bytes.end() )
            {
                throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
            }

            tlv_length <<= 8;

            tlv_length |= *itr++;
        }

        while( tlv_length-- )
        {
            if( itr == bytes.end() )
            {
                throw CommandException(ErrorInvalidData, "Incomplete data for TLV");
            }

            tlv.value.push_back(*itr++);
        }

        tlvs.push_back(tlv);
    }

    return tlvs;
}


}
}
}
