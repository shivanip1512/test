#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "dlldefs.h"

#include <string>
#include <map>

class IM_EX_CTIBASE PointAttribute
{
    private:
        enum Attribute
        {
            UnknownAttribute,
            LowerTapAttribute,
            RaiseTapAttribute,
            LtcVoltageAttribute,
            AutoRemoteControlAttribute,
            UpperVoltLimitAttribute,
            LowerVoltLimitAttribute,
            KeepAliveAttribute
        };

    public:

        static const PointAttribute Unknown;
        static const PointAttribute LowerTap;
        static const PointAttribute RaiseTap;
        static const PointAttribute LtcVoltage;
        static const PointAttribute AutoRemoteControl;
        static const PointAttribute UpperVoltLimit;
        static const PointAttribute LowerVoltLimit;
        static const PointAttribute KeepAlive;

        std::string name() const;
        int value() const;

        static const PointAttribute& valueOf(const std::string& name);

        const bool operator == (const PointAttribute& rhs) const;

    private:

        /**
         * This should never be called in code. Attributes will be built
         * up statically
         */
        PointAttribute(Attribute value, const std::string& name);

        int _value;
        std::string _name;

        typedef std::map<std::string,PointAttribute*> AttributeMap;
        static AttributeMap nameToAttributeMap;
};
