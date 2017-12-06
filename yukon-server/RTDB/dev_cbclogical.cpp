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
    :   _attributeMapping { [this](const std::string& name) { return getDevicePointByName(name); } },
        _parentDeviceId {}
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

void CbcLogicalDevice::DecodeDatabaseReader(RowReader& rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    rdr["ParentID"] >> _parentDeviceId;
}

void CbcLogicalDevice::setParentDeviceId(const long parentDeviceId, Test::use_in_unit_tests_only&)
{
    _parentDeviceId = parentDeviceId;
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

YukonError_t CbcLogicalDevice::executeRequestOnParent(const std::string& command, const CtiRequestMsg& req, CtiMessageList& retList)
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

YukonError_t CbcLogicalDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
try
{
    _attributeMapping.refresh(getDeviceConfig(), getMappableAttributes());

    if( parse.getCommand() == LoopbackRequest ||
        parse.getCommand() == ScanRequest )
    {
        return executeRequestOnParent(pReq->CommandString(), *pReq, retList);
    }
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
                const auto controlOffset = _attributeMapping.getControlOffset(*attrib);

                const std::string command = "control " + controlAction + " offset " + std::to_string(controlOffset);

                return executeRequestOnParent(command, *pReq, retList);
            }
        }
    }
    if( parse.getCommand() == ControlRequest )
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
