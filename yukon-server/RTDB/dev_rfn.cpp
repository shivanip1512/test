#include "precompiled.h"

#include "dev_rfn.h"

namespace Cti {
namespace Devices {

int RfnDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, RfnRequestMessages &rfnRequests)
{
    return NoMethod;
}


}
}
