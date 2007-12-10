#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __CtiQUE_H__
#define __CtiQUE_H__


#include <iostream>
#include <functional>
#include <iostream>
#include <LIMITS>

#include "cparms.h"
#include "dlldefs.h"
#include "logger.h"
#include "utility.h"

#include <boost/thread/condition.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/xtime.hpp>

using std::string;


// Template Queuing class
// This now uses class C as the sorting operator, and guarantees fifo and priority operation
// What this means is when using class C (a <(C compare)> b) == true, a will come off the list first.
// If a and b are identical (a<C>b and b<C>a == false) then the ordering is depending on insertion order
template <class T,  class C>
class IM_EX_CTIBASE CtiQueue
{
public:
    struct QueueDataStruct
    {
        unsigned long insertOrder;
        T* dataPointer;

        //Although this says > it is really doing whatever the user gives us...
        bool operator>(const QueueDataStruct &rhs) const
        {
            bool retVal = false;
            C compare;
            if( dataPointer != NULL && rhs.dataPointer != NULL )
            {
                if( compare(*dataPointer, *(rhs.dataPointer)) )
                {
                    retVal = true;
                }
                else if( compare(*(rhs.dataPointer), *dataPointer) )
                {
                    retVal = false;
                }
                else
                {
                    //The operator wasnt able to decide (they are probably ==)
                    if( insertOrder < rhs.insertOrder )
                    {
                        retVal = true;
                    }
                    else
                        retVal = false;
                }
            }
            return retVal;
        }
    };
private:
    boost::condition           dataAvailable;
    mutable boost::timed_mutex mux;
    
    std::list<QueueDataStruct> *_col;
    string                      _name;
    unsigned int                _insertValue;

    boost::xtime xt_eot;


    std::list<QueueDataStruct> & getCollection()
    {
        if(!_col)
            _col = new std::list<struct QueueDataStruct>;

        return *_col;
    }

    unsigned int getNextInsertValue()
    {
        if( size() == 0 )
        {
            _insertValue = 0;
        }
        return _insertValue++;
    }

public:

    CtiQueue() :
    _col(0),
    _name("Unnamed Queue")
    {
        xt_eot.sec  = INT_MAX;
        xt_eot.nsec = 0;
    }

