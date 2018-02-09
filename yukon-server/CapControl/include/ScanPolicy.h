#pragma once

#include "Policy.h"


namespace Cti           {
namespace CapControl    {

struct ScanPolicy : Policy
{
    virtual Actions IntegrityScan();

protected:

    AttributeList getSupportedAttributes() const override final;

    virtual AttributeList getRequiredAttributes() const = 0;
    virtual AttributeList getOptionalAttributes() const;

    Action makeIntegrityScanCommand( const LitePoint & point );
};

}
}

