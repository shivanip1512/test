#pragma once

#include <vector>
#include <memory>

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator{

template<class T>
class BehaviorCollection
{
    std::vector<std::unique_ptr<T>> _behaviors;

public:
    typedef typename T::target_type Type;

    void processMessage(Type &value, Logger &logger)
    {
        for( const auto &behavior : _behaviors )
        {
            behavior->apply(value, logger);
        }
    }
    void push_back(std::unique_ptr<T> &&behavior)
    {
        if( behavior )
        {
            _behaviors.push_back(std::move(behavior));
        }
    }
};

}
}
