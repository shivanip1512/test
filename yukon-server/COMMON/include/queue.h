#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __CtiQUE_H__
#define __CtiQUE_H__


#include <iostream>
#include <functional>
#include <iostream>
#include <LIMITS>

#include "dlldefs.h"
#include "logger.h"
#include "utility.h"

#include <boost/thread/condition.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/xtime.hpp>

using std::string;
using std::cerr;
using std::endl;




// Template Queuing class
template <class T,  class C>
class IM_EX_CTIBASE CtiQueue
{
private:
    boost::condition           dataAvailable;
    mutable boost::timed_mutex mux;
    std::list<T*>              _sortedCol;
    string                     _name;

    struct boost::xtime xt_eot;

public:

    CtiQueue() :
    _name("Unnamed Queue")
    {
        xt_eot.sec  = INT_MAX;
        xt_eot.nsec = 0;
    }

    virtual ~CtiQueue()
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        delete_list(_sortedCol);
        _sortedCol.clear();
    }

    void putQueue(T *pt)
    {
        try
        {
            if(pt != NULL)
            {
                boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
                dataAvailable.notify_one();
                _sortedCol.push_back(pt);

                try
                {
                    _sortedCol.sort();
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ") q.size() " << _sortedCol.size() << endl;
                    }
                    _sortedCol.clear();     // Dump the queue?
                }

                // mutex automatically released in LockGuard destructor
            }
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl; }
        }
    }

    T* getQueue()
    {
        T *pval = NULL;
        try
        {
            boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
            while( _sortedCol.empty() )
            {
                dataAvailable.wait(scoped_lock);
            }

            pval = _sortedCol.front();
            _sortedCol.pop_front();

            // cerr << "Number of entries " << _sortedCol.entries() << endl;
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl; }
        }

        return pval;
    }

    T* getQueue(unsigned time)
    {
        T *pval = NULL;
        try
        {
            bool wRes = false;
            struct boost::xtime xt;
            boost::xtime_get(&xt, boost::TIME_UTC);
            xt.sec  += (time/1000);
            xt.nsec += (time%1000)*1000;
            boost::timed_mutex::scoped_timed_lock scoped_lock(mux,xt);

            if(scoped_lock.locked())
            {
                if(_sortedCol.empty())
                {
                    wRes = dataAvailable.timed_wait(scoped_lock,xt); // monitor mutex released automatically
                    // thread must have been signalled AND mutex reacquired to reach here OR RW_THR_TIMEOUT
                    if(wRes == true)
                    {
                        pval = _sortedCol.front();
                        _sortedCol.pop_front();
                    }
                }else{
                    pval = _sortedCol.front();
                    _sortedCol.pop_front();
                }
            }
            // mutex automatically released in LockGuard destructor
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl; }
        }
        return pval;
    }

    bool putQueue(T *pt, unsigned time)
    {
        bool putWasDone = false;
        try
        {
            if(pt != NULL)
            {
                boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
                {
                    _sortedCol.push_back(pt);
                    dataAvailable.notify_one();
                    putWasDone = true;

                    try
                    {
                        _sortedCol.sort();
                    }
                    catch(...)
                    {
                        cerr << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl; }
        }

        return putWasDone;
    }

    /*
     *  Allows us to shrink or grow the queue in question..
     */
    size_t   resize(size_t addition = 1)
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        return _sortedCol.size();
    }


    size_t   entries(void) const       // QueryQue.
    {
        //TryLockGuard lock(monitor());   // TRY to acquire monitor mutex. Not critical.  May be used as a getter, so we cannot be rigid here.
        return _sortedCol.size();
    }

    size_t   size(void)        // how big may it be?
    {
        //TryLockGuard lock(monitor());   // TRY to acquire monitor mutex. Not critical.  May be used as a getter, so we cannot be rigid here.
        return _sortedCol.size();
    }

    BOOL  isFull(void)
    {
        return(FALSE);
    }

    void     clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        delete_list(_sortedCol);
        _sortedCol.clear();
    }

    string getName() const
    {
        return _name;
    }

    CtiQueue< T, C > & setName(const string &str)
    {
        _name = str;
        return *this;
    }
    template<class _II, class _Fn> inline
    void ts_for_each(_II _F, _II _L, _Fn _Op, void* d)
    {
        for (; _F != _L; ++_F)
            _Op(*_F, d);

    }
    void apply(void (*fn)(T*&,void*), void* d)
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        ts_for_each(_sortedCol.begin(),_sortedCol.end(),fn,d);
    }

};

