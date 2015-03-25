#include "precompiled.h"

#include "LoadOnlyScanPolicy.h"


namespace Cti           {
namespace CapControl    {

LoadOnlyScanPolicy::LoadOnlyScanPolicy()
{
    _supportedAttributes = AttributeList
    {
        PointAttribute::VoltageY
    };
}

}
}

