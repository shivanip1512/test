#include "precompiled.h"

#include "dev_cbcdnp.h"
#include "pt_status.h"

#include "mgr_config.h"
#include "config_data_dnp.h"
#include "config_helpers.h"
#include "Exceptions.h"

#include "desolvers.h"

#include "Attribute.h"
#include "std_helper.h"

namespace Cti {
namespace Devices {

CbcDnpDevice::CbcDnpDevice()
    :   _attributeMapping([this](const std::string& name) { return getDevicePointByName(name); })
{
}
    
auto CbcDnpDevice::getMappableAttributes() const -> AttributeMapping::AttributeList
{
    return {
        Attribute::ControlPoint,
        Attribute::EnableOvuvControl,
        Attribute::EnableTemperatureControl,
        Attribute::EnableTimeControl,
        Attribute::EnableVarControl,
    };
}

int CbcDnpDevice::getDnpControlOffset(const Attribute attribute)
{
    //  Only attribute with a default on CBC-DNPs
    if( attribute == Attribute::ControlPoint )
    {
        if( const auto offset = _attributeMapping.findControlOffset(attribute) )
        {
            return *offset;
        }
        else
        {
            return 1;
        }
    }

    return _attributeMapping.getControlOffset(attribute);
}

YukonError_t CbcDnpDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
try
{
    _attributeMapping.refresh(getDeviceConfig(), getMappableAttributes());

    if( parse.getCommand() == PutConfigRequest )
    {
        if ( parse.isKeyValid( "local_control_type" ) && parse.isKeyValid( "local_control_state" ) )
        {
            static const std::map<std::string, Attribute> _attributeLookup
            {
                { "ovuv", Attribute::EnableOvuvControl        },
                { "temp", Attribute::EnableTemperatureControl },
                { "time", Attribute::EnableTimeControl        },
                { "var",  Attribute::EnableVarControl         },
            };

            const std::string controlType   = parse.getsValue("local_control_type");
            const std::string controlAction = parse.getsValue("local_control_state") == "enable" ? "close" : "open";

            if ( auto attribute = Cti::mapFind(_attributeLookup, controlType ) )
            {
                const long controlOffset = getDnpControlOffset(*attribute);

                const std::string command = "control " + controlAction + " offset " + std::to_string( controlOffset );

                pReq->setCommandString( command );
                parse = CtiCommandParser( pReq->CommandString() );
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

            if ( ! point )
            {
                std::string errorMessage = "The specified point is not on device " + getName();
                insertReturnMsg(ClientErrors::PointLookupFailed, OutMessage, retList, errorMessage);

                return ClientErrors::None;
            }

            if( point->isStatus() )
            {
                CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(point);

                if( const auto controlParameters = pStatus->getControlParameters() )
                {
                    if( controlParameters->getControlOffset() > 0 )
                    {
                        parse = CtiCommandParser(pReq->CommandString() + " offset " + CtiNumStr(controlParameters->getControlOffset()));
                    }
                }
            }
        }
        else if( !(parse.getFlags() & CMD_FLAG_OFFSET) && (parse.getFlags() & CMD_FLAG_CTL_OPEN || parse.getFlags() & CMD_FLAG_CTL_CLOSE) )
        {
            const auto controlOffset = getDnpControlOffset(Attribute::ControlPoint);
            
            if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
            {
                pReq->setCommandString("control open offset " + std::to_string(controlOffset));
            }
            else // if( parse.getFlags() & CMD_FLAG_CTL_CLOSE ) - implied because of the above if condition
            {
                pReq->setCommandString("control close offset " + std::to_string(controlOffset));
            }

            parse = CtiCommandParser { pReq->CommandString() };
        }
    }

    return Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);
}
catch( const YukonErrorException& ex )
{
    CTILOG_EXCEPTION_ERROR(dout, ex, "ExecuteRequest failed");

    insertReturnMsg(ex.error_code, OutMessage, retList, ex.error_description);

    return ClientErrors::None;
}

}
}
