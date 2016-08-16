#include <boost/test/unit_test.hpp>

#include "utility.h"
#include "std_helper.h"
#include "guard.h"
#include "mutex.h"
#include "critical_section.h"
#include "readers_writer_lock.h"
#include "millisecond_timer.h"

#include <boost/thread.hpp>
#include <boost/format.hpp>
#include <boost/algorithm/cxx11/all_of.hpp>
#include <boost/range/adaptor/transformed.hpp>

static std::string unlocked("[unlocked]");
static std::string parentTID;
static std::string childTID;

std::string makeTID(boost::thread::id threadid)
{
    std::stringstream tid;
    tid << "[w:" << std::hex << threadid << ",1]";
    return tid.str();
}


/**
Happy guard test.  We acquire it, and then release it.
**/
template <class T>
void happyGuardTest()
{
    static T lock;

    BOOST_CHECK( lock.lastAcquiredByTID() == 0 );

    {
        CTILOCKGUARD( T, guard,  lock );

        BOOST_CHECK( guard.isAcquired() );
        BOOST_CHECK( lock.lastAcquiredByTID() == GetCurrentThreadId() );
    }

    BOOST_CHECK( lock.lastAcquiredByTID() == 0 );
}

/**
Specialization for readers_writer_lock_t since it returns a string for lastAcquiredByTID()
**/
template<>
void happyGuardTest<Cti::readers_writer_lock_t>()
{
    static Cti::readers_writer_lock_t lock;

    BOOST_CHECK( lock.lastAcquiredByTID() == unlocked);

    {
        CTILOCKGUARD( Cti::readers_writer_lock_t, guard,  lock );

        BOOST_CHECK( guard.isAcquired() );
        BOOST_CHECK( lock.lastAcquiredByTID() == makeTID(boost::this_thread::get_id()));
    }

    BOOST_CHECK( lock.lastAcquiredByTID() == unlocked );
}

/*
Parent/Child guard test

The dance we're about to do:
    Parent aquires lock.
    Parent starts child thread, sets f1, and sleeps
    Child starts, sets f2, and blocks on lock
    Parent checks that they still have the thread
    Parent guard goes out of scope and sleeps
    Child acquires lock sets f3 and sleeps
    Parent verifies child has lock and waits for child to exit.
*/

int f1;     // Signal flags.  These are used concurrently by child and parent
int f2;     // but are intentionally not guarded because that's what the test
int f3;     // is all about.

/** Static lock uses in most of the tests. **/
template <class T>
static T lockGuardTestLock;

/** 
Generic test
**/
template <class T>
void lockGuardTest()
{
    BOOST_CHECK( lockGuardTestLock<T>.lastAcquiredByTID() == 0 );
    boost::thread testThread;

    f1 = 0;
    f2 = 0;
    f3 = 0;

    {
        CTILOCKGUARD( T, guard,  lockGuardTestLock<T> );

        BOOST_CHECK( guard.isAcquired() );
        BOOST_CHECK( lockGuardTestLock<T>.lastAcquiredByTID() == GetCurrentThreadId() );

        // Fire off the child, passing out thread id as an argument
        testThread = boost::thread( &lockGuardTestChild<T>, GetCurrentThreadId() );
        f1 = 1;

        Sleep( 20 );

        /* Worker should have run by now and set f2, but not f3 since it's blocked */
        BOOST_CHECK( f1 == 1 && f2 == 1 && f3 == 0 );

        BOOST_CHECK( lockGuardTestLock<T>.lastAcquiredByTID() == GetCurrentThreadId() );

    }  /* Guard is goes out of scope here */

    Sleep( 10 );
    /* Worker should have run by now and set f3 */
    BOOST_CHECK( f1 == 1 && f2 == 1 && f3 == 1 );

    BOOST_CHECK( lockGuardTestLock<T>.lastAcquiredByTID() != GetCurrentThreadId() );

    testThread.join();
    BOOST_CHECK( lockGuardTestLock<T>.lastAcquiredByTID() == 0 );
}

/** 
Generic test child process.
**/
template<class T>
void lockGuardTestChild(DWORD parentTid)
{
    Sleep( 10 );

    /* Parent should have set f1 first */
    BOOST_CHECK( f1 == 1 && f2 == 0 && f3 == 0 );

    f2 = 1;

    BOOST_CHECK( lockGuardTestLock<T>.lastAcquiredByTID() == parentTid );

    /* Try for lock, we will block */
    CTILOCKGUARD( T, lock,  lockGuardTestLock<T> );

    BOOST_CHECK( lockGuardTestLock<T>.lastAcquiredByTID() == GetCurrentThreadId() );
    f3 = 1;
    Sleep( 20 );
}

