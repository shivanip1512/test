#include "precompiled.h"

#include "dllbase.h"
#include "thread.h"
#include "logger.h"
#include "win_helper.h"

#include <process.h>

using Cti::getSystemErrorMessage;

using namespace std;

CtiThread::CtiThread() :
_thread_handle(0)
{
   set(STARTING, false);
   set(SHUTDOWN, false);

   hInterrupt = CreateEvent(NULL,FALSE,FALSE,NULL);
}

CtiThread::~CtiThread()
{
   if(hInterrupt != INVALID_HANDLE_VALUE) CloseHandle(hInterrupt);
}

/*-----------------------------------------------------------------------------
    start

    Starts the thread.
-----------------------------------------------------------------------------*/
void CtiThread::start()
{
    // set(STARTING, true) is the synchronization method here.
   if( !isRunning() && set(STARTING, true))
   {

      // ThreadProc is a static member function that will acquire _running_mux
      // and then call the run() memeber function using a pointer to this
      // smuggled across as a void*

      _thread_handle = _beginthreadex(NULL, 0, CtiThread::ThreadProc, this, 0, &_thread_id);

      if( ! _thread_handle )
      {
          const DWORD err = GetLastError();
          CTILOG_ERROR(dout, "_beginthreadex() failed with error code " << err <<" -> "<< getSystemErrorMessage(err));
      }
   }
}
/*-----------------------------------------------------------------------------
    interrupt

    Flags the running thread to interrupt itself.
-----------------------------------------------------------------------------*/
void CtiThread::interrupt()
{
   SetEvent(hInterrupt);
}

/*-----------------------------------------------------------------------------
    interrupt

    Flags the running thread to interrupt itself.
-----------------------------------------------------------------------------*/
void CtiThread::interrupt(int id)
{
   // must set the flag before releasing the mux
   // otherwise a thread blocked in sleep() might
   // return before the flag is set
   set(id, true);
   interrupt();
}

/*-----------------------------------------------------------------------------
    join

    Joins with the thread.
-----------------------------------------------------------------------------*/
void CtiThread::join()
{
   // A thread just started may not have acquired mux yet
   // wait till the thread has had the chance to acquire
   // mux before joining
   while( isSet( STARTING) )
      Sleep(10);

   CTILOCKGUARD(CtiMutex, guard, _running_mux);
}

/*-----------------------------------------------------------------------------
    sleep

    In the event interrupt is called,sleep will immediately return true,
    otherwise sleep will return false after at least millis milliseconds.

-----------------------------------------------------------------------------*/
bool CtiThread::sleep(unsigned long millis)
{
   return( WaitForSingleObject( hInterrupt, millis ) == WAIT_OBJECT_0 );
}

bool CtiThread::isSet(int id)
{
   CTILOCKGUARD(CtiMutex, guard, _event_mux);

   if( _event_map.find(id) != _event_map.end() )
   {
      return _event_map[id];
   }
   else
   {
      return false;
   }
}

// Sets the state and returns true if the state was changed
bool CtiThread::set(int id, bool state)
{
   CTILOCKGUARD(CtiMutex, guard, _event_mux);
   if(_event_map[id] != state)
   {
      _event_map[id] = state;
      return true;
   }
   return false;
}

bool CtiThread::isRunning()
{
   bool bAcu = _running_mux.acquire(0);

   if(bAcu)
   {
      _running_mux.release();
   }

   return !bAcu;
}

int CtiThread::getID() const
{
   return _thread_id;
}

unsigned WINAPI CtiThread::ThreadProc(LPVOID lpData )
{
   // We are using lpData to smuggle in a pointer to a CtiThread
   CtiThread* thr = (CtiThread*) lpData;

   // This order is very important!
   CTILOCKGUARD(CtiMutex, guard, thr->_running_mux);
   thr->set(STARTING, false );
   thr->run();

   return 0;
}

