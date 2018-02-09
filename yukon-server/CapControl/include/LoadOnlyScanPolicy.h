#pragma once

#include "yukon.h"

#include "ScanPolicy.h"


namespace Cti           {
namespace CapControl    {

class LoadOnlyScanPolicy : public ScanPolicy
{
    AttributeList getSupportedAttributes() const override;
};

}
}

