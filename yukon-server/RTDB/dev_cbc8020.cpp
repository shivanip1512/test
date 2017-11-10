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

            enum class PointOffsets : long
            {
                FirmwareRevision = 9999
            };

            CtiPointDataMsg *pt_msg = new CtiPointDataMsg(static_cast<long>( PointOffsets::FirmwareRevision ),
                                                          static_cast<double>( encodedValue ),
                                                          NormalQuality,
                                                          AnalogPointType);
            points.push_back(pt_msg);

            return;
        }
    }
}

auto Cbc8020Device::getMappableAttributes() const -> AttributeMapping::AttributeList
{
    auto list = Inherited::getMappableAttributes();

    list.emplace_back(Attribute::FirmwareVersionMajor);
    list.emplace_back(Attribute::FirmwareVersionMinor);

    return list;
}

int Cbc8020Device::getDnpPointOffset(const Attribute attribute)
{
    static const std::map<Attribute, int> AttributeOffsets{
        { Attribute::FirmwareVersionMajor, 3 },
        { Attribute::FirmwareVersionMinor, 4 },
    };

    if( auto mappedOffset = _attributeMapping.findPointOffset(attribute) )
    {
        return *mappedOffset;
    }

    if( auto defaultOffset = mapFind(AttributeOffsets, attribute) )
    {
        return *defaultOffset;
    }

    throw YukonErrorException(ClientErrors::NoConfigData, "Point offset not found for attribute " + attribute.getName());
}

void Cbc8020Device::processPoints( Protocols::Interface::pointlist_t &points )
{
    _attributeMapping.refresh(getDeviceConfig(), getMappableAttributes());

    const int pointOffsetFirmwareMajor = getDnpPointOffset(Attribute::FirmwareVersionMajor);
    const int pointOffsetFirmwareMinor = getDnpPointOffset(Attribute::FirmwareVersionMinor);

    combineFirmwarePoints(points, { pointOffsetFirmwareMajor, pointOffsetFirmwareMinor });

    //  do the final processing
    Inherited::processPoints(points);
}

int Cbc8020Device::getDnpControlOffset(const Attribute attribute)
{
    static const std::map<Attribute, int> AttributeOffsets{
        { Attribute::EnableOvuvControl,        14 },
        { Attribute::EnableVarControl,         15 },
        { Attribute::EnableTemperatureControl, 16 },
        { Attribute::EnableTimeControl,        23 },
    };

    //  Only handle attributes for which we have an explicit default
    if( auto defaultOffset = mapFind(AttributeOffsets, attribute) )
    {
        if( auto mappedOffset = _attributeMapping.findControlOffset(attribute) )
        {
            return *mappedOffset;
        }

        return *defaultOffset;
    }

    //  Otherwise pass it up to the parent
    return Inherited::getDnpControlOffset(attribute);
}

}
}
