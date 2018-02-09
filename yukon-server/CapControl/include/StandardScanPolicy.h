#pragma once

#include "yukon.h"

#include "ScanPolicy.h"


namespace Cti           {
namespace CapControl    {

class StandardScanPolicy : public ScanPolicy
{
    AttributeList getRequiredAttributes() const override;
    AttributeList getOptionalAttributes() const override;
};

}
}

