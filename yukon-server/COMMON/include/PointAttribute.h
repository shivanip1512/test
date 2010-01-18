#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "dlldefs.h"

#include <string>

class IM_EX_CTIBASE PointAttribute
{
    private:
        enum Attribute
        {
            LowerTapAttribute,
            RaiseTapAttribute,
            LtcVoltageAttribute,
            ControlModeAttribute,
            UpperVoltLimitAttribute,
            LowerVoltLimitAttribute,
            KeepAliveAttribute
        };

    public:

        static const PointAttribute LowerTap;
        static const PointAttribute RaiseTap;
        static const PointAttribute LtcVoltage;
        static const PointAttribute ControlMode;
        static const PointAttribute UpperVoltLimit;
        static const PointAttribute LowerVoltLimit;
        static const PointAttribute KeepAlive;

        std::string name() const;
        std::string getDescription() const;
        int value() const;

    private:

        /**
         * This should never be called in code. Attributes will be built
         * up statically
         */
        PointAttribute(Attribute value, std::string name, std::string description);

        std::string _name;
        std::string _description;
        int _value;
};
