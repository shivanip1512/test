#include "precompiled.h"

#include "attribute_mapping.h"
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

AttributeMapping::AttributeMapping(PointSource pointSource)
    :   _pointSource(pointSource)
{
}

void AttributeMapping::refresh(Config::DeviceConfigSPtr deviceConfig, const AttributeList& mappableAttributes)
{
    if( deviceConfig && deviceConfig->getInstanceId() == _lastConfigId )
    {
        return;
    }

    _overrides.clear();

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
        static const std::map<std::string, Attribute> overrideNames = vectorToMap(mappableAttributes, &Attribute::getName);

        for( const auto & entry : std::vector<AAPN> { indexed->begin(), indexed->end() } )
        {
            if( auto offset = mapFind(overrideNames, entry.attributeName) )
            {
                _overrides.emplace(*offset, entry.pointName);
            }
        }
    }
}


auto AttributeMapping::findControlOffset(const Attribute attribute) -> OptionalInt
{
    auto pcox = tryGetControlOffset(attribute);

    if( pcox.which() )
    {
        return boost::strict_get<int>(pcox);
    }

    CTILOG_DEBUG(dout, boost::strict_get<std::string>(pcox));

    return boost::none;
}

int AttributeMapping::getControlOffset(const Attribute attribute)
{
    auto pcox = tryGetControlOffset(attribute);

    if( pcox.which() )
    {
        return boost::strict_get<int>(pcox);
    }

    throw YukonErrorException{ ClientErrors::NoConfigData, boost::strict_get<std::string>(pcox) };
}

auto AttributeMapping::tryGetControlOffset(const Attribute attribute) -> IntExpected
{
    auto optName = mapFindRef(_overrides, attribute);

    if( ! optName || optName->empty() )
    {
        return "No control offset name" 
                + FormattedList::of(
                    "Attribute", attribute);
    }

    const auto& overridePointName = *optName;

    const auto pt = _pointSource(overridePointName);

    if( ! pt )
    {
        return "Override point not found" 
                + FormattedList::of(
                    "Override point name", overridePointName,
                    "Attribute", attribute);
    }

    if( ! pt->isStatus() )
    {
        return "Control offset override point not Status type"
            + FormattedList::of(
                "Override point name", overridePointName,
                "Override point ID", pt->getPointID(),
                "Override device ID", pt->getDeviceID(),
                "Override point type", desolvePointType(pt->getType()),
                "Attribute", attribute);
    }

    CtiPointStatusSPtr statusPt = boost::static_pointer_cast<CtiPointStatus>(pt);

    const auto control = statusPt->getControlParameters();

    if( ! control )
    {
        return "Control offset override point does not have control parameters"
            + FormattedList::of(
                "Override point name", overridePointName,
                "Override point ID", pt->getPointID(),
                "Override device ID", pt->getDeviceID(),
                "Attribute", attribute);
    }

    if( control->getControlOffset() <= 0 )
    {
        return "Control offset override not valid"
            + FormattedList::of(
                "Override point name", overridePointName,
                "Override point ID", pt->getPointID(),
                "Override device ID", pt->getDeviceID(),
                "Override control offset", control->getControlOffset(),
                "Attribute", attribute);
    }

    return control->getControlOffset();
}


auto AttributeMapping::findPointOffset(const Attribute attribute) -> OptionalInt
{
    auto pcox = tryGetPointOffset(attribute);

    if( pcox.which() )
    {
        return boost::strict_get<int>(pcox);
    }

    CTILOG_WARN(dout, boost::strict_get<std::string>(pcox));

    return boost::none;
}

auto AttributeMapping::getPointOffset(const Attribute attribute) -> int
{
    auto pcox = tryGetPointOffset(attribute);

    if( pcox.which() )
    {
        return boost::strict_get<int>(pcox);
    }

    throw YukonErrorException{ ClientErrors::NoConfigData, boost::strict_get<std::string>(pcox) };
}

auto AttributeMapping::tryGetPointOffset(const Attribute attribute) -> IntExpected
{
    auto optName = mapFindRef(_overrides, attribute);

    if( ! optName || optName->empty() )
    {
        return "No control offset name"
            + FormattedList::of(
                "Attribute", attribute);
    }

    const auto& overridePointName = *optName;

    const auto pt = _pointSource(overridePointName);

    if( ! pt )
    {
        return "Override point not found" 
            + FormattedList::of(
                "Override point name", overridePointName,
                "Attribute", attribute);
    }

    if( ! pt->isNumeric() )
    {
        CTILOG_WARN(dout, "Override point not Numeric type" <<
            FormattedList::of(
                "Override point name", overridePointName,
                "Override point ID", pt->getPointID(),
                "Override point type", desolvePointType(pt->getType()),
                "Attribute", attribute));
    }

    if( pt->getPointOffset() <= 0 )
    {
        return "Override point offset not valid" 
            + FormattedList::of(
                "Override point name", overridePointName,
                "Override point ID", pt->getPointID(),
                "Override point offset", pt->getPointOffset(),
                "Attribute", attribute);
    }

    return pt->getPointOffset();
}

}
}