#pragma once

#include <set>
#include <mutex>

namespace Cti {

template<typename T>
class ConcurrentSet
{
    std::set<T> _set;
    std::mutex _mux;

public:

    void insert(T&& t)
    {
        std::lock_guard<std::mutex> lg(_mux);

        _set.emplace(std::move(t));
    }

    bool contains(const T& t)
    {
        std::lock_guard<std::mutex> lg(_mux);

        return _set.count(t);
    }

    void erase(const T& t)
    {
        std::lock_guard<std::mutex> lg(_mux);

        _set.erase(t);
    }
};

}