#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __CtiQUE_H__
#define __CtiQUE_H__


#include <iostream>
#include <functional>
#include <iostream>
using namespace std;

#include "dlldefs.h"
#include "logger.h"


#include <rw\thr\condtion.h> // for RWCondition
#include <rw\thr\monitor.h>  // for RWMonitor<T>
#include <rw\thr\mutex.h>    // for RWMutexLock
#include <rw\tpsrtvec.h>      // for RWTPtrSortedVector<T>

// Template Queuing class
template <class T,  class C>
class IM_EX_CTIBASE CtiQueue : RWMonitor< RWMutexLock >
{
private:
    RWCondition                dataAvailable;
    RWTPtrSortedVector<T,C>    pvect_;

    RWCString                  _name;
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
                pvect_.insert(pt);
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
            while(!(pvect_.entries() > 0))
            {
                dataAvailable.wait(); // mutex released automatically
                // thread must have been signalled AND
                //   mutex reacquired to reach here
            }
            pval = pvect_.removeFirst();
            // cout << "Number of entries " << pvect_.entries() << endl;
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

            if(!(pvect_.entries() > 0))
            {
                wRes = dataAvailable.wait(time); // monitor mutex released automatically
                // thread must have been signalled AND mutex reacquired to reach here OR RW_THR_TIMEOUT
            }

            if(wRes == RW_THR_COMPLETED)
            {
                pval = pvect_.removeFirst();
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
                    pvect_.insert(pt);
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
            pval = pvect_.remove(fn, d);
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        return pvect_.entries();
    }


    size_t   entries(void) const       // QueryQue.
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        return pvect_.entries();
    }

    size_t   size(void)        // how big may it be?
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        return pvect_.entries();
    }

    BOOL  isFull(void)
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        return(FALSE);
    }

    void     clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        pvect_.clearAndDestroy();
    }

    RWCString getName() const
    {
        return _name;
    }

    CtiQueue< T, C > & setName(const RWCString &str)
    {
        _name = str;
        return *this;
    }

    void apply(void (*fn)(T*&,void*), void* d)
    {
        LockGuard lock(monitor());   // acquire monitor mutex
        pvect_.apply(fn,d);
    }
};

#endif //#ifndef __CtiQUE_H__
