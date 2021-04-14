#pragma once

#include "dev_dnp.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DnpRtuDevice : public DnpDevice
{
    std::set<long> _childDevices;

    long _executeId {};

    std::chrono::steady_clock::time_point _lastIntegrityScan {};
    std::chrono::seconds _childScanQuietPeriod { 3 };

    using Inherited = DnpDevice;

protected:

    PointsByType getDevicePointsByType() const override;

public:

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    CtiPointSPtr getDeviceControlPointOffsetEqual(int offset) override;
    CtiPointSPtr getDevicePointByID(int pointid) override;

    void addChildDevice(long childDevice);
    void removeChildDevice(long childDevice);
};

}
}

