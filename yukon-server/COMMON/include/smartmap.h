/*************************************************************************
 *
 * smartmap.h      7/10/2002
 *
 *****
 *
 *  A templatized run/real time data base class.  Initially done with a
 *  RogueWave singly linked list solely for the benefits of the apply and
 *  find functionality without the need for Standard C++ library.
 *
 * Originated by:
 *     Corey G. Plender    7/10/2002
 *
 *
 * (c) 2002 Cannon Technologies Inc.
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __SMARTMAP_H__
#define __SMARTMAP_H__
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....


#include <windows.h>
#include <iostream>
#include <functional>
#include <map>

#include "boost/shared_ptr.hpp"

using boost::shared_ptr;

#include "dllbase.h"
#include "dlldefs.h"
#include "hashkey.h"
#include "logger.h"
#include "mutex.h"

using std::map;
using std::pair;


template < class T >
class IM_EX_CTIBASE CtiSmartMap
{
public:

    typedef std::map< long, shared_ptr< T > >                coll_type;              // This is the collection type!
    typedef coll_type::value_type                       val_type;
    typedef coll_type::iterator                         spiterator;
    typedef std::pair<spiterator, bool>                      insert_pair;
    typedef shared_ptr< T >                             ptr_type;

protected:

    mutable CtiMutex _mux;
    // This is a keyed Mapping which does not allow duplicates!
    coll_type _map;

    int _dberrorcode;

public:


    CtiSmartMap() {}

    virtual ~CtiSmartMap()
    {
        CtiLockGuard< CtiMutex > gaurd(_mux);
        _map.clear();                   // The shared_ptrs are deleted when all references are de-scoped.
    }

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d)
    {
        CtiLockGuard< CtiMutex >  gaurd(_mux);
        spiterator itr;

        for(itr = _map.begin(); itr != _map.end(); ++itr)
        {
            val_type vp = *itr;
            applyFun( vp.first, vp.second, d);
        }
    }


    #ifdef MISCROSOFT_EVER_FIXES_STL
    /*
     *  Selects all collection entries which match the function and return a smartpointer to them in match_col
     */
    vector< long > select(bool (*selectFun)(const long, ptr_type, void*), void* d )
    {
        int count = 0;
        CtiLockGuard< CtiMutex >  gaurd(_mux);
        spiterator itr;

        vector< long > local_coll;

        for(itr = _map.begin(); itr != _map.end(); ++itr)
        {
            val_type vp = *itr;
            if(selectFun( vp.first, vp.second, d))
            {
                try
                {
                    local_coll.push_back(vp.first);
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
        }

        return local_coll;
    }
    #endif

    insert_pair insert(long key, T* val)
    {
        shared_ptr< T > storeval(val);
        CtiLockGuard< CtiMutex > gaurd(_mux);
        return _map.insert( val_type(key, storeval) );
    }

    insert_pair insert(long key, shared_ptr< T > storeval)
    {
        CtiLockGuard< CtiMutex > gaurd(_mux);
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
        CtiLockGuard< CtiMutex > gaurd(_mux);
        ptr_type retRef;
        spiterator itr = _map.find( key );

        if( itr != _map.end() )
        {
            retRef = itr->second;
        }

        return retRef;
    }

    ptr_type find(bool (*testFun)(T&, void*),void* d)
    {
        CtiLockGuard< CtiMutex > gaurd(_mux);
        ptr_type retRef;
        spiterator itr;

        for(itr = _map.begin(); itr != _map.end(); ++itr)
        {
            if(testFun(itr->second, d))
            {
                retRef = itr->second;
                break;
            }
        }

        return retRef;
    }

    ptr_type find(bool (*testFun)(ptr_type&, void*),void* d)
    {
        CtiLockGuard< CtiMutex > gaurd(_mux);
        ptr_type retRef;
        spiterator itr;

        for(itr = _map.begin(); itr != _map.end(); ++itr)
        {
            if(testFun(itr->second, d))
            {
                retRef = itr->second;
                break;
            }
        }

        return retRef;
    }

    ptr_type find(bool (*testFun)(const long, ptr_type, void*),void* d)
    {
        CtiLockGuard< CtiMutex > gaurd(_mux);
        ptr_type retRef;
        spiterator itr;

        for(itr = _map.begin(); itr != _map.end(); ++itr)
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
        CtiLockGuard< CtiMutex > gaurd(_mux);
        ptr_type retRef;
        spiterator itr;

        for(itr = _map.begin(); itr != _map.end(); ++itr)
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
        CtiLockGuard< CtiMutex > gaurd(_mux);
        while(remove(testFun, d));
        return;
    }

    ptr_type remove(long key)
    {
        CtiLockGuard< CtiMutex > gaurd(_mux);
        ptr_type retRef;
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
        CtiLockGuard< CtiMutex > gaurd(_mux);
        return _map.size();
    }

    bool empty() const
    {
        CtiLockGuard< CtiMutex > gaurd(_mux);
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

    map< long, shared_ptr< T > > & getMap()
    {
        return _map;
    }

    CtiMutex & getMux()
    {
        return _mux;
    }
};

#endif      // #ifndef __SMARTMAP_H__
