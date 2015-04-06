#include "precompiled.h"

#include "StandardScanPolicy.h"


namespace Cti           {
namespace CapControl    {

StandardScanPolicy::StandardScanPolicy()
{
}

Policy::AttributeList StandardScanPolicy::getSupportedAttributes()
{
    return
    {
        PointAttribute::VoltageX,
        PointAttribute::VoltageY
    };
}

}
}