/** 
Guard timeout test.
We set the lock, fire off the child and it should block until the timeout.
*/

Cti::Timing::MillisecondTimer timer;

template<class T>
void lockGuardTestTimeout()
{
    timer.reset();

    BOOST_CHECK(lockGuardTestLock<T>.lastAcquiredByTID() == 0);
    boost::thread testThread;
    {
        CTILOCKGUARD(T, guard, lockGuardTestLock<T>);
        BOOST_TEST_MESSAGE(timer.elapsed() << ": Parent: lock acquired");

        BOOST_CHECK(guard.isAcquired());
        BOOST_CHECK(lockGuardTestLock<T>.lastAcquiredByTID() == GetCurrentThreadId());

        // Fire off the child, passing out thread id as an argument
        testThread = boost::thread(&lockGuardTestTimeoutChild<T>, GetCurrentThreadId());

        // Now we wait for the child to start and do it's 20 ms timeout thing.  If this
        // is too short, the child may not have a chance to start, causing a false positive.
        Sleep(1000);
    }  /* Lock is gone */
    BOOST_TEST_MESSAGE(timer.elapsed() << ": Parent: lock released");

    Sleep(10);
    testThread.join();
    BOOST_TEST_MESSAGE(timer.elapsed() << ": Parent: child is dead");

    BOOST_CHECK(lockGuardTestLock<T>.lastAcquiredByTID() == 0);
}

/** 
Guard timeout test child.
*/
template<class T>
void lockGuardTestTimeoutChild( DWORD parentTid )
{
    BOOST_TEST_MESSAGE(timer.elapsed() << ": Child: started and waiting for lock");
    /* Try for lock, we will block then fail after 20 ms */
    CTILOCKGUARD2( T, guard2, lockGuardTestLock<T>, 20 );
    BOOST_TEST_MESSAGE(timer.elapsed() << ": Child: guard complete");

    BOOST_CHECK(!guard2.isAcquired());
    BOOST_CHECK( lockGuardTestLock<T>.lastAcquiredByTID() != GetCurrentThreadId() );
}

BOOST_AUTO_TEST_SUITE( test_guard )

BOOST_AUTO_TEST_CASE(test_Mutex_happyPath)
{
    happyGuardTest<CtiMutex>();
}

BOOST_AUTO_TEST_CASE(test_CriticalSection_happyPath)
{
    happyGuardTest<CtiCriticalSection>();
}

BOOST_AUTO_TEST_CASE(test_readers_writer_lock_happyPath)
{
    happyGuardTest<Cti::readers_writer_lock_t>();
}

BOOST_AUTO_TEST_CASE(test_guard_CtiMutex_lock)
{
    lockGuardTest<CtiMutex>();
}

BOOST_AUTO_TEST_CASE(test_guard_CtiCriticalSection_lock)
{
    lockGuardTest<CtiCriticalSection>();
}

/** Static lock uses in readers_writer lock tests. **/
Cti::readers_writer_lock_t lockGuardTestRW;

extern void lockGuardTestChildRW(DWORD parentTid);
/** Readers_writer lock test. **/
BOOST_AUTO_TEST_CASE(test_guard_test_readers_writer_lock)
{
    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == unlocked);
    boost::thread testThread;

    f1 = 0;
    f2 = 0;
    f3 = 0;

    {
        CTILOCKGUARD( Cti::readers_writer_lock_t, guard,  lockGuardTestRW );

        parentTID = makeTID(boost::this_thread::get_id());

        BOOST_CHECK( guard.isAcquired() );
        BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID()==parentTID );

        // Fire off the child, passing out thread id as an argument
        testThread = boost::thread( &lockGuardTestChildRW, GetCurrentThreadId() );
        childTID = makeTID( testThread.get_id() );

        f1 = 1;

        Sleep( 20 );

        /* Worker should have run by now and set f2, but not f3 since it's blocked */
        BOOST_TEST_MESSAGE("f1 == " << f1 << " && f2 == " << f2 << " && f3 == " << f3);
        BOOST_CHECK( f1 == 1 && f2 == 1 && f3 == 0 );
        BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID()==parentTID );

    }  /* Guard is goes out of scope here */

    Sleep( 10 );
    /* Worker should have run by now and set f3 */
    BOOST_CHECK( f1 == 1 && f2 == 1 && f3 == 1 );

    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == childTID );

    testThread.join();
    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == unlocked);
}

/** Readers_writer lock test child. **/
void lockGuardTestChildRW(DWORD parentTid)
{
    Sleep( 10 );

    /* Parent should have set f1 first */
    BOOST_CHECK( f1 == 1 && f2 == 0 && f3 == 0 );

    f2 = 1;

    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == parentTID );

    /* Try for lock, we will block */
    CTILOCKGUARD( Cti::readers_writer_lock_t , lock,  lockGuardTestRW );

    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == childTID );
    f3 = 1;
    Sleep( 20 );
}

