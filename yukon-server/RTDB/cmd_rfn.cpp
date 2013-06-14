#include "precompiled.h"

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

// Construct a byte vector request
RfnCommand::RfnRequest RfnCommand::execute(const CtiTime now)
{
    RfnRequest req;

    req.push_back(getCommandCode());
    req.push_back(getOperation());

    Bytes data = getData();

    req.insert(req.end(), data.begin(), data.end());

    return req;
}

// Convert type-length-value vector to a byte vector
RfnCommand::Bytes RfnCommand::getBytesFromTlvs( std::vector<TypeLengthValue> tlvs )
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

}
}
}
