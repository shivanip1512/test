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


#include "dlldefs.h"
#include "thread.h"
#include "mutex.h"
#include "ctitime.h"
#include "CtiPCPtrQueue.h"
#include "utility.h"


using std::endl;


class IM_EX_CTIBASE CtiLogger : public CtiThread
{
public:

    CtiLogger(const string& file = "", bool to_stdout = true);
    virtual ~CtiLogger();

    CtiLogger& setOutputPath(const string& path);
    CtiLogger& setOutputFile(const string& file);
    CtiLogger& setOwnerInfo(const compileinfo_t &ownerinfo);
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

    std::ostream& operator<<(std::ios_base& (*pf)(std::ios_base&));
    std::ostream& operator<<(std::ostream& (*pf)(std::ostream&));
    std::ostream& operator<<(const char *s);
    std::ostream& operator<<(char c);
    std::ostream& operator<<(bool n);
    std::ostream& operator<<(short n);
    std::ostream& operator<<(unsigned short n);
    std::ostream& operator<<(int n);
    std::ostream& operator<<(unsigned int n);
    std::ostream& operator<<(long n);
    std::ostream& operator<<(unsigned long n);
    std::ostream& operator<<(float n);
    std::ostream& operator<<(double n);
    std::ostream& operator<<(long double n);
    std::ostream& operator<<(void * n);
    std::ostream& operator<<(const string& s);
    std::ostream& operator<<(const CtiTime &r);

    char fill(char cfill);
    char fill() const;

#ifdef _DEBUG
    DWORD lastAcquiredByTID() const;
#endif

protected:
    void run();

    static string   scrub(string filename);
    static unsigned secondsUntilMidnight(const tm &tm_now);
    static bool     fileDateMatches(const string &filename, const unsigned month);

    string dayFilename(const unsigned day_of_month);

private:
    char    _fill;

    string  _path, _base_filename, _today_filename;
    time_t  _next_logfile_check;

    bool    _first_output;

    CtiTime _running_since;

    string  _project, _version;

    volatile long _write_interval;
    volatile bool _std_out;

    CtiPCPtrQueue<std::strstream> _queue;
    CtiMutex _log_mux;
    CtiMutex _flush_mux;

    std::strstream* _current_stream;

    void doOutput();
    void initStream();
    bool tryOpenOutputFile(std::ofstream& stream, bool append);
};


IM_EX_CTIBASE extern CtiLogger   dout;       // Global instance
IM_EX_CTIBASE extern CtiLogger   slog;       // Global instance. Simulator log
IM_EX_CTIBASE extern CtiLogger   blog;       // Global instance. Simulator log

#endif // #ifndef __CTILOGGER_HPP__

