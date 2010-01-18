#include "yukon.h"

#include "PointAttribute.h"

const PointAttribute PointAttribute::LowerTap = PointAttribute(PointAttribute::LowerTapAttribute,"LowerTap","Lower Tap Position");
const PointAttribute PointAttribute::RaiseTap = PointAttribute(PointAttribute::RaiseTapAttribute,"RaiseTap","Raise Tap Position");
const PointAttribute PointAttribute::LtcVoltage = PointAttribute(PointAttribute::LtcVoltageAttribute,"LtcVoltage","LTC Voltage");
const PointAttribute PointAttribute::ControlMode = PointAttribute(PointAttribute::ControlModeAttribute,"ControlMode","Auto/Remote Control");
const PointAttribute PointAttribute::UpperVoltLimit = PointAttribute(PointAttribute::UpperVoltLimitAttribute,"UpperVoltLimit","Upper Volt Limit");
const PointAttribute PointAttribute::LowerVoltLimit = PointAttribute(PointAttribute::LowerVoltLimitAttribute,"LowerVoltLimit","Lower Volt Limit");
const PointAttribute PointAttribute::KeepAlive = PointAttribute(PointAttribute::KeepAliveAttribute,"KeepAlive","Keep Alive");

std::string PointAttribute::name() const
{
    return _name;
}

std::string PointAttribute::getDescription() const
{
    return _description;
}

int PointAttribute::value() const
{
    return (int)_value;
}

PointAttribute::PointAttribute(Attribute value, std::string name, std::string description)
{
    _name = name;
    _description = description;
    _value = value;
}

