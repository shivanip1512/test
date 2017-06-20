#pragma once

#include "Policy.h"


namespace Cti           {
namespace CapControl    {

struct ScanPolicy : Policy
{
    virtual Actions IntegrityScan();

protected:

    Action makeIntegrityScanCommand( const Attribute & attribute );
};

}
}

