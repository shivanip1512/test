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
    
    long getStored() const
    {
        return InterlockedCompareExchange(const_cast<long*>(&_stored), 0, 0);
    }

    template<int size>
    T increment()
    {
        return operator+=(1);
    }

    template<>
    T increment<sizeof(long)>()
    {
        return InterlockedIncrement(&_stored);
    }

    template<int size>
    T decrement()
    {
        return operator-=(1);
    }

    template<>
    T decrement<sizeof(long)>()
    {
        return InterlockedDecrement(&_stored);
    }

public:
    Atomic() : _stored(0)
    {}

    Atomic(T val) : _stored(val)
    {}

    operator T() const
    {
        return getStored();
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
            const long prev = getStored();
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
            const long prev = getStored();
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
            const long prev = getStored();
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
        for(;;)
        {
            const long prev = getStored();
            T newVal = prev;
            newVal += val;
            if( InterlockedCompareExchange(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    T operator-=(T val)
    {
        for(;;)
        {
            const long prev = getStored();
            T newVal = prev;
            newVal -= val;
            if( InterlockedCompareExchange(&_stored, newVal, prev) == prev )
            {
                return newVal; // success
            }
        }
    }

    // pre-increment
    T operator++()
    {
        return increment<sizeof(T)>();
    }

    // pre-decrement
    T operator--()
    {
        return decrement<sizeof(T)>();
    }

    // post-increment
    T operator++(int)
    {
        T val = increment<sizeof(T)>();
        return --val;
    }

    // post-decrement
    T operator--(int)
    {
        T val = decrement<sizeof(T)>();
        return ++val;
    }
};

/**
 * Atomic class specialized for bool
 * Atomic<bool>
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
 */
template<>
struct Atomic<bool, typename boost::enable_if_c<true>::type> : private boost::noncopyable
{
private:
    long _stored;

    long getStored() const
    {
        return InterlockedCompareExchange(const_cast<long*>(&_stored), 0, 0);
    }

public:
    Atomic() : _stored(false)
    {}

    Atomic(bool val) : _stored(val)
    {}

    operator bool() const
    {
        return getStored();
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
            const long prev = getStored();
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
            const long prev = getStored();
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
            const long prev = getStored();
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

    void* getStored() const
    {
        return InterlockedCompareExchangePointer(const_cast<void**>(&_stored), 0, 0);
    }

public:
    Atomic() : _stored(0)
    {}

    Atomic(T* val) : _stored(val)
    {}

    operator T*() const
    {
        return static_cast<T*>(getStored());
    }

    T* exchange(T* val)
    {
        return static_cast<T*>(InterlockedExchangePointer(&_stored, val));
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
            void* const prev = getStored();
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
            void* const prev = getStored();
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
