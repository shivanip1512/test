#pragma once

#include "CommsBehavior.h"

#include <boost/noncopyable.hpp>
#include <boost/ptr_container/ptr_vector.hpp>

#include <vector>
#include <memory>

namespace Cti {
namespace Simulator{

template<class T>
class BehaviorCollection : boost::noncopyable
{
private:
    boost::ptr_vector<T> _behaviors;

public:
    typedef typename T::target_type Type;

    BehaviorCollection() {};
    error_t processMessage(Type &value)
    {
        boost::ptr_vector<T>::iterator itr = _behaviors.begin();
        for(;itr != _behaviors.end(); itr++)
        {
            (itr)->applyBehavior(value);
        }

        return error_t::success;
    }
    error_t addBehavior(std::auto_ptr<T> behavior)
    {
        _behaviors.push_back(behavior);
        return error_t::success;
    }
    error_t emptyBehaviors()
    {
        _behaviors.clear();
        return error_t::success;
    }
};

}
}
