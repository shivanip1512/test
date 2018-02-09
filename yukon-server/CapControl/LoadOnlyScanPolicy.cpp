#include "precompiled.h"

#include "LoadOnlyScanPolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList LoadOnlyScanPolicy::getSupportedAttributes() const
{
    return
    {
        Attribute::Voltage
    };
}

}
}

