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
    
    std::map<ControlOffsets, std::string> _controlOffsetNames;

    struct PaoOffset
    {
        long paoId;
        long controlOffset;
    };

    YukonError_t executeRequestOnParent(const PaoOffset paoOffset, const std::string& command, const CtiRequestMsg& req, CtiMessageList& retList);
        
    PaoOffset getControlDeviceOffset(ControlOffsets defaultControlOffset);

    size_t _lastConfigId = 0;
};

}
}
