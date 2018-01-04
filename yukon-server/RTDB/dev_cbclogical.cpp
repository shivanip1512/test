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
                        "The point ID is not a valid logical point for device " + getName() + FormattedList::of(
                            "Point ID", pointid) };
                }

                if( point->isStatus() )
                {
                    CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(point);

                    if( const auto controlParameters = pStatus->getControlParameters() )
                    {
                        if( controlParameters->isControlInhibited() )
                        {
                            CTILOG_WARN(dout, "control inhibited for device \"" << getName() << "\" point \"" << pStatus->getName());

                            std::string temp = "Control is inhibited for the specified status point on device " + getName();

                            insertReturnMsg(ClientErrors::ControlInhibitedOnPoint, OutMessage, retList, temp);

                            return ClientErrors::ControlInhibitedOnPoint;
                        }
                        if( controlParameters->getControlOffset() > 0 )
                        {
                            const auto command = pReq->CommandString() + " offset " + std::to_string(controlParameters->getControlOffset());

                            return executeRequestOnParent(command, *pReq, retList);
                        }
                    }
                }
            }
            else if( !(parse.getFlags() & CMD_FLAG_OFFSET) && (parse.getFlags() & CMD_FLAG_CTL_OPEN || parse.getFlags() & CMD_FLAG_CTL_CLOSE) )
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
            int offset;

            long control_offset = 0;

            if( parse.isKeyValid("analog") )
            {
                if( parse.isKeyValid("analogoffset") )
                {
                    control_offset = parse.getiValue("analogoffset");
                }
                else if( parse.isKeyValid("point") )
                {
                    const long pointid = parse.getiValue("point");

                    const CtiPointSPtr point = getDevicePointByID(pointid);

                    if( ! point )
                    {
                        insertReturnMsg(ClientErrors::PointLookupFailed, OutMessage, retList, "The specified point is not on the device" + FormattedList::of(
                            "Point ID", pointid));

                        return ClientErrors::PointLookupFailed;
                    }

                    if( point->getType() == AnalogPointType )
                    {
                        CtiPointAnalogSPtr pAnalog = boost::static_pointer_cast<CtiPointAnalog>(point);

                        if( const CtiTablePointControl *control = pAnalog->getControl() )
                        {
                            if( control->isControlInhibited() )
                            {
                                CTILOG_WARN(dout, "control inhibited for device \"" << getName() << "\" point \"" << pAnalog->getName());

                                insertReturnMsg(ClientErrors::ControlInhibitedOnPoint, OutMessage, retList, "Control is inhibited for the specified analog point" + FormattedList::of(
                                    "Point ID", pointid,
                                    "Point name", pAnalog->getName()));

                                return ClientErrors::ControlInhibitedOnPoint;
                            }

                            control_offset = control->getControlOffset();
                        }
                        else if( pAnalog->getPointOffset() > Protocols::DNP::AnalogOutputStatus::AnalogOutputOffset )
                        {
                            control_offset = point->getPointOffset() % Protocols::DNP::AnalogOutputStatus::AnalogOutputOffset;
                        }
                    }
                }

                if( control_offset > 0 )
                {
                    const auto analogValueStr =
                            parse.isKeyValid("analogfloatvalue")
                                ? std::to_string(parse.getdValue("analogvalue"))
                                : std::to_string(parse.getiValue("analogvalue"));

                    const auto command = "putvalue analog " + std::to_string(control_offset) + " " + analogValueStr;

                    return executeRequestOnParent(command, *pReq, retList);
                }
            }

            break;
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
