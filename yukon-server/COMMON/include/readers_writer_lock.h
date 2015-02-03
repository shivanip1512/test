#pragma once

#include "dlldefs.h"
#include "critical_section.h"
#include "guard.h"

#include "boost/thread/shared_mutex.hpp"

namespace Cti {

class IM_EX_CTIBASE readers_writer_lock_t
{
public:

    typedef DWORD thread_id_t;

private:

    enum
    {
        //  This is a hard-coded limit on the number of threads that may ever access this lock.
        //    Note that if we create and destroy many threads, we will quickly run out of space for thread IDs.
        MaxThreadCount = 1024
    };

    boost::shared_mutex _lock;

    //  We could extend this to a linked-list implementation if necessary,
    //    but we should probably rethink this whole class if we ever get to that scale.
    thread_id_t volatile _reader_ids[MaxThreadCount];
    unsigned             _reader_recursion[MaxThreadCount];
    thread_id_t volatile _writer_id;
    unsigned             _writer_recursion;

    //  indicates the last index + 1 of the _reader_ids in use
    DWORD       volatile _reader_index_end;

    //  thread ID reporting for deadlock debugging
    void add_reader();
    void add_writer();
    bool remove_reader();
    bool remove_writer();

    unsigned find_reader_index(thread_id_t tid) const;

    bool current_thread_owns_writer() const;
    bool current_thread_owns_reader() const;
    bool current_thread_owns_both  () const;
    bool current_thread_owns_any   () const;

protected:

    virtual void terminate_program() const;

public:

    typedef CtiReadLockGuard<readers_writer_lock_t>  reader_lock_guard_t;
    typedef CtiLockGuard<readers_writer_lock_t>      writer_lock_guard_t;

    readers_writer_lock_t();

    void acquireRead();
    void acquireWrite();

    bool acquireRead(unsigned long milliseconds);
    bool acquireWrite(unsigned long milliseconds);

    bool tryAcquireRead();
    bool tryAcquireWrite();

    void releaseRead();
    void releaseWrite();

    //  convenience functions - all delegate to ...Write()
    void acquire();
    bool acquire(unsigned long milliseconds);
    bool tryAcquire();

    void release();

    operator std::string() const;

    std::string lastAcquiredByTID() const;
};

};


