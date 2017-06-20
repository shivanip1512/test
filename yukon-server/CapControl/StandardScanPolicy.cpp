#include "precompiled.h"

#include "StandardScanPolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList StandardScanPolicy::getSupportedAttributes()
{
    return
    {
        Attribute::SourceVoltage,
        Attribute::Voltage
    };
}

}
}

