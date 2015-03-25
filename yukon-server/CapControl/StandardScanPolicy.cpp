#include "precompiled.h"

#include "StandardScanPolicy.h"


namespace Cti           {
namespace CapControl    {

StandardScanPolicy::StandardScanPolicy()
{
    _supportedAttributes = AttributeList
    {
        PointAttribute::VoltageX,
        PointAttribute::VoltageY
    };
}

}
}

