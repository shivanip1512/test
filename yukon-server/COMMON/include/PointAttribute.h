#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "dlldefs.h"

#include <string>
#include <map>

class IM_EX_CTIBASE PointAttribute
{
    public:
        enum Attribute
        {
            UnknownAttribute,
            TapDownAttribute,
            TapUpAttribute,
            VoltageAttribute,
            AutoRemoteControlAttribute,
            TapPositionAttribute,
            KeepAliveAttribute
        };

        static const PointAttribute Unknown;
        static const PointAttribute TapDown;
        static const PointAttribute TapUp;
        static const PointAttribute Voltage;
        static const PointAttribute AutoRemoteControl;
        static const PointAttribute TapPosition;
        static const PointAttribute KeepAlive;

        std::string name() const;
        Attribute value() const;

        static const PointAttribute& valueOf(const std::string& name);

        const bool operator == (const PointAttribute& rhs) const;
        const bool operator <  (const PointAttribute& rhs) const;

    private:

        /**
         * This should never be called in code. Attributes will be built
         * up statically
         */
        PointAttribute(Attribute value, const std::string& name);

        Attribute _value;
        std::string _name;

        typedef std::map<std::string,PointAttribute*> AttributeMap;
        static AttributeMap nameToAttributeMap;
};
