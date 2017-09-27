#include "precompiled.h"

#include "dev_cbc8020.h"
#include "pt_status.h"

#include "mgr_config.h"
#include "config_data_dnp.h"
#include "config_helpers.h"

#include "desolvers.h"

#include "Attribute.h"
#include "std_helper.h"

namespace Cti {
namespace Devices {

/**
 * This function will iterate over the <code>points</code>
 * vector, searching for the two firmware points. It will set
 * aside the first major or minor firmware revision points it
 * comes across until it finds its complement point and, as
 * soon as it finds the second point, it combines the
 * information from the major and minor revisions into a new
 * CtiPointDataMsg object and pushes it
 * onto the <code>points</code> vector and returns.
 *
 * If a major and minor point aren't both encountered in the
 * iteration, this function will effectively do nothing and the
 * vector of messages will remain untouched. If the function
 * does find both a major and minor, the size of the vector will
 * increase by one and all major and minor revision messages
 * previously in the vector will remain there untouched.
 */
void Cbc8020Device::combineFirmwarePoints( Protocols::Interface::pointlist_t &points, const FirmwareRevisionOffsets firmwareOffsets )
{
    CtiPointDataMsg *major = nullptr, *minor = nullptr;

    // We need to check if the firmware analog points are present, then consolidate
    // them into single points for processing.
    for( auto pt_msg : points )
    {
        if( pt_msg && pt_msg->getType() == AnalogPointType )
        {
            if( pt_msg->getId() == firmwareOffsets.major )
            {
                major = pt_msg;
            }
            if( pt_msg->getId() == firmwareOffsets.minor )
            {
                minor = pt_msg;
            }
        }

        if( major && minor )
        {
            /**
             * We've encountered both a major and minor revision message.
             * Use them to create the single revision data point message and
             * store it.
             */

            /*
                incoming data:
                    'major' : 8-bit value -- upper 4-bits == major_version
                                             lower 4-bits == minor_version
                    'minor' : 8-bit value -- revision

                outgoing data format:
                    a SIXBIT (http://nemesis.lonestar.org/reference/telecom/codes/sixbit.html)
                    encoded string packed inside a long long.  The point data message holds a
                    double which limits us to 8 (6-bit) encoded characters inside the 52-bit mantissa.
                    The string format is "major_version.minor_version.revision".  A string that is
                    too long will be truncated and the last character replaced by '#' to
                    denote that condition. eg: "10.11.123" --> "10.11.1#".
            */

            int major_minor = static_cast<int>( major->getValue() );
            int revision    = static_cast<int>( minor->getValue() );

            char buffer[16];

            int messageLength = _snprintf_s( buffer, 16, 15, "%d.%d.%d",
                                             ( major_minor >> 4 ) & 0x0f,
                                             major_minor & 0x0f,
                                             revision & 0x0ff );

            if ( messageLength > 8 )
            {
                buffer[7] = '#';
                buffer[8] = 0;

                messageLength = 8;
            }

            long long encodedValue = 0;

            for ( int i = 0; i < messageLength; ++i )
            {
                encodedValue <<= 6;
                encodedValue |= ( ( buffer[i] - ' ' ) & 0x03f );
            }

            CtiPointDataMsg *pt_msg = new CtiPointDataMsg(static_cast<long>( PointOffsets::FirmwareRevision ),
                                                          static_cast<double>( encodedValue ),
                                                          NormalQuality,
                                                          AnalogPointType);
            points.push_back(pt_msg);

            return;
        }
    }
}

void Cbc8020Device::refreshAttributeOverrides()
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

            static const std::map<std::string, PointOffsets> attributeOffsets {
                { Attribute::FirmwareVersionMajor.getName(), PointOffsets::FirmwareRevisionMajor },
                { Attribute::FirmwareVersionMinor.getName(), PointOffsets::FirmwareRevisionMinor }};

            if( auto offset = mapFind(controlAttributeOffsets, entry.attributeName) )
            {
                _controlOffsetOverrides.emplace(*offset, entry.pointName);
            }
            else if( auto offset = mapFind(attributeOffsets, entry.attributeName) )
            {
                _pointOffsetOverrides.emplace(*offset, entry.pointName);
            }
        }
    }
}

long Cbc8020Device::getPointOffset(const PointOffsets defaultOffset)
{
    if( auto optName = mapFindRef(_pointOffsetOverrides, defaultOffset) )
    {
        const auto& overridePointName = *optName;

        if( auto pt = getDevicePointByName(overridePointName) )
        {
            if( ! pt->isNumeric() )
            {
                CTILOG_WARN(dout, "Override point not Numeric type" <<
                    FormattedList::of(
                        "Override point name", overridePointName,
                        "Override point ID", pt->getPointID(),
                        "Override point type", desolvePointType(pt->getType()),
                        "Default offset", static_cast<long>(defaultOffset)));
            }

            if( pt->getPointOffset() > 0 )
            {
                return pt->getPointOffset();
            }
            else
            {
                CTILOG_ERROR(dout, "Override point offset not valid" <<
                    FormattedList::of(
                        "Override point name",   overridePointName,
                        "Override point ID",     pt->getPointID(),
                        "Override point offset", pt->getPointOffset(),
                        "Default offset", static_cast<long>(defaultOffset)));
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Override point not found" <<
                FormattedList::of(
                    "Override point name", overridePointName,
                    "Default offset", static_cast<long>(defaultOffset)));
        }
    }

    return static_cast<long>(defaultOffset);
}

void Cbc8020Device::processPoints( Protocols::Interface::pointlist_t &points )
{
    refreshAttributeOverrides();

    const long pointOffsetFirmwareMajor = getPointOffset(PointOffsets::FirmwareRevisionMajor);
    const long pointOffsetFirmwareMinor = getPointOffset(PointOffsets::FirmwareRevisionMinor);

    combineFirmwarePoints(points, { pointOffsetFirmwareMajor, pointOffsetFirmwareMinor });

    //  do the final processing
    DnpDevice::processPoints(points);
}

long Cbc8020Device::getControlOffset(const ControlOffsets defaultControlOffset)
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
                                "Device ID", getID(),
                                "Device name", getName(),
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
                            "Device ID", getID(),
                            "Device name", getName(),
                            "Override point name", overridePointName,
                            "Override point ID", pt->getPointID(),
                            "Default control offset", static_cast<long>(defaultControlOffset)));
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Control offset override point not Status type" <<
                    FormattedList::of(
                        "Device ID", getID(),
                        "Device name", getName(),
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
                    "Device ID", getID(),
                    "Device name", getName(),
                    "Override point name", overridePointName,
                    "Default control offset", static_cast<long>(defaultControlOffset)));
        }
    }

    return static_cast<long>(defaultControlOffset);
}


YukonError_t Cbc8020Device::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
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
