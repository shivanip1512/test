#pragma once

#include <boost/type_traits.hpp>

namespace Cti {

template<class T, typename Defined=void>
struct Atomic;

/**
 * Atomic class specialized for integral types (not bool) that are 32-bits or less
 * Atomic<int>
 * Atomic<short>
 * Atomic<char>
 * Atomic<long>
 * Atomic<unsigned int>
 * Atomic<unsigned short>
 * Atomic<unsigned char>
 * Atomic<unsigned long>
 *
 * Member functions:
 * Atomic()
 * Atomic(T val)
 * operator T() const
 * T exchange(T val)
 * T operator=(T val)
 * T operator&=(T val)
 * T operator|=(T val)
 * T operator^=(T val)
 * T operator+=(T val)
 * T operator-=(T val)
 * T operator++()
 * T operator++(int)
 * T operator--()
 * T operator--(int)
 */
template<class T>
struct Atomic<T, typename boost::enable_if_c<boost::is_integral<T>::value && sizeof(T) <= sizeof(long)>::type> : private boost::noncopyable
{
private:
    long _stored;
    
public:
    Atomic() : _stored(0)
    {}

    Atomic(T val) : _stored(val)
    {}

    operator T() const
    {
        return InterlockedCompareExchange(const_cast<long*>(&_stored), 0, 0);
    }

    T exchange(T val)
    {
        return InterlockedExchange(&_stored, val);
    }

    T operator=(T val)
    {
        InterlockedExchange(&_stored, val);
        return val;
    }

    T operator&=(T val)
    {
        for(;;)
        {
            const long prev = InterlockedCompareExchange(&_stored, 0, 0);
            T newVal = prev;
            newVal &= val;
            if( InterlockedCompareExchange(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    T operator|=(T val)
    {
        for(;;)
        {
            const long prev = InterlockedCompareExchange(&_stored, 0, 0);
            T newVal = prev;
            newVal |= val;
            if( InterlockedCompareExchange(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    T operator^=(T val)
    {
        for(;;)
        {
            const long prev = InterlockedCompareExchange(&_stored, 0, 0);
            T newVal = prev;
            newVal ^= val;
            if( InterlockedCompareExchange(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    T operator+=(T val)
    {
        return (InterlockedExchangeAdd(&_stored, val) + val);
    }

    T operator-=(T val)
    {
        const long negVal = ~static_cast<long>(val) + 1;
        return (InterlockedExchangeAdd(&_stored, negVal) + negVal);
    }

    // pre-increment
    T operator++()
    {
        return InterlockedIncrement(&_stored);
    }

    // pre-decrement
    T operator--()
    {
        return InterlockedDecrement(&_stored);
    }

    // post-increment
    T operator++(int)
    {
        T val = InterlockedIncrement(&_stored);
        return --val;
    }

    // post-decrement
    T operator--(int)
    {
        T val = InterlockedDecrement(&_stored);
        return ++val;
    }
};

/**
 * Atomic class specialized for bool
 * Atomic<bool>
 *
 * Member functions:
 * Atomic()
 * Atomic(bool val)
 * operator bool() const
 * bool exchange(bool val)
 * bool operator=(bool val)
 * bool operator&=(bool val)
 * bool operator|=(bool val)
 * bool operator^=(bool val)
 */
template<>
struct Atomic<bool, typename boost::enable_if_c<true>::type> : private boost::noncopyable
{
private:
    long _stored;

public:
    Atomic() : _stored(false)
    {}

    Atomic(bool val) : _stored(val)
    {}

    operator bool() const
    {
        return InterlockedCompareExchange(const_cast<long*>(&_stored), 0, 0);
    }

    bool exchange(bool val)
    {
        return InterlockedExchange(&_stored, val);
    }

    bool operator=(bool val)
    {
        InterlockedExchange(&_stored, val);
        return val;
    }

    bool operator&=(bool val)
    {
        for(;;)
        {
            const long prev = InterlockedCompareExchange(&_stored, 0, 0);
            bool newVal = prev;
            newVal &= val;
            if( InterlockedCompareExchange(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    bool operator|=(bool val)
    {
        for(;;)
        {
            const long prev = InterlockedCompareExchange(&_stored, 0, 0);
            bool newVal = prev;
            newVal |= val;
            if( InterlockedCompareExchange(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    bool operator^=(bool val)
    {
        for(;;)
        {
            const long prev = InterlockedCompareExchange(&_stored, 0, 0);
            bool newVal = prev;
            newVal ^= val;
            if( InterlockedCompareExchange(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }
};

/**
 * Atomic class specialized for pointers
 * Atomic<T*>
 *
 * Atomic()
 * Atomic(T* val)
 * operator T*() const
 * T* exchange(T* val)
 * T* operator=(T* val)
 * T* operator+=(std::ptrdiff_t val)
 * T* operator-=(std::ptrdiff_t val)
 * T* operator++()
 * T* operator++(int)
 * T* operator--()
 * T* operator--(int)
 */
template<class T>
struct Atomic<T*, typename boost::enable_if_c<true>::type> : private boost::noncopyable
{
private:
    void* _stored;

public:
    Atomic() : _stored(0)
    {}

    Atomic(T* val) : _stored(val)
    {}

    operator T*() const
    {
        void* const curr = InterlockedCompareExchangePointer(const_cast<void**>(&_stored), 0, 0);
        return static_cast<T*>(curr);
    }

    T* exchange(T* val)
    {
        void* const prev = InterlockedExchangePointer(&_stored, val);
        return static_cast<T*>(prev);
    }

    T* operator=(T* val)
    {
        InterlockedExchangePointer(&_stored, val);
        return val;
    }

    T* operator+=(std::ptrdiff_t val)
    {
        assert(_stored);
        for(;;)
        {
            void* const prev = InterlockedCompareExchangePointer(&_stored, 0, 0);
            T* newVal = static_cast<T*>(prev);
            newVal += val;
            if( InterlockedCompareExchangePointer(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    T* operator-=(std::ptrdiff_t val)
    {
        assert(_stored);
        for(;;)
        {
            void* const prev = InterlockedCompareExchangePointer(&_stored, 0, 0);
            T* newVal = static_cast<T*>(prev);
            newVal -= val;
            if( InterlockedCompareExchangePointer(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    // pre-increment
    T* operator++()
    {
        return operator+=(1);
    }

    // pre-decrement
    T* operator--()
    {
        return operator-=(1);
    }

    // post-increment
    T* operator++(int)
    {
        T* val = operator+=(1);
        return --val;
    }

    // post-decrement
    T* operator--(int)
    {
        T* val = operator-=(1);
        return ++val;
    }
};


} // namespace Cti
