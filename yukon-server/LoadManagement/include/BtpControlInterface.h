#pragma once
#include <boost/shared_ptr.hpp>

namespace Cti {
namespace LoadManagement {

class BtpControlInterface
{
public:
    virtual bool sendBtpControl(int tier, int timeout) = 0;
};

typedef boost::shared_ptr<BtpControlInterface> BtpControlInterfacePtr;

}
}
