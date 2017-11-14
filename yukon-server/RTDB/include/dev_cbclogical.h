#pragma once

#include "dev_base.h"
#include "attribute_mapping.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB CbcLogicalDevice : public CtiDeviceBase
{
public:

    CbcLogicalDevice();

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

protected:

    typedef CtiDeviceBase Inherited;

    CtiPointSPtr getDevicePointByName(const std::string& name) override;
    CtiPointSPtr getDevicePointByID(int pointid) override;

private:
    
    AttributeMapping::AttributeList getMappableAttributes() const;

    AttributeMapping _attributeMapping;

    YukonError_t executeRequestOnParent(const int deviceId, const std::string& command, const CtiRequestMsg& req, CtiMessageList& retList);
};

}
}
