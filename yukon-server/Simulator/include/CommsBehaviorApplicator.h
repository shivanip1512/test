#pragma once

#include "CommsBehaviorInterface.h"
#include "boost/noncopyable.hpp"

#include <vector>

namespace Cti {
namespace Simulator{

class CommsBehaviorApplicator : boost::noncopyable
{
public:
    CommsBehaviorApplicator();
    error_t ProcessMessage(bytes &message);
    error_t setBehavior(CommsBehavior* behavior);

private:
    std::vector<CommsBehavior*> _behaviors;
};

}
}
