#pragma once

#include <windows.h>

#include <map>

#include "mutex.h"
#include "guard.h"
#include "dlldefs.h"

class IM_EX_CTIBASE CtiThread : private boost::noncopyable
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

    bool set(int id, bool value = true );
    bool isSet(int id);

private:
    std::map< int, bool > _event_map;

    CtiMutex _event_mux;
    CtiMutex _running_mux;

    static unsigned WINAPI ThreadProc( LPVOID lpData );
    unsigned _thread_handle;
    unsigned _thread_id;

    HANDLE hInterrupt;
};

