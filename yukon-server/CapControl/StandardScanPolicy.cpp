#include "precompiled.h"

#include "StandardScanPolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList StandardScanPolicy::getRequiredAttributes() const
{
    return
    {
        Attribute::Voltage
    };
}

Policy::AttributeList StandardScanPolicy::getOptionalAttributes() const
{
    return
    {
        Attribute::SourceVoltage
    };
}

}
}

