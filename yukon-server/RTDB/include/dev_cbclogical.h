#pragma once

#include "dev_base.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB CbcLogicalDevice : public CtiDeviceBase
{
public:

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

protected:
    typedef CtiDeviceBase Inherited;

    enum class ControlOffsets : long
    {
        ControlPoint             =  1,
        EnableControlOvuv        = 14,
        EnableControlVar         = 15,
        EnableControlTemperature = 16,
        EnableControlTime        = 23,
    };

    void refreshAttributeOverrides();

private:
    
    std::map<PointOffsets, std::string> _pointOffsetOverrides;
    std::map<ControlOffsets, std::string> _controlOffsetOverrides;

    long getControlOffset(ControlOffsets defaultControlOffset);

    size_t _lastConfigId = 0;
};

}
}
