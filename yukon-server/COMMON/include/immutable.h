#pragma once

#include "boost/shared_ptr.hpp"
#include "readers_writer_lock.h"

namespace Cti {

template <class T>
class Immutable
{
    typedef boost::shared_ptr<const T> PointerType;
    
    PointerType _ptr;
    
    typedef readers_writer_lock_t::reader_lock_guard_t  ReaderGuard;
    typedef readers_writer_lock_t::writer_lock_guard_t  WriterGuard;

    mutable readers_writer_lock_t _mux;

public:
    Immutable()
    {}

    Immutable(T* obj) :
        _ptr(obj)
    {}

    Immutable(const T& obj) :
        _ptr(new T(obj))
    {}

    Immutable(const Immutable<T>& other) :
        _ptr(other.get())
    {}

    void reset()
    {
        WriterGuard guard(_mux);
        _ptr.reset();
    }

    void reset(T* obj)
    {
        WriterGuard guard(_mux);
        _ptr.reset(obj);
    }

    PointerType get() const
    {
        ReaderGuard guard(_mux);
        return _ptr;
    }

    Immutable& operator=(const T& rhs)
    {
        this->reset(new T(rhs));
        return *this;
    }

    Immutable& operator=(const Immutable<T>& other)
    {
        PointerType &other_ptr = other.get();

        {
            WriterGuard guard(_mux);
            _ptr = other_ptr;
        }

        return *this;
    }
    
    void swap(Immutable<T>& other)
    {
        // check the pointers addresses to make sure we always
        // acquire locks in the same order and avoid deadlocks
        if(this < &other)
        {
            WriterGuard guard1(_mux);
            WriterGuard guard2(other._mux);
            _ptr.swap(other._ptr);
        }
        else
        {
            WriterGuard guard1(other._mux);
            WriterGuard guard2(_mux);
            _ptr.swap(other._ptr);
        }
    }
};

} // namespace Cti

namespace std {

template <class T>
void swap(Cti::Immutable<T>& rhs, Cti::Immutable<T>& lhs)
{
    rhs.swap(lhs);
}
    
} // namespace std
