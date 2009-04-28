/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __CTI_OBJECTPOOL_H__
#define __CTI_OBJECTPOOL_H__

#include <boost/pool/singleton_pool.hpp>
#include <boost/shared_ptr.hpp>


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
        boost::details::pool::default_mutex,
        SIZE
    >
    _pool;

    static T *CreateObject()
    {
        T *p = static_cast<T*>(_pool::malloc());
        ::new (p) T();

        return p;
    }
    
    static void DeleteObject(T *p)
    {
        p->~T();
        _pool::free(p);
    }
};

}

#endif

