#pragma once

namespace Cti {
namespace Messaging {
namespace Rfn {

struct /*IM_EX_MSG*/ E2eMsg  //  no methods, does not need to be exported
{
    enum Protocol
    {
        Application = 0x00,
        Network     = 0x01,
        Link        = 0x02,
        //  ...
    };

    virtual ~E2eMsg() {};
};

}
}
}
