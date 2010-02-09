#include "yukon.h"

#include "PointAttribute.h"

PointAttribute::AttributeMap PointAttribute::nameToAttributeMap = PointAttribute::AttributeMap();

const PointAttribute PointAttribute::Unknown = PointAttribute(PointAttribute::UnknownAttribute,"UNKNOWN");
const PointAttribute PointAttribute::TapDown = PointAttribute(PointAttribute::TapDownAttribute,"TAP_DOWN");
const PointAttribute PointAttribute::TapUp = PointAttribute(PointAttribute::TapUpAttribute,"TAP_UP");
const PointAttribute PointAttribute::LtcVoltage = PointAttribute(PointAttribute::LtcVoltageAttribute,"VOLTAGE");
const PointAttribute PointAttribute::AutoRemoteControl = PointAttribute(PointAttribute::AutoRemoteControlAttribute,"AUTO_REMOTE_CONTROL");
const PointAttribute PointAttribute::TapPosition = PointAttribute(PointAttribute::TapPositionAttribute,"TAP_POSITION");
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

PointAttribute::Attribute PointAttribute::value() const
{
    return _value;
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
