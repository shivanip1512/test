/*-----------------------------------------------------------------------------
    Filename:  thread.h

    Programmer:  Aaron  Lauinger

    Description:    Source file for CtiThread.

    Initial Date:  11/7/00

* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/thread.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/02/17 22:50:24 $
*

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>
using namespace std;

#include "dllbase.h"
#include "thread.h"

CtiThread::CtiThread() :
_usingCreateThread(false),
_thrhandle(INVALID_HANDLE_VALUE),
_thrhandle2(0)
{
   set(STARTING, false);
   set(SHUTDOWN, false);

   hInterrupt = CreateEvent(NULL,FALSE,FALSE,NULL);
}

CtiThread::~CtiThread()
{
   if(hInterrupt != INVALID_HANDLE_VALUE) CloseHandle(hInterrupt);
   if(_usingCreateThread && _thrhandle != INVALID_HANDLE_VALUE) CloseHandle(_thrhandle);
}

/*-----------------------------------------------------------------------------
    start

    Starts the thread.
-----------------------------------------------------------------------------*/
void CtiThread::start()
{
    bool failed = false;

    // set(STARTING, true) is the synchronization method here.
   if( !isRunning() && set(STARTING, true))
   {

#ifdef _WINDOWS

      // ThreadProc is a static member function that will acquire _running_mux
      // and then call the run() memeber function using a pointer to this
      // smuggled across as a void*

      if(_usingCreateThread)
      {
      _thrhandle = CreateThread(NULL, 0, CtiThread::ThreadProc, this, 0, &_thrid);
          if( _thrhandle == NULL ) failed = true;
      }
      else
      {
          _thrhandle2 = _beginthreadex(NULL, 0, CtiThread::ThreadProc2, this, 0, &_thrid2);
          if( _thrhandle2 == 0 ) failed = true;
      }

      if( failed )
      {
         LPVOID lpMsgBuf;
         FormatMessage( FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
                        NULL,
                        GetLastError(),
                        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
                        (LPTSTR) &lpMsgBuf,
                        0,
                        NULL
                      );
         {
            // RWMutexLock::LockGuard  guard(coutMux);
            cerr << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            cerr << "   " << (LPCTSTR)lpMsgBuf << endl;
         }

         // Free the buffer.
         LocalFree( lpMsgBuf );
      }
#endif
   }
}
/*-----------------------------------------------------------------------------
    interrupt

    Flags the running thread to interrupt itself.
-----------------------------------------------------------------------------*/
void CtiThread::interrupt()
{
#ifdef _WINDOWS
   SetEvent(hInterrupt);
#endif
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

   CtiLockGuard<CtiMutex> guard(_running_mux);
}

/*-----------------------------------------------------------------------------
    sleep

    In the event interrupt is called,sleep will immediately return true,
    otherwise sleep will return false after at least millis milliseconds.

-----------------------------------------------------------------------------*/
bool CtiThread::sleep(unsigned long millis)
{
#ifdef _WINDOWS
   return( WaitForSingleObject( hInterrupt, millis ) == WAIT_OBJECT_0 );
#endif
}

bool CtiThread::isSet(int id)
{
   CtiLockGuard<CtiMutex> guard(_event_mux);

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
   CtiLockGuard<CtiMutex> guard(_event_mux);
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
#ifdef _WINDOWS
   int id;

   if(_usingCreateThread) id = _thrid;
   else id = _thrid2;

   return id;
#endif
}
#ifdef _WINDOWS

DWORD WINAPI CtiThread::ThreadProc(LPVOID lpData )
{
   // We are using lpData to smuggle in a pointer to a CtiThread
   CtiThread* thr = (CtiThread*) lpData;

   // This order is very important!
   CtiLockGuard<CtiMutex> guard(thr->_running_mux);
   thr->set(STARTING, false );
   thr->run();

   return 0;
}

unsigned WINAPI CtiThread::ThreadProc2(LPVOID lpData )
{
   // We are using lpData to smuggle in a pointer to a CtiThread
   CtiThread* thr = (CtiThread*) lpData;

   // This order is very important!
   CtiLockGuard<CtiMutex> guard(thr->_running_mux);
   thr->set(STARTING, false );
   thr->run();

   return 0;
}

#endif
