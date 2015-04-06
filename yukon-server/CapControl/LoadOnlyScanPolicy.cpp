#include "precompiled.h"

#include "LoadOnlyScanPolicy.h"


namespace Cti           {
namespace CapControl    {

LoadOnlyScanPolicy::LoadOnlyScanPolicy()
{
}

Policy::AttributeList LoadOnlyScanPolicy::getSupportedAttributes()
{
    return
    {
        PointAttribute::VoltageY
    };
}

}
}

