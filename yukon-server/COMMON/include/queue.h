#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __CtiQUE_H__
#define __CtiQUE_H__


#include <iostream>
#include <functional>
#include <iostream>
//using namespace std;

#include "dlldefs.h"
#include "logger.h"


#include <rw\thr\condtion.h> // for RWCondition
#include <rw\thr\monitor.h>  // for RWMonitor<T>
#include <rw\thr\mutex.h>    // for RWMutexLock
#include <rw\tpsrtdli.h>     // for RWTPtrSortedDlist<T>
#include <rw\tpdlist.h>      // for RWTPtrDlist<T>

using std::string;
using std::cout;
using std::endl;

// Template Queuing class
template <class T,  class C>
class IM_EX_CTIBASE CtiQueue : RWMonitor< RWMutexLock >
{
private:
    RWCondition                dataAvailable;
    RWTPtrSortedDlist<T,C>     _sortedCol;

    string                  _name;
public:

    CtiQueue() :
    _name("Unnamed Queue"),
    dataAvailable(mutex())     // init with monitor mutex
    {}

    void putQueue(T *pt)
    {
        try
        {
            if(pt != NULL)
            {
                LockGuard lock(monitor()); // acquire monitor mutex
                _sortedCol.insert(pt);
                dataAvailable.signal();
                // mutex automatically released in LockGuard destructor
            }
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
        }
    }

    T* getQueue()
    {
        T *pval = NULL;
        try
        {
            LockGuard lock(monitor());   // acquire monitor mutex
            while(!(_sortedCol.entries() > 0))
            {
                dataAvailable.wait(); // mutex released automatically
                // thread must have been signalled AND
                //   mutex reacquired to reach here
            }
            pval = _sortedCol.removeFirst();
            // cout << "Number of entries " << _sortedCol.entries() << endl;
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
            RWTHROW(x);
        }

        return pval;
    }

    T* getQueue(unsigned time)
    {
        T *pval = NULL;
        try
        {
            LockGuard lock(monitor());   // acquire monitor mutex
            RWWaitStatus wRes = RW_THR_COMPLETED;

            if(!(_sortedCol.entries() > 0))
            {
                wRes = dataAvailable.wait(time); // monitor mutex released automatically
                // thread must have been signalled AND mutex reacquired to reach here OR RW_THR_TIMEOUT
            }

            if(wRes == RW_THR_COMPLETED)
            {
                pval = _sortedCol.removeFirst();
            }

            // mutex automatically released in LockGuard destructor
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
            RWTHROW(x);
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
                LockGuard lock(monitor()); // acquire monitor mutex
                {
                    _sortedCol.insert(pt);
                    dataAvailable.signal();
                    // mutex automatically released in LockGuard destructor
                    putWasDone = true;
                }
            }
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
        }

        return putWasDone;
    }

    T* getByFunc(RWBoolean (*fn)(const T*, void *), void *d)
    {
        try
        {
            T *pval = NULL;

            LockGuard lock(monitor());   // acquire monitor mutex
            pval = _sortedCol.remove(fn, d);
            return pval;
            // mutex automatically released in LockGuard destructor
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
        }
    }

    /*
     *  Allows us to shrink or grow the queue in question..
     */
    size_t   resize(size_t addition = 1)
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        return _sortedCol.entries();
    }


    size_t   entries(void) const       // QueryQue.
    {
        TryLockGuard lock(monitor());   // TRY to acquire monitor mutex. Not critical.  May be used as a getter, so we cannot be rigid here.
        return _sortedCol.entries();
    }

    size_t   size(void)        // how big may it be?
    {
        TryLockGuard lock(monitor());   // TRY to acquire monitor mutex. Not critical.  May be used as a getter, so we cannot be rigid here.
        return _sortedCol.entries();
    }

    BOOL  isFull(void)
    {
        return(FALSE);
    }

    void     clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        _sortedCol.clearAndDestroy();
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

    void apply(void (*fn)(T*&,void*), void* d)
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        _sortedCol.apply(fn,d);
    }
};

// Template Queuing class
template <class T>
class IM_EX_CTIBASE CtiFIFOQueue : RWMonitor< RWMutexLock >
{
private:
    RWCondition dataAvailable;
    RWTPtrDlist<T>  _col;

    string       _name;
public:

    CtiFIFOQueue() :
    _name("Unnamed Queue"),
    dataAvailable(mutex())     // init with monitor mutex
    {}

    void putQueue(T *pt)
    {
        try
        {
            if(pt != NULL)
            {
                LockGuard lock(monitor()); // acquire monitor mutex
                _col.insert(pt);
                dataAvailable.signal();
                // mutex automatically released in LockGuard destructor
            }
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
        }
    }

    T* getQueue()
    {
        T *pval = NULL;
        try
        {
            LockGuard lock(monitor());   // acquire monitor mutex
            while(!(_col.entries() > 0))
            {
                dataAvailable.wait(); // mutex released automatically
                // thread must have been signalled AND
                //   mutex reacquired to reach here
            }
            pval = _col.removeFirst();
            // cout << "Number of entries " << _col.entries() << endl;
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
            RWTHROW(x);
        }

        return pval;
    }

    T* getQueue(unsigned time)
    {
        T *pval = NULL;
        try
        {
            LockGuard lock(monitor());   // acquire monitor mutex
            RWWaitStatus wRes = RW_THR_COMPLETED;

            if(!(_col.entries() > 0))
            {
                wRes = dataAvailable.wait(time); // monitor mutex released automatically
                // thread must have been signalled AND mutex reacquired to reach here OR RW_THR_TIMEOUT
            }

            if(wRes == RW_THR_COMPLETED)
            {
                pval = _col.removeFirst();
            }

            // mutex automatically released in LockGuard destructor
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
            RWTHROW(x);
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
                LockGuard lock(monitor()); // acquire monitor mutex
                {
                    _col.insert(pt);
                    dataAvailable.signal();
                    // mutex automatically released in LockGuard destructor
                    putWasDone = true;
                }
            }
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
        }

        return putWasDone;
    }

    T* getByFunc(RWBoolean (*fn)(const T*, void *), void *d)
    {
        try
        {
            T *pval = NULL;

            LockGuard lock(monitor());   // acquire monitor mutex
            pval = _col.remove(fn, d);
            return pval;
            // mutex automatically released in LockGuard destructor
        }
        catch(const RWxmsg& x)
        {
            cout << "Exception: " << x.why() << endl;
        }
    }

    /*
     *  Allows us to shrink or grow the queue in question..
     */
    size_t   resize(size_t addition = 1)
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        return _col.entries();
    }


    size_t   entries(void) const       // QueryQue.
    {
        TryLockGuard lock(monitor());   // TRY to acquire monitor mutex. Not critical.  May be used as a getter, so we cannot be rigid here.
        return _col.entries();
    }

    size_t   size(void)        // how big may it be?
    {
        TryLockGuard lock(monitor());   // TRY to acquire monitor mutex. Not critical.  May be used as a getter, so we cannot be rigid here.
        return _col.entries();
    }

    BOOL  isFull(void)
    {
        return(FALSE);
    }

    void     clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        _col.clearAndDestroy();
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

    void apply(void (*fn)(T*&,void*), void* d)
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        _col.apply(fn,d);
    }
};

#endif //#ifndef __CtiQUE_H__
