#pragma once

namespace Cti {
namespace Messaging {


class IM_EX_MSG BaseConnection
{
protected:

    BaseConnection();

public:

    virtual ~BaseConnection();

    virtual void close() = 0;

    static void closeAll();
};


struct AutoCloseAllConnections
{
    ~AutoCloseAllConnections()
    {
        BaseConnection::closeAll();
    }
};


}
}
