#pragma once

#include "readers_writer_lock.h"

namespace Cti {

template <typename T>
class Immutable
{
    T _obj;
    mutable readers_writer_lock_t _lock;

public:
    Immutable()
    {}

    Immutable(const T& obj)
    {
        *this = obj;
    }

    Immutable(const Immutable<T>& other)
    {
        *this = other;
    }

    T get() const
    {
        readers_writer_lock_t::reader_lock_guard_t guard(_lock);
        return _obj;
    }

    operator T() const
    {
        readers_writer_lock_t::reader_lock_guard_t guard(_lock);
        return _obj;
    }

    Immutable& operator=(const T& obj)
    {
        readers_writer_lock_t::writer_lock_guard_t guard(_lock);
        _obj = obj;
        return *this;
    }

    Immutable& operator=(const Immutable<T>& other)
    {
        T& tmp = other.get();
        readers_writer_lock_t::writer_lock_guard_t guard(_lock);
        std::swap(_obj, tmp);
        return *this;
    }

    void swap(T& obj)
    {
        readers_writer_lock_t::writer_lock_guard_t guard(_lock);
        std::swap(_obj, obj);
    }

    // this would probably require to use both locks, for now std::swap() can be use if needed
    // void swap(Immutable<T>& other)
};

} // namespace Cti
