#include "yukon.h"

#include "logger.h"

#include "readers_writer_lock.h"

namespace Cti {

readers_writer_lock_t::readers_writer_lock_t() :
    _writer_id(0),
    _writer_recursion(0)
{
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
        thread_id_t thread_id = GetCurrentThreadId();
        bool found;

        {
            bookkeeping_reader_guard_t guard(_bookkeeping_lock);

            if( found = (_reader_ids.find(thread_id) != _reader_ids.end()) )
            {
                ++_reader_ids[thread_id];
            }
        }

        if( !found )
        {
            bookkeeping_writer_guard_t guard(_bookkeeping_lock);

            _reader_ids[thread_id] = 1;
        }
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
        thread_id_t thread_id = GetCurrentThreadId();

        {
            bookkeeping_reader_guard_t guard(_bookkeeping_lock);

            if( _reader_ids.find(thread_id) == _reader_ids.end() ||
                !_reader_ids[thread_id] )
            {
                //  clear_tid() called one too many times
                autopsy(__FILE__, __LINE__);
                throw;
            }

            _reader_ids[thread_id]--;
        }
    }
}

bool readers_writer_lock_t::current_thread_owns_writer() const
{
    //  threadsafe, since _writer_id is written atomically
    return _writer_id && (_writer_id == GetCurrentThreadId());
}

bool readers_writer_lock_t::current_thread_owns_reader() const
{
    thread_id_t thread_id = GetCurrentThreadId();

    {
        bookkeeping_reader_guard_t guard(_bookkeeping_lock);

        return (_reader_ids.find(thread_id) != _reader_ids.end()) && _reader_ids.find(thread_id)->second;
    }
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
    bookkeeping_writer_guard_t guard(_bookkeeping_lock);

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
        if( !_reader_ids.empty() )
        {
            id_coll_t::const_iterator itr     = _reader_ids.begin(),
                                      itr_end = _reader_ids.end();

            str = "r:";

            for( ; itr != itr_end && itr->second; itr++ )
            {
                str += CtiNumStr(itr->first).hex();
                str += "/";
                str += CtiNumStr(itr->second);
                str += ",";
            }
        }
        else
        {
            str = "(no reader thread ids!)";
        }
    }

    return str;
}

};
