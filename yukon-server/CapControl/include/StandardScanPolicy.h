#pragma once

#include "yukon.h"

#include "ScanPolicy.h"


namespace Cti           {
namespace CapControl    {

struct StandardScanPolicy : ScanPolicy
{
    AttributeList getSupportedAttributes() const override;
};

}
}

