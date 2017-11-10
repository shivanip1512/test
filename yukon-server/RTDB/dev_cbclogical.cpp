#include "precompiled.h"

#include "dev_cbclogical.h"
#include "pt_status.h"

#include "msg_pcrequest.h"

#include "mgr_point.h"

#include "mgr_config.h"
#include "config_data_dnp.h"
#include "config_helpers.h"
#include "Exceptions.h"

#include "desolvers.h"

#include "Attribute.h"
#include "std_helper.h"

using namespace std::string_literals;

namespace Cti {
namespace Devices {

CbcLogicalDevice::CbcLogicalDevice()
    :   _attributeMapping([this](const std::string& name) { return getDevicePointByName(name); })
{
}

CtiPointSPtr CbcLogicalDevice::getDevicePointByName(const std::string& pointName)
{
    if( !_pointMgr )
    {
        throw YukonErrorException {
            ClientErrors::NoConfigData,
            "No point manager"
            + FormattedList::of(
                "Override point name", pointName) };
    }

    return _pointMgr->getLogicalPoint(getID(), pointName);
}

auto CbcLogicalDevice::getMappableAttributes() const -> AttributeMapping::AttributeList
{
    return {
        Attribute::ControlPoint,
        Attribute::EnableOvuvControl,
        Attribute::EnableTemperatureControl,
        Attribute::EnableTimeControl,
        Attribute::EnableVarControl,
    };
}

YukonError_t CbcLogicalDevice::executeRequestOnParent(const int deviceId, const std::string& command, const CtiRequestMsg& req, CtiMessageList& retList)
{
    auto newRequest = std::make_unique<CtiRequestMsg>(req);

    newRequest->setDeviceId(deviceId);
    newRequest->setCommandString(command);
    newRequest->setConnectionHandle(req.getConnectionHandle());

    CTILOG_INFO(dout, "Submitting request to logical CBC parent device" << *newRequest);

    retList.push_back(newRequest.release());

    return ClientErrors::None;
}

YukonError_t CbcLogicalDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
try
{
    _attributeMapping.refresh(getDeviceConfig(), getMappableAttributes());

    if( parse.getCommand() == PutConfigRequest )
    {
        if ( parse.isKeyValid( "local_control_type" ) && parse.isKeyValid( "local_control_state" ) )
        {
            static const std::map<std::string, Attribute> _offsetLookup
            {
                { "ovuv", Attribute::EnableOvuvControl        },
                { "temp", Attribute::EnableTemperatureControl },
                { "time", Attribute::EnableTimeControl        },
                { "var",  Attribute::EnableVarControl         },
            };

            const std::string controlType   = parse.getsValue("local_control_type");
            const std::string controlAction = parse.getsValue("local_control_state") == "enable" ? "close" : "open";

            if ( auto attrib = Cti::mapFind( _offsetLookup, controlType ) )
            {
                const auto paoOffset = _attributeMapping.getPaoControlOffset(*attrib);

                const std::string command = "control " + controlAction + " offset " + std::to_string( paoOffset.controlOffset );

                return executeRequestOnParent(paoOffset.paoId, command, *pReq, retList);
            }
        }
    }
    if( parse.getCommand() == ControlRequest )
    {
        if( !(parse.getFlags() & CMD_FLAG_OFFSET) && (parse.getFlags() & CMD_FLAG_CTL_OPEN || parse.getFlags() & CMD_FLAG_CTL_CLOSE) )
        {
            const auto paoOffset = _attributeMapping.getPaoControlOffset(Attribute::ControlPoint);
            
            const std::string command = 
                "control"s 
                    + (parse.getFlags() & CMD_FLAG_CTL_OPEN 
                        ? " open"
                        : " close")
                    + " offset " + std::to_string(paoOffset.controlOffset);

            return executeRequestOnParent(paoOffset.paoId, command, *pReq, retList);
        }
    }

    return ClientErrors::NoMethod;
}
catch( const YukonErrorException& ex )
{
    CTILOG_EXCEPTION_ERROR(dout, ex, "ExecuteRequest failed" + FormattedList::of(
        "Device name", getName(), 
        "Device ID",   getID()));

    insertReturnMsg(ex.error_code, OutMessage, retList, ex.error_description);

    return ClientErrors::None;
}

}
}
