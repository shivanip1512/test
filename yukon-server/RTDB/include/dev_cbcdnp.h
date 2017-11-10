#pragma once

#include "dev_dnp.h"
#include "attribute_mapping.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB CbcDnpDevice : public DnpDevice
{
public:

    CbcDnpDevice();

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

protected:

    virtual AttributeMapping::AttributeList getMappableAttributes() const;

    AttributeMapping _attributeMapping;

    virtual int getDnpControlOffset(const Attribute attribute);

private:

    using Inherited = DnpDevice;
};

}
}
