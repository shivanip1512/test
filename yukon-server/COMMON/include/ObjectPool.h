/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __CTI_OBJECTPOOL_H__
#define __CTI_OBJECTPOOL_H__

#include <boost/pool/singleton_pool.hpp>
#include <boost/shared_ptr.hpp>

#include "mutex.h"
#include "guard.h"

namespace Cti
{

template
<
    typename T,         // Object type that the pool allocates
    int SIZE = 32       // 'Base' number of allocated objects.  First allocation of pool will hold
>                       //  this many objects.  Subsequent allocations will successively double
class ObjectPool        //  the number of objects.  SIZE + 2 * SIZE + 4 * SIZE + .....
{
public:

    static boost::shared_ptr<T> Create()
    {
        return boost::shared_ptr<T>(ObjectPool<T, SIZE>::CreateObject(), ObjectPool<T, SIZE>::DeleteObject);
    }

private:

    typedef boost::singleton_pool
    <   
        T,
        sizeof(T),
        boost::default_user_allocator_new_delete,
        boost::details::pool::null_mutex,           // No mutex on pool_ because we lock it externally
        SIZE
    >
    pool_;

    static T *CreateObject()
    {
        CtiLockGuard<CtiMutex> guard_(mutex_);

        T *p = static_cast<T*>(pool_::malloc());
        ::new (p) T();

        ++count_;

        return p;
    }
    
    static void DeleteObject(T *p)
    {
        CtiLockGuard<CtiMutex> guard_(mutex_);

        p->~T();
        pool_::free(p);

        // Once in a while we want to reclaim unused chunks of the pool.  release_memory() only works
        //  from the biggest chunk to the smallest so it probably won't do a lot unless we free in
        //  reverse order.
        // The offset [pool_::next_size / 2] is so we don't try to release memory when the pool is full,
        //  but right on an allocation border.
        // Note: We never totally purge all memory when the pool is empty.

        if (((--count_ + (pool_::next_size / 2)) % pool_::next_size) == 0)
        {
            pool_::release_memory();
        }
    }

    static unsigned count_;         // Total count of allocated objects in the pool
    static CtiMutex mutex_;         // Mutex to lock the pool etc...
};

template <typename T, int SIZE> unsigned ObjectPool<T, SIZE>::count_ = 0;
template <typename T, int SIZE> CtiMutex ObjectPool<T, SIZE>::mutex_;

}

#endif

