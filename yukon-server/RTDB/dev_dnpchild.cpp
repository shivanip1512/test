#include "precompiled.h"

#include "dev_dnpchild.h"

#include "msg_pcrequest.h"
#include "Exceptions.h"

using namespace std::string_literals;

namespace Cti {
namespace Devices {

void DnpChildDevice::DecodeDatabaseReader(RowReader& rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    rdr["ParentID"] >> _parentDeviceId;
}

long DnpChildDevice::getParentDeviceId() const
{
    return _parentDeviceId;
}

void DnpChildDevice::setParentDeviceId(const long parentDeviceId, Test::use_in_unit_tests_only&)
{
    _parentDeviceId = parentDeviceId;
}

YukonError_t DnpChildDevice::executeRequestOnParent(const std::string& command, const CtiRequestMsg& req, CtiMessageList& retList)
{
    if( ! _parentDeviceId )
    {
        throw YukonErrorException(ClientErrors::MissingConfig, "Parent device ID not set");
    }

    auto newRequest = std::make_unique<CtiRequestMsg>(req);

    newRequest->setDeviceId(_parentDeviceId);
    newRequest->setCommandString(command);
    newRequest->setConnectionHandle(req.getConnectionHandle());
    newRequest->setOptionsField(getID());

    CTILOG_INFO(dout, "Submitting request to logical CBC parent device" << *newRequest);

    retList.push_back(newRequest.release());

    return ClientErrors::None;
}

}
}
