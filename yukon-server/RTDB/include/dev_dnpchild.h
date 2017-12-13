#pragma once

#include "dev_base.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DnpChildDevice : public CtiDeviceBase
{
public:

    long getParentDeviceId() const;

    void DecodeDatabaseReader(RowReader& rdr) override;

protected:

    YukonError_t executeRequestOnParent(const std::string& command, const CtiRequestMsg& req, CtiMessageList& retList);

    void setParentDeviceId(long parentDeviceId, Test::use_in_unit_tests_only&);

private:
    
    using Inherited = CtiDeviceBase;

    long _parentDeviceId {};
};

}
}
