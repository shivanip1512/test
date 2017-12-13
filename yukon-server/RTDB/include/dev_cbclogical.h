#pragma once

#include "dev_dnpchild.h"
#include "attribute_mapping.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB CbcLogicalDevice : public DnpChildDevice
{
public:

    CbcLogicalDevice();

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    std::string getSQLCoreStatement() const override;

private:
    
    AttributeMapping::AttributeList getMappableAttributes() const;

    AttributeMapping _attributeMapping;
};

}
}
