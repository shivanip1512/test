#include "precompiled.h"

#include "readers_writer_lock.h"

#include "boost/date_time/microsec_time_clock.hpp"

#include "utility.h"

#include <sstream>

using namespace std;

namespace Cti {

readers_writer_lock_t::readers_writer_lock_t() :
    _writer_id(0),
    _writer_recursion(0)
{
    fill(_reader_ids,       _reader_ids       + MaxThreadCount,  0);
    fill(_reader_recursion, _reader_recursion + MaxThreadCount,  0);
}

inline void readers_writer_lock_t::acquire()                  {  acquireWrite();  }
inline bool readers_writer_lock_t::acquire(unsigned long ms)  {  return acquireWrite(ms);  }
inline bool readers_writer_lock_t::tryAcquire()               {  return tryAcquireWrite();  }

void readers_writer_lock_t::acquireRead()
{
    //  If we own a prior reader OR writer lock, we will inherit it
    if( !current_thread_owns_reader() &&
        !current_thread_owns_writer() )
    {
        _lock.lock_shared();
    }

    add_reader();
}

void readers_writer_lock_t::acquireWrite()
{
    if( !current_thread_owns_writer() )
    {
        if( current_thread_owns_reader() )
        {
            //  upgrades not currently allowed
            autopsy(__FILE__, __LINE__);
            terminate_program();
        }

        _lock.lock();
    }

    add_writer();
}

bool readers_writer_lock_t::acquireRead(unsigned long milliseconds)
{
    //  If we own a prior reader OR writer lock, we will inherit it
    if( !current_thread_owns_reader() &&
        !current_thread_owns_writer() )
    {
        if( !_lock.timed_lock_shared(boost::posix_time::milliseconds(milliseconds)) )
        {
            return false;
        }
    }

    add_reader();

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
            terminate_program();
        }

        if( !_lock.timed_lock(boost::posix_time::milliseconds(milliseconds)) )
        {
            return false;
        }
    }

    add_writer();

    return true;
}

bool readers_writer_lock_t::tryAcquireRead()
{
    //  If we own a prior reader OR writer lock, we will inherit it
    if( !current_thread_owns_reader() &&
        !current_thread_owns_writer() &&
        !_lock.try_lock_shared() )
    {
        return false;
    }

    add_reader();

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
            terminate_program();
        }

        if( !_lock.try_lock() )
        {
            return false;
        }
    }

    add_writer();

    return true;
}

inline void readers_writer_lock_t::release()  {  releaseWrite();  }

void readers_writer_lock_t::releaseWrite()
{
    //  if this is the last recursive writer lock, release the exclusive lock
    if( !remove_writer() )
    {
        _lock.unlock();
    }
}

void readers_writer_lock_t::releaseRead()
{
    //  if this is the last recursive reader lock, release the shared lock
    //    Make sure we don't try to release the shared lock if we inherited from a writer!
    if( !remove_reader() && !current_thread_owns_writer() )
    {
        _lock.unlock_shared();
    }
}

//  Tracks writer recursion so we know when to release the lock
void readers_writer_lock_t::add_writer()
{
    if( !_writer_recursion++ )
    {
        _writer_id = GetCurrentThreadId();
    }
}

//  Tracks writer recursion so we know when to release the lock
void readers_writer_lock_t::add_reader()
{
    const thread_id_t tid = GetCurrentThreadId();

    unsigned reader_index = find_reader_index(tid);

    if( _reader_ids[reader_index] != tid )
    {
    //  we aren't in the list yet, so we need to add ourselves
        //
        //  try to insert our id to the list - if someone else got there first, the exchange will fail and the return will be nonzero
        while( reader_index < MaxThreadCount
               && InterlockedCompareExchange(reinterpret_cast<volatile LONG *>(_reader_ids + reader_index), tid, 0L) )
        {
            //  insert failed, try the next index
            ++reader_index;
        }
    }

    if( reader_index == MaxThreadCount )
    {
        //  Out of thread storage space!
        autopsy(__FILE__, __LINE__);
        terminate_program();
    }

    ++_reader_recursion[reader_index];
}


//  Return value indicates if we still hold a recursive lock
bool readers_writer_lock_t::remove_reader()
{
    const thread_id_t tid = GetCurrentThreadId();

    unsigned reader_index = find_reader_index(tid);

    if( _reader_ids[reader_index] != tid )
    {
        //  It wasn't in the list - never inserted?
        autopsy(__FILE__, __LINE__);
        terminate_program();
    }

    if( !_reader_recursion[reader_index] )
    {
        //  clear_tid() called one too many times
        autopsy(__FILE__, __LINE__);
        terminate_program();
    }

    return --_reader_recursion[reader_index];
}


//  Return value indicates if we still hold a recursive lock
bool readers_writer_lock_t::remove_writer()
{
    if( !--_writer_recursion )
    {
        _writer_id = 0;
    }

    return _writer_id;
}


unsigned readers_writer_lock_t::find_reader_index(thread_id_t tid) const
{
    unsigned reader_index = 0;

    for( ; reader_index < MaxThreadCount; ++reader_index )
    {
        //  break on first empty OR if we found our thread id
        if( !_reader_ids[reader_index] || _reader_ids[reader_index] == tid )
        {
            break;
        }
    }

    if( reader_index == MaxThreadCount )
    {
        //  Out of thread storage space!
        autopsy(__FILE__, __LINE__);
        terminate_program();
    }

    return reader_index;
}


bool readers_writer_lock_t::current_thread_owns_writer() const
{
    //  threadsafe, since _writer_id is written atomically
    return _writer_id && (_writer_id == GetCurrentThreadId());
}

bool readers_writer_lock_t::current_thread_owns_reader() const
{
    const thread_id_t tid = GetCurrentThreadId();

    unsigned reader_index = find_reader_index(tid);

    return _reader_ids[reader_index] == tid && _reader_recursion[reader_index];
}

bool readers_writer_lock_t::current_thread_owns_both() const
{
    return current_thread_owns_writer() && current_thread_owns_reader();
}

bool readers_writer_lock_t::current_thread_owns_any() const
{
    return current_thread_owns_writer() || current_thread_owns_reader();
}

string readers_writer_lock_t::lastAcquiredByTID() const
{
    return *this;
}

readers_writer_lock_t::operator string() const
{
    stringstream s;

    s << "[";

    if( _writer_recursion )
    {
        s << "w:" << hex << _writer_id << "," << dec << _writer_recursion;
    }

    bool readers = false;

    for( unsigned i = 0; i < MaxThreadCount && _reader_ids[i]; ++i )
    {
        if( _reader_recursion[i] )
        {
            if( readers )
            {
                s << ",";
            }
            else
            {
                if( _writer_recursion )
                {
                    s << "/";
                }

                s << "r:";

                readers = true;
            }

            s << hex << _reader_ids[i] << "," << dec << _reader_recursion[i];
        }
    }

    if( !_writer_recursion && !readers )
    {
        s << "unlocked";
    }

    s << "]";

    return s.str();
}

void readers_writer_lock_t::terminate_program() const
{
    terminate();
}

};