/** Test CtiMutex timeout. **/
BOOST_AUTO_TEST_CASE(test_guard_CtiMutex_timeout)
{
    lockGuardTestTimeout<CtiMutex>();
}

// There is no timed version for CtiCriticalSection
//BOOST_AUTO_TEST_CASE(test_guard_CtiCriticalSection_timeout)

extern void lockGuardTestTimeoutChildRW(DWORD parentTid);
/** Test readers_writer timeout. **/
BOOST_AUTO_TEST_CASE(test_guard_test_readers_writer_lock_timeout)
{
    timer.reset();

    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == unlocked);
    boost::thread testThread;

    {
        CTILOCKGUARD( Cti::readers_writer_lock_t, guard,  lockGuardTestRW );
        BOOST_TEST_MESSAGE(timer.elapsed() << ": Parent: lock acquired");

        parentTID = makeTID(boost::this_thread::get_id());

        BOOST_CHECK( guard.isAcquired() );
        BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID()==parentTID );

        // Fire off the child, passing out thread id as an argument
        testThread = boost::thread( &lockGuardTestTimeoutChildRW, GetCurrentThreadId() );
        childTID = makeTID( testThread.get_id() );

        // Now we wait for the child to start and do it's 20 ms timeout thing.  If this
        // is too short, the child may not have a chance to start, causing a false positive.
        Sleep( 1000 );
    }  /* Guard is goes out of scope here */
    BOOST_TEST_MESSAGE(timer.elapsed() << ": Parent: lock released");

    Sleep( 10 );
    testThread.join();
    BOOST_TEST_MESSAGE(timer.elapsed() << ": Parent: child is dead");

    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == unlocked);
}

/** Readers_writer timeout child. **/
void lockGuardTestTimeoutChildRW(DWORD parentTid)
{
    BOOST_TEST_MESSAGE(timer.elapsed() << ": Child: started and waiting for lock");

    /* Try for lock, we will block then fail after 20 ms */
    CTILOCKGUARD2( Cti::readers_writer_lock_t , guard2,  lockGuardTestRW, 20 );
    BOOST_TEST_MESSAGE(timer.elapsed() << ": Child: guard complete");

    BOOST_CHECK(!guard2.isAcquired());
    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() != childTID );
}

/** 
In this test, we will set up n reader locks and one writer lock.  
The writer lock should block the readers until it's released, then the 
readers should both run.
**/

#define NUMBER_OF_CHILDREN 10

std::array<int, NUMBER_OF_CHILDREN> cf;

extern void lockGuardTestChildRRW(DWORD parentTid, int childIndex);
BOOST_AUTO_TEST_CASE(test_guard_test_reader_reader_writer_lock)
{
    using boost::algorithm::all_of_equal;

    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == unlocked);
    std::vector<boost::thread> testThreads;
    f1 = 0;
    {
        // GUARD defaults to writer lock
        CTILOCKGUARD( Cti::readers_writer_lock_t, guard,  lockGuardTestRW );

        parentTID = makeTID(boost::this_thread::get_id());

        BOOST_CHECK( guard.isAcquired() );
        BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID()==parentTID );

        // Fire off the children, passing out thread id and child index as an argument
        for (int childIndex=0; childIndex< NUMBER_OF_CHILDREN; ++childIndex)
        {
            cf[childIndex] = 0;
            testThreads.push_back(boost::thread(&lockGuardTestChildRRW, GetCurrentThreadId(), childIndex));
        }
        f1 = 1;

        Sleep( 20 );

        /* Children should have started and set status to 1 */
        BOOST_CHECK(all_of_equal(cf, 1));

        /* and the parent is still in charge. */
        std::string tid = makeTID(boost::this_thread::get_id());
        BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID()==tid);
        Sleep( 100 );

    }  /* Guard is goes out of scope here */

    Sleep( 10 );
    /* Worker should have run by now and set f3 */
    BOOST_CHECK(all_of_equal(cf, 2));

    for (int childIndex = 0; childIndex < NUMBER_OF_CHILDREN; ++childIndex)
    {
        testThreads[childIndex].join();
    }

    BOOST_CHECK( lockGuardTestRW.lastAcquiredByTID() == unlocked);
}

/** multiple readers test child. **/
void lockGuardTestChildRRW(DWORD parentTid, int childIndex)
{
    cf[childIndex] = 1;

    /* Try for lock, we will block */
    CTIREADLOCKGUARD( lock,  lockGuardTestRW );

    cf[childIndex] = 2;

    Sleep( 20 );
}

BOOST_AUTO_TEST_SUITE_END()