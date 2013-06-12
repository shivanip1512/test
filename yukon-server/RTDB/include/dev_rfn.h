#pragma once

#include <dev_single.h>

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfnDevice : public CtiDeviceSingle
{
public:

    virtual void ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, CtiMessageList &rfRequests);
}

}
}


