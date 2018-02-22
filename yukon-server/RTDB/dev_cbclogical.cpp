#include "precompiled.h"

#include "dev_cbclogical.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "dnp_object_analogoutput.h"

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
    :   _attributeMapping { [this](const std::string& name) { return getDevicePointByName(name); } }
{
}

std::string CbcLogicalDevice::getSQLCoreStatement() const
{
    static const std::string sqlCore = 
        "SELECT"
            " YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag"
            ", DV.deviceid, DV.alarminhibit, DV.controlinhibit"
            ", DP.parentid"
        " FROM"
            " YukonPAObject YP"
            " JOIN Device DV on YP.PAObjectID=DV.DeviceID"
            " JOIN DeviceParent DP on DV.DeviceID=DP.DeviceID"
        " WHERE"
            " YP.type='CBC Logical'";

    return sqlCore;
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

YukonError_t CbcLogicalDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
try
{
    _attributeMapping.refresh(getDeviceConfig(), getMappableAttributes());

    switch( parse.getCommand() )
    {
        case LoopbackRequest:   
        case ScanRequest:
        {
            return executeRequestOnParent(pReq->CommandString(), *pReq, retList);
        }
        case PutConfigRequest:
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
                    const auto controlOffset = _attributeMapping.getControlOffset(*attrib);

                    const std::string command = "control " + controlAction + " offset " + std::to_string(controlOffset);

                    return executeRequestOnParent(command, *pReq, retList);
                }
            }
            break;
        }
        case ControlRequest:
        {
            const int pointid = parse.getiValue("point");

            if( pointid > 0 )
            {
                //  select by raw pointid
                CtiPointSPtr point = getDevicePointByID(pointid);

                if( ! point )
                {
                    throw YukonErrorException {
                        ClientErrors::PointLookupFailed,
                        "The specified point is not on the device" };
                }

                return executeRequestOnParent(pReq->CommandString(), *pReq, retList);
            }
            else if( parse.getFlags() & CMD_FLAG_OFFSET )
            {
                const auto controlOffset = parse.getiValue("offset");

                //  make sure the control offset is on the device to prevent sending a command to another Logical device on the same RTU
                if( ! getDeviceControlPointOffsetEqual(controlOffset) )
                {
                    throw YukonErrorException {
                        ClientErrors::PointLookupFailed,
                        "The control offset is not associated with any points on the device" };
                }

                return executeRequestOnParent(pReq->CommandString(), *pReq, retList);
            }
            else if( parse.getFlags() & CMD_FLAG_CTL_OPEN || parse.getFlags() & CMD_FLAG_CTL_CLOSE )
            {
                const auto controlOffset = _attributeMapping.getControlOffset(Attribute::ControlPoint);
            
                const auto command = 
                    "control"s 
                        + (parse.getFlags() & CMD_FLAG_CTL_OPEN 
                            ? " open"
                            : " close")
                        + " offset " + std::to_string(controlOffset);

                return executeRequestOnParent(command, *pReq, retList);
            }
        }
        case PutValueRequest:
        {
            if( parse.isKeyValid("analog") )
            {
                if( parse.isKeyValid("analogoffset") || parse.isKeyValid("point") )
                {
                    //  parent device will do point validation
                    return executeRequestOnParent(pReq->CommandString(), *pReq, retList);
                }
            }

            break;
        }
    }

    throw YukonErrorException(ClientErrors::NoMethod, "Invalid command");
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
