#include "precompiled.h"

#include "LoadOnlyScanPolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList LoadOnlyScanPolicy::getRequiredAttributes() const
{
    return
    {
        Attribute::Voltage
    };
}

}
}

