/*-----------------------------------------------------------------------------
    Filename:  logger.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiLogger

                    CtiLogger logs strings to a log file and optionally
                    standard out.  The stream insertion operators
                    are mostly all usable, they will not block.

                    Example of usage:

                    CtiLogger myout;

                    myout.start();
                    myout.setOutputPath("e:\\this\\is\\my\\logfile\\directory");
                    myout.setOutputFile("mylogfilename");
                    myout.setToStdOut(true);
                    myout.setWriteInterval(30000);

                    {
                        CtiLockGuard<CtiLogger> guard(myout);
                        myout << "Hi, i'm nice." << endl;
                    }

                    myout.interrupt(CtiThread::SHUTDOWN);
                    myout.join();


    Initial Date:  11/7/00

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#ifndef __CTILOGGER_HPP__
#define __CTILOGGER_HPP__

#ifdef _WINDOWS
    #include <windows.h>
#endif

#include <rw/rwtime.h>
#include <rw/thr/prodcons.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>

#include <limits>
#include <sstream>
#include <strstream>
#include <iostream>
#include <fstream>
#include <string>
using namespace std;

#include "dlldefs.h"
#include "thread.h"
#include "mutex.h"

class IM_EX_CTIBASE CtiLogger : public CtiThread
{
public:

    CtiLogger(const string& file = "", bool to_stdout = true);
    virtual ~CtiLogger();

    CtiLogger& setOutputPath(const string& path);
    CtiLogger& setOutputFile(const string& file);
    CtiLogger& setWriteInterval(long millis);
    CtiLogger& setToStdOut(bool to_stdout);

    //Immediately write out everything in the queue waiting
    // to be written
    CtiLogger& write();

    //Immediately move everything to the queue for writing
    CtiLogger& flush();

    bool acquire(unsigned long millis);
    CtiLogger& acquire();
    CtiLogger& release();

    ostream& operator<<(ios_base& (*pf)(ios_base&));
    ostream& operator<<(ostream& (*pf)(ostream&));
    ostream& operator<<(const char *s);
    ostream& operator<<(char c);
    ostream& operator<<(bool n);
    ostream& operator<<(short n);
    ostream& operator<<(unsigned short n);
    ostream& operator<<(int n);
    ostream& operator<<(unsigned int n);
    ostream& operator<<(long n);
    ostream& operator<<(unsigned long n);
    ostream& operator<<(float n);
    ostream& operator<<(double n);
    ostream& operator<<(long double n);
    ostream& operator<<(void * n);
    ostream& operator<<(const string& s);
    ostream& operator<<(const RWTime &r);

    char fill(char cfill);
    char fill() const;

#ifdef _DEBUG
    DWORD lastAcquiredByTID() const;
#endif

protected:
    void run();

private:
    char   _fillChar;
    string _filename;
    string _path;

    volatile long _write_interval;
    volatile bool _std_out;

    RWPCPtrQueue<strstream> _queue;
    CtiMutex _log_mux;
    CtiMutex _flush_mux;

    strstream* _current_stream;

    void doOutput();
    string scrub(const string& filename);
    bool tryOpenOutputFile(ofstream& strm, const string& file);
    string getTodaysFileName() const;
};


IM_EX_CTIBASE extern CtiLogger   dout;       // Global instance
IM_EX_CTIBASE extern CtiLogger   slog;       // Global instance. Simulator log
IM_EX_CTIBASE extern CtiLogger   blog;       // Global instance. Simulator log

#endif // #ifndef __PROCLOG_HPP__


