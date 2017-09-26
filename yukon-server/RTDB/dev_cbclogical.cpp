#include "precompiled.h"

#include "dev_cbclogical.h"
#include "pt_status.h"

#include "msg_pcrequest.h"

#include "mgr_config.h"
#include "config_data_dnp.h"
#include "config_helpers.h"

#include "desolvers.h"

#include "Attribute.h"
#include "std_helper.h"

namespace Cti {
namespace Devices {

void CbcLogicalDevice::refreshAttributeOverrides()
{
    auto deviceConfig = ConfigManager::getConfigForIdAndType(getID(), getDeviceType());

    if( deviceConfig && deviceConfig->getInstanceId() == _lastConfigId )
    {
        return;
    }

    _controlOffsetOverrides.clear();
    _pointOffsetOverrides.clear();

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
                _controlOffsetOverrides.emplace(*offset, entry.pointName);
            }
        }
    }
}

long CbcLogicalDevice::getControlOffset(const ControlOffsets defaultControlOffset)
{
    if( auto optName = mapFindRef(_controlOffsetOverrides, defaultControlOffset) )
    {
        const auto& overridePointName = *optName;

        if( const auto pt = getDevicePointByName(overridePointName) )
        {
            if( pt->isStatus() )
            {
                CtiPointStatus& statusPt = static_cast<CtiPointStatus&>(*pt);

                if( const auto control = statusPt.getControlParameters() )
                {
                    if( control->getControlOffset() > 0 )
                    {
                        return control->getControlOffset();
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "Control offset override not valid" <<
                            FormattedList::of(
                                "Override point name", overridePointName,
                                "Override point ID", pt->getPointID(),
                                "Override control offset", control->getControlOffset(),
                                "Default control offset", static_cast<long>(defaultControlOffset)));
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Control offset override point does not have control parameters" <<
                        FormattedList::of(
                            "Override point name", overridePointName,
                            "Override point ID", pt->getPointID(),
                            "Default control offset", static_cast<long>(defaultControlOffset)));
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Control offset override point not Status type" <<
                    FormattedList::of(
                        "Override point name", overridePointName,
                        "Override point ID", pt->getPointID(),
                        "Override point type", desolvePointType(pt->getType()),
                        "Default control offset", static_cast<long>(defaultControlOffset)));
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Override point not found" <<
                FormattedList::of(
                    "Override point name", overridePointName,
                    "Default control offset", static_cast<long>(defaultControlOffset)));
        }
    }

    return static_cast<long>(defaultControlOffset);
}


YukonError_t CbcLogicalDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    if( parse.getCommand() == PutConfigRequest )
    {
        if ( parse.isKeyValid( "local_control_type" ) && parse.isKeyValid( "local_control_state" ) )
        {
            refreshAttributeOverrides();

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
                const long controlOffset = getControlOffset(*defaultOffset);

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

                return ClientErrors::PointLookupFailed;
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
            const auto controlOffset = getControlOffset(ControlOffsets::ControlPoint);
            
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

}
}
