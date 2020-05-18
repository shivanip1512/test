#pragma once

#include "cparms.h"
#include "dlldefs.h"
#include "logger.h"
#include "utility.h"

#include <boost/thread/condition.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/xtime.hpp>

#include <queue>
#include <set>


// Template Queuing class
// This now uses class C as the sorting operator, and guarantees fifo and priority operation
// What this means is when using class C (a <(C compare)> b) == true, a will come off the list first.
// If a and b are identical (a<C>b and b<C>a == false) then the ordering is depending on insertion order
template <class T,  class C>
class CtiQueue
{
public:
    struct QueueDataStruct
    {
        unsigned long insertOrder;
        T* dataPointer;
        QueueDataStruct() : dataPointer(NULL), insertOrder(0){};

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

    typedef std::set<QueueDataStruct, std::greater<QueueDataStruct>> queue_t;
    typedef boost::timed_mutex::scoped_timed_lock lock_t;

    queue_t      *_col;
    std::string   _name;
    unsigned int  _insertValue;
    bool          _interruptBlockingRead;

    boost::xtime xt_eot;


    queue_t &getCollection()
    {
        if(!_col)
            _col = new queue_t;

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
    _name("Unnamed Queue"),
    _interruptBlockingRead(false)
    {
        xt_eot.sec  = INT_MAX;
        xt_eot.nsec = 0;
    }

    CtiQueue(std::string name) :
        _col(0),
        _interruptBlockingRead(false)
    {
        xt_eot.sec = INT_MAX;
        xt_eot.nsec = 0;
        _name = name;
    }

    virtual ~CtiQueue()
    {
        lock_t scoped_lock(mux, xt_eot);
        resetCollection();
    }

   /**
    * interrupt a wait on the current or the next getQueue().
    * if the collection is empty the next getQueue() will return immediately with a NULL pointer
    * if the collection is not empty, interrupt read flag is reset and getQueue return a valid object has it normally does
    */
    void interruptBlockingRead()
    {
        lock_t scoped_lock(mux, xt_eot);

        try
        {
            dataAvailable.notify_one();
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        _interruptBlockingRead = true;
    }

    void putQueue(T *pt)
    {
        lock_t scoped_lock(mux, xt_eot);
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
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                try
                {
                    QueueDataStruct tempStruct;
                    tempStruct.dataPointer = pt;
                    tempStruct.insertOrder = getNextInsertValue();
                    getCollection().insert(tempStruct);
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                // mutex automatically released in LockGuard destructor
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

            resetCollection();     // Dump the queue?
        }
    }

    T* getQueue()
    {
        T *pval = NULL;
        QueueDataStruct data;
        try
        {
            lock_t scoped_lock(mux, xt_eot);
            while( getCollection().empty() )
            {
                if( _interruptBlockingRead )
                {
                    // if the collection is empty, reset the interrupt flag and return a NULL object
                    _interruptBlockingRead = false;
                    return NULL;
                }

                dataAvailable.wait(scoped_lock);
            }

            pval = (getCollection().begin())->dataPointer;
            getCollection().erase(getCollection().begin());

            // make sure the interrupt flag is false
            _interruptBlockingRead = false;
        }
        catch ( boost::thread_interrupted & )
        {
            throw;
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            boost::xtime_get(&xt, boost::TIME_UTC_);
            xt.sec  += (time/1000);
            xt.nsec += (time%1000)*1000000;

            lock_t scoped_lock(mux,xt); //This now does the lock!

            if( scoped_lock.owns_lock() )
            {
                if(getCollection().empty())
                {
                    if( _interruptBlockingRead )
                    {
                        // if the collection is empty, reset the interrupt flag and return a NULL object
                        _interruptBlockingRead = false;
                        return NULL;
                    }

                    wRes = dataAvailable.timed_wait(scoped_lock,xt); // monitor mutex released automatically
                    // thread must have been signalled AND mutex reacquired to reach here OR mux timed out
                    if(wRes == true && !getCollection().empty())
                    {
                        pval = (getCollection().begin())->dataPointer;
                        getCollection().erase(getCollection().begin());
                    }
                }
                else
                {
                    pval = (getCollection().begin())->dataPointer;
                    getCollection().erase(getCollection().begin());
                }

                // make sure the interrupt flag is set to false
                _interruptBlockingRead = false;
            }
            // mutex automatically released in LockGuard destructor
        }
        catch ( boost::thread_interrupted & )
        {
            throw;
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                lock_t scoped_lock(mux, xt_eot);
                {
                    QueueDataStruct data;
                    data.insertOrder = getNextInsertValue();
                    data.dataPointer = pt;
                    getCollection().insert(data);
                    dataAvailable.notify_one();
                    putWasDone = true;
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

    BOOL  isEmpty(void)        // how big may it be?
    {
        bool empty = true;
        if(_col)
        {
            empty = _col->empty();
        }
        return empty;
    }

    void clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        lock_t scoped_lock(mux, xt_eot);
        deleteCollection();
        getCollection().clear();
    }

    std::string getName() const
    {
        return _name;
    }

    CtiQueue< T, C > & setName(const std::string &str)
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
    void apply(void (*fn)(T*,void*), void* d)
    {
        lock_t scoped_lock(mux, xt_eot);
        operand_for_each(getCollection().begin(),getCollection().end(),fn,d);
    }

    std::size_t getQueueMemoryConsumption()
    {
        lock_t scoped_lock(mux, xt_eot);

        size_t consumption = 0;
        for ( const auto & entry : getCollection() )
        {
            consumption += calculateMemoryConsumption( entry.dataPointer );
        }

        return consumption;
    }

    template<class Op>
    void erase_if(Op pred)
    {
        lock_t scoped_lock(mux, xt_eot);
        if(_col && !_col->empty())
        {
            queue_t::iterator itr     = _col->begin(),
                              itr_end = _col->end();

            while( itr != itr_end )
            {
                if( itr->dataPointer && pred(*(itr->dataPointer)) )
                {
                    delete itr->dataPointer;
                    itr = _col->erase(itr);
                }
                else
                {
                    itr++;
                }
            }
        }
    }

    void resetCollection()
    {
        try
        {
            if(_col)
            {
                try
                {
                    deleteCollection();
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                try
                {
                    _col->clear();
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                delete _col;
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        _col = 0;     // No matter what, I exit leaving the old one behind!
    }

    inline void deleteCollection( )
    {
        QueueDataStruct data;
        try
        {
            if(_col)
            {
                while(!_col->empty())
                {
                    delete (_col->begin())->dataPointer;
                    _col->erase(_col->begin());
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
};

// Template Queuing class
template <class T>
class CtiFIFOQueue
{
private:

    std::list<T*> _col;
    boost::condition           dataAvailable;
    mutable boost::timed_mutex mux;

    typedef boost::timed_mutex::scoped_timed_lock lock_t;

    std::string       _name;

    struct boost::xtime xt_eot;

    /* must have the mux */
    void resetCollection()
    {
        try
        {
            delete_container(_col);
            _col.clear();
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

public:

    CtiFIFOQueue() :
    _name("Unnamed Queue")
    {
        xt_eot.sec  = INT_MAX;
        xt_eot.nsec = 0;
    }

    virtual ~CtiFIFOQueue()
    {
        lock_t scoped_lock(mux, xt_eot);
        resetCollection();
    }

    void putQueue(T *pt)
    {
        try
        {
            if(pt != NULL)
            {
                lock_t scoped_lock(mux, xt_eot);
                _col.push_back(pt);
                dataAvailable.notify_one();
                // mutex automatically released in LockGuard destructor
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

    T* getQueue()
    {
        T *pval = NULL;
        try
        {
            lock_t scoped_lock(mux, xt_eot);
            while( _col.empty() )
            {
                dataAvailable.wait(scoped_lock);
            }
            pval = _col.front();
            _col.pop_front();

        }
        catch ( boost::thread_interrupted & )
        {
            throw;
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            boost::xtime_get(&xt, boost::TIME_UTC_);
            xt.sec  += (time/1000);
            xt.nsec += (time%1000)*1000000;

            lock_t scoped_lock(mux,xt);  //This now does the lock!

            if( scoped_lock.owns_lock() )
            {

                if(_col.empty())
                {
                    wRes = dataAvailable.timed_wait(scoped_lock,xt); // monitor mutex released automatically
                    // thread must have been signalled AND mutex reacquired to reach here OR mux timed out
                    if(wRes == true && !_col.empty())
                    {
                        pval = _col.front();
                        _col.pop_front();
                    }
                }
                else
                {
                    pval = _col.front();
                    _col.pop_front();
                }
            }
            // mutex automatically released in LockGuard destructor
        }
        catch ( boost::thread_interrupted & )
        {
            throw;
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                lock_t scoped_lock(mux, xt_eot);
                _col.push_back(pt);
                dataAvailable.notify_one();
                // mutex automatically released in LockGuard destructor
                putWasDone = true;
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        return putWasDone;
    }

    size_t   entries(void) const       // QueryQue.
    {
        return size();
    }

    size_t   size(void) const        // how big may it be?
    {
        lock_t scoped_lock(mux, xt_eot);
        return _col.size();
    }

    void clearAndDestroy(void)      // Destroys pointed to objects as well.
    {
        lock_t scoped_lock(mux, xt_eot);
        resetCollection();
    }

    std::string getName() const
    {
        return _name;
    }

    CtiFIFOQueue< T > & setName(const std::string &str)
    {
        _name = str;
        return *this;
    }
};



template<class T>
class CtiValueQueue
{
private:

    std::queue<T>   _col;

    boost::condition            _dataAvailable;
    mutable boost::timed_mutex  _mux;
    typedef boost::timed_mutex::scoped_timed_lock   lock_t;

    struct boost::xtime _xt_eot;

public:

    CtiValueQueue()
    {
        _xt_eot.sec  = INT_MAX;
        _xt_eot.nsec = 0;
    }

    std::size_t size() const
    {
        lock_t scoped_lock(_mux, _xt_eot);
        return _col.size();
    }

    bool empty() const
    {
        lock_t scoped_lock(_mux, _xt_eot);
        return _col.empty();
    }

    void putQueue(const T & item)
    {
        lock_t scoped_lock(_mux, _xt_eot);
        _col.push(item);
        _dataAvailable.notify_one();
    }

    T getQueue()
    {
        lock_t scoped_lock(_mux, _xt_eot);
        while(_col.empty())
        {
            _dataAvailable.wait(scoped_lock);
        }
        T value = _col.front();
        _col.pop();

        return value;
    }
};