    virtual ~CtiQueue()
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        resetCollection();
    }

    void putQueue(T *pt)
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        try
        {
            if(pt != NULL)
            {
                try
                {
                    dataAvailable.notify_one();
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                try
                {
                    QueueDataStruct tempStruct;
                    tempStruct.dataPointer = pt;
                    tempStruct.insertOrder = getNextInsertValue();
                    getCollection().push_back(tempStruct);
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                if(!gConfigParms.isTrue("YUKON_NOSORT_QUEUES"))
                {
                    try
                    {
                        getCollection().sort( std::greater<struct QueueDataStruct>() );
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ") q.size() " << getCollection().size() << endl;
                        }
                        resetCollection();     // Dump the queue?
                    }
                }

                // mutex automatically released in LockGuard destructor
            }
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl;}
            resetCollection();     // Dump the queue?
        }
    }

    T* getQueue()
    {
        T *pval = NULL;
        QueueDataStruct data;
        try
        {
            boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
            while( getCollection().empty() )
            {
                dataAvailable.wait(scoped_lock);
            }

            data = getCollection().front();
            pval = data.dataPointer;
            getCollection().pop_front();

            // cerr << "Number of entries " << getCollection().entries() << endl;
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl;}
        }

        return pval;
    }

    T* getQueue(unsigned time)
    {
        T *pval = NULL;
        QueueDataStruct data;
        try
        {
            bool wRes = false;
            struct boost::xtime xt;
            boost::xtime_get(&xt, boost::TIME_UTC);
            xt.sec  += (time/1000);
            xt.nsec += (time%1000)*1000000;
            boost::timed_mutex::scoped_timed_lock scoped_lock(mux,xt);

            if(scoped_lock.locked())
            {
                if(getCollection().empty())
                {
                    wRes = dataAvailable.timed_wait(scoped_lock,xt); // monitor mutex released automatically
                    // thread must have been signalled AND mutex reacquired to reach here OR RW_THR_TIMEOUT
                    if(wRes == true && !getCollection().empty())
                    {
                        data = getCollection().front();
                        pval = data.dataPointer;
                        getCollection().pop_front();
                    }
                }
                else
                {
                    data = getCollection().front();
                    pval = data.dataPointer;
                    getCollection().pop_front();
                }
            }
            // mutex automatically released in LockGuard destructor
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl;}
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
                    QueueDataStruct data;
                    data.insertOrder = getNextInsertValue();
                    data.dataPointer = pt;
                    getCollection().push_back(data);
                    dataAvailable.notify_one();
                    putWasDone = true;

                    if(!gConfigParms.isTrue("YUKON_NOSORT_QUEUES"))
                    {
                        try
                        {
                            getCollection().sort( std::greater<struct QueueDataStruct>() );
                        }
                        catch(...)
                        {
                            cerr << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }
            }
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl;}
        }

        return putWasDone;
    }

    size_t   entries(void) const       // QueryQue.
    {
        size_t sz = 0;
        if(_col)
        {
            sz = _col->size();
        }
        return sz;
    }

    size_t   size(void)        // how big may it be?
    {
        size_t sz = 0;
        if(_col)
        {
            sz = _col->size();
        }
        return sz;
    }

    BOOL  isFull(void)
    {
        return(FALSE);
    }

    void clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        delete_list();
        getCollection().clear();
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

    template<class input_iterator_t, class function_t> inline
    void operand_for_each(input_iterator_t first, input_iterator_t last, function_t operation,  void *parameter)
    {
        for(; first != last; ++first)
            operation((*first).dataPointer, parameter);

    }
    void apply(void (*fn)(T*&,void*), void* d)
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        operand_for_each(getCollection().begin(),getCollection().end(),fn,d);
    }

    void resetCollection()
    {
        try
        {
            if(_col)
            {
                try
                {
                    delete_list();
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                try
                {
                    _col->clear();
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                delete _col;
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        _col = 0;     // No matter what, I exit leaving the old one behind!
    }

    inline void delete_list( )
    {
        QueueDataStruct data;
        try
        {
            if(_col)
            {
                while(!_col->empty())
                {
                    data = _col->front();
                    T *pval = data.dataPointer;
                    _col->pop_front();
                    delete pval;
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
};

// Template Queuing class
template <class T>
class IM_EX_CTIBASE CtiFIFOQueue
{
private:

    std::list<T*> *_col;
    boost::condition           dataAvailable;
    mutable boost::timed_mutex                mux;

    string       _name;

    struct boost::xtime xt_eot;


    std::list<T*> & getCollection()
    {
        if(!_col)
            _col = new std::list<T*>;

        return *_col;
    }

    /* must have the mux */
    void resetCollection()
    {
        try
        {
            if(_col)
            {
                try
                {
                    delete_list();
                    _col->clear();
                }
                catch(...)
                {
                    {
                        cerr << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                delete _col;
            }
        }
        catch(...)
        {
            {
                cerr << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        _col = 0;     // No matter what, I exit leaving the old one behind!
    }

public:

    CtiFIFOQueue() :
    _col(0),
    _name("Unnamed Queue")
    {
        xt_eot.sec  = INT_MAX;
        xt_eot.nsec = 0;
    }

    virtual ~CtiFIFOQueue()
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        delete_list();
        getCollection().clear();
    }

    void putQueue(T *pt)
    {
        try
        {
            if(pt != NULL)
            {
                {
                    boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
                    getCollection().push_back(pt);
                    dataAvailable.notify_one();
                    // mutex automatically released in LockGuard destructor
                }
            }
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl;}
        }
    }

    T* getQueue()
    {
        T *pval = NULL;
        try
        {
            boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
            while( getCollection().empty() )
            {
                dataAvailable.wait(scoped_lock);
            }
            pval = getCollection().front();
            getCollection().pop_front();

        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl;}
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
            xt.nsec += (time%1000)*1000000;

            boost::timed_mutex::scoped_timed_lock scoped_lock(mux,xt);

            if(scoped_lock.locked())
            {

                if(getCollection().empty())
                {
                    wRes = dataAvailable.timed_wait(scoped_lock,xt); // monitor mutex released automatically
                    // thread must have been signalled AND mutex reacquired to reach here OR RW_THR_TIMEOUT
                    if(wRes == true && !getCollection().empty())
                    {
                        pval = getCollection().front();
                        getCollection().pop_front();
                    }
                }
                else
                {
                    pval = getCollection().front();
                    getCollection().pop_front();
                }
            }
            // mutex automatically released in LockGuard destructor
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl;}
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
                    getCollection().push_back(pt);
                    dataAvailable.notify_one();
                    // mutex automatically released in LockGuard destructor
                    putWasDone = true;
                }
            }
        }
        catch(...)
        {
            {   CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " **** Exception **** " << __FILE__ << " (" << __LINE__ << ")" << endl;}
        }

        return putWasDone;
    }

    /*
     *  Allows us to shrink or grow the queue in question..
     */
    size_t   resize(size_t addition = 1)
    {
        return size();
    }


    size_t   entries(void) const       // QueryQue.
    {
        return size();
    }

    size_t   size(void) const        // how big may it be?
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        size_t sz = 0;
        if(_col)
        {
            sz = _col->size();
        }
        return sz;
    }

    BOOL  isFull(void)
    {
        return(FALSE);
    }

    void clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
        delete_list();
        getCollection().clear();
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
        for(; _F != _L; ++_F)
            _Op(*_F, d);

    }
    void apply(void (*fn)(T*&,void*), void* d)
    {
        {
            boost::timed_mutex::scoped_timed_lock scoped_lock(mux, xt_eot);
            //getCollection().apply(fn,d);
            ts_for_each(getCollection().begin(),getCollection().end(),fn,d);
        }
    }

    inline void delete_list( )
    {
        if(_col)
        {
            while(!_col->empty())
            {
                T *pval = _col->front();
                _col->pop_front();
                delete pval;
            }
        }
    }
};

#endif //#ifndef __CtiQUE_H__
