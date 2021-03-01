#include "precompiled.h"

#include "rfn_uom.h"
#include "std_helper.h"

namespace {

template <size_t N>
static void addValue(std::set<std::string>& strings, size_t index, const std::array<const char*, N>& options)
{
    //  0 is always "not specified", meaning no string
    if( index && index <= options.size() && options[index - 1] )
    {
        strings.insert(options[index - 1]);
    }
}
template <size_t N>
static void addBitmaskedValues(std::set<std::string>& strings, size_t bitmask, const std::array<const char*, N>& options)
{
    for( auto i = 0; i < options.size(); ++i )
    {
        if( (bitmask & (1 << i)) && options[i] )
        {
            strings.insert(options[i]);
        }
    }
}

}

namespace Cti::Devices::Rfn {

UnitOfMeasure::UnitOfMeasure(unsigned char raw) :
    _uom { raw }
{}

bool UnitOfMeasure::getExtensionBit() const
{
    return _uom.ext;
}

bool UnitOfMeasure::isTime() const
{
    return _uom.uom == Time;
}

std::string UnitOfMeasure::getName() const
{
    static const std::map<unsigned char, std::string> UomStrings{
        //  Taken from Network Manager's DB tables at
        //      \ekadb\build\common\data\UOM.dat
        { 1, "Wh" },
        { 2, "Varh" },
        { 3, "Qh" },
        { 4, "VAh" },
        { 5, "s" },
        { 6, "SID" },
        { 7, "PID" },
        { 8, "Credit" },
        { 16, "V" },
        { 17, "A" },
        { 18, "V degree" },
        { 19, "A degree" },
        { 20, "V" },
        { 21, "A" },
        { 22, "PF degree" },
        { 24, "PF" },
        { 33, "gal" },
        { 34, "ft^3" },
        { 35, "m^3" },
        { 62, "Status" },
        { 63, "Pulse" },
        { 64, " " },
        { 65, "W" },
        { 66, "Var" },
        { 67, "Q" },
        { 68, "VA" },
        { 80, "Outage Count" },
        { 81, "Restore Count" },
        { 82, "Outage Blink Count" },
        { 83, "Restore Blink Count" },
        { 84, "deg C" },
        { 127, "-" },
    };
    return mapFindOrDefault(UomStrings, _uom.uom, "Unmapped UOM " + std::to_string(_uom.uom));
}

UomModifier1::UomModifier1(unsigned short uomm1) :
    _mod1{ uomm1 }
{
}

UomModifier1 UomModifier1::of(unsigned char hi, unsigned char lo)
{
    return static_cast<unsigned short>( (hi << 8) | lo );
}

bool UomModifier1::getExtensionBit() const
{
    return _mod1.extension_bit;
}

std::set<std::string> UomModifier1::getModifierStrings() const 
{
    //  Taken from Network Manager's DB tables at
    //      \ekadb\build\common\data\UOM_MODIFIER.dat

    std::set<std::string> modifierStrings;

    addValue<2>(modifierStrings, 
        _mod1.primary_secondary, { 
            "Primary", 
            "Secondary" });
    addValue<3>(modifierStrings, 
        _mod1.phase, { 
            "Phase A", 
            "Phase B", 
            "Phase C" });
    addBitmaskedValues<4>(modifierStrings, 
        _mod1.quadrant, { 
            "Quadrant 1", 
            "Quadrant 2", 
            "Quadrant 3", 
            "Quadrant 4" });
    addValue<2>(modifierStrings, 
        _mod1.fundamental_harmonic, { 
            "Fundamental", 
            "Harmonic" });
    addValue<2>(modifierStrings, 
        _mod1.additional_rates, { 
            "TOU Rate H", 
            "TOU Rate I" });
    addValue<7>(modifierStrings, 
        _mod1.range, { 
            "Net Flow", 
            "Min",
            "Avg",
            "Max",
            "Max Net Flow",
            "Daily Max",
            "Daily Min" });

    return modifierStrings;
}


UomModifier2::UomModifier2(unsigned short uomm2) :
    _mod2{ uomm2 }
{
}

UomModifier2 UomModifier2::of(unsigned char hi, unsigned char lo)
{
    return static_cast<unsigned short>( (hi << 8) | lo );
}

bool UomModifier2::getExtensionBit() const
{
    return _mod2.extension_bit;
}

unsigned UomModifier2::getCoincidentOffset() const
{
    return _mod2.coincident_value;
}

double UomModifier2::getScalingFactor() const
{
    switch( _mod2.scaling_factor )
    {
        default:
        case 0:  return 1;    //  10^0
        case 1:  return 1e3;  //  10^3
        case 2:  return 1e6;  //  mega, 10^6
        case 3:  return 1e9;  //  giga, 10^9
        case 4:  return 0;    //  Overflow
        case 5:  return 1e-1; //  0.1, no longer nano (10^-9)
        case 6:  return 1e-6; //  10^-6
        case 7:  return 1e-3; //  10^-3
    }
}

std::set<std::string> UomModifier2::getModifierStrings() const
{
    //  Taken from Network Manager's DB tables at
    //      \ekadb\build\common\data\UOM_MODIFIER.dat

    std::set<std::string> modifierStrings;

    addValue<7>(modifierStrings,
        _mod2.segmentation, {
            "Phase A->B",
            "Phase B->C",
            "Phase C->A",
            "Phase Neutral->Ground",
            "Phase A->Neutral",
            "Phase B->Neutral",
            "Phase C->Neutral" });
    addValue<7>(modifierStrings,
        _mod2.rate, {
            "TOU Rate A",
            "TOU Rate B",
            "TOU Rate C",
            "TOU Rate D",
            "TOU Rate E",
            "TOU Rate F",
            "TOU Rate G" });
    addValue<7>(modifierStrings,
        _mod2.scaling_factor, {
            "Kilo",
            "Mega",
            "Giga",
            "Error",
            "tenths",
            "micro",
            "milli" });
    //  Do not include the Coincident modifiers.  They are accessed with getCoincidentOffset().
    /*addValue<7>(modifierStrings,
        _mod2.coincident_value, {
            "Coincident Value 1",
            "Coincident Value 2",
            "Coincident Value 3",
            "Coincident Value 4",
            "Coincident Value 5",
            "Coincident Value 6",
            "Coincident Value 7" });*/
    addValue<1>(modifierStrings,
        _mod2.cumulative, {
            "Cumulative" });
    addValue<1>(modifierStrings,
        _mod2.continuous_cumulative, {
            "Continuous Cumulative" });
    addValue<1>(modifierStrings,
        _mod2.previous, {
            "Previous" });

    return modifierStrings;
}

}