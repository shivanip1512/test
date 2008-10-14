#include "yukon.h"

#include "logger.h"

#include "readers_writer_lock.h"

namespace Cti {

readers_writer_lock_t::readers_writer_lock_t() :
    _writer_id(0),
    _writer_recursion(0)
{
    std::fill(_reader_ids,       _reader_ids       + MaxThreadCount, 0);
    std::fill(_reader_recursion, _reader_recursion + MaxThreadCount, 0);
}

inline void readers_writer_lock_t::acquire()                  {  acquireWrite();  }
inline bool readers_writer_lock_t::acquire(unsigned long ms)  {  return acquireWrite(ms);  }
inline bool readers_writer_lock_t::tryAcquire()               {  return tryAcquireWrite();  }

void readers_writer_lock_t::acquireRead()
{
    if( !current_thread_owns_any() )
    {
        _lock.acquireRead();
    }

    set_tid(LockType_Reader);
}

void readers_writer_lock_t::acquireWrite()
{
    if( !current_thread_owns_writer() )
    {
        if( current_thread_owns_reader() )
        {
            //  upgrades not currently allowed
            autopsy(__FILE__, __LINE__);
            throw;
        }

        _lock.acquireWrite();
    }

    set_tid(LockType_Writer);
}

bool readers_writer_lock_t::acquireRead(unsigned long milliseconds)
{
    if( !current_thread_owns_any() )
    {
        if( !_lock.acquireRead(milliseconds) )
        {
            return false;
        }
    }

    set_tid(LockType_Reader);

    return true;
}

bool readers_writer_lock_t::acquireWrite(unsigned long milliseconds)
{
    if( !current_thread_owns_writer() )
    {
        if( current_thread_owns_reader() )
        {
            //  upgrades not currently allowed
            autopsy(__FILE__, __LINE__);
            throw;
        }

        if( !_lock.acquireWrite(milliseconds) )
        {
            return false;
        }
    }

    set_tid(LockType_Writer);

    return true;
}

bool readers_writer_lock_t::tryAcquireRead()
{
    if( !current_thread_owns_any() && !_lock.tryAcquireRead() )
    {
        return false;
    }

    set_tid(LockType_Reader);

    return true;
}

bool readers_writer_lock_t::tryAcquireWrite()
{
    if( !current_thread_owns_writer() )
    {
        if( current_thread_owns_reader() )
        {
            //  upgrades not currently allowed
            autopsy(__FILE__, __LINE__);
            throw;
        }

        if( !_lock.tryAcquireWrite() )
        {
            return false;
        }
    }

    set_tid(LockType_Writer);

    return true;
}

void readers_writer_lock_t::release()
{
    clear_tid();

    //  if it's the last reader OR it's the last writer, release the lock
    if( !current_thread_owns_any() )
    {
        _lock.release();
    }
}

void readers_writer_lock_t::set_tid(LockType_t lock_type)
{
    if( _writer_id )
    {
        //  if we own the writer lock, we treat every acquisition afterward as a writer lock
        _writer_recursion++;
    }
    else if( lock_type == LockType_Reader )
    {
        set_reader_id(GetCurrentThreadId());
    }
    else if( lock_type == LockType_Writer )
    {
        _writer_id = GetCurrentThreadId();
    }
}

void readers_writer_lock_t::clear_tid()
{
    if( _writer_id )
    {
        if( _writer_recursion )
        {
            _writer_recursion--;
        }
        else
        {
            _writer_id = 0;
        }
    }
    else
    {
        clear_reader_id(GetCurrentThreadId());
    }
}


int readers_writer_lock_t::find_reader_id(thread_id_t tid) const
{
    int i = 0;

    for( ; i < MaxThreadCount; ++i )
    {
        if( !_reader_ids[i] || _reader_ids[i] == tid )
        {
            break;
        }
    }

    return i;
}


void readers_writer_lock_t::set_reader_id(thread_id_t tid)
{
    int i = find_reader_id(tid);

    //  wasn't in the list
    if( _reader_ids[i] != tid )
    {
        //  find the first available entry
        for( ; i < MaxThreadCount; ++i )
        {
            //  try to exchange - if someone else got there first, the exchange will fail and the return will be nonzero
            if( !InterlockedCompareExchange(reinterpret_cast<PVOID *>(_reader_ids + i),
                                            reinterpret_cast<PVOID>(tid),
                                            reinterpret_cast<PVOID>(0)) )
            {
                break;
            }
        }
    }

    if( i == MaxThreadCount )
    {
        //  out of threads
        autopsy(__FILE__, __LINE__);
        throw;
    }

    ++_reader_recursion[i];
}


void readers_writer_lock_t::clear_reader_id(thread_id_t tid)
{
    int i = find_reader_id(tid);

    if( i == MaxThreadCount )
    {
        //  It wasn't in the list - never inserted?
        autopsy(__FILE__, __LINE__);
        throw;
    }

    if( !_reader_recursion[i] )
    {
        //  clear_tid() called one too many times
        autopsy(__FILE__, __LINE__);
        throw;
    }

    --_reader_recursion[i];
}


bool readers_writer_lock_t::current_thread_owns_writer() const
{
    //  threadsafe, since _writer_id is written atomically
    return _writer_id && (_writer_id == GetCurrentThreadId());
}

bool readers_writer_lock_t::current_thread_owns_reader() const
{
    thread_id_t tid = GetCurrentThreadId();

    int i = find_reader_id(tid);

    if( i < MaxThreadCount && _reader_ids[i] == tid && _reader_recursion[i] )
    {
        return true;
    }

    return false;
}

bool readers_writer_lock_t::current_thread_owns_both() const
{
    return current_thread_owns_writer() && current_thread_owns_reader();
}

bool readers_writer_lock_t::current_thread_owns_any() const
{
    return current_thread_owns_writer() || current_thread_owns_reader();
}

readers_writer_lock_t::thread_id_t readers_writer_lock_t::lastAcquiredByTID() const
{
    return _writer_id;
}

readers_writer_lock_t::operator std::string()
{
    std::string str;

    if( _writer_id )
    {
        str  = "w:";
        str += CtiNumStr(_writer_id).hex();

        if( _writer_recursion )
        {
            str += "/";
            str += CtiNumStr(_writer_recursion);
        }
    }
    else
    {
        int i = 0;

        str = "r:";

        for( ; i < MaxThreadCount && _reader_ids[i]; ++i )
        {
            if( _reader_recursion[i] )
            {
                str += CtiNumStr(_reader_ids[i]).hex();
                str += "/";
                str += CtiNumStr(_reader_recursion[i]);
                str += ",";
            }
        }

        if( !i )
        {
            str = "(no reader thread ids!)";
        }
    }

    return str;
}

};
