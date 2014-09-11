#pragma once

#include "atomic.h"
#include "critical_section.h"

#include "boost/shared_ptr.hpp"
#include "boost/scoped_ptr.hpp"
#include "boost/ptr_container/ptr_deque.hpp"

namespace Cti {

template <class T>
class Immutable
{
    typedef boost::shared_ptr<const T> SharedPtr;

    // using Atomic makes this class noncopyable
    Atomic<SharedPtr*> _content;

    mutable Atomic<unsigned>             _readers;
    mutable Atomic<bool>                 _needsCleanup;
    mutable boost::ptr_deque<SharedPtr>  _cleanupList;
    mutable CtiCriticalSection           _mux;

    void tryCleanup() const
    {
        CtiLockGuard<CtiCriticalSection> guard(_mux);

        if( !_readers && _needsCleanup.exchange(false) )
        {
            _cleanupList.clear();
        }
    }

    void reset(SharedPtr* ptr)
    {
        CtiLockGuard<CtiCriticalSection> guard(_mux);

        _cleanupList.push_back(_content.exchange(ptr));
        _needsCleanup = true;

        tryCleanup();
    }

public:

    Immutable() :
        _content(new SharedPtr())
    {}

    Immutable(T* obj) :
        _content(new SharedPtr(obj))
    {}

    Immutable(const T& obj) :
        _content(new SharedPtr(new T(obj)))
    {}

    Immutable(const Immutable<T>& other) :
        _content(new SharedPtr(other.get()))
    {}

    ~Immutable()
    {
        // Transfer ownership
        boost::scoped_ptr<SharedPtr> temp(_content.exchange(NULL));
    }

    void reset()
    {
        reset(new SharedPtr());
    }

    void reset(T* obj)
    {
        reset(new SharedPtr(obj));
    }

    SharedPtr get() const
    {
        ++_readers;

        SharedPtr copy = *_content;

        if( ! --_readers && _needsCleanup )
        {
            tryCleanup();
        }

        return copy;
    }

    Immutable& operator=(const T& rhs)
    {
        reset(new T(rhs));
        return *this;
    }

    Immutable& operator=(const Immutable<T>& other)
    {
        reset(new SharedPtr(other.get()));
    }

    void swap(Immutable<T>& other)
    {
        // check the pointers addresses to make sure we always
        // acquire locks in the same order and avoid deadlocks
        const bool lockThisFirst = (this < &other);

        CtiLockGuard<CtiCriticalSection> guard1(lockThisFirst ? _mux : other._mux);
        CtiLockGuard<CtiCriticalSection> guard2(lockThisFirst ? other._mux : _mux);

        // make copies - the raw pointers from _content should not leave since readers
        // may still be copying while we are swapping
        SharedPtr copy       = *_content;
        SharedPtr other_copy = *other._content;

        // swap/reset the content of both - will try to cleanup afterwards
        reset(new SharedPtr(other_copy));
        other.reset(new SharedPtr(copy));
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
