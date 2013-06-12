#include "precompiled.h"

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

RfnCommand::RfnRequest RfnCommand::execute(const CtiTime now)
{
    RfnRequest req;

    req.push_back(getCommandCode());
    req.push_back(getOperation());

    std::vector<TypeLengthValue> &tlvs = getTlvs();

    req.push_back(tlvs.size());

    for each( const TypeLengthValue &tlv in tlvs )
    {
        req.push_back(tlv.type);
        req.push_back(tlv.length);
        req.insert(req.end(), tlv.value.begin(), tlv.value.end());
    }

    return req;
}


}
}
}
