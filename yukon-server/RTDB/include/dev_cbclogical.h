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

    std::string getSQLCoreStatement() const override;
    void DecodeDatabaseReader(RowReader& rdr) override;

protected:

    typedef CtiDeviceBase Inherited;

    void setParentDeviceId(long parentDeviceId, Test::use_in_unit_tests_only&);

private:
    
    AttributeMapping::AttributeList getMappableAttributes() const;

    AttributeMapping _attributeMapping;

    long _parentDeviceId;

    YukonError_t executeRequestOnParent(const std::string& command, const CtiRequestMsg& req, CtiMessageList& retList);
};

}
}