// Template Queuing class
template <class T>
class IM_EX_CTIBASE CtiFIFOQueue
{
private:

    std::list<T*> _col;
    boost::condition           dataAvailable;
    mutable boost::timed_mutex                mux;

    string       _name;

    struct boost::xtime xt_eot;

public:

    CtiFIFOQueue() :
    _name("Unnamed Queue")
    {
        xt_eot.sec  = INT_MAX;
        xt_eot.nsec = 0;
    }

    virtual ~CtiFIFOQueue()
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        delete_list(_col);
        _col.clear();
    }

    void putQueue(T *pt)
    {
        try
        {
            if(pt != NULL)
            {
                {
                boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
                _col.push_back(pt);
                dataAvailable.notify_one();
                // mutex automatically released in LockGuard destructor
                }
            }
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl; }
        }
    }

    T* getQueue()
    {
        T *pval = NULL;
        try
        {
            boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
            while( _col.empty() )
            {
                dataAvailable.wait(scoped_lock);
            }
            pval = _col.front();
            _col.pop_front();

            // cerr << "Number of entries " << _sortedCol.entries() << endl;
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl; }
        }

        return pval;
    }

    T* getQueue(unsigned time)
    {
        T *pval = NULL;
        try
        {
            bool wRes = true;
            struct boost::xtime xt;
            boost::xtime_get(&xt, boost::TIME_UTC);
            xt.sec  += (time/1000);
            xt.nsec += (time%1000)*1000;

            boost::timed_mutex::scoped_timed_lock scoped_lock(mux,xt);

            if(scoped_lock.locked())
            {

                if(_col.empty())
                {
                    wRes = dataAvailable.timed_wait(scoped_lock,xt); // monitor mutex released automatically
                    // thread must have been signalled AND mutex reacquired to reach here OR RW_THR_TIMEOUT
                    if(wRes == true)
                    {
                        pval = _col.front();
                        _col.pop_front();
                    }
                }else{
                    pval = _col.front();
                    _col.pop_front();
                }
            }
            // mutex automatically released in LockGuard destructor
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl; }
        }
        return pval;
    }

    bool putQueue(T *pt, unsigned time)
    {
        bool putWasDone = false;
        try
        {
            if(pt != NULL)
            {
                {
                    boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
                    _col.push_back(pt);
                    dataAvailable.notify_one();
                    // mutex automatically released in LockGuard destructor
                    putWasDone = true;
                }
            }
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl; }
        }

        return putWasDone;
    }

    /*
     *  Allows us to shrink or grow the queue in question..
     */
    size_t   resize(size_t addition = 1)
    {
        {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        //LockGuard lock(monitor());   // acquire monitor mutex
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        return _col.size();

        }
    }


    size_t   entries(void) const       // QueryQue.
    {
        //TryLockGuard lock(monitor());   // TRY to acquire monitor mutex. Not critical.  May be used as a getter, so we cannot be rigid here.
        return _col.size();
    }

    size_t   size(void)        // how big may it be?
    {
        //TryLockGuard lock(monitor());   // TRY to acquire monitor mutex. Not critical.  May be used as a getter, so we cannot be rigid here.
        return _col.size();
    }

    BOOL  isFull(void)
    {
        return(FALSE);
    }

    void     clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        delete_list(_col);
        _col.clear();
        }
    }

    string getName() const
    {
        return _name;
    }

    CtiFIFOQueue< T > & setName(const string &str)
    {
        _name = str;
        return *this;
    }
    template<class _II, class _Fn> inline
    void ts_for_each(_II _F, _II _L, _Fn _Op, void* d)
    {
        for (; _F != _L; ++_F)
            _Op(*_F, d);

    }
    void apply(void (*fn)(T*&,void*), void* d)
    {
        {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        //_col.apply(fn,d);
        ts_for_each(_col.begin(),_col.end(),fn,d);
        }
    }
};

#endif //#ifndef __CtiQUE_H__
