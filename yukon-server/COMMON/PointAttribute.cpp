#include "yukon.h"

#include "PointAttribute.h"

PointAttribute::AttributeMap PointAttribute::nameToAttributeMap = PointAttribute::AttributeMap();

const PointAttribute PointAttribute::Unknown = PointAttribute(PointAttribute::UnknownAttribute,"UNKNOWN");
const PointAttribute PointAttribute::LowerTap = PointAttribute(PointAttribute::LowerTapAttribute,"LOWER_TAP");
const PointAttribute PointAttribute::RaiseTap = PointAttribute(PointAttribute::RaiseTapAttribute,"RAISE_TAP");
const PointAttribute PointAttribute::LtcVoltage = PointAttribute(PointAttribute::LtcVoltageAttribute,"LTC_VOLTAGE");
const PointAttribute PointAttribute::AutoRemoteControl = PointAttribute(PointAttribute::AutoRemoteControlAttribute,"AUTO_REMOTE_CONTROL");
const PointAttribute PointAttribute::UpperVoltLimit = PointAttribute(PointAttribute::UpperVoltLimitAttribute,"UPPER_VOLT_LIMIT");
const PointAttribute PointAttribute::LowerVoltLimit = PointAttribute(PointAttribute::LowerVoltLimitAttribute,"LOWER_VOLT_LIMIT");
const PointAttribute PointAttribute::KeepAlive = PointAttribute(PointAttribute::KeepAliveAttribute,"KEEP_ALIVE");


const PointAttribute& PointAttribute::valueOf(const std::string& name)
{
    AttributeMap::iterator itr = nameToAttributeMap.find(name);

    if (itr == nameToAttributeMap.end())
    {
        return Unknown;
    }

    return *(itr->second);
}

std::string PointAttribute::name() const
{
    return _name;
}

int PointAttribute::value() const
{
    return (int)_value;
}

PointAttribute::PointAttribute(Attribute value, const std::string& name)
{
    _name = name;
    _value = value;

    nameToAttributeMap[name] = this;
}

const bool PointAttribute::operator == (const PointAttribute& rhs) const
{
    return _value == rhs._value;
}
