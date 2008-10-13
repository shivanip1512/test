#ifndef __READERS_WRITERS_LOCK_H__
#define __READERS_WRITERS_LOCK_H__

#include <rw/thr/rwlock.h>
//#include <rw/thr/semaphore.h>
#include <ostream>

#include "dlldefs.h"
#include "critical_section.h"
#include "guard.h"

namespace Cti {

class IM_EX_CTIBASE readers_writer_lock_t
{
public:

    typedef DWORD thread_id_t;

private:

    typedef std::map<thread_id_t, unsigned> id_coll_t;
    typedef RWReadersWriterLock::ReadLockGuard  bookkeeping_reader_guard_t;
    typedef RWReadersWriterLock::WriteLockGuard bookkeeping_writer_guard_t;

    RWReadersWriterLock _lock;

    mutable RWReadersWriterLock _bookkeeping_lock;

    //RWSemaphore _write_signal, _upgrade_signal;

    id_coll_t   _reader_ids;
    thread_id_t _writer_id;
    unsigned    _writer_recursion;

    enum LockType_t
    {
        LockType_None,
        LockType_Reader,
        LockType_Writer,
    };

    //  thread ID reporting for deadlock debugging
    void set_tid(LockType_t lock_type);
    void clear_tid();

    bool current_thread_owns_writer() const;
    bool current_thread_owns_reader() const;
    bool current_thread_owns_both() const;
    bool current_thread_owns_any() const;

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

    //  convenience functions - all delegate to acquireWrite()
    void acquire();
    bool acquire(unsigned long milliseconds);
    bool tryAcquire();

    void release();

    operator std::string();

    thread_id_t lastAcquiredByTID() const;
};

};

#endif

