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

void CbcLogicalDevice::refreshAttributeOverrides()
{
    auto deviceConfig = ConfigManager::getConfigForIdAndType(getID(), getDeviceType());

    if( deviceConfig && deviceConfig->getInstanceId() == _lastConfigId )
    {
        return;
    }

    _controlOffsetNames.clear();

    if( ! deviceConfig )
    {
        _lastConfigId = 0;
        return;
    }

    _lastConfigId = deviceConfig->getInstanceId();

    using AMC  = Cti::Config::DNPStrings::AttributeMappingConfiguration;
    using AAPN = Cti::Devices::AttributeAndPointName;

    if( const auto indexed = deviceConfig->getIndexedItem(AMC::AttributeMappings_Prefix) )
    {
        for( const auto & entry : std::vector<AAPN> { indexed->begin(), indexed->end() } )
        {
            static const std::map<std::string, ControlOffsets> controlAttributeOffsets {
                { Attribute::ControlPoint.getName(),             ControlOffsets::ControlPoint             },
                { Attribute::EnableOvuvControl.getName(),        ControlOffsets::EnableControlOvuv        },
                { Attribute::EnableTemperatureControl.getName(), ControlOffsets::EnableControlTemperature },
                { Attribute::EnableTimeControl.getName(),        ControlOffsets::EnableControlTime        },
                { Attribute::EnableVarControl.getName(),         ControlOffsets::EnableControlVar         }};

            if( auto offset = mapFind(controlAttributeOffsets, entry.attributeName) )
            {
                _controlOffsetNames.emplace(*offset, entry.pointName);
            }
        }
    }
}

CtiPointSPtr CbcLogicalDevice::getLogicalPoint(const std::string& pointName)
{
    if( !_pointMgr )
    {
        throw YukonErrorException {
            ClientErrors::NoConfigData,
            "No point manager"
            + FormattedList::of(
                "Logical CBC ID", getID(),
                "Logical CBC name", getName(),
                "Override point name", pointName) };
    }

    return _pointMgr->getLogicalPoint(getID(), pointName);
}

auto CbcLogicalDevice::getControlDeviceOffset(const ControlOffsets controlOffset) -> PaoOffset
{
    auto optName = mapFindRef(_controlOffsetNames, controlOffset);

    if( ! optName )
    {
        throw YukonErrorException { 
                ClientErrors::NoConfigData, 
                "No control offset name" 
                + FormattedList::of(
                    "Logical CBC ID", getID(),
                    "Logical CBC name", getName(),
                    "Control offset", static_cast<long>(controlOffset)) };
    }

    const auto& overridePointName = *optName;

    const auto pt = getLogicalPoint(overridePointName);

    if( ! pt )
    {
        throw YukonErrorException{ 
                ClientErrors::NoConfigData, 
                "Override point not found" 
                + FormattedList::of(
                    "Logical CBC ID", getID(),
                    "Logical CBC name", getName(),
                    "Override point name", overridePointName,
                    "Control offset", static_cast<long>(controlOffset)) };
    }

    if( ! pt->isStatus() )
    {
        throw YukonErrorException{
            ClientErrors::NoConfigData,
            "Control offset override point not Status type"
            + FormattedList::of(
                "Logical CBC ID", getID(),
                "Logical CBC name", getName(),
                "Override point name", overridePointName,
                "Override point ID", pt->getPointID(),
                "Override device ID", pt->getDeviceID(),
                "Override point type", desolvePointType(pt->getType()),
                "Control offset", static_cast<long>(controlOffset)) };
    }

    CtiPointStatus& statusPt = static_cast<CtiPointStatus&>(*pt);

    const auto control = statusPt.getControlParameters();

    if( ! control )
    {
        throw YukonErrorException{
            ClientErrors::NoConfigData,
            "Control offset override point does not have control parameters"
            + FormattedList::of(
                "Logical CBC ID", getID(),
                "Logical CBC name", getName(),
                "Override point name", overridePointName,
                "Override point ID", pt->getPointID(),
                "Override device ID", pt->getDeviceID(),
                "Control offset", static_cast<long>(controlOffset)) };
    }

    if( control->getControlOffset() <= 0 )
    {
        throw YukonErrorException{
            ClientErrors::NoConfigData,
            "Control offset override not valid"
            + FormattedList::of(
                "Logical CBC ID", getID(),
                "Logical CBC name", getName(),
                "Override point name", overridePointName,
                "Override point ID", pt->getPointID(),
                "Override device ID", pt->getDeviceID(),
                "Override control offset", control->getControlOffset(),
                "Control offset", static_cast<long>(controlOffset)) };
    }

    return { pt->getDeviceID(), control->getControlOffset() };
}


YukonError_t CbcLogicalDevice::executeRequestOnParent(const PaoOffset paoOffset, const std::string& command, const CtiRequestMsg& req, CtiMessageList& retList)
{
    auto newRequest = std::make_unique<CtiRequestMsg>(req);

    newRequest->setDeviceId(paoOffset.paoId);
    newRequest->setCommandString(command);
    newRequest->setConnectionHandle(req.getConnectionHandle());

    CTILOG_INFO(dout, "Submitting request to logical CBC parent device" << *newRequest);

    retList.push_back(newRequest.release());

    return ClientErrors::None;
}

YukonError_t CbcLogicalDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
try
{
    refreshAttributeOverrides();

    if( parse.getCommand() == PutConfigRequest )
    {
        if ( parse.isKeyValid( "local_control_type" ) && parse.isKeyValid( "local_control_state" ) )
        {
            static const std::map<std::string, ControlOffsets> _offsetLookup
            {
                { "ovuv", ControlOffsets::EnableControlOvuv        },
                { "temp", ControlOffsets::EnableControlTemperature },
                { "time", ControlOffsets::EnableControlTime        },
                { "var",  ControlOffsets::EnableControlVar         },
            };

            const std::string controlType   = parse.getsValue("local_control_type");
            const std::string controlAction = parse.getsValue("local_control_state") == "enable" ? "close" : "open";

            if ( auto defaultOffset = Cti::mapFind( _offsetLookup, controlType ) )
            {
                const auto paoOffset = getControlDeviceOffset(*defaultOffset);

                const std::string command = "control " + controlAction + " offset " + std::to_string( paoOffset.controlOffset );

                return executeRequestOnParent(paoOffset, command, *pReq, retList);
            }
        }
    }
    if( parse.getCommand() == ControlRequest )
    {
        if( !(parse.getFlags() & CMD_FLAG_OFFSET) && (parse.getFlags() & CMD_FLAG_CTL_OPEN || parse.getFlags() & CMD_FLAG_CTL_CLOSE) )
        {
            const auto paoOffset = getControlDeviceOffset(ControlOffsets::ControlPoint);
            
            const std::string command = 
                "control"s 
                    + (parse.getFlags() & CMD_FLAG_CTL_OPEN 
                        ? " open"
                        : " close")
                    + " offset " + std::to_string(paoOffset.controlOffset);

            return executeRequestOnParent(paoOffset, command, *pReq, retList);
        }
    }

    return ClientErrors::NoMethod;
}
catch( const YukonErrorException& ex )
{
    CTILOG_EXCEPTION_ERROR(dout, ex, "ExecuteRequest failed");

    insertReturnMsg(ex.error_code, OutMessage, retList, ex.error_description);

    return ClientErrors::None;
}

}
}
