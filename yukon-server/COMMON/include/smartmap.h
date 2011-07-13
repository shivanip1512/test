#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <iostream>
#include <functional>
#include <map>

#include <rw/thr/rwlock.h>

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;

#include "dllbase.h"
#include "dlldefs.h"

#include "readers_writer_lock.h"

template < class T >
class CtiSmartMap
{
public:

    typedef std::map< long, shared_ptr< T > >   coll_type;     // This is the collection type!
    typedef typename coll_type::value_type               val_type;
    typedef typename coll_type::iterator                 spiterator;
    typedef typename std::pair<spiterator, bool>         insert_pair;
    typedef typename shared_ptr< T >                     ptr_type;

    typedef typename Cti::readers_writer_lock_t          lock_t;
    typedef typename lock_t::reader_lock_guard_t         reader_lock_guard_t;
    typedef typename lock_t::writer_lock_guard_t         writer_lock_guard_t;

protected:

    mutable lock_t _lock;

    // This is a keyed Mapping which does not allow duplicates!
    coll_type _map;

    int _dberrorcode;

public:

    CtiSmartMap() {}

    virtual ~CtiSmartMap()
    {
        writer_lock_guard_t guard(_lock);

        _map.clear();                   // The shared_ptrs are deleted when all references are de-scoped.
    }

    void clear()
    {
        _map.clear();
    }

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d)
    {
        reader_lock_guard_t guard(_lock);

        spiterator itr, itr_end = _map.end();

        for(itr = _map.begin(); itr != itr_end; ++itr)
        {
            val_type vp = *itr;
            applyFun( vp.first, vp.second, d);
        }
    }


    insert_pair insert(long key, T* val)
    {
        writer_lock_guard_t guard(_lock);

        return _map.insert( val_type(key, shared_ptr< T >(val)) );
    }

    insert_pair insert(long key, shared_ptr< T > &storeval)
    {
        writer_lock_guard_t guard(_lock);

        return _map.insert( val_type(key, storeval) );
    }

    /*
     *  This operation will return a value to us by
     *
     *  Function should have the following definition
     *
     *    bool yourTester(T*, void* d);
     */

    ptr_type find(long key)
    {
        ptr_type retRef;

        reader_lock_guard_t guard(_lock);

        spiterator itr = _map.find( key );

        if( itr != _map.end() )
        {
            retRef = itr->second;
        }

        return retRef;
    }

    ptr_type find(bool (*testFun)(T&, void*),void* d)
    {
        ptr_type retRef;

        reader_lock_guard_t guard(_lock);

        spiterator itr, itr_end = _map.end();

        for(itr = _map.begin(); itr != itr_end; ++itr)
        {
            if(testFun(*(itr->second), d))
            {
                retRef = itr->second;
                break;
            }
        }

        return retRef;
    }

    ptr_type find(bool (*testFun)(ptr_type&, void*),void* d)
    {
        ptr_type retRef;

        reader_lock_guard_t guard(_lock);

        spiterator itr, itr_end = _map.end();

        for(itr = _map.begin(); itr != itr_end; ++itr)
        {
            if(testFun(itr->second, d))
            {
                retRef = itr->second;
                break;
            }
        }

        return retRef;
    }

    ptr_type find(bool (*testFun)(const long, const ptr_type &, void*),void* d)
    {
        ptr_type retRef;

        reader_lock_guard_t guard(_lock);

        spiterator itr, itr_end = _map.end();

        for(itr = _map.begin(); itr != itr_end; ++itr)
        {
            if(testFun(itr->first, itr->second, d))
            {
                retRef = itr->second;
                break;
            }
        }

        return retRef;
    }

    ptr_type remove(bool (*testFun)(ptr_type&, void*), void* d)        // Removes first match which return bool true on testFun
    {
        ptr_type retRef;

        writer_lock_guard_t guard(_lock);

        spiterator itr, itr_end = _map.end();

        for(itr = _map.begin(); itr != itr_end; ++itr)
        {
            if(testFun)
            {
                if(testFun(itr->second, d))
                {
                    retRef = itr->second;
                    _map.erase( itr );
                    break;
                }
            }
            else
            {
                retRef = itr->second;
                _map.erase( itr );
                break;
            }
        }

        return retRef;
    }

    void removeAll(bool (*testFun)(ptr_type&, void*), void* d)        // Removes ALL that match this function
    {
        writer_lock_guard_t guard(_lock);

        while(remove(testFun, d))
            ;
    }

    ptr_type remove(long key)
    {
        ptr_type retRef;

        writer_lock_guard_t guard(_lock);

        spiterator itr = _map.find( key );

        if( itr != _map.end() )
        {
            retRef = itr->second;       // New reference count!
            _map.erase( key );          // Should reduce reference count.
        }

        return retRef;
    }

    size_t entries() const
    {
        reader_lock_guard_t guard(_lock);

        return _map.size();
    }

    bool empty() const
    {
        reader_lock_guard_t guard(_lock);

        return _map.empty();
    }

    int getErrorCode() const { return _dberrorcode;}
    int setErrorCode(int ec)
    {
        if( ec ) _dberrorcode = ec;      // Only set it if there was an error (don't re-set it)
        return ec;
    }

    void resetErrorCode()
    {
        _dberrorcode = 0;      // Only set it if there was an error (don't re-set it)
    }

    std::map< long, shared_ptr< T > > &getMap()
    {
        return _map;
    }

    lock_t &getLock()
    {
        return _lock;
    }

    lock_t &getLock() const
    {
        return _lock;
    }
};

