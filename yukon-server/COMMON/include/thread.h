/*-----------------------------------------------------------------------------
    Filename:  thread.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiThread.
                    CtiThread encapsulates common thread functionality.

                    To create a thread:
                    Create a class that inherits from CtiThread
                    - you must implement a run() function, this
                      is where the newly created thread will begin
                      execution.

    Initial Date:  11/7/00

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __CTITHREAD_H__
#define __CTITHREAD_H__

#ifdef _WINDOWS
    #include <windows.h>
#endif

#include <map>
using namespace std;

#include "mutex.h"
#include "guard.h"
#include "dlldefs.h"

class IM_EX_CTIBASE CtiThread
{
public:

    enum { STARTING, SHUTDOWN, LAST };

    CtiThread();
    virtual ~CtiThread();

    void start();
    void join();
    void interrupt();
    void interrupt(int id);

    // The underlying thread id
    int getID() const;
    bool isRunning();

protected:

    virtual void run() = 0;
    bool sleep(unsigned long millis);

    void set(int id, bool value = true );
    bool isSet(int id);

private:
    map< int, bool > _event_map;

    CtiMutex _event_mux;
    CtiMutex _running_mux;

#ifdef _WINDOWS

    bool _usingCreateThread;

    static DWORD WINAPI ThreadProc( LPVOID lpData );
    HANDLE _thrhandle;
    DWORD  _thrid;

    static unsigned WINAPI ThreadProc2( LPVOID lpData );
    unsigned long _thrhandle2;
    unsigned _thrid2;

    HANDLE hInterrupt;
#endif

};
#endif
