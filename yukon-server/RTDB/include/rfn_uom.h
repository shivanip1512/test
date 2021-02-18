#pragma once

#include "dlldefs.h"

#include <array>
#include <set>
#include <string>

namespace Cti::Devices::Rfn {

class IM_EX_DEVDB UnitOfMeasure
{
    union {
        unsigned char raw;
        struct {
            unsigned char uom : 7;
            unsigned char ext : 1;
        };
    } _uom;

public:

    UnitOfMeasure() = default;
    explicit UnitOfMeasure(unsigned char raw);

    bool getExtensionBit() const;
    std::string getName() const;
    bool isTime() const;

    enum {
        Reserved = 0x00,
        WattHour = 0x01,
        VarHour = 0x02,
        QHour = 0x03,
        VaHour = 0x04,
        Time = 0x05,
        Volts = 0x10,
        Current = 0x11,
        VoltageAngle = 0x12,
        CurrentAngle = 0x13,
        PowerFactorAngle = 0x16,
        PowerFactor = 0x18,
        PoundHour = 0x20,
        Gallon = 0x21,
        Pulse = 0x3f,
        NoUnits = 0x40,
        Watt = 0x41,
        Var = 0x42,
        Q = 0x43,
        Va = 0x44,
        OutageCount = 0x50,
        RestoreCount = 0x51,
        OutageBlinkCount = 0x52,
        RestoreBlinkCount = 0x53,
        Unknown = 0x7f
    };
};

class IM_EX_DEVDB UomModifier1
{
    union {
        unsigned short raw;
        struct {
            unsigned short primary_secondary    : 2;
            unsigned short phase                : 2;
            unsigned short quadrant             : 4;
            unsigned short fundamental_harmonic : 2;
            unsigned short additional_rates     : 2;
            unsigned short range                : 3;
            unsigned short extension_bit        : 1;
        };
    } _mod1;

public:

    UomModifier1(unsigned short uomm1);
    
    static UomModifier1 of(unsigned char hi, unsigned char lo);

    bool getExtensionBit() const;
    std::set<std::string> getModifierStrings() const;
};

class IM_EX_DEVDB UomModifier2
{
    union {
        unsigned short raw;
        struct {
            unsigned short segmentation          : 3;
            unsigned short rate                  : 3;
            unsigned short scaling_factor        : 3;
            unsigned short coincident_value      : 3;
            unsigned short cumulative            : 1;
            unsigned short continuous_cumulative : 1;
            unsigned short previous              : 1;
            unsigned short extension_bit         : 1;
        };
    } _mod2;

public:

    UomModifier2(unsigned short uomm2);
    
    static UomModifier2 of(unsigned char hi, unsigned char lo);

    bool getExtensionBit() const;

    unsigned getCoincidentOffset() const;

    double getScalingFactor() const;

    std::set<std::string> getModifierStrings() const;
};

}
