#include "yukon.h"

#include "logger.h"

#include "readers_writer_lock.h"

namespace Cti {

readers_writer_lock_t::readers_writer_lock_t() :
    //_writer_signal(1),
    _writer_id(0),
    _writer_recursion(0)
{
}

inline void readers_writer_lock_t::acquire()                  {  acquireWrite();  }
inline bool readers_writer_lock_t::acquire(unsigned long ms)  {  return acquireWrite(ms);  }
inline bool readers_writer_lock_t::tryAcquire()               {  return tryAcquireWrite();  }

void readers_writer_lock_t::acquireRead()
{
    bool recursive = false;

    {
        bookkeeping_reader_guard_t guard(_bookkeeping_lock);
        recursive = current_thread_owns_any();
    }

    if( !recursive )
    {
        _lock.acquireRead();
    }

    {
        bookkeeping_writer_guard_t guard(_bookkeeping_lock);
        set_tid(LockType_Reader);
    }
}

void readers_writer_lock_t::acquireWrite()
{
    bool recursive       = false;
    bool upgrade         = false;

    {
        bookkeeping_reader_guard_t guard(_bookkeeping_lock);

        recursive = current_thread_owns_writer();
        upgrade   = current_thread_owns_reader();
    }

    if( !recursive )
    {
        assert(!upgrade && "Read-to-write upgrades not allowed!");

        /*if( upgrade )
        {
            //  if we're an upgrade, we don't acquire the semaphore;
            //    this effectively moves all upgrades to the head of the write queue
            _lock.release();
        }
        else
        {
            _upgrade_signal.acquire();
        }*/

        _lock.acquireWrite();
    }

    //  we must have the writer lock, so no need to acquire the bookkeeping lock
    set_tid(LockType_Writer);
}

bool readers_writer_lock_t::acquireRead(unsigned long milliseconds)
{
    bool acquired  = true;
    bool recursive = false;

    {
        bookkeeping_reader_guard_t guard(_bookkeeping_lock);
        recursive = current_thread_owns_any();
    }

    if( !recursive )
    {
        acquired = _lock.acquireRead(milliseconds);
    }

    if( acquired )
    {
        bookkeeping_writer_guard_t guard(_bookkeeping_lock);
        set_tid(LockType_Reader);
    }

    return acquired;
}

bool readers_writer_lock_t::acquireWrite(unsigned long milliseconds)
{
    bool acquired  = true;
    bool recursive = false;
    bool upgrade   = false;

    {
        bookkeeping_reader_guard_t guard(_bookkeeping_lock);
        recursive = current_thread_owns_writer();
        upgrade   = current_thread_owns_reader();
    }

    if( !recursive )
    {
        assert(!upgrade && "Read-to-write upgrades not allowed!");

        /*if( upgrade )
        {
            _lock.release();

            if( !(acquired = _lock.acquireWrite(milliseconds)) )
            {
                do
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Thread " << GetCurrentThreadId() << " attempting to acquire read mux after failed upgrade; owned by " << operator string() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;

                } while( !_lock.acquireRead(30000) );
            }
        }
        else if( _write_signal.acquire(milliseconds) != RW_THR_TIMEOUT )
        {
            acquired = _lock.acquireWrite();
        }*/

        acquired = _lock.acquireWrite(milliseconds);
    }

    if( acquired )
    {
        set_tid(LockType_Writer);
    }

    return acquired;
}

bool readers_writer_lock_t::tryAcquireRead()
{
    bool acquired  = false;
    bool recursive = false;

    {
        bookkeeping_reader_guard_t guard(_bookkeeping_lock);
        recursive = current_thread_owns_any();
    }

    if( recursive )
    {
        acquired = true;
    }
    else
    {
        acquired = _lock.tryAcquireRead();
    }

    if( acquired )
    {
        bookkeeping_writer_guard_t guard(_bookkeeping_lock);
        set_tid(LockType_Reader);
    }

    return acquired;
}

bool readers_writer_lock_t::tryAcquireWrite()
{
    bool recursive = false;
    bool acquired  = false;
    bool upgrade   = false;
    //bool write_owned = false;

    {
        bookkeeping_reader_guard_t guard(_bookkeeping_lock);

        recursive = current_thread_owns_writer();
        upgrade   = current_thread_owns_reader();

        //write_owned = _writer_id;

        if( !recursive )
        {
            assert(!upgrade && "Read-to-write upgrades not allowed!");

            /*if( upgrade )
            {
                if( !write_owned )
                {
                    _lock.release();

                    _lock.acquireWrite();
                }
            }
            else if( _write_signal.tryAcquire() )
            {
                acquired = _lock.acquireWrite();
            }*/
        }
    }

    if( !recursive )
    {
        acquired = _lock.tryAcquireWrite();
    }

    if( acquired )
    {
        set_tid(LockType_Writer);
    }

    return acquired;
}

void readers_writer_lock_t::release()
{
    bookkeeping_writer_guard_t guard(_bookkeeping_lock);

    //  if it's the last reader OR it's the last writer
    clear_tid();

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
        //  try to re-use a zeroed out entry to prevent us from deleting from the vector
        id_coll_t::iterator itr = std::find(_reader_ids.begin(), _reader_ids.end(), 0);

        if( itr != _reader_ids.end() )
        {
            *itr = GetCurrentThreadId();
        }
        else
        {
            _reader_ids.push_back(GetCurrentThreadId());
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
        id_coll_t::iterator itr = std::find(_reader_ids.begin(), _reader_ids.end(), GetCurrentThreadId());

        assert(itr != _reader_ids.end() && "clear_tid() called once too many times!");

        *itr = 0;
    }
}

bool readers_writer_lock_t::current_thread_owns_writer() const
{
    return _writer_id && (_writer_id == GetCurrentThreadId());
}

bool readers_writer_lock_t::current_thread_owns_reader() const
{
    return std::find(_reader_ids.begin(), _reader_ids.end(), GetCurrentThreadId()) != _reader_ids.end();
}

bool readers_writer_lock_t::current_thread_owns_both() const
{
    return current_thread_owns_writer() && current_thread_owns_reader();
}

bool readers_writer_lock_t::current_thread_owns_any() const
{
    return current_thread_owns_writer() || current_thread_owns_reader();
}

int readers_writer_lock_t::lastAcquiredByTID()
{
    bookkeeping_reader_guard_t guard(_bookkeeping_lock);

    return _writer_id;
}

readers_writer_lock_t::operator std::string()
{
    bookkeeping_reader_guard_t guard(_bookkeeping_lock);

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

            for( ; itr != itr_end && *itr; itr++ )
            {
                str += CtiNumStr(*itr).hex();
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
