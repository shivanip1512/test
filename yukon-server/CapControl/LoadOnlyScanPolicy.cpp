#include "precompiled.h"

#include "LoadOnlyScanPolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList LoadOnlyScanPolicy::getSupportedAttributes()
{
    return
    {
        PointAttribute::VoltageY
    };
}

}
}

